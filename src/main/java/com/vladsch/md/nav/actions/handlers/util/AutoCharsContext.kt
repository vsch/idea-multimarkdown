// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers.util

import com.vladsch.plugin.util.maxLimit
import com.vladsch.plugin.util.psi.isTypeOf

class AutoCharsContext(val context: CaretContextInfo, val mirrorCount: Int) {
    fun canAutoType(char: Char): Boolean {
        return (CaretContextInfo.isMirrorChar(char) && mirrorCount < context.maxMirrored(char)) || char in " \t"
    }

    fun isAutoTypeEnabled(autoTypChar: Char?): Boolean {
        return CaretContextInfo.isMirrorChar(autoTypChar)
    }

    fun autoTypeChar(char: Char): Int {
        if (CaretContextInfo.isMirrorChar(char)) {
            context.document.insertString(context.caretOffset, char.toString())
            // highlight it, or better yet low lite it
            return 1
        } else if (char in " \t" && mirrorCount > 0) {
            // remove one inserted smart char
            val maxRemoved = mirrorCount.maxLimit(context.maxMirrored(char))
            context.document.deleteString(context.caretOffset, context.caretOffset + maxRemoved)
            return -maxRemoved
        }
        return 0
    }

    fun autoBackspaceChar(): Int {
        if (mirrorCount > 0) {
            // remove one inserted smart char
            context.document.deleteString(context.caretOffset, context.caretOffset + 1)
            return -1
        }
        return 0
    }

    companion object {
        @JvmStatic
        fun getContext(context: CaretContextInfo): AutoCharsContext? {
            // auto chars on if before cursor has a run of *_~ starting at start of line or a non-alphanumeric, underscore or dash characters, and after cursor has the same run ending in a space or end of line
            if (context.mirroredCount >= 0) {
                val element = context.findElementAt(context.caretOffset)
                if (element == null || element.isTypeOf(context.TEXT_SET)) {
                    return AutoCharsContext(context, context.mirroredCount)
                }
            }

            return null
        }
    }
}
