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

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class TestFilePathInfoResolveLinkRef {
    private String filePath;
    private String linkRef;
    private String resolveLinkRef;
    private String resolveLinkRefToWikiPage;

    private FilePathInfo filePathInfo;

    /* 0:  filePath, */
    /* 1:  linkRef, */
    /* 2:  resolveLinkRef, */
    /* 3:  resolveLinkRefToWikiPage, */
    public TestFilePathInfoResolveLinkRef(
            String filePath,
            String linkRef,
            String resolveLinkRef,
            String resolveLinkRefToWikiPage
    ) {
        this.filePath = filePath;
        this.filePathInfo = new FilePathInfo(filePath);
        this.linkRef = linkRef;
        this.resolveLinkRef = resolveLinkRef;
        this.resolveLinkRefToWikiPage = resolveLinkRefToWikiPage;
    }

    /* @formatter:off */
    @Test public void test_resolveLinkRef() { FilePathInfo pathInfo = filePathInfo.resolveLinkRef(linkRef);assertEquals(resolveLinkRef, pathInfo != null ? pathInfo.getFilePath() : null);}
    @Test public void test_resolveLinkRefToWikiPage() { FilePathInfo pathInfo = filePathInfo.resolveLinkRefToWikiPage(linkRef);assertEquals(resolveLinkRefToWikiPage, pathInfo != null ? pathInfo.getFilePath() : null); }

  /* @formatter:on */

    /* 0:  filePath, */
    /* 1:  linkRef, */
    /* 2:  resolveLinkRef, */
    /* 3:  resolveLinkRefToWikiPage, */
    @Parameterized.Parameters(name = "{index}: filePath = {0} {1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
                /* 0 */ filePathInfoTestData("/path/with/fileName.md", "/path/with/fileName.md", "/path/with/fileName.md", "/path/with/fileName.md"),
                /* 1 */ filePathInfoTestData("/path/with/fileName.md", "./fileName2.md", "/path/with/fileName2.md", "/path/with/fileName2.md"),
                /* 2 */ filePathInfoTestData("/path/with/fileName.md", "../fileName3.md", "/path/fileName3.md", "/path/fileName3.md"),
                /* 3 */ filePathInfoTestData("/path/with/fileName.md", "../../fileName4.md", "/fileName4.md", "/fileName4.md"),
                /* 4 */ filePathInfoTestData("/path/with/fileName.md", "../../../fileName5.md", null, null),
                /* 5 */ filePathInfoTestData("/path/with/wiki/fileName.md", "../wiki/fileName6.md", "/path/with/wiki/fileName6.md", "/path/with/wiki/fileName6.md"),
                /* 6 */ filePathInfoTestData("/path/with/wiki/fileName.md", "../../wiki/fileName7.md", "/path/wiki/fileName7.md", "/path/wiki/fileName7.md"),
                /* 7 */ filePathInfoTestData("/path/with/wiki/fileName.md", "../../../wiki/fileName8.md", "/wiki/fileName8.md", "/path/with/with.wiki/fileName8.md"),
                /* 8 */ filePathInfoTestData("/path/with/wiki/fileName.md", "../../../../wiki/fileName9.md", null, "/path/path.wiki/fileName9.md"),
                /* 9 */ filePathInfoTestData("/path/with/wiki/fileName.md", "../../../../../wiki/fileName10.md", null, null),
                /* 10 */ filePathInfoTestData("/path/with/test.wiki/fileName.md", "../wiki/fileName11.md", "/path/with/wiki/fileName11.md", "/path/with/test.wiki/fileName11.md"),
                /* 11 */ filePathInfoTestData("/path/with/test.wiki/fileName.md", "../../wiki/fileName12.md", "/path/wiki/fileName12.md", "/path/wiki/fileName12.md"),
                /* 12 */ filePathInfoTestData("/path/test/test.wiki/fileName.md", "../../wiki/fileName13.md", "/path/wiki/fileName13.md", "/path/wiki/fileName13.md"),
                /* 13 */ filePathInfoTestData("/path/with/test.wiki/fileName.md", "../../../wiki/fileName14.md", "/wiki/fileName14.md", "/path/with/with.wiki/fileName14.md"),

                /* 14 */ filePathInfoTestData("/path/with/test/fileName.md", "../wiki/fileName15.md", "/path/with/wiki/fileName15.md", "/path/with/wiki/fileName15.md"),
                /* 15 */ filePathInfoTestData("/path/with/test/fileName.md", "../../wiki/fileName16.md", "/path/wiki/fileName16.md", "/path/with/test/test.wiki/fileName16.md"),
                /* 16 */ filePathInfoTestData("/path/with/test/fileName.md", "../../../wiki/fileName17.md", "/wiki/fileName17.md", "/path/with/with.wiki/fileName17.md"),
                /* 17 */ filePathInfoTestData("/path/with/test/fileName.md", "../../../../wiki/fileName18.md", null, "/path/path.wiki/fileName18.md"),
                /* 18 */ filePathInfoTestData("/path/with/test/fileName.md", "../../../../../wiki/fileName19.md", null, null)
        );
    }

    private static Object[] filePathInfoTestData(String filePath, String linkRef, String resolveLinkRef, String resolveLinkRefToWikiPage) {
        Object[] result = new Object[4];

        /* @formatter:off */
/* 0: filePath, */                          result[0] = filePath;
/* 1: linkRef, */                           result[1] = linkRef;
/* 2: resolveLinkRef, */                    result[2] = resolveLinkRef;
/* 3: resolveLinkRefToWikiPage, */          result[3] = resolveLinkRefToWikiPage;
        /* @formatter:on */
        return result;
    }
}
