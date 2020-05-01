// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.api.MdElementTextProvider
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.plugin.util.maxLimit

open class MdAtxHeaderImpl(node: ASTNode) : MdHeaderElementImpl(node), MdAtxHeader {
    override fun getCanIncreaseLevel(): Boolean {
        return headerLevel < 6
    }

    override fun getCanDecreaseLevel(): Boolean {
        return headerLevel > 1
    }

    override fun getHeaderMarkerNode(): ASTNode? {
        val marker = node.findChildByType(MdTypes.HEADER_ATX_MARKER)
        return marker
    }

    override fun getHeaderLevel(): Int {
        return (headerMarkerNode?.text?.length ?: 0).maxLimit(6)
    }

    override fun setHeaderLevel(level: Int, editContext: PsiEditContext): MdHeaderElement? {
        assert(level in 1 .. 6)
        return MdPsiImplUtil.setHeaderLevel(this, level, trailingAttributesLength, editContext)
    }

    override fun getHeaderTextElement(): MdHeaderText? {
        val headerText = (MdPsiImplUtil.findChildByType(this, MdTypes.HEADER_TEXT) as MdHeaderText?)
        return headerText
    }

    override fun getHeaderText(): String {
        return headerTextElement?.text ?: ""
    }

    override fun getHeaderTextNoFormatting(): String {
        val headerTextElement = headerTextElement ?: return ""
        return MdPsiImplUtil.getNodeText(headerTextElement, true, false)
    }

    override fun getHeaderMarker(): String {
        return headerMarkerNode?.text ?: ""
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    override fun setName(newName: String, reason: Int): PsiElement {
        return this
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.psi.atx-header")

        fun getElementText(factoryContext: MdFactoryContext, text: CharSequence, level: Int, hasTrailingMarker: Boolean): CharSequence {
            return MdElementTextProvider.getElementText { it.getAtxHeaderText(factoryContext, text, level, hasTrailingMarker) }
        }
    }
}
