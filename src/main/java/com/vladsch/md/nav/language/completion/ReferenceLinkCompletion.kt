// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion

import com.intellij.codeInsight.completion.CompletionInitializationContext
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.LinkRef
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.ui.Helpers
import javax.swing.Icon

class ReferenceLinkCompletion : MdElementCompletion {
    override fun getWantElement(element: PsiElement, elementPos: PsiElement, parameters: CompletionParameters, context: ProcessingContext): Boolean {
        return wantElement(element)
    }

    private fun wantElement(element: PsiElement) =
        element is MdReferenceImageReferenceImpl || element is MdReferenceLinkReferenceImpl || element is MdReferenceLink

    override fun duringCompletion(context: CompletionInitializationContext, element: PsiElement, elementPos: PsiElement): Boolean {
        return wantElement(element)
    }

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet, element: PsiElement, containingFile: MdFile): Boolean {
        val file = element.containingFile.originalFile as? MdFile ?: return true

        val references = MdPsiImplUtil.getReferenceElements(file, MdTypes.REFERENCE, null, true)
        val resolver = GitHubLinkResolver(containingFile)

        for (referenceElement in references) {
            val isAcceptable = (element as MdReferencingElementReference).isAcceptable(referenceElement, false, false)
            var icon: Icon? = null
            var path: String? = null

            if (referenceElement is MdReference) {
                val linkRefElement = MdPsiImplUtil.findChildByType(referenceElement, MdTypes.REFERENCE_LINK_REF) as MdReferenceLinkRefImpl?

                if (linkRefElement != null) {
                    val nameWithAnchor = linkRefElement.fileNameWithAnchor
                    path = nameWithAnchor

                    val reference = linkRefElement.reference
                    val resolved = reference?.resolve()
                    if (resolved != null) {
                        icon = resolved.getIcon(0)
                    } else {
                        // try to see what type of link it is
                        val linkRef = LinkRef.parseLinkRef(FileRef(containingFile), nameWithAnchor, null)
                        icon = resolver.getIcon(linkRef)
                    }
                }
            }

            var lookupElementBuilder = LookupElementBuilder.create(referenceElement.referenceId).withCaseSensitivity(false)
            if (icon != null) lookupElementBuilder = lookupElementBuilder.withIcon(icon)
            if (path != null) lookupElementBuilder = lookupElementBuilder.withTypeText(path, !isAcceptable)

            if (!isAcceptable) {
                // get error color from color settings
                lookupElementBuilder = lookupElementBuilder.withItemTextForeground(Helpers.errorColor())
            }
            resultSet.addElement(PrioritizedLookupElement.withPriority(lookupElementBuilder, if (isAcceptable) 0.0 else -1.0))
            //resultSet.addElement(lookupElementBuilder)
        }
        return true
    }
}
