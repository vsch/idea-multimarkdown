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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageTitle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MultiMarkdownUtil {
    public static List<MultiMarkdownNamedElement> findNamedElements(Project project, String name) {
        List<MultiMarkdownNamedElement> result = null;
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, MultiMarkdownFileType.INSTANCE, GlobalSearchScope.allScope(project));

        for (VirtualFile virtualFile : virtualFiles) {
            MultiMarkdownFile markdownFile = (MultiMarkdownFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (markdownFile != null) {
                MultiMarkdownWikiPageRef[] wikiPageRefs = PsiTreeUtil.getChildrenOfType(markdownFile, MultiMarkdownWikiPageRef.class);
                if (wikiPageRefs != null) {

                    for (MultiMarkdownWikiPageRef wikiPageRef : wikiPageRefs) {
                        if (name.equals(wikiPageRef.getName())) {
                            if (result == null) {
                                result = new ArrayList<MultiMarkdownNamedElement>();
                            }
                            result.add(wikiPageRef);
                        }
                    }

                    MultiMarkdownWikiPageTitle[] wikiPageTitles = PsiTreeUtil.getChildrenOfType(markdownFile, MultiMarkdownWikiPageTitle.class);
                    if (wikiPageTitles != null) {
                        for (MultiMarkdownWikiPageTitle wikiPageTitle : wikiPageTitles) {
                            if (name.equals(wikiPageTitle.getName())) {
                                if (result == null) {
                                    result = new ArrayList<MultiMarkdownNamedElement>();
                                }
                                result.add(wikiPageTitle);
                            }
                        }
                    }
                }
            }
        }
        return result != null ? result : Collections.<MultiMarkdownNamedElement>emptyList();
    }

    public static List<MultiMarkdownNamedElement> findNamedElements(Project project) {
        List<MultiMarkdownNamedElement> result = null;
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, MultiMarkdownFileType.INSTANCE, GlobalSearchScope.allScope(project));

        for (VirtualFile virtualFile : virtualFiles) {
            MultiMarkdownFile markdownFile = (MultiMarkdownFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (markdownFile != null) {

                MultiMarkdownWikiPageRef[] wikiPageRefs = PsiTreeUtil.getChildrenOfType(markdownFile, MultiMarkdownWikiPageRef.class);
                if (wikiPageRefs != null) {
                    for (MultiMarkdownWikiPageRef wikiPageRef : wikiPageRefs) {
                        if (result == null) {
                            result = new ArrayList<MultiMarkdownNamedElement>();
                        }
                        result.add(wikiPageRef);
                    }
                }

                MultiMarkdownWikiPageTitle[] wikiPageTitles = PsiTreeUtil.getChildrenOfType(markdownFile, MultiMarkdownWikiPageTitle.class);
                if (wikiPageTitles != null) {
                    for (MultiMarkdownWikiPageTitle wikiPageTitle : wikiPageTitles) {
                        if (result == null) {
                            result = new ArrayList<MultiMarkdownNamedElement>();
                        }
                        result.add(wikiPageTitle);
                    }
                }
            }
        }
        return result != null ? result : Collections.<MultiMarkdownNamedElement>emptyList();
    }
}
