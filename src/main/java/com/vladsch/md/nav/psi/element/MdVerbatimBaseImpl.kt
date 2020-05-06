// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.LineAppendable
import com.vladsch.flexmark.util.sequence.LineAppendableImpl
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.BlockPrefixes
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.util.MdIndentConverter
import com.vladsch.md.nav.util.format.LinePrefixMatcher
import com.vladsch.md.nav.util.format.SpacePrefixMatcher
import com.vladsch.plugin.util.maxLimit
import com.vladsch.plugin.util.minLimit
import com.vladsch.plugin.util.toBased
import icons.MdIcons
import javax.swing.Icon

abstract class MdVerbatimBaseImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdVerbatim, MdIndentingComposite {
    override fun getPrefixMatcher(editContext: PsiEditContext): LinePrefixMatcher {
        return if (node.findChildByType(MdTypes.VERBATIM_OPEN) != null) LinePrefixMatcher.NULL
        else SpacePrefixMatcher.maxSpaces(4)
    }

    override fun removeLinePrefix(lines: LineAppendable, indentColumns: IntArray, isFirstChild: Boolean, editContext: PsiEditContext) {
        node.findChildByType(MdTypes.VERBATIM_OPEN) ?: MdIndentingCompositeImpl.removeLinePrefix(lines, indentColumns, isFirstChild, editContext, SpacePrefixMatcher.maxSpaces(4), 0)
    }

    override fun isFirstItemBlock(element: PsiElement): Boolean {
        return isTextStart(element.node)
    }

    final override fun isFirstItemBlockPrefix(element: PsiElement): Boolean {
        return false
    }

