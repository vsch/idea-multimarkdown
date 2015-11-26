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
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin
import org.intellij.images.fileTypes.ImageFileTypeManager
import java.util.*
import kotlin.text.RegexOption


class GitHubLinkResolver(projectResolver: LinkResolver.ProjectResolver, containingFile: FileRef, branchOrTag: String? = null) : LinkResolver(projectResolver, containingFile, branchOrTag) {

    companion object {
        @JvmStatic @JvmField val GITHUB_WIKI_HOME_DIRNAME = "wiki"
        @JvmStatic @JvmField val GITHUB_ISSUES_NAME = "issues"
        @JvmStatic @JvmField val GITHUB_GRAPHS_NAME = "graphs"
        @JvmStatic @JvmField val GITHUB_PULSE_NAME = "pulse"
        @JvmStatic @JvmField val GITHUB_PULLS_NAME = "pulls"

        @JvmStatic @JvmField val GITHUB_LINKS = arrayOf(GITHUB_WIKI_HOME_DIRNAME, GITHUB_ISSUES_NAME, GITHUB_GRAPHS_NAME, GITHUB_PULSE_NAME, GITHUB_PULLS_NAME)
    }

    constructor(virtualFile: VirtualFile, project: Project) : this(MultiMarkdownPlugin.getProjectComponent(project)!!, FileRef(virtualFile.path))

    constructor(projectFileRef: ProjectFileRef) : this(MultiMarkdownPlugin.getProjectComponent(projectFileRef.project)!!, projectFileRef)

    constructor(psiFile: PsiFile) : this(MultiMarkdownPlugin.getProjectComponent(psiFile.project)!!, FileRef(psiFile.virtualFile.path))

    constructor(psiElement: PsiElement) : this(psiElement.containingFile)

    internal val linkInspector: GitHubLinkInspector by lazy { GitHubLinkInspector(this) }

