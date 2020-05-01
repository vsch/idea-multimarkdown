// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.actions.editorLayout;

import com.intellij.openapi.application.ApplicationManager;
import com.vladsch.md.nav.editor.split.SplitFileEditor;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import org.jetbrains.annotations.NotNull;

public class ToggleEditorLayoutChangeAction extends BaseChangeSplitLayoutAction {
    protected ToggleEditorLayoutChangeAction() {
        super(null);
    }

    @Override
    protected void doAction(final @NotNull SplitFileEditor<?, ?> splitFileEditor) {
        if (splitFileEditor.getCurrentEditorLayout() != SplitFileEditor.SplitEditorLayout.FIRST) {
            splitFileEditor.triggerLayoutChange(SplitFileEditor.SplitEditorLayout.FIRST);
        } else {
            splitFileEditor.triggerLayoutChange(MdApplicationSettings.getInstance().getDocumentSettings().getTextSplitLayoutToggle() ? SplitFileEditor.SplitEditorLayout.SPLIT : SplitFileEditor.SplitEditorLayout.SECOND);
        }

        ApplicationManager.getApplication().invokeLater(splitFileEditor::takeFocus);
    }
}
