// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.util

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.Range
import com.vladsch.flexmark.util.sequence.TagRange
import com.vladsch.md.nav.editor.HtmlPanelHost
import com.vladsch.md.nav.editor.PreviewEditorState
import com.vladsch.md.nav.editor.api.MdPreviewCustomizationProvider
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension
import org.w3c.dom.Node
import javax.swing.JComponent

abstract class HtmlPanel(val myProject: Project, val myHtmlPanelHost: HtmlPanelHost) : Disposable {
    abstract val component: JComponent
    abstract fun setHtml(html: String): Boolean   // false means don't try to scroll to offset, update was rescheduled or engine not ready
    abstract fun setCSS(inlineCss: String?, fileUris: Array<String>)
    abstract fun scrollToMarkdownSrcOffset(offset: Int, lineOffsets: com.vladsch.flexmark.util.sequence.Range, verticalLocation: Float?, tagRanges: List<TagRange>, onLoadUpdate: Boolean, onTypingUpdate: Boolean)

    //    abstract fun setHtmlPanelHost(launcher: HtmlPanelHost)
    abstract fun setPageUrl(url: String): Boolean? // null means no such feature, need page HTML, false means update rescheduled, if URL blank just return true if can handle urls or null if not

    abstract fun setState(state: PreviewEditorState) // set the stored state
    abstract fun getState(): PreviewEditorState // get the stored state

    open fun print() {
    }

    open fun canPrint(): Boolean = false

    open fun debug(startStop: Boolean) {
    }

    open fun canDebug(): Boolean = false
    open fun isDebuggerEnabled(): Boolean = false
    open fun isDebugging(): Boolean = false
    open fun isDebugBreakOnLoad(): Boolean = false
    open fun debugBreakOnLoad(breakOnLoad: Boolean, debugBreakInjectionOnLoad: Boolean) {
    }

    companion object {
        @JvmStatic
        protected val LOG: Logger = Logger.getInstance("com.vladsch.md.nav.editor.browser")

        fun nodeToSrcRange(node: Node): Range<Int>? {
            if (!node.hasAttributes()) {
                return null
            }
            val attribute = node.attributes.getNamedItem(MdNavigatorExtension.SOURCE_POSITION_ATTRIBUTE_NAME) ?: return null
            val startEnd = StringUtil.split(attribute.nodeValue, "..")
            if (startEnd.size != 2) {
                return null
            }
            return Range(Integer.parseInt(startEnd[0]), Integer.parseInt(startEnd[1]))
        }

        fun getCssLines(inlineCss: String?, fileUris: Array<String>): String {
            val result = StringBuilder()

            for (uri in fileUris) {
                result.append("<link rel=\"stylesheet\" href=\"").append(uri).append("\" />\n")
            }

            if (inlineCss != null) {
                result.append("<style>\n").append(inlineCss).append("\n</style>\n")
            }
            return result.toString()
        }
    }
}
