// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes

abstract class MdReferencingElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdReferencingElement {
    override fun getReferenceType(): IElementType {
        val childElement = MdPsiImplUtil.findChildByType(this, referenceChildType) as MdReferencingElementReference?
        return childElement?.referenceType ?: MdTypes.WHITESPACE
    }

    override fun getReferenceId(): String {
        val childElement = MdPsiImplUtil.findChildByType(this, referenceChildType) as MdReferencingElementReference?
        return childElement?.name ?: return ""
    }

    override fun getReferenceIdElement(): MdReferencingElementReference? {
        return MdPsiImplUtil.findChildByType(this, referenceChildType) as MdReferencingElementReference?
    }

    override fun getReferenceText(): String? {
        return referenceTextElement?.text
    }

    override fun getReferenceTextElement(): MdReferencingElementText? {
        return MdPsiImplUtil.findChildByType(this, textChildType) as MdReferencingElementText?
    }

    override fun getReferenceElements(): Array<out MdReferenceElement> {
        val references = MdPsiImplUtil.getReferenceElements(containingFile as MdFile, referenceType, referenceId, true)
        return references.toTypedArray()
    }

    override fun getReferenceElement(): MdReferenceElement? {
        val references = referenceElements
        return if (references.isNotEmpty()) references[0] else null
    }

    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is MdPsiVisitor)
            visitor.visitElement(this)
        else
            super.accept(visitor)
    }

    override fun getDisplayName(): String? {
        return name
    }
}
