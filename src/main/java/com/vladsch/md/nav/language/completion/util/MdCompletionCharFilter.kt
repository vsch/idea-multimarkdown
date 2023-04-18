// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util

import com.intellij.codeInsight.lookup.CharFilter
import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.impl.LookupImpl
import com.intellij.openapi.util.ClassConditionKey
import com.intellij.util.containers.ContainerUtil

class MdCompletionCharFilter : CharFilter() {
    override fun acceptChar(c: Char, prefixLength: Int, lookup: Lookup): CharFilter.Result? {
        val item = lookup.currentItem ?: return null
        if (!lookup.isCompletion) return null
        val completionCharFiltering = item.`as`(COMPLETION_CHAR_FILTERING) ?: return null

        val lookupContext = MdLookupContext.get(lookup.editor)
        val isAutoPopup = lookupContext?.isAutoPopup ?: false
        lookup as LookupImpl

        if (completionCharFiltering.isSelectAndFinishChar(c, isAutoPopup)) {
            // here we only let the IDE insert the text supplied and really select item and finish lookup
            if (completionCharFiltering.isPreventingPartialCompletions) {
                val matcher = lookup.itemMatcher(item)
                val expanded = matcher.cloneWithPrefix(matcher.prefix + lookup.additionalPrefix + c)
                completionCharFiltering.preventPartialCompletionFor(expanded.prefix) // now trying to do partial completion will fail
            }

            return CharFilter.Result.SELECT_ITEM_AND_FINISH_LOOKUP
        }

        if (completionCharFiltering.isAddToPrefixChar(c) && willHaveMatchAfterAppendingChar(lookup, c)) {
            return CharFilter.Result.ADD_TO_PREFIX
        }

        return CharFilter.Result.HIDE_LOOKUP
    }

    companion object {
        val COMPLETION_CHAR_FILTERING: ClassConditionKey<MdPreventPartialCompletion> = ClassConditionKey.create(MdPreventPartialCompletion::class.java)

        private fun willHaveMatchAfterAppendingChar(lookup: LookupImpl, c: Char): Boolean {
            return ContainerUtil.exists(lookup.items) { matchesAfterAppendingChar(lookup, it, c) }
        }

        private fun matchesAfterAppendingChar(lookup: LookupImpl, item: LookupElement, c: Char): Boolean {
            val matcher = lookup.itemMatcher(item)
            return matcher.cloneWithPrefix(matcher.prefix + lookup.additionalPrefix + c).prefixMatches(item)
        }
    }
}
