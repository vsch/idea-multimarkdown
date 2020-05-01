// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.util.registry.RegistryValue;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.EditorTextField;
import com.intellij.util.LocalTimeCounter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.FocusEvent;
import java.util.EventListener;

public class CustomizableEditorTextField extends EditorTextField {
    private final FileType myFileType;

    // Could be null to allow usage in UI designer, as EditorTextField permits
    private @Nullable Project myProject;

    public interface EditorCustomizationListener extends EventListener {
        // return true to create highlighter for editor, false if highlighter initialization handled by listener
        boolean editorCreated(@NotNull final EditorEx editor, @Nullable final Project project);

        @Nullable
        EditorHighlighter getHighlighter(Project project, @NotNull FileType fileType, @NotNull EditorColorsScheme settings);
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

    public CustomizableEditorTextField(
            @Nullable FileType fileType,
            @Nullable Project project,
            @NotNull String value,
            @NotNull DocumentCreator documentCreator
    ) {
        this(fileType, project, value, documentCreator, true);
    }

    public CustomizableEditorTextField(
            @Nullable FileType fileType,
            @Nullable Project project,
            @NotNull String value,
            @NotNull DocumentCreator documentCreator,
            boolean oneLineMode
    ) {
        super(documentCreator.createDocument(value, (fileType == null ? StdFileTypes.PLAIN_TEXT : fileType), project), project, (fileType == null ? StdFileTypes.PLAIN_TEXT : fileType), fileType == null, oneLineMode);

        myFileType = (fileType == null ? StdFileTypes.PLAIN_TEXT : fileType);
        myProject = project;

        setEnabled(fileType != null);
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

    public static Document createDocument(
            String value, @NotNull FileType fileType, Project project,
            @NotNull SimpleDocumentCreator documentCreator
    ) {
        if (project != null) {
            final PsiFileFactory factory = PsiFileFactory.getInstance(project);

            final long stamp = LocalTimeCounter.currentTime();
            final PsiFile psiFile = factory.createFileFromText("Dummy." + fileType.getDefaultExtension(), fileType, value, stamp, true, true);
            documentCreator.customizePsiFile(psiFile);
            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            if (document == null) {
                document = new DocumentImpl(psiFile.getText());
            }
            return document;
        } else {
            EditorFactory editorFactory = EditorFactory.getInstance();
            return editorFactory.createDocument("");
        }
    }

    public void registerListener(EditorCustomizationListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeNotify() {
        listener = null;
        myProject = null;
        super.removeNotify();
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }

    @Override
    protected EditorEx createEditor() {
        final Editor oldEditor = super.getEditor();

        final EditorEx editor = super.createEditor();

        if (listener == null || listener.editorCreated(editor, myProject)) {
            EditorSettings settings = editor.getSettings();
            settings.setRightMarginShown(true);
            settings.setLineNumbersShown(true);
            settings.setIndentGuidesShown(true);
            settings.setVirtualSpace(true);

            //settings.setWheelFontChangeEnabled(false);
            editor.setHorizontalScrollbarVisible(true);
            editor.setVerticalScrollbarVisible(true);

            int lineCursorWidth = 2;
            // get the standard caret width from the registry
            try {
                RegistryValue value = Registry.get("editor.caret.width");
                if (value != null) {
                    lineCursorWidth = value.asInteger();
                }
            } catch (NoClassDefFoundError ignored) {
            } catch (Exception ignored) {
                // ignore
            }

            settings.setLineCursorWidth(lineCursorWidth);

            EditorHighlighter editorHighlighter = listener == null ? null : listener.getHighlighter(myProject, myFileType, EditorColorsManager.getInstance().getGlobalScheme());

            if (editorHighlighter == null) {
                if (myProject != null) {
                    editorHighlighter = HighlighterFactory.createHighlighter(myProject, myFileType);
                } else {
                    editorHighlighter = HighlighterFactory.createHighlighter(myFileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
                }
            }
            editor.setHighlighter(editorHighlighter);
        }
        editor.setEmbeddedIntoDialogWrapper(true);
        return editor;
    }

    public boolean isPendingTextUpdate() {
        return pendingTextUpdate != 0;
    }

    protected int pendingTextUpdate = 0;

    @Override
    public void setText(@Nullable final String text) {
        final Application application = ApplicationManager.getApplication();
        if (application.isDispatchThread()) {
            setRawText(text);
        } else {
            pendingTextUpdate++;
            application.invokeLater(() -> {
                setRawText(text);
                pendingTextUpdate--;
            }, application.getCurrentModalityState());
        }
    }

    private void setRawText(@Nullable final String text) {
        final CommandProcessor processor = CommandProcessor.getInstance();
        final Document myDocument = getDocument();
        processor.executeCommand(getProject(), () -> {
            processor.runUndoTransparentAction(() -> {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    myDocument.replaceString(0, myDocument.getTextLength(), StringUtil.notNullize(text));
                    Editor myEditor = getEditor();
                    if (myEditor != null) {
                        final CaretModel caretModel = myEditor.getCaretModel();
                        if (caretModel.getOffset() >= myDocument.getTextLength()) {
                            caretModel.moveToOffset(myDocument.getTextLength());
                        }
                    }
                });
            });
        }, null, null, UndoConfirmationPolicy.DEFAULT, myDocument);
    }

    @Override
    public void focusLost(FocusEvent e) {

        super.focusLost(e);
    }
}

