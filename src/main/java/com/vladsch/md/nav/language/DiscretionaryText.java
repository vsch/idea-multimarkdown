// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum DiscretionaryText implements ComboBoxAdaptable<DiscretionaryText> {
    AS_IS(0, CodeStyleBundle.message("discretionary-text.as-is"), com.vladsch.flexmark.util.format.options.DiscretionaryText.AS_IS),
    ADD(1, CodeStyleBundle.message("discretionary-text.add"), com.vladsch.flexmark.util.format.options.DiscretionaryText.ADD),
    REMOVE(-1, CodeStyleBundle.message("discretionary-text.remove"), com.vladsch.flexmark.util.format.options.DiscretionaryText.REMOVE);

    public final @NotNull String displayName;
    public final int intValue;
    public final com.vladsch.flexmark.util.format.options.DiscretionaryText flexMarkEnum;

    DiscretionaryText(int intValue, @NotNull String displayName, @NotNull com.vladsch.flexmark.util.format.options.DiscretionaryText flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<DiscretionaryText> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<DiscretionaryText> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public DiscretionaryText[] getValues() { return values(); }
}
