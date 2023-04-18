// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public interface NotNullNullableBiConsumer<T, U> extends BiConsumer<T, U> {
    @Override
    void accept(@NotNull T t, @Nullable U u);
}
