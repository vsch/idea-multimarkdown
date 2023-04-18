// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.api;

import com.vladsch.md.nav.psi.element.MdFile;
import org.jetbrains.annotations.NotNull;

public interface MdEditorRangeHighlighter {
    void updateRangeHighlighters(@NotNull MdFile psiFile);

    void removeRangeHighlighters();
}
