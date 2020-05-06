// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdFoldingBuilderProvider {
    ExtensionPointName<MdFoldingBuilderProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.foldingBuilderProvider");
    MdExtensions<MdFoldingBuilderProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdFoldingBuilderProvider[0]);

    @NotNull
    String getExtensionName();

    @Nullable
    Boolean isCollapsedByDefault(@NotNull ASTNode node);

    void extendFoldingHandler(@NotNull MdFoldingVisitorHandler handler, boolean quick);

    void extendFoldingOptions(@NotNull MdCodeFoldingOptionsHolder holder);
}
