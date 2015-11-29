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

import java.util.*
import kotlin.text.Regex
import kotlin.text.RegexOption

class GitHubLinkMatcher(val projectResolver: LinkResolver.ProjectResolver, val linkRef: LinkRef) {
    val subDirPattern = "(?:.+/)?"
    var fixedPrefix = ""
        private set

    var linkAllMatch: String? = null
        private set
    var linkLooseMatch: String? = null
        private set
    var linkSubAnchorExtMatch: String? = null
        private set
    var linkSubExtMatch: String? = null
        private set
    var linkFileAnchorMatch: String? = null
        private set
    var linkFileMatch: String? = null
        private set

    // if in the main repo then this will be set to the branch or tag from the link
    var branchOrTag: String? = null
        private set

    // set to true if it is a non-file link
    var gitHubLinks = false
        private set

    // this will be one of the links including file links: raw, blob, wiki
    var gitHubLink: String? = null
        private set

    // this will be the above github link but with whatever followed the link in the linkRef
    var gitHubLinkWithParams: String? = null
        private set

    // set if wiki rules are being matched, ie. match not case sensitive
    // applies only to exact match, loose and completion matches should always be not case sensitive
    var wikiMatchingRules = false
        private set

    val isValid by lazy { computeMatchText() }

    private fun matchExt(ext: String?): String {
        return if (ext != null && !ext.isEmpty()) linkTextToFileMatch(ext.prefixWith('.')) else ""
    }

    protected fun defaultExtensionPattern(vararg additionalExtensions: String, isOptional: Boolean): String {
        val hashSet = HashSet<String>()

        hashSet.addAll(additionalExtensions)
        hashSet.addAll(linkRef.linkExtensions)
        var extensionPattern = "(?:" + hashSet.reduce(splicer("|", { matchExt(it) })) + ")" + if (isOptional) "?" else ""
        return extensionPattern
    }

    fun linkTextToFileMatch(pathText: String?, isOptional: Boolean = false, emptyMatchesAll: Boolean = false): String {
        if (pathText == null || pathText.isEmpty()) return if (emptyMatchesAll) "(?:.*)" else ""
        return linkRef.linkToFileRegex(pathText.orEmpty()) + if (isOptional) "?" else ""
    }

    fun fileMatchPattern(pathText: String?, isOptional: Boolean = false, emptyMatchesAll: Boolean = false): String {
        if (pathText == null || pathText.isEmpty()) return if (emptyMatchesAll) "(?:.*)" else ""
        return "(?:\\Q" + pathText + "\\E)" + if (isOptional) "?" else ""
    }

    fun patternRegex(looseMatch: Boolean): Regex? {
        if (!isValid) return null
        return (if (looseMatch) linkLooseMatch else linkAllMatch)?.toRegex(RegexOption.IGNORE_CASE)
    }

