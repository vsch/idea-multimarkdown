// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ProjectCachedData implements CachedDataOwner {
    final private static Key<PsiFileCachedData> FILE_CACHED_DATA = new Key<>("FILE_CACHED_DATA");

    @NotNull
    public static ProjectCachedData getInstance(Project project) {
        return project.getService(ProjectCachedData.class);
    }

    @NotNull
    public static CachedDataSet projectCachedData(@NotNull Project project) {
        return getInstance(project).getCachedData();
    }

    @NotNull
    public static CachedDataSet projectCachedData(@NotNull PsiFile psiFile) {
        return getInstance(psiFile.getProject()).getCachedData();
    }

    @NotNull
    public static CachedDataSet fileCachedData(@NotNull PsiFile psiFile) {
        return getInstance(psiFile.getProject()).getFileCachedData(psiFile).getCachedData();
    }

    final private @NotNull Project myProject;
    //    private final PsiMap<PsiFile, PsiFileCachedData> myPsiFileCachedData = new PsiMap<>(HashMap::new);
    private final CachedDataSet myDataSet;

    public ProjectCachedData(@NotNull Project project) {
        myProject = project;
        myDataSet = new CachedDataSet("ProjectCachedData{" + myProject.getName() + '}');
    }

    @NotNull
    public Project getProject() {
        return myProject;
    }

    @NotNull
    @Override
    public CachedDataSet getCachedData() {
        return myDataSet;
    }

    @NotNull
    public PsiFileCachedData getFileCachedData(@NotNull PsiFile psiFile) {
        PsiFileCachedData container;

        container = psiFile.getUserData(FILE_CACHED_DATA);
        if (container == null) {
            synchronized (FILE_CACHED_DATA) {
                container = psiFile.getUserData(FILE_CACHED_DATA);
                if (container == null) {
                    container = new PsiFileCachedData(psiFile);
                    psiFile.putUserData(FILE_CACHED_DATA, container);
                }
            }
        }
        return container;
    }

    @Override
    public String toString() {
        return "ProjectCachedData{" + myProject.getName() + '}';
    }
}
