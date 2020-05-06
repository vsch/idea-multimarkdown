// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format;

import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import org.jetbrains.annotations.NotNull;

public interface LinePrefixMatcher {
    LinePrefixMatcher NULL = (lineChars, indentColumn, editContext) -> indentColumn;

    /**
     * Get the size of the prefix matched on the given line
     *
     * @param lineChars    line characters
     * @param indentColumn column number of first character on the line
     * @param editContext  parser settings to use for determining the prefix removal pattern
     *
     * @return column of first non-prefix line content
     */

    int contentColumn(@NotNull CharSequence lineChars, int indentColumn, @NotNull PsiEditContext editContext);
}
