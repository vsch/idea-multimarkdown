// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder
import com.vladsch.md.nav.actions.styling.util.MdActionUtil
import com.vladsch.plugin.util.minLimit

abstract class BlockQuoteItemAction : AnAction() {

    protected abstract fun canPerformAction(element: PsiElement?, conditionBuilder: DisabledConditionBuilder?): Boolean
    protected abstract fun performAction(element: PsiElement, editContext: CaretContextInfo, adjustCaret: Boolean)
    protected abstract fun getElementCondition(haveSelection: Boolean): Condition<PsiElement>

    protected fun getCommonParentOfType(psiFile: PsiFile, caret: Caret, isNestable: Boolean): PsiElement? {
        return MdActionUtil.getCommonParentOfType(psiFile, TextRange.create(caret.selectionStart, caret.selectionEnd), isNestable, getElementCondition(caret.hasSelection()))
    }

    override fun isDumbAware(): Boolean {
        return false
    }

    override fun update(e: AnActionEvent) {
        MdActionUtil.getConditionBuilder(e, this) { it, (_, editor, psiFile) ->
            it.andSingleCaret(editor)
                .and {
                    val closestElement = getCommonParentOfType(psiFile, editor.caretModel.currentCaret, false)
                    it.and(closestElement is PsiFile || canPerformAction(closestElement, it))
                }
        }.done()
    }

    override fun actionPerformed(e: AnActionEvent) {
        MdActionUtil.getProjectEditorPsiFile(e)?.let { (_, editor, psiFile) ->
            WriteCommandAction.runWriteCommandAction(psiFile.project) {
                val document = editor.document

                editor.caretModel.runForEachCaret({ caret ->
                    CaretContextInfo.withContext(psiFile, editor, null, false, caret.offset) { editContext ->
                        val closestElement = getCommonParentOfType(psiFile, editor.caretModel.currentCaret, false)
                        if (closestElement != null) {
                            if (closestElement !is PsiFile && canPerformAction(closestElement, null)) performAction(closestElement, editContext, true)
                            else if (caret.hasSelection()) {
                                // perform action on individual elements in the selection
                                var element = psiFile.findElementAt(caret.selectionStart)
                                while (element?.parent != psiFile && element?.parent != null) {
                                    element = element.parent
                                }

                                val elementList = ArrayList<PsiElement>()

                                while (element != null) {
                                    if (canPerformAction(element, null)) {
                                        elementList.add(element)
                                    }
                                    element = element.nextSibling ?: break
                                    if (element.node.startOffset >= caret.selectionEnd) break
                                }

                                if (elementList.isNotEmpty()) {
                                    val firstNode = elementList[0].node
                                    val lastNode = elementList.last().node

                                    val startOffset = firstNode.startOffset

                                    caret.setSelection((startOffset - 1).minLimit(0), (lastNode.startOffset + lastNode.textLength))

                                    for (blockElement in elementList.reversed()) {
                                        val caretSubContext = CaretContextInfo.subContext(editContext, blockElement.node.startOffset)
                                        performAction(blockElement, caretSubContext, false)
                                    }

                                    if (startOffset > 0) caret.setSelection(caret.selectionStart + 1, caret.selectionEnd)
                                }
                            }
                        }
                    }
                }, true)

                PsiDocumentManager.getInstance(psiFile.project).commitDocument(document)
            }
        }
    }
}
