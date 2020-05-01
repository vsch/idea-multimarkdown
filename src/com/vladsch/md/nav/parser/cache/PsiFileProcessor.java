// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache;

import com.intellij.psi.PsiFile;
import com.vladsch.md.nav.util.Result;
import com.vladsch.md.nav.util.ResultFunction;
import org.jetbrains.annotations.NotNull;

public interface PsiFileProcessor<T> extends ResultFunction<PsiFile, T> {
    @NotNull
    @Override
    Result<T> apply(@NotNull PsiFile psiFile);
}
