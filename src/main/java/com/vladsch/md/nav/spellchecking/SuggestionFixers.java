// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.spellchecking;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.spellchecker.SpellCheckerManager;
import com.vladsch.md.nav.util.FileRef;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.md.nav.util.ProjectFileRef;
import com.vladsch.md.nav.util.WikiLinkRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestionFixers {
    public static final CleanSpacedWordsFixer SuggestCleanSpacedWords = new CleanSpacedWordsFixer();
    public static final CapSpacedWordsFixer SuggestCapSpacedWords = new CapSpacedWordsFixer();
    public static final LowerSpacedWordsFixer SuggestLowerSpacedWords = new LowerSpacedWordsFixer();
    public static final UpperSpacedWordsFixer SuggestUpperSpacedWords = new UpperSpacedWordsFixer();
    public static final CleanSplicedWordsFixer SuggestCleanSplicedWords = new CleanSplicedWordsFixer();
    public static final CapSplicedWordsFixer SuggestCapSplicedWords = new CapSplicedWordsFixer();
    public static final LowerSplicedWordsFixer SuggestLowerSplicedWords = new LowerSplicedWordsFixer();
    public static final UpperSplicedWordsFixer SuggestUpperSplicedWords = new UpperSplicedWordsFixer();
    public static final CleanDashedWordsFixer SuggestCleanDashedWords = new CleanDashedWordsFixer();
    public static final CapDashedWordsFixer SuggestCapDashedWords = new CapDashedWordsFixer();
    public static final LowerDashedWordsFixer SuggestLowerDashedWords = new LowerDashedWordsFixer();
    public static final UpperDashedWordsFixer SuggestUpperDashedWords = new UpperDashedWordsFixer();
    public static final CleanSnakedWordsFixer SuggestCleanSnakedWords = new CleanSnakedWordsFixer();
    public static final CapSnakedWordsFixer SuggestCapSnakedWords = new CapSnakedWordsFixer();
    public static final LowerSnakedWordsFixer SuggestLowerSnakedWords = new LowerSnakedWordsFixer();
    public static final UpperSnakedWordsFixer SuggestUpperSnakedWords = new UpperSnakedWordsFixer();
    public static final SpellingFixer SuggestSpelling = new SpellingFixer();
    public static final WikiRefAsFilNameWithExtFixer SuggestWikiRefAsFilNameWithExt = new WikiRefAsFilNameWithExtFixer();
    public static final FileNameWithExtFixer SuggestFileNameWithExt = new FileNameWithExtFixer();
    public static final RemoveInvalidFileNamesFixer SuggestRemoveInvalidFileNames = new RemoveInvalidFileNamesFixer();
    //public static final SpaceCamelCaseWordsFixer SuggestSpaceCamelCaseWords = new SpaceCamelCaseWordsFixer();

    public static abstract class FixerBase implements Suggestion.Fixer {
        private SuggestionList suggestionList;
        private Suggestion sourceSuggestion;

        private void setFields(SuggestionList suggestionList, Suggestion sourceSuggestion) {
            this.suggestionList = suggestionList;
            this.sourceSuggestion = sourceSuggestion;
        }

        private void clearFields() {
            setFields(null, null);
        }

        @NotNull
        @Override
        final public SuggestionList fix(@NotNull Suggestion suggestion, Project project) {
            SuggestionList suggestedNames = new SuggestionList();

            setFields(suggestedNames, suggestion);
            makeSuggestions(suggestion.getText(), suggestion, project);
            clearFields();

            return suggestedNames;
        }

        protected void addSuggestion(@NotNull Suggestion suggestion) {
            suggestionList.add(suggestion.getText(), suggestion, sourceSuggestion);
        }

        protected void addSuggestion(@NotNull String suggestion) {
            suggestionList.add(suggestion, sourceSuggestion);
        }

        public abstract void makeSuggestions(@NotNull String text, @NotNull Suggestion suggestion, Project project);
    }

    public static class WikiRefAsFilNameWithExtFixer extends FixerBase {
        @Override
        public void makeSuggestions(@NotNull String text, @NotNull Suggestion suggestion, Project project) {
            String fileName = WikiLinkRef.linkAsFile(new PathInfo(text).getFileNameNoExt()) + PathInfo.WIKI_PAGE_EXTENSION;
            addSuggestion(fileName);
        }
    }

    // not really needed CleanSpacedWords does it already
    //public static class SpaceCamelCaseWordsFixer extends FixerBase {
    //    @Override
    //    public void makeSuggestions(@NotNull String text, @NotNull Suggestion suggestion, Project project) {
    //        // if text is camel case we convert it to spaced words
    //        int iMax = text.length();
    //        boolean prevWasLower = false;
    //        StringBuilder stringBuilder = new StringBuilder(iMax + 10);
    //
    //        for (int i = 0; i < iMax; i++) {
    //            Character c = text.charAt(i);
    //            if (Character.isLowerCase(c)) {
    //                prevWasLower = true;
    //            } else {
    //                if (Character.isUpperCase(c)) {
    //                    if (prevWasLower) {
    //                        stringBuilder.append(' ');
    //                    }
    //                }
    //                prevWasLower = false;
    //            }
    //            stringBuilder.append(c);
    //        }
    //        addSuggestion(stringBuilder.toString());
    //    }
    //}

    public static class RemoveInvalidFileNamesFixer extends FixerBase {
        @Override
        public void makeSuggestions(final @NotNull String text, @NotNull final Suggestion suggestion, Project project) {
            if (project != null && suggestion.hasParam(FILE_PATH)) {
                FileRef fileReference = new FileRef(suggestion.stringParam(FILE_PATH));
                FileRef renamedFileReference = fileReference.append("", text);
                ProjectFileRef projectFileRef = renamedFileReference.projectFileRef(project);
                if (projectFileRef == null || projectFileRef.getExists()) return;
            }
            addSuggestion(suggestion);
        }
    }

    public static class FileNameWithExtFixer extends FixerBase {
        @Override
        public void makeSuggestions(@NotNull String text, @NotNull Suggestion suggestion, Project project) {
            String wikiRef = new PathInfo(text).getFileNameNoExt();
            addSuggestion(wikiRef + PathInfo.WIKI_PAGE_EXTENSION);
        }
    }

    public static class SpellingFixer implements Suggestion.Fixer {
        @Nullable
        @Override
        public SuggestionList fix(@NotNull Suggestion suggestion, Project project) {
            if (!suggestion.boolParam(NEEDS_SPELLING_FIXER) || suggestion.boolParam(HAD_SPELLING_FIXER)) return null;

            SuggestionList suggestionList = new SuggestionList();
            SpellCheckerManager manager = SpellCheckerManager.getInstance(project);
            List<String> spellingSuggestions = getSuggestions(manager, suggestion.getText());
            Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(HAD_SPELLING_FIXER, true);
            for (String text : spellingSuggestions) {
                suggestionList.add(text, param, suggestion);
            }
            return suggestionList;
        }
    }

    public static abstract class WordsFixerBase extends FixerBase {
        private Suggestion.Param<Boolean> param;

        private void setFields(Suggestion.Param<Boolean> param) {
            this.param = param;
        }

        private void clearFields() {
            setFields(null);
        }

        @Override
        protected void addSuggestion(@NotNull Suggestion suggestion) {
            super.addSuggestion(new Suggestion(suggestion.getText(), param, suggestion));
        }

        @Override
        protected void addSuggestion(@NotNull String suggestion) {
            super.addSuggestion(new Suggestion(suggestion, param));
        }

        @Override
        final public void makeSuggestions(@NotNull String text, @NotNull Suggestion suggestion, Project project) {
            String[] words = suggestion.getWords();
            String cleanedSuggestion = "";
            boolean needSpellingSuggestions = false;
            boolean prevWasAlphaNum = false;

            SpellCheckerManager manager = project != null ? SpellCheckerManager.getInstance(project) : null;

            for (String word : words) {
                boolean isAlphaNum = isAlphaNum(word);

                if (manager != null && manager.hasProblem(word)) needSpellingSuggestions = true;
                if (isAlphaNum && prevWasAlphaNum) {
                    cleanedSuggestion += getWordSpacer();
                }
                cleanedSuggestion += fixWord(word);
                prevWasAlphaNum = isAlphaNum;
            }

            cleanedSuggestion = fixSuggestion(cleanedSuggestion, getRemoveChars(), getWordSpacer());
            setFields(new Suggestion.Param<Boolean>(Suggestion.Fixer.NEEDS_SPELLING_FIXER, needSpellingSuggestions));
            makeSuggestions(cleanedSuggestion);
            clearFields();
        }

        @NotNull
        protected String getRemoveChars() {
            return " -_.'/\\#\"";
        }

        @NotNull
        abstract public String getWordSpacer();

        @NotNull
        public String fixWord(@NotNull String word) {
            return word;
        }

        public void makeSuggestions(@NotNull String cleanedWord) {
            addSuggestion(cleanedWord);
        }
    }

    public static class CleanSpacedWordsFixer extends WordsFixerBase {
        @Override
        @NotNull
        public String getWordSpacer() {
            return " ";
        }
    }

    public static class CapSpacedWordsFixer extends CleanSpacedWordsFixer {
        @NotNull
        @Override
        public String fixWord(@NotNull String word) {
            return StringUtil.capitalize(word.toLowerCase());
        }
    }

    public static class LowerSpacedWordsFixer extends CleanSpacedWordsFixer {
        @NotNull
        @Override
        public String fixWord(@NotNull String word) {
            return word.toLowerCase();
        }
    }

    public static class UpperSpacedWordsFixer extends CleanSpacedWordsFixer {
        @NotNull
        @Override
        public String fixWord(@NotNull String word) {
            return word.toUpperCase();
        }
    }

    public static class CleanSplicedWordsFixer extends CleanSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "";
        }
    }

    public static class CapSplicedWordsFixer extends CapSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "";
        }
    }

    public static class LowerSplicedWordsFixer extends LowerSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "";
        }
    }

    public static class UpperSplicedWordsFixer extends UpperSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "";
        }
    }

    public static class CleanDashedWordsFixer extends CleanSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "-";
        }
    }

    public static class CapDashedWordsFixer extends CapSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "-";
        }
    }

    public static class LowerDashedWordsFixer extends LowerSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "-";
        }
    }

    public static class UpperDashedWordsFixer extends UpperSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "-";
        }
    }

    public static class CleanSnakedWordsFixer extends CleanSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "_";
        }
    }

    public static class CapSnakedWordsFixer extends CapSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "_";
        }
    }

    public static class LowerSnakedWordsFixer extends LowerSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "_";
        }
    }

    public static class UpperSnakedWordsFixer extends UpperSpacedWordsFixer {
        @NotNull
        @Override
        public String getWordSpacer() {
            return "_";
        }
    }

    public static boolean isAlphaNum(@NotNull String word) {
        int iMax = word.length();
        for (int i = 0; i < iMax; i++) {
            if (!Character.isLetterOrDigit(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public static String fixSuggestion(@NotNull String suggestion, @NotNull String remove, @NotNull String pad) {// replace all unacceptables with a space
        int iMax = suggestion.length();
        remove += pad;
        StringBuilder newSuggestion = new StringBuilder(suggestion.length());
        boolean lastWasPad = false;
        for (int i = 0; i < iMax; i++) {
            if (remove.indexOf(suggestion.charAt(i)) >= 0) {
                if (newSuggestion.length() > 0 && newSuggestion.charAt(newSuggestion.length() - 1) != ' ') {
                    newSuggestion.append(pad);
                    lastWasPad = true;
                }
                continue;
            }
            newSuggestion.append(suggestion.charAt(i));
            lastWasPad = false;
        }
        if (lastWasPad && pad.length() > 0) {
            newSuggestion.setLength(newSuggestion.length() - pad.length());
        }
        suggestion = newSuggestion.toString();
        return suggestion;
    }

    @NotNull
    protected static List<String> getSuggestions(SpellCheckerManager manager, @NotNull String text) {
        String[] words = NameUtil.nameToWords(text);

        int index = 0;
        List[] res = new List[words.length];
        int i = 0;
        for (String word : words) {
            if (word == null || word.isEmpty()) continue;

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
