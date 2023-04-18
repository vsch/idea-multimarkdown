// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.util

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.vladsch.md.nav.flex.psi.*
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.element.*

object MdElementFactory {

    fun createAtxHeader(factoryContext: MdFactoryContext, text: CharSequence, level: Int, hasTrailingMarker: Boolean, prefix: CharSequence): MdAtxHeader? {
        val elementText = MdAtxHeaderImpl.getElementText(factoryContext, text, level, hasTrailingMarker)
        val trimmedPrefix = if (prefix.trim().isEmpty() || !prefix.endsWith(' ')) prefix.trim() else prefix.trim().toString() + " "
        val lines = factoryContext.emptyAppendable.blankLine().append(elementText).line()
        MdPsiImplUtil.addLinePrefix(lines, trimmedPrefix, trimmedPrefix)
        return createElementFromText(factoryContext, lines.toString(-1), MdAtxHeader::class.java) as MdAtxHeader?
    }

    fun createSetextHeader(factoryContext: MdFactoryContext, text: CharSequence, level: Int, prefix: CharSequence, trailingAttributes: Int): MdSetextHeader? {
        val elementText = MdSetextHeaderImpl.getElementText(factoryContext, text, level, trailingAttributes)
        val trimmedPrefix = if (prefix.trim().isEmpty() || !prefix.endsWith(' ')) prefix.trim() else prefix.trim().toString() + " "
        val lines = factoryContext.emptyAppendable.blankLine().append(elementText).line()
        MdPsiImplUtil.addLinePrefix(lines, trimmedPrefix, trimmedPrefix)
        return createElementFromText(factoryContext, lines.toString(0), MdSetextHeader::class.java) as MdSetextHeader?
    }

    @JvmOverloads
    fun createWikiLink(factoryContext: MdFactoryContext, name: String, text: String? = null, anchor: String? = null): MdWikiLink? {
        val elementText = MdWikiLinkImpl.getElementText(factoryContext, name, text, anchor)
        return createElementFromText(factoryContext, elementText, MdWikiLink::class.java) as MdWikiLink?
    }

    fun createExplicitLink(factoryContext: MdFactoryContext, linkRef: String, text: String?, anchor: String?, title: String?): MdExplicitLink? {
        val elementText = MdExplicitLinkImpl.getElementText(factoryContext, linkRef, text ?: "", anchor, title)
        return createElementFromText(factoryContext, elementText, MdExplicitLink::class.java) as MdExplicitLink?
    }

    fun createAutoLink(factoryContext: MdFactoryContext, wrapInAngleBrackets: Boolean, linkRef: String, anchor: String?): MdAutoLink? {
        val elementText = MdAutoLinkImpl.getElementText(factoryContext, wrapInAngleBrackets, linkRef, anchor)
        return createElementFromText(factoryContext, elementText, MdAutoLink::class.java) as MdAutoLink?
    }

    fun createJekyllInclude(factoryContext: MdFactoryContext, linkRef: String): MdJekyllInclude? {
        val elementText = MdJekyllIncludeImpl.getElementText(factoryContext, linkRef)
        return createElementFromText(factoryContext, elementText, MdJekyllInclude::class.java) as MdJekyllInclude?
    }

    fun createImageLink(factoryContext: MdFactoryContext, linkRefWithAnchor: String, text: String?, title: String?): MdImageLink? {
        val elementText = MdImageLinkImpl.getElementText(factoryContext, linkRefWithAnchor, text ?: "", title)
        return createElementFromText(factoryContext, elementText, MdImageLink::class.java) as MdImageLink?
    }

    fun createReference(factoryContext: MdFactoryContext, name: String, address: String, anchor: String?, title: String?): MdReference? {
        val elementText = MdReferenceImpl.getElementText(factoryContext, name, address, anchor, title)
        return createElementFromText(factoryContext, elementText, MdReference::class.java) as MdReference?
    }

    fun findNestedChild(parent: PsiElement, elementClass: Class<out PsiElement>, elementType: IElementType?): PsiElement? {
        for (child in parent.children) {
            if (elementClass.isInstance(child) && (elementType == null || child.node.elementType == elementType)) {
                return child
            }
            val element = findNestedChild(child, elementClass, elementType)
            if (element != null) return element
        }
        return null
    }

    fun createElementFromText(factoryContext: MdFactoryContext, elementText: CharSequence, elementClass: Class<out PsiElement>): PsiElement? {
        return createElementFromText(factoryContext, elementText, elementClass, null)
    }

