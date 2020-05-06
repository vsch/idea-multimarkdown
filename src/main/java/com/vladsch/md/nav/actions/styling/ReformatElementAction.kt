// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.vladsch.md.nav.MdBundle
import com.vladsch.md.nav.actions.api.MdFormatElementHandler
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.handlers.util.ParagraphContext
import com.vladsch.md.nav.actions.styling.util.MdActionUtil
import com.vladsch.md.nav.language.MdCodeStyleSettings
import com.vladsch.md.nav.util.format.FormatControlProcessor

class ReformatElementAction : AnAction() {
    override fun isDumbAware(): Boolean {
        return false
    }

    override fun update(e: AnActionEvent) {
        MdActionUtil.getConditionBuilder(e, this) { it, (_, editor, psiFile) ->
            if (it.isEnabled) {
                val styleSettings = MdCodeStyleSettings.getInstance(psiFile)
                it.and(!(editor.settings.isUseSoftWraps && styleSettings.formatWithSoftWrapType.isDisabled)
                    , "Wrap on typing is disabled when soft wraps are enabled (Editor > Code Style > Markdown)"
                )
                    .and(FormatControlProcessor(psiFile).isFormattingRegion(editor.caretModel.primaryCaret.offset)
                        , "Element is in a non-formatting region"
                    )
            }
        }.done()
    }

    override fun actionPerformed(e: AnActionEvent) {
        MdActionUtil.getProjectEditorPsiFile(e)?.let { (project, editor, psiFile) ->
            CaretContextInfo.withContext(psiFile, editor, null, false, editor.caretModel.primaryCaret.offset) { caretContext ->
                for (handler in MdFormatElementHandler.EXTENSIONS.value) {
                    if (handler.formatElement(caretContext)) {
                        return@withContext
                    }
                }

                // fall back to default of formatting paragraph
                val context = ParagraphContext.getContext(caretContext)
                if (context != null) {
                    if (caretContext.isFormatRegion(caretContext.caretOffset)) {
                        WriteCommandAction.runWriteCommandAction(project) {
                            context.adjustParagraph(true)
                        }
                    } else {
                        CaretContextInfo.showEditorTooltip(editor, MdBundle.message("tooltip.document.format.not-formatting-region")) { }
                    }
                } else {
                    CaretContextInfo.showEditorTooltip(editor, MdBundle.message("tooltip.document.format.not-paragraph")) { }
                }
            }
        }
    }
}
