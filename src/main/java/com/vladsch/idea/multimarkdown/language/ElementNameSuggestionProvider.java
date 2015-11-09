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
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import com.vladsch.idea.multimarkdown.spellchecking.MultiMarkdownIdentifierTokenizer;
import com.vladsch.idea.multimarkdown.spellchecking.Suggestion;
import com.vladsch.idea.multimarkdown.spellchecking.SuggestionList;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import com.vladsch.idea.multimarkdown.util.FileReferenceList;
import com.vladsch.idea.multimarkdown.util.FileReferenceListQuery;
import org.jetbrains.annotations.NotNull;

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

            FilePathInfo filePathInfo = new FilePathInfo(((MultiMarkdownFile) element).getVirtualFile().getPath());

            suggestionList = suggestionList
                    .add(filePathInfo.getFileNameNoExtAsWikiRef(), new Suggestion.Param<String>(Suggestion.Fixer.FILE_PATH, filePathInfo.getFilePath()))
                    .add(suggestionList.chainFixers(SuggestCleanSpacedWords, (!selfActivated ? SuggestSpelling : null)))
                    .batchFixers(
                            SuggestCleanSpacedWords, SuggestCapSpacedWords
                            //, SuggestCleanDashedWords, SuggestCapDashedWords
                            //, SuggestCleanSplicedWords, SuggestCapSplicedWords
                    )
                    // fix names to files from wiki refs and remove those that we cannot use
                    .chainFixers(SuggestWikiRefAsFilNameWithExt, SuggestRemoveInvalidFileNames)
            ;

            if (suggestionList.size() > 0) {
                ContainerUtil.addAllNotNull(result, suggestionList.asList());
                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }

            return suggestedNameInfo;
        } else if (element instanceof MultiMarkdownWikiPageText) {
            // this is a rename on a wiki page title
            // always activate spelling suggestions for renaming wiki page refs
            // Get suggestions from the name of the pageRef text
            SuggestionList suggestionList = getWikiPageTextSuggestions(element.getParent());
            if (suggestionList.size() > 0) {
                ContainerUtil.addAllNotNull(result, suggestionList.asList());
                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }
            return suggestedNameInfo;
        } else if (element instanceof MultiMarkdownLinkRef) {
            // this is a rename on a missing link element, provide list of valid markdown files that can be reached via wikiPageRef
            // always activate spelling suggestions for renaming wiki page refs
            SuggestionList suggestionList = new SuggestionList(element.getProject());

            //suggestionList.add(FilePathInfo.linkRefNoAnchor(((MultiMarkdownWikiPageRef) element).getName()));

            MultiMarkdownFile markdownFile = (MultiMarkdownFile) element.getContainingFile();
            FileReferenceList linkRefs = new FileReferenceListQuery(element.getProject())
                    .gitHubWikiRules()
                    .sameGitHubRepo()
                    .inSource(markdownFile)
                    .all();

            if (linkRefs.size() > 0) {
                // add fixed up version to result
                suggestionList.addAll(linkRefs.getAllLinkRefNoExtStrings());
            }

            if (suggestionList.size() > 0) {
                ContainerUtil.addAllNotNull(result, suggestionList.asList());
                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }
            return suggestedNameInfo;
        } else if (element instanceof MultiMarkdownWikiPageRef) {
            // this is a rename on a missing link element, provide list of valid markdown files that can be reached via wikiPageRef
            // always activate spelling suggestions for renaming wiki page refs
            SuggestionList suggestionList = new SuggestionList(element.getProject());

            //suggestionList.add(FilePathInfo.linkRefNoAnchor(((MultiMarkdownWikiPageRef) element).getName()));

            MultiMarkdownFile markdownFile = (MultiMarkdownFile) element.getContainingFile();
            FileReferenceList wikiPages = new FileReferenceListQuery(element.getProject())
                    .gitHubWikiRules()
                    .inSource(markdownFile)
                    .wikiPageRefs(!markdownFile.isWikiPage());

            if (wikiPages.size() > 0) {
                // add fixed up version to result
                suggestionList.addAll(wikiPages.getAllWikiPageRefStrings());
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
        if (selfActivated) {
            selfActivated = false;
            active = false;
        }
        return null;
    }

    public static SuggestionList getWikiPageTextSuggestions(@NotNull PsiElement parent) {
        SuggestionList suggestionList = new SuggestionList(parent.getProject());
        MultiMarkdownWikiPageRef wikiPageRef = (MultiMarkdownWikiPageRef) MultiMarkdownPsiImplUtil.findChildByType(parent, MultiMarkdownTypes.WIKI_LINK_REF);
        MultiMarkdownWikiPageText wikiPageText = (MultiMarkdownWikiPageText) MultiMarkdownPsiImplUtil.findChildByType(parent, MultiMarkdownTypes.WIKI_LINK_TEXT);

        SuggestionList originalList = new SuggestionList(parent.getProject());

        if (wikiPageText != null) {
            String text = wikiPageText.getName();
            if (text != null) {
                text = text.replace(MultiMarkdownCompletionContributor.DUMMY_IDENTIFIER, "").trim();
                if (!text.isEmpty()) {
                    originalList.add(text);
                    suggestionList.add(originalList);
                }
            }
        }

        if (wikiPageRef != null) {
            String text = wikiPageRef.getNameWithAnchor();
            if (text != null) {
                FilePathInfo pathInfo = new FilePathInfo(text);
                text = pathInfo.getFileName();

                if (!FilePathInfo.linkRefAnchor(text).isEmpty()) {
                    originalList.add(FilePathInfo.linkRefNoAnchor(text));
                    originalList.add(FilePathInfo.linkRefNoAnchor(text) + ": " + FilePathInfo.linkRefAnchorNoHash(text));

                    SuggestionList anchoredList = new SuggestionList(originalList.getProject()).add(FilePathInfo.linkRefNoAnchor(text));

                    SuggestionList anchorList = new SuggestionList(originalList.getProject()).add(FilePathInfo.linkRefAnchorNoHash(text));

                    anchoredList.add(anchoredList.sequenceFixers(SuggestCleanSpacedWords, SuggestCapSpacedWords));
                    anchorList.add(anchorList.sequenceFixers(SuggestCleanSpacedWords, SuggestCapSpacedWords));

                    SuggestionList suggestions = new SuggestionList(originalList.getProject()).add(": ");

                    suggestions = suggestions.wrapPermuteFixedAligned(anchoredList, anchorList, SuggestCleanSpacedWords, SuggestCapSpacedWords);

                    originalList.add(suggestions);

                    //if (anchoredList.size() == 1 || anchorList.size() == 1) {
                    //    originalList.add(anchorList.prefixPermute(anchoredList.suffix(": ")));
                    //} else {
                    //    originalList.add(anchorList.prefixAlign(anchoredList.suffix(": ")));
                    //}
                }

                suggestionList.add(FilePathInfo.linkRefNoAnchor(text));
                suggestionList.add(text);

                // add with path parts, to 2 directories above
                String parentDir = (pathInfo = new FilePathInfo(pathInfo.getPath())).getFilePath();
                suggestionList.add(parentDir + FilePathInfo.linkRefNoAnchor(text));
                suggestionList.add(parentDir + text);
            }
        }

        suggestionList = originalList.add(suggestionList.chainFixers(SuggestCleanSpacedWords, SuggestSpelling)
                , suggestionList.sequenceFixers(
                        SuggestCleanSpacedWords, SuggestCapSpacedWords, SuggestLowerSpacedWords
                        //, SuggestCleanDashedWords, SuggestCapDashedWords
                        //, SuggestCleanSplicedWords, SuggestCapSplicedWords
                )
        );

        return suggestionList;
    }

    public static SuggestionList getLinkRefTextSuggestions(@NotNull PsiElement parent, boolean spellCheck) {
        SuggestionList suggestionList = new SuggestionList(parent.getProject());
        String linkRef = MultiMarkdownPsiImplUtil.getLinkRef(parent);
        String linkRefText = MultiMarkdownPsiImplUtil.getLinkRefText(parent);
        String linkRefAnchor = MultiMarkdownPsiImplUtil.getLinkRefAnchor(parent);

        SuggestionList originalList = new SuggestionList(parent.getProject());

        if (!linkRefText.isEmpty()) {
            String text = linkRefText;
            text = text.replace(MultiMarkdownCompletionContributor.DUMMY_IDENTIFIER, "").trim();
            if (!text.isEmpty()) {
                originalList.add(text);
                suggestionList.add(originalList);
            }
        }

        if (!linkRef.isEmpty()) {
            String text = linkRef;
            FilePathInfo pathInfo = new FilePathInfo(text);
            if (!pathInfo.isExternalReference()) {
                text = pathInfo.getFileNameNoExt();

                if (!FilePathInfo.linkRefAnchor(text).isEmpty()) {
                    originalList.add(FilePathInfo.linkRefNoAnchor(text));
                    originalList.add(FilePathInfo.linkRefNoAnchor(text) + ": " + FilePathInfo.linkRefAnchorNoHash(text));

                    SuggestionList anchoredList = new SuggestionList(originalList.getProject()).add(FilePathInfo.linkRefNoAnchor(text));

                    SuggestionList anchorList = new SuggestionList(originalList.getProject()).add(FilePathInfo.linkRefAnchorNoHash(text));

                    anchoredList.add(anchoredList.sequenceFixers(SuggestCleanSpacedWords, SuggestCapSpacedWords));
                    anchorList.add(anchorList.sequenceFixers(SuggestCleanSpacedWords, SuggestCapSpacedWords));

                    SuggestionList suggestions = new SuggestionList(originalList.getProject()).add(": ");

                    suggestions = suggestions.wrapPermuteFixedAligned(anchoredList, anchorList, SuggestCleanSpacedWords, SuggestCapSpacedWords);

                    originalList.add(suggestions);

                    //if (anchoredList.size() == 1 || anchorList.size() == 1) {
                    //    originalList.add(anchorList.prefixPermute(anchoredList.suffix(": ")));
                    //} else {
                    //    originalList.add(anchorList.prefixAlign(anchoredList.suffix(": ")));
                    //}
                }

                suggestionList.add(FilePathInfo.linkRefNoAnchor(text));
                suggestionList.add(text);

                // add with path parts, to 2 directories above
                String parentDir = (pathInfo = new FilePathInfo(pathInfo.getPath())).getFilePath();
                suggestionList.add(parentDir + " " + FilePathInfo.linkRefNoAnchor(text));
                suggestionList.add(parentDir + " " + text);
            }
        }

        suggestionList = originalList.add(suggestionList.chainFixers(SuggestCleanSpacedWords, spellCheck ? SuggestSpelling : null)
                , suggestionList.sequenceFixers(
                        SuggestCleanSpacedWords, SuggestCapSpacedWords, SuggestLowerSpacedWords
                        //, SuggestCleanDashedWords, SuggestCapDashedWords
                        //, SuggestCleanSplicedWords, SuggestCapSplicedWords
                )
        );

        return suggestionList;
    }
}
