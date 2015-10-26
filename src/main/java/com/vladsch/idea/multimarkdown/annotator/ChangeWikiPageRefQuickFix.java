/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.JavaRefactoringFactory;
import com.intellij.refactoring.JavaRenameRefactoring;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import org.jetbrains.annotations.NotNull;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.RENAME_KEEP_NOTHING;

class ChangeWikiPageRefQuickFix extends BaseIntentionAction {
    public static final int MATCH_CASE_TO_FILE = 1;
    public static final int REMOVE_DASHES = 2;
    public static final int REMOVE_SLASHES = 3;
    public static final int REMOVE_SUBDIR = 4;

    private String newWikiPageRef;
    private MultiMarkdownWikiPageRef wikiPageRefElement;
    private final int alternativeMsg;
    private final int renameFlags;

    ChangeWikiPageRefQuickFix(MultiMarkdownWikiPageRef wikiPageRefElement, String newWikiPageRef) {
        this(wikiPageRefElement, newWikiPageRef, 0);
    }

    ChangeWikiPageRefQuickFix(MultiMarkdownWikiPageRef wikiPageRefElement, String newWikiPageRef, int alternativeMsg) {
        this(wikiPageRefElement, newWikiPageRef, alternativeMsg, RENAME_KEEP_NOTHING);
    }

    ChangeWikiPageRefQuickFix(MultiMarkdownWikiPageRef wikiPageRefElement, String newWikiPageRef, int alternativeMsg, int renameFlags) {
        this.newWikiPageRef = newWikiPageRef;
        this.wikiPageRefElement = wikiPageRefElement;
        this.alternativeMsg = alternativeMsg;
        this.renameFlags = renameFlags;
    }

    @NotNull
    @Override
    public String getText() {
        String msg;
        switch (alternativeMsg) {
            case MATCH_CASE_TO_FILE:
                msg = MultiMarkdownBundle.message("quickfix.wikilink.0.match-target", FilePathInfo.wikiRefAsFileNameWithExt(newWikiPageRef));
                break;

            case REMOVE_DASHES:
                msg = MultiMarkdownBundle.message("quickfix.wikilink.0.remove-dashes", FilePathInfo.wikiRefAsFileNameWithExt(newWikiPageRef));
                break;

            case REMOVE_SLASHES:
                msg = MultiMarkdownBundle.message("quickfix.wikilink.0.remove-slashes", FilePathInfo.wikiRefAsFileNameWithExt(newWikiPageRef));
                break;

            case REMOVE_SUBDIR:
                msg = MultiMarkdownBundle.message("quickfix.wikilink.0.remove-subdirs", FilePathInfo.wikiRefAsFileNameWithExt(newWikiPageRef));
                break;

            default:
                msg = MultiMarkdownBundle.message("quickfix.wikilink.0.change-target", FilePathInfo.wikiRefAsFileNameWithExt(newWikiPageRef));
                break;
        }

        return msg;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return MultiMarkdownBundle.message("quickfix.wikilink.family-name");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                changeWikiPageRef(project, wikiPageRefElement, newWikiPageRef);
            }
        });
    }

    private void changeWikiPageRef(final Project project, final MultiMarkdownWikiPageRef wikiPageRefElement, final String fileName) {
        final MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        if (projectComponent != null) {
            new WriteCommandAction.Simple(project) {
                @Override
                public void run() {
                    // change the whole name
                    //wikiPageRefElement.setName(fileName, MultiMarkdownNamedElement.REASON_FILE_MOVED);
                    JavaRefactoringFactory factory = JavaRefactoringFactory.getInstance(project);
                    JavaRenameRefactoring rename = factory.createRename(wikiPageRefElement, fileName);
                    UsageInfo[] usages = rename.findUsages();

                    try {
                        projectComponent.pushRefactoringRenameFlags(RENAME_KEEP_NOTHING);
                        rename.doRefactoring(usages); // modified 'usages' array
                    } finally {
                        projectComponent.popRefactoringRenameFlags();
                    }
                }
            }.execute();
        }
    }
}
