// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.vcs

import com.intellij.openapi.project.Project
import com.vladsch.md.nav.settings.MdRenderingProfile
import com.vladsch.md.nav.util.*

abstract class MdLinkResolver(val projectResolver: ProjectResolver, val containingFile: FileRef, val branchOrTag: String?) {
    val projectBasePath = projectResolver.projectBasePath
    val project = projectResolver.project

    interface ProjectResolver {
        val projectBasePath: String
        val project: Project?

        fun isUnderVcs(fileRef: FileRef): Boolean
        fun isUnderVcsSynced(fileRef: FileRef): Boolean
        fun getVcsRoot(fileRef: FileRef): GitHubVcsRoot?
        fun vcsRepoBasePath(fileRef: FileRef): String?
        fun vcsRootBase(fileRef: FileRef): String?
        fun projectFileList(fileTypes: List<String>?): List<FileRef>?
        fun getVcsRootForUrl(url: String): GitHubVcsRoot?
        fun getGitHubRepo(path: String?): GitHubVcsRoot?
    }

    companion object {
        internal val LINK_REF_WAS_URI = 0x8000000                          // original linkRef was external, all resolution is done via relative links
        internal val LINK_REF_WAS_REPO_REL = 0x4000000                     // original linkRef was repo relative, ie. starts

        // delegated for convenience
        fun wantLocalType(options: Int) = Want.localType(options)

        fun wantRemoteType(options: Int) = Want.remoteType(options)
        fun wantMatchType(options: Int) = Want.matchType(options)
        fun wantLinksType(options: Int) = Want.linksType(options)
        fun wantExactMatch(options: Int) = Want.exactMatch(options)
        fun wantLooseMatch(options: Int) = Want.looseMatch(options)
        fun wantCompletionMatch(options: Int) = Want.completionMatch(options)
        fun wantLinks(options: Int) = Want.links(options)
        fun wantLinksREL(options: Int) = Want.linksREL(options)
        fun wantLinksABS(options: Int) = Want.linksABS(options)
        fun wantLinksURL(options: Int) = Want.linksURL(options)
        fun wantLocal(options: Int) = Want.local(options)
        fun wantLocalREF(options: Int) = Want.localREF(options)
        fun wantLocalURI(options: Int) = Want.localURI(options)
        fun wantLocalURL(options: Int) = Want.localURL(options)
        fun wantRemote(options: Int) = Want.remote(options)
        fun wantRemoteREF(options: Int) = Want.remoteREF(options)
        fun wantRemoteURI(options: Int) = Want.remoteURI(options)
        fun wantRemoteURL(options: Int) = Want.remoteURL(options)

        fun linkRefWasURI(options: Int): Boolean = (options and LINK_REF_WAS_URI != 0)
        fun linkRefWasRepoRel(options: Int): Boolean = (options and LINK_REF_WAS_REPO_REL != 0)
    }

    abstract fun inspect(linkRef: LinkRef, targetRef: FileRef, referenceId: Any? = null): List<InspectionResult>
    abstract fun isResolved(linkRef: LinkRef, options: Int = 0, inList: List<PathInfo>? = null): Boolean
    abstract fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean? = null, branchOrTag: String? = null): Boolean
    abstract fun linkAddress(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String? = null, anchor: String? = null, reduceToAnchor: Boolean): String
    abstract fun multiResolve(linkRef: LinkRef, options: Int = 0, inList: List<PathInfo>? = null): List<PathInfo>
    abstract fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String? = null): String?
    abstract fun resolve(linkRef: LinkRef, options: Int = 0, inList: List<PathInfo>? = null): PathInfo?
    abstract fun isAbsoluteUnchecked(linkRef: PathInfo): Boolean
    abstract fun isExternalUnchecked(linkRef: LinkRef): Boolean
    abstract fun linkEncodingExclusionMap(): Map<String, String>?
    abstract fun renderingProfile(): MdRenderingProfile?
}

