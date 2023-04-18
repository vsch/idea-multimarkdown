// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo.Companion.withContext
import com.vladsch.md.nav.editor.api.MdEditorRangeHighlighter
import com.vladsch.md.nav.highlighter.MdHighlighterColors
import com.vladsch.md.nav.parser.LexParserState
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.plugin.util.psi.isTypeOf
import com.vladsch.plugin.util.rangeLimit
import com.vladsch.plugin.util.ui.highlight.EditorRangeHighlighterSet
import java.util.function.Consumer

class SmartInsertEditorRangeHighlighter internal constructor(editor: Editor) : MdEditorRangeHighlighter {
    private val myHighlighters: EditorRangeHighlighterSet = EditorRangeHighlighterSet(editor)
    private val TEXT_SET = LexParserState.getInstance().state.TEXT_SET
    private val highlighterColors = MdHighlighterColors.getInstance()

    override fun updateRangeHighlighters(file: MdFile) {
        myHighlighters.clear()

        val editor = myHighlighters.editor
        if (editor.caretModel.caretCount == 1) {
            val element = file.findElementAt(editor.caretModel.primaryCaret.offset)
            if (element == null || element.isTypeOf(TEXT_SET)) {
                withContext(file, editor, null, false, editor.caretModel.primaryCaret.offset, Consumer { caretContextInfo: CaretContextInfo ->
                    if (caretContextInfo.mirroredCount > 0) {
                        myHighlighters.addRangeHighlighter(
                            caretContextInfo.caretOffset,
                            (caretContextInfo.caretOffset + caretContextInfo.mirroredCount).rangeLimit(caretContextInfo.caretOffset, caretContextInfo.charSequence.length),
                            highlighterColors.COMMENT_ATTR_KEY,
                            HighlighterLayer.ELEMENT_UNDER_CARET,
                            HighlighterTargetArea.EXACT_RANGE
                        )
                    }
                })
            }
        }
    }

    override fun removeRangeHighlighters() {
        myHighlighters.clear()
    }
}
