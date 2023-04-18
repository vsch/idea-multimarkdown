// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.vcs

import com.vladsch.md.nav.util.*
import com.vladsch.plugin.util.ifEmpty
import com.vladsch.plugin.util.prefixWith
import com.vladsch.plugin.util.suffixWith
import java.util.*

@Suppress("FunctionName")
class GitHubLinkInspector(val resolver: GitHubLinkResolver) {

    companion object {
        const val ID_TARGET_HAS_SPACES: String = "ID_TARGET_HAS_SPACES"
        const val ID_CASE_MISMATCH: String = "ID_CASE_MISMATCH"
        const val ID_WIKI_LINK_HAS_DASHES: String = "ID_WIKI_LINK_HAS_DASHES"
        const val ID_NOT_UNDER_WIKI_HOME: String = "ID_NOT_UNDER_WIKI_HOME"
        const val ID_TARGET_NOT_WIKI_PAGE_EXT: String = "ID_TARGET_NOT_WIKI_PAGE_EXT"
        const val ID_NOT_UNDER_SOURCE_WIKI_HOME: String = "ID_NOT_UNDER_SOURCE_WIKI_HOME"
        const val ID_TARGET_NAME_HAS_ANCHOR: String = "ID_TARGET_NAME_HAS_ANCHOR"
        const val ID_TARGET_PATH_HAS_ANCHOR: String = "ID_TARGET_PATH_HAS_ANCHOR"
        const val ID_WIKI_LINK_HAS_SLASH: String = "ID_WIKI_LINK_HAS_SLASH"
        const val ID_WIKI_LINK_HAS_SUBDIR: String = "ID_WIKI_LINK_HAS_SUBDIR"
        const val ID_WIKI_LINK_HAS_ONLY_ANCHOR: String = "ID_WIKI_LINK_HAS_ONLY_ANCHOR"
        const val ID_LINK_TARGETS_WIKI_HAS_EXT: String = "ID_LINK_TARGETS_WIKI_HAS_EXT"
        const val ID_LINK_TARGETS_WIKI_HAS_BAD_EXT: String = "ID_LINK_TARGETS_WIKI_HAS_BAD_EXT"
        const val ID_NOT_UNDER_SAME_REPO: String = "ID_NOT_UNDER_SAME_REPO"
        const val ID_TARGET_NOT_UNDER_VCS: String = "ID_TARGET_NOT_UNDER_VCS"
        const val ID_LINK_NEEDS_EXT: String = "ID_LINK_NEEDS_EXT"
        const val ID_LINK_HAS_BAD_EXT: String = "ID_LINK_HAS_BAD_EXT"
        const val ID_LINK_TARGET_NEEDS_EXT: String = "ID_LINK_TARGET_NEEDS_EXT"
        const val ID_LINK_TARGET_HAS_BAD_EXT: String = "ID_LINK_TARGET_HAS_BAD_EXT"
        const val ID_WIKI_LINK_NOT_IN_WIKI: String = "ID_WIKI_LINK_NOT_IN_WIKI"
        const val ID_IMAGE_TARGET_NOT_IN_RAW: String = "ID_IMAGE_TARGET_NOT_IN_RAW"
        const val ID_REPO_RELATIVE_ACROSS_VCS_ROOTS: String = "ID_REPO_RELATIVE_ACROSS_VCS_ROOTS"
    }

    private class Context(val resolver: GitHubLinkResolver, private val originalLinkRef: LinkRef, val targetRef: FileRef, val referenceId: Any?) {
        val results = ArrayList<InspectionResult>()

        val linkRef: LinkRef = if (originalLinkRef.isURI) resolver.absoluteToRelativeLink(originalLinkRef) else originalLinkRef
        private val linkRefRemote: LinkRef? = resolver.processMatchOptions(linkRef, targetRef, Want(Local.URL, Remote.URL)) as? LinkRef
        private val linkRefRemoteRaw: LinkRef? = resolver.processMatchOptions(linkRef, targetRef, Want(Local.RAW, Remote.RAW)) as? LinkRef

        private val linkAddressLocal: String = resolver.linkAddress(linkRef, targetRef, null, null, "", true)
        private val linkAddressNoExtLocal: String = stripSubDirAfterWiki(PathInfo(linkAddressLocal).filePathNoExt)

        private val linkAddressRemote: String = linkRefRemote?.filePath ?: linkAddressLocal
        private val linkAddressRemoteRaw: String = linkRefRemoteRaw?.filePath ?: linkAddressLocal
        private val linkAddressNoExtRemote: String = (if (linkRefRemote == null) null else stripSubDirAfterWiki(linkRefRemote.filePathNoExt))
            ?: linkAddressNoExtLocal

