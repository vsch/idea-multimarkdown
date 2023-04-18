// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.sequence.LineAppendable
import com.vladsch.flexmark.util.sequence.Range
import com.vladsch.md.nav.editor.resources.TextHtmlGeneratorProvider
import com.vladsch.md.nav.editor.split.SplitFileEditor
import com.vladsch.md.nav.editor.text.TextHtmlPanelProvider
import com.vladsch.md.nav.editor.util.HtmlGeneratorProvider
import com.vladsch.md.nav.parser.api.HtmlPurpose
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.TimeIt
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern
import kotlin.collections.LinkedHashMap

class MdPreviewFileEditor constructor(project: Project, file: VirtualFile) : PreviewFileEditorBase(project, file) {
    override fun makeHtmlPage(pattern: Pattern?): String {
        if (myDocument == null) return ""

        val plainText = myLastPanelProviderInfo == TextHtmlPanelProvider.INFO
        val htmlProviderInfo =
            if (plainText && mySplitEditorPreviewType == SplitFileEditor.SplitEditorPreviewType.UNMODIFIED_HTML) TextHtmlGeneratorProvider.INFO
            else myRenderingProfile.htmlSettings.htmlGeneratorProviderInfo

        if (myLastHtmlProviderInfo != htmlProviderInfo) myLastRenderedHtml = ""

        val myHtmlGenerator = HtmlGeneratorProvider.getFromInfoOrDefault(htmlProviderInfo).createHtmlGenerator(GitHubLinkResolver(myFile, myProject), myRenderingProfile)
        val psiFile = PsiManager.getInstance(myProject).findFile(myFile)

        var rangeList: List<Range>? = null
        var selectionList: List<Range>? = null
        myHighlightEnabled = true

        if (pattern != null) {
            val ranges = ArrayList<Range>()
            val matcher = pattern.matcher(myDocument.charsSequence)

            while (matcher.find()) {
                ranges.add(Range.of(matcher.start(), matcher.end()))
            }

            rangeList = ranges
        }

        if (myRenderingProfile.previewSettings.showSelectionInPreview) {
            val editor = myEditor
            if (editor != null && editor.selectionModel.hasSelection()) {
                val ranges = ArrayList<Range>()
                for (caret in editor.caretModel.allCarets) {
                    if (caret.hasSelection()) {
                        ranges.add(Range.of(caret.selectionStart, caret.selectionEnd))
                    }
                }

                selectionList = ranges

                // disable highlight when selections are displayed
                myHighlightEnabled = false
            }
        }

        var rangeMap: Map<Range, String>? = null

        if (selectionList != null && rangeList != null) {
            // merge ranges giving selection priority
            val highlights = rangeList.iterator()
            val selections = selectionList.iterator()
            rangeMap = LinkedHashMap()

            var highlight: Range? = null
            var selection: Range? = null

            while (true) {
                if (highlight == null && highlights.hasNext()) highlight = highlights.next()
                if (selection == null && selections.hasNext()) selection = selections.next()

                if (highlight == null && selection == null) break

                if (selection != null && highlight != null) {
                    if (selection.doesOverlap(highlight)) {
                        if (selection.doesContain(highlight)) {
                            highlight = null
                        } else {
                            if (highlight.doesContain(selection)) {
                                // split out into before and after
                                val before = Range.of(highlight.start, selection.start)
                                if (before.span > 0) {
                                    rangeMap.put(before, "search-highlight")
                                }

                                highlight = Range.of(selection.end, highlight.end)
                                if (highlight.span == 0) highlight = null

                                rangeMap.put(selection, "selection-highlight")
                                selection = null
                            } else {
                                // trim highlight to exclude selection, save selection
                                highlight = highlight.exclude(selection)
                                if (highlight.end <= selection.start) {
                                    rangeMap.put(highlight, "search-highlight")
                                    highlight = null
                                }
                                rangeMap.put(selection, "selection-highlight")
                                selection = null
                            }
                        }
                    } else {
                        // no overlap, save the one that comes before
                        if (highlight.end <= selection.start) {
                            rangeMap.put(highlight, "search-highlight")
                            highlight = null
                        } else {
                            rangeMap.put(selection, "selection-highlight")
                            selection = null
                        }
                    }
                } else {
                    if (highlight != null) {
                        rangeMap.put(highlight, "search-highlight")
                        highlight = null
                    } else if (selection != null) {
                        rangeMap.put(selection, "selection-highlight")
                        selection = null
                    }
                }
            }
        } else if (rangeList != null) {
            rangeMap = LinkedHashMap()
            for (range in rangeList) {
                rangeMap.put(range, "search-highlight")
            }
        } else if (selectionList != null) {
            rangeMap = LinkedHashMap()
            for (range in selectionList) {
                rangeMap.put(range, "selection-highlight")
            }
        }

        var currentHtml: String = ""

        TimeIt.logTime(LOG, "MarkdownPreviewFileEditor::makeHtmlPage toHtml") {
            // diagnostic/2612  use immutableCharSequence
            currentHtml = myHtmlGenerator.toHtml(psiFile, myDocument.immutableCharSequence, HtmlPurpose.RENDER, null, null, Consumer { it ->
                if (!plainText) {
                    if (rangeMap != null) {
                        // add range list
                        it.set(MdNavigatorExtension.HIGHLIGHT_RANGES, rangeMap)
                        it.set(Parser.FENCED_CODE_CONTENT_BLOCK, true)
                    }
                    // fastest output possible
                    it.set(HtmlRenderer.FORMAT_FLAGS, LineAppendable.F_PASS_THROUGH)
                }
            })
        }

        myHtmlTagRanges = myHtmlGenerator.htmlTagRanges

        return currentHtml
    }
}
