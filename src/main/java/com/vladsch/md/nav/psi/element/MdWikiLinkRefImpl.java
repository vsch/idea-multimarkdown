// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.vladsch.md.nav.MdProjectComponent;
import com.vladsch.md.nav.psi.reference.MdPsiReference;
import com.vladsch.md.nav.psi.reference.MdPsiReferenceWikiLinkRef;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.util.WikiLinkRef;
import org.jetbrains.annotations.NotNull;

public class MdWikiLinkRefImpl extends MdLinkRefImpl implements MdWikiLinkRef {
    public MdWikiLinkRefImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MdPsiReference createReference(@NotNull TextRange textRange, final boolean exactReference) {
        return new MdPsiReferenceWikiLinkRef(this, textRange, exactReference);
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return getParent() instanceof MdWikiLink ? ((MdWikiLink) getParent()).getDisplayName() : getFileName();
    }

    @NotNull
    @Override
    public String getFileName() {
        return WikiLinkRef.linkAsFile(new PathInfo(getText() == null ? "" : getText()).withExt(PathInfo.WIKI_PAGE_EXTENSION).getFilePath());
    }

    @NotNull
    @Override
    public String getFileNameWithAnchor() {
        return getFileName() + MdPsiImplUtil.getLinkAnchorText(getParent());
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
        return MdPsiImplUtil.setName(this, newName, renameFlags & (newName.contains("#") || ((MdWikiLink) getParent()).getLinkAnchorElement() != null ? ~RENAME_KEEP_ANCHOR : ~0));
    }

    @Override
    public String toString() {
        return "WIKI_LINK_REF '" + getName() + "' " + super.hashCode();
    }
}
