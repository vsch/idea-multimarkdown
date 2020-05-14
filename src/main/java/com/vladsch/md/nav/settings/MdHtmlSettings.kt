// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.resources.JavaFxHtmlGeneratorProvider
import com.vladsch.md.nav.editor.resources.SwingHtmlGeneratorProvider
import com.vladsch.md.nav.editor.resources.TextHtmlGeneratorProvider
import com.vladsch.md.nav.editor.text.TextHtmlPanelProvider
import com.vladsch.md.nav.editor.util.*
import com.vladsch.md.nav.settings.api.MdExtendableSettings
import com.vladsch.md.nav.settings.api.MdExtendableSettingsImpl
import com.vladsch.md.nav.settings.api.MdSettings
import java.util.*
import kotlin.collections.HashMap

class MdHtmlSettings(private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()) : StateHolderImpl({ MdHtmlSettings() }), MdExtendableSettings by mySettingsExtensions, MdSettings {
    init {
        initializeExtensions(this)
    }

    var htmlGeneratorProviderInfo: HtmlGeneratorProvider.Info = TextHtmlGeneratorProvider.INFO
    var headTop: String = ""
    var headTopEnabled: Boolean = false
    var headBottom: String = ""
    var headBottomEnabled: Boolean = false
    var bodyTop: String = ""
    var bodyTopEnabled: Boolean = false
    var bodyBottom: String = ""
    var bodyBottomEnabled: Boolean = false
    var addPageHeader: Boolean = false
    var addAnchorLinks: Boolean = true
    var anchorLinksWrapText: Boolean = false
    var imageUriSerials: Boolean = false
    var addDocTypeHtml: Boolean = true
    var noParaTags: Boolean = false
    var plantUmlConversion: Int = PlantUmlConversionType.EMBEDDED.intValue
    var migratedPlantUml: Boolean = false

    var plantUmlConversionType: PlantUmlConversionType
        get() = PlantUmlConversionType.ADAPTER.get(plantUmlConversion)
        set(value) {
            plantUmlConversion = value.intValue
        }

    val htmlGeneratorProvider: HtmlGeneratorProvider get() = HtmlGeneratorProvider.getFromInfoOrDefault(htmlGeneratorProviderInfo)

    override fun resetToDefaults() {
        copyFrom(DEFAULT, false)
    }

    val fencedCodeConversions: HashMap<String, String> = HashMap()

    fun copyFrom(other: MdHtmlSettings, withExtensions: Boolean = true) {
        this.htmlGeneratorProviderInfo = other.htmlGeneratorProviderInfo
        this.headTop = other.headTop
        this.headTopEnabled = other.headTopEnabled
        this.headBottom = other.headBottom
        this.headBottomEnabled = other.headBottomEnabled
        this.bodyTop = other.bodyTop
        this.bodyTopEnabled = other.bodyTopEnabled
        this.bodyBottom = other.bodyBottom
        this.bodyBottomEnabled = other.bodyBottomEnabled
        this.addPageHeader = other.addPageHeader
        this.addAnchorLinks = other.addAnchorLinks
        this.anchorLinksWrapText = other.anchorLinksWrapText
        this.imageUriSerials = other.imageUriSerials
        this.addDocTypeHtml = other.addDocTypeHtml
        this.noParaTags = other.noParaTags
        this.migratedPlantUml = other.migratedPlantUml
        this.plantUmlConversion = other.plantUmlConversion

        this.fencedCodeConversions.clear()
        this.fencedCodeConversions.putAll(other.fencedCodeConversions)

        if (withExtensions) mySettingsExtensions.copyFrom(other)
    }

