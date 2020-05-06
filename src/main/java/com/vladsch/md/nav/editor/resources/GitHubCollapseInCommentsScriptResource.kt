// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.resources

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.vladsch.flexmark.util.data.MutableDataHolder
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.md.nav.editor.util.HtmlScriptResource
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider
import com.vladsch.md.nav.editor.util.InjectHtmlResource
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import java.util.*

class GitHubCollapseInCommentsScriptResource(
    providerInfo: HtmlScriptResourceProvider.Info      // providerInfo for this script resource
    , sourceJs: String                                          // resource path for js script source
    , initializationHtml: String                                // initialization script with <script></script> wrapper, or any other HTML to be inserted at the bottom of body
    , data: MutableDataHolder = MutableDataSet()
) : HtmlScriptResource(providerInfo, sourceJs, initializationHtml, data) {

    override fun injectHtmlResource(project: Project, applicationSettings: MdApplicationSettings, renderingProfile: MdRenderingProfile, injections: ArrayList<InjectHtmlResource?>, forHtmlExport: Boolean, dataContext: DataContext) {
        injectScriptInitializationHtml(injections, true, false, true, initializationHtml)
    }
}
