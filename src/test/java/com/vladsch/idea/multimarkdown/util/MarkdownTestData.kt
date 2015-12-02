/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is property of the copyright holder and cannot be used without
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

import com.intellij.openapi.project.Project
import java.util.*

object MarkdownTestData : LinkResolver.ProjectResolver {

    val mainGitHubRepo = GitHubVcsRoot("https://github.com/vsch/MarkdownTest", "/Users/vlad/src/MarkdownTest")
    val wikiGitHubRepo = GitHubVcsRoot("https://github.com/vsch/MarkdownTest", "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki")

    override fun isUnderVcs(fileRef: FileRef): Boolean {
        return fileRef.filePath !in nonVcsFiles
    }

    override fun getVcsRoot(fileRef: FileRef): GitHubVcsRoot? {
        if (fileRef.filePath.suffixWith('/').startsWith(wikiGitHubRepo.basePath.suffixWith('/'))) return wikiGitHubRepo
        else if (fileRef.filePath.suffixWith('/').startsWith(mainGitHubRepo.basePath.suffixWith('/'))) return mainGitHubRepo
        else return null
    }

    override val projectBasePath: String
        get() = mainGitHubRepo.basePath

    override val project: Project?
        get() = null

    override fun vcsRepoBasePath(fileRef: FileRef): String? {
        return getVcsRoot(fileRef)?.projectBasePath
    }

    override fun vcsRootBase(fileRef: FileRef): String? {
        return getVcsRoot(fileRef)?.basePath
    }

    override fun projectFileList(fileTypes: List<String>?): List<FileRef>? {
        val matched = ArrayList<FileRef>()
        if (fileTypes != null) {
            val extSet = HashSet<String>()

            for (ext in fileTypes) {
                val cleanExt = ext.removePrefix(".")
                when (cleanExt) {
                    in PathInfo.MARKDOWN_EXTENSIONS -> extSet.addAll(PathInfo.MARKDOWN_EXTENSIONS)
                    in PathInfo.IMAGE_EXTENSIONS -> extSet.addAll(PathInfo.IMAGE_EXTENSIONS)
                    else -> extSet.add(cleanExt)
                }
            }

            for (file in fileList) {
                if (file.ext in extSet) {
                    matched.add(file)
                }
            }
        }
        return matched
    }

    val fileList: List<FileRef> by lazy {
        val fileList = ArrayList<FileRef>()
        for (path in filePaths) {
            fileList.add(FileRef(path))
        }
        fileList
    }

    val nonVcsFiles = arrayOf(
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/non-vcs-image.png",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Non-Vcs-Page.md",
            "/Users/vlad/src/MarkdownTest/SubDirectory/sub-dir-non-vcs-image.png",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-non-vcs-image.png",
            "/Users/vlad/src/MarkdownTest/SubDirectory/NonVcsNestedFile.md",
            "/Users/vlad/src/MarkdownTest/non-vcs-image.png"
    )

    val filePaths = arrayOf(
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/File-In-Subdirectory.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/In-Name.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-non-vcs-image.png",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-vcs-image.png",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/anchor-in-name#5.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/anchor-in-name.md#5",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/non-vcs-image.png",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Non-Vcs-Page.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext.mkd",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext-2.markdown",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/single-link-test.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name#6.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test.kt",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Test2.kt",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Sub-Test.kt",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test 4.2.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test-Name.kt",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test-Name.kt.md",
            "/Users/vlad/src/MarkdownTest/Test.kt",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/vcs-image.png",
            "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md",
            "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile#5.md",
            "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md#5",
            "/Users/vlad/src/MarkdownTest/SubDirectory/NonVcsNestedFile.md",
            "/Users/vlad/src/MarkdownTest/SubDirectory/Test.kt",
            "/Users/vlad/src/MarkdownTest/SubDirectory/sub-dir-non-vcs-image.png",
            "/Users/vlad/src/MarkdownTest/SubDirectory/sub-dir-vcs-image.png",
            "/Users/vlad/src/MarkdownTest/SubDirectory/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/Multiple-Match.markdown",
            "/Users/vlad/src/MarkdownTest/Multiple-Match.md",
            "/Users/vlad/src/MarkdownTest/Multiple-Match.mkd",
            "/Users/vlad/src/MarkdownTest/untitled/README.md",
            "/Users/vlad/src/MarkdownTest/untitled/untitled.iml",
            "/Users/vlad/src/MarkdownTest/anchor-in-name#5.md",
            "/Users/vlad/src/MarkdownTest/MarkdownTest.iml",
            "/Users/vlad/src/MarkdownTest/non-vcs-image.png",
            "/Users/vlad/src/MarkdownTest/NonWikiFile.md",
            "/Users/vlad/src/MarkdownTest/Readme.md",
            "/Users/vlad/src/MarkdownTest/Rendering-Sanity-Test.md",
            "/Users/vlad/src/MarkdownTest/single-link-test.md",
            "/Users/vlad/src/MarkdownTest/vcs-image.png",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/autoNumeric-2.0-BETA change log.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/ChangeLog.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/tests/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/CONTRIBUTING.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/CONTRIBUTING.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/LICENSE.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/CHANGELOG.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Extras.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/readme.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/CHANGELOG.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Installing.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Version 4 Contributors guide.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-modal/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/change log.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Functions.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/ContributorsGuide.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Options.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/CHANGELOG.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Events.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/readme.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/index.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/CONTRIBUTING.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/FAQ.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Version 4 Changelog.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/CONTRIBUTING.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/docs/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-daterangepicker/README.md",
            "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/README.md"
    )
}

