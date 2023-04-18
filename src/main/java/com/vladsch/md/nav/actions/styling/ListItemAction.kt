// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder
import com.vladsch.md.nav.actions.styling.util.MdActionUtil

abstract class ListItemAction : AnAction() {

    protected abstract fun canPerformAction(caretContextInfo: CaretContextInfo, conditionBuilder: DisabledConditionBuilder?): Boolean
    protected abstract fun performAction(caretContextInfo: CaretContextInfo)

    override fun isDumbAware(): Boolean {
        return false
    }

    open val inMenuAction = true

    override fun update(e: AnActionEvent) {
        MdActionUtil.getConditionBuilder(e, this) { it, (_, editor, psiFile) ->
            it.andSingleCaret(editor)
                .andNoSelection(editor)
                .and {
                    CaretContextInfo.withContextOrNull(psiFile, editor, null, false, editor.caretModel.primaryCaret.offset) { caretContext ->
                        it.notNull(caretContext) {
                            canPerformAction(caretContext!!, it)
                        }
                    }
                }
        }.done()
    }

    override fun actionPerformed(e: AnActionEvent) {
        MdActionUtil.getProjectEditorPsiFile(e)?.let { (_, editor, psiFile) ->
            WriteCommandAction.runWriteCommandAction(psiFile.project) {
                val document = editor.document

                editor.caretModel.runForEachCaret({ caret ->
                    CaretContextInfo.withContext(psiFile, editor, null, false, caret.offset) { caretContextInfo ->
                        performAction(caretContextInfo)
                    }
                }, true)

                PsiDocumentManager.getInstance(psiFile.project).commitDocument(document)
            }
        }
    }
}
