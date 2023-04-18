// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdReferenceElement extends MdNamedElement {
    @NotNull
    String getReferenceId();

    @NotNull
    default String getNormalizedReferenceId() {
        return normalizeReferenceId(getReferenceId());
    }

    @NotNull
    String normalizeReferenceId(@Nullable String referenceId);

    @Nullable
    MdReferenceElementIdentifier getReferenceIdentifier();

    @Nullable
    String getReferencingElementText();

    @NotNull
    String getReferenceDisplayName();

    boolean isReferenceFor(@Nullable String referenceId);

    boolean isReferenceFor(@Nullable MdReferencingElement refElement);

    IElementType getReferenceType();

    boolean isReferenced();
}
