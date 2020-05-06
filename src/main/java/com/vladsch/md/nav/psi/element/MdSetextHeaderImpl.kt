// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.sequence.RepeatedSequence
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings

open class MdSetextHeaderImpl(node: ASTNode) : MdHeaderElementImpl(node), MdSetextHeader {

    override fun getHeaderLevel(): Int {
        val marker = node.findChildByType(MdTypes.HEADER_SETEXT_MARKER)!!
        return if (marker.chars.trimStart(' ', '>', '|')[0] == '=') 1 else 2
    }

    override fun getCanIncreaseLevel(): Boolean {
        return headerLevel < 2
    }

    override fun getCanDecreaseLevel(): Boolean {
        return headerLevel > 1
    }

    override fun setHeaderLevel(level: Int, editContext: PsiEditContext): MdHeaderElement? {
        assert(level in 1 .. 2)
        return MdPsiImplUtil.setHeaderLevel(this, level, trailingAttributesLength, editContext)
    }

    override fun getHeaderMarkerNode(): ASTNode {
        val marker = node.findChildByType(MdTypes.HEADER_SETEXT_MARKER)!!
        return marker
    }

    override fun getHeaderTextElement(): MdHeaderText? {
        val headerText = (MdPsiImplUtil.findChildByType(this, MdTypes.HEADER_TEXT) as MdHeaderText?)
        return headerText
    }

    override fun getHeaderText(): String {
        return headerTextElement?.text ?: ""
    }

    override fun getHeaderTextNoFormatting(): String {
        return headerText
    }

    override fun getHeaderMarker(): String {
        return headerMarkerNode.text
    }

    override fun getBreadcrumbInfo(): String {
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText && !node.text.isEmpty()) {
            val prefix = "#".repeat(headerLevel) + " "
            val truncateStringForDisplay = MdPsiImplUtil.truncateStringForDisplay(prefix + node.text, settings.maxBreadcrumbText, false, true, true)
            if (!truncateStringForDisplay.isEmpty()) return truncateStringForDisplay
        }
        return MdPsiBundle.message("header", headerLevel.toString())
    }

    override fun setName(newName: String, reason: Int): PsiElement {
        return this
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, text: CharSequence, level: Int, trailingAttributes: Int): CharSequence {
            assert(level in 1 .. 2)
            // diagnostic/2599
            assert(trailingAttributes <= text.length)
            // diagnostic/3144 caused by conversion of empty ATX to setext
            assert(text.isNotEmpty())

            val strippedString = text.substring(0, text.length - trailingAttributes).trim()
            return if (strippedString.isEmpty()) {
                text.toString() + "\n" + RepeatedSequence.repeatOf(if (level == 1) '=' else '-', if (text.length > 3) text.length else 3) + "\n"
            } else {
                text.toString() + "\n" + RepeatedSequence.repeatOf(if (level == 1) '=' else '-', if (strippedString.length > 3) strippedString.length else 3) + "\n"
            }
        }
    }
}
