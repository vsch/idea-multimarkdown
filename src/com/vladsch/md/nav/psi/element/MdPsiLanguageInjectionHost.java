// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdPsiLanguageInjectionHost extends PsiLanguageInjectionHost {
    @Nullable
    ASTNode getContentElement();

    @NotNull
    String getContent();

    @NotNull
    PsiElement setContent(@NotNull String content);

    @NotNull
    default TextRange getContentRange() {
        return getContentRange(false);
    }

    @NotNull
    TextRange getContentRange(boolean inDocument);
}
