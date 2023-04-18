// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util

import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.lookup.LookupElement

/**
 * NOTE: The IDE currently does not 'select and finish' as promised but will try to add the selected
 *   item's chars up to the completionChar and restart completion, so this class works in cooperation
 *   with [MdCompletionCharFilter] to prevent that from happening.
 */
class MdPreventPartialCompletionImpl(private val keyHolder: UserDataKeyHolder, charFiltering: MdCompletionCharFiltering)
    : MdPreventPartialCompletion, MdCompletionCharFiltering by charFiltering {

    private var triggered = false

    override fun preventPartialCompletionFor(neverMatchPrefix: String) {
        if (triggered) return

        triggered = true
        for (key in keyHolder.keys) {
            if (key.toString() == "LookupArrangerMatcher") {
                val delegate = keyHolder.getUserData(key)
                keyHolder.putUserData(key, null)
                keyHolder.putUserData(key, PrefixMatcherDelegate(delegate as PrefixMatcher, neverMatchPrefix))
                break
            }
        }
    }

    class PrefixMatcherDelegate(val delegate: PrefixMatcher, val neverMatchPrefix: String) : PrefixMatcher(delegate.prefix) {
        override fun prefixMatches(element: LookupElement): Boolean = delegate.prefixMatches(element)
        override fun isStartMatch(element: LookupElement?): Boolean = delegate.isStartMatch(element)
        override fun isStartMatch(name: String?): Boolean = delegate.isStartMatch(name)
        override fun matchingDegree(string: String?): Int = delegate.matchingDegree(string)
        override fun prefixMatches(name: String): Boolean = delegate.prefixMatches(name)

        override fun cloneWithPrefix(prefix: String): PrefixMatcher =
            if (prefix == neverMatchPrefix) NEVER_MATCHER
            else delegate.cloneWithPrefix(prefix)
    }

    companion object {
        val NEVER_MATCHER = object : PrefixMatcher("\u0000") {
            override fun prefixMatches(element: LookupElement): Boolean = false
            override fun isStartMatch(element: LookupElement?): Boolean = false
            override fun isStartMatch(name: String?): Boolean = false
            override fun matchingDegree(string: String?): Int = 0
            override fun prefixMatches(name: String): Boolean = false
            override fun cloneWithPrefix(prefix: String): PrefixMatcher = this
        }
    }
}
