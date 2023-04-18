// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.lang.ASTNode
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.flexmark.util.sequence.Range
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder
import com.vladsch.md.nav.actions.styling.util.ElementListBag
import com.vladsch.md.nav.actions.styling.util.ElementType
import com.vladsch.md.nav.actions.styling.util.MdActionUtil
import com.vladsch.md.nav.psi.element.*
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.plugin.util.maxLimit
import com.vladsch.plugin.util.minLimit
import com.vladsch.plugin.util.psi.isTypeIn
import java.util.*
import java.util.function.Function

abstract class ListToggleStateSelectionAction : ToggleAction(), DumbAware, Function<PsiElement, ElementType> {
    protected abstract fun performAction(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>)
    protected abstract fun wantElement(element: PsiElement): Boolean
    protected abstract fun isSelected(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>): Boolean
    protected abstract fun wantUnselectedChildItems(): Boolean

    private fun adjustNoSelectionRange(editContext: CaretContextInfo): Range {
        var element = editContext.findElementAt((editContext.caretLineEnd - 1).minLimit(editContext.caretLineStart))

        if (element?.node?.elementType === MdTypes.EOL) {
            // take the element at previous caret position
            element = editContext.findElementAt((editContext.caretLineEnd - 2).minLimit(editContext.caretLineStart))
        }

        if (element != null) {
            var parent = element.parent
            while (parent !is PsiFile && parent !is MdList && parent !is MdListItem) parent = parent.parent

            if (parent is MdListItem) {
                return if (wantUnselectedChildItems()) {
                    editContext.nodeRange(parent.node)
                } else {
                    // just take the first line
                    val postEditNodeStart = editContext.postEditNodeStart(parent.node)
                    Range.of(postEditNodeStart, editContext.offsetLineEnd(postEditNodeStart)!!)
                }
            } else if (parent is MdList) {
                return if (wantUnselectedChildItems()) {
                    // take the first list item of the list
                    editContext.nodeRange(parent.firstChild.node)
                } else {
                    // just take the first line
                    val postEditNodeStart = editContext.postEditNodeStart(parent.firstChild.node)
                    Range.of(postEditNodeStart, editContext.offsetLineEnd(postEditNodeStart)!!)
                }
            }
        }
        return Range.of(editContext.caretLineStart, editContext.caretLineEnd)
    }

    override fun isSelected(e: AnActionEvent): Boolean {
        var state = false
        editContext(e, true) { editContext ->
            val elementBag = ElementListBag<ElementType>(this)
            if (collectSelectedElements(editContext, true, elementBag) && elementBag.size > 0) {
                state = isSelected(editContext, elementBag)
            }
        }
        return state
    }

    open fun isEnabled(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>): Boolean {
        return elementBag.size > 0
    }

    open fun conditionDone(conditionBuilder: DisabledConditionBuilder) {
        conditionBuilder.done(true, false)
    }

    override fun update(e: AnActionEvent) {
        val conditionBuilder = MdActionUtil.getConditionBuilder(e, this) { it, (_, _, _) ->
            caretContextInfoOrNull(e, true) { editContext ->
                it.notNull(editContext) {
                    val elementBag = ElementListBag(this)
                    collectSelectedElements(editContext!!, true, elementBag)
                    it.and(isEnabled(editContext, elementBag), "No compatible list items in context")
                }
            }
        }

        conditionDone(conditionBuilder)
        super.update(e)
    }

    override fun isDumbAware(): Boolean {
        return false
    }

    override fun apply(element: PsiElement): ElementType {
        if (element is MdListItem) {
            val taskItemMarker = element.taskItemMarker

            return when {
                taskItemMarker?.elementType == MdTypes.TASK_ITEM_MARKER -> ElementType.TASK_LIST_ITEM
                taskItemMarker?.elementType == MdTypes.TASK_DONE_ITEM_MARKER -> ElementType.TASK_LIST_DONE_ITEM
                element is MdOrderedListItem -> ElementType.ORDERED_LIST_ITEM
                else -> ElementType.UNORDERED_LIST_ITEM
            }
        } else if (element is MdParagraph) {
            return ElementType.PARAGRAPH_BLOCK
        }
        return ElementType.NONE
    }

    private fun wantChildren(startOffset: Int, endOffset: Int, topElement: PsiElement, limitTime: Boolean, elementBag: ElementListBag<ElementType>): Boolean {
        var element = topElement.firstChild

        while (element != null) {
            if (element.node.startOffset >= endOffset) break
            if (element.node.startOffset + element.node.textLength < startOffset) {
                element = element.nextSibling
                continue
            }

            if (element.node.startOffset >= startOffset) {
                if (wantElement(element)) {
                    elementBag.add(element)
                    if (limitTime && elementBag.size > 100) return false
                }
            }

            wantChildren(startOffset, endOffset, element, limitTime, elementBag)
            element = element.nextSibling
        }

        return true
    }

