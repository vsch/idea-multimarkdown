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


class GitHubMismatchAnalyzer(val resolverContext: GitHubLinkResolver.ContextImpl) {
    companion object {
        @JvmStatic val REASON_TARGET_HAS_SPACES = 1
        @JvmStatic val REASON_CASE_MISMATCH = 2
        @JvmStatic val REASON_WIKI_PAGEREF_HAS_DASHES = 4
        @JvmStatic val REASON_NOT_UNDER_WIKI_HOME = 8
        @JvmStatic val REASON_TARGET_NOT_WIKI_PAGE_EXT = 16
        @JvmStatic val REASON_NOT_UNDER_SOURCE_WIKI_HOME = 32
        @JvmStatic val REASON_TARGET_NAME_HAS_ANCHOR = 64
        @JvmStatic val REASON_TARGET_PATH_HAS_ANCHOR = 128
        @JvmStatic val REASON_WIKI_PAGEREF_HAS_SLASH = 256
        @JvmStatic val REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH = 512
        @JvmStatic val REASON_WIKI_PAGEREF_HAS_SUBDIR = 1024
        @JvmStatic val REASON_WIKI_PAGEREF_HAS_ONLY_ANCHOR = 2048
        @JvmStatic val REASON_WIKI_PAGEREF_HAS_EXT = 4096

        // flags cannot be reused, linkrefs under wiki home have similar errors as wiki links
        @JvmStatic val REASON_NOT_UNDER_SAME_REPO = 4096
        @JvmStatic val REASON_MISSING_EXTENSION = 8192
        @JvmStatic val REASON_CASE_MISMATCH_IN_FILENAME = 16384
        @JvmStatic val WANT_NO_EXTENSION = 32768
    }

    protected fun computeWikiPageRefReasonsFlags(linkRef: LinkRef, targetRef: FileRef): Int {
        var reasons = 0

        val wikiLinkAddress = resolverContext.linkAddress(linkRef, targetRef, linkRef.hasExt)

        if (targetRef.containsSpaces()) reasons = reasons or REASON_TARGET_HAS_SPACES

        if (linkRef.filePath.replace('-', ' ').equals(wikiLinkAddress.replace('-', ' '), ignoreCase = true) && linkRef.filePath.replace('-', ' ') != wikiLinkAddress.replace('-', ' '))
            reasons = reasons or REASON_CASE_MISMATCH

        if (linkRef.filePath.indexOf('-') >= 0) reasons = reasons or REASON_WIKI_PAGEREF_HAS_DASHES

        if (linkRef.containingFile.isWikiPage) {
            if (!targetRef.isUnderWikiDir) reasons = reasons or REASON_NOT_UNDER_WIKI_HOME
            else if (!targetRef.wikiDir.startsWith(linkRef.containingFile.wikiDir)) reasons = reasons or REASON_NOT_UNDER_SOURCE_WIKI_HOME
            if (!targetRef.isWikiPageExt) reasons = reasons or REASON_TARGET_NOT_WIKI_PAGE_EXT
        }

        if (targetRef.pathContainsAnchor()) reasons = reasons or REASON_TARGET_PATH_HAS_ANCHOR
        if (targetRef.fileNameContainsAnchor()) reasons = reasons or REASON_TARGET_NAME_HAS_ANCHOR

        if (linkRef.containingFile.filePath == targetRef.filePath && linkRef.filePath.startsWith("#")) {
            reasons = reasons or REASON_WIKI_PAGEREF_HAS_ONLY_ANCHOR
        }

        // From FileReferenceLinkGitHubRules
        if (linkRef.anchor != null && PathInfo(linkRef.anchor).isWikiPageExt) {
            if (resolverContext.wasAnchorUsedInMatch(linkRef, targetRef)) reasons = reasons or REASON_WIKI_PAGEREF_HAS_EXT
        } else if (linkRef.isWikiPageExt && !resolverContext.wasAnchorUsedInMatch(linkRef, targetRef)) {
            reasons = reasons or REASON_WIKI_PAGEREF_HAS_EXT
        }

        if (linkRef.contains('/')) {
            // see if it would resolve to the target without it
            if (resolverContext.equivalentWikiLinkRef(linkRef.fileName, wikiLinkAddress)) {
                reasons = reasons or REASON_WIKI_PAGEREF_HAS_SUBDIR
            } else if (resolverContext.equivalentWikiLinkRef(linkRef.fileName.replace("/", ""), wikiLinkAddress)) {
                reasons = reasons or REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH
            } else {
                reasons = reasons or REASON_WIKI_PAGEREF_HAS_SLASH
            }
        }
        return reasons
    }

