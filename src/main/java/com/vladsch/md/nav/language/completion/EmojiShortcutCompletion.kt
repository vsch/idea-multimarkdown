// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion

import com.intellij.codeInsight.completion.CompletionInitializationContext
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.vladsch.flexmark.ext.emoji.internal.EmojiShortcuts
import com.vladsch.md.nav.language.completion.util.MdCompletionContext
import com.vladsch.md.nav.language.completion.util.MoveCaretAfterCompletionDecorator
import com.vladsch.md.nav.language.completion.util.TextContext
import com.vladsch.md.nav.language.completion.util.WrappingDecorator
import com.vladsch.md.nav.psi.element.MdEmojiId
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdRenderingProfileManager
import com.vladsch.plugin.util.TestUtils
import com.vladsch.plugin.util.nullIf
import icons.MdEmojiIcons

class EmojiShortcutCompletion : MdElementCompletion {
    override fun getWantElement(element: PsiElement, elementPos: PsiElement, parameters: CompletionParameters, context: ProcessingContext): Boolean {
        val params = getContext(parameters.offset, element) ?: return false
        return wantParams(params, element, parameters.isAutoPopup)
    }

    private fun wantParams(params: TextContext, element: PsiElement, isAutoPopup: Boolean): Boolean {
        // need to see if have : before us in the element
        return completionContext.wantParams(params, element is MdEmojiId, isAutoPopup)
    }

    override fun duringCompletion(context: CompletionInitializationContext, element: PsiElement, elementPos: PsiElement): Boolean {
        val params = getContext(context.startOffset, element) ?: return false

        if (wantParams(params, element, context.invocationCount == 0)) {
            if (context.replacementOffset != params.replacementOffset) context.replacementOffset = params.replacementOffset
            return true
        }
        return false
    }

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet, element: PsiElement, containingFile: MdFile): Boolean {
        val params = getContext(parameters.offset, element) ?: return true
        val shortcutsType = MdRenderingProfileManager.getProfile(containingFile).parserSettings.emojiShortcutsType

        LOG.debug("Emoji: $params")

        val prefix = params.prefix.toString()
        val suffix = ": ".nullIf(params.hasEndMarker)

        @Suppress("NAME_SHADOWING")
        val resultSet = if (prefix.isNotEmpty()) resultSet.withPrefixMatcher(prefix) else resultSet

        for ((emojiShortcut, shortcut) in EmojiShortcuts.getEmojiShortcuts()) {
            shortcutsType.flexmarkType.getPreferred(shortcut.emojiCheatSheetFile, shortcut.githubFile) ?: continue // no file no completion

            var lookupElement: LookupElement = LookupElementBuilder.create(emojiShortcut).withCaseSensitivity(true)
                .withIcon(MdEmojiIcons.getEmojiIcon(emojiShortcut))
                .withTypeText(shortcut.category)

            if (suffix != null) {
                lookupElement = WrappingDecorator.withHiddenWrappingMods(lookupElement, null, suffix)
                lookupElement = MoveCaretAfterCompletionDecorator(lookupElement, 2, 1)
            }

            resultSet.addElement(lookupElement)
        }
        return true
    }

    fun getContext(startOffset: Int, element: PsiElement): TextContext? {
        return completionContext.getContext(startOffset, element, element is MdEmojiId)
    }

    companion object {
        private val LOG = MdElementCompletion.LOG_TRACE
        private val LOG_INFO = MdElementCompletion.LOG

        private const val NULL_CHAR = TestUtils.NULL_CHAR
        private const val PREFIX_CHAR = ':'
        private const val BEFORE_CHARS = "$NULL_CHAR \t\n"
        private const val AFTER_CHARS = "$NULL_CHAR \t\n$PREFIX_CHAR"
        private const val PREFIX_CHARS = "$PREFIX_CHAR \t"
        private const val END_MARKER_CHARS = "$PREFIX_CHAR"

        val completionContext: MdCompletionContext = object : MdCompletionContext(PREFIX_CHAR, PREFIX_CHARS, END_MARKER_CHARS) {
            override fun wantParams(params: TextContext, isDefault: Boolean, isAutoPopup: Boolean): Boolean {
                return isDefault || !isAutoPopup ||
                    // no auto-popup unless both colons are present, or have the right characters before and/or after
                    BEFORE_CHARS.contains(params.beforeStartChar) &&
                    (AFTER_CHARS.contains(params.afterCaretChar) ||
                        MdApplicationSettings.instance.documentSettings.toggleStylePunctuations.contains(params.afterCaretChar) ||
                        "^[a-z][a-z_]*".toRegex().find(params.prefix.toString()) != null // prefix starts with lower case
                        ) &&
                    AFTER_CHARS.contains(params.afterEndChar) &&
                    "^[a-z][a-z_]*".toRegex().find(params.prefix.toString()) != null // prefix starts with lower case
            }
        }
    }
}

