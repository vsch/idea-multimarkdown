// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
//
// This code is private property of the copyright holder and cannot be used without
// having obtained a license or prior written permission of the copyright holder.
//

package com.vladsch.md.nav.editor;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.md.nav.editor.api.MdColumnVisibleAreaWidthProvider;
import com.vladsch.md.nav.editor.api.MdEditorRangeHighlighter;
import com.vladsch.md.nav.settings.MdDocumentSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdEditorCustomizationProvider implements com.vladsch.md.nav.editor.api.MdEditorCustomizationProvider {
    @Override
    public void customizeMarkdownEditor(@NotNull final EditorEx editor) {

    }

    /**
     * Return non-null to add range highlighter or null to skip
     *
     * @param editor markdown editor
     *
     * @return non-null to add range highlighter
     */
    @Nullable
    @Override
    public MdEditorRangeHighlighter getEditorRangeHighlighter(@NotNull EditorEx editor) {
        return new WrappingEditorRangeHighlighter(editor);
    }

    @Nullable
    @Override
    public MdColumnVisibleAreaWidthProvider getColumnVisibleAreaWidthProvider(@NotNull final EditorEx editor) {
        return null;
    }

    @Override
    public boolean reloadEditor(@NotNull FileEditor editor, @NotNull Project project, @NotNull VirtualFile file) {
        return false;
    }

    @Nullable
    @Override
    public Editor getEditorEx(@NotNull FileEditor fileEditor) {
        return null;
    }

    @Override
    public boolean reparseMarkdown(@NotNull MdDocumentSettings oldDocumentSettings, @NotNull MdDocumentSettings newDocumentSettings) {
        return false;
    }
}
