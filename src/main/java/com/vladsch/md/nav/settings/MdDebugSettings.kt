// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.vladsch.md.nav.settings.api.MdExtendableSettings
import com.vladsch.md.nav.settings.api.MdExtendableSettingsImpl
import com.vladsch.md.nav.settings.api.MdSettings

class MdDebugSettings(private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()) : StateHolderImpl({ MdDebugSettings() }), MdExtendableSettings by mySettingsExtensions, MdSettings {
    init {
        initializeExtensions(this)
    }

    var debugFormatText: Boolean = false
    var debugCombinationColors: Boolean = false
    var generateParserExceptions: Boolean = false
    var preferencesDialogWidth: Int = 1000
    var preferencesDialogHeight: Int = 1000
    var preferencesDialogMenuSplit: Double = 0.23
    var lastSettingsImport: String = ""
    var remoteInvalidUntilFetched: Boolean = false
    var outputRemoteFetchExceptions: Boolean = false
    var reloadEditorsOnFileTypeChange: Boolean = true
    var reinitializeEditorsOnSettingsChange: Boolean = true
    var cacheSvgForSwing: Boolean = true
    var taskItemImages: Boolean = false
    var showSizePreferencesDialog: Boolean = false
    var showTextHexDialog: Boolean = false
    var useFileLinkCache: Boolean = true

    @Deprecated("Use DocumentSettings instead")
    var yandexFromLanguage: String = "de"

    @Deprecated("Use DocumentSettings instead")
    var yandexToLanguage: String = "en"

    @Deprecated("Use DocumentSettings instead")
    var yandexKey: String = ""

    constructor(other: MdDebugSettings) : this() {
        copyFrom(other)
    }

    override fun resetToDefaults() {
        copyFrom(DEFAULT, false)
    }

    fun copyFrom(other: MdDebugSettings, withExtensions: Boolean = true) {
        this.debugFormatText = other.debugFormatText
        this.debugCombinationColors = other.debugCombinationColors
        this.generateParserExceptions = other.generateParserExceptions
        this.preferencesDialogWidth = other.preferencesDialogWidth
        this.preferencesDialogHeight = other.preferencesDialogHeight
        this.preferencesDialogMenuSplit = other.preferencesDialogMenuSplit
        this.lastSettingsImport = other.lastSettingsImport
        this.remoteInvalidUntilFetched = other.remoteInvalidUntilFetched
        this.outputRemoteFetchExceptions = other.outputRemoteFetchExceptions
        this.reloadEditorsOnFileTypeChange = other.reloadEditorsOnFileTypeChange
        this.reinitializeEditorsOnSettingsChange = other.reinitializeEditorsOnSettingsChange
        this.cacheSvgForSwing = other.cacheSvgForSwing
        this.taskItemImages = other.taskItemImages
        this.showSizePreferencesDialog = other.showSizePreferencesDialog
        this.showTextHexDialog = other.showTextHexDialog
        this.useFileLinkCache = other.useFileLinkCache

        if (withExtensions) mySettingsExtensions.copyFrom(other)
    }

    interface Holder {
        var debugSettings: MdDebugSettings
    }

