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

import junit.framework.TestCase;

public class FileReferenceListTest extends TestCase {
    FileReferenceList fileReferenceList = null;

    public static final String[] filePaths =
            {
                    "/Users/vlad/src/MarkdownTest/untitled",
                    "/Users/vlad/src/MarkdownTest/untitled/META-INF",
                    "/Users/vlad/src/MarkdownTest/untitled/META-INF/plugin.xml",
                    "/Users/vlad/src/MarkdownTest/untitled/resources",
                    "/Users/vlad/src/MarkdownTest/untitled/resources/test.wiki",
                    "/Users/vlad/src/MarkdownTest/untitled/resources/test.wiki/test",
                    "/Users/vlad/src/MarkdownTest/untitled/resources/test.wiki/Bad file Name.md",
                    "/Users/vlad/src/MarkdownTest/untitled/resources/test.wiki/Home.md",
                    "/Users/vlad/src/MarkdownTest/untitled/resources/test.wiki/link-test.md",
                    "/Users/vlad/src/MarkdownTest/untitled/resources/test.wiki/NOTICE-TEXT.md",
                    "/Users/vlad/src/MarkdownTest/untitled/resources/test.wiki/sample-document.md",
                    "/Users/vlad/src/MarkdownTest/untitled/resources/test.wiki/sampledocument.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test/test2",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test/test2/test32.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test/test2/wikifile.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test/makdown.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/asdfsadf",
                    "/Users/vlad/src/MarkdownTest/untitled/src/back-link.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/create-file-test.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/darcula.css",
                    "/Users/vlad/src/MarkdownTest/untitled/src/DefaultTestData.simple",
                    "/Users/vlad/src/MarkdownTest/untitled/src/DefaultTestData2.simple",
                    "/Users/vlad/src/MarkdownTest/untitled/src/definition-list.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/error-test.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/github-#6.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/github_6.source.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test.png",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test1.png",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test2.jpeg",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test3.gif",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test4.png",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test5.jpeg",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test6.jpeg",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test7.gif",
                    "/Users/vlad/src/MarkdownTest/untitled/src/gravizo-test8.jpg",
                    "/Users/vlad/src/MarkdownTest/untitled/src/link-test.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/new-tests-and-more.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/NOTICE.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/readme.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/readme_short.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/sample-document.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/short-test.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/Source-Preview.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/task-wrap.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/Test.java",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test.js",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test.txt",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test-document.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test-images.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/test-one.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/testasdfs.md",
                    "/Users/vlad/src/MarkdownTest/untitled/src/TestClass.java",
                    "/Users/vlad/src/MarkdownTest/untitled/src/testScriptShell.js",
                    "/Users/vlad/src/MarkdownTest/untitled/test",
                    "/Users/vlad/src/MarkdownTest/untitled/test/test.wiki",
                    "/Users/vlad/src/MarkdownTest/untitled/test/test.wiki/test",
                    "/Users/vlad/src/MarkdownTest/untitled/test/test.wiki/Bad file Name.md",
                    "/Users/vlad/src/MarkdownTest/untitled/test/test.wiki/Home.md",
                    "/Users/vlad/src/MarkdownTest/untitled/test/test.wiki/link-test.md",
                    "/Users/vlad/src/MarkdownTest/untitled/test/test.wiki/NOTICE-TEXT.md",
                    "/Users/vlad/src/MarkdownTest/untitled/test/test.wiki/sample-document.md",
                    "/Users/vlad/src/MarkdownTest/untitled/test/test.wiki/sampledocument.md",
                    "/Users/vlad/src/MarkdownTest/untitled/test.wiki",
                    "/Users/vlad/src/MarkdownTest/untitled/untitled.iml",
                    "/Users/vlad/src/MarkdownTest/untitled/test/test.wiki/sampledocument.md#withAnchor"
            };

    public static FileReferenceList loadFileList() {
        FileReferenceList.Builder builder = new FileReferenceList.Builder();

        for (String filePath : filePaths) {
            FileReference fileReference = new FileReference(filePath);
            String ext = fileReference.getExt();

            if (fileReference.hasPureAnchor()) {
                // pure anchor
                builder.add(new FileReference(fileReference.getFilePath()));
            } else {
                builder.add(fileReference);
            }
        }

        return new FileReferenceList(builder);
    }

    public void setUp() throws Exception {
        fileReferenceList = loadFileList();
    }
}
