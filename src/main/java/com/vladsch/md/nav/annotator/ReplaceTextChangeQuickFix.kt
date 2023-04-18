// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.annotator

import com.intellij.codeInsight.FileModificationService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.MdBundle

internal class ReplaceTextChangeQuickFix(val message: String, val offsetStart: Int, val offsetEnd: Int, val chars: String) : MdBaseIntentionAction() {
    constructor(message: String, offset: Int, chars: String) : this(message, offset, offset, chars)

    override fun getText(): String {
        return message
    }

    override fun getFamilyName(): String {
        return MdBundle.message("quickfix.replace-text.family-name")
    }

    override fun isAvailable(project: Project, document: Document, file: PsiFile): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, document: Document, file: PsiFile) {
        ApplicationManager.getApplication().invokeLater { replaceChars(project, file, document, offsetStart, offsetEnd, chars) }
    }

    override fun startInWriteAction(): Boolean {
        // we handle our own
        return false
    }

    companion object {
        @JvmStatic
        fun replaceChars(project: Project, file: PsiFile, document: Document, offsetStart: Int, offsetEnd: Int, chars: String) {
            if (!FileModificationService.getInstance().prepareFileForWrite(file)) return

            WriteCommandAction.runWriteCommandAction(project) {
                if (offsetStart == offsetEnd) {
                    document.insertString(offsetStart, chars)
                } else {
                    document.replaceString(offsetStart, offsetEnd, chars)
                }
            }
        }
    }
}
