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

public class FileReferenceLink extends FileReference {
    private static final Logger logger = org.apache.log4j.Logger.getLogger(FileReferenceLink.class);

    public static final int REASON_TARGET_HAS_SPACES = 0x0001;
    public static final int REASON_CASE_MISMATCH = 0x0002;
    public static final int REASON_WIKI_PAGEREF_HAS_DASHES = 0x0004;
    public static final int REASON_NOT_UNDER_WIKI_HOME = 0x0008;
    public static final int REASON_TARGET_NOT_WIKI_PAGE_EXT = 0x0010;
    public static final int REASON_NOT_UNDER_SOURCE_WIKI_HOME = 0x0020;
    public static final int REASON_TARGET_NAME_HAS_ANCHOR = 0x0040;
    public static final int REASON_TARGET_PATH_HAS_ANCHOR = 0x0080;
    public static final int REASON_WIKI_PAGEREF_HAS_SLASH = 0x0100;
    public static final int REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH = 0x0200;
    public static final int REASON_WIKI_PAGEREF_HAS_SUBDIR = 0x0400;
    public static final int REASON_WIKI_PAGEREF_HAS_ONLY_ANCHOR = 0x0800;
    public static final int REASON_WIKI_PAGEREF_HAS_EXT = 0x1000;

    // flags cannot be reused, linkrefs under wiki home have similar errors as wiki links
    public static final int REASON_NOT_UNDER_SAME_REPO = 0x0001000;
    public static final int REASON_MISSING_EXTENSION = 0x0002000;
    public static final int REASON_CASE_MISMATCH_IN_FILENAME = 0x0004000;
    public static final int WANT_NO_EXTENSION = 0x0008000;

    protected final @NotNull FileReference sourceReference;
    protected String pathPrefix;
    protected boolean wikiAccessible;
    protected boolean linkAccessible;
    protected int upDirectories;
    protected int downDirectories;

    public FileReferenceLink(@NotNull String sourcePath, @NotNull String targetPath, Project project) {
        super(targetPath, project);
        this.sourceReference = new FileReference(sourcePath, project);

        computeLinkRefInfo(sourceReference.getFilePath(), getFilePath());
    }

    public FileReferenceLink(@NotNull FileReferenceLink other) {
        super(other);

        this.sourceReference = other.sourceReference;

        computeLinkRefInfo(sourceReference.getFilePath(), getFilePath());
    }

    public FileReferenceLink(@NotNull FileReference sourceReference, @NotNull FileReference targetReference) {
        super(targetReference);

        assert sourceReference.getProject() == targetReference.getProject();
        this.sourceReference = sourceReference;

        computeLinkRefInfo(sourceReference.getFilePath(), getFilePath());
    }

    public FileReferenceLink(@NotNull VirtualFile sourceFile, @NotNull FileReference targetReference) {
        super(targetReference);

        this.sourceReference = new FileReference(sourceFile, project);

        computeLinkRefInfo(sourceReference.getFilePath(), getFilePath());
    }

    public FileReferenceLink(@NotNull FileReference sourceReference, @NotNull VirtualFile targetFile) {
        super(new FileReference(targetFile, sourceReference.project));

        this.sourceReference = sourceReference;

        computeLinkRefInfo(sourceReference.getFilePath(), getFilePath());
    }

    public FileReferenceLink(@NotNull VirtualFile sourceFile, @NotNull VirtualFile targetFile, Project project) {
        super(new FileReference(targetFile, project));

        this.sourceReference = new FileReference(sourceFile, project);

        computeLinkRefInfo(sourceReference.getFilePath(), getFilePath());
    }

    public FileReferenceLink(@NotNull PsiFile sourceFile, @NotNull PsiFile targetFile) {
        super(new FileReference(targetFile));

        assert sourceFile.getProject() == targetFile.getProject();
        this.sourceReference = new FileReference(sourceFile);

        computeLinkRefInfo(sourceReference.getFilePath(), getFilePath());
    }

    public FileReferenceLink(@NotNull FileReference sourceReference, @NotNull PsiFile targetFile) {
        super(new FileReference(targetFile));

        assert sourceReference.getProject() == targetFile.getProject();
        this.sourceReference = sourceReference;

        computeLinkRefInfo(sourceReference.getFilePath(), getFilePath());
    }

