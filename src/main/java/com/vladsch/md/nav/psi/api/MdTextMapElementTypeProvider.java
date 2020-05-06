// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.psi.util.TextMapElementType;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdTextMapElementTypeProvider {
    ExtensionPointName<MdTextMapElementTypeProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.textMapElementTypeProvider");
    MdExtensions<MdTextMapElementTypeProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdTextMapElementTypeProvider[0]);

    @NotNull
    TextMapElementType[] getElementTypes();
}
