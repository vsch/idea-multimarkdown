// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.vladsch.flexmark.html.renderer.HtmlIdGenerator
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import icons.MdIcons
import javax.swing.Icon

class MdRefAnchorImpl(node: ASTNode) : MdReferenceElementImpl(node), MdRefAnchor {

    override fun getReferenceDisplayName(): String {
        return REFERENCE_DISPLAY_NAME
    }

    override fun getReferenceIdentifier(): MdRefAnchorId? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.ANCHOR_ID) as MdRefAnchorId?
    }

    override fun getAnchorText(): String? {
        return referenceIdentifier?.text
    }

    override fun getAnchorReferenceId(generator: HtmlIdGenerator?): String? {
        return anchorReferenceId
    }

    override fun isReferenceFor(refElement: MdReferencingElement?): Boolean {
        return refElement is MdLinkAnchor && isReferenceFor(refElement.referenceId)
    }

    override fun getReferenceType(): IElementType {
        return REFERENCE_TYPE
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ANCHOR
    }

    override fun getReferencingElementText(): String? {
        return null
    }

    override fun normalizeReferenceId(referenceId: String?): String {
        return normalizeReferenceText(referenceId)
    }

    override fun getAttributesElement(): MdAttributes? {
        return null
    }

    override fun getIdValueAttribute(): MdAttributeIdValue? {
        return null
    }

    override fun getCompletionTypeText(): String {
        return "<a id=\"$anchorText\">"
    }

    override fun getAnchorReferenceId(): String? {
        return anchorText
    }

    override fun getAttributedAnchorReferenceId(): String? {
        return anchorText
    }

    override fun getAttributedAnchorReferenceId(htmlIdGenerator: HtmlIdGenerator?): String? {
        return anchorText
    }

    override fun getAnchorReferenceElement(): PsiElement? {
        return referenceIdentifier
    }

    override fun isReferenceFor(refElement: MdLinkAnchor?): Boolean {
        if (refElement == null) return false
        val refElementName = refElement.name
        return refElementName.equals(anchorReferenceId, ignoreCase = true)
    }

    companion object {
        val REFERENCE_DISPLAY_NAME: String = MdBundle.message("reference.type.anchor")
        val REFERENCE_TYPE: IElementType = MdTypes.DUMMY_REFERENCE

        @Suppress("NAME_SHADOWING", "UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, referenceId: String, text: String?): String {
            var text = text
            if (text == null) text = ""
            return "<a id=\"$referenceId\">$text</a>"
        }

        @JvmStatic
        fun normalizeReferenceText(referenceId: String?): String {
            return referenceId ?: ""
        }
    }
}
