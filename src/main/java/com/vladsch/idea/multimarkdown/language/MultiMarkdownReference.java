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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiMarkdownReference extends PsiReferenceBase<MultiMarkdownNamedElement> implements PsiPolyVariantReference, BindablePsiReference {
    private static final Logger logger = Logger.getLogger(MultiMarkdownReference.class);
    public static final ResolveResult[] EMPTY_RESULTS = new ResolveResult[0];
    protected final MultiMarkdownFile.ReferenceChangeListener fileListListener;
    protected ResolveResult[] resolveResults;
    protected String resolveResultsName;
    protected boolean nameElementChanging = false;
    //private ResolveResult[] incompleteCodeResolveResults;

    @Override
    public String toString() {
        //PsiElement resolve = resolve();
        return "Reference for " + myElement.toString();
    }

    public MultiMarkdownReference(@NotNull MultiMarkdownNamedElement element, @NotNull TextRange textRange) {
        super(element, textRange);

        fileListListener = new MultiMarkdownFile.ReferenceChangeListener() {
            //@Override
            //public void namedElementChanged(@NotNull MultiMarkdownNamedElement newElement, @NotNull MultiMarkdownNamedElement oldElement) {
            //    boolean nameChanging = false;
            //
            //    synchronized (this) {
            //        nameChanging = nameElementChanging;
            //        if (!nameChanging) {
            //            nameElementChanging = true;
            //        }
            //    }
            //
            //    if (!nameChanging) {
            //        // only if our element is not the one being renamed
            //        if (myElement != oldElement && myElement != newElement) {
            //            logger.info("referenced element name changed " + " for " + myElement);
            //            bindToElement(newElement);
            //        }
            //
            //        synchronized (this) {
            //            nameElementChanging = false;
            //        }
            //    }
            //}

            @Override
            public void projectListsUpdated() {
                invalidateResolveResults();
            }
        };
    }

    public void invalidateResolveResults() {
        // invalidate multiresolve references
        //incompleteCodeResolveResults = null;
        //logger.info("invalidateResolveResults on " + this.toString());
        resolveResults = null;
        resolveResultsName = null;
        // logger.info("Invalidating resolve results" + " for " + myElement);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        refreshName();
        if (resolveResults == null) {
            resolveResultsName = getElement().getName();
            if (resolveResultsName == null) resolveResultsName = "";
            setRangeInElement(new TextRange(0, resolveResultsName.length()));

            resolveResults = getMultiResolveResults(incompleteCode);
        }
        return resolveResults;
    }

    public void refreshName() {
        // check if our resolveResult is stale because the node has been edited and its name has changed
        if (resolveResults != null && (resolveResultsName == null || !resolveResultsName.equals(getElement().getName()))) {
            // logger.info("refreshing name " + myElement);
            invalidateResolveResults();
        }
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // we will handle this by renaming the element to point to the new location
        if (element.getClass() == myElement.getClass()) {
            String name = ((MultiMarkdownNamedElement) element).getName();
            // this will create a new reference and loose connection to this one
            // logger.info("rebinding " + myElement + " to " + element);
            if (name != null) return myElement.setName(name, MultiMarkdownNamedElement.REASON_FILE_MOVED);
        }
        throw new IncorrectOperationException("Rebind cannot be performed for " + getClass());
    }

    //public void notifyNamedElementChange(MultiMarkdownNamedElement oldElement, MultiMarkdownNamedElement newElement) {
    //    String name = newElement.getName();
    //    if (name != null) {
    //        MultiMarkdownFile containingFile;
    //        if (!(oldElement.getContainingFile() instanceof MultiMarkdownFile)) {
    //            if (!(newElement.getContainingFile() instanceof MultiMarkdownFile)) {
    //                int tmp = 0;
    //                containingFile = null;
    //            } else {
    //                containingFile = (MultiMarkdownFile) newElement.getContainingFile();
    //            }
    //        } else {
    //            containingFile = (MultiMarkdownFile) oldElement.getContainingFile();
    //        }
    //
    //        if (containingFile != null) {
    //            containingFile.setMissingLinkElement(oldElement, newElement, newElement.getMissingElementNamespace() + name);
    //        }
    //    }
    //}

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

    @NotNull
    public abstract ResolveResult[] getMultiResolveResults(boolean incompleteCode);
}