    override fun getStateHolder(): StateHolder {
        return mySettingsExtensions.addItems(TagItemHolder("HtmlSettings").addItems(
            TagItem("GeneratorProvider", { htmlGeneratorProviderInfo }, { htmlGeneratorProviderInfo = it }, { val item = HtmlGeneratorProvider.Info(); item.loadState(it); item }),
            StringContent("headerTop", { headTop }, { headTop = it }),
            StringContent("headerBottom", { headBottom }, { headBottom = it }),
            StringContent("bodyTop", { bodyTop }, { bodyTop = it }),
            StringContent("bodyBottom", { bodyBottom }, { bodyBottom = it }),
            BooleanAttribute("headerTopEnabled", { headTopEnabled }, { headTopEnabled = it }),
            BooleanAttribute("headerBottomEnabled", { headBottomEnabled }, { headBottomEnabled = it }),
            BooleanAttribute("bodyTopEnabled", { bodyTopEnabled }, { bodyTopEnabled = it }),
            BooleanAttribute("bodyBottomEnabled", { bodyBottomEnabled }, { bodyBottomEnabled = it }),
            BooleanAttribute("addPageHeader", { addPageHeader }, { addPageHeader = it }),
            BooleanAttribute("addAnchorLinks", { addAnchorLinks }, { addAnchorLinks = it }),
            BooleanAttribute("anchorLinksWrapText", { anchorLinksWrapText }, { anchorLinksWrapText = it }),
            BooleanAttribute("imageUriSerials", { imageUriSerials }, { imageUriSerials = it }),
            BooleanAttribute("addDocTypeHtml", { addDocTypeHtml }, { addDocTypeHtml = it }),
            BooleanAttribute("noParaTags", { noParaTags }, { noParaTags = it }),
            BooleanAttribute("migratedPlantUml", { migratedPlantUml }, { migratedPlantUml = it }),
            IntAttribute("plantUmlConversion", { plantUmlConversion }, { plantUmlConversion = it }),
            MapItem("fencedCodeConversions",
                { fencedCodeConversions },
                { fencedCodeConversions.clear(); fencedCodeConversions.putAll(it) },
                { key, value -> Pair(key, value) },
                { key, value -> if (value != null) Pair(key, value) else null })
        ))
    }

    constructor(
        htmlGeneratorProviderInfo: HtmlGeneratorProvider.Info
        , headerTop: String
        , headerTopEnabled: Boolean
        , headerBottom: String
        , headerBottomEnabled: Boolean
        , bodyTop: String
        , bodyTopEnabled: Boolean
        , bodyBottom: String
        , bodyBottomEnabled: Boolean
        , addPageHeader: Boolean
        , imageUriSerials: Boolean
        , addDocTypeHtml: Boolean
        , noParaTags: Boolean
        , plantUmlConversion: Int
    ) : this() {
        this.htmlGeneratorProviderInfo = htmlGeneratorProviderInfo
        this.headTop = headerTop
        this.headTopEnabled = headerTopEnabled
        this.headBottom = headerBottom
        this.headBottomEnabled = headerBottomEnabled
        this.bodyTop = bodyTop
        this.bodyTopEnabled = bodyTopEnabled
        this.bodyBottom = bodyBottom
        this.bodyBottomEnabled = bodyBottomEnabled
        this.addPageHeader = addPageHeader
        this.imageUriSerials = imageUriSerials
        this.addDocTypeHtml = addDocTypeHtml
        this.noParaTags = noParaTags
        this.plantUmlConversion = plantUmlConversion
    }

    constructor(other: MdHtmlSettings) : this() {
        this.htmlGeneratorProviderInfo = other.htmlGeneratorProviderInfo
        this.headTop = other.headTop
        this.headTopEnabled = other.headTopEnabled
        this.headBottom = other.headBottom
        this.headBottomEnabled = other.headBottomEnabled
        this.bodyTop = other.bodyTop
        this.bodyTopEnabled = other.bodyTopEnabled
        this.bodyBottom = other.bodyBottom
        this.bodyBottomEnabled = other.bodyBottomEnabled
        this.addPageHeader = other.addPageHeader
        this.addAnchorLinks = other.addAnchorLinks
        this.anchorLinksWrapText = other.anchorLinksWrapText
        this.imageUriSerials = other.imageUriSerials
        this.addDocTypeHtml = other.addDocTypeHtml
        this.noParaTags = other.noParaTags
        this.migratedPlantUml = other.migratedPlantUml
        this.plantUmlConversion = other.plantUmlConversion

        this.fencedCodeConversions.clear()
        this.fencedCodeConversions.putAll(other.fencedCodeConversions)

        mySettingsExtensions.copyFrom(other)
    }

    interface Holder {
        var htmlSettings: MdHtmlSettings
    }

