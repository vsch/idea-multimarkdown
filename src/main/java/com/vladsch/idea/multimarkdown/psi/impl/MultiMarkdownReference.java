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
import com.intellij.psi.*;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownElementType;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownLinkRef;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLink;
import com.vladsch.idea.multimarkdown.util.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiMarkdownReference extends PsiReferenceBase<MultiMarkdownNamedElement> implements PsiPolyVariantReference, BindablePsiReference {
    private static final Logger logger = Logger.getLogger(MultiMarkdownReference.class);
    public static final ResolveResult[] EMPTY_RESULTS = new ResolveResult[0];
    protected ResolveResult[] resolveResults;
    protected String resolveResultsName;
    protected final ReferenceChangeListener referenceChangeListener;
    protected boolean resolveRefIsMissing;
    protected boolean resolveRefIsExternal;

    @Override
    public String toString() {
        //PsiElement resolve = resolve();
        return "Reference for " + myElement.toString();
    }

    public MultiMarkdownReference(@NotNull MultiMarkdownNamedElement element, @NotNull TextRange textRange) {
        super(element, textRange);

        final MultiMarkdownReference thizz = this;
        referenceChangeListener = new ReferenceChangeListener() {
            @Override
            public void referenceChanged(@Nullable String name) {
                if (resolveResultsName != null && (name == null || resolveResultsName.equals(name))) {
                    resolveResults = null;
                    resolveResultsName = null;
                }
            }
        };
    }

    protected void removeReferenceChangeListener() {
        if (resolveRefIsMissing && myElement.getParent() != null) {
            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(myElement.getProject());
            if (projectComponent != null) {
                projectComponent.removeListener(myElement.getMissingElementNamespace(), referenceChangeListener);
            }
        }

        resolveRefIsMissing = false;
    }

    @NotNull
    protected MultiMarkdownNamedElement getMissingRefElement(@NotNull String name) {
        if (myElement.getParent() != null) {
            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(myElement.getProject());
            if (projectComponent != null) {
                String namespace = myElement.getMissingElementNamespace();

                MultiMarkdownNamedElement referencedElement;

                referencedElement = projectComponent.getMissingLinkElement(myElement, namespace, name);

                if (!resolveRefIsMissing) {
                    projectComponent.addListener(namespace, referenceChangeListener);
                    resolveRefIsMissing = true;
                }

                return referencedElement;
            }
        }
        return myElement;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        if (incompleteCode) {
            return getMultiResolveResults(true);
        } else {
            if (resolveResults == null || resolveResultsName == null || !resolveResultsName.equals(getElement().getName())) {
                resolveResultsName = getElement().getName();
                if (resolveResultsName == null) resolveResultsName = "";
                setRangeInElement(new TextRange(0, resolveResultsName.length()));
                resolveResults = getMultiResolveResults(false);
            }
            return resolveResults;
        }
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // we will handle this by renaming the element to point to the new location
        if (myElement instanceof MultiMarkdownWikiLink || myElement instanceof MultiMarkdownLinkRef) {
            if (element instanceof PsiFile) {
                LinkRef linkRef = MultiMarkdownPsiImplUtil.getLinkRef(myElement);
                if (linkRef != null) {
                    String linkRefText = new GitHubLinkResolver(myElement).linkAddress(linkRef, new FileRef((PsiFile) element), null, null, null);
                    // this will create a new reference and loose connection to this one
                    return myElement.setName(linkRefText, MultiMarkdownNamedElement.REASON_BIND_TO_FILE);
                }
            }
        } else if (element.getClass() == myElement.getClass()) {
            String name = ((MultiMarkdownNamedElement) element).getName();
            // this will create a new reference and lose connection to this one
            // logger.info("rebinding " + myElement + " to " + element);
            if (name != null) return myElement.setName(name, MultiMarkdownNamedElement.REASON_FILE_MOVED);
        }
        throw new IncorrectOperationException("Rebind cannot be performed for " + getClass());
    }

    public boolean isResolveRefMissing() {
        return resolveRefIsMissing;
    }

    public boolean isResolveRefExternal() {
        return resolveRefIsExternal;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length > 0 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        //List<LookupElement> variants = new ArrayList<LookupElement>();
        //Project project = myElement.getProject();
        //List<MultiMarkdownFile> wikiFiles = MultiMarkdownUtil.findWikiFiles(project,
        //        myElement.getContainingFile() instanceof MultiMarkdownFile && ((MultiMarkdownFile) myElement.getContainingFile()).isWikiPage());
        //for (final MultiMarkdownFile wikFile : wikiFiles) {
        //    if (wikFile.isPageReference(name, myElement.getContainingFile().getVirtualFile())) {
        //        variants.add(LookupElementBuilder.create(wikFile).
        //                        withIcon(MultiMarkdownIcons.FILE).
        //                        withTypeText(wikFile.getContainingFile().getName())
        //        );
        //    }
        //}
        //return variants.toArray();
        return new Object[0];
    }

    /**
     * Default implementation resolves to missing element reference by namespace of the referencing element
     *
     * @param incompleteCode code is incomplete
     * @return resolve results
     */
    @NotNull
    protected ResolveResult[] getMultiResolveResults(boolean incompleteCode) {
        String name = myElement.getName();
        if (name != null) {
            if (myElement instanceof MultiMarkdownWikiLink || myElement instanceof MultiMarkdownLinkRef) {
                if (myElement.getContainingFile() != null && myElement.getContainingFile().getVirtualFile() != null) {

                    LinkRef linkRef = MultiMarkdownPsiImplUtil.getLinkRef(myElement);
                    if (linkRef != null) {
                        GitHubLinkResolver resolver = new GitHubLinkResolver(myElement);
                        List<PathInfo> pathInfos = resolver.multiResolve(linkRef, LinkResolver.PREFER_LOCAL | (incompleteCode ? LinkResolver.LOOSE_MATCH : 0), null);

                        if (pathInfos.size() > 0) {
                            List<ResolveResult> results = new ArrayList<ResolveResult>();
                            for (PathInfo pathInfo : pathInfos) {
                                if (pathInfo instanceof ProjectFileRef) {
                                    PsiFile psiFile = ((ProjectFileRef) pathInfo).getPsiFile();
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
            } else {
                // these are always missing but we create references by namespace and name of the element in the project so they can be renamed as a group
                // skip complex ones that contain other parsable elements
                PsiElement[] children = getElement().getChildren();
                for (PsiElement child : children) {
                    if (child.getNode().getElementType() instanceof MultiMarkdownElementType) return EMPTY_RESULTS;
                }

                MultiMarkdownNamedElement missingLinkElement = getMissingRefElement(name);
                return new ResolveResult[] { new PsiElementResolveResult(missingLinkElement) };
            }
        }

        return EMPTY_RESULTS;
    }
}
