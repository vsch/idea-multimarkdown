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

import com.vladsch.idea.multimarkdown.printData
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class TestPathInfo constructor(val fullPath: String
                               , val getExt: String
                               , val getFilePath: String
                               , val getFilePathNoExt: String
                               , val getPath: String
                               , val getFileName: String
                               , val getFileNameNoExt: String
                               , val getHasExt: Boolean
                               , val isRelative: Boolean
                               , val isExternal: Boolean
                               , val isURI: Boolean
                               , val isAbsolute: Boolean
                               , val withExt: String
                               , val append: String
                               , val addWithExt: String?
                               , val addAppend: Array<String>) {

    val pathInfo = PathInfo(fullPath);

    /* @formatter:off */
    @Test fun test_ext() { assertEquals(getExt, pathInfo.ext) }
    @Test fun test_filePath() { assertEquals(getFilePath, pathInfo.filePath) }
    @Test fun test_filePathNoExt() { assertEquals(getFilePathNoExt, pathInfo.filePathNoExt) }
    @Test fun test_path() { assertEquals(getPath, pathInfo.path) }
    @Test fun test_fileName() { assertEquals(getFileName, pathInfo.fileName) }
    @Test fun test_fileNameNoExt() { assertEquals(getFileNameNoExt, pathInfo.fileNameNoExt) }
    @Test fun test_hasExt() { assertEquals(getHasExt, pathInfo.hasExt) }
    @Test fun test_isRelative() { assertEquals(isRelative, pathInfo.isRelative) }
    @Test fun test_isExternal() { assertEquals(isExternal, pathInfo.isExternal) }
    @Test fun test_isURI() { assertEquals(isURI, pathInfo.isURI) }
    @Test fun test_isAbsolute() { assertEquals(isAbsolute, pathInfo.isAbsolute) }
    @Test fun test_withExt() { assertEquals(withExt, pathInfo.withExt(addWithExt).toString()) }
    @Test fun test_append() { assertEquals(append, pathInfo.append(*addAppend).toString()) }
    /* @formatter:on */

    companion object {
        @Parameterized.Parameters(name = "{index}: filePath = {0}")
        @JvmStatic
        public fun data(): Collection<Array<Any?>> {
            val genData = false
            //            val test = TestPathInfo("", "", "", "", "", "", "", false, true, false, false, false, "", "", null, arrayOf<String>())
            if (!genData) {
                //                return arrayListOf()
                /* @formatter:off */
            return arrayListOf<Array<Any?>>(
                /*      arrayOf<Any?>("fullPath"                     , "ext", "filePath"                     , "filePathNoExt"            , "path"             , "fileName"            , "fileNameNoExt"   , "hasExt", "isRelative", "isExternal", "isURI", "isAbsolute", "withExt"                      , "append"                                , "addWithExt", "addAppend"                                              ) */
                /*  0 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , ""                                      , null        , arrayOf<String>()                                        ),
                /*  1 */arrayOf<Any?>(".ext"                         , ""   , ".ext"                         , ".ext"                     , ""                 , ".ext"                , ".ext"            , false   , true        , false       , false  , false       , ".ext"                         , ".ext"                                  , null        , arrayOf<String>()                                        ),
                /*  2 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName"                     , "fileName"                              , null        , arrayOf<String>()                                        ),
                /*  3 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName"                     , "fileName"                              , null        , arrayOf<String>()                                        ),
                /*  4 */arrayOf<Any?>("fileName.ext"                 , "ext", "fileName.ext"                 , "fileName"                 , ""                 , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "fileName.ext"                 , "fileName.ext"                          , null        , arrayOf<String>()                                        ),
                /*  5 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName"                     , "fileName"                              , null        , arrayOf<String>()                                        ),
                /*  6 */arrayOf<Any?>("SubDir1/fileName"             , ""   , "SubDir1/fileName"             , "SubDir1/fileName"         , "SubDir1/"         , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "SubDir1/fileName"             , "SubDir1/fileName"                      , null        , arrayOf<String>()                                        ),
                /*  7 */arrayOf<Any?>("SubDir1.sub/fileName"         , ""   , "SubDir1.sub/fileName"         , "SubDir1.sub/fileName"     , "SubDir1.sub/"     , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "SubDir1.sub/fileName"         , "SubDir1.sub/fileName"                  , null        , arrayOf<String>()                                        ),
                /*  8 */arrayOf<Any?>("SubDir1/SubDir2/fileName"     , ""   , "SubDir1/SubDir2/fileName"     , "SubDir1/SubDir2/fileName" , "SubDir1/SubDir2/" , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "SubDir1/SubDir2/fileName"     , "SubDir1/SubDir2/fileName"              , null        , arrayOf<String>()                                        ),
                /*  9 */arrayOf<Any?>("SubDir1/fileName"             , ""   , "SubDir1/fileName"             , "SubDir1/fileName"         , "SubDir1/"         , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "SubDir1/fileName"             , "SubDir1/fileName"                      , null        , arrayOf<String>()                                        ),
                /* 10 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName"                     , "fileName"                              , null        , arrayOf<String>()                                        ),
                /* 11 */arrayOf<Any?>("SubDir1/fileName"             , ""   , "SubDir1/fileName"             , "SubDir1/fileName"         , "SubDir1/"         , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "SubDir1/fileName"             , "SubDir1/fileName"                      , null        , arrayOf<String>()                                        ),
                /* 12 */arrayOf<Any?>("SubDir1.sub/fileName"         , ""   , "SubDir1.sub/fileName"         , "SubDir1.sub/fileName"     , "SubDir1.sub/"     , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "SubDir1.sub/fileName"         , "SubDir1.sub/fileName"                  , null        , arrayOf<String>()                                        ),
                /* 13 */arrayOf<Any?>("SubDir1/SubDir2/fileName"     , ""   , "SubDir1/SubDir2/fileName"     , "SubDir1/SubDir2/fileName" , "SubDir1/SubDir2/" , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "SubDir1/SubDir2/fileName"     , "SubDir1/SubDir2/fileName"              , null        , arrayOf<String>()                                        ),
                /* 14 */arrayOf<Any?>("SubDir1/fileName"             , ""   , "SubDir1/fileName"             , "SubDir1/fileName"         , "SubDir1/"         , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "SubDir1/fileName"             , "SubDir1/fileName"                      , null        , arrayOf<String>()                                        ),
                /* 15 */arrayOf<Any?>("fileName.ext"                 , "ext", "fileName.ext"                 , "fileName"                 , ""                 , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "fileName.ext"                 , "fileName.ext"                          , null        , arrayOf<String>()                                        ),
                /* 16 */arrayOf<Any?>("SubDir1/fileName.ext"         , "ext", "SubDir1/fileName.ext"         , "SubDir1/fileName"         , "SubDir1/"         , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "SubDir1/fileName.ext"         , "SubDir1/fileName.ext"                  , null        , arrayOf<String>()                                        ),
                /* 17 */arrayOf<Any?>("SubDir1.sub/fileName.ext"     , "ext", "SubDir1.sub/fileName.ext"     , "SubDir1.sub/fileName"     , "SubDir1.sub/"     , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "SubDir1.sub/fileName.ext"     , "SubDir1.sub/fileName.ext"              , null        , arrayOf<String>()                                        ),
                /* 18 */arrayOf<Any?>("SubDir1/SubDir2/fileName.ext" , "ext", "SubDir1/SubDir2/fileName.ext" , "SubDir1/SubDir2/fileName" , "SubDir1/SubDir2/" , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "SubDir1/SubDir2/fileName.ext" , "SubDir1/SubDir2/fileName.ext"          , null        , arrayOf<String>()                                        ),
                /* 19 */arrayOf<Any?>("SubDir1/fileName.ext"         , "ext", "SubDir1/fileName.ext"         , "SubDir1/fileName"         , "SubDir1/"         , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "SubDir1/fileName.ext"         , "SubDir1/fileName.ext"                  , null        , arrayOf<String>()                                        ),
                /* 20 */arrayOf<Any?>("/fileName"                    , ""   , "/fileName"                    , "/fileName"                , "/"                , "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/fileName"                    , "/fileName"                             , null        , arrayOf<String>()                                        ),
                /* 21 */arrayOf<Any?>("/SubDir1/fileName"            , ""   , "/SubDir1/fileName"            , "/SubDir1/fileName"        , "/SubDir1/"        , "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/SubDir1/fileName"            , "/SubDir1/fileName"                     , null        , arrayOf<String>()                                        ),
                /* 22 */arrayOf<Any?>("/SubDir1.sub/fileName"        , ""   , "/SubDir1.sub/fileName"        , "/SubDir1.sub/fileName"    , "/SubDir1.sub/"    , "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/SubDir1.sub/fileName"        , "/SubDir1.sub/fileName"                 , null        , arrayOf<String>()                                        ),
                /* 23 */arrayOf<Any?>("/SubDir1/SubDir2/fileName"    , ""   , "/SubDir1/SubDir2/fileName"    , "/SubDir1/SubDir2/fileName", "/SubDir1/SubDir2/", "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/SubDir1/SubDir2/fileName"    , "/SubDir1/SubDir2/fileName"             , null        , arrayOf<String>()                                        ),
                /* 24 */arrayOf<Any?>("/SubDir1/fileName"            , ""   , "/SubDir1/fileName"            , "/SubDir1/fileName"        , "/SubDir1/"        , "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/SubDir1/fileName"            , "/SubDir1/fileName"                     , null        , arrayOf<String>()                                        ),
                /* 25 */arrayOf<Any?>("/fileName"                    , ""   , "/fileName"                    , "/fileName"                , "/"                , "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/fileName"                    , "/fileName"                             , null        , arrayOf<String>()                                        ),
                /* 26 */arrayOf<Any?>("/SubDir1/fileName"            , ""   , "/SubDir1/fileName"            , "/SubDir1/fileName"        , "/SubDir1/"        , "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/SubDir1/fileName"            , "/SubDir1/fileName"                     , null        , arrayOf<String>()                                        ),
                /* 27 */arrayOf<Any?>("/SubDir1.sub/fileName"        , ""   , "/SubDir1.sub/fileName"        , "/SubDir1.sub/fileName"    , "/SubDir1.sub/"    , "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/SubDir1.sub/fileName"        , "/SubDir1.sub/fileName"                 , null        , arrayOf<String>()                                        ),
                /* 28 */arrayOf<Any?>("/SubDir1/SubDir2/fileName"    , ""   , "/SubDir1/SubDir2/fileName"    , "/SubDir1/SubDir2/fileName", "/SubDir1/SubDir2/", "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/SubDir1/SubDir2/fileName"    , "/SubDir1/SubDir2/fileName"             , null        , arrayOf<String>()                                        ),
                /* 29 */arrayOf<Any?>("/SubDir1/fileName"            , ""   , "/SubDir1/fileName"            , "/SubDir1/fileName"        , "/SubDir1/"        , "fileName"            , "fileName"        , false   , false       , false       , false  , true        , "/SubDir1/fileName"            , "/SubDir1/fileName"                     , null        , arrayOf<String>()                                        ),
                /* 30 */arrayOf<Any?>("/fileName.ext"                , "ext", "/fileName.ext"                , "/fileName"                , "/"                , "fileName.ext"        , "fileName"        , true    , false       , false       , false  , true        , "/fileName.ext"                , "/fileName.ext"                         , null        , arrayOf<String>()                                        ),
                /* 31 */arrayOf<Any?>("/SubDir1/fileName.ext"        , "ext", "/SubDir1/fileName.ext"        , "/SubDir1/fileName"        , "/SubDir1/"        , "fileName.ext"        , "fileName"        , true    , false       , false       , false  , true        , "/SubDir1/fileName.ext"        , "/SubDir1/fileName.ext"                 , null        , arrayOf<String>()                                        ),
                /* 32 */arrayOf<Any?>("/SubDir1.sub/fileName.ext"    , "ext", "/SubDir1.sub/fileName.ext"    , "/SubDir1.sub/fileName"    , "/SubDir1.sub/"    , "fileName.ext"        , "fileName"        , true    , false       , false       , false  , true        , "/SubDir1.sub/fileName.ext"    , "/SubDir1.sub/fileName.ext"             , null        , arrayOf<String>()                                        ),
                /* 33 */arrayOf<Any?>("/SubDir1/SubDir2/fileName.ext", "ext", "/SubDir1/SubDir2/fileName.ext", "/SubDir1/SubDir2/fileName", "/SubDir1/SubDir2/", "fileName.ext"        , "fileName"        , true    , false       , false       , false  , true        , "/SubDir1/SubDir2/fileName.ext", "/SubDir1/SubDir2/fileName.ext"         , null        , arrayOf<String>()                                        ),
                /* 34 */arrayOf<Any?>("/SubDir1/fileName.ext"        , "ext", "/SubDir1/fileName.ext"        , "/SubDir1/fileName"        , "/SubDir1/"        , "fileName.ext"        , "fileName"        , true    , false       , false       , false  , true        , "/SubDir1/fileName.ext"        , "/SubDir1/fileName.ext"                 , null        , arrayOf<String>()                                        ),
                /* 35 */arrayOf<Any?>("http://fileName"              , ""   , "http://fileName"              , "http://fileName"          , "http://"          , "fileName"            , "fileName"        , false   , false       , true        , true   , true        , "http://fileName"              , "http://fileName"                       , null        , arrayOf<String>()                                        ),
                /* 36 */arrayOf<Any?>("https://fileName"             , ""   , "https://fileName"             , "https://fileName"         , "https://"         , "fileName"            , "fileName"        , false   , false       , true        , true   , true        , "https://fileName"             , "https://fileName"                      , null        , arrayOf<String>()                                        ),
                /* 37 */arrayOf<Any?>("ftp://fileName"               , ""   , "ftp://fileName"               , "ftp://fileName"           , "ftp://"           , "fileName"            , "fileName"        , false   , false       , true        , true   , true        , "ftp://fileName"               , "ftp://fileName"                        , null        , arrayOf<String>()                                        ),
                /* 38 */arrayOf<Any?>("file://fileName"              , ""   , "file://fileName"              , "file://fileName"          , "file://"          , "fileName"            , "fileName"        , false   , false       , false       , true   , true        , "file://fileName"              , "file://fileName"                       , null        , arrayOf<String>()                                        ),
                /* 39 */arrayOf<Any?>("mailto:test@test.com"         , "com", "mailto:test@test.com"         , "mailto:test@test"         , ""                 , "mailto:test@test.com", "mailto:test@test", true    , false       , true        , true   , true        , "mailto:test@test.com"         , "mailto:test@test.com"                  , null        , arrayOf<String>()                                        ),
                /* 40 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , ""                                      , ""          , arrayOf<String>()                                        ),
                /* 41 */arrayOf<Any?>(".ext"                         , ""   , ".ext"                         , ".ext"                     , ""                 , ".ext"                , ".ext"            , false   , true        , false       , false  , false       , ".ext"                         , ".ext"                                  , ""          , arrayOf<String>()                                        ),
                /* 42 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName"                     , "fileName"                              , ""          , arrayOf<String>()                                        ),
                /* 43 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName"                     , "fileName"                              , ""          , arrayOf<String>()                                        ),
                /* 44 */arrayOf<Any?>("fileName.ext"                 , "ext", "fileName.ext"                 , "fileName"                 , ""                 , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "fileName"                     , "fileName.ext"                          , ""          , arrayOf<String>()                                        ),
                /* 45 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , ""                                      , "."         , arrayOf<String>()                                        ),
                /* 46 */arrayOf<Any?>(".ext"                         , ""   , ".ext"                         , ".ext"                     , ""                 , ".ext"                , ".ext"            , false   , true        , false       , false  , false       , ".ext"                         , ".ext"                                  , "."         , arrayOf<String>()                                        ),
                /* 47 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName"                     , "fileName"                              , "."         , arrayOf<String>()                                        ),
                /* 48 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName"                     , "fileName"                              , "."         , arrayOf<String>()                                        ),
                /* 49 */arrayOf<Any?>("fileName.ext"                 , "ext", "fileName.ext"                 , "fileName"                 , ""                 , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "fileName"                     , "fileName.ext"                          , "."         , arrayOf<String>()                                        ),
                /* 50 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , ""                                      , ".ext"      , arrayOf<String>()                                        ),
                /* 51 */arrayOf<Any?>(".ext"                         , ""   , ".ext"                         , ".ext"                     , ""                 , ".ext"                , ".ext"            , false   , true        , false       , false  , false       , ".ext.ext"                     , ".ext"                                  , ".ext"      , arrayOf<String>()                                        ),
                /* 52 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName.ext"                 , "fileName"                              , ".ext"      , arrayOf<String>()                                        ),
                /* 53 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName.ext"                 , "fileName"                              , ".ext"      , arrayOf<String>()                                        ),
                /* 54 */arrayOf<Any?>("fileName.ext"                 , "ext", "fileName.ext"                 , "fileName"                 , ""                 , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "fileName.ext"                 , "fileName.ext"                          , ".ext"      , arrayOf<String>()                                        ),
                /* 55 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , ""                                      , ".ext2"     , arrayOf<String>()                                        ),
                /* 56 */arrayOf<Any?>(".ext"                         , ""   , ".ext"                         , ".ext"                     , ""                 , ".ext"                , ".ext"            , false   , true        , false       , false  , false       , ".ext.ext2"                    , ".ext"                                  , ".ext2"     , arrayOf<String>()                                        ),
                /* 57 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName.ext2"                , "fileName"                              , ".ext2"     , arrayOf<String>()                                        ),
                /* 58 */arrayOf<Any?>("fileName"                     , ""   , "fileName"                     , "fileName"                 , ""                 , "fileName"            , "fileName"        , false   , true        , false       , false  , false       , "fileName.ext2"                , "fileName"                              , ".ext2"     , arrayOf<String>()                                        ),
                /* 59 */arrayOf<Any?>("fileName.ext"                 , "ext", "fileName.ext"                 , "fileName"                 , ""                 , "fileName.ext"        , "fileName"        , true    , true        , false       , false  , false       , "fileName.ext2"                , "fileName.ext"                          , ".ext2"     , arrayOf<String>()                                        ),
                /* 60 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , ""                                      , null        , arrayOf<String>("")                                      ),
                /* 61 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , ""                                      , null        , arrayOf<String>(".")                                     ),
                /* 62 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , ""                                      , null        , arrayOf<String>("./")                                    ),
                /* 63 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , ""                                      , null        , arrayOf<String>("/./")                                   ),
                /* 64 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1"                              , null        , arrayOf<String>("SubDir1")                               ),
                /* 65 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1"                              , null        , arrayOf<String>("/SubDir1")                              ),
                /* 66 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1"                              , null        , arrayOf<String>("SubDir1/")                              ),
                /* 67 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1"                              , null        , arrayOf<String>("/SubDir1/")                             ),
                /* 68 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1/SubDir2"                      , null        , arrayOf<String>("/SubDir1/", "/SubDir2/")                ),
                /* 69 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1"                             , null        , arrayOf<String>(".SubDir1")                              ),
                /* 70 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1"                             , null        , arrayOf<String>("/.SubDir1")                             ),
                /* 71 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1"                             , null        , arrayOf<String>(".SubDir1/")                             ),
                /* 72 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1"                             , null        , arrayOf<String>("/.SubDir1/")                            ),
                /* 73 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1/SubDir2"                     , null        , arrayOf<String>("/.SubDir1/", "/SubDir2/")               ),
                /* 74 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1"                              , null        , arrayOf<String>("SubDir1.")                              ),
                /* 75 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1"                              , null        , arrayOf<String>("/SubDir1.")                             ),
                /* 76 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1"                              , null        , arrayOf<String>("SubDir1./")                             ),
                /* 77 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1"                              , null        , arrayOf<String>("/SubDir1./")                            ),
                /* 78 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/SubDir1/SubDir2"                      , null        , arrayOf<String>("/SubDir1./", "/SubDir2/")               ),
                /* 79 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1"                             , null        , arrayOf<String>(".SubDir1.")                             ),
                /* 80 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1"                             , null        , arrayOf<String>("/.SubDir1.")                            ),
                /* 81 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1"                             , null        , arrayOf<String>(".SubDir1./")                            ),
                /* 82 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1"                             , null        , arrayOf<String>("/.SubDir1./")                           ),
                /* 83 */arrayOf<Any?>(""                             , ""   , ""                             , ""                         , ""                 , ""                    , ""                , false   , true        , false       , false  , false       , ""                             , "/.SubDir1/SubDir2"                     , null        , arrayOf<String>("/.SubDir1./", "/SubDir2/")              ),
                /* 84 */arrayOf<Any?>("/Dir1/Dir2/Dir3/Dir4"         , ""   , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3/Dir4"     , "/Dir1/Dir2/Dir3/" , "Dir4"                , "Dir4"            , false   , false       , false       , false  , true        , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3"                       , null        , arrayOf<String>("..")                                    ),
                /* 85 */arrayOf<Any?>("/Dir1/Dir2/Dir3/Dir4"         , ""   , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3/Dir4"     , "/Dir1/Dir2/Dir3/" , "Dir4"                , "Dir4"            , false   , false       , false       , false  , true        , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3"                       , null        , arrayOf<String>("../")                                   ),
                /* 86 */arrayOf<Any?>("/Dir1/Dir2/Dir3/Dir4"         , ""   , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3/Dir4"     , "/Dir1/Dir2/Dir3/" , "Dir4"                , "Dir4"            , false   , false       , false       , false  , true        , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3"                       , null        , arrayOf<String>("/../")                                  ),
                /* 87 */arrayOf<Any?>("/Dir1/Dir2/Dir3/Dir4"         , ""   , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3/Dir4"     , "/Dir1/Dir2/Dir3/" , "Dir4"                , "Dir4"            , false   , false       , false       , false  , true        , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3a"                      , null        , arrayOf<String>("..", "..", "Dir3a")                     ),
                /* 88 */arrayOf<Any?>("/Dir1/Dir2/Dir3/Dir4"         , ""   , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3/Dir4"     , "/Dir1/Dir2/Dir3/" , "Dir4"                , "Dir4"            , false   , false       , false       , false  , true        , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2a"                           , null        , arrayOf<String>("..", "..", "..", "Dir2a")               ),
                /* 90 */arrayOf<Any?>("/Dir1/Dir2/Dir3/Dir4"         , ""   , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3/Dir4"     , "/Dir1/Dir2/Dir3/" , "Dir4"                , "Dir4"            , false   , false       , false       , false  , true        , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1a"                                , null        , arrayOf<String>("..", "..", "..", "..", "Dir1a")         ),
                /* 91 */arrayOf<Any?>("/Dir1/Dir2/Dir3/Dir4"         , ""   , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1/Dir2/Dir3/Dir4"     , "/Dir1/Dir2/Dir3/" , "Dir4"                , "Dir4"            , false   , false       , false       , false  , true        , "/Dir1/Dir2/Dir3/Dir4"         , "/Dir1b"                                , null        , arrayOf<String>("..", "..", "..", "..", "..", "Dir1b")   )
            )
                /* @formatter:on */
            } else {
                val data = arrayListOf(
                        /* 00 */  pathInfoTestData(".", null),
                        /* 01 */  pathInfoTestData(".ext", null),

                        /* 02 */  pathInfoTestData("fileName", null),
                        /* 03 */  pathInfoTestData("fileName.", null),
                        /* 04 */  pathInfoTestData("fileName.ext", null),

                        /* 05 */  pathInfoTestData("./fileName", null),
                        /* 06 */  pathInfoTestData("./SubDir1/fileName", null),
                        /* 07 */  pathInfoTestData("./SubDir1.sub/fileName", null),
                        /* 08 */  pathInfoTestData("./SubDir1/SubDir2/fileName", null),
                        /* 09 */  pathInfoTestData("././SubDir1/fileName", null),

                        /* 10 */  pathInfoTestData("./fileName.", null),
                        /* 11 */  pathInfoTestData("./SubDir1/fileName.", null),
                        /* 12 */  pathInfoTestData("./SubDir1.sub/fileName.", null),
                        /* 13 */  pathInfoTestData("./SubDir1/SubDir2/fileName.", null),
                        /* 14 */  pathInfoTestData("././SubDir1/fileName.", null),

                        /* 15 */  pathInfoTestData("./fileName.ext", null),
                        /* 16 */  pathInfoTestData("./SubDir1/fileName.ext", null),
                        /* 17 */  pathInfoTestData("./SubDir1.sub/fileName.ext", null),
                        /* 18 */  pathInfoTestData("./SubDir1/SubDir2/fileName.ext", null),
                        /* 19 */  pathInfoTestData("././SubDir1/fileName.ext", null),

                        /* 20 */  pathInfoTestData("/fileName", null),
                        /* 21 */  pathInfoTestData("/SubDir1/fileName", null),
                        /* 22 */  pathInfoTestData("/SubDir1.sub/fileName", null),
                        /* 23 */  pathInfoTestData("/SubDir1/SubDir2/fileName", null),
                        /* 24 */  pathInfoTestData("/./SubDir1/fileName", null),

                        /* 25 */  pathInfoTestData("/fileName.", null),
                        /* 26 */  pathInfoTestData("/SubDir1/fileName.", null),
                        /* 27 */  pathInfoTestData("/SubDir1.sub/fileName.", null),
                        /* 28 */  pathInfoTestData("/SubDir1/SubDir2/fileName.", null),
                        /* 29 */  pathInfoTestData("/./SubDir1/fileName.", null),

                        /* 30 */  pathInfoTestData("/fileName.ext", null),
                        /* 31 */  pathInfoTestData("/SubDir1/fileName.ext", null),
                        /* 32 */  pathInfoTestData("/SubDir1.sub/fileName.ext", null),
                        /* 33 */  pathInfoTestData("/SubDir1/SubDir2/fileName.ext", null),
                        /* 34 */  pathInfoTestData("/./SubDir1/fileName.ext", null),

                        /* 35 */  pathInfoTestData("http://fileName", null),
                        /* 36 */  pathInfoTestData("https://fileName", null),
                        /* 37 */  pathInfoTestData("ftp://fileName", null),
                        /* 38 */  pathInfoTestData("file://fileName", null),
                        /* 39 */  pathInfoTestData("mailto:test@test.com", null),

                        /* 40 */  pathInfoTestData(".", ""),
                        /* 41 */  pathInfoTestData(".ext", ""),
                        /* 42 */  pathInfoTestData("fileName", ""),
                        /* 43 */  pathInfoTestData("fileName.", ""),
                        /* 44 */  pathInfoTestData("fileName.ext", ""),

                        /* 45 */  pathInfoTestData(".", "."),
                        /* 46 */  pathInfoTestData(".ext", "."),
                        /* 47 */  pathInfoTestData("fileName", "."),
                        /* 48 */  pathInfoTestData("fileName.", "."),
                        /* 49 */  pathInfoTestData("fileName.ext", "."),

                        /* 50 */  pathInfoTestData(".", ".ext"),
                        /* 51 */  pathInfoTestData(".ext", ".ext"),
                        /* 52 */  pathInfoTestData("fileName", ".ext"),
                        /* 53 */  pathInfoTestData("fileName.", ".ext"),
                        /* 54 */  pathInfoTestData("fileName.ext", ".ext"),

                        /* 55 */  pathInfoTestData(".", ".ext2"),
                        /* 56 */  pathInfoTestData(".ext", ".ext2"),
                        /* 57 */  pathInfoTestData("fileName", ".ext2"),
                        /* 58 */  pathInfoTestData("fileName.", ".ext2"),
                        /* 59 */  pathInfoTestData("fileName.ext", ".ext2"),

                        /* 60 */  pathInfoTestData("", null, ""),
                        /* 61 */  pathInfoTestData("", null, "."),
                        /* 62 */  pathInfoTestData("", null, "./"),
                        /* 63 */  pathInfoTestData("", null, "/./"),
                        /* 64 */  pathInfoTestData("", null, "SubDir1"),
                        /* 65 */  pathInfoTestData("", null, "/SubDir1"),
                        /* 66 */  pathInfoTestData("", null, "SubDir1/"),
                        /* 67 */  pathInfoTestData("", null, "/SubDir1/"),
                        /* 68 */  pathInfoTestData("", null, "/SubDir1/", "/SubDir2/"),

                        /* 69 */  pathInfoTestData("", null, ".SubDir1"),
                        /* 70 */  pathInfoTestData("", null, "/.SubDir1"),
                        /* 71 */  pathInfoTestData("", null, ".SubDir1/"),
                        /* 72 */  pathInfoTestData("", null, "/.SubDir1/"),
                        /* 73 */  pathInfoTestData("", null, "/.SubDir1/", "/SubDir2/"),

                        /* 74 */  pathInfoTestData("", null, "SubDir1."),
                        /* 75 */  pathInfoTestData("", null, "/SubDir1."),
                        /* 76 */  pathInfoTestData("", null, "SubDir1./"),
                        /* 77 */  pathInfoTestData("", null, "/SubDir1./"),
                        /* 78 */  pathInfoTestData("", null, "/SubDir1./", "/SubDir2/"),

                        /* 79 */  pathInfoTestData("", null, ".SubDir1."),
                        /* 80 */  pathInfoTestData("", null, "/.SubDir1."),
                        /* 81 */  pathInfoTestData("", null, ".SubDir1./"),
                        /* 82 */  pathInfoTestData("", null, "/.SubDir1./"),
                        /* 83 */  pathInfoTestData("", null, "/.SubDir1./", "/SubDir2/"),

                        /* 84 */  pathInfoTestData("../Dir1/", null, ""),
                        /* 85 */  pathInfoTestData("../Dir1/", null, "."),
                        /* 86 */  pathInfoTestData("../Dir1/", null, "./"),
                        /* 87 */  pathInfoTestData("../Dir1/", null, "/./"),
                        /* 88 */  pathInfoTestData("../Dir1/", null, "SubDir1"),
                        /* 89 */  pathInfoTestData("../Dir1/", null, "/SubDir1"),
                        /* 90 */  pathInfoTestData("../Dir1/", null, "SubDir1/"),
                        /* 91 */  pathInfoTestData("../Dir1/", null, "/SubDir1/"),
                        /* 92 */  pathInfoTestData("../Dir1/", null, "/SubDir1/", "/SubDir2/"),
                        /* 93 */  pathInfoTestData("../Dir1/", null, "/SubDir1/", "/SubDir2/", "fileName"),
                        /* 94 */  pathInfoTestData("../Dir1/", null, "/SubDir1/", "/SubDir2/", "fileName."),
                        /* 95 */  pathInfoTestData("../Dir1/", null, "/SubDir1/", "/SubDir2/", "fileName.ext")

                )
                val header = arrayOf(
                        "fullPath",
                        "ext",
                        "filePath",
                        "filePathNoExt",
                        "path",
                        "fileName",
                        "fileNameNoExt",
                        "hasExt",
                        "isRelative",
                        "isExternal",
                        "isURI",
                        "isAbsolute",
                        "withExt",
                        "append",
                        "addWithExt",
                        "addAppend"
                )

                printData(data, header)
                return data
            }
        }

        // fullFilePath
        // ext
        // filePath
        // filePathNoExt
        // path
        // fileName
        // fileNameNoExt
        // hasExt
        // isRelative
        // isExternal
        // isURI
        // isAbsolute
        // withExt
        // append

        fun pathInfoTestData(path: String, withExt: String?, vararg append: String): Array<Any?> {
            val pathInfo: FilePathInfo = FilePathInfo(FilePathInfo.removeEnd(path, "."))

            return arrayOf<Any?>(
                    pathInfo.fullFilePath,
                    pathInfo.ext,
                    pathInfo.filePath,
                    pathInfo.filePathNoExt,
                    pathInfo.path,
                    pathInfo.fileName,
                    pathInfo.fileNameNoExt,
                    pathInfo.hasExt(),
                    pathInfo.isRelative,
                    pathInfo.isExternalReference,
                    pathInfo.isURI,
                    pathInfo.isAbsoluteReference,
                    if (pathInfo.fullFilePath.isEmpty()) "" else pathInfo.withExt(withExt).fullFilePath,
                    pathInfo.append(*append).fullFilePath
                    , withExt
                    , append
            );
        }
    }
}
