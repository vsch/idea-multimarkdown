// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.CodeFenceMarker;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum CodeFenceMarkerType implements ComboBoxAdaptable<CodeFenceMarkerType> {
    ANY(0, CodeStyleBundle.message("code-fence.marker-type.any"), CodeFenceMarker.ANY),
    BACK_TICK(1, CodeStyleBundle.message("code-fence.marker-type.back-tick"), CodeFenceMarker.BACK_TICK),
    TILDE(2, CodeStyleBundle.message("code-fence.marker-type.tilde"), CodeFenceMarker.TILDE);

    public final @NotNull String displayName;
    public final int intValue;
    public final CodeFenceMarker flexMarkEnum;

    CodeFenceMarkerType(int intValue, @NotNull String displayName, @NotNull CodeFenceMarker flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<CodeFenceMarkerType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(ANY));

    @NotNull
    @Override
    public ComboBoxAdapter<CodeFenceMarkerType> getAdapter() {
        return ADAPTER;
    }

    public char getMarkerChar() {
        if (this == TILDE) {
            return '~';
        } else {
            return '`';
        }
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public CodeFenceMarkerType[] getValues() { return values(); }
}