    fun createFile(factoryContext: MdFactoryContext, text: String): MdFile {
        return factoryContext.createFile(text) as MdFile
    }

    fun createElementFromText(factoryContext: MdFactoryContext, elementText: CharSequence, elementClass: Class<out PsiElement>, elementType: IElementType?): PsiElement? {
        val file = factoryContext.createFile(elementText)
        return findNestedChild(file, elementClass, elementType)
    }

    fun createCRLF(factoryContext: MdFactoryContext): PsiElement {
        val file = factoryContext.createFile("\n")
        return file.firstChild
    }

    fun createVerbatim(factoryContext: MdFactoryContext, marker: String?, languageName: String?, verbatimContent: String?, leadMarkerPrefix: String?): MdVerbatim? {
        val elementText = MdVerbatimImpl.getElementText(factoryContext, marker, languageName, verbatimContent, leadMarkerPrefix)
        return createElementFromText(factoryContext, elementText, MdVerbatim::class.java) as MdVerbatim?
    }

    fun createHtmlBlock(factoryContext: MdFactoryContext, content: String?): MdHtmlBlock? {
        val elementText = MdHtmlBlockImpl.getElementText(factoryContext, content)
        return createElementFromText(factoryContext, elementText, MdHtmlBlock::class.java) as MdHtmlBlock?
    }

    fun createJekyllFrontMatterBlock(factoryContext: MdFactoryContext, content: String?): MdJekyllFrontMatterBlock? {
        val elementText = MdJekyllFrontMatterBlockImpl.getElementText(factoryContext, content)
        return createElementFromText(factoryContext, elementText, MdJekyllFrontMatterBlock::class.java) as MdJekyllFrontMatterBlock?
    }

    fun createFlexmarkFrontMatterBlock(factoryContext: MdFactoryContext, content: String?): FlexmarkFrontMatterBlock? {
        val elementText = FlexmarkFrontMatterBlockImpl.getElementText(factoryContext, content)
        return createElementFromText(factoryContext, elementText, FlexmarkFrontMatterBlock::class.java) as FlexmarkFrontMatterBlock?
    }

    fun createReferenceLink(factoryContext: MdFactoryContext, referenceId: String, referenceText: String?): MdReferenceLink? {
        val elementText = MdReferenceLinkImpl.getElementText(factoryContext, referenceId, referenceText)
        return createElementFromText(factoryContext, elementText, MdReferenceLink::class.java) as MdReferenceLink?
    }

    fun createReferenceImage(factoryContext: MdFactoryContext, referenceId: String, referenceText: String?): MdReferenceImage? {
        val elementText = MdReferenceImageImpl.getElementText(factoryContext, referenceId, referenceText)
        return createElementFromText(factoryContext, elementText, MdReferenceImage::class.java) as MdReferenceImage?
    }

    fun createFootnoteRef(factoryContext: MdFactoryContext, referenceId: String): MdFootnoteRef? {
        val elementText = MdFootnoteRefImpl.getElementText(factoryContext, referenceId)
        return createElementFromText(factoryContext, elementText, MdFootnoteRef::class.java) as MdFootnoteRef?
    }

    fun createMacroRef(factoryContext: MdFactoryContext, referenceId: String): MdMacroRef? {
        val elementText = MdMacroRefImpl.getElementText(factoryContext, referenceId)
        return createElementFromText(factoryContext, elementText, MdMacroRef::class.java) as MdMacroRef?
    }

    fun createFootnote(factoryContext: MdFactoryContext, referenceId: String, footnoteText: String): MdFootnote? {
        val elementText = MdFootnoteImpl.getElementText(factoryContext, referenceId, footnoteText)
        return createElementFromText(factoryContext, elementText, MdFootnote::class.java) as MdFootnote?
    }

    fun createMacro(factoryContext: MdFactoryContext, referenceId: String, macroText: String): MdMacro? {
        val elementText = MdMacroImpl.getElementText(factoryContext, referenceId, macroText)
        return createElementFromText(factoryContext, elementText, MdMacro::class.java) as MdMacro?
    }

    fun createAttributes(factoryContext: MdFactoryContext, attributeText: String): MdAttributesImpl? {
        val elementText = MdAttributesImpl.getElementText(factoryContext, attributeText)
        return createElementFromText(factoryContext, elementText, MdAttributes::class.java) as MdAttributesImpl?
    }

    fun createAttribute(factoryContext: MdFactoryContext, name: String, value: String): MdAttributeImpl? {
        val attributeText = MdAttributeImpl.getElementText(factoryContext, name, value)
        val elementText = MdAttributesImpl.getElementText(factoryContext, attributeText)
        return createElementFromText(factoryContext, elementText, MdAttribute::class.java) as MdAttributeImpl?
    }

