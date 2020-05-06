// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.flexmark.util.sequence.RepeatedSequence
import com.vladsch.md.nav.actions.api.MdElementContextInfoProvider
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.util.BlockPrefixes
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdPsiImplUtil.isWhitespaceOrBlankLine
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.ListIndentationType
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.util.format.MdFormatter
import com.vladsch.plugin.util.maxLimit
import com.vladsch.plugin.util.minLimit
import com.vladsch.plugin.util.psi.isTypeIn
import com.vladsch.plugin.util.psi.isTypeOf
import com.vladsch.plugin.util.toBased
import java.util.*

open class ListItemContext(
    val context: CaretContextInfo,
    val listElement: MdListImpl,
    val listItemElement: MdListItemImpl,
    val lineOffset: Int,
    val isEmptyItem: Boolean,
    val isTaskItem: Boolean,
    val isItemDone: Boolean,
    val wrappingContext: WrappingContext
) {

    fun canIndentItem(): Boolean {
        if (isNonFirstListItem()) return true

        // see if it preceded by a list item
        val listElement = listElement
        val listLevel = getListLevel(listElement)
        var prevSibling = listElement.prevSibling
        while (prevSibling != null && isWhitespaceOrBlankLine(prevSibling.node.elementType)) prevSibling = prevSibling.prevSibling

        while (prevSibling != null && prevSibling !is PsiFile && prevSibling.node.elementType !in listOf(MdTypes.BULLET_LIST, MdTypes.ORDERED_LIST)) {
            prevSibling = prevSibling.parent
        }
        if (prevSibling == null || prevSibling is PsiFile) return false

        if (prevSibling.javaClass != listElement.javaClass) {
            // see if common mark where list types must match
            val profile = MdRenderingProfileManager.getProfile(listElement.containingFile)
            if (profile.parserSettings.parserListIndentationType == ListIndentationType.COMMONMARK) {
                return false
            }
        }
        val prevListLevel = getListLevel(prevSibling)
        return prevListLevel >= listLevel
    }

    fun canUnIndentItem(): Boolean {
        return isNonFirstLevelList()
    }

    fun isNonFirstLevelList(): Boolean {
        var element = listElement.parent ?: return false
        while (!(element is MdList)) {
            if (element is PsiFile || element.node == null) return false
            element = element.parent ?: return false
        }
        return element.isTypeOf(MdTokenSets.LIST_ELEMENT_TYPES)
    }

    fun isNonFirstListItem(): Boolean {
        val listItemElement = listItemElement
        val listElement = listElement
        return listElement.firstChild !== listItemElement
    }

    open fun indentItem(adjustCaret: Boolean, onlyEmptyItemLine: Boolean) {
        if (!canIndentItem()) return

        // need to take all its contents and indent
        val list = listElement
        val listItem = listItemElement
        val prefixes = MdPsiImplUtil.getBlockPrefixes(list, null, context)
        val caretLineInItem = context.caretLine - wrappingContext.firstLine

        // indent this item by inserting 4 spaces for it and all its elements and changing its number to 1
        val itemLines = MdPsiImplUtil.linesForWrapping(listItem, true, true, true, context)
        var caretDelta = 0
        var linesAdded = 0

        // see if we need to insert a blank line before, if the previous item's last child is not the first indenting and not blank line
        val prevSibling = context.findElementAt(context.offsetLineStart(listItem.node.startOffset)!! - 1)
        if (prevSibling != null && !MdPsiImplUtil.isPrecededByBlankLine(listItem)) {
            var blockElement = MdPsiImplUtil.getBlockElement(listItem)
            if (blockElement != null && blockElement.node.elementType == MdTypes.PARAGRAPH_BLOCK) {
                if (blockElement == MdPsiImplUtil.findChildTextBlock(blockElement.parent)?.parent) blockElement = blockElement.parent
            }

            if (blockElement !is MdListItemImpl) {
                // add a blank line
                if (blockElement == null || !MdPsiImplUtil.isFollowedByBlankLine(blockElement)) {
                    itemLines.insertLine(0, itemLines[0].prefix, "")
                    linesAdded++
                }
            }
        }

        val prevListItem = prevListItem(list, listItem) ?: listItem
        val listItemPrefixes = (prevListItem as MdListItemImpl).itemPrefixes(prefixes, context)
        val prefixedLines = itemLines.copyAppendable()
        MdPsiImplUtil.addLinePrefix(prefixedLines, listItemPrefixes.childPrefix, listItemPrefixes.childContPrefix)

        // adjust for prefix change
        // add 1 char for every \n added, plus one prefix for every line added plus one prefix change for every additional line caret is offset from the first line
        val prefixChange = prefixedLines[0].prefixLength - itemLines[0].prefixLength
        caretDelta += linesAdded + (linesAdded * listItemPrefixes.childPrefix.length) + prefixChange * (caretLineInItem + 1)

        // need to adjust the replaced end to include the EOL, if not done for empty task items this causes an extra \n to be left in the document
        val postEditNodeEnd = context.postEditNodeEnd(listItem.node)
        val virtualSpaces = context.editor.caretModel.logicalPosition.column - (context.caretOffset - context.document.getLineStartOffset(context.document.getLineNumber(context.caretOffset)))
        var endDelta = 0
        while (postEditNodeEnd + endDelta < context.document.textLength && !context.document.charsSequence.subSequence(context.offsetLineStart(context.postEditNodeStart(listItem.node))!!, postEditNodeEnd + endDelta).endsWith("\n")) {
            endDelta++
        }

        // need to insert trailing spaces after an empty list item
        if (isEmptyItem) {
            val trailingSpaces = context.beforeCaretChars.toBased().countTrailingSpaceTab()
            if (trailingSpaces > 0) {
                prefixedLines.setLine(0, prefixedLines[0].prefix, prefixedLines[0].text.append(RepeatedSequence.ofSpaces(trailingSpaces)))
            }
        }

        context.document.replaceString(context.offsetLineStart(context.postEditNodeStart(listItem.node))!!, postEditNodeEnd + endDelta, prefixedLines.toString(1, 1))

        val caretOffset = context.adjustedDocumentPosition((context.caretOffset + caretDelta).maxLimit(context.charSequence.length))

        if (adjustCaret) {
            if (endDelta > 0 || virtualSpaces > 0) {
                // adjust for any trailing spaces after the marker that were removed for an empty item
                val pos = context.editor.offsetToLogicalPosition(caretOffset.adjustedOffset - (endDelta - 1).minLimit(0))
                context.editor.caretModel.moveToLogicalPosition(LogicalPosition(pos.line, pos.column + (endDelta - 1).minLimit(0) + virtualSpaces))
            } else {
                //                System.out.println("Adjusting: change $prefixChange, line $caretLineInItem, caretDelta $caretDelta")
                context.editor.caretModel.moveToOffset(caretOffset.adjustedOffset)
            }
        }
    }

    // this will make this item the next after the parent list item
    open fun unIndentItem(adjustCaret: Boolean, onlyEmptyItemLine: Boolean) {
        if (!canUnIndentItem()) return
        val list = listElement
        val listItem = listItemElement
        val parentListItem = getParentListItemElement(list)
        val parentList = if (parentListItem != null) getParentListElement(parentListItem) else null
        val parentPrefixes = if (parentList != null) MdPsiImplUtil.getBlockPrefixes(parentList, null, context) else BlockPrefixes.EMPTY
        val caretLineInItem = context.caretLine - wrappingContext.firstLine

        // un indent this item by inserting the previous parent's prefix
        val itemLines = MdPsiImplUtil.linesForWrapping(listItem, true, true, true, context)
        var caretDelta = 0
        val linesAdded = 0
        val prefixedLines = itemLines.copyAppendable()
        MdPsiImplUtil.addLinePrefix(prefixedLines, parentPrefixes.childPrefix, parentPrefixes.childContPrefix)

        // adjust for prefix change
        // add 1 char for every \n added, plus one prefix for every line added plus one prefix change for every additional line caret is offset from the first line
        val prefixChange = prefixedLines[0].prefixLength - itemLines[0].prefixLength
        caretDelta += linesAdded + (linesAdded * parentPrefixes.childPrefix.length) + prefixChange * (caretLineInItem + 1)

        // need to adjust the replaced end to include the EOL, if not done for empty task items this causes an extra \n to be left in the document
        val postEditNodeEnd = context.postEditNodeEnd(listItem.node)
        val virtualSpaces = context.editor.caretModel.logicalPosition.column - (context.caretOffset - context.document.getLineStartOffset(context.document.getLineNumber(context.caretOffset)))
        var endDelta = 0
        while (postEditNodeEnd + endDelta < context.document.textLength && !context.document.charsSequence.subSequence(context.offsetLineStart(context.postEditNodeStart(listItem.node))!!, postEditNodeEnd + endDelta).endsWith("\n")) {
            endDelta++
        }
        context.document.replaceString(context.offsetLineStart(context.postEditNodeStart(listItem.node))!!, postEditNodeEnd + endDelta, prefixedLines.toString(1, 1))

        val caretOffset = context.adjustedDocumentPosition((context.caretOffset + caretDelta).minLimit(0))

        if (adjustCaret) {
            if (endDelta > 0 || virtualSpaces > 0) {
                // adjust for any trailing spaces after the marker that were removed for an empty item
                val pos = context.editor.offsetToLogicalPosition(caretOffset.adjustedOffset - (endDelta - 1).minLimit(0))
                context.editor.caretModel.moveToLogicalPosition(LogicalPosition(pos.line, pos.column + (endDelta - 1).minLimit(0) + virtualSpaces))
            } else {
                context.editor.caretModel.moveToOffset(caretOffset.adjustedOffset)
            }
        }
    }

    open fun addItem(adjustCaret: Boolean, removeDoneMarker: Boolean) {
        val startLineOffset = context.offsetLineStart(context.caretOffset)
        val endLineOffset = context.offsetLineEnd(context.caretOffset)
        val list = listElement

        if (startLineOffset != null && endLineOffset != null) {
            val listItem = listItemElement
            val prefixes = MdPsiImplUtil.getBlockPrefixes(list, null, context)
            val prefix = prefixes.childPrefix

            // take the prefix from wrapping context
            val itemOnlyPrefix = listItem.actualTextPrefix(context, true).toString()
            var itemPrefix = prefix.toString() + itemOnlyPrefix //wrappingContext.prefixText().toString()

            if (removeDoneMarker) {
                val taskMarkerPos = itemPrefix.toLowerCase().indexOf("[x]")
                if (taskMarkerPos > 0) {
                    // replace by not completed task
                    itemPrefix = itemPrefix.substring(0, taskMarkerPos + 1) + " " + itemPrefix.substring(taskMarkerPos + 2)
                }
            }

            // if prefix was inserted then it will have not just whitespace
            var lastPos = startLineOffset
            LOG.debug("replacing start of line prefix: '${context.charSequence.subSequence(lastPos, endLineOffset)}' with: '$itemPrefix'")
            while (lastPos < endLineOffset) {
                val c = context.charSequence[lastPos]
                if (!(c.isWhitespace() || context.isIndentingChar(c))) break
                lastPos++
            }

            context.document.replaceString(startLineOffset, lastPos, itemPrefix)

            val prefixLength = itemPrefix.length
            val caretOffset = context.adjustedDocumentPosition(startLineOffset + prefixLength)

            if (adjustCaret) {
                context.editor.caretModel.currentCaret.moveToOffset(caretOffset.adjustedOffset)
            }
        }
    }

    open fun removeItem(adjustCaret: Boolean, willBackspace: Boolean) {
        val list = listElement
        val listItem = listItemElement
        val listMarkerNode = listItem.firstChild.node
        val startLineOffset = context.offsetLineStart(context.caretOffset)

        if (startLineOffset != null) {
            val prefixes = MdPsiImplUtil.getBlockPrefixes(list, null, context)
            val prefix = prefixes.childPrefix

            var replacePrefix = if (willBackspace) " " else ""
            var adjustCaretOffset = if (willBackspace) 1 else 0
            val nodeStartOffset = context.postEditNodeStart(listMarkerNode)
            val lastMarkerNode = MdPsiImplUtil.nextNonWhiteSpaceSibling(listMarkerNode) ?: listMarkerNode
            val nodeEndOffset = context.postEditNodeEnd(lastMarkerNode)

            // see if we need to insert a blank line before, if the previous item's last child is not the first indenting and not blank line
            if (!isEmptyItem) {
                val prevSibling = context.findElementAt(context.offsetLineStart(listItem.node.startOffset)!! - 1)
                if (prevSibling != null) {
                    if (prevSibling !is MdBlankLine) {
                        val blockElement = MdPsiImplUtil.getBlockElement(prevSibling)
                        if (blockElement == null || !MdPsiImplUtil.isFollowedByBlankLine(blockElement)) {
                            // add a blank line
                            replacePrefix = "\n" + prefix + replacePrefix
                            adjustCaretOffset += prefix.length + 1
                        }
                    }
                }

                // see if we need to insert a blank line after, if the item's last child is not the first indenting and not blank line
                val nextSibling = listItem.nextSibling
                if (nextSibling !is MdBlankLine) {
                    if (listItem.children.size > 1) {
                        val childBlockElement = MdPsiImplUtil.getBlockElement(listItem.lastChild)
                        if (childBlockElement != null && !MdPsiImplUtil.isFollowedByBlankLine(childBlockElement)) {
                            val blockEndOffset = context.postEditNodeEnd(childBlockElement.node)
                            context.document.insertString(blockEndOffset, prefix.toString() + "\n")
                        }
                    }

                    // add a blank line after the paragraph or text
                    val blockElement = MdPsiImplUtil.findChildTextBlock(listItem)
                    if (blockElement != null && !MdPsiImplUtil.isFollowedByBlankLine(blockElement)) {
                        val blockEndOffset = context.postEditNodeEnd(blockElement.node)
                        context.document.insertString(blockEndOffset, prefix.toString() + "\n")
                    }
                }
                context.document.replaceString(nodeStartOffset, if (willBackspace) context.wrappingContext?.firstPrefixEnd
                    ?: nodeEndOffset else nodeEndOffset, replacePrefix)
            } else {
                context.document.replaceString(nodeStartOffset, if (willBackspace) context.wrappingContext?.firstPrefixEnd
                    ?: nodeEndOffset else nodeEndOffset, replacePrefix + if (willBackspace) "" else "\n")
            }

            val caretOffset = context.adjustedDocumentPosition(nodeStartOffset)

            if (adjustCaret) {
                context.editor.caretModel.currentCaret.moveToOffset(caretOffset.adjustedOffset + adjustCaretOffset)
            }
        }
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.editor.handlers.list")

        @JvmField
        val TRACE_LIST_ITEM_EDIT: Boolean = false

        @JvmStatic
        fun getContext(context: CaretContextInfo, preEditListItemElement: PsiElement? = null): ListItemContext? {
            val caretOffset = context.caretOffset
            var caretLine = context.offsetLineNumber(context.preEditOffset(caretOffset))!!
            var useContext = context
            if (MdPsiImplUtil.isBlankLine(context.findElementAt(caretOffset))) {
                // go up to non blank and check
                while (caretLine > 0) {
                    caretLine--
                    val startIndex = context.lineStart(caretLine)
                    val endIndex = context.lineEnd(caretLine)
                    if (startIndex != null && endIndex != null && startIndex < endIndex) {
                        if (!MdPsiImplUtil.isBlankLine(context.file.findElementAt(endIndex - 1))) {
                            // can find start here
                            useContext = CaretContextInfo.subContext(context, endIndex)
                            break
                        }
                    }

                    if (context.caretLine - caretLine > 1) return null
                }
            }

            val wrappingContext = useContext.wrappingContext?.withMainListItem(context, preEditListItemElement) ?: return null

            if (wrappingContext.mainElement is MdListItem) {
                if (wrappingContext.mainElement === preEditListItemElement || wrappingContext.formatElement == null || MdPsiImplUtil.isFirstIndentedBlock(wrappingContext.formatElement, false)) {
                    val nextSibling = MdPsiImplUtil.nextNonWhiteSpaceSibling(wrappingContext.mainElement.firstChild.node)
                    val isTaskItem = nextSibling.isTypeIn(MdTokenSets.TASK_LIST_ITEM_MARKERS)
                    val isItemDone = isTaskItem && nextSibling != null && nextSibling.elementType != MdTypes.TASK_ITEM_MARKER
                    val listElement = getParentListElement(wrappingContext.mainElement) as MdListImpl?
                    val listItemElement = wrappingContext.mainElement as? MdListItemImpl
                    if (listElement != null && listItemElement != null) {
                        return MdElementContextInfoProvider.PROVIDER.value.getListItemContext(context,
                            listElement,
                            listItemElement,
                            context.caretLine - caretLine,
                            wrappingContext.startOffset == wrappingContext.endOffset,
                            isTaskItem,
                            isItemDone,
                            wrappingContext)
                    }
                }
            }
            return null
        }

        @JvmStatic
        fun lastListItem(list: PsiElement): MdListItemImpl? {
            var childListItem = list.lastChild
            while (childListItem != null && childListItem !is MdListItemImpl) childListItem = childListItem.prevSibling
            return childListItem as? MdListItemImpl
        }

        @JvmStatic
        fun listItemCount(list: PsiElement?): Int {
            var items = 0
            if (list != null) {
                for (child in list.children) {
                    if (child is MdListItemImpl) items++
                }
            }

            return items
        }

        fun getParentListElement(listItem: PsiElement): PsiElement? {
            var element = listItem

            while (element !is MdList) {
                element = element.parent ?: return null
                if (element is PsiFile || element.node == null) return null
            }
            return element
        }

        fun getParentListItemElement(list: PsiElement): PsiElement? {
            var element = list.parent ?: return null

            while (element !is MdListItem) {
                element = element.parent ?: return null
                if (element is PsiFile || element.node == null) return null
            }
            return element
        }

        fun prevListItem(list: PsiElement, listItem: PsiElement): PsiElement? {
            var prevListItem: PsiElement? = null
            for (item in list.children) {
                if (item === listItem) break
                if (item is MdListItem) prevListItem = item
            }
            return prevListItem
        }

        fun getListLevel(listElement: PsiElement): Int {
            @Suppress("NAME_SHADOWING")
            var listElement = listElement
            var listLevel = 0
            while (listElement !is PsiFile) {
                if (listElement is MdList) listLevel++
                listElement = listElement.parent ?: break
            }
            return listLevel
        }

        enum class AddBlankLineType(val addBefore: Boolean, val addAfter: Boolean) {
            NONE(false, false), AFTER(false, true), BEFORE(true, false), AROUND(true, true)
        }

        @JvmStatic
        fun loosenListItems(listElement: PsiElement, listItems: Set<PsiElement>? = null, maxWanted: Int = Int.MAX_VALUE): Pair<List<AddBlankLineType>, Int> {
            val result = ArrayList<AddBlankLineType>()
            val listItemCount = listItemCount(listElement)
            var listItemOrdinal = 0
            var state = 0

            if (listItemCount < 2 || listElement !is MdList) {
                result.add(AddBlankLineType.NONE)
                return Pair(result, state)
            }

            for (item in listElement.children) {
                if (item !is MdListItem) {
                    continue
                }
                var addBlankLineType = AddBlankLineType.NONE

                if (listItems == null || listItems.contains(item)) {
                    if (!(item.nextSibling == null || item.nextSibling.isTypeOf(MdTokenSets.BLANK_LINE_SET)) && !MdPsiImplUtil.isFollowedByBlankLine(item)) {
                        if (listItemOrdinal == listItemCount - 1) {
                            if (listElement.parent is MdListItem) {
                            } else {
                                addBlankLineType = AddBlankLineType.AFTER
                            }
                        } else {
                            addBlankLineType = AddBlankLineType.AFTER
                        }
                    }

                    if (listItemOrdinal == 0 && MdFormatter.listNeedsBlankLineBefore(listElement, null, false)) {
                        val startOffset = item.node.startOffset
                        if (startOffset > 0) {
                            if (!addBlankLineType.addAfter) addBlankLineType = AddBlankLineType.BEFORE
                            else addBlankLineType = AddBlankLineType.AROUND
                        }
                    }

                    result.add(addBlankLineType)

                    if (addBlankLineType.addAfter) ++state
                    if (addBlankLineType.addBefore) ++state
                    if (state >= maxWanted) return Pair(result, state)
                }

                listItemOrdinal++
            }
            return Pair(result, state)
        }

        @JvmStatic
        fun tightenListItems(listElement: PsiElement, listItems: Set<PsiElement>? = null, maxWanted: Int = Int.MAX_VALUE): Pair<List<PsiElement>, Int> {
            val result = ArrayList<PsiElement>()
            val listItemCount = listItemCount(listElement)
            var listItemOrdinal = 0
            var state = 0

            if (listItemCount < 2 || listElement !is MdList) {
                return Pair(result, state)
            }

            loopFor@
            for (item in listElement.children) {
                if (item !is MdListItem) {
                    continue
                }

                if (listItems == null || listItems.contains(item)) {
                    if (listItemOrdinal == 0 && MdPsiImplUtil.parentSkipBlockQuote(listElement) is MdListItem && !MdFormatter.listNeedsBlankLineBefore(listElement, null, true)) {
                        val prevSibling = MdPsiImplUtil.prevNonWhiteSpaceSibling(item.prevSibling)
                        if (prevSibling is MdParagraph) {
                            // see if this is our parent item paragraph
                            val parent = listElement.parent
                            if (parent is MdListItem) {
                                if (parent.isFirstItemBlock(prevSibling)) {
                                    var blankLine = MdPsiImplUtil.precedingBlankLine(item)
                                    val index = result.size
                                    while (blankLine != null) {
                                        result.add(index, blankLine)
                                        state++
                                        if (state >= maxWanted) break@loopFor
                                        blankLine = MdPsiImplUtil.precedingBlankLine(blankLine)
                                    }
                                }
                            }
                        }
                    }

                    if (listItemOrdinal != listItemCount - 1) {
                        var blankLine = MdPsiImplUtil.followingBlankLine(item)
                        while (blankLine != null) {
                            result.add(blankLine)
                            state++
                            if (state >= maxWanted) break@loopFor
                            blankLine = MdPsiImplUtil.followingBlankLine(blankLine)
                        }
                    }
                }

                listItemOrdinal++
            }

            return Pair(result, state)
        }

        @JvmStatic
        fun canTightenList(listElement: PsiElement, listItems: Set<PsiElement>? = null): Boolean {
            return listItemCount(listElement) > 1 && tightenListItems(listElement, listItems, 1).second > 0
        }

        @JvmStatic
        fun canLoosenList(listElement: PsiElement, listItems: Set<PsiElement>? = null): Boolean {
            return listItemCount(listElement) > 1 && loosenListItems(listElement, listItems, 1).second > 0
        }

        @JvmStatic
        fun isLooseList(listElement: PsiElement, listItems: Set<PsiElement>? = null): Boolean {
            return !canTightenList(listElement, listItems)
        }

        @JvmStatic
        fun isTightList(listElement: PsiElement, listItems: Set<PsiElement>? = null): Boolean {
            return !canLoosenList(listElement, listItems)
        }

        @JvmStatic
        fun makeLooseList(context: CaretContextInfo, listElement: PsiElement, listItems: Set<PsiElement>?) {
            val caretOffset = context.adjustedDocumentPosition(context.caretOffset)
            val prefixes = MdPsiImplUtil.getBlockPrefixes(listElement, null, context)
            val (itemList, _) = loosenListItems(listElement, listItems)
            val children = listElement.children
            var i = listItems?.size ?: listItemCount(listElement)
            for (item in children.reversed()) {
                if (item !is MdListItemImpl || listItems != null && !listItems.contains(item)) continue
                val insertType = itemList[--i]

                if (insertType.addAfter) {
                    context.document.insertString(context.postEditNodeEnd(item.node), prefixes.childPrefix.toString() + "\n")
                }

                if (insertType.addBefore) {
                    context.document.insertString(context.offsetLineStart(context.postEditNodeStart(item.node))!!, prefixes.childPrefix.toString() + "\n")
                }
            }
            context.editor.caretModel.moveToOffset(caretOffset.adjustedOffset)
        }

        @JvmStatic
        fun makeTightList(context: CaretContextInfo, listElement: PsiElement, listItems: Set<PsiElement>?) {
            val (blankLines, _) = tightenListItems(listElement, listItems)
            val caretOffset = context.adjustedDocumentPosition(context.caretOffset)
            for (line in blankLines.reversed()) {
                val startOffset = line.node.startOffset
                val endOffset = startOffset + line.node.textLength
                context.document.deleteString(startOffset, endOffset.maxLimit(context.charSequence.length))
            }
            context.editor.caretModel.moveToOffset(caretOffset.adjustedOffset)
        }

        fun listItemOrdinal(list: PsiElement, listItem: PsiElement): Int {
            var listItemOrdinal = 0
            for (item in list.children) {
                if (item !is MdListItem) continue

                listItemOrdinal++
                if (item === listItem) break
            }
            return listItemOrdinal
        }
    }
}
