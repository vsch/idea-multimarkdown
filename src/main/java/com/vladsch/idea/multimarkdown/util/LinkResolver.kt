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

abstract class LinkResolver(val projectResolver: LinkResolver.ProjectResolver, val containingFile: FileRef, val branchOrTag: String?) {
    val projectBasePath = projectResolver.projectBasePath
    val project = projectResolver.project

    interface ProjectResolver {
        val projectBasePath:String
        val project:Project?

        fun isUnderVcs(fileRef:FileRef):Boolean
        fun vcsRepoUrlBase(fileRef: FileRef):String?
        fun vcsProjectRepoUrlBase():String?
        fun vcsRepoBase(fileRef: FileRef):String?
        fun repoUrlFor(fileRef:FileRef, withExt:Boolean, anchor:String?): String?
        fun projectFileList(): List<FileRef>?
    }

    companion object {
        @JvmField val ONLY_LOCAL = 1          // file ref that has local resolve
        @JvmField val ONLY_REMOTE = 2         // file ref that has remote resolve
        @JvmField val LOOSE_MATCH = 4         // inexact match for error detection
        @JvmField val REMOTE_URL = 8          // remote URL for file on repo website
        @JvmField val LOCAL_OR_REMOTE = ONLY_LOCAL or ONLY_REMOTE          // remote URL for file on repo website

        fun wantLocal(options: Int): Boolean = (options and ONLY_REMOTE == 0) || (options and ONLY_LOCAL != 0)
        fun wantRemote(options: Int): Boolean = (options and ONLY_LOCAL == 0) || (options and ONLY_REMOTE != 0)
        fun wantRemoteUrl(options: Int): Boolean = (options and REMOTE_URL != 0)
        fun wantLooseMatch(options: Int): Boolean = (options and LOOSE_MATCH != 0)
        fun wantSome(options: Int, flags: Int): Boolean = (options and flags != 0)
        fun wantAll(options: Int, flags: Int): Boolean = (options and flags == flags)
    }

    abstract fun inspect(linkRef: LinkRef, targetRef: FileRef): List<InspectionResult>
    abstract fun isResolved(linkRef: LinkRef, options: Int = 0, inList: List<FileRef>? = null): Boolean
    abstract fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, branchOrTag: String? = null): Boolean
    abstract fun linkAddress(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String? = null, anchor: String? = null): String
    abstract fun multiResolve(linkRef: LinkRef, options: Int = 0, inList: List<FileRef>? = null): List<PathInfo>
    abstract fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String? = null): String
    abstract fun resolve(linkRef: LinkRef, options: Int = 0, inList: List<FileRef>? = null): PathInfo?
}

