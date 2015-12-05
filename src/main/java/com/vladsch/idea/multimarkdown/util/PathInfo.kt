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
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiFile
import com.vladsch.idea.multimarkdown.MultiMarkdownFileTypeFactory
import org.apache.log4j.Logger

open class PathInfo(fullPath: String) : Comparable<PathInfo> {
    protected val _fullPath: String
    protected val _nameStart: Int
    protected val _nameEnd: Int

    constructor(__virtualFile: VirtualFile) : this(__virtualFile.path)

    constructor(psiFile: PsiFile) : this(psiFile.virtualFile.path)

    init {
        var cleanPath = cleanFullPath(fullPath)

        val lastSep = cleanPath.lastIndexOf('/')
        this._nameStart = if (lastSep < 0) 0 else if (lastSep < cleanPath.lastIndex) lastSep + 1 else lastSep

        val extStart = cleanPath.lastIndexOf('.')
        this._nameEnd = if (extStart <= _nameStart) cleanPath.length else extStart
        this._fullPath = cleanPath
    }

    override fun compareTo(other: PathInfo): Int = _fullPath.compareTo(other._fullPath)
    override fun toString(): String = _fullPath

    val filePath: String
        get() = _fullPath

    val filePathNoExt: String
        get() = if (_nameEnd >= _fullPath.length) _fullPath else _fullPath.substring(0, _nameEnd)

    val path: String
        get() = if (_nameStart == 0) EMPTY_STRING else _fullPath.substring(0, _nameStart)

    val fileName: String
        get() = if (_nameStart == 0) _fullPath else _fullPath.substring(_nameStart, _fullPath.length)

    val fileNameNoExt: String
        get() = if (_nameStart == 0 && _nameEnd >= _fullPath.length) _fullPath else _fullPath.substring(_nameStart, _nameEnd)

    val ext: String
        get() = if (_nameEnd + 1 >= _fullPath.length) EMPTY_STRING else _fullPath.substring(_nameEnd + 1, _fullPath.length)

    val extWithDot: String
        get() = if (_nameEnd >= _fullPath.length) EMPTY_STRING else _fullPath.substring(_nameEnd, _fullPath.length)

    val hasExt: Boolean
        get() = _nameEnd + 1 < _fullPath.length

    fun isExtIn(ignoreCase: Boolean = true, vararg extList: String): Boolean = isExtIn(ext, ignoreCase, *extList)

    val isImageExt: Boolean
        get() = hasExt && isImageExt(ext)

    val isMarkdownExt: Boolean
        get() = hasExt && isMarkdownExt(ext)

    val isWikiPageExt: Boolean
        get() = hasExt && isWikiPageExt(ext)

    fun contains(c: Char, ignoreCase: Boolean = false): Boolean {
        return _fullPath.contains(c, ignoreCase)
    }

    fun contains(c: String, ignoreCase: Boolean = false): Boolean {
        return _fullPath.contains(c, ignoreCase)
    }

    fun containsSpaces(): Boolean {
        return contains(' ')
    }

    fun containsAnchor(): Boolean {
        return contains('#')
    }

    fun pathContains(c: Char): Boolean = path.contains(c, false)
    fun pathContains(c: Char, ignoreCase: Boolean): Boolean = path.contains(c, ignoreCase)
    fun pathContains(c: String): Boolean = path.contains(c, false)
    fun pathContains(c: String, ignoreCase: Boolean): Boolean = path.contains(c, ignoreCase)
    fun pathContainsSpaces(): Boolean = pathContains(' ')
    fun pathContainsAnchor(): Boolean = pathContains('#')

    fun fileNameContains(c: Char): Boolean = fileName.contains(c, false)
    fun fileNameContains(c: Char, ignoreCase: Boolean): Boolean = fileName.contains(c, ignoreCase)
    fun fileNameContains(c: String): Boolean = fileName.contains(c, false)
    fun fileNameContains(c: String, ignoreCase: Boolean): Boolean = fileName.contains(c, ignoreCase)
    fun fileNameContainsSpaces(): Boolean = fileNameContains(' ')
    fun fileNameContainsAnchor(): Boolean = fileNameContains('#')

