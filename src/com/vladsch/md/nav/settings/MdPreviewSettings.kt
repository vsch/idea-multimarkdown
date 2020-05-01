// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.intellij.ide.ui.AntialiasingType
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.ui.JBUI
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.split.SplitFileEditor.SplitEditorLayout
import com.vladsch.md.nav.editor.split.SplitFileEditor.SplitEditorPreviewType
import com.vladsch.md.nav.editor.swing.SwingHtmlPanelProvider
import com.vladsch.md.nav.editor.text.TextHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlPanelProvider
import com.vladsch.md.nav.editor.util.InjectHtmlResource
import com.vladsch.md.nav.settings.api.MdExtendableSettings
import com.vladsch.md.nav.settings.api.MdExtendableSettingsImpl
import com.vladsch.md.nav.settings.api.MdSettings
import com.vladsch.md.nav.vcs.MdLinkResolver
import java.util.*

class MdPreviewSettings(private val mySettingsExtensions: MdExtendableSettingsImpl = MdExtendableSettingsImpl()) : StateHolderImpl({ MdPreviewSettings() }), MdExtendableSettings by mySettingsExtensions, MdSettings {
    init {
        initializeExtensions(this)
    }

    var splitEditorLayout: SplitEditorLayout = SplitEditorLayout.SPLIT
    var splitEditorPreviewType: SplitEditorPreviewType = SplitEditorPreviewType.PREVIEW
    var htmlPanelProviderInfo: HtmlPanelProvider.Info = SwingHtmlPanelProvider.INFO
    var useGrayscaleRendering: Boolean = ideGreyScale()
    var zoomFactor: Double = DEFAULT_ZOOM_FACTOR
    var maxImageWidth: Int = 0
    var synchronizePreviewPosition: Boolean = true
    var highlightPreviewTypeEnum: HighlightPreviewType
        get() = HighlightPreviewType.ADAPTER.get(highlightPreviewType)
        set(value) {
            highlightPreviewType = value.intValue
        }
    var highlightPreviewType: Int = HighlightPreviewType.ADAPTER.default.intValue
    var highlightFadeOut: Int = DEFAULT_HIGHLIGHT_FADEOUT
    var highlightOnTyping: Boolean = true
    var synchronizeSourcePositionOnClick: Boolean = true
    var verticallyAlignSourceAndPreviewSyncPosition: Boolean = true
    var showSearchHighlightsInPreview: Boolean = true
    var showSelectionInPreview: Boolean = true
    var lastLayoutSetsDefault: Boolean = false

    val htmlPanelProvider: HtmlPanelProvider get() = HtmlPanelProvider.getFromInfoOrDefault(htmlPanelProviderInfo)

