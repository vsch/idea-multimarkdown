// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxBooleanAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TextSplitLayoutToggleType implements ComboBoxAdaptable<TextSplitLayoutToggleType> {
    TEXT_SPLIT(0, MdBundle.message("markdown.text-split-layout.text-split")),
    TEXT_PREVIEW(1, MdBundle.message("markdown.text-split-layout.text-preview"));

    public final int intValue;
    public final @NotNull String displayName;

    TextSplitLayoutToggleType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static StaticBoolean<TextSplitLayoutToggleType> ADAPTER = new StaticBoolean<>(new ComboBoxBooleanAdapterImpl<>(TEXT_PREVIEW, TEXT_SPLIT));

    @NotNull
    @Override
    public ComboBoxAdapter<TextSplitLayoutToggleType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TextSplitLayoutToggleType[] getValues() { return values(); }
}
