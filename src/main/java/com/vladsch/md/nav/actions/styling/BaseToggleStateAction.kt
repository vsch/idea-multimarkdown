// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.containers.ContainerUtil
import com.vladsch.ReverseRegEx.util.ReversedCharSequence
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder
import com.vladsch.md.nav.actions.styling.util.MdActionUtil
import com.vladsch.md.nav.psi.element.MdInlineCode
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.plugin.util.maxLimit
import com.vladsch.plugin.util.minLimit

abstract class BaseToggleStateAction : ToggleAction(), DumbAware {

    protected open fun getStyleElementCondition(): Condition<PsiElement> = Condition { element -> getStyleElementClass().isInstance(element) }

    protected open fun isNestable(): Boolean = false
    protected open fun getParserOptionName(): String = ""
    protected open fun isParserEnabled(renderingProfile: MdRenderingProfile): Boolean = true

    protected open fun wrappedByStyle(element: PsiElement): Boolean = false
    protected open fun wrapsStyle(element: PsiElement): Boolean = element is MdInlineCode

    protected abstract fun getStyleElementClass(): Class<out PsiElement>
    protected abstract fun getBoundString(psiFile: PsiFile, text: CharSequence, selectionStart: Int, selectionEnd: Int, forInsertion: Boolean): String

    /**
     * Adjust the range of text to be wrapped to the preceding or next word if range is empty and at the end or begining of the word
     * @param charSequence character sequence
     * @param range        default selection range
     * @return range to wrap
     */
    private fun adjustWrappedRangeAtStartOrEndOfWord(charSequence: CharSequence, range: TextRange): TextRange {
        if (range.isEmpty) {
            var pos = range.startOffset
            val punctuations = MdApplicationSettings.instance.documentSettings.toggleStylePunctuations + " \t\n"
            if (pos > 0 && (pos >= charSequence.length || punctuations.indexOf(charSequence[pos]) != -1 && !Character.isWhitespace(charSequence[pos - 1]))) {
                pos--
                val rangeEnd = if (pos >= charSequence.length || punctuations.indexOf(charSequence[pos]) != -1) pos else range.endOffset
                while (pos > 0 && !Character.isWhitespace(charSequence[pos])) pos--
                if (Character.isWhitespace(charSequence[pos])) pos++
                return TextRange(pos.maxLimit(rangeEnd), rangeEnd)
            } else if (pos < charSequence.length && !Character.isWhitespace(charSequence[pos]) && (pos == 0 || Character.isWhitespace(charSequence[pos - 1]))) {
                while (pos < charSequence.length && !Character.isWhitespace(charSequence[pos])) pos++
                while (pos > 0 && punctuations.indexOf(charSequence[pos - 1]) != -1) pos--
                return TextRange(range.startOffset, pos.minLimit(range.startOffset))
            }
        }
        return range
    }

    private fun getCommonParentOfType(psiFile: PsiFile, caret: Caret, isNestable: Boolean): PsiElement? {
        val range = getAdjustedCaretRange(caret)
        return MdActionUtil.getCommonParentOfType(psiFile, range, isNestable, getStyleElementCondition())
    }

    private fun getCommonState(psiFile: PsiFile, caret: Caret, isNestable: Boolean): SelectionState {
        val parentOfType = getCommonParentOfType(psiFile, caret, isNestable)
        return if (parentOfType == null)
            SelectionState.NO
        else
            SelectionState.YES
    }

