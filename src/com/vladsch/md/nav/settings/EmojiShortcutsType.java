// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.flexmark.ext.emoji.EmojiShortcutType;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum EmojiShortcutsType implements ComboBoxAdaptable<EmojiShortcutsType> {
    EMOJI_CHEAT_SHEET(EmojiShortcutType.EMOJI_CHEAT_SHEET, 0, MdBundle.message("markdown.parser.emoji-shortcut-type.emoji-cheat-sheet")),
    GITHUB(EmojiShortcutType.GITHUB, 1, MdBundle.message("markdown.parser.emoji-shortcut-type.github")),
    ANY_EMOJI_CHEAT_SHEET_PREFERRED(EmojiShortcutType.ANY_EMOJI_CHEAT_SHEET_PREFERRED, 2, MdBundle.message("markdown.parser.emoji-shortcut-type.any-emoji-cheat-sheet-preferred")),
    ANY_GITHUB_PREFERRED(EmojiShortcutType.ANY_GITHUB_PREFERRED, 3, MdBundle.message("markdown.parser.emoji-shortcut-type.any-github-preferred")),
    ;

    public final EmojiShortcutType flexmarkType;
    public final int intValue;
    public final @NotNull String displayName;

    EmojiShortcutsType(final EmojiShortcutType flexmarkType, final int intValue, @NotNull final String displayName) {
        this.flexmarkType = flexmarkType;
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<EmojiShortcutsType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(EMOJI_CHEAT_SHEET));

    @NotNull
    @Override
    public ComboBoxAdapter<EmojiShortcutsType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public EmojiShortcutsType[] getValues() { return values(); }
}
