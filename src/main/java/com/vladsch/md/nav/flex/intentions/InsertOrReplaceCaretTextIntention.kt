// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.intentions

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.vladsch.flexmark.util.misc.Utils.escapeJavaString
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.PsiElementPredicateWithEditor
import com.vladsch.plugin.util.debug

abstract class InsertOrReplaceCaretTextIntention : FlexIntention() {
    @Throws(IncorrectOperationException::class)
    final override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        val document = editor.document
        val elementPredicate = getElementPredicate()
        if (elementPredicate.satisfiedBy(element) &&
            (elementPredicate !is PsiElementPredicateWithEditor
                || elementPredicate.satisfiedBy(editor, editor.selectionModel.selectionStart, editor.selectionModel.selectionEnd))) {
            val carets = editor.caretModel.allCarets.toMutableList()
            carets.sortBy { it.offset }

            for (caret in carets.reversed()) {
                val selectionStart = caret.selectionStart
                val selectionEnd = caret.selectionEnd

                val text = getText(element, document.charsSequence, selectionStart, selectionEnd)

                if (selectionStart < selectionEnd) {
                    LOG.debug { (String.format("Replacing %d,%d with '%s' in '%s' mod: %d", selectionStart, selectionEnd, escapeJavaString(text), escapeJavaString(document.text), document.modificationStamp)) }
                    document.replaceString(selectionStart, selectionEnd, text)
                } else if (text.isNotEmpty()) {
                    LOG.debug { (String.format("Inserting %d '%s' in '%s' mod: %d", selectionStart, escapeJavaString(text), escapeJavaString(document.text), document.modificationStamp)) }
                    document.insertString(selectionStart, text)
                }
                LOG.debug { String.format("Result '%s', moving caret to %d mod: %d", escapeJavaString(document.text), caret.selectionStart + text.length, document.modificationStamp) }
                caret.moveToOffset(caret.selectionStart + text.length)
            }
        }
    }

    override fun isAvailableIn(file: PsiFile): Boolean {
        return MdApplicationSettings.instance.debugSettings.getExtension(FlexmarkDebugSettings.KEY).enableFlexmarkFeatures &&
            (super.isAvailableIn(file) || file.viewProvider.languages.contains(PlainTextFileType.INSTANCE.language) || file.fileType.defaultExtension == "java")
    }

    abstract fun getText(element: PsiElement, documentChars: CharSequence, selectionStart: Int, selectionEnd: Int): String

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.intentions")
    }
}
