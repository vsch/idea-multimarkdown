// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.resources

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlCssResource
import com.vladsch.md.nav.editor.util.HtmlScriptResource
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider

object HljsScriptProvider : HtmlScriptResourceProvider() {
    val NAME = MdBundle.message("editor.hljs.html.script.provider.name")
    val ID = "com.vladsch.md.nav.editor.hljs.html.script"
    override val HAS_PARENT = false
    override val INFO = HtmlScriptResourceProvider.Info(ID, NAME)
    override val COMPATIBILITY = JavaFxHtmlPanelProvider.COMPATIBILITY

    override val scriptResource: HtmlScriptResource = HtmlScriptResource(INFO, MdPlugin.PREVIEW_FX_HIGHLIGHT_JS, "<script>hljs.initHighlightingOnLoad();</script>")
    override val cssResource: HtmlCssResource = HljsHtmlCssProvider.cssResource

    init {
        // don't highlight fenced code without info or indented code blocks
        scriptResource.set(HtmlRenderer.FENCED_CODE_NO_LANGUAGE_CLASS, "nohighlight")
    }
}
