// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.BasedSequence.NULL
import com.vladsch.flexmark.util.sequence.BasedSequence.SPACE
import com.vladsch.flexmark.util.sequence.LineAppendable
import com.vladsch.flexmark.util.sequence.RepeatedSequence
import com.vladsch.md.nav.actions.handlers.util.ParagraphContext
import com.vladsch.md.nav.actions.handlers.util.PostEditAdjust
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider
import com.vladsch.md.nav.psi.util.BlockPrefixes
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.util.format.LinePrefixMatcher
import com.vladsch.md.nav.util.format.SpacePrefixMatcher
import com.vladsch.plugin.util.maxLimit
import com.vladsch.plugin.util.psi.isTypeIn

open class MdIndentingCompositeImpl(node: ASTNode) : MdCompositeImpl(node), MdIndentingComposite {
    companion object {
        @JvmStatic
        fun indentSpaces(count: Int): String {
            return RepeatedSequence.ofSpaces(count).toString()
        }

        @JvmStatic
        fun getItemBlock(parent: PsiElement): PsiElement? {
            return MdPsiImplUtil.getItemBlock(parent)
        }

        @JvmStatic
        fun continuationIndent(firstLineTextIndent: Int, parentTextIndent: Int, editContext: PsiEditContext): Int {
            return MdBlockPrefixProvider.getContinuationIndent(firstLineTextIndent, parentTextIndent, editContext)
        }

        /**
         *  Remove prefix pattern, keeping MARKDOWN_START_LINE_CHAR at start of resulting line
         */
        @JvmStatic
        fun removeLinePrefix(lines: LineAppendable, indentColumns: IntArray, keepFirstLinePrefix: Boolean, editContext: PsiEditContext, linePrefixMatcher: LinePrefixMatcher, prefixDelta: Int) {
            assert(lines.lineCount == indentColumns.size)

            var isFirstChildLine = keepFirstLinePrefix
            for (info in lines) {
                val line = info.text
                val indentColumn = indentColumns[info.index]

                if (isFirstChildLine) {
                    isFirstChildLine = false
                } else {
                    val hadStartLine = line.isNotEmpty && line[0] == ParagraphContext.MARKDOWN_START_LINE_CHAR
                    val newColumn = linePrefixMatcher.contentColumn(line.subSequence(if (hadStartLine) 1 else 0, line.length), indentColumn, editContext) + prefixDelta

                    if (newColumn <= indentColumn) {
                        // no change
                    } else {
                        // remove all before newColumn, tabs are expanded to 4 chars and only removed if no spaces are left from the tabs.
                        var index = if (hadStartLine) 1 else 0
                        var column = indentColumn

                        while (column < newColumn) {
                            if (line[index] == '\t') {
                                val spaces = SpacePrefixMatcher.columnsToNextTabStop(column)
                                if (spaces + column <= newColumn) {
                                    // all spaces used up, use up tab
                                    column += spaces
                                    index++
                                } else {
                                    // some spaces of the expanded tab remaining, keep tab for next prefix
                                    column = newColumn
                                }
                            } else {
                                column++
                                index++
                            }
                        }

                        // remove the prefix from text and add it to prefix
                        lines.setPrefixLength(info.index, info.prefixLength + index)
                        indentColumns[info.index] = newColumn
                    }
                }
            }
        }

        @JvmStatic
        fun actualTextPrefix(element: MdIndentingComposite, editContext: PsiEditContext, addTrailingSpace: Boolean): BasedSequence {
            var node = element.firstChild?.node ?: return if (addTrailingSpace) SPACE else NULL
            val startIndex = editContext.postEditNodeStart(node)
            var endIndex: Int

            while (true) {
                if (node.elementType == MdTypes.EOL || node.isTypeIn(MdTokenSets.NON_WRAPPING_BLOCK_ELEMENTS) || element.isTextStart(node)) {
                    endIndex = editContext.postEditNodeStart(node, PostEditAdjust.IF_WHITESPACE_CHAR)
                    break
                }
                endIndex = editContext.postEditNodeEnd(node, PostEditAdjust.IF_WHITESPACE_CHAR)
                node = node.treeNext ?: break
            }

            val text = editContext.charSequence
            val result = text.subSequence(startIndex, endIndex.maxLimit(text.length))
            val endsInSpace = result.endsWith(CharPredicate.SPACE_TAB)
            if (!addTrailingSpace || endsInSpace) {
                if (!endsInSpace) {
                    // see if there is a space following marker
                    if (endIndex < text.length && (text[endIndex] == ' ' || text[endIndex] == '\t')) {
                        return result.append(SPACE)
                    }
                }
                return result
            } else {
                return result.append(SPACE)
            }
        }

        @JvmStatic
        fun actualTextPrefix(element: MdIndentingComposite, addTrailingSpace: Boolean): BasedSequence {
            return actualTextPrefix(element, PsiEditAdjustment(element.containingFile), addTrailingSpace)
        }

        @JvmStatic
        fun contentIndent(element: MdIndentingComposite): Int {
            return actualTextPrefix(element, PsiEditAdjustment(element.containingFile), true).length
        }

        @JvmStatic
        fun isEmptyItemText(element: MdIndentingComposite): Boolean {
            var node = element.firstChild?.node ?: return true
            while (true) {
                if (node.elementType == MdTypes.EOL || node.isTypeIn(MdTokenSets.NON_WRAPPING_BLOCK_ELEMENTS)) break
                if (element.isTextStart(node)) return false
                node = node.treeNext ?: break
            }
            return true
        }

        @JvmStatic
        fun isTextStartNode(node: ASTNode): Boolean {
            return node.elementType === MdTypes.TEXT_BLOCK || node.elementType === MdTypes.EOL || node.elementType === MdTypes.BLANK_LINE || node.isTypeIn(MdTokenSets.BLOCK_ELEMENT_SET)
        }
    }

