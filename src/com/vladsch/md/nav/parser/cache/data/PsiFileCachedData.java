// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class PsiFileCachedData implements CachedDataOwner {
    final private @NotNull Project myProject;
    final private @NotNull PsiFile myFile;
    final private @NotNull CachedDataSet myDataSet;

    public PsiFileCachedData(@NotNull PsiFile container) {
        super();
        myFile = container.getOriginalFile();
        myProject = myFile.getProject();
        myDataSet = new CachedDataSet("PsiFileCachedData{" + myFile.getName() + '}');
    }

    @NotNull
    public PsiFile getFile() {
        return myFile;
    }

    @NotNull
    @Override
    public Project getProject() {
        return myProject;
    }

    @NotNull
    @Override
    public CachedDataSet getCachedData() {
        return myDataSet;
    }

    @Override
    public String toString() {
        return "PsiFileCachedData{" + myFile + '}';
    }
}
