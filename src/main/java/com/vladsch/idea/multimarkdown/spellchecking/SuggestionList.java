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
package com.vladsch.idea.multimarkdown.spellchecking;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SuggestionList {

    public interface SuggestionFixer {
        String NEEDS_SPELLING_FIXER = "NeedsSpellingFixer";
        String HAD_SPELLING_FIXER = "HadSpellingFixer";

        @Nullable
        SuggestionList fix(final @NotNull Suggestion suggestion, final Project project);
    }

    protected ArrayList<Suggestion> suggestions = null;
    protected HashSet<String> suggestionSet = null;
    protected final Project project;

    public SuggestionList(@Nullable Project project) {
        this.suggestions = new ArrayList<Suggestion>();
        this.suggestionSet = new HashSet<String>();
        this.project = project;
    }

    public SuggestionList() {
        this((Project) null);
    }

    public SuggestionList(@NotNull SuggestionList other) {
        this(other.project);

        add(other);
    }

    public int size() {
        return suggestions.size();
    }

    public void add(Suggestion... suggestions) {
        for (Suggestion suggestion : suggestions) {
            String suggestionText = suggestion.getText();

            if (!this.suggestionSet.contains(suggestionText)) {
                this.suggestions.add(suggestion);
                this.suggestionSet.add(suggestionText);
            }
        }
    }

    public void add(@NotNull SuggestionList suggestionList) {
        for (Suggestion suggestion : suggestionList.suggestions) {
            String suggestionText = suggestion.getText();

            if (!this.suggestionSet.contains(suggestionText)) {
                this.suggestions.add(suggestion);
                this.suggestionSet.add(suggestionText);
            }
        }
    }

    public void add(@NotNull String suggestionText) {
        if (!this.suggestionSet.contains(suggestionText)) {
            this.suggestions.add(new Suggestion(suggestionText));
            this.suggestionSet.add(suggestionText);
        }
    }

    public void add(@NotNull String suggestionText, Suggestion... sourceSuggestions) {
        if (!this.suggestionSet.contains(suggestionText)) {
            this.suggestions.add(new Suggestion(suggestionText, sourceSuggestions));
            this.suggestionSet.add(suggestionText);
        }
    }

    public void add(@NotNull String suggestionText, @NotNull Suggestion.Param param, Suggestion... sourceSuggestions) {
        if (!this.suggestionSet.contains(suggestionText)) {
            this.suggestions.add(new Suggestion(suggestionText, param, sourceSuggestions));
            this.suggestionSet.add(suggestionText);
        }
    }

    @NotNull
    public List<String> asList() {
        ArrayList<String> list = new ArrayList<String>();

        for (Suggestion suggestion : suggestions) {
            list.add(suggestion.getText());
        }

        return list;
    }

    public ArrayList<Suggestion> getSuggestions() {
        return suggestions;
    }

    @Nullable
    protected SuggestionList chainFixers(int index, SuggestionList inList, SuggestionFixer... fixers) {
        SuggestionFixer fixer;

        do {
            if (fixers.length <= index) {
                return inList;
            }
            fixer = fixers[index++];
        }
        while (fixer == null);

        SuggestionList resultList = new SuggestionList();

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

    @Nullable
    public SuggestionList chainFixers(SuggestionFixer... fixers) {
        return chainFixers(0, this, fixers);
    }

    @NotNull
    public SuggestionList batchFixers(SuggestionFixer... fixers) {
        SuggestionList result = new SuggestionList();

        for (SuggestionFixer fixer : fixers) {
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
    public SuggestionList sequenceFixers(SuggestionFixer... fixers) {
        SuggestionList result = new SuggestionList();

        for (Suggestion suggestion : getSuggestions()) {
            if (suggestion.isEmpty()) continue;

            for (SuggestionFixer fixer : fixers) {
                if (fixer == null) continue;
                SuggestionList fixedList = fixer.fix(suggestion, project);
                if (fixedList != null) result.add(fixedList);
            }
        }

        return result;
    }
}
