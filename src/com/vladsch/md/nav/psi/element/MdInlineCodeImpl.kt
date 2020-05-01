// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.LineAppendableImpl
import com.vladsch.flexmark.util.sequence.RepeatedSequence
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.util.MdIndentConverter
import com.vladsch.plugin.util.*
import icons.MdIcons
import javax.swing.Icon

class MdInlineCodeImpl(node: ASTNode) : MdInlineStyleCompositeImpl(node), MdInlineCode {
    override fun getContentElement(): ASTNode? {
        return node.firstChildNode?.treeNext
    }

    override fun getContent(): String {
        return contentElement?.text ?: ""
    }

    override fun getContentRange(inDocument: Boolean): TextRange {
        val contentElement = contentElement
        return if (contentElement != null) inDocument.ifElse(contentElement.textRange, TextRange(0, contentElement.textLength)).shiftRight(contentElement.startOffset - textOffset)
        else TextRange.EMPTY_RANGE
    }

    override fun setContent(content: String): PsiElement {
        // REFACTOR: factor out common part. This is almost identical to MdInlineGitLabMathImpl
        val containingFile = containingFile
        val paragraph = MdPsiImplUtil.getParagraphParent(this) ?: this
        val editContext = PsiEditAdjustment(containingFile)
        val indentPrefix = MdPsiImplUtil.getBlockPrefixes(paragraph, null, editContext).finalizePrefixes(editContext).childContPrefix

        val markers = firstChild?.text ?: "`"
        var convertedContent = content.toRich().trim(CharPredicate.WHITESPACE).toString()
        val newElementText = getElementText(MdFactoryContext(this), convertedContent, markers).toBased()
        val newMarkers = newElementText.countTrailing(CharPredicate.anyOf('`'))

        if (indentPrefix.isNotEmpty()) {
            // re-indent if there is an indent ahead of our content on the line
            if (indentPrefix.isNotEmpty()) {
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

            val elementOffset = (newMarkers - markers.length) + if (newElementText.startsWith(NBSP_PREFIX)) {
                val prevSibling: PsiElement? = this.prevSibling
                if (prevSibling is LeafPsiElement && prevSibling.getNode().elementType === MdTypes.HTML_ENTITY) 0 // already had &nbsp; no change in offset
                else NBSP_PREFIX.length // &nbsp; added, look for it offset by string length
            } else 0

            val psiElement = newFile.findElementAt(node.startOffset + elementOffset)?.parent
            if (psiElement is MdInlineCode) {
                MdPsiImplUtil.replaceInlineCodeContent(this, psiElement)
            }
        } else {
            MdPsiImplUtil.setContent(this, convertedContent)
        }
        return this
    }

    override fun getPresentation(): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return if (!isValid) null else "Inline code"
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
        val thizz = this

        return object : LiteralTextEscaper<MdPsiLanguageInjectionHost>(this) {
            var converter: MdIndentConverter? = null

            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                if (converter == null) {
                    val contentNode = contentElement ?: return false

                    val contentLines = LineAppendableImpl(0).append(contentNode.text)
                    val paragraph = MdPsiImplUtil.getParagraphParent(thizz) ?: thizz
                    MdPsiImplUtil.adjustLinePrefix(paragraph, contentLines, PsiEditAdjustment(containingFile))

                    // un-indent if needed
                    // indent prefix are the characters from the beginning of line to the start of content
                    var startOffsetInParent = rangeInsideHost.startOffset
                    val leadingBlanks: Int

                    // conversion must start with no prefix on the first line!!!!
                    if (contentLines.lineCount > 0) contentLines.setLine(0, "", contentLines[0].text)

                    if (startOffsetInParent > 0 && contentLines.lineCount > 0) {
                        leadingBlanks = contentLines[0].line.countLeading(CharPredicate.SPACE_TAB).maxLimit(startOffsetInParent)
                        contentLines.setLine(0, "", contentLines[0].line.subSequence(leadingBlanks, contentLines[0].length))
                        startOffsetInParent -= leadingBlanks
                    }
                    converter = MdIndentConverter(contentLines.toSequence(-1, true), startOffsetInParent, contentLines.getLines(-1).toList(), contentLines.getLines(-1, false).toList(), null)
                }

                val result: Boolean
                val useConverter = converter
                if (useConverter != null) {
                    result = useConverter.decode(rangeInsideHost, outChars)
                } else {
                    val content = myHost.text
                    outChars.append(rangeInsideHost.substring(content))
                    result = true
                }

                return result
            }

            override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
                // map of offsets from un-indented to indented original
                val offset: Int
                val useConverter = converter

                if (useConverter != null) {
                    offset = useConverter.getOffsetInHost(offsetInDecoded, rangeInsideHost)
                } else {
                    offset = rangeInsideHost.startOffset + offsetInDecoded
                }

                return offset.minLimit(0)
            }

            override fun getRelevantTextRange(): TextRange {
                return getContentRange(false)
            }

            override fun isOneLine(): Boolean {
                return false
            }
        }
    }

    companion object {
        const val NBSP_PREFIX: String = "&nbsp;"

        fun getElementText(factoryContext: MdFactoryContext, content: String, markers: String?): String {
            // see how many we need based on content
            var useMarkers = markers

            var max = 0
            var lastPos = 0
            val length = content.length
            while (lastPos < length) {
                val pos = content.indexOf("`", lastPos)
                if (pos == -1) break
                val count = content.count('`', pos, length)
                if (max < count) max = count
                lastPos += max
            }
            max++
            if (useMarkers == null || useMarkers.length < max) {
                useMarkers = RepeatedSequence.repeatOf('`', max).toString()
            }

            // NOTE: if the inline-code is at the beginning of the paragraph and
            //   spans more than one line
            //   and has 3 or more leading markers
            //   then it will be parsed as fenced-code block not inline-code.
            //   If this is the condition, adding &nbsp; before the first marker will prevent this from happening.
            //   Since this text will be parsed by itself then we always and prefix.

            val sb = StringBuilder()
            val needNbspIfEol = useMarkers.length >= 3

            // NOTE: need to remove blank lines because these will interrupt the containing paragraph
            lastPos = 0
            var addEOL = false
            while (lastPos < length) {
                val pos = content.indexOf("\n", lastPos)
                if (lastPos == 0) {
                    if (pos != -1) {
                        if (needNbspIfEol) {
                            sb.append(NBSP_PREFIX)
                        }
                        addEOL = true
                    }
                    sb.append(useMarkers)
                }
                val end = if (pos == -1) length else pos + 1
                if (lastPos < end) {
                    val segment = content.substring(lastPos, end)
                    if (segment.trim().isNotEmpty()) {
                        // not a blank line, need to remove it

                        sb.append(content.substring(lastPos, end))
                        addEOL = false
                    }
                }
                lastPos = end
            }
            if (addEOL) sb.append("\n")
            sb.append(useMarkers)
            return sb.toString()
        }
    }
}