    fun isDefault(htmlPanelProvider: HtmlPanelProvider.Info?): Boolean {
        return this == getDefaultSettings(htmlPanelProvider)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MdHtmlSettings

        if (htmlGeneratorProviderInfo != other.htmlGeneratorProviderInfo) return false
        if (headTop != other.headTop) return false
        if (headTopEnabled != other.headTopEnabled) return false
        if (headBottom != other.headBottom) return false
        if (headBottomEnabled != other.headBottomEnabled) return false
        if (bodyTop != other.bodyTop) return false
        if (bodyTopEnabled != other.bodyTopEnabled) return false
        if (bodyBottom != other.bodyBottom) return false
        if (bodyBottomEnabled != other.bodyBottomEnabled) return false
        if (addPageHeader != other.addPageHeader) return false
        if (addAnchorLinks != other.addAnchorLinks) return false
        if (anchorLinksWrapText != other.anchorLinksWrapText) return false
        if (imageUriSerials != other.imageUriSerials) return false
        if (addDocTypeHtml != other.addDocTypeHtml) return false
        if (noParaTags != other.noParaTags) return false
        if (migratedPlantUml != other.migratedPlantUml) return false
        if (plantUmlConversion != other.plantUmlConversion) return false
        if (fencedCodeConversions != other.fencedCodeConversions) return false

        return mySettingsExtensions == other
    }

    override fun hashCode(): Int {
        var result = mySettingsExtensions.hashCode()
        result += 31 * result + htmlGeneratorProviderInfo.hashCode()
        result += 31 * result + headTop.hashCode()
        result += 31 * result + headTopEnabled.hashCode()
        result += 31 * result + headBottom.hashCode()
        result += 31 * result + headBottomEnabled.hashCode()
        result += 31 * result + bodyTop.hashCode()
        result += 31 * result + bodyTopEnabled.hashCode()
        result += 31 * result + bodyBottom.hashCode()
        result += 31 * result + bodyBottomEnabled.hashCode()
        result += 31 * result + addPageHeader.hashCode()
        result += 31 * result + addAnchorLinks.hashCode()
        result += 31 * result + anchorLinksWrapText.hashCode()
        result += 31 * result + imageUriSerials.hashCode()
        result += 31 * result + addDocTypeHtml.hashCode()
        result += 31 * result + noParaTags.hashCode()
        result += 31 * result + migratedPlantUml.hashCode()
        result += 31 * result + plantUmlConversion.hashCode()
        result += 31 * result + fencedCodeConversions.hashCode()
        return result
    }

    fun changeToProvider(fromPanelProviderInfo: HtmlPanelProvider.Info?, toPanelProviderInfo: HtmlPanelProvider.Info) {
        val defaults = getDefaultSettings(toPanelProviderInfo)
        htmlGeneratorProviderInfo = if (fromPanelProviderInfo == toPanelProviderInfo) htmlGeneratorProviderInfo else defaults.htmlGeneratorProviderInfo
    }

    companion object {
        val DEFAULT: MdHtmlSettings by lazy { MdHtmlSettings(SwingHtmlGeneratorProvider.INFO, "", false, "", false, "", false, "", false, true, false, true, false, 0) }
        val JAVAFX_DEFAULT: MdHtmlSettings by lazy { MdHtmlSettings(JavaFxHtmlGeneratorProvider.INFO, "", false, "", false, "", false, "", false, true, false, true, false, 0) }
        val TEXT_DEFAULT: MdHtmlSettings by lazy { MdHtmlSettings(TextHtmlGeneratorProvider.INFO, "", false, "", false, "", false, "", false, false, false, false, false, 0) }
        const val ADD_PAGE_HEADER: String = "html.addPageHeader"
        const val ADD_DOC_TYPE_HTML: String = "html.addDocTypeHtml"

        fun injectHtmlResource(renderingProfile: MdRenderingProfile, injections: ArrayList<InjectHtmlResource?>): Unit {
            HtmlResource.injectHtmlText(injections, renderingProfile.htmlSettings.headTopEnabled, HtmlPlacement.HEAD_TOP, false, renderingProfile.htmlSettings.headTop)
            HtmlResource.injectHtmlText(injections, renderingProfile.htmlSettings.headBottomEnabled, HtmlPlacement.HEAD_BOTTOM, false, renderingProfile.htmlSettings.headBottom)
            HtmlResource.injectHtmlText(injections, renderingProfile.htmlSettings.bodyTopEnabled, HtmlPlacement.BODY_TOP, false, renderingProfile.htmlSettings.bodyTop)
            HtmlResource.injectHtmlText(injections, renderingProfile.htmlSettings.bodyBottomEnabled, HtmlPlacement.BODY_BOTTOM, false, renderingProfile.htmlSettings.bodyBottom)
        }

        fun getDefaultSettings(htmlPanelProvider: HtmlPanelProvider.Info?): MdHtmlSettings {
            return when (htmlPanelProvider) {
                null -> DEFAULT
                JavaFxHtmlPanelProvider.INFO -> JAVAFX_DEFAULT
                TextHtmlPanelProvider.INFO -> TEXT_DEFAULT
                else -> {
                    DEFAULT
                }
            }
        }
    }
}
