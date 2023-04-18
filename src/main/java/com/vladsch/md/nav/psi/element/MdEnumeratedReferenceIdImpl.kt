// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.vladsch.flexmark.ext.enumerated.reference.EnumeratedReferenceRepository
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes

class MdEnumeratedReferenceIdImpl(node: ASTNode) : MdReferencingElementReferenceImpl(node), MdEnumeratedReferenceId {
    override fun getTypeText(): String {
        val typeText = EnumeratedReferenceRepository.getType(text)
        if (typeText.isEmpty()) {
            val header = MdPsiImplUtil.findAncestorOfType(this, MdHeaderElement::class.java)
            if (header is MdHeaderElement) return text ?: ""
        }
        return typeText
    }

    override fun getTypeList(): Pair<List<String>, List<TextRange>> {
        val rangeList = ArrayList<TextRange>()
        val typeList = ArrayList<String>()

        val typeText = "$typeText:"
        var lastPos = 0
        while (true) {
            val pos = typeText.indexOf(':', lastPos)
            if (pos == -1) {
                // special case, if this is the first and parent is Heading then it is a bare type
                if (lastPos == 0) {
                    val header = MdPsiImplUtil.findAncestorOfType(this, MdHeaderElement::class.java)
                    if (header is MdHeaderElement) {
                        rangeList.add(TextRange(lastPos, typeText.length))
                        typeList.add(typeText.substring(lastPos, typeText.length))
                    }
                }
                break
            }

            if (lastPos < pos) {
                rangeList.add(TextRange(lastPos, pos))
                typeList.add(typeText.substring(lastPos, pos))
            }
            lastPos = pos + 1
        }
        return Pair(typeList, rangeList)
    }

    override fun setType(newName: String, reason: Int): MdEnumeratedReferenceId {
        return MdPsiImplUtil.setEnumeratedReferenceType(this, newName)
    }

    override fun getReferenceDisplayName(): String {
        return "Enumerated Reference ID value"
    }

    override fun getReferenceType(): IElementType {
        return MdTypes.ENUM_REF_ID
    }

    override fun isAcceptable(referenceElement: PsiElement, forCompletion: Boolean, exactReference: Boolean): Boolean {
        return referenceElement is MdAttributeIdValue && referenceElement.isReferenceFor(text)
    }

    override fun toString(): String {
        return (parent as MdReferencingElement).toStringName + "_ID '" + name + "' " + super.hashCode()
    }
}
