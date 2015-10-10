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

import java.util.ArrayList;
import java.util.HashMap;

import static com.vladsch.idea.multimarkdown.TestUtils.compareUnorderedLists;
import static org.junit.Assert.assertArrayEquals;

public class TestFileReferenceListBuilder extends FileReferenceListTest {
    FileReferenceList.Builder builder = new FileReferenceList.Builder();
    HashMap<String, ArrayList<Integer>> extMap = new HashMap<String, ArrayList<Integer>>();
    ArrayList<String> filePathList = new ArrayList<String>();

    FileReference[] fileReferences;
    int[][] extFileReferenceIndices;
    String[] extensions;

    @Before
    public void setUp() throws Exception {
        for (String filePath : filePaths) {
            FilePathInfo pathInfo = new FilePathInfo(filePath);
            String ext = pathInfo.getExt();
            String withAnchorExt = pathInfo.getWithAnchorExt();
            FileReference fileReference = new FileReference(filePath);

            builder.add(fileReference);

            if (!extMap.containsKey(ext)) {
                extMap.put(ext, new ArrayList<Integer>());
            }
            extMap.get(ext).add(filePathList.size());

            if (!ext.equals(withAnchorExt)) {
                if (!extMap.containsKey(withAnchorExt)) {
                    extMap.put(withAnchorExt, new ArrayList<Integer>());
                }
                extMap.get(withAnchorExt).add(filePathList.size());
            }

            filePathList.add(pathInfo.getFilePathWithAnchor());
        }

        fileReferences = builder.getFileReferences();
        extFileReferenceIndices = builder.getExtensionFileReferences();
        extensions = builder.getExtensions();

        fileReferenceList = new FileReferenceList(builder);
    }

    @Test
    public void test_01_FileReferencesCount() throws Exception {
        assertEquals(filePathList.size(), fileReferences.length);
    }

    @Test
    public void test_02_FileReferencesContent() throws Exception {
        int index = 0;
        for (String filePath : filePathList) {
            assertEquals(filePath, fileReferences[index].getFilePathWithAnchor());
            index++;
        }
    }

    @Test
    public void test_03_ExtensionsCount() throws Exception {
        assertEquals(extMap.keySet().size(), extensions.length);
    }

    @Test
    public void test_04_ExtensionsContent() throws Exception {
        int index = 0;
        for (String ext : extMap.keySet()) {
            assertEquals(ext, extensions[index++]);
        }
    }

    @Test
    public void test_05_ExtFileReferenceIndicesCount() throws Exception {
        int index = 0;
        for (String ext : extMap.keySet()) {
            if (extMap.get(ext).size() != extFileReferenceIndices[index].length) {
                failNotEquals("Counts differ for ext " + ext, extMap.size(), extFileReferenceIndices[index].length);
            }
            index++;
        }
    }

    @Test
    public void test_06_ExtFileReferenceIndicesContent() throws Exception {
        int extIndex = 0;
        for (String ext : extMap.keySet()) {
            assertEquals(extMap.get(ext).size(), extFileReferenceIndices[extIndex].length);

            ArrayList<Integer> extIndices = extMap.get(ext);
            int index = 0;
            for (Integer extIndexOffs : extIndices) {
                if (extIndexOffs != extFileReferenceIndices[extIndex][index]) {
                    failNotEquals("ext " + ext + "[" + index + "] differs", extIndexOffs, extFileReferenceIndices[extIndex][index]);
                }
                index++;
            }
            extIndex++;
        }
    }

    @Test
    public void test_07_NewListFromBuilder() throws Exception {
        assertArrayEquals(fileReferences, fileReferenceList.get());
        assertArrayEquals(extensions, fileReferenceList.getExtensions());
        assertArrayEquals(extFileReferenceIndices, fileReferenceList.getExtensionFileRefIndices());
    }

    @Test
    public void test_08_FilterFileType_All() throws Exception {
        FileReferenceList allFiles = fileReferenceList.query()
                .wantAllFiles()
                .all();

        assertArrayEquals(fileReferences, fileReferenceList.get());
        assertArrayEquals(extensions, fileReferenceList.getExtensions());
        assertArrayEquals(extFileReferenceIndices, fileReferenceList.getExtensionFileRefIndices());
    }

    @Test
    public void test_09_FilterFileType_Image() throws Exception {
        FileReferenceList fileRefList = fileReferenceList.query()
                .wantImageFiles()
                .all();

        FileReferenceList.Builder filesBuilder = new FileReferenceList.Builder();

        for (String filePath : filePathList) {
            FileReference fileReference = new FileReference(filePath);
            if (fileReference.isImageExt()) {
                filesBuilder.add(fileReference);
            }
        }
        compareUnorderedLists(null, filesBuilder, fileRefList);
    }

    @Test
    public void test_10_FilterFileType_Markdown() throws Exception {
        FileReferenceList fileRefList = fileReferenceList.query()
                .wantMarkdownFiles()
                .all();

        FileReferenceList.Builder filesBuilder = new FileReferenceList.Builder();

        for (String filePath : filePathList) {
            FileReference fileReference = new FileReference(filePath);
            if (fileReference.isMarkdownExt() || FilePathInfo.isMarkdownExt(fileReference.getWithAnchorExt())) {
                filesBuilder.add(fileReference);
            }
        }
        compareUnorderedLists(null, filesBuilder, fileRefList);
    }

    @Test
    public void test_11_FilterFileType_WikiPage() throws Exception {
        FileReferenceList fileRefList = fileReferenceList.query()
                .wantWikiPages()
                .all();

        FileReferenceList.Builder filesBuilder = new FileReferenceList.Builder();

        for (String filePath : filePathList) {
            FileReference fileReference = new FileReference(filePath);
            if (fileReference.isWikiPage() || (fileReference.isUnderWikiHome() && FilePathInfo.isWikiPageExt(fileReference.getWithAnchorExt()))) {
                filesBuilder.add(fileReference);
            }
        }
        compareUnorderedLists(null, filesBuilder, fileRefList);
    }
}
