/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.language;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.util.ProjectFileListListener;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent.*;

public class MultiMarkdownReference extends PsiReferenceBase<MultiMarkdownNamedElement> implements PsiPolyVariantReference {
    private static final Logger logger = Logger.getLogger(MultiMarkdownReference.class);
    private ProjectFileListListener fileListListener;
    private ResolveResult[] resolveResults;
    private String resolveResultsName;
    //private ResolveResult[] incompleteCodeResolveResults;

    @Override
    public String toString() {
        //PsiElement resolve = resolve();
        return "Reference for " + myElement.toString();
    }

    public MultiMarkdownReference(@NotNull MultiMarkdownNamedElement element) {
        super(element);

        if (element instanceof MultiMarkdownWikiPageRef) {
            MultiMarkdownPlugin.getProjectComponent(element.getProject()).addListener(fileListListener = new ProjectFileListListener() {
                @Override
                public void projectListsUpdated() {
                    invalidateResolveResults();
                }
            });
        }
    }

    public void invalidateResolveResults() {
        // invalidate multiresolve references
        //incompleteCodeResolveResults = null;
        //logger.info("invalidateResolveResults on " + this.toString());
        resolveResults = null;
        resolveResultsName = null;
    }

    //@Override
    //public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    //    return getManipulator().handleContentChange(myElement, getRangeInElement(), newElementName);
    //}
    //
    //@Override
    //public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    //    throw new IncorrectOperationException("Rebind cannot be performed for " + getClass());
    //}

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        refreshName();
        if (resolveResults == null) {
            resolveResultsName = getElement().getName();
            if (resolveResultsName == null) resolveResultsName = "";
            setRangeInElement(new TextRange(0, resolveResultsName.length()));

            resolveResults = getMultiResolveResults(myElement, false);

            ((MultiMarkdownFile) myElement.getContainingFile()).removeListener(fileListListener);

            // register for changes in missingLinkElement list if we have one not a file
            for (ResolveResult result : resolveResults) {
                if (result.getElement() instanceof MultiMarkdownFile) {
                    continue;
                }

                // have a missing link reference, register for missing link changes
                ((MultiMarkdownFile) myElement.getContainingFile()).addListener(fileListListener);
                break;
            }

            //logger.info((isMissingReferece ? "Missing Reference" : "Reference") + " for " + myElement.toString() + ": " + resolveResults[0].getElement().toString());
        }
        return resolveResults;
    }


    public void refreshName() {
        // check if our resolveResult is stale because the node has been edited and its name has changed
        if (resolveResults != null && (resolveResultsName == null || !resolveResultsName.equals(getElement().getName()))) {
            resolveResults = null;
            resolveResultsName = null;
        }
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // we will handle this by renaming the element to point to the new location
        if (myElement instanceof MultiMarkdownWikiPageRef && element instanceof PsiFile) {
            VirtualFile file = ((PsiFile) element).getVirtualFile();
            String wikiPageRef = getWikiPageRef(file, myElement.getContainingFile().getVirtualFile(), WANT_WIKI_REF | ALLOW_INACCESSIBLE_WIKI_REF);
            // this will create a new reference and loose connection to this one
            return ((MultiMarkdownWikiPageRef) myElement).setName(wikiPageRef, true);
        }
        throw new IncorrectOperationException("Rebind cannot be performed for " + getClass());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<LookupElement>();
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
        return variants.toArray();
    }

    public static
    @NotNull
    ResolveResult[] getMultiResolveResults(MultiMarkdownNamedElement myElement, boolean incompleteCode) {
        Project project = myElement.getProject();
        MultiMarkdownFile containingFile = (MultiMarkdownFile) myElement.getContainingFile();
        int searchSettings = 0;
        String linkRef = null;

        if (myElement instanceof MultiMarkdownWikiPageRef) {
            searchSettings = WANT_WIKI_REF | WIKI_REF;
            searchSettings |= containingFile.isWikiPage() ? WIKIPAGE_FILE : MARKDOWN_FILE;

            linkRef = ((MultiMarkdownWikiPageRef) myElement).getName();
        }
        //} else if (myElement instanceof MultiMarkdownWikiPageRef) {
        //    searchSettings = MultiMarkdownProjectComponent.WANT_WIKI_REF | MultiMarkdownProjectComponent.WIKIPAGE_FILE;

        if (linkRef != null) {
            List<ResolveResult> results = new ArrayList<ResolveResult>();
            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
            if ((searchSettings & (MARKDOWN_FILE_ONLY | WIKIPAGE_FILE)) != 0) {
                final List<MultiMarkdownFile> files = projectComponent.findRefLinkMarkdownFiles(linkRef, containingFile, searchSettings);

                if (files != null) {
                    for (MultiMarkdownFile file : files) {
                        if (isLinkRefToFile(linkRef, file.getVirtualFile(), containingFile.getVirtualFile(), searchSettings)) {
                            results.add(new PsiElementResolveResult(file));
                        }
                    }
                }
            }
            else {
                final List<VirtualFile> files = projectComponent.findRefLinkFiles(linkRef, containingFile.getVirtualFile(), searchSettings);

                if (files != null) {
                    for (VirtualFile file : files) {
                        if (isLinkRefToFile(linkRef, file, containingFile.getVirtualFile(), searchSettings)) {
                            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                            if (psiFile != null) {
                                results.add(new PsiElementResolveResult(psiFile));
                            }
                        }
                    }
                }
            }

            if (results.size() == 0) {
                // this one is missing, see if there is a missing ref registered already
                results.add(new PsiElementResolveResult(((MultiMarkdownFile) myElement.getContainingFile()).getMissingLinkElement(myElement, linkRef)));
            }
            return results.toArray(new ResolveResult[results.size()]);
        }
        return new ResolveResult[0];
    }

}
