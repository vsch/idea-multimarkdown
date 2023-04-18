// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.psi.reference.MdPsiReference
import com.vladsch.md.nav.psi.util.MdTypes
import icons.MdIcons
import javax.swing.Icon

open class MdHeaderTextImpl(node: ASTNode) : MdNamedElementImpl(node), MdHeaderText {

    override fun createReference(textRange: TextRange, exactReference: Boolean): MdPsiReference? {
        return if (isAttributedId()) null else MdPsiReference(this, textRange, exactReference)
    }

    override fun isRenameAvailable(): Boolean {
        return !isAttributedId()
    }

    override fun getNameIdentifier(): PsiElement? {
        return if (isAttributedId()) null else this
    }

    private fun isAttributedId() = (parent as MdHeaderElement?)?.idValueAttribute != null

    override fun getAttributesElement(): MdAttributes? {
        return findChildByType(MdTypes.ATTRIBUTES)
    }

    override fun getIdValueAttribute(): PsiElement? {
        return attributesElement?.idValueAttribute
    }

    override fun getTrailingAttributesLength(): Int {
        // FIX: see if have trailing attributes that can be excluded from the text
        //val attributes = attributesElement ?: 0
        return 0
    }

    override fun getDisplayName(): String {
        return name
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.HEADER
    }

    override fun setName(newName: String, reason: Int): PsiElement? {
        return this
    }

    override fun isMemberInplaceRenameAvailable(context: PsiElement?): Boolean {
        val elementType = context?.node?.elementType
        return isRenameAvailable && (context is MdHeaderText || elementType == MdTypes.HEADER_TEXT || elementType == MdTypes.EOL && context?.node?.treePrev?.elementType == MdTypes.HEADER_TEXT)
    }

    override fun toString(): String {
        return "HEADER_TEXT '" + name + "' " + super.hashCode()
    }
}
