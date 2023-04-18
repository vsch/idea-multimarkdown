// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.swing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.vladsch.flexmark.util.sequence.Range
import com.vladsch.flexmark.util.sequence.TagRange
import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.MdResourceResolverImpl
import com.vladsch.md.nav.editor.*
import com.vladsch.md.nav.editor.api.MdPreviewCustomizationProvider
import com.vladsch.md.nav.editor.util.HtmlPanel
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension
import com.vladsch.md.nav.settings.HighlightPreviewType
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.plugin.util.debug
import com.vladsch.plugin.util.image.ImageUtils
import com.vladsch.plugin.util.max
import com.vladsch.plugin.util.min
import com.vladsch.plugin.util.minLimit
import com.vladsch.plugin.util.suffixWith
import java.awt.Color
import java.awt.Point
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.IOException
import java.io.StringReader
import java.net.URL
import java.util.*
import java.util.regex.Pattern
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.text.DefaultCaret
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.StyleSheet
import kotlin.text.startsWith

internal class SwingHtmlPanel(project: Project, htmlPanelHost: HtmlPanelHost) : HtmlPanel(project, htmlPanelHost), ExternalLinkLauncher {
    private var myLastRenderedHtml = ""
    private var myCssFileUris: Array<String> = arrayOf()
    private var myCssInlineText: String? = null
    private val jEditorPane: JEditorPane = JEditorPane()
    private val scrollPane: JBScrollPane = JBScrollPane(jEditorPane)
    private var myElemStartOffset: Int? = null
    private var myElemEndOffset: Int? = null
    private var myHtmlCaretOffset: Int? = null
    private var myHavePendingScroll = false
    private var myState = PreviewEditorState()

    override fun dispose() {
    }