        val linkAddress: String = if (originalLinkRef != linkRef) linkAddressRemote else linkAddressLocal
        private val linkAddressNoExt: String = if (originalLinkRef != linkRef) linkAddressNoExtRemote else linkAddressNoExtLocal

        private fun stripSubDirAfterWiki(linkAddress: String): String {
            val pos = linkAddress.indexOf("/wiki/")
            val lastDir = linkAddress.lastIndexOf("/")
            if (pos > 0) {
                return (linkAddress.substring(0, pos + "/wiki/".length) + linkAddress.substring(lastDir + 1)).removeSuffix("/")
            }
            return PathInfo(linkAddress).fileName
        }

        private fun addResult(result: InspectionResult) {
            result.referenceId = referenceId
            results.add(result)
        }

        fun INSPECT_LINK_TARGET_HAS_SPACES() {
            if (targetRef.containsSpaces() && linkRef is WikiLinkRef) {
                val severity = Severity.WEAK_WARNING
                addResult(InspectionResult(null, ID_TARGET_HAS_SPACES, severity, null, targetRef.filePath.replace(' ', '-')))
            }
        }

        private fun hadInspection(id: String): Boolean {
            for (inspection in results) {
                if (inspection.id == id) return true
            }
            return false
        }

        fun INSPECT_LINK_CASE_MISMATCH() {
            if (linkRef is WikiLinkRef) {
                if (resolver.equalLinks(linkRef.filePath, linkAddressLocal, ignoreCase = true) && !resolver.equalLinks(linkRef.filePath, linkAddressLocal, ignoreCase = false)) {
                    addResult(InspectionResult(null, ID_CASE_MISMATCH, Severity.WARNING, linkAddress, targetRef.path.suffixWith('/') + linkRef.linkToFile(linkRef.fileNameNoExt) + targetRef.ext.prefixWith('.')))
                }
            } else {
                if (linkAddressLocal.isEmpty() && linkRef.fileNameNoExt.equals(targetRef.fileNameNoExt, ignoreCase = true) && !linkRef.fileNameNoExt.equals(targetRef.fileNameNoExt, ignoreCase = false)
                    || linkRef.filePath.equals(linkAddressLocal, ignoreCase = true) && !linkRef.filePath.equals(linkAddressLocal, ignoreCase = false)) {
                    val fixedPath = targetRef.path.suffixWith('/') + linkRef.linkToFile(linkRef.fileNameNoExt) + linkRef.ext.ifEmpty(targetRef.ext).prefixWith('.')
                    // CAUTION: no fixed file name provided if the case mismatch is in the path not the file name
                    // TEST: no fixed file name provided if the case mismatch is in the path not the file name
                    val wikiPageHasExt = hadInspection(ID_LINK_TARGETS_WIKI_HAS_EXT)
                    addResult(InspectionResult(null, ID_CASE_MISMATCH, if (targetRef.isWikiPage && !wikiPageHasExt) Severity.WEAK_WARNING else Severity.ERROR, linkAddress, fixedPath))
                }
            }
        }

        // TEST: link and link target extension inspection
        fun INSPECT_LINK_TARGET_EXT() {
            if (linkRef is ImageLinkRef) {
                if (!linkRef.hasExt) {
                    addResult(InspectionResult(null, ID_LINK_NEEDS_EXT, Severity.ERROR, linkAddress, null))
                }

                if (linkRef.ext != targetRef.ext) {
                    addResult(InspectionResult(null, ID_LINK_HAS_BAD_EXT, Severity.ERROR, linkAddress, null))
                }

                if (!targetRef.hasExt || !targetRef.isImageExt) {
                    addResult(InspectionResult(null, if (!linkRef.hasExt) ID_LINK_TARGET_NEEDS_EXT else ID_LINK_TARGET_HAS_BAD_EXT, Severity.WARNING, null, targetRef.path.suffixWith('/') + linkRef.linkToFile(linkRef.fileNameNoExt) + targetRef.ext.prefixWith('.')))
                }
            }
        }

