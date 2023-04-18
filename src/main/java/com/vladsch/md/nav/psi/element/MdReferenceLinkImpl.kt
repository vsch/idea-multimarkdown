// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdTypes

class MdReferenceLinkImpl(node: ASTNode) : MdReferencingElementImpl(node), MdReferenceLink {
    override fun getToStringName(): String {
        return STRING_NAME
    }

    override fun getReferenceChildType(): IElementType {
        return MdTypes.REFERENCE_LINK_REFERENCE
    }

    override fun getTextChildType(): IElementType {
        return MdTypes.REFERENCE_LINK_TEXT
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("reference-link")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    companion object {
        private val STRING_NAME = "REFERENCE_LINK"

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, referenceId: String, referenceText: String?): String {
            if (referenceText == null) {
                return "[$referenceId]"
            } else if (referenceText.isEmpty()) {
                // dummy reference
                return "[$referenceId][]"
            } else {
                return "[$referenceText][$referenceId]"
            }
        }
    }
}
