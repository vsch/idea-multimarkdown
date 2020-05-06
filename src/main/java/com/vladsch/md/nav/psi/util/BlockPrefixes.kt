// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util

import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.util.format.Prefixes
import com.vladsch.plugin.util.minLimit

class BlockPrefixes private constructor(
    val parent: BlockPrefixes?,
    val prefix: BlockPrefix,
    val accumulatedPrefix: BlockPrefix
) {

    fun withNoChildItems(): BlockPrefixes {
        return parent?.append(prefix.withNoChildItems()) ?: this
    }

    val itemPrefixes: Prefixes get() = Prefixes(accumulatedPrefix.itemPrefix, accumulatedPrefix.itemContPrefix)
    val childPrefixes: Prefixes get() = Prefixes(accumulatedPrefix.childPrefix, accumulatedPrefix.childContPrefix)

    val itemPrefix: BasedSequence get() = accumulatedPrefix.itemPrefix
    val itemContPrefix: BasedSequence get() = accumulatedPrefix.itemContPrefix
    val childPrefix: BasedSequence get() = accumulatedPrefix.childPrefix
    val childContPrefix: BasedSequence get() = accumulatedPrefix.childContPrefix

    fun append(prefix: BlockPrefix): BlockPrefixes {
        return BlockPrefixes(this, prefix, accumulatedPrefix.append(prefix))
    }

    fun last(): BlockPrefix {
        return prefix
    }

    fun removeLastPrefix(): BlockPrefixes {
        return parent ?: this
    }

    fun isEmpty():Boolean = this == EMPTY
    fun isNotEmpty():Boolean = this != EMPTY

    fun append(isItem: Boolean, childPrefix: CharSequence, childContPrefix: CharSequence): BlockPrefixes {
        assert(!childPrefix.contains('>'))
        assert(!childPrefix.contains('|'))
        return append(BlockPrefix(isItem && !prefix.noChildItems, childPrefix, childContPrefix))
    }

    fun append(isItem: Boolean, itemPrefix: CharSequence, itemContPrefix: CharSequence, childPrefix: CharSequence, childContPrefix: CharSequence): BlockPrefixes {
        assert(!childPrefix.contains('>'))
        assert(!childPrefix.contains('|'))

        return append(BlockPrefix(isItem && !prefix.noChildItems, itemPrefix, itemContPrefix, childPrefix, childContPrefix))
    }

    fun getBlockQuotePrefixAt(blockQuoteIndex: Int): BlockPrefixes {
        val prefixes = ArrayList<BlockPrefix>()

        appendPrefixes(prefixes)

        var pos = 0
        for (prefix in prefixes) {
            if (prefix is BlockQuotePrefix) {
                if (pos == blockQuoteIndex) {
                    return EMPTY.append(prefix)
                }
                pos++
            }
        }
        return EMPTY
    }

    fun removeBlockQuotePrefixAt(blockQuoteIndex: Int): BlockPrefixes {
        var adjustedPrefixes = EMPTY
        val prefixes = ArrayList<BlockPrefix>()

        appendPrefixes(prefixes)

        var pos = 0
        for (prefix in prefixes) {
            if (prefix is BlockQuotePrefix) {
                if (pos != blockQuoteIndex) {
                    adjustedPrefixes = adjustedPrefixes.append(prefix)
                }
                pos++
            } else {
                adjustedPrefixes = adjustedPrefixes.append(prefix)
            }
        }
        return adjustedPrefixes
    }

    /**
     * Add block prefix at given block quote index position
     * if no such position then will be added as the last prefix
     *
     */
    fun addBlockPrefixAt(blockPrefixIndex: Int, blockPrefix: BlockPrefix): BlockPrefixes {
        var adjustedPrefixes = EMPTY
        val prefixes = ArrayList<BlockPrefix>()

        appendPrefixes(prefixes)

        var pos = 0
        var added = false
        for (prefix in prefixes) {
            if (pos == blockPrefixIndex) {
                adjustedPrefixes = adjustedPrefixes.append(blockPrefix)
                added = true
            } else {
                adjustedPrefixes = adjustedPrefixes.append(prefix)
            }

            pos++
        }

        if (!added) {
            adjustedPrefixes = adjustedPrefixes.append(blockPrefix)
        }

        return adjustedPrefixes
    }

    /**
     * Add block quote at given block quote index position
     * if no such position then will be added as the last prefix
     *
     */
    fun addBlockQuotePrefixAt(blockQuoteIndex: Int, blockQuotePrefix: BlockQuotePrefix): BlockPrefixes {
        var adjustedPrefixes = EMPTY
        val prefixes = ArrayList<BlockPrefix>()

        appendPrefixes(prefixes)

        var pos = 0
        var added = false
        for (prefix in prefixes) {
            if (prefix is BlockQuotePrefix) {
                if (pos == blockQuoteIndex) {
                    adjustedPrefixes = adjustedPrefixes.append(blockQuotePrefix)
                    added = true
                }
                pos++
            }
            adjustedPrefixes = adjustedPrefixes.append(prefix)
        }

        if (!added) {
            adjustedPrefixes = adjustedPrefixes.append(blockQuotePrefix)
        }
        return adjustedPrefixes
    }

    fun finalizePrefixes(editContext: PsiEditContext): BlockPrefixes {
        val itemPrefix = editContext.emptyBuilder
        val itemContPrefix = editContext.emptyBuilder
        val childPrefix = editContext.emptyBuilder
        val childContPrefix = editContext.emptyBuilder

        var prevPrefix: BlockPrefix? = null
        val prefixes = ArrayList<BlockPrefix>()

        appendPrefixes(prefixes)
        var nonItem = 0
        var i = prefixes.size
        while (i > 0) {
            i--
            val prefix = prefixes[i]
            if (!prefix.isItem || i > 0 && prefixes[i - 1].noChildItems) {
                nonItem = i
                break
            }
        }

        for ((j, prefix) in prefixes.withIndex()) {
            prevPrefix = if (prevPrefix == null) {
                prefix
            } else {
                val combinedPrefix = prevPrefix.combineWith(prefix)
                if (combinedPrefix != null) {
                    combinedPrefix
                } else {
                    // cannot combine
                    val finalized = prevPrefix.finalizePrefix(editContext)
                    if (j >= nonItem && prefix.isItem && !prevPrefix.noChildItems) {
                        itemPrefix.append(finalized.itemPrefix)
                        itemContPrefix.append(finalized.itemContPrefix)
                        childPrefix.append(finalized.childPrefix)
                        childContPrefix.append(finalized.childContPrefix)
                    } else {
                        itemPrefix.append(finalized.childPrefix)
                        itemContPrefix.append(finalized.childContPrefix)
                        childPrefix.append(finalized.childPrefix)
                        childContPrefix.append(finalized.childContPrefix)
                    }
                    prefix
                }
            }
        }

        if (prevPrefix != null) {
            val finalized = prevPrefix.finalizePrefix(editContext)
            itemPrefix.append(finalized.itemPrefix)
            itemContPrefix.append(finalized.itemContPrefix)
            childPrefix.append(finalized.childPrefix)
            childContPrefix.append(finalized.childContPrefix)
        }

        val itemPrefixText = itemPrefix.toSequence()
        val itemContPrefixText = itemContPrefix.toSequence()
        val childPrefixText = childPrefix.toSequence()
        val childContPrefixText = childContPrefix.toSequence()

        return BlockPrefixes(parent, prefix, BlockPrefix(false, itemPrefixText, itemContPrefixText, childPrefixText, childContPrefixText))
    }

    private fun appendPrefixes(list: ArrayList<BlockPrefix>) {
        parent?.appendPrefixes(list)
        list.add(prefix)
    }

    fun hasBlockQuotePrefix(): Boolean {
        return prefix is BlockQuotePrefix || parent?.hasBlockQuotePrefix() ?: false
    }

    private constructor() : this(null, BlockPrefix.EMPTY, BlockPrefix.EMPTY)

    companion object {
        @JvmField
        var EMPTY = BlockPrefixes()

        @JvmStatic
        fun removableTrailingSpaces(prefix: CharSequence): Int {
            var spaces = 0
            for (c in prefix.reversed()) {
                if (c == '>') return (spaces - 1).minLimit(0)
                if (c == '|') return (spaces - 1).minLimit(0)
                if (c != ' ') break
                spaces++
            }
            return 0
        }
    }
}
