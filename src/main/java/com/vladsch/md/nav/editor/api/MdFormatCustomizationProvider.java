// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.format.CharWidthProvider;
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import com.vladsch.md.nav.util.MdExtensions;
import com.vladsch.md.nav.util.format.FlexmarkFormatOptionsAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdFormatCustomizationProvider {
    ExtensionPointName<MdFormatCustomizationProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.formatCustomizationProvider");
    MdExtensions<MdFormatCustomizationProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdFormatCustomizationProvider[0]);

    @Nullable
    CharWidthProvider createCharWidthProvider(@NotNull PsiEditContext editContext, int startOffset, int endOffset);

    void customizeParserOptions(@NotNull MutableDataHolder options);

    void customizeFormatOptions(@NotNull FlexmarkFormatOptionsAdapter adapter, @NotNull MutableDataHolder options);
}
