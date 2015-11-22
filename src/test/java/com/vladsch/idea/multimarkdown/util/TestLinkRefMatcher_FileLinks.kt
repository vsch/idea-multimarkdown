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
class TestLinkRefMatcher_FileLinks constructor(val fullPath: String
                                               , val linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?) -> LinkRef
                                               , val linkText: String
                                               , val linkAddress: String
                                               , val linkAnchor: String?
                                               , val linkTitle: String?
                                               , resolvesLocalRel: String?
                                               , resolvesExternalRel: String?
                                               , val linkAddressText: String?
                                               , multiResolvePartial: Array<String>
) {

    val resolvesLocal: String?
    val resolvesExternal: String?
    val filePathInfo = FileRef(fullPath)
    val resolver = GitHubLinkResolver(null, filePathInfo, "/Users/vlad/src/MarkdownTest")
    val linkRef = LinkRef.parseLinkRef(filePathInfo, linkAddress + linkAnchor.startWith('#'), linkRefType)
    val fileList = ArrayList<FileRef>(MarkdownTestData.filePaths.size)
    val multiResolve: Array<String>
    val localLinkRef = resolvesLocalRel
    val externalLinkRef = resolvesExternalRel

    init {
        val fullPathInfo = PathInfo(fullPath)
        val filePathInfo = PathInfo(fullPathInfo.path)
        resolvesLocal = if (resolvesLocalRel == null) null else filePathInfo.append(resolvesLocalRel.splitToSequence("/")).filePath
        resolvesExternal = if (resolvesExternalRel == null) null else filePathInfo.append(resolvesExternalRel.splitToSequence("/")).filePath

        var multiResolveAbs = ArrayList<String>()

        if (multiResolvePartial.size == 0 && resolvesLocal != null) multiResolveAbs.add(resolvesLocal)

        for (path in multiResolvePartial) {
            multiResolveAbs.add(filePathInfo.append(path.splitToSequence("/")).filePath)
        }

        multiResolve = multiResolveAbs.toArray(Array(0, { "" }))

        for (path in MarkdownTestData.filePaths) {
            fileList.add(FileRef(path))
        }
    }


    @Test fun test_ResolveLocal() {
        val localRef = resolver.resolve(linkRef, LinkResolver.ONLY_LOCAL, fileList)
        assertEqualsMessage("Local does not match", resolvesLocal, localRef?.filePath)
    }

    @Test fun test_ResolveExternal() {
        val localRef = resolver.resolve(linkRef, LinkResolver.ONLY_EXTERNAL, fileList)
        assertEqualsMessage("External does not match", resolvesLocal, localRef?.filePath)
    }

    @Test fun test_LocalLinkAddress() {
        val localRef = resolver.resolve(linkRef, LinkResolver.ONLY_LOCAL, fileList) as? FileRef
        val localRefAddress = if (localRef != null) resolver.linkAddress(linkRef, localRef, linkRef.hasExt, null) else null
        assertEqualsMessage("Local link address does not match", this.linkAddressText, localRefAddress)
    }

    @Test fun test_MultiResolve() {
        val localRefs = resolver.multiResolve(linkRef, LinkResolver.ONLY_LOCAL or LinkResolver.LOOSE_MATCH, fileList)
        val actuals = Array<String>(localRefs.size, { "" })
        for (i in localRefs.indices) {
            actuals[i] = localRefs[i].filePath
        }
        compareOrderedLists("MultiResolve does not match", multiResolve, actuals)
    }

    companion object {
        @Parameterized.Parameters(name = "{index}: filePath = {0}, linkRef = {3}, linkAnchor = {4}")
        @JvmStatic
        public fun data(): Collection<Array<Any?>> {
            val genData = true
            //            val test = TestPathInfo("", "", "", "", "", "", "", false, true, false, false, false, "", "", null, arrayOf<String>())
            if (!genData) {
                //val test = TestPathInfo_WikiRepo("/home/home.wiki/file-Name", true, false, false, "/home/home.wiki", "/home", "file-Name", "home.wiki/file-Name", 1);
                //                return arrayListOf()
                /* @formatter:off */
            return arrayListOf<Array<Any?>>(
            )
                /* @formatter:on */
            } else {
                val data = arrayListOf<Array<Any?>>(
                        /* @formatter:off */
                        *markdownTestFileLinks("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"                , arrayListOf(
                        //      /*    */ arrayOf<Any?>(, "linkType", "linkText"             ,       "linkRef"                ,  "linkAnchor"          ,  "linkTitle"           ,  "resolvesLocal"                         ,      "resolvesExternal"       ,  "linkAddress"                           , "multiResolve"
                                /* 00 */arrayOf<Any?>(::FileLinkRef, ""                     , "Missing-File"                 , null                   , null                   , null                                     , null                          , null                                     , arrayOf<String>()),
                                /* 01 */arrayOf<Any?>(::FileLinkRef, ""                     , "normal-file"                  , null                   , null                   , "normal-file.md"                         , "normal-file"                 , "normal-file"                            , arrayOf<String>()),
                                /* 02 */arrayOf<Any?>(::FileLinkRef, ""                     , "Non-Vcs-Page"                 , null                   , null                   , "Non-Vcs-Page.md"                        , "Non-Vcs-Page"                , "Non-Vcs-Page"                           , arrayOf<String>()),
                                /* 03 */arrayOf<Any?>(::FileLinkRef, ""                     , "normal-file"                  , "5"                    , null                   , "normal-file.md"                         , "normal-file"                 , "normal-file"                            , arrayOf<String>()),
                                /* 04 */arrayOf<Any?>(::FileLinkRef, "normal file"          , "A-Normal-File"                , null                   , null                   , null                                     , null                          , null                                     , arrayOf<String>()),
                                /* 05 */arrayOf<Any?>(::FileLinkRef, "normal file"          , "In-Name"                      , null                   , null                   , "SubDirectory/In-Name.md"                , "In-Name"                     , "In-Name"                                , arrayOf<String>()),
                                /* 06 */arrayOf<Any?>(::FileLinkRef, ""                     , "normal-file.md"               , null                   , null                   , "normal-file.md"                         , "normal-file.md"              , "normal-file.md"                         , arrayOf<String>("normal-file.md")),
                                /* 07 */arrayOf<Any?>(::FileLinkRef, ""                     , "normal-file.mkd"              , null                   , null                   , null                                     , null                          , null                                     , arrayOf<String>("normal-file.md")),
                                /* 08 */arrayOf<Any?>(::FileLinkRef, ""                     , "normal-file.markdown"         , null                   , null                   , null                                     , null                          , null                                     , arrayOf<String>("normal-file.md")),
                                /* 09 */arrayOf<Any?>(::FileLinkRef, ""                     , "normal-file.mdk"              , null                   , null                   , null                                     , null                          , null                                     , arrayOf<String>()),
                                /* 10 */arrayOf<Any?>(::FileLinkRef, ""                     , "File-In-Subdirectory"         , null                   , null                   , "SubDirectory/File-In-Subdirectory.md"   , "File-In-Subdirectory"        , "File-In-Subdirectory"                   , arrayOf<String>()),
                                /* 11 */arrayOf<Any?>(::FileLinkRef, "Normal File Text"     , "normal-file"                  , null                   , null                   , "normal-file.md"                         , "normal-file.md"              , "normal-file"                            , arrayOf<String>("normal-file.md")),
                                /* 12 */arrayOf<Any?>(::FileLinkRef, ""                     , "anchor-in-name%235"           , null                   , null                   , "anchor-in-name#5.md"                    , "anchor-in-name%235"          , "anchor-in-name#5"                       , arrayOf<String>()),
                                /* 13 */arrayOf<Any?>(::FileLinkRef, ""                     , "anchor-in-name"               , "5"                    , null                   , "anchor-in-name#5.md"                    , "anchor-in-name%235"          , "anchor-in-name#5"                       , arrayOf<String>("anchor-in-name#5.md")),
                                /* 14 */arrayOf<Any?>(::FileLinkRef, ""                     , "Space In Name"                , null                   , null                   , "Space In Name.md"                       , "Space%20In%20Name"           , "Space%20In%20Name"                      , arrayOf<String>("Space In Name.md")),
                                /* 15 */arrayOf<Any?>(::FileLinkRef, ""                     , "Space%20In%20Name"            , null                   , null                   , "Space In Name.md"                       , "Space%20In%20Name"           , "Space%20In%20Name"                      , arrayOf<String>("Space In Name.md")),
                                /* 16 */arrayOf<Any?>(::FileLinkRef, ""                     , "Space%20In%20Name"            , "5"                    , null                   , "Space In Name.md"                       , "Space%20In%20Name"           , "Space%20In%20Name"                      , arrayOf<String>("Space In Name.md")),
                                /* 17 */arrayOf<Any?>(::FileLinkRef, ""                     , "Normal-File"                  , null                   , null                   , "normal-file.md"                         , "normal-file"                 , "normal-file"                            , arrayOf<String>()),
                                /* 18 */arrayOf<Any?>(::FileLinkRef, ""                     , "normal-file"                  , null                   , null                   , "normal-file.md"                         , "normal-file"                 , "normal-file"                            , arrayOf<String>()),
                                /* 19 */arrayOf<Any?>(::FileLinkRef, ""                     , "Normal-File"                  , null                   , null                   , "normal-file.md"                         , "normal-file"                 , "normal-file"                            , arrayOf<String>()),
                                /* 20 */arrayOf<Any?>(::FileLinkRef, ""                     , "Multiple-Match"               , null                   , null                   , "Multiple-Match.markdown"                , "Multiple-Match"              , "Multiple-Match"                         , arrayOf<String>("Multiple-Match.markdown","Multiple-Match.md","Multiple-Match.mkd","SubDirectory/Multiple-Match.md")),
                                /* 21 */arrayOf<Any?>(::FileLinkRef, ""                     , "NonWikiFile"                  , null                   , null                   , null                                     , null                          , null                                     , arrayOf<String>()),
                                /* 22 */arrayOf<Any?>(::FileLinkRef, ""                     , "Not-Wiki-Ext"                 , null                   , null                   , "Not-Wiki-Ext.mkd"                       , "Not-Wiki-Ext"                , "Not-Wiki-Ext"                           , arrayOf<String>()),
                                /* 23 */arrayOf<Any?>(::FileLinkRef, ""                     , "Not-Wiki-Ext-2"               , null                   , null                   , "Not-Wiki-Ext-2.markdown"                , "Not-Wiki-Ext-2"              , "Not-Wiki-Ext-2"                         , arrayOf<String>()),
                                /* 24 */arrayOf<Any?>(::FileLinkRef, "Normal File Text"     , "normal-file"                  , null                   , null                   , "normal-file.md"                         , "normal-file"                 , "normal-file"                            , arrayOf<String>()),
                                /* 25 */arrayOf<Any?>(::FileLinkRef, "normal file"          , "normal-file"                  , null                   , null                   , "normal-file.md"                         , "normal-file"                 , "normal-file"                            , arrayOf<String>()),
                                /* 26 */arrayOf<Any?>(::FileLinkRef, "Normal File"          , "normal-file"                  , null                   , null                   , "normal-file.md"                         , "normal-file"                 , "normal-file"                            , arrayOf<String>()),

                                // TODO: add all the explicit links from the MarkdownTest/wiki/Home.md page
                                /* 27 */arrayOf<Any?>(::FileLinkRef, "Normal File"          , "normal-file"                  , null                   , null                   , "normal-file.md"                         , "normal-file"                 , "normal-file"                            , arrayOf<String>())
                        ))
                        /* @formatter:on */
                )

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
                return data
            }
        }

        fun markdownTestFileLinks(fullPath: String, linkData: ArrayList<Array<Any?>>): Array<Array<Any?>> {
            // generate entries for links
            val fileData: ArrayList<Array<Any?>> = ArrayList(linkData.size)
            for (link in linkData) {
                fileData.add(arrayOf<Any?>(fullPath, *link))
            }
            return fileData.toArray(Array(0, { size -> Array<Any?>(size, { null }) }))
        }
    }
}

