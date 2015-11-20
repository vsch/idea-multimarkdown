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

open class LinkRef(fullPath: String, val anchor: String?) : PathInfo(fullPath) {
    val hasAnchor: Boolean
        get() = anchor != null

    val isSelfAnchor: Boolean
        get() = fullPath.isEmpty() && hasAnchor

    override val isEmpty: Boolean
        get() = fullPath.isEmpty() && !hasAnchor

    val isDoNothing: Boolean
        get() = isSelfAnchor && (anchor.isNullOrEmpty())

    val anchorText: String
        get() = if (anchor == null) EMPTY_STRING else "#" + anchor

    open val filePathWithAnchor: String
        get() = super.filePath + anchorText

    open val filePathNoExtWithAnchor: String
        get() = super.filePathNoExt + anchorText

    override val isRelative: Boolean
        get() = !isSelfAnchor && isRelative(fullPath)

    override val isAbsolute: Boolean
        get() = isSelfAnchor || isAbsolute(fullPath)

    open val linkExtensions: Array<String>
        get() {
            when (ext) {
                in IMAGE_EXTENSIONS -> return IMAGE_EXTENSIONS
                in MARKDOWN_EXTENSIONS -> return MARKDOWN_EXTENSIONS
                else -> return arrayOf(ext)
            }
        }

    companion object {
        @JvmStatic fun parseLinkRef(fullPath: String): LinkRef {
            var linkRef = fullPath;
            var protocol: String? = null
            var anchor: String? = null;

            for (uri in PathInfo.URI_PREFIXES) {
                if (linkRef.startsWith(uri)) {
                    protocol = uri
                    linkRef = if (uri.length == linkRef.length) EMPTY_STRING else linkRef.substring(uri.length)
                    break;
                }
            }

            var anchorPos = linkRef.indexOf('#')
            if (anchorPos >= 0) {
                anchor = if (anchorPos == linkRef.lastIndex) EMPTY_STRING else linkRef.substring(anchorPos + 1)
                linkRef = if (anchorPos == 0) EMPTY_STRING else linkRef.substring(0, anchorPos)
            }

            return if (protocol != null) UrlLinkRef(protocol.orEmpty(), linkRef, anchor) else LinkRef(linkRef, anchor)
        }
    }
}

open class UrlLinkRef(val protocol: String, fullPath: String, anchor: String?) : LinkRef(fullPath, anchor) {
    override val filePathWithAnchor: String
        get() = protocol + super.filePathWithAnchor

    override val filePathNoExtWithAnchor: String
        get() = protocol + super.filePathNoExtWithAnchor

    override val filePath: String
        get() = protocol + super.filePath

    override val filePathNoExt: String
        get() = protocol + super.filePathNoExt

    override val isAbsolute: Boolean
        get() = true

    override val isRelative: Boolean
        get() = false

    override val isExternal: Boolean
        get() = protocol in PathInfo.EXTERNAL_PREFIXES

    override val isURI: Boolean
        get() = true
}

open class FileLinkRef(val containingFile: FileInfo, fullPath: String, anchor: String?) : LinkRef(fullPath, anchor) {

    val preMatchPattern: String by lazy { getMatchPattern(true) }
    val matchPattern: String by lazy { getMatchPattern(false) }


    open fun getMatchPattern(preMatch: Boolean): String {
    }
}

open class WikiLinkRef(containingFile: FileInfo, fullPath: String, anchor: String?) : FileLinkRef(containingFile, fullPath, anchor) {
    override val linkExtensions: Array<String>
        get() = WIKI_PAGE_EXTENSIONS

    override fun getMatchPattern(preMatch: Boolean): String {
    }
}

open class ImageLinkRef(containingFile: FileInfo, fullPath: String, anchor: String?) : FileLinkRef(containingFile, fullPath, anchor) {
    override val linkExtensions: Array<String>
        get() = IMAGE_EXTENSIONS

    override fun getMatchPattern(preMatch: Boolean): String {
    }

}

