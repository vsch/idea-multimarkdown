// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.ElementPlacement;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum ElementPlacementType implements ComboBoxAdaptable<ElementPlacementType> {
    AS_IS(0, CodeStyleBundle.message("non-display.move-type.as-is"), ElementPlacement.AS_IS),
    DOCUMENT_TOP(1, CodeStyleBundle.message("non-display.move-type.to-top"), ElementPlacement.DOCUMENT_TOP),
    GROUP_WITH_FIRST(2, CodeStyleBundle.message("non-display.move-type.group-with-first"), ElementPlacement.GROUP_WITH_FIRST),
    GROUP_WITH_LAST(3, CodeStyleBundle.message("non-display.move-type.group-with-last"), ElementPlacement.GROUP_WITH_LAST),
    DOCUMENT_BOTTOM(4, CodeStyleBundle.message("non-display.move-type.to-bottom"), ElementPlacement.DOCUMENT_BOTTOM);

    public final @NotNull String displayName;
    public final int intValue;
    public final ElementPlacement flexMarkEnum;

    ElementPlacementType(int intValue, @NotNull String displayName, @NotNull ElementPlacement flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<ElementPlacementType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<ElementPlacementType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public ElementPlacementType[] getValues() { return values(); }
}
