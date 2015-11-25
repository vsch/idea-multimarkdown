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

import java.util.*

class GitHubLinkInspector(val resolver: GitHubLinkResolver) {
    companion object {
        @JvmStatic val ID_TARGET_HAS_SPACES = "TARGET_HAS_SPACES"
        @JvmStatic val ID_CASE_MISMATCH = "CASE_MISMATCH"
        @JvmStatic val ID_WIKI_LINK_HAS_DASHES = "WIKI_LINK_HAS_DASHES"
        @JvmStatic val ID_NOT_UNDER_WIKI_HOME = "NOT_UNDER_WIKI_HOME"
        @JvmStatic val ID_TARGET_NOT_WIKI_PAGE_EXT = "TARGET_NOT_WIKI_PAGE_EXT"
        @JvmStatic val ID_NOT_UNDER_SOURCE_WIKI_HOME = "NOT_UNDER_SOURCE_WIKI_HOME"
        @JvmStatic val ID_TARGET_NAME_HAS_ANCHOR = "TARGET_NAME_HAS_ANCHOR"
        @JvmStatic val ID_TARGET_PATH_HAS_ANCHOR = "TARGET_PATH_HAS_ANCHOR"
        @JvmStatic val ID_WIKI_LINK_HAS_SLASH = "WIKI_LINK_HAS_SLASH"
        @JvmStatic val ID_WIKI_LINK_HAS_SUBDIR = "WIKI_LINK_HAS_SUBDIR"
        @JvmStatic val ID_WIKI_LINK_HAS_ONLY_ANCHOR = "WIKI_LINK_HAS_ONLY_ANCHOR"
        @JvmStatic val ID_LINK_TARGETS_WIKI_HAS_EXT = "LINK_TARGETS_WIKI_HAS_EXT"
        @JvmStatic val ID_LINK_TARGETS_WIKI_HAS_BAD_EXT = "LINK_TARGETS_WIKI_HAS_BAD_EXT"
        @JvmStatic val ID_NOT_UNDER_SAME_REPO = "NOT_UNDER_SAME_REPO"
        @JvmStatic val ID_TARGET_NOT_UNDER_VCS = "TARGET_NOT_UNDER_VCS"
    }

    internal class Context(val resolver: GitHubLinkResolver, val linkRef: LinkRef, val targetRef: FileRef) {
        val results = ArrayList<InspectionResult>()

        val linkAddress: String by lazy() {
            resolver.linkAddress(linkRef, targetRef, linkRef.hasExt)
        }

        val linkAddressNoExt: String by lazy() {
            resolver.linkAddress(linkRef, targetRef, false)
        }

        fun INSPECT_LINK_TARGET_HAS_SPACES() {
            if (targetRef.containsSpaces()) {
                val severity = if (linkRef is WikiLinkRef) Severity.WEAK_WARNING else Severity.WARNING
                results.add(InspectionResult(ID_TARGET_HAS_SPACES, severity, null, targetRef.filePath.replace(' ', '-')))
            }
        }

        fun INSPECT_LINK_CASE_MISMATCH() {
            if (linkRef is WikiLinkRef) {
                if (resolver.equalLinks(linkRef.filePath, linkAddress, ignoreCase = true) && !resolver.equalLinks(linkRef.filePath, linkAddress, ignoreCase = false)) {
                    results.add(InspectionResult(ID_CASE_MISMATCH, Severity.WARNING, linkAddress, targetRef.path.endWith('/') + linkRef.linkToFile(linkRef.fileNameNoExt) + targetRef.ext.startWith('.')))
                }
            } else {
                if (linkRef.filePath.equals(linkAddress, ignoreCase = true) && !linkRef.filePath.equals(linkAddress, ignoreCase = false)) {
                    results.add(InspectionResult(ID_CASE_MISMATCH, Severity.ERROR, linkAddress, targetRef.path.endWith('/') + linkRef.linkToFile(linkRef.fileNameNoExt) + linkRef.ext.ifEmpty(targetRef.ext).startWith('.')))
                }
            }
        }

        fun INSPECT_LINK_TARGET_HAS_ANCHOR() {
            if (targetRef.pathContainsAnchor()) {
                results.add(InspectionResult(ID_TARGET_PATH_HAS_ANCHOR, Severity.WARNING, null, null))
            }

            if (targetRef.fileNameContainsAnchor()) {
                results.add(InspectionResult(ID_TARGET_NAME_HAS_ANCHOR, Severity.WARNING, null, targetRef.filePath.replace("#", "")))
            }
        }

