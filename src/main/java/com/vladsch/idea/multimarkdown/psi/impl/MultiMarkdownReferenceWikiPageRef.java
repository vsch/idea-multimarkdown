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
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.util.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiMarkdownReferenceWikiPageRef extends MultiMarkdownReference {
    private static final Logger logger = Logger.getLogger(MultiMarkdownReferenceWikiPageRef.class);

    public MultiMarkdownReferenceWikiPageRef(@NotNull MultiMarkdownWikiPageRef element, @NotNull TextRange textRange) {
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
        if (myElement instanceof MultiMarkdownWikiPageRef && element instanceof PsiFile) {
            LinkRef linkRef = MultiMarkdownPsiImplUtil.getLinkRef(myElement);
            if (linkRef != null) {
                String linkRefText = new GitHubLinkResolver(myElement).linkAddress(linkRef, new FileRef((PsiFile) element), null, null, null);
                // this will create a new reference and loose connection to this one
                return myElement.setName(linkRefText, MultiMarkdownNamedElement.RENAME_KEEP_TEXT | MultiMarkdownNamedElement.RENAME_KEEP_RENAMED_TEXT);
            }
        }
        return super.bindToElement(element);
    }

    @NotNull
    @Override
    protected ResolveResult[] getMultiResolveResults(boolean incompleteCode) {
        String name = myElement.getName();
        if (name != null && myElement.getContainingFile() != null && myElement.getContainingFile().getVirtualFile() != null) {

            LinkRef linkRef = MultiMarkdownPsiImplUtil.getLinkRef(myElement);
            if (linkRef != null) {
                GitHubLinkResolver resolver = new GitHubLinkResolver(myElement);
                List<PathInfo> pathInfos = resolver.multiResolve(linkRef, LinkResolver.LOOSE_MATCH, null);

                if (pathInfos.size() > 0) {
                    List<ResolveResult> results = new ArrayList<ResolveResult>();
                    for (PathInfo pathInfo : pathInfos) {
                        if (pathInfo instanceof VirtualFileRef) {
                            PsiFile psiFile = ((VirtualFileRef) pathInfo).getPsiFile();
                            if (psiFile != null) {
                                results.add(new PsiElementResolveResult(psiFile));
                            }
                        }
                    }

                    if (results.size() > 0) {
                        removeReferenceChangeListener();
                        return results.toArray(new ResolveResult[results.size()]);
                    }
                }
                return new ResolveResult[] { new PsiElementResolveResult(getMissingRefElement(name)) };
            }
        }

        return EMPTY_RESULTS;
    }
}