    override fun getStateHolder(): StateHolder {
        return mySettingsExtensions.addItems(TagItemHolder("PreviewSettings").addItems(
            TagItem("PanelProvider", { htmlPanelProviderInfo }, { htmlPanelProviderInfo = it }, { val item = HtmlPanelProvider.Info(); item.loadState(it); item }),
            StringAttribute("splitEditorLayout", { splitEditorLayout.idName }, {
                splitEditorLayout = SplitEditorLayout.enumConstant(it) ?: SplitEditorLayout.SPLIT
                Unit
            }),
            StringAttribute("splitEditorPreview", { splitEditorPreviewType.idName }, {
                splitEditorPreviewType = SplitEditorPreviewType.enumConstant(it) ?: SplitEditorPreviewType.PREVIEW
                Unit
            }),
            BooleanAttribute("useGrayscaleRendering", { useGrayscaleRendering }, { useGrayscaleRendering = it }),
            DoubleAttribute("zoomFactor", { zoomFactor }, { if (it in MIN_ZOOM_FACTOR .. MAX_ZOOM_FACTOR) zoomFactor = it else DEFAULT_ZOOM_FACTOR }),
            IntAttribute("maxImageWidth", { maxImageWidth }, { maxImageWidth = it }),
            BooleanAttribute("synchronizePreviewPosition", { synchronizePreviewPosition }, { synchronizePreviewPosition = it }),
            StringAttribute("highlightPreviewType", { highlightPreviewTypeEnum.name }, {
                highlightPreviewType = HighlightPreviewType.ADAPTER.findEnumName(it).intValue
                Unit
            }),
            IntAttribute("highlightFadeOut", { highlightFadeOut }, { highlightFadeOut = it }),
            BooleanAttribute("highlightOnTyping", { highlightOnTyping }, { highlightOnTyping = it }),
            BooleanAttribute("synchronizeSourcePosition", { synchronizeSourcePositionOnClick }, { synchronizeSourcePositionOnClick = it }),
            BooleanAttribute("verticallyAlignSourceAndPreviewSyncPosition", { verticallyAlignSourceAndPreviewSyncPosition }, { verticallyAlignSourceAndPreviewSyncPosition = it }),
            BooleanAttribute("showSearchHighlightsInPreview", { showSearchHighlightsInPreview }, { showSearchHighlightsInPreview = it }),
            BooleanAttribute("showSelectionInPreview", { showSelectionInPreview }, { showSelectionInPreview = it }),
            BooleanAttribute("lastLayoutSetsDefault", { lastLayoutSetsDefault }, { lastLayoutSetsDefault = it })
        ))
    }

    constructor(other: MdPreviewSettings) : this() {
        copyFrom(other)
    }

    override fun resetToDefaults() {
        copyFrom(DEFAULT, false)
    }

    fun copyFrom(other: MdPreviewSettings, withExtensions: Boolean = true) {
        this.splitEditorLayout = other.splitEditorLayout
        this.splitEditorPreviewType = other.splitEditorPreviewType
        this.htmlPanelProviderInfo = other.htmlPanelProviderInfo
        this.useGrayscaleRendering = other.useGrayscaleRendering
        this.zoomFactor = other.zoomFactor
        this.maxImageWidth = other.maxImageWidth
        this.synchronizePreviewPosition = other.synchronizePreviewPosition
        this.highlightPreviewTypeEnum = other.highlightPreviewTypeEnum
        this.highlightFadeOut = other.highlightFadeOut
        this.highlightOnTyping = other.highlightOnTyping
        this.synchronizeSourcePositionOnClick = other.synchronizeSourcePositionOnClick
        this.verticallyAlignSourceAndPreviewSyncPosition = other.verticallyAlignSourceAndPreviewSyncPosition
        this.showSearchHighlightsInPreview = other.showSearchHighlightsInPreview
        this.showSelectionInPreview = other.showSelectionInPreview
        this.lastLayoutSetsDefault = other.lastLayoutSetsDefault

        if (withExtensions) mySettingsExtensions.copyFrom(other)
    }

    constructor(
        splitEditorLayout: SplitEditorLayout
        , splitEditorPreviewType: SplitEditorPreviewType
        , htmlPanelProviderInfo: HtmlPanelProvider.Info
        , useGrayscaleRendering: Boolean
        , zoomFactor: Double
        , maxImageWidth: Int
        , synchronizePreviewPosition: Boolean
        , highlightPreviewType: HighlightPreviewType
        , highlightFadeOut: Int
        , highlightOnTyping: Boolean
        , synchronizeSourcePosition: Boolean
        , verticallyAlignSourceAndPreviewSyncPosition: Boolean
        , showSearchHighlightsInPreview: Boolean
        , showSelectionInPreview: Boolean
        , lastLayoutSetsDefault: Boolean
    ) : this() {
        this.splitEditorLayout = splitEditorLayout
        this.splitEditorPreviewType = splitEditorPreviewType
        this.htmlPanelProviderInfo = htmlPanelProviderInfo
        this.useGrayscaleRendering = useGrayscaleRendering
        this.zoomFactor = zoomFactor
        this.maxImageWidth = maxImageWidth
        this.synchronizePreviewPosition = synchronizePreviewPosition
        this.highlightPreviewTypeEnum = highlightPreviewType
        this.highlightFadeOut = highlightFadeOut
        this.highlightOnTyping = highlightOnTyping
        this.synchronizeSourcePositionOnClick = synchronizeSourcePosition
        this.verticallyAlignSourceAndPreviewSyncPosition = verticallyAlignSourceAndPreviewSyncPosition
        this.showSearchHighlightsInPreview = showSearchHighlightsInPreview
        this.showSelectionInPreview = showSelectionInPreview
        this.lastLayoutSetsDefault = lastLayoutSetsDefault
    }