    private fun getAdjustedCaretRange(caret: Caret): TextRange {
        val range: TextRange
        if (caret.hasSelection()) {
            range = TextRange(caret.selectionStart, caret.selectionEnd)
        } else {
            val rangeEnd: Int
            var pos = caret.selectionStart
            val charSequence = caret.editor.document.charsSequence
            val punctuations = MdApplicationSettings.instance.documentSettings.toggleStylePunctuations + " \t\n"
            if (pos > 0 && pos < charSequence.length && (punctuations.indexOf(charSequence[pos]) != -1 && !Character.isWhitespace(charSequence[pos - 1]))) {
                pos--
                while (pos > 0 && punctuations.indexOf(charSequence[pos]) != -1) pos--
                if (punctuations.indexOf(charSequence[pos]) == -1) pos++
                rangeEnd = if (pos >= charSequence.length || punctuations.indexOf(charSequence[pos]) != -1) pos else caret.selectionStart
            } else {
                rangeEnd = caret.selectionStart
            }

            range = TextRange(rangeEnd, rangeEnd)
        }
        return range
    }

    override fun update(e: AnActionEvent) {
        MdActionUtil.getConditionBuilder(e, this) { it, (_, editor, psiFile) ->
            it.and(isParserEnabled(MdRenderingProfileManager.getProfile(psiFile)), getParserOptionName() + " parser extension is disabled")
                .and { haveSelectedCarets(it, editor, psiFile) }
        }.doneToggleable()
    }

    override fun isSelected(e: AnActionEvent): Boolean {
        return MdActionUtil.getConditionBuilder(e, this) { it, (_, editor, psiFile) ->
            it.and { haveSelectedCarets(it, editor, psiFile) }
        }.done().isSelected
    }

    private fun haveSelectedCarets(conditionBuilder: DisabledConditionBuilder, editor: Editor, psiFile: PsiFile) {
        conditionBuilder
            .and {
                var lastState: SelectionState? = null
                val isNestable = !isNestable()

                for (caret in editor.caretModel.allCarets) {
                    val state = getCommonState(psiFile, caret, isNestable)
                    if (lastState == null) {
                        lastState = state
                    } else if (lastState != state) {
                        lastState = SelectionState.INCONSISTENT
                        break
                    }
                }

                it.and(lastState != SelectionState.INCONSISTENT, "Inconsistent context for multiple carets")
                it.and {
                    it.isSelected = lastState == SelectionState.YES
                }
            }
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        MdActionUtil.getProjectEditorPsiFile(e)?.let { (_, editor, psiFile) ->
            WriteCommandAction.runWriteCommandAction(psiFile.project) {
                val document = editor.document
                val isNestable = !isNestable()

                for (caret in ContainerUtil.reverse<Caret>(editor.caretModel.allCarets)) {
                    if (!state) {
                        val closestElement = getCommonParentOfType(psiFile, caret, isNestable)
                        if (closestElement == null) {
                            LOG.warn("Could not find enclosing element for removal")
                            continue
                        }

                        removeMarkersFromSelection(psiFile, document, caret, closestElement)
                    } else {
                        if (!caret.hasSelection()) {
                            val pos = caret.selectionStart
                            val boundString = getBoundString(psiFile, "", pos, pos, true)
                            val charSequence = BasedSequence.of(document.charsSequence)
                            val length = boundString.length
                            if (pos > length && charSequence.matchCharsReversed(boundString, pos) && charSequence.matchChars(boundString, pos)) {
                                document.deleteString(pos - length, pos + length)
                                continue
                            }
                        }
                        addMarkersToSelection(psiFile, document, caret)
                    }
                }

                PsiDocumentManager.getInstance(psiFile.project).commitDocument(document)
            }
        }
    }

