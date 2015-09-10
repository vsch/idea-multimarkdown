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
package com.vladsch.idea.multimarkdown.settings;

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;

public class CustomizableLanguageEditorTextField extends EditorTextField {

    FileType myFileType;

    public CustomizableLanguageEditorTextField(Document document, Project project, FileType fileType, boolean isViewer, boolean oneLine) {
        super(document, project, fileType, isViewer, oneLine);
        myFileType = fileType;
    }

    public interface EditorCustomizationListener extends CustomizableEditorTextField.EditorCustomizationListener {
        };

    CustomizableEditorTextField.EditorCustomizationListener  listener = null;

        public void registerListener(CustomizableEditorTextField.EditorCustomizationListener listener) {
            this.listener = listener;
        }

    @Override
    protected EditorEx createEditor() {
        final EditorEx ex = super.createEditor();

        Project project = CustomizableEditorTextField.getAnyProject(super.getProject(), true);
        if (listener == null || listener.editorCreated(ex, project)) {
            ex.setHighlighter(HighlighterFactory.createHighlighter(project, myFileType));
        }
        ex.setEmbeddedIntoDialogWrapper(true);
        return ex;
    }
}
