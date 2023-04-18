// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.util.format.options.HeadingStyle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum HeadingStyleType implements ComboBoxAdaptable<HeadingStyleType> {
    AS_IS(0, CodeStyleBundle.message("heading-preference.as-is"), HeadingStyle.AS_IS),
    ATX_PREFERRED(1, CodeStyleBundle.message("heading-preference.atx"), HeadingStyle.ATX_PREFERRED),
    SETEXT_PREFERRED(2, CodeStyleBundle.message("heading-preference.setext"), HeadingStyle.SETEXT_PREFERRED),
    ;

    public boolean isAtxPreferred() {
        return this == ATX_PREFERRED;
    }

    public boolean isSetextPreferred() {
        return this == SETEXT_PREFERRED;
    }

    public final @NotNull String displayName;
    public final int intValue;
    public final HeadingStyle flexMarkEnum;

    HeadingStyleType(int intValue, @NotNull String displayName, @NotNull HeadingStyle flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<HeadingStyleType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<HeadingStyleType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public HeadingStyleType[] getValues() { return values(); }
}
