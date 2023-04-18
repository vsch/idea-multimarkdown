// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiFile
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.md.nav.MdFileType
import com.vladsch.plugin.util.*
import java.io.File
import kotlin.text.endsWith
import kotlin.text.startsWith

open class PathInfo(fullPath: String) : Comparable<PathInfo> {
    protected val _fullPath: String
    protected val _nameStart: Int
    protected val _nameEnd: Int
    protected val _queryStart: Int
    protected val _uriEnd: Int

    constructor(__virtualFile: VirtualFile) : this(__virtualFile.path)

    constructor(psiFile: PsiFile) : this(psiFile.virtualFile.path)

    init {
        val cleanPath = cleanFullPath(fullPath)

        val pos = cleanPath.indexOf('?')
        _queryStart = if (pos < 0) cleanPath.length else pos

        val lastSep = cleanPath.lastIndexOf('/', _queryStart)
        _nameStart = if (lastSep < 0) 0 else if (lastSep < cleanPath.lastIndex) lastSep + 1 else lastSep

        val extStart = cleanPath.lastIndexOf('.', _queryStart)
        _nameEnd = if (extStart <= _nameStart) cleanPath.length else extStart
        _fullPath = cleanPath

        val possibleUri = cleanPath.indexOf(':')
        _uriEnd = when {
            possibleUri + 1 < _nameStart && cleanPath[possibleUri + 1] == '/' && isCustomURI(cleanPath) ->
                // if have another one then it is part of the URI
                if (possibleUri + 2 < _nameStart && cleanPath[possibleUri + 2] == '/') possibleUri + 3
                else possibleUri + 2
            else -> 0
        }
    }

    override fun compareTo(other: PathInfo): Int = _fullPath.compareTo(other._fullPath)
    override fun toString(): String = _fullPath

    // REFACTOR: create util function taking: start and multiple end args returning substring or unchanged
    //           these are all the same

    val filePath: String
        get() = _fullPath

    val filePathNoExt: String
        get() = if (_nameEnd >= _fullPath.length) _fullPath else _fullPath.substring(0, _nameEnd)

    val filePathNoQuery: String
        get() = if (_queryStart >= _fullPath.length) _fullPath else _fullPath.substring(0, _queryStart)

    val path: String
        get() = if (_nameStart == 0) EMPTY_STRING else _fullPath.substring(0, _nameStart)

    val pathNoURI: String
        get() = if (_nameStart <= _uriEnd) EMPTY_STRING else _fullPath.substring(_uriEnd, _nameStart)

    val fileName: String
        get() = if (_nameStart == 0) _fullPath else _fullPath.substring(_nameStart, _fullPath.length)

    val fileNameNoQuery: String
        get() = if (_nameStart == 0 && _queryStart >= _fullPath.length) _fullPath else _fullPath.substring(_nameStart, _queryStart)

    // no extension assumes no query, otherwise if there is no extension query will be included
    val fileNameNoExt: String
        get() = if (_nameStart == 0 && _nameEnd.min(_queryStart) >= _fullPath.length) _fullPath else _fullPath.substring(_nameStart, _nameEnd.min(_queryStart))

    val ext: String
        get() = if (_nameEnd + 1 >= _fullPath.length) EMPTY_STRING else _fullPath.substring(_nameEnd + 1, _queryStart)

    val extWithDot: String
        get() = if (_nameEnd >= _fullPath.length) EMPTY_STRING else _fullPath.substring(_nameEnd, _queryStart)

    val hasExt: Boolean
        get() = _nameEnd + 1 < _queryStart

    val query: String
        get() = if (_queryStart + 1 >= _fullPath.length) EMPTY_STRING else _fullPath.substring(_queryStart + 1, _fullPath.length)

    val queryWithQueryMark: String
        get() = if (_queryStart >= _fullPath.length) EMPTY_STRING else _fullPath.substring(_queryStart, _fullPath.length)

