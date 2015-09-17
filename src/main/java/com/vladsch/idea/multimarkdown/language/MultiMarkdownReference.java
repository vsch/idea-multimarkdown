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
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownWikiPageRefImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiMarkdownReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private String name;
    private Object[] myVariants = null;
    private ResolveResult[] myResolveResults = null;

    public MultiMarkdownReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        name = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
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
        if (myResolveResults == null) {
            Project project = myElement.getProject();
            final List<MultiMarkdownFile> wikiFiles = MultiMarkdownUtil.findWikiFiles(project,
                    myElement.getContainingFile() instanceof MultiMarkdownFile && ((MultiMarkdownFile) myElement.getContainingFile()).isWikiPage());
            List<ResolveResult> results = new ArrayList<ResolveResult>();
            for (MultiMarkdownFile wikiFile : wikiFiles) {
                if (wikiFile.isPageReference(name, myElement.getContainingFile().getVirtualFile())) {
                    results.add(new PsiElementResolveResult(wikiFile));
                }
            }
            myResolveResults = results.toArray(new ResolveResult[results.size()]);
        }
        return myResolveResults;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // we will handle this by renaming the element to point to the new location
        if (myElement instanceof MultiMarkdownNamedElement && element instanceof PsiFile) {
            VirtualFile file = ((PsiFile) element).getVirtualFile();
            String wikiPageRef = MultiMarkdownFile.getWikiLinkName(file, myElement.getContainingFile().getVirtualFile());
            // this will create a new reference and loose connection to this one
            return ((MultiMarkdownNamedElement) myElement).setName(wikiPageRef);
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
        //if (myVariants == null) {
        //    Project project = myElement.getProject();
        //    List<MultiMarkdownFile> wikiFiles = MultiMarkdownUtil.findWikiFiles(project,
        //            myElement.getContainingFile() instanceof MultiMarkdownFile && ((MultiMarkdownFile) myElement.getContainingFile()).isWikiPage());
        //    List<LookupElement> variants = new ArrayList<LookupElement>();
        //    for (final MultiMarkdownFile wikFile : wikiFiles) {
        //        if (wikFile.isPageReference(name, myElement.getContainingFile().getVirtualFile())) {
        //            variants.add(LookupElementBuilder.create(wikFile).
        //                            withIcon(MultiMarkdownIcons.FILE).
        //                            withTypeText(wikFile.getContainingFile().getName())
        //            );
        //        }
        //    }
        //    myVariants = variants.toArray();
        //}
        //return myVariants;
        return null;
    }

    public static boolean isReference(MultiMarkdownWikiPageRefImpl myElement) {
        Project project = myElement.getProject();
        final List<MultiMarkdownFile> wikiFiles = MultiMarkdownUtil.findWikiFiles(project,
                myElement.getContainingFile() instanceof MultiMarkdownFile && ((MultiMarkdownFile) myElement.getContainingFile()).isWikiPage());
        for (MultiMarkdownFile wikiFile : wikiFiles) {
            if (wikiFile.isPageReference(myElement.getName(), myElement.getContainingFile().getVirtualFile())) {
                return true;
            }
        }
        return false;
    }
}
