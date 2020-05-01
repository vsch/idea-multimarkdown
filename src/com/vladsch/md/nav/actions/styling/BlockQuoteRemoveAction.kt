// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder
import com.vladsch.md.nav.psi.element.MdBlockQuote
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.psi.util.BlockQuotePrefix
import com.vladsch.plugin.util.psi.isTypeOf

class BlockQuoteRemoveAction : BlockQuoteItemAction() {
    override fun getElementCondition(haveSelection: Boolean): Condition<PsiElement> {
        //noinspection ReturnOfInnerClass
        return Condition { it is MdBlockQuote }
    }

    override fun canPerformAction(element: PsiElement?, conditionBuilder: DisabledConditionBuilder?): Boolean {
        val enabled = element != null && element.isTypeOf(MdTokenSets.BLOCK_ELEMENT_SET) && (element is MdBlockQuote || element.parent is MdBlockQuote)
        conditionBuilder?.and(enabled, "Not block quoted element at caret")
        return enabled
    }

    override fun performAction(element: PsiElement, editContext: CaretContextInfo, adjustCaret: Boolean) {
        if (canPerformAction(element, null)) {
            val blockQuote = if (element.node.elementType == MdTypes.BLOCK_QUOTE) element else element.parent
            // use the parent's prefix and apply the prefix to the contents of this blockQuote
            val prefixes = MdPsiImplUtil.getBlockPrefixes(blockQuote.parent, null, editContext).finalizePrefixes(editContext)

            val wrappingLines = editContext.lineAppendable

            for (child in blockQuote.children) {
                val childLines = MdPsiImplUtil.linesForWrapping(child, false, true, false, editContext)
                wrappingLines.append(childLines, true)
            }

            // Fix: #320
            val isEmpty = if (wrappingLines.lineCount == 0) {
                // no text just prefixes
                val fullPrefixes = MdPsiImplUtil.getBlockPrefixes(blockQuote, null, editContext).finalizePrefixes(editContext)
                wrappingLines.append(fullPrefixes.childPrefix)
                true
            } else {
                false
            }

            wrappingLines.line()

            val prefixedLines = wrappingLines.copyAppendable()
            val isFirstIndentedChild = MdPsiImplUtil.isFirstIndentedBlock(element, false)
            MdPsiImplUtil.addLinePrefix(prefixedLines, prefixes, isFirstIndentedChild, prefixes.last() is BlockQuotePrefix || isFirstIndentedChild)

            var caretLine = editContext.caretLine - (editContext.offsetLineNumber(editContext.preEditOffset(blockQuote.node.startOffset)) ?: 0)
            if (caretLine >= wrappingLines.lineCount) {
                caretLine = wrappingLines.lineCount - 1
            }

            val originalPrefixLen = wrappingLines[caretLine].length - wrappingLines[caretLine].length
            val finalPrefixLen = prefixedLines[caretLine].length - wrappingLines[caretLine].length

            val postEditNodeStart = editContext.postEditNodeStart(blockQuote.node)
            val startOffset = editContext.offsetLineStart(postEditNodeStart) ?: return
            val endOffset = editContext.postEditNodeEnd(blockQuote.node)

            val logPos = editContext.editor.caretModel.primaryCaret.logicalPosition

            val prefixedText = prefixedLines.toSequence(editContext.styleSettings.KEEP_BLANK_LINES, true)
            editContext.document.replaceString(startOffset, endOffset, prefixedText)

            if (adjustCaret) {
                editContext.editor.caretModel.primaryCaret.moveToLogicalPosition(LogicalPosition(logPos.line, logPos.column + if (logPos.column >= originalPrefixLen) finalPrefixLen - originalPrefixLen else 0, logPos.leansForward))
                if (!isEmpty) editContext.editor.caretModel.primaryCaret.setSelection(postEditNodeStart + if (postEditNodeStart < editContext.charSequence.length && editContext.charSequence[postEditNodeStart] == ' ') 1 else 0, postEditNodeStart + prefixedText.length - (postEditNodeStart - startOffset))
                else editContext.editor.caretModel.primaryCaret.setSelection(editContext.editor.caretModel.primaryCaret.offset, editContext.editor.caretModel.primaryCaret.offset)
            }
        }
    }
}
