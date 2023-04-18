// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.DumbAware
import com.vladsch.md.nav.actions.styling.util.MdActionUtil
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.plugin.util.maxLimit

class ShowTextHexAction : AnAction(), DumbAware {
    override fun isDumbAware(): Boolean {
        return true
    }

    override fun update(e: AnActionEvent) {
        val debugSettings = MdApplicationSettings.instance.debugSettings
        if (debugSettings.showTextHexDialog) {
            MdActionUtil.getConditionBuilder(e, this).done()
        } else {
            e.presentation.isVisible = false
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        MdActionUtil.getProjectEditorPsiFile(e)?.let { (project, editor, _) ->
            // load all markdown files one by one and wait for them to render before loading the next one
            val primaryCaret = editor.caretModel.primaryCaret
            val document = editor.document
            val hadSelection = primaryCaret.hasSelection()
            val caretOffset = primaryCaret.offset
            if (!hadSelection) {
                // select the current line
                val line = document.getLineNumber(caretOffset)
                val start = document.getLineStartOffset(line)
                val end = document.getLineEndOffset(line)
                primaryCaret.setSelection(start, end)
            }

            val replacementText = TextHexDialog.showDialog(editor.component, primaryCaret.selectedText ?: "")
            if (replacementText != null) {
                val selectionStart = primaryCaret.selectionStart
                val selectionEnd = primaryCaret.selectionEnd
                WriteCommandAction.runWriteCommandAction(project, Runnable {
                    document.replaceString(selectionStart, selectionEnd, replacementText)
                    if (!hadSelection) {
                        val offset = caretOffset.maxLimit(document.textLength)
                        primaryCaret.setSelection(offset, offset)
                    }
                })
            } else if (!hadSelection) {
                primaryCaret.setSelection(caretOffset, caretOffset)
            }
        }
    }
}
