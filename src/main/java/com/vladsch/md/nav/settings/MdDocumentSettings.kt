// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.vladsch.md.nav.settings.api.MdExtendableSettings
import com.vladsch.md.nav.settings.api.MdExtendableSettingsImpl
import com.vladsch.md.nav.settings.api.MdSettings

class MdDocumentSettings(private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()) : StateHolderImpl({ MdDocumentSettings() }), MdExtendableSettings by mySettingsExtensions, MdSettings {
    init {
        initializeExtensions(this)
    }

    // basic
    var asteriskItalics: Boolean = true
    var codeLikeStyleToggle: Boolean = true
    var disableGifImages: Boolean = true
    var enableLineMarkers: Boolean = true
    var fullHighlightCombinations: Boolean = true
    var grammarIgnoreSimpleTextCasing: Boolean = true // ignore CASING rules for single line list item text
    var hideDisabledButtons: Boolean = false
    var hideToolbar: Boolean = false
    var htmlLangInjections: Boolean = true
    var iconGutters: Boolean = true
    var joinStripPrefix: Boolean = false
    var maxBreadcrumbText: Int = DEFAULT_MAX_BREADCRUMB_TEXT
    var multiLineImageUrlInjections: Boolean = true
    var previewScrollDelay: Int = DEFAULT_PREVIEW_DELAY
    var showBreadcrumbText: Boolean = true
    var textSplitLayoutToggle: Boolean = true
    var toggleStylePunctuations: String = ".,:;!?"
    var typingUpdateDelay: Int = DEFAULT_PREVIEW_DELAY
    var verbatimLangInjections: Boolean = true
    var verticalSplitPreview: Boolean = false
    var zoomFactor: Double = 1.0 //JBUI.scale(1.0f).toDouble()
    var yandexFromLanguage: String = "de"
    var yandexToLanguage: String = "en"
    var yandexKey: String = ""
    var showTranslateDocument: Boolean = false
    var translateAutoDetect: Boolean = false
    var wrapOnlyOnTypingSpace: Boolean = false

    var smartEditUnderscore: Boolean = false
    var copyPathFileName: Boolean = false
    var copyPathBareFileName: Boolean = false
    var copyPathWithLineNumbers: Boolean = false
    var copyUpsourcePathWithLineNumbers: Boolean = true
    var useUpsourceURL: Boolean = true
    var smartEditAsterisks: Boolean = false
    var smartEditTildes: Boolean = false
    var smartEditBackTicks: Boolean = false
    var syntaxHighlighting: Int
        get() = mySyntaxHighlighting
        set(value) {
            mySyntaxHighlighting = SyntaxHighlightingType.ADAPTER.get(value).intValue
        }

    var syntaxHighlightingType: SyntaxHighlightingType
        get() = SyntaxHighlightingType.ADAPTER.get(mySyntaxHighlighting)
        set(value) {
            mySyntaxHighlighting = value.intValue
        }

    private var mySyntaxHighlighting: Int = SyntaxHighlightingType.ADAPTER.default.intValue

    var documentIcon: Int
        get() = myDocumentIcon
        set(value) {
            myDocumentIcon = DocumentIconTypes.ADAPTER.get(value).intValue
        }

    var documentIconType: DocumentIconTypes
        get() = DocumentIconTypes.ADAPTER.get(myDocumentIcon)
        set(value) {
            myDocumentIcon = value.intValue
        }

    private var myDocumentIcon: Int = DocumentIconTypes.MARKDOWN_NAVIGATOR.intValue

    var wikiIcon: Int
        get() = myWikiIcon
        set(value) {
            myWikiIcon = DocumentIconTypes.ADAPTER.get(value).intValue
        }

    var wikiIconType: DocumentIconTypes
        get() = DocumentIconTypes.ADAPTER.get(myWikiIcon)
        set(value) {
            myWikiIcon = value.intValue
        }

    private var myWikiIcon: Int = DocumentIconTypes.MARKDOWN_NAVIGATOR_WIKI.intValue

    constructor(other: MdDocumentSettings) : this() {
        copyFrom(other)
    }

    override fun resetToDefaults() {
        copyFrom(DEFAULT, false)
    }

