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
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.util.*;
import org.jetbrains.annotations.NotNull;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;

class RenameFileAndReTargetQuickFix extends BaseIntentionAction {
    private String newFileName;
    private PsiFile targetFile;
    private MultiMarkdownNamedElement linkRefElement;
    private int renameFlags;

    RenameFileAndReTargetQuickFix(PsiFile targetFile, String newFileName, MultiMarkdownNamedElement linkRefElement) {
        this(targetFile, newFileName, linkRefElement, RENAME_KEEP_TEXT | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_ANCHOR | RENAME_KEEP_TITLE);
    }

    RenameFileAndReTargetQuickFix(PsiFile targetFile, String newFileName, MultiMarkdownNamedElement linkRefElement, int renameFlags) {
        this.newFileName = newFileName;
        this.targetFile = targetFile;
        this.linkRefElement = linkRefElement;
        this.renameFlags = renameFlags;
    }

    @NotNull
    @Override
    public String getText() {
        PathInfo filePathInfo = new PathInfo(targetFile.getVirtualFile().getPath());
        return MultiMarkdownBundle.message("quickfix.wikilink.rename-page-retarget", filePathInfo.getFileName(), newFileName);
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
                renameFileAndReTarget(project, targetFile, newFileName, renameFlags);
            }
        });
    }

    private static void renameFileAndReTarget(final Project project, final PsiFile targetFile, final String newFileName, final int renameFlags) {
        final MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        if (projectComponent != null) {
            new WriteCommandAction.Simple(project) {
                @Override
                public void run() {
                    projectComponent.pushRefactoringRenameFlags(renameFlags);

                    try {
                        final JavaRefactoringFactory factory = JavaRefactoringFactory.getInstance(project);

                        // RELEASE: make sure the change of not searching comments works
                        JavaRenameRefactoring rename = (JavaRenameRefactoring) factory.createRename(targetFile, newFileName, false, true);
                        UsageInfo[] usages = rename.findUsages();
                        rename.doRefactoring(usages);
                    } finally {
                        projectComponent.popRefactoringRenameFlags();
                    }
                }
            }.execute();
        }
    }
}
