// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.vladsch.md.nav.language.api.MdStripTrailingSpacesDocumentFilter
import com.vladsch.md.nav.language.api.MdStripTrailingSpacesExtension
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.psi.util.MdVisitHandler
import com.vladsch.plugin.util.psi.isIn
import com.vladsch.plugin.util.psi.isTypeIn

// NOTE: this should be the first extension used, the rest can override its handlers
//   so it is not registered but hard-coded in com.vladsch.md.nav.language.MdStripTrailingSpacesSmartFilter
class MdStripTrailingSpacesCoreExtension : MdStripTrailingSpacesExtension {

    override fun setStripTrailingSpacesFilters(filter: MdStripTrailingSpacesDocumentFilter) {
        val keepTrailingLineBreak = filter.codeStyleSettings.isKeepLineBreakSpaces
        val keepCodeTrailingSpaces = filter.codeStyleSettings.keepVerbatimTrailingSpaces

        val handler = CoreStripSpacedHandler(filter, keepTrailingLineBreak, keepCodeTrailingSpaces)
        filter.addHandlers(
            MdVisitHandler(MdOrderedListItem::class.java, handler::visit),
            MdVisitHandler(MdUnorderedListItem::class.java, handler::visit),
            MdVisitHandler(MdTextBlock::class.java, handler::visit),
            MdVisitHandler(MdVerbatim::class.java, handler::visit),
            MdVisitHandler(MdBlankLine::class.java, handler::visit)
        )
    }

    private class CoreStripSpacedHandler internal constructor(
        val filter: MdStripTrailingSpacesDocumentFilter,
        val keepTrailingLineBreak: Boolean,
        val keepCodeTrailingSpaces: Boolean
    ) {

        fun visit(it: MdVerbatim) {
            if (keepCodeTrailingSpaces) {
                filter.disableOffsetRange(it.getContentRange(true), false)
                filter.visitChildren(it);
            }
        }

        fun visit(it: MdBlankLine) {
            if (keepCodeTrailingSpaces) {
                filter.disableOffsetRange(it.textRange, true)
            }
        }

        fun visit(it: MdTextBlock) {
            if (keepTrailingLineBreak) {
                filter.disableOffsetRange(it.node.textRange, true)
            }
        }

        fun visit(it: MdListItem) {
            if (keepTrailingLineBreak) {
                val firstChild = it.firstChild
                var nextChild = firstChild?.nextSibling ?: firstChild

                if (nextChild is LeafPsiElement && nextChild.isTypeIn(MdTokenSets.TASK_LIST_ITEM_MARKERS)) {
                    nextChild = nextChild.nextSibling
                }

                if (nextChild.isIn(MdTypes.EOL) || nextChild is LeafPsiElement && nextChild.isTypeIn(MdTokenSets.LIST_ITEM_MARKER_SET)) {
                    filter.disableOffsetRange(it.node.textRange, true)
                }
            }
            filter.visitChildren(it);
        }
    }
}
