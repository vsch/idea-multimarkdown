// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdTypes

class MdInlineCommentImpl(node: ASTNode) : MdCompositeImpl(node), MdInlineComment {
    override fun getCommentText(): String {
        return commentTextNode?.text ?: ""
    }

    override fun getCommentTextNode(): ASTNode? {
        return node.findChildByType(MdTypes.COMMENT_TEXT)
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, text: String): String {
            return "<!-- $text -->"
        }
    }
}
