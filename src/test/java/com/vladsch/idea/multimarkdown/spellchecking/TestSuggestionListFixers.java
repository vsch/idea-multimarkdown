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
import org.junit.Test;

import static com.vladsch.idea.multimarkdown.TestUtils.compareOrderedLists;

public class TestSuggestionListFixers {

    public static final String TEST_TEXT = "test text";
    public static final String TEST_TEXT_1 = "test text 1";
    public static final String TEST_TEXT_2 = "test text 2";
    public static final String TEST_TEXT_3 = "test text 3";
    public static final String PARAM_NAME = "paramName";
    public static final String PARAM_NAME_1 = "paramName1";
    public static final String PARAM_NAME_2 = "paramName2";
    public static final String PARAM_NAME_3 = "paramName3";

    private static Suggestion.Fixer fixer1 = new SuggestionFixers.FixerBase() {
        @Override
        public void makeSuggestions(@NotNull String text, @NotNull Suggestion suggestion, Project project) {
            addSuggestion(text + ".1");
        }
    };

    private static Suggestion.Fixer fixer2 = new SuggestionFixers.FixerBase() {
        @Override
        public void makeSuggestions(@NotNull String text, @NotNull Suggestion suggestion, Project project) {
            addSuggestion(text + ".2");
        }
    };

    private static Suggestion.Fixer fixer3 = new SuggestionFixers.FixerBase() {
        @Override
        public void makeSuggestions(@NotNull String text, @NotNull Suggestion suggestion, Project project) {
            addSuggestion(text + ".3");
        }
    };

    private static Suggestion.Fixer nullFixer = new Suggestion.Fixer() {
        @Nullable
        @Override
        public SuggestionList fix(@NotNull Suggestion suggestion, Project project) {
            return null;
        }
    };

