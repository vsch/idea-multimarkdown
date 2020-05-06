// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.text

import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.vladsch.flexmark.util.sequence.Range
import com.vladsch.flexmark.util.sequence.TagRange
import com.vladsch.md.nav.editor.ExternalLinkLauncher
import com.vladsch.md.nav.editor.HtmlPanelHost
import com.vladsch.md.nav.editor.PreviewEditorState
import com.vladsch.md.nav.editor.util.HtmlPanel
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension
import javax.swing.JComponent

internal class TextHtmlPanel(project: Project, htmlPanelHost: HtmlPanelHost) : HtmlPanel(project, htmlPanelHost), ExternalLinkLauncher, Disposable {

    private var myLastRenderedHtml = ""
    private var myCssFileUris: Array<String> = arrayOf()
    private var myCssInlineText: String? = null
    private val myTextViewer: EditorImpl
    internal var myScrollTag: String = ""
    internal var myScrollAttribute: String = ""
    internal var myScrollReference: String = ""
    internal var myHavePendingScroll = false
    private var myState = PreviewEditorState()

    init {
        val language = Language.findLanguageByID("HTML")
        val fileType = language?.associatedFileType
        //myTextViewer = new EditorTextField(EditorFactory.getInstance().createDocument(""), project, fileType, true, false);
        val myDocument = EditorFactory.getInstance().createDocument("")
        myTextViewer = EditorFactory.getInstance().createViewer(myDocument, project) as EditorImpl
        if (fileType != null)
            myTextViewer.highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileType)
    }

    override fun setState(state: PreviewEditorState) {
        myState = state
    }

    override fun getState(): PreviewEditorState {
        return myState
    }

    override fun launchExternalLink(href: String): Boolean {
        return myHtmlPanelHost.launchExternalLink(href)
    }

    protected fun setStyleSheet() {
    }

    override val component: JComponent
        get() = myTextViewer.component

    override fun setCSS(inlineCss: String?, fileUris: Array<String>) {
        if (myCssInlineText != inlineCss || !myCssFileUris.contentEquals(fileUris)) {
            myCssInlineText = inlineCss
            myCssFileUris = fileUris
            setStyleSheet()
        }
    }

    override fun setHtml(html: String): Boolean {
        if (myProject.isDisposed) return false

        // diagnostic/2773
        val useHtml = html.replace("\r\n", "\n").replace("\r", "\n")
        myLastRenderedHtml = useHtml
        val myDocument = myTextViewer.document

        ApplicationManager.getApplication().runWriteAction(Runnable {
            if (myProject.isDisposed) return@Runnable

            CommandProcessor.getInstance().executeCommand(myProject, {
                if (!myProject.isDisposed) {
                    myDocument.replaceString(0, myDocument.textLength, useHtml)
                    val caretModel = myTextViewer.caretModel
                    if (caretModel.offset >= myDocument.textLength) {
                        caretModel.moveToOffset(myDocument.textLength)
                    }
                }
            }, null, null, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION, myDocument)

            if (myHavePendingScroll) {
                scrollToReference()
            }
        })
        return true
    }

    override fun setPageUrl(url: String): Boolean? {
        return null
    }

    fun scrollToReference() {
        if (myProject.isDisposed) return
        if (myScrollReference == "") return

        myHavePendingScroll = false

        ApplicationManager.getApplication().runReadAction(Runnable {
            if (myProject.isDisposed) return@Runnable

            val match = "\\b\\Q$myScrollReference\\E\\b".toRegex()
            val matchResult = match.find(myTextViewer.document.charsSequence) ?: return@Runnable

            val range = matchResult.groups[0] ?: return@Runnable
            val caretModel = myTextViewer.caretModel
            caretModel.moveToOffset(range.range.start, false)
            myTextViewer.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
        })
    }

    override fun scrollToMarkdownSrcOffset(offset: Int, lineOffsets: Range, verticalLocation: Float?, tagRanges: List<TagRange>, onLoadUpdate: Boolean, onTypingUpdate: Boolean) {
        // first we find the tag of interest and its range
        var bestTagRange: TagRange? = null

        for (tagRange in tagRanges) {
            if (tagRange.doesContain(offset)) {
                if (bestTagRange == null || tagRange.span < bestTagRange.span) {
                    bestTagRange = tagRange
                }
            }
        }

        val findBestTagRange = bestTagRange ?: return

        // now we have the best possible match from all the elements, we can find the tag and scroll it into view
        myScrollTag = findBestTagRange.tag
        myScrollAttribute = MdNavigatorExtension.SOURCE_POSITION_ATTRIBUTE_NAME
        myScrollReference = "${findBestTagRange.start}-${findBestTagRange.end}"
        myHavePendingScroll = true

        if (onLoadUpdate) {
            ApplicationManager.getApplication().invokeAndWait({
                scrollToReference()
            }, ModalityState.NON_MODAL)
        }
    }

    override fun dispose() {
        val application = ApplicationManager.getApplication()
        val runnable = Runnable {
            if (!myTextViewer.isDisposed) {
                EditorFactory.getInstance().releaseEditor(myTextViewer)
            }
        }

        if (application.isUnitTestMode || application.isDispatchThread) {
            runnable.run()
        } else {
            application.invokeLater(runnable)
        }

        Disposer.dispose(this)
    }

    companion object {
        private val FOCUS_ELEMENT_DY = 100
    }
}
