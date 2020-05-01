// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.DimensionService
import com.vladsch.md.nav.actions.styling.util.DisabledConditionBuilder
import com.vladsch.md.nav.settings.MdApplicationSettings
import java.awt.Dimension

class SizePreferencesDialogAction : AnAction(), DumbAware {
    override fun isDumbAware(): Boolean {
        return true
    }

    override fun update(e: AnActionEvent) {
        val debugSettings = MdApplicationSettings.instance.debugSettings
        if (debugSettings.showSizePreferencesDialog) {
            val project = e.project
            DisabledConditionBuilder(e, this)
                .notNull(project)
                .and(debugSettings.preferencesDialogWidth >= 500 && debugSettings.preferencesDialogHeight >= 500, "Dialog width/height not >= 500 (Languages & Frameworks > Markdown > Debug)")
                .done(false)
        } else {
            e.presentation.isVisible = false
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val debugSettings = MdApplicationSettings.instance.debugSettings
            DimensionService.getInstance().setSize("SettingsEditor", Dimension(debugSettings.preferencesDialogWidth, debugSettings.preferencesDialogHeight), project)
            PropertiesComponent.getInstance(project).setValue("settings.editor.splitter.proportion", debugSettings.preferencesDialogMenuSplit.toFloat(), 0.2f);
        }
    }
}
