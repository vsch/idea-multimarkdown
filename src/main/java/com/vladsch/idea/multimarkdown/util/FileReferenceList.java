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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;

public class FileReferenceList {
    private static final Logger logger = org.apache.log4j.Logger.getLogger(FileReferenceList.class);

    public interface Filter {
        boolean filterExt(@NotNull String ext, String anchor);

        boolean isRefFilter();

        @Nullable
        FileReference filterRef(@NotNull FileReference fileReference);
    }

    interface PostMatchFilter {
        FileReferenceList match(@NotNull FileReferenceList fileReferenceList, @NotNull FilePathInfo linkRefInfo, boolean isWikiLink, @Nullable Boolean caseSensitive, @Nullable Boolean keepWikiExt);
    }

    final protected static PostMatchFilter postMatchFilter = new PostMatchFilter() {
        @Override
        public FileReferenceList match(@NotNull FileReferenceList fileReferenceList, @NotNull FilePathInfo linkRefInfo, boolean isWikiLink, @Nullable Boolean caseSensitive, @Nullable Boolean keepExt) {
            ArrayList<FileReference> newList = new ArrayList<FileReference>();

            FilePathInfo fixedLinkRefInfo = linkRefInfo.getFullFilePath().contains("%23") ? new FilePathInfo(linkRefInfo.getFilePath().replace("%23", "#")) : linkRefInfo;

            String linkRefSubDir = FilePathInfo.removeEnd(fixedLinkRefInfo.getPath(), '/');
            boolean isWikiSubDir = linkRefSubDir.endsWith(FilePathInfo.GITHUB_WIKI_REL_HOME) || FilePathInfo.removeEnd(fixedLinkRefInfo.getFullFilePath(), '/').endsWith(FilePathInfo.GITHUB_WIKI_REL_HOME);
            boolean haveSubDir = !linkRefSubDir.isEmpty();
            String linkRefWithAnchor = fixedLinkRefInfo.getFilePathWithAnchor();
            boolean doKeepWikiExt = keepExt != null && keepExt || keepExt == null && fixedLinkRefInfo.hasWithAnchorExtWithDot() && !fixedLinkRefInfo.hasWithAnchorWikiPageExt();
            boolean doKeepLinkRefExt = keepExt != null && keepExt || keepExt == null && fixedLinkRefInfo.hasWithAnchorExtWithDot();

            String linkRef = fixedLinkRefInfo.getFilePath();
            String linkRefExt = doKeepLinkRefExt ? linkRef : fixedLinkRefInfo.getFilePathNoExt();
            String linkRefWithAnchorExt = doKeepLinkRefExt ? fixedLinkRefInfo.getFilePathWithAnchor() : fixedLinkRefInfo.getFilePathWithAnchorNoExt();

            // TODO: in the new patterned matched queries this has to be handled in the post match filter since queries are always ignoring dashes
            // always ignore space/dash differences, let the query handle that

            String wikiLinkRefExt = doKeepWikiExt ? linkRef.replace('-', ' ') : fixedLinkRefInfo.getFilePathNoExt().replace('-', ' ');
            String wikiLinkRefWithAnchorExt = doKeepWikiExt ? fixedLinkRefInfo.getFilePathWithAnchor().replace('-', ' ') : fixedLinkRefInfo.getFilePathWithAnchorNoExt().replace('-', ' ');

            for (FileReference linkTarget : fileReferenceList.get()) {
                if (!(linkTarget instanceof FileReferenceLink || linkTarget instanceof FileReferenceLinkGitHubRules)) {
                    int tmp = 0;
                }
                assert (linkTarget instanceof FileReferenceLink || linkTarget instanceof FileReferenceLinkGitHubRules);

                FileReferenceLink fileReferenceLink = (FileReferenceLink) linkTarget;

                if (!fileReferenceLink.isWikiPage()) {
                    // default is don't keep extension for non-wikis
                    String ref = keepExt == null || keepExt ? fileReferenceLink.getLinkRefWithAnchor() : fileReferenceLink.getLinkRefWithAnchorNoExt();

                    // default case sensitive
                    if ((caseSensitive == null || caseSensitive) && (ref.equals(linkRefExt) || ref.equals(linkRefWithAnchorExt))) {
                        newList.add(fileReferenceLink);
                    } else if ((caseSensitive != null && !caseSensitive) && (ref.equalsIgnoreCase(linkRefExt) || ref.equalsIgnoreCase(linkRefWithAnchorExt))) {
                        newList.add(fileReferenceLink);
                    }
                } else {
                    // default not case sensitive
                    if (isWikiLink) {
                        if (!haveSubDir) {
                            String wikiLinkRef = fileReferenceLink.getWikiPageRefWithAnchor();

                            if (caseSensitive != null && caseSensitive && (wikiLinkRef.equals(wikiLinkRefExt) || wikiLinkRef.equals(wikiLinkRefWithAnchorExt))) {
                                newList.add(fileReferenceLink);
                            } else if ((caseSensitive == null || !caseSensitive) && (wikiLinkRef.equalsIgnoreCase(wikiLinkRefExt) || wikiLinkRef.equalsIgnoreCase(wikiLinkRefWithAnchorExt))) {
                                newList.add(fileReferenceLink);
                            }
                        }
                    } else {
                        // Here we may need the source to know if the wikipage is accessible with no subdir prefix or needs one
                        // if extension is given then it will resolve to raw text not markdown and subdirectories must be specified
                        // here we need to test for either doKeepLineRefExt or !haveSubDir or the sub dir in link ref is a GitHub wiki home dir otherwise the link won't resolve on GitHub
                        if (!haveSubDir || doKeepLinkRefExt || isWikiSubDir) {
                            String ref;

                            if (fileReferenceLink.getSourceReference().isWikiPage() && (!haveSubDir && !doKeepLinkRefExt)) {
                                ref = fileReferenceLink.getNoPrefixLinkRefWithAnchorNoExt();
                            } else {
                                if (haveSubDir && !doKeepLinkRefExt) {
                                    // need to remove sub directory between wiki home prefix and the file name, since file is referenced with no extension it resolved by name only
                                    String refName = fileReferenceLink.getFileNameWithAnchorNoExt();
                                    ref = fileReferenceLink.getLinkRefWithAnchorNoExt();

                                    while (!ref.endsWith("/" + FilePathInfo.GITHUB_WIKI_HOME_DIRNAME + "/")) {
                                        int pos = ref.lastIndexOf('/', ref.length()-2);
                                        if (pos <= 0) break;
                                        ref = ref.substring(0, pos + 1);
                                    }
                                    ref += refName;
                                } else {
                                    ref = doKeepLinkRefExt ? fileReferenceLink.getLinkRefWithAnchor() : fileReferenceLink.getLinkRefWithAnchorNoExt();
                                }
                            }

                            if (caseSensitive != null && caseSensitive && (ref.equals(linkRefExt) || ref.equals(linkRefWithAnchorExt))) {
                                newList.add(fileReferenceLink);
                            } else if ((caseSensitive == null || !caseSensitive) && (ref.equalsIgnoreCase(linkRefExt) || ref.equalsIgnoreCase(linkRefWithAnchorExt))) {
                                newList.add(fileReferenceLink);
                            } else if (fileReferenceLink.isWikiPageHome()) {
                                if (caseSensitive != null && caseSensitive && (ref.equals(linkRefExt) || ref.equals(FilePathInfo.endWith(linkRefWithAnchorExt, '/') + FilePathInfo.GITHUB_WIKI_HOME_FILENAME))) {
                                    newList.add(fileReferenceLink);
                                } else if ((caseSensitive == null || !caseSensitive) && (ref.equalsIgnoreCase(linkRefExt) || ref.equalsIgnoreCase(FilePathInfo.endWith(linkRefWithAnchorExt, '/') + FilePathInfo.GITHUB_WIKI_HOME_FILENAME))) {
                                    newList.add(fileReferenceLink);
                                }
                            }
                        }
                    }
                }
            }

            return newList.size() != fileReferenceList.size() ? new FileReferenceList(newList) : fileReferenceList;
        }
    };

