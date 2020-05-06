// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.util.ui.UIUtil
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdProjectComponent
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.resources.HljsScriptProvider
import com.vladsch.md.nav.editor.resources.JavaFxHtmlCssProvider
import com.vladsch.md.nav.editor.resources.SwingHtmlCssProvider
import com.vladsch.md.nav.editor.resources.TextHtmlCssProvider
import com.vladsch.md.nav.editor.text.TextHtmlPanelProvider
import com.vladsch.md.nav.editor.util.*
import com.vladsch.md.nav.settings.api.MdExtendableSettings
import com.vladsch.md.nav.settings.api.MdExtendableSettingsImpl
import com.vladsch.md.nav.settings.api.MdSettings
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.md.nav.vcs.MdLinkResolver
import com.vladsch.plugin.util.suffixWith
import java.io.File
import java.util.*

class MdCssSettings(private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()) : StateHolderImpl({ MdCssSettings() }), MdExtendableSettings by mySettingsExtensions, MdSettings {
    init {
        initializeExtensions(this)
    }

    var previewScheme: MdCssSettings.PreviewScheme = PreviewScheme.UI_SCHEME
    var cssProviderInfo: HtmlCssResourceProvider.Info = TextHtmlCssProvider.INFO
    var htmlScriptProvidersInfo: ArrayList<HtmlScriptResourceProvider.Info> = arrayListOf()
    var isCssUriEnabled: Boolean = false
    var isCssUriSerial: Boolean = true
    var cssUri: String = ""
    var isCssTextEnabled: Boolean = false
    var cssText: String = ""
    var isDynamicPageWidth: Boolean = true
    val cssUriHistory = LinkedHashSet<String>()

    var cssUriHistoryList: List<String>
        get() = cssUriHistory.toList().reversed()
        set(value) {
            cssUriHistory.clear()
            cssUriHistory.addAll(value.reversed())
        }

    constructor(
        previewScheme: MdCssSettings.PreviewScheme
        , cssProviderInfo: HtmlCssResourceProvider.Info
        , htmlScriptProvidersInfo: ArrayList<HtmlScriptResourceProvider.Info>
        , isCssUriEnabled: Boolean
        , isCssUriSerial: Boolean
        , cssUri: String
        , isCssTextEnabled: Boolean
        , cssText: String
        , isDynamicPageWidth: Boolean
        , cssUriHistory: Collection<String>
    ) : this() {
        this.previewScheme = previewScheme
        this.cssProviderInfo = cssProviderInfo
        this.htmlScriptProvidersInfo = htmlScriptProvidersInfo
        this.isCssUriEnabled = isCssUriEnabled
        this.isCssUriSerial = isCssUriSerial
        this.cssUri = cssUri
        this.isCssTextEnabled = isCssTextEnabled
        this.cssText = cssText
        this.isDynamicPageWidth = isDynamicPageWidth
        this.cssUriHistory.addAll(cssUriHistory)
    }

    constructor(other: MdCssSettings) : this() {
        copyFrom(other)
    }

    override fun resetToDefaults() {
        copyFrom(DEFAULT, false)
    }

    fun copyFrom(other: MdCssSettings, withExtensions: Boolean = true) {
        previewScheme = other.previewScheme
        cssProviderInfo = HtmlCssResourceProvider.Info(other.cssProviderInfo)
        htmlScriptProvidersInfo = run {
            val array = ArrayList<HtmlScriptResourceProvider.Info>()
            for (info in other.htmlScriptProvidersInfo) {
                array.add(HtmlScriptResourceProvider.Info(info))
            }
            array
        }
        isCssUriEnabled = other.isCssUriEnabled
        isCssUriSerial = other.isCssUriSerial
        cssUri = other.cssUri
        isCssTextEnabled = other.isCssTextEnabled
        cssText = other.cssText
        isDynamicPageWidth = other.isDynamicPageWidth
        cssUriHistory.addAll(other.cssUriHistory)

        if (withExtensions) mySettingsExtensions.copyFrom(other)
    }

