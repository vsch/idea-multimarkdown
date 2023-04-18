// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.resources

import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.editor.text.TextHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlCssResourceProvider
import com.vladsch.md.nav.editor.util.HtmlGenerator
import com.vladsch.md.nav.editor.util.HtmlGeneratorProvider
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider
import com.vladsch.md.nav.settings.MdHtmlSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.vcs.MdLinkResolver

object SwingHtmlGeneratorProvider : HtmlGeneratorProvider() {
    val NAME = MdBundle.message("editor.swing.html.generator.provider.name")
    val ID = "com.vladsch.md.nav.editor.swing.html.generator"
    override val HAS_PARENT = false
    override val INFO = Info(ID, NAME)
    override val COMPATIBILITY = TextHtmlPanelProvider.COMPATIBILITY

    override fun createHtmlGenerator(linkResolver: MdLinkResolver, renderingProfile: MdRenderingProfile): HtmlGenerator = SwingHtmlGenerator(linkResolver, renderingProfile)
    override val cssProvider: HtmlCssResourceProvider get() = SwingHtmlCssProvider
    override val scriptProviders: Array<HtmlScriptResourceProvider> = arrayOf()

    override fun isSupportedSetting(settingName: String): Boolean {
        return when (settingName) {
            MdHtmlSettings.ADD_PAGE_HEADER -> true
            else -> false
        }
    }
}