    @NotNull
    public FileReferenceList postMatchFilter(@NotNull FilePathInfo linkRefInfo, boolean isWikiLink, @Nullable Boolean caseSensitive, @Nullable Boolean keepWikiExt) {
        return postMatchFilter.match(this, linkRefInfo, isWikiLink, caseSensitive, keepWikiExt);
    }

    @NotNull
    public FileReferenceList postMatchFilter(@NotNull String linkRefInfo, boolean isWikiLink, @Nullable Boolean caseSensitive, @Nullable Boolean keepWikiExt) {
        return postMatchFilter(new FilePathInfo(linkRefInfo), isWikiLink, caseSensitive, keepWikiExt);
    }

    @NotNull
    public FileReferenceList postMatchFilter(@NotNull FilePathInfo linkRefInfo, boolean isWikiLink, @Nullable Boolean caseSensitive) {
        return postMatchFilter(new FilePathInfo(linkRefInfo), isWikiLink, caseSensitive, null);
    }

    @NotNull
    public FileReferenceList postMatchFilter(@NotNull String linkRefInfo, boolean isWikiLink, @Nullable Boolean caseSensitive) {
        return postMatchFilter(new FilePathInfo(linkRefInfo), isWikiLink, caseSensitive, null);
    }

    @NotNull
    public FileReferenceList postMatchFilter(@NotNull FilePathInfo linkRefInfo, boolean isWikiLink) {
        return postMatchFilter(new FilePathInfo(linkRefInfo), isWikiLink, null, null);
    }

