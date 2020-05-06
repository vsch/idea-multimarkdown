// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util

import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.plugin.util.TestUtils

data class TextContext(
    val prefix: BasedSequence,              // prefix for completion
    val replacementOffset: Int,             // end position for id to set for completion context
    val beforeStartChar: Char,              // character before start marker or '\0' if marker at start of text
    val beforeStartChars: BasedSequence,    // characters before start marker
    val afterEndChar: Char,                 // character after endPos or '\0' if at end of text
    val afterCaretChar: Char,               // character after caret position or '\0' if at end of text
    val afterCaretChars: BasedSequence,     // characters after caret position
    val hasEndMarker: Boolean               // true if afterChar == ':'
) {

    fun Char?.asValue(): String = if (this == null) "null" else if (this == TestUtils.NULL_CHAR) "NULL_CHAR" else "'$this'"
    fun CharSequence?.asValue(): String = if (this == null) "null" else "\"$this\""

    override fun toString(): String {
        return """TextContext(
  prefix=${prefix.asValue()}
, replacementOffset = $replacementOffset
, beforeStartChar = ${beforeStartChar.asValue()}
, beforeStartChars = ${beforeStartChars.asValue()}
, afterEndChar = ${afterEndChar.asValue()}
, afterCaretChar = ${afterCaretChar.asValue()}
, afterCaretChars = ${afterCaretChars.asValue()}
, hasEndMarker = $hasEndMarker
)"""
    }
}

