// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.styling

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder
import com.vladsch.md.nav.settings.MdApplicationSettings
import com.vladsch.md.nav.settings.MdDebugSettings
import icons.MdIcons

class DebugTextBoundsToggleStateAction : ToggleAction(), DumbAware {

    override fun update(e: AnActionEvent) {
        val project = e.project
        val conditionBuilder = DisabledConditionBuilder(e, this)
            .notNull(project)

        conditionBuilder.done(false)

        e.presentation.icon = MdIcons.EditorActions.Debug_text_bounds
        super.update(e)
    }

    override fun isSelected(e: AnActionEvent): Boolean {
        return MdApplicationSettings.instance.debugSettings.debugFormatText
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        val newSettings = MdDebugSettings(MdApplicationSettings.instance.debugSettings)
        newSettings.debugFormatText = state
        MdApplicationSettings.instance.debugSettings = newSettings
    }

    companion object {
        internal val LOG = Logger.getInstance(DebugTextBoundsToggleStateAction::class.java)
    }
}