    // TEST: this needs tests to make sure it works
    override fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean?, branchOrTag: String?): Boolean {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        val linkRefText = linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag)
        return linkRef.filePath.equals(linkRefText, ignoreCase = true)
    }

    override fun isResolved(linkRef: LinkRef, options: Int, inList: List<PathInfo>?): Boolean {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return resolve(linkRef, options, inList) != null
    }

    override fun resolve(linkRef: LinkRef, options: Int, inList: List<PathInfo>?): PathInfo? {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        // TODO: if only want local, then can try to resolve external links to local file refs if they map, for that need to parse the
        // LinkRef's URL file path and remove the repoPrefix for non-Wiki and wikiRepoPrefix for wiki files, then prefix the result with the corresponding basePath
        var targetRef: PathInfo = linkRef

        if (linkRef.isSelfAnchor) {
            if (linkRef is WikiLinkRef && linkRef.filePath.isEmpty()) {
                // here it is a pure anchor wiki link, which does not resolve
                if (!wantLooseMatch(options)) return null
            }

            targetRef = linkRef.containingFile
        } else if (!linkRef.isAbsolute) {
            // resolve the relative link as per requested options
            val linkRefMatcher = GitHubLinkMatcher(linkRef, projectBasePath, wantLooseMatch(options) || linkRef.isEmpty)
            val matches = getMatchedRefs(linkRef, linkRefMatcher, options, inList)
            var resolvedRef = (if (matches.size > 0) matches[0] else null) ?: return null
            targetRef = resolvedRef
        }

        return processMatchOptions(linkRef, targetRef, options)
    }

    override fun multiResolve(linkRef: LinkRef, options: Int, inList: List<PathInfo>?): List<PathInfo> {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })

        if (linkRef is WikiLinkRef && !wantLooseMatch(options) && linkRef.hasExt) return ArrayList()  // wiki links don't resolve with extensions
        val linkRefMatcher = GitHubLinkMatcher(linkRef, projectBasePath, wantLooseMatch(options) || linkRef.isEmpty)
        return getMatchedRefs(linkRef, linkRefMatcher, options, inList)
    }

    override fun inspect(linkRef: LinkRef, targetRef: FileRef): List<InspectionResult> {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return linkInspector.inspect(linkRef, targetRef)
    }

    protected fun getTargetFileTypes(linkRef: LinkRef): HashSet<String> {
        var targetFileType = when {
            linkRef is WikiLinkRef, !linkRef.hasExt || linkRef.isMarkdownExt -> MultiMarkdownFileType.INSTANCE.toString()
            linkRef is ImageLinkRef, linkRef.isImageExt -> ImageFileTypeManager.getInstance().imageFileType.toString()
        // TODO: get the IDE to guess file type from extension
            else -> ""
        }

        val typeSet = HashSet<String>()
        typeSet.add(targetFileType)
        return typeSet
    }

    fun processMatchOptions(linkRef: LinkRef, targetRef: PathInfo, options: Int): PathInfo? {
        if (targetRef is FileRef) {
            if (!wantLocal(options) || wantOnlyURI(options)) {
                if (projectResolver.isUnderVcs(targetRef)) {
                    // remote available
                    if (wantOnlyURI(options)) {
                        val remoteUrl = projectResolver.repoUrlFor(targetRef, linkRef.hasExt, linkRef.anchor)
                        if (remoteUrl != null) {
                            val urlRef = LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl)
                            assert(urlRef.isExternal, { "expected to get URL, instead got $urlRef" })
                            return urlRef
                        }
                    } else {
                        return targetRef
                    }
                } else {
                    // local only, if URL only, we need to convert to URI file:// type
                    if (wantOnlyURI(options)) return LinkRef(containingFile, "file://" + targetRef.filePath, null)
                }
            }

            // local, remote or URL
            return targetRef
        } else {
            if (!wantOnlyURI(options) && targetRef.isLocal) {
                // must be a file:// type, we convert it to a projectFileType if we have a project and just a path if we do not
                assert(targetRef.filePath.startsWith("file:"))
                if (project != null) {
                    return targetRef.projectFileRef(project)
                }
                return FileRef(targetRef.filePath.removePrefix("file:").removePrefix("//").startWith('/'))
            }

            if (wantURI(options)) return targetRef
        }
        return null
    }

    fun getMatchedRefs(linkRef: LinkRef, linkMatcher: GitHubLinkMatcher, options: Int, fromList: List<PathInfo>?): List<PathInfo> {
        // process the files that match the pattern and put them in the list
        var matches = ArrayList<PathInfo>()

        val matchPattern = linkMatcher.patternText(wantLooseMatch(options)) ?: return matches

        val wikiMatch = if (wantLooseMatch(options) || !linkRef.hasExt || linkRef is WikiLinkRef) matchPattern.toRegex(RegexOption.IGNORE_CASE) else matchPattern.toRegex()
        val linkMatch = if (wantLooseMatch(options)) wikiMatch else matchPattern.toRegex()

        if (!linkMatcher.gitHubLinks) {
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
                        val virtualFileRef = ProjectFileRef(file, project)
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

            if (matches.size > 1) matches.sort { self, other -> self.compareTo(other) }

            // here we post process for remote or url type request
            if (!wantLocal(options) || wantOnlyURI(options)) {
                val postProcessedMatches = ArrayList<PathInfo>()

                for (fileRef in matches) {
                    val pathInfo = processMatchOptions(linkRef, fileRef, options)
                    if (pathInfo != null) postProcessedMatches.add(pathInfo)
                }

                matches = postProcessedMatches
            }
        }

        if (wantRemote(options) && wantURI(options)) {
            // add the fixed links for GitHub if they match
            val remoteUrl = projectResolver.vcsRepoUrlBase(linkRef.containingFile)
            if (remoteUrl != null) {
                assert(remoteUrl.startsWith("http://") || remoteUrl.startsWith("https://"), { "remote vcsRepoBase has to start with http:// or https://, instead got $remoteUrl" })
                for (part in GITHUB_LINKS) {
                    if ((projectBasePath.endWith('/') + part).matches(linkMatch)) {
                        val urlRef = LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl.endWith('/') + part)
                        matches.add(urlRef)
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
            else if (fileRef.isWikiHomePage && isSourceRef && isImageLinkRef) filePathInfo = PathInfo.appendParts(fullPath = fileRef.wikiDir, parts = "..")
            else filePathInfo = PathInfo(fileRef.wikiDir)
        } else {
            filePathInfo = PathInfo(projectBasePath.endWith('/') + "blob/" + (branchOrTag ?: "master").endWith('/') + PathInfo.relativePath(projectBasePath.endWith('/'), fileRef.path, withPrefix = false))
        }
        return filePathInfo
    }

    override fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String?): String {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        val containingFilePath = logicalRemotePath(containingFile, useWikiPageActualLocation = false, isSourceRef = true, isImageLinkRef = linkRef is ImageLinkRef, branchOrTag = branchOrTag).filePath.endWith('/')
        val targetFilePath = logicalRemotePath(targetRef, useWikiPageActualLocation = withExtForWikiPage, isSourceRef = false, isImageLinkRef = linkRef is ImageLinkRef, branchOrTag = branchOrTag).filePath.endWith('/')
        return PathInfo.relativePath(containingFilePath, targetFilePath, withPrefix = true)
    }

    fun linkAddress(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): String {
        val linkRef = LinkRef(containingFile, targetRef.fileNameNoExt, anchor);
        return linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor)
    }

    fun wikiLinkAddress(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): String {
        val linkRef = WikiLinkRef(containingFile, targetRef.fileNameNoExt, anchor);
        return linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor)
    }

    fun imageLinkAddress(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): String {
        val linkRef = ImageLinkRef(containingFile, targetRef.fileNameNoExt, anchor);
        return linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor)
    }

    fun linkRef(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): LinkRef {
        return LinkRef.parseLinkRef(containingFile, linkAddress(targetRef, withExtForWikiPage, branchOrTag, anchor), ::LinkRef)
    }

    fun wikiLinkRef(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): WikiLinkRef {
        return LinkRef.parseLinkRef(containingFile, wikiLinkAddress(targetRef, withExtForWikiPage, branchOrTag, anchor), ::WikiLinkRef) as WikiLinkRef
    }

    fun imageLinkRef(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): ImageLinkRef {
        return LinkRef.parseLinkRef(containingFile, imageLinkAddress(targetRef, withExtForWikiPage, branchOrTag, anchor), ::ImageLinkRef) as ImageLinkRef
    }

    fun linkRef(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean?, branchOrTag: String?, anchor: String?): LinkRef {
        return when (linkRef) {
            is WikiLinkRef -> LinkRef.parseLinkRef(containingFile, linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor), ::WikiLinkRef)
            is ImageLinkRef -> LinkRef.parseLinkRef(containingFile, linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor), ::ImageLinkRef)
            else -> LinkRef.parseLinkRef(containingFile, linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor), ::LinkRef)
        }
    }

    @Suppress("NAME_SHADOWING")
    override fun linkAddress(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean?, branchOrTag: String?, anchor: String?): String {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        val withExtForWikiPage = withExtForWikiPage ?: linkRef.hasExt

        if (targetRef is FileRef) {
            var prefix = relativePath(linkRef, targetRef, withExtForWikiPage, branchOrTag)

            if (linkRef is WikiLinkRef) {
                return prefix.endWith('/') + linkRef.fileToLink(if (withExtForWikiPage) targetRef.fileName else targetRef.fileNameNoExt) + (anchor ?: if (wasAnchorUsedInMatch(linkRef, targetRef)) "" else linkRef.anchor).startWith("#")
            } else {
                if (prefix.isNotEmpty() && targetRef.isUnderWikiDir) {
                    // if the prefix starts with the wiki dir change it to the generic wiki used in links
                    val wikiDirName = targetRef.wikiDir.substring(targetRef.mainRepoDir.length + 1).endWith('/')
                    if (containingFile.isUnderWikiDir && prefix.startsWith(wikiDirName)) prefix = "wiki/" + prefix.substring(wikiDirName.length)
                    else if (!containingFile.isUnderWikiDir && prefix.startsWith("../../" + wikiDirName)) prefix = "../../wiki/" + prefix.substring(("../../" + wikiDirName).length)
                }

                val selfRef = isSelfRef(linkRef, targetRef, withExtForWikiPage)
                val optimizedAnchor = (anchor ?: optimizedLinkAnchor(linkRef, targetRef, withExtForWikiPage)).startWith('#')

                if (targetRef.isWikiPage) {
                    if (selfRef) return optimizedAnchor
                    else {
                        val fileName = prefix.endWith('/') + if (!withExtForWikiPage) (if (targetRef.isWikiHomePage) "" else targetRef.fileNameNoExt) else targetRef.fileName
                        return linkRef.fileToLink(fileName).removeSuffix("/") + optimizedAnchor
                    }
                } else {
                    if (selfRef) return optimizedAnchor
                    else return linkRef.fileToLink(prefix.endWith('/') + targetRef.fileName) + optimizedAnchor
                }
            }
        } else if (targetRef.isURI) {
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
        return linkRef.hasAnchor && (!linkRef.linkToFile(linkRef.fileName).equals(targetRef.fileName) && targetRef.fileName.endsWith(linkRef.anchorText)
                                     || !linkRef.linkToFile(linkRef.fileNameNoExt).equals(targetRef.fileNameNoExt) && targetRef.fileNameNoExt.endsWith(linkRef.anchorText))
    }

    fun equalLinks(fileName: String, wikiLinkAddress: String, ignoreCase: Boolean = true): Boolean {
        return WikiLinkRef.convertFileToLink(fileName).equals(WikiLinkRef.convertFileToLink(wikiLinkAddress), ignoreCase)
    }
}

