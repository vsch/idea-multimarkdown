// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.TableCaptionHandling;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TableCaptionActionType implements ComboBoxAdaptable<TableCaptionActionType> {
    AS_IS(0, CodeStyleBundle.message("table-caption.as-is"), TableCaptionHandling.AS_IS),
    ADD(2, CodeStyleBundle.message("table-caption.add"), TableCaptionHandling.ADD),
    REMOVE_EMPTY(3, CodeStyleBundle.message("table-caption.remove-empty"), TableCaptionHandling.REMOVE_EMPTY),
    REMOVE(4, CodeStyleBundle.message("table-caption.remove"), TableCaptionHandling.REMOVE);

    public final @NotNull String displayName;
    public final int intValue;
    public final TableCaptionHandling flexMarkEnum;

    TableCaptionActionType(int intValue, @NotNull String displayName, @NotNull TableCaptionHandling flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<TableCaptionActionType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<TableCaptionActionType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TableCaptionActionType[] getValues() { return values(); }
}
