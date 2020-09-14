// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.resources

import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlCssResource
import com.vladsch.md.nav.editor.util.HtmlScriptResource
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider
import com.vladsch.md.nav.settings.MdPreviewSettings

object JBCefHtmlScriptProvider : HtmlScriptResourceProvider() {
    val NAME = MdBundle.message("editor.jbcef.html.script.provider.name")
    val ID = "com.vladsch.md.nav.editor.jbcef.html.script"
    override val HAS_PARENT = true
    override val INFO = Info(ID, NAME)
    override val COMPATIBILITY = JavaFxHtmlPanelProvider.COMPATIBILITY

    override val scriptResource: HtmlScriptResource = JBCefHtmlScriptResource(INFO, MdPlugin.PREVIEW_JBCEF_JS, "")
    override val cssResource: HtmlCssResource = GitHubCollapseMarkdownCssProvider.cssResource

    //    override val cssResource: HtmlCssResource = HtmlCssResource(HtmlCssResourceProvider.Info(ID, NAME)
    //            , MultiMarkdownPlugin.PREVIEW_GITHUB_COLLAPSE_DARK
    //            , MultiMarkdownPlugin.PREVIEW_GITHUB_COLLAPSE_LIGHT
    //            , null)

    override fun isSupportedSetting(settingName: String): Boolean {
        return when (settingName) {
            MdPreviewSettings.PERFORMANCE_WARNING -> true
            MdPreviewSettings.EXPERIMENTAL_WARNING -> false
            else -> false
        }
    }
}
