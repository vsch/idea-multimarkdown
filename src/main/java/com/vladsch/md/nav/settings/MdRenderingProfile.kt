// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.openapi.project.Project
import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.md.nav.editor.util.HtmlPanelProvider
import com.vladsch.md.nav.editor.util.InjectHtmlResource
import com.vladsch.md.nav.language.MdCodeStyleSettings
import com.vladsch.md.nav.settings.api.*
import com.vladsch.md.nav.vcs.MdLinkResolver
import java.util.*
import kotlin.collections.LinkedHashSet

class MdRenderingProfile(private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()) : StateHolderImpl({ MdRenderingProfile() })
    , MdExtendableSettings by mySettingsExtensions
    , MdRenderingProfileHolder {

    private val myHaveExtensionSettings = LinkedHashSet<DataKey<*>>()

    init {
        mySettingsExtensions.initializeExtensions(this)

        // set all extensions as overriding project settings if they have isOverride by default
        mySettingsExtensions.extensionKeys.forEach {
            if ((it.get(extensions) as MdOverrideSettings).isOverrideByDefault) {
                myHaveExtensionSettings.add(it)
            }
        }
    }

    var profileName: String = ""

    /**
     * this one is the default selected on render settings as default project profile
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var isDefaultProfile: Boolean = false
        private set

    /**
     * Project Default ie. the one shown in main settings
     */
    var isProjectProfile: Boolean = false
        private set

    private val _previewSettings: MdPreviewSettings = MdPreviewSettings()
    private val _parserSettings: MdParserSettings = MdParserSettings()
    private val _htmlSettings: MdHtmlSettings = MdHtmlSettings()
    private val _cssSettings: MdCssSettings = MdCssSettings()
    private val _styleSettings: MdCodeStyleSettings = MdCodeStyleSettings.getInstance()
    
    // these are project style settings
    private var _project: Project? = null

    override var previewSettings: MdPreviewSettings
        get() = _previewSettings
        set(value) = _previewSettings.copyFrom(value)

    override var parserSettings: MdParserSettings
        get() = _parserSettings
        set(value) = _parserSettings.copyFrom(value)

    override var htmlSettings: MdHtmlSettings
        get() = _htmlSettings
        set(value) = _htmlSettings.copyFrom(value)

    override var cssSettings: MdCssSettings
        get() = _cssSettings
        set(value) = _cssSettings.copyFrom(value)

    // FIX: complete implementation of rendering profile code style settings
    val resolvedStyleSettings: MdCodeStyleSettings
        get() {
            val project = _project
            return if ((isProjectProfile || !haveStyleSettings) && project != null) MdCodeStyleSettings.getInstance(project) else _styleSettings
        }

    private val isProjectStyleSettings: Boolean get() = isProjectProfile && _project != null

    override fun getStyleSettings(): MdCodeStyleSettings {
        val project = _project
        return if (isProjectProfile && project != null) MdCodeStyleSettings.getInstance(project) else _styleSettings
    }

    override fun setStyleSettings(value: MdCodeStyleSettings) {
        val project = _project
        if (isProjectProfile && project != null) MdCodeStyleSettings.getInstance(project).copyFrom(value)
        else _styleSettings.copyFrom(value)
    }

    override fun validateLoadedSettings() {
        // Allow extensions to validate profile settings and to migrate them if necessary
        for (validator in MdRenderingProfileValidator.EXTENSIONS.value) {
            validator.validateSettings(renderingProfile)
        }

        mySettingsExtensions.validateLoadedSettings()

        _previewSettings.validateLoadedSettings()
        _parserSettings.validateLoadedSettings()
        _htmlSettings.validateLoadedSettings()
        _cssSettings.validateLoadedSettings()
    }

    fun getProject(): Project? {
        return _project
    }

    fun setProject(project: Project) {
        _project = project
    }

    fun setProjectProfile(project: Project) {
        assert(this._project == null)
        this._project = project
        haveStyleSettings = false
        haveRightMargin = false
        isProjectProfile = true
    }

    // NOTE: changing to false messes up existing profiles
    var havePreviewSettings: Boolean = true
    var haveParserSettings: Boolean = true
    var haveHtmlSettings: Boolean = true
    var haveCssSettings: Boolean = true
    var haveStyleSettings: Boolean = false
    var haveRightMargin: Boolean = false

    @Suppress("PropertyName")
    var RIGHT_MARGIN: Int = MdCodeStyleSettings.DEFAULT_RIGHT_MARGIN

    fun getRightMargin(): Int {
        if (!haveRightMargin || (this.RIGHT_MARGIN < MdCodeStyleSettings.MIN_RIGHT_MARGIN || this.RIGHT_MARGIN > MdCodeStyleSettings.MAX_RIGHT_MARGIN)) {
            if (isProjectStyleSettings || !haveStyleSettings || (_styleSettings.RIGHT_MARGIN < MdCodeStyleSettings.MIN_RIGHT_MARGIN || _styleSettings.RIGHT_MARGIN > MdCodeStyleSettings.MAX_RIGHT_MARGIN)) {
                return if (_project != null) {
                    resolvedStyleSettings.rightMargin
                } else {
                    MdCodeStyleSettings.DEFAULT_RIGHT_MARGIN
                }
            }
            return _styleSettings.RIGHT_MARGIN
        }
        return this.RIGHT_MARGIN
    }

    fun <T : MdSettingsExtension<T>> getHaveExtensionSettings(key: DataKey<T>): Boolean {
        return myHaveExtensionSettings.contains(key)
    }

    fun <T : MdSettingsExtension<T>> setHaveExtensionSettings(key: DataKey<T>, value: Boolean) {
        if (value) {
            myHaveExtensionSettings.add(key)
        } else {
            myHaveExtensionSettings.remove(key)
        }
    }

    private constructor(
        isDefaultProfile: Boolean
        , profileName: String
        , previewSettings: MdPreviewSettings
        , parserSettings: MdParserSettings
        , htmlSettings: MdHtmlSettings
        , cssSettings: MdCssSettings
        , styleSettings: MdCodeStyleSettings
    ) : this() {
        this.isDefaultProfile = isDefaultProfile
        this.profileName = profileName
        this.previewSettings = previewSettings
        this.parserSettings = parserSettings
        this.htmlSettings = htmlSettings
        this.cssSettings = cssSettings
        this.styleSettings = styleSettings
    }

    constructor(other: MdRenderingProfile) : this() {
        copyFrom(other, true)
    }

    fun copyFrom(other: MdRenderingProfile, withExtensions: Boolean) {
        if (this !== other) {
            profileName = other.profileName
            previewSettings = other.previewSettings
            parserSettings = other.parserSettings
            htmlSettings = other.htmlSettings
            cssSettings = other.cssSettings

            havePreviewSettings = other.havePreviewSettings
            haveParserSettings = other.haveParserSettings
            haveHtmlSettings = other.haveHtmlSettings
            haveCssSettings = other.haveCssSettings

            _project = _project ?: other._project
            _styleSettings.copyFrom(other.styleSettings)
            haveStyleSettings = other.haveStyleSettings
            haveRightMargin = other.haveRightMargin
            RIGHT_MARGIN = other.RIGHT_MARGIN

            if (withExtensions) {
                mySettingsExtensions.copyFrom(other)
                myHaveExtensionSettings.clear()
                myHaveExtensionSettings.addAll(other.myHaveExtensionSettings.intersect(mySettingsExtensions.extensionKeys))
            }
        }
    }

    fun isDefault(): Boolean {
        if (!(havePreviewSettings && haveParserSettings && haveHtmlSettings && haveCssSettings && !haveStyleSettings && !haveRightMargin)) return false

        if (!mySettingsExtensions.extensionKeys.all { key ->
                myHaveExtensionSettings.contains(key)
            }) return false

        @Suppress("SuspiciousEqualsCombination")
        if (!(previewSettings.isDefault(previewSettings.htmlPanelProviderInfo) &&
                parserSettings.isDefault(previewSettings.htmlPanelProviderInfo) &&
                htmlSettings.isDefault(previewSettings.htmlPanelProviderInfo) &&
                cssSettings.isDefault(previewSettings.htmlPanelProviderInfo) &&
                (isProjectStyleSettings || _styleSettings == MdCodeStyleSettings.DEFAULT))) return false

        if (!mySettingsExtensions.extensionKeys.all { key ->
                // NOTE: kotlin compiler spews up if method changed to property access
                @Suppress("UsePropertyAccessSyntax")
                key[mySettingsExtensions.extensions].isDefault()
            }) return false
        return true
    }

    override fun isExtensionDefault(key: DataKey<MdSettingsExtension<*>>): Boolean {
        if (!mySettingsExtensions.extensionKeys.contains(key)) return true
        if (!myHaveExtensionSettings.contains(key)) return false

        val defaultSetting = key.defaultValue
        val settingsExtension = key[mySettingsExtensions.extensions]
        return settingsExtension == defaultSetting
    }

    override var renderingProfile: MdRenderingProfile
        get() = this
        set(value) {
            copyFrom(value, true)
        }

    override fun getResolvedProfile(parentProfile: MdRenderingProfile): MdRenderingProfile {
        if (havePreviewSettings && haveParserSettings && haveCssSettings && haveHtmlSettings) {
            return this
        } else {
            val newRenderingProfile = MdRenderingProfile()

            newRenderingProfile.profileName = profileName
            newRenderingProfile.previewSettings = if (havePreviewSettings) previewSettings else parentProfile.previewSettings
            newRenderingProfile.parserSettings = if (haveParserSettings) parserSettings else parentProfile.parserSettings
            newRenderingProfile.htmlSettings = if (haveHtmlSettings) htmlSettings else parentProfile.htmlSettings
            newRenderingProfile.cssSettings = if (haveCssSettings) cssSettings else parentProfile.cssSettings

            // these are copied but resolve to project code style settings on their own
            newRenderingProfile._project = _project ?: parentProfile._project
            newRenderingProfile.haveStyleSettings = haveStyleSettings
            newRenderingProfile.haveRightMargin = haveRightMargin
            newRenderingProfile._styleSettings.copyFrom(styleSettings)
            newRenderingProfile.RIGHT_MARGIN = RIGHT_MARGIN

            mySettingsExtensions.extensionKeys.forEach { key ->
                if (!myHaveExtensionSettings.contains(key) || !mySettingsExtensions.extensions.contains(key)) {
                    newRenderingProfile.mySettingsExtensions.extensions.set(key, key[parentProfile.mySettingsExtensions.extensions])
                } else {
                    newRenderingProfile.mySettingsExtensions.extensions.set(key, key[mySettingsExtensions.extensions])
                }
                newRenderingProfile.myHaveExtensionSettings.add(key)
            }

            return newRenderingProfile
        }
    }

    override fun groupNotifications(runnable: Runnable) {
        runnable.run()
    }

    override fun getStateHolder(): StateHolder {
        val tagItemHolder = TagItemHolder(STATE_ELEMENT_NAME).addItems(
            StringAttribute("name", { "myName" }, { }), // needed for rendering profiles, is a read only field, ie. it is written out to storage but not read in but used by IDE configuration state splitter
            StringAttribute("value", { profileName }, { profileName = it }),
            StringAttribute("profileName", { profileName }, { profileName = it }),

            BooleanAttribute("havePreviewSettings", { havePreviewSettings }, { havePreviewSettings = it }),
            BooleanAttribute("haveParserSettings", { haveParserSettings }, { haveParserSettings = it }),
            BooleanAttribute("haveHtmlSettings", { haveHtmlSettings }, { haveHtmlSettings = it }),
            BooleanAttribute("haveCssSettings", { haveCssSettings }, { haveCssSettings = it }),
            BooleanAttribute("haveStyleSettings", { haveStyleSettings }, { haveStyleSettings = it }),
            BooleanAttribute("haveRightMargin", { haveRightMargin }, { haveRightMargin = it }),
            IntAttribute("rightMargin", { this.RIGHT_MARGIN }, { this.RIGHT_MARGIN = it })
        )

        mySettingsExtensions.extensionKeys.forEach { key ->
            // add haveSettings attribute
            val name = getKeyHaveSettings(key)
            tagItemHolder.addItems(BooleanAttribute(name, { myHaveExtensionSettings.contains(key) }, {
                if (it) myHaveExtensionSettings.add(key)
                else myHaveExtensionSettings.remove(key)
            }))
        }

        addUnwrappedItems(this, tagItemHolder)

        return tagItemHolder
    }

    private val codeStyleSettingsSerializable: MdCodeStyleSettingsSerializable get() = MdCodeStyleSettingsSerializable(_styleSettings)

    override fun addUnwrappedItems(container: Any, tagItemHolder: TagItemHolder): TagItemHolder {
        mySettingsExtensions.addUnwrappedItems(container, tagItemHolder.addItems(
            UnwrappedSettings(previewSettings),
            UnwrappedSettings(parserSettings),
            UnwrappedSettings(htmlSettings),
            UnwrappedSettings(cssSettings)
        ))

        // FIX: enable when UI is ready
        if (!isProjectProfile && false) {
            // NOTE: no need to create a new item. Factory just loads settings into the instance
            val codeStyleSettingsSerializable1 = codeStyleSettingsSerializable
            tagItemHolder.addItems(UnwrappedItem({ codeStyleSettingsSerializable1 }, {}, {
                codeStyleSettingsSerializable1.loadState(it)
                codeStyleSettingsSerializable1
            }))
        }

        previewSettings.addUnwrappedItems(container, tagItemHolder)
        parserSettings.addUnwrappedItems(container, tagItemHolder)
        htmlSettings.addUnwrappedItems(container, tagItemHolder)
        cssSettings.addUnwrappedItems(container, tagItemHolder)

        return tagItemHolder
    }

    fun injectHtmlResource(linkResolver: MdLinkResolver, injections: ArrayList<InjectHtmlResource?>) {
        MdPreviewSettings.injectHtmlResource(linkResolver, this, injections)
        MdCssSettings.injectHtmlResource(linkResolver, this, injections)
        MdHtmlSettings.injectHtmlResource(this, injections)
    }

    fun getName(): String {
        return profileName
    }

    fun setName(name: String) {
        profileName = name
    }

    companion object {
        const val STATE_ELEMENT_NAME: String = "option"

        private fun getKeyHaveSettings(key: DataKey<*>): String {
            val name = key.name
            var useName = name
            val pos = name.lastIndexOf('.')
            if (pos >= 0) useName = useName.substring(pos + 1)
            useName = useName.removePrefix("MdEnh").removePrefix("Md")
            useName = "have$useName"
            return useName
        }

        @JvmStatic
        val DEFAULT: MdRenderingProfile = MdRenderingProfile(true, "", MdPreviewSettings.DEFAULT, MdParserSettings.DEFAULT, MdHtmlSettings.DEFAULT, MdCssSettings.DEFAULT, MdCodeStyleSettings.DEFAULT)

        @JvmStatic
        val FOR_SAMPLE_DOC: MdRenderingProfile = MdRenderingProfile(true, "", MdPreviewSettings.DEFAULT, MdParserSettings.FOR_SAMPLE_DOC, MdHtmlSettings.DEFAULT, MdCssSettings.DEFAULT, MdCodeStyleSettings.DEFAULT)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MdRenderingProfile

        if (havePreviewSettings != other.havePreviewSettings) return false
        if (haveParserSettings != other.haveParserSettings) return false
        if (haveHtmlSettings != other.haveHtmlSettings) return false
        if (haveCssSettings != other.haveCssSettings) return false
        if (haveStyleSettings != other.haveStyleSettings) return false
        if (haveRightMargin != other.haveRightMargin) return false

        if (!myHaveExtensionSettings.containsAll(other.myHaveExtensionSettings)) return false
        if (!other.myHaveExtensionSettings.containsAll(myHaveExtensionSettings)) return false

        if (profileName != other.profileName) return false

        if (previewSettings != other.previewSettings) return false
        if (parserSettings != other.parserSettings) return false
        if (htmlSettings != other.htmlSettings) return false
        if (cssSettings != other.cssSettings) return false
        if (!isProjectProfile && haveStyleSettings && _styleSettings != other._styleSettings) return false
        if (RIGHT_MARGIN != other.RIGHT_MARGIN) return false

        return mySettingsExtensions == other
    }

    override fun hashCode(): Int {
        var result = mySettingsExtensions.hashCode()
        result += 31 * result + myHaveExtensionSettings.hashCode()

        result += 31 * result + profileName.hashCode()
        result += 31 * result + previewSettings.hashCode()
        result += 31 * result + parserSettings.hashCode()
        result += 31 * result + htmlSettings.hashCode()
        result += 31 * result + cssSettings.hashCode()
        result += 31 * result + _styleSettings.hashCode()
        result += 31 * result + RIGHT_MARGIN.hashCode()
        result += 31 * result + havePreviewSettings.hashCode()
        result += 31 * result + haveParserSettings.hashCode()
        result += 31 * result + haveHtmlSettings.hashCode()
        result += 31 * result + haveCssSettings.hashCode()
        result += 31 * result + haveStyleSettings.hashCode()
        result += 31 * result + haveRightMargin.hashCode()
        return result
    }

    fun changeToProvider(fromPanelProviderInfo: HtmlPanelProvider.Info?, toPanelProviderInfo: HtmlPanelProvider.Info): MdRenderingProfile {
        val newRenderingProfile = MdRenderingProfile(this)

        newRenderingProfile.previewSettings.changeToProvider(fromPanelProviderInfo, toPanelProviderInfo)
        newRenderingProfile.cssSettings.changeToProvider(fromPanelProviderInfo, toPanelProviderInfo)
        newRenderingProfile.htmlSettings.changeToProvider(fromPanelProviderInfo, toPanelProviderInfo)
        newRenderingProfile.parserSettings.changeToProvider(fromPanelProviderInfo, toPanelProviderInfo)

        mySettingsExtensions.containedExtensionKeys.forEach {
            it[newRenderingProfile.extensions].copyFrom(extensions)
            it[newRenderingProfile.extensions].changeToProvider(fromPanelProviderInfo, toPanelProviderInfo)
        }
        return newRenderingProfile
    }
}
