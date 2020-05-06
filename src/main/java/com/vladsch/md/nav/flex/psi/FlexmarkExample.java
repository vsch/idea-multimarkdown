// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.StubBasedPsiElement;
import com.vladsch.md.nav.psi.element.MdBlockElementWithChildren;
import com.vladsch.md.nav.psi.element.MdBreadcrumbElement;
import com.vladsch.md.nav.psi.element.MdComposite;
import com.vladsch.md.nav.psi.element.MdStructureViewPresentableElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.List;

public interface FlexmarkExample extends StubBasedPsiElement<FlexmarkExampleStub>, MdComposite, MdStructureViewPresentableElement, MdBreadcrumbElement, MdBlockElementWithChildren {
    boolean hasCoordinates();

    @NotNull
    TextRange getContentRange(boolean inDocument);

    @Nullable
    ASTNode getSectionNode();

    @Nullable
    ASTNode getNumberNode();

    @NotNull
    String getSection();

    @NotNull
    String getNumber();

    @Nullable
    FlexmarkExampleOptions getOptionsList();

    @Nullable
    FlexmarkExampleSource getSource();

    @Nullable
    FlexmarkExampleHtml getHtml();

    @Nullable
    FlexmarkExampleAst getAst();

    boolean isIgnored();

    boolean isWithFail();

    boolean isWithErrors();

    /**
     * Set example coordinates and return pointer to new element
     *
     * @param section section or null if do not change
     * @param number  example number or null if do not change
     *
     * @return null if failed or pointer to new element
     */
    @Nullable
    FlexmarkExample setCoords(@Nullable String section, @Nullable String number);

    @Nullable
    FlexmarkExample setOptions(@Nullable List<String> options);

    @Nullable
    TextRange getFoldingRange();

    @Nullable
    String getLocationString();

    @Nullable
    String getPresentableText();

    @Nullable
    Icon getIcon(int flags);
}
