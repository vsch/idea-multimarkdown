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

open class LinkRef(val containingFile: FileRef, fullPath: String, anchorTxt: String?, val targetRef: FileRef?) : PathInfo(fullPath) {
    val anchor: String? = anchorTxt?.removePrefix("#")
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
        get() = if (anchor == null) EMPTY_STRING else "#" + anchor

    val filePathWithAnchor: String
        get() = super.filePath + anchorText

    val filePathNoExtWithAnchor: String
        get() = super.filePathNoExt + anchorText

    override val isRelative: Boolean
        get() = !isSelfAnchor && isRelative(_fullPath)

    override val isLocal: Boolean
        get() = isSelfAnchor || isLocal(_fullPath)

    override val isAbsolute: Boolean
        get() = isSelfAnchor || isAbsolute(_fullPath)

    override fun toString(): String = filePathWithAnchor

    open val linkExtensions: Array<String>
        get() {
            when {
                ext in IMAGE_EXTENSIONS -> return IMAGE_EXTENSIONS
                ext.isEmpty(), ext in MARKDOWN_EXTENSIONS -> return MARKDOWN_EXTENSIONS
                else -> return arrayOf(ext)
            }
        }

    fun resolve(resolver: GitHubLinkResolver, inList: List<PathInfo>? = null): LinkRef? {
        val targetRef = resolver.resolve(this, 0, inList)
        return if (targetRef == null) null else resolver.linkRef(this, targetRef, null, null, null)
    }

    val remoteURL: String? by lazy {
        if (targetRef is ProjectFileRef) {
            targetRef.gitHubVcsRoot?.gitHubBaseUrl.suffixWith('/') + filePath + anchorText
        } else if (isExternal) {
            filePath + anchorText
        } else {
            null
        }
    }

    // convert file name to link
    open fun fileToLink(linkAddress: String): String = urlEncode(linkAddress)

    // convert link to file name
    open fun linkToFile(linkAddress: String): String = urlDecode(linkAddress)

    // prepare text for matching files, wrap in (?:) so matches as a block
    open fun linkToFileRegex(linkText: String) = linkAsFileRegex(linkText)

    // make a copy of everything but fullPath and return the same type of link
    open fun replaceFilePath(fullPath: String, withTargetRef: Boolean = false): LinkRef {
        return LinkRef(containingFile, fullPath, anchor, if (withTargetRef) targetRef else null)
    }

    companion object {
        @JvmStatic fun parseLinkRef(containingFile: FileRef, fullPath: String, targetRef: FileRef?): LinkRef {
            return parseLinkRef(containingFile, fullPath, targetRef, ::LinkRef)
        }

        @JvmStatic fun parseWikiLinkRef(containingFile: FileRef, fullPath: String, targetRef: FileRef?): LinkRef {
            return parseLinkRef(containingFile, fullPath, targetRef, ::WikiLinkRef)
        }

        @JvmStatic fun parseImageLinkRef(containingFile: FileRef, fullPath: String, targetRef: FileRef?): LinkRef {
            return parseLinkRef(containingFile, fullPath, targetRef, ::ImageLinkRef)
        }

        @JvmStatic fun <T : LinkRef> parseLinkRef(containingFile: FileRef, fullPath: String, targetRef: FileRef?, linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?, targetRef: FileRef?) -> T): LinkRef {
            var linkRef = PathInfo.cleanFullPath(fullPath);
            var anchor: String? = null;

            var anchorPos = linkRef.indexOf('#')
            if (anchorPos >= 0) {
                anchor = if (anchorPos == linkRef.lastIndex) EMPTY_STRING else linkRef.substring(anchorPos + 1)
                linkRef = if (anchorPos == 0) EMPTY_STRING else linkRef.substring(0, anchorPos)
            }

            return linkRefType(containingFile, linkRef, anchor, targetRef)
        }

        @JvmStatic fun urlEncode(linkAddress: String): String = mapLinkChars(linkAddress, fileUrlMap)
        @JvmStatic fun urlDecode(linkAddress: String): String = unmapLinkChars(linkAddress, fileUrlMap)

        // prepare text for matching files, wrap in (?:) so matches as a block
        @JvmStatic fun linkAsFileRegex(linkText: String): String {
            return "(?:\\Q" + urlDecode(linkText) + "\\E)"
        }

        @JvmStatic fun mapLinkChars(linkAddress: String, charMap: Map<String, String>): String {
            var result = linkAddress
            for (pair in charMap) {
                result = result.replace(pair.key, pair.value)
            }
            return result
        }

        @JvmStatic fun unmapLinkChars(linkAddress: String, charMap: Map<String, String>): String {
            var result = linkAddress
            for (pair in charMap) {
                result = result.replace(pair.value, pair.key)
            }
            return result
        }

