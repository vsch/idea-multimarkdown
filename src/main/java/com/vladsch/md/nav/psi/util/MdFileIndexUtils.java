// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

import com.intellij.openapi.fileTypes.FileNameMatcher;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.vladsch.flexmark.util.sequence.SequenceUtils;
import com.vladsch.md.nav.MdLanguage;
import com.vladsch.md.nav.util.PathInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaResourceRootType;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.vladsch.flexmark.util.misc.Utils.suffixWith;

public class MdFileIndexUtils {
    public static final Set<JpsModuleSourceRootType<?>> SOURCE_RESOURCE_ROOT_TYPES = new HashSet<>(Arrays.asList(JavaResourceRootType.RESOURCE, JavaSourceRootType.SOURCE));
    public static final Set<JpsModuleSourceRootType<?>> TEST_SOURCE_RESOURCE_ROOT_TYPES = new HashSet<>(Arrays.asList(JavaResourceRootType.TEST_RESOURCE, JavaSourceRootType.TEST_SOURCE));
    public static final Set<JpsModuleSourceRootType<?>> SOURCE_TEST_SOURCE_ROOT_TYPES = new HashSet<>(Arrays.asList(JavaSourceRootType.SOURCE, JavaSourceRootType.TEST_SOURCE));
    public static final Set<JpsModuleSourceRootType<?>> SOURCE_ROOT_TYPES = new HashSet<>(Collections.singletonList(JavaSourceRootType.SOURCE));
    public static final Set<JpsModuleSourceRootType<?>> RESOURCE_TEST_RESOURCE_ROOT_TYPES = new HashSet<>(Arrays.asList(JavaResourceRootType.TEST_RESOURCE, JavaResourceRootType.RESOURCE));

    /**
     * Get module roots for file
     *
     * @param file      file
     * @param rootTypes root types or null for source roots
     *
     * @return list of virtual files for module's roots
     */
    @NotNull
    public static List<VirtualFile> getFileModuleRoots(@NotNull PsiFile file, @Nullable Set<JpsModuleSourceRootType<?>> rootTypes) {
        Module fileModule = ModuleUtilCore.findModuleForFile(file);
        if (fileModule == null || !fileModule.isLoaded()) return Collections.emptyList();
        return ModuleRootManager.getInstance(fileModule).getSourceRoots(rootTypes == null ? SOURCE_ROOT_TYPES : rootTypes);
    }

    /**
     * Get module roots for file
     *
     * @param file file
     *
     * @return list of roots for project
     */
    @NotNull
    public static VirtualFile[] getFileProjectModuleRoots(@NotNull PsiFile file) {
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(file.getProject());
        VirtualFile[] projectModuleRoots = projectRootManager.getContentRootsFromAllModules();
        Module fileModule = ModuleUtilCore.findModuleForFile(file);
        if (fileModule == null || !fileModule.isLoaded()) return VirtualFile.EMPTY_ARRAY;
        return projectModuleRoots;
    }

    /**
     * Get module root for file
     *
     * @param file      file
     * @param rootTypes root types or null for source roots
     *
     * @return virtualFile for the root which contains this file
     */
    @Nullable
    public static VirtualFile getFileModuleRoot(@NotNull PsiFile file, @Nullable Set<JpsModuleSourceRootType<?>> rootTypes) {
        // NOTE: find file in one of the content roots of the module where it is located
        List<VirtualFile> roots = getFileModuleRoots(file, rootTypes);

        VirtualFile virtualFile = file.getVirtualFile();
        for (VirtualFile root : roots) {
            String rootPath = suffixWith(root.getPath(), '/');
            if (virtualFile.getPath().startsWith(rootPath)) {
                return root;
            }
        }
        return null;
    }

    /**
     * Get project root which contains this file
     *
     * @param file file
     *
     * @return virtualFile for the project module root which contains this file, including project base directory, or null if no roots match
     */
    @Nullable
    public static VirtualFile getFileProjectModuleRoot(@NotNull PsiFile file) {
        // NOTE: find file in one of the content roots of the module where it is located
        VirtualFile[] roots = getFileProjectModuleRoots(file);

        VirtualFile virtualFile = file.getVirtualFile();
        VirtualFile fileRoot = null;
        for (VirtualFile root : roots) {
            String rootPath = suffixWith(root.getPath(), '/');
            if (virtualFile.getPath().startsWith(rootPath)) {
                if (fileRoot == null || fileRoot.getPath().length() < root.getPath().length()) {
                    fileRoot = root;
                }
            }
        }
        return fileRoot;
    }

