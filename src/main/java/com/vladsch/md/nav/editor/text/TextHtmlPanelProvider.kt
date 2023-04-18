// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.text

import com.intellij.openapi.project.Project
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.editor.HtmlPanelHost
import com.vladsch.md.nav.editor.util.HtmlCompatibility
import com.vladsch.md.nav.editor.util.HtmlPanel
import com.vladsch.md.nav.editor.util.HtmlPanelProvider

object TextHtmlPanelProvider : HtmlPanelProvider() {
    val NAME: String = MdBundle.message("editor.text.html.panel.provider.name")
    const val ID: String = "com.vladsch.md.nav.editor.text.html.panel"
    override val INFO: Info = Info(ID, NAME)
    override val COMPATIBILITY: HtmlCompatibility = HtmlCompatibility(ID, null, null, null, arrayOf(), arrayOf())

    override fun isSupportedSetting(settingName: String): Boolean {
        return false
    }

    override fun createHtmlPanel(project: Project, htmlPanelHost: HtmlPanelHost): HtmlPanel {
        return TextHtmlPanel(project, htmlPanelHost)
    }

    override val isAvailable: AvailabilityInfo
        get() = AvailabilityInfo.AVAILABLE
}
