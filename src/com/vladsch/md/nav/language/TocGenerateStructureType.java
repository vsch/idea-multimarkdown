// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.ext.toc.internal.TocOptions;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TocGenerateStructureType implements ComboBoxAdaptable<TocGenerateStructureType> {
    HIERARCHY(0, false, false, CodeStyleBundle.message("toc.generate.hierarchy")),
    FLAT(1, true, false, CodeStyleBundle.message("toc.generate.flat")),
    FLAT_REVERSED(2, true, false, CodeStyleBundle.message("toc.generate.flat-reversed")),
    SORTED(3, true, true, CodeStyleBundle.message("toc.generate.sorted")),
    SORTED_REVERSED(4, true, true, CodeStyleBundle.message("toc.generate.sorted-reversed"));

    public final @NotNull String displayName;
    public final int intValue;
    public final boolean isFlat;
    public final boolean isSorted;

    TocGenerateStructureType(int intValue, boolean isFlat, boolean isSorted, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.isFlat = isFlat;
        this.isSorted = isSorted;
    }

    public static Static<TocGenerateStructureType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(HIERARCHY));

    public static TocGenerateStructureType get(TocOptions.ListType options) {
        if (options == TocOptions.ListType.FLAT) return FLAT;
        if (options == TocOptions.ListType.FLAT_REVERSED) return FLAT_REVERSED;
        if (options == TocOptions.ListType.SORTED) return SORTED;
        if (options == TocOptions.ListType.SORTED_REVERSED) return SORTED_REVERSED;
        return HIERARCHY;
    }

    public TocOptions.ListType asTocListType() {
        if (this == FLAT) return TocOptions.ListType.FLAT;
        if (this == FLAT_REVERSED) return TocOptions.ListType.FLAT_REVERSED;
        if (this == SORTED) return TocOptions.ListType.SORTED;
        if (this == SORTED_REVERSED) return TocOptions.ListType.SORTED_REVERSED;
        return TocOptions.ListType.HIERARCHY;
    }

    @NotNull
    @Override
    public ComboBoxAdapter<TocGenerateStructureType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TocGenerateStructureType[] getValues() { return values(); }
}
