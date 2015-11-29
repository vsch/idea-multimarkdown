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

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.impl.FileTypeManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexImpl
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin
import org.intellij.images.fileTypes.ImageFileTypeManager
import java.util.*
import kotlin.text.RegexOption


class GitHubLinkResolver(projectResolver: LinkResolver.ProjectResolver, containingFile: FileRef, branchOrTag: String? = null) : LinkResolver(projectResolver, containingFile, branchOrTag) {

    companion object {
        @JvmStatic @JvmField val GITHUB_FORK_NAME = "fork"
        @JvmStatic @JvmField val GITHUB_GRAPHS_NAME = "graphs"
        @JvmStatic @JvmField val GITHUB_ISSUES_NAME = "issues"
        @JvmStatic @JvmField val GITHUB_PULLS_NAME = "pulls"
        @JvmStatic @JvmField val GITHUB_PULSE_NAME = "pulse"
        @JvmStatic @JvmField val GITHUB_RAW_NAME = "raw"
        @JvmStatic @JvmField val GITHUB_WIKI_NAME = "wiki"

        // IMPORTANT: keep alphabetically sorted. These are not re-sorted after match
        @JvmStatic @JvmField val GITHUB_LINKS = arrayOf(
                GITHUB_FORK_NAME,
                GITHUB_GRAPHS_NAME,
                GITHUB_ISSUES_NAME,
                GITHUB_PULLS_NAME,
                GITHUB_PULSE_NAME,
                GITHUB_RAW_NAME,
                GITHUB_WIKI_NAME
        )
    }

    constructor(virtualFile: VirtualFile, project: Project) : this(MultiMarkdownPlugin.getProjectComponent(project)!!, FileRef(virtualFile.path))

    constructor(projectFileRef: ProjectFileRef) : this(MultiMarkdownPlugin.getProjectComponent(projectFileRef.project)!!, projectFileRef)

    constructor(psiFile: PsiFile) : this(MultiMarkdownPlugin.getProjectComponent(psiFile.project)!!, FileRef(psiFile.virtualFile.path))

    constructor(psiElement: PsiElement) : this(psiElement.containingFile)

    internal val linkInspector: GitHubLinkInspector by lazy { GitHubLinkInspector(this) }

