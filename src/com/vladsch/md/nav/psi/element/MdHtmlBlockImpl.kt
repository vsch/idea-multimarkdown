// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import icons.MdIcons
import javax.swing.Icon

class MdHtmlBlockImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdHtmlBlock {
    override fun getContent(): String {
        return text
    }

    override fun getContentElement(): ASTNode? {
        return this.node
    }

    override fun setContent(htmlBlock: String): PsiElement {
        return MdPsiImplUtil.setContent(this, htmlBlock)
    }

    override fun getContentRange(inDocument: Boolean): TextRange {
        return if (inDocument) textRange else TextRange(0, textLength)
    }

    override fun getPresentation(): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                if (!isValid) return null
                return "Html block"
            }

            override fun getLocationString(): String? {
                if (!isValid) return null
                // create a shortened version that is still good to look at
                return MdPsiImplUtil.truncateStringForDisplay(text, 50, false, true, true)
            }

            override fun getIcon(unused: Boolean): Icon? {
                return MdIcons.getDocumentIcon()
            }
        }
    }

    override fun isValidHost(): Boolean {
        return isValid
    }

    override fun updateText(text: String): MdPsiLanguageInjectionHost {
        return ElementManipulators.handleContentChange(this, text)
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("html-block")
    }

    override fun getBreadcrumbTooltip(): String? {
        return content
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out MdPsiLanguageInjectionHost> {
        return object : LiteralTextEscaper<MdPsiLanguageInjectionHost>(this) {
            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                outChars.append(rangeInsideHost.substring(myHost.text))
                return true
            }

            override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
                return rangeInsideHost.startOffset + offsetInDecoded
            }

            override fun getRelevantTextRange(): TextRange {
                return contentRange
            }

            override fun isOneLine(): Boolean {
                return false
            }
        }
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getElementText(factoryContext: MdFactoryContext, content: String?): String {
            return content ?: ""
        }
    }
}