    fun copyFrom(other: MdDocumentSettings, withExtensions: Boolean = true) {
        this.asteriskItalics = other.asteriskItalics
        this.codeLikeStyleToggle = other.codeLikeStyleToggle
        this.disableGifImages = other.disableGifImages
        this.enableLineMarkers = other.enableLineMarkers
        this.fullHighlightCombinations = other.fullHighlightCombinations
        this.grammarIgnoreSimpleTextCasing = other.grammarIgnoreSimpleTextCasing
        this.hideDisabledButtons = other.hideDisabledButtons
        this.hideToolbar = other.hideToolbar
        this.htmlLangInjections = other.htmlLangInjections
        this.iconGutters = other.iconGutters
        this.joinStripPrefix = other.joinStripPrefix
        this.maxBreadcrumbText = other.maxBreadcrumbText
        this.multiLineImageUrlInjections = other.multiLineImageUrlInjections
        this.myDocumentIcon = other.myDocumentIcon
        this.mySyntaxHighlighting = other.mySyntaxHighlighting
        this.myWikiIcon = other.myWikiIcon
        this.previewScrollDelay = other.previewScrollDelay
        this.showBreadcrumbText = other.showBreadcrumbText
        this.smartEditAsterisks = other.smartEditAsterisks
        this.smartEditBackTicks = other.smartEditBackTicks
        this.smartEditTildes = other.smartEditTildes
        this.smartEditUnderscore = other.smartEditUnderscore
        this.copyPathFileName = other.copyPathFileName
        this.copyPathBareFileName = other.copyPathBareFileName
        this.copyPathWithLineNumbers = other.copyPathWithLineNumbers
        this.copyUpsourcePathWithLineNumbers = other.copyUpsourcePathWithLineNumbers
        this.useUpsourceURL = other.useUpsourceURL
        this.textSplitLayoutToggle = other.textSplitLayoutToggle
        this.toggleStylePunctuations = other.toggleStylePunctuations
        this.typingUpdateDelay = other.typingUpdateDelay
        this.verbatimLangInjections = other.verbatimLangInjections
        this.verticalSplitPreview = other.verticalSplitPreview
        this.zoomFactor = other.zoomFactor
        this.yandexFromLanguage = other.yandexFromLanguage
        this.yandexToLanguage = other.yandexToLanguage
        this.yandexKey = other.yandexKey
        this.showTranslateDocument = other.showTranslateDocument
        this.translateAutoDetect = other.translateAutoDetect
        this.wrapOnlyOnTypingSpace = other.wrapOnlyOnTypingSpace

        if (withExtensions) mySettingsExtensions.copyFrom(other)
    }

    interface Holder {
        var documentSettings: MdDocumentSettings
    }

