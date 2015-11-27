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
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
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

import java.util.List;

import static com.vladsch.idea.multimarkdown.annotator.AnnotationState.*;
import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;
import static com.vladsch.idea.multimarkdown.util.GitHubLinkInspector.*;

//public class MultiMarkdownAnnotator extends ExternalAnnotator<String, Set<MultiMarkdownAnnotator.HighlightableToken>> {
public class MultiMarkdownAnnotator implements Annotator {
    private static final Logger LOGGER = Logger.getInstance(MultiMarkdownAnnotator.class);

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
        } else if (element instanceof MultiMarkdownLinkRefElement) {
            // TODO: implement inspections and move these annotation to info type inspections
            if (element instanceof MultiMarkdownLinkRef) annotateChangeExplicitLinkToWikiLink(element, state, Severity.WEAK_WARNING);
            else if (element instanceof MultiMarkdownWikiLinkRef) {
                checkWikiLinkSwapRefTitle((MultiMarkdownWikiLink) element.getParent(), state);
                annotateChangeWikiLinkToExplicitLink(element, state, Severity.WEAK_WARNING);
            }

            annotateLinkRef((MultiMarkdownLinkRefElement) element, state);
        }
    }

    @LicensedFeature
    public void annotateLinkRef(@NotNull MultiMarkdownLinkRefElement element, @NotNull AnnotationState state) {
        MultiMarkdownPsiImplUtil.LinkRefElementTypes elementTypes = MultiMarkdownPsiImplUtil.getNamedElementTypes(element);

        if (elementTypes == null || !(element.getParent() instanceof MultiMarkdownLinkElement)
                || (elementTypes != MultiMarkdownPsiImplUtil.WIKI_LINK_ELEMENT && !MultiMarkdownPlugin.isLicensed())) return;

        LinkRef linkRefInfo = MultiMarkdownPsiImplUtil.getLinkRef(elementTypes, element);

        //noinspection StatementWithEmptyBody
        if (linkRefInfo.isExternal()) {
            //state.createInfoAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.link.resolves-to-external"));
        } else if (!linkRefInfo.getFilePath().isEmpty()) {
            Project project = element.getProject();
            ProjectFileRef containingFile = new ProjectFileRef(element.getContainingFile());
            GitHubLinkResolver resolver = new GitHubLinkResolver(element.getContainingFile());
            List<PathInfo> looseTargetRefs = resolver.multiResolve(linkRefInfo, LinkResolver.ANY | LinkResolver.LOOSE_MATCH, null);
            List<PathInfo> targetRefs = resolver.multiResolve(linkRefInfo, LinkResolver.ANY, looseTargetRefs);
            PathInfo targetInfo = targetRefs.size() > 0 ? targetRefs.get(0) : (looseTargetRefs.size() > 0 ? looseTargetRefs.get(0) : null);
            PsiElement parentElement = element.getParent();
            PsiElement textElement = MultiMarkdownPsiImplUtil.findChildByType(parentElement, elementTypes.textType);
            //PsiElement titleElement = elementTypes.titleType == null ? null : MultiMarkdownPsiImplUtil.findChildByType(parentElement, elementTypes.titleType);
            //PsiElement anchorElement = elementTypes.anchorType == null ? null : MultiMarkdownPsiImplUtil.findChildByType(parentElement, elementTypes.anchorType);

            if (targetInfo == null) {
                state.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unresolved-link-reference"));
            } else if (targetInfo instanceof LinkRef) {
                //state.createInfoAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.link.resolves-to-external"));
            } else {
                assert targetInfo instanceof FileRef;
                ProjectFileRef targetRef = targetInfo.projectFileRef(project);
                assert targetRef != null;
                PsiFile psiFile = targetRef.getPsiFile();

                String extensionList = "";
                for (String ext : linkRefInfo.getLinkExtensions()) {
                    if (!extensionList.isEmpty()) extensionList += ", ";
                    extensionList += ext;
                }

                List<InspectionResult> inspectionResults = resolver.inspect(linkRefInfo, targetRef);

                for (InspectionResult reason : inspectionResults) {
                    //*ID_TARGET_HAS_SPACES
                    //*ID_CASE_MISMATCH
                    //*ID_WIKI_LINK_HAS_DASHES
                    //*ID_NOT_UNDER_WIKI_HOME
                    //ID_TARGET_NOT_WIKI_PAGE_EXT
                    //*ID_NOT_UNDER_SOURCE_WIKI_HOME
                    //*ID_TARGET_NAME_HAS_ANCHOR
                    //*ID_TARGET_PATH_HAS_ANCHOR
                    //*ID_WIKI_LINK_HAS_SLASH
                    //*ID_WIKI_LINK_HAS_SUBDIR
                    //*ID_WIKI_LINK_HAS_ONLY_ANCHOR
                    //*ID_LINK_TARGETS_WIKI_HAS_EXT
                    //*ID_LINK_TARGETS_WIKI_HAS_BAD_EXT
                    //*ID_NOT_UNDER_SAME_REPO
                    //*ID_TARGET_NOT_UNDER_VCS
                    //*ID_LINK_NEEDS_EXT
                    //*ID_LINK_HAS_BAD_EXT
                    //*ID_LINK_TARGET_NEEDS_EXT
                    //*ID_LINK_TARGET_HAS_BAD_EXT
                    if (reason.getHandled()) continue;

                    String fixedFilePath = reason.getFixedFilePath();
                    String fixedLink = reason.getFixedLink();
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
                    } else if (reason.isA(ID_TARGET_NOT_WIKI_PAGE_EXT)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.target-not-wiki-extension", extensionList));

                        if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink));
                        }

                        if (psiFile != null && fixedFilePath != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, fixedFilePath, fixedFilePath)) {
                            state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, fixedFilePath));
                        }
                    } else if (reason.isA(ID_NOT_UNDER_WIKI_HOME) || reason.isA(ID_NOT_UNDER_SOURCE_WIKI_HOME)) {
                        // can offer to move the file, just add the logic
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unreachable-page-reference-not-in-wiki-home"));
                        InspectionResult.handled(inspectionResults, ID_NOT_UNDER_WIKI_HOME, ID_NOT_UNDER_SOURCE_WIKI_HOME);
                    } else if (reason.isA(ID_WIKI_LINK_HAS_SLASH) || reason.isA(ID_WIKI_LINK_HAS_SUBDIR)) {
                        state.canCreateFile = false;
                        // can offer to move the file, just add the logic
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.linkref-has-slash"));
                        InspectionResult.handled(inspectionResults, ID_WIKI_LINK_HAS_SLASH, ID_WIKI_LINK_HAS_SUBDIR);

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
                    } else if (reason.isA(ID_TARGET_HAS_SPACES)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.file-spaces"));

                        if (fixedFilePath != null && linkRefInfo.canRenameFileTo(fixedFilePath)) {
                            if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, fixedFilePath, fixedFilePath)) {
                                state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, fixedFilePath));
                            }
                        }
                    } else if (reason.isA(ID_WIKI_LINK_HAS_DASHES)) {
                        state.needTargetList = false;
                        state.canCreateFile = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.link-dashes"));

                        if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink, ChangeLinkRefQuickFix.REMOVE_DASHES));
                        }
                    } else if (textElement != null && reason.isA(ID_WIKI_LINK_HAS_REDUNDANT_TEXT)) {
                        state.needTargetList = false;
                        state.canCreateFile = false;
                        state.createAnnotation(reason.getSeverity(), textElement.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.redundant-page-title"));

                        if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX)) {
                            assert parentElement instanceof MultiMarkdownWikiLink;
                            state.annotator.registerFix(new DeleteWikiPageTitleQuickFix((MultiMarkdownWikiLink) parentElement));
                        }
                    } else if (textElement != null && reason.isA(ID_WIKI_LINK_HAS_ADDRESS_TEXT_SWAPPED)) {
                        if (!state.alreadyOfferedTypes(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) {
                            assert parentElement instanceof MultiMarkdownWikiLink;

                            state.createAnnotation(reason.getSeverity(), parentElement.getTextRange(),
                                    MultiMarkdownGlobalSettings.getInstance().githubWikiLinks.getValue()
                                            ? MultiMarkdownBundle.message("annotation.wikilink.ref-title-github")
                                            : MultiMarkdownBundle.message("annotation.wikilink.ref-title-swapped"));

                            if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX))
                                state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix((MultiMarkdownWikiLink) parentElement));
                            if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX))
                                state.annotator.registerFix(new DeleteWikiPageRefQuickFix((MultiMarkdownWikiLink) parentElement));
                        }
                    } else if (textElement != null && reason.isA(ID_WIKI_LINK_TEXT_MATCHES_ANOTHER_TARGET)) {
                        state.needTargetList = false;
                        state.canCreateFile = false;
                        if (state.alreadyOfferedTypes(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX, TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
                            assert parentElement instanceof MultiMarkdownWikiLink;

                            state.createAnnotation(reason.getSeverity(), textElement.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
                            if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX))
                                state.annotator.registerFix(new DeleteWikiPageTitleQuickFix((MultiMarkdownWikiLink) parentElement));
                            if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX))
                                state.annotator.registerFix(new DeleteWikiPageRefQuickFix((MultiMarkdownWikiLink) parentElement));
                            if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX))
                                state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix((MultiMarkdownWikiLink) parentElement));
                        }
                    } else if (textElement != null && reason.isA(ID_WIKI_LINK_TEXT_MATCHES_SELF_REF)) {
                        state.needTargetList = false;
                        state.canCreateFile = false;
                        if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
                            assert parentElement instanceof MultiMarkdownWikiLink;
                            state.createInfoAnnotation(textElement.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
                            state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix((MultiMarkdownWikiLink) parentElement));
                        }
                    } else if (reason.isA(ID_WIKI_LINK_HAS_ONLY_ANCHOR)) {
                        if (fixedLink != null && state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, fixedLink)) {
                            state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.has-only-anchor"));
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, fixedLink, ChangeLinkRefQuickFix.ADD_PAGE_REF, RENAME_KEEP_TEXT));
                        }

                        assert parentElement instanceof MultiMarkdownWikiLink;
                        if (element instanceof MultiMarkdownWikiLinkRef) offerWikiToExplicitQuickFix((MultiMarkdownWikiLink) parentElement, state);
                    } else if (reason.isA(ID_TARGET_NAME_HAS_ANCHOR) || reason.isA(ID_TARGET_PATH_HAS_ANCHOR)) {
                        state.needTargetList = false;
                        state.createAnnotation(reason.getSeverity(), element.getTextRange(), MultiMarkdownBundle.message("annotation.link.file-anchor"));
                        InspectionResult.handled(inspectionResults, ID_TARGET_NAME_HAS_ANCHOR, ID_TARGET_PATH_HAS_ANCHOR);

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
                                        PsiFile psiTargetFile = projectFileRef == null ? null : projectFileRef.getPsiFile();
                                        if (psiTargetFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, otherMatchInfo.getFilePath(), newName)) {
                                            state.annotator.registerFix(new RenameFileQuickFix(psiTargetFile, linkRef, newName, RenameFileQuickFix.RENAME_CONFLICTING_TARGET));
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
                }
            }
        }
    }

    protected void checkWikiLinkSwapRefTitle(@NotNull MultiMarkdownWikiLink element, @NotNull AnnotationState state) {
        // see if need to swap link ref and link text
        MultiMarkdownPsiImplUtil.LinkRefElementTypes elementTypes = MultiMarkdownPsiImplUtil.getNamedElementTypes(element);

        if (elementTypes == null || elementTypes != MultiMarkdownPsiImplUtil.WIKI_LINK_ELEMENT) return;

        LinkRef linkRefInfo = MultiMarkdownPsiImplUtil.getLinkRef(elementTypes, element);
        PsiNamedElement textElement = (PsiNamedElement) MultiMarkdownPsiImplUtil.findChildByType(element, elementTypes.textType);
        MultiMarkdownLinkRefElement linkRefElement = (MultiMarkdownLinkRefElement) MultiMarkdownPsiImplUtil.findChildByType(element, elementTypes.linkRefType);

        PsiReference reference = linkRefElement != null ? linkRefElement.getReference() : null;

        if (reference != null) {
            String wikiPageTextName = textElement != null ? textElement.getName() : null;
            if (wikiPageTextName != null) {
                // see if the link text resolves to a page

                if (wikiPageTextName.equals(linkRefElement.getNameWithAnchor())) {
                    // can get rid off the text
                    if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX)) {
                        state.createWeakWarningAnnotation(textElement.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.redundant-page-title"));
                        state.annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
                    }
                } else {
                    Project project = element.getProject();
                    ProjectFileRef containingFile = new ProjectFileRef(element.getContainingFile());
                    GitHubLinkResolver resolver = new GitHubLinkResolver(element.getContainingFile());
                    List<PathInfo> targetRefs = resolver.multiResolve(linkRefInfo, LinkResolver.ANY, null);
                    PathInfo targetInfo = targetRefs.size() > 0 ? targetRefs.get(0) : null;

                    if (targetRefs.size() > 0 && targetInfo != null) {
                        // have a resolve target
                        if (((MultiMarkdownReferenceWikiLinkRef) reference).isResolveRefMissing()) {
                            if (!state.alreadyOfferedTypes(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) {
                                state.createErrorAnnotation(element.getTextRange(),
                                        MultiMarkdownGlobalSettings.getInstance().githubWikiLinks.getValue()
                                                ? MultiMarkdownBundle.message("annotation.wikilink.ref-title-github")
                                                : MultiMarkdownBundle.message("annotation.wikilink.ref-title-swapped"));

                                if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                                if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
                            }
                        } else if (WikiLinkRef.convertFileToLink(targetInfo.getFileNameNoExt()).equals(wikiPageTextName)) {
                            if (state.alreadyOfferedTypes(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX, TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
                                state.createInfoAnnotation(textElement.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
                                if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
                                if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
                                if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                            }
                        }
                        // TODO: when we can validate existence of anchors add it to the condition below
                    } else if (wikiPageTextName.startsWith("#")) {
                        if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
                            state.createInfoAnnotation(textElement.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
                            state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
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
            annotateChangeExplicitLinkToWikiLink(element, state, Severity.WEAK_WARNING);
        }
    }

    @LicensedFeature
    protected void offerWikiToExplicitQuickFix(MultiMarkdownWikiLink wikiLink, AnnotationState state) {
        if (MultiMarkdownPlugin.isLicensed() && state.addingAlreadyOffered(TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK)) {
            state.annotator.registerFix(new ChangeWikiLinkToExplicitLinkQuickFix(wikiLink));
        }
    }

    @LicensedFeature
    protected void annotateChangeWikiLinkToExplicitLink(@NotNull PsiElement element, AnnotationState state, Severity type) {
        if (MultiMarkdownPlugin.isLicensed() && state.addingAlreadyOffered(TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK)) {
            MultiMarkdownWikiLink wikiLink = (MultiMarkdownWikiLink) element.getParent();
            annotateChangeLinkType(wikiLink, state, TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK, type, new ChangeWikiLinkToExplicitLinkQuickFix(wikiLink), "annotation.wikilink.change-to-linkref");
        }
    }

    @LicensedFeature
    protected void annotateChangeExplicitLinkToWikiLink(@NotNull PsiElement element, AnnotationState state, Severity type) {
        if (MultiMarkdownPlugin.isLicensed() && MultiMarkdownPsiImplUtil.isWikiLinkEquivalent(element) && state.addingAlreadyOffered(TYPE_CHANGE_EXPLICIT_LINK_TO_WIKI_LINK_QUICK_FIX)) {
            FileRef pathInfo = new FileRef(element.getContainingFile().getVirtualFile());
            if (pathInfo.isWikiPage()) {
                MultiMarkdownExplicitLink explicitLink = (MultiMarkdownExplicitLink) element.getParent();
                annotateChangeLinkType(explicitLink, state, TYPE_CHANGE_EXPLICIT_LINK_TO_WIKI_LINK_QUICK_FIX, type, new ChangeExplicitLinkToWikiLinkQuickFix(explicitLink), "annotation.link.change-to-wikilink");
            }
        }
    }

    protected void annotateChangeLinkType(@NotNull PsiElement element, @NotNull AnnotationState state, @NotNull String quickFixType, Severity type, @NotNull BaseIntentionAction quickFix, @NotNull String messageKey) {
        if (type != Severity.INFO && state.addingAlreadyOffered(quickFixType)) {
            state.createAnnotation(type, element.getTextRange(), MultiMarkdownBundle.message(messageKey));
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
