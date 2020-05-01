// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxBooleanAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TocGenerateListType implements ComboBoxAdaptable<TocGenerateListType> {
    BULLET_LIST(0, CodeStyleBundle.message("toc.generate.bullet-list")),
    NUMBERED_LIST(1, CodeStyleBundle.message("toc.generate.numbered-list"));

    public final @NotNull String displayName;
    public final int intValue;

    TocGenerateListType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static ComboBoxAdaptable.StaticBoolean<TocGenerateListType> ADAPTER = new StaticBoolean<>(new ComboBoxBooleanAdapterImpl<>(BULLET_LIST, NUMBERED_LIST));

    @NotNull
    @Override
    public ComboBoxAdapter<TocGenerateListType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TocGenerateListType[] getValues() { return values(); }
}
