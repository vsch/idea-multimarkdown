// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.handlers

import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.actions.api.MdFormatElementHandler
import com.vladsch.md.nav.actions.handlers.util.AutoCharsContext
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.handlers.util.ParagraphContext
import com.vladsch.md.nav.language.MdCodeStyleSettings
import com.vladsch.md.nav.psi.element.MdFile

class BackspaceHandler : BackspaceHandlerDelegate() {
    private var skipCharDeleted = false

    override fun beforeCharDeleted(c: Char, file: PsiFile, editor: Editor) {
        if (file is MdFile && editor.caretModel.caretCount == 1 && LookupManager.getInstance(file.project).activeLookup == null) {
            CaretContextInfo.withContext(file, editor, null, true) { caretContext ->
                caretContext.beforeBackspaceHandler()
                skipCharDeleted = false

                val autoCharsContext = AutoCharsContext.getContext(caretContext)
                if (autoCharsContext != null) {
                    if (autoCharsContext.isAutoTypeEnabled(c)) {
                        autoCharsContext.autoBackspaceChar()
                        skipCharDeleted = true
                    }
                }
            }
        }
    }

    override fun charDeleted(c: Char, file: PsiFile, editor: Editor): Boolean {
        for (handler in MdFormatElementHandler.EXTENSIONS.value) {
            if (handler.skipBackspaceHandler()) return false
        }

        if (file is MdFile && !skipCharDeleted && editor.caretModel.caretCount == 1 && LookupManager.getInstance(file.project).activeLookup == null) {
            return CaretContextInfo.withContextOr(file, editor, c, isDeleted = true, noContextValue = false, caretOffset = null) { caretContext ->
                caretContext.backspaceHandler()

                val styleSettings = MdCodeStyleSettings.getInstance(file)
                val isFormatRegion = caretContext.isFormatRegion(caretContext.caretOffset)

                if (isFormatRegion) {
                    if (caretContext.editOpDelta <= 0 && styleSettings.isWrapOnTyping) {
                        // see if need to wrap lines to margins on this line
                        // header looking lines, table looking lines will not get wrapped
                        caretContext.isForceDelete = true
                        val paragraphContext = ParagraphContext.getContext(caretContext)
                        @Suppress("IfThenToSafeAccess")
                        if (paragraphContext != null) {
                            paragraphContext.adjustParagraph(true)
                        }
                    }
                }

                true
            }
        }
        return false
    }
}