    protected fun computeLinkRefReasonsFlags(linkRef: LinkRef, targetRef: FileRef): Int {
        var reasons = if (linkRef.containingFile.isWikiPage) WANT_NO_EXTENSION else 0
        val linkAddress = resolverContext.linkAddress(linkRef, targetRef, linkRef.hasExt)

        if (targetRef.containsSpaces()) reasons = reasons or REASON_TARGET_HAS_SPACES

        if (linkRef.hasExt) {
            if (linkRef.filePath.equals(linkAddress, ignoreCase = true) && linkRef.filePath.equals(linkAddress, ignoreCase = false)) {
                reasons = reasons or REASON_CASE_MISMATCH
                if (targetRef.fileName != linkRef.fileName) reasons = reasons or REASON_CASE_MISMATCH_IN_FILENAME
            }
        } else {
            if (linkRef.filePathNoExt.equals(linkAddress, ignoreCase = true) && linkRef.filePathNoExt.equals(linkAddress, ignoreCase = false)) {
                reasons = reasons or REASON_CASE_MISMATCH
                if (targetRef.fileName != linkRef.fileName) reasons = reasons or REASON_CASE_MISMATCH_IN_FILENAME
            }
        }

        val targetGitHubRepoPath = resolverContext.projectResolver.vcsRepoBase(targetRef)
        val sourceGitHubRepoPath = resolverContext.projectResolver.vcsRepoBase(linkRef.containingFile)

        if (targetGitHubRepoPath != null || sourceGitHubRepoPath != null) {
            if (targetRef.isUnderWikiDir) {
                if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !targetGitHubRepoPath.startsWith(sourceGitHubRepoPath))
                    reasons = reasons or REASON_NOT_UNDER_SAME_REPO
            } else {
                if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !sourceGitHubRepoPath.startsWith(targetGitHubRepoPath))
                    reasons = reasons or REASON_NOT_UNDER_SAME_REPO
            }
        }

        if (targetRef.pathContainsAnchor()) reasons = reasons or REASON_TARGET_PATH_HAS_ANCHOR
        if (targetRef.fileNameContainsAnchor()) reasons = reasons or REASON_TARGET_NAME_HAS_ANCHOR

        // FileReferenceLinkGitHubRules
        if (linkRef.containingFile.isWikiPage) {
            if (!targetRef.isUnderWikiDir)
                reasons = reasons or REASON_NOT_UNDER_WIKI_HOME
            else if (!targetRef.wikiDir.startsWith(linkRef.containingFile.wikiDir)) reasons = reasons or REASON_NOT_UNDER_SOURCE_WIKI_HOME

            if (!targetRef.isWikiPageExt) reasons = reasons or REASON_TARGET_NOT_WIKI_PAGE_EXT
        }

        if (linkRef.contains('/')) {
            // see if it would resolve to the target without it
            if (resolverContext.equivalentWikiLinkRef(linkRef.fileName, linkAddress)) {
                reasons = reasons or REASON_WIKI_PAGEREF_HAS_SUBDIR
            } else if (resolverContext.equivalentWikiLinkRef(linkRef.fileName.replace("/", ""), linkAddress)) {
                reasons = reasons or REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH
            } else {
                reasons = reasons or REASON_WIKI_PAGEREF_HAS_SLASH
            }
        }
        return reasons
    }

    class InaccessibleLinkRefReasons internal constructor(internal val reasons: Int, internal val linkRef: String, internal val referenceLink: FileReferenceLink) {

        fun caseMismatch(): Boolean = (reasons and REASON_CASE_MISMATCH) != 0
        fun caseMismatchInFileName(): Boolean = (reasons and REASON_CASE_MISMATCH_IN_FILENAME) != 0
        fun caseMismatchLinkRefFixed(): String = if ((reasons and WANT_NO_EXTENSION) == 0) referenceLink.linkRef else referenceLink.linkRefNoExt
        fun caseMismatchFileNameFixed(): String {
            val pathInfo = FilePathInfo(linkRef)
            return if ((reasons and WANT_NO_EXTENSION) == 0) pathInfo.fileName else pathInfo.fileNameNoExt
        }

        fun targetNotInSameRepoHome(): Boolean = (reasons and REASON_NOT_UNDER_SAME_REPO) != 0
        fun targetNotInWikiHome(): Boolean = (reasons and REASON_NOT_UNDER_WIKI_HOME) != 0
        fun targetNotInSameWikiHome(): Boolean = (reasons and REASON_NOT_UNDER_SOURCE_WIKI_HOME) != 0

        fun targetNameHasAnchor(): Boolean = (reasons and REASON_TARGET_NAME_HAS_ANCHOR) != 0
        fun targetNameHasAnchorFixed(): String = referenceLink.fileNameWithAnchor.replace("#", "")
        fun targetPathHasAnchor(): Boolean = (reasons and REASON_TARGET_PATH_HAS_ANCHOR) != 0

        // GitHub rules
        fun linkRefHasSlash(): Boolean = (reasons and REASON_WIKI_PAGEREF_HAS_SLASH) != 0
        fun linkRefHasFixableSlash(): Boolean = (reasons and REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH) != 0
        fun linkRefHasSlashFixed(): String = linkRef.replace("/", "")

        fun linkRefHasSubDir(): Boolean = (reasons and REASON_WIKI_PAGEREF_HAS_SUBDIR) != 0
        fun linkRefHasSubDirFixed(): String = FilePathInfo(linkRef).fileNameWithAnchor
    }


    class InaccessibleWikiPageReasons internal constructor(internal val reasons: Int, val linkRef: LinkRef, val targetRef: FileRef, val wikiRef: String) {
        fun targetNameHasSpaces(): Boolean = (reasons and REASON_TARGET_HAS_SPACES) != 0
        fun targetNameHasSpacedFixed(): String = wikiRef
        fun caseMismatchOnly(): Boolean = (reasons and REASON_CASE_MISMATCH) != 0 && reasons == REASON_CASE_MISMATCH
        fun caseMismatch(): Boolean = (reasons and REASON_CASE_MISMATCH) != 0
        fun caseMismatchWikiRefFixed(): String = wikiRef
        fun caseMismatchFileNameFixed(): String = linkRef.fileName.replace(' ', '-') + (if (linkRef.hasExt) "" else PathInfo.WIKI_PAGE_EXTENSION)
        fun wikiRefHasDashes(): Boolean = (reasons and REASON_WIKI_PAGEREF_HAS_DASHES) != 0
        fun wikiRefHasDashesFixed(): String = wikiRef
        fun targetNotWikiPageExt(): Boolean = (reasons and REASON_TARGET_NOT_WIKI_PAGE_EXT) != 0
        fun targetNotWikiPageExtFixed(): String = targetRef.fileNameNoExt + PathInfo.WIKI_PAGE_EXTENSION
        fun targetNotInWikiHome(): Boolean = (reasons and REASON_NOT_UNDER_WIKI_HOME) != 0
        fun targetNotInWikiHomeFixed(): String = linkRef.containingFile.wikiDir + targetRef.fileName
        fun targetNotInSameWikiHome(): Boolean = (reasons and REASON_NOT_UNDER_SOURCE_WIKI_HOME) != 0
        fun targetNotInSameWikiHomeFixed(): String = linkRef.containingFile.wikiDir + targetRef.fileName
        fun targetNameHasAnchor(): Boolean = (reasons and REASON_TARGET_NAME_HAS_ANCHOR) != 0
        fun targetNameHasAnchorFixed(): String = targetRef.fileName.replace("#", "")
        fun targetPathHasAnchor(): Boolean = (reasons and REASON_TARGET_PATH_HAS_ANCHOR) != 0
        fun wikiRefHasExt(): Boolean = (reasons and REASON_WIKI_PAGEREF_HAS_EXT) != 0
        fun wikiRefHasExtFixed(): String = wikiRef
        fun wikiRefHasOnlyAnchor(): Boolean = (reasons and REASON_WIKI_PAGEREF_HAS_ONLY_ANCHOR) != 0
        fun wikiRefHasOnlyAnchorFixed(): String = targetRef.fileNameNoExt + wikiRef

        // from FileReferenceLinkGitHubRules
        fun wikiRefHasSlash(): Boolean = (reasons and REASON_WIKI_PAGEREF_HAS_SLASH) != 0
        fun wikiRefHasFixableSlash(): Boolean = (reasons and REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH) != 0
        fun wikiRefHasSlashFixed(): String = wikiRef.replace("/", "")
        fun wikiRefHasSubDir(): Boolean = (reasons and REASON_WIKI_PAGEREF_HAS_SUBDIR) != 0
        fun wikiRefHasSubDirFixed(): String = FilePathInfo(wikiRef).fileNameWithAnchor
    }
}
