// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.psi.element.MdInlineCode
import com.vladsch.md.nav.psi.element.MdInlineStyleElement
import com.vladsch.md.nav.settings.MdApplicationSettings

class ToggleCodeSpanAction : BaseToggleStateAction() {
    override fun getStyleElementClass(): Class<out PsiElement> = MdInlineCode::class.java
    override fun isNestable(): Boolean = !MdApplicationSettings.instance.documentSettings.codeLikeStyleToggle
    override fun wrappedByStyle(element: PsiElement): Boolean {
        return element is MdInlineStyleElement && element !is MdInlineCode
    }

    override fun getBoundString(psiFile: PsiFile, text: CharSequence, selectionStart: Int, selectionEnd: Int, forInsertion: Boolean): String {
        if (forInsertion) return "`"

        var maxBackTickSequenceSeen = 0
        var curBackTickSequence = 0
        for (i in selectionStart until selectionEnd) {
            if (text[i] != '`') {
                curBackTickSequence = 0
            } else {
                curBackTickSequence++
                maxBackTickSequenceSeen = Math.max(maxBackTickSequenceSeen, curBackTickSequence)
            }
        }

        return StringUtil.repeat("`", maxBackTickSequenceSeen)
    }
}
