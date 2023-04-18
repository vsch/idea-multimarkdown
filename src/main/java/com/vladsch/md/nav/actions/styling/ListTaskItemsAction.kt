// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.ElementListBag
import com.vladsch.md.nav.actions.styling.util.ElementType
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider
import com.vladsch.md.nav.psi.element.MdListItemImpl
import com.vladsch.md.nav.psi.element.MdParagraph
import com.vladsch.md.nav.psi.util.MdPsiImplUtil

class ListTaskItemsAction : ListToggleStateSelectionAction() {
    override fun isSelected(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>): Boolean {
        return elementBag.countMapped(ElementType.TASK_LIST_ITEM, ElementType.TASK_LIST_DONE_ITEM) == elementBag.size && elementBag.size > 0
    }

    override fun wantUnselectedChildItems(): Boolean = false

    override fun wantElement(element: PsiElement): Boolean {
        return element is MdListItemImpl ||
            (element is MdParagraph &&
                (element.parent !is MdListItemImpl || element != MdPsiImplUtil.findChildTextBlock(element.parent)?.parent))
    }

    override fun adjustItemPrefix(element: MdListItemImpl, itemMarker: ASTNode?, taskItemMarker: ASTNode?, prefix: CharSequence, removePrefix: Boolean): CharSequence {
        if (itemMarker != null) {
            val markerText = if (taskItemMarker != null && taskItemMarker !== itemMarker) itemMarker.text + taskItemMarker.text else itemMarker.text
            return when {
                taskItemMarker == null || taskItemMarker === itemMarker -> "$markerText[ ] "
                removePrefix -> itemMarker.text
                else -> markerText
            }
        }
        return prefix
    }

    override fun performAction(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>) {
        val someSelected = elementBag.countMapped(ElementType.TASK_LIST_ITEM, ElementType.TASK_LIST_DONE_ITEM) > 0

        // NOTE: add always adds bullet if item does not have one, remove only removes task list items
        // if none have task item, add
        // if some or all have task item, remove
        togglePrefix(editContext, elementBag, MdBlockPrefixProvider.taskBulletItemPrefix(editContext), removePrefix = someSelected, secondMarkerOnly = someSelected)
    }
}