    static class FileExtensionPredicate implements Predicate<String> {
        final private @NotNull String[] myExtensions;

        public FileExtensionPredicate(@NotNull String[] extensions) {
            myExtensions = extensions;
        }

        public FileExtensionPredicate(@NotNull Collection<String> extensions) {
            myExtensions = new String[extensions.size()];

            int i = 0;
            for (String ext : extensions) {
                myExtensions[i++] = ext;
            }
        }

        boolean isExtension(CharSequence sequence) {
            for (String ext : myExtensions) {
                if (SequenceUtils.equals(ext, sequence)) return true;
            }
            return false;
        }

        @Override
        public boolean test(String s) {
            int pos = s.lastIndexOf('.');
            return pos > 0 && isExtension(s.subSequence(pos + 1, s.length()));
        }
    }

    final private static FileExtensionPredicate IMAGE_EXT_PREDICATE = new FileExtensionPredicate(PathInfo.IMAGE_EXTENSIONS);
    private static @Nullable FileExtensionPredicate MARKDOWN_EXT_PREDICATE = null;
    private final static Object MARKDOWN_EXT_PREDICATE_LOCK = new Object();

    public static Predicate<String> imageNamePreFilter() {
        return IMAGE_EXT_PREDICATE;
    }

    public static Predicate<String> markdownNamePreFilter() {
        if (MARKDOWN_EXT_PREDICATE == null) {
            synchronized (MARKDOWN_EXT_PREDICATE_LOCK) {
                if (MARKDOWN_EXT_PREDICATE == null) {
                    FileTypeManager typeManager = FileTypeManager.getInstance();
                    FileType fileType = typeManager.findFileTypeByLanguage(MdLanguage.INSTANCE);
                    assert fileType != null;
                    List<FileNameMatcher> typeExtensions = typeManager.getAssociations(fileType);
                    ArrayList<String> extensionList = new ArrayList<>(Arrays.asList(PathInfo.MARKDOWN_EXTENSIONS));
                    HashSet<String> extensions = new HashSet<>(extensionList);

                    for (FileNameMatcher typeExt : typeExtensions) {
                        String typeExtText = typeExt.getPresentableString();

                        int pos = typeExtText.lastIndexOf('.');
                        if (pos > 0 /* && extText.charAt(pos - 1) == '*'*/) {
                            String substring = typeExtText.substring(pos + 1);
                            if (!extensions.contains(substring)) {
                                extensions.add(substring);
                                extensionList.add(substring);
                            }
                        }
                    }

                    MARKDOWN_EXT_PREDICATE = new FileExtensionPredicate(extensionList);
                }
            }
        }

        return MARKDOWN_EXT_PREDICATE;
    }

    public static void forAllFiles(@NotNull Project project, @NotNull GlobalSearchScope searchScope, @NotNull Predicate<String> namePreFilter, @NotNull Consumer<VirtualFile> consumer) {
        for (String name : FilenameIndex.getAllFilenames(project)) {
            if (namePreFilter.test(name)) {
                Collection<VirtualFile> filesByName = FilenameIndex.getVirtualFilesByName(project, name, searchScope);
                for (VirtualFile virtualFile : filesByName) {
                    consumer.accept(virtualFile);
                }
            }
        }
    }

    public static Collection<VirtualFile> getAllFiles(@NotNull Project project, @NotNull GlobalSearchScope searchScope, @NotNull Predicate<String> namePreFilter, @NotNull Predicate<VirtualFile> filter) {
        ArrayList<VirtualFile> files = new ArrayList<>();
        forAllFiles(project, searchScope, namePreFilter, virtualFile -> {
            if (filter.test(virtualFile)) {
                files.add(virtualFile);
            }
        });
        return files.isEmpty() ? Collections.emptyList() : files;
    }
}
