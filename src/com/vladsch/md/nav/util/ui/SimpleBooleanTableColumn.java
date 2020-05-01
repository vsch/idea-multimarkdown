// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.ui;

import com.vladsch.md.nav.util.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SimpleBooleanTableColumn<T> extends SimpleTableColumn<T, Boolean> {
    public SimpleBooleanTableColumn(String header, final int width, @NotNull Function<T, Boolean> getter, @Nullable BiConsumer<T, Boolean> setter) {
        super(header, width, getter, setter);
    }

    public SimpleBooleanTableColumn(String header, final int width, @NotNull Field<T, Boolean> field) {
        this(header, width, field.getGetter(), field.getSetter());
    }

    @Override
    public Class<?> getColumnClass() {
        return Boolean.class;
    }
}
