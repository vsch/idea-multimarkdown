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
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*
import kotlin.test.assertEquals

import com.vladsch.idea.multimarkdown.TestUtils.*

@RunWith(value = Parameterized::class)
class TestLinkRefMatcher_Basic constructor(val fullPath: String) {

    @Test fun test_linkRefMatcher() {
        val linkInfo = FileRef(fullPath)

        val linkRef = FileLinkRef(linkInfo, linkInfo.fileName, "5")

        val linkRefMatcher = LinkRefMatcher(linkRef, linkInfo.path, false)
        val matchText = linkRefMatcher.patternText()
        val pattern = linkRefMatcher.pattern()
        val matcher = pattern.matcher(linkInfo.filePath)

        assertEqualsMessage("$matchText does not match\n$fullPath\n", true as Object, matcher.matches())
    }

    @Test fun test_WikiLinkRefMatcher() {
        val linkInfo = FileRef(fullPath)

        val linkRef = LinkRef.parseLinkRef(linkInfo, linkInfo.fileName, ::WikiLinkRef)

        val linkRefMatcher = LinkRefMatcher(linkRef, linkInfo.path, false)
        val matchText = linkRefMatcher.patternText()
        val pattern = linkRefMatcher.pattern()
        val matcher = pattern.matcher(linkInfo.filePath)

        assertEqualsMessage("$matchText does not match\n$fullPath\n", linkInfo.isWikiPageExt, matcher.matches())
    }