    override fun getStateHolder(): StateHolder = mySettingsExtensions.addItems(TagItemHolder("CssSettings").addItems(
        AttributeItem("previewScheme", { previewScheme.previewSchemeId }, { previewScheme = it }, { PreviewScheme.getPreviewScheme(it) }),
        TagItem("StylesheetProvider", { cssProviderInfo }, { cssProviderInfo = it }, { val item = HtmlCssResourceProvider.Info(); item.loadState(it); item }),
        CollectionItem("ScriptProviders", { htmlScriptProvidersInfo }, { htmlScriptProvidersInfo = it.toCollection(ArrayList()) }, { val info = HtmlScriptResourceProvider.Info(); info.loadState(it); info }),
        StringAttribute("cssUri", { cssUri }, { cssUri = it }),
        StringContent("cssText", { cssText }, { cssText = it }),
        BooleanAttribute("isCssUriEnabled", { isCssUriEnabled }, { isCssUriEnabled = it }),
        BooleanAttribute("isCssUriSerial", { isCssUriSerial }, { isCssUriSerial = it }),
        BooleanAttribute("isCssTextEnabled", { isCssTextEnabled }, { isCssTextEnabled = it }),
        BooleanAttribute("isDynamicPageWidth", { isDynamicPageWidth }, { isDynamicPageWidth = it }),
        StringHashSetItem("cssUriHistory", { cssUriHistory }, { cssUriHistory.clear(); cssUriHistory.addAll(it); })
    ))

    val isDarkTheme: Boolean
        get() = when (previewScheme) {
            PreviewScheme.UI_SCHEME -> UIUtil.isUnderDarcula()
            else -> previewScheme == PreviewScheme.DARK_SCHEME
        }

    val scriptProviders: List<HtmlScriptResourceProvider>
        get() {
            val scriptProviders = ArrayList<HtmlScriptResourceProvider>()
            // load script in order of dependencies between script providers
            for (it in this.htmlScriptProvidersInfo) {
                scriptProviders.add(HtmlScriptResourceProvider.getFromInfoOrDefault(it))
            }

            return scriptProviders.toList()
        }

    enum class PreviewScheme {
        UI_SCHEME, DARK_SCHEME, LIGHT_SCHEME;

        val displayName: String
            get() = when (this) {
                UI_SCHEME -> MdBundle.message("settings.html-theme-3")
                DARK_SCHEME -> MdBundle.message("settings.html-theme-2")
                LIGHT_SCHEME -> MdBundle.message("settings.html-theme-1")
            }

        val previewSchemeId: String
            get() = when (this) {
                UI_SCHEME -> "UI_SCHEME"
                DARK_SCHEME -> "DARK_SCHEME"
                LIGHT_SCHEME -> "LIGHT_SCHEME"
            }

        override fun toString(): String {
            return displayName
        }

        companion object {
            fun getPreviewScheme(previewSchemeId: String?): PreviewScheme =
                when (previewSchemeId) {
                    "DARK_SCHEME" -> PreviewScheme.DARK_SCHEME
                    "LIGHT_SCHEME" -> PreviewScheme.LIGHT_SCHEME
                    else -> PreviewScheme.UI_SCHEME
                }
        }
    }

    val cssResources: List<HtmlCssResource>
        get() {
            val cssResources = ArrayList<HtmlCssResource>()
            val htmlCssProvider = HtmlCssResourceProvider.getFromId(this.cssProviderInfo.providerId)

            if (htmlCssProvider != null) cssResources.add(htmlCssProvider.cssResource)

            // add any script desired css
            for (it in this.scriptProviders) {
                val scriptCssResource = it.cssResource
                if (scriptCssResource != null) {
                    cssResources.add(scriptCssResource)
                }
            }

            return cssResources.toList()
        }

    val cssResourcesNoScripts: List<HtmlCssResource>
        get() {
            val cssResources = ArrayList<HtmlCssResource>()
            val htmlCssProvider = HtmlCssResourceProvider.getFromId(cssProviderInfo.providerId)
            if (htmlCssProvider != null) {
                cssResources.add(htmlCssProvider.cssResource)
            }
            return cssResources.toList()
        }