fun List<PathInfo>.asFilePaths(): List<String> {
    val result = this.map { it.filePath }
    return result
}

fun List<String>.asURI(): List<String> {
    val result = this.map { "file://" + it }
    return result
}

fun List<String>.with(list: List<String>): List<String> {
    val result = ArrayList<String>()
    result.addAll(this)
    result.addAll(list)
    return result
}

fun List<String>.asRemoteImageURI(branchOrTag: String? = null): List<String> {
    return this.asRemoteUriType(branchOrTag, "raw");
}

fun List<String>.asRemoteURI(branchOrTag: String? = null): List<String> {
    return this.asRemoteUriType(branchOrTag, "blob");
}

fun List<String>.asRemoteUriType(branchOrTag: String? = null, gitHubLink:String?): List<String> {
    val _branchOrTag = branchOrTag ?: "master"
    val projectResolver: LinkResolver.ProjectResolver = MarkdownTestData
    val result = this.map {
        val fileRef = FileRef(it)

        val vcsRoot = projectResolver.getVcsRoot(fileRef)
        if (projectResolver.isUnderVcs(fileRef) && vcsRoot != null) {
            val baseUrl = vcsRoot.baseUrl
            val basePath = vcsRoot.basePath
            if (fileRef.isUnderWikiDir) {
                baseUrl.suffixWith('/') + "wiki/" + it.substring(basePath.length).replace(" ", "%20").replace("#", "%23")
            } else {
                baseUrl.suffixWith('/') + gitHubLink.ifEmpty("blob") + "/$_branchOrTag/" + it.substring(basePath.length).replace(" ", "%20").replace("#", "%23")
            }
        } else {
            "file://" + it
        }
    }
    return result
}

fun List<String>.asLocalURI(branchOrTag: String? = null): List<String> {
    return this.map { "file://" + it }
}

val EMPTY_LIST = arrayListOf<String>()

val gitHubLinks = arrayListOf(
        "https://github.com/vsch/MarkdownTest/fork",
        "https://github.com/vsch/MarkdownTest/graphs",
        "https://github.com/vsch/MarkdownTest/issues",
        "https://github.com/vsch/MarkdownTest/pulls",
        "https://github.com/vsch/MarkdownTest/pulse"
)

val wikiImageRemoteFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/vcs-image.png"
)

val wikiImageFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-non-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/non-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/vcs-image.png"
)

val imageRemoteFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/vcs-image.png",
        "/Users/vlad/src/MarkdownTest/SubDirectory/sub-dir-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/vcs-image.png"
)

val imageFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-non-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/sub-dir-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/non-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/vcs-image.png",
        "/Users/vlad/src/MarkdownTest/SubDirectory/sub-dir-non-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/SubDirectory/sub-dir-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/non-vcs-image.png",
        "/Users/vlad/src/MarkdownTest/vcs-image.png"
)

val wikiKotlinFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Sub-Test.kt",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Test2.kt",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test-Name.kt",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test.kt"
)

val kotlinFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Sub-Test.kt",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Test2.kt",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test-Name.kt",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test.kt",
        "/Users/vlad/src/MarkdownTest/SubDirectory/Test.kt",
        "/Users/vlad/src/MarkdownTest/Test.kt"
)

val wikiMarkdownRemoteFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext-2.markdown",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext.mkd",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name#6.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/File-In-Subdirectory.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/In-Name.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test 4.2.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test-Name.kt.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/anchor-in-name#5.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/single-link-test.md"
)

val wikiMarkdownFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Non-Vcs-Page.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext-2.markdown",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext.mkd",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name#6.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/File-In-Subdirectory.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/In-Name.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test 4.2.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test-Name.kt.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/anchor-in-name#5.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/single-link-test.md"
)

val markdownRemoteFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/autoNumeric-2.0-BETA change log.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/readme.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/change log.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/readme.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/CHANGELOG.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/CONTRIBUTING.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/docs/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/tests/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-daterangepicker/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-modal/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/CHANGELOG.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/CONTRIBUTING.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/CHANGELOG.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/CONTRIBUTING.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/LICENSE.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/CONTRIBUTING.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/ChangeLog.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/ContributorsGuide.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Events.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Extras.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/FAQ.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Functions.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Installing.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Options.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Version 4 Changelog.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Version 4 Contributors guide.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/index.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext-2.markdown",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext.mkd",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name#6.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/File-In-Subdirectory.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/In-Name.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test 4.2.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test-Name.kt.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/anchor-in-name#5.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/single-link-test.md",
        "/Users/vlad/src/MarkdownTest/Multiple-Match.markdown",
        "/Users/vlad/src/MarkdownTest/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/Multiple-Match.mkd",
        "/Users/vlad/src/MarkdownTest/NonWikiFile.md",
        "/Users/vlad/src/MarkdownTest/Readme.md",
        "/Users/vlad/src/MarkdownTest/Rendering-Sanity-Test.md",
        "/Users/vlad/src/MarkdownTest/SubDirectory/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile#5.md",
        "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md",
        "/Users/vlad/src/MarkdownTest/anchor-in-name#5.md",
        "/Users/vlad/src/MarkdownTest/single-link-test.md",
        "/Users/vlad/src/MarkdownTest/untitled/README.md"
)

val markdownFiles = arrayListOf<String>(
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/autoNumeric-2.0-BETA change log.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/autoNumeric-2.0/readme.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/change log.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/autoNumeric/readme.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/CHANGELOG.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/CONTRIBUTING.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/docs/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-datepicker/tests/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-daterangepicker/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-modal/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/CHANGELOG.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/CONTRIBUTING.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap-sass/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/bootstrap/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/CHANGELOG.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/CONTRIBUTING.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/LICENSE.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/cropper/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/CONTRIBUTING.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/README.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/ChangeLog.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/ContributorsGuide.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Events.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Extras.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/FAQ.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Functions.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Installing.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Options.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Version 4 Changelog.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/Version 4 Contributors guide.md",
        "/Users/vlad/src/MarkdownTest/GitHubIssues/Issue-46/webbeheer_package/bower_components/eonasdan-bootstrap-datetimepicker/docs/index.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Home.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.markdown",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Multiple-Match.mkd",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Non-Vcs-Page.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext-2.markdown",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Not-Wiki-Ext.mkd",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name#6.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Space In Name.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/File-In-Subdirectory.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/In-Name.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/SubDirectory/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test 4.2.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/Test-Name.kt.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/anchor-in-name#5.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/normal-file.md",
        "/Users/vlad/src/MarkdownTest/MarkdownTest.wiki/single-link-test.md",
        "/Users/vlad/src/MarkdownTest/Multiple-Match.markdown",
        "/Users/vlad/src/MarkdownTest/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/Multiple-Match.mkd",
        "/Users/vlad/src/MarkdownTest/NonWikiFile.md",
        "/Users/vlad/src/MarkdownTest/Readme.md",
        "/Users/vlad/src/MarkdownTest/Rendering-Sanity-Test.md",
        "/Users/vlad/src/MarkdownTest/SubDirectory/Multiple-Match.md",
        "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile#5.md",
        "/Users/vlad/src/MarkdownTest/SubDirectory/NestedFile.md",
        "/Users/vlad/src/MarkdownTest/SubDirectory/NonVcsNestedFile.md",
        "/Users/vlad/src/MarkdownTest/anchor-in-name#5.md",
        "/Users/vlad/src/MarkdownTest/single-link-test.md",
        "/Users/vlad/src/MarkdownTest/untitled/README.md"
)


