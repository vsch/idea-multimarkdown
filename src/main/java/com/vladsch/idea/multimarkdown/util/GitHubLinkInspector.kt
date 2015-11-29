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

import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLinkRef
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiLinkText
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownReferenceWikiLinkRef
import java.util.*

class GitHubLinkInspector(val resolver: GitHubLinkResolver) {
    companion object {
        @JvmStatic @JvmField val ID_TARGET_HAS_SPACES = "TARGET_HAS_SPACES"
        @JvmStatic @JvmField val ID_CASE_MISMATCH = "CASE_MISMATCH"
        @JvmStatic @JvmField val ID_WIKI_LINK_HAS_DASHES = "WIKI_LINK_HAS_DASHES"
        @JvmStatic @JvmField val ID_NOT_UNDER_WIKI_HOME = "NOT_UNDER_WIKI_HOME"
        @JvmStatic @JvmField val ID_TARGET_NOT_WIKI_PAGE_EXT = "TARGET_NOT_WIKI_PAGE_EXT"
        @JvmStatic @JvmField val ID_NOT_UNDER_SOURCE_WIKI_HOME = "NOT_UNDER_SOURCE_WIKI_HOME"
        @JvmStatic @JvmField val ID_TARGET_NAME_HAS_ANCHOR = "TARGET_NAME_HAS_ANCHOR"
        @JvmStatic @JvmField val ID_TARGET_PATH_HAS_ANCHOR = "TARGET_PATH_HAS_ANCHOR"
        @JvmStatic @JvmField val ID_WIKI_LINK_HAS_SLASH = "WIKI_LINK_HAS_SLASH"
        @JvmStatic @JvmField val ID_WIKI_LINK_HAS_SUBDIR = "WIKI_LINK_HAS_SUBDIR"
        @JvmStatic @JvmField val ID_WIKI_LINK_HAS_ONLY_ANCHOR = "WIKI_LINK_HAS_ONLY_ANCHOR"
        @JvmStatic @JvmField val ID_LINK_TARGETS_WIKI_HAS_EXT = "LINK_TARGETS_WIKI_HAS_EXT"
        @JvmStatic @JvmField val ID_LINK_TARGETS_WIKI_HAS_BAD_EXT = "LINK_TARGETS_WIKI_HAS_BAD_EXT"
        @JvmStatic @JvmField val ID_NOT_UNDER_SAME_REPO = "NOT_UNDER_SAME_REPO"
        @JvmStatic @JvmField val ID_TARGET_NOT_UNDER_VCS = "TARGET_NOT_UNDER_VCS"
        @JvmStatic @JvmField val ID_LINK_NEEDS_EXT = "LINK_NEEDS_EXT"
        @JvmStatic @JvmField val ID_LINK_HAS_BAD_EXT = "LINK_HAS_BAD_EXT"
        @JvmStatic @JvmField val ID_LINK_TARGET_NEEDS_EXT = "LINK_TARGET_NEEDS_EXT"
        @JvmStatic @JvmField val ID_LINK_TARGET_HAS_BAD_EXT = "LINK_TARGET_HAS_BAD_EXT"

        // TODO: implment these inspections
        @JvmStatic @JvmField val ID_WIKI_LINK_HAS_REDUNDANT_TEXT = "WIKI_LINK_HAS_REDUNDANT_TEXT"
        @JvmStatic @JvmField val ID_WIKI_LINK_HAS_ADDRESS_TEXT_SWAPPED = "WIKI_LINK_HAS_ADDRESS_TEXT_SWAPPED"
        @JvmStatic @JvmField val ID_WIKI_LINK_TEXT_MATCHES_ANOTHER_TARGET = "WIKI_LINK_TEXT_MATCHES_ANOTHER_TARGET"
        @JvmStatic @JvmField val ID_WIKI_LINK_TEXT_MATCHES_SELF_REF = "TEXT_MATCHES_SELF_REF"
        @JvmStatic @JvmField val ID_WIKI_LINK_NOT_IN_WIKI = "WIKI_LINK_NOT_IN_WIKI"
    }

