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
import com.vladsch.md.nav.util.ImageLinkRef
import com.vladsch.md.nav.util.LinkRef
import com.vladsch.plugin.util.prefixWith
import com.vladsch.plugin.util.suffixWith
import com.vladsch.plugin.util.wrapWith
import icons.MdIcons
import javax.swing.Icon

//class MultiMarkdownImageLinkImpl(node: ASTNode) : MultiMarkdownLinkElementImpl(node), MultiMarkdownImageLink, MarkdownStructureViewPresentableElement, MarkdownStructureViewPresentableItem {
class MdImageLinkImpl(stub: MdImageLinkStub?, nodeType: IStubElementType<MdImageLinkStub, MdImageLink>?, node: ASTNode?) :
    MdLinkElementImpl<MdImageLink, MdImageLinkStub>(stub, nodeType, node),
    MdStructureViewPresentableElement, MdStructureViewPresentableItem,
    MdImageLink {

    constructor(stub: MdImageLinkStub, nodeType: IStubElementType<MdImageLinkStub, MdImageLink>) : this(stub, nodeType, null)
    constructor(node: ASTNode) : this(null, null, node)

    override fun isMultiLineURL(): Boolean {
        return linkRefUrlContentElement != null
    }

    override fun getLinkRefElement(): MdLinkRefElement? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.IMAGE_LINK_REF) as MdLinkRefElement?
    }

    override fun getLinkTextElement(): MdLinkText? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.IMAGE_LINK_REF_TEXT) as MdLinkText?
    }

    override fun getLinkRefUrlContentElement(): MdImageMultiLineUrlContentImpl? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.IMAGE_URL_CONTENT) as MdImageMultiLineUrlContentImpl?
    }

    override fun getLinkAnchorElement(): MdLinkAnchor? {
        return null
    }

    override fun getLinkTitleElement(): MdLinkTitle? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.IMAGE_LINK_REF_TITLE) as MdLinkTitle?
    }

    override fun createLinkRef(containingFile: FileRef, linkRefText: String, linkAnchorText: String?, targetRef: FileRef?): LinkRef {
        return ImageLinkRef(containingFile, linkRefText, linkAnchorText, targetRef, false)
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.IMAGE
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
        return MdPsiBundle.message("image")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, linkRef: String, linkText: String?, linkTitle: String?): String {
            @Suppress("NAME_SHADOWING")
            var linkText = linkText
            if (linkText == null) linkText = ""

            if (linkRef.contains("\n")) {
                return "![" + linkText + "](" + linkRef.suffixWith('\n') + linkTitle.wrapWith("\"", "\"") + ")"
            } else {
                return "![" + linkText + "](" + linkRef + linkTitle.wrapWith(" \"", "\"") + ")"
            }
        }

        fun getElementText(factoryContext: MdFactoryContext, linkRef: String, linkText: String?, linkAnchor: String?, linkTitle: String?): String {
            return getElementText(factoryContext, linkRef + linkAnchor.prefixWith('#'), linkText, linkTitle)
        }
    }
}
