// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor.split;

import com.intellij.openapi.fileEditor.FileEditor;
import org.jetbrains.annotations.NotNull;

public interface SplitPreviewChangeListener {
    void updatePreviewType(@NotNull SplitFileEditor.SplitEditorPreviewType editorPreview, @NotNull SplitFileEditor.SplitEditorLayout editorLayout, @NotNull FileEditor forEditor);
}
