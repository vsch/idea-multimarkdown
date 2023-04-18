// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util

import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.plugin.util.toBased

open class BlockPrefix(
    // true if this prefix was added by first item of parent, then its itemPrefix should be appended to parent's item prefix for accumulated item prefix
    val isItem: Boolean,        // true if this prefix was added by first item of parent, then its itemPrefix should be appended to parent's item prefix for accumulated item prefix
    itemPrefix: CharSequence,         // prefix for indenting blocks that have an item: list item, footnote, definition
    itemContPrefix: CharSequence,     // prefix for continuation lines of item paragraph
    childPrefix: CharSequence,        // prefix for first line of child block
    childContPrefix: CharSequence,     // prefix for child continuation lines
    // when set, child prefix isItem is ignored
    val noChildItems: Boolean
) {

    val itemPrefix: BasedSequence = itemPrefix.toBased()                    // prefix for indenting blocks that have an item: list item, footnote, definition
    val itemContPrefix: BasedSequence = itemContPrefix.toBased()            // prefix for continuation lines of item paragraph
    val childPrefix: BasedSequence = childPrefix.toBased()                  // prefix for first line of child block
    val childContPrefix: BasedSequence = childContPrefix.toBased()          // prefix for child continuation lines

    constructor(isItem: Boolean, itemPrefix: CharSequence, itemContPrefix: CharSequence, childPrefix: CharSequence, childContPrefix: CharSequence) : this(isItem, itemPrefix, itemContPrefix, childPrefix, childContPrefix, false)
    constructor(isItem: Boolean, childPrefix: CharSequence, childContPrefix: CharSequence) : this(isItem, childPrefix, childContPrefix, childPrefix, childContPrefix)

    open fun combineWith(other: BlockPrefix): BlockPrefix? {
        // return combined prefix or null if cannot combine
        return null
    }

    open fun finalizePrefix(editContext: PsiEditContext): BlockPrefix {
        return this
    }

    fun withNoChildItems(): BlockPrefix {
        return if (!noChildItems) {
            if (this is BlockQuotePrefix) {
                BlockQuotePrefix.create(false, childPrefix, childContPrefix, true)
            } else {
                BlockPrefix(false, itemPrefix, itemContPrefix, childPrefix, childContPrefix, true)
            }
        } else {
            this
        }
    }

    fun append(other: BlockPrefix): BlockPrefix {
        if (other.isItem && (other is BlockQuotePrefix || !noChildItems)) {
            return BlockPrefix(
                true,
                "$itemPrefix${other.itemPrefix}",
                "$itemContPrefix${other.itemContPrefix}",
                "$childPrefix${other.childPrefix}",
                "$childContPrefix${other.childContPrefix}"
            )
        } else {
            return BlockPrefix(
                false,
                "$childPrefix${other.itemPrefix}",
                "$childContPrefix${other.itemContPrefix}",
                "$childPrefix${other.childPrefix}",
                "$childContPrefix${other.childContPrefix}"
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockPrefix) return false

        if (isItem != other.isItem) return false
        if (itemPrefix != other.itemPrefix) return false
        if (itemContPrefix != other.itemContPrefix) return false
        if (childPrefix != other.childPrefix) return false
        if (childContPrefix != other.childContPrefix) return false
        if (noChildItems != other.noChildItems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isItem.hashCode()
        result = 31 * result + itemPrefix.hashCode()
        result = 31 * result + itemContPrefix.hashCode()
        result = 31 * result + childPrefix.hashCode()
        result = 31 * result + childContPrefix.hashCode()
        result = 31 * result + noChildItems.hashCode()
        return result
    }

    companion object {
        val EMPTY: BlockPrefix = BlockPrefix(false, "", "", "", "")
    }
}
