// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache;

import com.vladsch.md.nav.util.Result;
import org.jetbrains.annotations.NotNull;

public interface SourcedElementConsumer<T> {
    @NotNull
    Result<T> accept(@NotNull T t, @NotNull ElementSource source);
}
