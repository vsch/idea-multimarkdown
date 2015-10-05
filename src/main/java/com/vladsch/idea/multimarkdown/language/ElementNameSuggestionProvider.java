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
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import com.vladsch.idea.multimarkdown.spellchecking.Suggestion;
import com.vladsch.idea.multimarkdown.spellchecking.SuggestionList;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import com.vladsch.idea.multimarkdown.util.FileReferenceList;
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
        } else if (element instanceof MultiMarkdownWikiPageTitle) {
            // this is a rename on a wiki page title
            // always activate spelling suggestions for renaming wiki page refs
            // Get suggestions from the name of the pageRef text
            SuggestionList suggestionList = getWikiPageTitleSuggestions(element.getParent());
            if (suggestionList.size() > 0) {
                ContainerUtil.addAllNotNull(result, suggestionList.asList());
                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }
            return suggestedNameInfo;
        } else if (element instanceof MultiMarkdownWikiPageRef) {
            // this is a rename on a missing link element, provide list of valid markdown files that can be reached via wikiPageRef
            // always activate spelling suggestions for renaming wiki page refs
            SuggestionList suggestionList = new SuggestionList(element.getProject());

            suggestionList.add(((MultiMarkdownWikiPageRef) element).getName());

            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(element.getProject());
            MultiMarkdownFile markdownFile = (MultiMarkdownFile) element.getContainingFile();
            FileReferenceList wikiPages = projectComponent.getFileReferenceList().query()
                    .inSource(markdownFile)
                    .wikiPageRefs(!markdownFile.isWikiPage());

            if (wikiPages.getFileReferences().length > 0) {
                // add fixed up version to result
                suggestionList.addAll(wikiPages.getAllWikiPageRefStrings());
            }

            if (suggestionList.size() > 0) {
                ContainerUtil.addAllNotNull(result, suggestionList.asList());
                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }
            return suggestedNameInfo;
        }

        // false alarm, go back to sleep
        if (selfActivated) {
            selfActivated = false;
            active = false;
        }
        return null;
    }

    public static SuggestionList getWikiPageTitleSuggestions(@NotNull PsiElement parent) {
        SuggestionList suggestionList = new SuggestionList(parent.getProject());
        MultiMarkdownWikiPageRef wikiPageRef = (MultiMarkdownWikiPageRef) MultiMarkdownPsiImplUtil.findChildByType(parent, MultiMarkdownTypes.WIKI_LINK_REF);
        MultiMarkdownWikiPageTitle wikiPageTitle = (MultiMarkdownWikiPageTitle) MultiMarkdownPsiImplUtil.findChildByType(parent, MultiMarkdownTypes.WIKI_LINK_TITLE);

        String originalText = null;

        if (wikiPageTitle != null) {
            String text = wikiPageTitle.getName();
            if (text != null) {
                text = text.replace("IntellijIdeaRulezzz ", "").trim();
                if (!text.isEmpty()) {
                    originalText = text;
                    suggestionList.add(FilePathInfo.linkRefNoAnchor(text));
                    suggestionList.add(text);
                }
            }
        }

        if (wikiPageRef != null) {
            String text = wikiPageRef.getName();
            if (text != null) {
                FilePathInfo pathInfo = new FilePathInfo(text);
                text = pathInfo.getFileName();
                suggestionList.add(FilePathInfo.linkRefNoAnchor(text));
                suggestionList.add(text);

                // add with path parts, to 2 directories above
                String parentDir = (pathInfo = new FilePathInfo(pathInfo.getPath())).getFilePath();
                suggestionList.add(parentDir + FilePathInfo.linkRefNoAnchor(text));
                suggestionList.add(parentDir + text);
            }
        }

        if (suggestionList.size() > 0) {
            suggestionList = suggestionList
                    .add(suggestionList.chainFixers(SuggestCleanSpacedWords, SuggestSpelling))
                    .sequenceFixers(
                            SuggestCleanSpacedWords, SuggestCapSpacedWords, SuggestLowerSpacedWords
                            //, SuggestCleanDashedWords, SuggestCapDashedWords
                            //, SuggestCleanSplicedWords, SuggestCapSplicedWords
                    )
                    .add(originalText)
            ;
        }

        return suggestionList;
    }
}
