// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.psi.element.MdInlineStrikethrough
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.PegdownExtensions

class ToggleStrikeThroughAction : BaseToggleStateAction() {
    override fun getStyleElementClass(): Class<out PsiElement> = MdInlineStrikethrough::class.java

    override fun getParserOptionName(): String = MdBundle.message("settings.strikethrough.label")
    override fun isParserEnabled(renderingProfile: MdRenderingProfile): Boolean {
        return renderingProfile.parserSettings.pegdownFlags and PegdownExtensions.STRIKETHROUGH.flags != 0
    }

    override fun getBoundString(psiFile: PsiFile, text: CharSequence, selectionStart: Int, selectionEnd: Int, forInsertion: Boolean): String {
        return if (forInsertion) "~~" else text.subSequence(selectionStart, selectionStart + 2).toString()
    }
}
