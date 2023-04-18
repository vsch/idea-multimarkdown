// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.psi.element.MdInlineBold
import com.vladsch.md.nav.psi.element.MdInlineItalic
import com.vladsch.md.nav.settings.MdApplicationSettings

class ToggleItalicAction : BaseToggleStateAction() {
    override fun getStyleElementClass(): Class<out PsiElement> = MdInlineItalic::class.java
    override fun wrapsStyle(element: PsiElement): Boolean {
        return element is MdInlineBold || super.wrapsStyle(element)
    }

    override fun getBoundString(psiFile: PsiFile, text: CharSequence, selectionStart: Int, selectionEnd: Int, forInsertion: Boolean): String {
        return if (forInsertion) if (!MdApplicationSettings.instance.documentSettings.asteriskItalics && isWord(text, selectionStart, selectionEnd)) "_" else "*" else text[selectionStart].toString()
    }

    private fun isWord(text: CharSequence, from: Int, to: Int): Boolean {
        return (from <= 0 || from >= text.length || !Character.isLetterOrDigit(text[from - 1])) && (to >= text.length || !Character.isLetterOrDigit(text[to]))
    }
}
