// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.MdProjectComponent;
import com.vladsch.md.nav.psi.reference.MdPsiReference;
import com.vladsch.md.nav.psi.reference.MdPsiReferenceLinkRef;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.settings.ParserOptions;
import org.jetbrains.annotations.NotNull;

public class MdJekyllIncludeLinkRefImpl extends MdNamedElementImpl implements MdJekyllIncludeLinkRef {

    public MdJekyllIncludeLinkRefImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MdPsiReference createReference(@NotNull TextRange textRange, final boolean exactReference) {
        return new MdPsiReferenceLinkRef(this, textRange, exactReference);
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return getParent() instanceof MdJekyllInclude ? ((MdJekyllInclude) getParent()).getDisplayName() : getFileName();
    }

    @NotNull
    @Override
    public String getFileName() {
        boolean fixedIncludePath = (MdRenderingProfileManager.getProfile(((PsiElement) this).getContainingFile()).getParserSettings().getOptionsFlags() & ParserOptions.GITBOOK_URL_ENCODING.getFlags()) == 0;
        if (fixedIncludePath) {
            return "/_includes/" + getFileNameWithAnchor();
        } else {
            return getFileNameWithAnchor();
        }
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

        return MdPsiImplUtil.setName(this, newName, renameFlags);
    }

    @Override
    public String toString() {
        return "LINK_REF '" + getName() + "' " + super.hashCode();
    }
}
