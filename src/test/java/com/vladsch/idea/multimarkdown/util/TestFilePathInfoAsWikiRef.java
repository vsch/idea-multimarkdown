package com.vladsch.idea.multimarkdown.util;/*
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

@RunWith(value = Parameterized.class)
public class TestFilePathInfoAsWikiRef {
    private String filePath;
    private String wikiRef;

    public TestFilePathInfoAsWikiRef(String filePath, String wikiRef) {
        this.filePath = filePath;
        this.wikiRef = wikiRef;
    }

    @Test
    public void test_asWikiRef() throws Exception {
        assertEquals(wikiRef, FilePathInfo.asWikiRef(filePath));
    }

    @Parameterized.Parameters(name = "{index}: asWikiRef({0})={1}")
    public static Iterable<Object[]> data1() {
        return Arrays.asList(new Object[][] {
                { "fileName", "fileName" },
                { "file-Name", "file Name" },
                { "fileName.md", "fileName.md" },
                { "file-Name.md", "file Name.md" },
                { "/path/with/fileName.md", "/path/with/fileName.md" },
                { "/path/with/file-Name.md", "/path/with/file Name.md" },
                { "/pathName/with/fileName.md", "/pathName/with/fileName.md" },
                { "/path-Name/with/file-Name.md", "/path Name/with/file Name.md" },
        });
    }
}
