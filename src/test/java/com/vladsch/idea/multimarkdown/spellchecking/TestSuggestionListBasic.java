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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.spellchecking;

import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import static com.vladsch.idea.multimarkdown.TestUtils.*;
import static org.junit.Assert.assertEquals;

public class TestSuggestionListBasic {

    public static final String TEST_TEXT = "test text";
    public static final String TEST_TEXT_1 = "test text 1";
    public static final String TEST_TEXT_2 = "test text 2";
    public static final String TEST_TEXT_3 = "test text 3";
    public static final String PARAM_NAME = "paramName";
    public static final String PARAM_NAME_1 = "paramName1";
    public static final String PARAM_NAME_2 = "paramName2";
    public static final String PARAM_NAME_3 = "paramName3";

    @Test
    public void testNewSimple() {
        SuggestionList suggestionList = new SuggestionList();
        assertEquals(0, suggestionList.size());
    }

    @Test
    public void testAddSuggestion() {
        SuggestionList suggestionList = new SuggestionList();
        assertEquals(0, suggestionList.size());

        Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(PARAM_NAME, true);
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        suggestionList.add(suggestion);

        assertSuggestionListHasSuggestions(suggestionList, suggestion);
    }

    @Test(expected = ComparisonFailure.class)
    public void testAddSuggestionFailure() {
        SuggestionList suggestionList = new SuggestionList();
        assertEquals(0, suggestionList.size());

        Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(PARAM_NAME, true);
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        Suggestion suggestion1 = new Suggestion(TEST_TEXT);
        suggestionList.add(suggestion);

        assertSuggestionListHasSuggestions(suggestionList, suggestion1);
    }

    @Test
    public void testAddSuggestionsIndividually() {
        SuggestionList suggestionList = new SuggestionList();
        assertEquals(0, suggestionList.size());

        Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(PARAM_NAME, true);
        Suggestion.Param<String> param1 = new Suggestion.Param<String>(PARAM_NAME_1, TEST_TEXT_1);
        Suggestion.Param<Integer> param2 = new Suggestion.Param<Integer>(PARAM_NAME_2, (int)(Math.random()*100000));
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        Suggestion suggestion1 = new Suggestion(TEST_TEXT_1, param1);
        Suggestion suggestion2 = new Suggestion(TEST_TEXT_2, param1);
        suggestionList.add(suggestion);
        suggestionList.add(suggestion1);
        suggestionList.add(suggestion2);

        assertSuggestionListHasSuggestions(suggestionList, suggestion, suggestion1, suggestion2);
    }

    @Test
    public void testAddSuggestionsBatched() {
        SuggestionList suggestionList = new SuggestionList();
        assertEquals(0, suggestionList.size());

        Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(PARAM_NAME, true);
        Suggestion.Param<String> param1 = new Suggestion.Param<String>(PARAM_NAME_1, TEST_TEXT_1);
        Suggestion.Param<Integer> param2 = new Suggestion.Param<Integer>(PARAM_NAME_2, (int)(Math.random()*100000));
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        Suggestion suggestion1 = new Suggestion(TEST_TEXT_1, param1);
        Suggestion suggestion2 = new Suggestion(TEST_TEXT_2, param1);
        suggestionList.add(suggestion, suggestion1, suggestion2);

        assertSuggestionListHasSuggestions(suggestionList, suggestion, suggestion1, suggestion2);
    }

    @Test
    public void testAddSuggestionsList() {
        SuggestionList suggestionList = new SuggestionList();
        assertEquals(0, suggestionList.size());

        Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(PARAM_NAME, true);
        Suggestion.Param<String> param1 = new Suggestion.Param<String>(PARAM_NAME_1, TEST_TEXT_1);
        Suggestion.Param<Integer> param2 = new Suggestion.Param<Integer>(PARAM_NAME_2, (int)(Math.random()*100000));
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        Suggestion suggestion1 = new Suggestion(TEST_TEXT_1, param1);
        Suggestion suggestion2 = new Suggestion(TEST_TEXT_2, param1);
        suggestionList.add(suggestion, suggestion1, suggestion2);

        SuggestionList suggestionList2 = new SuggestionList();
        suggestionList2.add(suggestionList);

        compareOrderedLists(null, suggestionList, suggestionList2);
    }

    @Test
    public void testAsList() {
        SuggestionList suggestionList = new SuggestionList();
        assertEquals(0, suggestionList.size());

        Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(PARAM_NAME, true);
        Suggestion.Param<String> param1 = new Suggestion.Param<String>(PARAM_NAME_1, TEST_TEXT_1);
        Suggestion.Param<Integer> param2 = new Suggestion.Param<Integer>(PARAM_NAME_2, (int)(Math.random()*100000));
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        Suggestion suggestion1 = new Suggestion(TEST_TEXT_1, param1);
        Suggestion suggestion2 = new Suggestion(TEST_TEXT_2, param1);
        suggestionList.add(suggestion, suggestion1, suggestion2);
        assertSuggestionListHasSuggestions(suggestionList,suggestion, suggestion1, suggestion2);

        compareOrderedLists(null, new String[]{TEST_TEXT, TEST_TEXT_1, TEST_TEXT_2}, suggestionList.asList().toArray(new String[0]));
    }

}
