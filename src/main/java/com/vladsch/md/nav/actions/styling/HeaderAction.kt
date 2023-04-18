// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.actions.styling.BaseToggleStateAction.SelectionStateWithReason
import com.vladsch.md.nav.actions.styling.util.MdActionUtil
import com.vladsch.md.nav.actions.styling.util.MdActionUtil.getElementsUnderCaretOrSelection
import com.vladsch.md.nav.psi.element.MdHeaderElement
import com.vladsch.md.nav.psi.element.MdHeaderText
import com.vladsch.md.nav.psi.element.MdParagraph
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.plugin.util.psi.isTypeOf

abstract class HeaderAction : AnAction() {

    protected fun getCommonState(element1: PsiElement, element2: PsiElement): SelectionStateWithReason {
        var element = MdActionUtil.getCommonParentOfType(element1, element2, ELEMENT_CONDITION)

        element = getHeaderActionBlockElement(element)

        return if (element == null) SelectionStateWithReason.NULL
        else if (!canPerformAction(element)) SelectionStateWithReason.NO(cannotPerformActionReason(element))
        else SelectionStateWithReason.YES
    }

    private fun getHeaderActionBlockElement(element: PsiElement?): PsiElement? {
        var psiElement = element
        while (psiElement is LeafPsiElement || psiElement is MdHeaderText) {
            psiElement = psiElement.parent
        }

        // see if has paragraph or heading parent
        var parentElement = psiElement
        while (parentElement != null && !parentElement.isTypeOf(MdTokenSets.BLOCK_ELEMENT_SET)) {
            parentElement = parentElement.parent
        }

        if (parentElement is MdParagraph || parentElement is MdHeaderElement) {
            psiElement = parentElement
        }

        while (psiElement?.node?.elementType == MdTypes.TEXT_BLOCK || psiElement?.node?.elementType == MdTypes.TEXT) {
            psiElement = psiElement?.parent
        }
        return psiElement
    }

    internal abstract fun canPerformAction(element: PsiElement): Boolean

    internal abstract fun headerAction(element: PsiElement, document: Document, caretOffset: Int, editContext: PsiEditAdjustment): Int?
    internal abstract fun cannotPerformActionReason(element: PsiElement): String

    override fun isDumbAware(): Boolean {
        return false
    }

    override fun update(e: AnActionEvent) {
        MdActionUtil.getConditionBuilder(e, this) { it, (_, editor, psiFile) ->
            var lastState: SelectionStateWithReason? = null
            for (caret in editor.caretModel.allCarets) {
                val elements = getElementsUnderCaretOrSelection(psiFile, caret.selectionStart, caret.selectionEnd) ?: continue

                val caretState = getCommonState(elements.getFirst(), elements.getSecond())
                if (lastState == null) {
                    lastState = caretState
                } else if (lastState !== caretState) {
                    lastState = SelectionStateWithReason.INCONSISTENT
                    break
                }
            }

            it.and(lastState != SelectionStateWithReason.INCONSISTENT, "Inconsistent context for multiple carets")
            it.and(lastState == SelectionStateWithReason.YES, if (lastState == SelectionStateWithReason.NULL) "No heading elements in context" else lastState?.reason)
        }.done()
    }

    override fun actionPerformed(e: AnActionEvent) {
        MdActionUtil.getProjectEditorPsiFile(e)?.let { (_, editor, psiFile) ->
            WriteCommandAction.runWriteCommandAction(psiFile.project) {
                val document = editor.document
                val editContext = PsiEditAdjustment(psiFile, document.immutableCharSequence)
                editor.caretModel.runForEachCaret({ caret ->
                    val elements = getElementsUnderCaretOrSelection(psiFile, caret.selectionStart, caret.selectionEnd)
                    if (elements != null) {
                        var element = MdActionUtil.getCommonParentOfType(elements.getFirst(), elements.getSecond(), ELEMENT_CONDITION)
                        element = getHeaderActionBlockElement(element)

                        if (element != null) {
                            val caretOffset = headerAction(element, document, caret.offset, editContext)
                            if (caretOffset != null) {
                                caret.moveToOffset(caretOffset)
                            }
                        }
                    }
                }, true)

                PsiDocumentManager.getInstance(psiFile.project).commitDocument(document)
            }
        }
    }

    companion object {
        internal val ELEMENT_CONDITION: Condition<PsiElement> = Condition { _ -> true }
    }
}
