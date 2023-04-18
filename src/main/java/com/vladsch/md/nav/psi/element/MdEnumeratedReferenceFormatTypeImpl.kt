// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import icons.MdIcons

import javax.swing.Icon

class MdEnumeratedReferenceFormatTypeImpl(node: ASTNode) : MdNamedElementImpl(node), MdEnumeratedReferenceFormatType {
    override fun getReferenceElement(): PsiElement {
        return parent
    }

    override fun createReference(textRange: TextRange, exactReference: Boolean): MdPsiReference {
        return MdPsiReference(this, textRange, exactReference)
    }

    override fun getDisplayName(): String {
        return name
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ENUMERATED_REFERENCE
    }

    override fun setName(newName: String, reason: Int): PsiElement? {
        val referenceFormat = MdPsiImplUtil.setEnumeratedReferenceFormatName(parent as MdEnumeratedReferenceFormatImpl, newName)
        return referenceFormat.nameIdentifier
    }

    override fun toString(): String {
        return "ENUMERATED_REFERENCE_FORMAT_TYPE '" + name + "' " + super.hashCode()
    }
}