    val scriptResources: List<HtmlScriptResource>
        get() {
            val scriptResources = ArrayList<HtmlScriptResource>()
            for (it in this.scriptProviders) {
                scriptResources.add(it.scriptResource)
            }
            return scriptResources.toList()
        }

    val cssProvider: HtmlCssResourceProvider
        get() {
            return HtmlCssResourceProvider.getFromInfoOrDefault(cssProviderInfo)
        }

    fun getWithScheme(previewScheme: PreviewScheme): MdCssSettings {
        if (previewScheme == this.previewScheme) return this
        return MdCssSettings(previewScheme, cssProviderInfo, htmlScriptProvidersInfo, isCssUriEnabled, isCssUriSerial, cssUri, isCssTextEnabled, cssText, isDynamicPageWidth, cssUriHistory)
    }

    interface Holder {
        var cssSettings: MdCssSettings
    }

    fun isDefault(htmlPanelProvider: HtmlPanelProvider.Info?): Boolean {
        return this == getDefaultSettings(htmlPanelProvider)
    }

    companion object {
        @JvmField
        val DYNAMIC_PAGE_WIDTH = "css.dynamicPageWidth"

        @JvmField
        val DEFAULT: MdCssSettings = MdCssSettings(PreviewScheme.UI_SCHEME, SwingHtmlCssProvider.INFO, arrayListOf<HtmlScriptResourceProvider.Info>(), false, true, "", false, "", true, listOf())

        //        @JvmField val LOBO_DEFAULT: MarkdownCssSettings = MarkdownCssSettings(PreviewScheme.UI_SCHEME, LoboHtmlCssProvider.INFO, arrayListOf<HtmlScriptResourceProvider.Info>(), false, true, "", false, "", true)
        @JvmField
        val JAVAFX_DEFAULT: MdCssSettings = MdCssSettings(PreviewScheme.UI_SCHEME, JavaFxHtmlCssProvider.INFO, arrayListOf(HljsScriptProvider.INFO), false, true, "", false, "", true, listOf())

        @JvmField
        val TEXT_DEFAULT: MdCssSettings = MdCssSettings(PreviewScheme.UI_SCHEME, TextHtmlCssProvider.INFO, arrayListOf<HtmlScriptResourceProvider.Info>(), false, true, "", false, "", true, listOf())

        @JvmField
        val UNLICENSED_DEFAULT: MdCssSettings = MdCssSettings(PreviewScheme.UI_SCHEME, SwingHtmlCssProvider.INFO, arrayListOf<HtmlScriptResourceProvider.Info>(), false, true, "", false, "", true, listOf())

        @JvmField
        val UNLICENSED_JAVAFX_DEFAULT: MdCssSettings = MdCssSettings(PreviewScheme.UI_SCHEME, JavaFxHtmlCssProvider.INFO, arrayListOf<HtmlScriptResourceProvider.Info>(), false, true, "", false, "", true, listOf())

        fun injectHtmlResource(linkResolver: MdLinkResolver, renderingProfile: MdRenderingProfile, injections: ArrayList<InjectHtmlResource?>): Unit {
            val dataContext = SimpleDataContext.getSimpleContext(Collections.emptyMap(), null)
            for (it in renderingProfile.cssSettings.cssResources) it.injectHtmlResource(linkResolver.project!!, MdApplicationSettings.instance, renderingProfile, injections, false, dataContext)
            for (it in renderingProfile.cssSettings.scriptResources) it.injectHtmlResource(linkResolver.project!!, MdApplicationSettings.instance, renderingProfile, injections, false, dataContext)

            var fullPath = PathInfo.removeFileUriPrefix(renderingProfile.cssSettings.cssUri)
            val cssSerial = if (linkResolver.project != null && renderingProfile.cssSettings.isCssUriEnabled && PathInfo.isLocal(renderingProfile.cssSettings.cssUri)) {
                // may need a serial
                if (!PathInfo.isAbsolute(fullPath)) {
                    val path = linkResolver.containingFile.path
                    val file = File((if (path.isEmpty()) "" else path.suffixWith("/")) + fullPath)
                    if (file.exists()) {
                        fullPath = file.canonicalPath
                    }
                }
                "?" + MdProjectComponent.getInstance(linkResolver.project).getFileSerial(fullPath).toString()
            } else {
                ""
            }
            HtmlResource.injectHtmlCssUrl(injections, renderingProfile.cssSettings.isCssUriEnabled, HtmlPlacement.HEAD_CSS_LAST, false, renderingProfile.cssSettings.cssUri + cssSerial)
            HtmlResource.injectHtmlText(injections, renderingProfile.cssSettings.isCssTextEnabled, HtmlPlacement.HEAD_CSS_LAST, false, HtmlResource.wrapCSSText(renderingProfile.cssSettings.cssText))
        }

        fun getDefaultSettings(htmlPanelProvider: HtmlPanelProvider.Info?): MdCssSettings {
            return when (htmlPanelProvider) {
                JavaFxHtmlPanelProvider.INFO -> JAVAFX_DEFAULT
                TextHtmlPanelProvider.INFO, null -> DEFAULT
                else -> DEFAULT
            }
        }
    }

