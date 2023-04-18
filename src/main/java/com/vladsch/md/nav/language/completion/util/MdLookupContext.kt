// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.completion.util

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Key

class MdLookupContext {
    var isAutoPopup: Boolean = false
        private set

    var hadAutoPopup = false
        private set

    private var _definitionContexts: MdReferenceDefinitionContexts? = null

    val definitionContexts: MdReferenceDefinitionContexts
        get() {
            var definitionContexts = _definitionContexts
            if (definitionContexts == null) {
                definitionContexts = MdReferenceDefinitionContexts()
                _definitionContexts = definitionContexts
            }
            return definitionContexts
        }

    private var myInvocationCount = -2
    private var myCompletionId: String? = null

    var startOffset = -1
        private set

    val effectiveInvocationCount: Int
        get() = if (myInvocationCount <= 0) 0 else if (hadAutoPopup) myInvocationCount else myInvocationCount - 1

    private fun setCompletion(completionId: String, startOffset: Int, invocationCount: Int) {
        if (completionId != myCompletionId || this.startOffset != startOffset || this.myInvocationCount + 1 != invocationCount) {
            // new completion, reset hadAutoPopup
            hadAutoPopup = false
        }

        myCompletionId = completionId
        this.startOffset = startOffset
        myInvocationCount = invocationCount

        isAutoPopup = myInvocationCount == 0
        hadAutoPopup = hadAutoPopup || isAutoPopup
        _definitionContexts = null
    }

    companion object {
        private val LOOKUP_CONTEXT_KEY = Key<MdLookupContext>("LOOKUP_CONTEXT_KEY")

        @JvmStatic
        fun get(editor: Editor): MdLookupContext? {
            return editor.getUserData(LOOKUP_CONTEXT_KEY)
        }

        @JvmStatic
        fun clear(editor: Editor) {
            editor.putUserData(LOOKUP_CONTEXT_KEY, null)
        }

        fun getOrCreate(editor: Editor): MdLookupContext {
            var lookupContext = get(editor)
            if (lookupContext == null) {
                lookupContext = MdLookupContext()
                editor.putUserData(LOOKUP_CONTEXT_KEY, lookupContext)
            }
            return lookupContext
        }

        @JvmStatic
        fun set(completionClass: Class<*>, parameters: CompletionParameters): MdLookupContext {
            val lookupContext = getOrCreate(parameters.editor)
            lookupContext.setCompletion(completionClass.name, parameters.offset, parameters.invocationCount)
            return lookupContext
        }
    }
}
