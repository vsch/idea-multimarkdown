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
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.vladsch.idea.multimarkdown.MultiMarkdownFileTypeFactory
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin
import org.apache.log4j.Logger

open class PathInfo(fullPath: String) : Comparable<PathInfo> {
    protected val fullPath: String
    protected val nameStart: Int
    protected val nameEnd: Int
    protected val mainRepoDirEnd: Int
    protected val wikiHomeDirEnd: Int

    init {
        var cleanPath = cleanFullPath(fullPath)

        val lastSep = cleanPath.lastIndexOf('/')
        this.nameStart = if (lastSep < 0) 0 else if (lastSep < cleanPath.lastIndex) lastSep + 1 else lastSep

        val extStart = cleanPath.lastIndexOf('.')
        this.nameEnd = if (extStart <= nameStart) cleanPath.length else extStart

        // gitHub wiki home will be like ..../dirname/dirname.wiki
        var wikiHomeEnd = cleanPath.lastIndexOf(WIKI_HOME_EXTENSION + "/", nameStart)

        if (wikiHomeEnd >= nameStart || wikiHomeEnd < 0) wikiHomeEnd = 0
        var projHomeEnd = 0

        if (wikiHomeEnd > 0) {
            val wikiDirNameStart = cleanPath.lastIndexOf('/', wikiHomeEnd)
            if (wikiDirNameStart > 1) {
                val wikiDirName = cleanPath.substring(wikiDirNameStart + 1, wikiHomeEnd)
                // now previous to start has to be the same directory
                val mainProjDirStart = cleanPath.lastIndexOf('/', wikiDirNameStart - 1)
                if (mainProjDirStart >= 0) {
                    val mainProjDirName = cleanPath.substring(mainProjDirStart + 1, wikiDirNameStart)
                    if (mainProjDirName == wikiDirName) {
                        projHomeEnd = wikiDirNameStart
                        wikiHomeEnd += WIKI_HOME_EXTENSION.length
                    } else {
                        wikiHomeEnd = 0
                    }
                } else {
                    wikiHomeEnd = 0
                }
            } else {
                wikiHomeEnd = 0
            }
        }

        this.fullPath = cleanPath

        this.wikiHomeDirEnd = wikiHomeEnd
        this.mainRepoDirEnd = projHomeEnd
    }

    val isUnderWikiDir: Boolean
        get() = wikiHomeDirEnd > 0

    val isWikiPage: Boolean
        get() = isUnderWikiDir && isWikiPageExt

    val isWikiHomePage: Boolean
        get() = isWikiPage && WIKI_HOME_FILENAME == fileNameNoExt

    val wikiDir: String
        get() = if (wikiHomeDirEnd <= 0) EMPTY_STRING else filePath.substring(0, wikiHomeDirEnd)

    val mainRepoDir: String
        get() = if (mainRepoDirEnd <= 0) EMPTY_STRING else filePath.substring(0, mainRepoDirEnd)

    val pathFromWikiDir: String
        get() = if (wikiHomeDirEnd <= 0) filePath else filePath.substring(wikiHomeDirEnd + 1)

    val pathFromMainRepoDir: String
        get() = if (mainRepoDirEnd <= 0) filePath else filePath.substring(mainRepoDirEnd + 1)

    val upDirectoriesToWikiHome: Int by lazy {
        if (wikiHomeDirEnd <= 0 || wikiHomeDirEnd == filePath.length) 0
        else {
            var pos = wikiHomeDirEnd
            var upDirs = 0
            while (pos < filePath.length) {
                pos = filePath.indexOf('/', pos)
                if (pos < 0) break;

                upDirs++
                pos++
            }

            upDirs
        }
    }

    override fun compareTo(other: PathInfo): Int = fullPath.compareTo(other.fullPath)
    override fun toString(): String = fullPath

    val ext: String
        get() = if (nameEnd + 1 >= fullPath.length) EMPTY_STRING else fullPath.substring(nameEnd + 1, fullPath.length)

