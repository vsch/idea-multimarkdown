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

import com.vladsch.plugin.test.util.ParamRowGenerator
import com.vladsch.plugin.test.util.ParamRowGenerator.ColumnProvider
import com.vladsch.plugin.test.util.ParamRowGenerator.Decorator
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class TestLinkRef constructor(
    val location: String
    , val fullPath: String
    , val hasAnchor: Boolean
    , val isSelfAnchor: Boolean
    , val isEmpty: Boolean
    , val isDoNothing: Boolean
    , val anchorText: String
    , val isRelative: Boolean
    , val isAbsolute: Boolean
    , val isExternal: Boolean
    , val filePath: String
    , val filePathNoExt: String
    , val filePathWithAnchor: String
    , val filePathNoExtWithAnchor: String
) {

    val linkRef = LinkRef.parseLinkRef(FileRef(""), fullPath, null, { containingFile, fullPath, anchor, targetRef, isNormalized -> LinkRef(containingFile, fullPath, anchor, targetRef, isNormalized) })

    /* @formatter:off */
    @Test fun test_hasAnchor() { Assert.assertEquals(location, hasAnchor, linkRef.hasAnchor) }
    @Test fun test_isSelfAnchor() { Assert.assertEquals(location, isSelfAnchor, linkRef.isSelfAnchor) }
    @Test fun test_isEmpty() { Assert.assertEquals(location, isEmpty, linkRef.isEmpty) }
    @Test fun test_isDoNothing() { Assert.assertEquals(location, isDoNothing, linkRef.isDoNothingAnchor) }
    @Test fun test_anchor() { Assert.assertEquals(location, if (anchorText.isEmpty()) null else anchorText.removePrefix("#"), linkRef.anchor) }
    @Test fun test_anchorText() { Assert.assertEquals(location, anchorText, linkRef.anchorText) }
    @Test fun test_isRelative() { Assert.assertEquals(location, isRelative, linkRef.isRelative) }
    @Test fun test_isAbsolute() { Assert.assertEquals(location, isAbsolute, linkRef.isAbsolute) }
    @Test fun test_isExternal() { Assert.assertEquals(location, isExternal, linkRef.isExternal) }
    @Test fun test_filePath() { Assert.assertEquals(location, filePath, linkRef.filePath) }
    @Test fun test_filePathNoExt() { Assert.assertEquals(location, filePathNoExt, linkRef.filePathNoExt) }
    @Test fun test_filePathWithAnchor() { Assert.assertEquals(location, filePathWithAnchor, linkRef.filePathWithAnchor) }
    @Test fun test_filePathNoExtWithAnchor() { Assert.assertEquals(location, filePathNoExtWithAnchor, linkRef.filePathNoExtWithAnchor) }
    /* @formatter:on */

    companion object {
        class RowGenerator(private val lineProvider: LineProvider? = null) : ParamRowGenerator() {
            fun row(
                fullPath: String
                , hasAnchor: Boolean
                , isSelfAnchor: Boolean
                , isEmpty: Boolean
                , isDoNothing: Boolean
                , anchorText: String
                , isRelative: Boolean
                , isAbsolute: Boolean
                , isExternal: Boolean
                , filePath: String
                , filePathNoExt: String
                , filePathWithAnchor: String
                , filePathNoExtWithAnchor: String
            ): RowGenerator {
                super.row(1, arrayOf(fullPath, hasAnchor, isSelfAnchor, isEmpty, isDoNothing, anchorText, isRelative, isAbsolute, isExternal, filePath, filePathNoExt, filePathWithAnchor, filePathNoExtWithAnchor),
                    Decorator { _, prefix, suffix -> "$prefix\"$fullPath\"\n$suffix" },
                    lineProvider,
                    ColumnProvider { 39 })
                return this
            }
        }

        @Parameterized.Parameters(name = "{1}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            val data = RowGenerator()
                /* @formatter:off */
                /*                                                   , "hasAnchor", "isSelfAnchor", "isEmpty", "isDoNothing", "anchorText" , "isRelative", "isAbsolute", "isExternal", "filePath"                             , "filePathNoExt"                     , "filePathWithAnchor"                                , "filePathNoExtWithAnchor"                        */
                .row(""                                              , false      , false         , true     , false        , ""           , true        , false       , false       , ""                                     , ""                                  , ""                                                  , ""                                              )
                .row("#"                                             , true       , true          , false    , true         , "#"          , true        , false       , false       , ""                                     , ""                                  , "#"                                                 , "#"                                             )
                .row("#anchor"                                       , true       , true          , false    , false        , "#anchor"    , true        , false       , false       , ""                                     , ""                                  , "#anchor"                                           , "#anchor"                                       )
                .row("#anchor.ext"                                   , true       , true          , false    , false        , "#anchor.ext", true        , false       , false       , ""                                     , ""                                  , "#anchor.ext"                                       , "#anchor.ext"                                   )
                .row("fileName#anchor"                               , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "fileName"                             , "fileName"                          , "fileName#anchor"                                   , "fileName#anchor"                               )
                .row("fileName#anchor.ext"                           , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "fileName"                             , "fileName"                          , "fileName#anchor.ext"                               , "fileName#anchor.ext"                           )
                .row("fileName.ext#anchor"                           , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "fileName.ext"                         , "fileName"                          , "fileName.ext#anchor"                               , "fileName#anchor"                               )
                .row("fileName.ext#anchor.ext"                       , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "fileName.ext"                         , "fileName"                          , "fileName.ext#anchor.ext"                           , "fileName#anchor.ext"                           )
                .row("../../wiki/Home"                               , false      , false         , false    , false        , ""           , true        , false       , false       , "../../wiki/Home"                      , "../../wiki/Home"                   , "../../wiki/Home"                                   , "../../wiki/Home"                               )
                .row("../../wiki/Home#"                              , true       , false         , false    , false        , "#"          , true        , false       , false       , "../../wiki/Home"                      , "../../wiki/Home"                   , "../../wiki/Home#"                                  , "../../wiki/Home#"                              )
                .row("../../wiki/Home#anchor"                        , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "../../wiki/Home"                      , "../../wiki/Home"                   , "../../wiki/Home#anchor"                            , "../../wiki/Home#anchor"                        )
                .row("relDir/fileName"                               , false      , false         , false    , false        , ""           , true        , false       , false       , "relDir/fileName"                      , "relDir/fileName"                   , "relDir/fileName"                                   , "relDir/fileName"                               )
                .row("relDir/fileName#"                              , true       , false         , false    , false        , "#"          , true        , false       , false       , "relDir/fileName"                      , "relDir/fileName"                   , "relDir/fileName#"                                  , "relDir/fileName#"                              )
                .row("relDir/fileName#anchor"                        , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "relDir/fileName"                      , "relDir/fileName"                   , "relDir/fileName#anchor"                            , "relDir/fileName#anchor"                        )
                .row("relDir/fileName#anchor.ext"                    , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "relDir/fileName"                      , "relDir/fileName"                   , "relDir/fileName#anchor.ext"                        , "relDir/fileName#anchor.ext"                    )
                .row("relDir/fileName.ext#anchor"                    , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "relDir/fileName.ext"                  , "relDir/fileName"                   , "relDir/fileName.ext#anchor"                        , "relDir/fileName#anchor"                        )
                .row("relDir/fileName.ext#anchor.ext"                , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "relDir/fileName.ext"                  , "relDir/fileName"                   , "relDir/fileName.ext#anchor.ext"                    , "relDir/fileName#anchor.ext"                    )
                .row("/absDir/fileName"                              , false      , false         , false    , false        , ""           , false       , true        , false       , "/absDir/fileName"                     , "/absDir/fileName"                  , "/absDir/fileName"                                  , "/absDir/fileName"                              )
                .row("/absDir/fileName#"                             , true       , false         , false    , false        , "#"          , false       , true        , false       , "/absDir/fileName"                     , "/absDir/fileName"                  , "/absDir/fileName#"                                 , "/absDir/fileName#"                             )
                .row("/absDir/fileName#anchor"                       , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "/absDir/fileName"                     , "/absDir/fileName"                  , "/absDir/fileName#anchor"                           , "/absDir/fileName#anchor"                       )
                .row("/absDir/fileName#anchor.ext"                   , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "/absDir/fileName"                     , "/absDir/fileName"                  , "/absDir/fileName#anchor.ext"                       , "/absDir/fileName#anchor.ext"                   )
                .row("/absDir/fileName.ext#anchor"                   , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "/absDir/fileName.ext"                 , "/absDir/fileName"                  , "/absDir/fileName.ext#anchor"                       , "/absDir/fileName#anchor"                       )
                .row("/absDir/fileName.ext#anchor.ext"               , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "/absDir/fileName.ext"                 , "/absDir/fileName"                  , "/absDir/fileName.ext#anchor.ext"                   , "/absDir/fileName#anchor.ext"                   )
                .row("file:/absDir/fileName"                         , false      , false         , false    , false        , ""           , false       , true        , false       , "file:/absDir/fileName"                , "file:/absDir/fileName"             , "file:/absDir/fileName"                             , "file:/absDir/fileName"                         )
                .row("file:/absDir/fileName#"                        , true       , false         , false    , false        , "#"          , false       , true        , false       , "file:/absDir/fileName"                , "file:/absDir/fileName"             , "file:/absDir/fileName#"                            , "file:/absDir/fileName#"                        )
                .row("file:/absDir/fileName#anchor"                  , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "file:/absDir/fileName"                , "file:/absDir/fileName"             , "file:/absDir/fileName#anchor"                      , "file:/absDir/fileName#anchor"                  )
                .row("file:/absDir/fileName#anchor.ext"              , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "file:/absDir/fileName"                , "file:/absDir/fileName"             , "file:/absDir/fileName#anchor.ext"                  , "file:/absDir/fileName#anchor.ext"              )
                .row("file:/absDir/fileName.ext#anchor"              , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "file:/absDir/fileName.ext"            , "file:/absDir/fileName"             , "file:/absDir/fileName.ext#anchor"                  , "file:/absDir/fileName#anchor"                  )
                .row("file:/absDir/fileName.ext#anchor.ext"          , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "file:/absDir/fileName.ext"            , "file:/absDir/fileName"             , "file:/absDir/fileName.ext#anchor.ext"              , "file:/absDir/fileName#anchor.ext"              )
                .row("file:///absDir/fileName"                       , false      , false         , false    , false        , ""           , false       , true        , false       , "file:///absDir/fileName"              , "file:///absDir/fileName"           , "file:///absDir/fileName"                           , "file:///absDir/fileName"                       )
                .row("file:///absDir/fileName#"                      , true       , false         , false    , false        , "#"          , false       , true        , false       , "file:///absDir/fileName"              , "file:///absDir/fileName"           , "file:///absDir/fileName#"                          , "file:///absDir/fileName#"                      )
                .row("file:///absDir/fileName#anchor"                , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "file:///absDir/fileName"              , "file:///absDir/fileName"           , "file:///absDir/fileName#anchor"                    , "file:///absDir/fileName#anchor"                )
                .row("file:///absDir/fileName#anchor.ext"            , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "file:///absDir/fileName"              , "file:///absDir/fileName"           , "file:///absDir/fileName#anchor.ext"                , "file:///absDir/fileName#anchor.ext"            )
                .row("file:///absDir/fileName.ext#anchor"            , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "file:///absDir/fileName.ext"          , "file:///absDir/fileName"           , "file:///absDir/fileName.ext#anchor"                , "file:///absDir/fileName#anchor"                )
                .row("file:///absDir/fileName.ext#anchor.ext"        , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "file:///absDir/fileName.ext"          , "file:///absDir/fileName"           , "file:///absDir/fileName.ext#anchor.ext"            , "file:///absDir/fileName#anchor.ext"            )
                .row("http://test.com/absDir/fileName"               , false      , false         , false    , false        , ""           , false       , true        , true        , "http://test.com/absDir/fileName"      , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName"                   , "http://test.com/absDir/fileName"               )
                .row("http://test.com/absDir/fileName#"              , true       , false         , false    , false        , "#"          , false       , true        , true        , "http://test.com/absDir/fileName"      , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName#"                  , "http://test.com/absDir/fileName#"              )
                .row("http://test.com/absDir/fileName#anchor"        , true       , false         , false    , false        , "#anchor"    , false       , true        , true        , "http://test.com/absDir/fileName"      , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName#anchor"            , "http://test.com/absDir/fileName#anchor"        )
                .row("http://test.com/absDir/fileName#anchor.ext"    , true       , false         , false    , false        , "#anchor.ext", false       , true        , true        , "http://test.com/absDir/fileName"      , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName#anchor.ext"        , "http://test.com/absDir/fileName#anchor.ext"    )
                .row("http://test.com/absDir/fileName.ext#anchor"    , true       , false         , false    , false        , "#anchor"    , false       , true        , true        , "http://test.com/absDir/fileName.ext"  , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName.ext#anchor"        , "http://test.com/absDir/fileName#anchor"        )
                .row("http://test.com/absDir/fileName.ext#anchor.ext", true       , false         , false    , false        , "#anchor.ext", false       , true        , true        , "http://test.com/absDir/fileName.ext"  , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName.ext#anchor.ext"    , "http://test.com/absDir/fileName#anchor.ext"    )
                /* @formatter:on */
                .rows

            return data
        }
    }
}

