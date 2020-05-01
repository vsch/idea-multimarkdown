// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface NullableConsumer<T> extends Consumer<T> {
    @Override
    void accept(@Nullable T t);
}