    val hasExt: Boolean
        get() = nameEnd + 1 < fullPath.length

    fun isExtIn(ignoreCase: Boolean = true, vararg extList: String): Boolean = isExtIn(ext, ignoreCase, *extList)

    val isImageExt: Boolean
        get() = isImageExt(ext)

    val isMarkdownExt: Boolean
        get() = isMarkdownExt(ext)

    val isWikiPageExt: Boolean
        get() = isWikiPageExt(ext)

    open val filePath: String
        get() = fullPath

    open val filePathNoExt: String
        get() = if (nameEnd >= fullPath.length) fullPath else fullPath.substring(0, nameEnd)

    val path: String
        get() = if (nameStart == 0) EMPTY_STRING else fullPath.substring(0, nameStart)

    val fileName: String
        get() = if (nameStart == 0) fullPath else fullPath.substring(nameStart, fullPath.length)

    fun contains(c: Char, ignoreCase: Boolean = false): Boolean {
        return fullPath.contains(c, ignoreCase)
    }

    fun contains(c: String, ignoreCase: Boolean = false): Boolean {
        return fullPath.contains(c, ignoreCase)
    }

    fun containsSpaces(): Boolean {
        return contains(' ')
    }

    fun containsAnchor(): Boolean {
        return contains('#')
    }

    fun pathContains(c: Char, ignoreCase: Boolean = false): Boolean = path.contains(c, ignoreCase)
    fun pathContains(c: String, ignoreCase: Boolean = false): Boolean = path.contains(c, ignoreCase)
    fun pathContainsSpaces(): Boolean = pathContains(' ')
    fun pathContainsAnchor(): Boolean = pathContains('#')

    fun fileNameContains(c: Char, ignoreCase: Boolean = false): Boolean = fileName.contains(c, ignoreCase)
    fun fileNameContains(c: String, ignoreCase: Boolean = false): Boolean = fileName.contains(c, ignoreCase)
    fun fileNameContainsSpaces(): Boolean = fileNameContains(' ')
    fun fileNameContainsAnchor(): Boolean = fileNameContains('#')

    val fileNameNoExt: String
        get() = if (nameStart == 0 && nameEnd >= fullPath.length) fullPath else fullPath.substring(nameStart, nameEnd)

    open val isEmpty: Boolean
        get() = fullPath.isEmpty()

    val isRoot: Boolean
        get() = fullPath == "/"

    open val isRelative: Boolean
        get() = isRelative(fullPath)

    open val isExternal: Boolean
        get() = isExternal(fullPath)

    open val isURI: Boolean
        get() = isURI(fullPath)

    open val isAbsolute: Boolean
        get() = isAbsolute(fullPath)

    fun withExt(ext: String?): PathInfo = if (ext == null || isEmpty || this.ext == ext) this else PathInfo(filePathNoExt + ext.startWith('.'))
    fun append(vararg parts: String): PathInfo = PathInfo.append(fullPath, *parts)

