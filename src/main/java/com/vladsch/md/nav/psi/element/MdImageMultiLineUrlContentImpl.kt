// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.LineAppendableImpl
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.psi.util.MdItemPresentation
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.util.MdIndentConverter
import com.vladsch.plugin.util.maxLimit
import java.util.*

class MdImageMultiLineUrlContentImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdImageMultiLineUrlContent {

    override fun toString(): String {
        return "IMAGE_LINK_REF_URL_CONTENT '" + name + "' " + super.hashCode()
    }

    override fun hasContent(): Boolean {
        return contentElement != null
    }

    override fun getContentElement(): ASTNode? {
        return node.findChildByType(MdTypes.IMAGE_URL_CONTENT)
    }

    override fun getContent(): String {
        val content = node.findChildByType(MdTypes.IMAGE_URL_CONTENT)
        return content?.text ?: ""
    }

    override fun getContentCharSequence(): CharSequence {
        val content = node.findChildByType(MdTypes.IMAGE_URL_CONTENT)
        return content?.chars ?: BasedSequence.EMPTY
    }

    override fun getLeadMarkerPrefix(): String {
        val prefixes = MdPsiImplUtil.getBlockPrefixes(this, null, PsiEditAdjustment(containingFile))
        return prefixes.childContPrefix.toString()
    }

    override fun getContentUrlPrefix(): String {
        val urlNode = parent.node.findChildByType(MdTypes.IMAGE_LINK_REF)
        return urlNode?.text ?: ""
    }

    override fun getContentRange(inDocument: Boolean): TextRange {
        val content = node.findChildByType(MdTypes.IMAGE_URL_CONTENT) ?: return TextRange.EMPTY_RANGE

        var offsetInParent = content.startOffset - if (inDocument) 0 else node.startOffset
        var endOffset = offsetInParent + content.textLength

        val indentPrefix = leadMarkerPrefix
        val stripEndLine = stripSuffix ?: ""

        if (!inDocument && (indentPrefix.isNotEmpty() || stripEndLine.isNotEmpty())) {
            // NOTE: here have to skip prefix via the same rules as in the indent converter with handling of tab expansion
            //   we want to start and end with real code not prefixes because these confuse PSI mapping in IntelliLang plugin code
            val editContext = PsiEditAdjustment(containingFile)
            val contentLines = editContext.lineAppendable.append(content.text).line()
            MdPsiImplUtil.adjustLinePrefix(this, contentLines, editContext)
            val leadPrefix = contentLines[0].prefix

            if (leadPrefix.isNotEmpty) {
                // we want to start and end with real code not prefixes because these confuse PSI mapping in IntelliLang plugin code
                // need to see actual characters in the text before we can skip them
                val leadIndent = leadPrefix.length
                offsetInParent += leadIndent
            }

            if (stripEndLine.isNotEmpty() && content.text.endsWith(stripEndLine + "\n")) {
                endOffset -= stripEndLine.length + 1
            }
        }

        return TextRange(offsetInParent, endOffset)
    }

    override fun setContent(verbatimContent: String): PsiElement {
        val content = node.findChildByType(MdTypes.IMAGE_URL_CONTENT) ?: return this

        // remove blank lines because they won't parse multi-line url
        var convertedContent = LineAppendableImpl(0).append(verbatimContent).toString(0, true)
        val indentPrefix = leadMarkerPrefix

        // re-indent if there is an indent ahead of our content on the line
        val restoreSuffix = restoreSuffix
        val excludeEndLineSuffix = restoreSuffixExclusions
        if (indentPrefix.isNotEmpty() || restoreSuffix != null && restoreSuffix.isNotEmpty()) {
            convertedContent = MdIndentConverter.encode(convertedContent, "", indentPrefix, true, restoreSuffix, excludeEndLineSuffix)
            convertedContent = convertedContent.removeSuffix("\n")
        }

        if (indentPrefix.isNotEmpty()) {
            // we have no choice but to replace the element textually, because with all possible prefix combinations
            // it will not parse, so we have to create a file with contents of the top parent element that contains us
            // the easiest way is to just take the whole PsiFile and replace our content in it
            val file = containingFile.originalFile
            val fileText = file.text

            val changedText = fileText.substring(0, content.startOffset) +
                convertedContent +
                fileText.substring(content.startOffset + content.textLength)

            val factoryContext = MdFactoryContext(this)
            val newFile = MdElementFactory.createFile(factoryContext, changedText)

            var psiElement = newFile.findElementAt(parent.node.startOffset)

            // go up to image element
            while (psiElement !is MdFile && psiElement !is MdImageLink) psiElement = psiElement?.parent

            if (psiElement is MdImageLink) {
                val linkReUrlContentElement = psiElement.linkRefUrlContentElement
                if (linkReUrlContentElement != null) {
                    this.parent.replace(psiElement)
                    return linkReUrlContentElement
                }
            }
        } else {
            MdPsiImplUtil.setContent(this, convertedContent)
        }
        return this
    }

