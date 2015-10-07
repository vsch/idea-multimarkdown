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

public class SuggestionListFail {
    class Suggestion {

    }

    final protected @NotNull ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
    final protected @NotNull HashSet<String> suggestionSet = new HashSet<String>();

    public SuggestionListFail() {
    }

    public boolean isEmpty() {
        return suggestions.isEmpty();
    }

    @NotNull
    public SuggestionListFail wrap(@Nullable SuggestionListFail prefixes, @Nullable SuggestionListFail suffixes) {
        SuggestionListFail wrappedList = new SuggestionListFail();

        if ((prefixes == null || prefixes.isEmpty()) && (suffixes == null || suffixes.isEmpty())) {
        } else if (prefixes == null || prefixes.isEmpty()) {
            for (Suggestion suffix : suffixes.suggestions) {
                for (Suggestion suggestion : suggestions) {
                }
            }
        } else if (suffixes == null || suffixes.isEmpty()) {
            for (Suggestion prefix : prefixes.suggestions) {
                for (Suggestion suggestion : suggestions) {
                }
            }
        } else {
            for (Suggestion prefix : prefixes.suggestions) {
                for (Suggestion suffix : suffixes.suggestions) {
                    for (Suggestion suggestion : suggestions) {
                    }
                }
            }
        }
        return wrappedList;
    }
}