    override fun isFirstItemBlock(element: PsiElement): Boolean {
        // test if it is the first child, or the first Text block, or paragraph element when the first child is a leaf element
        return getItemBlock(this) === element // || element.node.elementType == MultiMarkdownTypes.PARAGRAPH_BLOCK && firstTextBlock.parent === element)
    }

    override fun actualItemPrefix(editContext: PsiEditContext): BasedSequence {
        return editContext.nodeText(firstChild.node)
    }

    override fun isFirstItemBlockPrefix(element: PsiElement): Boolean {
        return isFirstItemBlock(element)
    }

    override fun actualTextPrefix(editContext: PsiEditContext, addTrailingSpace: Boolean): BasedSequence {
        return actualTextPrefix(this, editContext, addTrailingSpace)
    }

    override fun actualTextPrefix(addTrailingSpace: Boolean): BasedSequence {
        return actualTextPrefix(this, addTrailingSpace)
    }

    override fun contentIndent(): Int {
        return contentIndent(this)
    }

    override fun isTextStart(node: ASTNode): Boolean {
        return isTextStartNode(node)
    }

    override fun isEmptyText(): Boolean {
        return isEmptyItemText(this)
    }

    final override fun itemPrefixes(parentPrefixes: BlockPrefixes?, editContext: PsiEditContext): BlockPrefixes {
        return MdBlockPrefixProvider.getBlockPrefixes(this, parentPrefixes, editContext)
    }

    override fun getPrefixMatcher(editContext: PsiEditContext): LinePrefixMatcher {
        return SpacePrefixMatcher.maxSpaces(4)
    }

    override fun removeLinePrefix(lines: LineAppendable, indentColumns: IntArray, isFirstChild: Boolean, editContext: PsiEditContext) {
        removeLinePrefix(lines, indentColumns, false, editContext, getPrefixMatcher(editContext), 0)
    }
}
