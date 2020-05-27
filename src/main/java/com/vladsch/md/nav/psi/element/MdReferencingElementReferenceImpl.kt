// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.util.MdPsiImplUtil

abstract class MdReferencingElementReferenceImpl(node: ASTNode) : MdRenameElementImpl(node), MdReferencingElementReference {

    override fun getText(): String? {
        val name = node.text
        return name.replace("\\s?\n".toRegex(), " ")
    }

    override fun setName(newName: String, reason: Int): PsiElement {
        return MdPsiImplUtil.setName(this, newName, reason)
    }

    override fun createReference(textRange: TextRange, exactReference: Boolean): MdPsiReference {
        return MdPsiReference(this, textRange, exactReference)
    }

    override fun toString(): String {
        return (parent as MdReferencingElement).toStringName + "_REFERENCE '" + name + "' " + super.hashCode()
    }
}
