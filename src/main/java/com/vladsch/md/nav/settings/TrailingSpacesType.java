// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.flexmark.util.format.options.TrailingSpaces;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

public enum TrailingSpacesType implements ComboBoxAdaptable<TrailingSpacesType> {
    KEEP_ALL(0, MdBundle.message("trailing-spaces.keep-type.keep-all"), TrailingSpaces.KEEP_ALL),
    KEEP_LINE_BREAK(1, MdBundle.message("trailing-spaces.keep-type.keep-break"), TrailingSpaces.KEEP_LINE_BREAK),
    KEEP_NONE(2, MdBundle.message("trailing-spaces.keep-type.keep-none"), TrailingSpaces.KEEP_NONE);

    public final @NotNull String displayName;
    public final int intValue;
    public final TrailingSpaces flexMarkEnum;

    TrailingSpacesType(int intValue, @NotNull String displayName, @NotNull TrailingSpaces flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<TrailingSpacesType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(KEEP_LINE_BREAK));

    @NotNull
    @Override
    public ComboBoxAdapter<TrailingSpacesType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TrailingSpacesType[] getValues() { return values(); }

    public static String[] getDisplayNames(@NotNull TrailingSpacesType... exclude) {
        HashSet<TrailingSpacesType> excluded = new HashSet<>(Arrays.asList(exclude));
        String[] names = new String[values().length - excluded.size()];
        int i = 0;
        for (TrailingSpacesType value : values()) {
            if (!excluded.contains(value)) {
                names[i++] = value.displayName + " ";
            }
        }
        return names;
    }

    public static int[] getOptionValues(@NotNull TrailingSpacesType... exclude) {
        HashSet<TrailingSpacesType> excluded = new HashSet<>(Arrays.asList(exclude));
        int[] options = new int[values().length - excluded.size()];
        int i = 0;
        for (TrailingSpacesType value : values()) {
            if (!excluded.contains(value)) {
                options[i++] = value.intValue;
            }
        }
        return options;
    }
}
