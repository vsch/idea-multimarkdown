// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor;

import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.WeighedFileEditorProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.MdLanguage;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class MdPreviewFileEditorProvider extends WeighedFileEditorProvider {
    public static final String EDITOR_TYPE_ID = MdLanguage.NAME + "PreviewEditor";

    public static boolean acceptFile(final Project project, @NotNull final VirtualFile file) {
        if (file instanceof LightVirtualFile) {
            if (((LightVirtualFile) file).getAssignedFileType() instanceof MdFileType) return true;
        }

        FileType fileType = file.getFileType();
        if (fileType instanceof MdFileType) return true;
//        return fileType == ScratchFileType.INSTANCE && LanguageUtil.getLanguageForPsi(project, file) == MdLanguage.INSTANCE;
        Language language = ScratchFileService.getInstance().getScratchesMapping().getMapping(file);
        boolean scratchMd = language == MdLanguage.INSTANCE;

        // NOTE: scratch file mapping is only present using New Scratch File action and is not properly set if using New File action on Scratches directory
        boolean oldScratchMd = scratchMd || LanguageUtil.getLanguageForPsi(project, file) == MdLanguage.INSTANCE;
        return oldScratchMd;
    }

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return acceptFile(project, file);
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new MdPreviewFileEditor(project, file);
    }

    @Override
    public void disposeEditor(@NotNull FileEditor editor) {
        Disposer.dispose(editor);
    }

    @NotNull
    @Override
    public FileEditorState readState(
            @NotNull Element sourceElement,
            @NotNull Project project,
            @NotNull VirtualFile file
    ) {
        return new PreviewEditorState(sourceElement);
    }

    @Override
    public void writeState(
            @NotNull FileEditorState _state,
            @NotNull Project project,
            @NotNull Element element
    ) {
        PreviewEditorState state = (PreviewEditorState) _state;

        if (state.getStateElement() != null) {
            Element clone = state.getStateElement().clone();
            element.addContent(clone);
        }
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
