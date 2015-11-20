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
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.containers.HashSet
import com.intellij.util.indexing.FileBasedIndex
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent
import org.apache.log4j.Logger
import org.intellij.images.fileTypes.ImageFileTypeManager

import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

class LinkResolver(val containingFile: PathInfo, val linkRef: FileLinkRef) {

    interface MatchFilter {
        enum class Result private constructor(internal var flags: Int) {
            SKIP(0), ADD(FLAGS_ADD), SKIP_STOP(FLAGS_STOP), ADD_STOP(FLAGS_ADD or FLAGS_STOP);

            val isStop: Boolean
                get() = (flags and FLAGS_STOP) != 0

            val isAdd: Boolean
                get() = (flags and FLAGS_ADD) != 0
        }

        fun process(resolver: LinkResolver, pathInfo: PathInfo): Result
    }

    interface MatchComparator {
        fun sort(resolver: LinkResolver, matches: ArrayList<FileReference>): ArrayList<FileReference>
        fun bestMatch(resolver: LinkResolver, matches: ArrayList<FileReference>): FileReference?
    }

    private val myProject = if (containingFile is FileInfo) containingFile.project else null

    private var exactMatches: PathInfoList? = null
    private var availableMatches: PathInfoList? = null

    val bestMatch: PathInfo? by lazy { loadBestMatch() }

    val project: Project?
        get() = if (myProject == null || myProject.isDisposed) null else myProject

    protected fun preMatchPattern(exactMatch: Boolean): Pattern {
        // return a regex that will match most loosely a file path to be used by this link
        val pattern: String
        val matchPattern: Pattern

        // we always match subdirectories for markdown and wiki's, even for exact match since if the destination is a wiki page then no directories will be used
        // image target types have no pattern subdirectories but use exact type
        var subDirPattern = "(?:.*)/"

        if (linkRef is WikiLinkRef) {
            // spaces match - and spaces, all subdirectories under Wiki Home match, only WIKI targets accepted, no case sensitivity
            if (exactMatch && linkRef.fileName.isNotEmpty()) {
                val filenamePattern = "\\Q" + linkRef.fileName.replace(" ", "-") + "\\E"
                val extensionPattern = ""
                val anchorPattern = ""
                pattern = "^\\Q" + containingFile.wikiDir + "\\E" + subDirPattern + filenamePattern + anchorPattern + extensionPattern + "$"
                matchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
            } else {
                val filenamePattern = if (linkRef.isEmpty) "(.*)" else "\\Q" + linkRef.fileNameNoExt.replace("-| ", "\\E(?:-| )\\Q") + "\\E"
                val extensionPattern = if (!linkRef.hasExt) "" else "(?:\\Q" + linkRef.ext.replace("-| ", "\\E(?:-| )\\Q") + "\\E)?"
                val anchorPattern = if (!linkRef.hasAnchor) "" else "(?:\\Q#" + linkRef.anchor.orEmpty().replace("-| ", "\\E(?:-| )\\Q") + "\\E)?"
                pattern = "^\\Q" + containingFile.wikiDir + "\\E" + subDirPattern + filenamePattern + anchorPattern + extensionPattern + "$"
                matchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
            }
        } else {
            // this is all other links, no space dash equivalence, %23 in link matches # in file, files under project or gitHubRepoHome will be matched
            // extension pattern is optional added to a list of all extensions for the targetType, with zero or none matching
            // anchor is an optional pattern right after the name so extensions follow, all subdirectories between project or repo home are optional
            // matching is not case sensitive even for exact match because the final could be a wiki page

            // project base, if none then file path
            var prefixPath = if (project != null && project!!.basePath != null) project!!.basePath else containingFile.filePath
            val typeExtensions = linkRef.linkExtensions
            var hadExtension = false
            var extensionPattern = ""

            for (ext in typeExtensions) {
                if (!extensionPattern.isEmpty()) extensionPattern += "|"
                if (!ext.isEmpty()) {
                    extensionPattern += "(?:\\Q" + ext.replace("%23", "#") + "\\E)"
                    if (ext.equals(linkRef.ext, ignoreCase = true)) hadExtension = true
                }
            }

            if (!hadExtension && linkRef.hasExt) {
                if (!extensionPattern.isEmpty()) extensionPattern += "|"
                extensionPattern += "(?:\\Q" + linkRef.ext.replace("%23", "#") + "\\E)"
            }

            if (!extensionPattern.isEmpty()) extensionPattern = "(?:$extensionPattern)"

            if (exactMatch && !linkRef.isEmpty) {
                val filenamePattern = "\\Q" + linkRef.fileNameNoExt.replace("%23", "#") + "\\E"
                val anchorPattern = ""

                // match all extensions for the type by default, if link ref has extension and must have it for exact match
                if (mustHaveExactExtension()) {
                    extensionPattern = if (!linkRef.hasExt) "" else "\\Q" + linkRef.ext.replace("%23", "#") + "\\E"
                } else {
                    extensionPattern += "?"
                }

                if (exactMatchNoWildcardSubDirs()) {
                    subDirPattern = ""
                    prefixPath = linkRef.filePath
                }

                // if no project or repo base path then prefix is the file path
                pattern = "^\\Q$prefixPath\\E$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"

                matchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
            } else {
                val filenamePattern = if (linkRef.isEmpty) "(.*)" else "\\Q" + linkRef.fileNameNoExt.replace("%23", "#") + "\\E"
                val anchorPattern = if (anchor.isEmpty()) "" else "(?:\\Q#$anchor\\E)?"

                if (!mustHaveNotExactExtension()) extensionPattern += "?"

                pattern = "^\\Q$prefixPath\\E$subDirPattern$filenamePattern$anchorPattern$extensionPattern$"
                matchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
            }
        }

        return matchPattern
    }

