// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.psi.element.MdVerbatimBaseImpl
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.md.nav.flex.settings.FlexmarkHtmlSettings
import com.vladsch.plugin.util.minLimit
import com.vladsch.plugin.util.nullIfBlank

import icons.MdIcons
import javax.swing.Icon

abstract class FlexmarkExampleSectionImpl(node: ASTNode) : MdVerbatimBaseImpl(node), FlexmarkExampleSection {
    override fun getLanguageNode(): ASTNode? {
        return null
    }

    override fun getVerbatimLanguage(): String {
        val flexmarkSettings = MdRenderingProfileManager.getProfile(containingFile).htmlSettings.getExtension(FlexmarkHtmlSettings.KEY)
        val sectionInfo = flexmarkSettings.sectionInfo(sectionIndex)
        return sectionInfo.first.toLowerCase().nullIfBlank() ?: "text"
    }

    override fun getContentElement(): ASTNode? {
        return node.firstChildNode
    }

    override fun isPrefixedContent(): Boolean {
        return false
    }

    abstract fun getFlexmarkExampleParams(example: FlexmarkExample, content: String?): FlexmarkExampleParams

    override fun getExampleSection(example: FlexmarkExample): FlexmarkExampleSection? {
        return when (sectionIndex) {
            1-> example.source
            2-> example.html
            3-> example.ast
            else -> throw IllegalStateException("Do not know how to convert sectionIndex: $sectionIndex to section element")
        }
    }

    override fun setContent(content: String): PsiElement {
        val example: FlexmarkExample = parent as FlexmarkExample
        val factoryContext = MdFactoryContext(this)
        val newExample = MdElementFactory.createFlexmarkExample(factoryContext, getFlexmarkExampleParams(example, content))
            ?: return this
        val contentNode = getExampleSection(newExample)?.contentElement
        if (contentNode != null && node.firstChildNode != null) {
            node.replaceChild(node.firstChildNode, contentNode)
            return this
        } else {
            val newContent = newExample.ast
            if (newContent != null) {
                replace(newContent)
                return newContent
            }
            return this
        }
    }

    override fun getVerbatimLanguageRange(inDocument: Boolean): TextRange {
        val offsetInParent = if (inDocument) node.startOffset else 0
        return TextRange(offsetInParent, offsetInParent)
    }

    override fun setVerbatimLanguage(verbatimLanguage: String?): PsiElement {
        return this
    }

    override fun getOpenMarkerNode(): ASTNode? {
        return null
    }

    override fun getCloseMarkerNode(): ASTNode? {
        return null
    }

    override fun getCloseMarker(): String {
        return closeMarkerNode?.text ?: ""
    }

    abstract fun getSectionDescription(): String

    override fun getPresentation(): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                if (!isValid) return null
//                val marker = node.findChildByType(MdTypes.VERBATIM_OPEN)
                return getSectionDescription()
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

    override fun getContentPrefix(): String? {
        return null
    }

    override fun getContentSuffix(): String? {
        return null
    }

    override fun getBreadcrumbTooltip(): String? {
        return null
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    override fun getHighlightRange(caret: Int, otherNode: ASTNode?): Pair<TextRange, TextRange>? {
        if (otherNode != null) {
            if (caret >= node.startOffset && caret < node.startOffset + node.textLength) {
                val offset = caret - node.startOffset
                val chars = node.firstChildNode.chars
                val offsetIndex = BasedSequence.of(chars)
                val safeIndex = BasedSequence.of(offsetIndex.lineAt(offset))
                var index = offset - safeIndex.startOffset

                // we need to find next [
                while (safeIndex.safeCharAt(index) != '[' && safeIndex.safeCharAt(index) != ']' && safeIndex.safeCharAt(index) != '"' && safeIndex.safeCharAt(index) != '\u0000') index++

                if (safeIndex.safeCharAt(index) != '[') {
                    while (index > 0 && safeIndex.safeCharAt(index) != '[' && safeIndex.safeCharAt(index) != '\u0000') index--
                }

                if (safeIndex.safeCharAt(index) == '[') {
                    while (index > 0 && safeIndex.safeCharAt(index) != ' ' && safeIndex.safeCharAt(index) != '\u0000') index--
                    if (safeIndex.safeCharAt(index) == ' ') index++
                    val astStart = index + safeIndex.startOffset + node.startOffset

                    val astEndRef = Array<Int>(1) { 0 }
                    val nodeRange = selfNodeRange(chars, astStart - node.startOffset, astEndRef)
                    if (nodeRange != null && nodeRange.startOffset >= 0 && nodeRange.startOffset < nodeRange.endOffset && nodeRange.endOffset <= otherNode.chars.length) {
                        return Pair(TextRange(nodeRange.startOffset + otherNode.startOffset, nodeRange.endOffset + otherNode.startOffset), TextRange(astStart, astEndRef[0] + node.startOffset))
                    }
                }
            }
        }
        return null
    }

    private fun selfNodeRange(chars: CharSequence, offset: Int, astEndRef: Array<Int>?): TextRange? {
        val match = "(\\s+)|\\S\\[([0-9]+)(?:\\s*,\\s*([0-9]+))".toRegex().find(chars, offset)
        if (match != null && match.groups[2] != null && match.groups[3] != null) {
            val start = match.groupValues[2].toInt()
            val end = match.groupValues[3].toInt()
            if (astEndRef != null) {
                astEndRef[0] = (match.groups[3]?.range?.endInclusive ?: 0) + 2
            }
            return TextRange(start.minLimit(0), end.minLimit(start))
        }
        return null
    }
}
