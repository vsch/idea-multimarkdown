// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.md.nav.util.Result;
import com.vladsch.md.nav.util.ResultBiFunction;
import org.jetbrains.annotations.NotNull;

public interface SpecInfoProcessor<T> extends ResultBiFunction<SpecFileInfo, VirtualFile, T> {
    @Override
    @NotNull
    Result<T> apply(@NotNull SpecFileInfo specFileInfo, @NotNull VirtualFile root);
}
