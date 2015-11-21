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
import java.util.regex.Pattern
import kotlin.text.Regex


class GitHubLinkResolver(project: Project?, containingFile: FileRef) : LinkResolver(project, containingFile) {

    override fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, options: Int): Boolean {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return Context(this, linkRef, options).isResolvedTo(targetRef)
    }

    override fun resolve(linkRef: LinkRef, options: Int, inList: List<FileRef>?): LinkInfo? {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return Context(this, linkRef, options, inList).resolve()
    }

    override fun multiResolve(linkRef: LinkRef, options: Int, inList: List<FileRef>?): List<LinkInfo> {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return Context(this, linkRef, options, inList).multiResolve()
    }

    override fun isResolved(linkRef: LinkRef, options: Int, inList: List<FileRef>?): Boolean {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return Context(this, linkRef, options, inList).resolve() != null
    }

    override fun analyze(linkRef: LinkRef, targetRef: FileRef): MismatchReasons {
        assert(linkRef.containingFile == containingFile, { "likRef containingFile differs from LinkResolver containingFile, need new Resolver for each containing file" })
        return Context(this, linkRef).analyze()
    }
}

private class Context(val resolver: GitHubLinkResolver, val linkRef: LinkRef, val options: Int = 0, val inList: List<FileRef>? = null) {
    val project: Project? = resolver.project

    fun isResolvedTo(targetRef: FileRef): Boolean {
        throw UnsupportedOperationException()
    }

    fun resolve(): LinkInfo? {
        // TODO: if only want external, then can try to resolve external links to local file refs if they map, for that need to parse the
        // UrlLinkRef's file path and have GitHubRepo reverse the href to local path

        if (linkRef.isExternal) return if (LinkResolver.wantExternal(options)) linkRef else null

        if (linkRef.isSelfAnchor) {
            if (LinkResolver.wantLocal(options)) return linkRef.containingFile

            // wantExternal only, so need href to containing file, we get it from GitHub if it exists
            if (resolver.project != null) {
                val virtualFileRef = linkRef.containingFile.virtualFileRef(resolver.project)
                if (virtualFileRef != null && virtualFileRef.isUnderVcs) {
                    val withExt = !virtualFileRef.isWikiPage && virtualFileRef.hasExt
                    val gitHubRepoHref = virtualFileRef.gitHubRepo?.repoUrlFor(virtualFileRef.virtualFile, withExt, linkRef.anchor)

                    assert(gitHubRepoHref == null || LinkInfo.isExternal(gitHubRepoHref), { "Expected external href, got $gitHubRepoHref" })
                    if (gitHubRepoHref != null) return LinkRef.parseLinkRef(linkRef.containingFile, gitHubRepoHref)
                }
            }
            return null
        }

        var targetRef: LinkInfo = linkRef

        if (!linkRef.isAbsolute) {
            // TODO: resolve the relative link as per requested options
            var resolvedRef: LinkInfo? = null

            targetRef = resolvedRef as LinkInfo
        }

        if (targetRef.isAbsolute) {
            return when {
                targetRef.isExternal -> if (LinkResolver.wantExternal(options)) targetRef else null
                targetRef.isLocal && targetRef.isURI && targetRef is UrlLinkRef -> if (!LinkResolver.wantLocal(options) || project == null) null else targetRef.virtualFileRef(project)
                else -> null
            }
        }

        return null
    }

    fun multiResolve(): List<LinkInfo> {
        val linkRefMatcher = LinkRefMatcher(linkRef, project?.basePath, LinkResolver.wantLooseMatch(options) || linkRef.isEmpty)
        return getMatchedFiles(linkRefMatcher.patternRegex(), inList)
    }

    fun analyze(): MismatchReasons {
        throw UnsupportedOperationException()
    }

    protected fun getTargetFileTypes(): HashSet<String> {
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

    fun getMatchedFiles(matchPattern: Regex, fromList: List<FileRef>?): List<FileRef> {
        // process the files that match the pattern and put them in the list
        val matches = ArrayList<FileRef>()

        if (fromList == null) {
            val targetFileTypes = getTargetFileTypes()
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
                if (fileRef.filePath.matches(matchPattern)) {
                    matches.add(fileRef)
                }
            }
        }
        return matches
    }

}
