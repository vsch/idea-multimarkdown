/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.annotator;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.license.LicensedFeature;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownReferenceWikiLinkRef;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.vladsch.idea.multimarkdown.annotator.AnnotationState.*;
import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;
import static com.vladsch.idea.multimarkdown.util.GitHubLinkInspector.*;

//public class MultiMarkdownAnnotator extends ExternalAnnotator<String, Set<MultiMarkdownAnnotator.HighlightableToken>> {
public class MultiMarkdownAnnotator implements Annotator {
    private static final Logger LOGGER = Logger.getInstance(MultiMarkdownAnnotator.class);

    protected static final int ANNOTATION_INFO = 0;
    protected static final int ANNOTATION_WEAK_WARNING = 1;
    protected static final int ANNOTATION_WARNING = 2;
    protected static final int ANNOTATION_ERROR = 3;

    @SuppressWarnings("ConstantIfStatement,StatementWithEmptyBody")
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        //noinspection StatementWithEmptyBody
        AnnotationState state = new AnnotationState(holder);

        //noinspection ConstantIfStatement,StatementWithEmptyBody
        if (false) {
        } else if (element instanceof MultiMarkdownWikiLink) {
        } else if (element instanceof MultiMarkdownExplicitLink) {
        } else if (element instanceof MultiMarkdownWikiLinkText) {
            //Annotation annotator = null;
            //MultiMarkdownWikiLink wikiLink = (MultiMarkdownWikiLink) element.getParent();
            //if (wikiLink != null) annotator = checkWikiLinkSwapRefTitle(wikiLink, holder);
        } else if (element instanceof MultiMarkdownLinkRef) {
            annotateChangeExplicitLinkToWikiLink(element, state, ANNOTATION_WEAK_WARNING);

            annotateLinkRef((MultiMarkdownLinkRef) element, state);
        } else if (element instanceof MultiMarkdownWikiLinkRef) {
            annotateChangeWikiLinkToExplicitLink(element, state, ANNOTATION_WEAK_WARNING);

            annotateWikiLinkRef((MultiMarkdownWikiLinkRef) element, state);
        }
    }

    @LicensedFeature
    public void annotateLinkRef(@NotNull MultiMarkdownLinkRef element, @NotNull AnnotationState state) {
        if (!MultiMarkdownPlugin.isLicensed()) return;

        LinkRef linkRefInfo = MultiMarkdownPsiImplUtil.getLinkRef(element);

        if (linkRefInfo != null && !linkRefInfo.isExternal() && !linkRefInfo.getFilePath().isEmpty()) {
            Project project = element.getProject();
            ProjectFileRef containingFile = new ProjectFileRef(element.getContainingFile());
            GitHubLinkResolver resolver = new GitHubLinkResolver(element.getContainingFile());
            List<PathInfo> looseTargetRefs = resolver.multiResolve(linkRefInfo, LinkResolver.ANY | LinkResolver.LOOSE_MATCH, null);
            List<PathInfo> targetRefs = resolver.multiResolve(linkRefInfo, LinkResolver.ANY, looseTargetRefs);
            PathInfo targetInfo = targetRefs.size() > 0 ? targetRefs.get(0) : (looseTargetRefs.size() > 0 ? looseTargetRefs.get(0) : null);

            if (targetInfo == null) {
                state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unresolved-link-reference"));
            } else if (targetInfo instanceof LinkRef) {
                state.createInfoAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.link.resolves-to-external"));
            } else {
                assert targetInfo instanceof FileRef;
                ProjectFileRef targetRef = targetInfo.projectFileRef(project);
                assert targetRef != null;
                PsiFile psiFile = targetRef.getPsiFile();

                List<InspectionResult> inspectionResults = resolver.inspect(linkRefInfo, targetRef);

                for (InspectionResult reason : inspectionResults) {
                    //ID_TARGET_HAS_SPACES
                    //*ID_CASE_MISMATCH
                    //ID_WIKI_LINK_HAS_DASHES
                    //*ID_NOT_UNDER_WIKI_HOME
                    //ID_TARGET_NOT_WIKI_PAGE_EXT
                    //*ID_NOT_UNDER_SOURCE_WIKI_HOME
                    //*ID_TARGET_NAME_HAS_ANCHOR
                    //*ID_TARGET_PATH_HAS_ANCHOR
                    //*ID_WIKI_LINK_HAS_SLASH
                    //*ID_WIKI_LINK_HAS_SUBDIR
                    //ID_WIKI_LINK_HAS_ONLY_ANCHOR
                    //*ID_LINK_TARGETS_WIKI_HAS_EXT
                    //*ID_LINK_TARGETS_WIKI_HAS_BAD_EXT
                    //*ID_NOT_UNDER_SAME_REPO
                    //*ID_TARGET_NOT_UNDER_VCS
                    //*ID_LINK_NEEDS_EXT
                    //*ID_LINK_HAS_BAD_EXT
                    //ID_LINK_TARGET_NEEDS_EXT
                    //ID_LINK_TARGET_HAS_BAD_EXT

                    String fixedFilePath = reason.getFixedFilePath();
                    String fixedLink = reason.getFixedLink();
                    String extensionList = "";
                    for (String ext : linkRefInfo.getLinkExtensions()) {
                        if (!extensionList.isEmpty()) extensionList += ", ";
                        extensionList += ext;
                    }

                    if (reason.isA(ID_CASE_MISMATCH)) {
                        // it is a weak warning for wiki targets and error for non-wiki targets
                        if (!state.alreadyOfferedIds(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)
                                || !state.alreadyOfferedId(TYPE_RENAME_FILE_QUICK_FIX, targetRef.getFilePath(), fixedLink)) {

                            state.needTargetList = false;
                            if (targetRef.isWikiPage()) {
                                state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.case-mismatch"));
                            } else {
                                state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.case-mismatch"));
                            }

                            if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink, ChangeLinkRefQuickFix.MATCH_CASE_TO_FILE, REASON_FILE_MOVED));
                            }

                            if (psiFile != null && fixedFilePath != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, targetRef.getFilePath(), fixedFilePath)) {
                                state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, fixedFilePath));
                            }
                        }
                    } else if (reason.isA(ID_LINK_NEEDS_EXT)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.missing-extension"));

                        if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink));
                        }
                    } else if (reason.isA(ID_LINK_HAS_BAD_EXT)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.bad-extension"));

                        if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink));
                        }
                    } else if (reason.isA(ID_LINK_TARGET_NEEDS_EXT)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.target-missing-extension", extensionList));

                        if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink));
                        }
                    } else if (reason.isA(ID_LINK_TARGET_HAS_BAD_EXT)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.target-bad-extension", extensionList));

                        if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink));
                        }
                    } else if (reason.isA(ID_LINK_TARGETS_WIKI_HAS_EXT)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.target-wikipage-with-extension"));

                        if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink));
                        }
                    } else if (reason.isA(ID_LINK_TARGETS_WIKI_HAS_BAD_EXT)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.target-wikipage-bad-extension"));

                        if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink));
                        }
                    } else if (reason.isA(ID_NOT_UNDER_WIKI_HOME) || reason.isA(ID_NOT_UNDER_SOURCE_WIKI_HOME)) {
                        // can offer to move the file, just add the logic
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unreachable-page-reference-not-in-wiki-home"));
                    } else if (reason.isA(ID_WIKI_LINK_HAS_SLASH) || reason.isA(ID_WIKI_LINK_HAS_SUBDIR)) {
                        state.canCreateFile = false;
                        // can offer to move the file, just add the logic
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.linkref-has-slash"));

                        if (fixedLink != null && reason.isA(ID_WIKI_LINK_HAS_SLASH)) {
                            if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink, ChangeLinkRefQuickFix.REMOVE_SLASHES, RENAME_KEEP_ANCHOR));
                            }
                        }

                        if (fixedLink != null && reason.isA(ID_WIKI_LINK_HAS_SUBDIR)) {
                            if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, "remove-subdir" + fixedLink)) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink, ChangeLinkRefQuickFix.REMOVE_SUBDIR, RENAME_KEEP_ANCHOR));
                            }
                        }
                    } else if (reason.isA(ID_NOT_UNDER_SAME_REPO)) {
                        // can offer to move the file, just add the logic
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.unreachable-link-reference-not-in-same-repo"));
                    } else if (reason.isA(ID_TARGET_NOT_UNDER_VCS)) {
                        // can offer to move the file, just add the logic
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.not-vcs-target"));
                    } else if (reason.isA(ID_TARGET_NAME_HAS_ANCHOR) || reason.isA(ID_TARGET_PATH_HAS_ANCHOR)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.file-anchor"));

                        if (reason.isA(ID_TARGET_NAME_HAS_ANCHOR)) {
                            if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink, ChangeLinkRefQuickFix.URL_ENCODE_ANCHOR, RENAME_KEEP_TEXT | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_TITLE));
                            }

                            if (fixedFilePath != null && targetRef.canRenameFileTo(fixedFilePath)) {
                                if (state.addingAlreadyOffered(TYPE_RENAME_FILE_AND_RE_TARGET_QUICK_FIX, targetRef.getFilePath(), fixedFilePath)) {
                                    state.annotator.registerFix(new RenameFileAndReTargetQuickFix(psiFile, fixedFilePath, element, RENAME_KEEP_PATH | RENAME_KEEP_TEXT | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_TITLE));
                                }
                            }
                        }

                        if (reason.isA(ID_TARGET_PATH_HAS_ANCHOR)) {
                            annotateTargetPathHasAnchor(element, state);
                            if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink, ChangeLinkRefQuickFix.URL_ENCODE_ANCHOR, RENAME_KEEP_TEXT | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_TITLE));
                            }
                        }
                    }

                    // if this is a valid wikiLink see if more than one match
                    if (state.warningsOnly) {
                        if (targetRefs.size() > 1 && targetRef.isWikiPage()) {
                            state.canCreateFile = false;
                            state.createWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.multiple-targets-match"));

                            //state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                            for (PathInfo otherMatchInfo : targetRefs.subList(1, targetRefs.size())) {
                                if (otherMatchInfo instanceof FileRef) {

                                    String linkRef = ((FileRef) otherMatchInfo).getFilePathFromWikiDir();
                                    String newName = linkRef.replace('/', '-');

                                    if (!linkRef.contains("/")) {
                                        // it is in wiki home, prefix with home-
                                        newName = "home-" + newName;
                                    }

                                    if (!linkRef.equals(newName) && !otherMatchInfo.getFileName().equals(newName) && otherMatchInfo.canRenameFileTo(newName)) {
                                        ProjectFileRef projectFileRef = otherMatchInfo.projectFileRef(project);
                                        PsiFile psiFile = projectFileRef == null ? null : projectFileRef.getPsiFile();
                                        if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, otherMatchInfo.getFilePath(), newName)) {
                                            state.annotator.registerFix(new RenameFileQuickFix(psiFile, linkRef, newName, RenameFileQuickFix.RENAME_CONFLICTING_TARGET));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!state.warningsOnly) {
                    registerCreateFileFix(element.getFileName(), element, state);

                    // get all accessible
                    if (state.needTargetList) {
                        LinkRef emptyLinkRef = linkRefInfo instanceof WikiLinkRef ? new WikiLinkRef(containingFile, "", null) : (linkRefInfo instanceof ImageLinkRef ? new ImageLinkRef(containingFile, "", null) : (new LinkRef(containingFile, "", null)));
                        List<PathInfo> availableTargetRefs = resolver.multiResolve(emptyLinkRef, LinkResolver.ANY | LinkResolver.LOOSE_MATCH, null);

                        if (availableTargetRefs.size() > 0) {
                        /*
                         *   have a file but it is not accessible we can:
                         *   1. rename the link to another accessible file?
                         */
                            for (PathInfo linkRef : availableTargetRefs) {
                                String linkRefText = resolver.linkAddress(emptyLinkRef, linkRef, null, null, null);

                                if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, linkRefText)) {
                                    state.annotator.registerFix(new ChangeLinkRefQuickFix(element, linkRefText, 0, RENAME_KEEP_ANCHOR));
                                    // TODO: make max quick fix wikilink targets a config item
                                    if (state.getAlreadyOfferedSize(TYPE_CHANGE_LINK_REF_QUICK_FIX) >= 15) break;
                                }
                            }
                        }
                    }

                    state.annotator.setNeedsUpdateOnTyping(true);
                } else {
                    offerExplicitToWikiQuickFix(element, state);
                }
            }
        }
    }

    protected void checkWikiLinkSwapRefTitle(@NotNull MultiMarkdownWikiLink element, @NotNull AnnotationState state) {
        // see if need to swap link ref and link text
        MultiMarkdownWikiLinkRef wikiPageRef = (MultiMarkdownWikiLinkRef) MultiMarkdownPsiImplUtil.findChildByType(element, MultiMarkdownTypes.WIKI_LINK_REF);
        PsiReference wikiPageRefReference = wikiPageRef != null ? wikiPageRef.getReference() : null;

        if (wikiPageRefReference != null) {
            MultiMarkdownWikiLinkText wikiPageText = (MultiMarkdownWikiLinkText) MultiMarkdownPsiImplUtil.findChildByType(element, MultiMarkdownTypes.WIKI_LINK_TEXT);

            String wikiPageTextName = wikiPageText != null ? wikiPageText.getName() : null;
            if (wikiPageTextName != null) {
                // see if the link title resolves to a page
                MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();

                if (wikiPageTextName.equals(wikiPageRef.getNameWithAnchor())) {
                    // can get rid off the text
                    if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX)) {
                        state.createWeakWarningAnnotation(wikiPageText.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.redundant-page-title"));
                        state.annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
                    }
                } else {
                    PathInfo linkRefInfo = new PathInfo(wikiPageTextName);
                    FileReferenceList accessibleWikiPageRefs = new FileReferenceListQuery(element.getProject())
                            .wantMarkdownFiles()
                            .gitHubWikiRules()
                            .inSource(containingFile)
                            .ignoreLinkRefExtension(linkRefInfo.hasWikiPageExt())
                            .matchWikiRef(wikiPageTextName)
                            .accessibleWikiPageRefs()
                            .postMatchFilter(linkRefInfo, true, false, null);

                    if (accessibleWikiPageRefs.size() == 1) {
                        if (((MultiMarkdownReferenceWikiLinkRef) wikiPageRefReference).isResolveRefMissing()) {
                            if (!state.alreadyOfferedTypes(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) {
                                state.createErrorAnnotation(element.getTextRange(),
                                        MultiMarkdownGlobalSettings.getInstance().githubWikiLinks.getValue()
                                                ? MultiMarkdownBundle.message("annotation.wikilink.ref-title-github")
                                                : MultiMarkdownBundle.message("annotation.wikilink.ref-title-swapped"));

                                state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                                if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                                if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
                            }
                        } else if (accessibleWikiPageRefs.get()[0].getFileNameNoExtAsWikiRef().equals(wikiPageTextName)) {
                            if (state.alreadyOfferedTypes(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX, TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
                                state.createInfoAnnotation(wikiPageText.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
                                if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
                                if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
                                if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                            }
                        }
                        // TODO: when we can validate existence of anchors add it to the condition below
                    } else if (wikiPageTextName.startsWith("#")) {
                        if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
                            state.createInfoAnnotation(wikiPageText.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
                            state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                        }
                    }
                }
            }
        }
    }

    public void annotateWikiLinkRef(MultiMarkdownWikiLinkRef element, AnnotationState state) {
        MultiMarkdownWikiLink wikiLink = (MultiMarkdownWikiLink) element.getParent();

        PathInfo linkRefInfo = new PathInfo(element.getNameWithAnchor());
        boolean haveExt = linkRefInfo.hasWithAnchorExt() && !linkRefInfo.hasWithAnchorWikiPageExt();

        // if not reversed ref and text and not just a link reference
        if (linkRefInfo.getFileName().isEmpty()) {
            if (linkRefInfo.getFileNameWithAnchor().startsWith("#")) {
                PathInfo sourceFileInfo = new PathInfo(element.getContainingFile().getVirtualFile());
                String newLinkRef = sourceFileInfo.getFileNameNoExtAsWikiRef() + linkRefInfo.getFilePath();

                if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, newLinkRef)) {
                    state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.has-only-anchor"));
                    state.annotator.registerFix(new ChangeLinkRefQuickFix(element, newLinkRef, ChangeLinkRefQuickFix.ADD_PAGE_REF, RENAME_KEEP_TEXT));
                }

                offerWikiToExplicitQuickFix(wikiLink, state);
            }
        } else {

            // see if it exists
            MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();

            if (wikiLink != null && containingFile.isWikiPage()) checkWikiLinkSwapRefTitle(wikiLink, state);

            FileReferenceList filesReferenceList = new FileReferenceListQuery(element.getProject())
                    .keepLinkRefAnchor()
                    .wantMarkdownFiles()
                    .all();

            FileReferenceList accessibleWikiPageRefs = filesReferenceList.query()
                    .gitHubWikiRules()
                    .ignoreLinkRefExtension(!haveExt)
                    .matchWikiRef(element)
                    .accessibleWikiPageRefs()
                    .postMatchFilter(linkRefInfo, true, true, true);

            FileReferenceLinkGitHubRules targetRefLink = accessibleWikiPageRefs.size() > 0 ? (FileReferenceLinkGitHubRules) accessibleWikiPageRefs.getAt(0) : null;

            if (!containingFile.isWikiPage()) {
                state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.github-only-on-wiki-page"));
                offerWikiToExplicitQuickFix(wikiLink, state);
                //} else if (accessibleWikiPageRefs.size() == 1 && !targetRefLink.hasAnchor()) {
                //    // empty
            } else {
                if (accessibleWikiPageRefs.size() > 1) {
                    state.canCreateFile = false;
                    state.createWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.multiple-targets-match"));

                    FileRef[] sorted = accessibleWikiPageRefs.getSorted(1);
                    for (FileRef referenceLink : sorted) {
                        String linkRef = referenceLink.getLinkRefFromWikiHome();
                        String newName = linkRef.replace('/', '-');

                        if (!linkRef.contains("/")) {
                            // it is in wiki home, prefix with home-
                            newName = "home-" + newName;
                        }

                        if (!linkRef.equals(newName) && !referenceLink.getFileName().equals(newName) && referenceLink.canRenameFileTo(newName)) {
                            PsiFile psiFile = referenceLink.getPsiFile();
                            if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFilePath(), newName)) {
                                state.annotator.registerFix(new RenameFileQuickFix(psiFile, linkRef, newName, RenameFileQuickFix.RENAME_CONFLICTING_TARGET));
                            }
                        }
                    }
                } else {
                    // not set to right name or to an accessible name
                    FileReferenceList matchedFilesReferenceList = filesReferenceList.query()
                            .caseInsensitive()
                            .gitHubWikiRules()
                            .ignoreLinkRefExtension(!haveExt)
                            .keepLinkRefAnchor()
                            .linkRefIgnoreSubDirs()
                            .spaceDashEqual()
                            .wantMarkdownFiles()
                            .inSource(containingFile)
                            .matchWikiRef(linkRefInfo.getFileNameWithAnchorAsWikiRef())
                            .all()
                            .postMatchFilter(linkRefInfo.getFileNameWithAnchorAsWikiRef(), true, false);

                    FileReferenceList otherFileRefList = matchedFilesReferenceList;

                    if (matchedFilesReferenceList.size() > 1) {
                        // see if eliminating files out of this wiki will help
                        otherFileRefList = matchedFilesReferenceList.sameWikiHomePageRefs();
                    }

                    FileRef[] otherReferences = otherFileRefList.get();

                    if (otherReferences.length > 0) targetRefLink = (FileReferenceLinkGitHubRules) otherReferences[0];

                    if (otherReferences.length != 1) {
                        assert otherReferences.length == 0 : "Should not happen, multiple matches and link unresolved";
                        state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unresolved-link-reference"));
                    } else {
                        FileReferenceLinkGitHubRules referenceLink = (FileReferenceLinkGitHubRules) otherReferences[0];
                        FileReferenceLink.InaccessibleWikiPageReasons reasons = referenceLink.inaccessibleWikiPageRefReasons(MultiMarkdownPsiImplUtil.getLinkRefTextWithAnchor(element));

                        PsiFile psiFile = referenceLink.getPsiFile();
                        if (reasons.caseMismatch()) {
                            state.canCreateFile = false;

                            if (!state.alreadyOfferedId(TYPE_CHANGE_LINK_REF_QUICK_FIX, "case-mismatch" + reasons.caseMismatchWikiRefFixed())
                                    || !state.alreadyOfferedId(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFilePath(), reasons.caseMismatchFileNameFixed())) {
                                state.needTargetList = false;
                                state.createWeakWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.case-mismatch"));

                                if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.caseMismatchWikiRefFixed())) {
                                    state.annotator.registerFix(new ChangeLinkRefQuickFix(element, reasons.caseMismatchWikiRefFixed(), ChangeLinkRefQuickFix.MATCH_CASE_TO_FILE));
                                }

                                if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFilePath(), reasons.caseMismatchFileNameFixed())) {
                                    state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, reasons.caseMismatchFileNameFixed()));
                                }
                            }
                        }

                        if (reasons.targetNotInWikiHome() || reasons.targetNotInSameWikiHome()) {
                            // can offer to move the file, just add the logic
                            state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unreachable-page-reference-not-in-wiki-home"));
                        }

                        if (reasons.wikiRefHasSlash() || reasons.wikiRefHasFixableSlash() || reasons.wikiRefHasSubDir()) {
                            state.canCreateFile = false;
                            // can offer to move the file, just add the logic
                            state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.linkref-has-slash"));

                            if (reasons.wikiRefHasFixableSlash()) {
                                if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.wikiRefHasSlashFixed())) {
                                    state.annotator.registerFix(new ChangeLinkRefQuickFix(element, reasons.wikiRefHasSlashFixed(), ChangeLinkRefQuickFix.REMOVE_SLASHES, RENAME_KEEP_ANCHOR));
                                }
                            }

                            if (reasons.wikiRefHasSubDir()) {
                                if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.wikiRefHasSubDirFixed())) {
                                    state.annotator.registerFix(new ChangeLinkRefQuickFix(element, reasons.wikiRefHasSubDirFixed(), ChangeLinkRefQuickFix.REMOVE_SUBDIR, RENAME_KEEP_ANCHOR));
                                }
                            }
                        }

                        if (reasons.targetNameHasSpaces()) {
                            state.needTargetList = false;
                            state.createWeakWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.file-spaces"));

                            if (referenceLink.canRenameFileTo(reasons.targetNameHasSpacedFixed())) {
                                if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFilePath(), reasons.targetNameHasSpacedFixed())) {
                                    state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, reasons.targetNameHasSpacedFixed()));
                                }
                            }
                        }

                        if (reasons.targetNameHasAnchor()) {
                            state.needTargetList = false;
                            state.createWeakWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.file-anchor"));

                            if (referenceLink.canRenameFileTo(reasons.targetNameHasAnchorFixed())) {
                                if (state.addingAlreadyOffered(TYPE_RENAME_FILE_AND_RE_TARGET_QUICK_FIX, referenceLink.getFilePath(), reasons.targetNameHasAnchorFixed())) {
                                    state.annotator.registerFix(new RenameFileAndReTargetQuickFix(referenceLink.getPsiFileWithAnchor(), reasons.targetNameHasAnchorFixed(), element, RENAME_KEEP_TEXT | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_TITLE));
                                }
                            }
                        }

                        if (!reasons.targetNameHasAnchor() && reasons.targetNotWikiPageExt()) {
                            state.createWeakWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.target-not-wiki-page-ext"));

                            if (referenceLink.canRenameFileTo(reasons.targetNotWikiPageExtFixed())) {
                                if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFilePath(), reasons.targetNotWikiPageExtFixed())) {
                                    state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, reasons.targetNotWikiPageExtFixed()));
                                }
                            }

                            // add quick fix to change wiki to explicit link
                            offerWikiToExplicitQuickFix(wikiLink, state);
                        }

                        if (reasons.targetPathHasAnchor()) {
                            annotateTargetPathHasAnchor(element, state);
                        }

                        if (reasons.wikiRefHasDashes()) {
                            state.needTargetList = false;
                            state.canCreateFile = false;
                            state.createWeakWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.link-dashes"));

                            if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.wikiRefHasDashesFixed())) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, reasons.wikiRefHasDashesFixed(), ChangeLinkRefQuickFix.REMOVE_DASHES));
                            }
                        }

                        if (reasons.wikiRefHasExt()) {
                            state.needTargetList = false;
                            state.canCreateFile = false;
                            state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.link-ext"));

                            if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.wikiRefHasExtFixed())) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, reasons.wikiRefHasExtFixed(), ChangeLinkRefQuickFix.REMOVE_EXT));
                            }
                        }
                    }
                }

                if (state.warningsOnly) {
                    if (targetRefLink != null && MultiMarkdownPlugin.isLicensed() && !targetRefLink.isUnderVcs()) {
                        state.createWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.link.not-vcs-target"));
                    }
                } else {
                    registerCreateFileFix(element.getFileName(), element, state);

                    // get all accessible
                    if (state.needTargetList && filesReferenceList.size() != 0) {
                        FileReferenceList wikiPageRefs = filesReferenceList.query().gitHubWikiRules().inSource(containingFile).accessibleWikiPageRefs();

                        FileRef[] references = wikiPageRefs.get();
                        Arrays.sort(references);

                        for (FileRef fileReference : references) {
                            FileReferenceLink wikiPageRef = (FileReferenceLink) fileReference;

                            if (wikiPageRef.getUpDirectories() <= wikiPageRef.getUpDirectoriesToWikiHome()) {
                                if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, wikiPageRef.getWikiPageRef())) {
                                    state.annotator.registerFix(new ChangeLinkRefQuickFix(element, wikiPageRef.getWikiPageRef(), 0, RENAME_KEEP_ANCHOR));

                                    // TODO: make max quick fix wikilink targets a config item
                                    if (state.getAlreadyOfferedSize(TYPE_CHANGE_LINK_REF_QUICK_FIX) >= 15) break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @LicensedFeature
    protected void offerExplicitToWikiQuickFix(@NotNull MultiMarkdownLinkRef element, @NotNull AnnotationState state) {
        MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();

        if (containingFile.isWikiPage() && MultiMarkdownPsiImplUtil.isWikiLinkEquivalent(element)) {
            annotateChangeExplicitLinkToWikiLink(element, state, ANNOTATION_WEAK_WARNING);
        }
    }

    @LicensedFeature
    protected void offerWikiToExplicitQuickFix(MultiMarkdownWikiLink wikiLink, AnnotationState state) {
        if (MultiMarkdownPlugin.isLicensed() && state.addingAlreadyOffered(TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK)) {
            state.annotator.registerFix(new ChangeWikiLinkToExplicitLinkQuickFix(wikiLink));
        }
    }

    @LicensedFeature
    protected void annotateChangeWikiLinkToExplicitLink(@NotNull PsiElement element, AnnotationState state, int type) {
        if (MultiMarkdownPlugin.isLicensed()) {
            MultiMarkdownWikiLink wikiLink = (MultiMarkdownWikiLink) element.getParent();
            annotateChangeLinkType(wikiLink, state, TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK, type, new ChangeWikiLinkToExplicitLinkQuickFix(wikiLink), "annotation.wikilink.change-to-linkref");
        }
    }

    @LicensedFeature
    protected void annotateChangeExplicitLinkToWikiLink(@NotNull PsiElement element, AnnotationState state, int type) {
        if (MultiMarkdownPlugin.isLicensed() && MultiMarkdownPsiImplUtil.isWikiLinkEquivalent(element)) {
            PathInfo pathInfo = new PathInfo(element.getContainingFile().getVirtualFile());
            if (pathInfo.isWikiPage()) {
                MultiMarkdownExplicitLink explicitLink = (MultiMarkdownExplicitLink) element.getParent();
                annotateChangeLinkType(explicitLink, state, TYPE_CHANGE_EXPLICIT_LINK_TO_WIKI_LINK_QUICK_FIX, type, new ChangeExplicitLinkToWikiLinkQuickFix(explicitLink), "annotation.link.change-to-wikilink");
            }
        }
    }

    protected void annotateChangeLinkType(@NotNull PsiElement element, @NotNull AnnotationState state, @NotNull String quickFixType, int type, @NotNull BaseIntentionAction quickFix, @NotNull String messageKey) {
        if (type != ANNOTATION_INFO && state.addingAlreadyOffered(quickFixType)) {
            switch (type) {
                case ANNOTATION_WEAK_WARNING:
                    state.createWeakWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message(messageKey));
                    break;

                case ANNOTATION_WARNING:
                    state.createWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message(messageKey));
                    break;

                case ANNOTATION_ERROR:
                    state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message(messageKey));
                    break;

                //case ANNOTATION_INFO:
                default:
                    state.createInfoAnnotation(element.getTextRange(), MultiMarkdownBundle.message(messageKey));
                    break;
            }

            state.annotator.registerFix(quickFix);
        }
    }

    protected void registerCreateFileFix(@NotNull String fileName, @NotNull MultiMarkdownNamedElement element, @NotNull AnnotationState state) {
        if (state.canCreateFile) {
            if (state.annotator == null) {
                // creation fix
                state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unresolved-link-reference"));
            }

            PathInfo thisFile = new FileRef(element.getContainingFile());
            PathInfo newFile = PathInfo.appendParts(thisFile.getPath(), fileName.split("/"));

            //if (newFile.canCreateFile()) {
            state.annotator.registerFix(new CreateFileQuickFix(newFile.getFilePath(), fileName));
            //}
        }
    }

    protected void annotateTargetPathHasAnchor(MultiMarkdownNamedElement element, AnnotationState state) {
        state.needTargetList = false;
        state.canCreateFile = false;
        state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.path-anchor"));

        // TODO: create quick fix to remove anchors from all directories in the path
        //if (canRenameFile(referenceLink.getVirtualFile(), reasons.targetNameHasAnchorFixed())) {
        //    state.annotator.registerFix(new RenameFileQuickFix(referenceLink.getVirtualFile(), reasons.targetNameHasAnchorFixed()));
        //}
    }
}
