// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util;

import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

public interface PsiElementPredicateWithEditor extends PsiElementPredicate {
    boolean satisfiedBy(@NotNull Editor editor, final int selectionStart, final int selectionEnd);
}

