// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.MdProjectComponent;
import com.vladsch.md.nav.psi.reference.MdPsiReference;
import com.vladsch.md.nav.psi.reference.MdPsiReferenceImageLinkRef;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import org.jetbrains.annotations.NotNull;

public class MdImageLinkRefImpl extends MdLinkRefImpl implements MdImageLinkRef {
    public MdImageLinkRefImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MdPsiReference createReference(@NotNull TextRange textRange, final boolean exactReference) {
        return new MdPsiReferenceImageLinkRef(this, textRange, exactReference);
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return getParent() instanceof MdImageLink ? ((MdImageLink) getParent()).getDisplayName() : getFileName();
    }

    @NotNull
    @Override
    public String getFileName() {
        return getText() == null ? "" : getText();
    }

    @NotNull
    @Override
    public String getFileNameWithAnchor() {
        return getNameWithAnchor();
    }

    @NotNull
    @Override
    public String getNameWithAnchor() {
        return MdPsiImplUtil.getLinkRefTextWithAnchor(getParent());
    }

    @Override
    public PsiElement setName(@NotNull String newName, int renameFlags) {
        MdProjectComponent projectComponent = MdProjectComponent.getInstance(getProject());

        if (projectComponent.getRefactoringRenameFlags() != RENAME_NO_FLAGS) renameFlags = projectComponent.getRefactoringRenameFlags();
        renameFlags &= ~RENAME_KEEP_ANCHOR;

        return MdPsiImplUtil.setName(this, newName, renameFlags);
    }

    @Override
    public String toString() {
        return "IMAGE_LINK_REF '" + getName() + "' " + super.hashCode();
    }
}
