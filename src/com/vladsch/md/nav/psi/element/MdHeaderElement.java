// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import com.vladsch.md.nav.util.looping.MdPsiIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdHeaderElement extends MdTaskItemContainer, MdNamedElement, MdAnchorTarget, MdStructureViewPresentableElement, MdBreadcrumbElement, MdBlockElementWithChildren {
    int getHeaderLevel();

    MdHeaderElement setHeaderLevel(int level, @NotNull PsiEditContext editContext);

    int getTrailingAttributesLength();

    boolean getCanIncreaseLevel();

    boolean getCanDecreaseLevel();

    @Nullable
    MdHeaderText getHeaderTextElement();

    @NotNull
    String getHeaderText();

    @NotNull
    String getHeaderTextNoFormatting();

    @NotNull
    String getHeaderMarker();

    @NotNull
    default MdPsiIterator<PsiElement> nestedHeadingSectionLooping() {
        return nestedHeadingSectionLooping(true);
    }

    @NotNull
    default MdPsiIterator<PsiElement> headingSectionLooping() {
        return nestedHeadingSectionLooping(false);
    }

    @NotNull
    MdPsiIterator<PsiElement> nestedHeadingSectionLooping(boolean wantNestedSubHeadings);

    @Nullable
    ASTNode getHeaderMarkerNode();
}