    public FileReferenceLink(@NotNull PsiFile sourceFile, @NotNull FileReference targetReference) {
        super(targetReference);

        assert sourceFile.getProject() == targetReference.getProject();
        this.sourceReference = new FileReference(sourceFile);

        computeLinkRefInfo(sourceReference.getFilePath(), getFilePath());
    }

    @NotNull
    public FileReference getSourceReference() {
        return sourceReference;
    }

    @NotNull
    public String getLinkRef() {
        return pathPrefix + getFileName();
    }

    @NotNull
    public String getNoPrefixLinkRef() {
        return getFileName();
    }

    @NotNull
    public String getLinkRefNoExt() {
        return pathPrefix + getFileNameNoExt();
    }

    @NotNull
    public String getNoPrefixLinkRefNoExt() {
        return getFileNameNoExt();
    }

    @NotNull
    protected String getWikiPageRefPathPrefix() {
        return FilePathInfo.asWikiRef(pathPrefix);
    }

    @NotNull
    public String getWikiPageRef() {
        return getWikiPageRefPathPrefix() + getFileNameNoExtAsWikiRef();
    }

    @NotNull
    public String getWikiPageRefWithAnchor() {
        return getWikiPageRefPathPrefix() + getFileNameWithAnchorNoExtAsWikiRef();
    }

    @NotNull
    public String getLinkRefWithAnchor() {
        return pathPrefix + getFileNameWithAnchor();
    }

    @NotNull
    public String getNoPrefixLinkRefWithAnchor() {
        return getFileNameWithAnchor();
    }

    @NotNull
    public String getLinkRefWithAnchorNoExt() {
        return pathPrefix + getFileNameWithAnchorNoExt();
    }

    @NotNull
    public String getNoPrefixLinkRefWithAnchorNoExt() {
        return getFileNameWithAnchorNoExt();
    }

    @NotNull
    public String getPathPrefix() {
        return pathPrefix;
    }

    public boolean isWikiAccessible() {
        return wikiAccessible;
    }

    public boolean linkRefHasSpaces() {
        return getLinkRef().indexOf(' ') >= 0;
    }

    public boolean linkRefWithoutExtHasSpaces() {
        return getLinkRefNoExt().indexOf(' ') >= 0;
    }

    public int getUpDirectories() {
        return upDirectories;
    }

    public int getDownDirectories() {
        return downDirectories;
    }

    // return true if the given string is a wikiRef of this link reference
    public boolean isWikiPageRef(@NotNull String wikiRef) {
        return equivalentWikiRef(true, false, getWikiPageRef(), wikiRef);
    }

    // return true if the given string is a linkRef of this link reference
    public boolean isLinkRef(@NotNull String linkRef) {
        return equivalent(true, false, getLinkRef(), linkRef);
    }

    public static class InaccessibleWikiPageReasons {
        final int reasons;
        final String wikiRef;
        final FileReferenceLink referenceLink;

        InaccessibleWikiPageReasons(int reasons, String wikiRef, FileReferenceLink referenceLink) {
            this.reasons = reasons;
            this.wikiRef = wikiRef;
            this.referenceLink = referenceLink;
        }

        public boolean targetNameHasSpaces() { return (reasons & REASON_TARGET_HAS_SPACES) != 0; }
        public String targetNameHasSpacedFixed() { return referenceLink.getFileName().replace(' ', '-'); }

        public boolean caseMismatchOnly() { return (reasons & REASON_CASE_MISMATCH) != 0 && reasons == REASON_CASE_MISMATCH; }
        public boolean caseMismatch() { return (reasons & REASON_CASE_MISMATCH) != 0; }
        public String caseMismatchWikiRefFixed() { return referenceLink.getLinkRefNoExt().replace('-', ' '); }
        public String caseMismatchFileNameFixed() { return FilePathInfo.wikiRefAsFileNameWithExt(new FilePathInfo(wikiRefHasDashesFixed()).getFileNameNoExt()); }

        public boolean wikiRefHasDashes() { return (reasons & REASON_WIKI_PAGEREF_HAS_DASHES) != 0; }
        public String wikiRefHasDashesFixed() { return wikiRef.replace('-', ' '); }