    private fun removeMarkersFromSelection(psiFile: PsiFile, document: Document, caret: Caret, element: PsiElement) {
        val markerText = getBoundString(psiFile, document.charsSequence, element.node.startOffset, element.node.startOffset + element.node.textLength, false)
        val leadMarker = element.firstChild.node

        if (markerText == leadMarker.text) {
            val trailMarker = element.lastChild.node
            val haveTrailMarker = markerText == trailMarker.text

            val range =
                if (haveTrailMarker)
                    TextRange(leadMarker.startOffset + leadMarker.textLength, trailMarker.startOffset)
                else
                    TextRange(leadMarker.startOffset + leadMarker.textLength, trailMarker.startOffset + trailMarker.textLength)

            caret.setSelection(range.startOffset, range.endOffset)

            document.deleteString(trailMarker.startOffset, trailMarker.startOffset + trailMarker.textLength)
            document.deleteString(leadMarker.startOffset, leadMarker.startOffset + leadMarker.textLength)

            // see if we can skip the selection
            //    FIX: should be checking if without selection at current caret position the toggled text would be the same as current selection
            val newRange = adjustWrappedRangeAtStartOrEndOfWord(document.charsSequence, getAdjustedCaretRange(caret))
            if (newRange.startOffset == caret.selectionStart && newRange.endOffset == caret.selectionEnd) {
                caret.setSelection(caret.offset, caret.offset)
            }
        } else {
            LOG.warn("Could not find bound string from found node")
        }
    }

    private fun addMarkersToSelection(psiFile: PsiFile, document: Document, caret: Caret) {
        val charSequence = document.charsSequence
        var range = getAdjustedCaretRange(caret)
        var virtualSpaces = caret.logicalPosition.column - (caret.offset - document.getLineStartOffset(document.getLineNumber(caret.offset)))
        var adjustedRange = false

        if (virtualSpaces == 0 && range.endOffset <= document.textLength &&
            (range.endOffset < document.textLength && !Character.isWhitespace(document.charsSequence[range.endOffset])
                || range.endOffset > 0 && !Character.isWhitespace(document.charsSequence[range.endOffset - 1]))) {
            // not after end of line or sitting on space, can adjust the range
            range = adjustWrappedRangeAtStartOrEndOfWord(charSequence, range)
            adjustedRange = true
        }

        val boundString = getBoundString(psiFile, charSequence, range.startOffset, range.endOffset, true)
        val wasAtStart = caret.offset == range.startOffset
        val wasAtEnd = caret.offset == range.endOffset

        // in general the bound string at end should be reversed since the bound string is not guaranteed to consist of the same characters (applies to GitLab inline math)
        document.insertString(range.endOffset, ReversedCharSequence.of(boundString))
        document.insertString(range.startOffset, boundString)
        if (virtualSpaces > 0 && range.isEmpty) {
            document.insertString(range.startOffset, StringUtil.repeat(" ", virtualSpaces))
            if (caret.offset > range.startOffset) virtualSpaces = 0
        }

        if (range.isEmpty || adjustedRange) {
            val endAdjustment = boundString.length
            if (caret.offset <= range.endOffset + endAdjustment || range.isEmpty) caret.moveToOffset(caret.offset + virtualSpaces + endAdjustment)
        } else {
            if (wasAtStart) caret.moveToOffset(caret.selectionStart)
            if (wasAtEnd) caret.moveToOffset(caret.selectionEnd + boundString.length)
        }
    }

    enum class SelectionState {
        YES,
        NULL,
        NO,
        INCONSISTENT
    }

    class SelectionStateWithReason private constructor(val state: SelectionState, val reason: String? = null) {
        companion object {
            val YES: SelectionStateWithReason = SelectionStateWithReason(SelectionState.YES)
            val NULL: SelectionStateWithReason = SelectionStateWithReason(SelectionState.NULL)
            val INCONSISTENT: SelectionStateWithReason = SelectionStateWithReason(SelectionState.INCONSISTENT)
            @Suppress("FunctionName")
            fun NO(reason: String): SelectionStateWithReason = SelectionStateWithReason(SelectionState.NO, reason)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) {
                return false
            }

            other as SelectionStateWithReason

            if (state != other.state) return false

            return true
        }

        override fun hashCode(): Int {
            return state.hashCode()
        }
    }

    companion object {
        val TRUE: Condition<PsiElement> = Condition<PsiElement> { true }
        internal val LOG = Logger.getInstance(BaseToggleStateAction::class.java)
    }
}