    override fun getStateHolder(): StateHolder {
        return mySettingsExtensions.addItems(TagItemHolder(STATE_ELEMENT_NAME).addItems(
            BooleanAttribute("asteriskItalics", { asteriskItalics }, { asteriskItalics = it }),
            BooleanAttribute("codeLikeStyleToggle", { codeLikeStyleToggle }, { codeLikeStyleToggle = it }),
            BooleanAttribute("disableGifImages", { disableGifImages }, { disableGifImages = it }),
            IntAttribute("documentIcon", { documentIcon }, { documentIcon = it }),
            BooleanAttribute("enableLineMarkers", { enableLineMarkers }, { enableLineMarkers = it }),
            BooleanAttribute("fullHighlightCombinations", { fullHighlightCombinations }, { fullHighlightCombinations = it }),
            BooleanAttribute("grammarIgnoreSimpleTextCasing", { grammarIgnoreSimpleTextCasing }, { grammarIgnoreSimpleTextCasing = it }),
            BooleanAttribute("hideDisabledButtons", { hideDisabledButtons }, { hideDisabledButtons = it }),
            BooleanAttribute("hideToolbar", { hideToolbar }, { hideToolbar = it }),
            BooleanAttribute("htmlLangInjections", { htmlLangInjections }, { htmlLangInjections = it }),
            BooleanAttribute("iconGutters", { iconGutters }, { iconGutters = it }),
            BooleanAttribute("joinStripPrefix", { joinStripPrefix }, { joinStripPrefix = it }),
            IntAttribute("maxBreadcrumbText", { maxBreadcrumbText }, { maxBreadcrumbText = it }),
            BooleanAttribute("multiLineImageUrlInjections", { multiLineImageUrlInjections }, { multiLineImageUrlInjections = it }),
            IntAttribute("previewScrollDelay", { previewScrollDelay }, { previewScrollDelay = it }),
            BooleanAttribute("showBreadcrumbText", { showBreadcrumbText }, { showBreadcrumbText = it }),
            BooleanAttribute("smartEditAsterisks", { smartEditAsterisks }, { smartEditAsterisks = it }),
            BooleanAttribute("smartEditBackTicks", { smartEditBackTicks }, { smartEditBackTicks = it }),
            BooleanAttribute("smartEditTildes", { smartEditTildes }, { smartEditTildes = it }),
            BooleanAttribute("smartEditUnderscore", { smartEditUnderscore }, { smartEditUnderscore = it }),
            BooleanAttribute("copyPathFileName", { copyPathFileName }, { copyPathFileName = it }),
            BooleanAttribute("copyPathBareFileName", { copyPathBareFileName }, { copyPathBareFileName = it }),
            BooleanAttribute("copyPathWithLineNumbers", { copyPathWithLineNumbers }, { copyPathWithLineNumbers = it }),
            BooleanAttribute("copyUpsourcePathWithLineNumbers", { copyUpsourcePathWithLineNumbers }, { copyUpsourcePathWithLineNumbers = it }),
            BooleanAttribute("useUpsourceURL", { useUpsourceURL }, { useUpsourceURL = it }),
            IntAttribute("syntaxHighlighting", { syntaxHighlighting }, { syntaxHighlighting = it }),
            BooleanAttribute("textSplitLayoutToggle", { textSplitLayoutToggle }, { textSplitLayoutToggle = it }),
            StringAttribute("toggleStylePunctuations", { toggleStylePunctuations }, { toggleStylePunctuations = it }),
            IntAttribute("typingUpdateDelay", { typingUpdateDelay }, { typingUpdateDelay = it }),
            BooleanAttribute("verbatimLangInjections", { verbatimLangInjections }, { verbatimLangInjections = it }),
            BooleanAttribute("verticalSplitPreview", { verticalSplitPreview }, { verticalSplitPreview = it }),
            IntAttribute("wikiIcon", { wikiIcon }, { wikiIcon = it }),
            DoubleAttribute("zoomFactor", { zoomFactor }, { if (it >= MdPreviewSettings.MIN_ZOOM_FACTOR && it <= MdPreviewSettings.MAX_ZOOM_FACTOR) zoomFactor = it else MdPreviewSettings.DEFAULT_ZOOM_FACTOR }),
            StringAttribute("translateFrom", { yandexFromLanguage }, { yandexFromLanguage = it }),
            StringAttribute("translateTo", { yandexToLanguage }, { yandexToLanguage = it }),
            StringAttribute("translateKey", { yandexKey }, { yandexKey = it }),
            BooleanAttribute("showTranslateDocument", { showTranslateDocument }, { showTranslateDocument = it }),
            BooleanAttribute("translateAutoDetect", { translateAutoDetect }, { translateAutoDetect = it }),
            BooleanAttribute("wrapOnlyOnTypingSpace", { wrapOnlyOnTypingSpace }, { wrapOnlyOnTypingSpace = it })
        ))
    }

    @Suppress("DEPRECATION")
    fun migrateSettings(settings: MdApplicationSettings.LocalState) {
        if (yandexKey.isEmpty() && settings.debugSettings.yandexKey.isNotEmpty()) {
            this.yandexFromLanguage = settings.debugSettings.yandexFromLanguage
            this.yandexToLanguage = settings.debugSettings.yandexToLanguage
            this.yandexKey = settings.debugSettings.yandexKey
        }
    }

    companion object {
        const val STATE_ELEMENT_NAME: String = "DocumentSettings"
        val DEFAULT: MdDocumentSettings by lazy { MdDocumentSettings() }

        const val DEFAULT_MAX_BREADCRUMB_TEXT: Int = 30
        const val MIN_MAX_BREADCRUMB_TEXT: Int = 20
        const val MAX_MAX_BREADCRUMB_TEXT: Int = 60
        const val DEFAULT_PREVIEW_DELAY: Int = 500
        const val MIN_PREVIEW_DELAY: Int = 50
        const val MAX_PREVIEW_DELAY: Int = 1000
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MdDocumentSettings) return false

