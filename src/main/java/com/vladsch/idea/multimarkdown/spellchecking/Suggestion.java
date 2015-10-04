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

import com.intellij.psi.codeStyle.NameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Suggestion {
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

    public boolean hasParam(@NotNull String key) {
        return params.containsKey(key);
    }

    @Nullable
    public Param getParam(@NotNull String key) {
        if (!params.containsKey(key)) return null;
        return params.get(key);
    }

    public boolean boolParam(@NotNull String key) {
        if (!params.containsKey(key)) return false;
        Param param = params.get(key);
        return param.value instanceof Boolean && (Boolean)param.value;
    }

    public int intParam(@NotNull String key) {
        if (!params.containsKey(key)) return 0;
        Param param = params.get(key);
        return param.value instanceof Integer ? (Integer)param.value : 0;
    }

    @NotNull
    public String stringParam(@NotNull String key) {
        if (!params.containsKey(key)) return "";
        Param param = params.get(key);
        return param.value instanceof String ? (String)param.value : "";
    }

    public Suggestion(@NotNull String text, @NotNull Param param, Suggestion... sourceSuggestions) {
        this.text = text;
        for (Suggestion suggestion : sourceSuggestions) {
            this.params.putAll(suggestion.params);
        }
        this.params.put(param.key, param);
    }

    public Suggestion(@NotNull String text, @NotNull Suggestion sourceSuggestion) {
        this.text = text;
        this.params.putAll(sourceSuggestion.params);
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
}
