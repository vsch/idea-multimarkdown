// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

public interface MdLinkRefCompletionExtension {
    ExtensionPointName<MdLinkRefCompletionExtension> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.linkRefCompletionExtension");
    MdExtensions<MdLinkRefCompletionExtension> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdLinkRefCompletionExtension[0]);

    boolean overrideCompletion(@NotNull String beforeCursor, @NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet resultSet, @NotNull PsiElement element, @NotNull MdFile containingFile);

    int invocationCountAdjustment();
}
