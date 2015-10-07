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

package com.vladsch.idea.multimarkdown.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;

@RunWith(value = Parameterized.class)
public class TestEndsWithWikiRef {
    private String filePath;
    private String wikiRef;
    private boolean endsWith;
    private boolean endsWithCase;
    private boolean endsWithSpaceDash;
    private boolean endsWithCaseSpaceDash;

    public TestEndsWithWikiRef(String filePath, String wikiRef, boolean endsWith, boolean endsWithCase, boolean endsWithSpaceDash, boolean endsWithCaseSpaceDash) {
        this.filePath = filePath;
        this.wikiRef = wikiRef;
        this.endsWith = endsWith;
        this.endsWithCase = endsWithCase;
        this.endsWithSpaceDash = endsWithSpaceDash;
        this.endsWithCaseSpaceDash = endsWithCaseSpaceDash;
    }

    @Test
    public void test_endsWith() throws Exception {
        assertEquals(endsWith, FilePathInfo.endsWithWikiRef(false, false, filePath, wikiRef));
    }

    @Test
    public void test_endsWithCase() throws Exception {
        assertEquals(endsWithCase, FilePathInfo.endsWithWikiRef(true, false, filePath, wikiRef));
    }

    @Test
    public void test_endsWithSpaceDash() throws Exception {
        assertEquals(endsWithSpaceDash, FilePathInfo.endsWithWikiRef(false, true, filePath, wikiRef));
    }

    @Test
    public void test_endsWithCaseSpaceDash() throws Exception {
        assertEquals(endsWithCaseSpaceDash, FilePathInfo.endsWithWikiRef(true, true, filePath, wikiRef));
    }

    public void test_nothing() throws Exception {

    }

    @Parameterized.Parameters(name = "{index}: data({0} {1} n:{2} c:{3} s:{4} cs:{5})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                /* @formatter:off */
                /*                                                  no-c      case      spda        ca sp */
                /* 0  */  { "fileName.md", "fileName",              false,    false,    false,     false },
                /* 1  */  { "fileName", "fileName.md",              false,    false,    false,     false },
                /* 2  */  { "fileName", "fileName",                 true,     true,     true,      true },
                /* 3  */  { "fileName", "Filename",                 true,     false,    true,      false },
                /* 4  */  { "filename", "fileName",                 true,     false,    true,      false },
                /* 5  */  { "FileName", "fileName",                 true,     false,    true,      false },
                /* 6  */  { "file-Name", "file Name",               false,    false,    false,     false},
                /* 7  */  { "file-Name", "File name",               false,    false,    false,     false},
                /* 8  */  { "file-name", "file Name",               false,    false,    false,     false},
                /* 9  */  { "File-Name", "file Name",               false,    false,    false,     false},
                /* 10  */  { "file Name", "file Name",              true,     true,     true,      true },
                /* 11  */  { "file Name", "File name",              true,     false,    true,      false },
                /* 12  */  { "file name", "file Name",              true,     false,    true,      false },
                /* 13  */  { "File Name", "file Name",              true,     false,    true,      false },
                /* 14  */  { "file-Name", "file-Name",              false,    false,    false,     false },
                /* 15  */  { "file-Name", "File-name",              false,    false,    false,     false },
                /* 16  */  { "file-name", "file-Name",              false,    false,    false,     false },
                /* 17  */  { "File-Name", "file-Name",              false,    false,    false,     false },
                /* 18  */  { "file Name", "file-Name",              false,    false,    true,      true },
                /* 19  */  { "file Name", "File-name",              false,    false,    true,      false },
                /* 20  */  { "file name", "file-Name",              false,    false,    true,      false },
                /* 21  */  { "File Name", "file-Name",              false,    false,    true,      false },
                /* 22  */  { "/path/with/fileName", "fileName",     true,     true,     true,      true },
                /* 23  */  { "/path/with/fileName", "Filename",     true,     false,    true,      false },
                /* 24  */  { "/path/with/filename", "fileName",     true,     false,    true,      false },
                /* 25  */  { "/path/with/FileName", "fileName",     true,     false,    true,      false },
                /* 26  */  { "/path/with/file-Name", "file Name",   false,    false,    false,     false },
                /* 27  */  { "/path/with/file-Name", "File name",   false,    false,    false,     false },
                /* 28  */  { "/path/with/file-name", "file Name",   false,    false,    false,     false },
                /* 29  */  { "/path/with/File-Name", "file Name",   false,    false,    false,     false },
                /* 30  */  { "/path/with/file Name", "file Name",   true,     true,     true,      true },
                /* 31  */  { "/path/with/file Name", "File name",   true,     false,    true,      false },
                /* 32  */  { "/path/with/file name", "file Name",   true,     false,    true,      false },
                /* 33  */  { "/path/with/File Name", "file Name",   true,     false,    true,      false },
                /* 34  */  { "/path/with/file-Name", "file-Name",   false,    false,    false,     false },
                /* 35  */  { "/path/with/file-Name", "File-name",   false,    false,    false,     false },
                /* 36  */  { "/path/with/file-name", "file-Name",   false,    false,    false,     false },
                /* 37  */  { "/path/with/File-Name", "file-Name",   false,    false,    false,     false },
                /* 38  */  { "/path/with/file Name", "file-Name",   false,    false,    true,      true },
                /* 39  */  { "/path/with/file Name", "File-name",   false,    false,    true,      false },
                /* 40  */  { "/path/with/file name", "file-Name",   false,    false,    true,      false },
                /* 41  */  { "/path/with/File Name", "file-Name",   false,    false,    true,      false }
                /* @formatter:on */
        });
    }
}
