// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.structure;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.vladsch.md.nav.psi.element.MdFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdStructureViewFactory implements PsiStructureViewFactory {
    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder(@NotNull final PsiFile psiFile) {
        return psiFile instanceof MdFile ? new MyTreeBasedStructureViewBuilder((MdFile) psiFile) : null;
    }

    static class MyTreeBasedStructureViewBuilder extends TreeBasedStructureViewBuilder {
        final @NotNull MdFile psiFile;

        public MyTreeBasedStructureViewBuilder(@NotNull final MdFile psiFile) {
            this.psiFile = psiFile;
        }

        @NotNull
        @Override
        public com.intellij.ide.structureView.StructureViewModel createStructureViewModel(@Nullable Editor editor) {
            return new MdStructureViewModel(psiFile, editor, new MdStructureViewFile(psiFile));
        }
    }
}
