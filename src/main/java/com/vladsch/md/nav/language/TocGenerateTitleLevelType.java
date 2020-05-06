// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TocGenerateTitleLevelType implements ComboBoxAdaptable<TocGenerateTitleLevelType> {
    H1(1, CodeStyleBundle.message("toc.generate.title-h1")),
    H2(2, CodeStyleBundle.message("toc.generate.title-h2")),
    H3(3, CodeStyleBundle.message("toc.generate.title-h3")),
    H4(4, CodeStyleBundle.message("toc.generate.title-h4")),
    H5(5, CodeStyleBundle.message("toc.generate.title-h5")),
    H6(6, CodeStyleBundle.message("toc.generate.title-h6"));

    public final @NotNull String displayName;
    public final int intValue;

    TocGenerateTitleLevelType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<TocGenerateTitleLevelType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(H1));

    @NotNull
    @Override
    public ComboBoxAdapter<TocGenerateTitleLevelType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TocGenerateTitleLevelType[] getValues() { return values(); }
}