    @NotNull
    public FileReferenceList postMatchFilter(@NotNull String linkRefInfo, boolean isWikiLink) {
        return postMatchFilter(new FilePathInfo(linkRefInfo), isWikiLink, null, null);
    }

    @Override
    public String toString() {
        return "FileReferenceList: [" + fileReferences.length + "]" + (fileReferences.length > 0 ? fileReferences[0].toString() : "");
    }

    public interface TransformFilter<T> extends Filter {
        @Nullable
        T transformRef(@NotNull FileReference fileReference);

        T[] getResult(Collection<T> result);
    }

    public static class Builder {
        ArrayList<FileReference> fileReferences = new ArrayList<FileReference>();
        HashSet<String> fileReferenceFilePath = new HashSet<String>();
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

        public Builder(@NotNull Collection fileReferences, @Nullable Project project, FileReferenceList.Filter... filters) {
            FileReferenceList.Filter firstFilter = filters.length > 0 ? filters[0] : null;
            ArrayList<FileReference> files = new ArrayList<FileReference>();

            for (Object o : fileReferences) {
                FileReference fileReference = null;
                if (o instanceof VirtualFile) {
                    fileReference = new FileReference((VirtualFile) o, project);
                } else if (o instanceof FileReference) {
                    if (project == null) project = ((FileReference) o).getProject();
                    fileReference = (FileReference) o;
                } else if (o instanceof String) {
                    fileReference = new FileReference((String) o, project);
                } else if (o instanceof PsiFile) {
                    if (project == null) project = ((PsiFile) o).getProject();
                    fileReference = new FileReference((PsiFile) o);
                } else {
                    throw new InvalidParameterException("Collection can only contain String, FileReference, VirtualFile or PsiFile elements");
                }

                if (firstFilter == null || firstFilter.filterExt(fileReference.getExt(), fileReference.getAnchor())) {
                    files.add(fileReference);
                }
            }

            for (FileReferenceList.Filter filter : filters) {
                if (filter == null) continue;

                ArrayList<FileReference> nextFiles = new ArrayList<FileReference>(files.size());

                for (FileReference fileReference : files) {
                    if (!filter.filterExt(fileReference.getExt(), fileReference.getFullFilePath())) continue;
                    if (filter.isRefFilter() && (fileReference = filter.filterRef(fileReference)) == null) continue;
                    nextFiles.add(fileReference);
                }

                files = nextFiles;
            }

            for (FileReference fileReference : files) {
                add(fileReference);
            }
        }

