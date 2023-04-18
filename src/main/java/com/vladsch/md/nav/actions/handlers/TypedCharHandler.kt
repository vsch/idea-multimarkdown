// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.handlers

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.actions.api.MdFormatElementHandler
import com.vladsch.md.nav.actions.handlers.util.AutoCharsContext
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.handlers.util.ParagraphContext
import com.vladsch.md.nav.language.MdCodeStyleSettings
import com.vladsch.md.nav.psi.element.MdFile

class TypedCharHandler : TypedHandlerDelegate() {

    override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
        return Result.CONTINUE
    }

    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        var result = Result.CONTINUE

        if (file is MdFile && editor.caretModel.caretCount == 1 && LookupManager.getInstance(file.project).activeLookup == null) {
            CaretContextInfo.withContext(file, editor, c, false) { caretContext ->
                caretContext.charTypedHandler()
                var useCaretContext = caretContext
                val styleSettings = MdCodeStyleSettings.getInstance(file)

                val isFormatRegion = caretContext.isFormatRegion(caretContext.caretOffset)

                if (isFormatRegion) {
                    val autoCharsContext = AutoCharsContext.getContext(caretContext)
                    if (autoCharsContext != null && autoCharsContext.canAutoType(c)) {
                        val isSp = c == '\t' || c == ' '
                        val nextC = autoCharsContext.context.afterCaretChar
                        val autoTypeChar = if (isSp) nextC else c

                        if (autoCharsContext.isAutoTypeEnabled(autoTypeChar)) {
                            autoCharsContext.autoTypeChar(c)
                            if (styleSettings.isWrapOnTyping) {
                                PsiDocumentManager.getInstance(project).commitDocument(caretContext.document)
                                CaretContextInfo.withContextOrNull(file,editor,c,false) { newCaretContext->
                                    if (newCaretContext != null) {
                                        caretContext.addSubContext(newCaretContext)
                                        useCaretContext = newCaretContext
                                    }
                                }
                            }
                        }
                    }

                    for (handler in MdFormatElementHandler.EXTENSIONS.value) {
                        if (handler.skipWrapOnTyping()) return@withContext
                    }

                    if (styleSettings.isWrapOnTyping) {
                        val paragraphContext = ParagraphContext.getContext(useCaretContext)
                        if (paragraphContext != null) {
                            paragraphContext.adjustParagraph(true)
                            return@withContext
                        }
                    }
                }
            }
        }
        return result
    }
}