        fun INSPECT_LINK_TARGETS_WIKI_HAS_EXT() {
            // wiki links with extensions only resolve to files in the main wiki directory and then they resolve to raw unprocessed source
            // explicit links to wiki pages with extension have to specify the full directory path to resolve, again to raw
            // no extension links
            if (targetRef.isWikiPage) {
                val anchorInfo = PathInfo(linkRef.anchor.orEmpty())
                if (linkRef.anchor != null && anchorInfo.isWikiPageExt) {
                    if (resolver.wasAnchorUsedInMatch(linkRef, targetRef)) {
                        // resolves to raw
                        results.add(InspectionResult(ID_LINK_TARGETS_WIKI_HAS_EXT, Severity.WARNING, linkAddressNoExt, null))
                        if (anchorInfo.ext != targetRef.ext) {
                            results.add(InspectionResult(ID_LINK_TARGETS_WIKI_HAS_BAD_EXT, Severity.ERROR, linkAddress, null))
                        }
                    }
                } else if (linkRef.isWikiPageExt && !resolver.wasAnchorUsedInMatch(linkRef, targetRef)) {
                    // resolves to raw
                    results.add(InspectionResult(ID_LINK_TARGETS_WIKI_HAS_EXT, Severity.WARNING, linkAddressNoExt, null))
                    if (linkRef.ext != targetRef.ext) {
                        results.add(InspectionResult(ID_LINK_TARGETS_WIKI_HAS_BAD_EXT, Severity.ERROR, linkAddress, null))
                    }
                }
            }
        }

        fun INSPECT_LINK_REPO() {
            if (linkRef !is WikiLinkRef && linkRef.isRelative) {
                val targetGitHubRepoPath = resolver.projectResolver.vcsRepoBase(targetRef)
                val sourceGitHubRepoPath = resolver.projectResolver.vcsRepoBase(linkRef.containingFile)

                if (targetGitHubRepoPath != null || sourceGitHubRepoPath != null) {
                    if (targetRef.isUnderWikiDir) {
                        if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !targetGitHubRepoPath.startsWith(sourceGitHubRepoPath))
                            results.add(InspectionResult(ID_NOT_UNDER_SAME_REPO, Severity.ERROR, linkAddress, null))
                    } else {
                        if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !sourceGitHubRepoPath.startsWith(targetGitHubRepoPath))
                            results.add(InspectionResult(ID_NOT_UNDER_SAME_REPO, Severity.ERROR, linkAddress, null))
                    }
                }
            }
        }

        fun INSPECT_LINK_TARGET_VCS() {
            if (!resolver.projectResolver.isUnderVcs(targetRef)) {
                results.add(InspectionResult(ID_TARGET_NOT_UNDER_VCS, Severity.WARNING, null, null))
            }
        }

        fun INSPECT_WIKI_LINK_HAS_DASHES() {
            assert (linkRef is WikiLinkRef)
            if (linkRef.filePath.indexOf('-') >= 0) {
                results.add(InspectionResult(ID_WIKI_LINK_HAS_DASHES, Severity.WEAK_WARNING, linkRef.filePath.replace('-', ' '), null))
            }
        }

        fun INSPECT_WIKI_TARGET_HOME() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.containingFile.isWikiPage) {
                if (!targetRef.isUnderWikiDir) {
                    results.add(InspectionResult(ID_NOT_UNDER_WIKI_HOME, Severity.ERROR, null, targetRef.filePath.replace(' ', '-')))
                } else if (!targetRef.wikiDir.startsWith(linkRef.containingFile.wikiDir)) {
                    results.add(InspectionResult(ID_NOT_UNDER_SOURCE_WIKI_HOME, Severity.ERROR, null, targetRef.filePath.replace(' ', '-')))
                }
            }
        }

        fun INSPECT_WIKI_TARGET_PAGE_EXT() {
            assert(linkRef is WikiLinkRef)
            if (!targetRef.isWikiPageExt) {
                results.add(InspectionResult(ID_TARGET_NOT_WIKI_PAGE_EXT, Severity.ERROR, null, targetRef.filePathNoExt + PathInfo.WIKI_PAGE_EXTENSION.startWith('.')))
            }
        }

        fun INSPECT_WIKI_LINK_ONLY_HAS_ANCHOR() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.filePath.isEmpty() && linkRef.anchor != null) {
                results.add(InspectionResult(ID_WIKI_LINK_HAS_ONLY_ANCHOR, Severity.ERROR, linkAddress, null))
            }
        }

        fun INSPECT_WIKI_LINK_HAS_SLASH() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.contains('/')) {
                // see if it would resolve to the target without it
                if (resolver.equalLinks(linkRef.fileName, linkAddress)) {
                    results.add(InspectionResult(ID_WIKI_LINK_HAS_SUBDIR, Severity.ERROR, linkAddress, null))
                } else {
                    results.add(InspectionResult(ID_WIKI_LINK_HAS_SLASH, Severity.ERROR, linkAddress, null))
                }
            }
        }
    }


    fun inspect(linkRef: LinkRef, targetRef: FileRef): List<InspectionResult> {
        val context = Context(resolver, linkRef, targetRef)

        context.INSPECT_LINK_TARGET_HAS_SPACES()
        context.INSPECT_LINK_CASE_MISMATCH()
        context.INSPECT_LINK_TARGET_HAS_ANCHOR()
        context.INSPECT_LINK_TARGETS_WIKI_HAS_EXT()
        context.INSPECT_LINK_REPO()
        context.INSPECT_LINK_TARGET_VCS()

        if (linkRef is WikiLinkRef) {
            context.INSPECT_WIKI_LINK_HAS_DASHES()
            context.INSPECT_WIKI_TARGET_HOME()
            context.INSPECT_WIKI_TARGET_PAGE_EXT()
            context.INSPECT_WIKI_LINK_ONLY_HAS_ANCHOR()
            context.INSPECT_WIKI_LINK_HAS_SLASH()
        }

        return context.results
    }
}