    val hasQuery: Boolean
        get() = _queryStart < _fullPath.length

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

    // these ones need resolving to absolute reference
    val isRepoRelative: Boolean
        get() = isRepoRelative(_fullPath)

    // these ones resolve to local references, if they resolve
    val isLocal: Boolean
        get() = isLocal(_fullPath)

    // these ones resolve to external references, if they resolve
    val isExternal: Boolean
        get() = isExternal(_fullPath)

    // these ones resolve to external references, if they resolve
    val isExternalNotPrefix: Boolean
        get() = isExternalNotPrefix(_fullPath)

    val isMailTo: Boolean
        get() = isMailTo(_fullPath)

    val isURL: Boolean
        get() = isURL(_fullPath)

    // these ones are URI prefixed
    val isURI: Boolean
        get() = isURI(_fullPath)

    // these ones are URI prefixed
    val isCustomURI: Boolean
        get() = isCustomURI(_fullPath)

    // these ones are URI prefixed
    val customURIPrefix: String?
        get() {
            if (isURL || isFileURI) return null
            val matches = CUSTOM_URI_SCHEME_PREFIXES.find(_fullPath) ?: return null
            return matches.groupValues[1]
        }

    // these ones are URI prefixed
    val protocolURIPrefix: String?
        get() {
            val matches = CUSTOM_URI_SCHEME_PREFIXES.find(_fullPath) ?: return null
            return matches.groupValues[1]
        }

    val isFileURI: Boolean
        get() = isFileURI(_fullPath)

    val isFileURINotPrefix: Boolean
        get() = isFileURINotPrefix(_fullPath)

    // these ones are not relative, ie don't need resolving
    val isAbsolute: Boolean
        get() = isAbsolute(_fullPath)

    fun withExt(ext: String?): PathInfo = if (ext == null || isEmpty || this.ext == ext.removePrefix(".")) this else PathInfo(filePathNoExt + ext.prefixWith('.'))
    open fun append(vararg parts: String): PathInfo = PathInfo.appendParts(_fullPath, *parts, construct = ::PathInfo)
    open fun append(parts: Collection<String>): PathInfo = PathInfo.appendParts(_fullPath, parts, construct = ::PathInfo)
    open fun append(parts: Sequence<String>): PathInfo = PathInfo.appendParts(_fullPath, parts, construct = ::PathInfo)

    open fun projectFileRef(project: Project): ProjectFileRef? {
        val virtualFile = if (!isAbsolute || !isLocal) null else VirtualFileManager.getInstance().findFileByUrl(prefixWithFileURI(_fullPath))
        val projectFileRef = if (virtualFile == null) null else ProjectFileRef(virtualFile, project)
        if (projectFileRef != null && this is LinkRef && targetRef != null && targetRef.isRawFile) {
            projectFileRef.isRawFile = true
        }
        return projectFileRef
    }

    open val virtualFile: VirtualFile?
        get() {
            val file = if (!isAbsolute || !isLocal) null
            else VirtualFileManager.getInstance().findFileByUrl(prefixWithFileURI(_fullPath))

            return file
        }

    fun canRenameFileTo(newName: String): Boolean {
        val newPathInfo = append("..", newName)
        val virtualFile = newPathInfo.virtualFile
        return this.virtualFile != null && (this.virtualFile as VirtualFile).exists() && virtualFile == null && (this.virtualFile as VirtualFile).parent != null
    }

    fun canCreateFile(): Boolean {
        val pathInfo = PathInfo(path)
        val parentDir = pathInfo.virtualFile
        return parentDir != null && !File(filePath).exists()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PathInfo) return false

        if (_fullPath != other._fullPath) return false

