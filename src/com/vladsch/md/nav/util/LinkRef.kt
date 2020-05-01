// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util

import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.ifEmptyNullArgs
import com.vladsch.plugin.util.splicer
import com.vladsch.plugin.util.suffixWith
import com.vladsch.plugin.util.urlDecode

import java.util.*

open class LinkRef(val containingFile: FileRef, fullPath: String, anchorText: String?, val targetRef: FileRef?, val isNormalized: Boolean) : PathInfo(fullPath) {
    val anchor: String? = anchorText?.removePrefix("#")
    val hasAnchor: Boolean
        get() = anchor != null

    val isSelfAnchor: Boolean
        get() = _fullPath.isEmpty() && hasAnchor

    val isResolved: Boolean
        get() = targetRef != null

    override val isEmpty: Boolean
        get() = _fullPath.isEmpty() && !hasAnchor

    val isDoNothingAnchor: Boolean
        get() = isSelfAnchor && (anchor.isNullOrEmpty())

    val anchorText: String
        get() = if (anchor == null) EMPTY_STRING else "#$anchor"

    val notEmptyAnchorText: String
        get() = "#$anchor"

    val filePathWithAnchor: String
        get() = super.filePath + anchorText

    val filePathNoExtWithAnchor: String
        get() = super.filePathNoExt + anchorText

    override fun toString(): String = filePathWithAnchor

    open val linkExtensions: Array<String>
        get() {
            return when {
                ext in IMAGE_EXTENSIONS -> IMAGE_EXTENSIONS
                ext.isEmpty() || ext in MARKDOWN_EXTENSIONS -> MARKDOWN_EXTENSIONS
                else -> arrayOf(ext, *MARKDOWN_EXTENSIONS)
            }
        }

    fun resolve(resolver: GitHubLinkResolver, inList: List<PathInfo>? = null): LinkRef? {
        val targetRef = resolver.resolve(this, 0, inList)
        return if (targetRef == null) null else resolver.linkRef(this, targetRef, null, null, null)
    }

    val remoteURL: String? by lazy {
        when {
            targetRef is ProjectFileRef -> targetRef.gitHubVcsRoot?.baseUrl.suffixWith('/') + filePath + this.anchorText
            isExternal -> filePath + this.anchorText
            else -> null
        }
    }

    fun withTargetRef(targetRef: FileRef?): LinkRef {
        return LinkRef(containingFile, _fullPath, anchor, targetRef, isNormalized)
    }

    // convert file name to link
    open fun fileToLink(linkAddress: String, exclusionMap: Map<String, String>?): String = urlEncode(linkAddress, exclusionMap)

    // convert link to file name
    open fun linkToFile(linkAddress: String): String = urlDecode(linkAddress)

    // prepare text for matching files, wrap in (?:) so matches as a block
    open fun linkToFileRegex(linkText: String): String = linkAsFileRegex(linkText)

    // make a copy of everything but fullPath and return the same type of link
    open fun replaceFilePath(fullPath: String, withTargetRef: Boolean = false, isNormalized: Boolean? = null): LinkRef {
        return LinkRef(containingFile, fullPath, anchor, if (withTargetRef) targetRef else null, isNormalized ?: this.isNormalized)
    }

    // remove anchor from ref and return a new copy
    open fun removeAnchor(): LinkRef {
        return LinkRef(containingFile, filePath, null, targetRef, isNormalized)
    }

    open fun replaceFilePath(fullPath: String, targetRef: FileRef, isNormalized: Boolean? = null): LinkRef {
        return LinkRef(containingFile, fullPath, anchor, targetRef, isNormalized ?: this.isNormalized)
    }

    open fun replaceFilePathAndAnchor(fullPath: String, withTargetRef: Boolean = false, replaceAnchor: String? = null, isNormalized: Boolean? = null): LinkRef {
        return LinkRef(containingFile, fullPath, replaceAnchor, if (withTargetRef) targetRef else null, isNormalized ?: this.isNormalized)
    }