        fun INSPECT_LINK_TARGET_HAS_ANCHOR() {
            if (targetRef.isWikiPage) {
                if (targetRef.pathContainsAnchor() && originalLinkRef.filePath.contains("#")) {
                    addResult(InspectionResult(null, ID_TARGET_PATH_HAS_ANCHOR, Severity.WARNING, null, null))
                }

                if (originalLinkRef is WikiLinkRef) {
                    // wiki links have not URL encoding so if target has #, the link will have it too
                    if (targetRef.fileNameContainsAnchor()) {
                        addResult(InspectionResult(null, ID_TARGET_NAME_HAS_ANCHOR, Severity.WARNING, null, targetRef.filePath.replace("#", "")))
                    }
                } else {
                    val fileNameWithAnchor = originalLinkRef.fileName + originalLinkRef.anchorText
                    if (targetRef.fileNameContainsAnchor() && (fileNameWithAnchor.equals(targetRef.fileName, ignoreCase = true) || fileNameWithAnchor.equals(targetRef.fileNameNoExt, ignoreCase = true))) {
                        addResult(InspectionResult(null, ID_TARGET_NAME_HAS_ANCHOR, Severity.WARNING, null, targetRef.filePath.replace("#", "")))
                    }
                }
            }
        }

        fun INSPECT_LINK_TARGETS_WIKI_HAS_EXT() {
            // NOTE: wiki links with extensions only resolve to files in the main wiki directory and then they resolve to raw unprocessed source
            //   explicit links to wiki pages with extension have to specify the full directory path to resolve to raw content
            if (targetRef.isWikiPage && !originalLinkRef.isURI) {
                val anchorInfo = PathInfo(linkRef.anchor.orEmpty())
                if (linkRef.anchor != null && anchorInfo.isWikiPageExt) {
                    if (resolver.wasAnchorUsedInMatch(linkRef, targetRef)) {
                        // resolves to raw
                        addResult(InspectionResult(null, ID_LINK_TARGETS_WIKI_HAS_EXT, Severity.WARNING, linkAddressNoExt, null))
                        if (anchorInfo.ext != targetRef.ext) {
                            addResult(InspectionResult(null, ID_LINK_TARGETS_WIKI_HAS_BAD_EXT, Severity.ERROR, linkAddress, null))
                        }
                    }
                } else if (linkRef.isWikiPageExt && !resolver.wasAnchorUsedInMatch(linkRef, targetRef)) {
                    // resolves to raw
                    addResult(InspectionResult(null, ID_LINK_TARGETS_WIKI_HAS_EXT, Severity.WARNING, linkAddressNoExt, null))
                    if (linkRef.ext != targetRef.ext) {
                        addResult(InspectionResult(null, ID_LINK_TARGETS_WIKI_HAS_BAD_EXT, Severity.ERROR, linkAddress, null))
                    }
                }
            }
        }

