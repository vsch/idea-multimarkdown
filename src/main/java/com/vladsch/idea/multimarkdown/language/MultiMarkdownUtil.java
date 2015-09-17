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
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLink;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MultiMarkdownUtil {
    public static List<MultiMarkdownWikiPageRef> findWikiPageRefs(Project project, String name) {
        List<MultiMarkdownWikiPageRef> result = null;
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, MultiMarkdownFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            MultiMarkdownFile simpleFile = (MultiMarkdownFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                MultiMarkdownWikiPageRef[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, MultiMarkdownWikiPageRef.class);
                if (properties != null) {
                    for (MultiMarkdownWikiPageRef property : properties) {
                        if (name.equals(property.getName())) {
                            if (result == null) {
                                result = new ArrayList<MultiMarkdownWikiPageRef>();
                            }
                            result.add(property);
                        }
                    }
                }
            }
        }
        return result != null ? result : Collections.<MultiMarkdownWikiPageRef>emptyList();
    }

    public static List<MultiMarkdownWikiPageRef> findWikiPageRefs(Project project) {
        List<MultiMarkdownWikiPageRef> result = null;
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, MultiMarkdownFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            MultiMarkdownFile simpleFile = (MultiMarkdownFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                MultiMarkdownWikiPageRef[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, MultiMarkdownWikiPageRef.class);
                if (properties != null) {
                    for (MultiMarkdownWikiPageRef property : properties) {
                        if (result == null) {
                            result = new ArrayList<MultiMarkdownWikiPageRef>();
                        }
                        result.add(property);
                    }
                }
            }
        }
        return result != null ? result : Collections.<MultiMarkdownWikiPageRef>emptyList();
    }

    public static List<MultiMarkdownFile> findWikiFiles(Project project, String name, VirtualFile inFile) {
        List<MultiMarkdownFile> markdownFiles = findWikiFiles(project, false);
        List<MultiMarkdownFile> result = new ArrayList<MultiMarkdownFile>(5);
        for (MultiMarkdownFile markdownFile : markdownFiles) {
            if (markdownFile != null) {
                VirtualFile afile = markdownFile.getVirtualFile();
                if (markdownFile.isPageReference(name, inFile)) {
                    result.add(markdownFile);
                }
            }
        }
        return result;
    }

    public static List<MultiMarkdownFile> findWikiFiles(Project project, String name) {
        return findWikiFiles(project, name, null);
    }

    protected static void addMarkdownFiles(Project project, VirtualFile virtualFile, List<MultiMarkdownFile> result, boolean wikiPagesOnly) {
        PsiFile aFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (aFile != null) {
            if (aFile instanceof MultiMarkdownFile) {
                MultiMarkdownFile markdownFile = (MultiMarkdownFile) aFile;
                if (!wikiPagesOnly || markdownFile.isWikiPage()) {
                    result.add(markdownFile);
                }
            }
        } else {
            VirtualFile[] virtualFiles = virtualFile.getChildren();
            if (virtualFiles != null) {
                for (VirtualFile file : virtualFiles) {
                    addMarkdownFiles(project, file, result, wikiPagesOnly);
                }
            }
        }
    }

    public static List<MultiMarkdownFile> findWikiFiles(Project project, boolean wikiPagesOnly) {
        List<MultiMarkdownFile> result = new ArrayList<MultiMarkdownFile>();
        VirtualFile baseDir = project.getBaseDir();
        //VirtualFile[] virtualFiles = baseDir != null ? baseDir.getChildren() : new VirtualFile[0];
        addMarkdownFiles(project, baseDir, result, wikiPagesOnly);
        return result;
    }
}
