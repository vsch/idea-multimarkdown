// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.javafx

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
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

class JavaFxHtmlPanelProvider : HtmlPanelProvider() {
    override val INFO: Info
        get() = JavaFxHtmlPanelProvider.INFO
    override val COMPATIBILITY: HtmlCompatibility
        get() = JavaFxHtmlPanelProvider.COMPATIBILITY

    override fun isSupportedSetting(settingName: String): Boolean {
        return when (settingName) {
            MdPreviewSettings.USE_GRAYSCALE_RENDERING,
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
        try {
            return JavaFxHtmlPanel(project, htmlPanelHost)
        } catch (e: SecurityException) {
            throw IllegalStateException("No appropriate constructor is available", e)
        } catch (e: NoSuchMethodException) {
            throw IllegalStateException("No appropriate constructor is available", e)
        } catch (e: ClassNotFoundException) {
            throw IllegalStateException("Should not be called if unavailable", e)
        } catch (e: NoClassDefFoundError) {
            throw IllegalStateException("Should not be called if unavailable", e)
        } catch (e: InstantiationException) {
            throw IllegalStateException("Should not be called if unavailable", e)
        } catch (e: IllegalAccessException) {
            throw IllegalStateException("Should not be called if unavailable", e)
        }
    }

    override val isAvailable: HtmlPanelProvider.AvailabilityInfo
        get() {
            if (hasClass("javafx.scene.web.WebView")) {
                return HtmlPanelProvider.AvailabilityInfo.AVAILABLE_NOTUSED
            }

            return HtmlPanelProvider.AvailabilityInfo.UNAVAILABLE
        }

    companion object {
        @JvmField
        val NAME: String = MdBundle.message("editor.javafx.html.panel.provider.name")
        
        const val ID = "com.vladsch.md.nav.editor.javafx.html.panel"

        @JvmField
        val INFO: Info = HtmlPanelProvider.Info(ID, NAME)

        @JvmField
        val COMPATIBILITY: HtmlCompatibility = HtmlCompatibility(ID, 4f, 3f, 4f, arrayOf(), arrayOf())

        fun hasClass(classPath: String): Boolean {
            return try {
                Class.forName(classPath)
                true
            } catch (t: Throwable) {
                false
            }
        }

        fun getJavaVendor(): String? {
            return System.getProperty("java.vendor")
        }

        fun isJetBrainsJvm(): Boolean {
            val vendor = getJavaVendor()
            return vendor != null && StringUtil.containsIgnoreCase(vendor, "jetbrains")
        }
    }
}
