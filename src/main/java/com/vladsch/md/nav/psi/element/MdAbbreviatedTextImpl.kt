// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdTypes
import icons.MdIcons
import javax.swing.Icon

class MdAbbreviatedTextImpl(node: ASTNode) : MdReferencingElementImpl(node), MdAbbreviatedText {
    companion object {
        private val STRING_NAME = "ABBREVIATED_TEXT"

        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, referenceId: String): String {
            return "$referenceId\n\n*[$referenceId]: dummy"
        }
    }

    override fun getToStringName(): String {
        return STRING_NAME
    }

    override fun getReferenceChildType(): IElementType {
        return MdTypes.ABBREVIATED_TEXT
    }

    override fun getTextChildType(): IElementType {
        return MdTypes.NONE
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.ABBREVIATION
    }
}