        // char in file name to link map
        @JvmStatic @JvmField
        val fileUrlMap = mapOf<String, String>(
                Pair(" ", "%20"),
                Pair("!", "%21"),
                Pair("#", "%23"),
                Pair("$", "%24"),
                Pair("%", "%25"),
                Pair("&", "%26"),
                Pair("'", "%27"),
                Pair("(", "%28"),
                Pair(")", "%29"),
                Pair("*", "%2A"),
                Pair("+", "%2B"),
                Pair(",", "%2C"),
                //Pair("/", "%2F"), // not supported, used for directory separator
                Pair(":", "%3A"),
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

        // CAUTION: just copies link address without figuring out whether it will resolve as is
        @JvmStatic fun from(linkRef: LinkRef): LinkRef? {
            return when (linkRef) {
                is ImageLinkRef ->
                    LinkRef(linkRef.containingFile, if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileNameNoExt else linkRef.fileName, linkRef.anchor, linkRef.targetRef)
                is WikiLinkRef ->
                    LinkRef(linkRef.containingFile, if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileNameNoExt else WikiLinkRef.linkAsFile(linkRef.fileName), linkRef.anchor, linkRef.targetRef)
                else -> linkRef
            }
        }
    }
}

// this is a [[]] style link ref
open class WikiLinkRef(containingFile: FileRef, fullPath: String, anchor: String?, targetRef: FileRef?) : LinkRef(containingFile, fullPath, anchor, targetRef) {
    override val linkExtensions: Array<String>
        get() = WIKI_PAGE_EXTENSIONS

    // convert file name to link
    override fun fileToLink(linkAddress: String): String = fileAsLink(linkAddress)

    // convert link to file name
    override fun linkToFile(linkAddress: String): String = linkAsFile(linkAddress)

    // prepare text for matching files, wrap in (?:) so matches as a block
    override fun linkToFileRegex(linkText: String) = linkAsFileRegex(linkText)

    // make a copy of everything but fullPath and return the same type of link
    override  fun replaceFilePath(fullPath: String, withTargetRef: Boolean): LinkRef {
        return WikiLinkRef(containingFile, fullPath, anchor, if (withTargetRef) targetRef else null)
    }

    companion object {
        // convert file name to link, usually url encode
        @JvmStatic fun fileAsLink(linkAddress: String): String = linkAddress.replace('-', ' ')

        // convert link to file name, usually url decode
        @JvmStatic fun linkAsFile(linkAddress: String): String = mapLinkChars(linkAddress, wikiLinkMap)

        // prepare text for matching files, wrap in (?:) so matches as a block
        @JvmStatic fun linkAsFileRegex(linkText: String): String {
            return "(?:\\Q" + linkText.replace(wikiLinkMatchRegex.toRegex(), wikiLinkReplaceRegex) + "\\E)"
        }

        @JvmStatic @JvmField
        val wikiLinkMap = mapOf<String, String>(
                Pair(" ", "-"),
                Pair("+", "-"),
                Pair("/", "-"),
                Pair("<", "-"),
                Pair(">", "-")
        )

        // pathText.replace("-| ".toRegex(), "\\\\E(?:-| )\\\\Q")
        val wikiLinkReplaceRegex by lazy { "\\\\E(?:-|\\\\Q" + wikiLinkMap.keys.splice("\\\\E|\\\\Q") + "\\\\E)\\\\Q" }
        val wikiLinkMatchRegex = "-| "

        // CAUTION: just copies link address without figuring out whether it will resolve as is
        @JvmStatic fun from(linkRef: LinkRef): WikiLinkRef? {
            return when (linkRef) {
                is ImageLinkRef -> null
                is WikiLinkRef -> linkRef
                else -> {
                    WikiLinkRef(linkRef.containingFile, fileAsLink(if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileNameNoExt else linkRef.fileName), linkRef.anchor, linkRef.targetRef)
                }
            }
        }
    }
}

open class ImageLinkRef(containingFile: FileRef, fullPath: String, anchor: String?, targetRef: FileRef?) : LinkRef(containingFile, fullPath, anchor, targetRef) {
    override val linkExtensions: Array<String>
        get() = IMAGE_EXTENSIONS

    // make a copy of everything but fullPath and targetRef and return the same type of link
    override  fun replaceFilePath(fullPath: String, withTargetRef: Boolean): LinkRef {
        return ImageLinkRef(containingFile, fullPath, anchor, if (withTargetRef) targetRef else null)
    }

    // CAUTION: just copies link address without figuring out whether it will resolve as is
    companion object {
        @JvmStatic fun from(linkRef: LinkRef): LinkRef? {
            return when (linkRef) {
                is ImageLinkRef -> linkRef
                is WikiLinkRef -> null
                else ->
                    // TODO: add validation for type of file and extension and return null when it is not possible to convert
                    ImageLinkRef(linkRef.containingFile, if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileName else linkRef.fileName, linkRef.anchor, linkRef.targetRef)
            }
        }
    }
}

