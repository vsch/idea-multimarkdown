/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FileReferenceList {
    public interface Filter {
        boolean filterExt(@NotNull String ext);

        @Nullable
        FileReference filterRef(@NotNull FileReference fileReference);
    }

    public interface TransformFilter<T> extends Filter {
        @Nullable
        T transformRef(@NotNull FileReference fileReference);

        T[] getResult(List<T> result);
    }

    public static final Filter ALL_WIKI_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }
    };
    public static final Filter ACCESSIBLE_WIKI_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isWikiPageExt(ext);
        }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference instanceof FileReferenceLink && ((FileReferenceLink) fileReference).isWikiAccessible() ? fileReference : null;
        }
    };

    public static final Filter INACCESSIBLE_WIKI_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return true;
        }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return !(fileReference instanceof FileReferenceLink) || !((FileReferenceLink) fileReference).isWikiAccessible() ? fileReference : null;
        }
    };

    public static final Filter IMAGE_FILE_FILTER = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isImageExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }
    };

    public static final Filter MARKDOWN_FILE_FILTER = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }
    };

    public static final Filter WIKIPAGE_FILE_FILTER = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isWikiPageExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference.isUnderWikiHome() ? fileReference : null;
        }
    };

    public static final Filter NULL_FILTER = null;

    public static final TransformFilter<VirtualFile> VIRTUAL_FILE_TRANSFILTER = new TransformFilter<VirtualFile>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return true;
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public VirtualFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getVirtualFile();
        }

        @Override
        public VirtualFile[] getResult(List<VirtualFile> result) {
            return result.toArray(new VirtualFile[result.size()]);
        }
    };

    public static final TransformFilter<PsiFile> PSI_FILE_TRANSFILTER = new TransformFilter<PsiFile>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return true;
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public PsiFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getPsiFile();
        }

        @Override
        public PsiFile[] getResult(List<PsiFile> result) {
            return result.toArray(new PsiFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> MARKDOWN_FILE_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public MultiMarkdownFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getMultiMarkdownFile();
        }

        @Override
        public MultiMarkdownFile[] getResult(List<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> ALL_WIKIPAGE_FILE_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public MultiMarkdownFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getMultiMarkdownFile();
        }

        @Override
        public MultiMarkdownFile[] getResult(List<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> ACCESSIBLE_WIKIPAGE_FILE_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isWikiPageExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return ACCESSIBLE_WIKI_REFS_FILTER.filterRef(fileReference);
        }

        @Nullable
        @Override
        public MultiMarkdownFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getMultiMarkdownFile();
        }

        @Override
        public MultiMarkdownFile[] getResult(List<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> INACCESSIBLE_WIKIPAGE_FILE_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return INACCESSIBLE_WIKI_REFS_FILTER.filterRef(fileReference);
        }

        @Nullable
        @Override
        public MultiMarkdownFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getMultiMarkdownFile();
        }

        @Override
        public MultiMarkdownFile[] getResult(List<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<String> ALL_WIKIPAGE_REFS_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public String transformRef(@NotNull FileReference fileReference) {
            return fileReference instanceof FileReferenceLink ? ((FileReferenceLink) fileReference).getWikiPageRef() : fileReference.getFileNameNoExtAsWikiRef();
        }

        @Override
        public String[] getResult(List<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final TransformFilter<String> ACCESSIBLE_WIKIPAGE_REFS_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isWikiPageExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return ACCESSIBLE_WIKI_REFS_FILTER.filterRef(fileReference);
        }

        @Nullable
        @Override
        public String transformRef(@NotNull FileReference fileReference) {
            return ((FileReferenceLink) fileReference).getWikiPageRef();
        }

        @Override
        public String[] getResult(List<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final TransformFilter<String> INACCESSIBLE_WIKIPAGE_REFS_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return INACCESSIBLE_WIKI_REFS_FILTER.filterRef(fileReference);
        }

        @Nullable
        @Override
        public String transformRef(@NotNull FileReference fileReference) {
            return fileReference instanceof FileReferenceLink ? ((FileReferenceLink) fileReference).getWikiPageRef() : fileReference.getFileNameNoExtAsWikiRef();
        }

        @Override
        public String[] getResult(List<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final TransformFilter<String> ALL_LINK_REFS_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return true;
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public String transformRef(@NotNull FileReference fileReference) {
            return fileReference instanceof FileReferenceLink ? ((FileReferenceLink) fileReference).getLinkRef() : fileReference.getFilePath();
        }

        @Override
        public String[] getResult(List<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final Filter ANY_FILE_FILTER = NULL_FILTER;

    public static class Builder {
        ArrayList<FileReference> fileReferences = new ArrayList<FileReference>();
        HashSet<String> extensions = new HashSet<String>();
        HashMap<String, ArrayList<Integer>> extensionFileReferences = new HashMap<String, ArrayList<Integer>>();

        public FileReference[] getFileReferences() {
            return fileReferences.toArray(new FileReference[fileReferences.size()]);
        }

        public String[] getExtensions() {
            return extensions.toArray(new String[extensions.size()]);
        }

        public int[][] getExtensionFileReferences() {
            int[][] extensionFileReferences = new int[extensions.size()][];
            int i = 0;
            for (String ext : extensions) {
                ArrayList<Integer> extIndices = this.extensionFileReferences.get(ext);
                int jMax = extIndices.size();
                int[] extIndicesArr = new int[jMax];
                for (int j = 0; j < jMax; j++) {
                    extIndicesArr[j] = extIndices.get(j);
                }
                extensionFileReferences[i] = extIndicesArr;
                i++;
            }
            return extensionFileReferences;
        }

        public Builder() {
        }

        public Builder(@NotNull Builder other) {
            this.fileReferences.addAll(other.fileReferences);
            this.extensions.addAll(other.extensions);
            this.extensionFileReferences.putAll(other.extensionFileReferences);
        }

        public Builder(FileReference... fileReferences) {
            for (FileReference fileReference : fileReferences) {
                add(fileReference);
            }
        }

        public Builder(Collection<? extends FileReference> fileReferences) {
            for (FileReference fileReference : fileReferences) {
                add(fileReference);
            }
        }

        public void add(@NotNull FileReference fileReference) {
            String ext = fileReference.getExt();
            if (!extensions.contains(ext)) {
                extensions.add(ext);
                extensionFileReferences.put(ext, new ArrayList<Integer>(1));
            }
            extensionFileReferences.get(ext).add(fileReferences.size());
            fileReferences.add(fileReference);
        }
    }

    protected final FileReference[] fileReferences;
    protected final int[][] extensionFileRefIndices; // [ext index][0....max] = index in fileReferences that contains that extension
    protected final String[] extensions;      //

    public int length() {
        return fileReferences.length;
    }

    @NotNull
    public FileReference[] getFileReferences() {
        return fileReferences.clone();
    }

    @NotNull
    public int[][] getExtensionFileRefIndices() {
        return extensionFileRefIndices;
    }

    @NotNull
    public String[] getExtensions() {
        return extensions;
    }

    public FileReferenceList(Collection<? extends FileReference> c) {
        this(new Builder(c));
    }

    public FileReferenceList(FileReference... fileReferences) {
        this(new Builder(fileReferences));
    }

    public FileReferenceList(@NotNull Builder builder) {
        fileReferences = builder.getFileReferences();
        extensions = builder.getExtensions();
        extensionFileRefIndices = builder.getExtensionFileReferences();
    }

    public FileReferenceList(@NotNull Filter[] filters, @NotNull FileReference[]... fileReferencesArray) {
        Builder builder = new Builder();

        for (FileReference[] fileReferences : fileReferencesArray) {

            Outer:
            for (FileReference fileReference : fileReferences) {
                if (fileReference == null) continue;

                for (Filter filter : filters) {
                    if (filter == null) continue;
                    if (!filter.filterExt(fileReference.getExt())) continue Outer;

                    fileReference = filter.filterRef(fileReference);
                    if (fileReference == null) continue Outer;
                }

                builder.add(fileReference);
            }
        }

        fileReferences = builder.getFileReferences();
        extensions = builder.getExtensions();
        extensionFileRefIndices = builder.getExtensionFileReferences();
    }

    // this is a union and filter constructor
    public FileReferenceList(@NotNull Filter[] filters, FileReferenceList... fileReferenceLists) {
        Builder builder = new Builder();

        for (FileReferenceList fileReferenceList : fileReferenceLists) {
            int extIndex = -1;

            Outer_Ext:
            for (String ext : fileReferenceList.extensions) {
                extIndex++;

                for (Filter filter : filters) {
                    if (filter != null && !filter.filterExt(ext)) continue Outer_Ext;
                }

                Outer:
                for (int extFileIndex : fileReferenceList.extensionFileRefIndices[extIndex]) {
                    FileReference addReference = fileReferenceList.fileReferences[extFileIndex];

                    if (addReference == null) continue;

                    for (Filter filter : filters) {
                        if (filter == null) continue;
                        addReference = filter.filterRef(addReference);
                        if (addReference == null) continue Outer;
                    }

                    builder.add(addReference);
                }
            }
        }

        fileReferences = builder.getFileReferences();
        extensions = builder.getExtensions();
        extensionFileRefIndices = builder.getExtensionFileReferences();
    }

    public FileReferenceList(@Nullable Filter filter, @NotNull FileReference... fileReferences) {
        this(new Filter[] { filter }, fileReferences);
    }

    public FileReferenceList(@NotNull Filter[] filters, @NotNull FileReference... fileReferences) {
        this(filters, new FileReference[][] { fileReferences });
    }

    public FileReferenceList(@NotNull FileReferenceList other, Filter... filters) {
        this(filters, other.fileReferences);
    }

    public FileReferenceList(@NotNull FileReferenceList other) {
        fileReferences = other.fileReferences;
        extensions = other.extensions;
        extensionFileRefIndices = other.extensionFileRefIndices;
    }

    @NotNull
    public FileReferenceListQuery getQuery() {
        return new FileReferenceListQuery(this);
    }

    @NotNull
    public static FileReference[][] fileReferencesArray(FileReferenceList... fileReferenceLists) {
        FileReference[][] fileReferences = new FileReference[fileReferenceLists.length][];
        int i = 0;
        for (FileReferenceList fileReferenceList : fileReferenceLists) {
            fileReferences[i++] = fileReferenceList.fileReferences;
        }
        return fileReferences;
    }

    @NotNull
    public FileReferenceList filter(Filter... filters) {
        return new FileReferenceList(this, filters);
    }

    @NotNull
    public FileReferenceList markdownFileRefs() {
        return new FileReferenceList(this, MARKDOWN_FILE_FILTER);
    }

    @NotNull
    public FileReferenceList imageFileRefs() {
        return new FileReferenceList(this, IMAGE_FILE_FILTER);
    }

    @NotNull
    public FileReferenceList accessibleWikiPageRefs() {
        return filter(ACCESSIBLE_WIKI_REFS_FILTER);
    }

    @NotNull
    public FileReferenceList inaccessibleWikiPageRefs() {
        return filter(INACCESSIBLE_WIKI_REFS_FILTER);
    }

    @NotNull
    public FileReferenceList allWikiPageRefs() {
        return filter(ALL_WIKI_REFS_FILTER);
    }

    @NotNull
    public <T> T[] transform(@NotNull TransformFilter<T> transFilter) {
        ArrayList<T> result = new ArrayList<T>(fileReferences.length);

        for (FileReference fileReference : fileReferences) {
            int extIndex = -1;

            for (String ext : extensions) {
                extIndex++;

                if (!transFilter.filterExt(ext)) continue;

                for (int extFileIndex : extensionFileRefIndices[extIndex]) {
                    FileReference addReference = fileReferences[extFileIndex];
                    if (addReference == null) continue;

                    addReference = transFilter.filterRef(addReference);
                    if (addReference == null) continue;

                    T transformed = transFilter.transformRef(addReference);
                    if (transformed == null) continue;

                    result.add(transformed);
                }
            }
        }

        return transFilter.getResult(result);
    }

    @NotNull
    public VirtualFile[] getVirtualFiles() {
        return transform(VIRTUAL_FILE_TRANSFILTER);
    }

    @NotNull
    public PsiFile[] getPsiFiles() {
        return transform(PSI_FILE_TRANSFILTER);
    }

    @NotNull
    public MultiMarkdownFile[] getMarkdownFiles() {
        return transform(MARKDOWN_FILE_TRANSFILTER);
    }

    @NotNull
    public MultiMarkdownFile[] getAllWikiPageFiles() {
        return transform(ALL_WIKIPAGE_FILE_TRANSFILTER);
    }

    @NotNull
    public MultiMarkdownFile[] getAccessibleWikiPageFiles() {
        return transform(ACCESSIBLE_WIKIPAGE_FILE_TRANSFILTER);
    }

    @NotNull
    public MultiMarkdownFile[] getInaccessibleWikiPageFiles() {
        return transform(INACCESSIBLE_WIKIPAGE_FILE_TRANSFILTER);
    }

    @NotNull
    public String[] getWikiPageRefStrings(boolean allowInaccessible) {
        return transform(allowInaccessible ? ALL_WIKIPAGE_REFS_TRANSFILTER : ACCESSIBLE_WIKIPAGE_REFS_TRANSFILTER);
    }

    @NotNull
    public String[] getAllWikiPageRefStrings() {
        return transform(ALL_WIKIPAGE_REFS_TRANSFILTER);
    }

    @NotNull
    public String[] getAccessibleWikiPageRefStrings() {
        return transform(ACCESSIBLE_WIKIPAGE_REFS_TRANSFILTER);
    }

    @NotNull
    public String[] getInaccessibleWikiPageRefStrings() {
        return transform(INACCESSIBLE_WIKIPAGE_REFS_TRANSFILTER);
    }

    @NotNull
    public int[] countByFilter(Filter... filters) {
        int[] result = new int[filters.length];
        for (String ext : extensions) {
            int filterIndex = 0;
            for (Filter filter : filters) {
                if (filter.filterExt(ext)) result[filterIndex++]++;
            }
        }
        return result;
    }
}