    protected fun getProjectFileType(targetFileType: String?): String {
        var targetFileType = targetFileType
        val project = project
        if (project == null || (targetFileType == null && targetType == TargetType.ANY)) return ""

        when (targetType) {
            LinkResolver.TargetType.WIKI, LinkResolver.TargetType.MARKDOWN -> targetFileType = MultiMarkdownFileType.INSTANCE.toString()

            LinkResolver.TargetType.IMAGE -> targetFileType = ImageFileTypeManager.getInstance().imageFileType.toString()

            LinkResolver.TargetType.ANY, else -> targetFileType = ""
        }

        return targetFileType
    }

    fun getAvailableMatches(): ArrayList<FileReference> {
        if (availableMatches == null) {
            availableMatches = getMatches(this, preMatchPattern(false), null, GITHUB_AVAILABLE_MATCH_FILTER)
        }
        return availableMatches
    }

    fun getExactMatches(): ArrayList<FileReference> {
        if (exactMatches == null) {
            exactMatches = getMatches(this, preMatchPattern(true), availableMatches, GITHUB_EXACT_MATCH_FILTER)
        }
        return exactMatches
    }

    private fun loadBestMatch(): PathInfo? {
        val matches = getMatches(this, preMatchPattern(true), if (exactMatches != null) exactMatches else availableMatches, GITHUB_BEST_MATCH_FILTER)
        val bestFileReference = GITHUB_MATCH_COMPARATOR.bestMatch(this, matches)
        if (bestFileReference != null) {
            return LinkReference(this, bestFileReference)
        }
        return null
    }

    fun getBestMatch(): LinkReference? {
        return bestMatch.get()
    }