        public void add(@NotNull FileReference fileReference) {
            if (!fileReferenceFilePath.contains(fileReference.getFilePathWithAnchor())) {
                fileReferenceFilePath.add(fileReference.getFilePathWithAnchor());

                String ext = fileReference.getExt();
                String withAnchorExt = fileReference.getWithAnchorExt();
                if (!extensions.contains(ext)) {
                    extensions.add(ext);
                    extensionFileReferences.put(ext, new ArrayList<Integer>(1));
                }
                extensionFileReferences.get(ext).add(fileReferences.size());

                if (!ext.equals(withAnchorExt)) {
                    if (!extensions.contains(withAnchorExt)) {
                        extensions.add(withAnchorExt);
                        extensionFileReferences.put(withAnchorExt, new ArrayList<Integer>(1));
                    }
                    //logger.info("adding withAnchorExt " + withAnchorExt + " for file " + fileReference.getFileNameWithAnchor());
                    extensionFileReferences.get(withAnchorExt).add(fileReferences.size());
                }

                fileReferences.add(fileReference);
            }
        }
    }

    public FileReference[] getFileReferences() {
        return fileReferences;
    }

    protected final FileReference[] fileReferences;
    protected final int[][] extensionFileRefIndices; // [ext index][0....max] = index in fileReferences that contains that extension
    protected final String[] extensions;      //

    public FileReferenceList sorted() {
        return new FileReferenceList(getSorted());
    }

    public FileReferenceList sorted(int skip) {
        return new FileReferenceList(getSorted(skip));
    }

    public FileReferenceList sorted(int skip, int get) {
        return new FileReferenceList(getSorted(skip, get));
    }

    public FileReferenceList copy() {
        return new FileReferenceList(get());
    }

    public FileReferenceList copy(int skip) {
        return new FileReferenceList(get(skip));
    }

    public FileReferenceList copy(int skip, int get) {
        return new FileReferenceList(get(skip, get));
    }

    public int size() {
        return fileReferences.length;
    }

    public FileReference getAt(int i) {
        return fileReferences.length > i ? fileReferences[i] : null;
    }

    @NotNull
    public FileReference[] get() {
        return get(0, size());
    }

    @NotNull
    public FileReference[] get(int skip) {
        return get(skip, size());
    }

    @NotNull
    public FileReference[] get(int skip, int get) {
        if (skip >= size()) {
            return new FileReference[0];
        }
        if (get > size() - skip) get = size() - skip;
        FileReference[] results = new FileReference[get];
        System.arraycopy(fileReferences, skip, results, 0, get);
        return results;
    }

    @NotNull
    public FileReference[] getSorted() {
        return getSorted(0, size());
    }

    @NotNull
    public FileReference[] getSorted(int skip) {
        return getSorted(skip, size());
    }

    @NotNull
    public FileReference[] getSorted(int skip, int get) {
        if (skip >= size()) {
            return new FileReference[0];
        }
        FileReference[] sorted = fileReferences.clone();
        Arrays.sort(sorted);
        if (get > size() - skip) get = size() - skip;
        FileReference[] results = new FileReference[get];
        System.arraycopy(sorted, skip, results, 0, get);
        return results;
    }

    @NotNull
    public int[][] getExtensionFileRefIndices() {
        return extensionFileRefIndices;
    }

    @NotNull
    public String[] getExtensions() {
        return extensions;
    }

    public FileReferenceList(@NotNull Collection c) {
        this(new Builder(c, null));
    }

    public FileReferenceList(@NotNull Collection c, Project project) {
        this(new Builder(c, project));
    }

