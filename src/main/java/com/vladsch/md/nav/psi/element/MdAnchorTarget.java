// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.psi.PsiElement;
import com.vladsch.flexmark.html.renderer.HtmlIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdAnchorTarget extends MdNamedElement {
    @Nullable
    PsiElement getAnchorReferenceElement();

    @Nullable
    String getAnchorReferenceId();

    @Nullable
    String getAnchorReferenceId(@Nullable HtmlIdGenerator generator);

    @Nullable
    String getAttributedAnchorReferenceId();

    @Nullable
    String getAttributedAnchorReferenceId(@Nullable HtmlIdGenerator generator);

    @Nullable
    MdAttributes getAttributesElement();

    @Nullable
    MdAttributeIdValue getIdValueAttribute();

    boolean isReferenceFor(@Nullable String referenceId);

    boolean isReferenceFor(@Nullable MdLinkAnchor refElement);

    @NotNull
    String getCompletionTypeText();
}
