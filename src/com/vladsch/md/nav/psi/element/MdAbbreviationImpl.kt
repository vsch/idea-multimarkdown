// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import icons.MdIcons
import javax.swing.Icon

open class MdAbbreviationImpl(node: ASTNode) : MdReferenceElementImpl(node), MdAbbreviation, MdStructureViewPresentableElement, MdStructureViewPresentableItem {
    companion object {
        val REFERENCE_DISPLAY_NAME: String = MdBundle.message("reference.type.abbreviation")
        val REFERENCE_TYPE: IElementType = MdTypes.ABBREVIATION

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, referenceId: String, expandedText: String): String {
            return "*[$referenceId]: $expandedText"
        }

        @JvmStatic
        fun normalizeReferenceText(referenceId: String?): String {
            return referenceId ?: ""
        }
    }

    override fun getReferenceIdentifier(): MdAbbreviationId? {
        val id = MdPsiImplUtil.findChildByType(this, MdTypes.ABBREVIATION_SHORT_TEXT) as MdAbbreviationId?
        return id
    }

    override fun createReference(textRange: TextRange, exactReference: Boolean): MdPsiReference? {
        return MdPsiReference(this, textRange, exactReference)
    }

    override fun getReferencingElementText(): String? {
        return referenceId
    }

    override fun getReferenceDisplayName(): String {
        return REFERENCE_DISPLAY_NAME
    }

    override fun getNameIdentifier(): PsiElement? {
        return referenceIdentifier
    }

    override fun getDisplayName(): String {
        return breadcrumbInfo
    }

    override fun getReferenceType(): IElementType {
        return REFERENCE_TYPE
    }

    override fun isReferenceFor(refElement: MdReferencingElement?): Boolean {
        return refElement is MdAbbreviatedTextImpl
    }

    override fun isReferenceFor(referenceId: String?): Boolean {
        return abbrText == referenceId
    }

    override fun normalizeReferenceId(referenceId: String?): String {
        return normalizeReferenceText(referenceId)
    }

    override fun getReferenceId(): String {
        return abbrText
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ABBREVIATION
    }

    override fun getAbbrText(): String = abbrTextNode?.text ?: ""

    override fun getAbbrTextNode(): ASTNode? = MdPsiImplUtil.findNestedChildByType(this, MdTypes.ABBREVIATION_SHORT_TEXT)?.node

    override fun getAbbrTextRange(): TextRange? = abbrTextNode?.textRange

    override fun getExpandedText(): String = expandedTextNode?.text ?: ""

    override fun getExpandedTextNode(): ASTNode? = MdPsiImplUtil.findNestedChildByType(this, MdTypes.ABBREVIATION_EXPANDED_TEXT)?.node

    override fun getExpandedTextRange(): TextRange? = expandedTextNode?.textRange

    override fun isReferenced(): Boolean {
        val abbreviations = MdPsiImplUtil.findChildrenByType(containingFile.node, true, abbrText, 2, MdTypes.ABBREVIATED_TEXT)
            ?: return false
        return abbreviations.isNotEmpty()
    }

    override fun getPresentableText(): String {
        //        return PsiBundle.message("abbreviation")
        return abbrText
    }

    override fun getLocationString(): String? {
        //        val abbr = abbrText
        return expandedText
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("abbreviation")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdPsiImplUtil.getPresentation(this)
    }
}
