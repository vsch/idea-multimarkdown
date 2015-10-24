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

//import com.intellij.ide.scratch.ScratchFileService;

import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.lang.Language;
import com.intellij.lang.PerFileMappings;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileTypeFactory;
import com.vladsch.idea.multimarkdown.MultiMarkdownLanguage;
import com.vladsch.idea.multimarkdown.settings.FailedBuildRunnable;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiMarkdownPreviewEditorProvider implements FileEditorProvider, PossiblyDumbAware {

    public static final String EDITOR_TYPE_ID = MultiMarkdownLanguage.NAME + "PreviewEditor";

    public static boolean accept(@NotNull final VirtualFile file) {
        String fileExt = file.getExtension();
        FileType fileType = file.getFileType();
        boolean doAccept = fileType instanceof MultiMarkdownFileType;

        if (!doAccept) {
            doAccept = MultiMarkdownGlobalSettings.getInstance().scratchFileServiceFailed.runBuild(new FailedBuildRunnable<Boolean>() {
                @Nullable @Override
                public Boolean runCanFail() throws Throwable {
                    // Issue: #14 scratch files have to be matched differently
                    ScratchFileService fileService = ScratchFileService.getInstance();
                    PerFileMappings<Language> scratchesMapping = fileService.getScratchesMapping();
                    Language language = scratchesMapping.getMapping(file);
                    return language instanceof MultiMarkdownLanguage;

                    //// Issue: #15 class not found ScratchFileService, so we take care of it through reflection
                    //Class<?> ScratchFileService = Class.forName("com.intellij.ide.scratch.ScratchFileService");
                    //Method getInstance = ScratchFileService.getMethod("getInstance");
                    //Method getScratchesMapping = ScratchFileService.getMethod("getScratchesMapping");
                    //Object fileService = getInstance.invoke(ScratchFileService);
                    //PerFileMappings<Language> mappings = (PerFileMappings<Language>) getScratchesMapping.invoke(fileService);
                    //Language language = mappings.getMapping(file);
                    //return language instanceof MultiMarkdownLanguage;
                }

                @Nullable @Override public Boolean run() {
                    return false;
                }
            });
        }
        return doAccept;
    }

    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return accept(file);
    }

    @NotNull
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
        assert document != null;
        return new MultiMarkdownPreviewEditor(project, document, false);
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
        return false;
    }
}
