// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.EqualizeTrailingMarker;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TrailingMarkerEqualizeOptions implements ComboBoxAdaptable<TrailingMarkerEqualizeOptions> {
    AS_IS(0, CodeStyleBundle.message("discretionary-text.as-is"), EqualizeTrailingMarker.AS_IS),
    ADD(1, CodeStyleBundle.message("discretionary-text.add"), EqualizeTrailingMarker.ADD),
    EQUALIZE(2, CodeStyleBundle.message("balanced-text.balance"), EqualizeTrailingMarker.EQUALIZE),
    REMOVE(-1, CodeStyleBundle.message("discretionary-text.remove"), EqualizeTrailingMarker.REMOVE);

    public final @NotNull String displayName;
    public final int intValue;
    public final EqualizeTrailingMarker flexMarkEnum;

    TrailingMarkerEqualizeOptions(int intValue, @NotNull String displayName, @NotNull EqualizeTrailingMarker flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<TrailingMarkerEqualizeOptions> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<TrailingMarkerEqualizeOptions> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TrailingMarkerEqualizeOptions[] getValues() { return values(); }
}
