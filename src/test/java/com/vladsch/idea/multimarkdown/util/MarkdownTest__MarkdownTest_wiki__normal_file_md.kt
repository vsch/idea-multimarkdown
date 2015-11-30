

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

// Auto-generated test data from: /Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md

class MarkdownTest__MarkdownTest_wiki__normal_file_md {
    companion object {
        //@Parameterized.Parameters(name = "{index}: filePath = {0}, linkRef = {3}, linkAnchor = {4}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {

            val cleanData = false
            val data = arrayListOf<Array<Any?>>(
                /* @formatter:off */
                /*      arrayOf<Any?>("fullPath"                                                     , "linkType"    , "linkText"              , "linkRef"                                    , "linkAnchor", "linkTitle", "resolvesLocal"                       , "resolvesExternal"                    , "linkAddress"                       , "multiResolve") */
                /*  0 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "Missing File"                               , null        , null       , null                                  , null                                  , null                                , arrayOf<String>()) /*  0 */,
                /*  1 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "normal file"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file"                       , arrayOf<String>()) /*  1 */,
                /*  2 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "Non Vcs Page"                               , null        , null       , "Non-Vcs-Page.md"                     , null                                  , "Non Vcs Page"                      , arrayOf<String>()) /*  2 */,
                /*  3 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "normal file"                                , "5"         , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file#5"                     , arrayOf<String>()) /*  3 */,
                /*  4 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , "normal file"           , "A Normal File"                              , null        , null       , null                                  , null                                  , null                                , arrayOf<String>()) /*  4 */,
                /*  5 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , "normal file"           , "In Name"                                    , null        , null       , "SubDirectory/In-Name.md"             , "SubDirectory/In-Name.md"             , "In Name"                           , arrayOf<String>()) /*  5 */,
                /*  6 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "normal file.md"                             , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file.md"                    , arrayOf<String>()) /*  6 */,
                /*  7 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "normal file.mkd"                            , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("normal-file.md")) /*  7 */,
                /*  8 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "normal file.markdown"                       , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("normal-file.md")) /*  8 */,
                /*  9 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "normal file.mdk"                            , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("normal-file.md")) /*  9 */,
                /* 10 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "File In Subdirectory"                       , null        , null       , "SubDirectory/File-In-Subdirectory.md", "SubDirectory/File-In-Subdirectory.md", "File In Subdirectory"              , arrayOf<String>()) /* 10 */,
                /* 11 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , "normal file"           , "Normal File Text"                           , null        , null       , null                                  , null                                  , null                                , arrayOf<String>()) /* 11 */,
                /* 12 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "anchor in name"                             , "5"         , null       , "anchor-in-name#5.md"                 , "anchor-in-name#5.md"                 , "anchor in name#5"                  , arrayOf<String>()) /* 12 */,
                /* 13 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "anchor in name"                             , "5#5"       , null       , null                                  , null                                  , null                                , arrayOf<String>()) /* 13 */,
                /* 14 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "Space In Name"                              , null        , null       , "Space In Name.md"                    , "Space In Name.md"                    , "Space In Name"                     , arrayOf<String>()) /* 14 */,
                /* 15 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "Space In Name"                              , "5"         , null       , "Space In Name.md"                    , "Space In Name.md"                    , "Space In Name#5"                   , arrayOf<String>()) /* 15 */,
                /* 16 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "Normal File"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file"                       , arrayOf<String>()) /* 16 */,
                /* 17 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "normal-file"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file"                       , arrayOf<String>()) /* 17 */,
                /* 18 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "Normal-File"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file"                       , arrayOf<String>()) /* 18 */,
                /* 19 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "In Name.md"                                 , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/In-Name.md")) /* 19 */,
                /* 20 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "SubDirectory/In Name"                       , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/In-Name.md")) /* 20 */,
                /* 21 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "SubDirectory/In Name.md"                    , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/In-Name.md")) /* 21 */,
                /* 22 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "Multiple Match"                             , null        , null       , "Multiple-Match.markdown"             , "Multiple-Match.markdown"             , "Multiple Match"                    , arrayOf<String>("Multiple-Match.markdown", "Multiple-Match.md", "Multiple-Match.mkd", "SubDirectory/Multiple-Match.md")) /* 22 */,
                /* 23 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "../../NonWikiFile"                          , null        , null       , null                                  , null                                  , null                                , arrayOf<String>()) /* 23 */,
                /* 24 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "Not Wiki Ext"                               , null        , null       , "Not-Wiki-Ext.mkd"                    , "Not-Wiki-Ext.mkd"                    , "Not Wiki Ext"                      , arrayOf<String>()) /* 24 */,
                /* 25 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , ""                      , "Not Wiki Ext 2"                             , null        , null       , "Not-Wiki-Ext-2.markdown"             , "Not-Wiki-Ext-2.markdown"             , "Not Wiki Ext 2"                    , arrayOf<String>()) /* 25 */,
                /* 26 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , "Normal File Text"      , "normal file"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file"                       , arrayOf<String>()) /* 26 */,
                /* 27 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , "normal file"           , "normal file"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file"                       , arrayOf<String>()) /* 27 */,
                /* 28 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , "Normal File"           , "normal file"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file"                       , arrayOf<String>()) /* 28 */,
                /* 29 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , "normal file"           , "Normal File"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file"                       , arrayOf<String>()) /* 29 */,
                /* 30 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "not relative link"     , "https://notchecked"                         , null        , null       , null                                  , null                                  , null                                , arrayOf<String>()) /* 30 */,
                /* 31 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "not relative link"     , "http://notchecked"                          , null        , null       , null                                  , null                                  , null                                , arrayOf<String>()) /* 31 */,
                /* 32 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "not relative link"     , "ftp://notchecked"                           , null        , null       , null                                  , null                                  , null                                , arrayOf<String>()) /* 32 */,
                /* 33 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "not relative link"     , "mailto://notchecked"                        , null        , null       , null                                  , null                                  , null                                , arrayOf<String>()) /* 33 */,
                /* 34 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Missing Link"          , "Missing-File"                               , null        , null       , null                                  , null                                  , null                                , arrayOf<String>()) /* 34 */,
                /* 35 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "normal file"           , "normal-file"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "#"                                 , arrayOf<String>()) /* 35 */,
                /* 36 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "normal file"           , "normal-file"                                , "3"         , null       , "normal-file.md"                      , "normal-file.md"                      , "#3"                                , arrayOf<String>()) /* 36 */,
                /* 37 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "normal file.md"        , "normal-file.md"                             , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "#"                                 , arrayOf<String>()) /* 37 */,
                /* 38 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Test.kt"               , "Test.kt"                                    , null        , null       , "Test.kt"                             , "Test.kt"                             , "Test.kt"                           , arrayOf<String>()) /* 38 */,
                /* 39 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Test2.kt"              , "Test2.kt"                                   , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/Test2.kt")) /* 39 */,
                /* 40 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Sub Directory Sub Test", "SubDirectory/Sub-Test.kt"                   , null        , null       , "SubDirectory/Sub-Test.kt"            , "SubDirectory/Sub-Test.kt"            , "SubDirectory/Sub-Test.kt"          , arrayOf<String>()) /* 40 */,
                /* 41 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Test.kt"               , "wiki/Test.kt"                               , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("Test.kt")) /* 41 */,
                /* 42 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Test2.kt"              , "wiki/Test2.kt"                              , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/Test2.kt")) /* 42 */,
                /* 43 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Sub Directory Sub Test", "wiki/SubDirectory/Sub-Test.kt"              , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/Sub-Test.kt")) /* 43 */,
                /* 44 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "normal file.md"        , "normal-file.md"                             , "33"        , null       , "normal-file.md"                      , "normal-file.md"                      , "#33"                               , arrayOf<String>()) /* 44 */,
                /* 45 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Normal File"           , "normal-file"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "#"                                 , arrayOf<String>()) /* 45 */,
                /* 46 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "anchor in name#5"      , "anchor-in-name"                             , "5"         , null       , null                                  , null                                  , null                                , arrayOf<String>("anchor-in-name#5.md")) /* 46 */,
                /* 47 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "anchor in name#5"      , "anchor-in-name"                             , "5.md"      , null       , null                                  , null                                  , null                                , arrayOf<String>("anchor-in-name#5.md")) /* 47 */,
                /* 48 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "anchor in name%235"    , "anchor-in-name%235"                         , null        , null       , "anchor-in-name#5.md"                 , "anchor-in-name#5.md"                 , "anchor-in-name%235"                , arrayOf<String>()) /* 48 */,
                /* 49 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "anchor in name%23#5"   , "anchor-in-name%235"                         , "5"         , null       , "anchor-in-name#5.md"                 , "anchor-in-name#5.md"                 , "anchor-in-name%235#5"              , arrayOf<String>()) /* 49 */,
                /* 50 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Normal File"           , "normal-file"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "#"                                 , arrayOf<String>()) /* 50 */,
                /* 51 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Normal File"           , "normal-file"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "#"                                 , arrayOf<String>()) /* 51 */,
                /* 52 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Not Wiki Ext"          , "Not-Wiki-Ext"                               , null        , null       , "Not-Wiki-Ext.mkd"                    , "Not-Wiki-Ext.mkd"                    , "Not-Wiki-Ext"                      , arrayOf<String>()) /* 52 */,
                /* 53 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Not Wiki Ext 2"        , "Not-Wiki-Ext-2"                             , null        , null       , "Not-Wiki-Ext-2.markdown"             , "Not-Wiki-Ext-2.markdown"             , "Not-Wiki-Ext-2"                    , arrayOf<String>()) /* 53 */,
                /* 54 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Wiki File"             , "In-Name"                                    , null        , null       , "SubDirectory/In-Name.md"             , "SubDirectory/In-Name.md"             , "In-Name"                           , arrayOf<String>()) /* 54 */,
                /* 55 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Wiki File"             , "In-Name.md"                                 , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/In-Name.md")) /* 55 */,
                /* 56 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Wiki File"             , "SubDirectory/In-Name"                       , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/In-Name.md")) /* 56 */,
                /* 57 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Wiki File"             , "SubDirectory/In-Name.md"                    , null        , null       , "SubDirectory/In-Name.md"             , "SubDirectory/In-Name.md"             , "SubDirectory/In-Name.md"           , arrayOf<String>()) /* 57 */,
                /* 58 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Wiki File"             , "SubDirectory/Multiple-Match"                , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/Multiple-Match.md")) /* 58 */,
                /* 59 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Wiki File"             , "Multiple-Match"                             , null        , null       , "Multiple-Match.markdown"             , "Multiple-Match.markdown"             , "Multiple-Match"                    , arrayOf<String>("Multiple-Match.markdown", "Multiple-Match.md", "Multiple-Match.mkd", "SubDirectory/Multiple-Match.md")) /* 59 */,
                /* 60 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "File In Subdirectory"  , "File-In-Subdirectory"                       , null        , null       , "SubDirectory/File-In-Subdirectory.md", "SubDirectory/File-In-Subdirectory.md", "File-In-Subdirectory"              , arrayOf<String>()) /* 60 */,
                /* 61 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Non Wiki File"         , "../NonWikiFile"                             , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("../NonWikiFile.md")) /* 61 */,
                /* 62 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Non Wiki File"         , "../blob/master/NonWikiFile.md"              , null        , null       , "../NonWikiFile.md"                   , "../NonWikiFile.md"                   , "../blob/master/NonWikiFile.md"     , arrayOf<String>()) /* 62 */,
                /* 63 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Non Wiki File"         , "../blob/develop/NonWikiFile.md"             , null        , null       , "../NonWikiFile.md"                   , "../NonWikiFile.md"                   , "../blob/master/NonWikiFile.md"     , arrayOf<String>()) /* 63 */,
                /* 64 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Non Wiki File"         , "../blob/master/nonwikiFile.md"              , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("../NonWikiFile.md")) /* 64 */,
                /* 65 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, "vcs image"             , "wiki/vcs-image.png"                         , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("vcs-image.png")) /* 65 */,
                /* 66 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, "sub dir vcs image"     , "wiki/SubDirectory/sub-dir-vcs-image.png"    , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/sub-dir-vcs-image.png")) /* 66 */,
                /* 67 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, "vcs image"             , "vcs-image.png"                              , null        , null       , "vcs-image.png"                       , "vcs-image.png"                       , "vcs-image.png"                     , arrayOf<String>()) /* 67 */,
                /* 68 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, "sub dir vcs image"     , "SubDirectory/sub-dir-vcs-image.png"         , null        , null       , "SubDirectory/sub-dir-vcs-image.png"  , "SubDirectory/sub-dir-vcs-image.png"  , "SubDirectory/sub-dir-vcs-image.png", arrayOf<String>()) /* 68 */,
                /* 69 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, "non vcs image"         , "wiki/non-vcs-image.png"                     , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("non-vcs-image.png")) /* 69 */,
                /* 70 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::ImageLinkRef, "sub dir non vcs image" , "wiki/SubDirectory/sub-dir-non-vcs-image.png", null        , null       , null                                  , null                                  , null                                , arrayOf<String>("SubDirectory/sub-dir-non-vcs-image.png")) /* 70 */,
                /* 71 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , "only anchor"           , ""                                           , "anchor"    , null       , null                                  , null                                  , null                                , arrayOf<String>("normal-file.md")) /* 71 */,
                /* 72 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::WikiLinkRef , "only anchor fixed"     , "normal-file"                                , "anchor"    , null       , "normal-file.md"                      , "normal-file.md"                      , "normal file#anchor"                , arrayOf<String>("normal-file.md")) /* 72 */,
                /* 73 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Space In Name"         , "Space%20In%20Name"                          , null        , null       , "Space In Name.md"                    , "Space In Name.md"                    , "Space%20In%20Name"                 , arrayOf<String>()) /* 73 */,
                /* 74 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "test.kt"               , "test.kt"                                    , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("Test.kt")) /* 74 */,
                /* 75 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Normal-File"           , "Normal-File"                                , null        , null       , "normal-file.md"                      , "normal-file.md"                      , "#"                                 , arrayOf<String>()) /* 75 */,
                /* 76 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md", ::LinkRef     , "Normal-File.md"        , "Normal-File.md"                             , null        , null       , null                                  , null                                  , null                                , arrayOf<String>("normal-file.md")) /* 76 */
                /* @formatter:on */
            )

            if (cleanData) {
                val header = arrayOf(
                        "fullPath",
                        "linkType",
                        "linkText",
                        "linkRef",
                        "linkAnchor",
                        "linkTitle",
                        "resolvesLocal",
                        "resolvesExternal",
                        "linkAddress",
                        "multiResolve"
                )

                printData(data, header)
            }
            return data
        }

