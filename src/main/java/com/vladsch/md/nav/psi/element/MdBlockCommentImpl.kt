// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdTypes

class MdBlockCommentImpl(node: ASTNode) : MdCompositeImpl(node), MdBlockComment {
    override fun getCommentText(): String {
        return commentTextNode?.text ?: ""
    }

    override fun getCommentTextNode(): ASTNode? {
        return node.findChildByType(MdTypes.BLOCK_COMMENT_TEXT)
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, text: String): String {
            return "<!-- $text -->"
        }
    }
}
