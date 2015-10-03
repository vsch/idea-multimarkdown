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
import com.vladsch.idea.multimarkdown.util.FilePathInfo;

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
    private String getFilePathNoExtAsWikiRef;
    private String getPathAsWikiRef;
    private String getFileNameAsWikiRef;
    private String getFileNameNoExtAsWikiRef;
    private int getUpDirectoriesToWikiHome;

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
    int getUpDirectoriesToWikiHome
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
    //@Parameterized.Parameters(name = "{index}: getExt({0})={1}") public static Iterable<Object[]> data_getExt() { return data();}
    //@Parameterized.Parameters(name = "{index}: getFilePath({0})={2}") public static Iterable<Object[]> data_getFilePath() { return data();}
    //@Parameterized.Parameters(name = "{index}: getFilePathNoExt({0})={3}") public static Iterable<Object[]> data_getFilePathNoExt() { return data();}
    //@Parameterized.Parameters(name = "{index}: getPath({0})={4}") public static Iterable<Object[]> data_getPath() { return data();}
    //@Parameterized.Parameters(name = "{index}: getWikiHome({0})={5}") public static Iterable<Object[]> data_getWikiHome() { return data();}
    //@Parameterized.Parameters(name = "{index}: getFileName({0})={6}") public static Iterable<Object[]> data_getFileName() { return data();}
    //@Parameterized.Parameters(name = "{index}: getFileNameNoExt({0})={7}") public static Iterable<Object[]> data_getFileNameNoExt() { return data();}
    //@Parameterized.Parameters(name = "{index}: getFilePathAsWikiRef({0})={8}") public static Iterable<Object[]> data_getFilePathAsWikiRef() { return data();}
    //@Parameterized.Parameters(name = "{index}: getFilePathNoExtAsWikiRef({0})={9}") public static Iterable<Object[]> data_getFilePathNoExtAsWikiRef() { return data();}
    //@Parameterized.Parameters(name = "{index}: getPathAsWikiRef({0})={10}") public static Iterable<Object[]> data_getPathAsWikiRef() { return data();}
    //@Parameterized.Parameters(name = "{index}: getFileNameAsWikiRef({0})={11}") public static Iterable<Object[]> data_getFileNameAsWikiRef() { return data();}
    //@Parameterized.Parameters(name = "{index}: getFileNameNoExtAsWikiRef({0})={12}") public static Iterable<Object[]> data_getFileNameNoExtAsWikiRef() { return data();}
    //@Parameterized.Parameters(name = "{index}: hasWikiPageExt({0})={13}") public static Iterable<Object[]> data_hasWikiPageExt() { return data();}
    //@Parameterized.Parameters(name = "{index}: containsSpaces({0})={14}") public static Iterable<Object[]> data_containsSpaces() { return data();}
    //@Parameterized.Parameters(name = "{index}: isWikiHome({0})={15}") public static Iterable<Object[]> data_isWikiHome() { return data();}
    //@Parameterized.Parameters(name = "{index}: isUnderWikiHome({0})={16}") public static Iterable<Object[]> data_isUnderWikiHome() { return data();}
    //@Parameterized.Parameters(name = "{index}: pathContainsSpaces({0})={17}") public static Iterable<Object[]> data_pathContainsSpaces() { return data();}
    //@Parameterized.Parameters(name = "{index}: fileNameContainsSpaces({0})={18}") public static Iterable<Object[]> data_fileNameContainsSpaces() { return data();}

    @Parameterized.Parameters
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
                /* 20 */ filePathInfoTestData("/somepath/home.wiki/file-Name", "/somepath/home.wiki",1),
                /* 21 */ filePathInfoTestData("/somepath/home.wiki/fileName.md", "/somepath/home.wiki",1),
                /* 22 */ filePathInfoTestData("/somepath/home.wiki/file-Name.md", "/somepath/home.wiki",1),
                /* 23 */ filePathInfoTestData("/somepath/home.wiki/path/with/fileName.md", "/somepath/home.wiki",3),
                /* 24 */ filePathInfoTestData("/somepath/home.wiki/path/with/file-Name.md", "/somepath/home.wiki",3),
                /* 25 */ filePathInfoTestData("/somepath/home.wiki/pathName/with/fileName.md", "/somepath/home.wiki",3),
                /* 26 */ filePathInfoTestData("/somepath/home.wiki/path-Name/with/file-Name.md", "/somepath/home.wiki",3),
                /* 27 */ filePathInfoTestData("/somepath/is-home.wiki/file-Name", "/somepath/is-home.wiki", 1),
                /* 28 */ filePathInfoTestData("/somepath/is-home.wiki/fileName.md", "/somepath/is-home.wiki", 1),
                /* 29 */ filePathInfoTestData("/somepath/is-home.wiki/file-Name.md", "/somepath/is-home.wiki", 1),
                /* 30 */ filePathInfoTestData("/somepath/is-home.wiki/path/with/fileName.md", "/somepath/is-home.wiki", 3),
                /* 31 */ filePathInfoTestData("/somepath/is-home.wiki/path/with/file-Name.md", "/somepath/is-home.wiki", 3),
                /* 32 */ filePathInfoTestData("/somepath/is-home.wiki/pathName/with/fileName.md", "/somepath/is-home.wiki", 3),
                /* 33 */ filePathInfoTestData("/somepath/is-home.wiki/pathName/with/fileName.", "/somepath/is-home.wiki", 3),
                /* 34 */ filePathInfoTestData("/somepath/is-home.wiki/path/file-Name.md", "/somepath/is-home.wiki", 2),
                /* 35 */ filePathInfoTestData("/somepath/is-home.wiki/path/path2/path3/file-Name.md", "/somepath/is-home.wiki", 4)
                );
    }

    private static Object[] filePathInfoTestData(String filePath, String wikiHome, int getUpDirectoriesToWikiHome) {
        Object[] result = new Object[21];
        String tmp;
        int itmp;
        /* @formatter:off */
        /* 0: filePath, */                   result[0] = filePath;
        /* 1: getExt, */                     result[1] = FilenameUtils.getExtension(filePath);
        /* 2: getExtWithDot, */              result[2] = (itmp = (tmp = FilenameUtils.getName(filePath)).lastIndexOf('.')) != -1 ? tmp.substring(itmp) : "";
        /* 3: getFilePath, */                result[3] = filePath;
        /* 4: getFilePathNoExt, */           result[4] = ((tmp = FilenameUtils.getPath(filePath)).length() > 1 ? "/" + tmp : tmp) + FilenameUtils.getBaseName(filePath);//filePath.substring(1, filePath.length() - FilenameUtils.getExtension(filePath).length());
        /* 5: getPath, */                    result[5] = ((tmp = FilenameUtils.getPath(filePath)).length() > 1 ? "/" + tmp : tmp);
        /* 6: getWikiHome, */                result[6] = wikiHome;
        /* 7: getFileName, */                result[7] = FilenameUtils.getName(filePath);
        /* 8: getFileNameNoExt, */           result[8] = FilenameUtils.getBaseName(filePath); //.substring(1, FilenameUtils.getName(filePath).length() - (itmp = FilenameUtils.getExtension(filePath).length()) > 1 ? itmp + 2 : 1);
        /* 9: getFilePathAsWikiRef, */       result[9] = filePath.replace('-', ' ');
        /* 10: getFilePathNoExtAsWikiRef, */  result[10] = (((tmp = FilenameUtils.getPath(filePath)).length() > 1 ? "/" + tmp : tmp) + FilenameUtils.getBaseName(filePath)).replace('-', ' ');
        /* 11: getPathAsWikiRef, */          result[11] = ((tmp = FilenameUtils.getPath(filePath)).equals(FilenameUtils.getName(filePath)) ? "" : (tmp.length() > 1 ? "/" + tmp : tmp)).replace('-', ' ');
        /* 12: getFileNameAsWikiRef, */      result[12] = FilenameUtils.getName(filePath).replace('-', ' ');
        /* 13: getFileNameNoExtAsWikiRef, */ result[13] = FilenameUtils.getBaseName(filePath).replace('-', ' ');
        /* 14: hasWikiPageExt, */            result[14] = FilenameUtils.getExtension(filePath).equals("md");
        /* 15: containsSpaces, */            result[15] = filePath.contains(" ");
        /* 16: isWikiHome, */                result[16] = FilenameUtils.getExtension(filePath).equals("wiki");
        /* 17: isUnderWikiHome, */           result[17] = (((tmp = FilenameUtils.getPath(filePath)).equals(FilenameUtils.getName(filePath)) ? "" : (tmp.length() > 1 ? "/" + tmp : tmp))+"/").contains(".wiki/");
        /* 18: pathContainsSpaces, */        result[18] = ((tmp = FilenameUtils.getPath(filePath)).equals(FilenameUtils.getName(filePath)) ? "" : (tmp.length() > 1 ? "/" + tmp : tmp)).contains(" ");
        /* 19: fileNameContainsSpaces, */    result[19] = FilenameUtils.getName(filePath).contains(" ");
        /* 20: getUpDirectoriesToWikiHome, */    result[20] = getUpDirectoriesToWikiHome;
        /* @formatter:on */
        return result;
    }
}
