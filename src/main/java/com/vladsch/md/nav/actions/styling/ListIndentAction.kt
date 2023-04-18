// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.handlers.util.ListItemContext
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder

class ListIndentAction : ListItemAction() {
    override val inMenuAction: Boolean
        get() = false

    override fun canPerformAction(caretContextInfo: CaretContextInfo, conditionBuilder: DisabledConditionBuilder?): Boolean {
        val context = ListItemContext.getContext(caretContextInfo, null)
        conditionBuilder?.and(context != null, "Not list item element at caret") {
            it.and(context!!.canIndentItem(), "Cannot indent first list item")
        }
        return conditionBuilder?.isEnabled ?: (context != null && context.canIndentItem())
    }

    override fun performAction(caretContextInfo: CaretContextInfo) {
        val context = ListItemContext.getContext(caretContextInfo, null) ?: return
        if (context.canIndentItem()) {
            context.indentItem(true, false)
        }
    }
}
