// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.psi.index;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.vladsch.md.nav.MdFileType;
import com.vladsch.md.nav.parser.MdFileElementType;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.util.PathInfo;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author yole
 */
public class FlexmarkFileIndex extends ScalarIndexExtension<String> {
    @NonNls public static final ID<String, Void> NAME = ID.create("FlexmarkFileIndex");
    private final MyDataIndexer myDataIndexer = new MyDataIndexer();

    @Override
    @NotNull
    public ID<String, Void> getName() {
        return NAME;
    }

    @Override
    @NotNull
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return myDataIndexer;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(MdFileType.INSTANCE);
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return MdFileElementType.MD_INDEX_VERSION;
    }

    private static class MyDataIndexer implements DataIndexer<String, Void, FileContent> {
        MyDataIndexer() {}

        @Override
        @NotNull
        public Map<String, Void> map(@NotNull final FileContent inputData) {
            String fileName = null;
            try {
                int frontMatterOffset = MdFile.frontMatterOffset(inputData.getContentAsText(), false, true);
                if (frontMatterOffset > 0) {
                    fileName = new PathInfo(inputData.getFileName()).getFileName();
                }
            } catch (Exception e) {
                // ignore
            }

            if (fileName != null) {
                return Collections.singletonMap(fileName, null);
            }
            return Collections.emptyMap();
        }
    }

    @NotNull
    public static List<MdFile> findFlexmarkFiles(@NotNull Project project, @Nullable String fileName) {
        return findFlexmarkFiles(project, fileName, ProjectScope.getAllScope(project));
    }

    @NotNull
    public static List<MdFile> findFlexmarkFiles(
            final @NotNull Project project,
            @Nullable String fileName,
            final @NotNull GlobalSearchScope scope
    ) {
        if (fileName != null) fileName = new PathInfo(fileName).getFileName();
        final String finalFileName = fileName;

        return ApplicationManager.getApplication().runReadAction((Computable<List<MdFile>>) () -> {
            final Collection<VirtualFile> files;
            GlobalSearchScope filter = GlobalSearchScope.projectScope(project).intersectWith(scope);

            try {
                if (finalFileName == null) {
                    files = new ArrayList<VirtualFile>();
                    Collection<String> keys = FileBasedIndex.getInstance().getAllKeys(NAME, project);

                    for (String key : keys) {
                        final Collection<VirtualFile> list = FileBasedIndex.getInstance().getContainingFiles(NAME, key, filter);
                        files.addAll(list);
                    }
                } else {
                    files = FileBasedIndex.getInstance().getContainingFiles(NAME, finalFileName, filter);
                }
            } catch (IndexNotReadyException e) {
                return Collections.emptyList();
            }

            if (files.isEmpty()) return Collections.emptyList();
            List<MdFile> result = new ArrayList<>();
            for (VirtualFile file : files) {
                if (!file.isValid()) continue;
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                if (psiFile != null) {
                    assert psiFile instanceof MdFile;
                    MdFile mdFile = (MdFile) psiFile;
                    if (mdFile.getSubType().equals(MdFile.FLEXMARK_SUBTYPE)) {
                        result.add(mdFile);
                    }
                }
            }
            return result;
        });
    }
}
