/*
/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
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

package com.vladsch.idea.multimarkdown.spellchecking;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.refactoring.rename.PreferrableNameSuggestionProvider;
import com.intellij.spellchecker.SpellCheckerManager;
import com.intellij.util.containers.ContainerUtil;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FileNameSuggestionProvider extends PreferrableNameSuggestionProvider {
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

        if (nameSuggestionContext == null || !(element instanceof MultiMarkdownFile)) {
            return null;
        }

        String text = nameSuggestionContext.getText();
        if (nameSuggestionContext instanceof PsiNamedElement) {
            //noinspection ConstantConditions
            text = ((PsiNamedElement) element).getName();

            if (text != null) {
                // remove the extension
                if (text.contains(" ") || text.contains("'") || text.contains("/") || text.contains("\\")) {
                    selfActivated = true;

                    // add fixed up version to result
                    String wikiRef = MultiMarkdownProjectComponent.fileNameToWikiRef(text, true);
                    String fixedUpName = MultiMarkdownProjectComponent.wikiPageRefToFileName(getFixedSuggestion(wikiRef), true);

                    result.add(fixedUpName);
                    result.add(text.replace(" ", "").replace("'", "").replace("/", "").replace("\\", ""));

                    if (!active) {
                        return SuggestedNameInfo.NULL_INFO;
                    }
                }
            }
        }

        if (text == null) {
            return null;
        }

        if (!active && (text.contains(" ") || text.contains("'") || text.contains("/") || text.contains("\\"))) {
            active = true;

            // add fixed up version to result and return
            String wikiRef = MultiMarkdownProjectComponent.wikiPageRefToFileName(getFixedSuggestion(text), true);
            result.add(wikiRef);
            return SuggestedNameInfo.NULL_INFO;
        }

        if (!active) {
            return null;
        }

        SpellCheckerManager manager = SpellCheckerManager.getInstance(element.getProject());

        List<String> suggestedNames = getSuggestions(manager, text);
        ArrayList<String> suggestions = new ArrayList<String>(suggestedNames.size());

        // we change the suggestions by adding an extension, and changing spaces to -, and removing any invalid file names, like ones with / \ '
        for (String suggestion : suggestedNames) {
            if (suggestion != null && suggestion.length() > 0) {
                String wikiRef = MultiMarkdownProjectComponent.wikiPageRefToFileName(getFixedSuggestion(suggestion), true);
                suggestions.add(wikiRef);
            }
        }

        if (suggestions.size() == 0) {
            return null;
        }

        ContainerUtil.addAllNotNull(result, suggestions);
        return SuggestedNameInfo.NULL_INFO;
    }

    @NotNull protected static String getFixedSuggestion(String suggestion) {// replace all unacceptables with a space
        int iMax = suggestion.length();
        StringBuilder newSuggestion = new StringBuilder(suggestion.length());
        for (int i = 0; i < iMax; i++) {
            if (suggestion.charAt(i) == '\'' || suggestion.charAt(i) == '/' || suggestion.charAt(i) == '\\') {
                if (newSuggestion.length() > 0 && newSuggestion.charAt(newSuggestion.length() - 1) != ' ') {
                    newSuggestion.append(' ');
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
            for (int j = 0; j < i; j++) {
                if (all[k] == null) {
                    all[k] = "";
                } else {
                    all[k] += " ";
                }

                all[k] += res[j].get(counter[j]);
                counter[j]++;
                if (counter[j] >= res[j].size()) {
                    counter[j] = 0;
                }
            }
        }

        return Arrays.asList(all);
    }
}
