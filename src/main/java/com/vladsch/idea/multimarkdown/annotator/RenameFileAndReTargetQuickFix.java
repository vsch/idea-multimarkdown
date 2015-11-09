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
import com.vladsch.idea.multimarkdown.util.FileReference;
import com.vladsch.idea.multimarkdown.util.FileReferenceLinkGitHubRules;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownNamedElement.*;

class RenameFileAndReTargetQuickFix extends BaseIntentionAction {
    private String name;
    private PsiFile targetFile;
    private MultiMarkdownNamedElement linkRefElement;
    private int renameFlags;

    RenameFileAndReTargetQuickFix(PsiFile targetFile, String newName, MultiMarkdownNamedElement linkRefElement) {
        this(targetFile, newName, linkRefElement, RENAME_KEEP_TEXT | RENAME_KEEP_RENAMED_TEXT | RENAME_KEEP_ANCHOR | RENAME_KEEP_TITLE);
    }

    RenameFileAndReTargetQuickFix(PsiFile targetFile, String newName, MultiMarkdownNamedElement linkRefElement, int renameFlags) {
        this.name = newName;
        this.targetFile = targetFile;
        this.linkRefElement = linkRefElement;
        this.renameFlags = renameFlags;
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
                renameFile(project, targetFile, name, linkRefElement);
            }
        });
    }

    private void renameFile(final Project project, final PsiFile psiFile, final String fileName, final MultiMarkdownNamedElement linkRefElement) {
        final MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        if (projectComponent != null) {
            new WriteCommandAction.Simple(project) {
                @Override
                public void run() {
                    projectComponent.pushRefactoringRenameFlags(renameFlags);

                    try {
                        final JavaRefactoringFactory factory = JavaRefactoringFactory.getInstance(project);
                        JavaRenameRefactoring rename = (JavaRenameRefactoring) factory.createRename(psiFile, fileName, true, true);

                        UsageInfo[] usages = rename.findUsages();

                        // now get list of links to retarget, get the root element for all of these
                        PsiReference reference = linkRefElement.getReference();
                        MultiMarkdownNamedElement rootElement;
                        FilePathInfo newFileInfo = new FilePathInfo(fileName);
                        HashSet<UsageInfo> realUsages = null;
                        JavaRenameRefactoring renameLinkRef = null;
                        UsageInfo[] linkRefUsages = null;
                        boolean withAnchor = (renameFlags & RENAME_KEEP_ANCHOR) == 0;
                        boolean withExt = false;

                        if (reference != null && (rootElement = (MultiMarkdownNamedElement) reference.resolve()) != null) {
                            String linkRename = newFileInfo.getFileName();
                            String oldLinkName = rootElement.getName();
                            if (linkRefElement instanceof MultiMarkdownWikiPageRef) {
                                linkRename = newFileInfo.getFileNameAsWikiRef();
                            }

                            renameLinkRef = (JavaRenameRefactoring) factory.createRename(rootElement, linkRename, true, true);
                            linkRefUsages = renameLinkRef.findUsages();
                            realUsages = new HashSet<UsageInfo>(linkRefUsages.length);
                            String gitHubRepoPath = new FileReference(linkRefElement.getContainingFile()).getGitHubRepoPath();

                            // see if all the usages will resolve to this file if not then leave them out
                            if (linkRefElement instanceof MultiMarkdownWikiPageRef) {
                                for (UsageInfo usageInfo : linkRefUsages) {
                                    PsiFile sourceFile = usageInfo.getFile();
                                    if (sourceFile != null) {
                                        FileReferenceLinkGitHubRules fileReferenceLink = new FileReferenceLinkGitHubRules(sourceFile, psiFile);
                                        String pageRef = withAnchor ? fileReferenceLink.getWikiPageRefWithAnchor() : fileReferenceLink.getWikiPageRef();
                                        if (fileReferenceLink.getSourceReference().isWikiPage() && fileReferenceLink.isWikiAccessible() && FilePathInfo.equivalentWikiRef(true, true, pageRef, oldLinkName)) {
                                            // this one's a keeper
                                            realUsages.add(usageInfo);
                                        }
                                    }
                                }
                            }
                        }

                        // now do refactoring of links
                        if (realUsages != null && realUsages.size() > 0) {
                            renameLinkRef.doRefactoring(realUsages.size() == linkRefUsages.length ? linkRefUsages : realUsages.toArray(new UsageInfo[realUsages.size()]));
                        }

                        // then the file
                        rename.doRefactoring(usages);
                    } finally {
                        projectComponent.popRefactoringRenameFlags();
                    }
                }
            }.execute();
        }
    }
}
