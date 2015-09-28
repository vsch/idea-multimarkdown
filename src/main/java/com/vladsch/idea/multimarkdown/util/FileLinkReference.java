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
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class FileLinkReference implements Comparable<FileLinkReference> {
    private static final String WIKI_PAGE_EXTENSION = ".md";
    public static final String WIKI_HOME_EXT = ".wiki";
    public static final int REASON_TARGET_HAS_SPACES = 1;
    public static final int REASON_CASE_MISMATCH = 2;
    public static final int REASON_WIKI_PAGEREF_HAS_DASHES = 3;
    public static final int REASON_NOT_UNDER_WIKI_HOME = 4;
    public static final int REASON_TARGET_NOT_WIKI_PAGE_EXT = 5;

    protected final String sourcePath;
    protected final String targetPath;
    protected final Project project;
    protected String linkRef;
    protected String wikiLinkRef;
    protected boolean targetNotUnderWikiHome;
    protected boolean wikiAccessible;
    protected boolean sourceHasWikiExt;
    protected boolean targetHasWikiExt;
    protected int upDirectories;
    protected int downDirectories;

    public FileLinkReference(@NotNull String sourcePath, @NotNull String targetPath, @NotNull Project project) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.project = project;

        computeLinkRefInfo();
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public String getLinkRef() {
        return linkRef;
    }

    public String getWikiLinkRef() {
        return wikiLinkRef;
    }

    public boolean isWikiAccessible() {
        return wikiAccessible;
    }

    public boolean isSourceHasWikiExt() {
        return sourceHasWikiExt;
    }

    public boolean isTargetHasWikiExt() {
        return targetHasWikiExt;
    }

    @Nullable
    public VirtualFile getSourceVirtualFile() {
        return getVirtualFile(sourcePath, project);
    }

    @Nullable
    public PsiFile getSourcePsiFile() {
        return getPsiFile(sourcePath, project);
    }

    @Nullable
    public VirtualFile getTargetVirtualFile() {
        return getVirtualFile(targetPath, project);
    }

    @Nullable
    public PsiFile getTargetPsiFile() {
        return getPsiFile(targetPath, project);
    }

    public boolean linkRefHasSpaces() {
        return linkRef.indexOf(' ') >= 0;
    }

    public Integer[] unresolvedWikiPageRefReasons(@NotNull String wikiPageRef) {
        ArrayList<Integer> reasons = new ArrayList<Integer>(10);

        if (linkRefHasSpaces()) reasons.add(REASON_TARGET_HAS_SPACES);
        if (!wikiPageRef.equals(wikiLinkRef) && wikiPageRef.toLowerCase().equals(wikiLinkRef.toLowerCase())) reasons.add(REASON_CASE_MISMATCH);
        if (wikiLinkRef.indexOf('-') >= 0) reasons.add(REASON_WIKI_PAGEREF_HAS_DASHES);
        if (targetNotUnderWikiHome) reasons.add(REASON_NOT_UNDER_WIKI_HOME);
        if (!targetHasWikiExt) reasons.add(REASON_TARGET_NOT_WIKI_PAGE_EXT);

        return reasons.toArray(new Integer[reasons.size()]);
    }

    @Override
    public int compareTo(@NotNull FileLinkReference o) {
        if (upDirectories != o.upDirectories) return upDirectories - o.upDirectories;
        if (downDirectories != o.downDirectories) return downDirectories - o.downDirectories;
        return linkRef.compareTo(o.linkRef);
    }

    public static boolean isWikiFile(String fileName) {
        if (fileName.endsWith(WIKI_PAGE_EXTENSION)) {
            int pos = 0;
            while (pos < fileName.length()) {
                pos = fileName.indexOf(WIKI_HOME_EXT + "/", pos);
                if (pos < 0) break;
                if (pos > 0 && fileName.charAt(pos - 1) != '/') {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isWikiHome(String sourcePath) {
        return sourcePath.endsWith(WIKI_HOME_EXT);
    }

    @Nullable
    public static VirtualFile getVirtualFile(@NotNull String sourcePath, @NotNull Project project) {
        String baseDir = project.getBasePath();
        if (baseDir != null && sourcePath.startsWith(baseDir + "/")) {
            return VirtualFileManager.getInstance().findFileByUrl("file:" + sourcePath);
        }
        return null;
    }

    @Nullable
    public static PsiFile getPsiFile(@NotNull String sourcePath, @NotNull Project project) {
        VirtualFile file = getVirtualFile(sourcePath, project);
        if (file != null) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile != null && psiFile instanceof MultiMarkdownFile) {
                return (MultiMarkdownFile) psiFile;
            }
        }
        return null;
    }

    @NotNull
    public static String fileNameExtension(@NotNull String fileName) {
        int pathPos = fileName.lastIndexOf('/');
        if (pathPos < 0) pathPos = 0;
        int dotPos;
        int endPos = (dotPos = fileName.substring(pathPos).lastIndexOf('.')) < 0 ? dotPos : dotPos + pathPos;
        if (endPos <= 0) return "";
        return fileName.substring(endPos);
    }

    @NotNull
    public static String fileNameWithoutExtension(@NotNull String fileName) {
        int pathPos = fileName.lastIndexOf('/');
        if (pathPos < 0) pathPos = 0;
        int dotPos;
        int endPos = (dotPos = fileName.substring(pathPos).lastIndexOf('.')) < 0 ? dotPos : dotPos + pathPos;
        if (endPos <= 0) endPos = fileName.length();
        return fileName.substring(0, endPos);
    }

    @NotNull
    public static String fileNameToWikiRef(@NotNull String fileName) {
        return fileNameWithoutExtension(fileName).replace('-', ' ');
    }

    @NotNull
    public static String wikiPageRefToFileName(@NotNull String wikiPageRef, boolean addExtension) {
        return wikiPageRef.replace(' ', '-') + (addExtension ? WIKI_PAGE_EXTENSION : "");
    }

    protected void computeLinkRefInfo() {
        String pathPrefix = "";
        String[] targetParts = targetPath.split("/");
        String[] sourceParts = sourcePath.split("/");
        wikiAccessible = true;
        boolean linkAccessible = true;
        downDirectories = 0;
        upDirectories = 0;

        int iMax = Math.min(targetParts.length - 1, sourceParts.length - 1);
        int i;

        for (i = 1; i < iMax; i++) {
            if (!targetParts[i].equals(sourceParts[i])) break;
        }

        // used up the common prefix, now for every source we need to add ../, if we hit the parent.wiki directory
        // then it cannot possibly we a proper reference. All wiki pages have to stay withing the parent wiki directory
        iMax = sourceParts.length - 1;
        for (int j = i; j < iMax; j++) {
            if (isWikiHome(sourceParts[j])) {
                targetNotUnderWikiHome = wikiAccessible = false;
            }

            pathPrefix += "../";
            upDirectories++;
        }

        // used up the common prefix, now for every target we need to add the part/
        // if we encounter one with .wiki extension then it is still accessible, because it is below the source wiki home,
        iMax = targetParts.length - 1;
        for (; i < iMax; i++) {
            //if (targetParts[i].endsWith(WIKI_HOME_EXT)) {
            //    wikiAccessible = false;
            //}

            pathPrefix += targetParts[i] + "/";
            downDirectories++;
        }

        linkRef = pathPrefix + targetParts[targetParts.length - 1];
        sourceHasWikiExt = sourcePath.endsWith(WIKI_PAGE_EXTENSION);
        targetHasWikiExt = targetPath.endsWith(WIKI_PAGE_EXTENSION);

        if (linkRef.indexOf(' ') >= 0) wikiAccessible = false;
        if (!targetHasWikiExt) wikiAccessible = false;

        wikiLinkRef = fileNameToWikiRef(linkRef);
    }
}
