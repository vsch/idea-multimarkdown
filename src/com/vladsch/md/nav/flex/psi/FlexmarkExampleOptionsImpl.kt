// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.DumbService
import com.intellij.psi.stubs.IStubElementType
import com.vladsch.md.nav.parser.MdFactoryContext
import com.vladsch.md.nav.flex.psi.util.FlexmarkPsiImplUtils
import com.vladsch.md.nav.psi.util.MdElementFactory
import com.vladsch.md.nav.psi.util.MdTypes
import java.util.*
import javax.swing.Icon

class FlexmarkExampleOptionsImpl(stub: FlexmarkExampleOptionsStub?, nodeType: IStubElementType<FlexmarkExampleOptionsStub, FlexmarkExampleOptions>?, node: ASTNode?)
    : FlexmarkStubElementImpl<FlexmarkExampleOptionsStub>(stub, nodeType, node)
    , FlexmarkExampleOptions {

    companion object {
        val EMPTY_NODES = listOf<ASTNode>()
        val EMPTY_OPTIONS = listOf<FlexmarkExampleOption>()
        val EMPTY_STRINGS = listOf<String>()

        val OPTION_NODE_TYPES = setOf(MdTypes.FLEXMARK_EXAMPLE_OPTION)
    }

    constructor(stub: FlexmarkExampleOptionsStub, nodeType: IStubElementType<FlexmarkExampleOptionsStub, FlexmarkExampleOptions>) : this(stub, nodeType, null)

    constructor(node: ASTNode) : this(null, null, node)

    override fun getOptionNodes(): List<ASTNode> {
        var child = node.firstChildNode
        if (child != null) {
            val list = ArrayList<ASTNode>()
            while (child != null) {
                if (OPTION_NODE_TYPES.contains(child.elementType)) {
                    list.add(child)
                }
                child = child.treeNext
            }
            if (list.isNotEmpty()) return list
        }
        return EMPTY_NODES
    }

    override fun handleContentChange(newContent: String): FlexmarkExampleOptions {
        val example: FlexmarkExample = parent as FlexmarkExample
        val factoryContext = MdFactoryContext(this)
        val newExample = MdElementFactory.createFlexmarkExample(factoryContext, FlexmarkExampleParams(example).withOptions(newContent))
            ?: return this
        val contentNodes = newExample.optionsList ?: return this
        replace(contentNodes)
        return contentNodes
    }

    override fun getOptionElements(): List<FlexmarkExampleOption> {
        var child = firstChild
        if (child != null) {
            val list = ArrayList<FlexmarkExampleOption>()
            while (child != null) {
                if (child is FlexmarkExampleOption) {
                    list.add(child)
                }
                child = child.nextSibling
            }
            if (list.isNotEmpty()) return list
        }
        return EMPTY_OPTIONS
    }

    override fun getOptionsString(): String {
        val stub = stub
        if (stub != null) {
            return stub.optionsString
        }

        return text
    }

    // REFACTOR: rename to getOptionNames()
    override fun getOptions(): List<String> {
        return optionsInfo.map { it.optionName }
    }

    override fun getOptionTexts(): List<String> {
        return optionsInfo.map { it.optionText }
    }

    override fun getOptionsInfo(): List<FlexmarkOptionInfo> {
        val stub = stub
        if (stub != null) {
            return stub.optionsInfo
        }

        if (optionNodes.isNotEmpty()) {
            val nodeList = optionNodes
            val list = ArrayList<FlexmarkOptionInfo>()
            for (node in nodeList) {
                val text = node.text
                if (text.isEmpty()) continue
                list.add(FlexmarkPsi.getFlexmarkOptionInfo(text))
            }
            return list
        }
        return emptyList()
    }

    override fun getIcon(flags: Int): Icon? {
        //        return MarkdownIcons.DEFINITION_ITEM
        return null
    }

    override fun isWithIgnore(): Boolean = optionsInfo.any { it.isIgnore }
    override fun isWithFail(): Boolean = optionsInfo.any { it.isFail }

    override fun toString(): String {
        return "FLEXMARK_EXAMPLE_OPTIONS" + super.hashCode()
    }

    override fun isWithErrors(): Boolean {
        val options = optionsInfo
        if (options.isNotEmpty() && !DumbService.isDumb(project)) {
            val definitions = FlexmarkPsiImplUtils.getOptionDefinitionStrings(mdFile, true)
            return !options.all { definitions.contains(it.optionName) || it.isDisabled || it.isBuiltIn }
        }
        return false
    }
}
