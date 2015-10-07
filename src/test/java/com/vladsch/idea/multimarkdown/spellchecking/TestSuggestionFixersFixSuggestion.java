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

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class TestSuggestionFixersFixSuggestion extends TestCase {
    private final @NotNull String suggestion;
    private final @NotNull String remove;
    private final @NotNull String pad;
    private final String result;

    public TestSuggestionFixersFixSuggestion(@NotNull String suggestion, @NotNull String remove, @NotNull String pad, String result) {
        this.suggestion = suggestion;
        this.remove = remove;
        this.pad = pad;
        this.result = result;
    }

    @Test
    public void testFixSuggestion() throws Exception {
        assertEquals(result, SuggestionFixers.fixSuggestion(suggestion,remove,pad));
    }

    @Parameterized.Parameters(name = "{index}: fixSuggestion({0},{1},{2}) == {3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                /* @formatter:off */
                { "fileName", " -_.'/\\", "", "fileName" },
                { " -_.'/\\fileName", " -_.'/\\", "", "fileName" },
                { "f -_.'/\\ileName", " -_.'/\\", "", "fileName" },
                { "fi -_.'/\\leName", " -_.'/\\", "", "fileName" },
                { "file -_.'/\\Name", " -_.'/\\", "", "fileName" },
                { "fileNa -_.'/\\me", " -_.'/\\", "", "fileName" },
                { "fileNam -_.'/\\e", " -_.'/\\", "", "fileName" },
                { "fileName -_.'/\\", " -_.'/\\", "", "fileName" },
                { "fileName", " -_.'/\\",         "", "fileName" },
                { " -_.'/\\fileName", " -_.'/\\", " ", "fileName" },
                { "f -_.'/\\ileName", " -_.'/\\", " ", "f ileName" },
                { "fi -_.'/\\leName", " -_.'/\\", " ", "fi leName" },
                { "file -_.'/\\Name", " -_.'/\\", " ", "file Name" },
                { "fileNa -_.'/\\me", " -_.'/\\", " ", "fileNa me" },
                { "fileNam -_.'/\\e", " -_.'/\\", " ", "fileNam e" },
                { "fileName -_.'/\\", " -_.'/\\", " ", "fileName" },
                /* @formatter:on */
        });
    }
}
