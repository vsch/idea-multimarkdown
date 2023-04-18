// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.resources

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlCssResource
import com.vladsch.md.nav.editor.util.HtmlScriptResource
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider
import com.vladsch.md.nav.parser.flexmark.FlexmarkAttributeProvider
import com.vladsch.md.nav.settings.MdPreviewSettings

object PrismScriptProvider : HtmlScriptResourceProvider() {
    val NAME = MdBundle.message("editor.prismjs.html.script.provider.name")
    val ID = "com.vladsch.md.nav.editor.prismjs.html.script"
    override val HAS_PARENT = false
    override val INFO = HtmlScriptResourceProvider.Info(ID, NAME)
    override val COMPATIBILITY = JavaFxHtmlPanelProvider.COMPATIBILITY

    override val scriptResource: HtmlScriptResource = HtmlScriptResource(INFO, MdPlugin.PREVIEW_FX_PRISM_JS, "")

    init {
        scriptResource.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "language-")
        scriptResource.set(FlexmarkAttributeProvider.FENCED_CODE_PRE_CLASS, "line-numbers")
        scriptResource.set(FlexmarkAttributeProvider.INDENTED_CODE_PRE_CLASS, "language-none line-numbers")
    }

    override val cssResource: HtmlCssResource = PrismHtmlCssProvider.cssResource

    override fun isSupportedSetting(settingName: String): Boolean {
        return when (settingName) {
            MdPreviewSettings.PERFORMANCE_WARNING -> true
            else -> false
        }
    }
}
