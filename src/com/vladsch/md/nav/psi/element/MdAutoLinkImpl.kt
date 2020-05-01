// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
import com.vladsch.plugin.util.prefixWith
import icons.MdIcons
import javax.swing.Icon

class MdAutoLinkImpl(stub: MdAutoLinkStub?, nodeType: IStubElementType<MdAutoLinkStub, MdAutoLink>?, node: ASTNode?) :
    MdLinkElementImpl<MdAutoLink, MdAutoLinkStub>(stub, nodeType, node),
    MdStructureViewPresentableElement, MdStructureViewPresentableItem,
    MdAutoLink {

    constructor(stub: MdAutoLinkStub, nodeType: IStubElementType<MdAutoLinkStub, MdAutoLink>) : this(stub, nodeType, null)
    constructor(node: ASTNode) : this(null, null, node)

    override fun createLinkRef(containingFile: FileRef, linkRefText: String, linkAnchorText: String?, targetRef: FileRef?): LinkRef {
        return LinkRef(containingFile, linkRefText, linkAnchorText, targetRef, false)
    }

    override fun getLinkRefElement(): MdLinkRefElement? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.AUTO_LINK_REF) as MdLinkRefElement?
    }

    override fun getLinkAnchorElement(): MdLinkAnchor? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.AUTO_LINK_ANCHOR) as MdLinkAnchor?
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

    override fun getPresentableText(): String? {
        //        return PsiBundle.message("image")
        return linkRefText
    }

    override fun getLocationString(): String? {
        return linkRefText
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("auto-link")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    companion object {
        private fun getElementText(wrapInAngleBrackets: Boolean, linkRefWithAnchor: String): String {
            return if (wrapInAngleBrackets) {
                "<" + getElementLinkRefWithAnchor(linkRefWithAnchor) + ">"
            } else {
                getElementLinkRefWithAnchor(linkRefWithAnchor)
            }
        }

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, wrapInAngleBrackets: Boolean, linkRefWithAnchor: String): String {
            return getElementText(wrapInAngleBrackets, linkRefWithAnchor)
        }

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, wrapInAngleBrackets: Boolean, linkRef: String, linkAnchor: String?): String {
            return getElementText(wrapInAngleBrackets, linkRef + linkAnchor.prefixWith('#'))
        }
    }
}
