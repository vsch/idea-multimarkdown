// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.BasicTextMapElementTypeProvider
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.psi.util.TextMapElementType
import com.vladsch.plugin.util.ifElse

import icons.MdIcons
import javax.swing.Icon

//class MultiMarkdownJekyllFrontMatterBlockImpl(node: ASTNode) : ASTWrapperPsiElement(node), MultiMarkdownJekyllFrontMatterBlock {
class MdJekyllFrontMatterBlockImpl(stub: MdJekyllFrontMatterBlockStub?, nodeType: IStubElementType<MdJekyllFrontMatterBlockStub, MdJekyllFrontMatterBlock>?, node: ASTNode?) :
    MdStubPlainTextImpl<MdJekyllFrontMatterBlockStub>(stub, nodeType, node),
    MdJekyllFrontMatterBlock {

    constructor(stub: MdJekyllFrontMatterBlockStub, nodeType: IStubElementType<MdJekyllFrontMatterBlockStub, MdJekyllFrontMatterBlock>) : this(stub, nodeType, null)
    constructor(node: ASTNode) : this(null, null, node)

    override fun getTextMapType(): TextMapElementType {
        return BasicTextMapElementTypeProvider.JEKYLL_FRONT_MATTER
    }

    val referenceableTextType: IElementType = MdTypes.JEKYLL_FRONT_MATTER_BLOCK

    override fun getReferenceableOffsetInParent(): Int {
        return MdPlainTextStubElementType.getReferenceableOffsetInParent(node, referenceableTextType)
    }

    override fun getReferenceableText(): String {
        return content
    }

    override fun replaceReferenceableText(text: String, startOffset: Int, endOffset: Int): PsiElement {
        val content = content
        val sb = StringBuilder(content.length - (endOffset - startOffset) + text.length)
        sb.append(content, 0, startOffset).append(text).append(content, endOffset, content.length)
        return setContent(sb.toString())
    }

    override fun getContentElement(): ASTNode? = node.findChildByType(referenceableTextType)

    override fun getContent(): String {
        val content = getContentElement()
        return content?.text ?: ""
    }

    override fun setContent(blockText: String): PsiElement {
        return MdPsiImplUtil.setContent(this, blockText)
    }

    override fun getContentRange(inDocument: Boolean): TextRange {
        val startMarker = node.findChildByType(MdTypes.JEKYLL_FRONT_MATTER_OPEN)
        val content = node.findChildByType(referenceableTextType)
        if (startMarker != null && content != null) {
            return TextRange(startMarker.textLength + 1, startMarker.textLength + 1 + content.textLength).shiftRight(inDocument.ifElse(0, startMarker.startOffset))
        }
        return TextRange(0, 0)
    }

    override fun getPresentation(): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                if (!isValid) return null
                return "Jekyll front matter block"
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
            val useContent = content ?: ""

            if (useContent.isEmpty() || useContent[useContent.length - 1] != '\n') {
                return "---\n$useContent\n---\n"
            } else {
                return "---\n$useContent---\n"
            }
        }
    }
}
