// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.psi.element.MdRefAnchor;
import com.vladsch.md.nav.psi.element.MdRefAnchorId;
import com.vladsch.md.nav.psi.element.MdReferenceElementIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdPsiReferenceAnchorRefId extends MdPsiReference {
    protected MdRefAnchor myRefAnchor;

    public MdPsiReferenceAnchorRefId(@NotNull MdRefAnchorId element, @NotNull MdRefAnchor refAnchorElement, @NotNull TextRange textRange, boolean exactReference) {
        super(element, textRange, exactReference);

        myRefAnchor = refAnchorElement;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return myRefAnchor.getReferenceIdentifier();
    }

    @NotNull
    @Override
    protected ResolveResult[] getMultiResolveResults(final boolean incompleteCode) {
        final MdReferenceElementIdentifier referenceIdentifier = myRefAnchor.getReferenceIdentifier();
        return referenceIdentifier != null ? new ResolveResult[] { new PsiElementResolveResult(referenceIdentifier) } : new ResolveResult[0];
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // we will handle this by renaming the element to point to the new location
        return super.bindToElement(element);
    }
}
