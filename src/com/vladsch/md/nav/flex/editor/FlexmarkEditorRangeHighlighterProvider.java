// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.editor;

import com.intellij.openapi.editor.ex.EditorEx;
import com.vladsch.md.nav.editor.api.MdEditorCustomizationProvider;
import com.vladsch.md.nav.editor.api.MdEditorRangeHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlexmarkEditorRangeHighlighterProvider implements MdEditorCustomizationProvider {
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
        return new FlexmarkEditorRangeHighlighter(editor);
    }
}