    private fun collectSelectedElements(editContext: CaretContextInfo, limitTime: Boolean, elementBag: ElementListBag<ElementType>): Boolean {
        val editor = editContext.editor
        val psiFile = editContext.file
        val startOffset: Int
        val endOffset: Int

        if (editContext.editor.caretModel.primaryCaret.hasSelection()) {
            startOffset = editor.caretModel.primaryCaret.selectionStart
            endOffset = editor.caretModel.primaryCaret.selectionEnd
        } else {
            val range = adjustNoSelectionRange(editContext)
            startOffset = range.start
            endOffset = range.end
        }

        var element = psiFile.firstChild
        val topElements = ArrayList<PsiElement>()
        while (element != null) {
            if (element.node.startOffset >= endOffset) break
            if (element.node.startOffset + element.node.textLength <= startOffset) {
                element = element.nextSibling
                continue
            }

            topElements.add(element)
            if (limitTime && topElements.size > 100) return false

            element = element.nextSibling
        }

        for (topElement in topElements) {
            if (wantElement(topElement)) {
                elementBag.add(topElement)
                if (limitTime && elementBag.size > 100) return false
            }

            if (!wantChildren(startOffset, endOffset, topElement, limitTime, elementBag)) return false
        }

        return true
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        editContext(e, true) { editContext ->
            WriteCommandAction.runWriteCommandAction(editContext.file.project) {
                val psiFile = editContext.file
                val editor = editContext.editor
                val document = editor.document

                // here we have to extract elements that are desired for processing by the action
                // take all block elements and sub-elements that intersect the selection
                val elementBag = ElementListBag<ElementType>(this)
                collectSelectedElements(editContext, false, elementBag)
                performAction(editContext, elementBag)

                PsiDocumentManager.getInstance(psiFile.project).commitDocument(document)
            }
        }
    }

    private fun editContext(e: AnActionEvent, wantSelection: Boolean, runnable: (CaretContextInfo) -> Unit) {
        caretContextInfoOrNull(e, wantSelection) {
            if (it != null) runnable.invoke(it)
        }
    }

    private fun caretContextInfoOrNull(e: AnActionEvent, wantSelection: Boolean, runnable: (CaretContextInfo?) -> Unit) {
        MdActionUtil.getProjectEditorPsiFile(e)?.let { (_, editor, psiFile) ->
            var handled = false
            if (editor.caretModel.caretCount == 1 && (wantSelection || !editor.caretModel.primaryCaret.hasSelection())) {
                CaretContextInfo.withContext(psiFile, editor, null, false, editor.caretModel.primaryCaret.offset) { caretContext ->
                    runnable.invoke(caretContext)
                    handled = true
                }

                if (!handled) runnable.invoke(null)
            }
        }
    }

