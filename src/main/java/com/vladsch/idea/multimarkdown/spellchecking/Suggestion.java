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
import com.intellij.psi.codeStyle.NameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class Suggestion {
    public interface Fixer {
        String NEEDS_SPELLING_FIXER = "NeedsSpellingFixer";
        String HAD_SPELLING_FIXER = "HadSpellingFixer";
        String FILE_PATH = "FilePath";

        @Nullable
        SuggestionList fix(final @NotNull Suggestion suggestion, final Project project);
    }

    public static class Param<T> {
        public final String key;
        public final T value;

        public Param(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }

    protected final String text;
    protected final HashMap<String, Param> params = new HashMap<String, Param>();

    public Param[] paramsArray() {
        Collection<Param> values = params.values();
        return values.toArray(new Param[values.size()]);
    }

    public boolean hasParam(@NotNull String key) {
        return params.containsKey(key);
    }

    public boolean hasParams() {
        return !params.isEmpty();
    }

    public boolean addParam(Param param) {
        if (params.containsKey(param.key)) return false;
        params.put(param.key, param);
        return true;
    }

    @Nullable
    protected Param getRawParam(@NotNull String key) {
        if (!params.containsKey(key)) return null;
        return params.get(key);
    }

    @Nullable
    public Object getParam(@NotNull String key) {
        if (!params.containsKey(key)) return null;
        return params.get(key).value;
    }

    public boolean boolParam(@NotNull String key) {
        if (!params.containsKey(key)) return false;
        Param param = params.get(key);
        return param.value instanceof Boolean && (Boolean) param.value;
    }

    public int intParam(@NotNull String key) {
        if (!params.containsKey(key)) return 0;
        Param param = params.get(key);
        return param.value instanceof Integer ? (Integer) param.value : 0;
    }

    @NotNull
    public String stringParam(@NotNull String key) {
        if (!params.containsKey(key)) return "";
        Param param = params.get(key);
        return param.value instanceof String ? (String) param.value : "";
    }

    protected static void copyParams(HashMap<String, Param> params, Suggestion... sourceSuggestions) {
        int iMax = sourceSuggestions.length;

        for (int i = iMax; i-- > 0; ) {
            if (sourceSuggestions[i] != null) params.putAll(sourceSuggestions[i].params);
        }
    }

    public Suggestion(@NotNull String text, @NotNull Param param, Suggestion... sourceSuggestions) {
        this.text = text;
        copyParams(this.params, sourceSuggestions);
        this.params.put(param.key, param);
    }

    public Suggestion(@NotNull String text, Suggestion... sourceSuggestions) {
        this.text = text;
        copyParams(this.params, sourceSuggestions);
    }

    public Suggestion(@NotNull String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String[] getWords() {
        return NameUtil.nameToWords(text);
    }

    public boolean isEmpty() {
        return text.trim().isEmpty();
    }

    public SuggestionList chainFixers(Project project, Fixer... fixers) {
        SuggestionList suggestionList = new SuggestionList(project);
        suggestionList.add(this);
        return suggestionList.chainFixers(fixers);
    }

    public SuggestionList batchFixers(Project project, Fixer... fixers) {
        SuggestionList suggestionList = new SuggestionList(project);
        suggestionList.add(this);
        return suggestionList.batchFixers(fixers);
    }

    public SuggestionList sequenceFixers(Project project, Fixer... fixers) {
        SuggestionList suggestionList = new SuggestionList(project);
        suggestionList.add(this);
        return suggestionList.sequenceFixers(fixers);
    }
}
