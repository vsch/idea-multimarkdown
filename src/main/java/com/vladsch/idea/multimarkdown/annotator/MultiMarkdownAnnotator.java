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

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.util.FileReference;
import com.vladsch.idea.multimarkdown.util.FileReferenceLink;
import com.vladsch.idea.multimarkdown.util.FileReferenceList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

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

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof MultiMarkdownWikiPageRef) {
            // see if it exists
            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(element.getProject());
            MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();
            VirtualFile virtualFile = containingFile.getVirtualFile();

            FileReferenceList filesReferenceList = projectComponent.getFileReferenceList().query()
                    .wantMarkdownFiles()
                    .all();

            FileReferenceList matchedFilesReferenceList = filesReferenceList.query()
                    .matchWikiRef((MultiMarkdownWikiPageRef) element)
                    .spaceDashEqual()
                    .caseInsensitive()
                    .allWikiPageRefs();

            FileReferenceList accessibleWikiPageRefs = matchedFilesReferenceList.query()
                    .matchWikiRef((MultiMarkdownWikiPageRef) element)
                    .accessibleWikiPageRefs();

            Annotation annotator = null;

            if (accessibleWikiPageRefs.length() != 1) {
                // not set to right name or to an accessible name
                HashSet<String> alreadyOffered = new HashSet<String>();
                FileReference[] otherReferences = matchedFilesReferenceList.getFileReferences();

                if (otherReferences.length == 1) {
                    FileReferenceLink referenceLink = (FileReferenceLink) otherReferences[0];
                    FileReferenceLink.InaccessibleWikiPageReasons inaccessibleReasons = referenceLink.inaccessibleWikiPageRefReasons(((MultiMarkdownWikiPageRef) element).getName());

                    String linkRefFileName = "";

                    if (inaccessibleReasons.targetNotInSameWikiHome() || inaccessibleReasons.targetNotInWikiHome()) {
                        // can offer to move the file, just add the logic
                        annotator = holder.createErrorAnnotation(element.getTextRange(),
                                MultiMarkdownBundle.message("annotation.wikilink.unreachable-page-reference"));
                    }

                    if (inaccessibleReasons.caseMismatch() || inaccessibleReasons.targetNameHasSpaces() || inaccessibleReasons.wikiRefHasDashes() || inaccessibleReasons.targetNotWikiPageExt()) {
                        // see if the file with spaces instead of dashes exists
                        // has a space in the file name instead of a dash

                        boolean wikiRefDashes = inaccessibleReasons.wikiRefHasDashes();
                        boolean fileNameHasSpaces = inaccessibleReasons.targetNameHasSpaces();
                        boolean caseMismatch = inaccessibleReasons.caseMismatch();
                        String fileNameHasSpacesFixed = fileNameHasSpaces ? inaccessibleReasons.targetNameHasSpacedFixed() : null;
                        String wikiRefDashesFixed = inaccessibleReasons.wikiRefHasDashesFixed();
                        String caseMismatchFileNameFixed = inaccessibleReasons.caseMismatchFileNameFixed();
                        String caseMismatchWikiRefFixed = inaccessibleReasons.caseMismatchWikiRefFixed();

                        if (fileNameHasSpaces) {
                            VirtualFile targetVirtualFile = referenceLink.getVirtualFile();
                            VirtualFile parent = targetVirtualFile != null ? targetVirtualFile.getParent() : null;
                            if (parent == null || parent.findChild(fileNameHasSpacesFixed) != null) {
                                // already exists
                                fileNameHasSpacesFixed = null;
                            }
                        }

                        annotator = holder.createErrorAnnotation(element.getTextRange(),
                                MultiMarkdownBundle.message("annotation.wikilink.unresolved-page-reference",
                                        (wikiRefDashes ? MultiMarkdownBundle.message("annotation.wikilink.link-dashes") : ""),
                                        (fileNameHasSpaces ? MultiMarkdownBundle.message("annotation.wikilink.file-spaces") : ""),
                                        (caseMismatch ? MultiMarkdownBundle.message("annotation.wikilink.case-mismatch") : "")
                                ));

                        if (wikiRefDashes && !alreadyOffered.contains(wikiRefDashesFixed)) {
                            annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, wikiRefDashesFixed));
                            alreadyOffered.add(wikiRefDashesFixed);
                        }

                        if (fileNameHasSpacesFixed != null)
                            annotator.registerFix(new RenameWikiPageQuickFix(referenceLink.getVirtualFile(), fileNameHasSpacesFixed));

                        if (caseMismatch && !alreadyOffered.contains(caseMismatchWikiRefFixed)) {
                            annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, caseMismatchWikiRefFixed));
                            alreadyOffered.add(caseMismatchWikiRefFixed);
                        }
                    }
                } else {
                    // allow creation fix
                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                            MultiMarkdownBundle.message("annotation.wikilink.unresolved-page-reference", "", "", ""));

                    // TODO: validate file name before creating a quick fix for it
                    String fileName = ((MultiMarkdownWikiPageRef) element).getFileName();
                    annotator.registerFix(new CreateWikiPageQuickFix(fileName));
                }

                if (annotator != null) annotator.setNeedsUpdateOnTyping(true);

                // get all accessibles
                if (annotator != null && filesReferenceList.length() != 0) {
                    /*
                     *   have a file but it is not accessible we can:
                     *   1. rename the link to another accessible file?
                     */
                    FileReferenceList wikiPageRefs = filesReferenceList.query().inSource(containingFile).allWikiPageRefs();

                    FileReference[] references = wikiPageRefs.getFileReferences();
                    Arrays.sort(references);

                    for (FileReference fileReference : references) {
                        FileReferenceLink wikiPageRef = (FileReferenceLink) fileReference;

                        if (wikiPageRef.getUpDirectories() <= wikiPageRef.getUpDirectoriesToWikiHome() && !alreadyOffered.contains(wikiPageRef.getWikiPageRef())) {
                            annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, wikiPageRef.getWikiPageRef()));
                            alreadyOffered.add(wikiPageRef.getWikiPageRef());
                            if (alreadyOffered.size() >= 20) break;
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