        public boolean targetNotWikiPageExt() { return (reasons & REASON_TARGET_NOT_WIKI_PAGE_EXT) != 0; }
        public String targetNotWikiPageExtFixed() { return referenceLink.getFileNameNoExt() + WIKI_PAGE_EXTENSION; }

        public boolean targetNotInWikiHome() { return (reasons & REASON_NOT_UNDER_WIKI_HOME) != 0; }
        public String targetNotInWikiHomeFixed() { return referenceLink.getSourceReference().getPath() + referenceLink.getFileName(); }

        public boolean targetNotInSameWikiHome() { return (reasons & REASON_NOT_UNDER_SOURCE_WIKI_HOME) != 0; }
        public String targetNotInSameWikiHomeFixed() { return referenceLink.getSourceReference().getPath() + referenceLink.getFileName(); }

        public boolean targetNameHasAnchor() { return (reasons & REASON_TARGET_NAME_HAS_ANCHOR) != 0; }
        public String targetNameHasAnchorFixed() { return referenceLink.getFileNameWithAnchor().replace("#", ""); }

        public boolean targetPathHasAnchor() { return (reasons & REASON_TARGET_PATH_HAS_ANCHOR) != 0; }

        public boolean wikiRefHasSlash() { return (reasons & REASON_WIKI_PAGEREF_HAS_SLASH) != 0; }
        public boolean wikiRefHasFixableSlash() { return (reasons & REASON_WIKI_PAGEREF_HAS_FIXABLE_SLASH) != 0; }
        public String wikiRefHasSlashFixed() { return wikiRef; }

        public boolean wikiRefHasSubDir() { return false; }
        public String wikiRefHasSubDirFixed() { return wikiRef; }

        public boolean wikiRefHasExt() { return (reasons & REASON_WIKI_PAGEREF_HAS_EXT) != 0; }
        public String wikiRefHasExtFixed() { return referenceLink.getWikiPageRef(); }

        public boolean wikiRefHasOnlyAnchor() { return (reasons & REASON_WIKI_PAGEREF_HAS_ONLY_ANCHOR) != 0; }
        public String wikiRefHasOnlyAnchorFixed() { return referenceLink.getFileNameNoExt() + wikiRef; }
    }

    @NotNull
    public InaccessibleWikiPageReasons inaccessibleWikiPageRefReasons(@Nullable String wikiPageRef) {
        return new InaccessibleWikiPageReasons(computeWikiPageRefReasonsFlags(wikiPageRef), wikiPageRef, this);
    }

    protected int computeWikiPageRefReasonsFlags(@Nullable String wikiPageRef) {
        int reasons = 0;

        if (linkRefHasSpaces()) reasons |= REASON_TARGET_HAS_SPACES;

        if (wikiPageRef != null && wikiPageRef.replace('-', ' ').equalsIgnoreCase(this.getWikiPageRef().replace('-', ' ')) && !wikiPageRef.replace('-', ' ').equals(this.getWikiPageRef().replace('-', ' ')))
            reasons |= REASON_CASE_MISMATCH;

        if (wikiPageRef != null && wikiPageRef.indexOf('-') >= 0) reasons |= REASON_WIKI_PAGEREF_HAS_DASHES;

        if (sourceReference.isWikiPage()) {
            if (!isUnderWikiHome()) reasons |= REASON_NOT_UNDER_WIKI_HOME;
            else if (!getWikiHome().startsWith(sourceReference.getWikiHome())) reasons |= REASON_NOT_UNDER_SOURCE_WIKI_HOME;
            if (!hasWikiPageExt()) reasons |= REASON_TARGET_NOT_WIKI_PAGE_EXT;
        }

        if (pathContainsAnchor()) reasons |= REASON_TARGET_PATH_HAS_ANCHOR;
        if (fileNameContainsAnchor()) reasons |= REASON_TARGET_NAME_HAS_ANCHOR;

        if (wikiPageRef != null && sourceReference.getFullFilePath().equals(getFullFilePath()) && wikiPageRef.startsWith("#")) {
            reasons |= REASON_WIKI_PAGEREF_HAS_ONLY_ANCHOR;
        }

        if (wikiPageRef != null) {
            FilePathInfo wikiRefInfo = new FilePathInfo(wikiPageRef);

            if (isWikiPageExt(wikiRefInfo.getWithAnchorExt())) {
                reasons |= REASON_WIKI_PAGEREF_HAS_EXT;
            }
        }
        return reasons;
    }