    fun togglePrefix(editContext: CaretContextInfo, elementBag: ElementListBag<ElementType>, PREFIX: CharSequence, removePrefix: Boolean, secondMarkerOnly: Boolean) {
        // if all items are paragraphs then we make them into items
        // otherwise we take all list items and we make them into our item type
        val allParagraphs = elementBag.countMapped(ElementType.PARAGRAPH_BLOCK) == elementBag.size
        val caretOffset = editContext.adjustedDocumentPosition(editContext.caretOffset)
        val document = editContext.editor.document
        if (allParagraphs) {
            if (elementBag.size == 1 && editContext.editor.selectionModel.hasSelection()) {
                // special case convert all lines to items
                val element = elementBag[0]
                val itemLines = MdPsiImplUtil.linesForWrapping(element, true, true, true, editContext)
                val selectionStart = editContext.editor.selectionModel.selectionStart
                val selectionEnd = editContext.editor.selectionModel.selectionEnd
                for (info in itemLines) {
                    val sourceStart = info.text.startOffset
                    val sourceEnd = info.text.endOffset

                    if (sourceEnd <= selectionStart || sourceStart >= selectionEnd) {
                        // not included, gets no prefix
                    } else {
                        // NOTE: add prefix as text so that it is not lost when copying without prefixes below
                        itemLines.setLine(info.index, info.prefix, info.text.prefixWith(PREFIX))
                    }
                }
                val prefixes = MdPsiImplUtil.getBlockPrefixes(element, null, editContext)

                val prefixedLines = itemLines.copyAppendable()
                MdPsiImplUtil.addLinePrefix(prefixedLines, prefixes.childPrefix, prefixes.childContPrefix)
                document.replaceString(element.node.startOffset, element.node.startOffset + element.node.textLength, prefixedLines.toSequence())
            } else {
                val selectionStart = editContext.editor.selectionModel.selectionStart

                for (element in elementBag.reversed()) {
                    document.insertString(element.node.startOffset, PREFIX)
                }

                if (editContext.editor.selectionModel.hasSelection()) {
                    val selectionEnd = editContext.editor.selectionModel.selectionEnd
                    editContext.editor.selectionModel.setSelection(selectionStart, selectionEnd)
                }
            }
        } else if (removePrefix && !secondMarkerOnly) {
            val blankLines = HashSet<Int>()
            var insertBlankLines = false

            // need to handle a special case of tight list with all elements being one line
            for ((index, element) in elementBag.withIndex()) {
                if (element is MdListItem) {
                    if (index + 1 < elementBag.size && MdPsiImplUtil.isFollowedByBlankLine(element)) {
                        insertBlankLines = true
                        break
                    }

                    val textLinesToWrap = MdPsiImplUtil.linesForWrapping(element, true, true, true, editContext)
                    if (textLinesToWrap.lineCount > 1) {
                        insertBlankLines = true
                        break
                    }
                }
            }

            for (element in elementBag.reversed()) {
                if (element is MdListItem) {
                    val itemMarker = element.listItemMarker ?: continue
                    var taskItemMarker = element.taskItemMarker ?: itemMarker

                    if (!taskItemMarker.isTypeIn(MdTokenSets.TASK_LIST_ITEM_MARKERS)) taskItemMarker = itemMarker
                    val insertBlankLineBefore = insertBlankLines && element.node.startOffset > 0 && !MdPsiImplUtil.isPrecededByBlankLine(element)

                    if (insertBlankLines && !MdPsiImplUtil.isFollowedByBlankLine(element)) {
                        val lineNumber = editContext.offsetLineNumber(editContext.postEditNodeEnd(element.node) - 1)
                        if (lineNumber != null && !blankLines.contains(lineNumber + 1)) {
                            blankLines.add(lineNumber + 1)
                            MdPsiImplUtil.insertBlankLineAfter(editContext.document, element, null, editContext)
                        }
                    }

                    if (insertBlankLineBefore) {
                        val lineNumber = editContext.offsetLineNumber(editContext.postEditNodeStart(element.node))
                        if (lineNumber != null && !blankLines.contains(lineNumber)) {
                            blankLines.add(lineNumber)
                            val prefixes = MdPsiImplUtil.getBlockPrefixes(element, null, editContext)

                            document.deleteString(itemMarker.startOffset, taskItemMarker.startOffset + taskItemMarker.textLength)

                            val offsetLineStart = editContext.offsetLineStart(editContext.postEditNodeStart(element.node))
                            if (offsetLineStart != null && offsetLineStart > 0) {
                                document.insertString(offsetLineStart, prefixes.childPrefix.suffixWithEOL())
                            }
                        }
                    } else {
                        document.deleteString(itemMarker.startOffset, taskItemMarker.startOffset + taskItemMarker.textLength)
                    }
                }
            }
        } else {
            for (element in elementBag.reversed()) {
                if (element is MdListItemImpl) {
                    val itemMarker = element.listItemMarker ?: continue

                    if (!secondMarkerOnly || !removePrefix) {
                        var taskItemMarker = element.taskItemMarker ?: itemMarker
                        var endOffset = taskItemMarker.startOffset
                        if (!taskItemMarker.isTypeIn(MdTokenSets.TASK_LIST_ITEM_MARKERS)) {
                            taskItemMarker = itemMarker
                            endOffset = itemMarker.startOffset + itemMarker.textLength
                            if (endOffset < document.textLength && document.charsSequence[endOffset] == ' ') endOffset++
                        }

                        val prefix = adjustItemPrefix(element, itemMarker, taskItemMarker, PREFIX, removePrefix)
                        if (secondMarkerOnly) {
                            if (taskItemMarker == itemMarker) return
                            endOffset = taskItemMarker.startOffset + taskItemMarker.textLength
                            if (endOffset < document.textLength && document.charsSequence[endOffset] == ' ') endOffset++
                            document.replaceString(taskItemMarker.startOffset, endOffset, prefix)
                        } else {
                            document.replaceString(itemMarker.startOffset, endOffset, prefix)
                        }
                    } else {
                        val taskItemMarker = MdPsiImplUtil.nextNonWhiteSpaceSibling(itemMarker) ?: continue
                        if (!taskItemMarker.isTypeIn(MdTokenSets.TASK_LIST_ITEM_MARKERS)) continue
                        val prefix = adjustItemPrefix(element, itemMarker, taskItemMarker, PREFIX, removePrefix)
                        document.replaceString(itemMarker.startOffset, (taskItemMarker.startOffset + taskItemMarker.textLength.minLimit(4)).maxLimit(document.textLength), prefix)
                    }
                }
            }
        }
        editContext.editor.caretModel.moveToOffset(caretOffset.adjustedOffset)
    }

    protected open fun adjustItemPrefix(element: MdListItemImpl, itemMarker: ASTNode?, taskItemMarker: ASTNode?, prefix: CharSequence, removePrefix: Boolean): CharSequence {
        return prefix
    }
}
