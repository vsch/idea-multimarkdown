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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class FileReferenceList {

    public static final Filter ALL_WIKI_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.equivalent(false, false, ext, "md");
        }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference instanceof FileReferenceLink ? fileReference : null;
        }
    };
    public static final Filter ACCESSIBLE_WIKI_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.equivalent(true, false, ext, "md");
        }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference instanceof FileReferenceLink && ((FileReferenceLink) fileReference).isWikiAccessible() ?
                    fileReference : null;
        }
    };
    public static final Filter INACCESSIBLE_WIKI_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return FilePathInfo.equivalent(false, false, ext, "md");
        }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return (fileReference instanceof FileReferenceLink) && !((FileReferenceLink) fileReference).isWikiAccessible() ?
                    fileReference : null;
        }
    };

    interface Filter {
        boolean filterExt(@NotNull String ext);

        @Nullable
        FileReference filterRef(@NotNull FileReference fileReference);
    }

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

    public static final Filter NULL_FILTER = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext) {
            return true;
        }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
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

    public FileReference[] getFileReferences() {
        return fileReferences.clone();
    }

    public int[][] getExtensionFileRefIndices() {
        return extensionFileRefIndices;
    }

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

    public FileReferenceList(@NotNull Filter[] filters, @NotNull FileReference[]... fileReferenceLists) {
        Builder builder = new Builder();

        for (FileReference[] fileReferences : fileReferenceLists) {

            Outer:
            for (FileReference fileReference : fileReferences) {
                FileReference addReference = fileReference;

                if (addReference == null) continue;

                for (Filter filter : filters) {
                    addReference = filter.filterRef(fileReference);
                    if (addReference == null) continue Outer;
                }

                builder.add(addReference);
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
            int extIndex = 0;

            Outer_Ext:
            for (String ext : fileReferenceList.extensions) {
                for (Filter filter : filters) {
                    if (!filter.filterExt(ext)) continue Outer_Ext;
                }

                Outer:
                for (int extFileIndex : fileReferenceList.extensionFileRefIndices[extIndex]) {
                    FileReference addReference = fileReferenceList.fileReferences[extFileIndex];

                    if (addReference == null) continue;

                    for (Filter filter : filters) {
                        addReference = filter.filterRef(addReference);
                        if (addReference == null) continue Outer;
                    }

                    builder.add(addReference);
                }

                extIndex++;
            }
        }

        fileReferences = builder.getFileReferences();
        extensions = builder.getExtensions();
        extensionFileRefIndices = builder.getExtensionFileReferences();
    }

    public static FileReference[][] fileReferencesArray(FileReferenceList... fileReferenceLists) {
        FileReference[][] fileReferences = new FileReference[fileReferenceLists.length][];
        int i = 0;
        for (FileReferenceList fileReferenceList : fileReferenceLists) {
            fileReferences[i++] = fileReferenceList.fileReferences;
        }
        return fileReferences;
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
    public FileReferenceList accessibleWikiPageRefs() {
        return new FileReferenceList(this, ACCESSIBLE_WIKI_REFS_FILTER);
    }

    @NotNull
    public FileReferenceList inaccessibleWikiPageRefs() {
        return new FileReferenceList(this, INACCESSIBLE_WIKI_REFS_FILTER);
    }

    @NotNull
    public FileReferenceList allWikiPageRefs() {
        return new FileReferenceList(this, ALL_WIKI_REFS_FILTER);
    }
}