    fun changeToProvider(fromPanelProviderInfo: HtmlPanelProvider.Info?, toPanelProviderInfo: HtmlPanelProvider.Info) {
        if (fromPanelProviderInfo != null && fromPanelProviderInfo != toPanelProviderInfo) {
            val previewSettings = MdPreviewSettings.getDefaultSettings(toPanelProviderInfo)
//            val provider = previewSettings.htmlPanelProvider
            val defaults = getDefaultSettings(previewSettings.htmlPanelProviderInfo)

            cssProviderInfo = defaults.cssProviderInfo
            htmlScriptProvidersInfo = defaults.htmlScriptProvidersInfo
        }
    }

    private fun getCompatibleScriptProviders(htmlPanelProvider: HtmlPanelProvider, scriptProvidersInfo: ArrayList<HtmlScriptResourceProvider.Info>): ArrayList<HtmlScriptResourceProvider.Info> {
        val compatible = ArrayList<HtmlScriptResourceProvider.Info>()
        for (htmlProviderInfo in scriptProvidersInfo) {
            if (htmlPanelProvider.COMPATIBILITY.isForAvailable(htmlPanelProvider.COMPATIBILITY)) {
                compatible.add(htmlProviderInfo)
            }
        }
        return compatible
    }

    fun scriptProvidersEquals(other: ArrayList<HtmlScriptResourceProvider.Info>): Boolean {
        if (htmlScriptProvidersInfo.size != other.size) return false

        // FIX: make the comparison unordered to be correct
        for (i in htmlScriptProvidersInfo.indices) {
            if (htmlScriptProvidersInfo[i] != other[i]) return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MdCssSettings) return false

        if (previewScheme != other.previewScheme) return false
        if (cssProviderInfo != other.cssProviderInfo) return false

        if (!scriptProvidersEquals(other.htmlScriptProvidersInfo)) return false

        if (isCssUriEnabled != other.isCssUriEnabled) return false
        if (isCssUriSerial != other.isCssUriSerial) return false
        if (cssUri != other.cssUri) return false
        if (isCssTextEnabled != other.isCssTextEnabled) return false
        if (cssText != other.cssText) return false
        if (isDynamicPageWidth != other.isDynamicPageWidth) return false

        return mySettingsExtensions == other
    }

    override fun hashCode(): Int {
        var result = mySettingsExtensions.hashCode()
        result += 31 * result + previewScheme.hashCode()
        result += 31 * result + cssProviderInfo.hashCode()
        result += 31 * result + htmlScriptProvidersInfo.hashCode()
        result += 31 * result + isCssUriEnabled.hashCode()
        result += 31 * result + cssUri.hashCode()
        result += 31 * result + isCssUriSerial.hashCode()
        result += 31 * result + isCssTextEnabled.hashCode()
        result += 31 * result + cssText.hashCode()
        result += 31 * result + isDynamicPageWidth.hashCode()
        return result
    }
}
