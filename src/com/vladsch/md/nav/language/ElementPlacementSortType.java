// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.ElementPlacementSort;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum ElementPlacementSortType implements ComboBoxAdaptable<ElementPlacementSortType> {
    AS_IS(0, CodeStyleBundle.message("non-display.sort-type.as-is"), ElementPlacementSort.AS_IS),
    SORT(1, CodeStyleBundle.message("non-display.sort-type.sort"), ElementPlacementSort.SORT),
    SORT_UNUSED_LAST(2, CodeStyleBundle.message("non-display.sort-unused-last"), ElementPlacementSort.SORT_UNUSED_LAST),
    SORT_DELETE_UNUSED(4, CodeStyleBundle.message("non-display.sort-delete-unused"), ElementPlacementSort.SORT_DELETE_UNUSED),
    DELETE_UNUSED(3, CodeStyleBundle.message("non-display.sort-type.delete-unused"), ElementPlacementSort.DELETE_UNUSED),
    ;

    public final @NotNull String displayName;
    public final int intValue;
    public final ElementPlacementSort flexMarkEnum;

    ElementPlacementSortType(int intValue, @NotNull String displayName, @NotNull ElementPlacementSort flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public boolean isDeleteUnused() {
        return this == DELETE_UNUSED || this == SORT_DELETE_UNUSED;
    }

    public boolean isSort() {
        return this == SORT || this == SORT_UNUSED_LAST || this == SORT_DELETE_UNUSED;
    }

    public boolean isUnused() {
        return this == SORT_UNUSED_LAST || this == SORT_DELETE_UNUSED || this == DELETE_UNUSED;
    }

    public static Static<ElementPlacementSortType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<ElementPlacementSortType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public ElementPlacementSortType[] getValues() { return values(); }
}
