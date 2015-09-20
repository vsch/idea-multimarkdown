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

}
