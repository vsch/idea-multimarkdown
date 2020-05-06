// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util

import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.plugin.util.*
import java.util.regex.Pattern

/**
 * NOTE: would effing nice to put some description of parameters, just to jog the memory six months later when this code is completely forgotten.
 */
abstract class MdCompletionContext(
    private val prefixChar: Char = TestUtils.NULL_CHAR,
    private val prefixChars: String = "$prefixChar \t",
    private val endMarkerChars: String = prefixChars,
    private val replacementEnd: Pattern = REPLACEMENT_END
) {

    companion object {
        private val REPLACEMENT_END: Pattern = Pattern.compile("[^\\w]")
    }

    abstract fun wantParams(params: TextContext, isDefault: Boolean, isAutoPopup: Boolean): Boolean

    fun getContext(startOffset: Int, element: PsiElement, wantDefaultContext: Boolean): TextContext? {
        val rawElementText = element.text
        val textOffset = element.textOffset
        return getContext(rawElementText, textOffset, startOffset, wantDefaultContext)
    }

    fun getContext(elementText: CharSequence, textOffset: Int, startOffset: Int, wantDefaultContext: Boolean): TextContext? {
        val cleanElementText = BasedSequence.of(elementText).replace(TestUtils.DUMMY_IDENTIFIER, "")
        val textLength = cleanElementText.length
        val caretPos = (startOffset - textOffset).indexOrNull() ?: return null // caret pos in element text

        if (wantDefaultContext) {
            return TextContext(
                cleanElementText.subSequence(0, caretPos),
                textOffset + textLength,
                TestUtils.NULL_CHAR,
                BasedSequence.NULL,
                TestUtils.NULL_CHAR,
                TestUtils.NULL_CHAR,
                BasedSequence.NULL,
                true
            )
        }

        val afterCaretPos = caretPos
        val hasPrefixChar = prefixChar != TestUtils.NULL_CHAR
        val prefixCharsSet = CharPredicate.anyOf(prefixChars)

        val prefixCharPos = cleanElementText.lastIndexOfAny(prefixCharsSet, caretPos - 1).indexOrNull()
        if (hasPrefixChar && (prefixCharPos == null || cleanElementText[prefixCharPos] != prefixChar)) return null

        val beforePrefixCharPos = hasPrefixChar.ifElse((prefixCharPos ?: 0) - 1, (prefixCharPos ?: -1)).indexOrNull()
        val afterPrefixCharPos = hasPrefixChar.ifElse((prefixCharPos ?: 0) + 1, (prefixCharPos ?: -1) + 1).maxLimit(textLength)

        // diagnostic/4143, caretPos
        val matcher = replacementEnd.matcher(cleanElementText.subSequence(caretPos.rangeLimit(0, cleanElementText.length)))
        val endCharPos = if (matcher.find()) caretPos + matcher.start() else textLength

        val prefix = cleanElementText.subSequence(afterPrefixCharPos, caretPos)
        val beforeStartChar = if (beforePrefixCharPos == null) TestUtils.NULL_CHAR else cleanElementText[beforePrefixCharPos]
        val beforeStartChars = cleanElementText.subSequence(0, beforePrefixCharPos?.plus(1) ?: 0)
        val afterCaretChar = if (afterCaretPos == textLength) TestUtils.NULL_CHAR else cleanElementText[afterCaretPos]
        val afterCaretChars = cleanElementText.subSequence(afterCaretPos)
        val afterEndChar = if (endCharPos == textLength) TestUtils.NULL_CHAR else cleanElementText[endCharPos]

        return TextContext(
            prefix,
            textOffset + endCharPos,
            beforeStartChar,
            beforeStartChars,
            afterEndChar,
            afterCaretChar,
            afterCaretChars,
            endMarkerChars.indexOf(afterEndChar) != -1
        )
    }
}
