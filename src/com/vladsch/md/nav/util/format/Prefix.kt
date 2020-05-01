// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format

import com.vladsch.plugin.util.appendSpaces
import com.vladsch.plugin.util.spaces

/**
 * Prefix for the item
 *
 * For block quote like prefix options compact and compact with space
 *
 * for compact then fixed = ">" optionalSpace = false
 * for compact with space then fixed = ">" optionalSpace = true
 * for spaced with two spaces on the end then fixed = "> " optionalSpace = true
 *
 *
 * @param marker     fixed part of the prefix
 * @param optionalSpaces  if true then combine without extra space if next prefix does not start with a space
 *
 */

data class Prefix(val marker: String, val optionalSpaces: Int = 0) {

    constructor(fixedText: CharSequence, optionalSpaces: Int) : this(fixedText.toString(), optionalSpaces)

    val text: String get() = if (optionalSpaces > 0) _text else marker

    private val _text: String by lazy { StringBuilder().append(marker).appendSpaces(optionalSpaces).toString() }

    fun append(other: Prefix): Prefix {
        return if (this.marker.isEmpty()) other
        else if (optionalSpaces == 0 || !other.marker.startsWith(' ')) Prefix(StringBuilder().append(marker).append(other.marker), other.optionalSpaces)
        else Prefix(StringBuilder().append(marker).appendSpaces(optionalSpaces).append(other.marker), other.optionalSpaces)
    }
}

/**
 * @param indent            prefix to use for child elements or first line of a paragraph child element
 * @param lazyContinuation  prefix to use for lazy continuation of a paragraph or same as prefix if no special lazy continuation
 */
data class Indent(val indent: Prefix, val lazyContinuation: Prefix) {

    constructor(prefix: Prefix) : this(prefix, prefix)
}

// block prefixes in old terminology
/**
 * Item prefix information
 * @param isFirstItemOnSameLine         if this prefix if for an item that is itself a first child on same line as the parent's marker
 * @param anyChild                      indent for children of the element
 * @param firstChild                    indent for the first child of the element
 */
data class ItemIndents(val parent: ItemIndents?, val isFirstItemOnSameLine: Boolean, val anyChild: Indent, val firstChild: Indent) {

    constructor(parent: ItemIndents?, isFirstOnSameLine: Boolean, anyChild: Indent) : this(parent, isFirstOnSameLine, anyChild, anyChild)

    // helpers mapping to old terminology fields
    val itemPrefix: String get() = firstChild.indent.text
    val itemContPrefix: String get() = firstChild.lazyContinuation.text
    val childPrefix: String get() = anyChild.indent.text
    val childContPrefix: String get() = anyChild.lazyContinuation.text

    /**
     * Append child item indent and return combined indent
     *
     * @param other                 child item prefixes to append
     */
    fun append(other: ItemIndents): ItemIndents {
        val firstPrefix = if (other.isFirstItemOnSameLine) firstChild else anyChild
        return ItemIndents(this,
            other.isFirstItemOnSameLine,
            Indent(anyChild.indent.append(other.anyChild.indent), anyChild.lazyContinuation.append(other.anyChild.lazyContinuation)),
            Indent(firstPrefix.indent.append(other.firstChild.indent), firstPrefix.lazyContinuation.append(other.firstChild.lazyContinuation))
        )
    }

    companion object {
        @JvmField
        val EMPTY_PREFIX = Prefix("")
        @JvmField
        val EMPTY_ITEM_INDENT = Indent(EMPTY_PREFIX)
        @JvmField
        val EMPTY = ItemIndents(null, false, EMPTY_ITEM_INDENT)

        @JvmField
        val BLOCK_QUOTE_PREFIX = Prefix(">", 1)
        @JvmField
        val ASIDE_BLOCK_PREFIX = Prefix("|", 1)

        // NOTE: block quotes and aside blocks will absorb the next space if present.
        //       therefore they must always have 1 optional space to prevent a following indent space
        //       from being absorbed by the block quote. So 0 optional spaces is mapped to 1 optional space prefix.
        private val COMMON_PREFIXES = mapOf(
            ">" to mapOf(0 to BLOCK_QUOTE_PREFIX, 1 to BLOCK_QUOTE_PREFIX),
            "|" to mapOf(0 to ASIDE_BLOCK_PREFIX, 1 to ASIDE_BLOCK_PREFIX),
            spaces(0) to mapOf(0 to EMPTY_PREFIX),
            spaces(1) to mapOf(0 to Prefix(spaces(1))),
            spaces(2) to mapOf(0 to Prefix(spaces(2))),
            spaces(3) to mapOf(0 to Prefix(spaces(3))),
            spaces(4) to mapOf(0 to Prefix(spaces(4))),
            spaces(5) to mapOf(0 to Prefix(spaces(5))),
            spaces(6) to mapOf(0 to Prefix(spaces(6))),
            spaces(7) to mapOf(0 to Prefix(spaces(7)))
        )

        @JvmStatic
        fun prefixOf(marker: String, optionalSpaces: Int = 0): Prefix {
            return COMMON_PREFIXES[marker]?.get(optionalSpaces) ?: Prefix(marker, optionalSpaces)
        }
    }
}

