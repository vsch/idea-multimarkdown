// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.vladsch.flexmark.test.util.TestUtils
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.parser.MdLexParser
import com.vladsch.md.nav.psi.element.MdElementItemPresentation
import com.vladsch.md.nav.psi.element.MdStructureViewPresentableItem
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.psi.util.MdPsiBundle
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.plugin.util.ifElse
import com.vladsch.plugin.util.minLimit
import com.vladsch.plugin.util.splice
import javax.swing.Icon

class FlexmarkExampleImpl(stub: FlexmarkExampleStub?, nodeType: IStubElementType<FlexmarkExampleStub, FlexmarkExample>?, node: ASTNode?) : FlexmarkStubElementImpl<FlexmarkExampleStub>(stub, nodeType, node), FlexmarkExample, MdStructureViewPresentableItem {

    constructor(stub: FlexmarkExampleStub, nodeType: IStubElementType<FlexmarkExampleStub, FlexmarkExample>) : this(stub, nodeType, null)

    constructor(node: ASTNode) : this(null, null, node)

    override fun getContentRange(inDocument: Boolean): TextRange {
        val firstEOL = node.chars.indexOf('\n')
        val startOffset =
            if (firstEOL == -1) inDocument.ifElse(node.startOffset + node.textLength, node.textLength)
            else inDocument.ifElse(node.startOffset + firstEOL, firstEOL)

        val endOffset = inDocument.ifElse(node.startOffset + node.textLength, node.textLength - 1)
        return TextRange(startOffset, endOffset)
    }

    override fun hasCoordinates(): Boolean {
        return section.isNotEmpty() || number.isNotEmpty()
    }

    override fun getSectionNode(): ASTNode? {
        return node.findChildByType(MdTypes.FLEXMARK_EXAMPLE_SECTION)
    }

    override fun getNumberNode(): ASTNode? {
        return node.findChildByType(MdTypes.FLEXMARK_EXAMPLE_NUMBER)
    }

    override fun getSection(): String {
        val stub = stub
        if (stub != null) {
            return stub.section
        }
        return sectionNode?.text ?: ""
    }

    override fun getNumber(): String {
        val stub = stub
        if (stub != null) {
            return stub.number
        }
        return numberNode?.text ?: ""
    }

    override fun getOptionsList(): FlexmarkExampleOptions? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.FLEXMARK_EXAMPLE_OPTIONS) as FlexmarkExampleOptions?
    }

    override fun getSource(): FlexmarkExampleSource? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.FLEXMARK_EXAMPLE_SOURCE) as FlexmarkExampleSource?
    }

    override fun getHtml(): FlexmarkExampleHtml? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.FLEXMARK_EXAMPLE_HTML) as FlexmarkExampleHtml?
    }

    override fun getAst(): FlexmarkExampleAst? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.FLEXMARK_EXAMPLE_AST) as FlexmarkExampleAst?
    }

    override fun setCoords(section: String?, number: String?): FlexmarkExample? {
        val example = this
        return MdElementFactory.createFlexmarkExample(MdFactoryContext(this), FlexmarkExampleParams(example).withCoords(section, number))
    }

    override fun setOptions(options: List<String>?): FlexmarkExample? {
        val example = this
        return MdElementFactory.createFlexmarkExample(MdFactoryContext(this), FlexmarkExampleParams(example).withOptions(options))
    }

    override fun getFoldingRange(): TextRange? {
        val length = MdLexParser.EXAMPLE_END.length
        return TextRange(node.startOffset + length, node.startOffset + (node.textLength - 1).minLimit(length))
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getLocationString(): String? {
        val optionsList = optionsList
        if (optionsList != null) {
            val options = optionsList.optionsString
            return " options($options)"
        }
        return null
    }

    override fun getPresentableText(): String? {
        return (if (section.isNotEmpty()) section else MdPsiBundle.message("flexmark-spec-example")) + ": " + number
    }

    override fun getBreadcrumbInfo(): String {
        return MdPsiBundle.message("flexmark-example")
    }

    override fun getBreadcrumbTooltip(): String? {
        return node.text
    }

    override fun getBreadcrumbTextElement(): PsiElement? {
        return null
    }

    override fun getIcon(flags: Int): Icon? {
        val optionsInfo = optionsList?.optionsInfo
        var flexmarkOptionInfo = FlexmarkPsi.FLEXMARK_NORMAL_OPTION_INFO

        if (optionsInfo != null) {
            if (optionsInfo.isNotEmpty() && !DumbService.isDumb(project)) {
                val definitions = FlexmarkPsiImplUtils.getOptionDefinitionStrings(mdFile, true)
                flexmarkOptionInfo = optionsInfo.map {
                    if (it.isBuiltIn || definitions.contains(it.optionName)) it
                    else FlexmarkPsi.FLEXMARK_ERROR_OPTION_INFO
                }.minByOrNull { it.index }
            }
        }
        return flexmarkOptionInfo.getIcon()
    }

    override fun isIgnored(): Boolean {
        return optionsList?.isWithIgnore ?: false
    }

    override fun isWithFail(): Boolean {
        return optionsList?.isWithFail ?: false
    }

    override fun isWithErrors(): Boolean {
        return optionsList?.isWithErrors ?: false
    }

    companion object {

        @Suppress("UNUSED_PARAMETER")
        @JvmStatic
        fun getElementText(factoryContext: MdFactoryContext, addFrontMatter: Boolean, params: FlexmarkExampleParams): String {
            val out = StringBuilder()

            if (addFrontMatter) {
                out.append("---\n\n\n...\n\n")
            }

            flexmarkSpecExample(out, params)
            return out.toString()
        }

        @JvmStatic
        fun flexmarkSpecExample(out: StringBuilder, params: FlexmarkExampleParams) {
            val optionsSet = params.options?.map(String::trim)?.filter { it.isNotEmpty() }?.splice(", ", true)
            TestUtils.addSpecExample(params.useTestExample, true, false, out, params.source, params.html, params.ast, optionsSet, params.haveCoords, params.section, params.number?.toInt() ?: 0)
        }
    }
}
