// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface MdHeaderText extends MdNamedElement {
    @Nullable
    PsiElement getAttributesElement();

    int getTrailingAttributesLength();

    @Nullable
    PsiElement getIdValueAttribute();
}
