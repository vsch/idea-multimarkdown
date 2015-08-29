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
 *
 * This class is based on LanguageTextField
 *
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
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.FocusEvent;
import java.util.EventListener;

public class CustomizableEditorTextField extends EditorTextField implements ComponentState {

    private final static String VERTICALSCROLLPOSITION = "verticalScrollPosition";
    private final static String SELECTIONSTARTOFFSET = "selectionStartOffset";
    private final static String SELECTIONENDOFFSET = "selectionEndOffset";
    private final static String CARETOFFSET = "caretOffset";

    private final FileType myFileType;
    //private final List<FocusListener> myFocusListeners = ContainerUtil.createLockFreeCopyOnWriteList();

    // Could be null to allow usage in UI designer, as EditorTextField permits
    private final @NotNull Project myProject;

    protected final SettingHandlers<EditorEx> handlers = new SettingHandlers<EditorEx>(null);

    SettingHandlers.GroupHandler verticalScrollPosition = handlers.newHandler(new SettingHandler<EditorEx, Integer>() {
        @Override public void setModelValue(@NotNull EditorEx model, Integer value) {
            assert isSettingValid(model, value);
            model.getScrollingModel().scrollVertically(value - model.getScrollingModel().getVerticalScrollOffset());
        }

        @Override public boolean isSettingValid(@NotNull EditorEx model, Integer value) {
            return value >= 0;
        }

        @Override public Integer getModelValue(@NotNull EditorEx model) {
            return model.getScrollingModel().getVerticalScrollOffset();
        }
    }, handlers.IntegerSetting(-1, VERTICALSCROLLPOSITION));

    SettingHandlers.GroupHandler caretOffset = handlers.newHandler(new SettingHandler<EditorEx, Integer>() {
        @Override public void setModelValue(@NotNull EditorEx model, Integer value) {
            assert isSettingValid(model, value);
            model.getCaretModel().getPrimaryCaret().moveToOffset(value);
        }

        @Override public boolean isSettingValid(@NotNull EditorEx model, Integer value) {
            return value <= model.getDocument().getTextLength();
        }

        @Override public Integer getModelValue(@NotNull EditorEx model) {
            return model.getCaretModel().getPrimaryCaret().getOffset();
        }
    }, handlers.IntegerSetting(0, CARETOFFSET));

    SettingHandlers.GroupHandler selectionOffset = handlers.newGroupHandler(new SettingGroupHandler<EditorEx>() {
        @Override public void loadModelValue(@NotNull EditorEx model, Settings.Setting[] settings) {
            assert isSettingValid(model, settings);
            model.getCaretModel().getPrimaryCaret().setSelection(((Settings.IntegerSetting) settings[0]).getValue(), ((Settings.IntegerSetting) settings[1]).getValue());
        }

        @Override public void saveModelValue(@NotNull EditorEx model, Settings.Setting[] settings) {
            ((Settings.IntegerSetting) settings[0]).setValue(model.getCaretModel().getPrimaryCaret().getSelectionStart());
            ((Settings.IntegerSetting) settings[1]).setValue(model.getCaretModel().getPrimaryCaret().getSelectionEnd());
        }

        @Override public boolean isSettingValid(@NotNull EditorEx model, Settings.Setting[] settings) {
            int textLength = model.getDocument().getTextLength();
            int startOffset = ((Settings.IntegerSetting) settings[0]).getValue();
            int endOffset = ((Settings.IntegerSetting) settings[1]).getValue();
            return startOffset >= 0 && endOffset >= 0 && startOffset < textLength && endOffset <= textLength;
        }

        // this returns individual values of the settings in string form
        @Override public @Nullable String getModelValue(@NotNull EditorEx model, int index) {
            switch (index) {
            case 0:
                return String.valueOf(model.getCaretModel().getPrimaryCaret().getSelectionStart());
            case 1:
                return String.valueOf(model.getCaretModel().getPrimaryCaret().getSelectionEnd());
            default:
                return null;
            }
        }
    }, handlers.IntegerSetting(-1, SELECTIONSTARTOFFSET), handlers.IntegerSetting(-1, SELECTIONENDOFFSET));

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

        setEnabled(fileType != null);
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

        handlers.loadState(ex);
        return ex;
    }

    @Override public Element getState(String elementName) {
        return handlers.getState((EditorEx) getEditor(), elementName, this);
    }

    public void loadState(@NotNull Element element) {
        handlers.loadState((EditorEx) getEditor(), element);
    }

    public boolean isChanged(@NotNull Element element) {
        return handlers.isChanged(element, this);
    }

    @Override public boolean haveSavedState() {
        return getEditor() != null && selectionOffset.isSettingValid((EditorEx) getEditor());
    }

    public boolean haveSavedState(@NotNull EditorEx ex) {
        return selectionOffset.isSettingValid(ex);
    }

    @Override
    public @Nullable Object getComponent(@NotNull String persistName) {
        return handlers.getComponentValue((EditorEx) getEditor(), persistName);
    }

    @Override
    public void focusLost(FocusEvent e) {

        super.focusLost(e);
    }
}

