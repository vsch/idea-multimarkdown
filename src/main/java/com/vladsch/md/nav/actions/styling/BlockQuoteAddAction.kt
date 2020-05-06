// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder
import com.vladsch.md.nav.psi.element.MdBlockQuote
import com.vladsch.md.nav.psi.element.MdHeaderElement
import com.vladsch.md.nav.psi.util.BlockQuotePrefix
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.plugin.util.psi.isTypeOf
import com.vladsch.plugin.util.toInt
import kotlin.math.max

class BlockQuoteAddAction : BlockQuoteItemAction() {
    override fun getElementCondition(haveSelection: Boolean): Condition<PsiElement> {
        //noinspection ReturnOfInnerClass
        return Condition { element ->
            element.node != null /*&& TokenTypeSets.BLOCK_ELEMENTS.contains(element.node.elementType)*/ && (
                haveSelection || (
                    (element.node.elementType != MdTypes.PARAGRAPH_BLOCK || !MdPsiImplUtil.isFirstIndentedBlock(element, false))
                        && !element.isTypeOf(MdTokenSets.LIST_BLOCK_ITEM_ELEMENT_TYPES)
                    )
                )
        }
    }

    override fun canPerformAction(element: PsiElement?, conditionBuilder: DisabledConditionBuilder?): Boolean {
        var useElement = element
        while (useElement?.node != null && !useElement.isTypeOf(MdTokenSets.BLOCK_ELEMENT_SET)) useElement = useElement.parent

        val enabled = useElement?.node != null && useElement.isTypeOf(MdTokenSets.BLOCK_ELEMENT_SET)
        conditionBuilder?.and(enabled, if (useElement?.node != null) "Not block element at caret" else "No element at caret")
        return enabled
    }

    override fun performAction(element: PsiElement, editContext: CaretContextInfo, adjustCaret: Boolean) {
        if (canPerformAction(element, null)) {
            var useElement: PsiElement? = element

            while (useElement?.node != null
                && !useElement.isTypeOf(MdTokenSets.BLOCK_ELEMENT_SET)) {
                useElement = useElement.parent
            }

            if (useElement is PsiFile) return

            // collapse to the innermost block quote that is a single child
            while (useElement != null && useElement.children.size == 1
                && useElement is MdBlockQuote && useElement.lastChild is MdBlockQuote) {
                useElement = useElement.lastChild
            }

            if (useElement == null) return

            // append a quote level
            val parentPrefixes = MdPsiImplUtil.getBlockPrefixes(useElement.parent, null, editContext)
            val elementPrefixes = MdPsiImplUtil.getBlockPrefixes(useElement, parentPrefixes, editContext)
            val blockQuotePrefix = BlockQuotePrefix.create(true, ">", ">")
            val prefixes =
                if (parentPrefixes.last() == elementPrefixes.last()) {
                    // append block quote at end of prefixes
                    parentPrefixes.append(blockQuotePrefix).finalizePrefixes(editContext)
                } else {
                    // insert before the elementPrefix
                    parentPrefixes.append(blockQuotePrefix).append(elementPrefixes.last()).finalizePrefixes(editContext)
                }

            // only include tail blank line if it is part of the block quote already in existence
            val includeTailBlankLine = useElement.node.elementType === MdTypes.BLOCK_QUOTE

            val wrappingLines = editContext.lineAppendable

            if (useElement.node.elementType == MdTypes.VERBATIM) {
                wrappingLines.append(useElement.text)
                wrappingLines.removeExtraBlankLines(1, 0)
                MdPsiImplUtil.adjustLinePrefix(useElement, wrappingLines, editContext)
            } else {
                if (useElement is MdHeaderElement) {
                    wrappingLines.append(useElement.text)
                    MdPsiImplUtil.adjustLinePrefix(useElement, wrappingLines, editContext)
                } else {
                    for (child in useElement.children) {
                        val textLines = MdPsiImplUtil.linesForWrapping(child, false, true, true, editContext)
                        wrappingLines.append(textLines, true)
                    }
                }
            }

            // Fix: #320
            if (wrappingLines.lineCount == 0) {
                // no text just prefixes
                wrappingLines.append(parentPrefixes.childPrefix)
            }

            wrappingLines.line()

            val isFirstIndentedChild = MdPsiImplUtil.isFirstIndentedBlock(element, false)
            val prefixedLines = wrappingLines.copyAppendable()
            MdPsiImplUtil.addLinePrefix(prefixedLines, prefixes, isFirstIndentedChild, true)

            var caretLine = editContext.caretLine - (editContext.offsetLineNumber(editContext.preEditOffset(useElement.node.startOffset)) ?: 0)
            val postEditNodeStart = editContext.postEditNodeStart(useElement.node)
            val startOffset = editContext.offsetLineStart(postEditNodeStart) ?: return
            var endOffset = editContext.postEditNodeEnd(useElement.node)

            if (!includeTailBlankLine) {
                val lastLeafChild = MdPsiImplUtil.lastLeafChild(useElement.node)
                if (lastLeafChild.elementType === MdTypes.BLANK_LINE) {
                    // remove the last line
                    endOffset = editContext.postEditNodeStart(lastLeafChild)
                }
            }

            // FIX: this needs to take unterminated lines into account
            if (caretLine >= wrappingLines.lineCountWithPending) {
                caretLine = max(0, wrappingLines.lineCountWithPending - 1)
            }

            val originalPrefixLen = wrappingLines[caretLine].length - wrappingLines[caretLine].length
            val finalPrefixLen = prefixedLines[caretLine].length - wrappingLines[caretLine].length

            val logPos = editContext.editor.caretModel.primaryCaret.logicalPosition
            val prefixedText = prefixedLines.toSequence(editContext.styleSettings.KEEP_BLANK_LINES, true)
            editContext.document.replaceString(startOffset, endOffset, prefixedText)

            if (adjustCaret) {
                editContext.editor.caretModel.primaryCaret.moveToLogicalPosition(LogicalPosition(logPos.line, logPos.column + if (logPos.column >= originalPrefixLen) finalPrefixLen - originalPrefixLen else 0, logPos.leansForward))
                if (editContext.editor.caretModel.primaryCaret.hasSelection() || useElement.isTypeOf(MdTokenSets.LIST_BLOCK_ELEMENT_TYPES)) {
                    val spaceDelta = (editContext.charSequence.safeCharAt(postEditNodeStart + 1) == ' ').toInt()
                    editContext.editor.caretModel.primaryCaret.setSelection(postEditNodeStart + 1 + spaceDelta, postEditNodeStart + prefixedText.length - (postEditNodeStart - startOffset))
                }
            }
        }
    }
}
