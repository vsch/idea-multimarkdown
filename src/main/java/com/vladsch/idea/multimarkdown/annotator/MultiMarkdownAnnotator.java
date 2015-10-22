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
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.language.MultiMarkdownReferenceWikiPageRef;
import com.vladsch.idea.multimarkdown.psi.*;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import com.vladsch.idea.multimarkdown.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.RENAME_KEEP_ANCHOR;

//public class MultiMarkdownAnnotator extends ExternalAnnotator<String, Set<MultiMarkdownAnnotator.HighlightableToken>> {
public class MultiMarkdownAnnotator implements Annotator {
    private static final Logger LOGGER = Logger.getInstance(MultiMarkdownAnnotator.class);

    //private static final SyntaxHighlighter SYNTAX_HIGHLIGHTER = new MultiMarkdownSyntaxHighlighter();
    //private MultiMarkdownGlobalSettingsListener globalSettingsListener = null;

    ///** Build a new instance of {@link MultiMarkdownAnnotator}. */
    //public MultiMarkdownAnnotator() {
    //    // Listen to global settings changes.
    //    MultiMarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MultiMarkdownGlobalSettingsListener() {
    //        public void handleSettingsChanged(@NotNull final MultiMarkdownGlobalSettings newSettings) {
    //        }
    //    });
    //}
    protected Annotation checkWikiLinkSwapRefTitle(@NotNull MultiMarkdownWikiLink element, @NotNull AnnotationHolder holder) {
        // see if need to swap link ref and link text
        Annotation annotator = null;

        MultiMarkdownWikiPageRef wikiPageRef = (MultiMarkdownWikiPageRef) MultiMarkdownPsiImplUtil.findChildByType(element, MultiMarkdownTypes.WIKI_LINK_REF);
        PsiReference wikiPageRefReference = wikiPageRef != null ? wikiPageRef.getReference() : null;

        if (wikiPageRefReference != null) {
            MultiMarkdownWikiPageTitle wikiPageTitle = (MultiMarkdownWikiPageTitle) MultiMarkdownPsiImplUtil.findChildByType(element, MultiMarkdownTypes.WIKI_LINK_TITLE);

            if (wikiPageTitle != null && wikiPageTitle.getName() != null) {
                // see if the link title resolves to a page
                MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(element.getProject());
                MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();

                if (wikiPageTitle.getName().equals(wikiPageRef.getName())) {
                    // can get rid off the text
                    annotator = holder.createWeakWarningAnnotation(wikiPageTitle.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.redundant-page-title"));
                    annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
                } else if (projectComponent != null) {
                    FileReferenceList accessibleWikiPageRefs = projectComponent.getFileReferenceList().query()
                            .wantMarkdownFiles()
                            .inSource(containingFile)
                            .matchWikiRef(wikiPageTitle.getName())
                            .accessibleWikiPageRefs();

                    if (accessibleWikiPageRefs.size() == 1) {
                        if (((MultiMarkdownReferenceWikiPageRef) wikiPageRefReference).isResolveRefMissing()) {
                            annotator = holder.createErrorAnnotation(element.getTextRange(),
                                    MultiMarkdownGlobalSettings.getInstance().githubWikiLinks.getValue()
                                            ? MultiMarkdownBundle.message("annotation.wikilink.ref-title-github")
                                            : MultiMarkdownBundle.message("annotation.wikilink.ref-title-swapped"));

                            annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                            annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                            annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
                        } else if (accessibleWikiPageRefs.get()[0].getFileNameNoExtAsWikiRef().equals(wikiPageTitle.getName())) {
                            annotator = holder.createWeakWarningAnnotation(wikiPageTitle.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.swap-ref-title"));
                            annotator.registerFix(new DeleteWikiPageTitleQuickFix(element));
                            annotator.registerFix(new DeleteWikiPageRefQuickFix(element));
                            annotator.registerFix(new SwapWikiPageRefTitleQuickFix(element));
                        }
                    }
                }
            }
        }
        return annotator;
    }

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        //noinspection StatementWithEmptyBody
        if (element instanceof MultiMarkdownWikiPageTitle) {
            //Annotation annotator = null;
            //MultiMarkdownWikiLink wikiLink = (MultiMarkdownWikiLink) element.getParent();
            //if (wikiLink != null) annotator = checkWikiLinkSwapRefTitle(wikiLink, holder);
        } else if (element instanceof MultiMarkdownWikiPageRef) {
            Annotation annotator = null;
            MultiMarkdownWikiLink wikiLink = (MultiMarkdownWikiLink) element.getParent();
            if (wikiLink != null) annotator = checkWikiLinkSwapRefTitle(wikiLink, holder);

            FilePathInfo pathInfo = new FilePathInfo(element.getText());

            // if not reversed ref and text and not just a link reference
            if (annotator == null && !FilePathInfo.linkRefNoAnchor(pathInfo.getFileName()).isEmpty()) {
                // see if it exists
                MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(element.getProject());
                if (projectComponent != null) {
                    MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();

                    FileReferenceList filesReferenceList = projectComponent.getFileReferenceList().query()
                            .keepLinkRefAnchor()
                            .wantMarkdownFiles()
                            .all();

                    FileReferenceList matchedFilesReferenceList = filesReferenceList.query()
                            .spaceDashEqual()
                            .caseInsensitive()
                            .keepLinkRefAnchor()
                            .wantMarkdownFiles()
                            .gitHubWikiRules()
                            .matchWikiRef((MultiMarkdownWikiPageRef) element)
                            .all();

                    FileReferenceList accessibleWikiPageRefs = filesReferenceList.query()
                            .caseSensitive() // we want to catch mismatches
                            .gitHubWikiRules()
                            .matchWikiRef((MultiMarkdownWikiPageRef) element)
                            .accessibleWikiPageRefs();

                    if (!containingFile.isWikiPage()) {
                        annotator = holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.github-only-on-wiki-page"));
                        annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                    } else if (accessibleWikiPageRefs.size() != 1) {
                        boolean warningsOnly = false;
                        boolean canCreateFile = true;
                        boolean needTargetList = true;
                        HashSet<String> alreadyOffered = new HashSet<String>();

                        if (accessibleWikiPageRefs.size() > 1) {
                            warningsOnly = false;
                            canCreateFile = false;
                            annotator = holder.createWarningAnnotation(element.getTextRange(),
                                    MultiMarkdownBundle.message("annotation.wikilink.multiple-targets-match"));

                            //annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                            FileReference[] sorted = accessibleWikiPageRefs.getSorted(1);
                            for (FileReference referenceLink : sorted) {
                                String linkRef = referenceLink.getLinkRefFromWikiHome();
                                String newName = linkRef.replace('/', '-');

                                if (!linkRef.contains("/")) {
                                    // it is in wiki home, prefix with home-
                                    newName = "home-" + newName;
                                }

                                if (!linkRef.equals(newName) && !referenceLink.getFileName().equals(newName) && referenceLink.canRenameFileTo(newName)) {
                                    annotator.registerFix(new RenameWikiPageQuickFix(referenceLink.getPsiFile(), linkRef, newName, RenameWikiPageQuickFix.RENAME_CONFLICTING_TARGET));
                                }
                            }
                        } else {
                            // not set to right name or to an accessible name
                            FileReferenceList otherFileRefList = matchedFilesReferenceList;

                            if (matchedFilesReferenceList.size() > 1) {
                                // see if eliminating files out of this wiki will help
                                otherFileRefList = matchedFilesReferenceList.sameWikiHomePageRefs();
                            }
                            FileReference[] otherReferences = otherFileRefList.get();

                            if (otherReferences.length == 1) {
                                FileReferenceLinkGitHubRules referenceLink = (FileReferenceLinkGitHubRules) otherReferences[0];
                                FileReferenceLink.InaccessibleWikiPageReasons reasons = referenceLink.inaccessibleWikiPageRefReasons(((MultiMarkdownWikiPageRef) element).getName());
                                warningsOnly = true;

                                if (reasons.caseMismatch()) {
                                    needTargetList = false;
                                    annotator = holder.createWarningAnnotation(element.getTextRange(),
                                            MultiMarkdownBundle.message("annotation.wikilink.case-mismatch"));

                                    if (!alreadyOffered.contains(reasons.caseMismatchWikiRefFixed())) {
                                        annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, reasons.caseMismatchWikiRefFixed(), ChangeWikiPageRefQuickFix.MATCH_CASE_TO_FILE));
                                        alreadyOffered.add(reasons.caseMismatchWikiRefFixed());
                                    }

                                    annotator.registerFix(new RenameWikiPageQuickFix(referenceLink.getPsiFile(), null, reasons.caseMismatchFileNameFixed()));
                                }

                                if (reasons.targetNotInWikiHome() || reasons.targetNotInSameWikiHome()) {
                                    warningsOnly = false;
                                    // can offer to move the file, just add the logic
                                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                                            MultiMarkdownBundle.message("annotation.wikilink.unreachable-page-reference-not-in-wiki-home"));

                                    annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                                }

                                if (reasons.wikiRefHasSlash() || reasons.wikiRefHasFixableSlash() || reasons.wikiRefHasSubDir()) {
                                    warningsOnly = false;
                                    // can offer to move the file, just add the logic
                                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                                            MultiMarkdownBundle.message("annotation.wikilink.wiki-ref-has-slash"));

                                    annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                                    if (reasons.wikiRefHasFixableSlash()) {
                                        if (!alreadyOffered.contains(reasons.wikiRefHasSlashFixed())) {
                                            annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, reasons.wikiRefHasSlashFixed(), ChangeWikiPageRefQuickFix.REMOVE_SLASHES, RENAME_KEEP_ANCHOR));
                                            alreadyOffered.add(reasons.wikiRefHasSlashFixed());
                                        }
                                    }
                                    if (reasons.wikiRefHasSubDir()) {
                                        if (!alreadyOffered.contains(reasons.wikiRefHasSubDirFixed())) {
                                            annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, reasons.wikiRefHasSubDirFixed(), ChangeWikiPageRefQuickFix.REMOVE_SUBDIR, RENAME_KEEP_ANCHOR));
                                            alreadyOffered.add(reasons.wikiRefHasSubDirFixed());
                                        }
                                    }
                                }