    companion object {
        @JvmStatic
        fun parseLinkRef(containingFile: FileRef, fullPath: String, targetRef: FileRef?): LinkRef {
            return parseLinkRef(containingFile, fullPath, targetRef, ::LinkRef)
        }

        @JvmStatic
        fun parseWikiLinkRef(containingFile: FileRef, fullPath: String, targetRef: FileRef?): WikiLinkRef {
            return parseLinkRef(containingFile, fullPath, targetRef, ::WikiLinkRef) as WikiLinkRef
        }

        @JvmStatic
        fun parseImageLinkRef(containingFile: FileRef, fullPath: String, targetRef: FileRef?): ImageLinkRef {
            return parseLinkRef(containingFile, fullPath, targetRef, ::ImageLinkRef) as ImageLinkRef
        }

        @JvmStatic
        fun <T : LinkRef> parseLinkRef(containingFile: FileRef, fullPath: String, targetRef: FileRef?, linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?, targetRef: FileRef?, isNormalized: Boolean) -> T): LinkRef {
            var linkRef = PathInfo.cleanFullPath(fullPath)
            var anchor: String? = null

            // if the target file name has anchor, and linkRef does not contain URL encoded #, then we leave the anchor as part of the link
            if (targetRef == null || !(!linkRef.contains("%23") && targetRef.fileNameContainsAnchor())) {
                val anchorPos = linkRef.indexOf('#')
                if (anchorPos >= 0) {
                    anchor = if (anchorPos == linkRef.lastIndex) EMPTY_STRING else linkRef.substring(anchorPos + 1)
                    linkRef = if (anchorPos == 0) EMPTY_STRING else linkRef.substring(0, anchorPos)
                }
            }

            return linkRefType(containingFile, linkRef, anchor, targetRef, false)
        }

        // URL encode/decode handling
        @JvmStatic
        fun urlEncode(linkAddress: String, exclusionMap: Map<String, String>?): String {
            return mapLinkChars(linkAddress, fileUrlMap, exclusionMap)
        }

        @JvmStatic
        fun urlDecode(linkAddress: String): String {
            return linkAddress.urlDecode()
        }

        // prepare text for matching files, wrap in (?:) so matches as a block
        @JvmStatic
        fun linkAsFileRegex(linkText: String): String {
            return "(?:\\Q$linkText\\E)"
        }

        @JvmStatic
        fun mapLinkChars(linkAddress: String, charMap: Map<String, String>, exclusionMap: Map<String, String>?): String {
            var result = linkAddress
            for (pair in charMap) {
                if (exclusionMap == null || !exclusionMap.containsKey(pair.key)) {
                    result = result.replace(pair.key, pair.value)
                }
            }
            return result
        }

        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun mapLinkCharsRegex(linkAddress: String, charMap: Map<String, Regex>): String {
            var result = linkAddress
            for (pair in charMap) {
                result = result.replace(pair.value, pair.key)
            }
            return result
        }

        @JvmStatic
        fun unmapLinkChars(linkAddress: String, charMap: Map<String, String>, exclusionMap: Map<String, String>?): String {
            var result = linkAddress
            for (pair in charMap) {
                if (exclusionMap == null || !exclusionMap.containsKey(pair.key)) {
                    result = result.replace(pair.value, pair.key)
                }
            }
            return result
        }

        // more efficient when multiple chars map to the same value, creates a regex to match all keys
        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun linkRegexMap(charMap: Map<String, String>, exclusionMap: Map<String, String>? = null): Map<String, Regex> {
            val regExMap = HashMap<String, Regex>()
//            val useMap = if (exclusionMap == null) charMap else charMap.filter { !exclusionMap.containsKey(it.key) }
            for (char in charMap.values) {
                val regex = charMap.filter { it.value == char }.keys.map { "\\Q$it\\E" }.reduce(splicer("|"))
                regExMap[char] = regex.toRegex()
            }
            return regExMap
        }

        // char in file name to link map
        @JvmField
        val fileUrlMap: Map<String, String> = mapOf(
            Pair("%", "%25"), // NOTE: must be first in list otherwise will replace % of url encoded entities
            Pair(" ", "%20"),
            Pair("!", "%21"),
            Pair("#", "%23"),
            Pair("$", "%24"),
            Pair("&", "%26"),
            Pair("'", "%27"),
            Pair("(", "%28"),
            Pair(")", "%29"),
            Pair("*", "%2A"),
            Pair("+", "%2B"),
            Pair(",", "%2C"),
            //Pair("/", "%2F"), // not supported, used for directory separator
            //Pair(":", "%3A"),  // not supported, windows needs this for drive specification
            Pair(";", "%3B"),
            Pair("<", "%3C"),
            Pair("=", "%3D"),
            Pair(">", "%3E"),
            Pair("?", "%3F"),
            Pair("@", "%40"),
            Pair("[", "%5B"),
            Pair("\\", "%5C"),
            Pair("]", "%5D"),
            Pair("^", "%5E"),
            Pair("`", "%60"),
            Pair("{", "%7B"),
            Pair("}", "%7D")
        )

