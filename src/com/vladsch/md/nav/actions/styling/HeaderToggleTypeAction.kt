// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.psi.element.MdHeaderElement
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.plugin.util.rangeLimit

class HeaderToggleTypeAction : HeaderAction() {
    override fun canPerformAction(element: PsiElement): Boolean {
        // diagnostic/3144 caused by conversion of empty ATX to setext
        return element is MdHeaderElement && element.headerLevel <= 2 && element.headerTextElement != null
    }

    override fun cannotPerformActionReason(element: PsiElement): String {
        return when {
            element is MdHeaderElement && element.headerLevel > 2 -> "Cannot convert ATX heading level 3 or higher to Setext heading"
            // diagnostic/3144 caused by conversion of empty ATX to setext
            element is MdHeaderElement -> "Cannot convert ATX heading with empty heading text"
            else -> "Not heading element"
        }
    }

    override fun headerAction(element: PsiElement, document: Document, caretOffset: Int, editContext: PsiEditAdjustment): Int? {
        if (element is MdHeaderElement) {
            val offset = (caretOffset - element.headerTextElement!!.node.startOffset).rangeLimit(0, element.headerTextElement!!.node.textLength)
            return MdPsiImplUtil.changeHeaderType(element, null, document, offset, editContext)
        }
        return null
    }
}
