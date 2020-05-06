// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.testUtil.cases;

import com.intellij.openapi.project.Project;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.testUtil.cases.MdCodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.renderers.LightFixtureSpecRenderer;
import icons.FlexmarkIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.HashMap;
import java.util.Map;

public interface MdEnhCodeInsightFixtureSpecTestCase extends MdCodeInsightFixtureSpecTestCase {
    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdCodeInsightFixtureSpecTestCase.getOptionsMap());
            }

            return optionsMap;
        }
    }

    static void resetSettings(@NotNull Project project) {
        MdCodeInsightFixtureSpecTestCase.resetSettings(project);

        validateDefaultSettings(project);
    }

    static void validateDefaultSettings(@NotNull Project project) {
        // FIX: add validation for link and image history
    }

    @NotNull
    static String resolveIcon(@Nullable Icon icon) {
        if (icon == null) return "null";

        // check all icons for match
        String iconName = FlexmarkIcons.getIconNamesMap().get(icon);
        if (iconName != null) return iconName;

        return MdCodeInsightFixtureSpecTestCase.resolveIcon(icon);
    }

    @Override
    default String resolveIconName(@Nullable Icon icon) {
        return resolveIcon(icon);
    }

    @Override
    default <T extends CodeInsightFixtureSpecTestCase> void augmentRenderer(@NotNull LightFixtureSpecRenderer<T> specRenderer) {

    }

    @Override
    default <T extends CodeInsightFixtureSpecTestCase> void finalizeRenderer(@NotNull LightFixtureSpecRenderer<T> specRenderer, @NotNull DataHolder specRendererOptions) {
        resetSettings(getProject());
    }
}
