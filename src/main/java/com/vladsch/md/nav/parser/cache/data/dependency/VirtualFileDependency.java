// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.dependency;

import com.intellij.openapi.vfs.VirtualFile;
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
final public class VirtualFileDependency implements DataDependency {
    private final @NotNull VirtualFile myFile;

    public VirtualFileDependency(@NotNull VirtualFile file) {
        myFile = file;
    }

    @Override
    public long getVersion() {
        return myFile.isValid() ? myFile.getModificationStamp() : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualFileDependency that = (VirtualFileDependency) o;

        return myFile.equals(that.myFile);
    }

    @Override
    public int hashCode() {
        return myFile.hashCode();
    }

    @Override
    public String toString() {
        return "VirtualFileDependency{" + myFile.getName() + '}';
    }
}
