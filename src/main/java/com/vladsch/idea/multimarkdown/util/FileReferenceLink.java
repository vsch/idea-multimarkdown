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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class FileReferenceLink extends FileReference implements Comparable<FileReferenceLink> {

    public static final int REASON_TARGET_HAS_SPACES = 1;
    public static final int REASON_CASE_MISMATCH = 2;
    public static final int REASON_WIKI_PAGEREF_HAS_DASHES = 3;
    public static final int REASON_NOT_UNDER_WIKI_HOME = 4;
    public static final int REASON_TARGET_NOT_WIKI_PAGE_EXT = 5;
    public static final int REASON_NOT_UNDER_SOURCE_WIKI_HOME = 6;

    public static final int REASON_MAX = 6;

    protected final @NotNull FileReference sourceReference;
    protected String linkRef;
    protected String wikiPageRef;
    protected boolean wikiAccessible;
    protected int upDirectories;
    protected int downDirectories;

    public FileReferenceLink(@NotNull String sourcePath, @NotNull String targetPath, Project project) {
        super(targetPath, project);
        this.sourceReference = new FileReference(sourcePath, project);

        computeLinkRefInfo();
    }

    public FileReferenceLink(@NotNull FileReference sourceReference, @NotNull FileReference targetReference) {
        super(targetReference);

        assert sourceReference.getProject() == targetReference.getProject();
        this.sourceReference = sourceReference;

        computeLinkRefInfo();
    }

    @NotNull
    public FileReference getSource() {
        return sourceReference;
    }

    @NotNull
    public String getLinkRef() {
        return linkRef;
    }

    @NotNull
    public String getWikiPageRef() {
        return wikiPageRef;
    }

    public boolean isWikiAccessible() {
        return wikiAccessible;
    }

    public boolean linkRefHasSpaces() {
        return linkRef.indexOf(' ') >= 0;
    }

    public int getUpDirectories() {
        return upDirectories;
    }

    public int getDownDirectories() {
        return downDirectories;
    }

    @NotNull
    public int[] inaccessibleWikiPageRefReasons(@Nullable String wikiPageRef) {
        int[] reasons = new int[REASON_MAX];
        int i = 0;

        if (linkRefHasSpaces()) reasons[i++] = REASON_TARGET_HAS_SPACES;
        if (wikiPageRef != null && wikiPageRef.equalsIgnoreCase(this.wikiPageRef) && !wikiPageRef.equals(this.wikiPageRef)) reasons[i++] = REASON_CASE_MISMATCH;
        if (wikiPageRef != null && wikiPageRef.indexOf('-') >= 0) reasons[i++] = REASON_WIKI_PAGEREF_HAS_DASHES;
        if (!isUnderWikiHome()) reasons[i++] = REASON_NOT_UNDER_WIKI_HOME;
        if (!hasWikiPageExt()) reasons[i++] = REASON_TARGET_NOT_WIKI_PAGE_EXT;
        if (!getWikiHome().startsWith(sourceReference.getWikiHome())) reasons[i++] = REASON_NOT_UNDER_SOURCE_WIKI_HOME;

        return Arrays.copyOfRange(reasons, 0, i);
    }

    @Override
    public int compareTo(@NotNull FileReferenceLink o) {
        if (upDirectories != o.upDirectories) return upDirectories - o.upDirectories;
        if (downDirectories != o.downDirectories) return downDirectories - o.downDirectories;
        return linkRef.compareTo(o.linkRef);
    }

    protected void computeLinkRefInfo() {
        String pathPrefix = "";
        String[] targetParts = getFilePath().split("/");
        String[] sourceParts = sourceReference.getFilePath().split("/");
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

        linkRef = pathPrefix + getFileName();
        wikiPageRef = FilePathInfo.asWikiRef(pathPrefix + getFileNameNoExt());

        wikiAccessible = linkRef.indexOf(' ') < 0 && hasWikiPageExt() && getWikiHome().startsWith(sourceReference.getWikiHome());
    }
}
