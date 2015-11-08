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

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.license.LicensedFeature;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownReferenceWikiPageRef;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.vladsch.idea.multimarkdown.annotator.AnnotationState.*;
import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;

//public class MultiMarkdownAnnotator extends ExternalAnnotator<String, Set<MultiMarkdownAnnotator.HighlightableToken>> {
public class MultiMarkdownAnnotator implements Annotator {
    private static final Logger LOGGER = Logger.getInstance(MultiMarkdownAnnotator.class);

    protected static final int ANNOTATION_INFO = 0;
    protected static final int ANNOTATION_WEAK_WARNING = 1;
    protected static final int ANNOTATION_WARNING = 2;
    protected static final int ANNOTATION_ERROR = 3;

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        //noinspection StatementWithEmptyBody
        AnnotationState state = new AnnotationState(holder);

        if (false) {
        } else if (element instanceof MultiMarkdownWikiLink) {
        } else if (element instanceof MultiMarkdownExplicitLink) {
        } else if (element instanceof MultiMarkdownWikiPageText) {
            //Annotation annotator = null;
            //MultiMarkdownWikiLink wikiLink = (MultiMarkdownWikiLink) element.getParent();
            //if (wikiLink != null) annotator = checkWikiLinkSwapRefTitle(wikiLink, holder);
        } else if (element instanceof MultiMarkdownLinkRef) {
            addChangeExplicitLinkToWikiLink(element, state, ANNOTATION_INFO);
        } else if (element instanceof MultiMarkdownWikiPageRef) {
            addChangeWikiLinkToExplicitLink(element, state, ANNOTATION_INFO);

            annotateWikiLinkRef((MultiMarkdownWikiPageRef) element, state);
        }
    }
    protected void addChangeWikiLinkToExplicitLink(@NotNull PsiElement element, AnnotationState state, int type) {
        if (MultiMarkdownPlugin.isLicensed()) {
            MultiMarkdownWikiLink wikiLink = (MultiMarkdownWikiLink) element.getParent();
            if (state.addingAlreadyOffered(TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK)) {
                switch (type) {
                    case ANNOTATION_WEAK_WARNING:
                        state.annotator = state.holder.createWeakWarningAnnotation(wikiLink.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.change-to-linkref"));
                        break;

                    case ANNOTATION_WARNING:
                        state.annotator = state.holder.createWarningAnnotation(wikiLink.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.change-to-linkref"));
                        break;

                    case ANNOTATION_ERROR:
                        state.annotator = state.holder.createWarningAnnotation(wikiLink.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.change-to-linkref"));
                        break;

                    case ANNOTATION_INFO:
                    default:
                        state.annotator = state.holder.createInfoAnnotation(wikiLink.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.change-to-linkref"));
                        break;
                }

                state.annotator.registerFix(new ChangeWikiLinkToExplicitLinkQuickFix(wikiLink));
            }
        }
    }
    protected void addChangeExplicitLinkToWikiLink(@NotNull PsiElement element, AnnotationState state, int type) {
        if (MultiMarkdownPlugin.isLicensed()) {
            FilePathInfo pathInfo = new FilePathInfo(element.getContainingFile().getVirtualFile());
            if (pathInfo.isWikiPage()) {
                MultiMarkdownExplicitLink explicitLink = (MultiMarkdownExplicitLink) element.getParent();
                if (state.addingAlreadyOffered(TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK)) {
                    switch (type) {
                        case ANNOTATION_WEAK_WARNING:
                            state.annotator = state.holder.createWeakWarningAnnotation(explicitLink.getTextRange(), MultiMarkdownBundle.message("annotation.link.change-to-wikilink"));
                            break;

                        case ANNOTATION_WARNING:
                            state.annotator = state.holder.createWarningAnnotation(explicitLink.getTextRange(), MultiMarkdownBundle.message("annotation.link.change-to-wikilink"));
                            break;

                        case ANNOTATION_ERROR:
                            state.annotator = state.holder.createErrorAnnotation(explicitLink.getTextRange(), MultiMarkdownBundle.message("annotation.link.change-to-wikilink"));
                            break;

                        case ANNOTATION_INFO:
                        default:
                            state.annotator = state.holder.createInfoAnnotation(explicitLink.getTextRange(), MultiMarkdownBundle.message("annotation.link.change-to-wikilink"));
                            break;
                    }

                    state.annotator.registerFix(new ChangeExplicitLinkToWikiLinkQuickFix(explicitLink));
                }
            }

            annotateLinkRef((MultiMarkdownLinkRef) element, state);
        }
    }

    protected void checkWikiLinkSwapRefTitle(@NotNull MultiMarkdownWikiLink element, @NotNull AnnotationState state) {
        // see if need to swap link ref and link text

        MultiMarkdownWikiPageRef wikiPageRef = (MultiMarkdownWikiPageRef) MultiMarkdownPsiImplUtil.findChildByType(element, MultiMarkdownTypes.WIKI_LINK_REF);
        PsiReference wikiPageRefReference = wikiPageRef != null ? wikiPageRef.getReference() : null;

        if (wikiPageRefReference != null) {
            MultiMarkdownWikiPageText wikiPageText = (MultiMarkdownWikiPageText) MultiMarkdownPsiImplUtil.findChildByType(element, MultiMarkdownTypes.WIKI_LINK_TEXT);

            String wikiPageTextName = wikiPageText != null ? wikiPageText.getName() : null;
            if (wikiPageTextName != null) {
                // see if the link title resolves to a page
                MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();

                if (wikiPageTextName.equals(wikiPageRef.getNameWithAnchor())) {
                    // can get rid off the text
                    if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX)) {
                        state.annotator = state.holder.createWeakWarningAnnotation(wikiPageText.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.redundant-page-title"));
                        state.annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
                    }
                } else {
                    FileReferenceList accessibleWikiPageRefs = new FileReferenceListQuery(element.getProject())
                            .wantMarkdownFiles()
                            .inSource(containingFile)
                            .matchWikiRef(wikiPageTextName)
                            .accessibleWikiPageRefs();

                    if (accessibleWikiPageRefs.size() == 1) {
                        if (((MultiMarkdownReferenceWikiPageRef) wikiPageRefReference).isResolveRefMissing()) {
                            if (!state.alreadyOfferedTypes(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) {
                                state.annotator = state.holder.createErrorAnnotation(element.getTextRange(),
                                        MultiMarkdownGlobalSettings.getInstance().githubWikiLinks.getValue()
                                                ? MultiMarkdownBundle.message("annotation.wikilink.ref-title-github")
                                                : MultiMarkdownBundle.message("annotation.wikilink.ref-title-swapped"));

                                state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                                if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                                if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
                            }
                        } else if (accessibleWikiPageRefs.get()[0].getFileNameNoExtAsWikiRef().equals(wikiPageTextName)) {
                            if (state.alreadyOfferedTypes(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX, TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX, TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
                                state.annotator = state.holder.createInfoAnnotation(wikiPageText.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
                                if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_TITLE_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
                                if (state.addingAlreadyOffered(TYPE_DELETE_WIKI_PAGE_REF_QUICK_FIX)) state.annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
                                if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                            }
                        }
                    } else if (wikiPageTextName.contains("#")) {
                        if (state.addingAlreadyOffered(TYPE_SWAP_WIKI_PAGE_REF_TITLE_QUICK_FIX)) {
                            state.annotator = state.holder.createInfoAnnotation(wikiPageText.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
                            state.annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                        }
                    }
                }
            }
        }
    }

    @LicensedFeature
    public void annotateLinkRef(@NotNull MultiMarkdownLinkRef element, @NotNull AnnotationState state) {
        if (!MultiMarkdownPlugin.isLicensed()) return;

        FilePathInfo pathInfo = new FilePathInfo(element.getNameWithAnchor());
        MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();
        FileReference sourceReference = new FileReference(containingFile);

        if (!pathInfo.isExternalReference() && !FilePathInfo.linkRefNoAnchor(pathInfo.getFileName()).isEmpty()) {
            // see if it exists
            FileReferenceList filesReferenceList;
            boolean haveExt = pathInfo.hasExt();
            boolean withExt = false;
            FileReferenceListQuery filesReferenceListQuery;

            if (element instanceof MultiMarkdownImageLinkRef) {
                withExt = true;
                filesReferenceList = new FileReferenceListQuery(element.getProject())
                        .keepLinkRefAnchor()
                        .wantImageFiles()
                        .all();
                filesReferenceListQuery = filesReferenceList.query().wantImageFiles();
            } else {
                filesReferenceList = new FileReferenceListQuery(element.getProject())
                        .keepLinkRefAnchor()
                        .wantMarkdownFiles()
                        .all();
                filesReferenceListQuery = filesReferenceList.query().wantMarkdownFiles();
                withExt = haveExt;
            }

            FileReferenceList accessibleLinkRefs = new FileReferenceListQuery(filesReferenceListQuery)
                    .caseSensitive() // we want to catch mismatches
                    .gitHubWikiRules()
                    .sameGitHubRepo()
                    .matchLinkRef(element, withExt)
                    .all();

            if (accessibleLinkRefs.size() == 1) {
                // add quick fix to change wiki to explicit link
                if (containingFile.isWikiPage() && MultiMarkdownPsiImplUtil.isWikiLinkEquivalent(element)) {
                    addChangeWikiLinkToExplicitLink(element, state, ANNOTATION_WEAK_WARNING);
                }
            } else {
                FileReference elementFileReference = new FileReference(element.getContainingFile());
                String gitHubRepoPath = elementFileReference.getGitHubRepoPath();

                // now set to right name or to an accessible name
                FileReferenceList matchedFilesReferenceList = new FileReferenceListQuery(filesReferenceListQuery)
                        .caseInsensitive()
                        .keepLinkRefAnchor()
                        .gitHubWikiRules()
                        .matchLinkRef(element, haveExt)
                        .all();

                FileReferenceList otherFileRefList = matchedFilesReferenceList;

                if (matchedFilesReferenceList.size() > 1 && gitHubRepoPath != null) {
                    // see if eliminating files out of this list
                    otherFileRefList = matchedFilesReferenceList.pathStartsWith(gitHubRepoPath);
                }

                FileReference[] otherReferences = otherFileRefList.get();

                if (otherReferences.length != 1) {
                    if (sourceReference.resolveExternalLinkRef(element.getFileName(), false, false) != null) {
                        state.annotator = state.holder.createInfoAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.link.resolves-to-external"));
                    }
                } else {
                    FileReferenceLinkGitHubRules referenceLink = (FileReferenceLinkGitHubRules) otherReferences[0];
                    FileReferenceLink.InaccessibleLinkRefReasons reasons = referenceLink.inaccessibleLinkRefReasons(element.getName());
                    state.warningsOnly = true;

                    PsiFile psiFile = referenceLink.getPsiFile();
                    if (reasons.caseMismatch()) {
                        if (!state.alreadyOfferedIds(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.caseMismatchLinkRefFixed())
                                || !state.alreadyOfferedId(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFullFilePath(), reasons.caseMismatchFileNameFixed())) {

                            state.needTargetList = false;
                            state.annotator = state.holder.createWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.case-mismatch"));

                            if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.caseMismatchLinkRefFixed())) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, reasons.caseMismatchLinkRefFixed(), ChangeLinkRefQuickFix.MATCH_CASE_TO_FILE));
                            }

                            if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFullFilePath(), reasons.caseMismatchFileNameFixed())) {
                                state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, reasons.caseMismatchFileNameFixed()));
                            }
                        }
                    }

                    if (!haveExt && withExt) {
                        state.needTargetList = false;
                        state.annotator = state.holder.createWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.image.missing-extension"));
                        state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                        if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, referenceLink.getLinkRef())) {
                            state.annotator.registerFix(new ChangeLinkRefQuickFix(element, referenceLink.getLinkRef()));
                        }
                    }

                    if (reasons.targetNotInSameRepoHome()) {
                        state.warningsOnly = false;
                        // can offer to move the file, just add the logic
                        state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.link.unreachable-link-reference-not-in-same-repo"));
                        state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                    }

                    if (reasons.targetNameHasAnchor()) {
                        state.needTargetList = false;
                        state.warningsOnly = false;
                        state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.file-anchor"));
                        state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                        if (referenceLink.canRenameFileTo(reasons.targetNameHasAnchorFixed())) {
                            if (state.addingAlreadyOffered(TYPE_RENAME_FILE_AND_RE_TARGET_QUICK_FIX, referenceLink.getFullFilePath(), reasons.targetNameHasAnchorFixed())) {
                                state.annotator.registerFix(new RenameFileAndReTargetQuickFix(referenceLink.getPsiFileWithAnchor(), reasons.targetNameHasAnchorFixed(), element, RENAME_KEEP_PATH | RENAME_KEEP_TEXT | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_TITLE));
                            }
                        }
                    }

                    if (reasons.targetPathHasAnchor()) {
                        state.needTargetList = false;
                        state.canCreateFile = false;
                        state.warningsOnly = false;
                        state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.path-anchor"));
                        state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                        // TODO: create quick fix to remove anchors from all directories in the path
                    }
                }

                if (!state.warningsOnly) {
                    // offer to create the file and
                    if (state.annotator == null) {
                        // creation fix
                        state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unresolved-link-reference"));
                        state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                    }

                    if (state.canCreateFile) {
                        String fileName = element.getFileName();
                        FileReference thisFile = new FileReference(element.getContainingFile());
                        FileReference newFile = new FileReference(thisFile.getPath() + fileName, element.getProject());
                        if (newFile.canCreateFile()) {
                            state.annotator.registerFix(new CreateFileQuickFix(fileName));
                        }
                    }

                    // get all accessibles
                    if (filesReferenceList.size() != 0 && state.needTargetList) {
                        /*
                         *   have a file but it is not accessible we can:
                         *   1. rename the link to another accessible file?
                         */
                        FileReferenceList linkRefs = filesReferenceList.query().gitHubWikiRules().inSource(containingFile).sameGitHubRepo().all();

                        FileReference[] references = linkRefs.get();
                        Arrays.sort(references);

                        for (FileReference fileReference : references) {
                            FileReferenceLink linkRef = (FileReferenceLink) fileReference;

                            if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, linkRef.getLinkRef())) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, linkRef.getLinkRef(), 0, RENAME_KEEP_ANCHOR));
                                // TODO: make max quick fix wikilink targets a config item
                                if (state.getAlreadyOfferedSize(TYPE_CHANGE_LINK_REF_QUICK_FIX) >= 15) break;
                            }
                        }
                    }

                    state.annotator.setNeedsUpdateOnTyping(true);
                }
            }
        }
    }

    public void annotateWikiLinkRef(MultiMarkdownWikiPageRef element, AnnotationState state) {
        MultiMarkdownWikiLink wikiLink = (MultiMarkdownWikiLink) element.getParent();

        FilePathInfo pathInfo = new FilePathInfo(element.getNameWithAnchor());

        // if not reversed ref and text and not just a link reference
        if (pathInfo.getFileName().isEmpty()) {
            if (pathInfo.getFileNameWithAnchor().startsWith("#")) {
                FilePathInfo sourceFileInfo = new FilePathInfo(element.getContainingFile().getVirtualFile());
                String newLinkRef = sourceFileInfo.getFileNameNoExtAsWikiRef() + pathInfo.getFullFilePath();

                if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, newLinkRef)) {
                    state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.has-only-anchor"));
                    state.annotator.registerFix(new ChangeLinkRefQuickFix(element, newLinkRef, ChangeLinkRefQuickFix.ADD_PAGE_REF, RENAME_KEEP_TEXT));
                }

                // add quick fix to change wiki to explicit link
                if (state.addingAlreadyOffered(TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK)) {
                    state.annotator.registerFix(new ChangeWikiLinkToExplicitLinkQuickFix(wikiLink));
                }
            }
        } else {
            if (wikiLink != null) checkWikiLinkSwapRefTitle(wikiLink, state);

            // see if it exists
            MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();

            FileReferenceList filesReferenceList = new FileReferenceListQuery(element.getProject())
                    .keepLinkRefAnchor()
                    .wantMarkdownFiles()
                    .all();

            FileReferenceList accessibleWikiPageRefs = filesReferenceList.query()
                    .caseSensitive() // we want to catch mismatches
                    .gitHubWikiRules()
                    .matchWikiRef(element)
                    .accessibleWikiPageRefs();

            if (!containingFile.isWikiPage()) {
                state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.github-only-on-wiki-page"));
                state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                // add quick fix to change wiki to explicit link
                if (state.addingAlreadyOffered(TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK)) {
                    state.annotator.registerFix(new ChangeWikiLinkToExplicitLinkQuickFix(wikiLink));
                }
            } else if (accessibleWikiPageRefs.size() == 1) {
                // add quick fix to change wiki to explicit link
            } else {
                if (accessibleWikiPageRefs.size() > 1) {
                    state.warningsOnly = false;
                    state.canCreateFile = false;
                    state.annotator = state.holder.createWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.multiple-targets-match"));

                    //state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                    FileReference[] sorted = accessibleWikiPageRefs.getSorted(1);
                    for (FileReference referenceLink : sorted) {
                        String linkRef = referenceLink.getLinkRefFromWikiHome();
                        String newName = linkRef.replace('/', '-');

                        if (!linkRef.contains("/")) {
                            // it is in wiki home, prefix with home-
                            newName = "home-" + newName;
                        }

                        if (!linkRef.equals(newName) && !referenceLink.getFileName().equals(newName) && referenceLink.canRenameFileTo(newName)) {
                            PsiFile psiFile = referenceLink.getPsiFile();
                            if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFullFilePath(), newName)) {
                                state.annotator.registerFix(new RenameFileQuickFix(psiFile, linkRef, newName, RenameFileQuickFix.RENAME_CONFLICTING_TARGET));
                            }
                        }
                    }
                } else {
                    // not set to right name or to an accessible name
                    FileReferenceList matchedFilesReferenceList = filesReferenceList.query()
                            .spaceDashEqual()
                            .caseInsensitive()
                            .keepLinkRefAnchor()
                            .wantMarkdownFiles()
                            .gitHubWikiRules()
                            .matchWikiRef(element)
                            .all();

                    FileReferenceList otherFileRefList = matchedFilesReferenceList;

                    if (matchedFilesReferenceList.size() > 1) {
                        // see if eliminating files out of this wiki will help
                        otherFileRefList = matchedFilesReferenceList.sameWikiHomePageRefs();
                    }

                    FileReference[] otherReferences = otherFileRefList.get();

                    if (otherReferences.length == 1) {
                        FileReferenceLinkGitHubRules referenceLink = (FileReferenceLinkGitHubRules) otherReferences[0];
                        FileReferenceLink.InaccessibleWikiPageReasons reasons = referenceLink.inaccessibleWikiPageRefReasons(element.getName());
                        state.warningsOnly = true;

                        PsiFile psiFile = referenceLink.getPsiFile();
                        if (reasons.caseMismatch()) {

                            if (!state.alreadyOfferedId(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.caseMismatchWikiRefFixed())
                                    || !state.alreadyOfferedId(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFullFilePath(), reasons.caseMismatchFileNameFixed())) {
                                state.needTargetList = false;
                                state.annotator = state.holder.createWarningAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.case-mismatch"));

                                if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.caseMismatchWikiRefFixed())) {
                                    state.annotator.registerFix(new ChangeLinkRefQuickFix(element, reasons.caseMismatchWikiRefFixed(), ChangeLinkRefQuickFix.MATCH_CASE_TO_FILE));
                                }

                                if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFullFilePath(), reasons.caseMismatchFileNameFixed())) {
                                    state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, reasons.caseMismatchFileNameFixed()));
                                }
                            }
                        }

                        if (reasons.targetNotInWikiHome() || reasons.targetNotInSameWikiHome()) {
                            state.warningsOnly = false;
                            // can offer to move the file, just add the logic
                            state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unreachable-page-reference-not-in-wiki-home"));
                            state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                        }

                        if (reasons.wikiRefHasSlash() || reasons.wikiRefHasFixableSlash() || reasons.wikiRefHasSubDir()) {
                            state.warningsOnly = false;
                            // can offer to move the file, just add the logic
                            state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.wiki-ref-has-slash"));
                            state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

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
                            state.warningsOnly = false;
                            state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.file-spaces"));
                            state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                            if (referenceLink.canRenameFileTo(reasons.targetNameHasSpacedFixed())) {
                                if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFullFilePath(), reasons.targetNameHasSpacedFixed())) {
                                    state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, reasons.targetNameHasSpacedFixed()));
                                }
                            }
                        }

                        if (reasons.targetNameHasAnchor()) {
                            state.needTargetList = false;
                            state.warningsOnly = false;
                            state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.file-anchor"));
                            state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                            if (referenceLink.canRenameFileTo(reasons.targetNameHasAnchorFixed())) {
                                if (state.addingAlreadyOffered(TYPE_RENAME_FILE_AND_RE_TARGET_QUICK_FIX, referenceLink.getFullFilePath(), reasons.targetNameHasAnchorFixed())) {
                                    state.annotator.registerFix(new RenameFileAndReTargetQuickFix(referenceLink.getPsiFileWithAnchor(), reasons.targetNameHasAnchorFixed(), element, RENAME_KEEP_TEXT | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_TITLE));
                                }
                            }
                        } else if (reasons.targetNotWikiPageExt()) {
                            // can offer to move the file, just add the logic
                            state.warningsOnly = false;
                            state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.target-not-wiki-page-ext"));
                            state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                            if (referenceLink.canRenameFileTo(reasons.targetNotWikiPageExtFixed())) {
                                if (psiFile != null && state.addingAlreadyOffered(TYPE_RENAME_FILE_QUICK_FIX, referenceLink.getFullFilePath(), reasons.targetNotWikiPageExtFixed())) {
                                    state.annotator.registerFix(new RenameFileQuickFix(psiFile, null, reasons.targetNotWikiPageExtFixed()));
                                }
                            }

                            // add quick fix to change wiki to explicit link
                            if (state.addingAlreadyOffered(TYPE_CHANGE_WIKI_LINK_QUICK_FIX_TO_EXPLICIT_LINK)) {
                                state.annotator.registerFix(new ChangeWikiLinkToExplicitLinkQuickFix(wikiLink));
                            }
                        }

                        if (reasons.targetPathHasAnchor()) {
                            state.needTargetList = false;
                            state.canCreateFile = false;
                            state.warningsOnly = false;
                            state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.path-anchor"));
                            state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                            // TODO: create quick fix to remove anchors from all directories in the path
                            //if (canRenameFile(referenceLink.getVirtualFile(), reasons.targetNameHasAnchorFixed())) {
                            //    state.annotator.registerFix(new RenameFileQuickFix(referenceLink.getVirtualFile(), reasons.targetNameHasAnchorFixed()));
                            //}
                        }

                        if (reasons.wikiRefHasDashes()) {
                            state.needTargetList = false;
                            state.warningsOnly = false;
                            state.canCreateFile = false;
                            state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.link-dashes"));

                            if (state.addingAlreadyOffered(TYPE_CHANGE_LINK_REF_QUICK_FIX, reasons.wikiRefHasDashesFixed())) {
                                state.annotator.registerFix(new ChangeLinkRefQuickFix(element, reasons.wikiRefHasDashesFixed(), ChangeLinkRefQuickFix.REMOVE_DASHES));
                            }
                        }
                    }
                }

                if (!state.warningsOnly) {
                    // offer to create the file and
                    if (state.annotator == null) {
                        // creation fix
                        state.annotator = state.holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unresolved-link-reference"));
                        state.annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                    }

                    if (state.canCreateFile) {
                        String fileName = element.getFileName();
                        FileReference thisFile = new FileReference(element.getContainingFile());
                        FileReference newFile = new FileReference(thisFile.getPath() + fileName, element.getProject());
                        if (newFile.canCreateFile()) {
                            state.annotator.registerFix(new CreateFileQuickFix(fileName));
                        }
                    }

                    // get all accessibles
                    if (filesReferenceList.size() != 0 && state.needTargetList) {
                        FileReferenceList wikiPageRefs = filesReferenceList.query().gitHubWikiRules().inSource(containingFile).accessibleWikiPageRefs();

                        FileReference[] references = wikiPageRefs.get();
                        Arrays.sort(references);

                        for (FileReference fileReference : references) {
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

                    state.annotator.setNeedsUpdateOnTyping(true);
                }
            }
        }
    }
}
