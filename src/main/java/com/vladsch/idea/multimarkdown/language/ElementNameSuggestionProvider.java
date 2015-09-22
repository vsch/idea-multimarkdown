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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.refactoring.rename.PreferrableNameSuggestionProvider;
import com.intellij.spellchecker.SpellCheckerManager;
import com.intellij.spellchecker.util.Strings;
import com.intellij.util.containers.ContainerUtil;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.util.PathDistance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent.*;

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

    // KLUDGE: clean up and refactor this hack-ball
    @Override
    public SuggestedNameInfo getSuggestedNames(PsiElement element, PsiElement nameSuggestionContext, Set<String> result) {
        assert result != null;
        selfActivated = false;

        boolean isFileRename = false;
        SuggestedNameInfo suggestedNameInfo = null;
        String[] paths = null;

        if (nameSuggestionContext == null || !(element instanceof MultiMarkdownFile || element instanceof MultiMarkdownWikiPageRef)) {
            return null;
        }

        selfActivated = !active;
        active = true;

        String text = nameSuggestionContext.getText();
        //PsiElement parent = nameSuggestionContext.getParent();
        if (nameSuggestionContext instanceof PsiNamedElement) {
            //noinspection ConstantConditions
            // this is a rename on a link ref pointing to a valid file
            text = ((PsiNamedElement) element).getName();
            isFileRename = true;

            if (text != null) {
                // remove the extension
                if (text.contains(" ") || text.contains("'") || text.contains("/") || text.contains("\\")) {
                    // add fixed up version to result
                    String wikiRef = MultiMarkdownProjectComponent.fileNameToWikiRef(text, true);
                    String fixedUpName = MultiMarkdownProjectComponent.wikiPageRefToFileName(fixSuggestion(wikiRef, " '/\\", " "), true);

                    result.add(fixedUpName);
                    result.add(text.replace(" ", "").replace("'", "").replace("/", "").replace("\\", ""));
                }
            }
        } else if (element instanceof MultiMarkdownWikiPageRef) {
            // this is a rename on a missing link element, provide list of valid markdown files that can be reached via wikiPageRef
            // always activate spelling suggestions for renaming wiki page refs
            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(element.getProject());
            MultiMarkdownFile markdownFile = (MultiMarkdownFile) element.getContainingFile();
            VirtualFile virtualFile = markdownFile.getVirtualFile();
            boolean wikiPage = markdownFile.isWikiPage();
            List<MultiMarkdownFile> wikiFiles = projectComponent.findRefLinkMarkdownFiles(null, virtualFile,
                    WANT_WIKI_REF | (wikiPage ? WIKIPAGE_FILE : MARKDOWN_FILE));

            if (wikiFiles != null && wikiFiles.size() != 0) {
                // add fixed up version to result
                paths = PathDistance.loadLinkRefsStrings(wikiFiles, virtualFile, WANT_WIKI_REF | (wikiPage ? 0 :
                        ALLOW_INACCESSIBLE_WIKI_REF));

                suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
            }

            text = ((MultiMarkdownWikiPageRef) element).getName();
        }

        if (text == null) {
            if (paths != null) ContainerUtil.addAllNotNull(result, paths);
            return suggestedNameInfo;
        }

        SpellCheckerManager manager = SpellCheckerManager.getInstance(element.getProject());

        if (isFileRename) text = MultiMarkdownProjectComponent.fileNameToWikiRef(text, true);

        // add first cap versions if the words are either lowercase or upppercase
        String[] words = NameUtil.nameToWords(text);
        String capedSuggestion = "";
        boolean mixedCase = false;
        boolean needSpellingSuggestions = false;
        boolean prevWasAlphaNum = false;

        ArrayList<String> suggestedNames = new ArrayList<String>();

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
        suggestedNames.add(fixSuggestion(capedSuggestion, " -_.'/\\", ""));

        suggestedNames.add(fixSuggestion(text, " -_.'/\\", " "));
        suggestedNames.add(fixSuggestion(text, " -_.'/\\", ""));

        if (needSpellingSuggestions) suggestedNames.addAll(getSuggestions(manager, text));

        ArrayList<String> suggestions = new ArrayList<String>(suggestedNames.size());

        // we change the suggestions by adding an extension, and changing spaces to -, and removing any invalid file names, like ones with / \ '
        for (String suggestion : suggestedNames) {
            if (suggestion != null && suggestion.length() > 0) {
                String wikiRef = !isFileRename ? suggestion :
                        MultiMarkdownProjectComponent.wikiPageRefToFileName(suggestion, false);

                if (wikiRef != null) {
                    suggestions.add(wikiRef + ".md");
                    suggestions.add(fixSuggestion(wikiRef, " -_.'/\\", "-") + ".md");
                    suggestions.add(fixSuggestion(wikiRef, " -_.'/\\", "") + ".md");
                }
            }
        }

        if (suggestions.size() != 0) {
            ContainerUtil.addAllNotNull(result, suggestions);
            suggestedNameInfo = SuggestedNameInfo.NULL_INFO;
        }

        // now we add the existing paths
        if (paths != null) {
            ContainerUtil.addAllNotNull(result, paths);
        }
        return suggestedNameInfo;
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
    public List<String> getSuggestions(SpellCheckerManager manager, @NotNull String text) {

        String[] words = NameUtil.nameToWords(text);

        int index = 0;
        List[] res = new List[words.length];
        int i = 0;
        for (String word : words) {
            int start = text.indexOf(word, index);
            int end = start + word.length();
            if (manager.hasProblem(word)) {
                List<String> variants = manager.getSuggestions(word);
                res[i++] = variants;
            } else {
                List<String> variants = new ArrayList<String>();
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
