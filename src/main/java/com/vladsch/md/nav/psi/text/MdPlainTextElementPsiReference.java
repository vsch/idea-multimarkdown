// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.text;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.md.nav.parser.api.MdLinkMapProvider;
import com.vladsch.md.nav.psi.MdPlainText;
import com.vladsch.md.nav.psi.util.TextMapElementType;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.util.FileRef;
import com.vladsch.md.nav.util.LinkRef;
import com.vladsch.md.nav.util.Local;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.util.ProjectFileRef;
import com.vladsch.md.nav.util.Remote;
import com.vladsch.md.nav.util.Want;
import com.vladsch.md.nav.vcs.GitHubLinkResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdPlainTextElementPsiReference extends PsiReferenceBase<MdPlainTextElementImpl> implements /*PsiPolyVariantReference,*/ BindablePsiReference {
    public static final Object[] OBJECTS = new Object[0];

    private final PsiElement myTarget;

    @Override
    public String toString() {
        return "Plain Text Reference for " + myElement.toString() + " to " + myTarget;
    }

    public MdPlainTextElementPsiReference(@NotNull MdPlainTextElementImpl element, @NotNull TextRange textRange, @NotNull PsiElement target) {
        super(element, textRange);
        myTarget = target;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // we will handle this by renaming the element to point to the new location
        if (element instanceof PsiFile) {
            // need to rename our element so that its reference text will refer to this new element, then we can rebind
            GitHubLinkResolver resolver = new GitHubLinkResolver(myElement);
            ProjectFileRef targetRef = new ProjectFileRef((PsiFile) element);
            FileRef containingFile = new FileRef(myElement.getContainingFile());
            LinkRef linkRef = new LinkRef(containingFile, "", null, null, true);
            PathInfo pathInfo = resolver.processMatchOptions(linkRef, targetRef, Want.INSTANCE.invoke(Local.getREF(), Remote.getREF()));
            if (pathInfo != null) {
                MdRenderingProfile renderingProfile = MdRenderingProfileManager.getInstance(element.getProject()).getRenderingProfile(element.getContainingFile());
                TextMapElementType textMapElementType = ((MdPlainText<?>) myElement.getParent()).getTextMapType();
                String filePath = pathInfo.getFilePath();
                for (MdLinkMapProvider provider : MdLinkMapProvider.EXTENSIONS.getValue()) {
                    String mappedLinkRef = provider.mapFileRef(textMapElementType, filePath, renderingProfile);
                    if (mappedLinkRef != null) {
                        return myElement.setName(mappedLinkRef);
                    }
                }
            }
            return myElement;
        }
        throw new IncorrectOperationException("Rebind cannot be performed for " + getClass());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return myTarget;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return OBJECTS;
    }
}
