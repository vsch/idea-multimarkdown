// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.LineAppendable;
import com.vladsch.flexmark.util.sequence.LineAppendableImpl;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.language.MdCodeStyleSettings;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.element.MdPsiElement;
import com.vladsch.md.nav.psi.util.MdPsiImplUtil;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.plugin.util.LazyFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdFactoryContext {
    final private @NotNull Project myProject;
    final private @Nullable PsiFile myPsiFile;
    final private @NotNull FileType myFileType;
    final private @Nullable PsiElement myContext;
    final private LazyFunction<MdFactoryContext, MdRenderingProfile> myRenderingProfile = new LazyFunction<>(MdFactoryContext::getLazyProfile);
    final private LazyFunction<MdFactoryContext, MdCodeStyleSettings> myCodeStyleSettings = new LazyFunction<>(MdFactoryContext::getLazyStyle);
    final private LazyFunction<MdFactoryContext, BasedSequence> myBaseSeq = new LazyFunction<>(MdFactoryContext::getLazyBaseSeq);

    public MdFactoryContext(@NotNull final Project project) {
        this(project, MdFileType.INSTANCE);
    }

    public MdFactoryContext(@NotNull final Project project, @Nullable PsiFile psiFile) {
        PsiFile containingFile = psiFile == null ? null : getOuterContainingFile(psiFile);
        myProject = project;
        myPsiFile = containingFile instanceof MdFile ? containingFile : null;
        myFileType = MdFileType.INSTANCE;
        myContext = null;
    }

    public MdFactoryContext(@NotNull final Project project, @NotNull FileType fileType) {
        myProject = project;
        myPsiFile = null;
        myFileType = fileType;
        myContext = null;
    }

    public MdFactoryContext(@NotNull final PsiFile psiFile) {
        PsiFile containingFile = getOuterContainingFile(psiFile);
        myPsiFile = containingFile instanceof MdFile ? containingFile : null;
        myProject = psiFile.getProject();
        myFileType = psiFile.getFileType();
        myContext = null;
    }

    public MdFactoryContext(@NotNull final PsiElement element) {
        PsiFile containingFile = getOuterContainingFile(element);
        myPsiFile = containingFile instanceof MdFile ? containingFile : null;
        myProject = containingFile.getProject();
        myFileType = containingFile.getFileType();
        myContext = element;
    }

    public static PsiFile getOuterContainingFile(@NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile.getContext() instanceof MdPsiElement) {
            containingFile = containingFile.getContext().getContainingFile();
        }
        return containingFile;
    }

    @Nullable
    public PsiElement getContext() {
        return myContext;
    }

    @NotNull
    public LineAppendable getEmptyAppendable() {
        return new LineAppendableImpl(myBaseSeq.getValue(this).getBuilder(), 0);
    }

    private static MdRenderingProfile getLazyProfile(MdFactoryContext mdFactoryContext) {
        if (mdFactoryContext.myPsiFile != null) {
            return MdRenderingProfileManager.getProfile(mdFactoryContext.myPsiFile);
        } else {
            return MdRenderingProfileManager.getInstance(mdFactoryContext.myProject).getDefaultRenderingProfile();
        }
    }

    private static MdCodeStyleSettings getLazyStyle(MdFactoryContext mdFactoryContext) {
        if (mdFactoryContext.myPsiFile != null) {
            return MdCodeStyleSettings.getInstance(mdFactoryContext.myPsiFile);
        } else {
            return MdCodeStyleSettings.getInstance(mdFactoryContext.myProject);
        }
    }

    private static BasedSequence getLazyBaseSeq(MdFactoryContext mdFactoryContext) {
        if (mdFactoryContext.myPsiFile != null) {
            return BasedSequence.of(mdFactoryContext.myPsiFile.getText());
        } else {
            return BasedSequence.EMPTY;
        }
    }

    public PsiFile createFile(CharSequence text) {
        if (myPsiFile != null && myFileType == MdFileType.INSTANCE) {
            // need to create a file based on this file
            PsiFile newFile = PsiFileFactory.getInstance(myProject).createFileFromText(
                    myPsiFile.getVirtualFile().getName(),
                    MdLanguage.INSTANCE,
                    text,
                    false,
                    true,
                    false,
                    MdPsiImplUtil.getVirtualFile(myPsiFile)
            );
            if (newFile != null) return newFile;
        }

        String name = "dummy" + myFileType.getDefaultExtension();
        return PsiFileFactory.getInstance(myProject).createFileFromText(name, myFileType, text);
    }

    @NotNull
    public Project getProject() {
        return myProject;
    }

    @Nullable
    public PsiFile getPsiFile() {
        return myPsiFile;
    }

    @NotNull
    public FileType getFileType() {
        return myFileType;
    }

    @NotNull
    public MdRenderingProfile getRenderingProfile() {
        return myRenderingProfile.getValue(this);
    }

    @NotNull
    public MdCodeStyleSettings getCodeStyleSettings() {
        return myCodeStyleSettings.getValue(this);
    }
}
