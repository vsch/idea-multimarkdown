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

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import java.util.*

abstract class LinkResolver(val projectResolver: LinkResolver.ProjectResolver, val containingFile: FileRef, val branchOrTag: String?) {
    val projectBasePath = projectResolver.projectBasePath
    val project = projectResolver.project

    interface ProjectResolver {
        val projectBasePath: String
        val project: Project?

        fun isUnderVcs(fileRef: FileRef): Boolean
        fun getVcsRoot(fileRef: FileRef): GitHubVcsRoot?;
        fun vcsRepoBasePath(fileRef: FileRef): String?
        fun vcsRootBase(fileRef: FileRef): String?
        fun projectFileList(fileTypes: List<String>?): List<FileRef>?
    }

    companion object {
        internal val LINK_REF_WAS_URI = 0x8000000                          // original linkref was external, all resolution is done via relative links

        // delegated for convenience
        fun wantLocalType(options: Int) = Want.localType(options)
        fun wantRemoteType(options: Int) = Want.remoteType(options)
        fun wantMatchType(options: Int) = Want.matchType(options)
        fun wantLinksType(options: Int) = Want.linksType(options)
        fun wantExactMatch(options: Int) = Want.exactMatch(options)
        fun wantLooseMatch(options: Int) = Want.looseMatch(options)
        fun wantCompletionMatch(options: Int) = Want.completionMatch(options)
        fun wantLinks(options: Int) = Want.links(options)
        fun wantLocal(options: Int) = Want.local(options)
        fun wantLocalREF(options: Int) = Want.localREF(options)
        fun wantLocalURI(options: Int) = Want.localURI(options)
        fun wantLocalURL(options: Int) = Want.localURL(options)
        fun wantRemote(options: Int) = Want.remote(options)
        fun wantRemoteREF(options: Int) = Want.remoteREF(options)
        fun wantRemoteURI(options: Int) = Want.remoteURI(options)
        fun wantRemoteURL(options: Int) = Want.remoteURL(options)

        fun linkRefWasURI(options: Int): Boolean = (options and LINK_REF_WAS_URI != 0)
    }

    abstract fun inspect(linkRef: LinkRef, targetRef: FileRef, referenceId: Any? = null): List<InspectionResult>
    abstract fun isResolved(linkRef: LinkRef, options: Int = 0, inList: List<PathInfo>? = null): Boolean
    abstract fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean? = null, branchOrTag: String? = null): Boolean
    abstract fun linkAddress(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String? = null, anchor: String? = null): String
    abstract fun multiResolve(linkRef: LinkRef, options: Int = 0, inList: List<PathInfo>? = null): List<PathInfo>
    abstract fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String? = null): String
    abstract fun resolve(linkRef: LinkRef, options: Int = 0, inList: List<PathInfo>? = null): PathInfo?
}

