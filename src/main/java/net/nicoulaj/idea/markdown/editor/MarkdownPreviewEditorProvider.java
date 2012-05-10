/*
 * Copyright (c) 2011-2012 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.editor;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import net.nicoulaj.idea.markdown.MarkdownLanguage;
import net.nicoulaj.idea.markdown.file.MarkdownFileType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * {@link FileEditorProvider} implementation to provide {@link MarkdownPreviewEditor}.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @see MarkdownPreviewEditor
 * @since 0.1
 */
public class MarkdownPreviewEditorProvider implements FileEditorProvider {

    /**
     * The id of the editors provided by this {@link FileEditorProvider}.
     */
    public static final String EDITOR_TYPE_ID = MarkdownLanguage.LANGUAGE_NAME + "PreviewEditor";

    /**
     * Check wether this {@link FileEditorProvider} can create a valid {@link FileEditor} for the file.
     *
     * @param project the project context.
     * @param file    the file to be tested for acceptance. This parameter must never be <code>null</code>.
     * @return whether the provider can create a valid editor for the specified <code>file</code>.
     */
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getFileType() instanceof MarkdownFileType;
    }

    /**
     * Create a valid editor for the specified file.
     * <p/>
     * Should be called only if the provider has accepted this file.
     *
     * @param project the project context.
     * @param file    the file for which an editor must be created.
     * @return an editor for the specified file.
     * @see #accept(com.intellij.openapi.project.Project, com.intellij.openapi.vfs.VirtualFile)
     */
    @NotNull
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new MarkdownPreviewEditor(FileDocumentManager.getInstance().getDocument(file));
    }

    /**
     * Dispose the specified <code>editor</code>.
     *
     * @param editor editor to be disposed. This parameter must not be <code>null</code>.
     */
    public void disposeEditor(@NotNull FileEditor editor) {
        editor.dispose();
    }

    /**
     * Deserialize state from the specified <code>sourceElemet</code>.
     * <p/>
     * Does not do anything as {@link MarkdownPreviewEditor} is stateless.
     *
     * @param sourceElement the source element.
     * @param project       the project.
     * @param file          the file.
     * @return {@link FileEditorState#INSTANCE}
     * @see #writeState(com.intellij.openapi.fileEditor.FileEditorState, com.intellij.openapi.project.Project, org.jdom.Element)
     */
    @NotNull
    public FileEditorState readState(@NotNull Element sourceElement, @NotNull Project project, @NotNull VirtualFile file) {
        return FileEditorState.INSTANCE;
    }

    /**
     * Serialize state into the specified <code>targetElement</code>
     * <p/>
     * Does not do anything as {@link MarkdownPreviewEditor} is stateless.
     *
     * @param state         the state to serialize.
     * @param project       the project.
     * @param targetElement the target element to serialize to.
     * @see #readState(org.jdom.Element, com.intellij.openapi.project.Project, com.intellij.openapi.vfs.VirtualFile)
     */
    public void writeState(@NotNull FileEditorState state, @NotNull Project project, @NotNull Element targetElement) {
    }

    /**
     * Get the id of the editors provided by this {@link FileEditorProvider}.
     *
     * @return {@link #EDITOR_TYPE_ID}
     */
    @NotNull
    public String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    /**
     * Get the {@link FileEditorPolicy} defining how to show editors created via the {@link FileEditorProvider}.
     *
     * @return {@link FileEditorPolicy#NONE}
     */
    @NotNull
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.NONE;
    }
}