    @Test
    public void testBatchSingleFixer() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);
        SuggestionList fixed = suggestionList.batchFixers(fixer1);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1");
        expected.add(TEST_TEXT_2 + ".1");
        expected.add(TEST_TEXT_3 + ".1");

        compareOrderedLists(null, expected, fixed);
    }

    @Test
    public void testSequenceSingleFixer() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);
        SuggestionList fixed = suggestionList.sequenceFixers(fixer1);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1");
        expected.add(TEST_TEXT_2 + ".1");
        expected.add(TEST_TEXT_3 + ".1");

        compareOrderedLists(null, expected, fixed);
    }

    @Test
    public void testChainSingleFixer() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);
        SuggestionList fixed = suggestionList.chainFixers(fixer1);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1");
        expected.add(TEST_TEXT_2 + ".1");
        expected.add(TEST_TEXT_3 + ".1");

        compareOrderedLists(null, expected, fixed);
    }

    @Test
    public void testBatchFixers() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);
        SuggestionList fixed = suggestionList.batchFixers(fixer1, fixer2, fixer3);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1");
        expected.add(TEST_TEXT_2 + ".1");
        expected.add(TEST_TEXT_3 + ".1");
        expected.add(TEST_TEXT_1 + ".2");
        expected.add(TEST_TEXT_2 + ".2");
        expected.add(TEST_TEXT_3 + ".2");
        expected.add(TEST_TEXT_1 + ".3");
        expected.add(TEST_TEXT_2 + ".3");
        expected.add(TEST_TEXT_3 + ".3");

        compareOrderedLists(null, expected, fixed);
    }

    @Test
    public void testSequenceFixer() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);
        SuggestionList fixed = suggestionList.sequenceFixers(fixer1, fixer2, fixer3);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1");
        expected.add(TEST_TEXT_1 + ".2");
        expected.add(TEST_TEXT_1 + ".3");
        expected.add(TEST_TEXT_2 + ".1");
        expected.add(TEST_TEXT_2 + ".2");
        expected.add(TEST_TEXT_2 + ".3");
        expected.add(TEST_TEXT_3 + ".1");
        expected.add(TEST_TEXT_3 + ".2");
        expected.add(TEST_TEXT_3 + ".3");

        compareOrderedLists(null, expected, fixed);
    }

    @Test
    public void testChainFixer() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);
        SuggestionList fixed = suggestionList.chainFixers(fixer1,fixer2,fixer3);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1.2.3");
        expected.add(TEST_TEXT_2 + ".1.2.3");
        expected.add(TEST_TEXT_3 + ".1.2.3");

        compareOrderedLists(null, expected, fixed);
    }

    @Test
    public void testNullBatchFixers() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1");
        expected.add(TEST_TEXT_2 + ".1");
        expected.add(TEST_TEXT_3 + ".1");
        expected.add(TEST_TEXT_1 + ".2");
        expected.add(TEST_TEXT_2 + ".2");
        expected.add(TEST_TEXT_3 + ".2");
        expected.add(TEST_TEXT_1 + ".3");
        expected.add(TEST_TEXT_2 + ".3");
        expected.add(TEST_TEXT_3 + ".3");

        SuggestionList fixed = suggestionList.batchFixers(null, fixer1, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed);
        SuggestionList fixed1 = suggestionList.batchFixers(fixer1, null, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed1);
        SuggestionList fixed2 = suggestionList.batchFixers(fixer1, fixer2, null, fixer3);
        compareOrderedLists(null, expected, fixed2);
        SuggestionList fixed3 = suggestionList.batchFixers(fixer1, fixer2, fixer3, null);
        compareOrderedLists(null, expected, fixed3);
    }

    @Test
    public void testNullSequenceFixer() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1");
        expected.add(TEST_TEXT_1 + ".2");
        expected.add(TEST_TEXT_1 + ".3");
        expected.add(TEST_TEXT_2 + ".1");
        expected.add(TEST_TEXT_2 + ".2");
        expected.add(TEST_TEXT_2 + ".3");
        expected.add(TEST_TEXT_3 + ".1");
        expected.add(TEST_TEXT_3 + ".2");
        expected.add(TEST_TEXT_3 + ".3");

        SuggestionList fixed = suggestionList.sequenceFixers(null, fixer1, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed);
        SuggestionList fixed1 = suggestionList.sequenceFixers(fixer1, null, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed1);
        SuggestionList fixed2 = suggestionList.sequenceFixers(fixer1, fixer2, null, fixer3);
        compareOrderedLists(null, expected, fixed2);
        SuggestionList fixed3 = suggestionList.sequenceFixers(fixer1, fixer2, fixer3, null);
        compareOrderedLists(null, expected, fixed3);
    }

    @Test
    public void testNullChainFixer() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1.2.3");
        expected.add(TEST_TEXT_2 + ".1.2.3");
        expected.add(TEST_TEXT_3 + ".1.2.3");

        SuggestionList fixed = suggestionList.chainFixers(null, fixer1, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed);
        SuggestionList fixed1 = suggestionList.chainFixers(fixer1, null, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed1);
        SuggestionList fixed2 = suggestionList.chainFixers(fixer1, fixer2, null, fixer3);
        compareOrderedLists(null, expected, fixed2);
        SuggestionList fixed3 = suggestionList.chainFixers(fixer1, fixer2, fixer3, null);
        compareOrderedLists(null, expected, fixed3);
    }
    @Test
    public void testNullFixerBatchFixers() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1");
        expected.add(TEST_TEXT_2 + ".1");
        expected.add(TEST_TEXT_3 + ".1");
        expected.add(TEST_TEXT_1 + ".2");
        expected.add(TEST_TEXT_2 + ".2");
        expected.add(TEST_TEXT_3 + ".2");
        expected.add(TEST_TEXT_1 + ".3");
        expected.add(TEST_TEXT_2 + ".3");
        expected.add(TEST_TEXT_3 + ".3");

        SuggestionList fixed = suggestionList.batchFixers(nullFixer, fixer1, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed);
        SuggestionList fixed1 = suggestionList.batchFixers(fixer1, nullFixer, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed1);
        SuggestionList fixed2 = suggestionList.batchFixers(fixer1, fixer2, nullFixer, fixer3);
        compareOrderedLists(null, expected, fixed2);
        SuggestionList fixed3 = suggestionList.batchFixers(fixer1, fixer2, fixer3, nullFixer);
        compareOrderedLists(null, expected, fixed3);
    }

    @Test
    public void testNullFixerSequenceFixer() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);

        SuggestionList expected = new SuggestionList();
        expected.add(TEST_TEXT_1 + ".1");
        expected.add(TEST_TEXT_1 + ".2");
        expected.add(TEST_TEXT_1 + ".3");
        expected.add(TEST_TEXT_2 + ".1");
        expected.add(TEST_TEXT_2 + ".2");
        expected.add(TEST_TEXT_2 + ".3");
        expected.add(TEST_TEXT_3 + ".1");
        expected.add(TEST_TEXT_3 + ".2");
        expected.add(TEST_TEXT_3 + ".3");

        SuggestionList fixed = suggestionList.sequenceFixers(nullFixer, fixer1, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed);
        SuggestionList fixed1 = suggestionList.sequenceFixers(fixer1, nullFixer, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed1);
        SuggestionList fixed2 = suggestionList.sequenceFixers(fixer1, fixer2, nullFixer, fixer3);
        compareOrderedLists(null, expected, fixed2);
        SuggestionList fixed3 = suggestionList.sequenceFixers(fixer1, fixer2, fixer3, nullFixer);
        compareOrderedLists(null, expected, fixed3);
    }

    @Test
    public void testNullFixerChainFixer() {
        SuggestionList suggestionList = new SuggestionList();
        suggestionList.add(TEST_TEXT_1);
        suggestionList.add(TEST_TEXT_2);
        suggestionList.add(TEST_TEXT_3);

        SuggestionList expected = new SuggestionList();
        //expected.add(TEST_TEXT_1 + ".1.2.3");
        //expected.add(TEST_TEXT_2 + ".1.2.3");
        //expected.add(TEST_TEXT_3 + ".1.2.3");

        SuggestionList fixed = suggestionList.chainFixers(nullFixer, fixer1, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed);
        SuggestionList fixed1 = suggestionList.chainFixers(fixer1, nullFixer, fixer2, fixer3);
        compareOrderedLists(null, expected, fixed1);
        SuggestionList fixed2 = suggestionList.chainFixers(fixer1, fixer2, nullFixer, fixer3);
        compareOrderedLists(null, expected, fixed2);
        SuggestionList fixed3 = suggestionList.chainFixers(fixer1, fixer2, fixer3, nullFixer);
        compareOrderedLists(null, expected, fixed3);
    }
}
