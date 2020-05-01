// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.settings.MdApplicationSettings

open class MdBreadcrumbCompositeImpl(node: ASTNode, breadcrumbInfo: String, showText: Boolean) : MdCompositeImpl(node), MdBreadcrumbElement {
    val myBreadCrumbInfo: String = breadcrumbInfo
    val myShowText: Boolean = showText

    override fun getBreadcrumbInfo(): String {
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText && !node.text.isEmpty() && myShowText) {
            val truncateStringForDisplay = MdPsiImplUtil.truncateStringForDisplay(node.text, settings.maxBreadcrumbText, false, true, true)
            if (!truncateStringForDisplay.isEmpty()) return truncateStringForDisplay
        }
        return myBreadCrumbInfo
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }
}
