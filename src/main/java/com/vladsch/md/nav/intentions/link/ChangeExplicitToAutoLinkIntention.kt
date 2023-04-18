// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.intentions.link

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.intentions.Intention
import com.vladsch.md.nav.psi.element.MdExplicitLink
import com.vladsch.md.nav.util.PsiElementPredicate

class ChangeExplicitToAutoLinkIntention : Intention(), LowPriorityAction {

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        if (element !is MdExplicitLink || !isConvertible(element)) return
        val document = editor.document

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)

        val text = element.linkText?.trim()
        val url = if (text.isNullOrEmpty()) element.linkRefText else text
        val autoUrl = if (text.isNullOrEmpty()) {
            if (url.startsWith("https://www.")) url.substring("https://".length)
            else if (url.startsWith("http://www.")) url.substring("http://".length)
            else url
        } else url
        val pos = element.getTextOffset()
        document.replaceString(pos, pos + element.getTextLength(), autoUrl)
    }

    fun isConvertible(element: MdExplicitLink): Boolean {
        val url = element.linkRefWithAnchorText
        val text = element.linkText?.trim()

        return text == null || text.isBlank() || url == text || url == "https://$text" || url == "http://$text"
    }

    override fun getElementPredicate(): PsiElementPredicate {
        return PsiElementPredicate { element ->
            when (element) {
                is MdExplicitLink -> isConvertible(element)
                else -> false
            }
        }
    }
}
