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

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        //registrar.registerReferenceProvider(PlatformPatterns.psiElement(MultiMarkdownFile.class),
        //        new PsiReferenceProvider() {
        //            @NotNull
        //            @Override
        //            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        //                // TODO: this is useless for references, we have references to files not wiki page references
        //                MultiMarkdownFile fileNode = (MultiMarkdownFile) element;
        //                if (fileNode.isWikiPage()) {
        //                    return new PsiReference[]{new MultiMarkdownReference(element, fileNode.getWikiPageRef())};
        //                }
        //                return new PsiReference[0];
        //            }
        //        });
        //
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(MultiMarkdownWikiPageRef.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (element instanceof MultiMarkdownWikiPageRef && ((MultiMarkdownWikiPageRef) element).getName() != null) {
                            return new PsiReference[]{new MultiMarkdownReference(element, element.getTextRange())};
                        } else {
                            return new PsiReference[0];
                        }
                    }
                });
    }
}
