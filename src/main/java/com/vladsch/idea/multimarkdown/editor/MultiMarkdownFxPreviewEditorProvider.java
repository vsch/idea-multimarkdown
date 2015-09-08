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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.editor;

//import com.intellij.ide.scratch.ScratchFileService;

import com.intellij.ide.plugins.cl.PluginClassLoader;
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
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MultiMarkdownFxPreviewEditorProvider implements FileEditorProvider, PossiblyDumbAware {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MultiMarkdownFxPreviewEditorProvider.class);

    private static final int FX_PREVIEW_UNKNOWN = 0;
    private static final int FX_PREVIEW_HAVE_JAR = 1;
    private static final int FX_PREVIEW_CAN_LOAD = 2;
    private static final int FX_PREVIEW_CANNOT_LOAD = 3;

    public static final String EDITOR_TYPE_ID = MultiMarkdownLanguage.NAME + "FxPreviewEditor";
    private static int canLoadFxEditor = FX_PREVIEW_UNKNOWN;
    private static Class<?> MultiMarkdownFxPreviewEditor;
    private static Constructor<?> classConstructor;

    public static boolean accept(@NotNull VirtualFile file) {
        String fileExt = file.getExtension();
        FileType fileType = file.getFileType();
        boolean doAccept = fileType instanceof MultiMarkdownFileType;

        if (!doAccept) {
            try {
                // Issue: #14 scratch files have to be matched differently
                //ScratchFileService fileService = ScratchFileService.classConstructor();
                //PerFileMappings<Language> scratchesMapping = fileService.getScratchesMapping();
                //Language language = scratchesMapping.getMapping(file);
                //doAccept = language instanceof MultiMarkdownLanguage;

                // Issue: #15 class not found ScratchFileService, so we take care of it through reflection
                Class<?> ScratchFileService = Class.forName("com.intellij.ide.scratch.ScratchFileService");
                Method getInstance = ScratchFileService.getMethod("classConstructor");
                Method getScratchesMapping = ScratchFileService.getMethod("getScratchesMapping");
                Object fileService = getInstance.invoke(ScratchFileService);
                PerFileMappings<Language> mappings = (PerFileMappings<Language>) getScratchesMapping.invoke(fileService);
                Language language = mappings.getMapping(file);
                doAccept = language instanceof MultiMarkdownLanguage;
            } catch (Exception ex) {
                // no such beast
            }
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
        return createEditor(project, file, false);
    }

    @NotNull
    public static FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file, boolean forRawHtml) {
        if (!MultiMarkdownGlobalSettings.getInstance().useOldPreview.getValue()) {
            if (canLoadFxEditor == FX_PREVIEW_UNKNOWN) {
                try {
                    MultiMarkdownPlugin plugin = MultiMarkdownPlugin.getInstance(project);
                    PluginClassLoader pluginClassLoader = (PluginClassLoader) plugin.getClassLoader();
                    MultiMarkdownFxPreviewEditor = Class.forName("com.vladsch.idea.multimarkdown.editor.MultiMarkdownFxPreviewEditor", true, pluginClassLoader);
                    classConstructor = MultiMarkdownFxPreviewEditor.getConstructor(Project.class, Document.class, boolean.class);
                    canLoadFxEditor = FX_PREVIEW_HAVE_JAR;
                } catch (ClassNotFoundException e) {
                    logger.error("ClaClassNotFoundException", e);
                } catch (NoSuchMethodException e) {
                    logger.error("NoSuchMethodException", e);
                }
            }

            if (canLoadFxEditor == FX_PREVIEW_HAVE_JAR || canLoadFxEditor == FX_PREVIEW_CAN_LOAD) {
                try {
                    Object fileEditor = classConstructor.newInstance(project, FileDocumentManager.getInstance().getDocument(file), forRawHtml);
                    if (canLoadFxEditor == FX_PREVIEW_HAVE_JAR) {
                        MultiMarkdownGlobalSettings.setIsFxHtmlPreview(true);
                        canLoadFxEditor = FX_PREVIEW_CAN_LOAD;
                    }
                    return (FileEditor) fileEditor;
                } catch (InvocationTargetException e) {
                    logger.error("InvocationTargetException", e.getTargetException());
                } catch (IllegalAccessException e) {
                    logger.error("IllegalAccessException", e);
                } catch (InstantiationException e) {
                    logger.error("InstantiationException", e);
                }
            }
        }

        // TODO: show notification of the problem and solutions
        if (canLoadFxEditor != FX_PREVIEW_CANNOT_LOAD) {
            canLoadFxEditor = FX_PREVIEW_CANNOT_LOAD;
            MultiMarkdownGlobalSettings.setIsFxHtmlPreview(false);
        }
        return new MultiMarkdownPreviewEditor(project, FileDocumentManager.getInstance().getDocument(file), forRawHtml);
    }

    public void disposeEditor(@NotNull FileEditor editor) {
        editor.dispose();
    }

    @NotNull
    public FileEditorState readState(@NotNull Element sourceElement, @NotNull Project project, @NotNull VirtualFile file) {
        return FileEditorState.INSTANCE;
    }

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
