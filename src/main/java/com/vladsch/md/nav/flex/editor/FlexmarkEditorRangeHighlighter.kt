// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.editor

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.vladsch.md.nav.editor.api.MdEditorRangeHighlighter
import com.vladsch.md.nav.flex.psi.FlexmarkExample
import com.vladsch.md.nav.flex.psi.FlexmarkExampleSection
import com.vladsch.md.nav.flex.settings.FlexmarkHtmlSettings
import com.vladsch.md.nav.highlighter.MdHighlighterColors
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.plugin.util.nullIf
import com.vladsch.plugin.util.ui.highlight.EditorRangeHighlighterSet

class FlexmarkEditorRangeHighlighter internal constructor(editor: Editor) : MdEditorRangeHighlighter {
    private val myHighlighters: EditorRangeHighlighterSet = EditorRangeHighlighterSet(editor)
    private val highlighterColors = MdHighlighterColors.getInstance()

    override fun updateRangeHighlighters(file: MdFile) {
        myHighlighters.clear()

        val caretModel = myHighlighters.editor.caretModel
        if (caretModel.caretCount == 1) {
            val caretOffset = caretModel.offset
            var element = file.findElementAt(caretOffset)
            if (element is LeafPsiElement && element.getNode() != null) {
                val flexmarkSettings = MdRenderingProfileManager.getProfile(file).htmlSettings.getExtension(FlexmarkHtmlSettings.KEY)
                val sectionMap = flexmarkSettings.flexmarkSectionLanguages

                val sectionIndex = when (element.getNode().elementType) {
                    MdTypes.FLEXMARK_EXAMPLE_SOURCE -> 1
                    MdTypes.FLEXMARK_EXAMPLE_HTML -> 2
                    MdTypes.FLEXMARK_EXAMPLE_AST -> 3
                    else -> 0
                }

                if (sectionIndex > 0) {
                    val languageString = sectionMap[sectionIndex];
                    val pair = FlexmarkHtmlSettings.languageSections(languageString)
                    if (pair.first == FlexmarkHtmlSettings.FLEXMARK_AST_LANGUAGE_NAME) {
                        element = element.getParent()

                        val sectionOffsets = pair.second

                        // maybe can highlight this one
                        var sourceRange: TextRange? = null
                        var htmlRange: TextRange? = null
                        var astRange: TextRange? = null
                        var selfRange: TextRange? = null

                        val exampleSource = (element.parent as FlexmarkExample).source
                        if (exampleSource != null && sectionOffsets.contains(1) && sectionIndex != 1) {
                            val ranges: Pair<TextRange?, TextRange?>? = (element as FlexmarkExampleSection).getHighlightRange(caretOffset, exampleSource.node)
                            if (ranges != null) {
                                sourceRange = ranges.first
                                selfRange = ranges.second
                            }
                        }

                        val exampleHtml = (element.parent as FlexmarkExample).html
                        if (exampleHtml != null && sectionOffsets.contains(2) && sectionIndex != 2) {
                            val ranges: Pair<TextRange?, TextRange?>? = (element as FlexmarkExampleSection).getHighlightRange(caretOffset, exampleHtml.node)
                            if (ranges != null) {
                                htmlRange = ranges.first
                                selfRange = ranges.second
                            }
                        }

                        val exampleAst = (element.parent as FlexmarkExample).ast
                        if (exampleAst != null && sectionOffsets.contains(3) && sectionIndex != 3) {
                            val ranges: Pair<TextRange?, TextRange?>? = (element as FlexmarkExampleSection).getHighlightRange(caretOffset, exampleAst.node)
                            if (ranges != null) {
                                astRange = ranges.first
                                selfRange = ranges.second
                            }
                        }

                        if (sourceRange != null || htmlRange != null || astRange != null || selfRange != null) {
                            val charSequence = myHighlighters.editor.document.charsSequence
                            val firstRange = sourceRange ?: htmlRange ?: astRange
                            val secondRange = htmlRange.nullIf(firstRange) ?: astRange.nullIf(firstRange)

                            highlightFlexmark(selfRange.validateRange(charSequence), firstRange.validateRange(charSequence), secondRange.validateRange(charSequence))
                        }
                    }
                }
            }
        }
    }

    private fun highlightFlexmark(offsetRange: TextRange?, sourceRange: TextRange?, htmlRange: TextRange?) {
        if (sourceRange != null) {
            myHighlighters.addRangeHighlighter(sourceRange.startOffset, sourceRange.endOffset, highlighterColors.DEBUG_FLEXMARK_SOURCE_KEY, 2, HighlighterTargetArea.EXACT_RANGE)
//                .setAfterEndOfLine(true)
        }
        if (htmlRange != null) {
            myHighlighters.addRangeHighlighter(htmlRange.startOffset, htmlRange.endOffset, highlighterColors.DEBUG_FLEXMARK_SOURCE_KEY, 2, HighlighterTargetArea.EXACT_RANGE)
//                .setAfterEndOfLine(true)
        }
        if (offsetRange != null) {
            myHighlighters.addRangeHighlighter(offsetRange.startOffset, offsetRange.endOffset, highlighterColors.DEBUG_FLEXMARK_AST_KEY, 2, HighlighterTargetArea.EXACT_RANGE)
//                .setAfterEndOfLine(true)
        }
    }

    private fun TextRange?.validateRange(charSequence: CharSequence): TextRange? {
        if (this != null) {
            if (startOffset >= endOffset) return null
            if (endOffset > startOffset && endOffset < charSequence.length && charSequence[endOffset - 1] == '\n' && charSequence[endOffset] == '\n') {
                return if (startOffset == endOffset - 1) null
                else TextRange(startOffset, endOffset - 1)
            }
        }

        return this
    }

    override fun removeRangeHighlighters() {
        myHighlighters.clear()
    }
}
