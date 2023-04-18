// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.DefinitionMarker;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum DefinitionMarkerType implements ComboBoxAdaptable<DefinitionMarkerType> {
    ANY(0, CodeStyleBundle.message("definition.marker-type.any"), DefinitionMarker.ANY),
    COLON(1, CodeStyleBundle.message("definition.marker-type.colon"), DefinitionMarker.COLON),
    TILDE(2, CodeStyleBundle.message("definition.marker-type.tilde"), DefinitionMarker.TILDE);

    public final @NotNull String displayName;
    public final int intValue;
    public final DefinitionMarker flexMarkEnum;

    DefinitionMarkerType(int intValue, @NotNull String displayName, @NotNull DefinitionMarker flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<DefinitionMarkerType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(ANY));

    @NotNull
    @Override
    public ComboBoxAdapter<DefinitionMarkerType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public DefinitionMarkerType[] getValues() { return values(); }
}
