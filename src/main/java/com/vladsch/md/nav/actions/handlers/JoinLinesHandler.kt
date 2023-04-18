// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.handlers

import com.intellij.codeInsight.editorActions.JoinLinesHandlerDelegate
import com.intellij.codeInsight.editorActions.JoinRawLinesHandlerDelegate
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.element.MdIndentingComposite
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTokenSets
import com.vladsch.md.nav.psi.util.MdTypes
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.plugin.util.psi.isTypeOf
import java.util.regex.Pattern

class JoinLinesHandler : JoinRawLinesHandlerDelegate {
    override fun tryJoinLines(doc: Document, psiFile: PsiFile, start: Int, end: Int): Int {
        return JoinLinesHandlerDelegate.CANNOT_JOIN
    }

    override fun tryJoinRawLines(document: Document, psiFile: PsiFile, start: Int, end: Int): Int {
        if (psiFile !is MdFile || !MdApplicationSettings.instance.documentSettings.joinStripPrefix) return JoinLinesHandlerDelegate.CANNOT_JOIN

        val NOT_HANDLED = JoinLinesHandlerDelegate.CANNOT_JOIN - 1
        var result = NOT_HANDLED

        // strip next line's prefixes
        val element = psiFile.findElementAt(end)
        if (element != null) {
            var useElement = element

            while (useElement != null && useElement is LeafPsiElement) {
                useElement = useElement.parent
            }

            if (useElement != null) {
                val editors = EditorFactory.getInstance().getEditors(document, psiFile.project)
                val editor = editors.find { it.document === document }
                if (editor != null) {
                    val chars = document.charsSequence
                    val psiNullAdjustment = PsiEditAdjustment(psiFile, chars)

                    result = CaretContextInfo.withContextOr(psiFile, editor, null, false, NOT_HANDLED, end) { caretContext ->
                        val wrappingContext = caretContext.wrappingContext

                        if (wrappingContext != null) {
                            // have wrapping context
                            val elementLine = caretContext.caretLine - wrappingContext.firstLine
                            val removePrefix = if (elementLine == 0) {
                                // first line of paragraph, use first line prefix
                                wrappingContext.firstPrefixText()
                            } else {
                                // here we need to strip out the prefixes from continuation lines, block quotes cause headaches because the prefix is optional
                                val prefixedLines = MdPsiImplUtil.linesForWrapping(useElement!!, false, false, false, psiNullAdjustment)

                                if (elementLine < prefixedLines.lineCount) {
                                    prefixedLines[elementLine].prefix
                                } else {
                                    wrappingContext.prefixText()
                                }
                            }

                            if (!removePrefix.isBlank()) {
                                // remove prefix
                                val text = chars.subSequence(start, end + removePrefix.length)
                                val skipStart = BasedSequence.of(text).countLeadingNot(CharPredicate.WHITESPACE)

                                val sb = StringBuilder()
                                var hadDigit = false
                                removePrefix.forEach { c ->
                                    if (c in '0' .. '9') {
                                        if (!hadDigit) {
                                            sb.append("\\d+")
                                            hadDigit = true
                                        }
                                    } else if (c == ' ') {
                                        sb.append("\\s*")
                                        hadDigit = false
                                    } else {
                                        sb.append("\\Q").append(c).append("\\E")
                                        hadDigit = false
                                    }
                                }

                                val prefixText = sb.toString()
                                val prefixPattern = Pattern.compile("^($prefixText[ \t]*)")

                                val endSequence = chars.subSequence(end, chars.length)
                                val matcher = prefixPattern.matcher(endSequence)

                                if (matcher.find()) {
                                    if (matcher.start(1) == 0) {
                                        document.deleteString(start + skipStart, end + matcher.end(1) - matcher.start(1))
                                        document.insertString(start + skipStart, " ")
                                        return@withContextOr start + skipStart + 1
                                    }
                                }
                            }

                            JoinLinesHandlerDelegate.CANNOT_JOIN
                        } else {
                            NOT_HANDLED
                        }
                    }

                    if (result == NOT_HANDLED) {
                        // blank lines with prefixes cause issues and need to be handled separately
                        if (useElement.node.elementType == MdTypes.BLANK_LINE) {
                            useElement = useElement.parent
                        }

                        if (useElement.isTypeOf(MdTokenSets.WRAPPING_BLOCK_ELEMENTS)) {
                            // delete the prefix for the element
                            if (useElement is MdIndentingComposite) {
                                val prefix = useElement.actualItemPrefix(psiNullAdjustment)
                                if (!prefix.isBlank()) {
                                    // remove prefix
                                    var length = prefix.length
                                    val text = chars.subSequence(start, end + length)
                                    val skipStart = BasedSequence.of(text).countLeadingNot(CharPredicate.WHITESPACE)
                                    if (length > 0 && text.isNotEmpty() && text[text.length - 1] == '\n') length--
                                    document.deleteString(start + skipStart, end + length)
                                    document.insertString(start + skipStart, " ")
                                    return start + skipStart + 1
                                }
                            }
                        }
                    }
                }
            }
        }

        return if (result == NOT_HANDLED) JoinLinesHandlerDelegate.CANNOT_JOIN else result
    }
}
