// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ColoredItemPresentation
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.vladsch.flexmark.util.sequence.LineAppendable
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.highlighter.MdHighlighterColors
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.ListIndentationType
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.format.LinePrefixMatcher
import com.vladsch.md.nav.util.format.SpacePrefixMatcher
import com.vladsch.plugin.util.ifElse
import com.vladsch.plugin.util.nullIf
import com.vladsch.plugin.util.nullIfEmpty
import com.vladsch.plugin.util.psi.isIn
import com.vladsch.plugin.util.psi.isTypeIn
import com.vladsch.plugin.util.toBased
import icons.MdIcons
import javax.swing.Icon

abstract class MdListItemImpl(node: ASTNode) : MdIndentingCompositeImpl(node), MdListItem {
    open fun itemPrefix(editContext: PsiEditContext): CharSequence {
        val prefix = actualItemPrefix(editContext).toBased()
        return MdBlockPrefixProvider.getAdjustedTaskItemPrefix(prefix, editContext)
    }

    final override fun directListParentLevel(): Int {
        var level = 0
        var parent = parent
        while (parent is MdList) {
            level++
            parent = parent.parent
            if (parent is MdListItem) parent = parent.parent
        }
        return level
    }

    fun parentItemPrefixForPrefixes(editContext: PsiEditContext): CharSequence {
        var parent = parent
        if (parent is MdList) {
            parent = parent.parent
            if (parent is MdListItemImpl) {
                return parent.itemPrefixForPrefixes(editContext)
            }
        }
        return ""
    }

    abstract fun itemTextPrefix(editContext: PsiEditContext): CharSequence

    final override fun isWantedTaskItem(wantEmptyItems: Boolean, wantCompleteItems: Boolean, emptiesCombined: Boolean): Boolean {
        return when (taskItemType) {
            MdTaskItemType.NONE -> false
            MdTaskItemType.COMPLETE -> wantCompleteItems || (emptiesCombined && wantEmptyItems && isEmptyItem)
            MdTaskItemType.INCOMPLETE -> wantEmptyItems || !isEmptyItem
        }
    }

    final override fun isEmptyItem(): Boolean {
        var child = firstChild
        while (child != null) {
            val elementType = child.node.elementType
            if (!elementType.isIn(MdTokenSets.LIST_ITEM_MARKER_OR_WHITESPACE_SET)) {
                return elementType.isIn(MdTokenSets.EOL_OR_BLANK_LINE_SET)
            }
            child = child.nextSibling
        }
        return true
    }

    final override fun isTaskItemPrefix(prefix: CharSequence): Boolean {
        return prefix.endsWith("[ ]") || prefix.endsWith("[x]") || prefix.endsWith("[X]")
    }

    final override fun getTaskItemMarker(): ASTNode? {
        val nextNode = node.firstChildNode?.treeNext ?: return null
        return nextNode.nullIf(!nextNode.isTypeIn(MdTokenSets.TASK_LIST_ITEM_MARKERS))
    }

    final override fun getTaskItemType(): MdTaskItemType {
        return when (taskItemMarker?.elementType) {
            MdTypes.TASK_DONE_ITEM_MARKER -> MdTaskItemType.COMPLETE
            MdTypes.TASK_ITEM_MARKER -> MdTaskItemType.INCOMPLETE
            else -> MdTaskItemType.NONE
        }
    }

    final override fun hasTaskItemDescendants(wantEmptyItems: Boolean, wantCompleteItems: Boolean, emptiesCombined: Boolean): Boolean {
        return isWantedTaskItem(wantEmptyItems, wantCompleteItems, emptiesCombined) || super.hasTaskItemDescendants(wantEmptyItems, wantCompleteItems, emptiesCombined)
    }

    final override fun getHasIncompleteTaskItemDescendants(): Boolean = super.getHasIncompleteTaskItemDescendants()
    final override fun getHasTaskItemDescendants(): Boolean = super.getHasTaskItemDescendants()