    public static class InaccessibleLinkRefReasons {
        final int reasons;
        final String linkRef;
        final FileReferenceLink referenceLink;

        InaccessibleLinkRefReasons(int reasons, String linkRef, FileReferenceLink referenceLink) {
            this.reasons = reasons;
            this.linkRef = linkRef;
            this.referenceLink = referenceLink;
        }

        public boolean caseMismatch() { return (reasons & REASON_CASE_MISMATCH) != 0; }
        public boolean caseMismatchInFileName() { return (reasons & REASON_CASE_MISMATCH_IN_FILENAME) != 0; }
        public String caseMismatchLinkRefFixed() { return (reasons & WANT_NO_EXTENSION) == 0 ? referenceLink.getLinkRef() : referenceLink.getLinkRefNoExt(); }
        public String caseMismatchFileNameFixed() {
            FilePathInfo pathInfo = new FilePathInfo(linkRef);
            return (reasons & WANT_NO_EXTENSION) == 0 ? pathInfo.getFileName() : pathInfo.getFileNameNoExt();
        }

        public boolean targetNotInSameRepoHome() { return (reasons & REASON_NOT_UNDER_SAME_REPO) != 0; }
        //public String targetNotInSameRepoHomeFixed() { return referenceLink.getSourceReference().getPath() + referenceLink.getFileName(); }

        public boolean targetNotInWikiHome() { return (reasons & REASON_NOT_UNDER_WIKI_HOME) != 0; }
        //public String targetNotInWikiHomeFixed() { return referenceLink.getSourceReference().getPath() + referenceLink.getFileName(); }

        public boolean targetNotInSameWikiHome() { return (reasons & REASON_NOT_UNDER_SOURCE_WIKI_HOME) != 0; }
        //public String targetNotInSameWikiHomeFixed() { return referenceLink.getSourceReference().getPath() + referenceLink.getFileName(); }

        public boolean targetNameHasAnchor() { return (reasons & REASON_TARGET_NAME_HAS_ANCHOR) != 0; }
        public String targetNameHasAnchorFixed() { return referenceLink.getFileNameWithAnchor().replace("#", ""); }

        public boolean targetPathHasAnchor() { return (reasons & REASON_TARGET_PATH_HAS_ANCHOR) != 0; }

        // GitHub rules
        public boolean linkRefHasSlash() { return false; }
        public boolean linkRefHasFixableSlash() { return false; }
        public String linkRefHasSlashFixed() { return linkRef; }
        public boolean linkRefHasSubDir() { return false; }
        public String linkRefHasSubDirFixed() { return linkRef; }
    }

    @NotNull
    public InaccessibleLinkRefReasons inaccessibleLinkRefReasons(@Nullable String linkRef) {
        return new InaccessibleLinkRefReasons(computeLinkRefReasonsFlags(linkRef), linkRef, this);
    }

    protected int computeLinkRefReasonsFlags(@Nullable String linkRef) {
        int reasons = isWikiPage() ? WANT_NO_EXTENSION : 0;

        if (linkRefHasSpaces()) reasons |= REASON_TARGET_HAS_SPACES;

        if (linkRef != null) {
            FilePathInfo linkRefInfo = new FilePathInfo(linkRef);

            if (linkRefInfo.hasExt()) {
                if (linkRef.equalsIgnoreCase(this.getLinkRef()) && !linkRef.equals(this.getLinkRef())) {
                    reasons |= REASON_CASE_MISMATCH;
                    if (!linkRefInfo.getFileName().equals(this.getFileName())) reasons |= REASON_CASE_MISMATCH_IN_FILENAME;
                }
            } else {
                if (linkRef.equalsIgnoreCase(this.getLinkRefNoExt()) && !linkRef.equals(this.getLinkRefNoExt())) {
                    reasons |= REASON_CASE_MISMATCH;
                    if (!linkRefInfo.getFileName().equals(this.getFileName())) reasons |= REASON_CASE_MISMATCH_IN_FILENAME;
                }
            }
        }

        String targetGitHubRepoPath = getGitHubRepoPath();
        String sourceGitHubRepoPath = sourceReference.getGitHubRepoPath();

        if (targetGitHubRepoPath != null || sourceGitHubRepoPath != null) {
            if (isUnderWikiHome()) {
                if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !targetGitHubRepoPath.startsWith(sourceGitHubRepoPath))
                    reasons |= REASON_NOT_UNDER_SAME_REPO;
            } else {
                if (targetGitHubRepoPath == null || sourceGitHubRepoPath == null || !sourceGitHubRepoPath.startsWith(targetGitHubRepoPath))
                    reasons |= REASON_NOT_UNDER_SAME_REPO;
            }
        }

