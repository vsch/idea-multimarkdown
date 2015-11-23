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
    companion object {
        @JvmStatic val GITHUB_WIKI_HOME_DIRNAME = "wiki"
        @JvmStatic val GITHUB_WIKI_HOME_FILENAME = "Home"
        @JvmStatic val GITHUB_ISSUES_NAME = "issues"
        @JvmStatic val GITHUB_GRAPHS_NAME = "graphs"
        @JvmStatic val GITHUB_PULSE_NAME = "pulse"
        @JvmStatic val GITHUB_PULLS_NAME = "pulls"

        @JvmStatic val GITHUB_LINKS = arrayOf(GITHUB_WIKI_HOME_DIRNAME, GITHUB_ISSUES_NAME, GITHUB_GRAPHS_NAME, GITHUB_PULSE_NAME, GITHUB_PULLS_NAME)
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

    override fun linkAddress(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String?): String {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return ContextImpl(this, linkRef.containingFile).linkAddress(linkRef, targetRef, withExtForWikiPage, branchOrTag)
    }

    class ContextImpl(resolver: GitHubLinkResolver, containingFile: FileRef, options: Int = 0, inList: List<FileRef>? = null, branchOrTag: String? = null) : LinkResolver.Context(resolver, containingFile, options, inList, branchOrTag) {
        val projectResolver: ProjectResolver = resolver.projectResolver
        val projectBasePath = resolver.projectBasePath
        val project: Project? = resolver.project

        override fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, branchOrTag: String?): Boolean {
            val linkRefText = relativePath(linkRef, targetRef, !(linkRef is WikiLinkRef || targetRef.isWikiPage && !linkRef.hasExt), branchOrTag)
            return linkRef.filePath.equals(linkRefText, ignoreCase = true)
        }

        override fun resolve(linkRef: LinkRef, options: Int): PathInfo? {
            // TODO: if only want external, then can try to resolve external links to local file refs if they map, for that need to parse the
            // UrlLinkRef's file path and have GitHubRepo reverse the href to local path

            if (linkRef.isRemote) return if (wantRemote(options)) linkRef else null

            if (linkRef.isSelfAnchor) {
                if (wantLocal(options)) return linkRef.containingFile

                // wantRemote only, so need href to containing file, we get it from GitHub if it exists
                val fileRef = linkRef.containingFile
                if (projectResolver.isUnderVcs(fileRef)) {
                    val withExt = !fileRef.isWikiPage && fileRef.hasExt
                    val gitHubRepoHref = projectResolver.repoUrlFor(fileRef, withExt, linkRef.anchor)

                    assert(gitHubRepoHref != null && PathInfo.isRemote(gitHubRepoHref), { "Expected external href, got $gitHubRepoHref" })
                    if (gitHubRepoHref != null) return LinkRef.parseLinkRef(linkRef.containingFile, gitHubRepoHref)
                }
                return null
            }

            var targetRef: PathInfo = linkRef

            if (!linkRef.isAbsolute) {
                // resolve the relative link as per requested options
                if (linkRef is WikiLinkRef && !wantLooseMatch(options) && linkRef.hasExt) return null  // wiki links don't resolve with extensions

                val linkRefMatcher = LinkRefMatcher(linkRef, projectBasePath, wantLooseMatch(options) || linkRef.isEmpty)
                val matches = getMatchedRefs(linkRef, linkRefMatcher.patternText(), options, inList)
                var resolvedRef = (if (matches.size > 0) matches[0] else null) ?: return null
                targetRef = resolvedRef
            }

            if (targetRef.isAbsolute) {
                return when {
                    targetRef.isRemote -> if (wantRemote(options)) targetRef else null
                    targetRef.isLocal && targetRef.isURI && targetRef is UrlLinkRef -> if (!wantLocal(options) || project == null) null else targetRef.virtualFileRef(project)
                    else -> targetRef
                }
            }

            return null
        }

        override fun multiResolve(linkRef: LinkRef, options: Int): List<PathInfo> {
            if (linkRef is WikiLinkRef && !wantLooseMatch(options) && linkRef.hasExt) return ArrayList()  // wiki links don't resolve with extensions
            val linkRefMatcher = LinkRefMatcher(linkRef, projectBasePath, wantLooseMatch(options) || linkRef.isEmpty)
            return getMatchedRefs(linkRef, linkRefMatcher.patternText(), options, inList)
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

        fun getMatchedRefs(linkRef: LinkRef, matchPattern: String?, options: Int = this.options, fromList: List<PathInfo>?): List<PathInfo> {
            // process the files that match the pattern and put them in the list
            val matches = ArrayList<PathInfo>()

            if (matchPattern == null) return matches

            val wikiMatch = matchPattern.toRegex(RegexOption.IGNORE_CASE)
            val linkMatch = if (wantLooseMatch(options)) wikiMatch else matchPattern.toRegex()

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
                    return remoteMatches
                } else {
                    matches.addAll(remoteMatches)
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
                filePathInfo = PathInfo.append(fileRef.path, "blob", branchOrTag ?: "master")
            }
            return filePathInfo
        }

        override fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String?): String {
            val containingFilePath = logicalRemotePath(containingFile, false, true, linkRef is ImageLinkRef, branchOrTag).filePath.endWith('/')
            val targetFilePath = logicalRemotePath(targetRef, withExtForWikiPage, false, linkRef is ImageLinkRef, branchOrTag).filePath.endWith('/')
            return PathInfo.relativePath(containingFilePath, targetFilePath, true)
        }

        override fun linkAddress(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String?): String {
            var prefix = relativePath(linkRef, targetRef, withExtForWikiPage, branchOrTag)

            if (linkRef is WikiLinkRef) {
                return prefix.endWith('/') + targetRef.fileNameNoExt.replace('-', ' ')
            } else {
                if (prefix.isNotEmpty() && targetRef.isUnderWikiDir) {
                    // if the prefix starts with the wiki dir change it to the generic wiki used in links
                    val wikiDirName = targetRef.wikiDir.substring(targetRef.mainRepoDir.length + 1).endWith('/')
                    if (prefix.startsWith(wikiDirName)) prefix = "wiki/" + prefix.substring(wikiDirName.length)
                }

                if (targetRef.isWikiPage) {
                    return prefix.endWith('/') + if (!withExtForWikiPage) targetRef.fileNameNoExt.replace(" ", "%20") else targetRef.fileName.replace(" ", "%20")
                } else {
                    return prefix.endWith('/') + targetRef.fileName.replace(" ", "%20").replace("#", "%23")
                }
            }
        }
    }
}

