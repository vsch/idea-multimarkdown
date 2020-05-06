// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo.Companion.withContext
import com.vladsch.md.nav.editor.api.MdEditorRangeHighlighter
import com.vladsch.md.nav.highlighter.MdHighlighterColors
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.plugin.util.toInt
import com.vladsch.plugin.util.ui.highlight.EditorRangeHighlighterSet
import java.util.function.Consumer

class WrappingEditorRangeHighlighter internal constructor(editor: Editor) : MdEditorRangeHighlighter {

    private val myHighlighters: EditorRangeHighlighterSet = EditorRangeHighlighterSet(editor)
    private val highlighterColors = MdHighlighterColors.getInstance()

    override fun updateRangeHighlighters(file: MdFile) {
        myHighlighters.clear()

        if (MdApplicationSettings.instance.debugSettings.debugFormatText) {
            val editor = myHighlighters.editor
            withContext(file, editor, null, false, editor.caretModel.primaryCaret.offset, Consumer { caretContextInfo: CaretContextInfo ->
                val wrappingContext = caretContextInfo.wrappingContext
                if (wrappingContext != null) {
                    val paraEnd = wrappingContext.endOffset - (wrappingContext.endOffset - 1 < caretContextInfo.charSequence.length && caretContextInfo.charSequence[wrappingContext.endOffset - 1] == '\n').toInt()
                    val paraStart = wrappingContext.startOffset
                    if ((paraStart < paraEnd || wrappingContext.firstPrefixStart < wrappingContext.firstPrefixEnd) && editor.caretModel.caretCount == 1) {
                        if (paraStart < paraEnd) {
                            myHighlighters.addRangeHighlighter(paraStart, paraEnd, highlighterColors.DEBUG_FORMAT_TEXT_BLOCK_KEY, 1, HighlighterTargetArea.EXACT_RANGE)
                        }
                        if (wrappingContext.firstPrefixStart < wrappingContext.firstPrefixEnd) {
                            myHighlighters.addRangeHighlighter(wrappingContext.firstPrefixStart, wrappingContext.firstPrefixEnd, highlighterColors.DEBUG_FORMAT_PREFIX_KEY, 1, HighlighterTargetArea.EXACT_RANGE)
                        }
                    }
                }
            })
        }
    }

    override fun removeRangeHighlighters() {
        myHighlighters.clear()
    }
}