    interface Holder {
        var previewSettings: MdPreviewSettings
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MdPreviewSettings

        if (splitEditorLayout != other.splitEditorLayout) return false
        if (splitEditorPreviewType != other.splitEditorPreviewType) return false
        if (htmlPanelProviderInfo != other.htmlPanelProviderInfo) return false
        if (useGrayscaleRendering != other.useGrayscaleRendering) return false
        if (zoomFactor != other.zoomFactor) return false
        if (maxImageWidth != other.maxImageWidth) return false
        if (synchronizePreviewPosition != other.synchronizePreviewPosition) return false
        if (highlightPreviewTypeEnum != other.highlightPreviewTypeEnum) return false
        if (highlightFadeOut != other.highlightFadeOut) return false
        if (highlightOnTyping != other.highlightOnTyping) return false
        if (synchronizeSourcePositionOnClick != other.synchronizeSourcePositionOnClick) return false
        if (verticallyAlignSourceAndPreviewSyncPosition != other.verticallyAlignSourceAndPreviewSyncPosition) return false
        if (showSearchHighlightsInPreview != other.showSearchHighlightsInPreview) return false
        if (showSelectionInPreview != other.showSelectionInPreview) return false
        if (lastLayoutSetsDefault != other.lastLayoutSetsDefault) return false

        return mySettingsExtensions == other
    }

    override fun hashCode(): Int {
        var result = mySettingsExtensions.hashCode()
        result += 31 * result + splitEditorLayout.hashCode()
        result += 31 * result + splitEditorPreviewType.hashCode()
        result += 31 * result + htmlPanelProviderInfo.hashCode()
        result += 31 * result + useGrayscaleRendering.hashCode()
        result += 31 * result + zoomFactor.hashCode()
        result += 31 * result + maxImageWidth.hashCode()
        result += 31 * result + synchronizePreviewPosition.hashCode()
        result += 31 * result + highlightPreviewTypeEnum.hashCode()
        result += 31 * result + highlightFadeOut.hashCode()
        result += 31 * result + highlightOnTyping.hashCode()
        result += 31 * result + synchronizeSourcePositionOnClick.hashCode()
        result += 31 * result + verticallyAlignSourceAndPreviewSyncPosition.hashCode()
        result += 31 * result + showSearchHighlightsInPreview.hashCode()
        result += 31 * result + showSelectionInPreview.hashCode()
        result += 31 * result + lastLayoutSetsDefault.hashCode()
        return result
    }

    val htmPanelProvider: HtmlPanelProvider get() = HtmlPanelProvider.getFromInfoOrDefault(htmlPanelProviderInfo)

    @Suppress("UNUSED_PARAMETER")
    fun changeToProvider(fromPanelProviderInfo: HtmlPanelProvider.Info?, toPanelProviderInfo: HtmlPanelProvider.Info?) {
        val defaults = getDefaultSettings(toPanelProviderInfo)
        htmlPanelProviderInfo = defaults.htmlPanelProviderInfo
    }

    fun isDefault(htmlPanelProvider: HtmlPanelProvider.Info?): Boolean {
        return this == getDefaultSettings(htmlPanelProvider)
    }

