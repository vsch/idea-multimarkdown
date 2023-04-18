// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.spellchecking;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SuggestionList {

    final protected @NotNull ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
    final protected @NotNull HashSet<String> suggestionSet = new HashSet<String>();
    final protected @Nullable Project project;
    final public static SuggestionList EMPTY_LIST = new SuggestionList();

    public SuggestionList(@Nullable Project project) {
        this.project = project;
    }

    public SuggestionList() {
        project = null;
    }

    public SuggestionList(@NotNull SuggestionList other) {
        this(other.project);
        add(other);
    }

    public int size() {
        return suggestions.size();
    }

    public SuggestionList add(Suggestion... suggestions) {
        for (Suggestion suggestion : suggestions) {
            String suggestionText = suggestion.getText();

            if (!this.suggestionSet.contains(suggestionText)) {
                this.suggestions.add(suggestion);
                this.suggestionSet.add(suggestionText);
            }
        }
        return this;
    }

    public SuggestionList add(SuggestionList... suggestionLists) {
        for (SuggestionList suggestionList : suggestionLists) {
            for (Suggestion suggestion : suggestionList.suggestions) {
                String suggestionText = suggestion.getText();

                if (!this.suggestionSet.contains(suggestionText)) {
                    this.suggestions.add(suggestion);
                    this.suggestionSet.add(suggestionText);
                }
            }
        }
        return this;
    }

    public SuggestionList addAll(String... suggestionTexts) {
        for (String suggestionText : suggestionTexts) {
            if (suggestionText != null && !this.suggestionSet.contains(suggestionText)) {
                this.suggestions.add(new Suggestion(suggestionText));
                this.suggestionSet.add(suggestionText);
            }
        }
        return this;
    }

    public SuggestionList add(@Nullable String suggestionText) {
        if (suggestionText != null && !this.suggestionSet.contains(suggestionText)) {
            this.suggestions.add(new Suggestion(suggestionText));
            this.suggestionSet.add(suggestionText);
        }
        return this;
    }

    public SuggestionList add(@Nullable String suggestionText, Suggestion... sourceSuggestions) {
        if (suggestionText != null && !this.suggestionSet.contains(suggestionText)) {
            this.suggestions.add(new Suggestion(suggestionText, sourceSuggestions));
            this.suggestionSet.add(suggestionText);
        }
        return this;
    }

    public SuggestionList add(@Nullable String suggestionText, @NotNull Suggestion.Param param, Suggestion... sourceSuggestions) {
        if (suggestionText != null && !this.suggestionSet.contains(suggestionText)) {
            this.suggestions.add(new Suggestion(suggestionText, param, sourceSuggestions));
            this.suggestionSet.add(suggestionText);
        }
        return this;
    }

    @NotNull
    public List<String> asList() {
        ArrayList<String> list = new ArrayList<String>();

        for (Suggestion suggestion : suggestions) {
            list.add(suggestion.getText());
        }

        return list;
    }

    @NotNull
    public ArrayList<Suggestion> getSuggestions() {
        return suggestions;
    }

    @Nullable
    protected SuggestionList chainFixers(int index, SuggestionList inList, Suggestion.Fixer... fixers) {
        Suggestion.Fixer fixer;

        do {
            if (fixers.length <= index) {
                return inList;
            }
            fixer = fixers[index++];
        }
        while (fixer == null);

        SuggestionList resultList = new SuggestionList(this.project);

        for (Suggestion suggestion : inList.getSuggestions()) {
            if (suggestion.isEmpty()) continue;

            SuggestionList outList = fixer.fix(suggestion, project);

            if (outList != null) {
                outList = chainFixers(index, outList, fixers);
                if (outList != null) {
                    resultList.add(outList);
                }
            }
        }

        return resultList;
    }

    @NotNull
    public SuggestionList chainFixers(Suggestion.Fixer... fixers) {
        SuggestionList suggestionList = chainFixers(0, this, fixers);
        return suggestionList == null ? new SuggestionList(this.project) : suggestionList;
    }

    @NotNull
    public SuggestionList batchFixers(Suggestion.Fixer... fixers) {
        SuggestionList result = new SuggestionList(this.project);

        for (Suggestion.Fixer fixer : fixers) {
            if (fixer == null) continue;

            for (Suggestion suggestion : getSuggestions()) {
                if (suggestion.isEmpty()) continue;

                SuggestionList fixedList = fixer.fix(suggestion, project);
                if (fixedList != null) result.add(fixedList);
            }
        }

        return result;
    }

    @NotNull
    public SuggestionList sequenceFixers(Suggestion.Fixer... fixers) {
        SuggestionList result = new SuggestionList(this.project);

        for (Suggestion suggestion : getSuggestions()) {
            if (suggestion.isEmpty()) continue;

            for (Suggestion.Fixer fixer : fixers) {
                if (fixer == null) continue;
                SuggestionList fixedList = fixer.fix(suggestion, project);
                if (fixedList != null) result.add(fixedList);
            }
        }

        return result;
    }

    @NotNull
    public SuggestionList prefix(@Nullable String prefix) {
        return wrap(prefix, null);
    }

    @NotNull
    public SuggestionList suffix(@Nullable String suffix) {
        return wrap(null, suffix);
    }

    @NotNull
    public SuggestionList prefixAlign(@Nullable SuggestionList prefixes) {
        return wrapAlign(prefixes, null);
    }

    @NotNull
    public SuggestionList suffixAlign(@Nullable SuggestionList suffixes) {
        return wrapAlign(null, suffixes);
    }

    @NotNull
    public SuggestionList prefixPermute(@Nullable SuggestionList prefixes) {
        return wrapPermute(prefixes, null);
    }

    @NotNull
    public SuggestionList suffixPermute(@Nullable SuggestionList suffixes) {
        return wrapPermute(null, suffixes);
    }

    // TEST: needs a test
    @NotNull
    public SuggestionList wrap(@Nullable String prefix, @Nullable String suffix) {
        SuggestionList prefixedList;

        if ((prefix == null || prefix.isEmpty()) && (suffix == null || suffix.isEmpty())) {
            prefixedList = new SuggestionList(this);
        } else {
            prefixedList = new SuggestionList(this.project);
            if (prefix == null) prefix = "";
            if (suffix == null) suffix = "";
            for (Suggestion suggestion : suggestions) {
                prefixedList.add(prefix + suggestion.getText() + suffix, suggestion);
            }
        }
        return prefixedList;
    }

    public boolean isEmpty() {
        return suggestions.isEmpty();
    }

    // TEST: needs a test
    @NotNull
    public SuggestionList wrapPermute(@Nullable SuggestionList prefixes, @Nullable SuggestionList suffixes) {
        SuggestionList wrappedList = new SuggestionList(project);

        if (prefixes != null && !prefixes.isEmpty() && suffixes != null && !suffixes.isEmpty()) {
            for (Suggestion prefix : prefixes.suggestions) {
                for (Suggestion suffix : suffixes.suggestions) {
                    for (Suggestion suggestion : suggestions) {
                        wrappedList.add(prefix.getText() + suggestion.getText() + suffix.getText(), prefix, suffix);
                    }
                }
            }
        } else if (suffixes != null && !suffixes.isEmpty()) {
            for (Suggestion suffix : suffixes.suggestions) {
                for (Suggestion suggestion : suggestions) {
                    wrappedList.add(suggestion.getText() + suffix.getText(), suffix);
                }
            }
        } else if (prefixes != null && !prefixes.isEmpty()) {
            for (Suggestion prefix : prefixes.suggestions) {
                for (Suggestion suggestion : suggestions) {
                    wrappedList.add(prefix.getText() + suggestion.getText(), prefix);
                }
            }
        }
        return wrappedList;
    }

    // TEST: needs a test
    @NotNull
    public SuggestionList wrapAlign(@Nullable SuggestionList prefixes, @Nullable SuggestionList suffixes) {
        SuggestionList wrappedList = new SuggestionList(project);

        if (prefixes != null && !prefixes.isEmpty() && suffixes != null && !suffixes.isEmpty()) {
            int iMax = Math.min(Math.min(prefixes.size(), suffixes.size()), size());

            for (int i = 0; i < iMax; i++) {
                Suggestion suggestion = suggestions.get(i);
                Suggestion prefix = prefixes.suggestions.get(i);
                Suggestion suffix = suffixes.suggestions.get(i);
                wrappedList.add(prefix.getText() + suggestion.getText() + suffix.getText(), prefix, suggestion, suffix);
            }
        } else if (suffixes != null && !suffixes.isEmpty()) {
            int iMax = Math.min(suffixes.size(), size());

            for (int i = 0; i < iMax; i++) {
                Suggestion suggestion = suggestions.get(i);
                Suggestion suffix = suffixes.suggestions.get(i);
                wrappedList.add(suggestion.getText() + suffix.getText(), suggestion, suffix);
            }
        } else if (prefixes != null && !prefixes.isEmpty()) {
            int iMax = Math.min(prefixes.size(), size());

            for (int i = 0; i < iMax; i++) {
                Suggestion suggestion = suggestions.get(i);
                Suggestion prefix = prefixes.suggestions.get(i);
                wrappedList.add(prefix.getText() + suggestion.getText(), prefix, suggestion);
            }
        }
        return wrappedList;
    }

    // TEST: needs a test
    /*
        returns a list that permutes suggestions from this list, prefixed and suffixed.
        filters applied to prefixes and suffixes and permutations happen on the fixed prefix and suffixed lists by applying a single fixer to both lists and then permuting. That way permutations happen on items returned by the same fixer type.
     */
    @NotNull
    public SuggestionList wrapPermuteFixedAligned(@Nullable SuggestionList prefixes, @Nullable SuggestionList suffixes, Suggestion.Fixer... fixers) {
        SuggestionList wrappedList = new SuggestionList(project);

        if (prefixes != null && !prefixes.isEmpty() && suffixes != null && !suffixes.isEmpty()) {
            for (Suggestion.Fixer fixer : fixers) {
                SuggestionList fixedPrefixes = prefixes.batchFixers(fixer);
                SuggestionList fixedSuffixes = suffixes.batchFixers(fixer);

                wrappedList.add(wrapPermute(fixedPrefixes, fixedSuffixes));
            }
        } else if (suffixes != null && !suffixes.isEmpty()) {
            for (Suggestion.Fixer fixer : fixers) {
                SuggestionList fixedSuffixes = suffixes.batchFixers(fixer);
                wrappedList.add(wrapPermute(null, fixedSuffixes));
            }
        } else if (prefixes != null && !prefixes.isEmpty()) {
            for (Suggestion.Fixer fixer : fixers) {
                SuggestionList fixedPrefixes = prefixes.batchFixers(fixer);
                wrappedList.add(wrapPermute(fixedPrefixes, null));
            }
        }
        return wrappedList;
    }

    @Nullable
    public Project getProject() {
        return project;
    }

    @NotNull
    public String get(int i) {
        Suggestion suggestion = suggestions.get(i);
        return suggestion != null ? suggestion.getText() : "";
    }
}
