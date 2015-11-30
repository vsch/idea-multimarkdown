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

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.NullableFunction;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownLinkRef;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLinkRef;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownReference;
import com.vladsch.idea.multimarkdown.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class MultiMarkdownLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull final PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof MultiMarkdownWikiLinkRef || element instanceof MultiMarkdownLinkRef) {
            PsiReference psiReference = element.getReference();
            //MultiMarkdownFile[] markdownFiles = MultiMarkdownPlugin.getProjectComponent(element.getProject()).getFileReferenceList().query()
            //        .matchWikiRef((MultiMarkdownWikiLinkRef) element)
            //        .accessibleWikiPageFiles()
            //        ;

            // incompleteCode in our case means looseMatch criteria, otherwise precise match as per repo rules
            final ResolveResult[] results = psiReference != null ? ((MultiMarkdownReference) psiReference).multiResolve(false) : null;
            if (results != null && results.length > 0) {
                final PsiFile containingFile = element.getContainingFile();

                NullableFunction<PsiElement, String> namer = new NullableFunction<PsiElement, String>() {
                    @Nullable
                    @Override
                    public String fun(PsiElement element) {
                        String linkAddress = new GitHubLinkResolver(containingFile).linkAddress(new FileRef((PsiFile) element), null, null, null);
                        return linkAddress;
                    }
                };

                final Project project = element.getProject();
                final String basePath = project.getBasePath() == null ? "/" : StringUtilKt.suffixWith(project.getBasePath(), '/');
                boolean showWikiHome = false;
                String lastWikiHome = null;

                if (results.length > 0) {
                    final ArrayList<PsiFile> markdownTargets = new ArrayList<PsiFile>();
                    Icon icon = null;
                    for (ResolveResult resolveResult : results) {
                        if (resolveResult.getElement() instanceof PsiFile && resolveResult.getElement() != containingFile) {
                            PsiFile file = (PsiFile) resolveResult.getElement();

                            if (icon == null) {
                                // TODO: use standard icon that IDE uses
                                icon = results.length > 1 ? MultiMarkdownIcons.MULTI_WIKI : file.getIcon(0);
                                //icon = MultiMarkdownIcons.GITHUB;
                            }

                            FileRef fileRef = new FileRef(file.getVirtualFile());
                            if (lastWikiHome == null) {
                                lastWikiHome = fileRef.getWikiDir();
                            } else if (!showWikiHome && !lastWikiHome.equals(fileRef.getWikiDir())) {
                                showWikiHome = true;
                            }
                            markdownTargets.add(file);
                        }
                    }

                    if (markdownTargets.size() > 0) {
                        final boolean showContainer = true || showWikiHome;

                        PsiElementListCellRenderer cellRenderer = new PsiElementListCellRenderer<PsiElement>() {

                            @Override
                            public String getElementText(PsiElement fileElement) {
                                if (fileElement instanceof PsiFile) {
                                    FileRef fileRef = new FileRef((PsiFile) fileElement);
                                    if (fileRef.isUnderWikiDir() && results.length > 1) {
                                        // need subdirectory and extension, there is more than one match
                                        String repoDir = StringUtilKt.suffixWith(fileRef.getWikiDir(), '/');
                                        String relativePath = PathInfo.relativePath(repoDir, fileRef.getFilePath(), false);
                                        return relativePath;
                                    } else {
                                        String linkAddress = new GitHubLinkResolver(containingFile).linkAddress(fileRef, null, null, null);
                                        return linkAddress;
                                    }
                                }

                                return "<unknown>";
                            }

                            protected Icon getIcon(PsiElement element) {
                                boolean firstItem = element == markdownTargets.get(0);
                                boolean isWikiPage = element instanceof MultiMarkdownFile && ((MultiMarkdownFile) element).isWikiPage();
                                return firstItem ? MultiMarkdownIcons.MULTI_WIKI : (element instanceof MultiMarkdownFile ? (isWikiPage ? MultiMarkdownIcons.HIDDEN_WIKI : MultiMarkdownIcons.HIDDEN_FILE) : element.getIcon(0));
                            }

                            @Nullable
                            @Override
                            protected String getContainerText(PsiElement element, String name) {
                                if (showContainer && element instanceof PsiFile) {
                                    FileRef fileRef = new FileRef((PsiFile) element);
                                    String repoDir;

                                    if (fileRef.isWikiPage()) {
                                        repoDir = fileRef.getWikiDir();
                                    } else {
                                        ProjectFileRef projectFileRef = fileRef.projectFileRef(project);
                                        String gitHubRepoPath = projectFileRef == null ? null : projectFileRef.getGitHubBasePath();
                                        repoDir = gitHubRepoPath == null ? basePath : gitHubRepoPath;
                                    }
                                    String relativePath = PathInfo.relativePath(basePath, repoDir, false);
                                    return relativePath;
                                }
                                return null;
                            }

                            @Override
                            protected int getIconFlags() {
                                return Iconable.ICON_FLAG_READ_STATUS;
                            }
                        };

                        if (icon == null) icon = MultiMarkdownIcons.FILE;

                        NavigationGutterIconBuilder<PsiElement> builder =
                                NavigationGutterIconBuilder.create(icon)
                                        .setCellRenderer(cellRenderer)
                                        .setTargets(markdownTargets)
                                        .setNamer(namer)
                                        .setTooltipText(MultiMarkdownBundle.message("linemarker.navigate-to"));

                        result.add(builder.createLineMarkerInfo(element));
                    }
                }
            }
        }
    }
}
