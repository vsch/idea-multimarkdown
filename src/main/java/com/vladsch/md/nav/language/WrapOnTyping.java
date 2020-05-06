// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum WrapOnTyping implements ComboBoxAdaptable<WrapOnTyping> {
    DEFAULT(CommonCodeStyleSettings.WrapOnTyping.DEFAULT.intValue, CodeStyleBundle.message("wrap-on-typing.default")),
    NO_WRAP(CommonCodeStyleSettings.WrapOnTyping.NO_WRAP.intValue, CodeStyleBundle.message("wrap-on-typing.no-wrap")),
    WRAP(CommonCodeStyleSettings.WrapOnTyping.WRAP.intValue, CodeStyleBundle.message("wrap-on-typing.wrap"));

    public final @NotNull String displayName;
    public final int intValue;

    WrapOnTyping(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<WrapOnTyping> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(DEFAULT));

    @NotNull
    @Override
    public ComboBoxAdapter<WrapOnTyping> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public WrapOnTyping[] getValues() { return values(); }
}
