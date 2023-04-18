// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.util.FileRef
import com.vladsch.md.nav.util.LinkRef
import com.vladsch.md.nav.util.PathInfo
import com.vladsch.plugin.util.prefixWith
import com.vladsch.plugin.util.wrapWith
import icons.MdIcons
import javax.swing.Icon

//class MultiMarkdownExplicitLinkImpl(node: ASTNode) : MultiMarkdownLinkElementImpl(node), MultiMarkdownExplicitLink, MarkdownStructureViewPresentableElement, MarkdownStructureViewPresentableItem {
class MdExplicitLinkImpl(stub: MdExplicitLinkStub?, nodeType: IStubElementType<MdExplicitLinkStub, MdExplicitLink>?, node: ASTNode?) :
    MdLinkElementImpl<MdExplicitLink, MdExplicitLinkStub>(stub, nodeType, node),
    MdStructureViewPresentableElement, MdStructureViewPresentableItem,
    MdExplicitLink {

    constructor(stub: MdExplicitLinkStub, nodeType: IStubElementType<MdExplicitLinkStub, MdExplicitLink>) : this(stub, nodeType, null)
    constructor(node: ASTNode) : this(null, null, node)

    override fun createLinkRef(containingFile: FileRef, linkRefText: String, linkAnchorText: String?, targetRef: FileRef?): LinkRef {
        return LinkRef(containingFile, linkRefText, linkAnchorText, targetRef, false)
    }

    override fun getLinkRefElement(): MdLinkRefElement? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.LINK_REF) as MdLinkRefElement?
    }

    override fun getLinkAnchorElement(): MdLinkAnchor? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.LINK_REF_ANCHOR) as MdLinkAnchor?
    }

    override fun getLinkTitleElement(): MdLinkTitle? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.LINK_REF_TITLE) as MdLinkTitle?
    }

    override fun getLinkTextElement(): MdLinkText? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.LINK_REF_TEXT) as MdLinkText?
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.LINK
    }

    override fun getPresentableText(): String? {
        //        return PsiBundle.message("image")
        return linkRefText
    }

    override fun getLocationString(): String? {
        return linkText
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("explicit-link")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    companion object {
        private fun getElementText(linkRefWithAnchor: String, linkText: String?, linkTitle: String?): String {
            @Suppress("NAME_SHADOWING") var linkText = linkText
            if (linkText == null || linkText.isEmpty()) linkText = PathInfo(linkRefWithAnchor).fileNameNoExt
            return "[" + linkText + "](" + getElementLinkRefWithAnchor(linkRefWithAnchor) + linkTitle.wrapWith(" \"", "\"") + ")"
        }

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, linkRefWithAnchor: String, linkText: String?, linkTitle: String?): String {
            return getElementText(linkRefWithAnchor, linkText, linkTitle)
        }

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, linkRef: String, linkText: String?, linkAnchor: String?, linkTitle: String?): String {
            return getElementText(linkRef + linkAnchor.prefixWith('#'), linkText, linkTitle)
        }
    }
}