    internal class Context(val resolver: GitHubLinkResolver, val linkRef: LinkRef, val targetRef: FileRef, val referenceId: Any?) {
        val results = ArrayList<InspectionResult>()

        fun addResult(result: InspectionResult) {
            result.referenceId = referenceId;
            results.add(result);
        }

        val linkAddress: String by lazy() {
            resolver.linkAddress(linkRef, targetRef, linkRef.hasExt)
        }

        val linkAddressNoExt: String by lazy() {
            resolver.linkAddress(linkRef, targetRef, false)
        }

        fun INSPECT_LINK_TARGET_HAS_SPACES() {
            if (targetRef.containsSpaces()) {
                val severity = if (linkRef is WikiLinkRef) Severity.WEAK_WARNING else Severity.WARNING
                addResult(InspectionResult(ID_TARGET_HAS_SPACES, severity, null, targetRef.filePath.replace(' ', '-')))
            }
        }

        fun INSPECT_LINK_CASE_MISMATCH() {
            if (linkRef is WikiLinkRef) {
                if (resolver.equalLinks(linkRef.filePath, linkAddress, ignoreCase = true) && !resolver.equalLinks(linkRef.filePath, linkAddress, ignoreCase = false)) {
                    addResult(InspectionResult(ID_CASE_MISMATCH, Severity.WARNING, linkAddress, targetRef.path.suffixWith('/') + linkRef.linkToFile(linkRef.fileNameNoExt) + targetRef.ext.prefixWith('.')))
                }
            } else {
                if (linkRef.filePath.equals(linkAddress, ignoreCase = true) && !linkRef.filePath.equals(linkAddress, ignoreCase = false)) {
                    val fixedPath = targetRef.path.suffixWith('/') + linkRef.linkToFile(linkRef.fileNameNoExt) + linkRef.ext.ifEmpty(targetRef.ext).prefixWith('.')
                    val fixedLinkRef = FileRef(linkAddress)
                    // caution: no fixed file name provided if the case mismatch is in the path not the file name
                    // test: no fixed file name provided if the case mismatch is in the path not the file name
                    addResult(InspectionResult(ID_CASE_MISMATCH, Severity.ERROR, linkAddress, if (linkRef.path == fixedLinkRef.path) fixedPath else null))
                }
            }
        }

        // test: link and link target extension inspection
        fun INSPECT_LINK_TARGET_EXT() {
            if (linkRef is ImageLinkRef ) {
                if (!linkRef.hasExt) {
                    addResult(InspectionResult(ID_LINK_NEEDS_EXT, Severity.ERROR, linkAddress, null))
                }

                if (linkRef.ext != targetRef.ext) {
                    addResult(InspectionResult(ID_LINK_HAS_BAD_EXT, Severity.ERROR, linkAddress, null))
                }

                if (!targetRef.hasExt || !targetRef.isImageExt) {
                    addResult(InspectionResult(if (!linkRef.hasExt) ID_LINK_TARGET_NEEDS_EXT else ID_LINK_TARGET_HAS_BAD_EXT, Severity.WARNING, null, targetRef.path.suffixWith('/') + linkRef.linkToFile(linkRef.fileNameNoExt) + targetRef.ext.prefixWith('.')))
                }
            }
        }

