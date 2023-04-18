// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util

import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.misc.CharPredicate.SPACE
import com.vladsch.flexmark.util.misc.CharPredicate.SPACE_TAB
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.settings.ListIndentationType
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.plugin.util.*
import javax.swing.Icon

open class MdBlockPrefixProviderImpl : MdBlockPrefixProvider {
    override fun continuationIndent(firstLineTextIndent: Int, parentTextIndent: Int, editContext: PsiEditContext): Int {
        return getContinuationIndent(firstLineTextIndent, parentTextIndent, editContext)
    }

    override fun adjustTaskItemPrefix(prefix: CharSequence, editContext: PsiEditContext): CharSequence {
        return prefix
    }

    override fun adjustBulletItemPrefix(prefix: CharSequence, isTaskItem: Boolean, editContext: PsiEditContext): CharSequence {
        return prefix
    }

    override fun orderedTaskItemPriority(editContext: PsiEditContext): Int {
        return MdListItem.NORMAL_PRIORITY
    }

    @Suppress("NAME_SHADOWING")
    override fun adjustOrderedItemPrefix(listItemOffset: Int, ordinal: Int, listItems: Int, actualItemPrefix: CharSequence, actualTextPrefix: CharSequence, listRenumberItems: Boolean?, listAlignNumeric: Int?, editContext: PsiEditContext): CharSequence {
        var itemPrefix = actualItemPrefix.toString()

        val prefixDiff = (actualTextPrefix.toBased().countTrailing(SPACE) - 1).minLimit(0)
        if (prefixDiff > 0) itemPrefix += " ".repeat(prefixDiff)
        return itemPrefix
    }

