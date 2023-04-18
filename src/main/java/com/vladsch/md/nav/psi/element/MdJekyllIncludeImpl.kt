// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.LinkRef
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.md.nav.util.WikiLinkRef
import com.vladsch.md.nav.vcs.MdLinkResolverManager
import icons.MdIcons
import javax.swing.Icon

class MdJekyllIncludeImpl(stub: MdJekyllIncludeStub?, nodeType: IStubElementType<MdJekyllIncludeStub, MdJekyllInclude>?, node: ASTNode?) :
    MdLinkElementImpl<MdJekyllInclude, MdJekyllIncludeStub>(stub, nodeType, node),
    MdJekyllInclude {

    constructor(node: ASTNode) : this(null, null, node)

    override fun getLinkRefElement(): MdJekyllIncludeLinkRef? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.JEKYLL_INCLUDE_TAG_LINK_REF) as? MdJekyllIncludeLinkRef
    }

    override fun getLinkRefText(): String {
        return MdPsiImplUtil.getLinkRefText(this)
    }

    override fun createLinkRef(containingFile: FileRef, linkRefText: String, linkAnchorText: String?, targetRef: FileRef?): LinkRef {
        return LinkRef(containingFile, linkRefText, linkAnchorText, targetRef, false)
    }

    override fun getLinkAnchorElement(): MdLinkAnchor? {
        return null
    }

    override fun getLinkTitleElement(): MdLinkTitle? {
        return null
    }

    override fun getLinkTextElement(): MdLinkText? {
        return null
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.LINK
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("jekyll-include")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    override fun getMissingElementNameSpace(prefix: String, addLinkRef: Boolean) = getMissingElementNameSpace(this, prefix, addLinkRef)

    companion object {
        fun getMissingElementNameSpace(element: MdJekyllInclude, prefix: String, addLinkRef: Boolean): String {
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

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, linkRef: String): String {
            return "{% include $linkRef %}\n"
        }
    }
}
