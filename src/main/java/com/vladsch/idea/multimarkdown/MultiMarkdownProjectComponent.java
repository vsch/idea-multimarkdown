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

package com.vladsch.idea.multimarkdown;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.vladsch.idea.multimarkdown.language.MultiMarkdownUtil;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiMarkdownProjectComponent implements ProjectComponent, VirtualFileListener {
    private Project project;

    public MultiMarkdownProjectComponent(Project project) {
        this.project = project;
    }

    protected void updateHighlighters() {
        List<MultiMarkdownFile> wikiFiles = MultiMarkdownUtil.findWikiFiles(project, false);
        DaemonCodeAnalyzer instance = DaemonCodeAnalyzer.getInstance(project);
        for (MultiMarkdownFile wikiFile : wikiFiles) {
            instance.restart(wikiFile);
        }
    }


    @Override public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        updateHighlighters();
    }

    @Override public void contentsChanged(@NotNull VirtualFileEvent event) {
        //updateHighlighters();
    }

    @Override public void fileCreated(@NotNull VirtualFileEvent event) {
         updateHighlighters();
    }

    @Override public void fileDeleted(@NotNull VirtualFileEvent event) {
        updateHighlighters();
    }

    @Override public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        updateHighlighters();
    }

    @Override public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        updateHighlighters();
    }

    @Override public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {

    }

    @Override public void beforeContentsChange(@NotNull VirtualFileEvent event) {

    }

    @Override public void beforeFileDeletion(@NotNull VirtualFileEvent event) {

    }

    @Override public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {

    }

    public void projectOpened() {
        VirtualFileManager.getInstance().addVirtualFileListener(this);
    }

    public void projectClosed() {
        VirtualFileManager.getInstance().removeVirtualFileListener(this);
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return this.getClass().getName();
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }
}
