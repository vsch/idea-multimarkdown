// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.sequence.LineAppendable
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.util.format.CharLinePrefixMatcher
import com.vladsch.md.nav.util.format.LinePrefixMatcher

import icons.MdIcons
import javax.swing.Icon

class MdBlockQuoteImpl(node: ASTNode) : MdIndentingCompositeImpl(node), MdBlockQuote, MdStructureViewPresentableElement, MdStructureViewPresentableItem, MdBreadcrumbElement {
    companion object {
        @JvmField
        val INDENT_PREFIX = "> "

        private val INDENT_PREFIX_MATCHER = CharLinePrefixMatcher('>')
    }

    override fun isTextStart(node: ASTNode): Boolean {
        return node !== node.firstChildNode
    }

    override fun getPrefixMatcher(editContext: PsiEditContext): LinePrefixMatcher {
        return INDENT_PREFIX_MATCHER
    }

    override fun removeLinePrefix(lines: LineAppendable, indentColumns: IntArray, isFirstChild: Boolean, editContext: PsiEditContext) {
        removeLinePrefix(lines, indentColumns, false, editContext, INDENT_PREFIX_MATCHER, 0)
    }

    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.BLOCK_QUOTE
    }

    override fun getPresentableText(): String? {
        return MdPsiBundle.message("block-quote")
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdPsiImplUtil.getPresentation(this)
    }

    fun getBlockQuoteNesting(): Int {
        var nesting = 1
        var parent = parent
        while (parent is MdBlockQuote) {
            nesting++
            parent = parent.parent
        }
        return nesting
    }

    override fun getBreadcrumbInfo(): String {
        //        val message = PsiBundle.message("block-quote")
        //        val nesting = getBlockQuoteNesting()
        //        return if (nesting > 1) "$message ×$nesting" else message
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText) {
            return ">"
        }
        return MdPsiBundle.message("block-quote")
    }

    override fun getBreadcrumbTooltip(): String? {
        return null
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }
}
