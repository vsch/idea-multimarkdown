// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings
import icons.MdIcons
import javax.swing.Icon

class MdGitLabBlockQuoteImpl(node: ASTNode) : MdCompositeImpl(node), MdGitLabBlockQuote, MdStructureViewPresentableElement, MdStructureViewPresentableItem, MdBreadcrumbElement {
    companion object;

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ASIDE_BLOCK
    }

    override fun getPresentableText(): String? {
        return MdPsiBundle.message("gitlab-block-quote")
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdPsiImplUtil.getPresentation(this)
    }

    override fun getOpenMarker(): String {
        val marker = node.findChildByType(MdTypes.GITLAB_BLOCK_QUOTE_MARKER)
        return marker!!.text
    }

    override fun getCloseMarker(): String {
        val marker = node.findChildByType(MdTypes.GITLAB_BLOCK_QUOTE_MARKER)
        val closeMarker = node.findChildByType(MdTypes.GITLAB_BLOCK_QUOTE_MARKER, marker!!.treeNext)
        return closeMarker!!.text
    }

    override fun getInfo(): String {
        val info = node.findChildByType(MdTypes.ADMONITION_INFO)
        return info!!.text
    }

    override fun getTitleElement(): ASTNode? {
        return node.findChildByType(MdTypes.ADMONITION_TITLE)
    }

    override fun getTitle(): String? {
        return node.findChildByType(MdTypes.ADMONITION_TITLE)?.text
    }

    override fun isFirstItemBlock(element: PsiElement): Boolean {
        val firstTextBlock = MdIndentingCompositeImpl.getItemBlock(this)
        return firstTextBlock !== null && (firstTextBlock === element) // || element.node.elementType == MultiMarkdownTypes.PARAGRAPH_BLOCK && firstTextBlock.parent === element)
    }

    override fun isFirstItemBlockPrefix(element: PsiElement): Boolean {
        return false
    }

    override fun getBreadcrumbInfo(): String {
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText) {
            val marker = node.findChildByType(MdTypes.GITLAB_BLOCK_QUOTE_MARKER)
            return marker!!.text
        }
        return MdPsiBundle.message("gitlab-block-quote")
    }

    override fun getBreadcrumbTooltip(): String? {
        return null
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }
}
