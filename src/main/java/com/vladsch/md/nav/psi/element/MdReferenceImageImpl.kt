// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdTypes
import icons.MdIcons
import javax.swing.Icon

class MdReferenceImageImpl(node: ASTNode) : MdReferencingElementImpl(node), MdReferenceImage, MdStructureViewPresentableElement, MdStructureViewPresentableItem {
    companion object {
        private val STRING_NAME = "REFERENCE_IMAGE"

        fun getElementText(factoryContext: MdFactoryContext, referenceId: String, referenceText: String?): String {
            return "!" + MdReferenceLinkImpl.getElementText(factoryContext, referenceId, referenceText)
        }
    }

    override fun getToStringName(): String {
        return STRING_NAME
    }

    override fun getReferenceChildType(): IElementType {
        return MdTypes.REFERENCE_IMAGE_REFERENCE
    }

    override fun getTextChildType(): IElementType {
        return MdTypes.REFERENCE_IMAGE_TEXT
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.IMAGE
    }

    override fun getPresentableText(): String? {
        return MdPsiBundle.message("reference-image")
    }

    override fun getLocationString(): String? {
        return node.text
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("image")
    }

    override fun getBreadcrumbTooltip(): String? {
        return locationString
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }
}
