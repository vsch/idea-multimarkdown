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
import java.util.HashSet;

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
    private final String linkRefNoAnchor;
    private final FileReference[] linkRefNoAnchorMatches;
    private final String linkRefNoAnchorNoExt;
    private final FileReference[] linkRefNoAnchorNoExtMatches;
    private final String wikiRef;
    private final FileReference[] wikiRefMatches;

    public TestFileReferenceListQueryMatch(String linkRef, FileReference[] linkRefMatches, String linkRefNoExt, FileReference[] linkRefNoExtMatches, String wikiRef, FileReference[] wikiRefMatches,String linkRefNoAnchor, FileReference[] linkRefNoAnchorMatches, String linkRefNoAnchorNoExt, FileReference[] linkRefNoAnchorNoExtMatches) {
        this.linkRef = linkRef;
        this.linkRefMatches = linkRefMatches;
        this.linkRefNoExt = linkRefNoExt;
        this.linkRefNoExtMatches = linkRefNoExtMatches;
        this.wikiRef = wikiRef;
        this.wikiRefMatches = wikiRefMatches;
        this.linkRefNoAnchor = linkRefNoAnchor;
        this.linkRefNoAnchorMatches = linkRefNoAnchorMatches;
        this.linkRefNoAnchorNoExt = linkRefNoAnchorNoExt;
        this.linkRefNoAnchorNoExtMatches = linkRefNoAnchorNoExtMatches;
    }

    @Parameterized.Parameters(name = "{index}: linkRef {0} linkRefNoExt {2} wikiRef {4}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> results = new ArrayList<Object[]>();

        FileReferenceList fileReferenceList = loadFileList();

        FileReferenceList fileRefList = fileReferenceList.query()
                .wantMarkdownFiles()
                .all();

        HashMap<String, HashMap<String, FileReference>> linkRefs = new HashMap<String, HashMap<String, FileReference>>();
        HashMap<String, HashMap<String, FileReference>> linkRefsNoAnchor = new HashMap<String, HashMap<String, FileReference>>();
        HashMap<String, HashMap<String, FileReference>> linkRefsNoExt = new HashMap<String, HashMap<String, FileReference>>();
        HashMap<String, HashMap<String, FileReference>> linkRefsNoAnchorNoExt = new HashMap<String, HashMap<String, FileReference>>();
        HashMap<String, HashMap<String, FileReference>> wikiRefs = new HashMap<String, HashMap<String, FileReference>>();

        for (String filePath : filePaths) {
            FilePathInfo filePathInfo = new FilePathInfo(filePath);
            FileReference fileReference = new FileReference(filePathInfo.hasPureAnchor() ? filePathInfo.getFilePath() : filePath);

            if (fileReference.isMarkdownExt()) {
                String linkRef = fileReference.getFileNameWithAnchor();
                if (!linkRefs.containsKey(linkRef)) {
                    linkRefs.put(linkRef, new HashMap<String, FileReference>());
                }
                linkRefs.get(linkRef).put(fileReference.getFullFilePath(), fileReference);

                String linkRefNoAnchor = fileReference.getFileName();
                if (!linkRefsNoAnchor.containsKey(linkRefNoAnchor)) {
                    linkRefsNoAnchor.put(linkRefNoAnchor, new HashMap<String, FileReference>());
                }
                linkRefsNoAnchor.get(linkRefNoAnchor).put(fileReference.getFullFilePath(), fileReference);

                String linkRefNoExt = fileReference.getFileNameNoExt() + fileReference.getAnchor();
                if (!linkRefsNoExt.containsKey(linkRefNoExt)) {
                    linkRefsNoExt.put(linkRefNoExt, new HashMap<String, FileReference>());
                }
                linkRefsNoExt.get(linkRefNoExt).put(fileReference.getFullFilePath(), fileReference);

                String linkRefNoAnchorNoExt = fileReference.getFileNameNoExt();
                if (!linkRefsNoAnchorNoExt.containsKey(linkRefNoAnchorNoExt)) {
                    linkRefsNoAnchorNoExt.put(linkRefNoAnchorNoExt, new HashMap<String, FileReference>());
                }
                linkRefsNoAnchorNoExt.get(linkRefNoAnchorNoExt).put(fileReference.getFullFilePath(), fileReference);

                String wikiRef = fileReference.getFileNameNoExtAsWikiRef();
                if (!wikiRefs.containsKey(wikiRef)) {
                    wikiRefs.put(wikiRef, new HashMap<String, FileReference>());
                }
                wikiRefs.get(wikiRef).put(fileReference.getFullFilePath(), fileReference);
            }
        }

        for (String linkRef : linkRefs.keySet()) {
            Object[] data = new Object[10];
            String linkRefNoAnchor = new FilePathInfo(linkRef).getFilePath();
            String linkRefNoExt = new FilePathInfo(linkRef).getFilePathWithAnchorNoExt();
            String linkRefNoAnchorNoExt = new FilePathInfo(linkRef).getFilePathNoExt();
            String wikiRef = FilePathInfo.asWikiRef(linkRefNoExt);


            data[0] = linkRef;
            data[1] = linkRefs.get(linkRef).values().toArray(EMPTY);
            data[2] = linkRefNoExt;
            data[3] = !linkRefsNoExt.containsKey(linkRefNoExt) ? EMPTY : linkRefsNoExt.get(linkRefNoExt).values().toArray(EMPTY);
            data[4] = wikiRef;
            data[5] = !wikiRefs.containsKey(wikiRef) ? EMPTY : wikiRefs.get(wikiRef).values().toArray(EMPTY);
            data[6] = linkRefNoAnchor;
            data[7] = linkRefsNoAnchor.get(linkRefNoAnchor).values().toArray(EMPTY);
            data[8] = linkRefNoAnchorNoExt;
            data[9] = linkRefsNoAnchorNoExt.get(linkRefNoAnchorNoExt).values().toArray(EMPTY);
            results.add(data);
        }
        return results;
    }

    @Test
    public void test_01_Match_LinkRef() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchLinkRef(linkRefNoAnchor);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, linkRefNoAnchorMatches, refs);
    }

    @Test
    public void test_02_Match_DotLinkRef() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchLinkRef("./" + linkRef);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, linkRefMatches, refs);
    }

    @Test
    public void test_03_Match_LinkRefNoExt() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchLinkRefNoExt(linkRefNoExt);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, linkRefNoExtMatches, refs);
    }

    @Test
    public void test_04_Match_DotLinkRefNoExt() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchLinkRefNoExt("./" + linkRefNoExt);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, linkRefNoExtMatches, refs);
    }

    @Test
    public void test_05_Match_WikiPageRef() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchWikiRef(wikiRef);
        FileReferenceList refs = fileReferenceListQuery.allWikiPageRefs();
        compareUnorderedLists(null, wikiRefMatches, refs);
    }

    @Test
    public void test_06_Match_DotWikiPageRef() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().matchWikiRef("./" + wikiRef);
        FileReferenceList refs = fileReferenceListQuery.allWikiPageRefs();
        compareUnorderedLists(null, wikiRefMatches, refs);
    }

    @Test
    public void test_07_Match_LinkRefAnchor() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().keepLinkRefAnchor().matchLinkRef(linkRef);
        FileReferenceList refs = fileReferenceListQuery.all();
        compareUnorderedLists(null, linkRefMatches, refs);
    }

    @Test
    public void test_07_Match_LinkRefAnchorNoAnchor() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query().wantMarkdownFiles().keepLinkRefAnchor();
        FileReferenceList refs = fileReferenceListQuery.all();
        String linkRefNoAnchor = new FilePathInfo(linkRef).getFileName();
        FileReferenceList refs1 = refs.query().matchLinkRef(linkRefNoAnchor).all();
        compareUnorderedLists(null, linkRefMatches, refs1);
    }

    @Test
    public void test_08_Match_DotLinkRefAnchor() throws Exception {
        FileReferenceListQuery fileReferenceListQuery = fileReferenceList.query()
                .wantMarkdownFiles()
                .keepLinkRefAnchor()
                .matchLinkRef("./" + linkRef);
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
