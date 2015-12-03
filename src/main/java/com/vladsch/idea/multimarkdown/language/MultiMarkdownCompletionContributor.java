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

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownReferenceWikiLinkRef;
import com.vladsch.idea.multimarkdown.spellchecking.SuggestionList;
import com.vladsch.idea.multimarkdown.util.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.*;

public class MultiMarkdownCompletionContributor extends CompletionContributor {
    private static final Logger logger = Logger.getLogger(MultiMarkdownCompletionContributor.class);
    public static final String DUMMY_IDENTIFIER = "\u001F";

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        context.setDummyIdentifier(DUMMY_IDENTIFIER);
    }

    public MultiMarkdownCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiElement.class).withLanguage(MultiMarkdownLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
                        PsiElement elementPos = parameters.getPosition();
                        int offset = parameters.getOffset();
                        //logger.info("Completion for " + element + " at pos " + String.valueOf(offset));

                        PsiElement element = elementPos;
                        while (element instanceof LeafPsiElement) {
                            element = element.getParent();
                        }

                        IElementType elementType = element.getNode().getElementType();

                        if (elementType == WIKI_LINK_TEXT) {
                            PsiElement parent = element.getParent();
                            while (parent != null && !(parent instanceof MultiMarkdownWikiLink) && !(parent instanceof MultiMarkdownFile)) {
                                parent = parent.getParent();
                            }

                            if (parent != null && parent instanceof MultiMarkdownWikiLink) {
                                SuggestionList suggestionList = ElementNameSuggestionProvider.getLinkTextSuggestions(parent, false);

                                for (String suggestion : suggestionList.asList()) {
                                    resultSet.addElement(LookupElementBuilder.create(suggestion)
                                            .withCaseSensitivity(true)
                                    );
                                }
                            }
                        } else if (elementType == IMAGE_LINK_REF_TEXT || elementType == LINK_REF_TEXT) {
                            PsiElement parent = element.getParent();

                            while (parent != null && !(parent.getNode().getElementType() instanceof MultiMarkdownElementType)) {
                                parent = parent.getParent();
                            }

                            if (parent != null) {
                                // skip suggestion for complex elements
                                PsiElement[] children = element.getChildren();
                                for (PsiElement child : children) {
                                    if (child.getNode().getElementType() instanceof MultiMarkdownElementType) return;
                                }

                                SuggestionList suggestionList = ElementNameSuggestionProvider.getLinkTextSuggestions(parent, false);

                                for (String suggestion : suggestionList.asList()) {
                                    resultSet.addElement(LookupElementBuilder.create(suggestion)
                                            .withCaseSensitivity(true)
                                    );
                                }
                            }
                        } else if (elementType == IMAGE_LINK_REF || elementType == LINK_REF || elementType == WIKI_LINK_REF) {
                            Document document = parameters.getEditor().getDocument();
                            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

                            if (virtualFile != null) {
                                Project fileProject = parameters.getEditor().getProject();
                                if (fileProject != null) {
                                    ProjectFileRef containingFile = new ProjectFileRef(virtualFile, fileProject);
                                    GitHubLinkResolver resolver = new GitHubLinkResolver(containingFile);
                                    String linkRefText = MultiMarkdownPsiImplUtil.getLinkRefText(element);
                                    int uriLinks = LinkResolver.ANY;
                                    LinkRef linkRef;
                                    String fullPath = "";

                                    if (MultiMarkdownPlugin.isLicensed()) {
                                        PathInfo linkRefTextInfo = LinkRef.parseLinkRef(containingFile, linkRefText.replace(DUMMY_IDENTIFIER, ""), null);

                                        if (linkRefTextInfo.getHasExt() && !linkRefTextInfo.isWikiPageExt()) fullPath = StringUtilKt.prefixWith(linkRefTextInfo.getExt(), '.');
                                        else if (linkRefTextInfo.getFileName().startsWith(".") && !linkRefTextInfo.getHasExt()) fullPath = StringUtilKt.prefixWith(linkRefTextInfo.getFileName(), '.');

                                        if (linkRefTextInfo.isURI() && elementType != WIKI_LINK_REF) uriLinks = linkRefTextInfo.isLocal() ? LinkResolver.ONLY_LOCAL_URI : LinkResolver.ONLY_REMOTE_URI;
                                    }

                                    if (elementType == WIKI_LINK_REF) {
                                        linkRef = new WikiLinkRef(containingFile, fullPath, null, null);
                                    } else {
                                        if (elementType == IMAGE_LINK_REF) {
                                            linkRef = new ImageLinkRef(containingFile, fullPath, null, null);
                                        } else {
                                            linkRef = new LinkRef(containingFile, fullPath, null, null);
                                        }
                                    }

                                    String gitHubRepoPath = resolver.getProjectResolver().vcsRepoBasePath(linkRef.getContainingFile());
                                    if (gitHubRepoPath == null) gitHubRepoPath = resolver.getProjectBasePath();

                                    List<PathInfo> matchedFiles = resolver.multiResolve(linkRef, uriLinks | LinkResolver.LOOSE_MATCH, null);

                                    for (PathInfo pathInfo : matchedFiles) {
                                        String linkRefAddress = uriLinks != LinkResolver.ANY ? pathInfo.getFilePath() : resolver.linkAddress(linkRef, pathInfo, !pathInfo.isWikiPageExt(), null, null);

                                        if (!linkRefAddress.equals("#") && !linkRefAddress.isEmpty()) {
                                            String linkRefFileName = linkRefAddress;
                                            Icon icon;
                                            FileRef fileRef;

                                            if (pathInfo instanceof FileRef) {
                                                fileRef = (FileRef) pathInfo;
                                            } else {
                                                assert pathInfo instanceof LinkRef && pathInfo.isURI();
                                                fileRef = ((LinkRef) pathInfo).getTargetRef();
                                            }

                                            if (fileRef != null) {
                                                linkRefFileName = PathInfo.relativePath(gitHubRepoPath, fileRef.getFilePath(), false);

                                                if (fileRef.isWikiPage()) icon = MultiMarkdownIcons.WIKI;
                                                else if (fileRef.isMarkdownExt()) icon = MultiMarkdownIcons.FILE;
                                                else {
                                                    Project project = resolver.getProject();
                                                    PsiFile psiFile = project == null ? null : fileRef.psiFile(project);
                                                    icon = psiFile == null ? null : psiFile.getIcon(0);
                                                }
                                            } else {
                                                // must be a github link
                                                icon = MultiMarkdownIcons.GITHUB;
                                            }

                                            LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(linkRefAddress).withCaseSensitivity(true);
                                            if (icon != null) lookupElementBuilder = lookupElementBuilder.withIcon(icon);
                                            if (!linkRefAddress.equals(linkRefFileName)) lookupElementBuilder = lookupElementBuilder.withTypeText(linkRefFileName, false);
                                            if (linkRef instanceof WikiLinkRef && linkRefAddress.contains("/")) {
                                                // TODO: get the color from color settings
                                                lookupElementBuilder = lookupElementBuilder.withItemTextForeground(JBColor.RED);
                                            }

                                            resultSet.addElement(lookupElementBuilder);
                                        }
                                    }
                                }
                            }
                        } else if (elementType == WIKI_LINK_REF_ANCHOR) {
                            MultiMarkdownWikiLinkRef pageRef = (MultiMarkdownWikiLinkRef) MultiMarkdownPsiImplUtil.findChildByType(element.getParent(), MultiMarkdownTypes.WIKI_LINK_REF);
                            MultiMarkdownReferenceWikiLinkRef pageRefRef = pageRef == null ? null : (MultiMarkdownReferenceWikiLinkRef) pageRef.getReference();
                            if (pageRefRef != null && !pageRefRef.isResolveRefMissing()) {
                                MultiMarkdownFile markdownFile = (MultiMarkdownFile) pageRefRef.resolve();
                                if (markdownFile != null) {
                                    // TODO: 2015-10-25 add list of anchors when anchor parsing is implemented
                                }
                            }
                        }
                    }
                }
        );
    }
}