        @JvmStatic
        fun mismatchInfo(): Collection<Array<Any?>> {
            val cleanData = false
            val data = arrayListOf<Array<Any?>>(
                /* @formatter:off */
                /*      arrayOf<Any?>("dataRow", "inspectionResult"             , "severity"           , "fixedLinkRef"                 , "fixedFilePath") */
                /*  0 */arrayOf<Any?>(2        , "TARGET_NOT_UNDER_VCS"         , Severity.WARNING     , null                           , null) /*  0 */,
                /*  1 */arrayOf<Any?>(6        , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "normal file"                  , null) /*  1 */,
                /*  2 */arrayOf<Any?>(7        , "LINK_TARGETS_WIKI_HAS_BAD_EXT", Severity.ERROR       , "normal file.md"               , null) /*  2 */,
                /*  3 */arrayOf<Any?>(7        , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "normal file"                  , null) /*  3 */,
                /*  4 */arrayOf<Any?>(8        , "LINK_TARGETS_WIKI_HAS_BAD_EXT", Severity.ERROR       , "normal file.md"               , null) /*  4 */,
                /*  5 */arrayOf<Any?>(8        , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "normal file"                  , null) /*  5 */,
                /*  6 */arrayOf<Any?>(12       , "TARGET_NAME_HAS_ANCHOR"       , Severity.WARNING     , null                           , "anchor-in-name5.md") /*  6 */,
                /*  7 */arrayOf<Any?>(14       , "TARGET_HAS_SPACES"            , Severity.WEAK_WARNING, null                           , "Space-In-Name.md") /*  7 */,
                /*  8 */arrayOf<Any?>(15       , "TARGET_HAS_SPACES"            , Severity.WEAK_WARNING, null                           , "Space-In-Name.md") /*  8 */,
                /*  9 */arrayOf<Any?>(16       , "CASE_MISMATCH"                , Severity.WARNING     , "normal file"                  , "Normal-File.md") /*  9 */,
                /* 10 */arrayOf<Any?>(17       , "WIKI_LINK_HAS_DASHES"         , Severity.WEAK_WARNING, "normal file"                  , null) /* 10 */,
                /* 11 */arrayOf<Any?>(18       , "CASE_MISMATCH"                , Severity.WARNING     , "normal file"                  , "Normal-File.md") /* 11 */,
                /* 12 */arrayOf<Any?>(18       , "WIKI_LINK_HAS_DASHES"         , Severity.WEAK_WARNING, "Normal File"                  , null) /* 12 */,
                /* 13 */arrayOf<Any?>(19       , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "In Name"                      , null) /* 13 */,
                /* 14 */arrayOf<Any?>(20       , "WIKI_LINK_HAS_SUBDIR"         , Severity.ERROR       , "In Name"                      , null) /* 14 */,
                /* 15 */arrayOf<Any?>(21       , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "In Name"                      , null) /* 15 */,
                /* 16 */arrayOf<Any?>(21       , "WIKI_LINK_HAS_SLASH"          , Severity.ERROR       , "SubDirectory/In Name.md"      , null) /* 16 */,
                /* 17 */arrayOf<Any?>(29       , "CASE_MISMATCH"                , Severity.WARNING     , "normal file"                  , "Normal-File.md") /* 17 */,
                /* 18 */arrayOf<Any?>(37       , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "#"                            , null) /* 18 */,
                /* 19 */arrayOf<Any?>(44       , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "#33"                          , null) /* 19 */,
                /* 20 */arrayOf<Any?>(46       , "TARGET_NAME_HAS_ANCHOR"       , Severity.WARNING     , null                           , "anchor-in-name5.md") /* 20 */,
                /* 21 */arrayOf<Any?>(47       , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "anchor-in-name%235"           , null) /* 21 */,
                /* 22 */arrayOf<Any?>(47       , "TARGET_NAME_HAS_ANCHOR"       , Severity.WARNING     , null                           , "anchor-in-name5.md") /* 22 */,
                /* 23 */arrayOf<Any?>(48       , "TARGET_NAME_HAS_ANCHOR"       , Severity.WARNING     , null                           , "anchor-in-name5.md") /* 23 */,
                /* 24 */arrayOf<Any?>(49       , "TARGET_NAME_HAS_ANCHOR"       , Severity.WARNING     , null                           , "anchor-in-name5.md") /* 24 */,
                /* 25 */arrayOf<Any?>(55       , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "In-Name"                      , null) /* 25 */,
                /* 26 */arrayOf<Any?>(57       , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "In-Name"                      , null) /* 26 */,
                /* 27 */arrayOf<Any?>(64       , "CASE_MISMATCH"                , Severity.ERROR       , "../blob/master/NonWikiFile.md", "../nonwikiFile.md") /* 27 */,
                /* 28 */arrayOf<Any?>(69       , "TARGET_NOT_UNDER_VCS"         , Severity.WARNING     , null                           , null) /* 28 */,
                /* 29 */arrayOf<Any?>(70       , "TARGET_NOT_UNDER_VCS"         , Severity.WARNING     , null                           , null) /* 29 */,
                /* 30 */arrayOf<Any?>(71       , "WIKI_LINK_HAS_ONLY_ANCHOR"    , Severity.ERROR       , "normal file#anchor"           , null) /* 30 */,
                /* 31 */arrayOf<Any?>(72       , "WIKI_LINK_HAS_DASHES"         , Severity.WEAK_WARNING, "normal file"                  , null) /* 31 */,
                /* 32 */arrayOf<Any?>(73       , "TARGET_HAS_SPACES"            , Severity.WARNING     , null                           , "Space-In-Name.md") /* 32 */,
                /* 33 */arrayOf<Any?>(74       , "CASE_MISMATCH"                , Severity.ERROR       , "Test.kt"                      , "test.kt") /* 33 */,
                /* 34 */arrayOf<Any?>(76       , "LINK_TARGETS_WIKI_HAS_EXT"    , Severity.WARNING     , "#"                            , null) /* 34 */
                /* @formatter:on */
            )

            if (cleanData) {
                val header = arrayOf(
                        "dataRow",
                        "inspectionResult",
                        "severity",
                        "fixedLinkRef",
                        "fixedFilePath"
                )

                printData(data, header)
            }
            return data
        }
    }
}
