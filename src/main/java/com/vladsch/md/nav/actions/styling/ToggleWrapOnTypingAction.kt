// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Toggleable
import com.intellij.openapi.project.DumbAware
import com.vladsch.md.nav.actions.handlers.util.CaretContextInfo
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder
import com.vladsch.md.nav.actions.styling.util.MdActionUtil
import com.vladsch.md.nav.language.MdCodeStyleSettings

internal class ToggleWrapOnTypingAction : AnAction(), DumbAware, Toggleable {
    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = MdActionUtil.findMarkdownEditor(e)
        val psiFile = MdActionUtil.getPsiFile(e)

        val conditionBuilder = DisabledConditionBuilder(e, this)
            .notNull(project)
            .notNull(editor)
            .notNull(psiFile)
            .and {
                if (editor != null) {
                    CaretContextInfo.withContextOrNull(psiFile!!, editor, null, false, editor.caretModel.primaryCaret.offset) { caretContext ->
                        if (caretContext != null) {
                            val isFormatRegion = caretContext.isFormatRegion(caretContext.caretOffset)
                            it.and(isFormatRegion, "Caret in non-formatting region")
                        }
                    }
                }
            }

        if (conditionBuilder.isEnabled) {
//            val renderingProfile = MdRenderingProfileManager.getProfile(psiFile!!)
//            val styleSettings = renderingProfile.resolvedStyleSettings
            val styleSettings = MdCodeStyleSettings.getInstance(psiFile!!)

            conditionBuilder.and(!(editor!!.settings.isUseSoftWraps && styleSettings.formatWithSoftWrapType.isDisabled)
                , "Soft wraps are enabled"
                , "Wrap on typing is disabled when soft wraps are enabled (Editor > Code Style > Markdown)"
            )
            Toggleable.setSelected(e.presentation, styleSettings.isWrapOnTyping)
        }
        conditionBuilder.done(true)
        super.update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val psiFile = MdActionUtil.getPsiFile(e)
        if (project != null && psiFile != null) {
//            val renderingProfile = MdRenderingProfileManager.getProfile(psiFile)
//            val styleSettings = renderingProfile.resolvedStyleSettings
            val styleSettings = MdCodeStyleSettings.getInstance(psiFile)
            styleSettings.isWrapOnTyping = !styleSettings.isWrapOnTyping
        }
    }
}
