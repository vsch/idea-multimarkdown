// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.editorLayout;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.vladsch.md.nav.actions.styling.util.MdActionUtil;
import com.vladsch.md.nav.editor.split.SplitFileEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class BaseChangePreviewAction extends AnAction implements DumbAware, Toggleable {
    @Nullable
    private final SplitFileEditor.SplitEditorPreviewType myPreviewToSet;

    protected BaseChangePreviewAction(@Nullable SplitFileEditor.SplitEditorPreviewType previewToSet) {
        myPreviewToSet = previewToSet;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        final SplitFileEditor<?, ?> splitFileEditor = MdActionUtil.INSTANCE.findSplitEditor(event);

        if (splitFileEditor != null) {
            if (myPreviewToSet != null) event.getPresentation().putClientProperty(SELECTED_PROPERTY, splitFileEditor.getCurrentEditorPreview() == myPreviewToSet);
            event.getPresentation().setEnabled(splitFileEditor.getSplitLayout().showSecond);
        } else {
            event.getPresentation().setEnabled(false);
        }
    }

    protected void doAction(@NotNull SplitFileEditor<?, ?> splitFileEditor) {
        splitFileEditor.triggerPreviewChange();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final SplitFileEditor<?, ?> splitFileEditor = MdActionUtil.INSTANCE.findSplitEditor(event);

        if (splitFileEditor != null) {
            if (myPreviewToSet == null) {
                doAction(splitFileEditor);
            } else {
                splitFileEditor.triggerPreviewChange(myPreviewToSet);
                event.getPresentation().putClientProperty(SELECTED_PROPERTY, true);
            }

            ApplicationManager.getApplication().invokeLater(splitFileEditor::takeFocus);
        }
    }
}
