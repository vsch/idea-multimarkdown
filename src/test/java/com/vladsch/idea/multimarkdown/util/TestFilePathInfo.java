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
public class TestFilePathInfo {
    private String filePath;
    private FilePathInfo filePathInfo;

    private String getExt;
    private String getExtWithDot;
    private String getFilePath;
    private String getFilePathAsWikiRef;
    private String getFilePathNoExt;
    private String getPath;
    private String getWikiHome;
    private String getFileName;
    private String getFileNameNoExt;
    private boolean hasWikiPageExt;
    private boolean containsSpaces;
    private boolean isWikiHome;
    private boolean isUnderWikiHome;
    private boolean pathContainsSpaces;
    private boolean fileNameContainsSpaces;
    private boolean containsAnchor;
    private boolean pathContainsAnchor;
    private boolean fileNameContainsAnchor;
    private String getFilePathNoExtAsWikiRef;
    private String getPathAsWikiRef;
    private String getFileNameAsWikiRef;
    private String getFileNameNoExtAsWikiRef;
    private int getUpDirectoriesToWikiHome;

    private String getAnchor;
    private String getAnchorNoHash;
    private String getFilePathWithAnchor;
    private String getFilePathWithAnchorAsWikiRef;
    private String getFilePathWithAnchorNoExtAsWikiRef;
    private String getFileNameWithAnchor;
    private String getFileNameWithAnchorAsWikiRef;
    private String getFileNameWithAnchorNoExtAsWikiRef;
    private String getWithAnchorExt;
    private String getWithAnchorExtWithDot;

    /* 0:  filePath, */
    /* 1:  getExt, */
    /* 2:  getExtWithDot, */
    /* 3:  getFilePath, */
    /* 4:  getFilePathNoExt, */
    /* 5:  getPath, */
    /* 6:  getWikiHome, */
    /* 7:  getFileName, */
    /* 8:  getFileNameNoExt, */
    /* 9:  getFilePathAsWikiRef, */
    /* 10:  getFilePathNoExtAsWikiRef, */
    /* 11: getPathAsWikiRef, */
    /* 12: getFileNameAsWikiRef, */
    /* 13: getFileNameNoExtAsWikiRef, */
    /* 14: hasWikiPageExt, */
    /* 15: containsSpaces, */
    /* 16: isWikiHome, */
    /* 17: isUnderWikiHome, */
    /* 18: pathContainsSpaces, */
    /* 19: fileNameContainsSpaces, */
    /* 20: getUpDirectoriesToWikiHome, */
    /* 21: containsAnchor, */
    /* 22: pathContainsAnchor, */
    /* 23: fileNameContainsAnchor, */
    /* 24: getAnchor, */
    /* 25: getAnchorNoHash, */
    /* 26: getFilePathWithAnchor, */
    /* 27: getFilePathWithAnchorAsWikiRef, */
    /* 28: getFilePathWithAnchorNoExtAsWikiRef, */
    /* 29: getFileNameWithAnchor, */
    /* 30: getFileNameWithAnchorAsWikiRef, */
    /* 31: getFileNameWithAnchorNoExtAsWikiRef, */
    /* 32: getWithAnchorExt, */
    /* 33: getWithAnchorExtWithDot, */
    public TestFilePathInfo(
            String filePath,
            String getExt,
            String getExtWithDot,
            String getFilePath,
            String getFilePathNoExt,
            String getPath,
            String getWikiHome,
            String getFileName,
            String getFileNameNoExt,
            String getFilePathAsWikiRef,
            String getFilePathNoExtAsWikiRef,
            String getPathAsWikiRef,
            String getFileNameAsWikiRef,
            String getFileNameNoExtAsWikiRef,
            boolean hasWikiPageExt,
            boolean containsSpaces,
            boolean isWikiHome,
            boolean isUnderWikiHome,
            boolean pathContainsSpaces,
            boolean fileNameContainsSpaces,
            int getUpDirectoriesToWikiHome,
            boolean containsAnchor,
            boolean pathContainsAnchor,
            boolean fileNameContainsAnchor,
            String getAnchor,
            String getAnchorNoHash,
            String getFilePathWithAnchor,
            String getFilePathWithAnchorAsWikiRef,
            String getFilePathWithAnchorNoExtAsWikiRef,
            String getFileNameWithAnchor,
            String getFileNameWithAnchorAsWikiRef,
            String getFileNameWithAnchorNoExtAsWikiRef,
            String getWithAnchorExt,
            String getWithAnchorExtWithDot
    ) {
        this.filePath = filePath;
        this.filePathInfo = new FilePathInfo(filePath);
        this.getExt = getExt;
        this.getExtWithDot = getExtWithDot;
        this.getFilePath = getFilePath;
        this.getFilePathAsWikiRef = getFilePathAsWikiRef;
        this.getFilePathNoExt = getFilePathNoExt;
        this.getPath = getPath;
        this.getWikiHome = getWikiHome;
        this.getFileName = getFileName;
        this.getFileNameNoExt = getFileNameNoExt;
        this.hasWikiPageExt = hasWikiPageExt;
        this.containsSpaces = containsSpaces;
        this.isWikiHome = isWikiHome;
        this.isUnderWikiHome = isUnderWikiHome;
        this.pathContainsSpaces = pathContainsSpaces;
        this.fileNameContainsSpaces = fileNameContainsSpaces;
        this.getFilePathNoExtAsWikiRef = getFilePathNoExtAsWikiRef;
        this.getPathAsWikiRef = getPathAsWikiRef;
        this.getFileNameAsWikiRef = getFileNameAsWikiRef;
        this.getFileNameNoExtAsWikiRef = getFileNameNoExtAsWikiRef;
        this.getUpDirectoriesToWikiHome = getUpDirectoriesToWikiHome;
        this.containsAnchor = containsAnchor;
        this.pathContainsAnchor = pathContainsAnchor;
        this.fileNameContainsAnchor = fileNameContainsAnchor;
        this.getAnchor = getAnchor;
        this.getAnchorNoHash = getAnchorNoHash;
        this.getFilePathWithAnchor = getFilePathWithAnchor;
        this.getFilePathWithAnchorAsWikiRef = getFilePathWithAnchorAsWikiRef;
        this.getFilePathWithAnchorNoExtAsWikiRef = getFilePathWithAnchorNoExtAsWikiRef;
        this.getFileNameWithAnchor = getFileNameWithAnchor;
        this.getFileNameWithAnchorAsWikiRef = getFileNameWithAnchorAsWikiRef;
        this.getFileNameWithAnchorNoExtAsWikiRef = getFileNameWithAnchorNoExtAsWikiRef;
        this.getWithAnchorExt= getWithAnchorExt;
        this.getWithAnchorExtWithDot= getWithAnchorExtWithDot;
    }

