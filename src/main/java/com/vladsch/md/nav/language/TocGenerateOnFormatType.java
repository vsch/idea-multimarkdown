// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.ext.toc.SimTocGenerateOnFormat;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TocGenerateOnFormatType implements ComboBoxAdaptable<TocGenerateOnFormatType> {
    AS_IS(0, CodeStyleBundle.message("toc.generate.on-format.as-is"), SimTocGenerateOnFormat.AS_IS),
    UPDATE(1, CodeStyleBundle.message("toc.generate.on-format.update"), SimTocGenerateOnFormat.UPDATE),
    REMOVE(-1, CodeStyleBundle.message("toc.generate.on-format.remove"), SimTocGenerateOnFormat.REMOVE);

    public final @NotNull String displayName;
    public final int intValue;
    public final SimTocGenerateOnFormat flexMarkEnum;

    TocGenerateOnFormatType(int intValue, @NotNull String displayName, @NotNull SimTocGenerateOnFormat flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<TocGenerateOnFormatType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(UPDATE));

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TocGenerateOnFormatType[] getValues() { return values(); }

    @NotNull
    @Override
    public ComboBoxAdapter<TocGenerateOnFormatType> getAdapter() {
        return ADAPTER;
    }
}
