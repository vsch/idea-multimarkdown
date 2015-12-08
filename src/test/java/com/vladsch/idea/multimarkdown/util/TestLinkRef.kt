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
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.util

import com.vladsch.idea.multimarkdown.printData
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class TestLinkRef constructor(val fullPath: String
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

    val linkRef = LinkRef.parseLinkRef(FileRef(""), fullPath, null, { containingFile, fullPath, anchor, targetRef -> LinkRef(containingFile, fullPath, anchor, targetRef) });

    /* @formatter:off */
    @Test fun test_hasAnchor() { Assert.assertEquals(hasAnchor, linkRef.hasAnchor) }
    @Test fun test_isSelfAnchor() { Assert.assertEquals(isSelfAnchor, linkRef.isSelfAnchor) }
    @Test fun test_isEmpty() { Assert.assertEquals(isEmpty, linkRef.isEmpty) }
    @Test fun test_isDoNothing() { Assert.assertEquals(isDoNothing, linkRef.isDoNothingAnchor) }
    @Test fun test_anchor() { Assert.assertEquals(if (anchorText.isEmpty()) null else anchorText.removePrefix("#"), linkRef.anchor) }
    @Test fun test_anchorText() { Assert.assertEquals(anchorText, linkRef.anchorText) }
    @Test fun test_isRelative() { Assert.assertEquals(isRelative, linkRef.isRelative) }
    @Test fun test_isAbsolute() { Assert.assertEquals(isAbsolute, linkRef.isAbsolute) }
    @Test fun test_isExternal() { Assert.assertEquals(isExternal, linkRef.isExternal) }
    @Test fun test_filePath() { Assert.assertEquals(filePath, linkRef.filePath) }
    @Test fun test_filePathNoExt() { Assert.assertEquals(filePathNoExt, linkRef.filePathNoExt) }
    @Test fun test_filePathWithAnchor() { Assert.assertEquals(filePathWithAnchor, linkRef.filePathWithAnchor) }
    @Test fun test_filePathNoExtWithAnchor() { Assert.assertEquals(filePathNoExtWithAnchor, linkRef.filePathNoExtWithAnchor) }
    /* @formatter:on */

    companion object {
        @Parameterized.Parameters(name = "{index}: filePath = {0}")
        @JvmStatic
        public fun data(): Collection<Array<Any?>> {
            val cleanData = false
            //            val test = TestPathInfo("", "", "", "", "", "", "", false, true, false, false, false, "", "", null, arrayOf<String>())
            var data = arrayListOf<Array<Any?>>(
                    /* @formatter:off */
                /*      arrayOf<Any?>("fullPath"                                      , "hasAnchor", "isSelfAnchor", "isEmpty", "isDoNothing", "anchorText" , "isRelative", "isAbsolute", "isExternal", "filePath"                             , "filePathNoExt"                     , "filePathWithAnchor"                                , "filePathNoExtWithAnchor"                       ) */
                /*  0 */arrayOf<Any?>(""                                              , false      , false         , true     , false        , ""           , true        , false       , false       , ""                                     , ""                                  , ""                                                  , ""                                              ),
                /*  1 */arrayOf<Any?>("#"                                             , true       , true          , false    , true         , "#"          , false       , true        , false       , ""                                     , ""                                  , "#"                                                 , "#"                                             ),
                /*  2 */arrayOf<Any?>("#anchor"                                       , true       , true          , false    , false        , "#anchor"    , false       , true        , false       , ""                                     , ""                                  , "#anchor"                                           , "#anchor"                                       ),
                /*  3 */arrayOf<Any?>("#anchor.ext"                                   , true       , true          , false    , false        , "#anchor.ext", false       , true        , false       , ""                                     , ""                                  , "#anchor.ext"                                       , "#anchor.ext"                                   ),
                /*  4 */arrayOf<Any?>("fileName#anchor"                               , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "fileName"                             , "fileName"                          , "fileName#anchor"                                   , "fileName#anchor"                               ),
                /*  5 */arrayOf<Any?>("fileName#anchor.ext"                           , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "fileName"                             , "fileName"                          , "fileName#anchor.ext"                               , "fileName#anchor.ext"                           ),
                /*  6 */arrayOf<Any?>("fileName.ext#anchor"                           , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "fileName.ext"                         , "fileName"                          , "fileName.ext#anchor"                               , "fileName#anchor"                               ),
                /*  7 */arrayOf<Any?>("fileName.ext#anchor.ext"                       , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "fileName.ext"                         , "fileName"                          , "fileName.ext#anchor.ext"                           , "fileName#anchor.ext"                           ),
                /*  8 */arrayOf<Any?>("../../wiki/Home"                               , false      , false         , false    , false        , ""           , true        , false       , false       , "../../wiki/Home"                      , "../../wiki/Home"                   , "../../wiki/Home"                                   , "../../wiki/Home"                               ),
                /*  9 */arrayOf<Any?>("../../wiki/Home#"                              , true       , false         , false    , false        , "#"          , true        , false       , false       , "../../wiki/Home"                      , "../../wiki/Home"                   , "../../wiki/Home#"                                  , "../../wiki/Home#"                              ),
                /* 10 */arrayOf<Any?>("../../wiki/Home#anchor"                        , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "../../wiki/Home"                      , "../../wiki/Home"                   , "../../wiki/Home#anchor"                            , "../../wiki/Home#anchor"                        ),
                /* 11 */arrayOf<Any?>("relDir/fileName"                               , false      , false         , false    , false        , ""           , true        , false       , false       , "relDir/fileName"                      , "relDir/fileName"                   , "relDir/fileName"                                   , "relDir/fileName"                               ),
                /* 12 */arrayOf<Any?>("relDir/fileName#"                              , true       , false         , false    , false        , "#"          , true        , false       , false       , "relDir/fileName"                      , "relDir/fileName"                   , "relDir/fileName#"                                  , "relDir/fileName#"                              ),
                /* 13 */arrayOf<Any?>("relDir/fileName#anchor"                        , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "relDir/fileName"                      , "relDir/fileName"                   , "relDir/fileName#anchor"                            , "relDir/fileName#anchor"                        ),
                /* 14 */arrayOf<Any?>("relDir/fileName#anchor.ext"                    , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "relDir/fileName"                      , "relDir/fileName"                   , "relDir/fileName#anchor.ext"                        , "relDir/fileName#anchor.ext"                    ),
                /* 15 */arrayOf<Any?>("relDir/fileName.ext#anchor"                    , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "relDir/fileName.ext"                  , "relDir/fileName"                   , "relDir/fileName.ext#anchor"                        , "relDir/fileName#anchor"                        ),
                /* 16 */arrayOf<Any?>("relDir/fileName.ext#anchor.ext"                , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "relDir/fileName.ext"                  , "relDir/fileName"                   , "relDir/fileName.ext#anchor.ext"                    , "relDir/fileName#anchor.ext"                    ),
                /* 17 */arrayOf<Any?>("/absDir/fileName"                              , false      , false         , false    , false        , ""           , false       , true        , false       , "/absDir/fileName"                     , "/absDir/fileName"                  , "/absDir/fileName"                                  , "/absDir/fileName"                              ),
                /* 18 */arrayOf<Any?>("/absDir/fileName#"                             , true       , false         , false    , false        , "#"          , false       , true        , false       , "/absDir/fileName"                     , "/absDir/fileName"                  , "/absDir/fileName#"                                 , "/absDir/fileName#"                             ),
                /* 19 */arrayOf<Any?>("/absDir/fileName#anchor"                       , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "/absDir/fileName"                     , "/absDir/fileName"                  , "/absDir/fileName#anchor"                           , "/absDir/fileName#anchor"                       ),
                /* 20 */arrayOf<Any?>("/absDir/fileName#anchor.ext"                   , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "/absDir/fileName"                     , "/absDir/fileName"                  , "/absDir/fileName#anchor.ext"                       , "/absDir/fileName#anchor.ext"                   ),
                /* 21 */arrayOf<Any?>("/absDir/fileName.ext#anchor"                   , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "/absDir/fileName.ext"                 , "/absDir/fileName"                  , "/absDir/fileName.ext#anchor"                       , "/absDir/fileName#anchor"                       ),
                /* 22 */arrayOf<Any?>("/absDir/fileName.ext#anchor.ext"               , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "/absDir/fileName.ext"                 , "/absDir/fileName"                  , "/absDir/fileName.ext#anchor.ext"                   , "/absDir/fileName#anchor.ext"                   ),
                /* 23 */arrayOf<Any?>("file:/absDir/fileName"                         , false      , false         , false    , false        , ""           , true        , false       , false       , "file:/absDir/fileName"                , "file:/absDir/fileName"             , "file:/absDir/fileName"                             , "file:/absDir/fileName"                         ),
                /* 24 */arrayOf<Any?>("file:/absDir/fileName#"                        , true       , false         , false    , false        , "#"          , true        , false       , false       , "file:/absDir/fileName"                , "file:/absDir/fileName"             , "file:/absDir/fileName#"                            , "file:/absDir/fileName#"                        ),
                /* 25 */arrayOf<Any?>("file:/absDir/fileName#anchor"                  , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "file:/absDir/fileName"                , "file:/absDir/fileName"             , "file:/absDir/fileName#anchor"                      , "file:/absDir/fileName#anchor"                  ),
                /* 26 */arrayOf<Any?>("file:/absDir/fileName#anchor.ext"              , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "file:/absDir/fileName"                , "file:/absDir/fileName"             , "file:/absDir/fileName#anchor.ext"                  , "file:/absDir/fileName#anchor.ext"              ),
                /* 27 */arrayOf<Any?>("file:/absDir/fileName.ext#anchor"              , true       , false         , false    , false        , "#anchor"    , true        , false       , false       , "file:/absDir/fileName.ext"            , "file:/absDir/fileName"             , "file:/absDir/fileName.ext#anchor"                  , "file:/absDir/fileName#anchor"                  ),
                /* 28 */arrayOf<Any?>("file:/absDir/fileName.ext#anchor.ext"          , true       , false         , false    , false        , "#anchor.ext", true        , false       , false       , "file:/absDir/fileName.ext"            , "file:/absDir/fileName"             , "file:/absDir/fileName.ext#anchor.ext"              , "file:/absDir/fileName#anchor.ext"              ),
                /* 29 */arrayOf<Any?>("file:///absDir/fileName"                       , false      , false         , false    , false        , ""           , false       , true        , false       , "file:///absDir/fileName"              , "file:///absDir/fileName"           , "file:///absDir/fileName"                           , "file:///absDir/fileName"                       ),
                /* 30 */arrayOf<Any?>("file:///absDir/fileName#"                      , true       , false         , false    , false        , "#"          , false       , true        , false       , "file:///absDir/fileName"              , "file:///absDir/fileName"           , "file:///absDir/fileName#"                          , "file:///absDir/fileName#"                      ),
                /* 31 */arrayOf<Any?>("file:///absDir/fileName#anchor"                , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "file:///absDir/fileName"              , "file:///absDir/fileName"           , "file:///absDir/fileName#anchor"                    , "file:///absDir/fileName#anchor"                ),
                /* 32 */arrayOf<Any?>("file:///absDir/fileName#anchor.ext"            , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "file:///absDir/fileName"              , "file:///absDir/fileName"           , "file:///absDir/fileName#anchor.ext"                , "file:///absDir/fileName#anchor.ext"            ),
                /* 33 */arrayOf<Any?>("file:///absDir/fileName.ext#anchor"            , true       , false         , false    , false        , "#anchor"    , false       , true        , false       , "file:///absDir/fileName.ext"          , "file:///absDir/fileName"           , "file:///absDir/fileName.ext#anchor"                , "file:///absDir/fileName#anchor"                ),
                /* 34 */arrayOf<Any?>("file:///absDir/fileName.ext#anchor.ext"        , true       , false         , false    , false        , "#anchor.ext", false       , true        , false       , "file:///absDir/fileName.ext"          , "file:///absDir/fileName"           , "file:///absDir/fileName.ext#anchor.ext"            , "file:///absDir/fileName#anchor.ext"            ),
                /* 35 */arrayOf<Any?>("http://test.com/absDir/fileName"               , false      , false         , false    , false        , ""           , false       , true        , true        , "http://test.com/absDir/fileName"      , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName"                   , "http://test.com/absDir/fileName"               ),
                /* 36 */arrayOf<Any?>("http://test.com/absDir/fileName#"              , true       , false         , false    , false        , "#"          , false       , true        , true        , "http://test.com/absDir/fileName"      , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName#"                  , "http://test.com/absDir/fileName#"              ),
                /* 37 */arrayOf<Any?>("http://test.com/absDir/fileName#anchor"        , true       , false         , false    , false        , "#anchor"    , false       , true        , true        , "http://test.com/absDir/fileName"      , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName#anchor"            , "http://test.com/absDir/fileName#anchor"        ),
                /* 38 */arrayOf<Any?>("http://test.com/absDir/fileName#anchor.ext"    , true       , false         , false    , false        , "#anchor.ext", false       , true        , true        , "http://test.com/absDir/fileName"      , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName#anchor.ext"        , "http://test.com/absDir/fileName#anchor.ext"    ),
                /* 39 */arrayOf<Any?>("http://test.com/absDir/fileName.ext#anchor"    , true       , false         , false    , false        , "#anchor"    , false       , true        , true        , "http://test.com/absDir/fileName.ext"  , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName.ext#anchor"        , "http://test.com/absDir/fileName#anchor"        ),
                /* 40 */arrayOf<Any?>("http://test.com/absDir/fileName.ext#anchor.ext", true       , false         , false    , false        , "#anchor.ext", false       , true        , true        , "http://test.com/absDir/fileName.ext"  , "http://test.com/absDir/fileName"   , "http://test.com/absDir/fileName.ext#anchor.ext"    , "http://test.com/absDir/fileName#anchor.ext"    )
                /* @formatter:on */
            )

            if (cleanData) {
                val header = arrayOf(
                        "fullPath",
                        "hasAnchor",
                        "isSelfAnchor",
                        "isEmpty",
                        "isDoNothing",
                        "anchorText",
                        "isRelative",
                        "isAbsolute",
                        "isExternal",
                        "filePath",
                        "filePathNoExt",
                        "filePathWithAnchor",
                        "filePathNoExtWithAnchor"
                )

                printData(data, header)
            }
            return data
        }
    }
}

