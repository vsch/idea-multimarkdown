// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.dependency;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestartableProjectFileDependency implements ProjectFilePredicate {
    final protected @NotNull PsiFile myFile;

    public RestartableProjectFileDependency(@NotNull PsiFile file) {
        myFile = file;
    }

    /**
     * NOTE: not invoked on ADT nor inside read action, have to do all that manually
     * if false is returned then key for the dependency will be invalidated.
     * CachedDataSet and CachedDataKey determined by where dependency was added.
     * <p>
     * Test dependency on given file and return true if still valid
     *
     * @param psiFile file to test
     *
     * @return true if still valid, false if dependent should be invalidated
     */
    @Override
    public boolean test(@NotNull PsiFile psiFile) {
        return true;
    }

    /**
     * Override and implement to restart daemon code analyzer for the file when this predicate is invalidated
     *
     * @return file to restart analyzer on or null if none
     */
    @Nullable
    public PsiFile getDependentFile() {
        return myFile;
    }
}
