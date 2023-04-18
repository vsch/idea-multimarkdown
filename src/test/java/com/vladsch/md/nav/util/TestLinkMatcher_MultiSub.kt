/*
 * Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

import com.vladsch.md.nav.testUtil.TestCaseUtils.compareOrderedLists
import com.vladsch.md.nav.vcs.GitHubLinkMatcher
import org.junit.Test
import java.util.*

class TestLinkMatcher_MultiSub {
    val projectResolver = MarkdownTestData

    private val exclusionMap: Map<String, String>? = null

    @Test
    fun test_linkRefMatcher_SubDirMultiWiki() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md")
        val linkRef = LinkRef(linkInfo, linkInfo.fileNameNoExt, null, null, false)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(true)
        val matchText = linkRefMatcher.linkLooseMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }
        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd"
        ), list)
    }

    @Test
    fun test_linkRefMatcher_SubDirMultiRepo() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/Multiple-Match.md")
        val linkRef = LinkRef(linkInfo, linkInfo.fileName, null, null, false)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(true)
        val matchText = linkRefMatcher.linkLooseMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd",
            "/Users/vlad/src/MarkdownTest/SubDirectory/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/Multiple-Match.markdown",
            "/Users/vlad/src/MarkdownTest/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/Multiple-Match.mkd"
        ), list)
    }

    @Test
    fun test_linkRefMatcher_SubDirMulti_Readme() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/single-link-test.md")
        val linkRef = LinkRef(linkInfo, "Readme.md", null, null, false)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(true)
        val matchText = linkRefMatcher.linkLooseMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/untitled/README.md",
            "/Users/vlad/src/MarkdownTest/Readme.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/tests/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/readme.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-modal/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/readme.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/docs/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-daterangepicker/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/README.md"
        ), list)
    }

    @Test
    fun test_linkRefMatcher_SubDirMulti_ReadmeNoExt() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/single-link-test.md")
        val linkRef = LinkRef(linkInfo, "Readme", null, null, false)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(true)
        val matchText = linkRefMatcher.linkLooseMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/untitled/README.md",
            "/Users/vlad/src/MarkdownTest/Readme.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/tests/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/readme.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-modal/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/readme.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/docs/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-daterangepicker/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/README.md"
        ), list)
    }

    @Test
    fun test_WikiLinkRefMatcher_SubDirMulti() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md")
        val linkRef = LinkRef.parseLinkRef(linkInfo, linkInfo.fileName, null, ::WikiLinkRef)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(true)
        val matchText = linkRefMatcher.linkLooseMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd"
        ), list)
    }

    @Test
    fun test_linkRefMatcher_SubDir() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md")
        val linkRef = LinkRef(linkInfo, "SubDirectory/NestedFile.md", null, null, false)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(true)
        val matchText = linkRefMatcher.linkLooseMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md",
            "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md#5"
        ), list)
    }

    @Test
    fun test_linkRefMatcher_SubDirMultiExact_Repo() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/Multiple-Match.mkd")
        val linkRef = LinkRef(linkInfo, linkInfo.fileName, null, null, false)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(false)
        val matchText = linkRefMatcher.linkAllMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/Multiple-Match.mkd"
        ), list)
    }

    @Test
    fun test_linkRefMatcher_SubDirMultiExact_Readme() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/single-link-test.md")
        val linkRef = LinkRef(linkInfo, "Readme.md", null, null, false)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(false)
        val matchText = linkRefMatcher.linkAllMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/Readme.md"
        ), list)
    }

    @Test
    fun test_linkRefMatcher_SubDirMultiExact_ReadmeNoExt() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/single-link-test.md")
        val linkRef = LinkRef(linkInfo, "Readme", null, null, false)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(false)
        val matchText = linkRefMatcher.linkAllMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
        ), list)
    }

    @Test
    fun test_WikiLinkRefMatcher_SubDirMultiExactNoExt() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md")
        val linkRef = LinkRef.parseLinkRef(linkInfo, linkInfo.fileNameNoExt, null, ::WikiLinkRef)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(false)
        val matchText = linkRefMatcher.linkAllMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd"
        ), list)
    }

    @Test
    fun test_WikiLinkRefMatcher_SubDirMultiExactWithExt() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md")
        val linkRef = LinkRef.parseLinkRef(linkInfo, linkInfo.fileName, null, ::WikiLinkRef)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(false)
        val matchText = linkRefMatcher.linkAllMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md"
        ), list)
    }

    @Test
    fun test_WikiLinkRefMatcher_SubDirMultiExact_Smart() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md")
        val linkRef = LinkRef.parseLinkRef(linkInfo, linkInfo.fileNameNoExt, null, ::WikiLinkRef)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(false)
        val matchText = linkRefMatcher.linkAllMatch
        var bestMatch = ""

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    if (bestMatch.isEmpty() || bestMatch > path) bestMatch = path
                }
            }
        }

        list.add(bestMatch)

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown"
        ), list)
    }

    @Test
    fun test_linkRefMatcher_SubDirExact() {
        val linkInfo = FileRef("/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md")
        val linkRef = LinkRef(linkInfo, "NestedFile.md", null, null, false)

        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef, exclusionMap)

        val list = ArrayList<String>()
        val regex = linkRefMatcher.patternRegex(false)
        val matchText = linkRefMatcher.linkAllMatch

        if (regex != null) {
            for (path in MarkdownTestData.filePaths) {
                if (path.matches(regex)) {
                    list.add(path)
                }
            }
        }

        compareOrderedLists("$matchText does not match\n${linkInfo.filePath}\n", arrayListOf<String>(
            "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md"
        ), list)
    }

    // TEST: need to test all the available regex pattern matches, raw, anchor, default ext, etc.
}

