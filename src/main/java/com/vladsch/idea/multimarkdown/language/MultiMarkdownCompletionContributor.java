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
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownReferenceWikiLinkRef;
import com.vladsch.idea.multimarkdown.spellchecking.SuggestionList;
import com.vladsch.idea.multimarkdown.util.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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
                                    LinkRef linkRef;

                                    if (elementType == IMAGE_LINK_REF) {
                                        linkRef = new ImageLinkRef(containingFile, "", null, null);
                                    } else if (elementType == WIKI_LINK_REF) {
                                        linkRef = new WikiLinkRef(containingFile, "", null, null);
                                    } else {
                                        linkRef = new LinkRef(containingFile, "", null, null);
                                    }

                                    GitHubLinkResolver resolver = new GitHubLinkResolver(containingFile);
                                    List<PathInfo> matchedFiles = resolver.multiResolve(linkRef, LinkResolver.LOOSE_MATCH, null);

                                    for (PathInfo fileRef : matchedFiles) {
                                        if (fileRef instanceof ProjectFileRef) {
                                            addLinkRefCompletion(resultSet, resolver, linkRef, (ProjectFileRef) fileRef, !((ProjectFileRef) fileRef).isWikiPage() || elementType == IMAGE_LINK_REF, true);
                                        }
                                    }

                                    // add standard GitHub parts
                                    if (elementType != IMAGE_LINK_REF && elementType != WIKI_LINK_REF && !containingFile.isWikiPage()) {
                                        for (String gitHubLink : GitHubLinkResolver.GITHUB_LINKS) {
                                            FileRef gitHubFileRef = new FileRef(containingFile.getGitHubRepoPath() + gitHubLink);
                                            String gitHubLinkRef = resolver.linkAddress(linkRef, gitHubFileRef, null, null, null);

                                            LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(gitHubFileRef)
                                                    //.withLookupString(wikiPageShortRef)
                                                    .withCaseSensitivity(true)
                                                    .withIcon(MultiMarkdownIcons.GITHUB)
                                                    .withTypeText(MultiMarkdownBundle.message("annotation.link.github-" + gitHubLink), false);

                                            resultSet.addElement(lookupElementBuilder);
                                        }
                                    }

                                    for (PathInfo fileRef : matchedFiles) {
                                        if (fileRef instanceof ProjectFileRef) {
                                            addLinkRefCompletion(resultSet, resolver, linkRef, (ProjectFileRef) fileRef, !((ProjectFileRef) fileRef).isWikiPage() || elementType == IMAGE_LINK_REF, true);
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

    protected void addLinkRefCompletion(@NotNull CompletionResultSet resultSet, GitHubLinkResolver resolver, LinkRef linkRef, ProjectFileRef projectFileRef, boolean withExtForWikiPage, boolean accessible) {
        String linkRefText = resolver.linkAddress(linkRef, projectFileRef, withExtForWikiPage, null, null);
        String gitHubRepoPath = resolver.getProjectResolver().vcsProjectBasePath(linkRef.getContainingFile());

        if (gitHubRepoPath == null) gitHubRepoPath = resolver.getProjectBasePath();
        boolean isLinkAccessible = projectFileRef.getPath().startsWith(gitHubRepoPath);

        if (accessible == isLinkAccessible) {
            String linkRefFileName = PathInfo.relativePath(gitHubRepoPath, linkRefText, false);

            if (isLinkAccessible || !linkRefFileName.startsWith("../")) {

                LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(linkRefText)
                        .withCaseSensitivity(true)
                        .withIcon(accessible && projectFileRef.isWikiPage() ? MultiMarkdownIcons.WIKI : MultiMarkdownIcons.FILE);

                if (!linkRefText.equals(linkRefFileName)) {
                    lookupElementBuilder = lookupElementBuilder.withTypeText(linkRefFileName, false);
                }

                if (!isLinkAccessible) {
                    // TODO: get the color from color settings
                    lookupElementBuilder = lookupElementBuilder.withItemTextForeground(Color.RED);
                }

                resultSet.addElement(lookupElementBuilder);
            }
        }
    }
}
