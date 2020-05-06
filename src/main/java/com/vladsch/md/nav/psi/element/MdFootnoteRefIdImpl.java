// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.psi.util.MdTypes;
import org.jetbrains.annotations.NotNull;

public class MdFootnoteRefIdImpl extends MdReferencingElementReferenceImpl implements MdFootnoteRefId {
    public MdFootnoteRefIdImpl(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getReferenceDisplayName() {
        return MdFootnoteImpl.Companion.getREFERENCE_DISPLAY_NAME();
    }

    @NotNull
    @Override
    public IElementType getReferenceType() {
        return MdTypes.FOOTNOTE;
    }

    @Override
    public boolean isAcceptable(@NotNull PsiElement referenceElement, boolean forCompletion, final boolean exactReference) {
        return referenceElement instanceof MdFootnote;
    }
}
