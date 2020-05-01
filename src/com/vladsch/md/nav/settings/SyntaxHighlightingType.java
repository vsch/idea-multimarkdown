// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum SyntaxHighlightingType implements ComboBoxAdaptable<SyntaxHighlightingType> {
    ANNOTATOR(1, MdBundle.message("syntax-highlighting.annotator")),
    LEXER(3, MdBundle.message("syntax-highlighting.lexer")),
    NONE(2, MdBundle.message("syntax-highlighting.none"));

    public final int intValue;
    public final @NotNull String displayName;

    SyntaxHighlightingType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<SyntaxHighlightingType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(ANNOTATOR));

    @NotNull
    @Override
    public ComboBoxAdapter<SyntaxHighlightingType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public SyntaxHighlightingType[] getValues() { return values(); }
}
