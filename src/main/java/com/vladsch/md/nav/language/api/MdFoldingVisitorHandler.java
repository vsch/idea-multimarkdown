// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.psi.util.MdNodeVisitorHandler;
import com.vladsch.md.nav.psi.util.MdVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdFoldingVisitorHandler extends MdNodeVisitorHandler {

    @Nullable
    MdVisitor<PsiElement> getFoldingHandler(@NotNull Class<? extends PsiElement> klass);

    <T extends PsiElement> boolean delegateFoldingHandler(@NotNull Class<T> forClass, Class<? extends T> toClass);

    @NotNull
    CharSequence getRootText();

    boolean addDescriptor(@NotNull FoldingDescriptor descriptor);

    @NotNull
    String getDefaultPlaceHolderText();
}