    companion object {
        // @formatter:off
        //noinspection deprecation
        // DEPRECATED: replacement JBUIScale#scale appeared in 2019-06-10
        @JvmField val DEFAULT_ZOOM_FACTOR: Double = JBUI.scale(1f).toDouble()
        
        const val MIN_ZOOM_FACTOR:Double = 0.1
        const val MAX_ZOOM_FACTOR:Double = 10.0

        const val DEFAULT_HIGHLIGHT_FADEOUT:Int = 5
        const val MIN_HIGHLIGHT_FADEOUT:Int = 0
        const val MAX_HIGHLIGHT_FADEOUT:Int = 60

        const val USE_GRAYSCALE_RENDERING:String = "preview.useGrayscaleRendering"
        const val SHOW_GIT_HUB_PAGE_IF_SYNCED:String = "preview.showGitHubPageIfSynced"
        const val ZOOM_FACTOR:String = "preview.zoomFactor"
        const val MAX_IMAGE_WIDTH:String = "preview.maxImageWidth"
        const val SYNCHRONIZE_PREVIEW_POSITION:String = "preview.synchronizePreviewPosition"
        const val SYNCHRONIZE_SOURCE_POSITION:String = "preview.synchronizeSourcePosition"
        const val FOCUS_HIGHLIGHT_PREVIEW:String = "preview.focusHighlightPreview"
        const val PERFORMANCE_WARNING:String = "preview.performanceWarning"
        const val EXPERIMENTAL_WARNING:String = "preview.experimentalWarning"

        @JvmField val JAVAFX_DEFAULT:MdPreviewSettings = MdPreviewSettings(SplitEditorLayout.SPLIT, SplitEditorPreviewType.PREVIEW, JavaFxHtmlPanelProvider().INFO, false, DEFAULT_ZOOM_FACTOR, 0, true, HighlightPreviewType.LINE, DEFAULT_HIGHLIGHT_FADEOUT, true, true, true, false, true, false)
        @JvmField val DEFAULT:MdPreviewSettings = MdPreviewSettings(SplitEditorLayout.SPLIT, SplitEditorPreviewType.PREVIEW, SwingHtmlPanelProvider.INFO, false, DEFAULT_ZOOM_FACTOR, 0, true, HighlightPreviewType.NONE, DEFAULT_HIGHLIGHT_FADEOUT, true, true, true, false, true, false)
        @JvmField val TEXT_DEFAULT:MdPreviewSettings = MdPreviewSettings(SplitEditorLayout.SPLIT, SplitEditorPreviewType.PREVIEW, TextHtmlPanelProvider.INFO, false, DEFAULT_ZOOM_FACTOR, 0, true, HighlightPreviewType.NONE, DEFAULT_HIGHLIGHT_FADEOUT, true, true, true, false, true, false)
        // @formatter:on

        @JvmStatic
        fun ideGreyScale(): Boolean {
            val application: Application? = ApplicationManager.getApplication()
            return if (application?.isUnitTestMode != false) {
                false
            } else {
                UISettingsProvider.getInstance()?.editorAAType == AntialiasingType.GREYSCALE
            }
        }

        @JvmStatic
        fun getDefaultSettings(htmlPanelProvider: HtmlPanelProvider.Info?): MdPreviewSettings {
            if (htmlPanelProvider == null) return DEFAULT
            if (htmlPanelProvider == SwingHtmlPanelProvider.INFO) return DEFAULT
            if (htmlPanelProvider == JavaFxHtmlPanelProvider().INFO) return JAVAFX_DEFAULT
            if (htmlPanelProvider == TextHtmlPanelProvider.INFO) return TEXT_DEFAULT
            return DEFAULT
        }

        @JvmStatic
        fun getCanBrowseInPreview(): Boolean {
            return true
        }

        fun injectHtmlResource(linkResolver: MdLinkResolver, renderingProfile: MdRenderingProfile, injections: ArrayList<InjectHtmlResource?>): Unit {
            renderingProfile.previewSettings.htmPanelProvider.injectHtmlResource(linkResolver, renderingProfile, injections)
        }
    }
}