                                if (reasons.targetNotWikiPageExt()) {
                                    // can offer to move the file, just add the logic
                                    warningsOnly = false;
                                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                                            MultiMarkdownBundle.message("annotation.wikilink.target-not-wiki-page-ext"));

                                    annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                                    if (referenceLink.canRenameFileTo(reasons.targetNotWikiPageExtFixed())) {
                                        annotator.registerFix(new RenameWikiPageQuickFix(referenceLink.getPsiFile(), null, reasons.targetNotWikiPageExtFixed()));
                                    }
                                }

                                if (reasons.targetNameHasSpaces()) {
                                    needTargetList = false;
                                    warningsOnly = false;
                                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                                            MultiMarkdownBundle.message("annotation.wikilink.file-spaces"));

                                    annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                                    if (referenceLink.canRenameFileTo(reasons.targetNameHasSpacedFixed())) {
                                        annotator.registerFix(new RenameWikiPageQuickFix(referenceLink.getPsiFile(), null, reasons.targetNameHasSpacedFixed()));
                                    }
                                }

                                if (reasons.targetNameHasAnchor()) {
                                    needTargetList = false;
                                    warningsOnly = false;
                                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                                            MultiMarkdownBundle.message("annotation.wikilink.file-anchor"));

                                    annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                                    if (referenceLink.canRenameFileTo(reasons.targetNameHasAnchorFixed())) {
                                        FileReference targetReference = new FileReference(referenceLink.getPath() + reasons.targetNameHasAnchorFixed(), element.getProject());
                                        FileReferenceLink re_targetedLink = new FileReferenceLink(containingFile, targetReference);
                                        annotator.registerFix(new RenameWikiPageAndReTargetQuickFix(referenceLink.getPsiFileWithAnchor(), reasons.targetNameHasAnchorFixed(), (MultiMarkdownWikiPageRef) element, re_targetedLink.getWikiPageRef()));
                                    }
                                }

                                if (reasons.targetPathHasAnchor()) {
                                    needTargetList = false;
                                    canCreateFile = false;
                                    warningsOnly = false;
                                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                                            MultiMarkdownBundle.message("annotation.wikilink.path-anchor"));

                                    annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

                                    // TODO: create quick fix to remove anchors from all directories in the path
                                    //if (canRenameFile(referenceLink.getVirtualFile(), reasons.targetNameHasAnchorFixed())) {
                                    //    annotator.registerFix(new RenameWikiPageQuickFix(referenceLink.getVirtualFile(), reasons.targetNameHasAnchorFixed()));
                                    //}
                                }

                                if (reasons.wikiRefHasDashes()) {
                                    needTargetList = false;
                                    warningsOnly = false;
                                    canCreateFile = false;

                                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                                            MultiMarkdownBundle.message("annotation.wikilink.link-dashes"));

                                    if (!alreadyOffered.contains(reasons.wikiRefHasDashesFixed())) {
                                        annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, reasons.wikiRefHasDashesFixed(), ChangeWikiPageRefQuickFix.REMOVE_DASHES));
                                        alreadyOffered.add(reasons.wikiRefHasDashesFixed());
                                    }
                                }
                            }

                        }

                        if (!warningsOnly) {
                            // offer to create the file and
                            if (annotator == null) {
                                // creation fix
                                annotator = holder.createErrorAnnotation(element.getTextRange(), MultiMarkdownBundle.message("annotation.wikilink.unresolved-page-reference"));
                                annotator.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                            }

                            if (canCreateFile) {
                                String fileName = ((MultiMarkdownWikiPageRef) element).getFileName();
                                FileReference thisFile = new FileReference(element.getContainingFile());
                                FileReference newFile = new FileReference(thisFile.getPath() + fileName, element.getProject());
                                if (newFile.canCreateFile()) {
                                    annotator.registerFix(new CreateWikiPageQuickFix(fileName));
                                }
                            }

                            // get all accessibles
                            if (filesReferenceList.size() != 0 && needTargetList) {
                            /*
                             *   have a file but it is not accessible we can:
                             *   1. rename the link to another accessible file?
                             */
                                FileReferenceList wikiPageRefs = filesReferenceList.query().gitHubWikiRules().inSource(containingFile).accessibleWikiPageRefs();

                                FileReference[] references = wikiPageRefs.get();
                                Arrays.sort(references);

                                for (FileReference fileReference : references) {
                                    FileReferenceLink wikiPageRef = (FileReferenceLink) fileReference;

                                    if (wikiPageRef.getUpDirectories() <= wikiPageRef.getUpDirectoriesToWikiHome() && !alreadyOffered.contains(wikiPageRef.getWikiPageRef())) {
                                        annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, wikiPageRef.getWikiPageRef(), 0, RENAME_KEEP_ANCHOR));
                                        alreadyOffered.add(wikiPageRef.getWikiPageRef());
                                        // TODO: make max quick fix wikilink targets a config item
                                        if (alreadyOffered.size() >= 15) break;
                                    }
                                }
                            }

                            annotator.setNeedsUpdateOnTyping(true);
                        }
                    }
                }
            }
        }
    }

    //private static Editor getEditorFromFocus() {
    //    final Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    //    if (c instanceof EditorComponentImpl) {
    //        return ((EditorComponentImpl) c).getEditor();
    //    }
    //    return null;
    //}
    //
    //protected void addProblemDescriptor(MultiMarkdownNamedElement element, ProblemsHolder holder) {
    //    final ProblemDescriptor problemDescriptor = createProblemDescriptor(element, offset, textRange, holder, fixes, true);
    //    Editor editor = getEditorFromFocus();
    //    editor.getProject()
    //
    //    holder.createProblem(problemDescriptor);
    //}
    //
    //private static ProblemDescriptor createProblemDescriptor(PsiElement element, int offset, TextRange textRange, ProblemsHolder holder,
    //                                                         SpellCheckerQuickFix[] fixes,
    //                                                         boolean onTheFly) {
    //    final String description = MultiMarkdownBundle.message("quickfix.wikilink.unresolved-link");
    //    final TextRange highlightRange = TextRange.from(offset + textRange.getStartOffset(), textRange.getLength());
    //    assert highlightRange.getStartOffset()>=0;
    //
    //    return holder.getManager()
    //            .createProblemDescriptor(element, highlightRange, description, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, onTheFly, fixes);
    //}

    ///**
    // * Get the text source of the given file.
    // *
    // * @param file the {@link PsiFile} to process.
    // *
    // * @return the file text.
    // */
    //@Nullable @Override
    //public String collectInformation(@NotNull PsiFile file) {
    //    return file.getText();
    //}
    //
    ///**
    // * Collect {@link MultiMarkdownAnnotator.HighlightableToken}s from the given file.
    // *
    // * @param source the source text to process.
    // *
    // * @return a {@link Set} of {@link MultiMarkdownAnnotator.HighlightableToken}s that should be used to do the file syntax highlighting.
    // */
    //@Override
    //public Set<HighlightableToken> doAnnotate(final String source) {
    //    return new HashSet<HighlightableToken>();
    //}
    //
    ///**
    // * Convert collected {@link MultiMarkdownAnnotator.HighlightableToken}s in syntax highlighting annotations.
    // *
    // * @param file             the source file.
    // * @param annotationResult the {@link Set} of {@link MultiMarkdownAnnotator.HighlightableToken}s collected on the file.
    // * @param holder           the annotation holder.
    // */
    //@Override
    //public void apply(final @NotNull PsiFile file,
    //        final Set<HighlightableToken> annotationResult,
    //        final @NotNull AnnotationHolder holder) {
    //
    //    for (final HighlightableToken token : annotationResult) {
    //        final TextAttributesKey[] attrs = SYNTAX_HIGHLIGHTER.getTokenHighlights(token.getElementType());
    //
    //        if (attrs.length > 0) holder.createInfoAnnotation(token.getRange(), null).setTextAttributes(attrs[0]);
    //    }
    //}
    //
    //protected class HighlightableToken {
    //
    //    protected final TextRange range;
    //    protected final IElementType elementType;
    //
    //    public HighlightableToken(final TextRange range, final IElementType elementType) {
    //        this.range = range;
    //        this.elementType = elementType;
    //    }
    //
    //    public TextRange getRange() { return range; }
    //
    //    public IElementType getElementType() { return elementType; }
    //}
}
