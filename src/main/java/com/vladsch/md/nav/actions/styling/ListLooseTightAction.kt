// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.psi.PsiElement
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.handlers.util.ListItemContext
import com.vladsch.md.nav.actions.styling.util.ElementListBag
import com.vladsch.md.nav.actions.styling.util.ElementType
import com.vladsch.md.nav.psi.element.MdListItemImpl
import java.util.*

abstract class ListLooseTightAction : ListToggleStateSelectionAction() {
    data class LooseTightStats(val canLoosenList: Boolean, val canTightenList: Boolean, val allSingleItems: Boolean)

    fun getLooseTightStats(caretContextInfo: CaretContextInfo, elementBag: ElementListBag<ElementType>): LooseTightStats {
        var canLoosenList = false
        var canTightenList = false
        var allSingleItems = true
        if (elementBag.size > 0) {
            val listData = listItemData(caretContextInfo, elementBag)
            for (list in listData.listOrder) {
                if (ListItemContext.listItemCount(list) > 1) {
                    if (ListItemContext.canLoosenList(list, listData.listItemsMap[list])) canLoosenList = true
                    if (ListItemContext.canTightenList(list, listData.listItemsMap[list])) canTightenList = true
                    allSingleItems = false
                }
                if (canLoosenList && canTightenList) break
            }
        }
        return LooseTightStats(canLoosenList, canTightenList, allSingleItems)
    }

    override fun wantUnselectedChildItems(): Boolean = true

    override fun wantElement(element: PsiElement): Boolean {
        return element is MdListItemImpl
    }

    data class ListsWithItems(val listOrder: List<PsiElement>, val listItemsMap: Map<PsiElement, Set<PsiElement>>)

    fun listItemData(caretContextInfo: CaretContextInfo, elementBag: ElementListBag<ElementType>): ListsWithItems {
        if (!caretContextInfo.editor.caretModel.primaryCaret.hasSelection()) {
            // do the whole list
            val listElement = elementBag[0].parent
            return ListsWithItems(listOf(listElement), mapOf())
        } else {
            // delete blank lines before and after the items in the selection
            val listElements = HashMap<PsiElement, Int>()
            var listOrdinal = 0

            for (item in elementBag) {
                if (item.parent !in listElements) {
                    listElements[item.parent] = listOrdinal++
                }
            }

            // now step through each list, in reverse order and create a set of items
            val listOrder = listElements.keys.sortedBy { listElements[it] }
            val listItemsMap = HashMap<PsiElement, Set<PsiElement>>()

            for (list in listOrder.reversed()) {
                val listItems = HashSet<PsiElement>()
                for (item in elementBag) {
                    if (item.parent === list) {
                        listItems.add(item)
                    }
                }

                listItemsMap[list] = listItems
            }

            return ListsWithItems(listOrder, listItemsMap)
        }
    }
}
