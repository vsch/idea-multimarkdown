/*
 * Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.md.nav.util

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class TestFileInfo constructor(val fullPath: String
    , val isUnderWikiDir: Boolean
    , val isWikiPage: Boolean
    , val isWikiHomePage: Boolean
    , val wikiDir: String
    , val mainRepoDir: String
    , val pathFromWikiDir: String
    , val pathFromMainRepoDir: String
    , val upDirectoriesToWikiHome: Int
) {

    val pathInfo = FileRef(fullPath)

    @Rule
    @JvmField
    var thrown = ExpectedException.none()

    /* @formatter:off */
    @Test fun test_isUnderWikiDir() { assertEquals(isUnderWikiDir, pathInfo.isUnderWikiDir) }
    @Test fun test_isWikiPage() { assertEquals(isWikiPage, pathInfo.isWikiPage) }
    @Test fun test_isWikiHomePage() { assertEquals(isWikiHomePage, pathInfo.isWikiHomePage) }
    @Test fun test_wikiDir() { assertEquals(wikiDir, pathInfo.wikiDir) }
    @Test fun test_mainRepoDir() {
        if (!isUnderWikiDir) {
//            thrown.expect(AssertionError::class.java)
//            thrown.expectMessage("mainRepo related values are only valid if isUnderWikiDir is true")
            //assertEquals(pathFromMainRepoDir, pathInfo.filePathFromMainRepoDir)
        }
        else assertEquals(mainRepoDir, pathInfo.mainRepoDir)
    }
    @Test fun test_filePathFromWikiDir() { assertEquals(pathFromWikiDir, pathInfo.filePathFromWikiDir) }
    @Test fun test_filePathFromMainRepoDir() {
        if (!isUnderWikiDir) {
//            thrown.expect(AssertionError::class.java)
//            thrown.expectMessage("mainRepo related values are only valid if isUnderWikiDir is true")
            //assertEquals(pathFromMainRepoDir, pathInfo.filePathFromMainRepoDir)
        }
        else assertEquals(pathFromMainRepoDir, pathInfo.filePathFromMainRepoDir)
    }
    @Test fun test_upDirectoriesToWikiHome() { assertEquals(upDirectoriesToWikiHome, pathInfo.upDirectoriesToWikiHome) }
    /* @formatter:on */

    companion object {
        @Parameterized.Parameters(name = "{index}: filePath = {0}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            val cleanData = false
            //            val test = TestPathInfo("", "", "", "", "", "", "", false, true, false, false, false, "", "", null, arrayOf<String>())
            val data = arrayListOf<Array<Any?>>(
                /* @formatter:off */
                /*      arrayOf<Any?>("fullPath"                                                 , "isUnderWikiDir", "isWikiPage", "isWikiHomePage", "wikiDir"              , "mainRepoDir", "pathFromWikiDir"                                         , "pathFromMainRepoDir"                                     , "upDirectoriesToWikiHome") */
                /*  0 */arrayOf<Any?>("/home/home.wiki/file-Name"                                , true            , false       , false           , "/home/home.wiki"      , "/home"      , "file-Name"                                               , "home.wiki/file-Name"                                     , 1                        ),
                /*  1 */arrayOf<Any?>("/home/home.wiki/fileName.md"                              , true            , true        , false           , "/home/home.wiki"      , "/home"      , "fileName.md"                                             , "home.wiki/fileName.md"                                   , 1                        ),
                /*  2 */arrayOf<Any?>("/home/home.wiki/file-Name.md"                             , true            , true        , false           , "/home/home.wiki"      , "/home"      , "file-Name.md"                                            , "home.wiki/file-Name.md"                                  , 1                        ),
                /*  3 */arrayOf<Any?>("/home/home.wiki/path/with/fileName.md"                    , true            , true        , false           , "/home/home.wiki"      , "/home"      , "path/with/fileName.md"                                   , "home.wiki/path/with/fileName.md"                         , 3                        ),
                /*  4 */arrayOf<Any?>("/home/home.wiki/path/with/file-Name.md"                   , true            , true        , false           , "/home/home.wiki"      , "/home"      , "path/with/file-Name.md"                                  , "home.wiki/path/with/file-Name.md"                        , 3                        ),
                /*  5 */arrayOf<Any?>("/home/home.wiki/pathName/with/fileName.md"                , true            , true        , false           , "/home/home.wiki"      , "/home"      , "pathName/with/fileName.md"                               , "home.wiki/pathName/with/fileName.md"                     , 3                        ),
                /*  6 */arrayOf<Any?>("/is-home/is-home.wiki/file-Name"                          , true            , false       , false           , "/is-home/is-home.wiki", "/is-home"   , "file-Name"                                               , "is-home.wiki/file-Name"                                  , 1                        ),
                /*  7 */arrayOf<Any?>("/is-home/is-home.wiki/fileName.md"                        , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "fileName.md"                                             , "is-home.wiki/fileName.md"                                , 1                        ),
                /*  8 */arrayOf<Any?>("/is-home/is-home.wiki/file-Name.md"                       , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "file-Name.md"                                            , "is-home.wiki/file-Name.md"                               , 1                        ),
                /*  9 */arrayOf<Any?>("/is-home/is-home.wiki/path/with/fileName.md"              , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "path/with/fileName.md"                                   , "is-home.wiki/path/with/fileName.md"                      , 3                        ),
                /* 10 */arrayOf<Any?>("/is-home/is-home.wiki/path/with/file-Name.md"             , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "path/with/file-Name.md"                                  , "is-home.wiki/path/with/file-Name.md"                     , 3                        ),
                /* 11 */arrayOf<Any?>("/is-home/is-home.wiki/pathName/with/fileName.md"          , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "pathName/with/fileName.md"                               , "is-home.wiki/pathName/with/fileName.md"                  , 3                        ),
                /* 12 */arrayOf<Any?>("/somepath/home.wiki/path-Name/with/file-Name.md"          , false           , false       , false           , ""                     , ""           , "/somepath/home.wiki/path-Name/with/file-Name.md"         , "/somepath/home.wiki/path-Name/with/file-Name.md"         , 0                        ),
                /* 13 */arrayOf<Any?>("/home/home.wiki/file-Name"                                , true            , false       , false           , "/home/home.wiki"      , "/home"      , "file-Name"                                               , "home.wiki/file-Name"                                     , 1                        ),
                /* 14 */arrayOf<Any?>("/home/home.wiki/fileName.md"                              , true            , true        , false           , "/home/home.wiki"      , "/home"      , "fileName.md"                                             , "home.wiki/fileName.md"                                   , 1                        ),
                /* 15 */arrayOf<Any?>("/home/home.wiki/file-Name.md"                             , true            , true        , false           , "/home/home.wiki"      , "/home"      , "file-Name.md"                                            , "home.wiki/file-Name.md"                                  , 1                        ),
                /* 16 */arrayOf<Any?>("/home/home.wiki/path/with/fileName.md"                    , true            , true        , false           , "/home/home.wiki"      , "/home"      , "path/with/fileName.md"                                   , "home.wiki/path/with/fileName.md"                         , 3                        ),
                /* 17 */arrayOf<Any?>("/home/home.wiki/path/with/file-Name.md"                   , true            , true        , false           , "/home/home.wiki"      , "/home"      , "path/with/file-Name.md"                                  , "home.wiki/path/with/file-Name.md"                        , 3                        ),
                /* 18 */arrayOf<Any?>("/home/home.wiki/pathName/with/fileName.md"                , true            , true        , false           , "/home/home.wiki"      , "/home"      , "pathName/with/fileName.md"                               , "home.wiki/pathName/with/fileName.md"                     , 3                        ),
                /* 19 */arrayOf<Any?>("/home/home.wiki/path-Name/with/file-Name.md"              , true            , true        , false           , "/home/home.wiki"      , "/home"      , "path-Name/with/file-Name.md"                             , "home.wiki/path-Name/with/file-Name.md"                   , 3                        ),
                /* 20 */arrayOf<Any?>("/./is-home/is-home.wiki/file-Name"                        , true            , false       , false           , "/is-home/is-home.wiki", "/is-home"   , "file-Name"                                               , "is-home.wiki/file-Name"                                  , 1                        ),
                /* 21 */arrayOf<Any?>("/./is-home/is-home.wiki/fileName.md"                      , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "fileName.md"                                             , "is-home.wiki/fileName.md"                                , 1                        ),
                /* 22 */arrayOf<Any?>("/./is-home/is-home.wiki/file-Name.md"                     , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "file-Name.md"                                            , "is-home.wiki/file-Name.md"                               , 1                        ),
                /* 23 */arrayOf<Any?>("/./is-home/is-home.wiki/path/with/fileName.md"            , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "path/with/fileName.md"                                   , "is-home.wiki/path/with/fileName.md"                      , 3                        ),
                /* 24 */arrayOf<Any?>("/is-home/is-home.wiki/path/with/file-Name.md"             , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "path/with/file-Name.md"                                  , "is-home.wiki/path/with/file-Name.md"                     , 3                        ),
                /* 25 */arrayOf<Any?>("/is-home/is-home.wiki/pathName/with/fileName.md"          , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "pathName/with/fileName.md"                               , "is-home.wiki/pathName/with/fileName.md"                  , 3                        ),
                /* 26 */arrayOf<Any?>("/is-home/is-home.wiki/pathName/with/fileName"             , true            , false       , false           , "/is-home/is-home.wiki", "/is-home"   , "pathName/with/fileName"                                  , "is-home.wiki/pathName/with/fileName"                     , 3                        ),
                /* 27 */arrayOf<Any?>("/is-home/is-home.wiki/path/file-Name.md"                  , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "path/file-Name.md"                                       , "is-home.wiki/path/file-Name.md"                          , 2                        ),
                /* 28 */arrayOf<Any?>("/is-home/is-home.wiki/path/path2/path3/file-Name.md"      , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "path/path2/path3/file-Name.md"                           , "is-home.wiki/path/path2/path3/file-Name.md"              , 4                        ),
                /* 29 */arrayOf<Any?>("/is-home/is-home.wiki/path/path2/path3/file-Name.mkd"     , true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "path/path2/path3/file-Name.mkd"                          , "is-home.wiki/path/path2/path3/file-Name.mkd"             , 4                        ),
                /* 30 */arrayOf<Any?>("/is-home/is-home.wiki/path/path2/path3/file-Name.markdown", true            , true        , false           , "/is-home/is-home.wiki", "/is-home"   , "path/path2/path3/file-Name.markdown"                     , "is-home.wiki/path/path2/path3/file-Name.markdown"        , 4                        ),
                /* 31 */arrayOf<Any?>("/is-hom/is-home.wiki/path/path2/path3/file-Name.markdown" , false           , false       , false           , ""                     , ""           , "/is-hom/is-home.wiki/path/path2/path3/file-Name.markdown", "/is-hom/is-home.wiki/path/path2/path3/file-Name.markdown", 0                        ),
                /* 32 */arrayOf<Any?>("/is-home/home/path/path2/path3/file-Name.markdown"        , false           , false       , false           , ""                     , ""           , "/is-home/home/path/path2/path3/file-Name.markdown"       , "/is-home/home/path/path2/path3/file-Name.markdown"       , 0                        ),
                /* 33 */arrayOf<Any?>("/is-home/is-home.wiki/Home.md"                            , true            , true        , true            , "/is-home/is-home.wiki", "/is-home"   , "Home.md"                                                 , "is-home.wiki/Home.md"                                    , 1                        ),
                /* 34 */arrayOf<Any?>("/is-home/is-home.wiki/Home.mkd"                           , true            , true        , true            , "/is-home/is-home.wiki", "/is-home"   , "Home.mkd"                                                , "is-home.wiki/Home.mkd"                                   , 1                        ),
                /* 35 */arrayOf<Any?>("/is-home/is-home.wiki/Home.markdown"                      , true            , true        , true            , "/is-home/is-home.wiki", "/is-home"   , "Home.markdown"                                           , "is-home.wiki/Home.markdown"                              , 1                        )
                /* @formatter:on */
            )

            if (cleanData) {
                val header = arrayOf(
                    "fullPath",
                    "isUnderWikiDir",
                    "isWikiPage",
                    "isWikiHomePage",
                    "wikiDir",
                    "mainRepoDir",
                    "pathFromWikiDir",
                    "pathFromMainRepoDir",
                    "upDirectoriesToWikiHome"
                )
                printData(data, header)
            }
            return data
        }
    }
}