    // TEST: this needs tests to make sure it works
    override fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean?, branchOrTag: String?): Boolean {
        assert(linkRef.containingFile.compareTo(containingFile) == 0, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        val linkRefText = linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag)
        return linkRef.filePath.equals(linkRefText, ignoreCase = targetRef.isWikiPage && !linkRef.hasExt)
    }

    override fun isResolved(linkRef: LinkRef, options: Int, inList: List<PathInfo>?): Boolean {
        assert(linkRef.containingFile.compareTo(containingFile) == 0, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return resolve(linkRef, options, inList) != null
    }

    override fun resolve(linkRef: LinkRef, options: Int, inList: List<PathInfo>?): PathInfo? {
        assert(linkRef.containingFile.compareTo(containingFile) == 0, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
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
            val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef)
            val matches = getMatchedRefs(linkRef, linkRefMatcher, options, inList)
            var resolvedRef = (if (matches.size > 0) matches[0] else null) ?: return null
            targetRef = resolvedRef
        }

        return processMatchOptions(linkRef, targetRef, options)
    }

    override fun multiResolve(linkRef: LinkRef, options: Int, inList: List<PathInfo>?): List<PathInfo> {
        assert(linkRef.containingFile.compareTo(containingFile) == 0, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        val linkRefMatcher = GitHubLinkMatcher(projectResolver, linkRef)
        return getMatchedRefs(linkRef, linkRefMatcher, options, inList)
    }

    // TODO: change this to take an exact resolve list and a loose matched list so that
    // all types of issues could be analyzed, not just based on single target
    override fun inspect(linkRef: LinkRef, targetRef: FileRef, referenceId: Any?): List<InspectionResult> {
        assert(linkRef.containingFile.compareTo(containingFile) == 0, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return linkInspector.inspect(linkRef, targetRef, referenceId)
    }

    protected fun getTargetFileTypes(linkRef: LinkRef): HashSet<FileType> {
        var targetFileType = when {
            linkRef is WikiLinkRef, (linkRef !is ImageLinkRef && !linkRef.hasExt) || linkRef.isMarkdownExt -> MultiMarkdownFileType.INSTANCE
            linkRef is ImageLinkRef, linkRef.isImageExt -> ImageFileTypeManager.getInstance().imageFileType
            else -> {
                // get the file type from extension
                val typeManager = FileTypeManager.getInstance() as FileTypeManagerImpl
                typeManager.getFileTypeByExtension(linkRef.ext)
            }
        }

        val typeSet = HashSet<FileType>()
        typeSet.add(targetFileType)
        return typeSet
    }

    fun processMatchOptions(linkRef: LinkRef, targetRef: PathInfo, options: Int): PathInfo? {
        if (targetRef is FileRef) {
            if (!wantLocal(options) || wantOnlyURI(options)) {
                if (!wantLocal(options) && projectResolver.isUnderVcs(targetRef)) {
                    // remote available
                    if (wantOnlyURI(options)) {
                        val remoteUrl = projectResolver.getGitHubRepo(targetRef)?.urlForVcsRemote(targetRef, targetRef.isRawFile, linkRef.anchor, branchOrTag)
                        if (remoteUrl != null) {
                            val urlRef = LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl, targetRef)
                            assert(urlRef.isExternal, { "expected to get URL, instead got $urlRef" })
                            return urlRef
                        }
                    } else {
                        return targetRef
                    }
                } else {
                    // URL only, we need to convert to URI file:// type
                    if (wantOnlyURI(options)) return LinkRef(containingFile, "file://" + targetRef.filePath, null, targetRef as FileRef?)
                }
            } else {
                // local, remote or URL
                return targetRef
            }
        } else {
            if (!wantOnlyURI(options) && targetRef.isLocal) {
                // must be a file:// type, we convert it to a projectFileType if we have a project and just a path if we do not
                assert(targetRef.filePath.startsWith("file:"))
                if (project != null) {
                    return targetRef.projectFileRef(project)
                }
                // preserve rawFile status
                val fileRef = FileRef(targetRef.filePath.removePrefix("file:").removePrefix("//").prefixWith('/'))
                if (targetRef is LinkRef && targetRef.targetRef != null && targetRef.targetRef.isRawFile) fileRef.isRawFile = true
                return fileRef
            }

            if (wantURI(options)) return targetRef
        }
        return null
    }

    fun getMatchedRefs(linkRef: LinkRef, linkMatcher: GitHubLinkMatcher, options: Int, fromList: List<PathInfo>?): List<PathInfo> {
        // process the files that match the pattern and put them in the list
        var matches = ArrayList<PathInfo>()

        if (!linkMatcher.isValid) return matches

        if (!linkMatcher.gitHubLinks) {

            // TODO: need to have a flag or to modify the regex to exclude wiki matches when exact matching in the repo
            val allMatch =
                    if (wantLooseMatch(options)) {
                        linkMatcher.linkLooseMatch!!.toRegex(RegexOption.IGNORE_CASE)
                    } else {
                        if (linkMatcher.wikiMatchingRules) {
                            linkMatcher.linkAllMatch!!.toRegex(RegexOption.IGNORE_CASE)
                        } else {
                            linkMatcher.linkFileMatch!!.toRegex()
                        }
                    }

            if (fromList == null) {
                val targetFileTypes = getTargetFileTypes(linkRef)
                if (targetFileTypes.isEmpty() || project == null) {
                    if (targetFileTypes.isNotEmpty()) {
                        // Only used in testing, runtime uses the file indices
                        val projectFileList = projectResolver.projectFileList(targetFileTypes)
                        if (projectFileList != null) {
                            for (fileRef in projectFileList) {
                                if (fileRef.filePath.matches(allMatch)) {
                                    // we need to see what matched, exactly but will do it after we have all the matches
                                    matches.add(fileRef)
                                }
                            }
                        }
                    } else {
                        return ArrayList(0)
                    }
                } else {
                    //val projectFileList = projectResolver.projectFileList(targetFileTypes)
                    val instance = FileBasedIndex.getInstance() as FileBasedIndexImpl
                    val containingFiles = instance.processFilesContainingAllKeys(FileTypeIndex.NAME, targetFileTypes, GlobalSearchScope.projectScope(project), null, Processor<VirtualFile> { virtualFile ->
                        if (virtualFile.getPath().matches(allMatch)) {
                            // we need to see what matched, exactly but will do it after we have all the matches
                            val virtualFileRef = ProjectFileRef(virtualFile, project)
                            matches.add(virtualFileRef)
                        }
                        true
                    })
                }
            } else {
                for (fileRef in fromList) {
                    // here we can have both local and external we skip external since we don't resolve them yet
                    if (fileRef is FileRef) {
                        if (fileRef.filePath.matches(allMatch)) {
                            // we need to see what matched, exactly but will do it after we have all the matches
                            matches.add(fileRef)
                        }
                    }
                }
            }

            // now we need to weed out the matches that will not work, unless this is a loose match
            if (linkMatcher.wikiMatchingRules) {
                // here some will be case sensitive some not,
                // anchor and ext based matches are to wiki pages
                if (linkRef is WikiLinkRef) {
                    // these match wiki pages, also have priority over the non-pages, ie. if Test.kt and Test.kt.md exists, then the .md will be matched first
                    // not case sensitive: linkSubExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$extensionPattern$"
                    // not case sensitive: linkSubAnchorExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
                    //val pageMatch = (linkMatcher.linkSubExtMatch + "|" + linkMatcher.linkSubAnchorExtMatch).toRegex(RegexOption.IGNORE_CASE)

                    // these match raw file content
                    // case sensitive: linkFileMatch = "^$fixedPrefix$filenamePattern$"
                    // case sensitive: linkFileAnchorMatch = "^$fixedPrefix$filenamePattern$anchorPattern$"
                    val fileOrAnchorMatch = (linkMatcher.linkFileMatch + "|" + linkMatcher.linkFileAnchorMatch).toRegex()
                    for (fileRef in matches) {
                        if (fileRef is FileRef && fileRef.filePath.matches(fileOrAnchorMatch)) {
                            fileRef.isRawFile = true
                        }
                    }
                } else {
                    // these match wiki pages, also have priority over the non-pages, ie. if Test.kt and Test.kt.md exists, then the .md will be matched first
                    // not case sensitive: linkSubExtMatch = "^$fixedPrefix$subDirPattern$filenamePattern$extensionPattern$"

                    // these match raw file content and images
                    // case sensitive: linkFileMatch = "^$fixedPrefix$filenamePattern$"
                    val fileMatch = linkMatcher.linkFileMatch!!.toRegex()
                    for (fileRef in matches) {
                        if (fileRef is FileRef && fileRef.filePath.matches(fileMatch)) {
                            fileRef.isRawFile = true
                        }
                    }
                }
            } else {
                // case sensitive: linkFileMatch = "^$fixedPrefix$filenamePattern$"
                for (fileRef in matches) {
                    if (fileRef is FileRef) {
                        fileRef.isRawFile = true
                    }
                }
            }

            if (matches.size > 1) matches.sort { self, other ->
                if (self is FileRef && other is FileRef) {
                    if (self.isRawFile && !other.isRawFile) 1
                    else if (!self.isRawFile && other.isRawFile) -1
                    else self.compareTo(other)
                } else self.compareTo(other)
            }

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

        if (linkMatcher.gitHubLinks && wantRemote(options) && wantURI(options)) {
            // no need to check for links, the matcher has the link already set and we even pass all the stuff after the link
            val remoteUrl = projectResolver.getGitHubRepo(linkRef.containingFile)?.gitHubBaseUrl
            if (remoteUrl != null) {
                assert(remoteUrl.startsWith("http://") || remoteUrl.startsWith("https://"), { "remote vcsRepoBase has to start with http:// or https://, instead got $remoteUrl" })
                val urlRef = LinkRef.parseLinkRef(linkRef.containingFile, remoteUrl.suffixWith('/') + linkMatcher.gitHubLinkWithParams, null)
                matches.add(urlRef)
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
            filePathInfo = PathInfo(projectBasePath.suffixWith('/') + "blob/" + (branchOrTag ?: "master").suffixWith('/') + PathInfo.relativePath(projectBasePath.suffixWith('/'), fileRef.path, withPrefix = false))
        }
        return filePathInfo
    }

    override fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String?): String {
        assert(linkRef.containingFile.compareTo(containingFile) == 0, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        val containingFilePath = logicalRemotePath(containingFile, useWikiPageActualLocation = false, isSourceRef = true, isImageLinkRef = linkRef is ImageLinkRef, branchOrTag = branchOrTag).filePath.suffixWith('/')
        val targetFilePath = logicalRemotePath(targetRef, useWikiPageActualLocation = withExtForWikiPage, isSourceRef = false, isImageLinkRef = linkRef is ImageLinkRef, branchOrTag = branchOrTag).filePath.suffixWith('/')
        return PathInfo.relativePath(containingFilePath, targetFilePath, withPrefix = true)
    }

    fun linkAddress(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): String {
        val linkRef = LinkRef(containingFile, targetRef.fileNameNoExt, anchor, null);
        return linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor)
    }

    fun wikiLinkAddress(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): String {
        val linkRef = WikiLinkRef(containingFile, targetRef.fileNameNoExt, anchor, null);
        return linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor)
    }

    fun imageLinkAddress(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): String {
        val linkRef = ImageLinkRef(containingFile, targetRef.fileNameNoExt, anchor, null);
        return linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor)
    }

    fun linkRef(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): LinkRef {
        return LinkRef.parseLinkRef(containingFile, linkAddress(targetRef, withExtForWikiPage, branchOrTag, anchor), targetRef as? FileRef, ::LinkRef)
    }

    fun wikiLinkRef(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): WikiLinkRef {
        return LinkRef.parseLinkRef(containingFile, wikiLinkAddress(targetRef, withExtForWikiPage, branchOrTag, anchor), targetRef as? FileRef, ::WikiLinkRef) as WikiLinkRef
    }

    fun imageLinkRef(targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String?, anchor: String?): ImageLinkRef {
        return LinkRef.parseLinkRef(containingFile, imageLinkAddress(targetRef, withExtForWikiPage, branchOrTag, anchor), targetRef as? FileRef, ::ImageLinkRef) as ImageLinkRef
    }

    fun linkRef(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean?, branchOrTag: String?, anchor: String?): LinkRef {
        return when (linkRef) {
            is WikiLinkRef -> LinkRef.parseLinkRef(containingFile, linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor), targetRef as? FileRef, ::WikiLinkRef)
            is ImageLinkRef -> LinkRef.parseLinkRef(containingFile, linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor), targetRef as? FileRef, ::ImageLinkRef)
            else -> LinkRef.parseLinkRef(containingFile, linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag, anchor), targetRef as? FileRef, ::LinkRef)
        }
    }

    @Suppress("NAME_SHADOWING")
    override fun linkAddress(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean?, branchOrTag: String?, anchor: String?): String {
        assert(linkRef.containingFile.compareTo(containingFile) == 0, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        val withExtForWikiPage = withExtForWikiPage ?: linkRef.hasExt

        if (targetRef is FileRef) {
            var prefix = relativePath(linkRef, targetRef, withExtForWikiPage, branchOrTag)

            if (linkRef is WikiLinkRef) {
                return prefix.suffixWith('/') + linkRef.fileToLink(if (withExtForWikiPage) targetRef.fileName else targetRef.fileNameNoExt) + (anchor ?: if (wasAnchorUsedInMatch(linkRef, targetRef)) "" else linkRef.anchor).prefixWith("#")
            } else {
                if (prefix.isNotEmpty() && targetRef.isUnderWikiDir) {
                    // if the prefix starts with the wiki dir change it to the generic wiki used in links
                    val wikiDirName = targetRef.wikiDir.substring(targetRef.mainRepoDir.length + 1).suffixWith('/')
                    if (containingFile.isUnderWikiDir && prefix.startsWith(wikiDirName)) prefix = "wiki/" + prefix.substring(wikiDirName.length)
                    else if (!containingFile.isUnderWikiDir && prefix.startsWith("../../" + wikiDirName)) prefix = "../../wiki/" + prefix.substring(("../../" + wikiDirName).length)
                }

                val selfRef = isSelfRef(linkRef, targetRef, withExtForWikiPage)
                val optimizedAnchor = (anchor ?: optimizedLinkAnchor(linkRef, targetRef, withExtForWikiPage)).prefixWith('#')

                if (targetRef.isWikiPage) {
                    if (selfRef) return optimizedAnchor
                    else {
                        val fileName = prefix.suffixWith('/') + if (!withExtForWikiPage) (if (targetRef.isWikiHomePage) "" else targetRef.fileNameNoExt) else targetRef.fileName
                        return linkRef.fileToLink(fileName).removeSuffix("/") + optimizedAnchor
                    }
                } else {
                    if (selfRef) return optimizedAnchor
                    else return linkRef.fileToLink(prefix.suffixWith('/') + targetRef.fileName) + optimizedAnchor
                }
            }
        } else if (targetRef.isURI) {
            // convert git hub links to relative links
            val remoteUrl = projectResolver.getGitHubRepo(linkRef.containingFile)?.gitHubBaseUrl
            if (remoteUrl != null) {
                assert(remoteUrl.startsWith("http://", "https://"), { "remote vcsRepoBase has to start with http:// or https://, instead got ${remoteUrl}" })

                if (targetRef.path.startsWith(remoteUrl.suffixWith('/'))) {
                    val fileName = targetRef.filePath.substring(remoteUrl.suffixWith('/').length)
                    if (fileName in GITHUB_LINKS) {
                        return when {
                            containingFile.isWikiHomePage -> fileName
                            containingFile.isUnderWikiDir -> "../" + fileName
                            else -> PathInfo.relativePath(containingFile.path, projectBasePath.suffixWith('/'), withPrefix = true) + "../../" + fileName
                        }
                    } else {
                        // TEST: conversion of remote links to link addresses for all files and from all source files wiki/Home.md, wiki/normal-file.md and Readme.md
                        if (fileName.startsWith("wiki/")) {
                            // trying for wiki page
                            val filePath = when {
                                containingFile.isWikiHomePage -> fileName
                                containingFile.isUnderWikiDir -> fileName.substring("wiki/".length)
                                else -> PathInfo.relativePath(containingFile.path, projectBasePath.suffixWith('/'), withPrefix = true).suffixWith("/") + "../../" + fileName
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
                                        else -> PathInfo.relativePath(containingFile.path, projectBasePath.suffixWith('/'), withPrefix = true).suffixWith("/") + fileNamePart
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
        return WikiLinkRef.fileAsLink(fileName).equals(WikiLinkRef.fileAsLink(wikiLinkAddress), ignoreCase)
    }
}