    open val isEmpty: Boolean
        get() = _fullPath.isEmpty()

    val isRoot: Boolean
        get() = _fullPath == "/"

    // these ones need resolving to absolute reference
    val isRelative: Boolean
        get() = isRelative(_fullPath)

    // these ones resolve to local references, if they resolve
    val isLocal: Boolean
        get() = isLocal(_fullPath)

    // these ones resolve to external references, if they resolve
    val isExternal: Boolean
        get() = isExternal(_fullPath)

    // these ones are URI prefixed
    val isURI: Boolean
        get() = isURI(_fullPath)

    // these ones are not relative, ie don't need resolving
    val isAbsolute: Boolean
        get() = isAbsolute(_fullPath)

    fun withExt(ext: String?): PathInfo = if (ext == null || isEmpty || this.ext == ext.removePrefix(".")) this else PathInfo(filePathNoExt + ext.prefixWith('.'))
    open fun append(vararg parts: String): PathInfo = PathInfo.appendParts(_fullPath, *parts, construct = ::PathInfo)
    open fun append(parts: Collection<String>): PathInfo = PathInfo.appendParts(_fullPath, parts, construct = ::PathInfo)
    open fun append(parts: Sequence<String>): PathInfo = PathInfo.appendParts(_fullPath, parts, construct = ::PathInfo)

    open fun projectFileRef(project: Project): ProjectFileRef? {
        val virtualFile = if (!isAbsolute || !isLocal) null else VirtualFileManager.getInstance().findFileByUrl(_fullPath.prefixWith("file://"))
        val projectFileRef = if (virtualFile == null) null else ProjectFileRef(virtualFile, project);
        if (projectFileRef != null && this is LinkRef && targetRef != null && targetRef.isRawFile) {
           projectFileRef.isRawFile = true
        }
        return projectFileRef
    }

     open val virtualFile by lazy {
        if (!isAbsolute || !isLocal) null
        else VirtualFileManager.getInstance().findFileByUrl(filePath.prefixWith("file://"))
    }

    fun canRenameFileTo(newName: String): Boolean {
        val newPathInfo = append("..", newName)
        val virtualFile = newPathInfo.virtualFile
        return this.virtualFile != null && (this.virtualFile as VirtualFile).exists() && virtualFile != null && !virtualFile.exists() && virtualFile.parent != null
    }

    fun canCreateFile(): Boolean {
        val pathInfo = PathInfo(path)
        val parentDir = pathInfo.virtualFile
        return parentDir != null
    }