        if (pathContainsAnchor()) reasons |= REASON_TARGET_PATH_HAS_ANCHOR;
        if (fileNameContainsAnchor()) reasons |= REASON_TARGET_NAME_HAS_ANCHOR;

        return reasons;
    }

    protected void computeLinkRefInfo(@NotNull String sourceReferencePath, @NotNull String targetReferencePath) {
        pathPrefix = "";
        String[] targetParts = targetReferencePath.split("/");
        String[] sourceParts = sourceReferencePath.split("/");
        downDirectories = 0;
        upDirectories = 0;

        int iMax = Math.min(targetParts.length - 1, sourceParts.length - 1);
        int i;

        for (i = 1; i < iMax; i++) {
            if (!targetParts[i].equals(sourceParts[i])) break;
        }

        // used up the common prefix, now for every source we need to add ../
        iMax = sourceParts.length - 1;
        for (int j = i; j < iMax; j++) {
            pathPrefix += "../";
            upDirectories++;
        }

        // used up the common prefix, now for every target we need to add the part/
        iMax = targetParts.length - 1;
        for (; i < iMax; i++) {
            pathPrefix += targetParts[i] + "/";
            downDirectories++;
        }

        wikiAccessible = getLinkRef().indexOf(' ') < 0 && hasWikiPageExt() && !getWikiHome().isEmpty() && getWikiHome().equals(sourceReference.getWikiHome()) && !containsAnchor();
    }

    public int reflinkCompareTo(@NotNull FileReferenceLink o) {
        if (upDirectories != o.upDirectories) return upDirectories - o.upDirectories;
        if (downDirectories != o.downDirectories) return downDirectories - o.downDirectories;
        return getLinkRef().compareTo(o.getLinkRef());
    }

    @Override
    public int compareTo(FilePathInfo o) {
        return !(o instanceof FileReferenceLink) ||
                (
                        sourceReference == ((FileReferenceLink) o).sourceReference &&
                                getLinkRef().equals(((FileReferenceLink) o).getLinkRef()) &&
                                getLinkRefWithAnchor().equals(((FileReferenceLink) o).getLinkRefWithAnchor()) &&
                                getLinkRefNoExt().equals(((FileReferenceLink) o).getLinkRefNoExt()) &&
                                getWikiPageRef().equals(((FileReferenceLink) o).getWikiPageRef()) &&
                                getWikiPageRefWithAnchor().equals(((FileReferenceLink) o).getWikiPageRefWithAnchor()) &&
                                wikiAccessible == ((FileReferenceLink) o).wikiAccessible &&
                                upDirectories == ((FileReferenceLink) o).upDirectories &&
                                downDirectories == ((FileReferenceLink) o).downDirectories
                )
                ? super.compareTo(o) : reflinkCompareTo((FileReferenceLink) o);
    }

    @Override
    public String toString() {
        return "FileReferenceLink(" +
                innerString() +
                ")";
    }

    @Override
    public String innerString() {
        return super.innerString() +
                "sourceReference  = " + sourceReference + ", " +
                "linkRef = " + "'" + getLinkRef() + "', " +
                "linkRefNoExt = " + "'" + getLinkRefNoExt() + "', " +
                "linkRefWithAnchor = " + "'" + getLinkRefWithAnchor() + "', " +
                "linkRefWithAnchorNoExt = " + "'" + getLinkRefWithAnchorNoExt() + "', " +
                "wikiPageRef = " + "'" + getWikiPageRef() + "', " +
                "wikiPageRefWithAnchor = " + "'" + getWikiPageRefWithAnchor() + "', " +
                "wikiAccessible  = " + wikiAccessible + ", " +
                "upDirectories  = " + upDirectories + ", " +
                "downDirectories  = " + downDirectories + ", " +
                "";
    }

}

