// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum UpdateStreamType implements ComboBoxAdaptable<UpdateStreamType> {
    STABLE(0, MdBundle.message("update-stream.default")),
    //    PATCHES(1, MdBundle.message("update-stream.patches")),
    EAP(2, MdBundle.message("update-stream.eap")),
    LEGACY(3, MdBundle.message("update-stream.legacy")),
    LEGACY_EAP(4, MdBundle.message("update-stream.legacy-eap")),
    ;

    public final int intValue;
    public final @NotNull String displayName;

    UpdateStreamType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<UpdateStreamType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(STABLE));

    @NotNull
    @Override
    public ComboBoxAdapter<UpdateStreamType> getAdapter() {
        return ADAPTER;
    }

    public boolean isLegacy() {
        return this == LEGACY || this == LEGACY_EAP;
    }

    public boolean isEap() {
        return this == EAP || this == LEGACY_EAP;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public UpdateStreamType[] getValues() { return values(); }
}
