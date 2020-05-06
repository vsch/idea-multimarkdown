// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.annotator;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

abstract public class MdBaseIntentionAction extends BaseIntentionAction {
    public abstract boolean isAvailable(@NotNull final Project project, final Document document, final PsiFile file);

    public abstract void invoke(@NotNull final Project project, final Document document, final PsiFile file) throws IncorrectOperationException;

    public boolean isAvailable(@NotNull final Project project, final Document document, final PsiFile file, ProblemDescriptor problemDescriptor) {
        return isAvailable(project, document, file);
    }

    public void invoke(@NotNull final Project project, final Document document, final PsiFile file, ProblemDescriptor problemDescriptor) throws IncorrectOperationException {
        invoke(project, document, file);
    }

    @Override
    public boolean isAvailable(@NotNull final Project project, final Editor editor, final PsiFile file) {
        return isAvailable(project, editor.getDocument(), file);
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
        invoke(project, editor.getDocument(), file);
    }
}
