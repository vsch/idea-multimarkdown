// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.dependency;

import org.jetbrains.annotations.NotNull;

/**
 * Versioned dependency
 * <p>
 * NOTE: equals and hashCode for two versioned instances for the same underlying object should result in equivalence between the versioned instances
 * <p>
 * If the underlying dependency is a PsiElement then original psi element should be used for version tracking and comparisons
 * <p>
 * If the underlying dependency is a PsiFile then original original file should be used for version tracking and comparisons
 */
final public class ProjectFileDependency implements DataDependency {
    private final @NotNull DataKeyDependency myDataKeyDependency;
    private final @NotNull ProjectFilePredicate myFilePredicate;

    public ProjectFileDependency(@NotNull DataKeyDependency dataKeyDependency, @NotNull ProjectFilePredicate filePredicate) {
        myDataKeyDependency = dataKeyDependency;
        myFilePredicate = filePredicate;
    }

    @NotNull
    public DataKeyDependency getDataKeyDependency() {
        return myDataKeyDependency;
    }

    @NotNull
    public ProjectFilePredicate getFilePredicate() {
        return myFilePredicate;
    }

    // never invalidated except when predicate is tested and reports true
    @Override
    public long getVersion() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectFileDependency)) return false;

        ProjectFileDependency that = (ProjectFileDependency) o;

        return myDataKeyDependency.equals(that.myDataKeyDependency);
    }

    @Override
    public int hashCode() {
        return myDataKeyDependency.hashCode();
    }

    @Override
    public String toString() {
        return "ProjectFileDependency{" + myDataKeyDependency + '}';
    }
}
