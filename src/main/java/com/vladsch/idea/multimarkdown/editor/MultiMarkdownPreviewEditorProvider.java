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
package com.vladsch.idea.multimarkdown.editor;

import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.lang.Language;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileTypeFactory;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class MultiMarkdownPreviewEditorProvider implements FileEditorProvider, PossiblyDumbAware {

    public static final String EDITOR_TYPE_ID = MultiMarkdownLanguage.NAME + "PreviewEditor";

    public static boolean accept(@NotNull VirtualFile file) {
        String name = file.getName();
        String fileExt = file.getExtension();
        FileType fileType = file.getFileType();
        boolean doAccept = fileType instanceof MultiMarkdownFileType;

        if (!doAccept) {
            // Issue: #14 scratch files have to be matched differently
            ScratchFileService fileService = ScratchFileService.getInstance();
            Language language = fileService.getScratchesMapping().getMapping(file);
            doAccept = language instanceof MultiMarkdownLanguage;
        }

        if (!doAccept && fileExt != null) {
            for (String ext : MultiMarkdownFileTypeFactory.getExtensions()) {
                if (ext.equals(fileExt)) {
                    doAccept = true;
                    break;
                }
            }
        }
        return doAccept;
    }

    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return accept(file);
    }

    @NotNull
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new MultiMarkdownPreviewEditor(project, FileDocumentManager.getInstance().getDocument(file), false);
    }

    public void disposeEditor(@NotNull FileEditor editor) {
        editor.dispose();
    }

    /**
     * Deserialize state from the specified <code>sourceElemet</code>.
     * <p/>
     * Does not do anything as {@link MultiMarkdownPreviewEditor} is stateless.
     *
     * @param sourceElement the source element.
     * @param project       the project.
     * @param file          the file.
     *
     * @return {@link FileEditorState#INSTANCE}
     *
     * @see #writeState(com.intellij.openapi.fileEditor.FileEditorState, com.intellij.openapi.project.Project, org.jdom.Element)
     */
    @NotNull
    public FileEditorState readState(@NotNull Element sourceElement, @NotNull Project project, @NotNull VirtualFile file) {
        return FileEditorState.INSTANCE;
    }

    /**
     * Serialize state into the specified <code>targetElement</code>
     * <p/>
     * Does not do anything as {@link MultiMarkdownPreviewEditor} is stateless.
     *
     * @param state         the state to serialize.
     * @param project       the project.
     * @param targetElement the target element to serialize to.
     *
     * @see #readState(org.jdom.Element, com.intellij.openapi.project.Project, com.intellij.openapi.vfs.VirtualFile)
     */
    public void writeState(@NotNull FileEditorState state, @NotNull Project project, @NotNull Element targetElement) {
    }

    @NotNull
    public String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @NotNull
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
