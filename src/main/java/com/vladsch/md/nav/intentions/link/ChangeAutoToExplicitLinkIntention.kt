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
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.element.MdAutoLink
import com.vladsch.md.nav.psi.element.MdExplicitLinkImpl
import com.vladsch.md.nav.util.PsiElementPredicate

class ChangeAutoToExplicitLinkIntention : Intention(), LowPriorityAction {

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        if (element !is MdAutoLink) return
        val document = editor.document

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)

        val text = element.linkRefText
        val url = if (text.startsWith("www.")) "https://$text" else text
        val factoryContext = MdFactoryContext(element)
        val mappedText = MdExplicitLinkImpl.getElementText(factoryContext, url, text, null)
        val pos = element.getTextOffset()
        document.replaceString(pos, pos + element.getTextLength(), mappedText)
    }

    override fun getElementPredicate(): PsiElementPredicate {
        return PsiElementPredicate { element ->
            when (element) {
                is MdAutoLink -> true
                else -> false
            }
        }
    }
}
