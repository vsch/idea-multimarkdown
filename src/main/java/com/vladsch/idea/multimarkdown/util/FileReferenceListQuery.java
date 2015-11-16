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
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.vladsch.idea.multimarkdown.MultiMarkdownFileType;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownImageLinkRef;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownLinkRef;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownWikiPageRef;
import com.vladsch.idea.multimarkdown.psi.impl.MultiMarkdownPsiImplUtil;
import org.apache.log4j.Logger;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class FileReferenceListQuery {
    private static final Logger logger = org.apache.log4j.Logger.getLogger(FileReferenceListQuery.class);
    // types of files to search
    public final static int ANY_FILE = 0x0000;
    public final static int IMAGE_FILE = 0x0001;
    public final static int WIKIPAGE_FILE = 0x0002;
    public final static int MARKDOWN_FILE = 0x0003;

    public final static int FILE_TYPE_FLAGS = 0x000f;

    public final static int EXCLUDE_SOURCE = 0x0010;

    // comparison options
    public final static int SPACE_DASH_EQUIVALENT = 0x0020;
    public final static int CASE_INSENSITIVE = 0x0040;

    // what is provided for the match
    public final static int LINK_WITH_EXT_REF = 0x0080;
    public final static int WIKIPAGE_REF = 0x0100;
    public final static int LINK_REF_NO_EXT = 0x0200;

    public final static int MATCH_TYPE_FLAGS = LINK_REF_NO_EXT | WIKIPAGE_REF | LINK_WITH_EXT_REF;

    // don't strip # from links so that files with # can be matched
    public final static int MATCH_WITH_ANCHOR = 0x0400;
    public final static int WIKIPAGE_GITHUB_RULES = 0x0800;
    public final static int LINK_REF_SAME_REPO = 0x1000;
    public final static int LINK_REF_IGNORE_EXT = 0x2000;
    public final static int MATCH_IGNORE_SUBDIRS = 0x4000;
    public final static int WANT_FIRST_MATCH = 0x8000;

    protected @Nullable FileReferenceList defaultFileList;
    protected int queryFlags;
    protected String matchLinkRef;
    protected FileReference sourceReference;
    protected Project project = null;

    public FileReferenceListQuery(@NotNull FileReferenceList defaultFileList) {
        this.defaultFileList = defaultFileList;
        this.queryFlags = CASE_INSENSITIVE;
        this.matchLinkRef = null;
        this.sourceReference = null;
    }

    public FileReferenceListQuery(@NotNull FileReferenceList defaultFileList, int queryFlags) {
        this.defaultFileList = defaultFileList;
        this.queryFlags = queryFlags;
        this.matchLinkRef = null;
        this.sourceReference = null;
    }

    public FileReferenceListQuery(@NotNull FileReferenceListQuery other) {
        this.defaultFileList = other.defaultFileList;
        this.queryFlags = other.queryFlags;
        this.matchLinkRef = other.matchLinkRef;
        this.sourceReference = other.sourceReference;
    }

    public FileReferenceListQuery(@NotNull Collection c) {
        this.defaultFileList = new FileReferenceList(c);
        this.queryFlags = CASE_INSENSITIVE;
        this.matchLinkRef = null;
        this.sourceReference = null;
    }

    public FileReferenceListQuery(@NotNull Project project) {
        this.defaultFileList = null;
        this.queryFlags = CASE_INSENSITIVE;
        this.matchLinkRef = null;
        this.sourceReference = null;
        this.project = project;
    }

    public FileReferenceListQuery(@NotNull Collection c, Project project) {
        this.defaultFileList = new FileReferenceList(c, project);
        this.queryFlags = CASE_INSENSITIVE;
        this.matchLinkRef = null;
        this.sourceReference = null;
    }

    public FileReferenceListQuery(@NotNull Collection c, int queryFlags) {
        this.defaultFileList = new FileReferenceList(c);
        this.queryFlags = queryFlags;
        this.matchLinkRef = null;
        this.sourceReference = null;
    }

    public FileReferenceListQuery(@NotNull Collection c, Project project, int queryFlags) {
        this.defaultFileList = new FileReferenceList(c, project);
        this.queryFlags = queryFlags;
        this.matchLinkRef = null;
        this.sourceReference = null;
    }

    @NotNull
    public FileReferenceList getDefaultFileList(FileReferenceList.Filter... filters) {
        return getDefaultFileList(false, filters);
    }

    @NotNull
    public FileReferenceList getDefaultFileList(boolean firstMatchOnly, FileReferenceList.Filter... filters) {
        if (defaultFileList == null) {
            // build it from project and query flags
            return getProjectFileType(project, queryFlags, filters);
        }

        return filters.length > 0 ? defaultFileList.filter(firstMatchOnly, filters) : defaultFileList;
    }

    public int getQueryFlags() {
        return queryFlags;
    }

    public String getMatchLinkRef() {
        return matchLinkRef;
    }

    public FileReference getSourceReference() {
        return sourceReference;
    }

    @NotNull
    public FileReferenceListQuery wantWikiPages() {
        queryFlags = (queryFlags & ~FILE_TYPE_FLAGS) | WIKIPAGE_FILE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery wantMarkdownFiles() {
        queryFlags = (queryFlags & ~FILE_TYPE_FLAGS) | MARKDOWN_FILE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery wantImageFiles() {
        queryFlags = (queryFlags & ~FILE_TYPE_FLAGS) | IMAGE_FILE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery wantAllFiles() {
        queryFlags = (queryFlags & ~FILE_TYPE_FLAGS);
        return this;
    }

    @NotNull
    public FileReferenceListQuery spaceDashEqual() {
        queryFlags |= SPACE_DASH_EQUIVALENT;
        return this;
    }

    @NotNull
    public FileReferenceListQuery spaceDashNotEqual() {
        queryFlags &= ~SPACE_DASH_EQUIVALENT;
        return this;
    }

    @NotNull
    public FileReferenceListQuery withoutAnchor() {
        queryFlags &= ~MATCH_WITH_ANCHOR;
        return this;
    }

    @NotNull
    public FileReferenceListQuery withAnchor() {
        queryFlags |= MATCH_WITH_ANCHOR;
        return this;
    }

    @NotNull
    public FileReferenceListQuery caseInsensitive() {
        queryFlags |= CASE_INSENSITIVE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery caseSensitive() {
        queryFlags &= ~CASE_INSENSITIVE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery gitHubWikiRules() {
        return gitHubWikiRules(true);
    }

    @NotNull
    public FileReferenceListQuery regularWikiRules() {
        return gitHubWikiRules(false);
    }

    @NotNull
    public FileReferenceListQuery gitHubWikiRules(boolean gitHubRules) {
        queryFlags = (queryFlags & ~WIKIPAGE_GITHUB_RULES) | (gitHubRules ? WIKIPAGE_GITHUB_RULES : 0);
        return this;
    }

    @NotNull
    public FileReferenceListQuery linkRefIgnoreSubDirs() {
        return linkRefIgnoreSubDirs(true);
    }

    @NotNull
    public FileReferenceListQuery linkRefIgnoreSubDirs(boolean gitHubRules) {
        queryFlags = (queryFlags & ~MATCH_IGNORE_SUBDIRS) | (gitHubRules ? MATCH_IGNORE_SUBDIRS : 0);
        return this;
    }

    @NotNull
    public FileReferenceListQuery ignoreLinkRefExtension() {
        return ignoreLinkRefExtension(true);
    }

    @NotNull
    public FileReferenceListQuery ignoreLinkRefExtension(boolean ignoreExtension) {
        queryFlags = (queryFlags & ~LINK_REF_IGNORE_EXT) | (ignoreExtension ? LINK_REF_IGNORE_EXT : 0);
        return this;
    }

    @NotNull
    public FileReferenceListQuery ignoreLinkRefSubDirs() {
        return ignoreLinkRefSubDirs(true);
    }

    @NotNull
    public FileReferenceListQuery ignoreLinkRefSubDirs(boolean ignoreSubDirs) {
        queryFlags = (queryFlags & ~MATCH_IGNORE_SUBDIRS) | (ignoreSubDirs ? LINK_REF_IGNORE_EXT : 0);
        return this;
    }

    @NotNull
    public FileReferenceListQuery keepLinkRefAnchor() {
        queryFlags |= MATCH_WITH_ANCHOR;
        return this;
    }

    @NotNull
    public FileReferenceListQuery removeLinkRefAnchor() {
        queryFlags &= ~MATCH_WITH_ANCHOR;
        return this;
    }

    @NotNull
    public FileReferenceListQuery matchAnyRef() {
        this.matchLinkRef = null;
        queryFlags &= ~MATCH_TYPE_FLAGS;
        return this;
    }

    @NotNull
    public FileReferenceListQuery matchWikiRef(@Nullable String wikiRef) {
        // set wiki page files as default if not markdown or wiki pages are already set
        if ((this.queryFlags & FILE_TYPE_FLAGS) == 0) this.wantWikiPages();
        if (wikiRef != null) {
            if ((queryFlags & WIKIPAGE_GITHUB_RULES) != 0) {
                FilePathInfo pathInfo = new FilePathInfo(wikiRef);
                if ((queryFlags & MATCH_IGNORE_SUBDIRS) != 0) {
                    this.matchLinkRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? pathInfo.getFileNameWithAnchor() : pathInfo.getFileName();
                } else {
                    this.matchLinkRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? pathInfo.getFilePathWithAnchor() : pathInfo.getFilePath();
                }
            } else {
                this.matchLinkRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? wikiRef : FilePathInfo.linkRefNoAnchor(wikiRef);
            }
        }
        queryFlags = (queryFlags & ~MATCH_TYPE_FLAGS) | WIKIPAGE_REF;
        return this;
    }

    @NotNull
    public FileReferenceListQuery matchWikiRef(@NotNull MultiMarkdownWikiPageRef wikiPageRef) {
        return inSource(new FileReference(wikiPageRef.getContainingFile()))
                .matchWikiRef((queryFlags & MATCH_WITH_ANCHOR) != 0 ? MultiMarkdownPsiImplUtil.getLinkRefWithAnchor(wikiPageRef) : wikiPageRef.getName());
    }

    @NotNull
    public FileReferenceListQuery matchLinkRef(@NotNull MultiMarkdownLinkRef linkRef) {
        return inSource(new FileReference(linkRef.getContainingFile()))
                .matchLinkRef((queryFlags & MATCH_WITH_ANCHOR) != 0 ? linkRef.getFileNameWithAnchor() : linkRef.getFileName());
    }

    @NotNull
    public FileReferenceListQuery matchLinkRef(@NotNull MultiMarkdownLinkRef linkRef, boolean withExt) {
        return inSource(new FileReference(linkRef.getContainingFile()))
                .matchLinkRef((queryFlags & MATCH_WITH_ANCHOR) != 0 ? linkRef.getFileNameWithAnchor() : linkRef.getFileName(), withExt);
    }

    @NotNull
    public FileReferenceListQuery matchLinkRef(@NotNull MultiMarkdownImageLinkRef linkRef) {
        return inSource(new FileReference(linkRef.getContainingFile()))
                .matchLinkRef((queryFlags & MATCH_WITH_ANCHOR) != 0 ? linkRef.getFileNameWithAnchor() : linkRef.getFileName(), true);
    }

    @NotNull
    public FileReferenceListQuery matchLinkRefNoExt(@NotNull MultiMarkdownLinkRef linkRef) {
        return inSource(new FileReference(linkRef.getContainingFile()))
                .matchLinkRefNoExt((queryFlags & MATCH_WITH_ANCHOR) != 0 ? linkRef.getFileNameWithAnchor() : linkRef.getFileName());
    }

    @NotNull
    public FileReferenceListQuery matchLinkRef(@NotNull String linkRef, boolean withExt) {
        // it has url encoded #, then remove actual anchor
        if (linkRef.contains("%23")) {
            FilePathInfo linkRefInfo = new FilePathInfo(linkRef);
            linkRef = linkRefInfo.getFilePath().replace("%23", "#");
        }

        if (false && (queryFlags & MATCH_IGNORE_SUBDIRS) != 0) {
            FilePathInfo linkRefInfo = new FilePathInfo(linkRef);
            this.matchLinkRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? linkRefInfo.getFileNameWithAnchor() : linkRefInfo.getFileNameNoExt();
        } else {
            this.matchLinkRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? linkRef : FilePathInfo.linkRefNoAnchor(linkRef);
        }
        queryFlags = (queryFlags & ~MATCH_TYPE_FLAGS) | (withExt ? LINK_WITH_EXT_REF : LINK_REF_NO_EXT);
        return this;
    }

    @NotNull
    public FileReferenceListQuery sameGitHubRepo() {
        queryFlags |= LINK_REF_SAME_REPO;
        return this;
    }

    @NotNull
    public FileReferenceListQuery sameGitHubRepo(boolean sameRepo) {
        if (sameRepo) queryFlags |= LINK_REF_SAME_REPO;
        else queryFlags &= ~LINK_REF_SAME_REPO;
        return this;
    }

    @NotNull
    public FileReferenceListQuery notSameGitHubRepo() {
        queryFlags &= ~LINK_REF_SAME_REPO;
        return this;
    }

    @NotNull
    public FileReferenceListQuery matchLinkRefNoExt(@NotNull String linkRef) {
        return matchLinkRef(linkRef, false);
    }

    @NotNull
    public FileReferenceListQuery matchLinkRefNoExt(@NotNull String href, @NotNull VirtualFile virtualFile, @NotNull Project project) {
        return inSource(virtualFile, project)
                .matchLinkRef(href, false);
    }

    @NotNull
    public FileReferenceListQuery matchLinkRefWithExt(@NotNull String href, @NotNull VirtualFile virtualFile, @NotNull Project project) {
        return inSource(virtualFile, project)
                .matchLinkRef(href, true);
    }

    @NotNull
    public FileReferenceListQuery matchLinkRef(@NotNull String href, @NotNull VirtualFile virtualFile, @NotNull Project project) {
        return inSource(virtualFile, project)
                .matchLinkRef(href, new FilePathInfo(href).hasWithAnchorExtWithDot());
    }

    @NotNull
    public FileReferenceListQuery matchLinkRef(@NotNull String linkRef) {
        FilePathInfo pathInfo = new FilePathInfo(linkRef);
        return matchLinkRef(linkRef, (queryFlags & MATCH_WITH_ANCHOR) != 0 ? pathInfo.hasWithAnchorExtWithDot() : pathInfo.hasExtWithDot());
    }

    @NotNull
    public FileReferenceListQuery inSource(@NotNull FileReference sourceFileReference) {
        // set default file types to wikipage if source is a wikipage
        if ((queryFlags & FILE_TYPE_FLAGS) == 0) queryFlags |= (sourceFileReference.isWikiPage()) ? WIKIPAGE_FILE : MARKDOWN_FILE;
        this.sourceReference = sourceFileReference;
        return this;
    }

    @NotNull
    public FileReferenceListQuery inSource(@NotNull PsiFile sourceMarkdownFile) {
        return inSource(new FileReference(sourceMarkdownFile.getVirtualFile(), sourceMarkdownFile.getProject()));
    }

    @NotNull
    public FileReferenceListQuery inSource(@NotNull VirtualFile virtualFile, @NotNull Project project) {
        return inSource(new FileReference(virtualFile, project));
    }

    @NotNull
    public FileReferenceListQuery inAnySource() {
        this.sourceReference = null;
        return this;
    }

    @NotNull
    public FileReferenceListQuery excludeSource() {
        this.queryFlags |= EXCLUDE_SOURCE;
        return this;
    }

    @NotNull
    protected FileReferenceList buildResults(@Nullable FileReferenceList fileList, FileReferenceList.Filter... postFilters) {
        boolean haveTypeFilter = false;
        boolean haveQueryFilter = false;

        FileReferenceList.Filter queryFilter = getQueryFilter();
        FileReferenceList.Filter typeFilter = getFileTypeFilter(queryFlags);
        int iMax = postFilters.length;
        int additionalFilters = 0;

        for (FileReferenceList.Filter filter : postFilters) {
            if (filter == queryFilter) {
                haveQueryFilter = true;
                if (haveTypeFilter) break;
            }

            if (filter == typeFilter) {
                haveTypeFilter = true;
                if (haveQueryFilter) break;
            }
        }

        if (!haveTypeFilter && typeFilter != null) additionalFilters++;
        if (!haveQueryFilter && queryFilter != null) additionalFilters++;

        FileReferenceList.Filter[] filters = new FileReferenceList.Filter[iMax + additionalFilters];

        int filterIndex = 0;
        if (!haveTypeFilter && typeFilter != null) filters[filterIndex++] = typeFilter;
        if (!haveQueryFilter && queryFilter != null) filters[filterIndex++] = queryFilter;

        if (iMax > 0) System.arraycopy(postFilters, 0, filters, filterIndex, iMax);

        boolean firstMatchOnly = (queryFlags & WANT_FIRST_MATCH) != 0;
        return (fileList == null) ? getDefaultFileList(firstMatchOnly, filters) : new FileReferenceList(firstMatchOnly, filters, fileList);
    }

    protected static FileReferenceList.Filter getFileTypeFilter(int queryFlags) {
        FileReferenceList.Filter filter;

        switch (queryFlags & FILE_TYPE_FLAGS) {
            case IMAGE_FILE:
                filter = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? FileReferenceList.IMAGE_FILE_FILTER_WITH_ANCHOR : FileReferenceList.IMAGE_FILE_FILTER;
                break;

            case MARKDOWN_FILE:
                filter = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? FileReferenceList.MARKDOWN_FILE_FILTER_WITH_ANCHOR : FileReferenceList.MARKDOWN_FILE_FILTER;
                break;

            case WIKIPAGE_FILE:
                filter = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? FileReferenceList.WIKIPAGE_FILE_FILTER_WITH_ANCHOR : FileReferenceList.WIKIPAGE_FILE_FILTER;
                break;

            default:
            case ANY_FILE:
                filter = FileReferenceList.ANY_FILE_FILTER;
                break;
        }
        return filter;
    }

    protected static FileReferenceList getProjectFileType(Project project, int queryFlags, FileReferenceList.Filter... filters) {
        FileReferenceList.Builder builder;

        switch (queryFlags & FILE_TYPE_FLAGS) {
            case WIKIPAGE_FILE:
            case MARKDOWN_FILE:
                builder = new FileReferenceList.Builder(FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, MultiMarkdownFileType.INSTANCE, GlobalSearchScope.allScope(project)), project, filters);
                break;

            case IMAGE_FILE:
                builder = new FileReferenceList.Builder(FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, ImageFileTypeManager.getInstance().getImageFileType(), GlobalSearchScope.allScope(project)), project, filters);
                break;

            case ANY_FILE:
            default:
                builder = new FileReferenceList.Builder(FileBasedIndex.getInstance().getAllKeys(FilenameIndex.NAME, project), project, filters);
                break;
        }

        return new FileReferenceList(builder);
    }

    @Nullable
    public FileReferenceList.Filter getQueryFilter() {
        return getQueryFilter(sourceReference, matchLinkRef, queryFlags);
    }

    @NotNull
    public FileReferenceList all(@NotNull FileReferenceList fileReferenceList, FileReferenceList.Filter... queryFilters) {
        return buildResults(fileReferenceList, queryFilters);
    }

    @NotNull
    public FileReferenceList all() {
        return buildResults(null);
    }

    @NotNull
    public FileReferenceList all(@NotNull FileReferenceList fileReferenceList) {
        return buildResults(fileReferenceList);
    }

    @NotNull
    public FileReferenceList all(FileReferenceList.Filter... queryFilters) {
        return buildResults(null, queryFilters);
    }

    @NotNull
    public FileReferenceListQuery firstOnly() {
        queryFlags |= WANT_FIRST_MATCH;
        return this;
    }

    @NotNull
    public FileReferenceList first() {
        firstOnly();
        return all();
    }

    @NotNull
    public FileReferenceList accessibleWikiPageRefs() {
        return buildResults(null, FileReferenceList.ACCESSIBLE_WIKI_REFS_FILTER);
    }

    @NotNull
    public FileReferenceList inaccessibleWikiPageRefs() {
        return buildResults(null, FileReferenceList.INACCESSIBLE_WIKI_REFS_FILTER);
    }

    @NotNull
    public FileReferenceList allWikiPageRefs() {
        return buildResults(null, FileReferenceList.ALL_WIKI_REFS_FILTER);
    }

    @NotNull
    public FileReferenceList wikiPageRefs(boolean allowInaccessibleRefs) {
        return buildResults(null,
                allowInaccessibleRefs ? FileReferenceList.ALL_WIKI_REFS_FILTER : FileReferenceList.ACCESSIBLE_WIKI_REFS_FILTER);
    }

    @NotNull
    public VirtualFile[] virtualFiles() {
        return buildResults(null).getVirtualFiles();
    }

    @NotNull
    public PsiFile[] psiFiles() {
        return buildResults(null).getPsiFiles();
    }

    @NotNull
    public MultiMarkdownFile[] markdownFiles() {
        if ((queryFlags & FILE_TYPE_FLAGS) == 0) {
            wantMarkdownFiles();
        }
        return buildResults(null).getMarkdownFiles();
    }

    @NotNull
    public MultiMarkdownFile[] wikiPageFiles(boolean allowInaccessiblePages) {
        return allowInaccessiblePages ? allWikiPageFiles() : accessibleWikiPageFiles();
    }

    @NotNull
    public MultiMarkdownFile[] allWikiPageFiles() {
        if ((queryFlags & FILE_TYPE_FLAGS) == 0) {
            wantWikiPages();
        }
        return buildResults(null).getAllWikiPageFiles();
    }

    @NotNull
    public MultiMarkdownFile[] accessibleWikiPageFiles() {
        if ((queryFlags & FILE_TYPE_FLAGS) == 0) {
            wantWikiPages();
        }
        return buildResults(null).getAccessibleWikiPageFiles();
    }

    @NotNull
    public MultiMarkdownFile[] inaccessibleWikiPageFiles() {
        if ((queryFlags & FILE_TYPE_FLAGS) == 0) {
            wantWikiPages();
        }
        return buildResults(null).getInaccessibleWikiPageFiles();
    }

    // Implementation details for queries and lists
    protected static boolean endsWith(int queryFlags, @NotNull String fileRef, @NotNull String wikiRef) {
        return FilePathInfo.endsWith((queryFlags & CASE_INSENSITIVE) == 0, (queryFlags & SPACE_DASH_EQUIVALENT) != 0, fileRef, wikiRef);
    }

    protected static boolean equivalent(int queryFlags, @NotNull String fileRef, @NotNull String wikiRef) {
        return FilePathInfo.equivalent((queryFlags & CASE_INSENSITIVE) == 0, (queryFlags & SPACE_DASH_EQUIVALENT) != 0, fileRef, wikiRef);
    }

    protected static boolean endsWithWikiRef(int queryFlags, @NotNull String fileRef, @NotNull String wikiRef) {
        return FilePathInfo.endsWithWikiRef((queryFlags & CASE_INSENSITIVE) == 0, (queryFlags & SPACE_DASH_EQUIVALENT) != 0, fileRef, wikiRef);
    }

    protected static boolean equivalentWikiRef(int queryFlags, @NotNull String fileRef, @NotNull String wikiRef) {
        return FilePathInfo.equivalentWikiRef((queryFlags & CASE_INSENSITIVE) == 0, (queryFlags & SPACE_DASH_EQUIVALENT) != 0, fileRef, wikiRef);
    }

    @Nullable
    protected static FileReferenceList.Filter getQueryFilter(FileReference sourceFileReference, String matchPattern, int queryFlags) {
        FileReferenceList.Filter filter;
        if (sourceFileReference == null) {
            // if match then it is the ending of the reference path
            if (matchPattern == null) {
                filter = null;
            } else {
                filter = getMatchAnyFileFilter(matchPattern, queryFlags);
            }
        } else {
            if (matchPattern == null) {
                filter = (queryFlags & WIKIPAGE_GITHUB_RULES) != 0 ? getAnyFileFilterGitHubRules(sourceFileReference) : getAnyFileFilter(sourceFileReference);
            } else {
                filter = getMatchFileFilter(matchPattern, queryFlags, sourceFileReference);
            }
        }
        return filter;
    }

    @NotNull
    protected static FileReferenceList.Filter getAnyFileFilter(@NotNull final FileReference sourceFileReference) {
        return new FileReferenceList.Filter() {
            @Override
            public boolean filterExt(@NotNull String ext, String anchor) {
                return true;
            }

            @Override
            public boolean isRefFilter() {
                return true;
            }

            @Override
            public FileReference filterRef(@NotNull FileReference fileReference) {
                return new FileReferenceLink(sourceFileReference, fileReference);
            }
        };
    }

    @NotNull
    protected static FileReferenceList.Filter getAnyFileFilterGitHubRules(@NotNull final FileReference sourceFileReference) {
        return new FileReferenceList.Filter() {
            @Override
            public boolean filterExt(@NotNull String ext, String anchor) {
                return true;
            }

            @Override
            public boolean isRefFilter() { return true; }
            ;

            @Override
            public FileReference filterRef(@NotNull FileReference fileReference) {
                return new FileReferenceLinkGitHubRules(sourceFileReference, fileReference);
            }
        };
    }

    @NotNull
    protected static FileReferenceList.Filter getMatchAnyFileFilter(@NotNull final String matchPattern, final int queryFlags) {
        FileReferenceList.Filter filter;
        String matchPatternNoExt = null;

        if ((queryFlags & LINK_REF_IGNORE_EXT) != 0) {
            FilePathInfo pathInfo = new FilePathInfo(matchPattern);
            matchPatternNoExt = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? pathInfo.getFilePathWithAnchorNoExt() : pathInfo.getFilePathNoExt();
            if (matchPattern.equals(matchPatternNoExt)) matchPatternNoExt = null;
        }

        final String finalMatchPatternNoExt = matchPatternNoExt;

        switch (queryFlags & MATCH_TYPE_FLAGS) {
            case WIKIPAGE_REF:
                filter = new FileReferenceList.Filter() {
                    @Override
                    public boolean filterExt(@NotNull String ext, String anchor) {
                        return true;
                    }

                    @Override
                    public boolean isRefFilter() {
                        return true;
                    }

                    @Override
                    public FileReference filterRef(@NotNull FileReference fileReference) {
                        String targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? fileReference.getFileNameWithAnchorNoExtAsWikiRef() : fileReference.getFileNameNoExtAsWikiRef();

                        return equivalentWikiRef(queryFlags, targetRef, matchPattern)
                                || (finalMatchPatternNoExt != null && equivalentWikiRef(queryFlags, targetRef, finalMatchPatternNoExt))
                                ? fileReference :
                                null;
                    }
                };
                break;

            case LINK_WITH_EXT_REF:
                filter = new FileReferenceList.Filter() {
                    @Override
                    public boolean filterExt(@NotNull String ext, String anchor) {
                        return true;
                    }

                    @Override
                    public boolean isRefFilter() {
                        return true;
                    }

                    @Override
                    public FileReference filterRef(@NotNull FileReference fileReference) {
                        String targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? fileReference.getFileNameWithAnchor() : fileReference.getFileName();

                        return equivalent(queryFlags, targetRef, matchPattern)
                                ? fileReference :
                                null;
                    }
                };
                break;

            default:
            case LINK_REF_NO_EXT:
                filter = new FileReferenceList.Filter() {
                    @Override
                    public boolean filterExt(@NotNull String ext, String anchor) {
                        return true;
                    }

                    @Override
                    public boolean isRefFilter() {
                        return true;
                    }

                    @Override
                    public FileReference filterRef(@NotNull FileReference fileReference) {
                        String targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? fileReference.getFileNameWithAnchorNoExt() : fileReference.getFileNameNoExt();

                        return equivalent(queryFlags, targetRef, matchPattern)
                                || (finalMatchPatternNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoExt))
                                ? fileReference :
                                null;
                    }
                };
                break;
        }
        return filter;
    }

    @NotNull
    protected static FileReferenceList.Filter getMatchFileFilter(@NotNull final String matchPattern, final int queryFlags, @NotNull final FileReference sourceFileReference) {
        FileReferenceList.Filter filter;
        String githubRepoPath = null;
        MultiMarkdownProjectComponent projComponent = null;

        if ((queryFlags & LINK_REF_SAME_REPO) != 0) {
            projComponent = MultiMarkdownPlugin.getProjectComponent(sourceFileReference.getProject());
            GitHubRepo gitHubRepo = projComponent != null ? projComponent.getGitHubRepo(sourceFileReference.getPath()) : null;
            githubRepoPath = gitHubRepo != null ? FilePathInfo.endWith(gitHubRepo.getBasePath(), '/') : null;
        }

        String matchPatternNoExt = null;
        String matchPatternNoSubDir = null;
        String matchPatternNoSubDirNoExt = null;
        String matchPatternNoAnchor = null;
        String matchPatternNoAnchorNoExt = null;
        FilePathInfo pathInfo = new FilePathInfo(matchPattern);

        if ((queryFlags & LINK_REF_IGNORE_EXT) != 0) {
            matchPatternNoExt = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? pathInfo.getFilePathWithAnchorNoExt() : pathInfo.getFilePathNoExt();
            if (matchPattern.equals(matchPatternNoExt)) matchPatternNoExt = null;
        }

        if ((queryFlags & MATCH_IGNORE_SUBDIRS) != 0) {
            matchPatternNoSubDir = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? pathInfo.getFileNameWithAnchor() : pathInfo.getFileName();
            if (matchPattern.equals(matchPatternNoSubDir)) matchPatternNoSubDir = null;
        }

        if (matchPatternNoExt != null && matchPatternNoSubDir != null) {
            matchPatternNoSubDirNoExt = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? pathInfo.getFileNameWithAnchorNoExt() : pathInfo.getFileNameNoExt();
            if (matchPattern.equals(matchPatternNoSubDirNoExt)) matchPatternNoSubDirNoExt = null;
        }

        // TODO: the meaning of with_anchor should really be TRY_WITH_ANCHOR, so that a match with the linkref's anchor is tried.
        // file paths have no anchors, this crap is really there to serve linkrefs which should be factored out to their own two classes: WikiLink and FileLink
        if ((queryFlags & MATCH_WITH_ANCHOR) != 0) {
            matchPatternNoAnchor = pathInfo.getFilePath();
            if (matchPattern.equals(matchPatternNoAnchor)) matchPatternNoAnchor = null;
            if ((queryFlags & LINK_REF_IGNORE_EXT) != 0) {
                matchPatternNoAnchorNoExt = pathInfo.getFilePathNoExt();
                if (matchPattern.equals(matchPatternNoAnchorNoExt)
                        || (matchPatternNoExt != null && matchPatternNoExt.equals(matchPatternNoAnchorNoExt))
                        || (matchPatternNoAnchor != null && matchPatternNoAnchor.equals(matchPatternNoAnchorNoExt)))
                    matchPatternNoAnchorNoExt = null;
            }
        }

        final String finalMatchPatternNoExt = matchPatternNoExt;
        final String finalMatchPatternNoSubDir = matchPatternNoSubDir;
        final String finalMatchPatternNoSubDirNoExt = matchPatternNoSubDirNoExt;
        final String gitHubRepoPath = githubRepoPath;
        final String finalMatchPatternNoAnchor = matchPatternNoAnchor;
        final String finalMatchPatternNoAnchorNoExt = matchPatternNoAnchorNoExt;
        final MultiMarkdownProjectComponent projectComponent = projComponent;

        switch (queryFlags & MATCH_TYPE_FLAGS) {
            case WIKIPAGE_REF:
                if ((queryFlags & WIKIPAGE_GITHUB_RULES) != 0) {
                    filter = new FileReferenceList.Filter() {
                        @Override
                        public boolean filterExt(@NotNull String ext, String anchor) {
                            return true;
                        }

                        @Override
                        public boolean isRefFilter() {
                            return true;
                        }

                        @Override
                        public FileReference filterRef(@NotNull FileReference fileReference) {
                            FileReferenceLinkGitHubRules referenceLink = new FileReferenceLinkGitHubRules(sourceFileReference, fileReference);
                            String targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getWikiPageRefWithAnchor() : referenceLink.getWikiPageRef();

                            return (gitHubRepoPath == null || fileReference.getPath().startsWith(gitHubRepoPath))
                                    && (equivalentWikiRef(queryFlags, targetRef, matchPattern)
                                    || (finalMatchPatternNoExt != null && equivalentWikiRef(queryFlags, targetRef, finalMatchPatternNoExt))
                                    || (finalMatchPatternNoAnchor != null && equivalentWikiRef(queryFlags, targetRef, finalMatchPatternNoAnchor))
                                    || (finalMatchPatternNoAnchorNoExt != null && equivalentWikiRef(queryFlags, targetRef, finalMatchPatternNoAnchorNoExt)))
                                    && ((queryFlags & EXCLUDE_SOURCE) == 0 || !fileReference.getFilePath().equals(sourceFileReference.getFilePath()))
                                    ? referenceLink : null;
                        }
                    };
                } else {
                    filter = new FileReferenceList.Filter() {
                        @Override
                        public boolean filterExt(@NotNull String ext, String anchor) {
                            return true;
                        }

                        @Override
                        public boolean isRefFilter() {
                            return true;
                        }

                        @Override
                        public FileReference filterRef(@NotNull FileReference fileReference) {
                            FileReferenceLink referenceLink = new FileReferenceLink(sourceFileReference, fileReference);
                            String targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getWikiPageRefWithAnchor() : referenceLink.getWikiPageRef();

                            return (gitHubRepoPath == null || fileReference.getPath().startsWith(gitHubRepoPath))
                                    && (equivalentWikiRef(queryFlags, targetRef, matchPattern)
                                    || (finalMatchPatternNoExt != null && equivalentWikiRef(queryFlags, targetRef, finalMatchPatternNoExt))
                                    || (finalMatchPatternNoAnchor != null && equivalentWikiRef(queryFlags, targetRef, finalMatchPatternNoAnchor))
                                    || (finalMatchPatternNoAnchorNoExt != null && equivalentWikiRef(queryFlags, targetRef, finalMatchPatternNoAnchorNoExt)))
                                    && ((queryFlags & EXCLUDE_SOURCE) == 0 || !fileReference.getFilePath().equals(sourceFileReference.getFilePath()))
                                    ? referenceLink : null;
                        }
                    };
                }

                break;

            default:
            case LINK_WITH_EXT_REF:
                if ((queryFlags & WIKIPAGE_GITHUB_RULES) != 0) {
                    filter = new FileReferenceList.Filter() {
                        @Override
                        public boolean filterExt(@NotNull String ext, String anchor) {
                            return true;
                        }

                        @Override
                        public boolean isRefFilter() {
                            return true;
                        }

                        @Override
                        public FileReference filterRef(@NotNull FileReference fileReference) {
                            FileReferenceLinkGitHubRules referenceLink = new FileReferenceLinkGitHubRules(sourceFileReference, fileReference);
                            String targetRef;
                            String targetRefWithSubDirs;
                            boolean isTargetWikiPage = referenceLink.isWikiPage();

                            //if ((sourceFileReference.isWikiPage() || isTargetWikiPage) && (queryFlags & FILE_TYPE_FLAGS) == MARKDOWN_FILE) {
                            if ((queryFlags & FILE_TYPE_FLAGS) == MARKDOWN_FILE) {
                                // strip off prefix from targetRef
                                targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getNoPrefixLinkRefWithAnchor() : referenceLink.getNoPrefixLinkRef();
                                targetRefWithSubDirs = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getLinkRefWithAnchor() : referenceLink.getLinkRef();
                            } else {
                                targetRefWithSubDirs = targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getLinkRefWithAnchor() : referenceLink.getLinkRef();
                            }

                            return (gitHubRepoPath == null || fileReference.getPath().startsWith(gitHubRepoPath))
                                    && (equivalent(queryFlags, targetRef, matchPattern) || equivalent(queryFlags, targetRefWithSubDirs, matchPattern)
                                    || (referenceLink.isWikiPageHome() && equivalent(queryFlags, targetRefWithSubDirs, FilePathInfo.endWith(matchPattern, '/') + FilePathInfo.GITHUB_WIKI_HOME_FILENAME))
                                    || (finalMatchPatternNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoExt))
                                    || (finalMatchPatternNoAnchor != null && equivalent(queryFlags, targetRef, finalMatchPatternNoAnchor))
                                    || (finalMatchPatternNoAnchorNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoAnchorNoExt))
                                    || (isTargetWikiPage && finalMatchPatternNoSubDir != null && equivalent(queryFlags, targetRef, finalMatchPatternNoSubDir))
                                    || (isTargetWikiPage && finalMatchPatternNoSubDirNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoSubDirNoExt)))
                                    && ((queryFlags & EXCLUDE_SOURCE) == 0 || !fileReference.getFilePath().equals(sourceFileReference.getFilePath()))
                                    ? referenceLink : null;
                        }
                    };
                } else {
                    filter = new FileReferenceList.Filter() {
                        @Override
                        public boolean filterExt(@NotNull String ext, String anchor) {
                            return true;
                        }

                        @Override
                        public boolean isRefFilter() {
                            return true;
                        }

                        @Override
                        public FileReference filterRef(@NotNull FileReference fileReference) {
                            FileReferenceLink referenceLink = new FileReferenceLink(sourceFileReference, fileReference);

                            String targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getLinkRefWithAnchor() : referenceLink.getLinkRef();
                            return (gitHubRepoPath == null || fileReference.getPath().startsWith(gitHubRepoPath))
                                    && (equivalent(queryFlags, targetRef, matchPattern)
                                    || (finalMatchPatternNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoExt))
                                    || (finalMatchPatternNoAnchor != null && equivalent(queryFlags, targetRef, finalMatchPatternNoAnchor))
                                    || (finalMatchPatternNoAnchorNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoAnchorNoExt)))
                                    && ((queryFlags & EXCLUDE_SOURCE) == 0 || !fileReference.getFilePath().equals(sourceFileReference.getFilePath()))
                                    ? referenceLink : null;
                        }
                    };
                }
                break;

            case LINK_REF_NO_EXT:
                if ((queryFlags & WIKIPAGE_GITHUB_RULES) != 0) {
                    filter = new FileReferenceList.Filter() {
                        @Override
                        public boolean filterExt(@NotNull String ext, String anchor) {
                            return true;
                        }

                        @Override
                        public boolean isRefFilter() {
                            return true;
                        }

                        @Override
                        public FileReference filterRef(@NotNull FileReference fileReference) {
                            FileReferenceLinkGitHubRules referenceLink = new FileReferenceLinkGitHubRules(sourceFileReference, fileReference);
                            String targetRef;
                            String targetRefWithSubDirs;
                            boolean isTargetWikiPage = referenceLink.isWikiPage();

                            //if ((sourceFileReference.isWikiPage() || isTargetWikiPage) && (queryFlags & FILE_TYPE_FLAGS) == MARKDOWN_FILE) {
                            if ((queryFlags & FILE_TYPE_FLAGS) == MARKDOWN_FILE) {
                                // strip off prefix from targetRef
                                targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getNoPrefixLinkRefWithAnchorNoExt() : referenceLink.getNoPrefixLinkRefNoExt();
                                targetRefWithSubDirs = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getLinkRefWithAnchorNoExt() : referenceLink.getLinkRefNoExt();
                            } else {
                                targetRefWithSubDirs = targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getLinkRefWithAnchorNoExt() : referenceLink.getLinkRefNoExt();
                            }

                            return (gitHubRepoPath == null || fileReference.getPath().startsWith(gitHubRepoPath))
                                    && (equivalent(queryFlags, targetRef, matchPattern) || equivalent(queryFlags, targetRefWithSubDirs, matchPattern)
                                    || (referenceLink.isWikiPageHome() && equivalent(queryFlags, targetRefWithSubDirs, FilePathInfo.endWith(matchPattern, '/') + FilePathInfo.GITHUB_WIKI_HOME_FILENAME))
                                    || (finalMatchPatternNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoExt))
                                    || (finalMatchPatternNoAnchor != null && equivalent(queryFlags, targetRef, finalMatchPatternNoAnchor))
                                    || (finalMatchPatternNoAnchorNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoAnchorNoExt))
                                    || (isTargetWikiPage && finalMatchPatternNoSubDir != null && equivalent(queryFlags, targetRef, finalMatchPatternNoSubDir)))
                                    && ((queryFlags & EXCLUDE_SOURCE) == 0 || !fileReference.getFilePath().equals(sourceFileReference.getFilePath()))
                                    ? referenceLink : null;
                        }
                    };
                } else {
                    filter = new FileReferenceList.Filter() {
                        @Override
                        public boolean filterExt(@NotNull String ext, String anchor) {
                            return true;
                        }

                        @Override
                        public boolean isRefFilter() {
                            return true;
                        }

                        @Override
                        public FileReference filterRef(@NotNull FileReference fileReference) {
                            FileReferenceLink referenceLink = new FileReferenceLink(sourceFileReference, fileReference);
                            String targetRef = (queryFlags & MATCH_WITH_ANCHOR) != 0 ? referenceLink.getLinkRefWithAnchorNoExt() : referenceLink.getLinkRefNoExt();

                            return (gitHubRepoPath == null || fileReference.getPath().startsWith(gitHubRepoPath))
                                    && (equivalent(queryFlags, targetRef, matchPattern)
                                    || (finalMatchPatternNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoExt))
                                    || (finalMatchPatternNoAnchor != null && equivalent(queryFlags, targetRef, finalMatchPatternNoAnchor))
                                    || (finalMatchPatternNoAnchorNoExt != null && equivalent(queryFlags, targetRef, finalMatchPatternNoAnchorNoExt)))
                                    && ((queryFlags & EXCLUDE_SOURCE) == 0 || !fileReference.getFilePath().equals(sourceFileReference.getFilePath()))
                                    ? referenceLink : null;
                        }
                    };
                }
                break;
        }
        return filter;
    }
}
