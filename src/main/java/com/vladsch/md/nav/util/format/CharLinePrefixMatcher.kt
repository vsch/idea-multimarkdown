// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format

import com.vladsch.md.nav.actions.handlers.util.PsiEditContext

class CharLinePrefixMatcher(val prefixChar: Char) : LinePrefixMatcher {
    override fun contentColumn(lineChars: CharSequence, indentColumn: Int, editContext: PsiEditContext): Int {
        var column = indentColumn
        val pos = lineChars.indexOf(prefixChar)
        if (pos in 0 .. 4 && lineChars.substring(0, pos).isBlank()) {
            column += pos + 1
            if (lineChars.length > pos + 1 && (lineChars[pos + 1] == ' ' || lineChars[pos + 1] == '\t')) {
                column++
            }
        }

        return column
    }
}
