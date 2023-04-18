// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.psi.element.MdInlineUnderline
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.settings.PegdownExtensions

class ToggleUnderlineAction : BaseToggleStateAction() {
    override fun getStyleElementClass(): Class<out PsiElement> = MdInlineUnderline::class.java

    override fun getParserOptionName(): String = MdBundle.message("settings.inserted.label")
    override fun isParserEnabled(renderingProfile: MdRenderingProfile): Boolean {
        return renderingProfile.parserSettings.pegdownFlags and PegdownExtensions.INSERTED.flags != 0
    }

    override fun getBoundString(psiFile: PsiFile, text: CharSequence, selectionStart: Int, selectionEnd: Int, forInsertion: Boolean): String {
        return if (forInsertion) "++" else text.subSequence(selectionStart, selectionStart + 2).toString()
    }
}
