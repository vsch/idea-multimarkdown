// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface RenderingProfileManager {
    /**
     * Used to create temporary rendering profile mappings
     *
     * @param virtualFile      virtual file for which to register custom rendering
     * @param renderingProfile custom rendering profile
     */
    void registerRenderingProfile(@NotNull VirtualFile virtualFile, @NotNull MdRenderingProfile renderingProfile);

    void unregisterRenderingProfile(@NotNull VirtualFile virtualFile);

    void registerRenderingProfile(@NotNull PsiFile psiFile, @NotNull MdRenderingProfile renderingProfile);

    void unregisterRenderingProfile(@NotNull PsiFile psiFile);

    default void forVirtualFileOf(@Nullable PsiFile psiFile, Consumer<VirtualFile> consumer) {
        if (psiFile == null || !psiFile.isValid()) return;

        if (psiFile.getVirtualFile() != null) consumer.accept(psiFile.getVirtualFile());
        else if (psiFile.getOriginalFile() != null && psiFile.getOriginalFile().getVirtualFile() != null) consumer.accept(psiFile.getOriginalFile().getVirtualFile());
        else consumer.accept(psiFile.getViewProvider().getVirtualFile());
    }

    void groupNotifications(Runnable runnable);

    void copyFrom(RenderingProfileManager other);

    @NotNull
    MdRenderingProfile getDefaultRenderingProfile();

    boolean hasAnyProfiles();

    @NotNull
    MdRenderingProfile getRenderingProfile(@Nullable VirtualFile file);

    @NotNull
    MdRenderingProfile getRenderingProfile(@Nullable PsiFile psiFile);

    @Nullable
    MdRenderingProfile getRenderingProfile(@Nullable String name);

    @NotNull
    Project getProject();

    void replaceProfile(String displayName, MdRenderingProfile renderingProfile);
}
