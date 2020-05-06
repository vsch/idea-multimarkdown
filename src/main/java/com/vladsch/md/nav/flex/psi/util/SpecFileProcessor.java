// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi.util;

import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.util.Result;
import com.vladsch.md.nav.util.ResultBiFunction;
import org.jetbrains.annotations.NotNull;

public interface SpecFileProcessor<T> extends ResultBiFunction<MdFile, String, T> {
    @Override
    @NotNull
    Result<T> apply(@NotNull MdFile mdFile, @NotNull String specResourcePath);
}
