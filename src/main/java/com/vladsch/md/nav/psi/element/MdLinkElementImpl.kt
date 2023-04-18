// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.IStubElementType
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.LinkRef
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.md.nav.util.WikiLinkRef
import com.vladsch.md.nav.vcs.MdLinkResolverManager
import com.vladsch.plugin.util.ifEmpty
import com.vladsch.plugin.util.prefixWith

abstract class MdLinkElementImpl<Elem : MdLinkElement<Stub>, Stub : MdLinkElementStub<Elem>>(stub: Stub?, nodeType: IStubElementType<Stub, Elem>?, node: ASTNode?) :
    MdStubElementImpl<Stub>(stub, nodeType, node), MdLinkElement<Stub> {

    override fun getDisplayName(): String {
        return linkText ?: ""
    }

    override fun getLinkText(): String? {
        return MdPsiImplUtil.getLinkText(this)
    }

    override fun getLinkRefText(): String {
        return MdPsiImplUtil.getLinkRefText(this)
    }

    override fun getLinkTitleText(): String? {
        return linkTitleElement?.text
    }

    override fun getLinkAnchorText(): String? {
        return linkAnchorElement?.text
    }

    override fun getLinkRefUrlContentElement(): MdImageMultiLineUrlContentImpl? {
        return null
    }

    override fun getLinkRefWithAnchorText(): String {
        val stub = stub
        if (stub != null) {
            return stub.linkRefWithAnchorText
        }

        val anchorElement = linkAnchorElement
        if (anchorElement != null) {
            return linkRefText + "#" + anchorElement.text
        }
        return linkRefText
    }

    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is MdPsiVisitor)
            visitor.visitElement(this)
        else
            super.accept(visitor)
    }

    override fun getMissingElementNameSpace(prefix: String, addLinkRef: Boolean) = getMissingElementNameSpace(this, prefix, addLinkRef)

    override fun getLinkRef() = MdPsiImplUtil.getLinkRef(this) ?: getLinkRef(this)

    companion object {
        fun getMissingElementNameSpace(element: MdLinkElement<*>, prefix: String, addLinkRef: Boolean): String {
            with(element) {
                val projectComponent = MdLinkResolverManager.getInstance(project)
                val psiFile = containingFile
                val virtualFile = psiFile.originalFile.virtualFile
                val filePathInfo = PathInfo(virtualFile)
                val gitHubVcsRoot = projectComponent.getGitHubRepo(filePathInfo.path)
                val vcsHome = if (gitHubVcsRoot != null) gitHubVcsRoot.basePath + "::" else ""

                if (addLinkRef) {
                    var pageRef = MdPsiImplUtil.getLinkRefTextWithAnchor(this)
                    if (pageRef.isEmpty()) pageRef = WikiLinkRef.fileAsLink(filePathInfo.fileName, null)
                    return prefix + if (vcsHome.isEmpty()) vcsHome else vcsHome + "::" + if (pageRef.isEmpty()) pageRef else pageRef + "::"
                }
                return prefix + if (vcsHome.isEmpty()) vcsHome else vcsHome + "::"
            }
        }

        fun getLinkRef(element: MdLinkElement<*>): LinkRef {
            with(element) {
                val linkRefElement = linkRefElement
                var targetRef: FileRef? = null
                if (linkRefElement != null) {
                    val psiReference = linkRefElement.reference
                    if (psiReference != null) {
                        val targetElement = psiReference.resolve()
                        if (targetElement is PsiFile) {
                            targetRef = FileRef((targetElement as PsiFile?)!!)
                        }
                    }
                }

                return createLinkRef(FileRef(containingFile.originalFile), linkRefText, linkAnchorText, targetRef)
            }
        }

        fun getElementLinkRefWithAnchor(linkRef: String, linkAnchor: String?): String {
            return linkRef.replace("#", "%23") + linkAnchor.prefixWith('#')
        }

        fun getElementLinkRefWithAnchor(linkRefWithAnchor: String): String {
            val useLinkRefWithAnchor = linkRefWithAnchor.ifEmpty("#")

            val anchorPos = useLinkRefWithAnchor.indexOf('#')
            if (anchorPos == 0) {
                return getElementLinkRefWithAnchor("", useLinkRefWithAnchor)
            } else if (anchorPos > 0) {
                val linkRef = useLinkRefWithAnchor.substring(0, anchorPos)
                val linkAnchor = useLinkRefWithAnchor.substring(anchorPos)
                return getElementLinkRefWithAnchor(linkRef, linkAnchor)
            } else {
                return getElementLinkRefWithAnchor(useLinkRefWithAnchor, null)
            }
        }
    }
}
