// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.api;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.md.nav.settings.MdDocumentSettings;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdEditorCustomizationProvider {
    ExtensionPointName<MdEditorCustomizationProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.editorCustomizationProvider");
    MdExtensions<MdEditorCustomizationProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdEditorCustomizationProvider[0]);

    default void customizeMarkdownEditor(@NotNull EditorEx editor) {

    }

    /**
     * Return non-null to stop checking
     *
     * @param editor markdown editor
     *
     * @return non-null to add range highlighter
     */
    @Nullable
    default MdEditorRangeHighlighter getEditorRangeHighlighter(@NotNull EditorEx editor) {
        return null;
    }

    /**
     * Return non-null to stop checking
     *
     * @param editor markdown editor
     *
     * @return non-null to add column width provider
     */
    @Nullable
    default MdColumnVisibleAreaWidthProvider getColumnVisibleAreaWidthProvider(@NotNull EditorEx editor) {
        return null;
    }

    default boolean reloadEditor(@NotNull FileEditor editor, @NotNull Project project, @NotNull final VirtualFile file) {
        return false;
    }

    @Nullable
    default Editor getEditorEx(@NotNull FileEditor fileEditor) {
        return null;
    }

    default boolean reloadMarkdown(@NotNull MdDocumentSettings oldDocumentSettings, @NotNull MdDocumentSettings newDocumentSettings) {
        return false;
    }

    default boolean reparseMarkdown(@NotNull MdDocumentSettings oldDocumentSettings, @NotNull MdDocumentSettings newDocumentSettings) {
        return false;
    }
}
