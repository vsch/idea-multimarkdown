// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.highlighter;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.EditorHighlighterProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.settings.SyntaxHighlightingType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdEditorHighlighterProvider implements EditorHighlighterProvider {
    final static Logger LOG_EDITOR = Logger.getInstance("com.vladsch.md.nav.project.editors");

    @Override
    public EditorHighlighter getEditorHighlighter(@Nullable Project project, @NotNull FileType fileType, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors) {
        boolean forAnnotator = false;
        MdRenderingProfile renderingProfile;

        if (virtualFile == null || project == null) {
            renderingProfile = project == null ? MdRenderingProfile.getDEFAULT() : MdRenderingProfileManager.getProfile(project);
        } else {
            renderingProfile = MdRenderingProfileManager.getProfile(project, virtualFile);
            forAnnotator = MdApplicationSettings.getInstance().getDocumentSettings().getSyntaxHighlighting() == SyntaxHighlightingType.ANNOTATOR.intValue && virtualFile.isWritable();
        }

        SyntaxHighlighter mdSyntaxHighlighter = new MdSyntaxHighlighter(renderingProfile, false, forAnnotator);
        LexerEditorHighlighter highlighter = new LexerEditorHighlighter(mdSyntaxHighlighter, colors);

        if (LOG_EDITOR.isDebugEnabled()) LOG_EDITOR.debug(String.format("MdEditorHighlightProvider profile: %s, %s for writeable:%s, %s", renderingProfile.getProfileName(), mdSyntaxHighlighter.getHighlightingLexer().getClass().getSimpleName(), virtualFile != null && virtualFile.isWritable(), virtualFile));
        return highlighter;
    }
}
