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

import com.vladsch.idea.multimarkdown.MultiMarkdownFileTypeFactory
import org.apache.log4j.Logger

open class PathInfo(fullPath: String) : Comparable<PathInfo> {
    protected val fullPath: String
    protected val nameStart: Int
    protected val nameEnd: Int

    init {
        var cleanPath = cleanFullPath(fullPath)

        val lastSep = cleanPath.lastIndexOf('/')
        this.nameStart = if (lastSep < 0) 0 else if (lastSep < cleanPath.lastIndex) lastSep + 1 else lastSep

        val extStart = cleanPath.lastIndexOf('.')
        this.nameEnd = if (extStart <= nameStart) cleanPath.length else extStart
        this.fullPath = cleanPath
    }

    override fun compareTo(other: PathInfo): Int = fullPath.compareTo(other.fullPath)
    override fun toString(): String = fullPath

    val filePath: String
        get() = fullPath

    val filePathNoExt: String
        get() = if (nameEnd >= fullPath.length) fullPath else fullPath.substring(0, nameEnd)

    val path: String
        get() = if (nameStart == 0) EMPTY_STRING else fullPath.substring(0, nameStart)

    val fileName: String
        get() = if (nameStart == 0) fullPath else fullPath.substring(nameStart, fullPath.length)

    val fileNameNoExt: String
        get() = if (nameStart == 0 && nameEnd >= fullPath.length) fullPath else fullPath.substring(nameStart, nameEnd)

    val ext: String
        get() = if (nameEnd + 1 >= fullPath.length) EMPTY_STRING else fullPath.substring(nameEnd + 1, fullPath.length)

    val hasExt: Boolean
        get() = nameEnd + 1 < fullPath.length

    fun isExtIn(ignoreCase: Boolean = true, vararg extList: String): Boolean = isExtIn(ext, ignoreCase, *extList)

    val isImageExt: Boolean
        get() = hasExt && isImageExt(ext)

    val isMarkdownExt: Boolean
        get() = hasExt && isMarkdownExt(ext)

    val isWikiPageExt: Boolean
        get() = hasExt && isWikiPageExt(ext)

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

    open val isEmpty: Boolean
        get() = fullPath.isEmpty()

    val isRoot: Boolean
        get() = fullPath == "/"

    // these ones need resolving to absolute reference
    open val isRelative: Boolean
        get() = isRelative(fullPath)

    // these ones resolve to local references, if they resolve
    open val isLocal: Boolean
        get() = isLocal(fullPath)

    // these ones resolve to external references, if they resolve
    open val isRemote: Boolean
        get() = isRemote(fullPath)

    // these ones are URI prefixed
    open val isURI: Boolean
        get() = isURI(fullPath)

    // these ones are not relative, ie don't need resolving
    open val isAbsolute: Boolean
        get() = isAbsolute(fullPath)

    fun withExt(ext: String?): PathInfo = if (ext == null || isEmpty || this.ext == ext) this else PathInfo(filePathNoExt + ext.startWith('.'))
    fun append(vararg parts: String): PathInfo = PathInfo.append(fullPath, *parts)
    fun append(parts: Collection<String>): PathInfo = PathInfo.append(fullPath, parts)
    fun append(parts: Sequence<String>): PathInfo = PathInfo.append(fullPath, parts)

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
        @JvmStatic val RELATIVE_PREFIXES = arrayOf<String>()
        @JvmStatic val LOCAL_PREFIXES = arrayOf("file:", "/", *RELATIVE_PREFIXES)
        @JvmStatic val ABSOLUTE_PREFIXES = arrayOf("/", *URI_PREFIXES)

        // true if needs resolving to absolute reference
        @JvmStatic fun isRelative(fullPath: String?): Boolean = fullPath != null && !isAbsolute(fullPath)

        // true if resolves to external
        @JvmStatic fun isRemote(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*EXTERNAL_PREFIXES)

        // true if resolves to local, if it resolves
        @JvmStatic fun isLocal(fullPath: String?): Boolean = fullPath != null && (fullPath.startsWith(*LOCAL_PREFIXES) || isRelative(fullPath))

        // true if it is a URI
        @JvmStatic fun isURI(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*URI_PREFIXES)

        // true if it is already an absolute ref, no need to resolve relative, just see if it maps
        @JvmStatic fun isAbsolute(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*ABSOLUTE_PREFIXES)

        @JvmStatic fun append(fullPath: String?, vararg parts: String): PathInfo {
            return append(fullPath, parts.asSequence());
        }

        @JvmStatic fun append(fullPath: String?, parts: Collection<String>): PathInfo {
            return append(fullPath, parts.asSequence())
        }

        @JvmStatic fun append(fullPath: String?, parts: Sequence<String>): PathInfo {
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
    }
}

