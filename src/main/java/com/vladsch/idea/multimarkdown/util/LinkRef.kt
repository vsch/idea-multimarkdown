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
import com.intellij.openapi.vfs.VirtualFileManager

open class LinkRef(val containingFile: FileRef, fullPath: String, anchorTxt: String?) : PathInfo(fullPath) {
    val anchor: String? = anchorTxt?.removePrefix("#")
    val hasAnchor: Boolean
        get() = anchor != null

    val isSelfAnchor: Boolean
        get() = fullPath.isEmpty() && hasAnchor

    override val isEmpty: Boolean
        get() = fullPath.isEmpty() && !hasAnchor

    val isDoNothingAnchor: Boolean
        get() = isSelfAnchor && (anchor.isNullOrEmpty())

    val anchorText: String
        get() = if (anchor == null) EMPTY_STRING else "#" + anchor

    val filePathWithAnchor: String
        get() = super.filePath + anchorText

    val filePathNoExtWithAnchor: String
        get() = super.filePathNoExt + anchorText

    override val isRelative: Boolean
        get() = !isSelfAnchor && isRelative(fullPath)

    override val isLocal: Boolean
        get() = isSelfAnchor || isLocal(fullPath)

    override val isAbsolute: Boolean
        get() = isSelfAnchor || isAbsolute(fullPath)

    override fun toString(): String = filePathWithAnchor

    open val linkExtensions: Array<String>
        get() {
            when {
                ext in IMAGE_EXTENSIONS -> return IMAGE_EXTENSIONS
                ext.isEmpty(), ext in MARKDOWN_EXTENSIONS -> return MARKDOWN_EXTENSIONS
                else -> return arrayOf(ext)
            }
        }

    // convert file name to link, usually url encode
    open fun fileToLink(linkAddress: String): String = linkAddress

    // convert link to file name, usually url decode
    open fun linkToFile(linkAddress: String): String = linkAddress

    companion object {
        @JvmStatic fun parseLinkRef(containingFile: FileRef, fullPath: String): LinkRef {
            return parseLinkRef(containingFile, fullPath, ::FileLinkRef)
        }

        @JvmStatic fun parseFileLinkRef(containingFile: FileRef, fullPath: String): LinkRef {
            return parseLinkRef(containingFile, fullPath, ::FileLinkRef)
        }

        @JvmStatic fun parseWikiLinkRef(containingFile: FileRef, fullPath: String): LinkRef {
            return parseLinkRef(containingFile, fullPath, ::WikiLinkRef)
        }

        @JvmStatic fun parseImageLinkRef(containingFile: FileRef, fullPath: String): LinkRef {
            return parseLinkRef(containingFile, fullPath, ::ImageLinkRef)
        }

        @JvmStatic fun <T : LinkRef> parseLinkRef(containingFile: FileRef, fullPath: String, linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?) -> T): LinkRef {
            var linkRef = fullPath;
            var anchor: String? = null;

            var anchorPos = linkRef.indexOf('#')
            if (anchorPos >= 0) {
                anchor = if (anchorPos == linkRef.lastIndex) EMPTY_STRING else linkRef.substring(anchorPos + 1)
                linkRef = if (anchorPos == 0) EMPTY_STRING else linkRef.substring(0, anchorPos)
            }

            return if (isURI(linkRef) && !isLocal(linkRef)) UrlLinkRef(containingFile, linkRef, anchor)
            else linkRefType(containingFile, linkRef, anchor)
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
    }
}

open class UrlLinkRef(containingFile: FileRef, fullPath: String, anchor: String?) : LinkRef(containingFile, fullPath, anchor) {
    override val linkExtensions: Array<String>
        get() = Array(0, { "" })

    override val isAbsolute: Boolean
        get() = true

    override val isRelative: Boolean
        get() = false

    override val isURI: Boolean
        get() = true

    open fun virtualFileRef(project: Project): VirtualFileRef? {
        val virtualFile = if (isLocal) null else VirtualFileManager.getInstance().findFileByUrl(fullPath)
        return if (virtualFile == null) null else VirtualFileRef(virtualFile, project);
    }
}

// this is a generic file link
open class FileLinkRef(containingFile: FileRef, fullPath: String, anchor: String?) : LinkRef(containingFile, fullPath, anchor) {
    // convert file name to link, usually url encode
    override fun fileToLink(linkAddress: String): String = convertFileToLink(linkAddress)

    // convert link to file name, usually url decode
    override fun linkToFile(linkAddress: String): String = convertLinkToFile(linkAddress)

    companion object {
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

        @JvmStatic fun from(fileLinkRef: FileLinkRef): FileLinkRef? {
            return when (fileLinkRef) {
                is ImageLinkRef ->
                    FileLinkRef(fileLinkRef.containingFile, if (fileLinkRef.filePath.isEmpty()) fileLinkRef.containingFile.fileNameNoExt else fileLinkRef.fileName, fileLinkRef.anchor)
                is WikiLinkRef ->
                    FileLinkRef(fileLinkRef.containingFile, if (fileLinkRef.filePath.isEmpty()) fileLinkRef.containingFile.fileNameNoExt else WikiLinkRef.convertLinkToFile(fileLinkRef.fileName), fileLinkRef.anchor)
                else -> fileLinkRef
            }
        }
    }
}

// this is a [[]] style link ref
open class WikiLinkRef(containingFile: FileRef, fullPath: String, anchor: String?) : FileLinkRef(containingFile, fullPath, anchor) {
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

        @JvmStatic fun from(fileLinkRef: FileLinkRef): WikiLinkRef? {
            return when (fileLinkRef) {
                is ImageLinkRef -> null
                is WikiLinkRef -> fileLinkRef
                else -> {
                    WikiLinkRef(fileLinkRef.containingFile, convertFileToLink(if (fileLinkRef.filePath.isEmpty()) fileLinkRef.containingFile.fileNameNoExt else fileLinkRef.fileName), fileLinkRef.anchor)
                }
            }
        }
    }
}

open class ImageLinkRef(containingFile: FileRef, fullPath: String, anchor: String?) : FileLinkRef(containingFile, fullPath, anchor) {
    override val linkExtensions: Array<String>
        get() = IMAGE_EXTENSIONS

    companion object {
        @JvmStatic fun from(fileLinkRef: FileLinkRef): FileLinkRef? {
            return when (fileLinkRef) {
                is ImageLinkRef -> fileLinkRef
                is WikiLinkRef -> null
                else ->
                    // TODO: add validation for type of file and extension and return null when it is not possible to convert
                    ImageLinkRef(fileLinkRef.containingFile, if (fileLinkRef.filePath.isEmpty()) fileLinkRef.containingFile.fileName else fileLinkRef.fileName, fileLinkRef.anchor)
            }
        }
    }
}