    companion object {
        @Parameterized.Parameters(name = "{index}: filePath = {0}")
        @JvmStatic
        public fun data(): Collection<Array<Any?>> {
            val genData = false
            //            val test = TestPathInfo("", "", "", "", "", "", "", false, true, false, false, false, "", "", null, arrayOf<String>())
            if (!genData) {
                //val test = TestPathInfo_WikiRepo("/home/home.wiki/file-Name", true, false, false, "/home/home.wiki", "/home", "file-Name", "home.wiki/file-Name", 1);
                //                return arrayListOf()
                /* @formatter:off */
            return arrayListOf<Array<Any?>>(
                /*      arrayOf<Any?>("fullPath"                                                                                                                                                    ) */
                /*  0 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/File-In-Subdirectory.md"                                                                         ),
                /*  1 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/In-Name.md"                                                                                      ),
                /*  2 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md"                                                                               ),
                /*  3 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-non-vcs-image.png"                                                                       ),
                /*  4 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-vcs-image.png"                                                                           ),
                /*  5 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/anchor-in-name#5.md"                                                                                          ),
                /*  6 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/anchor-in-name.md#5"                                                                                          ),
                /*  7 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md"                                                                                                      ),
                /*  8 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown"                                                                                      ),
                /*  9 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md"                                                                                            ),
                /* 10 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd"                                                                                           ),
                /* 11 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/non-vcs-image.png"                                                                                            ),
                /* 12 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Non-Vcs-Page.md"                                                                                              ),
                /* 13 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md"                                                                                               ),
                /* 14 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext.mkd"                                                                                             ),
                /* 15 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext-2.markdown"                                                                                      ),
                /* 16 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/single-link-test.md"                                                                                          ),
                /* 17 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name.md"                                                                                             ),
                /* 18 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/vcs-image.png"                                                                                                ),
                /* 19 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/vcs-image.png"                                                                                                ),
                /* 20 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md"                                                                                                     ),
                /* 21 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile#5.md"                                                                                                   ),
                /* 22 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md#5"                                                                                                   ),
                /* 23 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/NonVcsNestedFile.md"                                                                                               ),
                /* 24 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/sub-dir-non-vcs-image.png"                                                                                         ),
                /* 25 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/sub-dir-vcs-image.png"                                                                                             ),
                /* 26 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/SubDirectory/Multiple-Match.md"                                                                                                 ),
                /* 27 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Multiple-Match.markdown"                                                                                                        ),
                /* 28 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Multiple-Match.md"                                                                                                              ),
                /* 29 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Multiple-Match.mkd"                                                                                                             ),
                /* 30 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/untitled/README.md"                                                                                                             ),
                /* 31 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/untitled/untitled.iml"                                                                                                          ),
                /* 32 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/anchor-in-name#5.md"                                                                                                            ),
                /* 33 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/MarkdownTest.iml"                                                                                                               ),
                /* 34 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/non-vcs-image.png"                                                                                                              ),
                /* 35 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/NonWikiFile.md"                                                                                                                 ),
                /* 36 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Readme.md"                                                                                                                      ),
                /* 37 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/Rendering-Sanity-Test.md"                                                                                                       ),
                /* 38 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/single-link-test.md"                                                                                                            ),
                /* 39 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/vcs-image.png"                                                                                                                  ),
                /* 40 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/autoNumeric-2.0-BETA change log.md"        ),
                /* 41 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/ChangeLog.md"                   ),
                /* 42 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/README.md"                                        ),
                /* 43 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/tests/README.md"                                  ),
                /* 44 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/CONTRIBUTING.md"                                        ),
                /* 45 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/README.md"                                                     ),
                /* 46 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/CONTRIBUTING.md"                     ),
                /* 47 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/LICENSE.md"                                                    ),
                /* 48 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/CHANGELOG.md"                                                  ),
                /* 49 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Extras.md"                      ),
                /* 50 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/README.md"                           ),
                /* 51 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/readme.md"                                                 ),
                /* 52 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/CHANGELOG.md"                                     ),
                /* 53 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Installing.md"                  ),
                /* 54 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Version 4 Contributors guide.md"),
                /* 55 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-modal/README.md"                                             ),
                /* 56 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/change log.md"                                             ),
                /* 57 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Functions.md"                   ),
                /* 58 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/ContributorsGuide.md"           ),
                /* 59 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Options.md"                     ),
                /* 60 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/CHANGELOG.md"                                           ),
                /* 61 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Events.md"                      ),
                /* 62 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/readme.md"                                 ),
                /* 63 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/index.md"                       ),
                /* 64 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/CONTRIBUTING.md"                                  ),
                /* 65 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap/README.md"                                                   ),
                /* 66 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/FAQ.md"                         ),
                /* 67 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Version 4 Changelog.md"         ),
                /* 68 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/CONTRIBUTING.md"                                               ),
                /* 69 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/docs/README.md"                                   ),
                /* 70 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-daterangepicker/README.md"                                   ),
                /* 71 */arrayOf<Any?>("/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/README.md"                                              )
            )
                /* @formatter:on */
            } else {
                val data = ArrayList<Array<Any?>>()
                for (elem in MarkdownTestData.filePaths.toArrayList()) {
                    data.add(arrayOf(elem))
                }

                val header = arrayOf(
                        "fullPath"
                )

                printData(data, header)
                return data
            }
        }

        // fullFilePath
        // hasAnchor
        // isSelfAnchor
        // isEmpty
        // isDoNothing
        // anchorText

        fun pathInfoTestData(path: String): Array<Any?> {
            val pathInfo: FilePathInfo = FilePathInfo(FilePathInfo.removeEnd(path, "."))

            return arrayOf<Any?>(
                    pathInfo.fullFilePath,
                    pathInfo.hasAnchor(),
                    pathInfo.filePath.isEmpty() && pathInfo.hasAnchor(),
                    pathInfo.isEmpty,
                    pathInfo.fullFilePath.equals("#"),
                    if (pathInfo.hasAnchor()) pathInfo.anchor else LinkInfo.EMPTY_STRING,
                    pathInfo.isRelative,
                    pathInfo.isAbsoluteReference,
                    pathInfo.isExternalReference,
                    pathInfo.filePath,
                    pathInfo.filePathNoExt,
                    pathInfo.filePathWithAnchor,
                    pathInfo.filePathWithAnchorNoExt
            );
        }
    }
}

