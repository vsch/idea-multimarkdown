// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.util.IncorrectOperationException
import com.intellij.util.Processor
import com.vladsch.flexmark.html.renderer.HtmlIdGenerator
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.util.MdIndexUtil
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import icons.MdIcons
import javax.swing.Icon

class MdAttributeIdValueImpl(node: ASTNode) : MdNamedElementImpl(node), MdAttributeIdValue {
    companion object {
        @JvmStatic
        fun normalizeReferenceText(referenceId: String?): String {
            return referenceId ?: ""
        }
    }

    override fun getReferenceId(): String {
        return text
    }

    override fun normalizeReferenceId(referenceId: String?): String {
        return normalizeReferenceText(referenceId)
    }

    override fun getReferenceIdentifier(): MdReferenceElementIdentifier? {
        return this
    }

    override fun getReferenceDisplayName(): String {
        return referenceId
    }

    override fun getAnchorReferenceElement(): PsiElement? {
        return this
    }

    override fun getReferencingElementText(): String? {
        return null
    }

    override fun isReferenceFor(referenceId: String?): Boolean {
        return getReferenceId() == referenceId
    }

    override fun getReferenceType(): IElementType {
        return MdTypes.ATTRIBUTE_ID_VALUE
    }

    override fun isReferenceFor(refElement: MdLinkAnchor?): Boolean {
        if (refElement == null) return false
        val refElementName = refElement.name
        return refElementName.equals(anchorReferenceId, ignoreCase = true)
    }

    override fun getCompletionTypeText(): String {
        val element = MdPsiImplUtil.findAncestorOfType(this, MdHeaderElement::class.java)
        return if (element is MdHeaderElement) {
            element.completionTypeText
        } else {
            "{#$text}"
        }
    }

    override fun getAnchorReferenceId(): String? {
        return referenceId
    }

    override fun getAnchorReferenceId(generator: HtmlIdGenerator?): String? {
        return referenceId
    }

    override fun getAttributedAnchorReferenceId(): String? {
        return referenceId
    }

    override fun getAttributedAnchorReferenceId(htmlIdGenerator: HtmlIdGenerator?): String? {
        return referenceId
    }

    override fun getAttributesElement(): MdAttributes? {
        return parent as MdAttributes?
    }

    override fun getIdValueAttribute(): MdAttributeIdValue? {
        return this
    }

    override fun getReferenceElement(): PsiElement {
        return this
    }

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(range: TextRange, newContent: String): MdAttributeIdValue {
        return handleContentChange(newContent)
    }

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(newContent: String): MdAttributeIdValue {
        return setName(newContent, MdNamedElement.REASON_FILE_RENAMED) as MdAttributeIdValue
    }

    override fun isReferenceFor(refElement: MdReferencingElement?): Boolean {
        return refElement != null && isReferenceFor(refElement.referenceId)
    }

    override fun isReferenced(): Boolean {
        var isReferenced = false
        MdIndexUtil.processReferences(this, GlobalSearchScope.projectScope(project), Processor {
            isReferenced = true
            false
        })
        return isReferenced
    }

    override fun createReference(textRange: TextRange, exactReference: Boolean): MdPsiReference? {
        return MdPsiReference(this, textRange, exactReference)
    }

    override fun getDisplayName(): String {
        return name
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ATTRIBUTE_ID_VALUE
    }

    override fun setName(newName: String, reason: Int): PsiElement? {
        val element = MdPsiImplUtil.setAttributeIdValueName(parent as MdAttributeImpl, newName)
        return element.attributeValueElement
    }

    override fun getTypeText(): String? {
        val pos = text.indexOf(':')
        if (pos > -1) {
            return text.substring(0, pos)
        }
        return ""
    }

    override fun setType(newName: String, reason: Int): MdAttributeIdValue? {
        val element = MdPsiImplUtil.setAttributeIdValueType(parent as MdAttributeImpl, newName)
        return element.attributeValueElement as MdAttributeIdValue?
    }

    override fun toString(): String {
        return "ID_ATTRIBUTE_VALUE '" + name + "' " + super.hashCode()
    }
}
