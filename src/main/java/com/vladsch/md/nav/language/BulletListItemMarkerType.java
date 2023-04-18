// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.ListBulletMarker;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum BulletListItemMarkerType implements ComboBoxAdaptable<BulletListItemMarkerType> {
    ANY(0, CodeStyleBundle.message("bullet-list.marker-type.any"), null, ListBulletMarker.ANY),
    DASH(1, CodeStyleBundle.message("bullet-list.marker-type.dash"), "- ", ListBulletMarker.DASH),
    ASTERISK(2, CodeStyleBundle.message("bullet-list.marker-type.asterisk"), "* ", ListBulletMarker.ASTERISK),
    PLUS(3, CodeStyleBundle.message("bullet-list.marker-type.plus"), "+ ", ListBulletMarker.PLUS);

    @NotNull public final String displayName;
    public final int intValue;
    @Nullable public final String prefix;
    public final ListBulletMarker flexMarkEnum;

    BulletListItemMarkerType(int intValue, @NotNull String displayName, @Nullable String prefix, @NotNull ListBulletMarker flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.prefix = prefix;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<BulletListItemMarkerType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(ANY));

    @NotNull
    @Override
    public ComboBoxAdapter<BulletListItemMarkerType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public BulletListItemMarkerType[] getValues() { return values(); }
}