    companion object {
        private val logger = Logger.getLogger(LinkResolver::class.java)
        private val FLAGS_STOP = 2
        private val FLAGS_ADD = 1

        protected val EMPTY_STRINGS = arrayOf<String>()

        fun getMatches(resolver: LinkResolver, matchPattern: Pattern, fromList: ArrayList<FileReference>?, matchFilter: MatchFilter?): ArrayList<FileReference> {
            val targetFileType = resolver.getProjectFileType("")

            // process the files that match the pattern and put them in the list
            val matches = ArrayList<FileReference>()
            if (fromList == null) {
                val project = resolver.project
                if (targetFileType.isEmpty() || project == null) {
                    return ArrayList(0)
                } else {
                    val targetFileTypes = HashSet<String>()
                    targetFileTypes.add(targetFileType)

                    if (matchFilter != null) {
                        FileBasedIndex.getInstance().getFilesWithKey(FilenameIndex.NAME, targetFileTypes, Processor<VirtualFile> { file ->
                            val filePath = file.path
                            val matcher = matchPattern.matcher(filePath)
                            if (matcher.matches()) {
                                val result = matchFilter.process(resolver, file, null)
                                if (result.isAdd) matches.add(FileReference(file, project))
                                return@Processor !result.isStop
                            }
                            true
                        }, GlobalSearchScope.projectScope(project))
                    } else {
                        FileBasedIndex.getInstance().getFilesWithKey(FilenameIndex.NAME, targetFileTypes, { file ->
                            val filePath = file.path
                            val matcher = matchPattern.matcher(filePath)
                            if (matcher.matches()) {
                                matches.add(FileReference(file, project))
                            }
                            true
                        }, GlobalSearchScope.projectScope(project))
                    }
                }
            } else {
                if (matchFilter != null) {
                    for (fileReference in fromList) {
                        val matcher = matchPattern.matcher(fileReference.fullFilePath)
                        if (matcher.matches()) {
                            assert(fileReference.virtualFile != null) { "FileReference already has non-null virtualFile field" }
                            val result = matchFilter.process(resolver, fileReference.virtualFile, fileReference)
                            if (result.isAdd) matches.add(fileReference)
                            if (result.isStop) break
                        }
                    }
                } else {
                    for (fileReference in fromList) {
                        val matcher = matchPattern.matcher(fileReference.fullFilePath)
                        if (matcher.matches()) {
                            matches.add(fileReference)
                        }
                    }
                }
            }
            return matches
        }

        // available filter will allow errors and warnings but keep all that can possibly be used as a link
        // sub-directory differences are ignored, as long as the link is reachable,
        // presence or absence of extension ignored
        // lack of url encoding for # in link ignored
        // space/dash equivalence for wiki
        // matching is not case sensitive even for non-wiki
        // will potentially match anchor from link in file name if that works
        // will match link with no extension to file with a compatible extension for the type
        // see getMatchPattern(false)
        internal val GITHUB_AVAILABLE_MATCH_FILTER: MatchFilter = object : MatchFilter {
            override fun process(resolver: LinkResolver, file: VirtualFile, fileReference: FileReference?): MatchFilter.Result {
                // todo: see if file can be processed
                return LinkResolver.MatchFilter.Result.ADD
            }
        }

        // exact filter will not allow errors, only warnings
        // used to get set of matching files for a link, that are matching on loose standards
        // sub-directory differences are ignored, as long as the link is reachable,
        // extension must match if present, ignored if not present
        // lack of url encoding for # in link ignored
        // space/dash equivalence for wiki
        // matching is not case sensitive even for non-wiki
        // will potentially match anchor
        // see getMatchPattern(false)
        internal val GITHUB_EXACT_MATCH_FILTER: MatchFilter = object : MatchFilter {
            override fun process(resolver: LinkResolver, file: VirtualFile, fileReference: FileReference?): MatchFilter.Result {
                return LinkResolver.MatchFilter.Result.ADD
            }
        }

        // best filter will filter out the best candidate our of a set
        // used to get the resolved reference
        internal val GITHUB_BEST_MATCH_FILTER: MatchFilter = object : MatchFilter {
            override fun process(resolver: LinkResolver, file: VirtualFile, fileReference: FileReference?): MatchFilter.Result {
                return LinkResolver.MatchFilter.Result.ADD
            }
        }

        val GITHUB_MATCH_COMPARATOR: MatchComparator = object : MatchComparator {
            override fun sort(resolver: LinkResolver, matches: ArrayList<FileReference>): ArrayList<FileReference> {
                return matches
            }

            override fun bestMatch(resolver: LinkResolver, matches: ArrayList<FileReference>): FileReference? {
                return if (matches.size > 0) matches[0] else null
            }
        }
    }
}
