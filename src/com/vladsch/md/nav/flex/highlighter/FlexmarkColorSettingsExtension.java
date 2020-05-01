// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.highlighter;

import com.vladsch.md.nav.flex.PluginBundle;
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings;
import com.vladsch.md.nav.highlighter.MdHighlighterColors;
import com.vladsch.md.nav.highlighter.api.MdColorSettingsExtension;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlexmarkColorSettingsExtension implements MdColorSettingsExtension {
    @Nullable
    @Override
    public String getDemoText() {
        return null;
    }

    @Override
    public void addTextAttributes(@NotNull MdColorSettings colorSettings) {
        if (MdApplicationSettings.getInstance().getDebugSettings().getExtension(FlexmarkDebugSettings.KEY).getEnableFlexmarkFeatures()) {
            MdHighlighterColors highlighterColors = MdHighlighterColors.getInstance();

            colorSettings.addTextAttributesKey(PluginBundle.message("colors.debug.format-text-block"), highlighterColors.DEBUG_FORMAT_TEXT_BLOCK_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.debug.format-prefix"), highlighterColors.DEBUG_FORMAT_PREFIX_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.debug.flexmark-ast"), highlighterColors.DEBUG_FLEXMARK_AST_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.debug.flexmark-source"), highlighterColors.DEBUG_FLEXMARK_SOURCE_KEY);

            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-marker"), highlighterColors.FLEXMARK_MARKER_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-section"), highlighterColors.FLEXMARK_EXAMPLE_SECTION_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-section-markers"), highlighterColors.FLEXMARK_EXAMPLE_SECTION_MARKER_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-number"), highlighterColors.FLEXMARK_EXAMPLE_NUMBER_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-options-keyword"), highlighterColors.FLEXMARK_EXAMPLE_OPTIONS_KEYWORD_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-keyword"), highlighterColors.FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-options-marker"), highlighterColors.FLEXMARK_EXAMPLE_OPTIONS_MARKER_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-option"), highlighterColors.FLEXMARK_EXAMPLE_OPTION_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-option-param"), highlighterColors.FLEXMARK_EXAMPLE_OPTION_PARAM_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-option-param-marker"), highlighterColors.FLEXMARK_EXAMPLE_OPTION_PARAM_MARKER_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-option-built-in"), highlighterColors.FLEXMARK_EXAMPLE_OPTION_BUILT_IN_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-option-ignore"), highlighterColors.FLEXMARK_EXAMPLE_OPTION_IGNORE_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-option-fail"), highlighterColors.FLEXMARK_EXAMPLE_OPTION_FAIL_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-option-disabled"), highlighterColors.FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME_ATTR_KEY);
            colorSettings.addTextAttributesKey(PluginBundle.message("colors.flexmark-example-separator"), highlighterColors.FLEXMARK_EXAMPLE_SEPARATOR_ATTR_KEY);
        }
    }
}