    companion object {
        private val logger = Logger.getLogger(PathInfo::class.java)

        @JvmStatic @JvmField val EMPTY_STRING = ""

        @JvmStatic @JvmField val WIKI_PAGE_EXTENSION = ".md"
        @JvmStatic @JvmField val WIKI_HOME_DIR_EXTENSION = ".wiki"
        @JvmStatic @JvmField val WIKI_HOME_FILENAME = "Home"

        @JvmStatic @JvmField val IMAGE_EXTENSIONS = arrayOf("png", "jpg", "jpeg", "gif")
        @JvmStatic @JvmField val MARKDOWN_EXTENSIONS = MultiMarkdownFileTypeFactory.EXTENSIONS
        @JvmStatic @JvmField val WIKI_PAGE_EXTENSIONS = MultiMarkdownFileTypeFactory.EXTENSIONS

        @JvmStatic @JvmField val EXTERNAL_PREFIXES = arrayOf("http://", "ftp://", "https://", "mailto:")
        @JvmStatic @JvmField val URI_PREFIXES = arrayOf("file://", *EXTERNAL_PREFIXES)
        @JvmStatic @JvmField val RELATIVE_PREFIXES = arrayOf<String>()
        @JvmStatic @JvmField val LOCAL_PREFIXES = arrayOf("file:", "/", *RELATIVE_PREFIXES)
        @JvmStatic @JvmField val ABSOLUTE_PREFIXES = arrayOf("/", *URI_PREFIXES)

        // true if needs resolving to absolute reference
        @JvmStatic fun isRelative(fullPath: String?): Boolean = fullPath != null && !isAbsolute(fullPath)

        // true if resolves to external
        @JvmStatic fun isExternal(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*EXTERNAL_PREFIXES)

        // true if resolves to local, if it resolves
        @JvmStatic fun isLocal(fullPath: String?): Boolean = fullPath != null && (fullPath.startsWith(*LOCAL_PREFIXES) || isRelative(fullPath))

        // true if it is a URI
        @JvmStatic fun isURI(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*URI_PREFIXES)

        // true if it is already an absolute ref, no need to resolve relative, just see if it maps
        @JvmStatic fun isAbsolute(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*ABSOLUTE_PREFIXES)

        @JvmStatic fun appendParts(fullPath: String?, vararg parts: String): PathInfo {
            return appendParts(fullPath, parts.asSequence(), ::PathInfo);
        }

        @JvmStatic fun appendParts(fullPath: String?, parts: Collection<String>): PathInfo {
            return appendParts(fullPath, parts.asSequence(), ::PathInfo)
        }

        @JvmStatic fun appendParts(fullPath: String?, parts: Sequence<String>): PathInfo {
            return appendParts(fullPath, parts, ::PathInfo)
        }

        @JvmStatic fun <T : PathInfo> appendParts(fullPath: String?, vararg parts: String, construct: (fullPath: String) -> T): T {
            return appendParts(fullPath, parts.asSequence(), construct);
        }

        @JvmStatic fun <T : PathInfo> appendParts(fullPath: String?, parts: Collection<String>, construct: (fullPath: String) -> T): T {
            return appendParts(fullPath, parts.asSequence(), construct)
        }

        @JvmStatic fun <T : PathInfo> appendParts(fullPath: String?, parts: Sequence<String>, construct: (fullPath: String) -> T): T {
            var path: String = cleanFullPath(fullPath)

            for (mainPart in parts) {
                for (part in mainPart.split('/')) {
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
            }
            return construct(path)
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
            var cleanPath = removeDotDirectory(fullPath?.trim())
            if (!cleanPath.endsWith("//")) cleanPath = cleanPath.removeSuffix("/")
            return cleanPath.removeSuffix(".")
        }

        @JvmStatic fun relativePath(fromPath: String, toPath: String, withPrefix: Boolean = true): String {
            var lastSlash = -1
            val iMax = Math.min(fromPath.length, toPath.length) - 1
            for (i in  0..iMax) {
                if (fromPath[i] != toPath[i]) break
                if (fromPath[i] == '/') lastSlash = i
            }

            // for every dir in containingFilePath after lastSlash add ../ as the prefix
            if (withPrefix) {
                var prefix = "../".repeat(fromPath.count('/', lastSlash + 1))
                prefix += toPath.substring(lastSlash + 1)
                return prefix
            } else {
                return toPath.substring(lastSlash + 1)
            }
        }

        @JvmStatic fun fileNames(pathInfos: List<PathInfo>): Array<String> {
            val list = pathInfos.map { pathInfo -> pathInfo.fileName }
            return list.toTypedArray()
        }

        @JvmStatic fun fileNamesNoExt(pathInfos: List<PathInfo>): Array<String> {
            val list = pathInfos.map { pathInfo -> pathInfo.fileNameNoExt }
            return list.toTypedArray()
        }

        @JvmStatic fun removeDotDirectoriesPrefix(path: String?): String {
            if (path == null) return EMPTY_STRING
            var pos = 0
            var outPath = path
            while (true) {
                if (outPath.startsWith("../")) outPath = outPath.removeStart("../")
                else if (outPath.startsWith("./")) outPath = outPath.removeStart("./")
                else break
            }
            return cleanFullPath(outPath)
        }
    }
}

