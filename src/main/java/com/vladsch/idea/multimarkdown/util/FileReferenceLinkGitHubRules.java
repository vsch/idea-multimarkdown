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
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileReferenceLinkGitHubRules extends FileReferenceLink {
    private static final Logger logger = org.apache.log4j.Logger.getLogger(FileReferenceLinkGitHubRules.class);

    protected String originalPrefix;

    public FileReferenceLinkGitHubRules(@NotNull String sourcePath, @NotNull String targetPath, Project project) {
        super(sourcePath, targetPath, project);
    }
    public FileReferenceLinkGitHubRules(@NotNull FileReference sourceReference, @NotNull FileReference targetReference) {
        super(sourceReference, targetReference);
    }
    public FileReferenceLinkGitHubRules(@NotNull VirtualFile sourceFile, @NotNull FileReference targetReference) {
        super(sourceFile, targetReference);
    }
    public FileReferenceLinkGitHubRules(@NotNull FileReference sourceReference, @NotNull VirtualFile targetFile) {
        super(sourceReference, targetFile);
    }
    public FileReferenceLinkGitHubRules(@NotNull VirtualFile sourceFile, @NotNull VirtualFile targetFile, Project project) {
        super(sourceFile, targetFile, project);
    }
    public FileReferenceLinkGitHubRules(@NotNull PsiFile sourceFile, @NotNull PsiFile targetFile) {
        super(sourceFile, targetFile);
    }
    public FileReferenceLinkGitHubRules(@NotNull FileReference sourceReference, @NotNull PsiFile targetFile) {
        super(sourceReference, targetFile);
    }
    public FileReferenceLinkGitHubRules(@NotNull PsiFile sourceFile, @NotNull FileReference targetReference) {
        super(sourceFile, targetReference);
    }

    @NotNull
    @Override
    protected String getWikiPageRefPathPrefix() {
        // for github wikis this is always "", all wiki page refs are without subdirectories
        return "";
    }

    public static class InaccessibleGitHubWikiPageReasons extends InaccessibleWikiPageReasons {
        InaccessibleGitHubWikiPageReasons(int reasons, String wikiRef, FileReferenceLinkGitHubRules referenceLink) {
            super(reasons, wikiRef, referenceLink);
        }

        @Override
        public boolean wikiRefHasSlash() { return (reasons & REASON_WIKI_PAGEREF_HAS_SLASH) != 0; }

        @Override
        public boolean wikiRefHasFixableSlash() { return (reasons & REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH) != 0; }

        @Override
        public String wikiRefHasSlashFixed() { return wikiRef.replace("/", ""); }

        @Override
        public boolean wikiRefHasSubDir() { return (reasons & REASON_WIKI_PAGEREF_HAS_SUBDIR) != 0; }

        @Override
        public String wikiRefHasSubDirFixed() { return new FilePathInfo(wikiRef).getFileNameWithAnchor(); }
    }

    @NotNull
    @Override
    public InaccessibleWikiPageReasons inaccessibleWikiPageRefReasons(@Nullable String wikiPageRef) {
        int reasons = computeWikiPageRefReasonsFlags(wikiPageRef);

        if (wikiPageRef != null) {
            if (wikiPageRef.contains("/")) {
                // see if it would resolve to the target without it
                FilePathInfo wikiPageRefInfo = new FilePathInfo(wikiPageRef);

                if (equivalentWikiRef(false, false, getWikiPageRef(), wikiPageRefInfo.getFileNameWithAnchor())) {
                    reasons |= REASON_WIKI_PAGEREF_HAS_SUBDIR;
                } else if (equivalentWikiRef(false, false, getWikiPageRef(), wikiPageRef.replace("/", ""))) {
                    reasons |= REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH;
                } else {
                    reasons |= REASON_WIKI_PAGEREF_HAS_SLASH;
                }
            }
        }
        return new InaccessibleGitHubWikiPageReasons(reasons, wikiPageRef, this);
    }

    @Override
    protected int computeWikiPageRefReasonsFlags(@Nullable String wikiPageRef) {
        // add our own
        return super.computeWikiPageRefReasonsFlags(wikiPageRef);
    }

    public static class InaccessibleGitHubLinkRefReasons extends InaccessibleLinkRefReasons {
        InaccessibleGitHubLinkRefReasons(int reasons, String wikiRef, FileReferenceLinkGitHubRules referenceLink) {
            super(reasons, wikiRef, referenceLink);
        }

        @Override
        public boolean linkRefHasSlash() { return (reasons & REASON_WIKI_PAGEREF_HAS_SLASH) != 0; }

        @Override
        public boolean linkRefHasFixableSlash() { return (reasons & REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH) != 0; }

        @Override
        public String linkRefHasSlashFixed() { return linkRef.replace("/", ""); }

        @Override
        public boolean linkRefHasSubDir() { return (reasons & REASON_WIKI_PAGEREF_HAS_SUBDIR) != 0; }

        @Override
        public String linkRefHasSubDirFixed() { return new FilePathInfo(linkRef).getFileNameWithAnchor(); }
    }

    @NotNull
    @Override
    public InaccessibleLinkRefReasons inaccessibleLinkRefReasons(@Nullable String linkRef) {
        int reasons = computeLinkRefReasonsFlags(linkRef);

        if (linkRef != null) {
            if (sourceReference.isWikiPage()) {
                if (!isUnderWikiHome()) reasons |= REASON_NOT_UNDER_WIKI_HOME;
                else if (!getWikiHome().startsWith(sourceReference.getWikiHome())) reasons |= REASON_NOT_UNDER_SOURCE_WIKI_HOME;

                if (!hasWikiPageExt()) reasons |= REASON_TARGET_NOT_WIKI_PAGE_EXT;
            }

            if (linkRef.contains("/")) {
                // see if it would resolve to the target without it
                FilePathInfo linkRefInfo = new FilePathInfo(linkRef);

                if (equivalent(false, false, getLinkRef(), linkRefInfo.getFileNameWithAnchor())) {
                    reasons |= REASON_WIKI_PAGEREF_HAS_SUBDIR;
                } else if (equivalent(false, false, getWikiPageRef(), linkRef.replace("/", ""))) {
                    reasons |= REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH;
                } else {
                    reasons |= REASON_WIKI_PAGEREF_HAS_SLASH;
                }
            }
        }
        return new InaccessibleGitHubLinkRefReasons(reasons, linkRef, this);
    }

    @Override
    protected int computeLinkRefReasonsFlags(@Nullable String linkRef) {
        // add our own
        return super.computeWikiPageRefReasonsFlags(linkRef);
    }

    @Override
    public int compareTo(FilePathInfo o) {
        int itmp;
        return (itmp = getLinkRefFromWikiHome().compareTo(o.getLinkRefFromWikiHome())) != 0 ? itmp : super.compareTo(o);
    }

    // TEST: needs testing
    @Override
    protected void computeLinkRefInfo(@NotNull String sourceReferencePath, @NotNull String targetReferencePath) {
        super.computeLinkRefInfo(sourceReferencePath, targetReferencePath);
        originalPrefix = pathPrefix;

        // if the source is in the main project and target in the wiki for main project then we re-map
        if (!sourceReference.isUnderWikiHome() && this.isUnderWikiHome() && project != null && sourceReference.getFilePath().startsWith(endWith(getProjectHome(), '/'))) {
            // need to insert ../../wiki where wiki home directory is in the path prefix
            FilePathInfo wikiHomeInfo = new FilePathInfo(getWikiHome());
            String prefixSlash = endWith(wikiHomeInfo.getFileName(), '/');
            if (pathPrefix.startsWith(prefixSlash)) {
                pathPrefix = GITHUB_WIKI_REL_HOME + pathPrefix.substring(prefixSlash.length() - 1);
            } else {
                int pos = pathPrefix.indexOf(startWith(prefixSlash, '/'));
                if (pos >= 0) {
                    pathPrefix = pathPrefix.substring(0, pos + 1) + GITHUB_WIKI_REL_HOME + pathPrefix.substring(pos + prefixSlash.length());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "FileReferenceLinkGitHubRules" +
                innerString() +
                ")";
    }
}

