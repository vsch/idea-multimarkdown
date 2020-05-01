// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PlantUmlConversionType implements ComboBoxAdaptable<PlantUmlConversionType> {
    NONE(-1, MdBundle.message("plant-uml.conversion.none"), ""),
    EMBEDDED(0, MdBundle.message("plant-uml.conversion.embedded"), ".png"),
    GRAVIZO_PNG(1, MdBundle.message("plant-uml.conversion.gravizo-png"), ".png"),
    GRAVIZO_SVG(2, MdBundle.message("plant-uml.conversion.gravizo-svg"), ".svg"),
    ;

    public final int intValue;
    public final @NotNull String displayName;
    public final @NotNull String extension;

    PlantUmlConversionType(int intValue, @NotNull String displayName, @NotNull String extension) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.extension = extension;
    }

    public static Static<PlantUmlConversionType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(EMBEDDED));

    @Nullable
    public String urlPrefix() {
        switch (this) {
            case GRAVIZO_PNG:
                return "https://g.gravizo.com/g?";
            case GRAVIZO_SVG:
                return "https://g.gravizo.com/svg?";

            default:
                return null;
        }
    }

    public boolean isEmbedded() {
        return this == EMBEDDED;
    }

    @NotNull
    @Override
    public ComboBoxAdapter<PlantUmlConversionType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public PlantUmlConversionType[] getValues() { return values(); }
}
