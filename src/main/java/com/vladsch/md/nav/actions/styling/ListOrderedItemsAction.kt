// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.psi.PsiElement
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.ElementListBag
import com.vladsch.md.nav.actions.styling.util.ElementType
import com.vladsch.md.nav.psi.element.MdListItemImpl
import com.vladsch.md.nav.psi.element.MdParagraph
import com.vladsch.md.nav.psi.util.MdPsiImplUtil

class ListOrderedItemsAction : ListToggleStateSelectionAction() {
    override fun isSelected(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>): Boolean {
        return elementBag.countMapped(ElementType.ORDERED_LIST_ITEM) == elementBag.size && elementBag.size > 0
    }

    override fun wantUnselectedChildItems(): Boolean = false

    override fun wantElement(element: PsiElement): Boolean {
        return element is MdListItemImpl ||
            (element is MdParagraph &&
                (element.parent !is MdListItemImpl || element != MdPsiImplUtil.findChildTextBlock(element.parent)?.parent))
    }

    override fun performAction(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>) {
        togglePrefix(editContext, elementBag, "1. ", isSelected(editContext, elementBag), secondMarkerOnly = false)
    }
}
