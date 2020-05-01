// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.language

import com.intellij.codeInsight.completion.CompletionInitializationContext
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.md.nav.language.completion.MdElementCompletion
import com.vladsch.md.nav.language.completion.util.MdCompletionContext
import com.vladsch.md.nav.language.completion.util.MoveCaretAfterCompletionDecorator
import com.vladsch.md.nav.language.completion.util.TextContext
import com.vladsch.md.nav.language.completion.util.WrappingDecorator
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.flex.psi.FlexmarkExample
import com.vladsch.plugin.util.TestUtils
import com.vladsch.plugin.util.ifElse

class FlexmarkOptionsKeywordCompletion : MdElementCompletion {
    override fun getWantElement(element: PsiElement, elementPos: PsiElement, parameters: CompletionParameters, context: ProcessingContext): Boolean {
        val params = getContext(parameters.offset, element) ?: return false
        return wantParams(params, element, parameters.isAutoPopup)
    }

    private fun wantParams(params: TextContext, element: PsiElement, isAutoPopup: Boolean): Boolean {
        // need to see if have : before us in the element
        if (element !is FlexmarkExample) return false
        return completionContext.wantParams(params, true, isAutoPopup)
    }

    override fun duringCompletion(context: CompletionInitializationContext, element: PsiElement, elementPos: PsiElement): Boolean {
        val params = getContext(context.startOffset, element) ?: return false

        if (wantParams(params, element, context.invocationCount == 0)) {
            val replacementOffset = element.text.indexOf("\n")
            if (context.replacementOffset != replacementOffset) context.replacementOffset = replacementOffset
            return true
        }
        return false
    }

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet, element: PsiElement, containingFile: MdFile): Boolean {
        val params = getContext(parameters.offset, element) ?: return true
        val pos = params.prefix.lastIndexOfAny(CharPredicate.anyOf(PREFIX_CHARS))
        val prefix = if (pos == -1) params.prefix else params.prefix.subSequence(pos + 1)
        val suffix = element.text.replace(TestUtils.DUMMY_IDENTIFIER, "").substring(params.prefix.length)
        val addParens = !suffix.startsWith('(')

        @Suppress("NAME_SHADOWING")
        val resultSet = if (prefix.isNotEmpty) resultSet.withPrefixMatcher(prefix.toString()) else resultSet

        val lookupElementBuilder = LookupElementBuilder.create("options").withCaseSensitivity(false)
        var lookupElement: LookupElement = WrappingDecorator.withSuffixMods(lookupElementBuilder, addParens.ifElse("()", ""), 0)
        lookupElement = MoveCaretAfterCompletionDecorator(lookupElement, addParens.ifElse(-1, 1), 1)
        resultSet.addElement(lookupElement)
        return true
    }

    fun getContext(startOffset: Int, element: PsiElement): TextContext? {
        return completionContext.getContext(startOffset, element, element is FlexmarkExample)
    }

    companion object {
        private val LOG = MdElementCompletion.LOG_TRACE
        private val LOG_INFO = MdElementCompletion.LOG

        private const val NULL_CHAR = TestUtils.NULL_CHAR
        private const val PREFIX_CHAR = NULL_CHAR
        private const val BEFORE_CHARS = "$NULL_CHAR \t\u00A0"
        private const val AFTER_CHARS = "$BEFORE_CHARS(\n"
        private const val PREFIX_CHARS = BEFORE_CHARS
        private const val END_MARKER_CHARS = AFTER_CHARS

        val completionContext: MdCompletionContext = object : MdCompletionContext(PREFIX_CHAR, PREFIX_CHARS, END_MARKER_CHARS) {
            override fun wantParams(params: TextContext, isDefault: Boolean, isAutoPopup: Boolean): Boolean {
                val pos = params.prefix.lastIndexOfAny(CharPredicate.anyOf(PREFIX_CHARS))
                val prefix = (if (pos == -1) params.prefix else params.prefix.subSequence(pos + 1)).toString()
                return "options".startsWith(prefix) && "options" != prefix &&
                    BEFORE_CHARS.contains(params.beforeStartChar) &&
                    AFTER_CHARS.contains(params.afterEndChar)
            }
        }
    }
}
