// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.vladsch.md.nav.psi.element.MdFile;
import org.jetbrains.annotations.NotNull;

public class SpecFileInfo {
    final public @NotNull PsiClassInfo info;
    final public @NotNull MdFile mdFile;
    final public @NotNull VirtualFile mdVirtualFile;

    private SpecFileInfo(@NotNull PsiClassInfo info, @NotNull MdFile mdFile) {
        this.info = info;
        this.mdFile = mdFile;
        this.mdVirtualFile = mdFile.getVirtualFile();
    }

    @NotNull
    public static SpecFileInfo get(@NotNull PsiClassInfo info, @NotNull MdFile mdFile) {
        return new SpecFileInfo(info, mdFile);
    }
}
