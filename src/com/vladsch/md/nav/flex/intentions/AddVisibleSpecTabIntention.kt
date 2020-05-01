// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPlainText
import com.vladsch.flexmark.test.util.TestUtils
import com.vladsch.md.nav.psi.element.MdBlankLine
import com.vladsch.md.nav.psi.element.MdParagraph
import com.vladsch.md.nav.flex.psi.FlexmarkExampleSection
import com.vladsch.md.nav.psi.element.MdTextBlock
import com.vladsch.md.nav.util.PsiElementPredicateWithEditor

class AddVisibleSpecTabIntention : InsertOrReplaceCaretTextIntention() {

    override fun getText(element: PsiElement, documentChars: CharSequence, selectionStart: Int, selectionEnd: Int): String {
        return TestUtils.toVisibleSpecText("\t")
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
