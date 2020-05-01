// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.ui.components.JBCheckBox;
import com.vladsch.md.nav.settings.EmojiImagesType;
import com.vladsch.md.nav.settings.EmojiShortcutsType;
import com.vladsch.md.nav.settings.MdParserSettingsForm;
import com.vladsch.md.nav.settings.ParserOptions;
import com.vladsch.md.nav.settings.PegdownExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MdParserSettingsComponent<T> extends MdSettingsComponent<T> {
    void addPegdownExtensionCheckboxes(Map<JBCheckBox, PegdownExtensions> checkBoxPegdownMap);

    void addParserOptionCheckboxes(Map<JBCheckBox, ParserOptions> checkBoxParserMap);

    /**
     * Return parser profile based on settings
     *
     * @param pegdownOptions
     * @param parserOptions
     * @param emojiShortcutsType
     * @param emojiImagesType
     *
     * @return null or parser profile selected by settings
     */
    @Nullable
    MdParserSettingsForm.ParserProfile getProfile(int pegdownOptions, long parserOptions, EmojiShortcutsType emojiShortcutsType, EmojiImagesType emojiImagesType);

    @Nullable
    MdParserSettingsForm.ParserProfileItem getParserProfileItem(MdParserSettingsForm.ParserProfile parserProfile);

    @Nullable
    MdParserSettingsForm.ParserProfileItem[] getParserProfileItems();
}
