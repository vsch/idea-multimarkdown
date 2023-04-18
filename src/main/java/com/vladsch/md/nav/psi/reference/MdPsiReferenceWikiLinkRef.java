// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.MdProjectComponent;
import com.vladsch.md.nav.psi.element.MdExplicitLink;
import com.vladsch.md.nav.psi.element.MdLinkElement;
import com.vladsch.md.nav.psi.element.MdRenameElement;
import com.vladsch.md.nav.psi.element.MdWikiLinkRef;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.util.FileRef;
import com.vladsch.md.nav.util.LinkRef;
import com.vladsch.md.nav.util.ProjectFileRef;
import com.vladsch.md.nav.vcs.GitHubLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdPsiReferenceWikiLinkRef extends MdPsiReference {
    public MdPsiReferenceWikiLinkRef(@NotNull MdWikiLinkRef element, @NotNull TextRange textRange, boolean exactReference) {
        super(element, textRange, exactReference);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length > 0 ? resolveResults[0].getElement() : null;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // we will handle this by renaming the element to point to the new location
        if (myElement instanceof MdWikiLinkRef && element instanceof PsiFile) {
            LinkRef linkRef = MdPsiImplUtil.getLinkRef(myElement);
            if (linkRef != null) {
                // here if the old file name has anchor and the new one does not we cannot know that the anchor needs to be dropped, hence the new RENAME_DROP_ANCHOR flags
                // to signal that the old file name had anchor and the new one does not
                PsiFile psiFile = element.getContainingFile();
                ProjectFileRef targetRef = new ProjectFileRef(psiFile);

                int renameFlags = MdProjectComponent.getInstance(myElement.getProject()).getRefactoringRenameFlags();
                if (renameFlags != MdRenameElement.RENAME_NO_FLAGS && (renameFlags & MdRenameElement.RENAME_DROP_ANCHOR) != 0) {
                    linkRef = linkRef.removeAnchor();
                }

                FileRef containingFile = new FileRef(psiFile.getOriginalFile());
                if (targetRef.isUnderWikiDir() && targetRef.getWikiDir().equals(containingFile.getWikiDir())) {
                    String linkAddress = new GitHubLinkResolver(myElement).linkAddress(linkRef, new FileRef(psiFile), null, null, null, true);
                    // this will create a new reference and loose connection to this one
                    return myElement.setName(linkAddress, MdRenameElement.REASON_BIND_TO_FILE);
                } else {
                    // change to explicit link
                    GitHubLinkResolver resolver = new GitHubLinkResolver(myElement);
                    LinkRef expLinkRef = LinkRef.from(linkRef, resolver.getLinkEncodingExclusionMap());
                    String linkAddress = resolver.linkAddress(expLinkRef, new FileRef(psiFile), null, null, "", true);
                    String mappedAddress = resolver.denormalizedLinkRef(linkAddress);

                    PsiElement newLink = MdPsiImplUtil.changeToExplicitLink((MdLinkElement<?>) myElement.getParent(), mappedAddress);
                    if (newLink instanceof MdExplicitLink) {
                        return MdPsiImplUtil.findChildByType(newLink, MdTypes.LINK_REF);
                    }
                }
            }
        }
        return super.bindToElement(element);
    }
}
