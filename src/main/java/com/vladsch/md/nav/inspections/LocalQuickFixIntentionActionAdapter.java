// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.inspections;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorFactoryImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.vladsch.md.nav.annotator.MdBaseIntentionAction;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocalQuickFixIntentionActionAdapter implements LocalQuickFix {
    final private BaseIntentionAction myIntentionAction;

    public LocalQuickFixIntentionActionAdapter(final BaseIntentionAction intentionAction) {
        myIntentionAction = intentionAction;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return myIntentionAction.getFamilyName();
    }

    @Override
    public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
        final PsiFile containingFile = descriptor.getPsiElement().getContainingFile();
        final VirtualFile virtualFile = containingFile.getVirtualFile();
        final Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

        if (document != null) {
            if (myIntentionAction instanceof MdBaseIntentionAction) {
                if (((MdBaseIntentionAction) myIntentionAction).isAvailable(project, document, containingFile)) {
                    ((MdBaseIntentionAction) myIntentionAction).invoke(project, document, containingFile, descriptor);
                }
            } else {
                // needs editor
                final Editor[] editors = EditorFactoryImpl.getInstance().getEditors(document);
                if (editors.length > 0 && !editors[0].isDisposed()) {
                    if (myIntentionAction.isAvailable(project, editors[0], containingFile)) {
                        myIntentionAction.invoke(project, editors[0], containingFile);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public PsiElement getElementToMakeWritable(@NotNull final PsiFile currentFile) {
        return currentFile;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return myIntentionAction.getText(); //String.format("%s: %s", myIntentionAction.getFamilyName(), myIntentionAction.getText());
    }

    @Override
    public boolean startInWriteAction() {
        return myIntentionAction.startInWriteAction();
    }

    public static LocalQuickFix of(BaseIntentionAction intentionAction) {
        return new LocalQuickFixIntentionActionAdapter(intentionAction);
    }
}
