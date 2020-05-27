// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.reference.MdPsiReferenceAnchorRefId
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import icons.MdIcons
import javax.swing.Icon

class MdRefAnchorIdImpl(node: ASTNode) : MdRenameElementImpl(node), MdRefAnchorId {

    override fun getReferenceElement(): PsiElement {
        return parent
    }

    override fun createReference(textRange: TextRange, exactReference: Boolean): MdPsiReference? {
        return null
    }

    override fun getDisplayName(): String {
        return name
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ANCHOR
    }

    override fun getReference(): PsiReference? {
        return MdPsiReferenceAnchorRefId(this, parent as MdRefAnchor, TextRange(0, textLength), false)
    }

    override fun setName(newName: String, reason: Int): PsiElement? {
        val element = MdPsiImplUtil.setRefAnchorName(parent as MdRefAnchorImpl, newName)
        return element.referenceIdentifier
    }

    override fun toString(): String {
        return "ANCHOR_ID '" + name + "' " + super.hashCode()
    }
}
