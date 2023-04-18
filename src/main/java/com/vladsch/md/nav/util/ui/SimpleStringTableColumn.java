// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.ui;

import com.vladsch.md.nav.util.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SimpleStringTableColumn<T> extends SimpleTableColumn<T, String> {
    public SimpleStringTableColumn(String header, final int width, @NotNull Function<T, String> getter, @Nullable BiConsumer<T, String> setter) {
        super(header, width, getter, setter);
    }

    public SimpleStringTableColumn(String header, final int width, @NotNull Field<T, String> field) {
        super(header, width, field);
    }

    @Override
    public Class<?> getColumnClass() {
        return String.class;
    }
}
