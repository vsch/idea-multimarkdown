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

import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import static com.vladsch.idea.multimarkdown.TestUtils.*;
import static org.junit.Assert.assertEquals;

public class TestSuggestionBasic {

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
        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertEquals(TEST_TEXT, suggestion.getText());
        assertSuggestionHasNoParams(suggestion);
    }

    @Test(expected = AssertionError.class)
    public void testNewSimpleFail() {
        Suggestion.Param<Integer> param = new Suggestion.Param<Integer>(PARAM_NAME, 10);
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        assertEquals(TEST_TEXT, suggestion.getText());
        assertSuggestionHasNoParams(suggestion);
    }

    @Test(expected = AssertionError.class)
    public void testNewSimpleParamValueNullFail() {
        Suggestion.Param<Integer> param = new Suggestion.Param<Integer>(PARAM_NAME, 10);
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        assertEquals(TEST_TEXT, suggestion.getText());
        assertSuggestionParamValue(PARAM_NAME, null, suggestion);
    }

    @Test(expected = AssertionError.class)
    public void testNewSimpleParamValueIntFail() {
        Suggestion.Param<Integer> param = new Suggestion.Param<Integer>(PARAM_NAME, 10);
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        assertEquals(TEST_TEXT, suggestion.getText());
        assertSuggestionParamValue(PARAM_NAME, "10", suggestion);
    }

    @Test
    public void testNewSimpleNonExistentParam() {
        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertSuggestionHasNoParams(suggestion);
        assertEquals(null, suggestion.getRawParam(PARAM_NAME));
        assertSuggestionParamValue(PARAM_NAME, null, suggestion);
    }

    @Test
    public void testNewSimpleNonExistentBoolParam() {
        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertSuggestionHasNoParams(suggestion);
        assertEquals(false, suggestion.boolParam(PARAM_NAME));
    }

    @Test
    public void testNewSimpleNonExistentIntParam() {
        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertSuggestionHasNoParams(suggestion);
        assertEquals(0, suggestion.intParam(PARAM_NAME));
    }

    @Test
    public void testNewSimpleNonExistentStringParam() {
        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertSuggestionHasNoParams(suggestion);
        assertEquals("", suggestion.stringParam(PARAM_NAME));
    }

    @Test
    public void testNewWithBooleanTrueParam() {
        Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(PARAM_NAME, true);
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        assertSuggestionHasParam(param, suggestion);
        assertSuggestionParamValue(param, suggestion);
    }

    @Test
    public void testNewWithBooleanFalseParam() {
        Suggestion.Param<Boolean> param = new Suggestion.Param<Boolean>(PARAM_NAME, false);
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        assertSuggestionHasParam(param, suggestion);
        assertSuggestionParamValue(param, suggestion);
    }

    @Test
    public void testNewWithIntParam() {
        Suggestion.Param<Integer> param = new Suggestion.Param<Integer>(PARAM_NAME, 10);
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        assertSuggestionHasParam(param, suggestion);
        assertSuggestionParamValue(param, suggestion);
    }

    @Test
    public void testNewWithStringParam() {
        Suggestion.Param<String> param = new Suggestion.Param<String>(PARAM_NAME, TEST_TEXT);
        Suggestion suggestion = new Suggestion(TEST_TEXT, param);
        assertSuggestionHasParam(param, suggestion);
        assertSuggestionParamValue(param, suggestion);
    }

    @Test
    public void testAddParam() {
        Suggestion.Param<String> param = new Suggestion.Param<String>(PARAM_NAME, TEST_TEXT);
        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertSuggestionHasNoParams(suggestion);
        assertEquals(true, suggestion.addParam(param));
        assertSuggestionParamValue(param, suggestion);
    }

    @Test
    public void testAddExistingParam() {
        Suggestion.Param<String> param = new Suggestion.Param<String>(PARAM_NAME, TEST_TEXT);
        Suggestion.Param<Boolean> param2 = new Suggestion.Param<Boolean>(PARAM_NAME, false);
        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertSuggestionHasNoParams(suggestion);
        assertEquals(true, suggestion.addParam(param));
        assertSuggestionParamValue(param, suggestion);
        assertEquals(false, suggestion.addParam(param2));
        assertSuggestionParamValue(param, suggestion);
    }

    @Test
    public void testAddParams() {
        Suggestion.Param<String> param = new Suggestion.Param<String>(PARAM_NAME, TEST_TEXT);
        Suggestion.Param<Boolean> param1 = new Suggestion.Param<Boolean>(PARAM_NAME_1, false);
        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertSuggestionHasNoParams(suggestion);
        assertEquals(true, suggestion.addParam(param));
        assertSuggestionParamValue(param, suggestion);
        assertEquals(true, suggestion.addParam(param1));
        assertSuggestionParamValue(param, suggestion);
        assertSuggestionParamValue(param1, suggestion);
    }

    @Test
    public void testNewWithInheritedParams() {
        Suggestion.Param<String> param = new Suggestion.Param<String>(PARAM_NAME, TEST_TEXT);
        Suggestion.Param<Boolean> param1 = new Suggestion.Param<Boolean>(PARAM_NAME_1, false);
        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertEquals(true, suggestion.addParam(param));
        assertEquals(true, suggestion.addParam(param1));
        Suggestion suggestion1 = new Suggestion(TEST_TEXT_1, suggestion);
        assertSuggestionParamValue(param, suggestion1);
        assertSuggestionParamValue(param1, suggestion1);
    }

    @Test
    public void testNewWithOverriddenInheritedParams() {
        Suggestion.Param<String> param = new Suggestion.Param<String>(PARAM_NAME, TEST_TEXT);
        Suggestion.Param<Boolean> param1 = new Suggestion.Param<Boolean>(PARAM_NAME_1, false);
        Suggestion.Param<Integer> param2 = new Suggestion.Param<Integer>(PARAM_NAME_1, 10);

        Suggestion suggestion = new Suggestion(TEST_TEXT);

        assertEquals(true, suggestion.addParam(param));
        assertEquals(true, suggestion.addParam(param1));

        Suggestion suggestion1 = new Suggestion(TEST_TEXT_1, param2, suggestion);
        assertSuggestionParamValue(param, suggestion1);
        assertSuggestionParamValue(param2, suggestion1);
    }

    @Test
    public void testNewWithInheritedOverriddenParams() {
        Suggestion.Param<String> param = new Suggestion.Param<String>(PARAM_NAME, TEST_TEXT);
        Suggestion.Param<Boolean> param1 = new Suggestion.Param<Boolean>(PARAM_NAME_1, false);
        Suggestion.Param<Integer> param2 = new Suggestion.Param<Integer>(PARAM_NAME_1, 10);

        Suggestion suggestion = new Suggestion(TEST_TEXT);
        assertEquals(true, suggestion.addParam(param));
        assertEquals(true, suggestion.addParam(param1));

        Suggestion suggestion1 = new Suggestion(TEST_TEXT_1, param2);
        assertSuggestionParamValue(param2, suggestion1);

        Suggestion suggestion2 = new Suggestion(TEST_TEXT_1, suggestion1, suggestion);
        assertSuggestionParamValue(param, suggestion2);
        assertSuggestionParamValue(param2, suggestion2);
    }

}
