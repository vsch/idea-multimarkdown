// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.psi.reference.MdPsiReference;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import org.jetbrains.annotations.NotNull;

public class MdImageLinkTextImpl extends MdPsiElementImpl implements MdImageLinkText {
    public MdImageLinkTextImpl(ASTNode node) {
        super(node);
    }

    //@Override
    //public MdPsiReference createReference(@NotNull TextRange textRange, final boolean exactReference) {
    //    return new MdPsiReference(this, textRange, exactReference);
    //}
    //
    //@NotNull
    //@Override
    //public String getDisplayName() {
    //    return getParent() instanceof MdImageLink ? ((MdImageLink) getParent()).getDisplayName() : getName();
    //}
    //
    //@Override
    //public PsiElement setName(@NotNull String newName, int reason) {
    //    return MdPsiImplUtil.setName(this, newName, reason);
    //}

    @Override
    public String toString() {
        return "IMAGE_LINK_REF_TEXT '" + getName() + "' " + super.hashCode();
    }
}