    @Suppress("DEPRECATION")
    override fun getStateHolder(): StateHolder = mySettingsExtensions.addItems(TagItemHolder("DebugSettings").addItems(
        BooleanAttribute("debugFormatText", { debugFormatText }, { debugFormatText = it }),
        BooleanAttribute("debugCombinationColors", { debugCombinationColors }, { debugCombinationColors = it }),
        BooleanAttribute("generateParserExceptions", { generateParserExceptions }, { generateParserExceptions = it }),
        IntAttribute("preferencesDialogWidth", { preferencesDialogWidth }, { preferencesDialogWidth = it }),
        IntAttribute("preferencesDialogHeight", { preferencesDialogHeight }, { preferencesDialogHeight = it }),
        DoubleAttribute("preferencesDialogMenuSplit", { preferencesDialogMenuSplit }, { preferencesDialogMenuSplit = it }),
        StringAttribute("lastSettingsImport", { lastSettingsImport }, { lastSettingsImport = it }),
        BooleanAttribute("remoteInvalidUntilFetched", { remoteInvalidUntilFetched }, { remoteInvalidUntilFetched = it }),
        BooleanAttribute("outputRemoteFetchExceptions", { outputRemoteFetchExceptions }, { outputRemoteFetchExceptions = it }),
        BooleanAttribute("reloadEditorsOnFileTypeChange", { reloadEditorsOnFileTypeChange }, { reloadEditorsOnFileTypeChange = it }),
        BooleanAttribute("reinitializeEditorsOnSettingsChange", { reinitializeEditorsOnSettingsChange }, { reinitializeEditorsOnSettingsChange = it }),
        BooleanAttribute("cacheSvgForSwing", { cacheSvgForSwing }, { cacheSvgForSwing = it }),
        BooleanAttribute("taskItemImages", { taskItemImages }, { taskItemImages = it }),
        BooleanAttribute("showSizePreferencesDialog", { showSizePreferencesDialog }, { showSizePreferencesDialog = it }),
        StringAttribute("translateFrom", true, { yandexFromLanguage }, { yandexFromLanguage = it }),
        StringAttribute("translateTo", true, { yandexToLanguage }, { yandexToLanguage = it }),
        StringAttribute("translateKey", true, { yandexKey }, { yandexKey = it }),
        BooleanAttribute("showTextHexDialog", { showTextHexDialog }, { showTextHexDialog = it }),
        BooleanAttribute("useFileLinkCache", { useFileLinkCache }, { useFileLinkCache = it })
    ))

    companion object {
        val DEFAULT: MdDebugSettings get() = MdDebugSettings()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MdDebugSettings) return false

        if (debugFormatText != other.debugFormatText) return false
        if (debugCombinationColors != other.debugCombinationColors) return false
        if (generateParserExceptions != other.generateParserExceptions) return false
        if (preferencesDialogWidth != other.preferencesDialogWidth) return false
        if (preferencesDialogHeight != other.preferencesDialogHeight) return false
        if (preferencesDialogMenuSplit != other.preferencesDialogMenuSplit) return false
        if (lastSettingsImport != other.lastSettingsImport) return false
        if (remoteInvalidUntilFetched != other.remoteInvalidUntilFetched) return false
        if (outputRemoteFetchExceptions != other.outputRemoteFetchExceptions) return false
        if (reloadEditorsOnFileTypeChange != other.reloadEditorsOnFileTypeChange) return false
        if (reinitializeEditorsOnSettingsChange != other.reinitializeEditorsOnSettingsChange) return false
        if (cacheSvgForSwing != other.cacheSvgForSwing) return false
        if (taskItemImages != other.taskItemImages) return false
        if (showSizePreferencesDialog != other.showSizePreferencesDialog) return false
        if (showTextHexDialog != other.showTextHexDialog) return false
        if (useFileLinkCache != other.useFileLinkCache) return false

        return mySettingsExtensions == other
    }

    override fun hashCode(): Int {
        var result = mySettingsExtensions.hashCode()
        result += 31 * result + debugFormatText.hashCode()
        result += 31 * result + debugCombinationColors.hashCode()
        result += 31 * result + generateParserExceptions.hashCode()
        result += 31 * result + preferencesDialogWidth.hashCode()
        result += 31 * result + preferencesDialogHeight.hashCode()
        result += 31 * result + preferencesDialogMenuSplit.hashCode()
        result += 31 * result + lastSettingsImport.hashCode()
        result += 31 * result + remoteInvalidUntilFetched.hashCode()
        result += 31 * result + outputRemoteFetchExceptions.hashCode()
        result += 31 * result + reloadEditorsOnFileTypeChange.hashCode()
        result += 31 * result + reinitializeEditorsOnSettingsChange.hashCode()
        result += 31 * result + cacheSvgForSwing.hashCode()
        result += 31 * result + taskItemImages.hashCode()
        result += 31 * result + showSizePreferencesDialog.hashCode()
        result += 31 * result + showTextHexDialog.hashCode()
        result += 31 * result + useFileLinkCache.hashCode()
        return result
    }
}
