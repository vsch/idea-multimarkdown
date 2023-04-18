// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxBooleanAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TocGenerateOnSaveType implements ComboBoxAdaptable<TocGenerateOnSaveType> {
    AS_IS(0, CodeStyleBundle.message("toc.generate.on-save.as-is")),
    FORMAT(1, CodeStyleBundle.message("toc.generate.on-save.format"));

    public final @NotNull String displayName;
    public final int intValue;

    TocGenerateOnSaveType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static ComboBoxAdaptable.StaticBoolean<TocGenerateOnSaveType> ADAPTER = new StaticBoolean<>(new ComboBoxBooleanAdapterImpl<>(AS_IS, FORMAT));

    @NotNull
    @Override
    public ComboBoxAdapter<TocGenerateOnSaveType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TocGenerateOnSaveType[] getValues() { return values(); }
}