        // char in file name to link map
        @JvmField
        val gitBookFileUrlExclusionsMap: Map<String, String> = mapOf(
            Pair("+", "%2B")
        )

        // CAUTION: just copies link address without figuring out whether it will resolve as is
        @JvmStatic
        fun from(linkRef: LinkRef, exclusionMap: Map<String, String>?): LinkRef {
            @Suppress("NAME_SHADOWING")
            var linkRef: LinkRef = linkRef

            return when (linkRef) {
                is ImageLinkRef ->
                    LinkRef(linkRef.containingFile, if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileNameNoExt else linkRef.fileName, linkRef.anchor, linkRef.targetRef, false)
                is WikiLinkRef -> {
                    var wikiLink = WikiLinkRef.linkAsFile(linkRef.filePath)
                    var withExt = false

                    if (linkRef.hasAnchor && (linkRef.targetRef?.fileNameContainsAnchor() == true)) {
                        wikiLink += linkRef.anchorText
                        linkRef = linkRef.removeAnchor()
                    }

                    if (linkRef.targetRef?.isWikiPage == false && !linkRef.hasExt) {
                        wikiLink += WIKI_PAGE_EXTENSION
                        withExt = true
                    }

                    if (wikiLink.equals(if (withExt) linkRef.containingFile.fileName else linkRef.containingFile.fileNameNoExt, ignoreCase = true))
                        LinkRef(linkRef.containingFile, "", linkRef.anchor.orEmpty(), linkRef.targetRef, false)
                    else
                        LinkRef(linkRef.containingFile, urlEncode(wikiLink, exclusionMap), wikiLink.ifEmptyNullArgs(linkRef.anchor.orEmpty(), linkRef.anchor), linkRef.targetRef, false)
                }
                else -> linkRef
            }
        }
    }

    open fun withContainingFile(destFile: FileRef): LinkRef {
        return LinkRef(destFile, filePath, anchor, targetRef, isNormalized)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LinkRef) return false
        if (!super.equals(other)) return false

        if (containingFile != other.containingFile) return false
        if (anchor != other.anchor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + containingFile.hashCode()
        result = 31 * result + (anchor?.hashCode() ?: 0)
        return result
    }
}

// this is a [[]] style link ref
open class WikiLinkRef(containingFile: FileRef, fullPath: String, anchor: String?, targetRef: FileRef?, isNormalized: Boolean) : LinkRef(containingFile, fullPath, anchor, targetRef, isNormalized) {

    override val linkExtensions: Array<String>
        get() = WIKI_PAGE_EXTENSIONS

    // convert file name to link
    override fun fileToLink(linkAddress: String, exclusionMap: Map<String, String>?): String = fileAsLink(linkAddress, exclusionMap)

    // convert link to file name
    override fun linkToFile(linkAddress: String): String = linkAsFile(linkAddress)

    // prepare text for matching files, wrap in (?:) so matches as a block
    override fun linkToFileRegex(linkText: String): String = linkAsFileRegex(linkText)

    // make a copy of everything but fullPath and return the same type of link
    override fun replaceFilePath(fullPath: String, withTargetRef: Boolean, isNormalized: Boolean?): LinkRef {
        return WikiLinkRef(containingFile, fullPath, anchor, if (withTargetRef) targetRef else null, isNormalized ?: this.isNormalized)
    }

    override fun replaceFilePath(fullPath: String, targetRef: FileRef, isNormalized: Boolean?): LinkRef {
        return WikiLinkRef(containingFile, fullPath, anchor, targetRef, isNormalized ?: this.isNormalized)
    }

    // remove anchor from ref and return a new copy
    override fun removeAnchor(): LinkRef {
        return WikiLinkRef(containingFile, filePath, null, targetRef, isNormalized)
    }

    override fun replaceFilePathAndAnchor(fullPath: String, withTargetRef: Boolean, replaceAnchor: String?, isNormalized: Boolean?): LinkRef {
        return WikiLinkRef(containingFile, fullPath, replaceAnchor, if (withTargetRef) targetRef else null, isNormalized
            ?: this.isNormalized)
    }

