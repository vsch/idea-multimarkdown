// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language

import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.SmartStripTrailingSpacesFilter
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.vladsch.md.nav.language.api.MdStripTrailingSpacesDocumentFilter
import com.vladsch.md.nav.language.api.MdStripTrailingSpacesExtension
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.util.MdNodeVisitor
import com.vladsch.md.nav.psi.util.MdNodeVisitorHandler
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdVisitor
import com.vladsch.plugin.util.maxLimit
import com.vladsch.plugin.util.psi.isTypeIn
import java.util.*
import java.util.function.BiConsumer

class MdStripTrailingSpacesSmartFilter(var document: Document) : SmartStripTrailingSpacesFilter() {
    private val myTwoSpaceLimit = BitSet()
    private val myOneSpaceLimit = BitSet()
    private val textLength = document.textLength
    private val lineCount = document.lineCount

    override fun getTrailingSpacesToLeave(line: Int): Int {
        val type = getToLeave(line)
        return if (type == 3) 9999 else type
    }

    private fun getToLeave(line: Int): Int {
        var type = 0
        if (myTwoSpaceLimit[line]) type = type or 2
        if (myOneSpaceLimit[line]) type = type or 1
        return type
    }

    /**
     * Check all children for hard break spaces, recurse into compound nodes
     */
    private fun checkTrailingSpacesBlock(node: ASTNode) {
        var child = node.firstChildNode
        while (child != null) {
            if (child is LeafPsiElement) {
                if (child.isTypeIn(MdTokenSets.LINE_BREAK_SPACES_SET)) {
                    keepTrailingSpaces(child, 2)
                }
            } else {
                checkTrailingSpacesBlock(child)
            }
            child = child.treeNext
        }
    }

    private fun keepTrailingSpaces(node: ASTNode, keepTrailingSpaces: Int) {
        if (node.startOffset <= textLength) {
            val startLine = getLineNumber(node.startOffset)
            val endLine = getLineNumber(node.startOffset + node.textLength)
            keepTrailingSpaces(startLine, endLine + 1, keepTrailingSpaces)
        }
    }

    private fun keepTrailingSpaces(startLine: Int, endLine: Int, keepTrailingSpaces: Int) {
        if (startLine in 0 until endLine && startLine < lineCount && keepTrailingSpaces > 0) {
            val useKeepTrailingSpaces = keepTrailingSpaces.maxLimit(3)
            val useEndLine = endLine.maxLimit(lineCount)
            if (useKeepTrailingSpaces and 1 != 0) myOneSpaceLimit.set(startLine, useEndLine)
            if (useKeepTrailingSpaces and 2 != 0) myTwoSpaceLimit.set(startLine, useEndLine)
        }
    }

    private fun getLineNumber(offset: Int): Int {
        return when {
            offset <= 0 -> 0
            offset >= textLength -> lineCount
            else -> document.getLineNumber(offset)
        }
    }

    fun process(psiFile: MdFile) {
        val codeStyle = MdCodeStyleSettings.getInstance(psiFile)
        val visitor = object : MdNodeVisitor(true) {
            override fun processNode(node: PsiElement, withChildren: Boolean, processor: BiConsumer<PsiElement, MdVisitor<PsiElement>>) {
                val endLine = getLineNumber(node.node.startOffset + node.node.textLength)
                if (getToLeave(endLine) != 3) {
                    super.processNode(node, withChildren, processor)
                }
            }
        }

        val filter = object : MdStripTrailingSpacesDocumentFilter, MdNodeVisitorHandler by visitor {
            override fun getPsiFile(): MdFile = psiFile
            override fun getLineNumber(offset: Int): Int = this@MdStripTrailingSpacesSmartFilter.getLineNumber(offset)
            override fun checkBlockForTrailingSpaces(node: ASTNode) = this@MdStripTrailingSpacesSmartFilter.checkTrailingSpacesBlock(node)
            override fun keepLineTrailingSpaces(node: ASTNode, keepTrailingSpaces: Int) = this@MdStripTrailingSpacesSmartFilter.keepTrailingSpaces(node, keepTrailingSpaces)
            override fun getCodeStyleSettings(): MdCodeStyleSettings = codeStyle
            override fun keepLineTrailingSpaces(startLine: Int, endLine: Int, keepTrailingSpaces: Int) = this@MdStripTrailingSpacesSmartFilter.keepTrailingSpaces(startLine, endLine, keepTrailingSpaces)
        }

        CORE_FILTER_EXTENSION.setStripTrailingSpacesFilters(filter)

        // let extensions set themselves up
        for (extension in MdStripTrailingSpacesExtension.EXTENSIONS.value) {
            extension.setStripTrailingSpacesFilters(filter)
        }

        visitor.visit(psiFile)
    }

    companion object {
        private val CORE_FILTER_EXTENSION: MdStripTrailingSpacesCoreExtension by lazy { MdStripTrailingSpacesCoreExtension() }
    }
}
