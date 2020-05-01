// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxBooleanAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TocGenerateWithType implements ComboBoxAdaptable<TocGenerateWithType> {
    TEXT_AND_INLINES(0, CodeStyleBundle.message("toc.generate.text-and-inlines")),
    TEXT_ONLY(1, CodeStyleBundle.message("toc.generate.text-only"));

    public final @NotNull String displayName;
    public final int intValue;

    TocGenerateWithType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static StaticBoolean<TocGenerateWithType> ADAPTER = new StaticBoolean<>(new ComboBoxBooleanAdapterImpl<>(TEXT_AND_INLINES, TEXT_ONLY));

    @NotNull
    @Override
    public ComboBoxAdapter<TocGenerateWithType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TocGenerateWithType[] getValues() { return values(); }
}
