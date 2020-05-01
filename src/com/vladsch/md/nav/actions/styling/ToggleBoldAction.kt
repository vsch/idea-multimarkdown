// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.psi.element.MdInlineBold
import com.vladsch.md.nav.psi.element.MdInlineItalic

class ToggleBoldAction : BaseToggleStateAction() {
    override fun getStyleElementClass(): Class<out PsiElement> = MdInlineBold::class.java
    override fun wrappedByStyle(element: PsiElement): Boolean {
        return element is MdInlineItalic
    }

    override fun getBoundString(psiFile: PsiFile, text: CharSequence, selectionStart: Int, selectionEnd: Int, forInsertion: Boolean): String {
        return if (forInsertion) "**" else text.subSequence(selectionStart, selectionStart + 2).toString()
    }
}
