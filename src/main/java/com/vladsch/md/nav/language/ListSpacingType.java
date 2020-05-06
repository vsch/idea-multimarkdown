// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.ListSpacing;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum ListSpacingType implements ComboBoxAdaptable<ListSpacingType> {
    AS_IS(0, CodeStyleBundle.message("list-spacing.as-is"), ListSpacing.AS_IS),
    LOOSEN(1, CodeStyleBundle.message("list-spacing.loosen"), ListSpacing.LOOSEN),
    TIGHTEN(2, CodeStyleBundle.message("list-spacing.tighten"), ListSpacing.TIGHTEN),
    LOOSE(3, CodeStyleBundle.message("list-spacing.loose"), ListSpacing.LOOSE),
    TIGHT(4, CodeStyleBundle.message("list-spacing.tight"), ListSpacing.TIGHT);

    public final @NotNull String displayName;
    public final int intValue;
    public final ListSpacing flexMarkEnum;

    ListSpacingType(int intValue, @NotNull String displayName, @NotNull ListSpacing flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<ListSpacingType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<ListSpacingType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public ListSpacingType[] getValues() { return values(); }
}
