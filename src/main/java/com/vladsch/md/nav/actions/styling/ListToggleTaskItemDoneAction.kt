// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.psi.PsiElement
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.ElementListBag
import com.vladsch.md.nav.actions.styling.util.ElementType
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider
import com.vladsch.md.nav.psi.element.MdListItem

class ListToggleTaskItemDoneAction : ListToggleStateSelectionAction() {
    override fun isSelected(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>): Boolean {
        return elementBag.countMapped(ElementType.TASK_LIST_DONE_ITEM) == elementBag.size && elementBag.size > 0
    }

    override fun wantUnselectedChildItems(): Boolean = false

    override fun wantElement(element: PsiElement): Boolean {
        return element is MdListItem && element.isTaskItem
    }

    override fun performAction(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>) {
        val allDone = elementBag.countMapped(ElementType.TASK_LIST_DONE_ITEM) == elementBag.size
        val prefix = if (allDone) "[ ] " else MdBlockPrefixProvider.taskItemPrefix(editContext)
        togglePrefix(editContext, elementBag, prefix, removePrefix = false, secondMarkerOnly = true)
    }
}
