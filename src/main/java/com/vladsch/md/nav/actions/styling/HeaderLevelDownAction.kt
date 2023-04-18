// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.RepeatedSequence
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.psi.element.MdAtxHeader
import com.vladsch.md.nav.psi.element.MdHeaderElement
import com.vladsch.md.nav.psi.element.MdSetextHeader

class HeaderLevelDownAction : HeaderAction() {
    companion object {
        val ATX_HEADING_HASH_SPACE_TAB: CharPredicate = CharPredicate.anyOf("# \t")
    }

    override fun canPerformAction(element: PsiElement): Boolean {
        return element is MdHeaderElement
    }

    override fun cannotPerformActionReason(element: PsiElement): String {
        return "Not heading element"
    }

    override fun headerAction(element: PsiElement, document: Document, caretOffset: Int, editContext: PsiEditAdjustment): Int? {
        if (element is MdHeaderElement) {
            if (element.canDecreaseLevel) {
                // disabled because it is not undoable without changing more of the context
                if (element is MdSetextHeader && element.headerLevel == 2) {
                    val markerElement = element.headerMarkerNode ?: return null
                    document.replaceString(markerElement.startOffset, markerElement.startOffset + markerElement.textLength, RepeatedSequence.repeatOf('=', element.headerText.length))
                } else {
                    element.setHeaderLevel(element.headerLevel - 1, editContext)
                }
            } else if (element is MdAtxHeader) {
                // remove # and following blanks
                val count = BasedSequence.of(element.text).countLeading(ATX_HEADING_HASH_SPACE_TAB)
                if (count > 0) {
                    document.deleteString(element.node.startOffset, element.node.startOffset + count)
                }
            } else if (element is MdSetextHeader) {
                // remove marker and following EOL
                var offset: Int? = null
                val headerMarkerNode = element.headerMarkerNode ?: return null
                if (caretOffset >= headerMarkerNode.startOffset) {
                    // caret needs to move up one line
                    val caretLine = document.getLineNumber(caretOffset)
                    offset = document.getLineStartOffset(caretLine - 1) + (caretOffset - document.getLineStartOffset(caretLine))
                }
                document.deleteString(headerMarkerNode.startOffset, headerMarkerNode.startOffset + headerMarkerNode.textLength + 1)
                return offset
            }
        }
        return null
    }
}
