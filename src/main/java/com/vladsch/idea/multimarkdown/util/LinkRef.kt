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

    companion object {
        @JvmStatic fun parseLinkRef(containingFile: FileRef, fullPath: String, linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?) -> LinkRef = ::FileLinkRef): LinkRef {
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
    }
}

open class UrlLinkRef(containingFile: FileRef, fullPath: String, anchor: String?) : LinkRef(containingFile, fullPath, anchor) {
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

open class FileLinkRef(containingFile: FileRef, fullPath: String, anchor: String?) : LinkRef(containingFile, fullPath, anchor) {

}

open class WikiLinkRef(containingFile: FileRef, fullPath: String, anchor: String?) : FileLinkRef(containingFile, fullPath, anchor) {
    override val linkExtensions: Array<String>
        get() = WIKI_PAGE_EXTENSIONS
}

open class ImageLinkRef(containingFile: FileRef, fullPath: String, anchor: String?) : FileLinkRef(containingFile, fullPath, anchor) {
    override val linkExtensions: Array<String>
        get() = IMAGE_EXTENSIONS
}

