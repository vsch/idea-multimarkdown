// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.resources

import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlCssResource
import com.vladsch.md.nav.editor.util.HtmlScriptResource
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider
import com.vladsch.md.nav.settings.MdPreviewSettings

object JavaFxHtmlScriptProvider : HtmlScriptResourceProvider() {
    val NAME = MdBundle.message("editor.javafx.html.script.provider.name")
    val ID = "com.vladsch.md.nav.editor.javafx.html.script"
    override val HAS_PARENT = true
    override val INFO = HtmlScriptResourceProvider.Info(ID, NAME)
    override val COMPATIBILITY = JavaFxHtmlPanelProvider.COMPATIBILITY

    override val scriptResource: HtmlScriptResource = JavaFxHtmlScriptResource(INFO, MdPlugin.PREVIEW_FX_JS, "")
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
