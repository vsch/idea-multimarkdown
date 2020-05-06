// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum FormatWithSoftWrap implements ComboBoxAdaptable<FormatWithSoftWrap> {
    DISABLED(0, CodeStyleBundle.message("format.with-soft-wrap.disabled")),
    ENABLED(1, CodeStyleBundle.message("format.with-soft-wrap.enabled")),
    INFINITE_MARGIN(2, CodeStyleBundle.message("format.with-soft-wrap.infinite-margins"));

    public final @NotNull String displayName;
    public final int intValue;

    FormatWithSoftWrap(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public boolean isDisabled() {
        return this == DISABLED;
    }

    public boolean isEnabled() {
        return this == ENABLED;
    }

    public boolean isInfiniteMargin() {
        return this == INFINITE_MARGIN;
    }

    public static Static<FormatWithSoftWrap> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(DISABLED));

    @NotNull
    @Override
    public ComboBoxAdapter<FormatWithSoftWrap> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public FormatWithSoftWrap[] getValues() { return values(); }
}
