// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum SoftWrapType implements ComboBoxAdaptable<SoftWrapType> {
    IDE_DEFAULT(0, MdBundle.message("soft-wrap.default")),
    DISABLED(1, MdBundle.message("soft-wrap.disabled")),
    ENABLED(2, MdBundle.message("soft-wrap.enabled"));

    public final int intValue;
    public final @NotNull String displayName;

    SoftWrapType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<SoftWrapType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(IDE_DEFAULT));

    @NotNull
    @Override
    public ComboBoxAdapter<SoftWrapType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public SoftWrapType[] getValues() { return values(); }
}