    public FileReferenceList(FileReference... fileReferences) {
        this(new Builder(fileReferences));
    }

    public FileReferenceList(@NotNull Builder builder) {
        fileReferences = builder.getFileReferences();
        extensions = builder.getExtensions();
        extensionFileRefIndices = builder.getExtensionFileReferences();
    }

    public FileReferenceList(boolean firstMatchOnly, @NotNull Filter[] filters, @NotNull FileReference[]... fileReferencesArray) {
        Builder builder = new Builder();

        for (FileReference[] fileReferences : fileReferencesArray) {

            Outer:
            for (FileReference fileReference : fileReferences) {
                if (fileReference == null) continue;

                for (Filter filter : filters) {
                    if (filter == null) continue;
                    if (!filter.filterExt(fileReference.getExt(), fileReference.getAnchor())) continue Outer;

                    if (!filter.isRefFilter()) continue;
                    fileReference = filter.filterRef(fileReference);
                    if (fileReference == null) continue Outer;
                }

                builder.add(fileReference);
                if (firstMatchOnly) break;
            }
        }

        fileReferences = builder.getFileReferences();
        extensions = builder.getExtensions();
        extensionFileRefIndices = builder.getExtensionFileReferences();
    }

    // this is a union and filter constructor
    public FileReferenceList(boolean firstMatchOnly, @NotNull Filter[] filters, FileReferenceList... fileReferenceLists) {
        Builder builder = new Builder();

        for (FileReferenceList fileReferenceList : fileReferenceLists) {
            int extIndex = -1;

            Outer_Ext:
            for (String ext : fileReferenceList.extensions) {
                extIndex++;

                for (Filter filter : filters) {
                    if (filter != null && !filter.filterExt(ext, "")) continue Outer_Ext;
                }

                Outer:
                for (int extFileIndex : fileReferenceList.extensionFileRefIndices[extIndex]) {
                    FileReference addReference = fileReferenceList.fileReferences[extFileIndex];

                    if (addReference == null) continue;

                    for (Filter filter : filters) {
                        if (filter == null || !filter.isRefFilter()) continue;
                        addReference = filter.filterRef(addReference);
                        if (addReference == null) continue Outer;
                    }

                    builder.add(addReference);
                    if (firstMatchOnly) break;
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
        this(false, filters, new FileReference[][] { fileReferences });
    }

    public FileReferenceList(@NotNull FileReferenceList other, Filter... filters) {
        this(filters, other.fileReferences);
    }

    public FileReferenceList(boolean firstMatchOnly, @NotNull FileReferenceList other, Filter... filters) {
        this(firstMatchOnly, filters, other.fileReferences);
    }

    public FileReferenceList(@NotNull FileReferenceList other) {
        fileReferences = other.fileReferences;
        extensions = other.extensions;
        extensionFileRefIndices = other.extensionFileRefIndices;
    }

    @NotNull
    public FileReferenceListQuery query() {
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
    public FileReferenceList filter(boolean firstMatchOnly, Filter... filters) {
        return new FileReferenceList(firstMatchOnly, this, filters);
    }

    @NotNull
    public FileReferenceList markdownFileRefs() {
        return filter(MARKDOWN_FILE_FILTER);
    }

    @NotNull
    public FileReferenceList imageFileRefs() {
        return filter(IMAGE_FILE_FILTER);
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
    public FileReferenceList sameWikiHomePageRefs() {
        return filter(SAME_WIKI_HOME_REFS_FILTER);
    }

    @NotNull
    public FileReferenceList pathStartsWith(@NotNull String pathPrefix) {
        final String pathPrefixSlash = FilePathInfo.endWith(pathPrefix, '/');

        return filter(new Filter() {
            @Override
            public boolean filterExt(@NotNull String ext, String anchor) {
                return true;
            }

            @Override
            public boolean isRefFilter() {
                return true;
            }

            @Nullable
            @Override
            public FileReference filterRef(@NotNull FileReference fileReference) {
                return fileReference.getFullFilePath().startsWith(pathPrefixSlash) ? fileReference : null;
            }
        });
    }

    @NotNull
    public <T> T[] transform(@NotNull TransformFilter<T> transFilter) {
        ArrayList<T> result = new ArrayList<T>(fileReferences.length);

        for (FileReference fileReference : fileReferences) {
            FileReference addReference = fileReference;
            if (addReference == null) continue;

            if (transFilter.isRefFilter()) {
                addReference = transFilter.filterRef(addReference);
                if (addReference == null) continue;
            }

            T transformed = transFilter.transformRef(addReference);
            if (transformed == null) continue;

            result.add(transformed);
        }

        return transFilter.getResult(result);
    }

    @NotNull
    public VirtualFile[] getVirtualFiles() {
        return transform(VIRTUAL_FILE_TRANSFILTER);
    }

    @NotNull
    public VirtualFile[] getVirtualFilesWithAnchor() {
        return transform(VIRTUAL_FILE_WITH_ANCHOR_TRANSFILTER);
    }

    @NotNull
    public PsiFile[] getPsiFiles() {
        return transform(PSI_FILE_TRANSFILTER);
    }

    @NotNull
    public PsiFile[] getPsiFilesWithAnchor() {
        return transform(PSI_FILE_WITH_ANCHOR_TRANSFILTER);
    }

    @NotNull
    public MultiMarkdownFile[] getMarkdownFiles() {
        return transform(MARKDOWN_FILE_TRANSFILTER);
    }

    @NotNull
    public MultiMarkdownFile[] getMarkdownFilesWithAnchor() {
        return transform(MARKDOWN_FILE_WITH_ANCHOR_TRANSFILTER);
    }

    @NotNull
    public MultiMarkdownFile[] getAllWikiPageFiles() {
        return transform(ALL_WIKIPAGE_FILE_TRANSFILTER);
    }

    @NotNull
    public MultiMarkdownFile[] getAllWikiPageFilesWithAnchor() {
        return transform(ALL_WIKIPAGE_FILE_WITH_ANCHOR_TRANSFILTER);
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
    public MultiMarkdownFile[] getInaccessibleWikiPageFilesWithAnchor() {
        return transform(INACCESSIBLE_WIKIPAGE_FILE_WITH_ANCHOR_TRANSFILTER);
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
    public String[] getAllLinkRefStrings(boolean withExt) {
        return transform(withExt ? ALL_LINK_REFS_WITH_EXT_TRANSFILTER : ALL_LINK_REFS_NO_EXT_TRANSFILTER);
    }

    @NotNull
    public String[] getAllLinkRefNoExtStrings() {
        return transform(ALL_LINK_REFS_NO_EXT_TRANSFILTER);
    }

    @NotNull
    public String[] getAllLinkRefWithExtStrings() {
        return transform(ALL_LINK_REFS_WITH_EXT_TRANSFILTER);
    }

    @NotNull
    public int[] countByFilter(Filter... filters) {
        int[] result = new int[filters.length];
        int extIndex = 0;
        for (String ext : extensions) {
            int[] extFiles = extensionFileRefIndices[extIndex];
            int filterIndex = 0;
            for (Filter filter : filters) {
                if (filter != null && filter.filterExt(ext, "")) {
                    if (filter.isRefFilter()) {
                        int keptFiles = 0;
                        for (int refIndex : extFiles) {
                            if (fileReferences[refIndex] != null && filter.filterRef(fileReferences[refIndex]) != null) keptFiles++;
                        }
                        result[filterIndex] += keptFiles;
                    } else {
                        result[filterIndex] += extFiles.length;
                    }
                }
                filterIndex++;
            }

            extIndex++;
        }
        return result;
    }

    public static final Filter ALL_WIKI_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }
    };

    public static final Filter SAME_WIKI_HOME_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isWikiPageExt(ext);
        }

        @Override
        public boolean isRefFilter() { return true; }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            boolean b = fileReference instanceof FileReferenceLink;
            String targetWikiHome = fileReference.getWikiHome();
            String sourceWikiHome = b ? ((FileReferenceLink) fileReference).getSourceReference().getWikiHome() : "";
            return b && targetWikiHome.equals(sourceWikiHome) ? fileReference : null;
        }
    };

    public static final Filter ACCESSIBLE_WIKI_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isWikiPageExt(ext);
        }

        @Override
        public boolean isRefFilter() { return true; }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference instanceof FileReferenceLink && ((FileReferenceLink) fileReference).isWikiAccessible() ? fileReference : null;
        }
    };

    public static final Filter INACCESSIBLE_WIKI_REFS_FILTER = new Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return true;
        }

        @Override
        public boolean isRefFilter() { return true; }

        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return !(fileReference instanceof FileReferenceLink) || !((FileReferenceLink) fileReference).isWikiAccessible() ? fileReference : null;
        }
    };

    public static final Filter IMAGE_FILE_FILTER = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isImageExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }
    };

    public static final Filter IMAGE_FILE_FILTER_WITH_ANCHOR = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return ext.isEmpty() && !anchor.isEmpty() ? FilePathInfo.isImageExt(new FilePathInfo(anchor).getWithAnchorExt()) : FilePathInfo.isImageExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }
    };

    public static final Filter MARKDOWN_FILE_FILTER = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }
    };

    public static final Filter MARKDOWN_FILE_FILTER_WITH_ANCHOR = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return ext.isEmpty() && !anchor.isEmpty() ? FilePathInfo.isMarkdownExt(new FilePathInfo(anchor).getWithAnchorExt()) : FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }
    };

    public static final Filter WIKIPAGE_FILE_FILTER = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isWikiPageExt(ext);
        }

        @Override
        public boolean isRefFilter() { return true; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference.isUnderWikiHome() ? fileReference : null;
        }
    };

    public static final Filter WIKIPAGE_FILE_FILTER_WITH_ANCHOR = new FileReferenceList.Filter() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return ext.isEmpty() && !anchor.isEmpty() ? FilePathInfo.isWikiPageExt(new FilePathInfo(anchor).getWithAnchorExt()) : FilePathInfo.isWikiPageExt(ext);
        }

        @Override
        public boolean isRefFilter() { return true; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference.isUnderWikiHome() ? fileReference : null;
        }
    };

    public static final Filter NULL_FILTER = null;

    public static final TransformFilter<VirtualFile> VIRTUAL_FILE_TRANSFILTER = new TransformFilter<VirtualFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return true;
        }

        @Override
        public boolean isRefFilter() { return false; }

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
        public VirtualFile[] getResult(Collection<VirtualFile> result) {
            return result.toArray(new VirtualFile[result.size()]);
        }
    };

    public static final TransformFilter<VirtualFile> VIRTUAL_FILE_WITH_ANCHOR_TRANSFILTER = new TransformFilter<VirtualFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return true;
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public VirtualFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getVirtualFileWithAnchor();
        }

        @Override
        public VirtualFile[] getResult(Collection<VirtualFile> result) {
            return result.toArray(new VirtualFile[result.size()]);
        }
    };

    public static final TransformFilter<PsiFile> PSI_FILE_TRANSFILTER = new TransformFilter<PsiFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return true;
        }

        @Override
        public boolean isRefFilter() { return false; }

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
        public PsiFile[] getResult(Collection<PsiFile> result) {
            return result.toArray(new PsiFile[result.size()]);
        }
    };

    public static final TransformFilter<PsiFile> PSI_FILE_WITH_ANCHOR_TRANSFILTER = new TransformFilter<PsiFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return true;
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public PsiFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getPsiFileWithAnchor();
        }

        @Override
        public PsiFile[] getResult(Collection<PsiFile> result) {
            return result.toArray(new PsiFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> MARKDOWN_FILE_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

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
        public MultiMarkdownFile[] getResult(Collection<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> MARKDOWN_FILE_WITH_ANCHOR_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public MultiMarkdownFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getMultiMarkdownFileWithAnchor();
        }

        @Override
        public MultiMarkdownFile[] getResult(Collection<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> ALL_WIKIPAGE_FILE_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

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
        public MultiMarkdownFile[] getResult(Collection<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> ALL_WIKIPAGE_FILE_WITH_ANCHOR_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public MultiMarkdownFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getMultiMarkdownFileWithAnchor();
        }

        @Override
        public MultiMarkdownFile[] getResult(Collection<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> ACCESSIBLE_WIKIPAGE_FILE_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isWikiPageExt(ext);
        }

        @Override
        public boolean isRefFilter() { return ACCESSIBLE_WIKI_REFS_FILTER.isRefFilter(); }

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
        public MultiMarkdownFile[] getResult(Collection<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> INACCESSIBLE_WIKIPAGE_FILE_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return INACCESSIBLE_WIKI_REFS_FILTER.isRefFilter(); }

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
        public MultiMarkdownFile[] getResult(Collection<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<MultiMarkdownFile> INACCESSIBLE_WIKIPAGE_FILE_WITH_ANCHOR_TRANSFILTER = new TransformFilter<MultiMarkdownFile>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return INACCESSIBLE_WIKI_REFS_FILTER.isRefFilter(); }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return INACCESSIBLE_WIKI_REFS_FILTER.filterRef(fileReference);
        }

        @Nullable
        @Override
        public MultiMarkdownFile transformRef(@NotNull FileReference fileReference) {
            return fileReference.getMultiMarkdownFileWithAnchor();
        }

        @Override
        public MultiMarkdownFile[] getResult(Collection<MultiMarkdownFile> result) {
            return result.toArray(new MultiMarkdownFile[result.size()]);
        }
    };

    public static final TransformFilter<String> ALL_WIKIPAGE_REFS_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

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
        public String[] getResult(Collection<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final TransformFilter<String> ALL_LINK_REFS_NO_EXT_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public String transformRef(@NotNull FileReference fileReference) {
            return fileReference instanceof FileReferenceLink ? ((FileReferenceLink) fileReference).getLinkRef() : fileReference.getFileNameNoExt();
        }

        @Override
        public String[] getResult(Collection<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final TransformFilter<String> ALL_LINK_REFS_WITH_EXT_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return true;
        }

        @Override
        public boolean isRefFilter() { return false; }

        @Nullable
        @Override
        public FileReference filterRef(@NotNull FileReference fileReference) {
            return fileReference;
        }

        @Nullable
        @Override
        public String transformRef(@NotNull FileReference fileReference) {
            return fileReference instanceof FileReferenceLink ? ((FileReferenceLink) fileReference).getLinkRef() : fileReference.getFileName();
        }

        @Override
        public String[] getResult(Collection<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final TransformFilter<String> ACCESSIBLE_WIKIPAGE_REFS_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isWikiPageExt(ext);
        }

        @Override
        public boolean isRefFilter() { return ACCESSIBLE_WIKI_REFS_FILTER.isRefFilter(); }

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
        public String[] getResult(Collection<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final TransformFilter<String> INACCESSIBLE_WIKIPAGE_REFS_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return FilePathInfo.isMarkdownExt(ext);
        }

        @Override
        public boolean isRefFilter() { return INACCESSIBLE_WIKI_REFS_FILTER.isRefFilter(); }

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
        public String[] getResult(Collection<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final TransformFilter<String> ALL_LINK_REFS_TRANSFILTER = new TransformFilter<String>() {
        @Override
        public boolean filterExt(@NotNull String ext, String anchor) {
            return true;
        }

        @Override
        public boolean isRefFilter() { return false; }

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
        public String[] getResult(Collection<String> result) {
            return result.toArray(new String[result.size()]);
        }
    };

    public static final Filter ANY_FILE_FILTER = NULL_FILTER;
}