    protected fun computeMatchText(): Boolean {
        // return a regex that will match most loosely a file path to be used by this link
        val vcsRepoBasePath = PathInfo.cleanFullPath(with(projectResolver.vcsRepoBasePath(linkRef.containingFile)) {
            if (this == null || this.isEmpty()) projectResolver.projectBasePath else this
        }).suffixWith('/')

        assert(!vcsRepoBasePath.isEmpty(), { "vcsRepoBasePath cannot be empty" })

        val useLooseMatch = true

        if (linkRef is WikiLinkRef) {
            // spaces match - and spaces, all subdirectories under Wiki Home match, only WIKI targets accepted, no case sensitivity
            fixedPrefix = linkRef.containingFile.wikiDir.suffixWith('/')

            if (useLooseMatch) {
                val looseAnchorPattern = linkTextToFileMatch(PathInfo(linkRef.anchorText).filePathNoExt, isOptional = true)
                val looseFilenamePattern = linkTextToFileMatch(linkRef.fileNameNoExt.trim())
                val looseExtensionPattern = defaultExtensionPattern(linkRef.ext, PathInfo(linkRef.anchorText).ext, isOptional = true)
                linkLooseMatch = "^$fixedPrefix$subDirPattern$looseFilenamePattern$looseAnchorPattern$looseExtensionPattern$"
            }
            val anchorPattern = linkTextToFileMatch(linkRef.anchorText, isOptional = false)
            val extensionPattern = defaultExtensionPattern(isOptional = false)
            val filenamePattern = linkTextToFileMatch(linkRef.filePath.trim())

            linkFileMatch = "^$fixedPrefix$filenamePattern$"
            linkFileAnchorMatch = "^$fixedPrefix$filenamePattern$anchorPattern$"
            linkSubExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$extensionPattern$"
            linkSubAnchorExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
            linkAllMatch = "^$fixedPrefix(?:$filenamePattern${anchorPattern.suffixWith('?')}|$subDirPattern$filenamePattern${anchorPattern.suffixWith('?')}$extensionPattern)$"
            wikiMatchingRules = true
        } else {
            // it is assumed that if there is a wiki directory then it is a sub dir of the vcsRepoBasePath with the same name and .wiki appended
            // so if we encounter vcsRepoBasePath/wiki will will change it to vcsRepoBasePath/projectBasePathName.wiki
            // going below the vcsRepoBasePath is not supported for now.

            // if the page is a wiki home page then it will be treated as if it is located in the vcsRepoBasePath so that its relative links resolve correctly
            // however, this must be done for image links but is optional for non-image explicit links which resolve if the page was under the wiki directory
            // if it is a wiki but not the main page or not image link then its prefix is not changed
            var prefixPath: String
            var repoLink: String
            var projectDirName = PathInfo(vcsRepoBasePath).fileName

            // here we resolve the start of the relative path
            val containingFilePrefixPath =
                    if (linkRef.containingFile.isWikiPage) {
                        if (linkRef.containingFile.isWikiHomePage) vcsRepoBasePath
                        else vcsRepoBasePath + "wiki/"
                    } else {
                        vcsRepoBasePath + "blob/master/"
                    }

            // now append the link's path to see where we wind up
            prefixPath = PathInfo.appendParts(containingFilePrefixPath, linkRef.linkToFile(linkRef.path)).filePath.suffixWith('/')

            // we now see if we are still in the repo, if not then no match
            if (!prefixPath.startsWith(vcsRepoBasePath)) return false

            if (prefixPath.equals(vcsRepoBasePath) || prefixPath.equals(vcsRepoBasePath.removeSuffix("/"))) {
                // here we should match the README markdown
                wikiMatchingRules = true
                prefixPath = vcsRepoBasePath
                // this link should always resolve even when the readme file is not present, we should treat it as an empty gitHubLink
                gitHubLink = ""
                gitHubLinks = true
            } else {
                prefixPath = prefixPath.substring(vcsRepoBasePath.length).removeSuffix("/")
                val pathParts = prefixPath.split('/', limit = 3).iterator()

                // we take the first subdirectory after the prefix
                val firstSub = pathParts.next()
                val linkQuery = firstSub.split('?', limit = 2).iterator()
                val gitHubLink = linkQuery.next()
                var linkParams = if (linkQuery.hasNext()) "?" + linkQuery.next() else ""

                // now we see where we are
                when (gitHubLink) {
                    "wiki" -> {
                        if (linkParams.isEmpty() && !pathParts.hasNext()) {
                            // just a link to wiki, match Home page
                            linkParams = "Home" + linkParams
                            prefixPath = vcsRepoBasePath
                        } else {
                            prefixPath = "$vcsRepoBasePath$projectDirName${PathInfo.WIKI_HOME_DIR_EXTENSION}/"
                        }

                        this.gitHubLink = gitHubLink
                        wikiMatchingRules = true
                    }
                    "blob", "raw" -> {
                        if (!pathParts.hasNext()) return@computeMatchText false;

                        branchOrTag = pathParts.next()
                        prefixPath = vcsRepoBasePath
                        this.gitHubLink = gitHubLink
                    }
                    in GitHubLinkResolver.GITHUB_LINKS -> {
                        prefixPath = "$vcsRepoBasePath$gitHubLink/"
                        gitHubLinks = true
                        this.gitHubLink = gitHubLink
                    }
                    else -> return false
                }

                repoLink = linkParams + pathParts.splice("/", skipEmpty = false)
                gitHubLinkWithParams = this.gitHubLink + (if (!repoLink.startsWith("?", "/")) "/" else "") + repoLink
            }

            fixedPrefix = prefixPath

            // no need for pattern match, we have the stuff
            if (gitHubLinks) return true

            if (wikiMatchingRules) {
                // same as wiki link but without the wiki to file name conversion and the anchor matching, anchor needs to be escaped
                // we do anchor matching for loose match identical to wiki links but without the wiki to file conversion
                val extensionPattern = defaultExtensionPattern(isOptional = false)
                val filenamePattern = linkTextToFileMatch(linkRef.filePath)

                if (useLooseMatch) {
                    val looseAnchorPattern = linkTextToFileMatch(PathInfo(linkRef.anchorText).filePathNoExt, isOptional = true)
                    val looseFilenamePattern = linkTextToFileMatch(linkRef.fileNameNoExt.trim())
                    val looseExtensionPattern = defaultExtensionPattern(linkRef.ext, PathInfo(linkRef.anchorText).ext, isOptional = true)
                    linkLooseMatch = "^$fixedPrefix$subDirPattern$looseFilenamePattern$looseAnchorPattern${looseExtensionPattern.suffixWith('?')}$"

                    val anchorPattern = linkTextToFileMatch(linkRef.anchorText, isOptional = false)
                    linkFileAnchorMatch = "^$fixedPrefix$filenamePattern$anchorPattern$"
                    linkSubAnchorExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
                }

                linkFileMatch = "^$fixedPrefix$filenamePattern$"
                linkSubExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$extensionPattern$"
                linkAllMatch = "^$fixedPrefix(?:$filenamePattern|$subDirPattern$filenamePattern$extensionPattern)$"
            } else {
                // regular repo match, we build up all options for looseMatch and later resolution as to what we really matched
                val extensionPattern = defaultExtensionPattern(isOptional = false)
                val filenamePattern = linkTextToFileMatch(linkRef.filePath)

                if (useLooseMatch) {
                    val looseAnchorPattern = linkTextToFileMatch(PathInfo(linkRef.anchorText).filePathNoExt, isOptional = true)
                    val looseFilenamePattern = linkTextToFileMatch(linkRef.fileNameNoExt.trim())
                    val looseExtensionPattern = defaultExtensionPattern(linkRef.ext, PathInfo(linkRef.anchorText).ext, isOptional = true)
                    linkLooseMatch = "^$fixedPrefix$subDirPattern$looseFilenamePattern$looseAnchorPattern${looseExtensionPattern.suffixWith('?')}$"

                    val anchorPattern = linkTextToFileMatch(linkRef.anchorText, isOptional = false)
                    linkFileAnchorMatch = "^$fixedPrefix$filenamePattern$anchorPattern$"
                    linkSubAnchorExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
                    linkSubExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$extensionPattern$"
                }

                // only exact matches for files, the rest are loose matches
                linkFileMatch = "^$fixedPrefix$filenamePattern$"
                linkAllMatch = linkFileMatch
            }
        }

        return true
    }
}
