// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.vladsch.md.nav.actions.styling.util.MdActionUtil

class InsertLinkAction : AnAction() {
    override fun isDumbAware(): Boolean {
        return false
    }

    override fun update(e: AnActionEvent) {
        MdActionUtil.getConditionBuilder(e, this).done()
    }

    override fun actionPerformed(e: AnActionEvent) {
        MdActionUtil.getProjectEditorPsiFile(e)?.let { (_, editor, psiFile) ->
            WriteCommandAction.runWriteCommandAction(psiFile.project) {
                val document = editor.document

                editor.caretModel.runForEachCaret({ caret ->
                    val linkString = "[" + document.charsSequence.subSequence(caret.selectionStart, caret.selectionEnd).toString() + "]()"
                    var leadSpaces = ""
                    var trailSpaces = ""

                    if (caret.selectionEnd < document.charsSequence.length && !Character.isWhitespace(document.charsSequence[caret.selectionEnd])) {
                        trailSpaces = " "
                    }

                    if (caret.selectionStart > 0 && !Character.isWhitespace(document.charsSequence[caret.selectionStart - 1])) {
                        leadSpaces = " "
                    }

                    document.replaceString(caret.selectionStart, caret.selectionEnd, leadSpaces + linkString + trailSpaces)

                    val caretPos = if (caret.hasSelection()) caret.selectionEnd - 1 + leadSpaces.length else caret.offset + leadSpaces.length + 3
                    editor.caretModel.currentCaret.moveToOffset(caretPos)
                    editor.caretModel.currentCaret.setSelection(caretPos, caretPos)
                }, true)

                PsiDocumentManager.getInstance(psiFile.project).commitDocument(document)
            }
        }
    }
}
