// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum KeepAtStartOfLine implements ComboBoxAdaptable<KeepAtStartOfLine> {
    NONE(-1, CodeStyleBundle.message("keep-at-start.none")),
    JEKYLL(0, CodeStyleBundle.message("keep-at-start.jekyll")),
    ALL(1, CodeStyleBundle.message("keep-at-start.all"));

    public final @NotNull String displayName;
    public final int intValue;

    KeepAtStartOfLine(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<KeepAtStartOfLine> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(JEKYLL));

    @NotNull
    @Override
    public ComboBoxAdapter<KeepAtStartOfLine> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public KeepAtStartOfLine[] getValues() { return values(); }
}