    /* @formatter:off */
    @Test public void test_getExt() { assertEquals(getExt, filePathInfo.getExt());}
    @Test public void test_getExtWithDot() { assertEquals(getExtWithDot, filePathInfo.getExtWithDot());}
    @Test public void test_hasWikiPageExt() { assertEquals(hasWikiPageExt, filePathInfo.hasWikiPageExt());}
    @Test public void test_getFilePath() { assertEquals(getFilePath, filePathInfo.getFilePath());}
    @Test public void test_getFilePathAsWikiRef() { assertEquals(getFilePathAsWikiRef, filePathInfo.getFilePathAsWikiRef()); }
    @Test public void test_containsSpaces() { assertEquals(containsSpaces, filePathInfo.containsSpaces()); }
    @Test public void test_isWikiHome() { assertEquals(isWikiHome, filePathInfo.isWikiHome()); }
    @Test public void test_getFilePathNoExt() { assertEquals(getFilePathNoExt, filePathInfo.getFilePathNoExt()); }
    @Test public void test_getFilePathNoExtAsWikiRef() { assertEquals(getFilePathNoExtAsWikiRef, filePathInfo.getFilePathNoExtAsWikiRef()); }
    @Test public void test_getPath() { assertEquals(getPath, filePathInfo.getPath()); }
    @Test public void test_getPathAsWikiRef() { assertEquals(getPathAsWikiRef, filePathInfo.getPathAsWikiRef()); }
    @Test public void test_isUnderWikiHome() { assertEquals(isUnderWikiHome, filePathInfo.isUnderWikiHome()); }
    @Test public void test_getWikiHome() { assertEquals(getWikiHome, filePathInfo.getWikiHome()); }
    @Test public void test_pathContainsSpaces() { assertEquals(pathContainsSpaces, filePathInfo.pathContainsSpaces()); }
    @Test public void test_getFileName() { assertEquals(getFileName, filePathInfo.getFileName()); }
    @Test public void test_fileNameContainsSpaces() { assertEquals(fileNameContainsSpaces, filePathInfo.fileNameContainsSpaces()); }
    @Test public void test_getFileNameAsWikiRef() { assertEquals(getFileNameAsWikiRef, filePathInfo.getFileNameAsWikiRef()); }
    @Test public void test_getFileNameNoExt() { assertEquals(getFileNameNoExt, filePathInfo.getFileNameNoExt()); }
    @Test public void test_getFileNameNoExtAsWikiRef() { assertEquals(getFileNameNoExtAsWikiRef, filePathInfo.getFileNameNoExtAsWikiRef()); }
    @Test public void test_getUpDirectoriesToWikiHome() { assertEquals(getUpDirectoriesToWikiHome, filePathInfo.getUpDirectoriesToWikiHome()); }
    @Test public void test_containsAnchor() { assertEquals(containsAnchor, filePathInfo.containsAnchor()); }
    @Test public void test_pathContainsAnchor() { assertEquals(pathContainsAnchor, filePathInfo.pathContainsAnchor()); }
    @Test public void test_fileNameContainsAnchor() { assertEquals(fileNameContainsAnchor, filePathInfo.fileNameContainsAnchor()); }
    @Test public void test_getAnchor() { assertEquals(getAnchor, filePathInfo.getAnchor()); }
    @Test public void test_getAnchorNoHash() { assertEquals(getAnchorNoHash, filePathInfo.getAnchorNoHash()); }
    @Test public void test_getFilePathWithAnchor() { assertEquals(getFilePathWithAnchor, filePathInfo.getFilePathWithAnchor()); }
    @Test public void test_getFilePathAsWikiRefWithAnchor() { assertEquals(getFilePathWithAnchorAsWikiRef, filePathInfo.getFilePathWithAnchorAsWikiRef()); }
    @Test public void test_getFilePathWithAnchorNoExtAsWikiRef() { assertEquals(getFilePathWithAnchorNoExtAsWikiRef, filePathInfo.getFilePathWithAnchorNoExtAsWikiRef()); }
    @Test public void test_getFileNameWithAnchor() { assertEquals(getFileNameWithAnchor, filePathInfo.getFileNameWithAnchor()); }
    @Test public void test_getFileNameWithAnchorAsWikiRef() { assertEquals(getFileNameWithAnchorAsWikiRef, filePathInfo.getFileNameWithAnchorAsWikiRef()); }
    @Test public void test_getFileNameWithAnchorNoExtAsWikiRef() { assertEquals(getFileNameWithAnchorNoExtAsWikiRef, filePathInfo.getFileNameWithAnchorNoExtAsWikiRef()); }
    @Test public void test_getWithAnchorExt() { assertEquals(getWithAnchorExt, filePathInfo.getWithAnchorExt()); }
    @Test public void test_getWithAnchorExtWithDot() { assertEquals(getWithAnchorExtWithDot, filePathInfo.getWithAnchorExtWithDot()); }

