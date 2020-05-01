// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.openapi.diagnostic.Logger
import com.vladsch.md.nav.editor.util.HtmlPanelProvider
import com.vladsch.md.nav.parser.Extensions
import com.vladsch.md.nav.parser.MdLexParser
import com.vladsch.md.nav.settings.api.MdExtendableSettings
import com.vladsch.md.nav.settings.api.MdExtendableSettingsImpl
import com.vladsch.md.nav.settings.api.MdSettings

class MdParserSettings @JvmOverloads constructor(extensionFlags: Int,
    optionFlags: Long,
    wasShownGitHubSyntaxChange: Boolean,
    emojiShortcuts: Int,
    emojiImages: Int,
    private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()
) : StateHolderImpl({ MdParserSettings() }), MdExtendableSettings by mySettingsExtensions, MdSettings {

    init {
        initializeExtensions(this)
    }

    constructor() : this(DEFAULT._pegdownFlags, DEFAULT._parserFlags, false, DEFAULT.emojiShortcuts, DEFAULT.emojiImages)

    constructor(pegdownExtensions: Collection<PegdownExtensions>, parserOptions: Collection<ParserOptions>, wasShownGitHubSyntaxChange: Boolean, emojiShortcuts: EmojiShortcutsType, emojiImages: EmojiImagesType)
        : this(PegdownExtensions.asFlags(pegdownExtensions), ParserOptions.asFlags(parserOptions), wasShownGitHubSyntaxChange, emojiShortcuts.intValue, emojiImages.intValue)

    constructor(other: MdParserSettings) : this(other._pegdownFlags, other._parserFlags, other.gitHubSyntaxChange, other.emojiShortcuts, other.emojiImages) {
        mySettingsExtensions.copyFrom(other)
    }

    override fun resetToDefaults() {
        copyFrom(DEFAULT, false)
    }

    private var correctedInvalidSettings = false

    fun copyFrom(other: MdParserSettings, withExtensions: Boolean = true) {
        if (other._pegdownFlags == 0 && other._parserFlags == 0L) {
            LOG.error("Copying cleared Parser settings from $other to $this")
        }

        this._pegdownFlags = other._pegdownFlags
        this._parserFlags = other._parserFlags
        this.gitHubSyntaxChange = other.gitHubSyntaxChange
        this.emojiShortcuts = other.emojiShortcuts
        this.emojiImages = other.emojiImages
//        this.correctedInvalidSettings = other.correctedInvalidSettings

        if (withExtensions) mySettingsExtensions.copyFrom(other)
    }

    private var _pegdownFlags = extensionFlags
    var pegdownFlags: Int
        get() = _pegdownFlags
        set(value) {
            _pegdownFlags = value
        }

    private val pegdownExtensions: Set<PegdownExtensions>
        get() = PegdownExtensions.asSet(pegdownFlags)
//        private set(value) {
//            _extensionFlags = PegdownExtensions.asFlags(value)
//        }

    fun anyExtensions(flags: Int): Boolean {
        return (_pegdownFlags and flags) != 0
    }

    override fun validateLoadedSettings() {
        mySettingsExtensions.validateLoadedSettings()

        // NOTE: if settings are cleared then this will reset them to defaults
        if (_pegdownFlags == 0 && _parserFlags == 0L) {
            LOG.info("Resetting Parser settings on load: $this")

            correctedInvalidSettings = true
            _pegdownFlags = DEFAULT._pegdownFlags
            _parserFlags = DEFAULT._parserFlags
            emojiShortcuts = DEFAULT.emojiShortcuts
            emojiImages = DEFAULT.emojiImages
        } else {
            correctedInvalidSettings = false
        }
    }

    fun anyExtensions(vararg flags: PegdownExtensions): Boolean {
        return flags.any { anyExtensions(it.flags) }
    }

    fun allExtensions(flags: Int): Boolean {
        return (_pegdownFlags and flags) == flags
    }

    fun allExtensions(vararg flags: PegdownExtensions): Boolean {
        return flags.all { anyExtensions(it.flags) }
    }

    private var _parserFlags: Long = optionFlags
    var optionsFlags: Long
        get() = _parserFlags
        set(value) {
            _parserFlags = value
        }

    fun anyOptions(flags: Long): Boolean {
        return (_parserFlags and flags) != 0L
    }

    fun anyOptions(vararg flags: ParserOptions): Boolean {
        return flags.any { anyOptions(it.flags) }
    }

    fun allOptions(flags: Long): Boolean {
        return (_parserFlags and flags) != 0L
    }

    fun allOptions(vararg flags: ParserOptions): Boolean {
        return flags.all { anyOptions(it.flags) }
    }

    private var parserOptions: Set<ParserOptions>
        get() = ParserOptions.asSet(_parserFlags)
        private set(value) {
            _parserFlags = ParserOptions.asFlags(value)
        }

    val parserListIndentationType: ListIndentationType
        get() {
            return listIndentationType(optionsFlags)
        }

    var gitHubSyntaxChange: Boolean = wasShownGitHubSyntaxChange

    var emojiShortcuts: Int = emojiShortcuts
        private set

    var emojiShortcutsType: EmojiShortcutsType
        get() = EmojiShortcutsType.ADAPTER.findEnum(emojiShortcuts)
        set(value) {
            emojiShortcuts = value.intValue
        }

    var emojiImages: Int = emojiImages
        protected set

    var emojiImagesType: EmojiImagesType
        get() = EmojiImagesType.ADAPTER.findEnum(emojiImages)
        set(value) {
            emojiImages = value.intValue
        }

    override fun getStateHolder(): StateHolder {
        return mySettingsExtensions.addItems(TagItemHolder("ParserSettings").addItems(
            MapItem("PegdownExtensions",
                // NOTE: turning off code only flags to prevent saved settings toggling their state from version to version
                {
                    val map = PegdownExtensions.asMap((_pegdownFlags and EXCLUDED_PEGDOWN_EXTENSIONS.inv()) or PegdownExtensions.INTELLIJ_DUMMY_IDENTIFIER.flags)
                    val filtered = map.filter { it.value }.toMap()
                    filtered
                },
                { _pegdownFlags = PegdownExtensions.asFlags(it) },
                { key, value -> Pair(key.name, value.toString()) },
                { key, value ->
                    val enumConstant = PegdownExtensions.enumConstant(key)
                    if (enumConstant == null) null else Pair(enumConstant, value?.toBoolean() ?: false)
                }),
            MapItem("ParserOptions",
                {
                    val map = ParserOptions.asMap((_parserFlags and EXCLUDED_PARSER_OPTIONS.inv()) or ParserOptions.PRODUCTION_SPEC_PARSER.flags)
                    val filtered = map.filter { it.value }.toMap()
                    filtered
                },
                { _parserFlags = ParserOptions.asFlags(it) },
                { key, value -> Pair(key.name, value.toString()) },
                { key, value ->
                    val enumConstant = ParserOptions.enumConstant(key)
                    if (enumConstant == null) null else Pair(enumConstant, value?.toBoolean() ?: false)
                }),
            BooleanAttribute("gitHubSyntaxChange", { gitHubSyntaxChange }, { gitHubSyntaxChange = it }),
            BooleanAttribute("correctedInvalidSettings", { correctedInvalidSettings }, { correctedInvalidSettings = it }),
            IntAttribute("emojiShortcuts", { emojiShortcuts }, { emojiShortcuts = it }),
            IntAttribute("emojiImages", { emojiImages }, { emojiImages = it })
        ))
    }

    interface Holder {
        var parserSettings: MdParserSettings
    }

    fun isDefault(htmlPanelProvider: HtmlPanelProvider.Info?): Boolean {
        return !correctedInvalidSettings && this == getDefaultSettings(htmlPanelProvider)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MdParserSettings

        if ((_pegdownFlags and EXCLUDED_PEGDOWN_EXTENSIONS.inv()) != (other._pegdownFlags and EXCLUDED_PEGDOWN_EXTENSIONS.inv())) return false
        if ((_parserFlags and EXCLUDED_PARSER_OPTIONS.inv()) != (other._parserFlags and EXCLUDED_PARSER_OPTIONS.inv())) return false
        if (gitHubSyntaxChange != other.gitHubSyntaxChange) return false
        if (emojiShortcuts != other.emojiShortcuts) return false
        if (emojiImages != other.emojiImages) return false
//        if (correctedInvalidSettings != other.correctedInvalidSettings) return false

        return mySettingsExtensions == other
    }

    override fun hashCode(): Int {
        var result = mySettingsExtensions.hashCode()
        result += 31 * result + (_pegdownFlags and EXCLUDED_PEGDOWN_EXTENSIONS.inv()).hashCode()
        result += 31 * result + (_parserFlags and EXCLUDED_PARSER_OPTIONS.inv()).hashCode()
        result += 31 * result + gitHubSyntaxChange.hashCode()
        result += 31 * result + emojiShortcuts.hashCode()
        result += 31 * result + emojiImages.hashCode()
//        result += 31 * result + correctedInvalidSettings.hashCode()
        return result
    }

    @Suppress("UNUSED_PARAMETER")
    fun changeToProvider(fromPanelProviderInfo: HtmlPanelProvider.Info?, toPanelProviderInfo: HtmlPanelProvider.Info) {
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.settings")

        @JvmField
        val EXCLUDED_PEGDOWN_EXTENSIONS: Int =
            PegdownExtensions.INTELLIJ_DUMMY_IDENTIFIER.flags or
                PegdownExtensions.MULTI_LINE_IMAGE_URLS.flags or
                PegdownExtensions.EXTANCHORLINKS.flags or
                PegdownExtensions.EXTANCHORLINKS_WRAP.flags

        @JvmField
        val EXCLUDED_PARSER_OPTIONS: Long = ParserOptions.PRODUCTION_SPEC_PARSER.flags

        @JvmField
        val GITHUB_DOCUMENT_COMPATIBLE: Long = ParserOptions.EMOJI_SHORTCUTS.flags or
            ParserOptions.GFM_TABLE_RENDERING.flags or
            ParserOptions.COMMONMARK_LISTS.flags

        @JvmField
        val GITBOOK_DOCUMENT_COMPATIBLE: Long = ParserOptions.EMOJI_SHORTCUTS.flags or
            ParserOptions.GFM_TABLE_RENDERING.flags or
            ParserOptions.GITHUB_LISTS.flags or
            ParserOptions.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA.flags or
            ParserOptions.GITBOOK_URL_ENCODING.flags or
            ParserOptions.ATTRIBUTES_EXT.flags or
            ParserOptions.HEADER_ID_NO_DUPED_DASHES.flags or
            ParserOptions.HEADER_ID_NON_ASCII_TO_LOWERCASE.flags

        @JvmField
        val GITHUB_WIKI_COMPATIBLE: Long = GITHUB_DOCUMENT_COMPATIBLE or ParserOptions.GITHUB_WIKI_LINKS.flags

        @JvmField
        val COMMONMARK: Long = ParserOptions.COMMONMARK_LISTS.flags or
            ParserOptions.HEADER_ID_NON_ASCII_TO_LOWERCASE.flags

        @JvmField
        val COMMONMARK_EXTENSIONS: Int = Extensions.FENCED_CODE_BLOCKS or
            Extensions.RELAXEDHRULES or
            Extensions.ATXHEADERSPACE

        @JvmField
        val GITHUB_COMMENT_COMPATIBLE: Long = ParserOptions.COMMONMARK_LISTS.flags or
            ParserOptions.EMOJI_SHORTCUTS.flags or
            ParserOptions.GFM_TABLE_RENDERING.flags

        @JvmField
        val GITLAB_DOCUMENT_COMPATIBLE: Long = ParserOptions.COMMONMARK_LISTS.flags or
            ParserOptions.EMOJI_SHORTCUTS.flags or
            ParserOptions.GFM_TABLE_RENDERING.flags or
            ParserOptions.GITLAB_EXT.flags or
            ParserOptions.GITLAB_MATH_EXT.flags or
            ParserOptions.GITLAB_MERMAID_EXT.flags or
            ParserOptions.HEADER_ID_NO_DUPED_DASHES.flags or
            ParserOptions.HEADER_ID_NON_ASCII_TO_LOWERCASE.flags

        private val DEFAULT_EXTENSION_FLAGS = mapOf(
            PegdownExtensions.FENCED_CODE_BLOCKS to true,
            PegdownExtensions.TABLES to true,
//            PegdownExtensions.AUTOLINKS to true,
            PegdownExtensions.ANCHORLINKS to true,
            PegdownExtensions.TASKLISTITEMS to true,
            PegdownExtensions.STRIKETHROUGH to true,
            PegdownExtensions.ATXHEADERSPACE to true,
            PegdownExtensions.RELAXEDHRULES to true,
            PegdownExtensions.INTELLIJ_DUMMY_IDENTIFIER to true // added to make sure proper extensions are never 0
        )

        private val DEFAULT_OPTION_FLAGS = mapOf(
            ParserOptions.EMOJI_SHORTCUTS to true,
            ParserOptions.GFM_TABLE_RENDERING to true,
            ParserOptions.PRODUCTION_SPEC_PARSER to true // added to make sure proper parser options are never 0
        )

        @JvmStatic
        val DEFAULT: MdParserSettings by lazy { MdParserSettings(Extensions.GITHUB_DOCUMENT_COMPATIBLE and Extensions.AUTOLINKS.inv(), GITHUB_DOCUMENT_COMPATIBLE or MdLexParser.SIM_TOC_BLANK_LINE_SPACER, false, EmojiShortcutsType.GITHUB.intValue, EmojiImagesType.IMAGE_ONLY.intValue) }

        @JvmStatic
        val GITBOOK: MdParserSettings by lazy { MdParserSettings(Extensions.GITHUB_DOCUMENT_COMPATIBLE, GITBOOK_DOCUMENT_COMPATIBLE or MdLexParser.SIM_TOC_BLANK_LINE_SPACER, false, EmojiShortcutsType.GITHUB.intValue, EmojiImagesType.IMAGE_ONLY.intValue) }

        @JvmStatic
        val GITLAB: MdParserSettings by lazy { MdParserSettings(Extensions.GITHUB_DOCUMENT_COMPATIBLE, GITLAB_DOCUMENT_COMPATIBLE or MdLexParser.SIM_TOC_BLANK_LINE_SPACER, false, EmojiShortcutsType.GITHUB.intValue, EmojiImagesType.IMAGE_ONLY.intValue) }

        @JvmStatic
        val GITHUB: MdParserSettings by lazy { MdParserSettings(Extensions.GITHUB_DOCUMENT_COMPATIBLE, GITHUB_DOCUMENT_COMPATIBLE or MdLexParser.SIM_TOC_BLANK_LINE_SPACER, false, EmojiShortcutsType.GITHUB.intValue, EmojiImagesType.IMAGE_ONLY.intValue) }

        @JvmStatic
        val FOR_SAMPLE_DOC: MdParserSettings by lazy {
            val pegdownExtensionFlags = (
                Extensions.GITHUB_DOCUMENT_COMPATIBLE or
                    Extensions.ALL or
                    Extensions.AUTOLINKS or
                    Extensions.RELAXEDHRULES or
                    Extensions.TOC or
                    Extensions.TASKLISTITEMS or
                    Extensions.FOOTNOTES or
//                    Extensions.ASIDE or   // this one conflicts with table
                    Extensions.DEFINITIONS or
                    Extensions.SUBSCRIPT or
                    Extensions.SUPERSCRIPT or
                    Extensions.INSERTED
                ) and (Extensions.ANCHORLINKS or Extensions.ATXHEADERSPACE).inv()
            val parserOptionsFlags = (
                MdLexParser.ATTRIBUTES_EXT or
                    MdLexParser.COMMONMARK_LISTS or
                    MdLexParser.EMOJI_SHORTCUTS or
                    MdLexParser.ENUMERATED_REFERENCES_EXT or
                    MdLexParser.GITHUB_WIKI_LINKS or
                    MdLexParser.GITLAB_EXT or
                    MdLexParser.JEKYLL_FRONT_MATTER or
                    MdLexParser.MACROS_EXT or
                    MdLexParser.SIM_TOC_BLANK_LINE_SPACER
                ) and (MdLexParser.GITHUB_LISTS).inv()

            MdParserSettings(pegdownExtensionFlags, parserOptionsFlags, true, EmojiShortcutsType.ANY_GITHUB_PREFERRED.intValue, EmojiImagesType.IMAGE_ONLY.intValue)
        }

        @Suppress("UNUSED_PARAMETER")
        fun getDefaultSettings(htmlPanelProvider: HtmlPanelProvider.Info?): MdParserSettings = DEFAULT

        @JvmStatic
        fun listIndentationType(parserOptionsFlags: Long): ListIndentationType {
            if (parserOptionsFlags and ParserOptions.COMMONMARK_LISTS.flags != 0L) {
                return ListIndentationType.COMMONMARK
            }
            if (parserOptionsFlags and ParserOptions.GITHUB_LISTS.flags != 0L) {
                return ListIndentationType.GITHUB
            }
            return ListIndentationType.FIXED
        }
    }
}
