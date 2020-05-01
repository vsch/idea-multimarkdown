// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.intentions.wiki

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.intentions.Intention
import com.vladsch.md.nav.psi.element.MdWikiLink
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.util.PsiElementPredicate

class DeleteUnusedWikiLinkTextIntention : Intention() {

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        if (element !is MdWikiLink) return
        MdPsiImplUtil.deleteWikiLinkTitle(element)
    }

    override fun getElementPredicate(): PsiElementPredicate {
        return PsiElementPredicate { element ->
            when (element) {
                is MdWikiLink -> element.linkText != null && element.linkText == element.linkRefText
                else -> false
            }
        }
    }

    companion object : IntentionCompanion(DeleteUnusedWikiLinkTextIntention::class.java)
}
