// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.plugin.util.ifElse
import com.vladsch.plugin.util.toRich

open class MdInlineGitLabMathImpl(node: ASTNode) : MdInlineStyleCompositeImpl(node), MdInlineGitLabMath {
    override fun getContent(): String {
        return contentElement?.text ?: ""
    }

    override fun getContentElement(): ASTNode? {
        return node.findChildByType(MdTypes.GITLAB_MATH_TEXT)
    }

    override fun setContent(content: String): PsiElement {
        // REFACTOR: factor out common part. This is almost identical to MdInlineCodeImpl
        val containingFile = containingFile
        val editContext = PsiEditAdjustment(containingFile)
        val paragraph = MdPsiImplUtil.getParagraphParent(this) ?: this
        val indentPrefix = MdPsiImplUtil.getBlockPrefixes(paragraph, null, editContext).finalizePrefixes(editContext).childContPrefix

        var convertedContent = content
        val newElementText = getElementText(MdFactoryContext(this), convertedContent).toRich()

        if (indentPrefix.isNotEmpty) {
            // re-indent if there is an indent ahead of our content on the line
            if (indentPrefix.isNotEmpty) {
                val contentLines = newElementText.split("\n", 0, BasedSequence.SPLIT_INCLUDE_DELIMS)
                val sb = StringBuilder()
                val firstIndentPrefix = BasedSequence.EMPTY
                var useIndentPrefix = firstIndentPrefix
                for (line in contentLines) {
                    sb.append(useIndentPrefix)
                    useIndentPrefix = indentPrefix
                    sb.append(line)
                }
                convertedContent = sb.toString()
            }

            // we have no choice but to replace the element textually, because with all possible prefix combinations
            // it will not parse, so we have to create a file with contents of the top parent element that contains us
            // the easiest way is to just take the whole PsiFile and replace our content in it
            val file = containingFile.originalFile
            val fileText: String = file.text

            val changedText = fileText.substring(0, this.node.startOffset) +
                convertedContent +
                fileText.substring(this.node.startOffset + this.textLength, fileText.length)

            val factoryContext = MdFactoryContext(this)
            val newFile = MdElementFactory.createFile(factoryContext, changedText)

            val psiElement = newFile.findElementAt(node.startOffset)?.parent
            if (psiElement is MdInlineGitLabMath) {
                // NOTE: replacing the content node leaves the element unchanged and preserves the injection all the time
                //    if the element is changed, injection may be lost and will have to be initiated again by the user.
                node.replaceAllChildrenToChildrenOf(psiElement.getNode())
            }
        } else {
            MdPsiImplUtil.setContent(this, convertedContent)
        }
        return this
    }

    override fun getContentRange(inDocument: Boolean): TextRange {
        val contentElement = contentElement
        return if (contentElement != null) inDocument.ifElse(contentElement.textRange, TextRange(0, contentElement.textLength)).shiftRight(2)
        else TextRange.EMPTY_RANGE
    }

    override fun isValidHost(): Boolean {
        return isValid
    }

    override fun updateText(text: String): MdPsiLanguageInjectionHost? {
        return ElementManipulators.handleContentChange(this, text)
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out MdPsiLanguageInjectionHost?> {
        return object : LiteralTextEscaper<MdPsiLanguageInjectionHost?>(this) {
            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                outChars.append(rangeInsideHost.shiftLeft(2).substring((myHost as MdInlineGitLabMathImpl).contentElement!!.text))
                return true
            }

            override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
                return rangeInsideHost.startOffset + offsetInDecoded
            }

            override fun getRelevantTextRange(): TextRange {
                return contentRange
            }

            override fun isOneLine(): Boolean {
                return true
            }
        }
    }

    companion object {
        fun getElementText(factoryContext: MdFactoryContext, content: String): String {
            return "\$`$content`\$"
        }
    }
}
