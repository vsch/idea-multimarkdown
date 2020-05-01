// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.md.nav.editor.split.SplitTextEditorProvider;
import org.jetbrains.annotations.NotNull;

public class MdSplitEditorProvider extends SplitTextEditorProvider {
    public MdSplitEditorProvider() {
        super(new PsiAwareTextEditorProvider(), new MdPreviewFileEditorProvider());
    }

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return super.accept(project, file);
    }

    @Override
    protected FileEditor createSplitEditor(@NotNull final FileEditor firstEditor, @NotNull FileEditor secondEditor) {
        if (!(firstEditor instanceof TextEditor) || !(secondEditor instanceof MdPreviewFileEditor)) {
            throw new IllegalArgumentException("Main editor should be TextEditor");
        }
        return new MdSplitEditor(((TextEditor) firstEditor), ((MdPreviewFileEditor) secondEditor));
    }
}
