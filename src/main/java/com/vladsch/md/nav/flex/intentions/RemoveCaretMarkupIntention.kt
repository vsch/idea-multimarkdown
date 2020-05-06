// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiPlainText
import com.intellij.util.IncorrectOperationException
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.flex.psi.FlexmarkExampleSection
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings
import com.vladsch.md.nav.psi.element.MdParagraph
import com.vladsch.md.nav.psi.element.MdTextBlock
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.PsiElementPredicateWithEditor
import com.vladsch.plugin.util.TestUtils
import com.vladsch.plugin.util.minLimit

class RemoveCaretMarkupIntention : FlexIntention() {

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        val document = editor.document
        if (getElementPredicate().satisfiedBy(element)) {

            val text = BasedSequence.of(element.text)
            val textOffset = element.node.startOffset
            val selectionStart = if (editor.selectionModel.hasSelection()) editor.selectionModel.selectionStart else textOffset
            val selectionEnd = (if (editor.selectionModel.hasSelection()) editor.selectionModel.selectionEnd else text.length + textOffset).minLimit(selectionStart)

            var lastPosition = selectionEnd
            while (lastPosition > selectionStart) {
                val pos = text.lastIndexOfAny(TestUtils.CARET_MARKUP_SET, selectionStart - textOffset, lastPosition - textOffset)

                if (pos == -1) break
                document.deleteString(pos + textOffset, pos + textOffset + 1)
                lastPosition = pos - 1 + textOffset
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
                return (element is PsiPlainText
                    || element is MdParagraph
                    || element.parent is MdTextBlock
                    || element is FlexmarkExampleSection
                    || element.containingFile.context is FlexmarkExampleSection
                    || (element.toString() == "PsiJavaToken:STRING_LITERAL") || element.toString() == "PsiJavaToken:CHARACTER_LITERAL")
                    && BasedSequence.of(element.text).indexOfAny(TestUtils.CARET_MARKUP_SET) != -1
            }
        }
    }
}
