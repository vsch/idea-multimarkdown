// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.LocalTimeCounter;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.settings.RenderingProfileManager;
import org.jetbrains.annotations.NotNull;

public class MdFactoryRenderingProfile {
    final private @NotNull Project myProject;
    final private @NotNull MdRenderingProfile myRenderingProfile;

    public MdFactoryRenderingProfile(@NotNull final Project project, @NotNull final MdRenderingProfile renderingProfile) {
        myProject = project;
        myRenderingProfile = renderingProfile;
        renderingProfile.setProject(project);
    }

    @NotNull
    public PsiFile createFileFromText(@NotNull String name, @NotNull CharSequence text) {
        RenderingProfileManager profileManager = MdRenderingProfileManager.getInstance(myProject);
        LightVirtualFile virtualFile = new LightVirtualFile(name, MdFileType.INSTANCE, text, LocalTimeCounter.currentTime());

        try {
            profileManager.registerRenderingProfile(virtualFile, myRenderingProfile);

            PsiFile newFile = PsiFileFactory.getInstance(myProject).createFileFromText(
                    name,
                    MdLanguage.INSTANCE,
                    text,
                    false,
                    true,
                    false,
                    virtualFile
            );

            if (newFile != null) {
                profileManager.registerRenderingProfile(newFile, myRenderingProfile);
                return newFile;
            }

            return PsiFileFactory.getInstance(myProject).createFileFromText(name, MdFileType.INSTANCE, text);
        } finally {
            profileManager.unregisterRenderingProfile(virtualFile);
        }
    }

    public void unregisterRenderingProfile(PsiFile file) {
        RenderingProfileManager profileManager = MdRenderingProfileManager.getInstance(myProject);
        profileManager.unregisterRenderingProfile(file);
    }

    @NotNull
    public MdRenderingProfile getRenderingProfile() {
        return myRenderingProfile;
    }
}
