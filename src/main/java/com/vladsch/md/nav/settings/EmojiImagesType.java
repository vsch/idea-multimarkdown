// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.flexmark.ext.emoji.EmojiImageType;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum EmojiImagesType implements ComboBoxAdaptable<EmojiImagesType> {
    IMAGE_ONLY(EmojiImageType.IMAGE_ONLY, 0, MdBundle.message("markdown.parser.emoji-image-type.image-only")),
    UNICODE_FALLBACK_TO_IMAGE(EmojiImageType.UNICODE_FALLBACK_TO_IMAGE, 1, MdBundle.message("markdown.parser.emoji-image-type.unicode-fallback-to-image")),
    UNICODE_ONLY(EmojiImageType.UNICODE_ONLY, 2, MdBundle.message("markdown.parser.emoji-image-type.unicode-only")),
    ;

    public final EmojiImageType flexmarkType;
    public final int intValue;
    public final @NotNull String displayName;

    EmojiImagesType(final EmojiImageType flexmarkType, final int intValue, @NotNull final String displayName) {
        this.flexmarkType = flexmarkType;
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<EmojiImagesType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(IMAGE_ONLY));

    @NotNull
    @Override
    public ComboBoxAdapter<EmojiImagesType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public EmojiImagesType[] getValues() { return values(); }
}
