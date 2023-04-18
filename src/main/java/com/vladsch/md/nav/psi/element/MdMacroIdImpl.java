// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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

public class MdMacroIdImpl extends MdNamedElementImpl implements MdMacroId {

    public MdMacroIdImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getReferenceElement() {
        return getParent();
    }

    @Override
    public MdPsiReference createReference(@NotNull TextRange textRange, final boolean exactReference) {
        return null;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return MdIcons.Element.MACRO;
    }

    @Override
    public PsiElement setName(@NotNull String newName, int reason) {
        MdMacroImpl footnoteElement = MdPsiImplUtil.setMacroName((MdMacroImpl) getParent(), newName);
        return footnoteElement.getNameIdentifier();
    }

    @Override
    public String toString() {
        return "MACRO_ID '" + getName() + "' " + super.hashCode();
    }
}
