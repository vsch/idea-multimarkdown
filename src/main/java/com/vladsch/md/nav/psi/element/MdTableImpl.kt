// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import icons.MdIcons
import javax.swing.Icon

open class MdTableImpl(node: ASTNode) : MdCompositeImpl(node), MdTable, MdStructureViewPresentableElement, MdStructureViewPresentableItem, MdBreadcrumbElement {
    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.TABLE
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getLocationString(): String? {
        val captionElement = captionElement
        if (captionElement != null) {
            val captionText = MdPsiImplUtil.getNodeText(captionElement, true, false)
            if (captionText.isNotEmpty()) return captionText
        }
        val header = MdPsiImplUtil.findChildByType(this, MdTypes.TABLE_HEADER)
        if (header != null) {
            val headerText = header.firstChild
            if (headerText != null && headerText.text.isNotEmpty()) return getTableRowText(headerText).replace("\\s+".toRegex(), " ")
        }
        val body = MdPsiImplUtil.findChildByType(this, MdTypes.TABLE_HEADER)
        if (body != null) {
            val bodyText = body.firstChild
            if (bodyText != null && bodyText.text.isNotEmpty()) return getTableRowText(bodyText).replace("\\s+".toRegex(), " ")
        }
        return null
    }

    private fun getTableRowText(tableRow: PsiElement): String {
        val sb = StringBuilder()
        sb.append("| ")
        for (cell in tableRow.children) {
            sb.append(MdPsiImplUtil.getNodeText(cell, true, false))
            sb.append(cell.node.treeNext.text)
        }
        return sb.toString()
    }

    val captionElement: PsiElement?
        get() {
            return MdPsiImplUtil.findChildByType(this, MdTypes.TABLE_CAPTION)
        }

    val captionText: String?
        get() {
            val captionElement = captionElement ?: return null
            val captionText = captionElement.text.removeSurrounding("[", "]")
            return captionText
        }

    override fun getPresentableText(): String? {
        return MdPsiBundle.message("table")
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("table")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }
}
