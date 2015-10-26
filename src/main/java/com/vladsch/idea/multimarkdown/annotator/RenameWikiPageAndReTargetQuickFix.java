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
import com.intellij.psi.PsiReference;
import com.intellij.refactoring.JavaRefactoringFactory;
import com.intellij.refactoring.JavaRenameRefactoring;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.util.FilePathInfo;
import com.vladsch.idea.multimarkdown.util.FileReferenceLink;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

class RenameWikiPageAndReTargetQuickFix extends BaseIntentionAction {
    private String name;
    private PsiFile targetFile;
    private MultiMarkdownWikiPageRef wikiPageRefElement;
    private String newWikiPageRef;

    RenameWikiPageAndReTargetQuickFix(PsiFile targetFile, String newName, MultiMarkdownWikiPageRef wikiPageRefElement, String newWikiPageRef) {
        this.name = newName;
        this.targetFile = targetFile;
        this.wikiPageRefElement = wikiPageRefElement;
        this.newWikiPageRef = newWikiPageRef;
    }

    @NotNull
    @Override
    public String getText() {
        FilePathInfo filePathInfo = new FilePathInfo(targetFile.getVirtualFile().getPath());
        return MultiMarkdownBundle.message("quickfix.wikilink.rename-page", filePathInfo.getFileNameWithAnchor(), name);
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
                renameWikiFile(project, targetFile, name, wikiPageRefElement, newWikiPageRef);
            }
        });
    }

    private void renameWikiFile(final Project project, final PsiFile psiFile, final String fileName, final MultiMarkdownWikiPageRef wikiPageRefElement, final String newWikiPageRef) {
        final MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        if (projectComponent != null) {
            new WriteCommandAction.Simple(project) {
                @Override
                public void run() {
                    projectComponent.pushRefactoringRenameFlags(MultiMarkdownNamedElement.RENAME_KEEP_TITLE | MultiMarkdownNamedElement.RENAME_KEEP_RENAMED_TITLE);

                    try {
                        final JavaRefactoringFactory factory = JavaRefactoringFactory.getInstance(project);
                        JavaRenameRefactoring rename = (JavaRenameRefactoring) factory.createRename(psiFile, fileName, true, true);

                        UsageInfo[] usages = rename.findUsages();
                        rename.doRefactoring(usages);

                        // now rename the links, get the root element for all of these
                        PsiReference reference = wikiPageRefElement.getReference();
                        MultiMarkdownNamedElement rootElement;

                        if (reference != null && (rootElement = (MultiMarkdownNamedElement) reference.resolve()) instanceof MultiMarkdownWikiPageRef) {
                            JavaRenameRefactoring renameWiki = (JavaRenameRefactoring) factory.createRename(rootElement, newWikiPageRef, true, true);
                            UsageInfo[] wikiUsages = renameWiki.findUsages();
                            HashSet<UsageInfo> realUsages = new HashSet<UsageInfo>(wikiUsages.length);

                            // see if all the usages will resolve to this file if not then leave them out
                            for (UsageInfo usageInfo : wikiUsages) {
                                PsiFile sourceFile = usageInfo.getFile();
                                if (sourceFile != null) {
                                    FileReferenceLink fileReferenceLink = new FileReferenceLink(sourceFile, psiFile);
                                    if (fileReferenceLink.getSourceReference().isWikiPage() && fileReferenceLink.isWikiAccessible() && fileReferenceLink.isWikiPageRef(newWikiPageRef)) {
                                        // this one's a keeper
                                        realUsages.add(usageInfo);
                                    }
                                }
                            }

                            if (realUsages.size() > 0) {
                                renameWiki.doRefactoring(realUsages.size() == wikiUsages.length ? wikiUsages : realUsages.toArray(new UsageInfo[realUsages.size()]));
                            }
                        }
                    } finally {
                        projectComponent.popRefactoringRenameFlags();
                    }
                }
            }.execute();
        }
    }
}
