// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdTypes

class MdEnumeratedReferenceLinkImpl(node: ASTNode) : MdEnumeratedReferenceBaseImpl(node), MdEnumeratedReferenceLink {
    override fun getToStringName(): String {
        return STRING_NAME
    }

    override fun getReferenceChildType(): IElementType {
        return MdTypes.ENUM_REF_ID
    }

    override fun getTextChildType(): IElementType {
        throw IllegalStateException("Enumerated references do not have a text child type")
    }

    companion object {
        private val STRING_NAME = "ENUMERATED_REFERENCE_LINK"

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, referenceId: String): String {
            return "[@$referenceId]"
        }
    }
}
