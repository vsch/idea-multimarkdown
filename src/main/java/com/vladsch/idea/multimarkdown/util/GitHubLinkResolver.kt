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
import kotlin.text.Regex


class GitHubLinkResolver(project: Project?, containingFile: FileRef, basePath: String? = null) : LinkResolver(project, containingFile, basePath) {
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
        val project: Project? = resolver.project
        val projectBasePath = resolver.projectBasePath

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

                // wantExternal only, so need href to containing file, we get it from GitHub if it exists
                if (resolver.project != null) {
                    val virtualFileRef = linkRef.containingFile.virtualFileRef(resolver.project)
                    if (virtualFileRef != null && virtualFileRef.isUnderVcs) {
                        val withExt = !virtualFileRef.isWikiPage && virtualFileRef.hasExt
                        val gitHubRepoHref = virtualFileRef.gitHubRepo?.repoUrlFor(virtualFileRef.virtualFile, withExt, linkRef.anchor)

                        assert(gitHubRepoHref == null || PathInfo.isRemote(gitHubRepoHref), { "Expected external href, got $gitHubRepoHref" })
                        if (gitHubRepoHref != null) return LinkRef.parseLinkRef(linkRef.containingFile, gitHubRepoHref)
                    }
                }
                return null
            }

            var targetRef: PathInfo = linkRef

            if (!linkRef.isAbsolute) {
                // resolve the relative link as per requested options
                if (linkRef is WikiLinkRef && !wantLooseMatch(options) && linkRef.hasExt) return null  // wiki links don't resolve with extensions

                val linkRefMatcher = LinkRefMatcher(linkRef, projectBasePath, wantLooseMatch(options) || linkRef.isEmpty)
                val matches = getMatchedFiles(linkRef, linkRefMatcher.patternRegex(), options, inList)
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
            return getMatchedFiles(linkRef, linkRefMatcher.patternRegex(), options, inList)
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

        fun getMatchedFiles(linkRef: LinkRef, matchPattern: Regex?, options: Int = this.options, fromList: List<FileRef>?): List<FileRef> {
            // process the files that match the pattern and put them in the list
            val matches = ArrayList<FileRef>()

            if (matchPattern == null) return matches

            if (fromList == null) {
                val targetFileTypes = getTargetFileTypes(linkRef)
                if (targetFileTypes.isEmpty() || resolver.project == null) {
                    return ArrayList(0)
                } else {
                    val project: Project = resolver.project
                    FileBasedIndex.getInstance().getFilesWithKey(FilenameIndex.NAME, targetFileTypes, { file ->
                        if (file.path.matches(matchPattern)) {
                            matches.add(VirtualFileRef(file, project))
                        }
                        true
                    }, GlobalSearchScope.projectScope(project))
                }
            } else {
                for (fileRef in fromList) {
                    // here we can have both local and external we skip external since we don't resolve them yet
                    if (fileRef.isLocal) {
                        if (fileRef.filePath.matches(matchPattern)) {
                            matches.add(fileRef)
                        }
                    }
                }
            }
            if (linkRef !is ImageLinkRef && matches.size > 1) matches.sort { self, other -> self.compareTo(other) }
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
            val prefix = relativePath(linkRef, targetRef, withExtForWikiPage, branchOrTag)

            if (linkRef is WikiLinkRef) {
                return prefix.endWith('/') + targetRef.fileNameNoExt.replace('-', ' ')
            } else {
                if (targetRef.isWikiPage) {
                    return prefix.endWith('/') + if (!withExtForWikiPage) targetRef.fileNameNoExt.replace(" ", "%20") else targetRef.fileName.replace(" ", "%20")
                } else {
                    return prefix.endWith('/') + targetRef.fileName.replace(" ", "%20").replace("#", "%23")
                }
            }
        }
    }
}

