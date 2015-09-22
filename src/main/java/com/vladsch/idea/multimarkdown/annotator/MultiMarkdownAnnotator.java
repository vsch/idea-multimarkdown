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

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.quickfixes.SpellCheckerQuickFix;
import com.intellij.spellchecker.util.SpellCheckerBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.util.PathDistance;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashSet;
import java.util.List;

import static com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent.*;

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
            int searchSettings = WIKI_REF;

            MultiMarkdownFile containingFile = (MultiMarkdownFile) element.getContainingFile();
            VirtualFile virtualFile = containingFile.getVirtualFile();

            boolean isWikiPage = containingFile.isWikiPage();
            searchSettings |= isWikiPage ? WIKIPAGE_FILE : MARKDOWN_FILE;

            MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(element.getProject());
            String elementName = ((MultiMarkdownWikiPageRef) element).getName();
            if (elementName == null) elementName = "";

            List<MultiMarkdownFile> wikiFiles = projectComponent.findRefLinkMarkdownFiles(elementName, virtualFile, searchSettings);

            Annotation annotator = null;

            if (wikiFiles != null && wikiFiles.size() == 1) {
                //String path = wikiFiles.get(0).getVirtualFile().getPath();
                //annotator = holder.createInfoAnnotation(element.getTextRange(), " ");
                //annotator.setTooltip(path);
            } else {
                // not set to right name or to an accessible name
                // get all accessibles
                wikiFiles = projectComponent.findRefLinkMarkdownFiles(elementName, virtualFile, searchSettings | ALLOW_INACCESSIBLE_WIKI_REF);

                boolean linkInaccessible = wikiFiles != null && wikiFiles.size() == 1;

                String linkRefFileName = "";

                HashSet<String> alreadyOffered = new HashSet<String>();

                if (linkInaccessible) {
                    // can offer to move the file, just add the logic
                    annotator = holder.createErrorAnnotation(element.getTextRange(),
                            MultiMarkdownBundle.message("annotation.wikilink.unreachable-page-reference"));
                } else {
                    // see if the file with spaces instead of dashes exists
                    wikiFiles = projectComponent.findRefLinkMarkdownFiles(elementName, virtualFile, searchSettings | SPACE_DASH_EQUIVALENT);

                    if (wikiFiles != null && wikiFiles.size() == 1) {
                        // has a space in the file name instead of a dash
                        String alreadyOfferedRename = "";
                        boolean linkRefDashes = false;
                        boolean fileNameSpaces = false;
                        boolean caseMismatch = false;

                        alreadyOfferedRename = elementName.replace('-', ' ');
                        if (!alreadyOfferedRename.equals(elementName)) {
                            linkRefDashes = true;
                        }

                        String fileName = wikiFiles.get(0).getLinkRef(null, searchSettings);
                        linkRefFileName = wikiFiles.get(0).getLinkRef(virtualFile, searchSettings);
                        if (linkRefFileName != null) {
                            linkRefFileName = linkRefFileName.replace(' ', '-');
                        }

                        if (fileName != null && !fileName.replace(' ', '-').equals(fileName)) {
                            VirtualFile parent = wikiFiles.get(0).getVirtualFile().getParent();
                            fileName = fileName.replace(' ', '-');
                            if (parent.findChild(fileName) == null) {
                                fileNameSpaces = true;
                            }
                        }

                        String fileNameNoExt = fileName == null ? "" : fileName.substring(0, fileName.length() - 3);
                        if (alreadyOfferedRename.toLowerCase().equals(fileNameNoExt.replace('-', ' ').toLowerCase())
                                && !alreadyOfferedRename.equals(fileNameNoExt.replace('-', ' '))) {
                            caseMismatch = true;
                        }

                        annotator = holder.createErrorAnnotation(element.getTextRange(),
                                MultiMarkdownBundle.message("annotation.wikilink.unresolved-page-reference",
                                        (linkRefDashes ? MultiMarkdownBundle.message("annotation.wikilink.link-dashes") : ""),
                                        (fileNameSpaces ? MultiMarkdownBundle.message("annotation.wikilink.file-spaces") : ""),
                                        (caseMismatch ? MultiMarkdownBundle.message("annotation.wikilink.case-mismatch") : "")
                                ));

                        if (linkRefDashes) {
                            if (!alreadyOffered.contains(alreadyOfferedRename)) {
                                annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, alreadyOfferedRename));
                                alreadyOffered.add(alreadyOfferedRename);
                            }
                        }
                        if (fileNameSpaces)
                            annotator.registerFix(new RenameWikiPageQuickFix(wikiFiles.get(0).getVirtualFile(), fileName));

                        if (caseMismatch) {
                            // offer to rename the file to link ref case
                            List<String> path = FileUtil.splitPath(alreadyOfferedRename);
                            String name = path.get(path.size() - 1).replace(' ', '-') + ".md";
                            annotator.registerFix(new RenameWikiPageQuickFix(wikiFiles.get(0).getVirtualFile(), name));

                            if (linkRefFileName != null && !fileNameSpaces) {
                                // offer to rename the link to file case
                                String replaced = linkRefFileName.substring(0, linkRefFileName.length() - 3).replace('-', ' ');
                                if (!alreadyOffered.contains(replaced)) {
                                    annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, replaced));
                                    alreadyOffered.add(replaced);
                                }
                            }
                        }
                    } else {
                        // allow creation fix
                        annotator = holder.createErrorAnnotation(element.getTextRange(),
                                MultiMarkdownBundle.message("annotation.wikilink.unresolved-page-reference", "", "", ""));

                        // TODO: validate file name before creating a quick fix for it
                        String fileName = ((MultiMarkdownWikiPageRef) element).getFileName();
                        //VirtualFileSystem fileSystem = element.getContainingFile().getVirtualFile().getFileSystem();
                        annotator.registerFix(new CreateWikiPageQuickFix(fileName));
                    }
                }

                if (annotator != null) annotator.setNeedsUpdateOnTyping(true);

                // get all accessibles
                wikiFiles = projectComponent.findRefLinkMarkdownFiles(null, virtualFile, searchSettings);

                if (annotator != null && wikiFiles != null && wikiFiles.size() != 0) {
                    /*
                     *   have a file but it is not accessible we can:
                     *   1. rename the link to another accessible file?
                     */
                    PathDistance[] paths = PathDistance.loadLinkRefsPaths(wikiFiles, virtualFile, searchSettings);
                    for (PathDistance wikiPageRef : paths) {
                        if (wikiPageRef.getDistance() < 1 && !alreadyOffered.contains(wikiPageRef.getPath())) {
                            annotator.registerFix(new ChangeWikiPageRefQuickFix((MultiMarkdownWikiPageRef) element, wikiPageRef.getPath()));
                            alreadyOffered.add(wikiPageRef.getPath());
                            if (alreadyOffered.size() >= 10) break;
                        }
                    }
                }

                if (false && !isWikiPage) {
                    List<MultiMarkdownFile> markdownFiles = projectComponent.findRefLinkMarkdownFiles(elementName, virtualFile, (searchSettings & ~WIKIPAGE_FILE) | ALLOW_INACCESSIBLE_WIKI_REF);

                    if (markdownFiles != null && markdownFiles.size() != 0) {
                    /*
                     *   have files but they are not accessible we can:
                     *   1. move file
                     */
                        // TODO: create MoveFileQuickFix
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
