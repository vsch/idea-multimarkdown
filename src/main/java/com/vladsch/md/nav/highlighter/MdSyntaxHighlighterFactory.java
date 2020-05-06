// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.highlighter;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @NotNull
    @Override
    public SyntaxHighlighter getSyntaxHighlighter(@Nullable final Project project, @Nullable final VirtualFile virtualFile) {
        MdRenderingProfile renderingProfile;
        if (project == null) renderingProfile = MdRenderingProfile.getDEFAULT();
        else if (virtualFile == null) renderingProfile = MdRenderingProfileManager.getProfile(project);
        else renderingProfile = MdRenderingProfileManager.getProfile(project, virtualFile);
        return new MdSyntaxHighlighter(renderingProfile, false, false);
    }
}
