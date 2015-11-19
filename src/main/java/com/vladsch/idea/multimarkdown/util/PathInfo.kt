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

class PathInfo : Comparable<PathInfo> {

    private val nameStart: Int
    private val nameEnd: Int
    private val fullPath: String

    constructor(fullPath: String) {
        this.fullPath = removeDotDirectory(fullPath).removeSuffix("/").removeSuffix(".")
        val lastSep = fullPath.lastIndexOf('/')
        this.nameStart = if (lastSep < 0) 0 else if (lastSep < fullPath.lastIndex) lastSep+1 else lastSep

        val extStart = fullPath.lastIndexOf('.')
        this.nameEnd = if (extStart <= nameStart) this.fullPath.length else extStart
    }

    constructor(other: PathInfo) {
        this.nameStart = other.nameStart
        this.nameEnd = other.nameEnd
        this.fullPath = other.fullPath
    }

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

    val filePath: String
        get() = fullPath

    val filePathNoExt: String
        get() = if (nameEnd >= fullPath.length) fullPath else fullPath.substring(0, nameEnd)

    val path: String
        get() = if (nameStart == 0) EMPTY_STRING else if (nameStart == fullPath.length) fullPath else fullPath.substring(0, nameStart)

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
        get() = if (nameStart == 0 && nameEnd>=fullPath.length) fullPath else fullPath.substring(nameStart, nameEnd)

    override fun compareTo(other: PathInfo): Int = fullPath.compareTo(other.fullPath)

    override fun toString(): String = fullPath

    val isEmpty: Boolean
        get() = fullPath.isEmpty()

    val isRoot: Boolean
        get() = fullPath == "/"

    val isRelative: Boolean
        get() = isRelative(fullPath)

    val isExternal: Boolean
        get() = isExternal(fullPath)

    val isURI: Boolean
        get() = isURI(fullPath)

    val isAbsolute: Boolean
        get() = isAbsolute(fullPath)

    fun withExt(ext: String?): PathInfo = if (ext == null || isEmpty || this.ext == ext) this else PathInfo(filePathNoExt + ext.startWith('.'))
    fun append(vararg parts: String): PathInfo = PathInfo.append(fullPath, *parts)

    companion object {
        private val logger = Logger.getLogger(PathInfo::class.java)

        @JvmStatic val EMPTY_STRING = ""

        @JvmStatic val IMAGE_EXTENSIONS = arrayOf("png", "jpg", "jpeg", "gif")
        @JvmStatic val MARKDOWN_EXTENSIONS = MultiMarkdownFileTypeFactory.EXTENSIONS
        @JvmStatic val WIKI_PAGE_EXTENSIONS = MultiMarkdownFileTypeFactory.EXTENSIONS

        @JvmStatic val EXTERNAL_PREFIXES = arrayOf("http://", "ftp://", "https://", "mailto:")
        @JvmStatic val URI_PREFIXES = arrayOf("file://", *EXTERNAL_PREFIXES)
        @JvmStatic val ABSOLUTE_PREFIXES = arrayOf("/", *URI_PREFIXES)
        @JvmStatic val RELATIVE_PREFIXES = arrayOf("#")

        @JvmStatic fun isRelative(fullPath: String?): Boolean {
            return fullPath == null || fullPath.startsWith(*RELATIVE_PREFIXES) || !isAbsolute(fullPath)
        }

        @JvmStatic fun isExternal(href: String?): Boolean {
            return href != null && href.startsWith(*EXTERNAL_PREFIXES)
        }

        @JvmStatic fun isURI(href: String?): Boolean {
            return href != null && href.startsWith(*URI_PREFIXES)
        }

        @JvmStatic fun isAbsolute(href: String?): Boolean {
            return href != null && href.startsWith(*ABSOLUTE_PREFIXES)
        }

        @JvmStatic fun append(fullPath: String?, vararg parts: String): PathInfo {
            var path: String = fullPath.orEmpty()

            for (part in parts) {
                if (part !in arrayOf("", ".", "/.", "./", "/./")) {
                    if (path.isEmpty() || !path.endsWith('/')) path += '/'
                    path += part.removePrefix("/")
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

        @JvmStatic fun isImageExt(ext: String, ignoreCase: Boolean = true): Boolean {
            return isExtIn(ext, ignoreCase, *IMAGE_EXTENSIONS)
        }

        @JvmStatic fun isMarkdownExt(ext: String, ignoreCase: Boolean = true): Boolean {
            return isExtIn(ext, ignoreCase, *MARKDOWN_EXTENSIONS)
        }

        @JvmStatic fun isWikiPageExt(ext: String, ignoreCase: Boolean = true): Boolean {
            return isExtIn(ext, ignoreCase, *WIKI_PAGE_EXTENSIONS)
        }

        @JvmStatic fun removeDotDirectory(path: String?): String = path.orEmpty().replace("/./", "/").removePrefix("./")
    }
}
