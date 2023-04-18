// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.SequenceUtils
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.plugin.util.min
import com.vladsch.plugin.util.psi.isTypeOf

class AdjustingDocumentPosition(val offset: Int) {
    var adjustedOffset: Int = offset
    var wasDeleted: Boolean = false
}

fun CharSequence.subSequenceOrEmpty(startIndex: Int, endIndex: Int): CharSequence {
    if (startIndex < 0 || startIndex >= this.length || startIndex >= endIndex) return BasedSequence.EMPTY
    return this.subSequence(startIndex, this.length.min(endIndex))
}

fun Char.isAlphaNumeric(): Boolean {
    return this.isLetterOrDigit()
}

fun CharSequence.isMirrorStopChar(index: Int): Boolean {
    var i = index
    var c: Char = ' '
    while (i in 0 until length) {
        c = this[i]
        if (c != '_') break
        i--
    }
    return !c.isAlphaNumeric() && c != '_'
}

fun CharSequence.isMirrored(other: CharSequence, mirrorChars: String, maxMirroring: Int): Int? {
    // return number of chars mirrored before encountering a stop char
    val revOther = other.reversed()
    val iMax = this.length.min(other.length, maxMirroring)
    for (i in 0 until iMax) {
        if (this[i] !in mirrorChars && revOther[i] !in mirrorChars) {
            return i
        } else if (this[i] !in mirrorChars || revOther[i] !in mirrorChars) {
            return null
        }
    }

    // reached the end
    if ((this.length <= iMax || this[iMax] !in mirrorChars) && (revOther.length <= iMax || revOther[iMax] !in mirrorChars)) {
        return iMax
    }
    return null
}

data class WrappingContext(
    val charSequence: CharSequence,
    val mainElement: PsiElement,
    val formatElement: PsiElement?,
    val startOffset: Int,
    val endOffset: Int,
    val firstPrefixStart: Int,
    val firstPrefixEnd: Int,
    val firstLine: Int,
    val lastLine: Int,
    val virtualLastLine: Int
) {

    fun withMainListItem(context: CaretContextInfo, preEditListItemElement: PsiElement? = null): WrappingContext? {
        // go up until mainElement is a list item
        var element = preEditListItemElement ?: mainElement
        while (!element.isTypeOf(MdTokenSets.LIST_ITEM_ELEMENT_TYPES)) {
            element = element.parent
            if (element is PsiFile) return null
        }

        return WrappingContext(
            charSequence,
            element,
            formatElement,
            startOffset,
            endOffset,
            context.offsetLineStart(context.postEditNodeStart(element.firstChild.node)) ?: firstPrefixStart,
            context.postEditNodeEnd(element.firstChild.node),
            firstLine,
            lastLine,
            virtualLastLine
        )
    }

    fun prefixText(): CharSequence {
        return charSequence.subSequence(firstPrefixStart, startOffset)
    }

    fun firstPrefixText(): CharSequence {
        return charSequence.subSequence(firstPrefixStart, firstPrefixEnd)
    }

    override fun toString(): String {
        return "WrappingContext(\n" +
            "charSequence=${SequenceUtils.toVisibleWhitespaceString(charSequence)}, \n" +
            "mainElement=$mainElement, \n" +
            "formatElement=$formatElement, \n" +
            "startOffset=$startOffset, \n" +
            "endOffset=$endOffset, \n" +
            "firstPrefixStart=$firstPrefixStart, \n" +
            "firstPrefixEnd=$firstPrefixEnd, \n" +
            "firstLine=$firstLine, \n" +
            "lastLine=$lastLine, \n" +
            "virtualLastLine=$virtualLastLine\n" +
            ")"
    }
}
