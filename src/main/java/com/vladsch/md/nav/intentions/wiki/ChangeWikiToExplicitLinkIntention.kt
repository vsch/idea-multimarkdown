// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.intentions.wiki

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.intentions.Intention
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.element.MdExplicitLinkImpl
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.element.MdWikiLink
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.util.*
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.md.nav.vcs.MdLinkResolverManager

class ChangeWikiToExplicitLinkIntention : Intention(), LowPriorityAction {

    @Throws(IncorrectOperationException::class)
    override fun processIntention(element: PsiElement, project: Project, editor: Editor, editContext: PsiEditContext) {
        if (element !is MdWikiLink) return
        val document = editor.document

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)

        val markdownFile = element.containingFile as MdFile
        if (!markdownFile.isWikiPage) {
            val linkRef = element.linkRef
            val resolver = GitHubLinkResolver(element.containingFile)
            var linkRefText: String? = null

            // need to set destination for wiki page in the project wiki
            val basePath = project.basePath
            if (basePath != null) {
                val projectInfo = PathInfo(basePath)
                val wikiRef = FileRef(projectInfo.filePath + "/" + projectInfo.fileName + ".wiki/" + linkRef.filePathWithAnchor)
                val wikiRoot = MdLinkResolverManager.getInstance(project).getVcsRoot(wikiRef)
                if (wikiRoot?.isWiki == true) {
                    var link = wikiRoot.urlForVcsRemote(linkRef.filePath, null, null, null)
                    if (link != null) {
                        var altLinkRef = LinkRef(FileRef(markdownFile), link, linkRef.anchor, null, false)
                        val linkToFile = linkRef.linkToFile(linkRef.filePath)
                        var newLink = resolver.resolve(altLinkRef, Want(Local.REL, Remote.REL, Links.REL, Match.LOOSE))
                        if (newLink == null) {
                            link = wikiRoot.urlForVcsRemote(linkToFile, null, null, null)
                            if (link != null) {
                                altLinkRef = LinkRef(FileRef(markdownFile), link, linkRef.anchor, null, false)
                                newLink = resolver.resolve(altLinkRef, Want(Local.REL, Remote.REL, Links.REL, Match.LOOSE))
                            }
                        }
                        linkRefText = newLink?.filePath ?: linkToFile
                    }
                }
            }

            if (linkRefText != null) {
                val factoryContext = MdFactoryContext(markdownFile)
                val newLink = MdExplicitLinkImpl.getElementText(factoryContext, linkRefText, element.linkText ?: linkRef.filePathWithAnchor, null)
                val pos = element.getTextOffset()
                document.replaceString(pos, pos + element.getTextLength(), newLink)
            } else {
                // FEATURE: need to inform user of error
            }
        } else {
            val text = MdPsiImplUtil.getTextForChangeToExplicitLink(element, null)
            val mappedText = GitHubLinkResolver(element).denormalizedLinkRef(text)
            val pos = element.getTextOffset()
            document.replaceString(pos, pos + element.getTextLength(), mappedText)
        }
    }

    override fun getElementPredicate(): PsiElementPredicate {
        return PsiElementPredicate { element ->
            when (element) {
                is MdWikiLink -> true
                else -> false
            }
        }
    }
}
