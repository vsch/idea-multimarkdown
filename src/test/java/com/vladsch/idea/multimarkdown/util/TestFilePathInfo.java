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
import static org.junit.Assert.assertFalse;

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

    private String getProjectHome;
    private boolean hasWithAnchorExt;

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
    /* 34: getProjectHome, */
    /* 35: hasWithAnchorExt, */
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
            String getWithAnchorExtWithDot,
            String getProjectHome,
            boolean hasWithAnchorExt
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
        this.getWithAnchorExt = getWithAnchorExt;
        this.getWithAnchorExtWithDot = getWithAnchorExtWithDot;
        this.getProjectHome = getProjectHome;
        this.hasWithAnchorExt = hasWithAnchorExt;
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
    @Test public void test_getProjectHome() { assertEquals(getProjectHome, filePathInfo.getProjectHome()); }
    @Test public void test_hasWithAnchorExt() { assertEquals(hasWithAnchorExt, filePathInfo.hasWithAnchorExt()); }

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
    /* 34: getProjectHome, */
    @Parameterized.Parameters(name = "{index}: filePath = {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(
                /*  0*/  filePathInfoTestData("file-Name", "", 0, ""),
                /*  1*/  filePathInfoTestData("fileName.md", "", 0, ""),
                /*  2*/  filePathInfoTestData("file-Name.md", "", 0, ""),
                /*  3*/  filePathInfoTestData("/path/with/fileName.md", "", 0, ""),
                /*  4*/  filePathInfoTestData("/path/with/file-Name.md", "", 0, ""),
                /*  5*/  filePathInfoTestData("/pathName/with/fileName.md", "", 0, ""),
                /*  6*/  filePathInfoTestData("/path-Name/with/file-Name.md", "", 0, ""),
                /*  7*/  filePathInfoTestData("/home/home.wiki/file-Name", "/home/home.wiki", 1, "/home"),
                /*  8*/  filePathInfoTestData("/home/home.wiki/fileName.md", "/home/home.wiki", 1, "/home"),
                /*  9 */ filePathInfoTestData("/home/home.wiki/file-Name.md", "/home/home.wiki", 1, "/home"),
                /* 10 */ filePathInfoTestData("/home/home.wiki/path/with/fileName.md", "/home/home.wiki", 3, "/home"),
                /* 11 */ filePathInfoTestData("/home/home.wiki/path/with/file-Name.md", "/home/home.wiki", 3, "/home"),
                /* 12 */ filePathInfoTestData("/home/home.wiki/pathName/with/fileName.md", "/home/home.wiki", 3, "/home"),
                /* 13 */ filePathInfoTestData("/is-home/is-home.wiki/file-Name", "/is-home/is-home.wiki", 1, "/is-home"),
                /* 14 */ filePathInfoTestData("/is-home/is-home.wiki/fileName.md", "/is-home/is-home.wiki", 1, "/is-home"),
                /* 15 */ filePathInfoTestData("/is-home/is-home.wiki/file-Name.md", "/is-home/is-home.wiki", 1, "/is-home"),
                /* 16 */ filePathInfoTestData("/is-home/is-home.wiki/path/with/fileName.md", "/is-home/is-home.wiki", 3, "/is-home"),
                /* 17 */ filePathInfoTestData("/is-home/is-home.wiki/path/with/file-Name.md", "/is-home/is-home.wiki", 3, "/is-home"),
                /* 18 */ filePathInfoTestData("/is-home/is-home.wiki/pathName/with/fileName.md", "/is-home/is-home.wiki", 3, "/is-home"),
                /* 19 */ filePathInfoTestData("/somepath/home.wiki/path-Name/with/file-Name.md", "", 0, ""),
                /* 20 */ filePathInfoTestData("/home/home.wiki/file-Name", "/home/home.wiki", 1, "/home"),
                /* 21 */ filePathInfoTestData("/home/home.wiki/fileName.md", "/home/home.wiki", 1, "/home"),
                /* 22 */ filePathInfoTestData("/home/home.wiki/file-Name.md", "/home/home.wiki", 1, "/home"),
                /* 23 */ filePathInfoTestData("/home/home.wiki/path/with/fileName.md", "/home/home.wiki", 3, "/home"),
                /* 24 */ filePathInfoTestData("/home/home.wiki/path/with/file-Name.md", "/home/home.wiki", 3, "/home"),
                /* 25 */ filePathInfoTestData("/home/home.wiki/pathName/with/fileName.md", "/home/home.wiki", 3, "/home"),
                /* 26 */ filePathInfoTestData("/home/home.wiki/path-Name/with/file-Name.md", "/home/home.wiki", 3, "/home"),
                /* 27 */ filePathInfoTestData("/is-home/is-home.wiki/file-Name", "/is-home/is-home.wiki", 1, "/is-home"),
                /* 28 */ filePathInfoTestData("/is-home/is-home.wiki/fileName.md", "/is-home/is-home.wiki", 1, "/is-home"),
                /* 29 */ filePathInfoTestData("/is-home/is-home.wiki/file-Name.md", "/is-home/is-home.wiki", 1, "/is-home"),
                /* 30 */ filePathInfoTestData("/is-home/is-home.wiki/path/with/fileName.md", "/is-home/is-home.wiki", 3, "/is-home"),
                /* 31 */ filePathInfoTestData("/is-home/is-home.wiki/path/with/file-Name.md", "/is-home/is-home.wiki", 3, "/is-home"),
                /* 32 */ filePathInfoTestData("/is-home/is-home.wiki/pathName/with/fileName.md", "/is-home/is-home.wiki", 3, "/is-home"),
                /* 33 */ filePathInfoTestData("/is-home/is-home.wiki/pathName/with/fileName.", "/is-home/is-home.wiki", 3, "/is-home"),
                /* 34 */ filePathInfoTestData("/is-home/is-home.wiki/path/file-Name.md", "/is-home/is-home.wiki", 2, "/is-home"),
                /* 35 */ filePathInfoTestData("/is-home/is-home.wiki/path/path2/path3/file-Name.md", "/is-home/is-home.wiki", 4, "/is-home"),
                /* 36  */ filePathInfoTestData("file-Name#", "", 0, ""),
                /* 37  */ filePathInfoTestData("file-Name#anchor", "", 0, ""),
                /* 38  */ filePathInfoTestData("fileName#.md", "", 0, "", true),
                /* 39  */ filePathInfoTestData("fileName#anchor.md", "", 0, "", true),
                /* 40  */ filePathInfoTestData("file-Name#.md", "", 0, "", true),
                /* 41  */ filePathInfoTestData("file-Name#anchor.md", "", 0, "", true),
                /* 42  */ filePathInfoTestData("/path/with/fileName#.md", "", 0, "", true),
                /* 43  */ filePathInfoTestData("/path/with/fileName#anchor.md", "", 0, "", true),
                /* 44  */ filePathInfoTestData("/path/with/file-#Name.md", "", 0, "", true),
                /* 45  */ filePathInfoTestData("/path/with/file-#anchorName.md", "", 0, "", true),
                /* 46  */ filePathInfoTestData("/pathName/with/#fileName.md", "", 0, "", true),
                /* 47  */ filePathInfoTestData("/pathName/with/#anchorfileName.md", "", 0, "", true),
                /* 48  */ filePathInfoTestData("/path-Name/with/file-Name.md", "", 0, ""),
                /* 49  */ filePathInfoTestData("/home/home.wiki/file-Name-#6", "/home/home.wiki", 1, "/home"),
                /* 50  */ filePathInfoTestData("/home/home.wiki/fileName-6.md", "/home/home.wiki", 1, "/home"),
                /* 51  */ filePathInfoTestData("/another.wiki/home.wiki/fileName-6.md", "", 0, ""),
                /* 52  */ filePathInfoTestData("/another.wiki/home/home.wiki/fileName-6.md", "/another.wiki/home/home.wiki", 1, "/another.wiki/home"),
                /* 53  */ filePathInfoTestData("/wiki/fileName-6.md", "", 0, ""),
                /* 54  */ filePathInfoTestData("/another.wiki/another.wiki/fileName-6.md", "", 0, ""),
                /* 55  */ filePathInfoTestData("/funny/funny.wiki/fileName-6.md", "/funny/funny.wiki", 1, "/funny"),
                /* 56  */ filePathInfoTestData("/funny/funny.wiki/fileName-6.md", "/funny/funny.wiki", 1, "/funny"),
                /* 57  */ filePathInfoTestData("/home/home.wiki/fileName-6.md", "/home/home.wiki", 1, "/home"),
                /* 58  */ filePathInfoTestData("/home/home.wiki/fileName-6.md#test", "/home/home.wiki", 1, "/home", true),
                /* 59  */ filePathInfoTestData("/home/home.wiki/fileName-6.md#test.md", "/home/home.wiki", 1, "/home", true),
                /* 60  */ filePathInfoTestData("/home/home.wiki/fileName-6#test.md", "/home/home.wiki", 1, "/home", true)
        );
    }

    private static Object[] filePathInfoTestData(String filePath, String wikiHome, int getUpDirectoriesToWikiHome, String projectHome) {
        return filePathInfoTestData(filePath, wikiHome, getUpDirectoriesToWikiHome, projectHome, false);
    }

    private static Object[] filePathInfoTestData(String filePath, String wikiHome, int getUpDirectoriesToWikiHome, String projectHome, boolean hasWithAnchorExt) {
        Object[] result = new Object[36];
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
/* 34: getProjectHome, */                       result[34] = projectHome;
/* 34: hasWithAnchorExt, */                     result[35] = ((String)result[24]).isEmpty() ? !((String)result[1]).isEmpty() : hasWithAnchorExt;


        /* @formatter:on */
        return result;
    }
}
