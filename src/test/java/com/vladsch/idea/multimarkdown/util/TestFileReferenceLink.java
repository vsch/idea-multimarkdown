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
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class TestFileReferenceLink {

    //public int[] inaccessibleWikiPageRefReasons(@NotNull String wikiPageRef) {
    //    int[] reasons = new int[REASON_MAX];
    //    int i = 0;
    //
    //    if (targetNameHasSpaces()) reasons[i++] = REASON_TARGET_HAS_SPACES;
    //    if (wikiPageRef.equalsIgnoreCase(this.wikiPageRef) && !wikiPageRef.equals(this.wikiPageRef)) reasons[i++] = REASON_CASE_MISMATCH;
    //    if (this.wikiPageRef.indexOf('-') >= 0) reasons[i++] = REASON_WIKI_PAGEREF_HAS_DASHES;
    //    if (!targetReference.isUnderWikiHome()) reasons[i++] = REASON_NOT_UNDER_WIKI_HOME;
    //    if (!targetReference.hasWikiPageExt()) reasons[i++] = REASON_TARGET_NOT_WIKI_PAGE_EXT;
    //    if (!targetReference.getWikiHome().startsWith(sourceReference.getWikiHome())) reasons[i++] = REASON_NOT_UNDER_SOURCE_WIKI_HOME;
    //
    //    return Arrays.copyOfRange(reasons, 0, i);
    //}

    @Before
    public void setUp() throws Exception {
        FileReferenceLink.projectFileResolver = null;
    }

    private final FileReferenceLink fileReferenceLink;
    private final String getLinkRef;
    private final String getWikiPageRef;
    private final boolean isWikiAccessible;
    private final boolean linkRefHasSpaces;
    private final int getUpDirectories;
    private final int getDownDirectories;
    private final String getLinkRefWithAnchor;
    private final String getWikiPageRefWithAnchor;

    /* 0:  sourceReference, */
    /* 1:  targetReference, */
    /* 2:  getLinkRef, */
    /* 3:  getWikiPageRef, */
    /* 4:  isWikiAccessible, */
    /* 5:  targetNameHasSpaces, */
    /* 6:  getUpDirectories, */
    /* 7:  getDownDirectories, */
    /* 8:  getLinkRefWithAnchor, */
    /* 9:  getWikiPageRefWithAnchor, */
    public TestFileReferenceLink(
            String sourceReference,
            String targetReference,
            String getLinkRef,
            String getWikiPageRef,
            boolean isWikiAccessible,
            boolean linkRefHasSpaces,
            int getUpDirectories,
            int getDownDirectories,
            String getLinkRefWithAnchor,
            String getWikiPageRefWithAnchor
    ) {
        fileReferenceLink = new FileReferenceLink(sourceReference, targetReference, null);
        this.getLinkRef = getLinkRef;
        this.getWikiPageRef = getWikiPageRef;
        this.isWikiAccessible = isWikiAccessible;
        this.linkRefHasSpaces = linkRefHasSpaces;
        this.getUpDirectories = getUpDirectories;
        this.getDownDirectories = getDownDirectories;
        this.getLinkRefWithAnchor = getLinkRefWithAnchor;
        this.getWikiPageRefWithAnchor = getWikiPageRefWithAnchor;
    }

    /* @formatter:off */
    @Test public void test_getLinkRef() { assertEquals(getLinkRef, fileReferenceLink.getLinkRef());}
    @Test public void test_getWikiPageRef() { assertEquals(getWikiPageRef, fileReferenceLink.getWikiPageRef());}
    @Test public void test_isWikiAccessible() { assertEquals(isWikiAccessible, fileReferenceLink.isWikiAccessible()); }
    @Test public void test_linkRefHasSpaces() { assertEquals(linkRefHasSpaces, fileReferenceLink.linkRefHasSpaces()); }
    @Test public void test_getUpDirectories() { assertEquals(getUpDirectories, fileReferenceLink.getUpDirectories()); }
    @Test public void test_getDownDirectories() { assertEquals(getDownDirectories, fileReferenceLink.getDownDirectories()); }
    @Test public void test_getLinkRefWithAnchor() { assertEquals(getLinkRefWithAnchor, fileReferenceLink.getLinkRefWithAnchor()); }
    @Test public void test_getWikiPageRefWithAnchor() { assertEquals(getWikiPageRefWithAnchor, fileReferenceLink.getWikiPageRefWithAnchor()); }
    @Test public void test_getLinkRefNoExt() { assertEquals(new FilePathInfo(getLinkRef).getFilePathNoExt(), fileReferenceLink.getLinkRefNoExt());}
    @Test public void test_getLinkRefWithAnchorNoExt() { assertEquals(new FilePathInfo(getLinkRefWithAnchor).getFilePathWithAnchorNoExt(), fileReferenceLink.getLinkRefWithAnchorNoExt()); }
    /* @formatter:on */

    @Parameterized.Parameters(name = "{index} source={0} target={1} wikiAcc={2}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-2-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-2-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/Level-1-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Bad File Name.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Other-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-1-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-2-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/Level-2-File.mkd
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/Level-1-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/dummy.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/main.md

                /* @formatter:off */
                /* 0 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , true ),
                /* 1 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-2-File.md"
                                              , true ),
                /* 2 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-2-File.md"
                                              , true ),
                /* 3 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , true ),
                /* 4 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/Level-1-File.md"
                                              , true ),
                /* 5 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Bad File Name.md"
                                              , false ),
                /* 6 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Other-File.md"
                                              , true ),
                /* 7 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , false ),
                /* 8 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-2-File.md"
                                              , false ),
                /* 9 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/Level-2-File.mkd"
                                              , false ),
                /* 10 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/Level-1-File.md"
                                              , false ),
                /* 11 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/dummy.md"
                                              , false ),
                /* 12 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/main.md"
                                              , false ),

                /* 13 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , true ),
                /* 14 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-2-File.md"
                                              , true ),
                /* 15 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-2-File.md"
                                              , true ),
                /* 16 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , true ),
                /* 17 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/Level-1-File.md"
                                              , true ),
                /* 18 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Bad File Name.md"
                                              , false ),
                /* 19 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Other-File.md"
                                              , true ),
                /* 20 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , false ),
                /* 21 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-2-File.md"
                                              , false ),
                /* 22 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/Level-2-File.mkd"
                                              , false ),
                /* 23 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/Level-1-File.md"
                                              , false ),
                /* 24 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/dummy.md"
                                              , false ),
                /* 25 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/main.md"
                                              , false ),

                /* 26 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , true ),
                /* 27 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-2-File.md"
                                              , true ),
                /* 28 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-2-File.md"
                                              , true ),
                /* 29 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , true ),
                /* 30 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/Level-1-File.md"
                                              , true ),
                /* 31 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Bad File Name.md"
                                              , false ),
                /* 32 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Other-File.md"
                                              , true ),
                /* 33 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , true ),
                /* 34 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-2-File.md"
                                              , true ),
                /* 35 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/Level-2-File.mkd"
                                              , false ),
                /* 36 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/Level-1-File.md"
                                              , true ),
                /* 37 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/dummy.md"
                                              , true ),
                /* 38 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/main.md"
                                              , true ),

                /* 39 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , false ),
                /* 40 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-2-File.md"
                                              , false ),
                /* 41 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-2-File.md"
                                              , false ),
                /* 42 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md"
                                              , false ),
                /* 43 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/Level-1-File.md"
                                              , false ),
                /* 44 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Bad File Name.md"
                                              , false ),
                /* 45 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Other-File.md"
                                              , false ),
                /* 46 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , true ),
                /* 47 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/Level-2-File.md"
                                              , true ),
                /* 48 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/Level-2-File.mkd"
                                              , false ),
                /* 49 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/Level-1-File.md"
                                              , false ),
                /* 50 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/dummy.md"
                                              , false ),
                /* 51 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/level-1/level-2/level3.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/main.md"
                                              , false ),
                /* 28 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-#2/Level-2-File.md"
                                              , false ),
                /* 28 */  filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/markdown/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-#2-File.md"
                                              , false )
                 /* @formatter:on */

        );
    }

    private static int countParts(String path, boolean backDirs) {
        String[] parts = path.split("/");
        int dirs = 0;
        if (backDirs) {
            for (String part : parts) {
                if (part.equals("..")) {
                    dirs++;
                }
            }
        } else {
            for (String part : parts) {
                if (!part.equals("..")) {
                    dirs++;
                }
            }
            dirs--;
        }
        return dirs;
    }

    /* 0:  sourceReference, */
    /* 1:  targetReference, */
    /* 2:  getLinkRef, */
    /* 3:  getWikiPageRef, */
    /* 4:  isWikiAccessible, */
    /* 5:  targetNameHasSpaces, */
    /* 6:  getUpDirectories, */
    /* 7:  getDownDirectories, */
    /* 8:  getLinkRefWithAnchor, */
    /* 9:  getWikiPageRefWithAnchor, */
    private static Object[] filePathInfoTestData(
            String sourceReference,
            String targetReference,
            boolean isWikiAccessible
    ) {
        Object[] result = new Object[10];
        int itmp;
        String sourceReferenceNoAnchor = new FilePathInfo(sourceReference).getFilePath();
        String targetReferenceNoAnchor = new FilePathInfo(targetReference).getFilePath();
        result[0] = sourceReference;
        result[1] = targetReference;
        result[2] = Paths.get("/" + FilenameUtils.getPath(sourceReferenceNoAnchor)).relativize(Paths.get(targetReferenceNoAnchor)).toString();
        result[3] = (FilenameUtils.getPath((String) result[2]) + FilenameUtils.getBaseName((String) result[2])).replace('-', ' ');
        result[4] = isWikiAccessible;
        result[5] = ((String) result[2]).contains(" ");
        result[6] = countParts((String) result[2], true);
        result[7] = countParts((String) result[2], false);
        result[8] = Paths.get("/" + FilenameUtils.getPath(sourceReference)).relativize(Paths.get(targetReference)).toString();;
        result[9] = (FilenameUtils.getPath((String) result[8]) + FilenameUtils.getBaseName((String) result[8])).replace('-', ' ');;
        return result;
    }
}
