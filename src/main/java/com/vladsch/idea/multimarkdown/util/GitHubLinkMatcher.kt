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

    // extension set for all file matches
    var linkAllMatchExtensions: List<String>? = null
        private set

    // extension set for loose file matches
    var linkLooseMatchExtensions: List<String>? = null
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

    // effective file extension: either file ext or if the filename starts with . and has no ext then the file name
    var effectiveExt: String? = null
        private set

    val isOnlyLooseMatchValid by lazy {
        computeMatchText()
    }

    private fun matchExt(ext: String?): String {
        return if (ext != null && !ext.isEmpty()) linkTextToFileMatch(ext.prefixWith('.')) else ""
    }

    protected fun extensionPattern(vararg extensions: String, isOptional: Boolean): String {
        val hashSet = HashSet<String>()

        hashSet.addAll(extensions.filter { !it.isEmpty() }.map { matchExt(it) })
        hashSet.addAll(linkRef.linkExtensions.map { matchExt(it) })
        var extensionPattern = "(?:" + hashSet.reduce(skipEmptySplicer("|")) + ")" + if (isOptional) "?" else ""
        return extensionPattern
    }

    fun linkTextToFileMatch(pathText: String?, isOptional: Boolean = false, emptyMatchesAll: Boolean = false): String {
        if (pathText == null || pathText.isEmpty()) return if (emptyMatchesAll) "(?:.*)" else ""
        return linkRef.linkToFileRegex(linkRef.linkToFile(pathText.orEmpty())) + if (isOptional) "?" else ""
    }

    fun quoteMatchPattern(pathText: String?, isOptional: Boolean = false, emptyMatchesAll: Boolean = false): String {
        if (pathText == null || pathText.isEmpty()) return if (emptyMatchesAll) "(?:.*)" else ""
        return "(?:\\Q" + pathText + "\\E)" + if (isOptional) "?" else ""
    }

    fun patternRegex(looseMatch: Boolean): Regex? {
        if (!isOnlyLooseMatchValid && !looseMatch) return null
        return (if (looseMatch) linkLooseMatch else linkAllMatch)?.toRegex(RegexOption.IGNORE_CASE)
    }

    protected fun computeMatchText(): Boolean {
        // return a regex that will match most loosely a file path to be used by this link
        val vcsRepoBasePath = PathInfo.cleanFullPath(with(projectResolver.vcsRepoBasePath(linkRef.containingFile)) {
            if (this == null || this.isEmpty()) projectResolver.projectBasePath else this
        }).suffixWith('/')

        assert(!vcsRepoBasePath.isEmpty(), { "vcsRepoBasePath cannot be empty" })

        // empty path or file name is an extension: .something, so cannot link to . files
        val useLooseMatch = true
        val subDirPattern = "(?:.+/)?"
        var looseMatchOnly = false
        val originalLinkRef = linkRef

        if (linkRef is WikiLinkRef) {
            // spaces match - and spaces, all subdirectories under Wiki Home match, only WIKI targets accepted, no case sensitivity
            val linkRef = linkRef.replaceFilePath(linkRef.filePath.trim())

            fixedPrefix = linkRef.containingFile.wikiDir.suffixWith('/')
            val wantCompletionMatch = linkRef.filePath.isEmpty() || linkRef.fileNameNoExt.startsWith('.') && !linkRef.hasExt
            val filePath = if (wantCompletionMatch) "" else linkRef.filePath
            val filePathNoExt = if (wantCompletionMatch) "" else linkRef.filePathNoExt
            val fileName = if (wantCompletionMatch) "" else linkRef.fileName
            val fileNameNoExt = if (wantCompletionMatch) "" else linkRef.fileNameNoExt
            val ext = if (linkRef.fileName.startsWith('.') && !linkRef.hasExt) linkRef.fileName else linkRef.ext
            val linkExtensions = if (linkRef.fileName.startsWith('.') && !linkRef.hasExt) arrayOf() else linkRef.linkExtensions
            val emptyMatchesAll = filePath.isEmpty() || wantCompletionMatch
            effectiveExt = ext

            val pureAnchor = linkRef.filePath.isEmpty() && linkRef.anchor != null
            if (useLooseMatch) {
                val looseAnchorPattern = linkTextToFileMatch(PathInfo(linkRef.anchorText).filePathNoExt, isOptional = true)
                val looseFilenamePattern = linkTextToFileMatch(if (pureAnchor) linkRef.containingFile.fileNameNoExt else fileNameNoExt, isOptional = false, emptyMatchesAll = emptyMatchesAll)
                val looseExtensionPattern = (extensionPattern(ext, isOptional = true) + extensionPattern(PathInfo(linkRef.anchorText).ext, *linkExtensions, isOptional = true)).regexGroup()
                linkLooseMatch = "^$fixedPrefix$subDirPattern$looseFilenamePattern$looseAnchorPattern$looseExtensionPattern$"
                linkLooseMatchExtensions = arrayOf(ext, PathInfo(linkRef.anchorText).ext, *linkExtensions).filter { !it.isEmpty() }
            }

            looseMatchOnly = pureAnchor || !linkRef.containingFile.isWikiPage

            val anchorPattern = linkTextToFileMatch(linkRef.anchorText, isOptional = false)
            // if it has extension then we inlude it as alternative before default extensions because it may not be an extension but part of the file name
            val defaultExtensions = extensionPattern(*linkExtensions, isOptional = false)
            val extensionPattern = if (!ext.isEmpty()) (extensionPattern(ext, isOptional = false) + "|" + extensionPattern(ext, isOptional = false) + defaultExtensions).regexGroup() else defaultExtensions
            val filenamePattern = linkTextToFileMatch(filePath, isOptional = false, emptyMatchesAll = emptyMatchesAll)
            linkAllMatchExtensions = arrayOf(ext, *linkExtensions).filter { !it.isEmpty() }

            linkFileMatch = "^$fixedPrefix$filenamePattern$"
            if (!anchorPattern.isEmpty()) linkFileAnchorMatch = "^$fixedPrefix$filenamePattern$anchorPattern$"
            linkSubExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$extensionPattern$"
            if (!anchorPattern.isEmpty()) linkSubAnchorExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
            linkAllMatch = "^$fixedPrefix(?:$filenamePattern${anchorPattern.suffixWith('?')}|$subDirPattern$filenamePattern${anchorPattern.suffixWith('?')}$extensionPattern)$"
            wikiMatchingRules = true
        } else {
            // it is assumed that if there is a wiki directory then it is a sub dir of the vcsRepoBasePath with the same name and .wiki appended
            // so if we encounter vcsRepoBasePath/wiki will will change it to vcsRepoBasePath/projectBasePathName.wiki
            // going below the vcsRepoBasePath is not supported for now.

            // if the page is a wiki home page then it will be treated as if it is located in the vcsRepoBasePath so that its relative links resolve correctly
            // however, this must be done for image links but is optional for non-image explicit links which resolve if the page was under the wiki directory
            // if it is a wiki but not the main page or not image link then its prefix is not changed
            var repoLink: String = linkRef.filePath
            val projectDirName = PathInfo(vcsRepoBasePath).fileName
            val noWikiSubDirPattern = "(?!\\Q$projectDirName.wiki\\E)"
            var wikiPrefixIsOptional: Boolean? = null
            var linkRef = linkRef

            val originalWantCompletionMatch = linkRef.anchor == null && (linkRef.filePath.isEmpty() || linkRef.fileNameNoExt.startsWith('.') && !linkRef.hasExt)

            if (originalWantCompletionMatch) {
                fixedPrefix = vcsRepoBasePath
            } else {
                var prefixPath: String

                if (linkRef.filePath.isEmpty() && linkRef.anchor != null) {
                    linkRef = linkRef.replaceFilePath(linkRef.containingFile.fileName);
                }

                // here we resolve the start of the relative path
                val containingFilePrefixPath =
                        if (linkRef.containingFile.isWikiPage) {
                            // for the home page image links require wiki/ prefix, for the rest it is optional
                            if (linkRef.containingFile.isWikiHomePage) {
                                if (linkRef is ImageLinkRef) {
                                    wikiPrefixIsOptional = false
                                    vcsRepoBasePath
                                } else {
                                    wikiPrefixIsOptional = true
                                    vcsRepoBasePath + "wiki/"
                                }
                            } else vcsRepoBasePath + "wiki/"
                        } else {
                            vcsRepoBasePath + "blob/master/"
                        }

                // now append the link's path to see where we wind up
                prefixPath = PathInfo.appendParts(containingFilePrefixPath, linkRef.linkToFile(linkRef.path)).filePath.suffixWith('/')

                // we now see if we are still in the repo, if not then no match
                if (!prefixPath.startsWith(vcsRepoBasePath)) return false

                if (linkRef.fileName.isEmpty() && (prefixPath.equals(vcsRepoBasePath) || prefixPath.equals(vcsRepoBasePath.removeSuffix("/")))) {
                    // main repo link
                    prefixPath = vcsRepoBasePath
                    gitHubLink = ""
                    gitHubLinks = true
                } else {
                    prefixPath = prefixPath.substring(vcsRepoBasePath.length).removeSuffix("/")

                    if (!(linkRef.filePath.isEmpty() && linkRef.anchor == null)) {

                    } else {
                        // completion match
                    }

                    if (prefixPath.isEmpty() && linkRef.fileName in GitHubLinkResolver.GITHUB_LINKS) {
                        // add these to the path
                        prefixPath += linkRef.fileName
                        linkRef = linkRef.replaceFilePath("")
                    }

                    val pathParts = prefixPath.split('/', limit = 3).iterator()

                    // we take the first subdirectory after the prefix
                    val firstSub = pathParts.next()
                    val linkQuery = firstSub.split('?', limit = 2).iterator()
                    val gitHubLink = linkQuery.next()
                    var linkParams = if (linkQuery.hasNext()) "?" + linkQuery.next() else ""
                    var linkParamsSuffix = ""

                    // now we see where we are
                    when {
                        gitHubLink == "" && !linkRef.containingFile.isWikiHomePage -> {
                            prefixPath = vcsRepoBasePath
                            this.gitHubLink = "blob"
                            this.branchOrTag = "master"
                            linkParamsSuffix = "master"
                        }
                        gitHubLink == "wiki", gitHubLink == "" && linkRef.containingFile.isWikiHomePage -> {
                            if (linkParams.isEmpty() && !pathParts.hasNext() && linkRef.fileNameNoExt.isEmpty()) {
                                // just a link to wiki, match Home page
                                linkParams = "Home" + linkParams
                            }

                            if (gitHubLink == "" && wikiPrefixIsOptional === false) {
                                // has to have a wiki prefix or it will not resolve in the wiki
                                if (!pathParts.hasNext() || pathParts.next() != "wiki") {
                                    // to allow for loose matching
                                    looseMatchOnly = true
                                }
                            }

                            prefixPath = "$vcsRepoBasePath$projectDirName${PathInfo.WIKI_HOME_DIR_EXTENSION}/"
                            this.gitHubLink = "wiki"
                            wikiMatchingRules = true
                        }
                        gitHubLink == "blob", gitHubLink == "raw" -> {
                            if (pathParts.hasNext()) {
                                linkParamsSuffix = pathParts.next()
                            } else {
                                linkParamsSuffix = "master"
                                looseMatchOnly = true
                            }

                            branchOrTag = linkParamsSuffix
                            prefixPath = vcsRepoBasePath
                            this.gitHubLink = gitHubLink
                        }
                        gitHubLink in GitHubLinkResolver.GITHUB_LINKS -> {
                            if (linkRef is ImageLinkRef) {
                                looseMatchOnly = true
                            }

                            prefixPath = "$vcsRepoBasePath$gitHubLink/"
                            gitHubLinks = true
                            this.gitHubLink = gitHubLink
                        }
                        else -> {
                            if (linkRef.containingFile.isWikiHomePage) {
                                // this one does checks in wiki, but only if it is not an image link
                                if (linkRef is ImageLinkRef) {
                                    looseMatchOnly = true
                                }
                            }
                            prefixPath = "$vcsRepoBasePath$projectDirName${PathInfo.WIKI_HOME_DIR_EXTENSION}/"
                            this.gitHubLink = "wiki"
                            wikiMatchingRules = true
                        }
                    }

                    var splicedParts = pathParts.splice("/", skipEmpty = false)

                    // if we have wiki already handled, and there is a wiki/ in the link then it must be optional or only loose match is possible
                    if (wikiPrefixIsOptional !== true && gitHubLink == "wiki" && (splicedParts.startsWith("wiki/") || splicedParts == "wiki")) looseMatchOnly = true;

                    // handle the optional wiki prefix for wiki/Home page
                    if (wikiPrefixIsOptional === true || looseMatchOnly) {
                        if (splicedParts.startsWith("wiki/")) splicedParts = splicedParts.substring("wiki/".length)
                        else if (splicedParts == "wiki") splicedParts = ""
                    }

                    val gitHubRepoLink = (linkParams + arrayOf(linkParamsSuffix, splicedParts).reduce(skipEmptySplicer("/")).suffixWith('/') + linkRef.fileName).removePrefix("/")
                    repoLink = (linkParams + splicedParts.suffixWith('/') + linkRef.fileName).removePrefix("/")
                    gitHubLinkWithParams = this.gitHubLink + (if (!gitHubRepoLink.startsWith("?", "/")) "/" else "") + gitHubRepoLink + linkRef.anchorText
                }

                fixedPrefix = prefixPath

                // no need for pattern match, we have the stuff
                if (gitHubLinks) return true
            }


            // from here we need to use repoLink, this is the stuff that comes after fixedPrefix
            linkRef = linkRef.replaceFilePath(fullPath = repoLink)
            val wantCompletionMatch = originalWantCompletionMatch || linkRef.filePath.isEmpty() || linkRef.fileNameNoExt.startsWith('.') && !linkRef.hasExt
            val filePath = if (wantCompletionMatch) "" else linkRef.filePath
            val filePathNoExt = if (wantCompletionMatch) "" else linkRef.filePathNoExt
            val fileName = if (wantCompletionMatch) "" else linkRef.fileName
            val fileNameNoExt = if (wantCompletionMatch) "" else linkRef.fileNameNoExt
            val ext = if (linkRef.fileName.startsWith('.') && !linkRef.hasExt) linkRef.fileName else linkRef.ext
            val linkExtensions = if (linkRef.fileName.startsWith('.') && !linkRef.hasExt) arrayOf() else linkRef.linkExtensions
            val emptyMatchesAll = filePath.isEmpty() || wantCompletionMatch
            effectiveExt = ext

            if (wikiMatchingRules) {
                // same as wiki link but without the wiki to file name conversion and the anchor matching, anchor needs to be escaped
                // we do anchor matching for loose match identical to wiki links but without the wiki to file conversion

                // if it has extension then we inlude it as alternative before default extensions because it may not be an extension but part of the file name
                if (useLooseMatch) {
                    val looseAnchorPattern = linkTextToFileMatch(PathInfo(linkRef.anchorText).filePathNoExt, isOptional = true)
                    val looseFilenamePattern = linkTextToFileMatch(fileNameNoExt, isOptional = false, emptyMatchesAll = emptyMatchesAll)
                    val looseFilePrefixPattern = linkTextToFileMatch(if (filePath.isEmpty()) linkRef.containingFile.filePath else linkRef.path)
                    val looseExtensionPattern = (extensionPattern(ext, isOptional = true) + extensionPattern(PathInfo(linkRef.anchorText).ext, *linkExtensions, isOptional = true)).regexGroup()
                    linkLooseMatch = "^$fixedPrefix$looseFilePrefixPattern$subDirPattern$looseFilenamePattern$looseAnchorPattern${looseExtensionPattern.suffixWith('?')}$"
                    linkLooseMatchExtensions = arrayOf(ext, PathInfo(linkRef.anchorText).ext, *linkExtensions).filter { !it.isEmpty() }
                }

                val defaultExtensions = extensionPattern(*linkExtensions, isOptional = false)
                val extensionPattern = if (!ext.isEmpty()) (extensionPattern(ext, isOptional = false) + "|" + extensionPattern(ext, isOptional = false) + defaultExtensions).regexGroup() else defaultExtensions
                val filenamePattern = linkTextToFileMatch(filePath, isOptional = false, emptyMatchesAll = emptyMatchesAll)
                val filenameNoExtPattern = linkTextToFileMatch(filePathNoExt, isOptional = false, emptyMatchesAll = emptyMatchesAll)
                val anchorPattern = linkTextToFileMatch(linkRef.anchorText, isOptional = false)

                linkFileMatch = "^$fixedPrefix$filenamePattern$"
                if (!anchorPattern.isEmpty()) linkFileAnchorMatch = "^$fixedPrefix$filenameNoExtPattern$anchorPattern$"
                if (!anchorPattern.isEmpty()) linkSubAnchorExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
                linkSubExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$extensionPattern$"
                linkAllMatchExtensions = arrayOf(ext, *linkExtensions).filter { !it.isEmpty() }

                // if linkref path is empty then this should match linkFileMatch|linkSubExtMatch|linkSubAnchorExtMatch|fileAnchorMatch
                linkAllMatch = if (linkRef.path.isEmpty()) "$linkFileMatch|$linkSubExtMatch" else linkFileMatch
            } else {
                // regular repo match, we build up all options for looseMatch and later resolution as to what we really matched
                val extensionPattern = if (linkRef.hasExt) extensionPattern(ext, isOptional = false) else extensionPattern(*linkExtensions, isOptional = false)
                val filenamePattern = linkTextToFileMatch(filePath, isOptional = false, emptyMatchesAll = emptyMatchesAll)
                val filenameNoExtPattern = linkTextToFileMatch(filePathNoExt, isOptional = false, emptyMatchesAll = emptyMatchesAll)

                if (useLooseMatch) {
                    val looseAnchorPattern = linkTextToFileMatch(PathInfo(linkRef.anchorText).filePathNoExt, isOptional = true)
                    val looseFilenamePattern = linkTextToFileMatch(fileNameNoExt, isOptional = false, emptyMatchesAll = emptyMatchesAll)
                    val looseExtensionPattern = extensionPattern(ext, PathInfo(linkRef.anchorText).ext, *linkExtensions, isOptional = true)
                    linkLooseMatch = "^$fixedPrefix$subDirPattern$looseFilenamePattern$looseAnchorPattern${looseExtensionPattern.suffixWith('?')}$"
                    linkLooseMatchExtensions = arrayOf(ext, PathInfo(linkRef.anchorText).ext, *linkExtensions).filter { !it.isEmpty() }

                    val anchorPattern = linkTextToFileMatch(linkRef.anchorText, isOptional = false)
                    if (!anchorPattern.isEmpty()) linkFileAnchorMatch = "^$fixedPrefix$filenameNoExtPattern$anchorPattern$"
                    if (!anchorPattern.isEmpty()) linkSubAnchorExtMatch = "^$fixedPrefix$noWikiSubDirPattern$filenameNoExtPattern$anchorPattern$extensionPattern$"
                    linkSubExtMatch = "^$fixedPrefix$noWikiSubDirPattern$filenameNoExtPattern$extensionPattern$"
                }

                // only exact matches for files, the rest are loose matches
                linkFileMatch = "^$fixedPrefix$filenamePattern$"
                linkAllMatch = linkFileMatch
                linkAllMatchExtensions = if (linkRef.hasExt) arrayListOf(ext) else arrayListOf(*linkExtensions)
            }
        }

        assert(linkLooseMatch != null)
        assert(linkAllMatch != null)
        return !looseMatchOnly
    }
}
