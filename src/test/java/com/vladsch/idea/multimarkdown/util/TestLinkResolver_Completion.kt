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
 *
 */
package com.vladsch.idea.multimarkdown.util

import com.vladsch.idea.multimarkdown.TestUtils.*
import com.vladsch.idea.multimarkdown.printData
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(value = Parameterized::class)

//arrayOf<Any?>("fullPath", "linkType", "linkRef", "linkAnchor", "options", "optionsText", "multiResolve") * /
class TestLinkResolver_Completion constructor(val rowId: Int, val containingFile: String
                                              , val linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?, targetRef: FileRef?) -> LinkRef
                                              , val linkAddress: String
                                              , val linkAnchor: String?
                                              , val matchOptions: Int
                                              , val matchOptionsText: String
                                              , val multiResolve: List<String>
) {
    val projectResolver: LinkResolver.ProjectResolver = MarkdownTestData
    val containingFileRef = FileRef(containingFile)
    val resolver = GitHubLinkResolver(MarkdownTestData, containingFileRef)
    val linkRef = LinkRef.parseLinkRef(containingFileRef, linkAddress + linkAnchor.prefixWith('#'), null, linkRefType)

    @Test
    fun test_Completion() {
        val list = resolver.multiResolve(linkRef, matchOptions)
        val matchText = resolver.getLastMatcher()?.linkLooseMatch

        compareOrderedLists("$matchText does not match\n", multiResolve, list.asFilePaths())
    }

    companion object {
        @Parameterized.Parameters(name = "{index}: filePath = {1}, linkRef = {3}, matchOptions = {6}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            val data = completionData
            val amendedData = ArrayList<Array<Any?>>()
            val cleanData = false

            var i = 0
            for (row in data) {
                val amendedRow = Array<Any?>(row.size + 1, { null })
                System.arraycopy(row, 0, amendedRow, 1, row.size)
                amendedRow[0] = i
                amendedData.add(amendedRow)
                i++
            }

            if (cleanData) {
                val header = arrayOf("fullPath", "linkType", "linkRef", "linkAnchor", "options", "optionsText", "multiResolve")
                printData(data, header)
            }

            return amendedData
        }

        val completionData = arrayListOf<Array<Any?>>(
                /* @formatter:off */
                /*       arrayOf<Any?>("fullPath"                                                     , "linkType"    , "linkRef", "linkAnchor", "options", "optionsText"                               , "multiResolve") */
                /*  0 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asRemoteURI()) /* 0 */,
                /*  1 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI()) /* 1 */,
                /*  2 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /* 2 */,
                /*  3 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteURI()) /* 3 */,
                /*  4 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".png"   , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /* 4 */,
                /*  5 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /* 5 */,
                /*  6 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /* 6 */,
                /*  7 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /* 7 */,
                /*  8 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asURI()) /* 8 */,
                /*  9 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /* 9 */,
                /* 10 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".kt"    , null        , 16       , "LOOSE_MATCH"                               , kotlinFiles) /* 10 */,
                /* 11 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".md"    , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /* 11 */,
                /* 12 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /* 12  */,
                /* 13 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI()) /* 13 */,
                /* 14 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , kotlinFiles) /* 14 */,
                /* 15 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /* 15 */,
                /* 16 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ""       , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /* 16 */,
                /* 17 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , kotlinFiles.asURI()) /* 17 */,
                /* 18 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /* 18 */,
                /* 19 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , ::WikiLinkRef , ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /* 19 */,
                /* 20 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiKotlinFiles.asRemoteURI()) /* 20 */,
                /* 21 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownRemoteFiles.asRemoteURI()) /* 21 */,
                /* 22 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiImageFiles.asURI()) /* 22 */,
                /* 23 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiImageRemoteFiles.asRemoteURI()) /* 23 */,
                /* 24 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".png"   , null        , 16       , "LOOSE_MATCH"                               , wikiImageFiles) /* 24 */,
                /* 25 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiImageFiles.asURI()) /* 25 */,
                /* 26 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiMarkdownFiles.asURI()) /* 26 */,
                /* 27 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiMarkdownRemoteFiles) /* 27 */,
                /* 28 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiKotlinFiles.asURI()) /* 28 */,
                /* 29 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownFiles.asURI()) /* 29 */,
                /* 30 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".kt"    , null        , 16       , "LOOSE_MATCH"                               , wikiKotlinFiles) /* 30 */,
                /* 31 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".md"    , null        , 16       , "LOOSE_MATCH"                               , wikiMarkdownFiles) /* 31 */,
                /* 32 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiImageRemoteFiles) /* 32 */,
                /* 33 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownRemoteFiles.asRemoteURI()) /* 33 */,
                /* 34 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiKotlinFiles) /* 34 */,
                /* 35 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiMarkdownRemoteFiles) /* 35 */,
                /* 36 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ""       , null        , 16       , "LOOSE_MATCH"                               , wikiMarkdownFiles) /* 36 */,
                /* 37 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiKotlinFiles.asURI()) /* 37 */,
                /* 38 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiMarkdownFiles.asURI()) /* 38 */,
                /* 39 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"        , ::WikiLinkRef , ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownFiles.asURI()) /* 39 */,
                /* 40 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"     , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiKotlinFiles.asRemoteURI()) /* 40 */,
                /* 41 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"     , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownRemoteFiles.asRemoteURI()) /* 41 */,
                /* 42 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiImageFiles.asURI()) /* 42 */,
                /* 43 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiImageRemoteFiles.asRemoteURI()) /* 43 */,
                /* 44 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"    , null        , 16       , "LOOSE_MATCH"                               , wikiImageFiles) /* 44 */,
                /* 45 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiImageFiles.asURI()) /* 45 */,
                /* 46 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""        , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiMarkdownFiles.asURI()) /* 46 */,
                /* 47 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""        , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiMarkdownRemoteFiles) /* 47 */,
                /* 48 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"     , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiKotlinFiles.asURI()) /* 48 */,
                /* 49 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"     , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownFiles.asURI()) /* 49 */,
                /* 50 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"     , null        , 16       , "LOOSE_MATCH"                               , wikiKotlinFiles) /* 50 */,
                /* 51 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"     , null        , 16       , "LOOSE_MATCH"                               , wikiMarkdownFiles) /* 51 */,
                /* 52 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiImageRemoteFiles) /* 52 */,
                /* 53 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""        , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownRemoteFiles.asRemoteURI()) /* 53 */,
                /* 54 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"     , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiKotlinFiles) /* 54 */,
                /* 55 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"     , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiMarkdownRemoteFiles) /* 55 */,
                /* 56 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""        , null        , 16       , "LOOSE_MATCH"                               , wikiMarkdownFiles) /* 56 */,
                /* 57 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"     , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiKotlinFiles.asURI()) /* 57 */,
                /* 58 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"     , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiMarkdownFiles.asURI()) /* 58 */,
                /* 59 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""        , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownFiles.asURI()) /*599 */
                /* @formatter:on */
        )
    }
}