        if (asteriskItalics != other.asteriskItalics) return false
        if (codeLikeStyleToggle != other.codeLikeStyleToggle) return false
        if (disableGifImages != other.disableGifImages) return false
        if (enableLineMarkers != other.enableLineMarkers) return false
        if (fullHighlightCombinations != other.fullHighlightCombinations) return false
        if (grammarIgnoreSimpleTextCasing != other.grammarIgnoreSimpleTextCasing) return false
        if (hideDisabledButtons != other.hideDisabledButtons) return false
        if (hideToolbar != other.hideToolbar) return false
        if (htmlLangInjections != other.htmlLangInjections) return false
        if (iconGutters != other.iconGutters) return false
        if (joinStripPrefix != other.joinStripPrefix) return false
        if (maxBreadcrumbText != other.maxBreadcrumbText) return false
        if (multiLineImageUrlInjections != other.multiLineImageUrlInjections) return false
        if (previewScrollDelay != other.previewScrollDelay) return false
        if (showBreadcrumbText != other.showBreadcrumbText) return false
        if (smartEditAsterisks != other.smartEditAsterisks) return false
        if (smartEditBackTicks != other.smartEditBackTicks) return false
        if (smartEditTildes != other.smartEditTildes) return false
        if (smartEditUnderscore != other.smartEditUnderscore) return false
        if (copyPathFileName != other.copyPathFileName) return false
        if (copyPathBareFileName != other.copyPathBareFileName) return false
        if (copyPathWithLineNumbers != other.copyPathWithLineNumbers) return false
        if (copyUpsourcePathWithLineNumbers != other.copyUpsourcePathWithLineNumbers) return false
        if (useUpsourceURL != other.useUpsourceURL) return false
        if (syntaxHighlighting != other.syntaxHighlighting) return false
        if (textSplitLayoutToggle != other.textSplitLayoutToggle) return false
        if (toggleStylePunctuations != other.toggleStylePunctuations) return false
        if (typingUpdateDelay != other.typingUpdateDelay) return false
        if (verbatimLangInjections != other.verbatimLangInjections) return false
        if (verticalSplitPreview != other.verticalSplitPreview) return false
        if (zoomFactor != other.zoomFactor) return false
        if (yandexFromLanguage != other.yandexFromLanguage) return false
        if (yandexToLanguage != other.yandexToLanguage) return false
        if (yandexKey != other.yandexKey) return false
        if (showTranslateDocument != other.showTranslateDocument) return false
        if (translateAutoDetect != other.translateAutoDetect) return false
        if (wrapOnlyOnTypingSpace != other.wrapOnlyOnTypingSpace) return false

        return mySettingsExtensions == other
    }

    override fun hashCode(): Int {
        var result = mySettingsExtensions.hashCode()
        result += 31 * result + asteriskItalics.hashCode()
        result += 31 * result + codeLikeStyleToggle.hashCode()
        result += 31 * result + disableGifImages.hashCode()
        result += 31 * result + enableLineMarkers.hashCode()
        result += 31 * result + fullHighlightCombinations.hashCode()
        result += 31 * result + grammarIgnoreSimpleTextCasing.hashCode()
        result += 31 * result + hideDisabledButtons.hashCode()
        result += 31 * result + hideToolbar.hashCode()
        result += 31 * result + htmlLangInjections.hashCode()
        result += 31 * result + iconGutters.hashCode()
        result += 31 * result + joinStripPrefix.hashCode()
        result += 31 * result + maxBreadcrumbText.hashCode()
        result += 31 * result + multiLineImageUrlInjections.hashCode()
        result += 31 * result + myDocumentIcon.hashCode()
        result += 31 * result + mySyntaxHighlighting.hashCode()
        result += 31 * result + myWikiIcon.hashCode()
        result += 31 * result + previewScrollDelay.hashCode()
        result += 31 * result + showBreadcrumbText.hashCode()
        result += 31 * result + smartEditAsterisks.hashCode()
        result += 31 * result + smartEditBackTicks.hashCode()
        result += 31 * result + smartEditTildes.hashCode()
        result += 31 * result + smartEditUnderscore.hashCode()
        result += 31 * result + copyPathFileName.hashCode()
        result += 31 * result + copyPathBareFileName.hashCode()
        result += 31 * result + copyPathWithLineNumbers.hashCode()
        result += 31 * result + copyUpsourcePathWithLineNumbers.hashCode()
        result += 31 * result + useUpsourceURL.hashCode()
        result += 31 * result + textSplitLayoutToggle.hashCode()
        result += 31 * result + toggleStylePunctuations.hashCode()
        result += 31 * result + typingUpdateDelay.hashCode()
        result += 31 * result + verbatimLangInjections.hashCode()
        result += 31 * result + verticalSplitPreview.hashCode()
        result += 31 * result + zoomFactor.hashCode()
        result += 31 * result + yandexFromLanguage.hashCode()
        result += 31 * result + yandexToLanguage.hashCode()
        result += 31 * result + yandexKey.hashCode()
        result += 31 * result + showTranslateDocument.hashCode()
        result += 31 * result + translateAutoDetect.hashCode()
        result += 31 * result + wrapOnlyOnTypingSpace.hashCode()
        return result
    }
}
