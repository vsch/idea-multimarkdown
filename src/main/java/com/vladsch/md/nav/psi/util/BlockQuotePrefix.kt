// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util

import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider

open class BlockQuotePrefix(
    isItem: Boolean,             // if block quote is item of parent
    childPrefix: CharSequence,        // prefix for first line of child block
    childContPrefix: CharSequence,    // prefix for child continuation lines
    noChildItems: Boolean         // when set, child isItem is ignored
) : BlockPrefix(isItem, childPrefix, childContPrefix, childPrefix, childContPrefix, noChildItems) {

    protected constructor(isItem: Boolean, childPrefix: CharSequence, childContPrefix: CharSequence) : this(isItem, childPrefix, childContPrefix, false)

    override fun combineWith(other: BlockPrefix): BlockPrefix? {
        // return combined prefix or null if cannot combine
        if (other is BlockQuotePrefix) {
            return append(other)
        }

        return null
    }

    fun append(other: BlockQuotePrefix): BlockQuotePrefix {
        return if (other.isItem) {
            BlockQuotePrefix(
                true,
                "$itemPrefix${other.childPrefix}",
                "$itemContPrefix${other.childContPrefix}",
                this.noChildItems || other.noChildItems
            )
        } else {
            BlockQuotePrefix(
                false,
                "$childPrefix${other.childPrefix}",
                "$childContPrefix${other.childContPrefix}",
                this.noChildItems || other.noChildItems
            )
        }
    }

    override fun finalizePrefix(editContext: PsiEditContext): BlockPrefix {
        return BlockQuotePrefix(false, childPrefix, this.childContPrefix)
    }

    companion object {
        @JvmStatic
        fun create(isItem: Boolean, childPrefix: CharSequence, childContPrefix: CharSequence, noChildItems: Boolean): BlockQuotePrefix {
            return MdBlockPrefixProvider.createBlockQuotePrefix(isItem, childPrefix, childContPrefix, noChildItems)
        }

        @JvmStatic
        fun create(isItem: Boolean, childPrefix: String, childContPrefix: String): BlockQuotePrefix = create(isItem, childPrefix, childContPrefix, false)
    }
}
