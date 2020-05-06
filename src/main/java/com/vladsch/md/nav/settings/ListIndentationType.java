// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum ListIndentationType implements ComboBoxAdaptable<ListIndentationType> {
    GITHUB(0, MdBundle.message("list-indentation.github")),
    COMMONMARK(1, MdBundle.message("list-indentation.commonmark")),
    FIXED(2, MdBundle.message("list-indentation.fixed"));

    public final int intValue;
    public final @NotNull String displayName;

    ListIndentationType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<ListIndentationType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(FIXED));

    @NotNull
    @Override
    public ComboBoxAdapter<ListIndentationType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public ListIndentationType[] getValues() { return values(); }
}
