// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.


package com.vladsch.md.nav.editor.resources

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.editor.util.HtmlCssResource
import com.vladsch.md.nav.editor.util.InjectHtmlResource
import com.vladsch.md.nav.parser.api.MdParserExtension
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import java.util.*

object JavaFxHtmlCssResource : HtmlCssResource(JavaFxHtmlCssProvider.INFO
    , MdPlugin.PREVIEW_FX_STYLESHEET_DARK
    , MdPlugin.PREVIEW_FX_STYLESHEET_LIGHT
    , MdPlugin.PREVIEW_FX_STYLESHEET_LAYOUT
) {

    override fun injectHtmlResource(project: Project, applicationSettings: MdApplicationSettings, renderingProfile: MdRenderingProfile, injections: ArrayList<InjectHtmlResource?>, forHtmlExport: Boolean, dataContext: DataContext) {
        super.injectHtmlResource(project, MdApplicationSettings.instance, renderingProfile, injections, forHtmlExport, dataContext)

        injectCSSText(injections, renderingProfile.cssSettings.isDynamicPageWidth, true, false,
            """.container { width: 100%; }
.wiki-container { width: 100%; }
.wiki-body { width: 100%; }
"""
        )

        // let extensions add their css and script providers
        for (extension in MdParserExtension.EXTENSIONS.value) {
            extension.injectHtmlResource(providerInfo, project, applicationSettings, renderingProfile, injections, forHtmlExport, dataContext)
        }
    }
}
