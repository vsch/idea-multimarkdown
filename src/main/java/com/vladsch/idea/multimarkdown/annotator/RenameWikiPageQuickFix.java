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
import org.jetbrains.annotations.NotNull;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;

class RenameWikiPageQuickFix extends BaseIntentionAction {
    public static final int RENAME_CONFLICTING_TARGET = 1;

    private String displayName;
    private String newName;
    private PsiFile targetFile;
    private final int alternativeMsg;

    RenameWikiPageQuickFix(PsiFile targetFile, String displayName, String newName) {
        this(targetFile, displayName, newName, 0);
    }

    RenameWikiPageQuickFix(PsiFile targetFile, String displayName, String newName, int alternativeMsg) {
        this.displayName = displayName != null ? displayName : targetFile.getVirtualFile().getName();
        this.newName = newName;
        this.targetFile = targetFile;
        this.alternativeMsg = alternativeMsg;
    }

    @NotNull
    @Override
    public String getText() {
        String msg;

        switch (alternativeMsg) {
            case RENAME_CONFLICTING_TARGET:
                msg = MultiMarkdownBundle.message("quickfix.wikilink.rename-conflicting-page", displayName, newName);
                break;

            default:
                msg = MultiMarkdownBundle.message("quickfix.wikilink.rename-page", displayName, newName);
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
                renameWikiFile(project, targetFile, newName);
            }
        });
    }

    private void renameWikiFile(final Project project, final PsiFile psiFile, final String fileName) {
        final MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        if (projectComponent != null) {
            new WriteCommandAction.Simple(project) {
                @Override
                public void run() {
                    JavaRefactoringFactory factory = JavaRefactoringFactory.getInstance(project);
                    JavaRenameRefactoring rename = factory.createRename(psiFile, fileName);
                    UsageInfo[] usages = rename.findUsages();
                    try {
                        projectComponent.pushRefactoringRenameFlags(alternativeMsg == RENAME_CONFLICTING_TARGET ? RENAME_KEEP_ANCHOR | RENAME_KEEP_PATH | RENAME_KEEP_TEXT : REASON_FILE_RENAMED);
                        rename.doRefactoring(usages); // modified 'usages' array
                    } finally {
                        projectComponent.popRefactoringRenameFlags();
                    }
                }
            }.execute();
        }
    }
}
