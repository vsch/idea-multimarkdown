// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.vladsch.flexmark.util.sequence.RepeatedSequence
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.MdActionUtil
import com.vladsch.plugin.util.maxLimit

class InsertTableAction : AnAction() {
    override fun isDumbAware(): Boolean {
        return false
    }

    override fun update(e: AnActionEvent) {
        MdActionUtil.getConditionBuilder(e, this) { it, (_, editor, psiFile) ->
            it.andSingleCaret(editor)
                .andNoSelection(editor)
                .and {
                    CaretContextInfo.withContextOrNull(psiFile, editor, null, false, editor.caretModel.primaryCaret.offset) { caretContext ->
                        it.notNull(caretContext) {
                            it.and(caretContext!!.caretLineChars.isBlank(), "Caret not on a blank line")
                        }
                    }
                }
        }.done(true, false)
    }

    override fun actionPerformed(e: AnActionEvent) {
        // FIX: need to let user select the row/column count for the table before inserting
        //   and to remember the last settings

        MdActionUtil.getProjectEditorPsiFile(e)?.let { (_, editor, psiFile) ->
            WriteCommandAction.runWriteCommandAction(psiFile.project) {
                val document = editor.document

                CaretContextInfo.withContext(psiFile, editor, null, false, editor.caretModel.primaryCaret.offset) { caretContext ->
                    if (caretContext.caretLineChars.isBlank()) {
                        // table will be inserted with a blank line above, blank line below and not indented for now

                        // need to find the first non-blank line above and get the listContext for it, if it exists then
                        // we can figure out the max indentation for the table

                        val indent = (((caretContext.caretOffset - caretContext.caretLineStart) / 4) * 4).maxLimit(40)
                        val columns = 1
                        val rows = 2

                        val indentString = RepeatedSequence.ofSpaces(indent)
                        val table = StringBuilder()
                        val startOffset = caretContext.caretLineStart
                        val endOffset = (caretContext.caretLineEnd + 1).maxLimit(caretContext.charSequence.length)
                        var firstColumnOffset = indent + 1

                        if (caretContext.previousLineChars != null && !caretContext.previousLineChars.isBlank()) {
                            table.append('\n')
                            firstColumnOffset++
                        }

                        // this one is for the leading table pipe
                        firstColumnOffset++

                        for (row in 1 .. rows + 1) {
                            table.append(indentString)
                            table.append("|")

                            for (col in 1 .. columns) {
                                if (col > 1) table.append("|")

                                if (row != 2) table.append("   ")
                                else table.append("---")
                            }
                            table.append("|\n")
                        }

                        if (caretContext.nextLineChars != null && !caretContext.nextLineChars.isBlank()) {
                            table.append('\n')
                        }

                        document.replaceString(startOffset, endOffset, table.toString())
                        //caret.setSelection(caret.getSelectionStart() + 3, caret.getSelectionStart() + 3);
                        editor.caretModel.moveToOffset(startOffset + firstColumnOffset)
                        PsiDocumentManager.getInstance(psiFile.project).commitDocument(document)
                    }
                }
            }
        }
    }
}
