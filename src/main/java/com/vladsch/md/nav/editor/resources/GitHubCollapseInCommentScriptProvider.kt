// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.resources

import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.editor.javafx.JavaFxHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlCompatibility
import com.vladsch.md.nav.editor.util.HtmlCssResource
import com.vladsch.md.nav.editor.util.HtmlScriptResource
import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider
import com.vladsch.md.nav.settings.MdPreviewSettings
import org.intellij.lang.annotations.Language

object GitHubCollapseInCommentScriptProvider : HtmlScriptResourceProvider() {
    @JvmField
    val NAME: String = MdBundle.message("editor.github-collapse-in-comment.html.script.provider.name")
    
    const val ID: String = "com.vladsch.md.nav.editor.github-collapse-in-comment.html.script"

    override val HAS_PARENT: Boolean = false

    override val INFO: Info = Info(ID, NAME)

    override val COMPATIBILITY: HtmlCompatibility = JavaFxHtmlPanelProvider.COMPATIBILITY

    @Language("HTML")
    override val scriptResource: HtmlScriptResource = GitHubCollapseInCommentsScriptResource(INFO, "", """
<script>
(function() {
    var elemList = window.document.getElementsByTagName("details");
    for (var i = 0; i < elemList.length; i++) {
        elemList[i].setAttribute('open','');
    }
})();
</script>
""")

    override val cssResource: HtmlCssResource? = null

    override fun isSupportedSetting(settingName: String): Boolean {
        return when (settingName) {
            MdPreviewSettings.PERFORMANCE_WARNING -> false
            MdPreviewSettings.EXPERIMENTAL_WARNING -> false
            else -> false
        }
    }
}
