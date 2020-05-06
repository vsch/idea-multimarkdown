// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings

class MdUnorderedListItemImpl(node: ASTNode) : MdListItemImpl(node), MdUnorderedListItem {
    override fun getListItemMarker(): ASTNode? {
        return node.findChildByType(MdTypes.BULLET_LIST_ITEM_MARKER)
    }

    override fun itemPrefix(editContext: PsiEditContext): CharSequence {
        val prefix = actualItemPrefix(editContext)
        return MdBlockPrefixProvider.getAdjustedTaskItemPrefix(prefix, editContext)
    }

    override fun getTaskItemPriority(editContext: PsiEditContext): Int {
        return if (isTaskItem) {
            if (isEmptyItem) {
                MdListItem.LOW_PRIORITY
            } else {
                when (listItemMarker?.text?.trim()) {
                    "+" -> MdListItem.HIGH_PRIORITY
                    "*" -> MdListItem.NORMAL_PRIORITY
                    "-" -> MdListItem.LOW_PRIORITY
                    else -> -1
                }
            }
        } else {
            -1
        }
    }

    override fun itemTextPrefix(editContext: PsiEditContext): CharSequence {
        val prefix = actualTextPrefix(editContext, true)
        val taskPrefixed = MdBlockPrefixProvider.getAdjustedTaskItemPrefix(prefix, editContext)
        return MdBlockPrefixProvider.getAdjustedBulletItemPrefix(taskPrefixed, isTaskItem, editContext)
    }

    override fun itemPrefixForPrefixes(editContext: PsiEditContext): CharSequence {
        return itemTextPrefix(editContext)
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

    override fun getBreadcrumbInfo(): String {
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText && node.text.isNotEmpty()) {
            val truncateStringForDisplay = MdPsiImplUtil.truncateStringForDisplay(node.text, settings.maxBreadcrumbText, false, true, true)
            if (truncateStringForDisplay.isNotEmpty()) return truncateStringForDisplay
        }
        return MdPsiBundle.message("list-item")
    }

    override fun getBreadcrumbTooltip(): String? {
        val marker = firstChild.text
        val taskItemMarker = firstChild.nextSibling
        val taskText =
            if (taskItemMarker is LeafPsiElement && taskItemMarker.node.elementType in arrayOf(MdTypes.TASK_ITEM_MARKER, MdTypes.TASK_DONE_ITEM_MARKER)) {
                taskItemMarker.text
            } else {
                ""
            }
        val textElement = MdPsiImplUtil.findChildTextBlock(this) ?: return marker + taskText
        return marker + taskText + MdPsiImplUtil.getNodeText(textElement, true, false)
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return getItemBlock(this)
    }
}
