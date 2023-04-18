// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.api;

import org.jetbrains.annotations.NotNull;

public class MdImageFencedCode {
    public final @NotNull String content;
    public final @NotNull String[] info;
    public final @NotNull String conversionVariant;
    public final boolean isBlock;

    public MdImageFencedCode(@NotNull String content, @NotNull String[] info, @NotNull String conversionVariant, boolean isBlock) {
        this.content = content;
        this.info = info;
        this.conversionVariant = conversionVariant;
        this.isBlock = isBlock;
    }
}
