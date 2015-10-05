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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.vladsch.idea.multimarkdown.MultiMarkdownBundle;
import com.vladsch.idea.multimarkdown.util.FileReference;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

class CreateWikiPageQuickFix extends BaseIntentionAction {
    private String name;

    CreateWikiPageQuickFix(String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String getText() {
        return MultiMarkdownBundle.message("quickfix.wikilink.create-page", name);
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
                createWikiFile(project, file, name);
            }
        });
    }

    private void createWikiFile(final Project project, final PsiFile file, final String fileName) {
        new WriteCommandAction.Simple(project) {
            @Override
            public void run() {
                //FileEditorManager.getInstance().openFile();
                FileReference thisFile = new FileReference(file);
                FileReference newFile = new FileReference(thisFile.getPath() + fileName, project);
                VirtualFile parentDir = newFile.getVirtualParent();
                if (parentDir != null) {
                    try {
                        VirtualFile quickFixFile = parentDir.createChildData(this.getClass().toString(), newFile.getFileName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }
}
