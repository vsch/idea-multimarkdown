// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementDecorator
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.impl.source.PostprocessReformattingAspect

open class MoveCaretAfterCompletionDecorator(delegate: LookupElement, val tailDelta: Int, val onSpaceDeleteTail: Int) : LookupElementDecorator<LookupElement>(delegate) {
    override fun handleInsert(context: InsertionContext) {
        delegate.handleInsert(context)

        if (context.completionChar != ' ') {
            PostprocessReformattingAspect.getInstance(context.project).doPostponedFormatting()
            LOG.debug("CompletionChar: '${context.completionChar}' Move by $tailDelta, caretOffset: ${context.editor.caretModel.offset}, tailOffset: ${context.tailOffset}")
            val caretOffset = context.editor.caretModel.offset + tailDelta
            val document = context.editor.document
            if (caretOffset >= 0 && caretOffset < document.textLength) {
                context.editor.caretModel.moveToOffset(caretOffset)
            }
        } else if (onSpaceDeleteTail > 0) {
            // need to delete the extra space after : that was added
            LOG.debug("CompletionChar: '${context.completionChar}' delete tail $onSpaceDeleteTail, caretOffset: ${context.editor.caretModel.offset}, tailOffset: ${context.tailOffset}")
            val document = context.editor.document
            val tailOffset = context.tailOffset
            if (tailOffset <= document.textLength && tailOffset >= onSpaceDeleteTail) {
                document.deleteString(tailOffset - onSpaceDeleteTail, tailOffset)
            }
        }
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.language.completion")
    }
}
