// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language.folding

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.util.TextRange
import com.vladsch.md.nav.flex.PluginBundle
import com.vladsch.md.nav.language.api.MdFoldingVisitorHandler
import com.vladsch.md.nav.flex.psi.*
import com.vladsch.md.nav.psi.util.MdVisitHandler

// FLEXMARK_PLUGIN
class MdFlexmarkFoldingBuilder(handler: MdFoldingVisitorHandler, @Suppress("UNUSED_PARAMETER") quick: Boolean) : MdFoldingVisitorHandler by handler {

    init {
        addHandlers(
            MdVisitHandler(FlexmarkFrontMatterBlock::class.java, this::fold),
            MdVisitHandler(FlexmarkExample::class.java, this::fold),
            MdVisitHandler(FlexmarkExampleSource::class.java, this::fold),
            MdVisitHandler(FlexmarkExampleHtml::class.java, this::fold),
            MdVisitHandler(FlexmarkExampleAst::class.java, this::fold)
        )
    }

    private fun fold(element: FlexmarkFrontMatterBlock) {
        val range = element.textRange
        if (!range.isEmpty && range.startOffset + 3 < range.endOffset) {
            addDescriptor(object : FoldingDescriptor(element.node, TextRange(range.startOffset + 3, range.endOffset), null) {
                override fun getPlaceholderText(): String? {
                    return defaultPlaceHolderText
                }
            })
            visitChildren(element)
        }
    }

    private fun fold(element: FlexmarkExampleSource) {
        if (element.node.textLength > 0) {
            val textRange = TextRange(element.node.startOffset, element.node.startOffset + element.node.textLength - 1)
            if (!textRange.isEmpty) {
                addDescriptor(object : FoldingDescriptor(element.node, textRange, null) {
                    override fun getPlaceholderText(): String? {
                        // FLEXMARK_PLUGIN
                        return defaultPlaceHolderText + PluginBundle.message("code-folding.flexmark.example.source.placeholder") + defaultPlaceHolderText
                    }
                })
            }
            visitChildren(element)
        }
    }

    private fun fold(element: FlexmarkExampleHtml) {
        if (element.node.textLength > 0) {
            val textRange = TextRange(element.node.startOffset, element.node.startOffset + element.node.textLength - 1)
            if (!textRange.isEmpty) {
                addDescriptor(object : FoldingDescriptor(element.node, textRange, null) {
                    override fun getPlaceholderText(): String? {
                        // FLEXMARK_PLUGIN
                        return defaultPlaceHolderText + PluginBundle.message("code-folding.flexmark.example.html.placeholder") + defaultPlaceHolderText
                    }
                })
            }
            visitChildren(element)
        }
    }

    private fun fold(element: FlexmarkExampleAst) {
        if (element.node.textLength > 0) {
            val textRange = TextRange(element.node.startOffset, element.node.startOffset + element.node.textLength - 1)
            if (!textRange.isEmpty) {
                addDescriptor(object : FoldingDescriptor(element.node, textRange, null) {
                    override fun getPlaceholderText(): String? {
                        // FLEXMARK_PLUGIN
                        return defaultPlaceHolderText + PluginBundle.message("code-folding.flexmark.example.ast.placeholder") + defaultPlaceHolderText
                    }
                })
            }
            visitChildren(element)
        }
    }

    private fun fold(element: FlexmarkExample) {
        val exRange = element.foldingRange
        if (exRange != null && !exRange.isEmpty) {
            val text = " " + element.presentableText + " ..."
            addDescriptor(object : FoldingDescriptor(element.node, exRange, null) {
                override fun getPlaceholderText(): String? {
                    return text
                }
            })
            visitChildren(element)
        }
    }
}
