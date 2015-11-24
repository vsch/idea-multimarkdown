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

import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType
import org.intellij.images.fileTypes.ImageFileTypeManager
import java.util.*
import kotlin.text.RegexOption


class GitHubLinkResolver(projectResolver: LinkResolver.ProjectResolver, containingFile: FileRef) : LinkResolver(containingFile, projectResolver) {

    class GitHubMismatchReason internal constructor(id: String, severity: MismatchReason.Severity, fixedLink: String? = null, fixedFilePath: String? = null) : MismatchReason(id,severity, fixedLink, fixedFilePath) {

        internal constructor(mismatch:MismatchReason, fixedLink: String? = null, fixedFilePath: String? = null) : this(mismatch.id, mismatch.severity, fixedLink, fixedFilePath)
    }

    companion object {
        @JvmStatic val GITHUB_WIKI_HOME_DIRNAME = "wiki"
        @JvmStatic val GITHUB_WIKI_HOME_FILENAME = "Home"
        @JvmStatic val GITHUB_ISSUES_NAME = "issues"
        @JvmStatic val GITHUB_GRAPHS_NAME = "graphs"
        @JvmStatic val GITHUB_PULSE_NAME = "pulse"
        @JvmStatic val GITHUB_PULLS_NAME = "pulls"

        @JvmStatic val GITHUB_LINKS = arrayOf(GITHUB_WIKI_HOME_DIRNAME, GITHUB_ISSUES_NAME, GITHUB_GRAPHS_NAME, GITHUB_PULSE_NAME, GITHUB_PULLS_NAME)

        val GITHUB_REASON_NON_VCS_FILE = GitHubMismatchReason("NON_VCS_FILE", MismatchReason.Severity.WARNING)
        val GITHUB_REASON_WIKI_LINK_HAS_EXT = GitHubMismatchReason("WIKI_LINK_HAS_EXT", MismatchReason.Severity.WARNING)
        val GITHUB_REASON_WIKI_LINK_HAS_WRONG_EXT = GitHubMismatchReason("WIKI_LINK_HAS_WRONG_EXT", MismatchReason.Severity.ERROR)
        val GITHUB_REASON_WIKI_LINK_CANNOT_HAVE_EXT = GitHubMismatchReason("WIKI_LINK_CANNOT_HAVE_EXT", MismatchReason.Severity.ERROR)
        val GITHUB_REASON_WIKI_LINK_TARGET_HAS_ANCHOR = GitHubMismatchReason("WIKI_LINK_TARGET_HAS_ANCHOR", MismatchReason.Severity.WARNING)
        val GITHUB_REASON_WIKI_FILE_HAS_ANCHOR_LINK_ANCHOR = GitHubMismatchReason("WIKI_FILE_HAS_ANCHOR_LINK_ANCHOR", MismatchReason.Severity.ERROR)
        val GITHUB_REASON_WIKI_HAS_SUBDIR = GitHubMismatchReason("WIKI_HAS_SUBDIR", MismatchReason.Severity.ERROR)
        val GITHUB_REASON_WIKI_FILE_HAS_SPACE = GitHubMismatchReason("WIKI_FILE_HAS_SPACE", MismatchReason.Severity.WEAK_WARNING)
        val GITHUB_REASON_WIKI_LINK_HAS_DASH = GitHubMismatchReason("WIKI_LINK_HAS_DASH", MismatchReason.Severity.WEAK_WARNING)
        val GITHUB_REASON_WIKI_CASE_MISMATCH = GitHubMismatchReason("WIKI_CASE_MISMATCH", MismatchReason.Severity.WEAK_WARNING)
        val GITHUB_REASON_WIKI_MULTIPLE_MATCH = GitHubMismatchReason("WIKI_MULTIPLE_MATCH", MismatchReason.Severity.WARNING)
        val GITHUB_REASON_WIKI_LINK_TO_RAW = GitHubMismatchReason("WIKI_LINK_TO_RAW", MismatchReason.Severity.WARNING)
        val GITHUB_REASON_LINK_TARGET_HAS_ANCHOR = GitHubMismatchReason("LINK_TARGET_HAS_ANCHOR", MismatchReason.Severity.ERROR)
    }

