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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@RunWith(value = Parameterized.class)
public class TestFileReferenceListQueryMatch extends FileReferenceListTest {
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    private String wikiRef;
    private FileReference[] wikiRefMatches;

    public TestFileReferenceListQueryMatch(String wikiRef, FileReference[] wikiRefMatches) {
        this.wikiRef = wikiRef;
        this.wikiRefMatches = wikiRefMatches;
    }

    @Parameterized.Parameters(name = "{index}: wikiRef({0})={1}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> results = new ArrayList<Object[]>();

        FileReferenceList fileReferenceList = loadFileList();

        FileReferenceList fileRefList = fileReferenceList.getQuery()
                .markdownFiles()
                .getList();

        HashMap<String, ArrayList<FileReference>> wikiRefs = new HashMap<String, ArrayList<FileReference>>();

        for (String filePath : filePaths) {
            FileReference fileReference = new FileReference(filePath);
            if (fileReference.isMarkdownExt()) {
                String wikiRef = fileReference.getFileNameNoExtAsWikiRef();
                if (!wikiRefs.containsKey(wikiRef)) {
                    wikiRefs.put(wikiRef, new ArrayList<FileReference>());
                }
                wikiRefs.get(wikiRef).add(fileReference);
            }
        }

        for (String wikiRef : wikiRefs.keySet()) {
            FileReferenceList wikiPageRefs = fileRefList.getQuery().markdownFiles().matchWikiRef(wikiRef).getAllWikiPageRefs();
            Object[] data = new Object[2];

            data[0] = wikiRef;
            data[1] = wikiRefs.get(wikiRef).toArray(new FileReference[0]);
            results.add(data);
        }
        return results;
    }

    @Test
    public void test_01_Match_WikiPageRef() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.getQuery().markdownFiles().matchWikiRef(wikiRef);
        FileReferenceList refs = fileReferenceListQuery.getAllWikiPageRefs();
        compareUnorderedLists(null, wikiRefMatches, refs);
    }
}
