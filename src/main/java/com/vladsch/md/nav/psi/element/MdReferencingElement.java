// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdReferencingElement extends MdPsiElement {
    @Nullable
    ItemPresentation getPresentation();

    String getDisplayName();

    @NotNull
    IElementType getReferenceType();

    @NotNull
    String getReferenceId();

    @Nullable
    MdReferencingElementText getReferenceTextElement();

    @Nullable
    String getReferenceText();

    @Nullable
    MdReferencingElementReference getReferenceIdElement();

    // type of child element holding the reference
    @NotNull
    String getToStringName();

    @NotNull
    IElementType getReferenceChildType();

    @NotNull
    IElementType getTextChildType();

    @NotNull
    MdReferenceElement[] getReferenceElements();

    @Nullable
    MdReferenceElement getReferenceElement();
}
