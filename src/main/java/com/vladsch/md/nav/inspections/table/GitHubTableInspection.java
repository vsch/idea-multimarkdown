// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.inspections.table;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.vladsch.flexmark.ext.tables.TableExtractingVisitor;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.format.MarkdownTable;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.LineAppendable;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.actions.handlers.util.PsiEditAdjustment;
import com.vladsch.md.nav.annotator.ReplaceTextChangeQuickFix;
import com.vladsch.md.nav.inspections.LocalInspectionToolBase;
import com.vladsch.md.nav.inspections.ProblemDescriptors;
import com.vladsch.md.nav.parser.MdLexParserManager;
import com.vladsch.md.nav.psi.element.MdComposite;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdTable;
import com.vladsch.md.nav.psi.util.BlockPrefixes;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.settings.ParserOptions;
import com.vladsch.md.nav.util.format.FlexmarkFormatOptionsAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GitHubTableInspection extends LocalInspectionToolBase {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (file.getFileType() != MdFileType.INSTANCE || !file.isValid() || isIgnoreFencedCodeContent(file)) {
            return null;
        }

        final Project project = file.getProject();
        final MdRenderingProfile renderingProfile = MdRenderingProfileManager.getInstance(project).getRenderingProfile(file);

        if (!renderingProfile.getParserSettings().anyOptions(ParserOptions.GFM_TABLE_RENDERING)) {
            return null;
        }

        final ProblemDescriptors problems = new ProblemDescriptors();
        final PsiEditAdjustment editContext = new PsiEditAdjustment(file);

        MdPsiImplUtil.findChildrenOfAnyType((MdFile) file, false, false, false, element -> {
            LineAppendable tableChars = editContext.getLineAppendable();
            tableChars.append(editContext.elementText(element)).line();
            MdPsiImplUtil.adjustLinePrefix(element, tableChars, editContext);

            Document root = MdLexParserManager.parseFlexmarkDocument(renderingProfile, tableChars.toString(false), null, true);

            if (root != null) {
                BlockPrefixes prefixes = MdPsiImplUtil.getBlockPrefixes(element, null, editContext).finalizePrefixes(editContext);
                BasedSequence childContPrefix = prefixes.getChildContPrefix();
                FlexmarkFormatOptionsAdapter flexmarkFormatOptionsAdapter = new FlexmarkFormatOptionsAdapter(editContext, element.getTextOffset(), element.getTextOffset() + element.getTextLength());
                MutableDataHolder formatOptions = flexmarkFormatOptionsAdapter.getTableFormatOptions("");
                formatOptions.set(TablesExtension.APPEND_MISSING_COLUMNS, true);
                TableExtractingVisitor tableVisitor = new TableExtractingVisitor(formatOptions);
                MarkdownTable[] tables = tableVisitor.getTables(root);
                if (tables.length > 0) {
                    MarkdownTable table = tables[0];

                    table.normalize();

                    if (table.header.rows.size() != 1) {
                        PsiElement tableHeader = MdPsiImplUtil.findNestedChildByType(element, MdTypes.TABLE_HEADER);
                        if (tableHeader != null) {
                            problems.add(manager.createProblemDescriptor(element, MdBundle.message("annotation.table.header-rows"), true, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly, problems.fixes()));
                        }
                    }

                    int minColumns = Integer.MAX_VALUE;
                    int maxColumns = 0;
                    boolean hadColumnSpans = false;
                    StringBuilder tableText = new StringBuilder();
                    CharSequence sep = "";

                    for (PsiElement section : element.getChildren()) {
                        for (PsiElement row : section.getNode().getElementType() == MdTypes.TABLE_SEPARATOR ? new PsiElement[] { section } : section.getChildren()) {
                            int columns = 0;
                            tableText.append(sep);
                            sep = "\n" + childContPrefix.toString();

                            for (PsiElement part : row.getChildren()) {
                                if (part instanceof MdComposite && part.getNode().getElementType() == MdTypes.TABLE_CELL) {
                                    PsiElement leadSeparators = part.getPrevSibling();

                                    if (columns == 0 && leadSeparators != null) {
                                        tableText.append(leadSeparators.getText());
                                    }

                                    tableText.append(part.getText());

                                    PsiElement separators = part.getNextSibling();
                                    int colSpan = Utils.minLimit(1, separators == null ? 1 : separators.getTextLength());
                                    if (separators != null && colSpan > 1) {
                                        hadColumnSpans = true;
                                        final StringBuilder sb = new StringBuilder();
                                        String sepCell = "";
                                        for (int i = 0; i < colSpan; i++) {
                                            sb.append(sepCell);
                                            sepCell = " ";
                                            sb.append('|');
                                        }
                                        tableText.append(sb);
                                    } else if (separators != null) {
                                        tableText.append(separators.getText().trim());
                                    }

                                    columns += colSpan;
                                }
                            }

                            if (minColumns > columns) minColumns = columns;
                            if (maxColumns < columns) maxColumns = columns;
                        }
                    }

                    int startOffset = element.getTextOffset();
                    int endOffset = startOffset + element.getTextLength();

                    if (minColumns != maxColumns) {
                        LineAppendable formattedTable = editContext.getLineAppendable();
                        table.appendTable(formattedTable);
                        MdPsiImplUtil.addLinePrefix(formattedTable, "", childContPrefix);
                        problems.add(new ReplaceTextChangeQuickFix(MdBundle.message("quickfix.reformat-table"), startOffset, endOffset, formattedTable.toString()));
                        problems.add(manager.createProblemDescriptor(element, MdBundle.message("annotation.table.inconsistent-columns"), true, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly, problems.fixes()));
                    }

                    PsiElement caption = MdPsiImplUtil.findNestedChildByType(element, MdTypes.TABLE_CAPTION);
                    if (caption != null) {
                        BasedSequence charSequence = editContext.getCharSequence();
                        int capStartOffset = charSequence.startOfLine(caption.getTextOffset());
                        int capEndOffset = charSequence.endOfLineAnyEOL(capStartOffset);
                        capEndOffset += charSequence.eolStartLength(capEndOffset);

                        problems.add(new ReplaceTextChangeQuickFix(MdBundle.message("annotation.table.caption-support.delete-caption"), capStartOffset, capEndOffset, ""));
                        problems.add(manager.createProblemDescriptor(caption, MdBundle.message("annotation.table.caption-support"), true, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly, problems.fixes()));

                        // make end offset for table the previous end of line
                        endOffset = Math.max(0, capStartOffset - charSequence.eolEndLength(capStartOffset));
                    }

                    if (hadColumnSpans) {
                        // need to add spaces between |
                        problems.add(new ReplaceTextChangeQuickFix(MdBundle.message("annotation.table.column-span.insert-space"), startOffset, endOffset, tableText.toString()));
                        problems.add(manager.createProblemDescriptor(element, MdBundle.message("annotation.table.column-span"), true, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly, problems.fixes()));
                    }
                }
            }
        }, MdTable.class);

        return problems.done();
    }
}