    open fun itemPrefixForPrefixes(editContext: PsiEditContext): CharSequence {
        return itemTextPrefix(editContext)
    }

    final override fun getNextItem(): MdListItem? {
        var nextItem = nextSibling
        while (nextItem != null && nextItem !is MdListItem) nextItem = nextItem.nextSibling
        return nextItem as MdListItem?
    }

    final override fun getPrevItem(): MdListItem? {
        var prevItem = prevSibling
        while (prevItem != null && prevItem !is MdListItem) prevItem = prevItem.prevSibling
        return prevItem as MdListItem?
    }

    final override fun isFirstItemBlockPrefix(element: PsiElement): Boolean {
        // if there is an EOL between item marker and element, ie empty item but markers not on same line
        val itemMarker = node.firstChildNode
        if (itemMarker != null && itemMarker.treeNext?.elementType == MdTypes.EOL) {
            return false
        }
        return true
    }

    final override fun getIcon(flags: Int): Icon? {
        val icon = MdBlockPrefixProvider.elementIcon(this);
        if (icon != null) return icon;

        return when (taskItemType) {
            MdTaskItemType.NONE -> MdIcons.Element.LIST_ITEM
            MdTaskItemType.COMPLETE -> MdIcons.Structure.COMPLETE_TASK_LIST_ITEM
            MdTaskItemType.INCOMPLETE -> isEmptyItem.ifElse(MdIcons.Structure.EMPTY_TASK_LIST_ITEM, MdIcons.Structure.INCOMPLETE_TASK_LIST_ITEM)
        }
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return object : ColoredItemPresentation {
            override fun getPresentableText(): String? {
                return this@MdListItemImpl.presentableText
            }

            override fun getLocationString(): String? {
                return this@MdListItemImpl.locationString
            }

            override fun getIcon(open: Boolean): Icon? {
                return this@MdListItemImpl.getIcon(0)
            }

            override fun getTextAttributesKey(): TextAttributesKey? {
                return if (this@MdListItemImpl.isCompleteTaskItem) MdHighlighterColors.getInstance().TASK_DONE_ITEM_TEXT_ATTR_KEY else null
            }
        }
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun getPresentableText(): String? {
        //        val settings = MarkdownApplicationSettings.instance.documentSettings
        val textElement = MdPsiImplUtil.findChildTextBlock(this) ?: return ""
        // take the first paragraph text without prefixes and collapse spaces then pass for truncation with firsLineOnly false
        val truncateStringForDisplay = MdPsiImplUtil.truncateStringForDisplay(textElement.text, 100, false, true, true)
        return truncateStringForDisplay.nullIfEmpty()
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
        val text = marker + taskText + MdPsiImplUtil.getNodeText(textElement, true, false)
        return text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return getItemBlock(this)
    }

    private fun itemPrefixOnly(): CharSequence {
        val prefix = actualTextPrefix(this, true)
        val pos = prefix.indexOf('[')
        if (pos > 0) {
            return prefix.substring(0, pos)
        }
        return prefix
    }

    final override fun getPrefixMatcher(editContext: PsiEditContext): LinePrefixMatcher {
        return when (editContext.renderingProfile.parserSettings.parserListIndentationType) {
            ListIndentationType.COMMONMARK -> SpacePrefixMatcher.maxSpaces(itemPrefixOnly().length)
            ListIndentationType.GITHUB -> SpacePrefixMatcher.maxSpaces(itemPrefixOnly().length)
            else -> SpacePrefixMatcher.maxSpaces(4)
        }
    }

    final override fun removeLinePrefix(lines: LineAppendable, indentColumns: IntArray, isFirstChild: Boolean, editContext: PsiEditContext) {
        removeLinePrefix(lines, indentColumns, false, editContext, getPrefixMatcher(editContext), 0)
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}{marker: ${actualTextPrefix(false)}, priority: ${getTaskItemPriority(PsiEditAdjustment(containingFile))}, descendantPriority: ${getTaskItemDescendantPriority(PsiEditAdjustment(containingFile))}, text: ${presentableText}}"
    }
}