    override fun getPresentation(): ItemPresentation {
        return MdItemPresentation("Image Url Content", text.substring(0, text.length.maxLimit(50)), null)
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
            var myConverter: MdIndentConverter? = null

            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                if (myConverter == null) {

                    val content = node.findChildByType(MdTypes.IMAGE_URL_CONTENT)!!
                    val contentLines = LineAppendableImpl(0).append(content.text).line()
                    MdPsiImplUtil.adjustLinePrefix(thizz, contentLines, PsiEditAdjustment(containingFile))
                    var unsuffixedLines: ArrayList<BasedSequence>? = null

                    if (!stripSuffix.isNullOrEmpty()) {
                        val stripEndLine = "(?:$stripSuffix)\n?$".toRegex()
                        unsuffixedLines = ArrayList()
                        for (info in contentLines) {
                            val match = stripEndLine.find(info.line)
                            if (match != null) {
                                unsuffixedLines.add(info.line.subSequence(0, info.length - match.value.length))
                            } else {
                                unsuffixedLines.add(info.line)
                            }
                        }
                    }

                    // indent prefix are the characters from the beginning of line to the start of content
                    val startOffsetInParent = rangeInsideHost.startOffset

                    // first line must have no prefix
                    if (contentLines.lineCount > 0) contentLines.setLine(0, "", contentLines[0].text)
                    myConverter = MdIndentConverter(contentLines.toSequence(-1, true), startOffsetInParent, contentLines.getLines(-1).toList(), contentLines.getLines(-1, false).toList(), unsuffixedLines)
                }

                val converter = myConverter
                val result = if (converter != null) {
                    converter.decode(rangeInsideHost, outChars)
                } else {
                    outChars.append(rangeInsideHost.substring(myHost.text))
                    true
                }

                // DEBUG: enable to debug details during testing
//                LOG.debug { ("decode(" + rangeInsideHost.startOffset + ", " + rangeInsideHost.endOffset + ") \"" + outChars.toString() + "\"") }
                return result
            }

            override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
                // map of offsets from un-indented to indented original
                val offset: Int
                val converter = myConverter
                if (converter != null) {
                    offset = converter.getOffsetInHost(offsetInDecoded, rangeInsideHost)
                } else {
                    offset = rangeInsideHost.startOffset + offsetInDecoded
                }

