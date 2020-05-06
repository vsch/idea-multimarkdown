// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.vladsch.md.nav.psi.MdPlainText;
import com.vladsch.md.nav.psi.element.MdBlockElementWithChildren;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FlexmarkFrontMatterBlock extends MdPlainText<FlexmarkFrontMatterBlockStub>, NavigationItem, PsiLanguageInjectionHost, MdBlockElementWithChildren {
    @NotNull
    String getContent();

    @NotNull
    PsiElement setContent(@Nullable String htmlBlock);

    @NotNull
    TextRange getContentRange();
}
