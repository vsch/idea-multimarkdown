// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface NotNullFunction<T, R> extends Function<T, R> {
    @NotNull
    @Override
    R apply(@NotNull T t);
}
