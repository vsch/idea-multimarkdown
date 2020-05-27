// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.util.IncorrectOperationException
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.plugin.util.maxLimit

abstract class MdReferenceElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdReferenceElement, MdNamedElement {

    override fun isReferenceFor(referenceId: String?): Boolean {
        return normalizeReferenceId(getReferenceId()) == normalizeReferenceId(referenceId)
    }

    override fun getReferenceId(): String {
        val element = referenceIdentifier
        val name = element?.name
        return name ?: ""
    }

    override fun getExactReference(): PsiReference? {
        return createReference(TextRange(0, node.textLength), true)
    }

    override fun getDisplayName(): String {
        return name
    }

    override fun toString(): String {
        return referenceDisplayName.toUpperCase() + " '" + name + "' " + super.hashCode()
    }

    //@Override
    //public String getDisplayName() {return getReferenceIdentifier().getDisplayName();}
    override fun setName(newName: String): PsiElement {
        return if (referenceIdentifier == null) this else referenceIdentifier!!.setName(newName)
    }

    override fun setName(newName: String, reason: Int): PsiElement {
        return if (referenceIdentifier == null) this else referenceIdentifier!!.setName(newName, reason)
    }

    override fun getName(): String {
        return if (referenceIdentifier == null) "" else referenceIdentifier!!.name
    }

    override fun getNameIdentifier(): PsiElement? {
        return referenceIdentifier
    }

    override fun isReferenced(): Boolean {
        return MdPsiImplUtil.isElementReferenced(this.mdFile, this)
    }

    override fun getPresentation(): ItemPresentation {
        return MdPsiImplUtil.getPresentation(this)
    }

    override fun getLocationString(): String? {
        return if (text == null) "" else text.substring(0, text.length.maxLimit(50))
    }

    override fun getPresentableText(): String {
        return displayName
    }

    //@Override
    //public ItemPresentation getPresentation() {return getReferenceIdentifier().getPresentation();}
    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(range: TextRange, newContent: String): MdRenameElement {
        return if (referenceIdentifier == null) this else referenceIdentifier!!.handleContentChange(range, newContent)
    }

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(newContent: String): MdRenameElement {
        return if (referenceIdentifier == null) this else referenceIdentifier!!.handleContentChange(newContent)
    }

    override fun createReference(textRange: TextRange, exactReference: Boolean): MdPsiReference? {
        return null
    }

    override fun isInplaceRenameAvailable(context: PsiElement?): Boolean {
        return referenceIdentifier != null && referenceIdentifier!!.isInplaceRenameAvailable(context)
    }

    override fun isMemberInplaceRenameAvailable(context: PsiElement?): Boolean {
        return referenceIdentifier != null && referenceIdentifier!!.isMemberInplaceRenameAvailable(context)
    }
}
