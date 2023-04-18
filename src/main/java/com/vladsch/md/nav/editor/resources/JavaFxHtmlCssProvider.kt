// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.resources

import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlCssResource
import com.vladsch.md.nav.editor.util.HtmlCssResourceProvider
import com.vladsch.md.nav.settings.MdCssSettings

object JavaFxHtmlCssProvider : HtmlCssResourceProvider() {
    val NAME = MdBundle.message("editor.javafx.html.css.provider.name")
    val ID = "com.vladsch.md.nav.editor.javafx.html.css"
    override val HAS_PARENT = false
    override val INFO = Info(ID, NAME)
    override val COMPATIBILITY = JavaFxHtmlPanelProvider.COMPATIBILITY

    override fun isSupportedSetting(settingName: String): Boolean {
        return when (settingName) {
            MdCssSettings.DYNAMIC_PAGE_WIDTH -> true
            else -> false
        }
    }

    override val cssResource: HtmlCssResource = JavaFxHtmlCssResource
}
