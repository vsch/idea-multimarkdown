// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.vladsch.flexmark.test.util.TestUtils;
import com.vladsch.md.nav.util.PathInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import java.util.Collection;
import java.util.List;

import static com.vladsch.flexmark.util.misc.Utils.prefixWith;
import static com.vladsch.flexmark.util.misc.Utils.suffixWith;

public class PsiClassInfo {
    final public @NotNull PsiClass psiClass;
    final public @NotNull String specResourceText;
    final public @NotNull Project project;
    final public @NotNull ProjectFileIndex projectFileIndex;
    final public @NotNull Module psiClassModule;
    final public @NotNull GlobalSearchScope psiClassScope;
    final public boolean isTestClass;
    final public @NotNull VirtualFile psiClassContentRoot;
    final public @NotNull String psiClassContentRootRelativePath;
    final public @NotNull String resolvedSpecResourcePath;
    final public @NotNull String psiClassQualifiedPath;
    final public @NotNull String psiQualifiedSuffixedPath;

    private PsiClassInfo(
            @NotNull PsiClass psiClass,
            @NotNull String specResourceText,
            @NotNull Project project,
            @NotNull ProjectFileIndex projectFileIndex,
            @NotNull Module psiClassModule,
            @NotNull GlobalSearchScope psiClassScope,
            boolean isTestClass,
            @NotNull VirtualFile psiClassContentRoot,
            @NotNull String resolvedSpecResourcePath,
            @NotNull String psiClassQualifiedPath
    ) {
        this.psiClass = psiClass;
        this.specResourceText = specResourceText;
        this.project = project;
        this.projectFileIndex = projectFileIndex;
        this.psiClassModule = psiClassModule;
        this.psiClassScope = psiClassScope;
        this.isTestClass = isTestClass;
        this.psiClassContentRoot = psiClassContentRoot;
        this.psiClassContentRootRelativePath = psiClassContentRoot.getPath();
        this.resolvedSpecResourcePath = resolvedSpecResourcePath;
        this.psiClassQualifiedPath = psiClassQualifiedPath;
        this.psiQualifiedSuffixedPath = suffixWith(psiClassQualifiedPath, '/');
    }

    @Nullable
    public static PsiClassInfo getOrNull(@Nullable PsiClass psiClass, @NotNull String specResourceText) {
        if (psiClass == null) return null;

        Project project = psiClass.getProject();
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null) qualifiedName = psiClass.getName();
        if (qualifiedName == null) return null;

        Module psiClassModule = ModuleUtilCore.findModuleForPsiElement(psiClass);
        if (psiClassModule == null || !psiClassModule.isLoaded()) return null;

        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        ProjectFileIndex projectFileIndex = projectRootManager.getFileIndex();
        VirtualFile psiClassVirtualFile = psiClass.getContainingFile().getOriginalFile().getVirtualFile();

        boolean isTestClass = projectFileIndex.isInTestSourceContent(psiClassVirtualFile);
        GlobalSearchScope psiClassScope = isTestClass ? psiClassModule.getModuleTestsWithDependentsScope() : psiClassModule.getModuleWithDependenciesScope();

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(psiClassModule);
        List<VirtualFile> roots = moduleRootManager.getSourceRoots(isTestClass ? JavaSourceRootType.TEST_SOURCE : JavaSourceRootType.SOURCE);
        VirtualFile psiClassContentRoot = psiClassVirtualFile.getParent();

        // NOTE: content root does not give the starting directory, only the parent directory, so we fake the class qualified name to include the prefix
        //   only works if the package reflects the class file system path
        String psiClassVirtualFilePath = psiClassVirtualFile.getPath();
        String psiClassQualifiedPath = new PathInfo(prefixWith(qualifiedName.replace('.', '/'), '/')).getPath();

        for (VirtualFile root : roots) {
            String contentDirPath = suffixWith(root.getPath(), '/');
            if (psiClassVirtualFilePath.startsWith(contentDirPath)) {
                psiClassContentRoot = root;
                break;
            }
        }

        String resolvedSpecResourcePath = TestUtils.getResolvedSpecResourcePath(qualifiedName, specResourceText);
        return new PsiClassInfo(psiClass,
                specResourceText,
                project,
                projectFileIndex,
                psiClassModule,
                psiClassScope,
                isTestClass,
                psiClassContentRoot,
                resolvedSpecResourcePath,
                psiClassQualifiedPath
        );
    }

    private static boolean isAncestorOfAll(@NotNull VirtualFile ancestor, @NotNull Collection<VirtualFile> descendants) {
        for (VirtualFile descendant : descendants) {
            if (!isAncestor(ancestor, descendant)) return false;
        }

        return true;
    }

    private static boolean isAncestor(@NotNull VirtualFile ancestor, @NotNull VirtualFile descendant) {
        return descendant.getPath().startsWith(ancestor.getPath() + "/");
    }
}