        fun INSPECT_LINK_REPO() {
            val targetVcsRoot = resolver.projectResolver.getVcsRoot(targetRef)
            val sourceVcsRoot = resolver.projectResolver.getVcsRoot(linkRef.containingFile)

            if (targetVcsRoot?.basePath != sourceVcsRoot?.basePath && !originalLinkRef.isURI && originalLinkRef.isAbsolute) {
                addResult(InspectionResult(null, ID_REPO_RELATIVE_ACROSS_VCS_ROOTS, Severity.ERROR, linkAddress, null))
            }

            if (linkRef !is WikiLinkRef && (linkRef.isRelative || linkRef.isRepoRelative)) {
                val targetGitHubRepoPath = resolver.projectResolver.vcsRootBase(targetRef)
                val sourceGitHubRepoPath = resolver.projectResolver.vcsRootBase(linkRef.containingFile)

                if (targetGitHubRepoPath != null || sourceGitHubRepoPath != null) {
                    if (targetRef.isUnderWikiDir) {
                        if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !targetGitHubRepoPath.startsWith(sourceGitHubRepoPath))
                            addResult(InspectionResult(null, ID_NOT_UNDER_SAME_REPO, Severity.ERROR, linkAddress, null))
                    } else {
                        if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !sourceGitHubRepoPath.startsWith(targetGitHubRepoPath))
                            addResult(InspectionResult(null, ID_NOT_UNDER_SAME_REPO, Severity.ERROR, linkAddress, null))
                    }
                }
            }
        }

        fun INSPECT_LINK_TARGET_VCS() {
            // ignore local only if the link is to the file itself
            if (!resolver.projectResolver.isUnderVcs(targetRef) && linkRef.containingFile != targetRef) {
                addResult(InspectionResult(null, ID_TARGET_NOT_UNDER_VCS, Severity.WARNING, null, null))
            }

            //            if (linkRef is ImageLinkRef) {
            // check for all links and annotator will make sure it is image or reference
            // see if it is pointed at the raw/ or blob/ branch
            if (linkRef.containingFile.isWikiPage && !originalLinkRef.isURI) {
                if (!linkRef.filePath.equals(linkAddressLocal, ignoreCase = true) && linkRef.filePath.replace("\\bblob/".toRegex(), "raw/").equals(linkAddressLocal, ignoreCase = true)) {
                    addResult(InspectionResult(null, ID_IMAGE_TARGET_NOT_IN_RAW, Severity.WARNING, linkAddress, null))
                }
            } else if (originalLinkRef !is WikiLinkRef && originalLinkRef.isURL && originalLinkRef.isImageExt) {
                if (!originalLinkRef.filePath.equals(linkAddressRemoteRaw, ignoreCase = true) && originalLinkRef.filePath.replace("\\bblob/".toRegex(), "raw/").equals(linkAddressRemoteRaw, ignoreCase = true)) {
                    addResult(InspectionResult(null, ID_IMAGE_TARGET_NOT_IN_RAW, Severity.WARNING, linkAddressRemoteRaw, null))
                }
            }
            //            }
        }

        fun INSPECT_WIKI_LINK_HAS_DASHES() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.filePath.indexOf('-') >= 0) {
                addResult(InspectionResult(null, ID_WIKI_LINK_HAS_DASHES, Severity.WEAK_WARNING, linkRef.filePath.replace('-', ' '), null))
            }
        }

        fun INSPECT_WIKI_TARGET_HOME() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.containingFile.isWikiPage) {
                if (!targetRef.isUnderWikiDir) {
                    addResult(InspectionResult(null, ID_NOT_UNDER_WIKI_HOME, Severity.ERROR, null, targetRef.filePath.replace(' ', '-')))
                } else if (!targetRef.wikiDir.startsWith(linkRef.containingFile.wikiDir)) {
                    addResult(InspectionResult(null, ID_NOT_UNDER_SOURCE_WIKI_HOME, Severity.ERROR, null, targetRef.filePath.replace(' ', '-')))
                }
            }
        }

        // test: wiki link target has non-markdown extension
        fun INSPECT_WIKI_TARGET_PAGE_EXT() {
            assert(linkRef is WikiLinkRef)
            if (!linkRef.hasExt && !targetRef.isWikiPageExt) {
                addResult(InspectionResult(null, ID_TARGET_NOT_WIKI_PAGE_EXT, Severity.ERROR, linkAddress, targetRef.filePathNoExt + PathInfo.WIKI_PAGE_EXTENSION.prefixWith('.')))
            }
        }

        fun INSPECT_WIKI_LINK_ONLY_HAS_ANCHOR() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.filePath.isEmpty() && linkRef.anchor != null) {
                addResult(InspectionResult(null, ID_WIKI_LINK_HAS_ONLY_ANCHOR, Severity.ERROR, linkAddress, null))
            }
        }

        fun INSPECT_WIKI_LINK_NOT_IN_WIKI() {
            assert(linkRef is WikiLinkRef)
            if (!linkRef.containingFile.isWikiPage) {
                addResult(InspectionResult(null, ID_WIKI_LINK_NOT_IN_WIKI, Severity.ERROR, null, null))
            }
        }

        fun INSPECT_WIKI_LINK_HAS_SLASH() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.contains('/')) {
                // see if it would resolve to the target without it
                if (resolver.equalLinks(linkRef.fileName, linkAddress)) {
                    addResult(InspectionResult(null, ID_WIKI_LINK_HAS_SUBDIR, Severity.ERROR, linkAddress, null))
                } else {
                    addResult(InspectionResult(null, ID_WIKI_LINK_HAS_SLASH, Severity.ERROR, linkAddress, null))
                }
            }
        }
    }

    fun inspect(linkRef: LinkRef, targetRef: FileRef, referenceId: Any?): List<InspectionResult> {
        val context = Context(resolver, linkRef, targetRef, referenceId)

        if (linkRef.filePath.isNotEmpty()) {
            context.INSPECT_LINK_TARGET_HAS_SPACES()
            context.INSPECT_LINK_TARGET_HAS_ANCHOR()
            context.INSPECT_LINK_TARGETS_WIKI_HAS_EXT()
            context.INSPECT_LINK_CASE_MISMATCH() // NOTE: has to be run after WIKI_HAS_EXT it uses the info to decide on severity level
            context.INSPECT_LINK_REPO()
            context.INSPECT_LINK_TARGET_VCS()
            context.INSPECT_LINK_TARGET_EXT()

            if (linkRef is WikiLinkRef) {
                context.INSPECT_WIKI_LINK_HAS_DASHES()
                context.INSPECT_WIKI_TARGET_HOME()
                context.INSPECT_WIKI_TARGET_PAGE_EXT()
                context.INSPECT_WIKI_LINK_ONLY_HAS_ANCHOR()
                context.INSPECT_WIKI_LINK_HAS_SLASH()
                context.INSPECT_WIKI_LINK_NOT_IN_WIKI()
            }
        }
        return context.results
    }
}
