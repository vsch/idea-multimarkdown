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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.spellchecker.SpellCheckerManager;
import com.intellij.spellchecker.util.Strings;
import com.intellij.util.ProcessingContext;
import com.vladsch.idea.multimarkdown.MultiMarkdownIcons;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import com.vladsch.idea.multimarkdown.util.FileReference;
import com.vladsch.idea.multimarkdown.util.FileReferenceLink;
import com.vladsch.idea.multimarkdown.util.FileReferenceList;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.WIKI_LINK_REF;
import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.WIKI_LINK_TITLE;

public class MultiMarkdownCompletionContributor extends CompletionContributor {
    private static final Logger logger = Logger.getLogger(MultiMarkdownCompletionContributor.class);

    public MultiMarkdownCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiElement.class).withLanguage(MultiMarkdownLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
                        PsiElement element = parameters.getPosition();
                        int offset = parameters.getOffset();
                        //logger.info("Completion for " + element + " at pos " + String.valueOf(offset));

                        IElementType elementType = element.getNode().getElementType();

                        if (elementType == WIKI_LINK_TITLE) {
                            PsiElement parent = element.getParent();
                            while (parent != null && !(parent instanceof MultiMarkdownWikiLink) && !(parent instanceof MultiMarkdownFile)) {
                                parent = parent.getParent();
                            }

                            if (parent instanceof MultiMarkdownWikiLink) {
                                MultiMarkdownWikiPageRef wikiPageRef = (MultiMarkdownWikiPageRef) MultiMarkdownPsiImplUtil.findChildByType(parent, MultiMarkdownTypes.WIKI_LINK_REF);
                                MultiMarkdownWikiPageTitle wikiPageTitle = (MultiMarkdownWikiPageTitle) MultiMarkdownPsiImplUtil.findChildByType(parent, MultiMarkdownTypes.WIKI_LINK_TITLE);
                                ArrayList<String> suggestedNames = new ArrayList<String>();
                                ArrayList<String> suggestions = new ArrayList<String>();

                                if (wikiPageTitle != null) {
                                    String text = wikiPageTitle.getName();
                                    if (text != null) {
                                        text = text.replace("IntellijIdeaRulezzz ", "").trim();
                                        if (!text.isEmpty()) {
                                            suggestions.add(text);
                                            suggestedNames.add(text);
                                        }
                                    }
                                }

                                if (wikiPageRef != null) {
                                    String text = wikiPageRef.getName();
                                    if (text != null) {
                                        FilePathInfo pathInfo = new FilePathInfo(text);
                                        suggestions.add(text = pathInfo.getFileName());
                                        suggestedNames.add(text);

                                        // add with path parts, to 2 directories above
                                        suggestions.add(text = (pathInfo = new FilePathInfo(pathInfo.getPath())).getFilePath() + text);
                                        suggestions.add(text = (pathInfo = new FilePathInfo(pathInfo.getPath())).getFilePath() + text);
                                    }
                                }

                                if (suggestions.size() > 0) {
                                    SpellCheckerManager manager = SpellCheckerManager.getInstance(element.getProject());

                                    for (String suggestionText : suggestions) {
                                        String[] words = NameUtil.nameToWords(suggestionText);
                                        String capedSuggestion = "";
                                        boolean mixedCase = false;
                                        boolean needSpellingSuggestions = false;
                                        boolean prevWasAlphaNum = false;

                                        for (String word : words) {
                                            boolean isAlphaNum = isAlphaNum(word);

                                            if (Strings.isMixedCase(word)) mixedCase = true;
                                            if (manager.hasProblem(word)) needSpellingSuggestions = true;
                                            if (isAlphaNum && prevWasAlphaNum) capedSuggestion += " ";
                                            capedSuggestion += StringUtil.capitalize(word.toLowerCase());
                                            prevWasAlphaNum = isAlphaNum;
                                        }

                                        // create a capitalized version
                                        suggestedNames.add(fixSuggestion(capedSuggestion, " -_.'/\\", " "));
                                        if (!capedSuggestion.equals(capedSuggestion.toLowerCase())) {
                                            suggestedNames.add(fixSuggestion(capedSuggestion.toLowerCase(), " -_.'/\\", " "));
                                        }

                                        if (needSpellingSuggestions) suggestedNames.addAll(getSuggestions(manager, capedSuggestion));
                                        if (needSpellingSuggestions) suggestedNames.addAll(getSuggestions(manager, capedSuggestion.toLowerCase()));
                                    }

                                    for (String suggestion : suggestedNames) {
                                        resultSet.addElement(LookupElementBuilder.create(suggestion)
                                                .withCaseSensitivity(true)
                                        );
                                    }
                                }
                            }
                        } else if (elementType == WIKI_LINK_REF) {
                            Document document = parameters.getEditor().getDocument();
                            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);

                            if (virtualFile != null) {
                                Project fileProject = parameters.getEditor().getProject();
                                if (fileProject != null) {
                                    MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(fileProject);
                                    FileReferenceList wikiFileReferenceList = projectComponent.getFileReferenceList().query()
                                            .inSource(virtualFile, fileProject)
                                            .spaceDashEqual()
                                            .allWikiPageRefs();

                                    for (FileReference fileReference : wikiFileReferenceList.getFileReferences()) {
                                        addWikiPageRefCompletion(resultSet, (FileReferenceLink) fileReference, true);
                                    }

                                    for (FileReference fileReference : wikiFileReferenceList.getFileReferences()) {
                                        addWikiPageRefCompletion(resultSet, (FileReferenceLink) fileReference, false);
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

    protected void addWikiPageRefCompletion(@NotNull CompletionResultSet resultSet, FileReferenceLink fileReference, boolean accessible) {
        String wikiPageRef = fileReference.getWikiPageRef();
        boolean isWikiPageAccessible = fileReference.isWikiAccessible();

        if (accessible == isWikiPageAccessible) {
            if (isWikiPageAccessible || fileReference.getUpDirectories() == 0) {
                //String wikiPageShortRef = toFile.getWikiPageRef(null, WANT_WIKI_REF | ALLOW_INACCESSIBLE_WIKI_REF);
                String linkRefFileName = fileReference.getLinkRef();

                //logger.info("Adding " + wikiPageRef + " to completions");
                LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(wikiPageRef)
                        //.withLookupString(wikiPageShortRef)
                        .withCaseSensitivity(true)
                        .withIcon(accessible && fileReference.isWikiPage() ? MultiMarkdownIcons.WIKI : MultiMarkdownIcons.FILE)
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

    protected boolean isAlphaNum(@NotNull String word) {
        int iMax = word.length();
        for (int i = 0; i < iMax; i++) {
            char c = word.charAt(i);
            if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    protected static String fixSuggestion(@NotNull String suggestion, @NotNull String remove, @NotNull String pad) {// replace all unacceptables with a space
        int iMax = suggestion.length();
        remove += pad;
        StringBuilder newSuggestion = new StringBuilder(suggestion.length());
        for (int i = 0; i < iMax; i++) {
            if (remove.indexOf(suggestion.charAt(i)) >= 0) {
                if (newSuggestion.length() > 0 && newSuggestion.charAt(newSuggestion.length() - 1) != ' ') {
                    newSuggestion.append(pad);
                }
                continue;
            }
            newSuggestion.append(suggestion.charAt(i));
        }
        suggestion = newSuggestion.toString();
        return suggestion;
    }

    @NotNull
    public java.util.List<String> getSuggestions(SpellCheckerManager manager, @NotNull String text) {

        String[] words = NameUtil.nameToWords(text);

        int index = 0;
        java.util.List[] res = new java.util.List[words.length];
        int i = 0;
        for (String word : words) {
            int start = text.indexOf(word, index);
            int end = start + word.length();
            if (manager.hasProblem(word)) {
                java.util.List<String> variants = manager.getSuggestions(word);
                res[i++] = variants;
            } else {
                java.util.List<String> variants = new ArrayList<String>();
                variants.add(word);
                res[i++] = variants;
            }
            index = end;
        }

        int[] counter = new int[i];
        int size = 1;
        for (int j = 0; j < i; j++) {
            size *= res[j].size();
        }
        String[] all = new String[size];

        for (int k = 0; k < size; k++) {
            boolean prevAlnum = false;

            for (int j = 0; j < i; j++) {
                boolean isAlnum = isAlphaNum((String) res[j].get(counter[j]));

                if (all[k] == null) {
                    all[k] = "";
                } else if (isAlnum && prevAlnum) {
                    all[k] += " ";
                }

                all[k] += res[j].get(counter[j]);
                prevAlnum = isAlnum;

                counter[j]++;
                if (counter[j] >= res[j].size()) {
                    counter[j] = 0;
                }
            }
        }

        return Arrays.asList(all);
    }
}
