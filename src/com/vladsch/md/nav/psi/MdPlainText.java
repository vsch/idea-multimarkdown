// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import com.vladsch.md.nav.psi.element.MdStubBasedPsiElement;
import com.vladsch.md.nav.psi.util.TextMapMatch;
import com.vladsch.md.nav.psi.util.TextMapElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Plain Referenceable Text Element Interface
 */
public interface MdPlainText<Stub extends StubElement<?>> extends MdStubBasedPsiElement<Stub> {
    @NotNull
    String getReferenceableText();

    @NotNull
    PsiElement replaceReferenceableText(@NotNull String text, int startOffset, int endOffset);

    int getReferenceableOffsetInParent();

    @NotNull
    TextMapElementType getTextMapType();

    @NotNull
    TextMapMatch[] getTextMapMatches();
}
