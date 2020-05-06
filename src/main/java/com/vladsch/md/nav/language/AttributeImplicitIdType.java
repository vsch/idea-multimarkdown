// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.ext.attributes.AttributeImplicitName;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum AttributeImplicitIdType implements ComboBoxAdaptable<AttributeImplicitIdType> {
    AS_IS(0, CodeStyleBundle.message("attribute-name.id.as-is"), AttributeImplicitName.AS_IS),
    IMPLICIT_PREFERRED(1, CodeStyleBundle.message("attribute-name.id.implicit-preferred"), AttributeImplicitName.IMPLICIT_PREFERRED),
    EXPLICIT_PREFERRED(2, CodeStyleBundle.message("attribute-name.id.explicit-preferred"), AttributeImplicitName.EXPLICIT_PREFERRED),
    ;

    public final @NotNull String displayName;
    public final int intValue;
    public final AttributeImplicitName flexMarkEnum;

    AttributeImplicitIdType(int intValue, @NotNull String displayName, @NotNull AttributeImplicitName flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<AttributeImplicitIdType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<AttributeImplicitIdType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public AttributeImplicitIdType[] getValues() { return values(); }
}
