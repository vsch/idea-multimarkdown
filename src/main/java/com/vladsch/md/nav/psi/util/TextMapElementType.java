// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

import com.intellij.openapi.ui.ComboBox;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.psi.api.MdTextMapElementTypeProvider;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComboBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public final class TextMapElementType {
    final public static TextMapElementType NONE = new TextMapElementType("NONE", MdBundle.message("settings.link-map.element-type.none.display-name"), MdBundle.message("settings.link-map.element-type.none.banner"), true, true, true, MdIcons.Misc.LINK_MAP_NOT_USED);
    final public static TextMapElementType[] EMPTY_TYPES = new TextMapElementType[0];
    final public static TextMapElementType[] ELEMENT_TYPES = new TextMapElementType[] { NONE };

    private final @NotNull String name;
    private final @NotNull String displayName;
    private final @NotNull String banner;
    private final boolean hasOption;
    private final boolean allowPrefix;
    private final boolean hasReverseMap;
    private final @Nullable Icon icon;

    public TextMapElementType(@NotNull String name, @NotNull String displayName, @NotNull String banner, boolean hasOption, boolean allowPrefix, final boolean hasReverseMap, @Nullable Icon icon) {
        this.name = name;
        this.displayName = displayName;
        this.banner = banner;
        this.hasOption = hasOption;
        this.allowPrefix = allowPrefix;
        this.hasReverseMap = hasReverseMap;
        this.icon = icon;
    }

    @NotNull
    public String getBanner() {
        return banner;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    public boolean hasOption() {
        return hasOption;
    }

    public boolean isAllowPrefix() {
        return allowPrefix;
    }

    public boolean hasReverseMap() {
        return hasReverseMap;
    }

    @Nullable
    public Icon getIcon() {
        return icon;
    }

    @NotNull
    public static JComboBox<String> createComboBox(@NotNull TextMapElementType... exclude) {
        JComboBox<String> comboBox = new ComboBox<>();
        fillComboBox(comboBox, exclude);
        return comboBox;
    }

    public static void fillComboBox(@NotNull JComboBox<String> comboBox, @NotNull TextMapElementType... exclude) {
        Set<TextMapElementType> excluded = new HashSet<>(Arrays.asList(exclude));
        comboBox.removeAllItems();
        for (TextMapElementType item : getValues()) {
            if (!excluded.contains(item)) {
                String displayName = item.getDisplayName();
                comboBox.addItem(displayName);
            }
        }
    }

    private static TextMapElementType[] ourValues = null;

    @NotNull
    public static TextMapElementType[] getValues() {
        if (ourValues == null) {
            ArrayList<TextMapElementType> elementTypes = new ArrayList<>();
            for (MdTextMapElementTypeProvider provider : MdTextMapElementTypeProvider.EXTENSIONS.getValue()) {
                TextMapElementType[] types = provider.getElementTypes();
                elementTypes.addAll(Arrays.asList(types));
            }

            elementTypes.sort(Comparator.comparing(TextMapElementType::getDisplayName));
            elementTypes.add(NONE);
            ourValues = elementTypes.toArray(EMPTY_TYPES);
        }

        return ourValues;
    }

    @NotNull
    public static TextMapElementType findEnum(@Nullable String displayName) {
        if (displayName != null) {
            for (TextMapElementType type : getValues()) {
                if (type.getDisplayName().equals(displayName)) return type;
            }
        }

        return NONE;
    }

    @NotNull
    public static TextMapElementType getElementType(@Nullable String name) {
        if (name != null) {
            for (TextMapElementType type : getValues()) {
                if (type.getName().equals(name)) return type;
            }
        }

        return NONE;
    }
}
