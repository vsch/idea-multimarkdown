// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.api;

import com.vladsch.flexmark.util.html.Attributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdFencedCodeImage {
    public final @NotNull String url;
    public final @NotNull String imageExtension;
    public final boolean isBlock;
    public final @Nullable Attributes imageAttributes;
    public final @Nullable Attributes blockAttributes;

    public MdFencedCodeImage(@NotNull String url, @NotNull String imageExtension, boolean isBlock, @Nullable Attributes imageAttributes, @Nullable Attributes blockAttributes) {
        this.url = url;
        this.imageExtension = imageExtension;
        this.isBlock = isBlock;
        this.imageAttributes = imageAttributes;
        this.blockAttributes = blockAttributes;
    }
}
