// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.ui;

import com.intellij.util.ui.ColumnInfo;
import com.vladsch.md.nav.util.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JTable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SimpleTableColumn<T, A> extends ColumnInfo<T, A> {
    final int myWidth;
    final @NotNull Function<T, A> myGetter;
    final @Nullable BiConsumer<T, A> mySetter;

    public SimpleTableColumn(String header, final int width, @NotNull Function<T, A> getter, @Nullable BiConsumer<T, A> setter) {
        super(header);
        this.myWidth = width;
        this.myGetter = getter;
        this.mySetter = setter;
    }

    public SimpleTableColumn(String header, final int width, @NotNull Field<T, A> field) {
        this(header, width, field.getGetter(), field.getSetter());
    }

    @Override
    public boolean isCellEditable(final T t) {
        return mySetter != null;
    }

    @Override
    final public void setValue(final T t, final A value) {
        if (mySetter != null) {
            mySetter.accept(t, value);
        }
    }

    @Nullable
    @Override
    final public A valueOf(final T t) {
        return myGetter.apply(t);
    }

    @Override
    public int getWidth(JTable table) {
        return myWidth;
    }
}
