// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.vladsch.md.nav.psi.element.MdVerbatim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FlexmarkExampleSection extends MdVerbatim {
    @Nullable
    Pair<TextRange, TextRange> getHighlightRange(int caret, final @Nullable ASTNode sourceNode);

    @Nullable
    FlexmarkExampleSection getExampleSection(@NotNull FlexmarkExample example);

    int getSectionIndex();
}
