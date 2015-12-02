/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
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
                /*   0 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asRemoteURI()) /*   0 */,
                /*   1 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI()) /*   1 */,
                /*   2 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /*   2 */,
                /*   3 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteURI()) /*   3 */,
                /*   4 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".png"   , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /*   4 */,
                /*   5 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /*   5 */,
                /*   6 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI().with(gitHubLinks)) /*   6 */,
                /*   7 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /*   7 */,
                /*   8 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asURI()) /*   8 */,
                /*   9 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /*   9 */,
                /*  10 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".kt"    , null        , 16       , "LOOSE_MATCH"                               , kotlinFiles) /*  10 */,
                /*  11 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".md"    , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /*  11 */,
                /*  12 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /*  12 */,
                /*  13 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI().with(gitHubLinks)) /*  13 */,
                /*  14 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , kotlinFiles) /*  14 */,
                /*  15 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /*  15 */,
                /*  16 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ""       , null        , 16       , "LOOSE_MATCH"                               , markdownFiles.with(gitHubLinks)) /*  16 */,
                /*  17 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , kotlinFiles.asURI()) /*  17 */,
                /*  18 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /*  18 */,
                /*  19 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::LinkRef     , ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI().with(gitHubLinks)) /*  19 */,
                /*  20 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asRemoteURI()) /*  20 */,
                /*  21 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI()) /*  21 */,
                /*  22 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /*  22 */,
                /*  23 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteURI()) /*  23 */,
                /*  24 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".png"   , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /*  24 */,
                /*  25 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /*  25 */,
                /*  26 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /*  26 */,
                /*  27 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /*  27 */,
                /*  28 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asURI()) /*  28 */,
                /*  29 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /*  29 */,
                /*  30 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".kt"    , null        , 16       , "LOOSE_MATCH"                               , kotlinFiles) /*  30 */,
                /*  31 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".md"    , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /*  31 */,
                /*  32 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /*  32 */,
                /*  33 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI()) /*  33 */,
                /*  34 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , kotlinFiles) /*  34 */,
                /*  35 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /*  35 */,
                /*  36 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ""       , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /*  36 */,
                /*  37 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , kotlinFiles.asURI()) /*  37 */,
                /*  38 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /*  38 */,
                /*  39 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::WikiLinkRef , ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /*  39 */,
                /*  40 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asRemoteImageURI()) /*  40 */,
                /*  41 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteImageURI()) /*  41 */,
                /*  42 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /*  42 */,
                /*  43 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteImageURI()) /*  43 */,
                /*  44 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".png"   , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /*  44 */,
                /*  45 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /*  45 */,
                /*  46 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /*  46 */,
                /*  47 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /*  47 */,
                /*  48 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asURI()) /*  48 */,
                /*  49 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /*  49 */,
                /*  50 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".kt"    , null        , 16       , "LOOSE_MATCH"                               , kotlinFiles) /*  50 */,
                /*  51 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".md"    , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /*  51 */,
                /*  52 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /*  52 */,
                /*  53 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteImageURI()) /*  53 */,
                /*  54 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , kotlinFiles) /*  54 */,
                /*  55 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /*  55 */,
                /*  56 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ""       , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /*  56 */,
                /*  57 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , kotlinFiles.asURI()) /*  57 */,
                /*  58 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /*  58 */,
                /*  59 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                       , ::ImageLinkRef, ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /*  59 */,
                /*  60 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asRemoteURI()) /*  60 */,
                /*  61 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI()) /*  61 */,
                /*  62 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /*  62 */,
                /*  63 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteURI()) /*  63 */,
                /*  64 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".png"   , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /*  64 */,
                /*  65 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /*  65 */,
                /*  66 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI().with(gitHubLinks)) /*  66 */,
                /*  67 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /*  67 */,
                /*  68 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asURI()) /*  68 */,
                /*  69 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /*  69 */,
                /*  70 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".kt"    , null        , 16       , "LOOSE_MATCH"                               , kotlinFiles) /*  70 */,
                /*  71 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".md"    , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /*  71 */,
                /*  72 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /*  72 */,
                /*  73 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI().with(gitHubLinks)) /*  73 */,
                /*  74 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , kotlinFiles) /*  74 */,
                /*  75 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /*  75 */,
                /*  76 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ""       , null        , 16       , "LOOSE_MATCH"                               , markdownFiles.with(gitHubLinks)) /*  76 */,
                /*  77 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , kotlinFiles.asURI()) /*  77 */,
                /*  78 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /*  78 */,
                /*  79 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::LinkRef     , ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI().with(gitHubLinks)) /*  79 */,
                /*  80 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiKotlinFiles.asRemoteURI()) /*  80 */,
                /*  81 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownRemoteFiles.asRemoteURI()) /*  81 */,
                /*  82 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiImageFiles.asURI()) /*  82 */,
                /*  83 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiImageRemoteFiles.asRemoteURI()) /*  83 */,
                /*  84 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".png"   , null        , 16       , "LOOSE_MATCH"                               , wikiImageFiles) /*  84 */,
                /*  85 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiImageFiles.asURI()) /*  85 */,
                /*  86 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiMarkdownFiles.asURI()) /*  86 */,
                /*  87 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiMarkdownRemoteFiles) /*  87 */,
                /*  88 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiKotlinFiles.asURI()) /*  88 */,
                /*  89 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownFiles.asURI()) /*  89 */,
                /*  90 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".kt"    , null        , 16       , "LOOSE_MATCH"                               , wikiKotlinFiles) /*  90 */,
                /*  91 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".md"    , null        , 16       , "LOOSE_MATCH"                               , wikiMarkdownFiles) /*  91 */,
                /*  92 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiImageRemoteFiles) /*  92 */,
                /*  93 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownRemoteFiles.asRemoteURI()) /*  93 */,
                /*  94 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiKotlinFiles) /*  94 */,
                /*  95 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiMarkdownRemoteFiles) /*  95 */,
                /*  96 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ""       , null        , 16       , "LOOSE_MATCH"                               , wikiMarkdownFiles) /*  96 */,
                /*  97 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiKotlinFiles.asURI()) /*  97 */,
                /*  98 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiMarkdownFiles.asURI()) /*  98 */,
                /*  99 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::WikiLinkRef , ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownFiles.asURI()) /*  99 */,
                /* 100 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asRemoteImageURI()) /* 100 */,
                /* 101 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteImageURI()) /* 101 */,
                /* 102 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /* 102 */,
                /* 103 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteImageURI()) /* 103 */,
                /* 104 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".png"   , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /* 104 */,
                /* 105 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /* 105 */,
                /* 106 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /* 106 */,
                /* 107 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /* 107 */,
                /* 108 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asURI()) /* 108 */,
                /* 109 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /* 109 */,
                /* 110 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".kt"    , null        , 16       , "LOOSE_MATCH"                               , kotlinFiles) /* 110 */,
                /* 111 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".md"    , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /* 111 */,
                /* 112 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /* 112 */,
                /* 113 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteImageURI()) /* 113 */,
                /* 114 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , kotlinFiles) /* 114 */,
                /* 115 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /* 115 */,
                /* 116 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ""       , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /* 116 */,
                /* 117 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , kotlinFiles.asURI()) /* 117 */,
                /* 118 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /* 118 */,
                /* 119 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"       , ::ImageLinkRef, ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /* 119 */,
                /* 120 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asRemoteURI()) /* 120 */,
                /* 121 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI()) /* 121 */,
                /* 122 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /* 122 */,
                /* 123 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteURI()) /* 123 */,
                /* 124 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".png"   , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /* 124 */,
                /* 125 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /* 125 */,
                /* 126 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI().with(gitHubLinks)) /* 126 */,
                /* 127 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /* 127 */,
                /* 128 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asURI()) /* 128 */,
                /* 129 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /* 129 */,
                /* 130 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".kt"    , null        , 16       , "LOOSE_MATCH"                               , kotlinFiles) /* 130 */,
                /* 131 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".md"    , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /* 131 */,
                /* 132 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /* 132 */,
                /* 133 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteURI().with(gitHubLinks)) /* 133 */,
                /* 134 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , kotlinFiles) /* 134 */,
                /* 135 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /* 135 */,
                /* 136 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ""       , null        , 16       , "LOOSE_MATCH"                               , markdownFiles.with(gitHubLinks)) /* 136 */,
                /* 137 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , kotlinFiles.asURI()) /* 137 */,
                /* 138 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /* 138 */,
                /* 139 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI().with(gitHubLinks)) /* 139 */,
                /* 140 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiKotlinFiles.asRemoteURI()) /* 140 */,
                /* 141 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownRemoteFiles.asRemoteURI()) /* 141 */,
                /* 142 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiImageFiles.asURI()) /* 142 */,
                /* 143 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiImageRemoteFiles.asRemoteURI()) /* 143 */,
                /* 144 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"   , null        , 16       , "LOOSE_MATCH"                               , wikiImageFiles) /* 144 */,
                /* 145 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiImageFiles.asURI()) /* 145 */,
                /* 146 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiMarkdownFiles.asURI()) /* 146 */,
                /* 147 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiMarkdownRemoteFiles) /* 147 */,
                /* 148 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiKotlinFiles.asURI()) /* 148 */,
                /* 149 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownFiles.asURI()) /* 149 */,
                /* 150 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"    , null        , 16       , "LOOSE_MATCH"                               , wikiKotlinFiles) /* 150 */,
                /* 151 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"    , null        , 16       , "LOOSE_MATCH"                               , wikiMarkdownFiles) /* 151 */,
                /* 152 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiImageRemoteFiles) /* 152 */,
                /* 153 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownRemoteFiles.asRemoteURI()) /* 153 */,
                /* 154 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiKotlinFiles) /* 154 */,
                /* 155 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , wikiMarkdownRemoteFiles) /* 155 */,
                /* 156 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""       , null        , 16       , "LOOSE_MATCH"                               , wikiMarkdownFiles) /* 156 */,
                /* 157 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiKotlinFiles.asURI()) /* 157 */,
                /* 158 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , wikiMarkdownFiles.asURI()) /* 158 */,
                /* 159 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", wikiMarkdownFiles.asURI()) /* 159 */,
                /* 160 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".kt"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asRemoteImageURI()) /* 160 */,
                /* 161 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".md"    , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownRemoteFiles.asRemoteImageURI()) /* 161 */,
                /* 162 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".png"   , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /* 162 */,
                /* 163 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".png"   , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteImageURI()) /* 163 */,
                /* 164 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".png"   , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /* 164 */,
                /* 165 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".png"   , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /* 165 */,
                /* 166 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ""       , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , imageFiles.asURI()) /* 166 */,
                /* 167 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ""       , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /* 167 */,
                /* 168 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".kt"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", kotlinFiles.asURI()) /* 168 */,
                /* 169 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".md"    , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", markdownFiles.asURI()) /* 169 */,
                /* 170 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".kt"    , null        , 16       , "LOOSE_MATCH"                               , kotlinFiles) /* 170 */,
                /* 171 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".md"    , null        , 16       , "LOOSE_MATCH"                               , markdownFiles) /* 171 */,
                /* 172 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".png"   , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , imageRemoteFiles) /* 172 */,
                /* 173 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ""       , null        , 22       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageRemoteFiles.asRemoteImageURI()) /* 173 */,
                /* 174 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".kt"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , kotlinFiles) /* 174 */,
                /* 175 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".md"    , null        , 18       , "LOCAL_OR_REMOTE or LOOSE_MATCH"            , markdownRemoteFiles) /* 175 */,
                /* 176 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ""       , null        , 16       , "LOOSE_MATCH"                               , imageFiles) /* 176 */,
                /* 177 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".kt"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , kotlinFiles.asURI()) /* 177 */,
                /* 178 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ".md"    , null        , 20       , "ONLY_URI or LOOSE_MATCH"                   , markdownFiles.asURI()) /* 178 */,
                /* 179 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, ""       , null        , 23       , "LOCAL_OR_REMOTE or ONLY_URI or LOOSE_MATCH", imageFiles.asURI()) /* 179 */
                /* @formatter:on */
        )
    }
}
