// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.swing

import com.intellij.openapi.project.Project
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.editor.HtmlPanelHost
import com.vladsch.md.nav.editor.resources.SwingHtmlGeneratorProvider
import com.vladsch.md.nav.editor.util.HtmlCompatibility
import com.vladsch.md.nav.editor.util.HtmlPanel
import com.vladsch.md.nav.editor.util.HtmlPanelProvider
import com.vladsch.md.nav.settings.MdPreviewSettings

object SwingHtmlPanelProvider : HtmlPanelProvider() {
    val NAME = MdBundle.message("editor.swing.html.panel.provider.name")
    val ID = "com.vladsch.md.nav.editor.swing.html.panel"
    override val INFO = HtmlPanelProvider.Info(ID, NAME)
    override val COMPATIBILITY = HtmlCompatibility(ID, 3f, 1f, 0f, arrayOf(SwingHtmlGeneratorProvider.ID), arrayOf<String>())

    override fun isSupportedSetting(settingName: String): Boolean {
        return when (settingName) {
            MdPreviewSettings.MAX_IMAGE_WIDTH -> true
            MdPreviewSettings.ZOOM_FACTOR -> true
            // FIX: sync preview work for swing browser
            MdPreviewSettings.SYNCHRONIZE_PREVIEW_POSITION -> false
            else -> false
        }
    }

    override fun createHtmlPanel(project: Project, htmlPanelHost: HtmlPanelHost): HtmlPanel {
        return SwingHtmlPanel(project, htmlPanelHost)
    }

    override val isAvailable: HtmlPanelProvider.AvailabilityInfo
        get() = HtmlPanelProvider.AvailabilityInfo.AVAILABLE
}
