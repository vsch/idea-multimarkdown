// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.vladsch.md.nav.settings.api.MdExtendableSettings
import com.vladsch.md.nav.settings.api.MdExtendableSettingsImpl
import com.vladsch.md.nav.settings.api.MdSettings

class MdWasShownSettings(private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()) : StateHolderImpl({ MdWasShownSettings() }), MdExtendableSettings by mySettingsExtensions, MdSettings {
    init {
        initializeExtensions(this)
    }

    var licensedAvailable: Boolean = false
    var javaFxAvailable: Boolean = false
    var jbCefAvailable: Boolean = false
    private var _lastFeatureUpdateVersion: String = ""

    var lastFeatureUpdateVersion: String
        get() {
            return _lastFeatureUpdateVersion
        }
        set(value) {
            _lastFeatureUpdateVersion = value
        }

    var jekyllFrontMatter: Boolean = false
    var unicodeLineSeparator: Boolean = false
    var lastLicensedAvailableVersion: String = ""
    var gitHubSyntaxChange: Boolean = false
    var autoLinksExtension: Boolean = false
    var needLegacyUpdateChannel: Boolean = false
    var needStandardUpdateChannel: Boolean = false
    var plantUmlExtensionAvailable: Boolean = false

    interface Holder {
        var wasShownSettings: MdWasShownSettings
    }

    constructor(other: MdWasShownSettings) : this() {
        copyFrom(other)
    }

    override fun resetToDefaults() {
        copyFrom(DEFAULT, false)
    }

    fun copyFrom(other: MdWasShownSettings, withExtensions: Boolean = true) {
        licensedAvailable = other.licensedAvailable
        javaFxAvailable = other.javaFxAvailable
        jbCefAvailable = other.jbCefAvailable
        _lastFeatureUpdateVersion = other._lastFeatureUpdateVersion
        jekyllFrontMatter = other.jekyllFrontMatter
        unicodeLineSeparator = other.unicodeLineSeparator
        lastLicensedAvailableVersion = other.lastLicensedAvailableVersion
        gitHubSyntaxChange = other.gitHubSyntaxChange
        autoLinksExtension = other.autoLinksExtension
        needLegacyUpdateChannel = other.needLegacyUpdateChannel
        needStandardUpdateChannel = other.needStandardUpdateChannel
        plantUmlExtensionAvailable = other.plantUmlExtensionAvailable

        if (withExtensions) mySettingsExtensions.copyFrom(other)
    }

    override fun getStateHolder(): StateHolder = mySettingsExtensions.addItems(TagItemHolder("WasShown").addItems(
        BooleanAttribute("licensedAvailable", { licensedAvailable }, { licensedAvailable = it }),
        StringAttribute("lastFeatureUpdateVersion", { lastFeatureUpdateVersion }, { lastFeatureUpdateVersion = it }),
        StringAttribute("lastLicensedAvailableVersion", { lastLicensedAvailableVersion }, { lastLicensedAvailableVersion = it }),
        BooleanAttribute("javaFxAvailable", { javaFxAvailable }, { javaFxAvailable = it }),
        BooleanAttribute("jbCefAvailable", { jbCefAvailable }, { jbCefAvailable = it }),
        BooleanAttribute("jekyllFrontMatter", { jekyllFrontMatter }, { jekyllFrontMatter = it }),
        BooleanAttribute("unicodeLineSeparator", { unicodeLineSeparator }, { unicodeLineSeparator = it }),
        BooleanAttribute("gitHubSyntaxChange", { gitHubSyntaxChange }, { gitHubSyntaxChange = it }),
        BooleanAttribute("autoLinksExtension", { autoLinksExtension }, { autoLinksExtension = it }),
        BooleanAttribute("needLegacyUpdateChannel", { needLegacyUpdateChannel }, { needLegacyUpdateChannel = it }),
        BooleanAttribute("needStandardUpdateChannel", { needStandardUpdateChannel }, { needStandardUpdateChannel = it }),
        BooleanAttribute("plantUmlExtensionAvailable", { plantUmlExtensionAvailable }, { plantUmlExtensionAvailable = it })
    ))

    companion object {
        val DEFAULT: MdWasShownSettings get() = MdWasShownSettings()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MdWasShownSettings

        if (licensedAvailable != other.licensedAvailable) return false
        if (javaFxAvailable != other.javaFxAvailable) return false
        if (jbCefAvailable != other.jbCefAvailable) return false
        if (jekyllFrontMatter != other.jekyllFrontMatter) return false
        if (unicodeLineSeparator != other.unicodeLineSeparator) return false
        if (gitHubSyntaxChange != other.gitHubSyntaxChange) return false
        if (autoLinksExtension != other.autoLinksExtension) return false
        if (needLegacyUpdateChannel != other.needLegacyUpdateChannel) return false
        if (needStandardUpdateChannel != other.needStandardUpdateChannel) return false
        if (lastFeatureUpdateVersion != other.lastFeatureUpdateVersion) return false
        if (lastLicensedAvailableVersion != other.lastLicensedAvailableVersion) return false
        if (plantUmlExtensionAvailable != other.plantUmlExtensionAvailable) return false

        return mySettingsExtensions == other
    }

    override fun hashCode(): Int {
        var result = mySettingsExtensions.hashCode()
        result += 31 * result + licensedAvailable.hashCode()
        result += 31 * result + javaFxAvailable.hashCode()
        result += 31 * result + jbCefAvailable.hashCode()
        result += 31 * result + jekyllFrontMatter.hashCode()
        result += 31 * result + unicodeLineSeparator.hashCode()
        result += 31 * result + gitHubSyntaxChange.hashCode()
        result += 31 * result + autoLinksExtension.hashCode()
        result += 31 * result + needLegacyUpdateChannel.hashCode()
        result += 31 * result + needStandardUpdateChannel.hashCode()
        result += 31 * result + lastFeatureUpdateVersion.hashCode()
        result += 31 * result + lastLicensedAvailableVersion.hashCode()
        result += 31 * result + plantUmlExtensionAvailable.hashCode()
        return result
    }
}
