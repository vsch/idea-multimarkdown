// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.editorLayout;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdDocumentSettings;
import org.jetbrains.annotations.NotNull;

class ToggleEditorSplitLayoutAction extends ToggleAction implements DumbAware {
    public ToggleEditorSplitLayoutAction() {

    }

    @Override
    public boolean isSelected(@NotNull final AnActionEvent event) {
        final MdDocumentSettings settings = MdApplicationSettings.getInstance().getDocumentSettings();
        return settings.getVerticalSplitPreview();
    }

    @Override
    public void setSelected(@NotNull final AnActionEvent event, final boolean b) {
        final MdDocumentSettings settings = MdApplicationSettings.getInstance().getDocumentSettings();
        settings.setVerticalSplitPreview(b);
        MdApplicationSettings.getInstance().setDocumentSettings(settings);
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(true);
    }
}