    override fun blockPrefixes(element: MdIndentingComposite, parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes {
        val result = getProviderPrefixes(element, parentPrefixes, editContext)
        return result ?: BlockPrefixes.EMPTY
    }

    override fun getBlockQuotePrefix(isItem: Boolean, childPrefix: CharSequence, childContPrefix: CharSequence, noChildItems: Boolean): BlockQuotePrefix {
        return BlockQuotePrefix(isItem, childPrefix, childContPrefix, noChildItems)
    }

    override fun getTaskItemPrefix(editContext: PsiEditContext): CharSequence {
        return "[x] "
    }

    override fun getTaskBulletItemPrefix(editContext: PsiEditContext): CharSequence {
        return "* [ ] "
    }

    override fun getBulletItemPrefix(editContext: PsiEditContext): CharSequence {
        return "* "
    }

    override fun getIcon(element: PsiElement): Icon? {
        return null
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getContinuationIndent(firstLineTextIndent: Int, parentTextIndent: Int, editContext: PsiEditContext): Int {
            return firstLineTextIndent
        }

        interface BlockPrefixProvider<T : Any> {
            fun blockPrefixes(element: T, parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes

            fun blockPrefixesAny(element: Any, parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes {
                @Suppress("UNCHECKED_CAST")
                return blockPrefixes(element as T, parentPrefixes, editContext)
            }
        }

        object ProviderAdmonition : BlockPrefixProvider<MdAdmonition> {
            override fun blockPrefixes(element: MdAdmonition, parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes {
                val prefixes = parentPrefixes ?: MdPsiImplUtil.getBlockPrefixes(element.parent, parentPrefixes, editContext)
                val result = MdAdmonitionImpl.INDENT_PREFIX
                return prefixes.append(MdPsiImplUtil.isFirstIndentedBlockPrefix(element, false), result, result)
            }
        }

        object ProviderListItemImpl : BlockPrefixProvider<MdListItemImpl> {
            override fun blockPrefixes(element: MdListItemImpl, parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes {
                //System.out.println("ListItem " + text + " itemPrefixes, parent: " + parent)
                val prefixes = parentPrefixes ?: MdPsiImplUtil.getBlockPrefixes(element.parent, parentPrefixes, editContext)

                val itemPrefixForPrefixes = element.itemPrefixForPrefixes(editContext).toBased()
                val actualPrefix = itemPrefixForPrefixes
                val afterPrefixSpaces = actualPrefix.countTrailing(SPACE_TAB)
                val itemSuffixSize = element.isTaskItemPrefix(actualPrefix.trimEnd()).ifElse(3 + afterPrefixSpaces, 0)
                val itemPrefix = actualPrefix.subSequence(0, actualPrefix.length - itemSuffixSize)
                val childPrefix: String
                val childContPrefix: String

                val settings = MdRenderingProfileManager.getInstance(element.project).getRenderingProfile(element.containingFile.originalFile)
                val n: Int

                n = when (settings.parserSettings.parserListIndentationType) {
                    ListIndentationType.COMMONMARK -> {
                        // FIX: tabs need to be expanded to spaces here for proper count
                        itemPrefix.trimEnd().length + itemPrefix.countTrailing(SPACE_TAB).maxLimit(3)
                    }
                    ListIndentationType.FIXED -> 4
                    ListIndentationType.GITHUB -> itemPrefix.length
                }

                val prefixLen = n.minLimit(0)
                val childPrefixLen = prefixLen
                childPrefix = " ".repeat(childPrefixLen)
                childContPrefix = " ".repeat(MdIndentingCompositeImpl.continuationIndent(childPrefixLen, childPrefixLen, editContext))
                val itemContPrefix: String = " ".repeat(MdIndentingCompositeImpl.continuationIndent(actualPrefix.length, prefixLen, editContext))

                val firstIndentedChildBlock = MdPsiImplUtil.isFirstIndentedBlockPrefix(element, false)
                return prefixes.append(firstIndentedChildBlock, actualPrefix, itemContPrefix, childPrefix, childContPrefix)
            }
        }

        object ProviderDefinition : BlockPrefixProvider<MdDefinition> {
            override fun blockPrefixes(element: MdDefinition, parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes {
                val prefixes = parentPrefixes ?: MdPsiImplUtil.getBlockPrefixes(element.parent, parentPrefixes, editContext)

                val itemPrefix = element.actualTextPrefix(editContext, true).toString()
                val styleSettings = editContext.styleSettings
                val itemPrefixForPrefixes = itemPrefix

                val childPrefix: String = MdIndentingCompositeImpl.indentSpaces(4)
                val childContPrefix: String = MdIndentingCompositeImpl.indentSpaces(MdIndentingCompositeImpl.continuationIndent(childPrefix.length, childPrefix.length, editContext))
                val trailing = itemPrefixForPrefixes.toBased().countTrailing(SPACE)
                val n = itemPrefixForPrefixes.length

                val prefix = if (trailing > styleSettings.DEFINITION_MARKER_SPACES) itemPrefixForPrefixes.substring(0, n - trailing + styleSettings.DEFINITION_MARKER_SPACES)
                else itemPrefixForPrefixes.padEnd(n - trailing + styleSettings.DEFINITION_MARKER_SPACES, ' ')

                val n1 = prefix.length
                val itemContPrefix: String = MdIndentingCompositeImpl.indentSpaces(MdIndentingCompositeImpl.continuationIndent(n1, n1, editContext))

                return prefixes.append(MdPsiImplUtil.isFirstIndentedBlockPrefix(element, false), prefix, itemContPrefix, childPrefix, childContPrefix)
            }
        }

        object ProviderBlockQuote : BlockPrefixProvider<MdBlockQuote> {
            override fun blockPrefixes(element: MdBlockQuote, parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes {
                val prefixes = parentPrefixes ?: MdPsiImplUtil.getBlockPrefixes(element.parent, parentPrefixes, editContext)

                val trailingSpaces = element.actualTextPrefix(editContext, true).toBased().trimmedEnd().toString()
                val itemPrefix = element.actualItemPrefix(editContext).toString().trimEnd()
                val prefix = "$itemPrefix$trailingSpaces"

                // here we grab first > and any spaces that follow as our prefix marker
                val result = prefix.replace("[^ \t\\>]".toRegex(), "") // when inserting between >, can cause the start of node to grab inserted char
                val firstMarkerPos = result.indexOf('>').indexOrNull()?.plus(1) ?: result.length
                val lastAfterSpacePos = result.toBased().indexOfAnyNot(CharPredicate.SPACE_TAB, firstMarkerPos).indexOrNull() ?: result.length
                val useResult = result.substring(0, lastAfterSpacePos)
                assert(useResult.toBased().countOfAny(CharPredicate.anyOf('>')) <= 1)
                return prefixes.append(BlockQuotePrefix.create(MdPsiImplUtil.isFirstIndentedBlock(element, false), useResult, useResult))
            }
        }

        private val providerMap = HashMap<Class<*>, BlockPrefixProvider<*>>()

        init {
            providerMap[MdAdmonitionImpl::class.java] = ProviderAdmonition
            providerMap[MdOrderedListItemImpl::class.java] = ProviderListItemImpl
            providerMap[MdUnorderedListItemImpl::class.java] = ProviderListItemImpl
            providerMap[MdDefinitionImpl::class.java] = ProviderDefinition
            providerMap[MdBlockQuoteImpl::class.java] = ProviderBlockQuote
        }

        fun getProviderPrefixes(element: Any, parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes? {
            val blockPrefixProvider = providerMap[element.javaClass] ?: return null
            return blockPrefixProvider.blockPrefixesAny(element, parentPrefixes, editContext)
        }
    }
}

