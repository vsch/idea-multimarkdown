// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.psi.element.MdLinkRef;
import com.vladsch.md.nav.psi.element.MdLinkRefElement;
import com.vladsch.md.nav.psi.element.MdNamedElement;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.util.LinkRef;
import com.vladsch.md.nav.util.ProjectFileRef;
import com.vladsch.md.nav.vcs.GitHubLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdPsiReferenceLinkRef extends MdPsiReference {
    public MdPsiReferenceLinkRef(@NotNull MdLinkRefElement element, @NotNull TextRange textRange, boolean exactReference) {
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
        if (myElement instanceof MdLinkRef) {
            PsiFile psiFile = element.getContainingFile();
            LinkRef linkRef = MdPsiImplUtil.getLinkRef(myElement);
            if (linkRef != null && psiFile != null) {
                GitHubLinkResolver resolver = new GitHubLinkResolver(myElement);
                ProjectFileRef targetRef = new ProjectFileRef(psiFile);
                LinkRef preservedLinkRef = resolver.preserveLinkFormat(linkRef, linkRef.replaceFilePath("", targetRef, false));
                String mappedLinkRef = resolver.denormalizedLinkRef(preservedLinkRef.getFilePath());
                return myElement.setName(mappedLinkRef, MdNamedElement.REASON_BIND_TO_FILE);
            }
        }
        return super.bindToElement(element);
    }
}
