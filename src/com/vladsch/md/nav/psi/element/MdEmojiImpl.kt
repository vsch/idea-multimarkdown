// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings

class MdEmojiImpl(node: ASTNode) : MdCompositeImpl(node), MdEmoji {

    override fun getEmojiIdText(): String {
        val emojiIdentifier = emojiIdentifier
        val emojiText = emojiIdentifier?.text
        return emojiText ?: ""
    }

    override fun getEmojiIdentifier(): MdEmojiId? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.EMOJI_ID) as MdEmojiId?
    }

    override fun getBreadcrumbInfo(): String {
        val settings = MdApplicationSettings.instance.documentSettings
        if (settings.showBreadcrumbText && !node.text.isEmpty()) {
            val truncateStringForDisplay = MdPsiImplUtil.truncateStringForDisplay(node.text, settings.maxBreadcrumbText, false, true, true)
            if (!truncateStringForDisplay.isEmpty()) return truncateStringForDisplay
        }
        return MdPsiBundle.message("emoji")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, name: String): String {
            return ":$name:"
        }
    }
}
