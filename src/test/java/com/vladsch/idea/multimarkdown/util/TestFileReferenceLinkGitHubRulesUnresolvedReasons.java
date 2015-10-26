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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static com.vladsch.idea.multimarkdown.util.FileReferenceLink.*;
import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class TestFileReferenceLinkGitHubRulesUnresolvedReasons {

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

    private final FileReferenceLinkGitHubRules fileReferenceLink;
    private final String wikiPageRef;
    private final int inaccessibleReasonFlags;
    private final String computedWikiPageRef;
    private final String targetNameHasSpacedFixed;
    private final String caseMismatchWikiRefFixed;
    private final String caseMismatchFileNameFixed;
    private final String wikiRefHasDashesFixed;
    private final String targetNotWikiPageExtFixed;
    private final String targetNotInWikiHomeFixed;
    private final String targetNotInSameWikiHomeFixed;
    private final String wikiRefHasSlashFixed;
    private final String wikiRefHasSubDirFixed;

    // computed, not in constructor list
    private final InaccessibleWikiPageReasons inaccessibleReasons;
    /* 0:  fileReferenceLink, */
    /* 1:  wikiPageRef, */
    /* 2:  inaccessibleReasonFlags, */
     /* 4: targetNameHasSpacedFixed, */
    /* 5:  caseMismatchWikiRefFixed, */
    /* 6:  caseMismatchFileNameFixed, */
    /* 7:  wikiRefHasDashesFixed, */
    /* 8:  targetNotWikiPageExtFixed, */
    /* 9:  targetNotInWikiHomeFixed, */
    /* 10: targetNotInSameWikiHomeFixed, */
    /* 11: wikiRefHasSlashFixed, */
    /* 12: wikiRefHasSubDirFixed, */
    public TestFileReferenceLinkGitHubRulesUnresolvedReasons(FileReferenceLinkGitHubRules fileReferenceLink, String wikiPageRef, int inaccessibleReasonFlags, String computedWikiPageRef, String targetNameHasSpacedFixed, String caseMismatchWikiRefFixed, String caseMismatchFileNameFixed, String wikiRefHasDashesFixed, String targetNotWikiPageExtFixed, String targetNotInWikiHomeFixed
            , String targetNotInSameWikiHomeFixed
            , String wikiRefHasSlashFixed
            , String wikiRefHasSubDirFixed
    ) {
        this.fileReferenceLink = fileReferenceLink;
        this.wikiPageRef = wikiPageRef;
        this.inaccessibleReasonFlags = inaccessibleReasonFlags;
        this.computedWikiPageRef = computedWikiPageRef;
        this.targetNameHasSpacedFixed = targetNameHasSpacedFixed;
        this.caseMismatchWikiRefFixed = caseMismatchWikiRefFixed;
        this.caseMismatchFileNameFixed = caseMismatchFileNameFixed;
        this.wikiRefHasDashesFixed = wikiRefHasDashesFixed;
        this.targetNotWikiPageExtFixed = targetNotWikiPageExtFixed;
        this.targetNotInWikiHomeFixed = targetNotInWikiHomeFixed;
        this.targetNotInSameWikiHomeFixed = targetNotInSameWikiHomeFixed;
        this.wikiRefHasSlashFixed = wikiRefHasSlashFixed;
        this.wikiRefHasSubDirFixed = wikiRefHasSubDirFixed;

        this.inaccessibleReasons = fileReferenceLink.inaccessibleWikiPageRefReasons(wikiPageRef);
    }

    //this.inaccessibleReasons = fileReferenceLink.inaccessibleWikiPageRefReasons(wikiPageRef);
    /* @formatter:off */
    @Test public void test_inaccessibleWikiPageRefReasons() { assertEquals(inaccessibleReasonFlags, inaccessibleReasons.reasons);}
    @Test public void test_targetNameHasSpacedFixed() { if (targetNameHasSpacedFixed != null) assertEquals(targetNameHasSpacedFixed, inaccessibleReasons.targetNameHasSpacedFixed());}
    @Test public void test_caseMismatchWikiRefFixed() { if (caseMismatchWikiRefFixed != null) assertEquals(caseMismatchWikiRefFixed, inaccessibleReasons.caseMismatchWikiRefFixed());}
    @Test public void test_caseMismatchFileNameFixed() { if (caseMismatchFileNameFixed != null) assertEquals(caseMismatchFileNameFixed, inaccessibleReasons.caseMismatchFileNameFixed());}
    @Test public void test_wikiRefHasDashesFixed() { if (wikiRefHasDashesFixed != null) assertEquals(wikiRefHasDashesFixed, inaccessibleReasons.wikiRefHasDashesFixed());}
    @Test public void test_targetNotWikiPageExtFixed() { if (targetNotWikiPageExtFixed != null) assertEquals(targetNotWikiPageExtFixed, inaccessibleReasons.targetNotWikiPageExtFixed());}
    @Test public void test_targetNotInWikiHomeFixed() { if (targetNotInWikiHomeFixed != null) assertEquals(targetNotInWikiHomeFixed, inaccessibleReasons.targetNotInWikiHomeFixed());}
    @Test public void test_targetNotInSameWikiHomeFixed() { if (targetNotInSameWikiHomeFixed != null) assertEquals(targetNotInSameWikiHomeFixed, inaccessibleReasons.targetNotInSameWikiHomeFixed());}
    @Test public void test_wikiRefHasSlashFixed() { if (wikiRefHasSlashFixed != null) assertEquals(wikiRefHasSlashFixed, inaccessibleReasons.wikiRefHasSlashFixed());}
    @Test public void test_wikiRefHasSubDirFixed() { if (wikiRefHasSubDirFixed != null) assertEquals(wikiRefHasSubDirFixed, inaccessibleReasons.wikiRefHasSubDirFixed());}
    /* @formatter:on */

    /* 0:  fileReferenceLink, */
    /* 1:  wikiPageRef, */
    /* 2:  inaccessibleReasonFlags, */
    /* 4:  targetNameHasSpacedFixed, */
    /* 5:  caseMismatchWikiRefFixed, */
    /* 6:  caseMismatchFileNameFixed, */
    /* 7:  wikiRefHasDashesFixed, */
    /* 8:  targetNotWikiPageExtFixed, */
    /* 9:  targetNotInWikiHomeFixed, */
    /* 10: targetNotInSameWikiHomeFixed, */
    @Parameterized.Parameters(name = "{index}: {3} {1} = {2}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-2-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-2-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/wiki-links.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/Level-1-File.md
                // /Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Bad-File-Name.md
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
                                              , "Level 1 File"
                                              , 0 ),
                /* 1 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , "level 1 file"
                                              , REASON_CASE_MISMATCH, "level 1/level 2/level3.wiki/Level 1 File", "level-1-file.md"),
                /* 2 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/level-1-File.md"
                                              , "Level 1 file"
                                              , REASON_CASE_MISMATCH, "level 1/level 2/level3.wiki/level 1 File", "Level-1-file.md"),
                /* 3 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level 1 File.md"
                                              , "Level 1 File"
                                              , REASON_TARGET_HAS_SPACES, "Level-1-File.md" ),
                /* 4 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.mkd"
                                              , "Level 1 File"
                                              , REASON_TARGET_NOT_WIKI_PAGE_EXT, "Level-1-File.md" ),
                /* 5 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.markdown"
                                              , "Level 1 File"
                                              , REASON_TARGET_NOT_WIKI_PAGE_EXT, "Level-1-File.md" ),
                /* 6 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , "Level 1 File"
                                              , REASON_NOT_UNDER_SOURCE_WIKI_HOME, "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Level-1-File.md"),
                /* 7 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages/level-1/level-2/level-3/Level-1-File.md"
                                              , "Level 1 File"
                                              , REASON_NOT_UNDER_WIKI_HOME, "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/Level-1-File.md"),
                /* 8 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/level3.wiki/Level-1-File.md"
                                              , "Level-1-File"
                                              , REASON_WIKI_PAGEREF_HAS_DASHES, "Level 1 File" ),
                /* 9 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages/level 1/level-2/level-3/level 1-File.mkd"
                                              , "Level-1-file"
                                              , REASON_CASE_MISMATCH | REASON_TARGET_HAS_SPACES | REASON_TARGET_NOT_WIKI_PAGE_EXT | REASON_WIKI_PAGEREF_HAS_DASHES | REASON_NOT_UNDER_WIKI_HOME | REASON_WIKI_PAGEREF_HAS_DASHES),
                /* 10 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-1-File.md"
                                              , "/Level /1 Files"
                                              , REASON_WIKI_PAGEREF_HAS_SLASH, "Level 1 File" ),
                /* 11 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-1-File.md"
                                              , "/Level /1 File/"
                                              , REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH, "Level 1 File" ),
                /* 11 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-1-File.md"
                                              , "level 2/Level 1 File"
                                              , REASON_WIKI_PAGEREF_HAS_SUBDIR, "Level 1 File" ),
                /* 12 */   filePathInfoTestData( "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/wiki-links.md"
                                              , "/Users/vlad/src/idea-multimarkdown2/src/test/resources/WikiPages.wiki/level-1/level-2/Level-1-File.md"
                                              , "level 1/level 2/Level 1 File"
                                              , REASON_WIKI_PAGEREF_HAS_SUBDIR, "Level 1 File" )
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

    /* 0:  fileReferenceLink, */
    /* 1:  wikiPageRef, */
    /* 2:  inaccessibleReasonFlags, */
    /* 3:  computedWikiPageRef, */
    /* 4:  targetNameHasSpacedFixed, */
    /* 5:  caseMismatchWikiRefFixed, */
    /* 6:  caseMismatchFileNameFixed, */
    /* 7:  wikiRefHasDashesFixed, */
    /* 8:  targetNotWikiPageExtFixed, */
    /* 9:  targetNotInWikiHomeFixed, */
    /* 10: targetNotInSameWikiHomeFixed, */
    /* 11: targetNotInSameWikiHomeFixed, */
    /* 12: targetNotInSameWikiHomeFixed, */
    private static Object[] filePathInfoTestData(
            String sourceReference,
            String targetReference,
            String wikiPageRef,
            int inaccessibleReasons,
            String... fixedStrings
    ) {
        Object[] result = new Object[13];
        int itmp;
        FileReferenceLink referenceLink;

        result[0] = referenceLink = new FileReferenceLinkGitHubRules(sourceReference, targetReference, null);
        result[1] = wikiPageRef;
        result[2] = inaccessibleReasons;
        result[3] = referenceLink.getWikiPageRef();
        result[4] = null;
        result[5] = null;
        result[6] = null;
        result[7] = null;
        result[8] = null;
        result[9] = null;
        result[10] = null;
        result[11] = null;
        result[12] = null;

        int reasons = inaccessibleReasons;
        int fMax = fixedStrings.length;
        Fixed_List:

        for (int f = 0; f < fMax; f++) {
            String fixed = fixedStrings[f];
            // see where it goes
            for (int i = 0; i < 6; i++) {
                if ((reasons & (1 << i)) != 0) {
                    reasons &= (1 << i);
                    switch (1 << i) {
                        case REASON_TARGET_HAS_SPACES:
                            result[4] = fixed;
                            break;

                        case REASON_CASE_MISMATCH:
                            result[5] = fixed;
                            if (++f < fMax) {
                                result[6] = fixedStrings[f];
                            }
                            break;

                        case REASON_WIKI_PAGEREF_HAS_DASHES:
                            result[7] = fixed;
                            break;

                        case REASON_TARGET_NOT_WIKI_PAGE_EXT:
                            result[8] = fixed;
                            break;

                        case REASON_NOT_UNDER_WIKI_HOME:
                            result[9] = fixed;
                            break;

                        case REASON_NOT_UNDER_SOURCE_WIKI_HOME:
                            result[10] = fixed;
                            break;

                        case REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH:
                            result[11] = fixed;
                            break;

                        case REASON_WIKI_PAGEREF_HAS_SUBDIR:
                            result[12] = fixed;
                            break;

                        case REASON_WIKI_PAGEREF_HAS_SLASH:
                        default:
                            break;
                    }

                    continue Fixed_List;
                }
            }

            break;
        }

        return result;
    }
}
