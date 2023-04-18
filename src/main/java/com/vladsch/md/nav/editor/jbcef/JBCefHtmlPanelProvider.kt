// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.jbcef

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.ui.jcef.JBCefApp
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.editor.HtmlPanelHost
import com.vladsch.md.nav.editor.util.HtmlCompatibility
import com.vladsch.md.nav.editor.util.HtmlPanel
import com.vladsch.md.nav.editor.util.HtmlPanelProvider
import com.vladsch.md.nav.editor.util.InjectHtmlResource
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdPreviewSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import java.util.*

class JBCefHtmlPanelProvider : HtmlPanelProvider() {
    override val INFO: Info
        get() = JBCefHtmlPanelProvider.INFO
    override val COMPATIBILITY: HtmlCompatibility
        get() = JBCefHtmlPanelProvider.COMPATIBILITY

    override fun isSupportedSetting(settingName: String): Boolean {
        return when (settingName) {
//            MdPreviewSettings.USE_GRAYSCALE_RENDERING,
            MdPreviewSettings.SHOW_GIT_HUB_PAGE_IF_SYNCED,
            MdPreviewSettings.ZOOM_FACTOR -> true
            MdPreviewSettings.SYNCHRONIZE_PREVIEW_POSITION -> true
            MdPreviewSettings.SYNCHRONIZE_SOURCE_POSITION -> true
            MdPreviewSettings.FOCUS_HIGHLIGHT_PREVIEW -> true
            else -> false
        }
    }

    override fun injectHtmlResource(project: Project, applicationSettings: MdApplicationSettings, renderingProfile: MdRenderingProfile, injections: ArrayList<InjectHtmlResource?>, forHtmlExport: Boolean, dataContext: DataContext) {
        //        JavaFxHtmlScriptProvider.scriptResource.injectHtmlResource(project, applicationSettings, renderingProfile, injections, forHtmlExport, dataContext)
    }

    override fun createHtmlPanel(project: Project, htmlPanelHost: HtmlPanelHost): HtmlPanel {
        if (JBCefApp.isSupported()) {
            return JBCefHtmlPanel(project, htmlPanelHost)
        }
        
        throw IllegalStateException("Should not be called if unavailable")
    }

    override val isAvailable: AvailabilityInfo
        get() {
            if (JBCefApp.isSupported()) {
                return AvailabilityInfo.AVAILABLE_NOTUSED
            } else {
                return AvailabilityInfo.UNAVAILABLE
            }
        }

    companion object {
        @JvmField
        val NAME: String = MdBundle.message("editor.jbcef.html.panel.provider.name")
        
        const val ID: String = "com.vladsch.md.nav.editor.jbcef.html.panel"
        
        @JvmField
        val INFO: Info = Info(ID, NAME)
        
        @JvmField
        val COMPATIBILITY: HtmlCompatibility = HtmlCompatibility(ID, 5f, 3f, 6f, arrayOf(), arrayOf())

        fun hasClass(classPath: String): Boolean {
            return try {
                Class.forName(classPath)
                true
            } catch (t: Throwable) {
                false
            }
        }
    }
}
