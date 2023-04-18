// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ElementSource {
    public enum Source {
        FILE,
        OUTER_FILE,
        INCLUDED_FILE,
        INCLUDING_FILE;

        public ElementSource withFile(@NotNull PsiFile file) {
            return new ElementSource(this, file);
        }
    }

    public static ElementSource FILE(@NotNull PsiFile file) { return Source.FILE.withFile(file); }

    public static ElementSource OUTER_FILE(@NotNull PsiFile file) { return Source.OUTER_FILE.withFile(file); }

    public static ElementSource INCLUDED_FILE(@NotNull PsiFile file) { return Source.INCLUDED_FILE.withFile(file); }

    public static ElementSource INCLUDING_FILE(@NotNull PsiFile file) { return Source.INCLUDING_FILE.withFile(file); }

    public final @NotNull Source source;
    public final @NotNull PsiFile file;

    ElementSource(@NotNull Source source, @NotNull PsiFile file) {
        this.source = source;
        this.file = file;
    }
}
