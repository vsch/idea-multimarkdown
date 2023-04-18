// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.handlers.util.ListItemContext
import com.vladsch.md.nav.actions.styling.util.ElementListBag
import com.vladsch.md.nav.actions.styling.util.ElementType

class ListTightAction : ListLooseTightAction() {
    override fun isSelected(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>): Boolean {
        val stats = getLooseTightStats(editContext, elementBag)
        return !stats.canTightenList
    }

    override fun isEnabled(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>): Boolean {
        val stats = getLooseTightStats(editContext, elementBag)
        return stats.canTightenList
    }

    override fun performAction(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>) {
        val listData = listItemData(editContext, elementBag)
        for (list in listData.listOrder) {
            ListItemContext.makeTightList(editContext, list, listData.listItemsMap[list])
        }
    }
}
