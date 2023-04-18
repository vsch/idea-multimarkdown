// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum MathConversionType implements ComboBoxAdaptable<MathConversionType> {
    NONE(-2, MdBundle.message("math.conversion.none"), ""),
    KATEX(-1, MdBundle.message("math.conversion.katex"), ""),
    EMBEDDED_PNG(0, MdBundle.message("math.conversion.embedded-png"), ".png"),
    EMBEDDED_SVG(1, MdBundle.message("math.conversion.embedded-svg"), ".svg"),
    CODECOGS_PNG(2, MdBundle.message("math.conversion.codecogs-png"), ".png"),
    CODECOGS_SVG(3, MdBundle.message("math.conversion.codecogs-svg"), ".svg"),
    ;

    public final int intValue;
    public final @NotNull String displayName;
    public final @NotNull String extension;

    MathConversionType(int intValue, @NotNull String displayName, @NotNull String extension) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.extension = extension;
    }

    public static Static<MathConversionType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(KATEX));

    public String urlPrefix() {
        switch (this) {
            case CODECOGS_PNG:
                return "https://latex.codecogs.com/png.latex?";
            case CODECOGS_SVG:
                return "https://latex.codecogs.com/svg.latex?";

            default:
                return null;
        }
    }

    public boolean isEmbedded() {
        return this == EMBEDDED_PNG || this == EMBEDDED_SVG;
    }

    @NotNull
    @Override
    public ComboBoxAdapter<MathConversionType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public MathConversionType[] getValues() { return values(); }
}
