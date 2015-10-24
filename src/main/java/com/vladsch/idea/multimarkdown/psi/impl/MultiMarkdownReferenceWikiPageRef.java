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
import com.vladsch.idea.multimarkdown.util.FileReferenceLinkGitHubRules;
import com.vladsch.idea.multimarkdown.util.FileReferenceList;
import com.vladsch.idea.multimarkdown.util.FileReferenceListQuery;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiMarkdownReferenceWikiPageRef extends MultiMarkdownReference {
    private static final Logger logger = Logger.getLogger(MultiMarkdownReferenceWikiPageRef.class);
    protected boolean resolveRefIsMissing;
    protected boolean resolveRefIsAccessible;

    public MultiMarkdownReferenceWikiPageRef(@NotNull MultiMarkdownWikiPageRef element, @NotNull TextRange textRange) {
        super(element, textRange);
    }

    public boolean isResolveRefMissing() {
        return resolveRefIsMissing;
    }

    public boolean isResolveRefIsAccessible() {
        return resolveRefIsAccessible;
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
            FileReferenceLinkGitHubRules fileReferenceLink = new FileReferenceLinkGitHubRules(myElement.getContainingFile(), ((PsiFile) element));
            String wikiPageRef = fileReferenceLink.getWikiPageRef();
            // this will create a new reference and loose connection to this one
            return myElement.setName(wikiPageRef, MultiMarkdownNamedElement.RENAME_KEEP_ANCHOR);
        }
        return super.bindToElement(element);
    }

    @NotNull
    @Override
    protected ResolveResult[] getMultiResolveResults(boolean incompleteCode) {
        String name = myElement.getName();
        if (name != null) {
            //logger.info("enter getMultiResolveResults" + " for " + myElement + " on " + Thread.currentThread());
            List<ResolveResult> results = new ArrayList<ResolveResult>();

            FileReferenceList fileReferenceList = new FileReferenceListQuery(myElement.getProject())
                    .gitHubWikiRules()
                    .matchWikiRef((MultiMarkdownWikiPageRef) myElement)
                    .accessibleWikiPageRefs()
                    .sorted();

            PsiFile[] files = fileReferenceList
                    .getPsiFiles();

            if (files.length > 0) {
                removeReferenceChangeListener();
                resolveRefIsMissing = false;
                resolveRefIsAccessible = true;

                for (PsiFile file : files) {
                    results.add(new PsiElementResolveResult(file));
                    //logger.info("Reference " + resolveResults.length + " for " + myElement + ": " + resolveResults[0].getElement().hashCode());
                }
            } else {
                // this one is missing, see if it is a matter of screwed up file name
                //FileReferenceList all = new FileReferenceListQuery(myElement.getProject())
                //        .spaceDashEqual()
                //        .caseInsensitive()
                //        .keepLinkRefAnchor()
                //        .wantMarkdownFiles()
                //        .gitHubWikiRules()
                //        .matchWikiRef((MultiMarkdownWikiPageRef) myElement)
                //        .all()
                //        .sorted();
                //
                //files = all.getPsiFilesWithAnchor();
                //
                //if (files.length > 0) {
                //    resolveRefIsAccessible = false;
                //
                //    for (PsiFile file : files) {
                //        results.add(new PsiElementResolveResult(file));
                //        //logger.info("Reference " + resolveResults.length + " for " + myElement + ": " + resolveResults[0].getElement().hashCode());
                //    }
                //} else
                {
                    // this one is missing, see if there is a missing ref registered already
                    resolveRefIsMissing = true;

                    //logger.info("getting dummy Reference" + " for " + myElement + " named: " + myElement.getMissingElementNamespace() + name);
                    MultiMarkdownNamedElement missingLinkElement = getMissingLinkElement(name);

                    //if (missingLinkElement == myElement) {
                    //    logger.info("dummy Reference" + " for " + myElement + " is itself");
                    //}
                    //
                    //logger.info("setting dummy Reference" + " for " + myElement + " named: " + myElement.getMissingElementNamespace() + name + " to " + missingLinkElement);
                    results.add(new PsiElementResolveResult(missingLinkElement));
                }
            }

            //logger.info("exit getMultiResolveResults" + " for " + myElement + " on " + Thread.currentThread());
            return results.toArray(new ResolveResult[results.size()]);
        }

        return EMPTY_RESULTS;
    }
}
