// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.psi.element.MdNamedElement;
import com.vladsch.md.nav.psi.element.MdStructureViewPresentableItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FlexmarkExampleOption extends StubBasedPsiElement<FlexmarkExampleOptionStub>, MdNamedElement, MdStructureViewPresentableItem {
    @NotNull
    String getOptionName();

    boolean isIgnore();

    boolean isFail();

    boolean isBuiltIn();

    boolean isDisabled();

    @NotNull
    FlexmarkOptionInfo getOptionInfo();

    @Nullable
    String getOptionParams();

    @Override
    FlexmarkExampleOption handleContentChange(@NotNull TextRange range, @NotNull String newContent) throws IncorrectOperationException;

    @Override
    FlexmarkExampleOption handleContentChange(@NotNull String newContent) throws IncorrectOperationException;
}
