/*
 * Copyright 2000-2009 JetBrains s.r.o.
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vladsch.idea.multimarkdown.language;

import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.refactoring.rename.PreferrableNameSuggestionProvider;
import com.intellij.util.containers.ContainerUtil;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.spellchecking.MultiMarkdownIdentifierTokenizer;
import com.vladsch.idea.multimarkdown.spellchecking.Suggestion;
import com.vladsch.idea.multimarkdown.spellchecking.SuggestionList;
import com.vladsch.idea.multimarkdown.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static com.vladsch.idea.multimarkdown.spellchecking.SuggestionFixers.*;

public class ElementNameSuggestionProvider extends PreferrableNameSuggestionProvider {
    private boolean active;
    private boolean selfActivated;

    public void setActive(boolean active) {
        this.active = active;
        if (!active) selfActivated = false;
    }

    @Override
    public boolean shouldCheckOthers() {
        return !(active || selfActivated);
    }

    @Override
    public SuggestedNameInfo getSuggestedNames(PsiElement element, PsiElement nameSuggestionContext, Set<String> result) {
        assert result != null;
        selfActivated = false;

        SuggestedNameInfo suggestedNameInfo = null;

        if (nameSuggestionContext == null || !(element instanceof MultiMarkdownFile || element instanceof MultiMarkdownNamedElement)) {
            return null;
        }

        selfActivated = !active;
        active = true;

        if (element instanceof MultiMarkdownFile) {
            //noinspection ConstantConditions
            // this is a rename of a file on a link ref pointing to a valid file
            SuggestionList suggestionList = new SuggestionList(element.getProject());

            PathInfo filePathInfo = new PathInfo(((MultiMarkdownFile) element).getVirtualFile().getPath());

            suggestionList = suggestionList
                    .add(WikiLinkRef.fileAsLink(filePathInfo.getFileNameNoExt()), new Suggestion.Param<String>(Suggestion.Fixer.FILE_PATH, filePathInfo.getFilePath()))
                    .add(suggestionList.chainFixers(SuggestCleanSpacedWords, (!selfActivated ? SuggestSpelling : null)))
                    .batchFixers(
                            SuggestCleanSpacedWords, SuggestCapSpacedWords
                            //, SuggestCleanDashedWords, SuggestCapDashedWords
                            //, SuggestCleanSplicedWords, SuggestCapSplicedWords
                    )
                    // fix names to files from wiki refs and remove those that we cannot use
                    .chainFixers(SuggestWikiRefAsFilNameWithExt, SuggestRemoveInvalidFileNames);

            if (suggestionList.size() > 0) {
                ContainerUtil.addAllNotNull(result, suggestionList.asList());
                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }

            return suggestedNameInfo;
        } else if (element instanceof MultiMarkdownWikiLinkText) {
            // this is a rename on a wiki page title
            // always activate spelling suggestions for renaming wiki page refs
            // Get suggestions from the name of the pageRef text
            SuggestionList suggestionList = getLinkTextSuggestions(element.getParent(), !selfActivated);
            if (suggestionList.size() > 0) {
                ContainerUtil.addAllNotNull(result, suggestionList.asList());
                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }
            return suggestedNameInfo;
        } else if (element instanceof MultiMarkdownLinkRef) {
            // this is a rename on a missing link element, provide list of valid markdown files that can be reached via wikiPageRef
            // always activate spelling suggestions for renaming wiki page refs
            SuggestionList suggestionList = new SuggestionList(element.getProject());
            FileRef fileRef = new FileRef(element.getContainingFile());
            LinkRef linkRef = new LinkRef(fileRef, "", null, null);

            GitHubLinkResolver resolver = new GitHubLinkResolver(element.getContainingFile());
            List<PathInfo> linkRefs = resolver.multiResolve(linkRef, LinkResolver.LOOSE_MATCH, null);

            if (linkRefs.size() > 0) {
                // add fixed up version to result
                suggestionList.addAll(PathInfo.fileNamesNoExt(linkRefs));
            }

            if (suggestionList.size() > 0) {
                ContainerUtil.addAllNotNull(result, suggestionList.asList());
                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }
            return suggestedNameInfo;
        } else if (element instanceof MultiMarkdownWikiLinkRef) {
            // this is a rename on a missing link element, provide list of valid markdown files that can be reached via wikiPageRef
            // always activate spelling suggestions for renaming wiki page refs
            SuggestionList suggestionList = new SuggestionList(element.getProject());
            FileRef fileRef = new FileRef(element.getContainingFile());
            LinkRef linkRef = new WikiLinkRef(fileRef, "", null, null);

            GitHubLinkResolver resolver = new GitHubLinkResolver(element.getContainingFile());
            List<PathInfo> linkRefs = resolver.multiResolve(linkRef, LinkResolver.LOOSE_MATCH, null);

            if (linkRefs.size() > 0) {
                // add fixed up version to result
                suggestionList.addAll(PathInfo.fileNamesNoExt(linkRefs));
            }

            if (suggestionList.size() > 0) {
                ContainerUtil.addAllNotNull(result, suggestionList.asList());
                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }
            return suggestedNameInfo;
        } else {
            // this is a rename on an element
            // only activate spelling suggestions if spell check activated
            if (!selfActivated) {
                SuggestionList uncheckedSuggestionList = new SuggestionList(element.getProject());
                final StringBuilder text = new StringBuilder(element.getTextLength());
                MultiMarkdownIdentifierTokenizer tokenizer = new MultiMarkdownIdentifierTokenizer();
                Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(Suggestion.Fixer.NEEDS_SPELLING_FIXER, true);

                tokenizer.tokenizeSpellingSuggestions((MultiMarkdownNamedElement) element, new MultiMarkdownIdentifierTokenizer.SpellCheckConsumer() {
                    @Override
                    public void consume(String word, boolean spellCheck) {
                        text.append(word);
                    }
                });

                uncheckedSuggestionList.add(text.toString(), param);

                SuggestionList suggestionList = uncheckedSuggestionList.batchFixers(SuggestSpelling);
                if (suggestionList.size() > 0) {
                    ContainerUtil.addAllNotNull(result, suggestionList.asList());
                    suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
                }
                return suggestedNameInfo;
            }
        }

        // false alarm, go back to sleep
        selfActivated = false;
        active = false;
        return null;
    }

    @NotNull
    public static SuggestionList getLinkTextSuggestions(@NotNull PsiElement parent, boolean spellCheck) {
        SuggestionList suggestionList = new SuggestionList(parent.getProject());
        if (!(parent instanceof MultiMarkdownLinkElement)) return suggestionList;

        MultiMarkdownLinkElement linkElement = (MultiMarkdownLinkElement) parent;

        SuggestionList originalList = new SuggestionList(parent.getProject());
        String linkText = linkElement.getLinkText();
        String linkRefText = linkElement.getLinkRef();
        String linkAnchor = linkElement.getLinkAnchor();
        String originalText = null;

        if (!linkText.isEmpty()) {
            String text = linkText;
            text = text.replace(MultiMarkdownCompletionContributor.DUMMY_IDENTIFIER, "").trim();
            if (!text.isEmpty()) {
                originalText = text;
                originalList.add(text);
                suggestionList.add(originalList);
            }
        }

        if (!linkRefText.isEmpty()) {
            String linkRef = new PathInfo(linkRefText).getFileNameNoExt();

            if (!linkAnchor.isEmpty()) {
                originalList.add(linkRef);
                originalList.add(linkRef + ": " + linkAnchor);

                SuggestionList anchoredList = new SuggestionList(originalList.getProject()).add(linkRef);
                SuggestionList anchorList = new SuggestionList(originalList.getProject()).add(linkAnchor);

                anchoredList.add(anchoredList.sequenceFixers(SuggestCleanSpacedWords, SuggestCapSpacedWords));
                anchorList.add(anchorList.sequenceFixers(SuggestCleanSpacedWords, SuggestCapSpacedWords));

                SuggestionList suggestions = new SuggestionList(originalList.getProject()).add(": ");
                suggestions = suggestions.wrapPermuteFixedAligned(anchoredList, anchorList, SuggestCleanSpacedWords, SuggestCapSpacedWords);
                originalList.add(suggestions);
            }

            suggestionList.add(linkRef);
            String text = linkRef + " " + linkAnchor;
            suggestionList.add(text);

            // add with path parts that are in the link
            linkRefText = new PathInfo(linkRefText).getPath();
            suggestionList.add(linkRefText);
            linkRefText = new PathInfo(linkRefText).getPath();
            suggestionList.add(linkRefText + " " + text);
        }

        suggestionList = originalList.add(suggestionList.chainFixers(SuggestCleanSpacedWords, spellCheck ? SuggestSpelling : null)
                , suggestionList.sequenceFixers(
                        SuggestCleanSpacedWords, SuggestCapSpacedWords, SuggestLowerSpacedWords
                        //, SuggestCleanDashedWords, SuggestCapDashedWords
                        //, SuggestCleanSplicedWords, SuggestCapSplicedWords
                )
        );

        return suggestionList.size() == 1 && suggestionList.get(0).equals(originalText) ? SuggestionList.EMPTY_LIST : suggestionList;
    }
}
