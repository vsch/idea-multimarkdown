// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiPlainText
import com.intellij.util.IncorrectOperationException
import com.vladsch.flexmark.util.sequence.Range
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.flex.psi.FlexmarkExampleSection
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings
import com.vladsch.md.nav.psi.element.MdBlankLine
import com.vladsch.md.nav.psi.element.MdParagraph
import com.vladsch.md.nav.psi.element.MdTextBlock
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.PsiElementPredicateWithEditor
import com.vladsch.plugin.util.TestUtils

class AddCaretSelectionMarkupIntention : FlexIntention() {

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        val document = editor.document
        if (getElementPredicate().satisfiedBy(element)) {
            val carets = editor.caretModel.allCarets.map { Range.of(it.selectionStart, it.selectionEnd) }.toMutableList()
            carets.sortWith(Comparator { o1, o2 -> o1.compare(o2) })

            for (range in carets.reversed()) {
                document.insertString(range.end, TestUtils.END_STRING)
                document.insertString(range.start, TestUtils.START_STRING)
            }
        }
    }

    override fun isAvailableIn(file: PsiFile): Boolean {
        return MdApplicationSettings.instance.debugSettings.getExtension(FlexmarkDebugSettings.KEY).enableFlexmarkFeatures &&
            (super.isAvailableIn(file) || file.viewProvider.languages.contains(PlainTextFileType.INSTANCE.language) || file.fileType.defaultExtension == "java")
    }

    override fun getElementPredicate(): PsiElementPredicateWithEditor {
        return object : PsiElementPredicateWithEditor {
            override fun satisfiedBy(editor: Editor, selectionStart: Int, selectionEnd: Int): Boolean {
                return true
            }

            override fun satisfiedBy(element: PsiElement): Boolean {
                return element is PsiPlainText
                    || element is MdParagraph
                    || element.parent is MdTextBlock
                    || element is MdBlankLine
                    || element is FlexmarkExampleSection
                    || element.containingFile.context is FlexmarkExampleSection
                    || element.toString() == "PsiJavaToken:STRING_LITERAL"
                    || element.toString() == "PsiJavaToken:CHARACTER_LITERAL"
            }
        }
    }
}
