/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownLinkRef;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.util.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiMarkdownReferenceLinkRef extends MultiMarkdownReference {
    private static final Logger logger = Logger.getLogger(MultiMarkdownReferenceLinkRef.class);

    public MultiMarkdownReferenceLinkRef(@NotNull MultiMarkdownLinkRef element, @NotNull TextRange textRange) {
        super(element, textRange);
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
        if (myElement instanceof MultiMarkdownLinkRef && element instanceof PsiFile) {
            FileReferenceLinkGitHubRules fileReferenceLink = new FileReferenceLinkGitHubRules(myElement.getContainingFile(), ((PsiFile) element));
            String linkRef = fileReferenceLink.getLinkRef();
            // this will create a new reference and loose connection to this one
            return myElement.setName(linkRef, MultiMarkdownNamedElement.REASON_FILE_MOVED);
        }
        return super.bindToElement(element);
    }

    @NotNull
    @Override
    protected ResolveResult[] getMultiResolveResults(boolean incompleteCode) {
        String name = myElement.getName();
        if (name != null && myElement.getContainingFile() != null && myElement.getContainingFile().getVirtualFile() != null) {
            if (!PathInfo.isExternalReference(name)) {
                FileReference sourceReference = new FileReference(myElement.getContainingFile());
                String anchor = MultiMarkdownPsiImplUtil.getLinkRefAnchor(myElement);
                PathInfo linkRefInfo = new PathInfo(name + (!anchor.isEmpty() ? "#" + anchor : ""));

                FileReferenceList fileReferenceList = new FileReferenceListQuery(myElement.getProject())
                        .caseInsensitive()
                        .gitHubWikiRules()
                        .keepLinkRefAnchor()
                        .linkRefIgnoreSubDirs()
                        .sameGitHubRepo()
                        .wantMarkdownFiles()
                        .inSource(sourceReference)
                        .matchLinkRef(linkRefInfo.getFileNameWithAnchorNoExt(), false)
                        .all();

                fileReferenceList = fileReferenceList
                        .postMatchFilter(linkRefInfo.getFilePath(), false, false)
                        .sorted();

                PsiFile[] files = fileReferenceList
                        .getPsiFiles();

                if (files.length > 0) {
                    resolveRefIsExternal = false;
                    removeReferenceChangeListener();
                    if (files.length == 1) {
                        return new ResolveResult[] { new PsiElementResolveResult(files[0]) };
                    } else {
                        List<ResolveResult> results = new ArrayList<ResolveResult>();
                        for (PsiFile file : files) {
                            results.add(new PsiElementResolveResult(file));
                        }
                        return results.toArray(new ResolveResult[results.size()]);
                    }
                } else if (sourceReference.resolveExternalLinkRef(name, false, false) != null) {
                    resolveRefIsExternal = true;
                } else {
                    resolveRefIsExternal = false;
                    return new ResolveResult[] { new PsiElementResolveResult(getMissingRefElement(name)) };
                }
            }
        }

        return EMPTY_RESULTS;
    }
}
