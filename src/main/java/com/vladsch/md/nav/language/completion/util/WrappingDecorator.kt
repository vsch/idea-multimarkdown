// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementDecorator
import com.intellij.psi.impl.source.PostprocessReformattingAspect

open class WrappingDecorator(delegate: LookupElement
    , val prefixString: String?
    , val deletePrefixLength: Int
    , val hiddenPrefixString: String?
    , val suffixString: String?
    , val deleteSuffixLength: Int      // number of tail characters of the suffix to delete
    , val hiddenSuffixString: String?
    , private val autoCompletionPolicy: AutoCompletionPolicy?
    , val deleteAdjuster: DeleteAdjuster?
    , val deleteSuffixPolicy: DeleteSuffixPolicy?

) : LookupElementDecorator<LookupElement>(delegate) {

    override fun getAutoCompletionPolicy(): AutoCompletionPolicy {
        return autoCompletionPolicy ?: super.getAutoCompletionPolicy()
    }

    fun withAutoCompletionPolicy(autoCompletionPolicy: AutoCompletionPolicy): WrappingDecorator {
        if (autoCompletionPolicy == this.autoCompletionPolicy) return this
        return WrappingDecorator(delegate, prefixString, deletePrefixLength, hiddenPrefixString, suffixString, deleteSuffixLength, hiddenSuffixString, autoCompletionPolicy, null, null)
    }

    fun withDeleteAdjuster(deleteAdjuster: DeleteAdjuster?): WrappingDecorator {
        if (deleteAdjuster == this.deleteAdjuster) return this
        return WrappingDecorator(delegate, prefixString, deletePrefixLength, hiddenPrefixString, suffixString, deleteSuffixLength, hiddenSuffixString, autoCompletionPolicy, deleteAdjuster, null)
    }

    fun withDeleteSuffixOnInsert(): WrappingDecorator {
        if (DeleteSuffixPolicy.ON_INSERT == deleteSuffixPolicy) return this
        return WrappingDecorator(delegate, prefixString, deletePrefixLength, hiddenPrefixString, suffixString, deleteSuffixLength, hiddenSuffixString, autoCompletionPolicy, deleteAdjuster, DeleteSuffixPolicy.ON_INSERT)
    }

    fun withDeleteSuffixOnReplace(): WrappingDecorator {
        if (DeleteSuffixPolicy.ON_REPLACE == deleteSuffixPolicy) return this
        return WrappingDecorator(delegate, prefixString, deletePrefixLength, hiddenPrefixString, suffixString, deleteSuffixLength, hiddenSuffixString, autoCompletionPolicy, deleteAdjuster, DeleteSuffixPolicy.ON_REPLACE)
    }

    override fun getLookupString(): String {
        val lookupString = super.getLookupString()
        val result = prefixString.orEmpty() + lookupString + suffixString.orEmpty()
        return result
    }

    override fun getAllLookupStrings(): Set<String> {
        return setOf(lookupString)
    }

    override fun handleInsert(context: InsertionContext) {
        val delegate = delegate
//        val identifierEndOffset = context.getOffset(CompletionInitializationContext.IDENTIFIER_END_OFFSET)
        delegate.handleInsert(context)

        if (!isCaseSensitive || deleteAdjuster != null || deletePrefixLength > 0 || deleteSuffixLength > 0 || prefixString != null && !prefixString.isEmpty() || suffixString != null && !suffixString.isEmpty()) {
            val document = context.editor.document
            val chars = document.charsSequence

            val headOffset = context.startOffset
            val tailOffset = context.tailOffset
            val prefixedLength = prefixString?.length ?: 0
            val suffixedLength = suffixString?.length ?: 0
            var tailDelta = 0
            val adjustedDeletePrefix = deletePrefixLength + (deleteAdjuster?.getDeletePrefixDelta(chars, headOffset + prefixedLength, context) ?: 0)
            val adjustedDeleteSuffix = (if (when (deleteSuffixPolicy) {
                    DeleteSuffixPolicy.ON_REPLACE -> context.completionChar == '\t'
                    DeleteSuffixPolicy.ON_INSERT -> context.completionChar != '\t'
                    else -> true
                }) deleteSuffixLength else 0) +
                (deleteAdjuster?.getDeleteSuffixDelta(chars, tailOffset + prefixedLength + suffixedLength, context) ?: 0)

            if (!isCaseSensitive && tailOffset - headOffset == lookupString.length) {
                // this is used to completely replace the IDE inserted element
                document.replaceString(headOffset, tailOffset, lookupString)
            }

            if (headOffset >= 0 && (headOffset + prefixedLength + adjustedDeletePrefix <= document.textLength)) {
                if (adjustedDeletePrefix > 0) {
                    document.deleteString(headOffset + prefixedLength, headOffset + prefixedLength + adjustedDeletePrefix)
                    tailDelta -= adjustedDeletePrefix
                }

                if (hiddenPrefixString != null) {
                    document.insertString(headOffset + prefixedLength, hiddenPrefixString)
                    tailDelta += hiddenPrefixString.length
                }
            }

            var tailFinal = tailOffset + tailDelta
            if (tailFinal >= suffixedLength && tailFinal + adjustedDeleteSuffix <= document.textLength) {
                if (adjustedDeleteSuffix > 0) {
                    document.deleteString(tailFinal + suffixedLength - adjustedDeleteSuffix, tailFinal + suffixedLength)
                    tailFinal = tailFinal + suffixedLength - adjustedDeleteSuffix
                }
                if (hiddenSuffixString != null) {
                    document.insertString(tailFinal, hiddenSuffixString)
                    tailFinal += hiddenSuffixString.length
                }

                context.tailOffset = tailFinal
            }
        } else if (hiddenPrefixString != null || hiddenSuffixString != null) {
            val document = context.editor.document
            var tailFinal = context.tailOffset
            if (hiddenSuffixString != null) {
                document.insertString(context.tailOffset, hiddenSuffixString)
                tailFinal += hiddenSuffixString.length
                context.tailOffset = tailFinal
            }
            if (hiddenPrefixString != null) document.insertString(context.startOffset, hiddenPrefixString)
        }

        PostprocessReformattingAspect.getInstance(context.project).doPostponedFormatting()
    }

    companion object {
        fun withPrefixMods(delegate: LookupElement, prefixString: String?, deletePrefixLength: Int): WrappingDecorator {
            return WrappingDecorator(delegate, prefixString, deletePrefixLength, null, null, 0, null, null, null, null)
        }

        fun withPrefixMods(delegate: LookupElement, prefixString: String?, deletePrefixLength: Int, deleteAdjuster: DeleteAdjuster): WrappingDecorator {
            return WrappingDecorator(delegate, prefixString, deletePrefixLength, null, null, 0, null, null, deleteAdjuster, null)
        }

        fun withSuffixMods(delegate: LookupElement, suffixString: String?, deleteSuffixLength: Int): WrappingDecorator {
            return WrappingDecorator(delegate, null, 0, null, suffixString, deleteSuffixLength, null, null, null, null)
        }

        fun withSuffixMods(delegate: LookupElement, suffixString: String?, deleteSuffixLength: Int, deleteAdjuster: DeleteAdjuster): WrappingDecorator {
            return WrappingDecorator(delegate, null, 0, null, suffixString, deleteSuffixLength, null, null, deleteAdjuster, null)
        }

        fun withWrappingMods(delegate: LookupElement, prefixString: String?, deletePrefixLength: Int, suffixString: String?, deleteSuffixLength: Int): WrappingDecorator {
            return WrappingDecorator(delegate, prefixString, deletePrefixLength, null, suffixString, deleteSuffixLength, null, null, null, null)
        }

        fun withWrappingMods(delegate: LookupElement, prefixString: String?, deletePrefixLength: Int, suffixString: String?, deleteSuffixLength: Int, deleteAdjuster: DeleteAdjuster): WrappingDecorator {
            return WrappingDecorator(delegate, prefixString, deletePrefixLength, null, suffixString, deleteSuffixLength, null, null, deleteAdjuster, null)
        }

        fun withHiddenWrappingMods(delegate: LookupElement, hiddenPrefixString: String?, hiddenSuffixString: String?): WrappingDecorator {
            return WrappingDecorator(delegate, null, 0, hiddenPrefixString, null, 0, hiddenSuffixString, null, null, null)
        }

        fun withHiddenWrappingMods(delegate: LookupElement, hiddenPrefixString: String?, deletePrefixLength: Int): WrappingDecorator {
            return WrappingDecorator(delegate, null, deletePrefixLength, hiddenPrefixString, null, 0, null, null, null, null)
        }

        fun withAutoCompletion(delegate: LookupElement, autoCompletionPolicy: AutoCompletionPolicy): WrappingDecorator {
            return when (delegate) {
                is WrappingDecorator -> delegate.withAutoCompletionPolicy(autoCompletionPolicy)
                else -> WrappingDecorator(delegate, null, 0, null, null, 0, null, autoCompletionPolicy, null, null)
            }
        }

        fun withDeleteAdjuster(delegate: LookupElement, deleteAdjuster: DeleteAdjuster): WrappingDecorator {
            return when (delegate) {
                is WrappingDecorator -> delegate.withDeleteAdjuster(deleteAdjuster)
                else -> WrappingDecorator(delegate, null, 0, null, null, 0, null, null, deleteAdjuster, null)
            }
        }
    }
}
