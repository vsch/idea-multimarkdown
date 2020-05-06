// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.resources

import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlCssResource
import com.vladsch.md.nav.editor.util.HtmlCssResourceProvider

object PrismHtmlCssProvider : HtmlCssResourceProvider() {
    val NAME = MdBundle.message("editor.prismjs.html.css.provider.name")
    val ID = "com.vladsch.md.nav.editor.prismjs.html.css"

    override val HAS_PARENT = true
    override val INFO = Info(ID, NAME)
    override val COMPATIBILITY = JavaFxHtmlPanelProvider.COMPATIBILITY

    override val cssResource: HtmlCssResource = HtmlCssResource(INFO
        , MdPlugin.PREVIEW_FX_PRISM_JS_STYLESHEET_DARK
        , MdPlugin.PREVIEW_FX_PRISM_JS_STYLESHEET_LIGHT
        , null)
}
