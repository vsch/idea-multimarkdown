// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.sequence.RepeatedSequence
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.psi.element.MdHeaderElement
import com.vladsch.md.nav.psi.element.MdParagraph
import com.vladsch.md.nav.psi.element.MdSetextHeader
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.plugin.util.rangeLimit

class HeaderLevelUpAction : HeaderAction() {
    override fun canPerformAction(element: PsiElement): Boolean {
        return element is MdHeaderElement && element.headerLevel < 6 || element is MdParagraph
    }

    override fun cannotPerformActionReason(element: PsiElement): String {
        return if (element is MdHeaderElement && element.canIncreaseLevel) "Cannot increase level heading beyond 6" else "Not Heading or Text element"
    }

    override fun headerAction(element: PsiElement, document: Document, caretOffset: Int, editContext: PsiEditAdjustment): Int? {
        if (element is MdHeaderElement) {
            if (element.canIncreaseLevel) {
                if (element is MdSetextHeader && element.headerLevel == 1) {
                    val markerElement = element.headerMarkerNode ?: return null
                    document.replaceString(markerElement.startOffset, markerElement.startOffset + markerElement.textLength, RepeatedSequence.repeatOf('-', element.headerText.length))
                } else {
                    element.setHeaderLevel(element.headerLevel + 1, editContext)
                }
            } else if (element.headerLevel < 6) {
                // change to ATX and increase level
                val offset = (caretOffset - element.headerTextElement!!.node.startOffset).rangeLimit(0, element.headerTextElement!!.node.textLength)
                return MdPsiImplUtil.changeHeaderType(element, element.headerLevel + 1, document, offset, editContext)
            }
        } else if (element is MdParagraph) {
            // add # before start of line without prefixes
            val startLine = document.getLineNumber(element.node.startOffset)
            val caretLine = document.getLineNumber(caretOffset) - startLine

            val lines = MdPsiImplUtil.linesForWrapping(element, false, false, false, editContext)

            // disabled because it is not undoable by heading down
            //if (false && MdCodeStyleSettings.getInstance(element.project).HEADING_PREFERENCE_TYPE().isSetextPreferred) {
            //    val offset = lines[caretLine].trackedSourceLocation(0).offset + lines[caretLine].trimEnd().length
            //    document.insertString(offset, "\n" + prefixes.childPrefix + RepeatedSequence.of('=', unPrefixedLines[caretLine].trimEnd().length))
            //
            //    if (caretLine > 0) {
            //        // insert blank line or all preceding lines will be treated as part of heading
            //        val startOffset = unPrefixedLines[caretLine].trackedSourceLocation(0).offset
            //        document.insertString(startOffset, prefixes.childPrefix + "\n")
            //    }
            //} else {

            // diagnostics/2556, index out of bounds
            val offset = if (caretLine >= lines.lineCount) document.textLength else lines[caretLine].text.startOffset
            return if (offset > 0 && document.charsSequence[offset - 1] != '\n') {
                document.insertString(offset, "\n# ")
                offset + 3
            } else {
                document.insertString(offset, "# ")
                offset + 2
            }
            //}
        }
        return null
    }
}
