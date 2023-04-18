// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.util.ImageLinkRef;
import com.vladsch.md.nav.util.LinkRef;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.vcs.GitHubLinkResolver;
import com.vladsch.md.nav.vcs.GitHubVcsRoot;
import org.jetbrains.annotations.NotNull;

import static com.vladsch.plugin.util.image.ImageUtils.isEncodedImage;

public class MdReferenceImageReferenceImpl extends MdReferencingElementReferenceImpl implements MdReferenceImageReference {
    public MdReferenceImageReferenceImpl(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getReferenceDisplayName() {
        return MdReferenceImpl.REFERENCE_DISPLAY_NAME;
    }

    @NotNull
    @Override
    public IElementType getReferenceType() {
        return MdTypes.REFERENCE;
    }

    @Override
    public boolean isAcceptable(@NotNull PsiElement referenceElement, boolean forCompletion, final boolean exactReference) {
        if (referenceElement instanceof MdReference) {
            final String linkRefText = ((MdReference) referenceElement).getLinkRefText();

            if (isEncodedImage(linkRefText)) {
                return true;
            }

            PathInfo refInfo = new PathInfo(linkRefText);
            if (forCompletion) return true;
            //if (!refInfo.isURL() || !forCompletion) return true;

            if (!refInfo.isURL()) {
                return refInfo.getHasExt() && refInfo.isImageExt();
            }

            if (refInfo.getHasExt() && !refInfo.isImageExt() && exactReference) return false;

            GitHubLinkResolver resolver = new GitHubLinkResolver(getContainingFile());
            LinkRef linkRef = new ImageLinkRef(resolver.getContainingFile(), refInfo.getFilePath(), null, null, false);
            if (resolver.isExternalUnchecked(linkRef)) return true;

            if (!exactReference) return true;

            // need to see if it is a blob reference
            LinkRef normalizedLinkRef = resolver.normalizedLinkRef(linkRef);
            GitHubVcsRoot vcsRoot = resolver.getProjectResolver().getVcsRoot(resolver.getContainingFile());
            if (vcsRoot == null) return true;
            String prefix = vcsRoot.getBaseUrl() + "blob/";
            String filePath = normalizedLinkRef.getFilePath();
            return !filePath.startsWith(prefix);
        }
        return false;
    }
}
