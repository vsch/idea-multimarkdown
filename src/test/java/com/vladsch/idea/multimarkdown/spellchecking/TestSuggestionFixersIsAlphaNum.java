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

import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class TestSuggestionFixersIsAlphaNum extends TestCase {
    private final @NotNull String suggestion;
    private final boolean result;

    public TestSuggestionFixersIsAlphaNum(@NotNull String suggestion, boolean result) {
        this.suggestion = suggestion;
        this.result = result;
    }

    @Test
    public void testIsAlphaNum() throws Exception {
        assertEquals(result, SuggestionFixers.isAlphaNum(suggestion));
    }

    @Parameterized.Parameters(name = "{index}: fixSuggestion({0},{1},{2}) == {3}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> data = new ArrayList<Object[]>();
        String trueSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789абвгдеёжзийклмнопрстуфхцчшщыьъэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЫЬЪЭЮЯ";
        String falseSet = "~!`~@#$%^&*()_+-=\\][{}|/'\"";
        int iMax;

        iMax = trueSet.length();
        for (int i = 0; i < iMax; i++) {
            data.add(new Object[]{trueSet.substring(i, i+1), true});
        }

        iMax = falseSet.length();
        for (int i = 0; i < iMax; i++) {
            data.add(new Object[]{falseSet.substring(i, i+1), false});
        }

        return data;
    }

}
