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
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.MultiMarkdownProjectComponent;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class FileReferenceListQuery {
    // types of files to search
    public final static int ANY_FILE = 0x0000;
    public final static int WIKIPAGE_FILE = 0x0001;
    public final static int MARKDOWN_FILE = 0x0002;
    public final static int IMAGE_FILE = 0x0003;

    public final static int FILE_TYPE_FLAGS = 0x000f;

    public final static int INCLUDE_SOURCE = 0x0010;

    // comparison options
    public final static int SPACE_DASH_EQUIVALENT = 0x0020;
    public final static int CASE_INSENSITIVE = 0x0040;

    // what is provided for the match
    public final static int WIKIPAGE_REF = 0x0080;
    public final static int LINK_REF = 0x0100;
    public final static int LINK_WITH_EXT_REF = 0x0180;

    public final static int MATCH_TYPE_FLAGS = LINK_WITH_EXT_REF;

    protected final @NotNull Project project;
    protected int searchFlags;
    protected String matchPattern;
    protected FileReference sourceFileReference;

    public FileReferenceListQuery(@NotNull Project project) {
        this.project = project;
        this.searchFlags = 0;
        this.matchPattern = null;
        this.sourceFileReference = null;
    }

    public FileReferenceListQuery(@NotNull Project project, int searchFlags) {
        this.project = project;
        this.searchFlags = searchFlags;
        this.matchPattern = null;
        this.sourceFileReference = null;
    }

    public FileReferenceListQuery(@NotNull FileReferenceListQuery other) {
        this.project = other.project;
        this.searchFlags = other.searchFlags;
        this.matchPattern = other.matchPattern;
        this.sourceFileReference = other.sourceFileReference;
    }

    @NotNull
    public Project getProject() {
        return project;
    }

    public int getSearchFlags() {
        return searchFlags;
    }

    public String getMatchPattern() {
        return matchPattern;
    }

    public FileReference getSourceFileReference() {
        return sourceFileReference;
    }

    @NotNull
    public FileReferenceListQuery wikiPages() {
        searchFlags = (searchFlags & ~FILE_TYPE_FLAGS) | WIKIPAGE_FILE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery includeSource() {
        searchFlags = (searchFlags & ~FILE_TYPE_FLAGS) | WIKIPAGE_FILE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery markdownFiles() {
        searchFlags = (searchFlags & ~FILE_TYPE_FLAGS) | MARKDOWN_FILE | WIKIPAGE_FILE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery spaceDashEqual() {
        searchFlags |= SPACE_DASH_EQUIVALENT;
        return this;
    }

    @NotNull
    public FileReferenceListQuery spaceDashNotEqual() {
        searchFlags &= ~SPACE_DASH_EQUIVALENT;
        return this;
    }

    @NotNull
    public FileReferenceListQuery caseInsensitive() {
        searchFlags |= CASE_INSENSITIVE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery caseSensitive() {
        searchFlags &= ~CASE_INSENSITIVE;
        return this;
    }

    @NotNull
    public FileReferenceListQuery forAnyRef() {
        this.matchPattern = null;
        searchFlags &= ~MATCH_TYPE_FLAGS;
        return this;
    }

    @NotNull
    public FileReferenceListQuery forWikiRef(@NotNull String wikiRef) {
        this.matchPattern = wikiRef;
        searchFlags &= ~MATCH_TYPE_FLAGS;
        return this;
    }

    @NotNull
    public FileReferenceListQuery forLinkRef(@NotNull String linkRef, boolean withExt) {
        this.matchPattern = linkRef;
        searchFlags = (searchFlags & ~MATCH_TYPE_FLAGS) | LINK_REF | (withExt ? LINK_WITH_EXT_REF : 0);
        return this;
    }

    @NotNull
    public FileReferenceListQuery forLinkRef(@NotNull String linkRef) {
        return forLinkRef(linkRef, false);
    }

    @NotNull
    public FileReferenceListQuery forLinkRefWithExt(@NotNull String linkRef) {
        return forLinkRef(linkRef, true);
    }

    @NotNull
    public FileReferenceListQuery forSource(@NotNull FileReference sourceFileReference) {
        this.sourceFileReference = sourceFileReference;
        return this;
    }

    @NotNull
    public FileReferenceListQuery forSource(@NotNull MultiMarkdownFile sourceMarkdownFile) {
        this.sourceFileReference = new FileReference(sourceMarkdownFile.getVirtualFile(), project);
        return this;
    }

    @NotNull
    public FileReferenceListQuery forAnySource() {
        this.sourceFileReference = null;
        return this;
    }

    interface QueryFilter {
        @Nullable
        FileReference filter(@NotNull FileReference fileReference);
    }

    static class QueryResults {
        protected final FileReference[] results;

        public FileReference[] getResults() {
            return results.clone();
        }

        protected QueryResults(Collection<? extends FileReference> c) {
            this.results = c.toArray(new FileReference[c.size()]);
        }

        protected QueryResults(FileReference... fileReferences) {
            this.results = fileReferences.clone();
        }

        protected QueryResults(@Nullable QueryFilter filter, @NotNull FileReference... fileReferences) {
            ArrayList<FileReference> results = new ArrayList<FileReference>(fileReferences.length);

            for (FileReference fileReference : fileReferences) {
                FileReference addReference = filter == null || fileReference == null ? fileReference : filter.filter(fileReference);
                if (addReference != null) results.add(addReference);
            }
            this.results = results.toArray(new FileReference[results.size()]);
        }

        public QueryResults(@NotNull QueryFilter[] filters, @NotNull FileReference... fileReferences) {
            ArrayList<FileReference> results = new ArrayList<FileReference>(fileReferences.length);

            Outer:
            for (FileReference fileReference : fileReferences) {
                FileReference addReference = fileReference;

                if (addReference == null) continue;

                for (QueryFilter filter : filters) {
                    addReference = filter.filter(fileReference);
                    if (addReference == null) continue Outer;
                }

                results.add(addReference);
            }

            this.results = results.toArray(new FileReference[results.size()]);
        }

        public QueryResults(@NotNull QueryFilter[] filters, @NotNull FileReference[]... fileReferenceLists) {
            ArrayList<FileReference> results = new ArrayList<FileReference>();

            for (FileReference[] fileReferences : fileReferenceLists) {

                Outer:
                for (FileReference fileReference : fileReferences) {
                    FileReference addReference = fileReference;

                    if (addReference == null) continue;

                    for (QueryFilter filter : filters) {
                        addReference = filter.filter(fileReference);
                        if (addReference == null) continue Outer;
                    }

                    results.add(addReference);
                }
            }

            this.results = results.toArray(new FileReference[results.size()]);
        }

        public QueryResults(@NotNull QueryResults other, QueryFilter... filters) {
            this(filters, other.results);
        }

        public QueryResults(@NotNull QueryFilter[] filters, @NotNull QueryResults... queryResultLists) {
            ArrayList<FileReference> results = new ArrayList<FileReference>();

            for (QueryResults queryResults : queryResultLists) {

                Outer:
                for (FileReference fileReference : queryResults.results) {
                    FileReference addReference = fileReference;

                    if (addReference == null) continue;

                    for (QueryFilter filter : filters) {
                        addReference = filter.filter(fileReference);
                        if (addReference == null) continue Outer;
                    }

                    results.add(addReference);
                }
            }

            this.results = results.toArray(new FileReference[results.size()]);
        }
    }

    @NotNull
    protected QueryResults buildResults(QueryFilter... postFilters) {
        FileReference[] fileList = getFileReferences(project, searchFlags);
        int iMax = postFilters.length;
        QueryFilter[] filters = new QueryFilter[iMax+1];
        filters[0] = getQueryFilter();
        if (iMax > 0) System.arraycopy(postFilters, 0, filters, 1, iMax);
        return new QueryResults(filters, fileList);
    }

    @Nullable
    public QueryFilter getQueryFilter() {
        return getQueryFilter(sourceFileReference, matchPattern, searchFlags);
    }

    @NotNull
    public QueryResults getResults() {
        return buildResults();
    }

    @NotNull
    public QueryResults getResults(QueryFilter... queryFilters) {
        return buildResults(queryFilters);
    }

    @NotNull
    public QueryResults wikiPageRefs(boolean accessibleRefs, boolean inaccessibleRefs) {
    }

    @NotNull
    public QueryResults accessibleWikiPageRefs() {
        return wikiPageRefs(true, false);
    }

    @NotNull
    public QueryResults inaccessibleWikiPageRefs() {
        return wikiPageRefs(false, true);
    }

    @NotNull
    public QueryResults allWikiPageRefs() {
        return wikiPageRefs(true, true);
    }

    // Implementation details for queries and lists
    protected static boolean compare(int searchFlags, int i, int iMax, @NotNull String param, int paramOffs, @NotNull String tail, int tailOffs) {
        for (; i < iMax; i++) {
            char tC = tail.charAt(i + tailOffs);
            char pC = param.charAt(i + paramOffs);
            if (tC == pC) continue;
            if ((searchFlags & CASE_INSENSITIVE) == 0 || Character.toLowerCase(pC) != Character.toLowerCase(tC)) return false;
            if ((searchFlags & SPACE_DASH_EQUIVALENT) == 0 || !((pC == ' ' || pC == '-') && (tC == ' ' || tC == '-'))) return false;
        }
        return true;
    }

    protected static boolean endsWith(int searchFlags, @NotNull String param, @NotNull String tail) {
        int tailLen = tail.length();
        int paramLen = param.length();
        int paramOffs = paramLen - tailLen;

        return paramLen >= tailLen && compare(searchFlags, 0, tailLen, tail, 0, param, paramOffs);
    }

    protected static boolean equivalent(int searchFlags, @NotNull String param, @NotNull String other) {
        int tailLen = other.length();
        int paramLen = param.length();

        return paramLen == tailLen && compare(searchFlags, 0, tailLen, other, 0, param, 0);
    }

    @Nullable
    protected static QueryFilter getQueryFilter(FileReference sourceFileReference, String matchPattern, int searchFlags) {
        QueryFilter filter;
        if (sourceFileReference == null) {
            // if match then it is the ending of the reference path
            if (matchPattern == null) {
                filter = null;
            } else {
                filter = getMatchAnyFileFilter(matchPattern, searchFlags);
            }
        } else {
            if (matchPattern == null) {
                filter = getAnyFileFilter(sourceFileReference);
            } else {
                filter = getMatchFileFilter();
            }
        }
        return filter;
    }

    @NotNull
    protected static QueryFilter getAnyFileFilter(@NotNull final FileReference sourceFileReference) {
        return new QueryFilter() {
            @Override
            public FileReference filter(@NotNull FileReference fileReference) {
                return new FileReferenceLink(sourceFileReference, fileReference);
            }
        };
    }

    @NotNull
    protected static QueryFilter getMatchAnyFileFilter(@NotNull final String matchPattern, final int searchFlags) {
        QueryFilter filter;
        switch (searchFlags & MATCH_TYPE_FLAGS) {
            case WIKIPAGE_REF:
                filter = new QueryFilter() {
                    @Override
                    public FileReference filter(@NotNull FileReference fileReference) {
                        return endsWith(searchFlags, fileReference.getFilePathNoExtAsWikiRef(),
                                matchPattern.charAt(0) == '/' ? matchPattern : '/' + matchPattern) ? fileReference : null;
                    }
                };
                break;

            case LINK_WITH_EXT_REF:
                filter = new QueryFilter() {
                    @Override
                    public FileReference filter(@NotNull FileReference fileReference) {
                        return endsWith(searchFlags, fileReference.getFilePath(),
                                matchPattern.charAt(0) == '/' ? matchPattern : '/' + matchPattern) ? fileReference : null;
                    }
                };
                break;

            default:
            case LINK_REF:
                filter = new QueryFilter() {
                    @Override
                    public FileReference filter(@NotNull FileReference fileReference) {
                        return endsWith(searchFlags, fileReference.getFilePathNoExt(),
                                matchPattern.charAt(0) == '/' ? matchPattern : '/' + matchPattern) ? fileReference : null;
                    }
                };
                break;
        }
        return filter;
    }

    @NotNull
    protected static QueryFilter getMatchFileFilter(@NotNull final String matchPattern, final int searchFlags, @NotNull final FileReference sourceFileReference) {
        QueryFilter filter;
        switch (searchFlags & MATCH_TYPE_FLAGS) {
            case WIKIPAGE_REF:
                filter = new QueryFilter() {
                    @Override
                    public FileReference filter(@NotNull FileReference fileReference) {
                        FileReferenceLink referenceLink = new FileReferenceLink(sourceFileReference, fileReference);
                        return equivalent(searchFlags, referenceLink.getWikiPageRef(), matchPattern) ? referenceLink : null;
                    }
                };
                break;

            case LINK_WITH_EXT_REF:
                filter = new QueryFilter() {
                    @Override
                    public FileReference filter(@NotNull FileReference fileReference) {
                        FileReferenceLink referenceLink = new FileReferenceLink(sourceFileReference, fileReference);
                        return equivalent(searchFlags, fileReference.getFilePath(), matchPattern) ? referenceLink : null;
                    }
                };
                break;

            default:
            case LINK_REF:
                filter = new QueryFilter() {
                    @Override
                    public FileReference filter(@NotNull FileReference fileReference) {
                        FileReferenceLink referenceLink = new FileReferenceLink(sourceFileReference, fileReference);
                        return equivalent(searchFlags, fileReference.getFilePathNoExt(), matchPattern) ? referenceLink : null;
                    }
                };
                break;
        }
        return filter;
    }

    protected static FileReference[] getFileReferences(@NotNull Project project, int searchFlags) {
        FileReference[] fileList;
        MultiMarkdownProjectComponent projectComponent = MultiMarkdownPlugin.getProjectComponent(project);
        MultiMarkdownProjectComponent.FileList fileLists = projectComponent.getFileList();
        switch (searchFlags & FILE_TYPE_FLAGS) {
            case ANY_FILE:
                fileList = fileLists.getProjectFileRefs();
                break;
            case IMAGE_FILE:
                fileList = fileLists.getImageFileRefs();
                break;
            case MARKDOWN_FILE:
                fileList = fileLists.getMarkdownFileRefs();
                break;
            case WIKIPAGE_FILE:
                fileList = fileLists.getWikiFileRefs();
                break;
            default:
                fileList = new FileReference[0];
        }
        return fileList;
    }
}
