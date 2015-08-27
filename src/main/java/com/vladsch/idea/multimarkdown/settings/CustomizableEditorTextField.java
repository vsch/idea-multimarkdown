/*
 * Copyright 2006 Sascha Weinreuter
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific fileType governing permissions and
 * limitations under the License.
 */

package com.vladsch.idea.multimarkdown.settings;

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.LocalTimeCounter;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EventListener;

public class CustomizableEditorTextField extends EditorTextField {

    private final FileType myFileType;

    // Could be null to allow usage in UI designer, as EditorTextField permits
    private final @NotNull Project myProject;

    public interface EditorCustomizationListener extends EventListener {

        // return false if highlighter initialization handled for editor
        boolean editorCreated(@NotNull final EditorEx editor, @NotNull final Project project);
    }

    EditorCustomizationListener listener = null;

    public CustomizableEditorTextField() {
        this(null, null, "");
    }

    public CustomizableEditorTextField(FileType fileType, @Nullable Project project, @NotNull String value) {
        this(fileType, project, value, true);
    }

    public CustomizableEditorTextField(FileType fileType, Project project, @NotNull String value, boolean oneLineMode) {
        this(fileType, project, value, new SimpleDocumentCreator(), oneLineMode);
    }

    public CustomizableEditorTextField(@Nullable FileType fileType,
            @Nullable Project project,
            @NotNull String value,
            @NotNull DocumentCreator documentCreator) {
        this(fileType, project, value, documentCreator, true);
    }

    public CustomizableEditorTextField(@Nullable FileType fileType,
            @Nullable Project project,
            @NotNull String value,
            @NotNull DocumentCreator documentCreator,
            boolean oneLineMode) {
        super(documentCreator.createDocument(value, (fileType == null ? StdFileTypes.PLAIN_TEXT : fileType), project), project, (fileType == null ? StdFileTypes.PLAIN_TEXT : fileType), fileType == null, oneLineMode);

        myFileType = (fileType == null ? StdFileTypes.PLAIN_TEXT : fileType);
        myProject = getAnyProject(project, false);

        setEnabled(fileType != null && myProject != null);
    }

    protected static Project getAnyProject(Project project, boolean neverNull) {
        if (project == null) {
            ProjectManager projectManager = ProjectManager.getInstance();
            Project[] projects = projectManager.getOpenProjects();
            project = projects.length > 0 ? projects[0] : (neverNull ? projectManager.getDefaultProject() : null);
        }
        return project;
    }

    public interface DocumentCreator {

        Document createDocument(String value, @NotNull FileType fileType, Project project);
    }

    public static class SimpleDocumentCreator implements DocumentCreator {

        @Override
        public Document createDocument(String value, @NotNull FileType fileType, Project project) {
            return CustomizableEditorTextField.createDocument(value, fileType, project, this);
        }

        public void customizePsiFile(PsiFile file) {
        }
    }

    private static Document createDocument(String value, @NotNull FileType fileType, Project project,
            @NotNull SimpleDocumentCreator documentCreator) {
        project = getAnyProject(project, true);
        final PsiFileFactory factory = PsiFileFactory.getInstance(project);

        final long stamp = LocalTimeCounter.currentTime();
        final PsiFile psiFile = factory.createFileFromText("Dummy." + fileType.getDefaultExtension(), fileType, value, stamp, true, true);
        documentCreator.customizePsiFile(psiFile);
        final Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        assert document != null;
        return document;
    }

    public void registerListener(EditorCustomizationListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeNotify() {
        listener = null;
        super.removeNotify();
    }

    @Override
    protected EditorEx createEditor() {
        final EditorEx ex = super.createEditor();

        Project project = getAnyProject(myProject, true);
        if (listener != null && listener.editorCreated(ex, project)) {
            ex.setHighlighter(HighlighterFactory.createHighlighter(project, myFileType));
        }
        ex.setEmbeddedIntoDialogWrapper(true);

        return ex;
    }
}

