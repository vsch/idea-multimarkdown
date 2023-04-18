// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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

class MdAdmonitionImpl(node: ASTNode) : MdIndentingCompositeImpl(node), MdAdmonition, MdStructureViewPresentableElement, MdStructureViewPresentableItem, MdBreadcrumbElement {
    companion object {
        @JvmField
        val INDENT_PREFIX = "    "
    }

    override fun isTextStart(node: ASTNode): Boolean {
        return node !== node.firstChildNode
    }

    override fun isFirstItemBlock(element: PsiElement): Boolean {
        return false
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ASIDE_BLOCK
    }

    override fun getPresentableText(): String? {
        return MdPsiBundle.message("block-quote")
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun isEmptyText(): Boolean {
        return true
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdPsiImplUtil.getPresentation(this)
    }

    override fun getMarker(): String {
        val marker = node.findChildByType(MdTypes.ADMONITION_MARKER)
        return marker!!.text
    }

    override fun getInfoNode(): ASTNode? {
        return node.findChildByType(MdTypes.ADMONITION_INFO)
    }

    override fun getInfo(): String {
        val info = infoNode
        return info?.text ?: ""
    }

    override fun getTitleElement(): ASTNode? {
        return node.findChildByType(MdTypes.ADMONITION_TITLE)
    }

    override fun getTitle(): String? {
        return node.findChildByType(MdTypes.ADMONITION_TITLE)?.text
    }

    override fun getBreadcrumbInfo(): String {
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText) {
            val marker = node.findChildByType(MdTypes.ADMONITION_MARKER)
            return marker!!.text
        }
        return MdPsiBundle.message("admonition-block")
    }

    override fun getBreadcrumbTooltip(): String? {
        return null
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }
}
