// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache;

import com.intellij.psi.PsiClass;
import com.vladsch.md.nav.util.Result;
import com.vladsch.md.nav.util.ResultBiFunction;
import org.jetbrains.annotations.NotNull;

public interface PsiClassProcessor<T> extends ResultBiFunction<PsiClass, Integer, T> {
    @NotNull
    @Override
    Result<T> apply(@NotNull PsiClass psiClass, @NotNull Integer level);
}