                // DEBUG: enable to debug details during testing
//                LOG.debug { "getOffsetInHost(" + offsetInDecoded + ", " + rangeInsideHost.startOffset + ", " + rangeInsideHost.endOffset + ") = " + offset }
                return offset
            }

            override fun getRelevantTextRange(): TextRange {
                return getContentRange(false)
            }

            override fun isOneLine(): Boolean {
                return false
            }
        }
    }

    val contentType: ContentType?
        get() = getContentType(contentUrlPrefix, contentCharSequence)

    val restoreSuffixExclusions: String?
        get() = getContentType(contentUrlPrefix, contentCharSequence).restoreSuffixExclusions

    val restoreSuffix: String?
        get() = getContentType(contentUrlPrefix, contentCharSequence).restoreSuffix

    val stripSuffix: String?
        get() = getContentType(contentUrlPrefix, contentCharSequence).stripSuffix

    val contentPrefix: String?
        get() {
            val urlPrefix = contentUrlPrefix
            val urlContent = contentCharSequence
            return getContentType(urlPrefix, urlContent).contentPrefix?.invoke(urlPrefix, urlContent)
        }

    val contentSuffix: String?
        get() {
            val urlPrefix = contentUrlPrefix
            val urlContent = contentCharSequence
            return getContentType(urlPrefix, urlContent).contentSuffix?.invoke(urlPrefix, urlContent)
        }

    val contentLanguage: String?
        get() {
            val urlPrefix = contentUrlPrefix
            val urlContent = contentCharSequence
            val editContext = PsiEditAdjustment(containingFile)
            val contentLines = editContext.lineAppendable.append(urlContent).line()
            MdPsiImplUtil.adjustLinePrefix(this, contentLines, editContext)
            return getContentType(urlPrefix, contentLines.toSequence(false)).language?.invoke(urlPrefix, contentLines.toSequence(false))
        }

    data class ContentType(
        val urlPrefix: String,
        val contentMatch: String?,
        val stripSuffix: String?,
        val restoreSuffix: String?,
        val restoreSuffixExclusions: String?,  // characters at end of line after which the suffix is not restored
        val language: ((CharSequence, CharSequence) -> String?)?,
        val contentPrefix: ((CharSequence, CharSequence) -> String?)?,  // uneditable prefix to add to fragment
        val contentSuffix: ((CharSequence, CharSequence) -> String?)?   // uneditable suffix to add to fragment
    )

    @Suppress("UNUSED_PARAMETER")
    companion object {

        internal fun getContentLanguage(urlPrefix: CharSequence, urlContent: CharSequence): String? {
            // FEATURE: use configuration settings for language extensions
            return null
        }

        internal fun getContentPrefix(urlPrefix: CharSequence, urlContent: CharSequence): String? {
            // FEATURE: use configuration settings for language extensions
            return null
        }

        internal fun getContentSuffix(urlPrefix: CharSequence, urlContent: CharSequence): String? {
            // FEATURE: use configuration settings for language extensions
            return null
        }

        private val LOG = Logger.getInstance("com.vladsch.md.nav.psi.image-url-content")
        val EMPTY_TYPE = ContentType("", null, null, null, null, Companion::getContentLanguage, Companion::getContentPrefix, Companion::getContentSuffix)

        const val GRAVIZO_PNG_PREFIX: String = "g.gravizo.com/g"
        const val GRAVIZO_SVG_PREFIX: String = "g.gravizo.com/svg"
        const val GRAVIZO_PNG_PREFIX_Q: String = "$GRAVIZO_PNG_PREFIX?"
        const val GRAVIZO_SVG_PREFIX_Q: String = "$GRAVIZO_SVG_PREFIX?"

        const val CODECOGS_PNG_PREFIX: String = "latex.codecogs.com/png.latex"
        const val CODECOGS_SVG_PREFIX: String = "latex.codecogs.com/svg.latex"
        const val CODECOGS_PNG_PREFIX_Q: String = "$CODECOGS_PNG_PREFIX?"
        const val CODECOGS_SVG_PREFIX_Q: String = "$CODECOGS_SVG_PREFIX?"

        private val contentTypeList = arrayListOf(
            ContentType(GRAVIZO_PNG_PREFIX, "^@startuml(?:;?\\s*\n)", ";*", ";", "{}", { _, _ -> "puml" }, Companion::getContentPrefix, Companion::getContentSuffix),
            ContentType(GRAVIZO_SVG_PREFIX, "^@startuml(?:;?\\s*\n)", ";*", ";", "{}", { _, _ -> "puml" }, Companion::getContentPrefix, Companion::getContentSuffix),
            ContentType(GRAVIZO_PNG_PREFIX, null, "", "", null, Companion::getContentLanguage, Companion::getContentPrefix, Companion::getContentSuffix),
            ContentType(GRAVIZO_SVG_PREFIX, null, "", "", null, Companion::getContentLanguage, Companion::getContentPrefix, Companion::getContentSuffix),
            ContentType(CODECOGS_PNG_PREFIX, null, "", "", null, { _, _ -> "math" }, Companion::getContentPrefix, Companion::getContentSuffix),
            ContentType(CODECOGS_SVG_PREFIX, null, "", "", null, { _, _ -> "math" }, Companion::getContentPrefix, Companion::getContentSuffix)
        )

        internal fun getContentType(urlPrefix: String, urlContent: CharSequence): ContentType {
            for (contentType in contentTypeList) {
                val pattern = "https?://\\Q${contentType.urlPrefix}\\E\\?"
                if (urlPrefix.trim().matches(pattern.toRegex())) {
                    if (contentType.contentMatch == null || contentType.contentMatch.toRegex().find(urlContent) != null) {
                        return contentType
                    }
                }
            }
            return EMPTY_TYPE
        }
    }
}