    override fun withContainingFile(destFile: FileRef): WikiLinkRef {
        return WikiLinkRef(destFile, filePath, anchor, targetRef, isNormalized)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WikiLinkRef) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    companion object {
        // convert file name to link, usually url encode
        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun fileAsLink(linkAddress: String, exclusionMap: Map<String, String>? = null): String = linkAddress.replace('-', ' ')

        @JvmStatic
        val wikiLinkRegexMap: Map<String, Regex> by lazy {
            linkRegexMap(wikiLinkMap)
        }

        // convert link to file name, usually url decode
        @JvmStatic
        fun linkAsFile(linkAddress: String): String = mapLinkCharsRegex(linkAddress, wikiLinkRegexMap)

        // prepare text for matching files, wrap in (?:) so matches as a block
        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun linkAsFileRegex(linkText: String): String {
            // GitHub Wiki Change: No longer true
            // return "(?:\\Q" + linkText.replace(wikiLinkMatchRegex, "\\\\E(?:-| |\\\\+|>|<|/)\\\\Q") + "\\E)"
            return "(?:\\Q" + linkText.replace(wikiLinkMatchRegex, "\\\\E(?:-| |\\\\+|/)\\\\Q") + "\\E)"
        }

        @JvmField
        val wikiLinkMap: Map<String, String> = mapOf(
            Pair(" ", "-"),
            Pair("+", "-"),
            Pair("/", "-")
            // GitHub Wiki Change: No longer true
            //                Pair("<", "-"),
            //                Pair(">", "-")
        )

        // GitHub Wiki Change: No longer true
        //        val wikiLinkMatchRegex = "-| |\\+|>|<|/".toRegex()
        val wikiLinkMatchRegex: Regex = "-| |\\+|/".toRegex()

        // CAUTION: just copies link address without figuring out whether it will resolve as is
        @JvmStatic
        fun from(linkRef: LinkRef): WikiLinkRef? {
            return when (linkRef) {
                is ImageLinkRef -> null
                is WikiLinkRef -> linkRef
                else -> {
                    when {
                        linkRef.path.isNotEmpty() && linkRef.hasExt && !linkRef.isWikiPageExt -> null // won't resolve as wiki link
                        else -> WikiLinkRef(linkRef.containingFile, fileAsLink(urlDecode(if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileNameNoExt else if (linkRef.isWikiPageExt) linkRef.fileNameNoExt else linkRef.fileName)), linkRef.anchor, linkRef.targetRef, false)
                    }
                }
            }
        }
    }
}

open class ImageLinkRef(containingFile: FileRef, fullPath: String, anchor: String?, targetRef: FileRef?, isNormalized: Boolean) : LinkRef(containingFile, fullPath, anchor, targetRef, isNormalized) {
    override val linkExtensions: Array<String>
        get() = IMAGE_EXTENSIONS

    // make a copy of everything but fullPath and targetRef and return the same type of link
    override fun replaceFilePath(fullPath: String, withTargetRef: Boolean, isNormalized: Boolean?): LinkRef {
        return ImageLinkRef(containingFile, fullPath, anchor, if (withTargetRef) targetRef else null, isNormalized ?: this.isNormalized)
    }

    override fun replaceFilePath(fullPath: String, targetRef: FileRef, isNormalized: Boolean?): LinkRef {
        return ImageLinkRef(containingFile, fullPath, anchor, targetRef, isNormalized ?: this.isNormalized)
    }

    // remove anchor from ref and return a new copy
    override fun removeAnchor(): LinkRef {
        return ImageLinkRef(containingFile, filePath, null, targetRef, isNormalized)
    }

    override fun replaceFilePathAndAnchor(fullPath: String, withTargetRef: Boolean, replaceAnchor: String?, isNormalized: Boolean?): LinkRef {
        return ImageLinkRef(containingFile, fullPath, replaceAnchor, if (withTargetRef) targetRef else null, isNormalized
            ?: this.isNormalized)
    }

    override fun withContainingFile(destFile: FileRef): ImageLinkRef {
        return ImageLinkRef(destFile, filePath, anchor, targetRef, isNormalized)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageLinkRef) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    // CAUTION: just copies link address without figuring out whether it will resolve as is
    companion object {

        @JvmStatic
        fun from(linkRef: LinkRef): LinkRef? {
            return when (linkRef) {
                is ImageLinkRef -> linkRef
                is WikiLinkRef -> null
                else ->
                    // FIX: add validation for type of file and extension and return null when it is not possible to convert
                    ImageLinkRef(linkRef.containingFile, if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileName else linkRef.fileName, linkRef.anchor, linkRef.targetRef, false)
            }
        }
    }
}

