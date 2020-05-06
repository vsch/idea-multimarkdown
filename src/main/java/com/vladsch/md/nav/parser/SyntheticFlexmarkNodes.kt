// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser

import com.intellij.psi.tree.IElementType
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.sequence.BasedSequence
import java.util.*

class SyntheticFlexmarkNodes(val node: Node, val nodeType: IElementType, val nodeTokenType: IElementType) {
    val chars: BasedSequence = node.chars
    val nodes = ArrayList<SyntheticNode>()

    val nodeStart: Int get() = chars.startOffset
    val nodeEnd: Int get() = chars.endOffset

    //    constructor(node: Node, nodeType: IElementType) : this(node, nodeType, MultiMarkdownTypes.NONE)

    override fun toString(): String {
        return nodeType.toString() + "[" + chars.startOffset + "," + chars.endOffset + ") " + chars.toString()
    }

    data class SyntheticNode(val chars: BasedSequence, val type: IElementType, val compositeType: IElementType? = null) {
        fun isComposite(): Boolean {
            return compositeType != null
        }

        val startOffset: Int get() = chars.startOffset
        val endOffset: Int get() = chars.endOffset

        override fun toString(): String {
            return (if (isComposite()) "*" else "") + type.toString() + "[" + startOffset + ", " + endOffset + "]"
        }
    }

    fun addLeaf(chars: BasedSequence, type: IElementType): SyntheticFlexmarkNodes {
        if (chars.isNotNull && chars.isNotEmpty()) nodes.add(SyntheticNode(chars, type, null))
        return this
    }

    fun addComposite(chars: BasedSequence, compositeType: IElementType, type: IElementType): SyntheticFlexmarkNodes {
        if (chars.isNotNull) nodes.add(SyntheticNode(chars, type, compositeType))
        return this
    }

    //    fun addComposite(chars: BasedSequence, compositeType: IElementType): SyntheticFlexmarkNodes {
    //        if (chars.isNotNull) nodes.add(SyntheticNode(chars, MultiMarkdownTypes.NONE, compositeType))
    //        return this;
    //    }

    //    fun addNodes(chars: Array<BasedSequence>, types: Array<IElementType>): SyntheticFlexmarkNodes {
    //        if (chars.size != types.size) {
    //            throw IllegalArgumentException("chars.size ${chars.size} not equal to types.size ${types.size}")
    //        }
    //
    //        for (i in 0..chars.size - 1) {
    //            addLeaf(chars[i], types[i])
    //        }
    //
    //        return this;
    //    }
    //
    //    fun addComposites(chars: Array<BasedSequence>, compositeTypes: Array<IElementType>): SyntheticFlexmarkNodes {
    //        if (chars.size != compositeTypes.size) {
    //            throw IllegalArgumentException("chars.size ${chars.size} not equal to compositeTypes.size ${compositeTypes.size}")
    //        }
    //
    //        for (i in 0..chars.size - 1) {
    //            addComposite(chars[i], compositeTypes[i])
    //        }
    //
    //        return this;
    //    }
}