        return true
    }

    override fun hashCode(): Int {
        return _fullPath.hashCode()
    }

    companion object {
        const val EMPTY_STRING: String = ""
        const val WIKI_PAGE_EXTENSION: String = ".md"
        const val WIKI_HOME_DIR_EXTENSION: String = ".wiki"
        const val WIKI_HOME_FILENAME: String = "Home"

        @JvmField
        val IMAGE_EXTENSIONS: Array<String> = arrayOf("png", "jpg", "jpeg", "gif", "svg")

        @JvmField
        val MARKDOWN_EXTENSIONS: Array<String> = MdFileType.EXTENSIONS

        @JvmField
        val WIKI_PAGE_EXTENSIONS: Array<String> = MdFileType.EXTENSIONS

        @JvmField
        val URL_PREFIXES: Array<String> = arrayOf("http://", "https://")

        @JvmField
        val MAILTO_PREFIXES: Array<String> = arrayOf("mailto:")

        @JvmField
        val EXTERNAL_PREFIXES: Array<String> = arrayOf("ftp://", *MAILTO_PREFIXES, *URL_PREFIXES)

        @JvmField
        val WINDOWS_OS: Boolean = System.getProperty("os.name").startsWith("Windows", ignoreCase = true)

        @JvmField
        val FILE_URI_PREFIXES: Array<String> = arrayOf("file://", "file:/")

        @JvmField
        val URI_PREFIXES: Array<String> = arrayOf(*FILE_URI_PREFIXES, *EXTERNAL_PREFIXES)

        @JvmField
        val RELATIVE_PREFIXES: Array<String> = arrayOf<String>()

        @JvmField
        val LOCAL_PREFIXES: Array<String> = arrayOf("file:", "/", *RELATIVE_PREFIXES)

        @JvmField
        val ABSOLUTE_PREFIXES: Array<String> = arrayOf("/", "file://", *EXTERNAL_PREFIXES)

        @JvmField
        val CUSTOM_URI_SCHEME_PREFIXES: Regex = "^([a-zA-Z][a-zA-Z0-9+.-]*:/{1,2})".toRegex()

        @JvmField
        val WINDOWS_PATH_URI: Regex = "^([a-zA-Z]:)".toRegex()

        // true if needs resolving to absolute reference
        @JvmStatic
        fun isRelative(fullPath: String?): Boolean = fullPath != null && !isAbsolute(fullPath)

        // true if needs resolving to absolute reference
        @JvmStatic
        fun isRepoRelative(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith("/")

        @JvmStatic
        fun isOnlyUri(fullPath: String?): Boolean = fullPath != null && fullPath in URI_PREFIXES

        // true if resolves to external
        @JvmStatic
        fun isExternal(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*EXTERNAL_PREFIXES)

        // true if resolves to external
        @JvmStatic
        fun isExternalNotPrefix(fullPath: String?): Boolean = fullPath != null && fullPath.startsWithNotEqual(*EXTERNAL_PREFIXES)

        // true if resolves to http or https
        @JvmStatic
        fun isURL(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*URL_PREFIXES)

        // true if resolves to http or https
        @JvmStatic
        fun isMailTo(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*MAILTO_PREFIXES)

        // true if resolves to local, if it resolves
        @JvmStatic
        fun isLocal(fullPath: String?): Boolean = fullPath != null && (fullPath.length > 1 && fullPath[1] == ':' && fullPath[0].isLetter() || fullPath.startsWith(*LOCAL_PREFIXES) || isRelative(fullPath))

        // true if it is a URI
        @JvmStatic
        fun isURI(fullPath: String?): Boolean = fullPath != null && (fullPath.startsWith(*URI_PREFIXES) || CUSTOM_URI_SCHEME_PREFIXES.containsMatchIn(fullPath))

        @JvmStatic
        fun isWindowsPathURI(fullPath: String?): Boolean = fullPath != null && (WINDOWS_PATH_URI.containsMatchIn(fullPath))

        // true if it is a URI
        @JvmStatic
        fun isCustomURI(fullPath: String?): Boolean = fullPath != null && CUSTOM_URI_SCHEME_PREFIXES.containsMatchIn(fullPath)

        @JvmStatic
        fun isFileURI(fullPath: String?): Boolean = fullPath != null && fullPath.startsWith(*FILE_URI_PREFIXES)

        @JvmStatic
        fun isFileURINotPrefix(fullPath: String?): Boolean = fullPath != null && fullPath.startsWithNotEqual(*FILE_URI_PREFIXES)

        // true if it is already an absolute ref, no need to resolve relative, just see if it maps
        @JvmStatic
        fun isAbsolute(fullPath: String?): Boolean {
            if (fullPath == null) return false
            val useFullPath = removeFileUriPrefix(fullPath)

            return if (WINDOWS_OS) useFullPath.length > 2 && useFullPath[1] == ':' && useFullPath[2] == '/' && useFullPath[0].isLetter() || useFullPath.startsWith(*ABSOLUTE_PREFIXES)
            else useFullPath.startsWith(*ABSOLUTE_PREFIXES)
        }

        @JvmStatic
        fun appendParts(fullPath: String?, vararg parts: String): PathInfo {
            return appendParts(fullPath, parts.toList(), ::PathInfo)
        }

        @JvmStatic
        fun appendParts(fullPath: String?, parts: Collection<String>): PathInfo {
            return appendParts(fullPath, parts, ::PathInfo)
        }

        @JvmStatic
        fun appendParts(fullPath: String?, parts: Sequence<String>): PathInfo {
            return appendParts(fullPath, parts.toList(), ::PathInfo)
        }

        @JvmStatic
        fun <T : PathInfo> appendParts(fullPath: String?, vararg parts: String, construct: (fullPath: String) -> T): T {
            return appendParts(fullPath, parts.toList(), construct)
        }

        @JvmStatic
        fun <T : PathInfo> appendParts(fullPath: String?, parts: Sequence<String>, construct: (fullPath: String) -> T): T {
            return appendParts(fullPath, parts.toList(), construct)
        }

        @JvmStatic
        fun <T : PathInfo> appendParts(fullPath: String?, parts: Collection<String>, construct: (fullPath: String) -> T): T {
            var path: String = cleanFullPath(fullPath)

            for (mainPart in parts) {
                @Suppress("NAME_SHADOWING")
                val mainPart = mainPart.replace('\\', '/')
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

        @JvmStatic
        fun prefixWithFileURI(path: String?): String {
            // the fix will have to go into LinkRenderer, otherwise VirtualFile will not find the file or the browser will not display it
            return when {
                path == null -> ""
                WINDOWS_OS && path.startsWith('/') -> path.substring(1).replace('/', '\\').prefixWith("file:/")
                WINDOWS_OS -> path.replace('/', '\\').prefixWith("file:/")
                else -> path.prefixWith("file://")
            }
        }

        @JvmStatic
        fun fileURIPrefix(path: String?): String {
            // the fix will have to go into LinkRenderer, otherwise VirtualFile will not find the file or the browser will not display it
            return when {
                path == null -> ""
                WINDOWS_OS -> "file:/"
                else -> "file://"
            }
        }

        @JvmStatic
        fun isExtIn(ext: String, ignoreCase: Boolean = true, vararg extList: String): Boolean {
            for (listExt in extList) {
                if (listExt.equals(ext, ignoreCase)) return true
            }
            return false
        }

        @JvmStatic
        fun isImageExt(ext: String, ignoreCase: Boolean = true): Boolean = isExtIn(ext, ignoreCase, *IMAGE_EXTENSIONS)

        @JvmStatic
        fun isMarkdownExt(ext: String, ignoreCase: Boolean = true): Boolean = isExtIn(ext, ignoreCase, *MARKDOWN_EXTENSIONS)

        @JvmStatic
        fun isWikiPageExt(ext: String, ignoreCase: Boolean = true): Boolean = isExtIn(ext, ignoreCase, *WIKI_PAGE_EXTENSIONS)

        @JvmStatic
        fun removeDotDirectory(path: CharSequence?): String = path?.toString().orEmpty().replace("/./", "/").removePrefix("./")

        @JvmStatic
        fun cleanFullPath(fullPath: CharSequence?): String {
            var cleanPath = removeDotDirectory(fullPath?.trim()).replace('\\', '/')
            if (!cleanPath.endsWith("//") && cleanPath != "/") cleanPath = cleanPath.removeSuffix("/")
            return cleanPath.removeSuffix(".")
        }

        @JvmStatic
        fun relativePath(fromPath: CharSequence, toPath: CharSequence, withPrefix: Boolean = true, blobRawEqual: Boolean): String {
            var useFromPath = fromPath.toString().replace('\\', '/')
            var useToPath = toPath.toString().replace('\\', '/')
            var toHadRaw = false

            if (blobRawEqual) {
                var pos = useToPath.indexOf("/raw/")
                if (pos != -1) {
                    useToPath = useToPath.substring(0, pos) + "/blob/" + useToPath.substring(pos + "/raw/".length)
                    toHadRaw = true
                }
                pos = useFromPath.indexOf("/raw/")
                if (pos != -1) {
                    useFromPath = useFromPath.substring(0, pos) + "/blob/" + useFromPath.substring(pos + "/raw/".length)
                }
            }

            var lastSlash = -1
            val iMax = Math.min(useFromPath.length, useToPath.length) - 1
            for (i in 0 .. iMax) {
                if (useFromPath[i] != useToPath[i]) break
                if (useFromPath[i] == '/') lastSlash = i
            }

            // for every dir in containingFilePath after lastSlash add ../ as the prefix
            var result = if (withPrefix) {
                var prefix = "../".repeat(useFromPath.toBased().countOfAny(CharPredicate.anyOf('/'), lastSlash + 1))
                prefix += useToPath.substring(lastSlash + 1)
                prefix
            } else {
                useToPath.substring(lastSlash + 1)
            }

            if (toHadRaw) {
                // change /blob/ back to raw
                val pos = result.indexOf("/blob/")
                if (pos != -1) {
                    result = result.substring(0, pos) + "/raw/" + result.substring(pos + "/blob/".length)
                }
            }
            return result
        }

        @JvmStatic
        fun fileNames(pathInfos: List<PathInfo>): Array<String> {
            val list = pathInfos.map { pathInfo -> pathInfo.fileName }
            return list.toTypedArray()
        }

        @JvmStatic
        fun fileNamesNoExt(pathInfos: List<PathInfo>): Array<String> {
            val list = pathInfos.map { pathInfo -> pathInfo.fileNameNoExt }
            return list.toTypedArray()
        }

        @JvmStatic
        fun removeDotDirectoriesPrefix(path: String?): String {
            if (path == null) return EMPTY_STRING
            var outPath = path.toBased()
            while (true) {
                if (outPath.startsWith("../")) outPath = outPath.removePrefix("../")
                else if (outPath.startsWith("./")) outPath = outPath.removePrefix("./")
                else break
            }
            return cleanFullPath(outPath)
        }

        @JvmStatic
        fun skipParts(filePath: CharSequence, skipLeading: Int): String? {
            val parts = filePath.split("/")
            if (parts.size < skipLeading) return null
            return parts.slice(skipLeading .. parts.lastIndex).splice("/", false)
        }

        @JvmStatic
        fun removeFileUriPrefix(toFile: CharSequence): String {
            return when {
                toFile.startsWith("file:///") -> toFile.substring("file://".length)
                toFile.startsWith("file://") ->
                    if (WINDOWS_OS) toFile.substring("file://".length)
                    else toFile.substring("file:/".length)
                toFile.startsWith("file:/") ->
                    if (WINDOWS_OS) toFile.substring("file:/".length)
                    else toFile.substring("file:".length)
                toFile.startsWith("temp:///") -> toFile.substring("temp://".length)
                else -> toFile
            }.toString()
        }
    }
}

