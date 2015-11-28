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

    // convert file name to link, usually url encode
    open fun fileToLink(linkAddress: String): String = convertFileToLink(linkAddress)

    // convert link to file name, usually url decode
    open fun linkToFile(linkAddress: String): String = convertLinkToFile(linkAddress)

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

        @JvmStatic fun encodeLink(linkAddress: String, charMap: Map<String, String>): String {
            var result = linkAddress
            for (pair in charMap) {
                result = result.replace(pair.key, pair.value)
            }
            return result
        }

        @JvmStatic fun decodeLink(linkAddress: String, charMap: Map<String, String>): String {
            var result = linkAddress
            for (pair in charMap) {
                result = result.replace(pair.value, pair.key)
            }
            return result
        }

        @JvmStatic @JvmField
        val fileToLinkMap = mapOf<String, String>(
                Pair(" ", "%20"),
                Pair("#", "%23"),
                Pair("&", "%26"),
                Pair("?", "%3F")
        )

        // convert file name to link, usually url encode
        @JvmStatic fun convertFileToLink(linkAddress: String): String = LinkRef.encodeLink(linkAddress, fileToLinkMap)

        // convert link to file name, usually url decode
        @JvmStatic fun convertLinkToFile(linkAddress: String): String = LinkRef.decodeLink(linkAddress, fileToLinkMap)

        // CAUTION: just copies link address without figuring out whether it will resolve as is
        @JvmStatic fun from(linkRef: LinkRef): LinkRef? {
            return when (linkRef) {
                is ImageLinkRef ->
                    LinkRef(linkRef.containingFile, if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileNameNoExt else linkRef.fileName, linkRef.anchor, linkRef.targetRef)
                is WikiLinkRef ->
                    LinkRef(linkRef.containingFile, if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileNameNoExt else WikiLinkRef.convertLinkToFile(linkRef.fileName), linkRef.anchor, linkRef.targetRef)
                else -> linkRef
            }
        }
    }
}

// this is a [[]] style link ref
open class WikiLinkRef(containingFile: FileRef, fullPath: String, anchor: String?, targetRef: FileRef?) : LinkRef(containingFile, fullPath, anchor, targetRef) {
    override val linkExtensions: Array<String>
        get() = WIKI_PAGE_EXTENSIONS

    // convert file name to link, usually url encode
    override fun fileToLink(linkAddress: String): String = linkAddress.replace('-', ' ')

    // convert link to file name, usually url decode
    override fun linkToFile(linkAddress: String): String = linkAddress.replace(' ', '-')

    companion object {
        // convert file name to link, usually url encode
        @JvmStatic fun convertFileToLink(linkAddress: String): String = linkAddress.replace('-', ' ')

        // convert link to file name, usually url decode
        @JvmStatic fun convertLinkToFile(linkAddress: String): String = linkAddress.replace(' ', '-')

        // CAUTION: just copies link address without figuring out whether it will resolve as is
        @JvmStatic fun from(linkRef: LinkRef): WikiLinkRef? {
            return when (linkRef) {
                is ImageLinkRef -> null
                is WikiLinkRef -> linkRef
                else -> {
                    WikiLinkRef(linkRef.containingFile, convertFileToLink(if (linkRef.filePath.isEmpty()) linkRef.containingFile.fileNameNoExt else linkRef.fileName), linkRef.anchor, linkRef.targetRef)
                }
            }
        }
    }
}

open class ImageLinkRef(containingFile: FileRef, fullPath: String, anchor: String?, targetRef: FileRef?) : LinkRef(containingFile, fullPath, anchor, targetRef) {
    override val linkExtensions: Array<String>
        get() = IMAGE_EXTENSIONS

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

