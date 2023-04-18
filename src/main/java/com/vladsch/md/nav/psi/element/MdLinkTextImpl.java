// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import com.vladsch.md.nav.psi.util.MdTypes;
import org.jetbrains.annotations.Nullable;

public class MdLinkTextImpl extends MdPsiElementImpl implements MdLinkText {
    @Nullable
    @Override
    public PsiReference getReference() {
        // if one of our children is an image or ref image we return null
        TokenSet imageSet = TokenSet.create(MdTypes.IMAGE, MdTypes.REFERENCE_IMAGE);
        PsiElement filter = findChildByFilter(imageSet);
        return filter != null ? null : super.getReference();
    }

    public MdLinkTextImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "LINK_REF_TEXT '" + getName() + "' " + super.hashCode();
    }
}
