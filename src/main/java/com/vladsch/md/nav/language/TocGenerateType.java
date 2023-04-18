// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxBooleanAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TocGenerateType implements ComboBoxAdaptable<TocGenerateType> {
    MARKDOWN(0, CodeStyleBundle.message("toc.generate.markdown")),
    HTML(1, CodeStyleBundle.message("toc.generate.html"));

    public final @NotNull String displayName;
    public final int intValue;

    TocGenerateType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static ComboBoxAdaptable.StaticBoolean<TocGenerateType> ADAPTER = new StaticBoolean<>(new ComboBoxBooleanAdapterImpl<>(MARKDOWN, HTML));

    @NotNull
    @Override
    public ComboBoxAdapter<TocGenerateType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TocGenerateType[] getValues() { return values(); }
}
