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
import kotlin.test.assertEquals

@RunWith(value = Parameterized::class)

//arrayOf<Any?>("fullPath", "linkType", "linkRef", "linkAnchor", "options", "optionsText", "multiResolve") * /
class TestGitHubVcsRoot_Basic constructor(val rowId: Int, val containingFile: String
                                          , val linkAddress: String
                                          , val normalizedAddress: String
) {
    val projectResolver: LinkResolver.ProjectResolver = MarkdownTestData
    val containingFileRef = FileRef(containingFile)
    val resolver = GitHubLinkResolver(MarkdownTestData, containingFileRef)
    val linkRef = LinkRef(containingFileRef, linkAddress, null, null)
    val normalizedRef = LinkRef(containingFileRef, normalizedAddress, null, null)
    val linkWithAnchorRef = LinkRef(containingFileRef, linkAddress, "anchor", null)
    val normalizedWithAnchorRef = LinkRef(containingFileRef, normalizedAddress, "anchor", null)
    val gitHubVcsRoot:GitHubVcsRoot

    init {
        val gitHubVcsRoot_ = resolver.projectResolver.getVcsRoot(containingFileRef)
        assert(gitHubVcsRoot_ != null, {"Unexpected null vcsRoot"})
        gitHubVcsRoot = gitHubVcsRoot_ as GitHubVcsRoot
    }

    @Test
    fun test_normalizedLinkRef() {
        val normalizedLinkRef = resolver.normalizedLinkRef(linkRef)

        assertEquals(normalizedRef.filePathWithAnchor,normalizedLinkRef.filePathWithAnchor)
    }

    @Test
    fun test_normalizedLinkWithAnchorRef() {
        val normalizedLinkRef = resolver.normalizedLinkRef(linkWithAnchorRef)

        assertEquals(normalizedWithAnchorRef.filePathWithAnchor,normalizedLinkRef.filePathWithAnchor)
    }

    companion object {
        @Parameterized.Parameters(name = "{index}: filePath = {1}, linkRef = {2}, normalizedRef = {3}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            val data = completionData
            val amendedData = ArrayList<Array<Any?>>()
            val cleanData = true

            var i = 0
            for (row in data) {
                val amendedRow = Array<Any?>(row.size + 1, { null })
                System.arraycopy(row, 0, amendedRow, 1, row.size)
                amendedRow[0] = i
                amendedData.add(amendedRow)
                i++
            }

            if (cleanData) {
                val header = arrayOf("fullPath", "linkRef", "normalizedRef")
                printData(data, header)
            }

            return amendedData
        }

        val completionData = arrayListOf<Array<Any?>>(
                /* @formatter:off */
                /*      arrayOf<Any?>("fullPath"                                                      , "linkRef"                                                                                                                                                      , "normalizedRef") */
                /*  0 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/Readme.md"                                                                                         , "https://github.com/vsch/MarkdownTest/raw/master/Readme.md") /* 0 */,
                /*  1 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-non-vcs-image.png"                                                            , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-non-vcs-image.png") /* 1 */,
                /*  2 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-vcs-image.png"                                                                , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-vcs-image.png") /* 2 */,
                /*  3 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                        , "https://raw.githubusercontent.com/wiki/vsch/MarkdownTest/img/ScreenShot_source_preview.png?token=AJ0mzve3jxMArvfYq7nKkL1ZaYZbPVxXks5Was-1wA%3D%3D"            , "https://github.com/vsch/MarkdownTest/wiki/img/ScreenShot_source_preview.png") /* 3 */,
                /*  4 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/Multiple-Match.md"   , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/Readme.md"                                                                                         , "https://github.com/vsch/MarkdownTest/raw/master/Readme.md") /* 4 */,
                /*  5 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/Multiple-Match.md"   , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-non-vcs-image.png"                                                            , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-non-vcs-image.png") /* 5 */,
                /*  6 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/Multiple-Match.md"   , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-vcs-image.png"                                                                , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-vcs-image.png") /* 6 */,
                /*  7 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/Multiple-Match.md"   , "https://raw.githubusercontent.com/wiki/vsch/MarkdownTest/img/ScreenShot_source_preview.png?token=AJ0mzve3jxMArvfYq7nKkL1ZaYZbPVxXks5Was-1wA%3D%3D"            , "https://github.com/vsch/MarkdownTest/wiki/img/ScreenShot_source_preview.png") /* 7 */,
                /*  8 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/NonVcsNestedFile.md" , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/Readme.md"                                                                                         , "https://github.com/vsch/MarkdownTest/raw/master/Readme.md") /* 8 */,
                /*  9 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/NonVcsNestedFile.md" , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-non-vcs-image.png"                                                            , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-non-vcs-image.png") /* 9 */,
                /* 10 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/NonVcsNestedFile.md" , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-vcs-image.png"                                                                , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-vcs-image.png") /* 10 */,
                /* 11 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/NonVcsNestedFile.md" , "https://raw.githubusercontent.com/wiki/vsch/MarkdownTest/img/ScreenShot_source_preview.png?token=AJ0mzve3jxMArvfYq7nKkL1ZaYZbPVxXks5Was-1wA%3D%3D"            , "https://github.com/vsch/MarkdownTest/wiki/img/ScreenShot_source_preview.png") /* 11 */,
                /* 12 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Readme.md"      , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/Readme.md"                                                                                         , "https://github.com/vsch/MarkdownTest/raw/master/Readme.md") /* 12 */,
                /* 13 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Readme.md"      , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-non-vcs-image.png"                                                            , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-non-vcs-image.png") /* 13 */,
                /* 14 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Readme.md"      , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-vcs-image.png"                                                                , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-vcs-image.png") /* 14 */,
                /* 15 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Readme.md"      , "https://raw.githubusercontent.com/wiki/vsch/MarkdownTest/img/ScreenShot_source_preview.png?token=AJ0mzve3jxMArvfYq7nKkL1ZaYZbPVxXks5Was-1wA%3D%3D"            , "https://github.com/vsch/MarkdownTest/wiki/img/ScreenShot_source_preview.png") /* 15 */,
                /* 16 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md" , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/Readme.md"                                                                                         , "https://github.com/vsch/MarkdownTest/raw/master/Readme.md") /* 16 */,
                /* 17 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md" , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-non-vcs-image.png"                                                            , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-non-vcs-image.png") /* 17 */,
                /* 18 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md" , "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-vcs-image.png"                                                                , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-vcs-image.png") /* 18 */,
                /* 19 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md" , "https://raw.githubusercontent.com/wiki/vsch/MarkdownTest/img/ScreenShot_source_preview.png?token=AJ0mzve3jxMArvfYq7nKkL1ZaYZbPVxXks5Was-1wA%3D%3D"            , "https://github.com/vsch/MarkdownTest/wiki/img/ScreenShot_source_preview.png") /* 19 */,
                /* 20 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Non-Vcs-Page.md", "https://raw.githubusercontent.com/vsch/MarkdownTest/master/Readme.md"                                                                                         , "https://github.com/vsch/MarkdownTest/raw/master/Readme.md") /* 20 */,
                /* 21 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Non-Vcs-Page.md", "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-non-vcs-image.png"                                                            , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-non-vcs-image.png") /* 21 */,
                /* 22 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Non-Vcs-Page.md", "https://raw.githubusercontent.com/vsch/MarkdownTest/master/SubDirectory/sub-dir-vcs-image.png"                                                                , "https://github.com/vsch/MarkdownTest/raw/master/SubDirectory/sub-dir-vcs-image.png") /* 22 */,
                /* 23 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Non-Vcs-Page.md", "https://raw.githubusercontent.com/wiki/vsch/MarkdownTest/img/ScreenShot_source_preview.png?token=AJ0mzve3jxMArvfYq7nKkL1ZaYZbPVxXks5Was-1wA%3D%3D"            , "https://github.com/vsch/MarkdownTest/wiki/img/ScreenShot_source_preview.png") /* 23  */
                /* @formatter:on */
        )
    }
}