    override fun context(containingFile: FileRef, options: Int, inList: List<FileRef>?, branchOrTag: String?): ContextImpl {
        return ContextImpl(this, containingFile, options, inList, branchOrTag)
    }

    override fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String?): String {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return ContextImpl(this, linkRef.containingFile, 0, null, branchOrTag).relativePath(linkRef, targetRef, withExtForWikiPage)
    }

    override fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, branchOrTag: String?): Boolean {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return ContextImpl(this, linkRef.containingFile, 0, null, branchOrTag).isResolvedTo(linkRef, targetRef)
    }

    override fun resolve(linkRef: LinkRef, options: Int, inList: List<FileRef>?): PathInfo? {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return ContextImpl(this, linkRef.containingFile, options, inList).resolve(linkRef)
    }

    override fun multiResolve(linkRef: LinkRef, options: Int, inList: List<FileRef>?): List<PathInfo> {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return ContextImpl(this, linkRef.containingFile, options, inList).multiResolve(linkRef)
    }

    override fun isResolved(linkRef: LinkRef, options: Int, inList: List<FileRef>?): Boolean {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return ContextImpl(this, linkRef.containingFile, options, inList).resolve(linkRef) != null
    }

    override fun analyze(linkRef: LinkRef, targetRef: FileRef): MismatchReasons {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return ContextImpl(this, linkRef.containingFile).analyze(linkRef, targetRef)
    }

    override fun linkAddress(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean, branchOrTag: String?): String {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return ContextImpl(this, linkRef.containingFile).linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag)
    }

    class ContextImpl(resolver: GitHubLinkResolver, containingFile: FileRef, options: Int = 0, inList: List<FileRef>? = null, branchOrTag: String? = null) : LinkResolver.Context(resolver, containingFile, options, inList, branchOrTag) {
        val projectResolver: ProjectResolver = resolver.projectResolver
        val projectBasePath = resolver.projectBasePath
        val project: Project? = resolver.project

        // TEST: this needs tests to make sure it works
        override fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, branchOrTag: String?): Boolean {
            val linkRefText = relativePath(linkRef, targetRef, !(linkRef is WikiLinkRef || targetRef.isWikiPage && !linkRef.hasExt), branchOrTag)
            return linkRef.filePath.equals(linkRefText, ignoreCase = true)
        }

        override fun resolve(linkRef: LinkRef, options: Int): PathInfo? {
            // TODO: if only want external, then can try to resolve external links to local file refs if they map, for that need to parse the
            // UrlLinkRef's file path and have GitHubRepo reverse the href to local path

            if (linkRef.isRemote) return if (wantRemote(options)) linkRef else null

            if (linkRef.isSelfAnchor) {
                if (wantLocal(options) || projectResolver.isUnderVcs(linkRef.containingFile)) return linkRef.containingFile
                return null
            }

            var targetRef: PathInfo = linkRef

            if (!linkRef.isAbsolute) {
                // resolve the relative link as per requested options
                val linkRefMatcher = GitHubLinkRefMatcher(linkRef, projectBasePath, wantLooseMatch(options) || linkRef.isEmpty)
                val matches = getMatchedRefs(linkRef, linkRefMatcher, options, inList)
                var resolvedRef = (if (matches.size > 0) matches[0] else null) ?: return null
                targetRef = resolvedRef
            }

            if (targetRef.isAbsolute) {
                val pathInfo = when {
                    targetRef.isRemote -> if (wantRemote(options)) targetRef else null
                    targetRef.isLocal && targetRef.isURI && targetRef is UrlLinkRef -> if (!wantLocal(options) || project == null) null else targetRef.virtualFileRef(project)
                    else -> targetRef
                }
                return pathInfo
            }

            return null
        }

        override fun multiResolve(linkRef: LinkRef, options: Int): List<PathInfo> {
            if (linkRef is WikiLinkRef && !wantLooseMatch(options) && linkRef.hasExt) return ArrayList()  // wiki links don't resolve with extensions
            val linkRefMatcher = GitHubLinkRefMatcher(linkRef, projectBasePath, wantLooseMatch(options) || linkRef.isEmpty)
            return getMatchedRefs(linkRef, linkRefMatcher, options, inList)
        }

        override fun analyze(linkRef: LinkRef, targetRef: FileRef): MismatchReasons {
            throw UnsupportedOperationException()
        }

        protected fun getTargetFileTypes(linkRef: LinkRef): HashSet<String> {
            var targetFileType = when {
                linkRef is WikiLinkRef, linkRef is FileLinkRef && (!linkRef.hasExt || linkRef.isMarkdownExt) -> MultiMarkdownFileType.INSTANCE.toString()
                linkRef is ImageLinkRef, linkRef is FileLinkRef && (linkRef.isImageExt) -> ImageFileTypeManager.getInstance().imageFileType.toString()
            // TODO: get the IDE to guess file type from extension
                else -> ""
            }

            val typeSet = HashSet<String>()
            typeSet.add(targetFileType)
            return typeSet
        }

        fun getMatchedRefs(linkRef: LinkRef, linkRefMatcher: GitHubLinkRefMatcher, options: Int = this.options, fromList: List<PathInfo>?): List<PathInfo> {
            // process the files that match the pattern and put them in the list
            var matches = ArrayList<PathInfo>()

            val matchPattern = linkRefMatcher.patternText(wantLooseMatch(options)) ?: return matches

            val wikiMatch = matchPattern.toRegex(RegexOption.IGNORE_CASE)
            val linkMatch = if (wantLooseMatch(options)) wikiMatch else matchPattern.toRegex()

            if (!linkRefMatcher.gitHubLinks) {
                if (fromList == null) {
                    val targetFileTypes = getTargetFileTypes(linkRef)
                    if (targetFileTypes.isEmpty() || project == null) {
                        if (targetFileTypes.isNotEmpty()) {
                            // Only used in testing, runtime uses the file indices
                            val projectFileList = projectResolver.projectFileList()
                            if (projectFileList != null) {
                                for (fileRef in projectFileList) {
                                    if (fileRef.filePath.matches(if (fileRef.isWikiPage) wikiMatch else linkMatch)) {
                                        matches.add(fileRef)
                                    }
                                }
                            }
                        } else {
                            return ArrayList(0)
                        }
                    } else {
                        FileBasedIndex.getInstance().getFilesWithKey(FilenameIndex.NAME, targetFileTypes, { file ->
                            val virtualFileRef = VirtualFileRef(file, project)
                            if (virtualFileRef.filePath.matches(if (virtualFileRef.isWikiPage) wikiMatch else linkMatch)) {
                                matches.add(virtualFileRef)
                            }
                            true
                        }, GlobalSearchScope.projectScope(project))
                    }
                } else {
                    for (fileRef in fromList) {
                        // here we can have both local and external we skip external since we don't resolve them yet
                        if (fileRef is FileRef) {
                            if (fileRef.filePath.matches(if (fileRef.isWikiPage) wikiMatch else linkMatch)) {
                                matches.add(fileRef)
                            }
                        }
                    }
                }

                if (linkRef !is ImageLinkRef && matches.size > 1) matches.sort { self, other -> self.compareTo(other) }

                // here we post process for local/remote desires
                if ((wantRemote(options) && !wantLocal(options)) || wantRemoteUrl(options)) {
                    val remoteMatches = ArrayList<PathInfo>()

                    for (fileRef in matches) {
                        if (fileRef is FileRef && projectResolver.isUnderVcs(fileRef)) {
                            if (!wantLocal(options)) remoteMatches.add(fileRef)

                            if (wantRemoteUrl(options)) {
                                val remoteUrl = projectResolver.repoUrlFor(fileRef, linkRef !is WikiLinkRef && linkRef.hasExt, linkRef.anchor)
                                if (remoteUrl != null) {
                                    val urlRef = LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl)
                                    assert(urlRef is UrlLinkRef, { "expected to get UrlLinkRef, instead got ${urlRef.javaClass}" })
                                    remoteMatches.add(urlRef)
                                }
                            }
                        }
                    }

                    if (!wantLocal(options)) {
                        matches = remoteMatches
                    } else {
                        matches.addAll(remoteMatches)
                    }
                }
            } else {
                if (wantRemote(options) || wantRemoteUrl(options)) {
                    // add the fixed links for GitHub if they match
                    val remoteUrl = projectResolver.vcsRepoUrlBase(linkRef.containingFile)
                    if (remoteUrl != null) {
                        assert(remoteUrl.startsWith("http://") || remoteUrl.startsWith("https://"), { "remote vcsRepoBase has to start with http:// or https://, instead got ${remoteUrl}" })
                        for (part in GITHUB_LINKS) {
                            if ((projectBasePath.endWith('/') + part).matches(linkMatch)) {
                                val urlRef = LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl.endWith('/') + part)
                                matches.add(urlRef)
                            }
                        }
                    }
                }
            }
            return matches
        }

        fun logicalRemotePath(fileRef: FileRef, useWikiPageActualLocation: Boolean, isSourceRef: Boolean, isImageLinkRef: Boolean, branchOrTag: String?): PathInfo {
            var filePathInfo: PathInfo

            if (fileRef.isUnderWikiDir) {
                if (useWikiPageActualLocation && !isSourceRef) filePathInfo = PathInfo(fileRef.path)
                else if (fileRef.isWikiHomePage && isSourceRef && isImageLinkRef) filePathInfo = PathInfo.append(fileRef.wikiDir, "..")
                else filePathInfo = PathInfo(fileRef.wikiDir)
            } else {
                filePathInfo = PathInfo(projectBasePath.endWith('/') + "blob/" + (branchOrTag ?: "master").endWith('/') + PathInfo.relativePath(projectBasePath.endWith('/'), fileRef.path, withPrefix = false))
            }
            return filePathInfo
        }

        override fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String?): String {
            val containingFilePath = logicalRemotePath(containingFile, useWikiPageActualLocation = false, isSourceRef = true, isImageLinkRef = linkRef is ImageLinkRef, branchOrTag = branchOrTag).filePath.endWith('/')
            val targetFilePath = logicalRemotePath(targetRef, useWikiPageActualLocation = withExtForWikiPage, isSourceRef = false, isImageLinkRef = linkRef is ImageLinkRef, branchOrTag = branchOrTag).filePath.endWith('/')
            return PathInfo.relativePath(containingFilePath, targetFilePath, withPrefix = true)
        }

        override fun linkAddress(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean, branchOrTag: String?): String {
            if (targetRef is FileRef) {
                var prefix = relativePath(linkRef, targetRef, withExtForWikiPage, branchOrTag)

                if (linkRef is WikiLinkRef) {
                    return prefix.endWith('/') + (if (withExtForWikiPage) targetRef.fileName else targetRef.fileNameNoExt).replace('-', ' ')
                } else {
                    if (prefix.isNotEmpty() && targetRef.isUnderWikiDir) {
                        // if the prefix starts with the wiki dir change it to the generic wiki used in links
                        val wikiDirName = targetRef.wikiDir.substring(targetRef.mainRepoDir.length + 1).endWith('/')
                        if (containingFile.isUnderWikiDir && prefix.startsWith(wikiDirName)) prefix = "wiki/" + prefix.substring(wikiDirName.length)
                        else if (!containingFile.isUnderWikiDir && prefix.startsWith("../../" + wikiDirName)) prefix = "../../wiki/" + prefix.substring(("../../" + wikiDirName).length)
                    }

                    val selfRef = isSelfRef(linkRef, targetRef, withExtForWikiPage)
                    val anchor = optimizedLinkAnchor(linkRef, targetRef, withExtForWikiPage)

                    if (targetRef.isWikiPage) {
                        if (selfRef) return anchor
                        else {
                            val fileName = (prefix.endWith('/') + if (!withExtForWikiPage) (if (targetRef.isWikiHomePage) "" else targetRef.fileNameNoExt) else targetRef.fileName).replace(" ", "%20")
                            return (if (linkRef !is WikiLinkRef) fileName.replace("#", "%23") else fileName).removeSuffix("/") + anchor
                        }
                    } else {
                        if (selfRef) return anchor
                        else return (prefix.endWith('/') + targetRef.fileName).replace(" ", "%20").replace("#", "%23") + anchor
                    }
                }
            } else if (targetRef is UrlLinkRef) {
                // convert git hub links to relative links
                val remoteUrl = projectResolver.vcsRepoUrlBase(linkRef.containingFile)
                if (remoteUrl != null) {
                    assert(remoteUrl.startsWith("http://", "https://"), { "remote vcsRepoBase has to start with http:// or https://, instead got ${remoteUrl}" })

                    if (targetRef.path.startsWith(remoteUrl.endWith('/'))) {
                        val fileName = targetRef.filePath.substring(remoteUrl.endWith('/').length)
                        if (fileName in GITHUB_LINKS) {
                            return when {
                                containingFile.isWikiHomePage -> fileName
                                containingFile.isUnderWikiDir -> "../" + fileName
                                else -> PathInfo.relativePath(containingFile.path, projectBasePath.endWith('/'), withPrefix = true) + "../../" + fileName
                            }
                        } else {
                            // TEST: conversion of remote links to link addresses for all files and from all source files wiki/Home.md, wiki/normal-file.md and Readme.md
                            if (fileName.startsWith("wiki/")) {
                                // trying for wiki page
                                val filePath = when {
                                    containingFile.isWikiHomePage -> fileName
                                    containingFile.isUnderWikiDir -> fileName.substring("wiki/".length)
                                    else -> PathInfo.relativePath(containingFile.path, projectBasePath.endWith('/'), withPrefix = true).endWith("/") + "../../" + fileName
                                }
                                return filePath
                            } else {
                                // main repo file, if it starts with blob/something/ then we can handle it
                                var repoPrefixPathPattern = ("^\\Qblob/\\E([^/]+)\\Q/\\E").toRegex()
                                if (fileName.matches(repoPrefixPathPattern)) {
                                    val match = repoPrefixPathPattern.find(fileName)
                                    if (match != null) {
                                        val oldBranchOrTag = match.groups[0]
                                        // we throw out the branch if one is given to us or if linking from another file in the repo, its branch or tag will be used by GitHub
                                        var fileNamePart = fileName.substring(match.range.end + 1)
                                        val filePath = when {
                                            containingFile.isWikiHomePage -> "blob/${branchOrTag ?: oldBranchOrTag ?: "master"}/" + fileNamePart
                                            containingFile.isUnderWikiDir -> "../blob/${branchOrTag ?: oldBranchOrTag ?: "master"}/" + fileNamePart
                                            else -> PathInfo.relativePath(containingFile.path, projectBasePath.endWith('/'), withPrefix = true).endWith("/") + fileNamePart
                                        }
                                        return filePath
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return ""
        }

        fun optimizedLinkAnchor(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean): String {
            val anchorUsedInMatch = wasAnchorUsedInMatch(linkRef, targetRef)
            val selfRef = isSelfRef(linkRef, targetRef, withExtForWikiPage)

            return if (anchorUsedInMatch)
                (if (selfRef) "#" else "")
            else
                (if (selfRef) "#" + linkRef.anchor.orEmpty() else linkRef.anchorText)
        }

        fun isSelfRef(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean): Boolean {
            return if ((targetRef.isWikiPage && withExtForWikiPage) || (!targetRef.isWikiPage && linkRef.hasExt)) linkRef.containingFile.filePathNoExt.equals(targetRef.filePathNoExt)
            else linkRef.containingFile.filePath.equals(targetRef.filePath)
        }

        fun wasAnchorUsedInMatch(linkRef: LinkRef, targetRef: PathInfo): Boolean {
            return linkRef.hasAnchor && (!linkRef.fileName.replace("%23", "#").equals(targetRef.fileName) && targetRef.fileName.endsWith(linkRef.anchorText)
                    || !linkRef.fileNameNoExt.replace("%23", "#").equals(targetRef.fileNameNoExt) && targetRef.fileNameNoExt.endsWith(linkRef.anchorText))
        }

        fun equivalentWikiLinkRef(fileName: String, wikiLinkAddress: String): Boolean {
            return fileName.replace(' ', '-').equals(wikiLinkAddress.replace(' ', '-'), true)
        }
    }
}

