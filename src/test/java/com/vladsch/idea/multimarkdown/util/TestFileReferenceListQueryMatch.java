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

import static com.vladsch.idea.multimarkdown.TestUtils.compareUnorderedLists;

@RunWith(value = Parameterized.class)
public class TestFileReferenceListQueryMatch extends FileReferenceListTest {
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    private static FileReference[] EMPTY = new FileReference[0];

    private final String linkRef;
    private final FileReference[] linkRefMatches;
    private final String linkRefNoExt;
    private final FileReference[] linkRefNoExtMatches;
    private final String wikiRef;
    private final FileReference[] wikiRefMatches;

    public TestFileReferenceListQueryMatch(String linkRef, FileReference[] linkRefMatches, String linkRefNoExt, FileReference[] linkRefNoExtMatches, String wikiRef, FileReference[] wikiRefMatches) {
        this.linkRef = linkRef;
        this.linkRefMatches = linkRefMatches;
        this.linkRefNoExt = linkRefNoExt;
        this.linkRefNoExtMatches = linkRefNoExtMatches;
        this.wikiRef = wikiRef;
        this.wikiRefMatches = wikiRefMatches;
    }

    @Parameterized.Parameters(name = "{index}: linkRef {0} linkRefNoExt {2} wikiRef {4}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> results = new ArrayList<Object[]>();

        FileReferenceList fileReferenceList = loadFileList();

        FileReferenceList fileRefList = fileReferenceList.query()
                .wantMarkdownFiles()
                .all();

        HashMap<String, ArrayList<FileReference>> linkRefs = new HashMap<String, ArrayList<FileReference>>();
        HashMap<String, ArrayList<FileReference>> linkRefsNoExt = new HashMap<String, ArrayList<FileReference>>();
        HashMap<String, ArrayList<FileReference>> wikiRefs = new HashMap<String, ArrayList<FileReference>>();

        for (String filePath : filePaths) {
            FileReference fileReference = new FileReference(filePath);
            if (fileReference.isMarkdownExt()) {
                String linkRef = fileReference.getFileName();
                if (!linkRefs.containsKey(linkRef)) {
                    linkRefs.put(linkRef, new ArrayList<FileReference>());
                }
                linkRefs.get(linkRef).add(fileReference);

                String linkRefNoExt = fileReference.getFileNameNoExt();
                if (!linkRefsNoExt.containsKey(linkRefNoExt)) {
                    linkRefsNoExt.put(linkRefNoExt, new ArrayList<FileReference>());
                }
                linkRefsNoExt.get(linkRefNoExt).add(fileReference);

                String wikiRef = fileReference.getFileNameNoExtAsWikiRef();
                if (!wikiRefs.containsKey(wikiRef)) {
                    wikiRefs.put(wikiRef, new ArrayList<FileReference>());
                }
                wikiRefs.get(wikiRef).add(fileReference);
            }
        }

        for (String linkRef : linkRefs.keySet()) {
            Object[] data = new Object[6];
            String linkRefNoExt = new FilePathInfo(linkRef).getFilePathNoExt();
            String wikiRef = FilePathInfo.asWikiRef(linkRefNoExt);


            data[0] = linkRef;
            data[1] = linkRefs.get(linkRef).toArray(EMPTY);
            data[2] = linkRefNoExt;
            data[3] = !linkRefsNoExt.containsKey(linkRefNoExt) ? EMPTY : linkRefsNoExt.get(linkRefNoExt).toArray(EMPTY);
            data[4] = wikiRef;
            data[5] = !wikiRefs.containsKey(wikiRef) ? EMPTY : wikiRefs.get(wikiRef).toArray(EMPTY);
            results.add(data);
        }
        return results;
    }

    @Test
    public void test_01_Match_LinkRef() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchLinkRef(linkRef);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, (linkRef.indexOf('#') >= 0) ? EMPTY  : linkRefMatches, refs);
    }

    @Test
    public void test_02_Match_DotLinkRef() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchLinkRef("./" + linkRef);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, (linkRef.indexOf('#') >= 0) ? EMPTY  : linkRefMatches, refs);
    }

    @Test
    public void test_03_Match_LinkRefNoExt() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchLinkRefNoExt(linkRefNoExt);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, (linkRef.indexOf('#') >= 0) ? EMPTY  : linkRefNoExtMatches, refs);
    }

    @Test
    public void test_04_Match_DotLinkRefNoExt() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchLinkRefNoExt("./" + linkRefNoExt);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, (linkRef.indexOf('#') >= 0) ? EMPTY  : linkRefNoExtMatches, refs);
    }

    @Test
    public void test_05_Match_WikiPageRef() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchWikiRef(wikiRef);
        FileReferenceList refs = fileReferenceListQuery.allWikiPageRefs();
        compareUnorderedLists(null, (linkRef.indexOf('#') >= 0) ? EMPTY  : wikiRefMatches, refs);
    }

    @Test
    public void test_06_Match_DotWikiPageRef() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchWikiRef("./" + wikiRef);
        FileReferenceList refs = fileReferenceListQuery.allWikiPageRefs();
        compareUnorderedLists(null, (linkRef.indexOf('#') >= 0) ? EMPTY  : wikiRefMatches, refs);
    }

    @Test
    public void test_07_Match_LinkRefAnchor() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().keepLinkRefAnchor().matchLinkRef(linkRef);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, linkRefMatches, refs);
    }

    @Test
    public void test_08_Match_DotLinkRefAnchor() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().keepLinkRefAnchor().matchLinkRef("./" + linkRef);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, linkRefMatches, refs);
    }

    @Test
    public void test_09_Match_LinkRefNoExtAnchor() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().keepLinkRefAnchor().matchLinkRefNoExt(linkRefNoExt);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, linkRefNoExtMatches, refs);
    }

    @Test
    public void test_10_Match_DotLinkRefNoExtAnchor() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().keepLinkRefAnchor().matchLinkRefNoExt("./" + linkRefNoExt);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, linkRefNoExtMatches, refs);
    }

    @Test
    public void test_11_Match_WikiPageRefAnchor() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().keepLinkRefAnchor().matchWikiRef(wikiRef);
        FileReferenceList refs = fileReferenceListQuery.allWikiPageRefs();
        compareUnorderedLists(null, wikiRefMatches, refs);
    }

    @Test
    public void test_12_Match_DotWikiPageRefAnchor() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().keepLinkRefAnchor().matchWikiRef("./" + wikiRef);
        FileReferenceList refs = fileReferenceListQuery.allWikiPageRefs();
        compareUnorderedLists(null, wikiRefMatches, refs);
    }
}
