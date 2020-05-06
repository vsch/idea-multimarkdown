// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.settings

import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.md.nav.editor.util.HtmlPanelProvider
import com.vladsch.md.nav.settings.*
import com.vladsch.md.nav.settings.api.MdSettingsExtension
import com.vladsch.plugin.util.nullIfBlank
import com.vladsch.plugin.util.nullIfEmpty
import java.util.*
import kotlin.collections.HashMap

class FlexmarkHtmlSettings() : StateHolderImpl({ FlexmarkHtmlSettings() }), MdSettingsExtension<FlexmarkHtmlSettings> {
    var flexmarkSpecExampleRendering: Int = 0
    var flexmarkSpecExampleRenderHtml: Boolean = false

    var flexmarkSpecExampleRenderingType: FlexmarkSpecExampleRenderingType
        get() = FlexmarkSpecExampleRenderingType.ADAPTER.get(flexmarkSpecExampleRendering)
        set(value) {
            flexmarkSpecExampleRendering = value.intValue
        }

    val flexmarkSectionLanguages: HashMap<Int, String> = HashMap(DEFAULT_SECTION_LANGUAGES)

    fun getFlexmarkSectionLanguagesOnly(): Map<Int, String> {
        val map = HashMap<Int, String>()
        flexmarkSectionLanguages
            .filter { !(it.key > 0 && it.value != FLEXMARK_AST_LANGUAGE_NAME && it.value.startsWith(FLEXMARK_AST_LANGUAGE_PREFIX)) }
            .forEach { map[it.key] = if (it.value.startsWith(FLEXMARK_AST_LANGUAGE_PREFIX)) "text" else it.value.toLowerCase() }
        return map
    }

    fun getFlexmarkSectionNames(): Map<Int, String> {
        val map = HashMap<Int, String>()
        flexmarkSectionLanguages
            .forEach { map[it.key] = if (it.value.startsWith(FLEXMARK_AST_LANGUAGE_PREFIX)) "AST" else it.value }
        return map
    }

    override fun isDefault(): Boolean {
        return this == DEFAULT
    }

    constructor(other: FlexmarkHtmlSettings) : this() {
        copyFrom(other)
    }

    interface Holder {
        var htmlSettings: FlexmarkHtmlSettings
    }

    fun isDefault(htmlPanelProvider: HtmlPanelProvider.Info?): Boolean {
        return this == getDefaultSettings(htmlPanelProvider)
    }

    override fun createCopy(): FlexmarkHtmlSettings {
        return FlexmarkHtmlSettings(this)
    }

    override fun copyFrom(other: FlexmarkHtmlSettings) {
        this.flexmarkSpecExampleRendering = other.flexmarkSpecExampleRendering
        this.flexmarkSpecExampleRenderHtml = other.flexmarkSpecExampleRenderHtml
        this.flexmarkSectionLanguages.clear();
        this.flexmarkSectionLanguages.putAll(other.flexmarkSectionLanguages)
    }

    override fun getKey(): DataKey<FlexmarkHtmlSettings> = KEY
    override fun getDefault(): FlexmarkHtmlSettings = DEFAULT

    override fun getStateHolder(): StateHolder {
        val tagItemHolder = TagItemHolder("FlexmarkHtmlSettings")
        addItems(false, tagItemHolder)
        return tagItemHolder
    }

    fun sectionInfo(index: Int): com.vladsch.flexmark.util.misc.Pair<String, Set<Int>> {
        return languageSections(flexmarkSectionLanguages[index])
    }

    override fun addItems(readOnly: Boolean, tagItemHolder: TagItemHolder) {
        // readOnly items are only loaded if their attribute is actually found in the element
        // non-read only will set to default to empty value (false, "", 0, etc.)
        tagItemHolder.addItems(
            IntAttribute("flexmarkSpecExampleRendering", readOnly, { flexmarkSpecExampleRendering }, { flexmarkSpecExampleRendering = it }),
            BooleanAttribute("flexmarkSpecExampleRenderHtml", readOnly, { flexmarkSpecExampleRenderHtml }, { flexmarkSpecExampleRenderHtml = it }),
            MapItem("flexmarkSectionLanguages", readOnly,
                { flexmarkSectionLanguages },
                {
                    flexmarkSectionLanguages.clear();
                    flexmarkSectionLanguages.putAll(it.nullIfEmpty() ?: DEFAULT_SECTION_LANGUAGES)
                },
                { key: Int, value: String ->
                    Pair(key.toString(), value)
                },
                { key: String, value: String? ->
                    val index = key.toIntOrNull()
                    if (value != null && index != null) {
                        Pair(index, value)
                    } else {
                        null
                    }
                }
            )
        )
    }

    companion object {
        const val FLEXMARK_AST_LANGUAGE_NAME: String = "flexmark-ast"
        const val FLEXMARK_AST_LANGUAGE_PREFIX: String = "$FLEXMARK_AST_LANGUAGE_NAME:"

        @JvmField
        val DEFAULT_SECTION_LANGUAGES: Map<Int, String> = mapOf(1 to "Markdown", 2 to "HTML", 3 to "$FLEXMARK_AST_LANGUAGE_NAME:1")

        @JvmField
        val KEY = DataKey("FlexmarkHtmlSettings") { FlexmarkHtmlSettings() }

        @JvmStatic
        val DEFAULT: FlexmarkHtmlSettings by lazy { FlexmarkHtmlSettings() }

        @Suppress("UNUSED_PARAMETER")
        fun getDefaultSettings(htmlPanelProvider: HtmlPanelProvider.Info?): FlexmarkHtmlSettings {
            return DEFAULT
        }

        @JvmStatic
        fun languageSections(languageString: String?): com.vladsch.flexmark.util.misc.Pair<String, Set<Int>> {
            val languageSections = languageString.nullIfBlank() ?: return com.vladsch.flexmark.util.misc.Pair.of("", emptySet())
            var language: String = languageSections
            val astSections = HashSet<Int>()
            val pos = languageSections.indexOf(":")
            if (pos >= 0) {
                language = languageSections.substring(0, pos)
                val sections = languageSections.substring(pos + 1).trim().split(",").toTypedArray()
                for (section in sections) {
                    val trimmedSection = section.trim()
                    if (trimmedSection.isEmpty()) continue
                    try {
                        val index = Integer.parseInt(trimmedSection)
                        if (index > 0) {
                            astSections.add(index)
                        }
                    } catch (ignored: NumberFormatException) {
                    }
                }
            }
            return com.vladsch.flexmark.util.misc.Pair.of(language, astSections)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as FlexmarkHtmlSettings

        if (flexmarkSpecExampleRendering != other.flexmarkSpecExampleRendering) return false
        if (flexmarkSpecExampleRenderHtml != other.flexmarkSpecExampleRenderHtml) return false
        if (flexmarkSectionLanguages != other.flexmarkSectionLanguages) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 0
        result += 31 * result + flexmarkSpecExampleRendering.hashCode()
        result += 31 * result + flexmarkSpecExampleRenderHtml.hashCode()
        result += 31 * result + flexmarkSectionLanguages.hashCode()
        return result
    }

    @Suppress("UNUSED_PARAMETER")
    fun changeToProvider(fromPanelProviderInfo: HtmlPanelProvider.Info?, toPanelProviderInfo: HtmlPanelProvider.Info): FlexmarkHtmlSettings {
        return FlexmarkHtmlSettings(this)
    }
}
