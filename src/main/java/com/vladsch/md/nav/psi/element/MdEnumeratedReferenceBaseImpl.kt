// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.vladsch.md.nav.psi.util.MdPsiImplUtil

abstract class MdEnumeratedReferenceBaseImpl(node: ASTNode) : MdReferencingElementImpl(node), MdEnumeratedReferenceBase {
    override fun getReferenceIdElement(): MdEnumeratedReferenceId? {
        return super.getReferenceIdElement() as? MdEnumeratedReferenceId
    }

    override fun getReferenceElements(): Array<out MdReferenceElement> {
        val referenceTypes = referenceIdElement?.typeList?.first ?: return arrayOf()
        val references: List<MdReferenceElement> =
            when {
                referenceTypes.size > 1 -> MdPsiImplUtil.getReferenceElements(containingFile as MdFile, referenceType, null, true).filter { referenceTypes.contains(it.referenceId) }
                referenceTypes.size == 1 -> MdPsiImplUtil.getReferenceElements(containingFile as MdFile, referenceType, referenceId, true)
                else -> listOf()
            }
        return references.toTypedArray()
    }
}
