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
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownReferenceWikiPageRef;
import com.vladsch.idea.multimarkdown.spellchecking.SuggestionList;
import com.vladsch.idea.multimarkdown.util.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

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
                                SuggestionList suggestionList = ElementNameSuggestionProvider.getWikiPageTextSuggestions(parent);

                                for (String suggestion : suggestionList.asList()) {
                                    resultSet.addElement(LookupElementBuilder.create(suggestion)
                                            .withCaseSensitivity(true)
                                    );
                                }
                            }
                        } else if (elementType == IMAGE_LINK_REF_TEXT || elementType == LINK_REF_TEXT) {
                            PsiElement parent = element.getParent();
                            while (parent != null && !(parent instanceof MultiMarkdownExplicitLink)) {
                                parent = parent.getParent();
                            }

                            if (parent != null) {
                                SuggestionList suggestionList = ElementNameSuggestionProvider.getLinkRefTextSuggestions(parent, false);

                                for (String suggestion : suggestionList.asList()) {
                                    resultSet.addElement(LookupElementBuilder.create(suggestion)
                                            .withCaseSensitivity(true)
                                    );
                                }
                            }
                        } else if (elementType == IMAGE_LINK_REF) {
                            Document document = parameters.getEditor().getDocument();
                            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

                            if (virtualFile != null) {
                                Project fileProject = parameters.getEditor().getProject();
                                if (fileProject != null) {
                                    FileReferenceList linkFileReferenceList = new FileReferenceListQuery(fileProject)
                                            .gitHubWikiRules()
                                            .sameGitHubRepo()
                                            .wantImageFiles()
                                            .inSource(virtualFile, fileProject)
                                            .all();

                                    for (FileReference fileReference : linkFileReferenceList.get()) {
                                        addLinkRefCompletion(resultSet, (FileReferenceLink) fileReference, false, true);
                                    }

                                    for (FileReference fileReference : linkFileReferenceList.get()) {
                                        addLinkRefCompletion(resultSet, (FileReferenceLink) fileReference, false, false);
                                    }
                                }
                            }
                        } else if (elementType == LINK_REF) {
                            Document document = parameters.getEditor().getDocument();
                            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

                            if (virtualFile != null) {
                                Project fileProject = parameters.getEditor().getProject();
                                if (fileProject != null) {
                                    FileReferenceList linkFileReferenceList = new FileReferenceListQuery(fileProject)
                                            .gitHubWikiRules()
                                            .sameGitHubRepo()
                                            .inSource(virtualFile, fileProject)
                                            .all();

                                    for (FileReference fileReference : linkFileReferenceList.get()) {
                                        addLinkRefCompletion(resultSet, (FileReferenceLink) fileReference, true, true);
                                    }

                                    // add standard github parts
                                    FileReference sourceReference = new FileReference(virtualFile, fileProject);

                                    for (String gitHubLink : FilePathInfo.GITHUB_LINKS) {
                                        FileReferenceLink newRefLink = new FileReferenceLink(sourceReference.getFullFilePath(), sourceReference.getGitHubRepoPath() + FilePathInfo.GITHUB_WIKI_REL_OFFSET + gitHubLink, fileProject);

                                        LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(newRefLink.getLinkRef())
                                                //.withLookupString(wikiPageShortRef)
                                                .withCaseSensitivity(true)
                                                .withIcon(MultiMarkdownIcons.GITHUB)
                                                .withTypeText(MultiMarkdownBundle.message("annotation.link.github-" + gitHubLink), false);

                                        resultSet.addElement(lookupElementBuilder);
                                    }

                                    for (FileReference fileReference : linkFileReferenceList.get()) {
                                        addLinkRefCompletion(resultSet, (FileReferenceLink) fileReference, true, false);
                                    }
                                }
                            }
                        } else if (elementType == WIKI_LINK_REF) {
                            Document document = parameters.getEditor().getDocument();
                            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

                            if (virtualFile != null) {
                                Project fileProject = parameters.getEditor().getProject();
                                if (fileProject != null) {
                                    FileReferenceList wikiFileReferenceList = new FileReferenceListQuery(fileProject)
                                            .gitHubWikiRules()
                                            .inSource(virtualFile, fileProject)
                                            .spaceDashEqual()
                                            .allWikiPageRefs();

                                    for (FileReference fileReference : wikiFileReferenceList.get()) {
                                        addWikiPageRefCompletion(resultSet, (FileReferenceLink) fileReference, true);
                                    }

                                    for (FileReference fileReference : wikiFileReferenceList.get()) {
                                        addWikiPageRefCompletion(resultSet, (FileReferenceLink) fileReference, false);
                                    }
                                }
                            }
                        } else if (elementType == WIKI_LINK_REF_ANCHOR) {
                            MultiMarkdownWikiPageRef pageRef = (MultiMarkdownWikiPageRef) MultiMarkdownPsiImplUtil.findChildByType(element.getParent(), MultiMarkdownTypes.WIKI_LINK_REF);
                            MultiMarkdownReferenceWikiPageRef pageRefRef = pageRef == null ? null : (MultiMarkdownReferenceWikiPageRef) pageRef.getReference();
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

    protected void addWikiPageRefCompletion(@NotNull CompletionResultSet resultSet, FileReferenceLink fileReference, boolean accessible) {
        FileReferenceLinkGitHubRules fileReferenceGitHub = (FileReferenceLinkGitHubRules) fileReference;
        String wikiPageRef = fileReferenceGitHub.getWikiPageRef();
        boolean isWikiPageAccessible = fileReferenceGitHub.isWikiAccessible();

        if (accessible == isWikiPageAccessible) {
            if (isWikiPageAccessible || fileReferenceGitHub.getUpDirectories() == 0) {
                //String wikiPageShortRef = toFile.getWikiPageRef(null, WANT_WIKI_REF | ALLOW_INACCESSIBLE_WIKI_REF);
                String linkRefFileName = fileReferenceGitHub.getLinkRef();

                //logger.info("Adding " + wikiPageRef + " to completions");
                LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(wikiPageRef)
                        //.withLookupString(wikiPageShortRef)
                        .withCaseSensitivity(true)
                        .withIcon(accessible && fileReferenceGitHub.isWikiPage() ? MultiMarkdownIcons.WIKI : MultiMarkdownIcons.FILE)
                        .withTypeText(linkRefFileName, false);

                if (!isWikiPageAccessible) {
                    // TODO: get the color from color settings
                    lookupElementBuilder = lookupElementBuilder
                            .withItemTextForeground(Color.RED);
                }

                resultSet.addElement(lookupElementBuilder);
            }
        }
    }

    protected void addLinkRefCompletion(@NotNull CompletionResultSet resultSet, FileReferenceLink fileReference, boolean noExt, boolean accessible) {
        FileReferenceLinkGitHubRules fileReferenceGitHub = (FileReferenceLinkGitHubRules) fileReference;
        String linkRef = noExt ? fileReferenceGitHub.getLinkRefNoExt() : fileReferenceGitHub.getLinkRef();
        String gitHubRepoPath = fileReferenceGitHub.getSourceReference().getGitHubRepoPath("");
        boolean isLinkAccessible = fileReferenceGitHub.getPath().startsWith(gitHubRepoPath);

        if (accessible == isLinkAccessible) {
            if (isLinkAccessible || fileReferenceGitHub.getUpDirectories() == 0) {
                //String wikiPageShortRef = toFile.getWikiPageRef(null, WANT_WIKI_REF | ALLOW_INACCESSIBLE_WIKI_REF);
                String linkRefFileName = gitHubRepoPath.isEmpty() || !isLinkAccessible ? fileReferenceGitHub.getLinkRef() : fileReferenceGitHub.getFilePath().substring(gitHubRepoPath.length());

                //logger.info("Adding " + linkRef + " to completions");
                LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(linkRef)
                        //.withLookupString(wikiPageShortRef)
                        .withCaseSensitivity(true)
                        .withIcon(accessible && fileReferenceGitHub.isWikiPage() ? MultiMarkdownIcons.WIKI : MultiMarkdownIcons.FILE);

                if (!linkRef.equals(linkRefFileName)) {
                    lookupElementBuilder = lookupElementBuilder.withTypeText(linkRefFileName, false);
                }

                if (!isLinkAccessible) {
                    // TODO: get the color from color settings
                    lookupElementBuilder = lookupElementBuilder
                            .withItemTextForeground(Color.RED);
                }

                resultSet.addElement(lookupElementBuilder);
            }
        }
    }
}
