// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.BlockQuoteMarker;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum BlockQuoteMarkerOptions implements ComboBoxAdaptable<BlockQuoteMarkerOptions> {
    AS_IS(0, CodeStyleBundle.message("block-quote-marker.as-is"), BlockQuoteMarker.AS_IS),
    ADD_COMPACT(1, CodeStyleBundle.message("block-quote-marker.compact"), BlockQuoteMarker.ADD_COMPACT),
    ADD_COMPACT_WITH_SPACE(2, CodeStyleBundle.message("block-quote-marker.compact-with-space"), BlockQuoteMarker.ADD_COMPACT_WITH_SPACE),
    ADD_SPACED(3, CodeStyleBundle.message("block-quote-marker.spaced"), BlockQuoteMarker.ADD_SPACED);

    public final @NotNull String displayName;
    public final int intValue;
    public final BlockQuoteMarker flexMarkEnum;

    BlockQuoteMarkerOptions(int intValue, @NotNull String displayName, @NotNull BlockQuoteMarker flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<BlockQuoteMarkerOptions> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<BlockQuoteMarkerOptions> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public BlockQuoteMarkerOptions[] getValues() { return values(); }
}