    companion object {
        private val logger = Logger.getLogger(PathInfo::class.java)

        @JvmStatic val EMPTY_STRING = ""

        @JvmStatic val WIKI_PAGE_EXTENSION = ".md"
        @JvmStatic val WIKI_HOME_EXTENSION = ".wiki"
        @JvmStatic val WIKI_HOME_FILENAME = "Home"

        @JvmStatic val IMAGE_EXTENSIONS = arrayOf("png", "jpg", "jpeg", "gif")
        @JvmStatic val MARKDOWN_EXTENSIONS = MultiMarkdownFileTypeFactory.EXTENSIONS
        @JvmStatic val WIKI_PAGE_EXTENSIONS = MultiMarkdownFileTypeFactory.EXTENSIONS

        @JvmStatic val EXTERNAL_PREFIXES = arrayOf("http://", "ftp://", "https://", "mailto:")
        @JvmStatic val URI_PREFIXES = arrayOf("file://", *EXTERNAL_PREFIXES)
        @JvmStatic val ABSOLUTE_PREFIXES = arrayOf("/", *URI_PREFIXES)
        @JvmStatic val RELATIVE_PREFIXES = arrayOf("#")

        @JvmStatic fun isRelative(fullPath: String?): Boolean = fullPath == null || fullPath.startsWith(*RELATIVE_PREFIXES) || !isAbsolute(fullPath)
        @JvmStatic fun isExternal(href: String?): Boolean = href != null && href.startsWith(*EXTERNAL_PREFIXES)
        @JvmStatic fun isURI(href: String?): Boolean = href != null && href.startsWith(*URI_PREFIXES)
        @JvmStatic fun isAbsolute(href: String?): Boolean = href != null && href.startsWith(*ABSOLUTE_PREFIXES)

        @JvmStatic fun append(fullPath: String?, vararg parts: String): PathInfo {
            var path: String = cleanFullPath(fullPath)

            for (part in parts) {
                var cleanPart = part.removePrefix("/").removeSuffix("/")
                if (cleanPart != "..") cleanPart = cleanPart.removeSuffix(".")

                if (cleanPart !in arrayOf("", ".")) {
                    if (cleanPart == "..") {
                        path = PathInfo(path).path.removeSuffix("/")
                    } else {
                        if (path.isEmpty() || !path.endsWith('/')) path += '/'
                        path += cleanPart
                    }
                }
            }
            return PathInfo(path)
        }

        @JvmStatic fun isExtIn(ext: String, ignoreCase: Boolean = true, vararg extList: String): Boolean {
            for (listExt in extList) {
                if (listExt.equals(ext, ignoreCase)) return true
            }
            return false
        }

        @JvmStatic fun isImageExt(ext: String, ignoreCase: Boolean = true): Boolean = isExtIn(ext, ignoreCase, *IMAGE_EXTENSIONS)
        @JvmStatic fun isMarkdownExt(ext: String, ignoreCase: Boolean = true): Boolean = isExtIn(ext, ignoreCase, *MARKDOWN_EXTENSIONS)
        @JvmStatic fun isWikiPageExt(ext: String, ignoreCase: Boolean = true): Boolean = isExtIn(ext, ignoreCase, *WIKI_PAGE_EXTENSIONS)
        @JvmStatic fun removeDotDirectory(path: String?): String = path.orEmpty().replace("/./", "/").removePrefix("./")

        @JvmStatic fun cleanFullPath(fullPath: String?): String {
            var cleanPath = removeDotDirectory(fullPath)
            if (!cleanPath.endsWith("//")) cleanPath = cleanPath.removeSuffix("/")
            return cleanPath.removeSuffix(".")
        }
    }
}

open class FileInfo(val virtualFile: VirtualFile, val project: Project) : PathInfo(virtualFile.path) {

    constructor(psiFile: PsiFile) : this(psiFile.virtualFile, psiFile.project)

    val psiFile: PsiFile? by lazy { PsiManager.getInstance(project).findFile(virtualFile) }

    val exists: Boolean
        get() = virtualFile.exists()

    val gitHubRepo: GitHubRepo? by lazy {
        MultiMarkdownPlugin.getProjectComponent(project)?.getGitHubRepo(path)
    }

    val isUnderVcs: Boolean
        get() {
            val status = FileStatusManager.getInstance(project).getStatus(virtualFile)
            val id = status.id
            val fileStatus = status == FileStatus.DELETED || status == FileStatus.ADDED || status == FileStatus.UNKNOWN || status == FileStatus.IGNORED || id.startsWith("IGNORE")
            return !fileStatus
        }

    val gitHubRepoPath: String?
        get() = gitHubRepo?.basePath
}

open class PathInfoList : IndexedList<String, PathInfo>() {
    override fun elemKey(item: PathInfo): String {
        return item.fileNameNoExt.toLowerCase();
    }
}