        fun INSPECT_LINK_TARGET_HAS_ANCHOR() {
            if (targetRef.pathContainsAnchor()) {
                addResult(InspectionResult(ID_TARGET_PATH_HAS_ANCHOR, Severity.WARNING, null, null))
            }

            if (targetRef.fileNameContainsAnchor()) {
                addResult(InspectionResult(ID_TARGET_NAME_HAS_ANCHOR, Severity.WARNING, null, targetRef.filePath.replace("#", "")))
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
                        addResult(InspectionResult(ID_LINK_TARGETS_WIKI_HAS_EXT, Severity.WARNING, linkAddressNoExt, null))
                        if (anchorInfo.ext != targetRef.ext) {
                            addResult(InspectionResult(ID_LINK_TARGETS_WIKI_HAS_BAD_EXT, Severity.ERROR, linkAddress, null))
                        }
                    }
                } else if (linkRef.isWikiPageExt && !resolver.wasAnchorUsedInMatch(linkRef, targetRef)) {
                    // resolves to raw
                    addResult(InspectionResult(ID_LINK_TARGETS_WIKI_HAS_EXT, Severity.WARNING, linkAddressNoExt, null))
                    if (linkRef.ext != targetRef.ext) {
                        addResult(InspectionResult(ID_LINK_TARGETS_WIKI_HAS_BAD_EXT, Severity.ERROR, linkAddress, null))
                    }
                }
            }
        }

        fun INSPECT_LINK_REPO() {
            if (linkRef !is WikiLinkRef && linkRef.isRelative) {
                val targetGitHubRepoPath = resolver.projectResolver.vcsRootBase(targetRef)
                val sourceGitHubRepoPath = resolver.projectResolver.vcsRootBase(linkRef.containingFile)

                if (targetGitHubRepoPath != null || sourceGitHubRepoPath != null) {
                    if (targetRef.isUnderWikiDir) {
                        if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !targetGitHubRepoPath.startsWith(sourceGitHubRepoPath))
                            addResult(InspectionResult(ID_NOT_UNDER_SAME_REPO, Severity.ERROR, linkAddress, null))
                    } else {
                        if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !sourceGitHubRepoPath.startsWith(targetGitHubRepoPath))
                            addResult(InspectionResult(ID_NOT_UNDER_SAME_REPO, Severity.ERROR, linkAddress, null))
                    }
                }
            }
        }

        fun INSPECT_LINK_TARGET_VCS() {
            if (!resolver.projectResolver.isUnderVcs(targetRef)) {
                addResult(InspectionResult(ID_TARGET_NOT_UNDER_VCS, Severity.WARNING, null, null))
            }
        }

        fun INSPECT_WIKI_LINK_HAS_DASHES() {
            assert (linkRef is WikiLinkRef)
            if (linkRef.filePath.indexOf('-') >= 0) {
                addResult(InspectionResult(ID_WIKI_LINK_HAS_DASHES, Severity.WEAK_WARNING, linkRef.filePath.replace('-', ' '), null))
            }
        }

        fun INSPECT_WIKI_TARGET_HOME() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.containingFile.isWikiPage) {
                if (!targetRef.isUnderWikiDir) {
                    addResult(InspectionResult(ID_NOT_UNDER_WIKI_HOME, Severity.ERROR, null, targetRef.filePath.replace(' ', '-')))
                } else if (!targetRef.wikiDir.startsWith(linkRef.containingFile.wikiDir)) {
                    addResult(InspectionResult(ID_NOT_UNDER_SOURCE_WIKI_HOME, Severity.ERROR, null, targetRef.filePath.replace(' ', '-')))
                }
            }
        }

        // test: wiki link target has non-markdown extension
        fun INSPECT_WIKI_TARGET_PAGE_EXT() {
            assert(linkRef is WikiLinkRef)
            if (!linkRef.hasExt && !targetRef.isWikiPageExt) {
                addResult(InspectionResult(ID_TARGET_NOT_WIKI_PAGE_EXT, Severity.ERROR, linkAddress, targetRef.filePathNoExt + PathInfo.WIKI_PAGE_EXTENSION.prefixWith('.')))
            }
        }

        fun INSPECT_WIKI_LINK_ONLY_HAS_ANCHOR() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.filePath.isEmpty() && linkRef.anchor != null) {
                addResult(InspectionResult(ID_WIKI_LINK_HAS_ONLY_ANCHOR, Severity.ERROR, linkAddress, null))
            }
        }

        fun INSPECT_WIKI_LINK_NOT_IN_WIKI() {
            assert(linkRef is WikiLinkRef)
            if (!linkRef.containingFile.isWikiPage) {
                addResult(InspectionResult(ID_WIKI_LINK_NOT_IN_WIKI, Severity.ERROR, null, null))
            }
        }

        fun INSPECT_WIKI_LINK_HAS_SLASH() {
            assert(linkRef is WikiLinkRef)
            if (linkRef.contains('/')) {
                // see if it would resolve to the target without it
                if (resolver.equalLinks(linkRef.fileName, linkAddress)) {
                    addResult(InspectionResult(ID_WIKI_LINK_HAS_SUBDIR, Severity.ERROR, linkAddress, null))
                } else {
                    addResult(InspectionResult(ID_WIKI_LINK_HAS_SLASH, Severity.ERROR, linkAddress, null))
                }
            }
        }

        fun INSPECT_WIKI_LINK_TEXT_ADDRESS_SWAP() {
//            assert(linkRef is WikiLinkRef)
//            // see if need to swap link ref and link text
//
//            val wikiPageRef = MultiMarkdownPsiImplUtil.findChildByType(element, MultiMarkdownTypes.WIKI_LINK_REF) as MultiMarkdownWikiLinkRef?
//            val wikiPageRefReference = wikiPageRef?.reference
//
//            if (wikiPageRefReference != null) {
//                val wikiPageText = MultiMarkdownPsiImplUtil.findChildByType(element, MultiMarkdownTypes.WIKI_LINK_TEXT) as MultiMarkdownWikiLinkText?
//
//                val wikiPageTextName = wikiPageText?.name
//                if (wikiPageTextName != null) {
//                    // see if the link title resolves to a page
//                    val containingFile = element.containingFile as MultiMarkdownFile
//
//                    if (wikiPageTextName == wikiPageRef!!.nameWithAnchor) {
//                        // can get rid off the text
//                        //if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX)) {
//                        //    state.createWeakWarningAnnotation(wikiPageText.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.redundant-page-title"));
//                        //    state.annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
//                        //}
//                        //ID_WIKI_LINK_HAS_REDUNDANT_TEXT
//                    } else {
//                        val linkRefInfo = PathInfo(wikiPageTextName)
//                        val accessibleWikiPageRefs = FileReferenceListQuery(element.project).wantMarkdownFiles().gitHubWikiRules().inSource(containingFile).ignoreLinkRefExtension(linkRefInfo.hasWikiPageExt()).matchWikiRef(wikiPageTextName).accessibleWikiPageRefs().postMatchFilter(linkRefInfo, true, false, null)
//
//                        if (accessibleWikiPageRefs.size() === 1) {
//                            if ((wikiPageRefReference as MultiMarkdownReferenceWikiLinkRef?).isResolveRefMissing()) {
//                                //ID_WIKI_LINK_HAS_ADDRESS_TEXT_SWAPPED
//                                //if (!state.alreadyOfferedTypes(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) {
//                                //    state.createErrorAnnotation(element.getTextRange(),
//                                //            MultiMarkdownGlobalSettings.getInstance().githubWikiLinks.getValue()
//                                //                    ? MultiMarkdownBundle.message("annotation.wikilink.ref-title-github")
//                                //                    : MultiMarkdownBundle.message("annotation.wikilink.ref-title-swapped"));
//                                //
//                                //    state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
//                                //
//                                //    if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
//                                //    if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
//                                //}
//                            } else if (accessibleWikiPageRefs.get()[0].getFileNameNoExtAsWikiRef().equals(wikiPageTextName)) {
//                                //ID_WIKI_LINK_TEXT_MATCHES_ANOTHER_TARGET
//                                //if (state.alreadyOfferedTypes(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX, TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
//                                //    state.createInfoAnnotation(wikiPageText.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
//                                //    if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
//                                //    if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
//                                //    if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
//                                //}
//                            }
//                            // TODO: when we can validate existence of anchors add it to the condition below
//                        } else if (wikiPageTextName.startsWith("#")) {
//                            ////ID_WIKI_LINK_TEXT_MATCHES_SELF_REF
//                            //if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
//                            //    state.createInfoAnnotation(wikiPageText.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
//                            //    state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
//                            //}
//                        }
//                    }
//                }
//            }
        }
    }


    fun inspect(linkRef: LinkRef, targetRef: FileRef, referenceId: Any?): List<InspectionResult> {
        val context = Context(resolver, linkRef, targetRef, referenceId)

        context.INSPECT_LINK_TARGET_HAS_SPACES()
        context.INSPECT_LINK_CASE_MISMATCH()
        context.INSPECT_LINK_TARGET_HAS_ANCHOR()
        context.INSPECT_LINK_TARGETS_WIKI_HAS_EXT()
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

        return context.results
    }
}