    override fun itemPrefixes(parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes {
        val prefixes = parentPrefixes ?: MdPsiImplUtil.getBlockPrefixes(parent, parentPrefixes, editContext)
        val actualTextPrefix = if (openMarkerNode != null) "" else "    "
        val childContPrefix = actualTextPrefix
        return prefixes.append(MdPsiImplUtil.isFirstIndentedBlockPrefix(this, false), actualTextPrefix, childContPrefix, childContPrefix, childContPrefix)
    }

    override fun actualItemPrefix(editContext: PsiEditContext): CharSequence {
        return actualTextPrefix(false)
    }

    override fun actualTextPrefix(editContext: PsiEditContext, addTrailingSpace: Boolean): CharSequence {
        return actualTextPrefix(addTrailingSpace)
    }

    override fun actualTextPrefix(addTrailingSpace: Boolean): String {
        // take from start of line to start of node
        val text = containingFile.text
        var pos = node.startOffset
        while (pos >= 0 && text[pos] != '\n') pos--
        if (pos < 0) pos = 0
        else pos++

        return text.substring(pos, node.startOffset)
    }

    override fun contentIndent(): Int {
        return actualTextPrefix(false).length
    }

    override fun isTextStart(node: ASTNode): Boolean {
        return node.elementType == MdTypes.VERBATIM_CONTENT || node.elementType == MdTypes.VERBATIM_OPEN
    }

    override fun isEmptyText(): Boolean {
        return false
    }

    override fun getLanguageNode(): ASTNode? {
        val node = node.findChildByType(MdTypes.VERBATIM_LANG)
        return node
    }

    override fun getVerbatimLanguage(): String {
        //final PsiElement element = MultiMarkdownPsiImplUtil.findChildByType(this, MultiMarkdownTypes.VERBATIM_LANG);
        val node = languageNode
        val application = ApplicationManager.getApplication()
        return if (application.isReadAccessAllowed) {
            node?.text?.trim { it <= ' ' } ?: ""
        } else {
            var text = ""
            application.runReadAction {
                text = node?.text?.trim { it <= ' ' } ?: ""
            }
            text
        }
    }

    override fun getVerbatimLanguageRange(inDocument: Boolean): TextRange {
        val langNode = languageNode
        return if (langNode == null) {
            val offsetInParent = if (inDocument) node.startOffset else 0
            TextRange(offsetInParent, offsetInParent)
        } else {
            val offsetInParent = node.startOffset - if (inDocument) 0 else node.startOffset
            TextRange(offsetInParent, offsetInParent + node.textLength)
        }
    }

    override fun getContentElement(): ASTNode? {
        return node.findChildByType(MdTypes.VERBATIM_CONTENT)
    }

    override fun getContent(): String {
        val node = contentElement
        return node?.text ?: "\n"
    }

    override fun getContentCharSequence(): CharSequence {
        return contentElement?.chars ?: BasedSequence.EMPTY
    }

    override fun getLeadMarkerPrefix(): String {
        val prefixes = MdPsiImplUtil.getBlockPrefixes(this, null, PsiEditAdjustment(containingFile))
        return prefixes.childPrefix.toString()
    }

    override fun getOpenMarkerNode(): ASTNode? {
        return node.findChildByType(MdTypes.VERBATIM_OPEN)
    }

    override fun getCloseMarkerNode(): ASTNode? {
        return node.findChildByType(MdTypes.VERBATIM_CLOSE)
    }

    override fun getOpenMarker(): String {
        return openMarkerNode?.text ?: ""
    }

    override fun getCloseMarker(): String {
        return closeMarkerNode?.text ?: ""
    }

    abstract fun isPrefixedContent(): Boolean

    override fun getContentRange(inDocument: Boolean): TextRange {
        val contentElement = contentElement ?: return TextRange(0, 0)
        val marker = openMarkerNode

        var offsetInParent = contentElement.startOffset - if (inDocument) 0 else node.startOffset
        val endOffset = offsetInParent + contentElement.textLength

        val text = contentElement.text.toBased()
        if (!inDocument && marker != null && isPrefixedContent()) {
            val contentLines = LineAppendableImpl(0).append(text)
            if (contentLines.isNotEmpty) {
                MdPsiImplUtil.adjustLinePrefix(contentElement.psi, contentLines, PsiEditAdjustment(containingFile))
                val leadPrefix = contentLines[0].prefix

                if (leadPrefix.isNotEmpty) {
                    // basically, here we want to start and end with real code not prefixes because these confuse PSI mapping in IntelliLang plugin code
                    // need to see actual characters in the text before we can skip them
                    val leadIndent = leadPrefix.length
                    offsetInParent += leadIndent
                }
            }
        } else if (this is MdVerbatimImpl && marker == null && !inDocument) {
            // remove initial prefix
            val leadingTrim = text.countLeading(CharPredicate.SPACE).maxLimit(4) + text.countOfAny(CharPredicate.TAB).maxLimit(1)
            return TextRange(offsetInParent + leadingTrim, endOffset)
        }

        return TextRange(offsetInParent, endOffset)
    }

    override fun setContent(content: String): PsiElement {
        val contentElement = contentElement ?: return this
        val marker = node.findChildByType(MdTypes.VERBATIM_OPEN)
        val contentNode = contentElement

        var convertedContent = if (content.isEmpty()) "\n" else content
        val containingFile = this.containingFile
        val editContext = PsiEditAdjustment(containingFile)
        val indentPrefixes = MdPsiImplUtil.getBlockPrefixes(this, null, editContext).finalizePrefixes(editContext)

        // re-indent if there is an indent ahead of our content on the line
        if (indentPrefixes.isNotEmpty() || marker == null) {
            val contentLines = content.toBased().splitList("\n", false, CharPredicate.NONE)
            val sb = StringBuilder()
            val firstIndentPrefix: CharSequence = if (marker == null) "    " else ""
            val fileSequence = containingFile.text.toBased()
            val firstLine = fileSequence.lineAt(textOffset)
            val firstLinePrefix = firstLine.baseSubSequence(firstLine.startOffset, textOffset)
            val trimEnd = indentPrefixes.hasBlockQuotePrefix() && !firstLinePrefix.endsWith(' ') && indentPrefixes.childContPrefix.endsWith(' ')
            val restIndentPrefix: CharSequence = when {
                marker == null -> indentPrefixes.childContPrefix.append("    ")
                trimEnd -> indentPrefixes.childContPrefix.substring(0, indentPrefixes.childContPrefix.length - 1)
                else -> indentPrefixes.childContPrefix
            }
            var useIndentPrefix: CharSequence = firstIndentPrefix
            for (line in contentLines) {
                sb.append(useIndentPrefix)
                useIndentPrefix = restIndentPrefix
                sb.append(line).append("\n")
            }

            convertedContent = sb.toString()

            // we have no choice but to replace the element textually, because with all possible prefix combinations
            // it will not parse, so we have to create a file with contents of the top parent element that contains us
            // the easiest way is to just take the whole PsiFile and replace our content in it
            val file = containingFile.originalFile
            val fileText = file.text

            val changedText = fileText.substring(0, contentNode.startOffset) +
                convertedContent +
                fileText.substring(contentNode.startOffset + contentNode.textLength)

            val factoryContext = MdFactoryContext(this)
            val newFile = MdElementFactory.createFile(factoryContext, changedText)

            val psiElement = newFile.findElementAt(node.startOffset)?.parent
            if (psiElement != null && psiElement.node.elementType === MdTypes.VERBATIM) {
                return this.replace(psiElement)
            }
        } else if (this is MdVerbatimImpl) {
            MdPsiImplUtil.setContent(this, convertedContent, null)
        }
        return this
    }

    override fun getPresentation(): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                if (!isValid) return null
                val marker = node.findChildByType(MdTypes.VERBATIM_OPEN)

                return if (marker == null) "Verbatim block" else "Code fence"
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

    open fun isTerminatedContent(): Boolean {
        return true
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out MdPsiLanguageInjectionHost> {
        val thizz = this
        val isClosed = isTerminatedContent()

        return object : LiteralTextEscaper<MdPsiLanguageInjectionHost>(this) {
            var converter: MdIndentConverter? = null

            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                if (converter == null && thizz.isPrefixedContent()) {
                    val contentElement = (MdPsiImplUtil.findChildByType(thizz, MdTypes.VERBATIM_CONTENT) ?: return false)
                    val contentLines = LineAppendableImpl(0).append(contentElement.text).line()

                    MdPsiImplUtil.adjustLinePrefix(contentElement, contentLines, PsiEditAdjustment(containingFile))

                    // un-indent if needed
                    // indent prefix are the characters from the beginning of line to the start of content
                    var startOffsetInParent = rangeInsideHost.startOffset
                    val leadingBlanks: Int

                    // conversion must start with no prefix on the first line!!!!
                    if (contentLines.lineCount > 0) contentLines.setLine(0, "", contentLines[0].text)

                    if (startOffsetInParent > 0 && thizz is MdVerbatimImpl && thizz.openMarkerNode == null && contentLines.lineCount > 0) {
                        leadingBlanks = contentLines[0].text.countLeading(CharPredicate.SPACE_TAB).maxLimit(startOffsetInParent)
                        contentLines.setLine(0, "", contentLines[0].text.subSequence(leadingBlanks, contentLines[0].length))
                        startOffsetInParent -= leadingBlanks
                    }

                    converter = MdIndentConverter(contentLines.toSequence(), startOffsetInParent, contentLines.getLines(true).toList(), contentLines.getLines(false).toList(), null)
                }

                val useConverter = converter
                val result = if (useConverter != null) {
                    if (isClosed) {
                        useConverter.decode(rangeInsideHost, outChars)
                    } else {
                        val text = StringBuilder()
                        useConverter.decode(rangeInsideHost, text)
                        if (text.length > rangeInsideHost.endOffset - rangeInsideHost.startOffset) {
                            // NOTE: if fenced code is open then the EOL at end of content is virtual and should be excluded
                            outChars.append(text, 0, text.length - 1)
                        } else {
                            outChars.append(text)
                        }
                    }
                    true
                } else {
                    val content = myHost.text
                    outChars.append(rangeInsideHost.substring(content))
                    true
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

    // FEATURE: create a provider for injection prefixes and suffixes by language type, for example java most likely needs class Dummy { } wrapper, php will need <?php prefix, etc.
    // FEATURE: add settings config for language injections, see if we start with html or php, assume php
    override fun getContentPrefix(): String? {
        val fenceLanguage = verbatimLanguage
        if (fenceLanguage == "php") {
            val contentText = content
            var i = 0
            while (i < contentText.length && "\t \n".indexOf(contentText[i]) >= 0) i++

            if (!(i < contentText.length && contentText[i] == '<')) {
                return "<?php\n"
            }
        }

        return null
    }

    override fun getContentSuffix(): String? = null
}
