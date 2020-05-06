// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.psi.reference.MdPsiReference;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import icons.MdIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class MdAbbreviationIdImpl extends MdNamedElementImpl implements MdAbbreviationId {

    public MdAbbreviationIdImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MdPsiReference createReference(@NotNull TextRange textRange, final boolean exactReference) {
        return null;
    }

    @Override
    public PsiElement getReferenceElement() {
        return getParent();
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return MdIcons.Element.ABBREVIATION;
    }

    @Override
    public PsiElement setName(@NotNull String newName, int reason) {
        MdAbbreviationImpl abbreviationElement = MdPsiImplUtil.setAbbreviationName((MdAbbreviationImpl) getParent(), newName);
        return abbreviationElement.getNameIdentifier();
    }

    @Override
    public String toString() {
        return "ABBREVIATION_ID '" + getName() + "' " + super.hashCode();
    }
}