    fun createEnumeratedReferenceFormat(factoryContext: MdFactoryContext, referenceId: String, text: String): MdEnumeratedReferenceFormat? {
        val elementText = MdEnumeratedReferenceFormatImpl.getElementText(factoryContext, referenceId, text)
        return createElementFromText(factoryContext, elementText, MdEnumeratedReferenceFormat::class.java) as MdEnumeratedReferenceFormat?
    }

    fun createAbbreviatedText(factoryContext: MdFactoryContext, referenceId: String): MdAbbreviatedText? {
        val elementText = MdAbbreviatedTextImpl.getElementText(factoryContext, referenceId)
        return createElementFromText(factoryContext, elementText, MdAbbreviatedText::class.java) as MdAbbreviatedText?
    }

    fun createEnumeratedReferenceLink(factoryContext: MdFactoryContext, referenceId: String): MdEnumeratedReferenceLink? {
        val elementText = MdEnumeratedReferenceLinkImpl.getElementText(factoryContext, referenceId)
        return createElementFromText(factoryContext, elementText, MdEnumeratedReferenceLink::class.java) as MdEnumeratedReferenceLink?
    }

    fun createEnumeratedReferenceText(factoryContext: MdFactoryContext, referenceId: String): MdEnumeratedReferenceText? {
        val elementText = MdEnumeratedReferenceTextImpl.getElementText(factoryContext, referenceId)
        return createElementFromText(factoryContext, elementText, MdEnumeratedReferenceText::class.java) as MdEnumeratedReferenceText?
    }

    fun createAbbreviation(factoryContext: MdFactoryContext, referenceId: String, expandedText: String): MdAbbreviation? {
        val elementText = MdAbbreviationImpl.getElementText(factoryContext, referenceId, expandedText)
        return createElementFromText(factoryContext, elementText, MdAbbreviation::class.java) as MdAbbreviation?
    }

    fun createRefAnchor(factoryContext: MdFactoryContext, referenceId: String, anchorText: String?): MdRefAnchor? {
        val elementText = MdRefAnchorImpl.getElementText(factoryContext, referenceId, anchorText)
        return createElementFromText(factoryContext, elementText, MdRefAnchor::class.java) as MdRefAnchor?
    }

    fun createBlankLine(factoryContext: MdFactoryContext, prefix: String): MdComposite? {
        val trimmedPrefix = if (prefix.trim().isEmpty() || !prefix.endsWith(' ')) prefix.trim() else prefix.trim() + " "
        val blankLine = createElementFromText(factoryContext, "$trimmedPrefix\n$trimmedPrefix\n$trimmedPrefix\n", MdComposite::class.java, MdTypes.BLANK_LINE) as? MdComposite
        // we want the second one because it will incorporate the parent prefix
        return blankLine?.nextSibling as? MdComposite ?: blankLine
    }

    fun createEmoji(factoryContext: MdFactoryContext, name: String): MdEmoji? {
        val elementText = MdEmojiImpl.getElementText(factoryContext, name)
        return createElementFromText(factoryContext, elementText, MdEmoji::class.java) as MdEmoji?
    }

    fun createFlexmarkExample(factoryContext: MdFactoryContext, params: FlexmarkExampleParams): FlexmarkExample? {
        val elementText = FlexmarkExampleImpl.getElementText(factoryContext, true, params)
        return createElementFromText(factoryContext, elementText, FlexmarkExample::class.java) as FlexmarkExample?
    }

    fun createInlineCode(factoryContext: MdFactoryContext, code: String, markers: String?): MdInlineCode? {
        val elementText = MdInlineCodeImpl.getElementText(factoryContext, code, markers)
        return createElementFromText(factoryContext, elementText, MdInlineCode::class.java) as MdInlineCode?
    }

    fun createInlineGitLabMath(factoryContext: MdFactoryContext, code: String): MdInlineGitLabMath? {
        val elementText = MdInlineGitLabMathImpl.getElementText(factoryContext, code)
        return createElementFromText(factoryContext, elementText, MdInlineGitLabMath::class.java) as MdInlineGitLabMath?
    }

    fun createInlineHtml(factoryContext: MdFactoryContext, html: String): MdInlineHtml? {
        val elementText = MdInlineHtmlImpl.getElementText(factoryContext, html)
        return createElementFromText(factoryContext, elementText, MdInlineHtml::class.java) as MdInlineHtml?
    }
}

