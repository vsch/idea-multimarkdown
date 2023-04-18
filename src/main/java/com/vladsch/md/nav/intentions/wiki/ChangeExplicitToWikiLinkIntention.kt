// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.intentions.wiki

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.intentions.Intention
import com.vladsch.md.nav.psi.element.MdExplicitLink
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.PsiElementPredicate

class ChangeExplicitToWikiLinkIntention : Intention(), LowPriorityAction {

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        if (element !is MdExplicitLink) return
        val resolvedElement = resolveElement<MdFile>(element.linkRefElement)
        MdPsiImplUtil.changeToWikiLink(element, resolvedElement != null && resolvedElement.isWikiPage)
    }

    override fun getElementPredicate(): PsiElementPredicate {
        return PsiElementPredicate { element ->
            when (element) {
                is MdExplicitLink -> {
                    val containingFile = element.containingFile
                    if (containingFile is MdFile && containingFile.isWikiPage && MdPsiImplUtil.isWikiLinkEquivalent(element)) {
                        val resolvedElement = resolveElement<PsiFile>(element.linkRefElement)
                        if (resolvedElement != null) {
                            val targetRef = FileRef(resolvedElement)
                            targetRef.isUnderWikiDir && FileRef(containingFile).wikiDir == targetRef.wikiDir
                        } else false
                    } else false
                }
                else -> false
            }
        }
    }
}
