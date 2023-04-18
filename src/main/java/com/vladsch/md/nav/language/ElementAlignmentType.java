// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.ElementAlignment;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum ElementAlignmentType implements ComboBoxAdaptable<ElementAlignmentType> {
    NONE(0, CodeStyleBundle.message("alignment.none"), ElementAlignment.NONE),
    LEFT_ALIGN(2, CodeStyleBundle.message("alignment.align-left"), ElementAlignment.LEFT_ALIGN),
    RIGHT_ALIGN(1, CodeStyleBundle.message("alignment.align-right"), ElementAlignment.RIGHT_ALIGN),
    ;

    public final @NotNull String displayName;
    public final int intValue;
    public final ElementAlignment flexMarkEnum;

    ElementAlignmentType(int intValue, @NotNull String displayName, @NotNull ElementAlignment flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<ElementAlignmentType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(NONE));

    @NotNull
    @Override
    public ComboBoxAdapter<ElementAlignmentType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public ElementAlignmentType[] getValues() { return values(); }
}
