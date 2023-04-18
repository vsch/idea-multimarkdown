// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.plugin.util.ifElse

class MdOrderedListItemImpl(node: ASTNode) : MdListItemImpl(node), MdOrderedListItem {
    companion object {
        @JvmStatic
        fun adjustOrderedItemPrefix(listItemOffset: Int, ordinal: Int, listItems: Int, actualItemPrefix: CharSequence, actualTextPrefix: CharSequence, LIST_RENUMBER_ITEMS: Boolean?, LIST_ALIGN_NUMERIC: Int?, editContext: PsiEditContext): CharSequence {
            return MdBlockPrefixProvider.getAdjustedOrderedItemPrefix(listItemOffset, ordinal, listItems, actualItemPrefix, actualTextPrefix, LIST_RENUMBER_ITEMS, LIST_ALIGN_NUMERIC, editContext)
        }
    }

    override fun getListItemMarker(): ASTNode? {
        return node.findChildByType(MdTypes.ORDERED_LIST_ITEM_MARKER)
    }

    override fun getTaskItemPriority(editContext: PsiEditContext): Int {
        return isTaskItem.ifElse(isEmptyItem.ifElse(MdListItem.LOW_PRIORITY, MdBlockPrefixProvider.getOrderedTaskItemPriority(editContext)), -1)
    }

    private fun itemPrefixOffset(editContext: PsiEditContext): Int {
        return (parent as? MdOrderedList)?.itemOrdinalOffset(false, editContext) ?: 0
    }

    override fun itemPrefix(editContext: PsiEditContext): CharSequence {
        val ordinal = MdPsiImplUtil.childElementOrdinal(this)
        val offset = itemPrefixOffset(editContext) + 1
        return itemPrefix(offset, ordinal, parent.children.size, editContext)
    }

    override fun itemPrefixForPrefixes(editContext: PsiEditContext): CharSequence {
        val actualTextPrefix = actualTextPrefix(this, editContext, true)
        val itemPrefix = itemPrefix(editContext)
        val prefix = actualTextPrefix.replaceFirst("\\s*\\d+[.)]\\s*".toRegex(), itemPrefix.toString())
        return MdBlockPrefixProvider.getAdjustedTaskItemPrefix(prefix, editContext)
    }

    fun itemPrefix(listItemOffset: Int, ordinal: Int, listItems: Int, editContext: PsiEditContext): CharSequence {
        return adjustOrderedItemPrefix(listItemOffset, ordinal, listItems, actualItemPrefix(editContext), actualTextPrefix(editContext, true), null, null, editContext)
    }

    override fun itemTextPrefix(editContext: PsiEditContext): CharSequence {
        val actualTextPrefix = actualTextPrefix(editContext, true)
        val actualItemPrefix = actualItemPrefix(editContext)
        val itemPrefix = itemPrefix(editContext)
        val rightPad = actualTextPrefix.length - actualItemPrefix.length

        return if (rightPad > 0) itemPrefix.toString() + " ".repeat(rightPad) else itemPrefix
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return super.getStructureViewPresentation()
    }

    override fun getLocationString(): String? {
        return super.getLocationString()
    }

    override fun getPresentableText(): String? {
        return super.getPresentableText()
    }
}