  /* @formatter:on */

    /* 0:  filePath, */
    /* 1:  getExt, */
    /* 2:  getExtWithDot, */
    /* 3:  getFilePath, */
    /* 4:  getFilePathNoExt, */
    /* 5:  getPath, */
    /* 6:  getWikiHome, */
    /* 7:  getFileName, */
    /* 8:  getFileNameNoExt, */
    /* 9:  getFilePathAsWikiRef, */
    /* 10:  getFilePathNoExtAsWikiRef, */
    /* 11: getPathAsWikiRef, */
    /* 12: getFileNameAsWikiRef, */
    /* 13: getFileNameNoExtAsWikiRef, */
    /* 14: hasWikiPageExt, */
    /* 15: containsSpaces, */
    /* 16: isWikiHome, */
    /* 17: isUnderWikiHome, */
    /* 18: pathContainsSpaces, */
    /* 19: fileNameContainsSpaces, */
    /* 20: getUpDirectoriesToWikiHome, */
    /* 21: containsAnchor, */
    /* 22: pathContainsAnchor, */
    /* 23: fileNameContainsAnchor, */
    /* 24: getAnchor, */
    /* 25: getAnchorNoHash, */
    /* 26: getFilePathWithAnchor, */
    /* 27: getFilePathWithAnchorAsWikiRef, */
    /* 28: getFilePathWithAnchorNoExtAsWikiRef, */
    /* 29: getFileNameWithAnchor, */
    /* 30: getFileNameWithAnchorAsWikiRef, */
    /* 31: getFileNameWithAnchorNoExtAsWikiRef, */
    /* 32: getWithAnchorExt, */
    /* 33: getWithAnchorExtWithDot, */
    @Parameterized.Parameters(name = "{index}: filePath = {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
                /*  0*/ filePathInfoTestData("file-Name", "", 0),
                /*  1*/ filePathInfoTestData("fileName.md", "", 0),
                /*  2*/ filePathInfoTestData("file-Name.md", "", 0),
                /*  3*/ filePathInfoTestData("/path/with/fileName.md", "", 0),
                /*  4*/ filePathInfoTestData("/path/with/file-Name.md", "", 0),
                /*  5*/ filePathInfoTestData("/pathName/with/fileName.md", "", 0),
                /*  6*/ filePathInfoTestData("/path-Name/with/file-Name.md", "", 0),
                /*  7*/ filePathInfoTestData("/home.wiki/file-Name", "/home.wiki", 1),
                /*  8*/ filePathInfoTestData("/home.wiki/fileName.md", "/home.wiki", 1),
                /*  9 */ filePathInfoTestData("/home.wiki/file-Name.md", "/home.wiki", 1),
                /* 10 */ filePathInfoTestData("/home.wiki/path/with/fileName.md", "/home.wiki", 3),
                /* 11 */ filePathInfoTestData("/home.wiki/path/with/file-Name.md", "/home.wiki", 3),
                /* 12 */ filePathInfoTestData("/home.wiki/pathName/with/fileName.md", "/home.wiki", 3),
                /* 13 */ filePathInfoTestData("/is-home.wiki/file-Name", "/is-home.wiki", 1),
                /* 14 */ filePathInfoTestData("/is-home.wiki/fileName.md", "/is-home.wiki", 1),
                /* 15 */ filePathInfoTestData("/is-home.wiki/file-Name.md", "/is-home.wiki", 1),
                /* 16 */ filePathInfoTestData("/is-home.wiki/path/with/fileName.md", "/is-home.wiki", 3),
                /* 17 */ filePathInfoTestData("/is-home.wiki/path/with/file-Name.md", "/is-home.wiki", 3),
                /* 18 */ filePathInfoTestData("/is-home.wiki/pathName/with/fileName.md", "/is-home.wiki", 3),
                /* 19 */ filePathInfoTestData("/somepath/home.wiki/path-Name/with/file-Name.md", "/somepath/home.wiki", 3),
                /* 20 */ filePathInfoTestData("/somepath/home.wiki/file-Name", "/somepath/home.wiki", 1),
                /* 21 */ filePathInfoTestData("/somepath/home.wiki/fileName.md", "/somepath/home.wiki", 1),
                /* 22 */ filePathInfoTestData("/somepath/home.wiki/file-Name.md", "/somepath/home.wiki", 1),
                /* 23 */ filePathInfoTestData("/somepath/home.wiki/path/with/fileName.md", "/somepath/home.wiki", 3),
                /* 24 */ filePathInfoTestData("/somepath/home.wiki/path/with/file-Name.md", "/somepath/home.wiki", 3),
                /* 25 */ filePathInfoTestData("/somepath/home.wiki/pathName/with/fileName.md", "/somepath/home.wiki", 3),
                /* 26 */ filePathInfoTestData("/somepath/home.wiki/path-Name/with/file-Name.md", "/somepath/home.wiki", 3),
                /* 27 */ filePathInfoTestData("/somepath/is-home.wiki/file-Name", "/somepath/is-home.wiki", 1),
                /* 28 */ filePathInfoTestData("/somepath/is-home.wiki/fileName.md", "/somepath/is-home.wiki", 1),
                /* 29 */ filePathInfoTestData("/somepath/is-home.wiki/file-Name.md", "/somepath/is-home.wiki", 1),
                /* 30 */ filePathInfoTestData("/somepath/is-home.wiki/path/with/fileName.md", "/somepath/is-home.wiki", 3),
                /* 31 */ filePathInfoTestData("/somepath/is-home.wiki/path/with/file-Name.md", "/somepath/is-home.wiki", 3),
                /* 32 */ filePathInfoTestData("/somepath/is-home.wiki/pathName/with/fileName.md", "/somepath/is-home.wiki", 3),
                /* 33 */ filePathInfoTestData("/somepath/is-home.wiki/pathName/with/fileName.", "/somepath/is-home.wiki", 3),
                /* 34 */ filePathInfoTestData("/somepath/is-home.wiki/path/file-Name.md", "/somepath/is-home.wiki", 2),
                /* 35 */ filePathInfoTestData("/somepath/is-home.wiki/path/path2/path3/file-Name.md", "/somepath/is-home.wiki", 4),
                /* 36  */ filePathInfoTestData("file-Name#", "", 0),
                /* 37  */ filePathInfoTestData("file-Name#anchor", "", 0),
                /* 38  */ filePathInfoTestData("fileName#.md", "", 0),
                /* 39  */ filePathInfoTestData("fileName#anchor.md", "", 0),
                /* 40  */ filePathInfoTestData("file-Name#.md", "", 0),
                /* 41  */ filePathInfoTestData("file-Name#anchor.md", "", 0),
                /* 42  */ filePathInfoTestData("/path/with/fileName#.md", "", 0),
                /* 43  */ filePathInfoTestData("/path/with/fileName#anchor.md", "", 0),
                /* 44  */ filePathInfoTestData("/path/with/file-#Name.md", "", 0),
                /* 45  */ filePathInfoTestData("/path/with/file-#anchorName.md", "", 0),
                /* 46  */ filePathInfoTestData("/pathName/with/#fileName.md", "", 0),
                /* 47  */ filePathInfoTestData("/pathName/with/#anchorfileName.md", "", 0),
                /* 48  */ filePathInfoTestData("/path-Name/with/file-Name.md", "", 0),
                /* 49  */ filePathInfoTestData("/home.wiki/file-Name-#6", "/home.wiki", 1),
                /* 50  */ filePathInfoTestData("/home.wiki/fileName-6.md", "/home.wiki", 1),
                /* 51  */ filePathInfoTestData("/another.wiki/home.wiki/fileName-6.md", "/another.wiki/home.wiki", 1),
                /* 52  */ filePathInfoTestData("/another.wiki/test/home.wiki/fileName-6.md", "/another.wiki/test/home.wiki", 1),
                /* 53  */ filePathInfoTestData("/wiki/fileName-6.md", "/wiki", 1),
                /* 54  */ filePathInfoTestData("/another.wiki/wiki/fileName-6.md", "/another.wiki/wiki", 1),
                /* 55  */ filePathInfoTestData("wiki/fileName-6.md", "wiki", 1),
                /* 56  */ filePathInfoTestData("wiki/fileName-6.md", "wiki", 1),
                /* 57  */ filePathInfoTestData("/home/wiki/fileName-6.md", "/home/wiki", 1)
        );
    }

    private static Object[] filePathInfoTestData(String filePath, String wikiHome, int getUpDirectoriesToWikiHome) {
        Object[] result = new Object[34];
        String tmp;
        int itmp;
        String pathPrefix = filePath.isEmpty() || filePath.charAt(0) != '/' ? "" : "/";
        String filePathNoAnchor = (!FilenameUtils.getPath(filePath).isEmpty() ? pathPrefix + FilenameUtils.getPath(filePath) : "") + FilePathInfo.linkRefNoAnchor(FilenameUtils.getName(filePath));
        String anchor = FilePathInfo.linkRefAnchor(FilenameUtils.getName(filePath));

        /* @formatter:off */
/* 0: filePath, */                              result[0] = filePath;
/* 1: getExt, */                                result[1] = FilenameUtils.getExtension(filePathNoAnchor);
/* 2: getExtWithDot, */                         result[2] = (itmp = (tmp = FilenameUtils.getName(filePathNoAnchor)).lastIndexOf('.')) != -1 ? tmp.substring(itmp) : "";
/* 3: getFilePath, */                           result[3] = filePathNoAnchor;
/* 4: getFilePathNoExt, */                      result[4] = ((tmp = FilenameUtils.getPath(filePathNoAnchor)).length() > 1 ? pathPrefix + tmp : tmp) + FilenameUtils.getBaseName(filePathNoAnchor);
/* 5: getPath, */                               result[5] = ((tmp = FilenameUtils.getPath(filePathNoAnchor)).length() > 1 ? pathPrefix + tmp : tmp);
/* 6: getWikiHome, */                           result[6] = wikiHome;
/* 7: getFileName, */                           result[7] = FilenameUtils.getName(filePathNoAnchor);
/* 8: getFileNameNoExt, */                      result[8] = FilenameUtils.getBaseName(filePathNoAnchor);
/* 9: getFilePathAsWikiRef, */                  result[9] = filePathNoAnchor.replace('-', ' ');
/* 10: getFilePathNoExtAsWikiRef, */            result[10] = (((tmp = FilenameUtils.getPath(filePathNoAnchor)).length() > 1 ? pathPrefix + tmp : tmp) + FilenameUtils.getBaseName(filePathNoAnchor)).replace('-', ' ');
/* 11: getPathAsWikiRef, */                     result[11] = ((tmp = FilenameUtils.getPath(filePathNoAnchor)).equals(FilenameUtils.getName(filePathNoAnchor)) ? "" : (tmp.length() > 1 ? pathPrefix + tmp : tmp)).replace('-', ' ');
/* 12: getFileNameAsWikiRef, */                 result[12] = FilenameUtils.getName(filePathNoAnchor).replace('-', ' ');
/* 13: getFileNameNoExtAsWikiRef, */            result[13] = FilenameUtils.getBaseName(filePathNoAnchor).replace('-', ' ');
/* 14: hasWikiPageExt, */                       result[14] = FilenameUtils.getExtension(filePathNoAnchor).equals("md");
/* 15: containsSpaces, */                       result[15] = filePathNoAnchor.contains(" ");
/* 16: isWikiHome, */                           result[16] = FilenameUtils.getExtension(filePathNoAnchor).equals("wiki");
/* 17: isUnderWikiHome, */                      result[17] = !wikiHome.isEmpty();
/* 18: pathContainsSpaces, */                   result[18] = ((tmp = FilenameUtils.getPath(filePathNoAnchor)).equals(FilenameUtils.getName(filePathNoAnchor)) ? "" : (tmp.length() > 1 ? pathPrefix + tmp : tmp)).contains(" ");
/* 19: fileNameContainsSpaces, */               result[19] = FilenameUtils.getName(filePathNoAnchor).contains(" ");
/* 20: getUpDirectoriesToWikiHome, */           result[20] = getUpDirectoriesToWikiHome;
/* 21: containsAnchor, */                       result[21] = filePath.contains("#");
/* 22: pathContainsAnchor, */                   result[22] = FilenameUtils.getPath(filePath).contains("#");
/* 23: fileNameContainsAnchor, */               result[23] = FilenameUtils.getName(filePath).contains("#");
/* 24: getAnchor, */                            result[24] = (itmp = filePath.lastIndexOf('#')) < 0 ? "" : filePath.substring(itmp);
/* 25: getAnchorNoHash, */                      result[25] = (itmp = filePath.lastIndexOf('#')) < 0 ? "" : filePath.substring(itmp+1);
/* 26: getFilePathWithAnchor, */                result[26] = filePath;
/* 27: getFilePathWithAnchorAsWikiRef, */       result[27] = filePath.replace('-', ' ');
/* 28: getFilePathWithAnchorNoExtAsWikiRef, */  result[28] = (((tmp = FilenameUtils.getPath(filePath)).length() > 1 ? pathPrefix + tmp : tmp) + FilenameUtils.getBaseName(filePath)).replace('-', ' ');
/* 29: getFileNameWithAnchor, */                result[29] = FilenameUtils.getName(filePath);
/* 30: getFileNameWithAnchorAsWikiRef, */       result[30] = FilenameUtils.getName(filePath).replace('-', ' ');
/* 31: getFileNameWithAnchorNoExtAsWikiRef, */  result[31] = FilenameUtils.getBaseName(filePath).replace('-', ' ');
/* 32: getFileNameWithAnchorAsWikiRef, */       result[32] = FilenameUtils.getExtension(filePath);
/* 33: getFileNameWithAnchorNoExtAsWikiRef, */  result[33] = (itmp = (tmp = FilenameUtils.getName(filePath)).lastIndexOf('.')) != -1 ? tmp.substring(itmp) : "";

        /* @formatter:on */
        return result;
    }
}
