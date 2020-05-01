// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.ui;

import com.vladsch.md.nav.util.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SimpleField<T, V> implements Field<T, V> {
    final private @NotNull Function<T, V> getter;
    final private @Nullable BiConsumer<T, V> setter;

    public SimpleField(@NotNull final Function<T, V> getter, @Nullable final BiConsumer<T, V> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @NotNull
    public Function<T, V> getGetter() {
        return getter;
    }

    @Nullable
    public BiConsumer<T, V> getSetter() {
        return setter;
    }
}