    init {
        // Add a custom link listener which can resolve local link references.
        jEditorPane.addHyperlinkListener(MdLinkListener(jEditorPane, this))
        jEditorPane.isEditable = false

        // Set the editor pane caret position to top left, and do not let it reset it
        jEditorPane.caret.magicCaretPosition = Point(0, 0)
        (jEditorPane.caret as DefaultCaret).updatePolicy = DefaultCaret.NEVER_UPDATE

        jEditorPane.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                adjustBrowserSize()
            }
        })
    }

    override fun launchExternalLink(href: String): Boolean {
        return myHtmlPanelHost.launchExternalLink(href)
    }

    override fun setState(state: PreviewEditorState) {
        myState = state
    }

    override fun getState(): PreviewEditorState {
        return myState
    }

    protected fun setStyleSheet() {
        val htmlKit = MdEditorKit(myProject, this)
        val style = StyleSheet()

        for (cssFileUrl in myCssFileUris) {
            style.importStyleSheet(URL(cssFileUrl))
        }

        val cssInlineText = myCssInlineText
        if (cssInlineText != null && !cssInlineText.isEmpty()) {
            style.loadRules(StringReader(cssInlineText), null)
        }

        htmlKit.styleSheet = style
        jEditorPane.editorKit = htmlKit
    }

    override val component: JComponent
        get() = scrollPane

    override fun setCSS(inlineCss: String?, fileUris: Array<String>) {
        if (myCssInlineText != inlineCss || !myCssFileUris.contentEquals(fileUris)) {
            myCssInlineText = inlineCss
            myCssFileUris = fileUris
            setStyleSheet()
        }
    }

    override fun setHtml(html: String): Boolean {
        var useHtml = html

        if (myCssFileUris.isNotEmpty()) {
            setStyleSheet()
        } else {
            // have to fish them out of the html
            val documentPath = myHtmlPanelHost.getVirtualFile().parent?.path?.suffixWith('/')
            val systemPath = if (documentPath != null) FileUtil.toSystemDependentName(documentPath) else null
            val FILE_URI_PREFIX = if (documentPath != null && documentPath.length > 1 && documentPath[1] == ':') "file:/" else MdNavigatorExtension.FILE_URI_PREFIX
            val cssURLs = ArrayList<String>()

            // don't change links, they are resolved on link click
            useHtml = LINK_REPLACE_PATTERN.replace(useHtml) { matchResult ->
                val values = matchResult.groupValues
                val url: String

                if (!PathInfo.isURI(values[2]) && PathInfo.isRelative(values[2])) {
                    // change it to absolute
                    if (values[2].startsWith('#')) {
                        // local, no change
                        url = values[2]
                        values[1] + url + values[3]
                    } else {
                        url = FILE_URI_PREFIX + systemPath + values[2]
                        values[1] + url + values[3]
                    }
                } else {
                    url = values[2]
                    matchResult.value
                }

                if (values[1].startsWith("link")) {
                    cssURLs.add(url)
                }

                // result
                matchResult.value
            }

            useHtml = IMAGE_REPLACE_PATTERN.replace(useHtml) { matchResult ->
                val values = matchResult.groupValues
                val url: String

                val result = if (!ImageUtils.isPossiblyEncodedImage(values[2]) && !PathInfo.isURI(values[2]) && PathInfo.isRelative(values[2])) {
                    // change it to absolute
                    if (values[2].startsWith('#')) {
                        // local, no change
                        url = values[2]
                        values[1] + url + values[3]
                    } else {
                        url = FILE_URI_PREFIX + systemPath + values[2]
                        values[1] + url + values[3]
                    }
                } else {
//                    url = values[2]
                    matchResult.value
                }
                result
            }

            if (MdApplicationSettings.instance.documentSettings.disableGifImages) {
                useHtml = PAGE_GIF_REPLACE_PATTERN.matcher(useHtml).replaceAll("$1_gif_$2")
            }

            myCssFileUris = cssURLs.toTypedArray()
            if (!myCssFileUris.isEmpty()) {
                setStyleSheet()
            } else {
                Companion.setStyleSheet(jEditorPane, MdEditorKit(myProject, this))
            }
        }

        myLastRenderedHtml = useHtml
        try {
            jEditorPane.text = useHtml
        } catch (e: Throwable) {
            MdPreviewCustomizationProvider.textErrorReport("Swing browser exception", e, "HTML", useHtml)
        }

        if (myHavePendingScroll) {
            scrollToReference()
        }
        return true
    }

    override fun setPageUrl(url: String): Boolean? {
        return null
    }

    private fun adjustBrowserSize() {
    }

    fun scrollToReference() {
        // FIX: sync preview work for swing browser
        val elemStartOffsetFinal = myElemStartOffset
        val elemEndOffsetFinal = myElemEndOffset
        val htmlCaretOffsetFinal = myHtmlCaretOffset
        myHavePendingScroll = false

        if ((elemStartOffsetFinal != null && elemEndOffsetFinal != null) || htmlCaretOffsetFinal != null) {
            // now we have the best possible match from all the elements, we can find the tag and scroll it into view
            ApplicationManager.getApplication().invokeAndWait({
                try {
                    if (htmlCaretOffsetFinal != null) {
                        jEditorPane.moveCaretPosition(htmlCaretOffsetFinal)
                        jEditorPane.caret.isVisible = true
                        // leave it on for line
                        jEditorPane.caret.isVisible = false

                        if (elemStartOffsetFinal != null && elemEndOffsetFinal != null) {
                            if (/*MultiMarkdownPlugin.isLicensed &&*/ myHtmlPanelHost.getRenderingProfile().previewSettings.highlightPreviewTypeEnum == HighlightPreviewType.LINE) {
                                jEditorPane.selectedTextColor = Color.MAGENTA
                                jEditorPane.selectionColor = Color.CYAN
                            }
                            val start = elemStartOffsetFinal.min(0)
                            val end = elemEndOffsetFinal.max(elemEndOffsetFinal)
                            jEditorPane.select(start, end)
                        }
                    }
                } catch (ignored: Throwable) {

                }
            }, ModalityState.NON_MODAL)
        }
    }

    override fun scrollToMarkdownSrcOffset(offset: Int, lineOffsets: Range, verticalLocation: Float?, tagRanges: List<TagRange>, onLoadUpdate: Boolean, onTypingUpdate: Boolean) {
        // FIX: sync preview work for swing browser
        val editorKit = jEditorPane.editorKit as? MdEditorKit ?: return
        val viewList = editorKit.elementList

        var bestElementView: MdEditorKit.ElementPosition? = null
        var prevBestView: MdEditorKit.ElementPosition? = null
        var nextBestView: MdEditorKit.ElementPosition? = null

        for (elementView in viewList) {
            if (offset >= elementView.startOffset && offset < elementView.endOffset) {
                val htmlOffset = offset - elementView.startOffset
                val elemSpan = elementView.endElementOffset - elementView.startElementOffset
                if (bestElementView == null || elementView.span() < bestElementView.span()) {
                    bestElementView = elementView
                    LOG.debug { "best element now: offset $offset, view $elementView, $htmlOffset <= $elemSpan" }
                }
            }

            if (offset > elementView.endOffset) {
                if (prevBestView == null || prevBestView.endOffset < elementView.endOffset) {
                    prevBestView = elementView
                }
            }

            if (offset < elementView.startOffset) {
                if (nextBestView == null || nextBestView.startOffset > elementView.startOffset) {
                    nextBestView = elementView
                }
            }
        }

        if (prevBestView == null) prevBestView = bestElementView
        if (nextBestView == null) nextBestView = bestElementView

        if (bestElementView != null || prevBestView != null || nextBestView != null) {
            val elementView = bestElementView
            val caretOffset = offset

            var elemStartOffset: Int? = null
            var elemEndOffset: Int? = null
            var htmlCaretOffset: Int? = null
            var prevElemStartOffset: Int? = null
            var prevElemEndOffset: Int? = null
            var prevHtmlCaretOffset: Int? = null
            var nextElemStartOffset: Int? = null
            var nextElemEndOffset: Int? = null
            var nextHtmlCaretOffset: Int? = null

            if (elementView != null) {
                val elemOffset = caretOffset - elementView.startOffset
                htmlCaretOffset = if (elementView.startElementOffset + elemOffset >= elementView.endElementOffset) elementView.endElementOffset - 1 else elementView.startElementOffset + elemOffset
                elemStartOffset = elementView.startOffset
                elemEndOffset = elementView.endOffset
                LOG.debug { "htmlCaret: $caretOffset, elementOffset $elemOffset, htmlCaret $htmlCaretOffset, $elementView" }
            }

            if (prevBestView != null) {
                val prevElemOffset = caretOffset - prevBestView.startOffset
                prevHtmlCaretOffset = prevBestView.startElementOffset + prevElemOffset //if (prevBestView.startElementOffset + prevElemOffset >= prevBestView.endElementOffset) prevBestView.endElementOffset - 1 else prevBestView.startElementOffset + prevElemOffset;
                prevElemStartOffset = prevBestView.startOffset
                prevElemEndOffset = prevBestView.endOffset
            }

            if (nextBestView != null) {
                val nextElemOffset = caretOffset - nextBestView.startOffset
                nextHtmlCaretOffset = nextBestView.startElementOffset + nextElemOffset //if (nextBestView.startElementOffset + nextElemOffset >= nextBestView.endElementOffset) nextBestView.endElementOffset - 1 else nextBestView.startElementOffset + nextElemOffset;
                nextElemStartOffset = nextBestView.startOffset
                nextElemEndOffset = nextBestView.endOffset
            }

            if ((prevElemStartOffset != null && prevElemEndOffset != null) && (nextElemStartOffset != null && nextElemEndOffset != null)) {
                elemStartOffset = prevElemEndOffset - 1
                elemEndOffset = nextElemStartOffset
            } else if (prevElemStartOffset != null && prevElemEndOffset != null) {
                if (elemEndOffset == null || elemStartOffset == null) {
                    elemStartOffset = prevElemEndOffset
                    elemEndOffset = prevElemEndOffset + 100
                } else {
                    elemStartOffset = prevElemEndOffset
                }
            } else if (nextElemStartOffset != null && nextElemEndOffset != null) {
                if (elemEndOffset == null || elemStartOffset == null) {
                    elemStartOffset = nextElemStartOffset - 100
                    elemEndOffset = nextElemStartOffset + 1
                } else {
                    elemEndOffset = nextElemEndOffset + 1
                }
            }

            val htmlCaretPos = jEditorPane.caretPosition

            if (htmlCaretOffset == null) {
                if (prevHtmlCaretOffset != null && nextHtmlCaretOffset != null) {
                    htmlCaretOffset = (prevHtmlCaretOffset + nextHtmlCaretOffset) / 2
                } else if (prevHtmlCaretOffset != null) {
                    htmlCaretOffset = prevHtmlCaretOffset
                }
                if (nextHtmlCaretOffset != null) {
                    htmlCaretOffset = nextHtmlCaretOffset
                }
            }

            if (htmlCaretOffset != null) {
                if (htmlCaretOffset < htmlCaretPos) {
                    htmlCaretOffset = (htmlCaretOffset - 50).minLimit(0)
                } else if (htmlCaretOffset > htmlCaretPos) {
                    htmlCaretOffset = (htmlCaretOffset + 50)
                }
            }

            myElemStartOffset = elemStartOffset
            myElemEndOffset = elemEndOffset
            myHtmlCaretOffset = htmlCaretOffset
            myHavePendingScroll = true

            if (onLoadUpdate) {
                scrollToReference()
            }
        }
    }

    companion object {
        private val FOCUS_ELEMENT_DY = 100
        val LINK_REPLACE_PATTERN = "(<(?:a|link)[^>]+href=\")([^\"]*)(\"[^>]*>)".toRegex()
        val IMAGE_REPLACE_PATTERN = "(<img[^>]+src=\")([^\"]*)(\"[^>]*>)".toRegex()
        val COMMENT_REPLACE_PATTERN = "<style>/\\*.*\\*/".toRegex()
        val PAGE_GIF_REPLACE_PATTERN: Pattern = Pattern.compile("(<img[^>]+src=\"[^\"]+\\.)gif([^\"]*\"[^>]*>)")

        // used to display notices html text
        fun setStyleSheet(jEditorPane: JEditorPane, htmlEditorKit: MdEditorKit?) {
            val htmlKit = htmlEditorKit ?: HTMLEditorKit()
            val style = StyleSheet()

            try {
                val settings = MdApplicationSettings.instance
                val resolverImpl = MdResourceResolverImpl.instance
                val dark = resolverImpl.getResourceFileContent(MdPlugin.PREVIEW_STYLESHEET_DARK, null)
                val light = resolverImpl.getResourceFileContent(MdPlugin.PREVIEW_STYLESHEET_LIGHT, null)
                val layout = resolverImpl.getResourceFileContent(MdPlugin.PREVIEW_STYLESHEET_LAYOUT, null)
                val scale = JBUI.scale(11)
                val fontSize = "\nbody { font-size: " + scale + "px; }\n"

                style.loadRules(StringReader(layout + (if (settings.isDarcula) dark else light) + fontSize), null)
            } catch (e: IOException) {
                LOG.info(e)
            }

            htmlKit.styleSheet = style
            jEditorPane.editorKit = htmlKit
        }
    }
}
