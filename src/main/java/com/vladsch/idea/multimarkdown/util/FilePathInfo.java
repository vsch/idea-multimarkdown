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
import com.vladsch.idea.multimarkdown.MultiMarkdownFileTypeFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilePathInfo implements Comparable<FilePathInfo> {
    public static final String WIKI_PAGE_EXTENSION = ".md";
    public static final String WIKI_HOME_EXTENTION = ".wiki";
    public static final String[] IMAGE_EXTENSIONS = { "png", "jpg", "jpeg", "gif", };
    public static final String[] MARKDOWN_EXTENSIONS = MultiMarkdownFileTypeFactory.EXTENSIONS;
    public static final String[] WIKI_PAGE_EXTENSIONS = { MultiMarkdownFileTypeFactory.DEFAULT_EXTENSION };

    private final int wikiHomeEnd;
    private final int nameStart;
    private final int nameEnd;
    private final @NotNull String filePath;

    public FilePathInfo(@NotNull String filePath) {
        this.filePath = filePath;
        int lastSep;
        int extStart;
        this.nameStart = (lastSep = filePath.lastIndexOf('/')) < 0 ? 0 : (lastSep == filePath.length() - 1 ? lastSep : lastSep + 1);
        int wikiHomeEnd;
        this.wikiHomeEnd = (wikiHomeEnd = filePath.indexOf(WIKI_HOME_EXTENTION + "/", 0)) >= nameStart || wikiHomeEnd < 0 ? 0 :
                wikiHomeEnd + WIKI_HOME_EXTENTION.length();

        // if file name ends in . then it has no extension and the . is part of its name.
        this.nameEnd = (extStart = filePath.lastIndexOf('.', filePath.length())) <= nameStart ? filePath.length() : extStart;
    }

    public FilePathInfo(@NotNull VirtualFile file) {
        this(file.getPath());
    }

    public FilePathInfo(@NotNull FilePathInfo other) {
        this.wikiHomeEnd = other.wikiHomeEnd;
        this.nameStart = other.nameStart;
        this.nameEnd = other.nameEnd;
        this.filePath = other.filePath;
    }

    @NotNull
    public static String asWikiRef(@NotNull String filePath) {
        return filePath.replace('-', ' ');
    }

    @NotNull
    public static String asFileRef(@NotNull String filePath) {
        return filePath.replace(' ', '-');
    }

    protected static boolean compare(boolean forWikiRef, boolean caseSensitive, boolean spaceDashEquivalent, int i, int iMax, @NotNull String fileRef, int fileRefOffs, @NotNull String wikiRef, int wikiRefOffs) {
        if (forWikiRef) {
            for (; i < iMax; i++) {
                char wC = wikiRef.charAt(i + wikiRefOffs);
                char fC = fileRef.charAt(i + fileRefOffs);
                if (fC == '-') return false;
                if (wC == '-' && !spaceDashEquivalent) return false;

                if (wC == fC) continue;
                if (fC == ' ' && wC == '-') continue;
                if (!caseSensitive && Character.toLowerCase(fC) == Character.toLowerCase(wC)) continue;
                return false;
            }
        } else {
            for (; i < iMax; i++) {
                char wC = wikiRef.charAt(i + wikiRefOffs);
                char fC = fileRef.charAt(i + fileRefOffs);
                if (wC == fC) continue;
                if (!caseSensitive && Character.toLowerCase(fC) == Character.toLowerCase(wC)) continue;
                if (spaceDashEquivalent && fC == '-' && wC == ' ') continue;
                return false;
            }
        }
        return true;
    }

    private static boolean endsWith(boolean forWikiRef, boolean caseSensitive, boolean spaceDashEquivalent, @NotNull String fileRef, @NotNull String wikiRef) {
        if (!forWikiRef && !spaceDashEquivalent && caseSensitive) {
            return fileRef.endsWith(wikiRef);
        }

        int wikiRefLen = wikiRef.length();
        int fileRefLen = fileRef.length();
        int fileRefOffs = fileRefLen - wikiRefLen;

        return fileRefLen >= wikiRefLen && compare(forWikiRef, caseSensitive, spaceDashEquivalent, 0, wikiRefLen, fileRef, fileRefOffs, wikiRef, 0);
    }

    private static boolean equivalent(boolean forWikiRef, boolean caseSensitive, boolean spaceDashEquivalent, @NotNull String fileRef, @NotNull String wikiRef) {
        if (!forWikiRef && !spaceDashEquivalent) {
            return caseSensitive ? fileRef.equals(wikiRef) : fileRef.equalsIgnoreCase(wikiRef);
        }

        int wikiRefLen = wikiRef.length();
        int fileRefLen = fileRef.length();

        return fileRefLen == wikiRefLen && compare(forWikiRef, caseSensitive, spaceDashEquivalent, 0, wikiRefLen, fileRef, 0, wikiRef, 0);
    }

    public static boolean equivalent(boolean caseSensitive, boolean spaceDashEquivalent, @NotNull String fileRef, @NotNull String wikiRef) {
        return equivalent(false, caseSensitive, spaceDashEquivalent, fileRef, wikiRef);
    }

    public static boolean endsWith(boolean caseSensitive, boolean spaceDashEquivalent, @NotNull String fileRef, @NotNull String wikiRef) {
        return endsWith(false, caseSensitive, spaceDashEquivalent, fileRef, wikiRef);
    }

    public static boolean equivalentWikiRef(boolean caseSensitive, boolean spaceDashEquivalent, @NotNull String fileRef, @NotNull String wikiRef) {
        return equivalent(true, caseSensitive, spaceDashEquivalent, fileRef, wikiRef);
    }

    public static boolean endsWithWikiRef(boolean caseSensitive, boolean spaceDashEquivalent, @NotNull String fileRef, @NotNull String wikiRef) {
        return endsWith(true, caseSensitive, spaceDashEquivalent, fileRef, wikiRef);
    }
    @Nullable
    public static String wikiRefNoAnchorRef(@Nullable String wikiRef) {
        if (wikiRef != null) {
            int pos;
            // WikiLinks can have anchor # refs
            if ((pos = wikiRef.lastIndexOf("#")) >= 0) {
                wikiRef = wikiRef.substring(0, pos);
            }
        }
        return wikiRef;
    }

    @NotNull
    final public String getExt() {
        return nameEnd + 1 < filePath.length() ? filePath.substring(nameEnd + 1) : "";
    }

    final public boolean isImageExt() {
        return isImageExt(getExt());
    }

    final public boolean isMarkdownExt() {
        return isMarkdownExt(getExt());
    }

    public boolean hasExt() {
        return nameEnd + 1 < filePath.length();
    }

    @NotNull
    final public String getExtWithDot() {
        return nameEnd < filePath.length() ? filePath.substring(nameEnd) : "";
    }

    final public boolean hasWikiPageExt() {
        return filePath.endsWith(WIKI_PAGE_EXTENSION);
    }

    @NotNull
    final public String getFilePath() {
        return filePath;
    }

    @NotNull
    final public String getFilePathAsWikiRef() {
        return asWikiRef(filePath);
    }

    final public boolean containsSpaces() {
        return filePath.indexOf(' ') >= 0;
    }

    final public boolean isWikiHome() {
        return filePath.endsWith(WIKI_HOME_EXTENTION);
    }

    @NotNull
    final public String getFilePathNoExt() {
        return filePath.substring(0, nameEnd);
    }

    @NotNull
    final public String getFilePathNoExtAsWikiRef() {
        return asWikiRef(getFilePathNoExt());
    }

    @NotNull
    final public String getPath() {
        return nameStart == 0 ? "" : filePath.substring(0, nameStart);
    }

    @NotNull
    final public String getPathAsWikiRef() {
        return asWikiRef(getPath());
    }

    final public boolean isUnderWikiHome() {
        return wikiHomeEnd > 0;
    }

    final public boolean isWikiPage() {
        return isUnderWikiHome() && isWikiPageExt(getExt());
    }

    @NotNull
    final public String getWikiHome() {
        return wikiHomeEnd <= 0 ? "" : filePath.substring(0, wikiHomeEnd);
    }

    final public int getUpDirectoriesToWikiHome() {
        if (wikiHomeEnd <= 0 || wikiHomeEnd == filePath.length()) return 0;
        int pos = wikiHomeEnd;
        int upDirs = 0;
        while (pos < filePath.length() && (pos = filePath.indexOf('/', pos)) >= 0) {
            upDirs++;
            pos++;
        }
        return upDirs;
    }

    final public boolean pathContainsSpaces() {
        return getPath().indexOf(' ') >= 0;
    }

    @NotNull
    final public String getFileName() {
        return filePath.substring(nameStart, filePath.length());
    }

    final public boolean fileNameContainsSpaces() {
        return getFileName().indexOf(' ') >= 0;
    }

    @NotNull
    final public String getFileNameAsWikiRef() {
        return asWikiRef(getFileName());
    }

    @NotNull
    final public String getFileNameNoExt() {
        return filePath.substring(nameStart, nameEnd);
    }

    @NotNull
    final public String getFileNameNoExtAsWikiRef() {
        return asWikiRef(getFileNameNoExt());
    }

    public static boolean isExtInList(boolean caseSensitive, @NotNull String ext, String... extList) {
        for (String listExt : extList) {
            if (caseSensitive ? listExt.equals(ext) : listExt.equalsIgnoreCase(ext)) return true;
        }
        return false;
    }

    public static boolean isExtInList(@NotNull String ext, String... extList) {
        return isExtInList(false, ext, extList);
    }

    public static boolean isExtInListCaseSensitive(@NotNull String ext, String... extList) {
        return isExtInList(true, ext, extList);
    }

    public static boolean isImageExt(@NotNull String ext) {
        return isExtInList(false, ext, IMAGE_EXTENSIONS);
    }

    public static boolean isMarkdownExt(@NotNull String ext) {
        return isExtInList(false, ext, MARKDOWN_EXTENSIONS);
    }

    public static boolean isWikiPageExt(@NotNull String ext) {
        return isExtInList(false, ext, WIKI_PAGE_EXTENSIONS);
    }

    public static String wikiRefAsFileName(String name, boolean addExtension) {
        return name.replace(' ', '-') + (addExtension ? WIKI_PAGE_EXTENSION : "");
    }

    public static String wikiRefAsFileNameNoExt(String name) {
        return wikiRefAsFileName(name, false);
    }

    public static String wikiRefAsFileNameWithExt(String name) {
        return wikiRefAsFileName(name, true);
    }

    @Override
    public int compareTo(FilePathInfo o) {
        return wikiHomeEnd == o.wikiHomeEnd &&
                nameStart == o.nameStart &&
                nameEnd == o.nameEnd ? filePath.compareTo(o.filePath) : -99;
    }

    @Override
    public String toString() {
        return "FilePathInfo(" +
                innerString() +
                ")";
    }

    public String innerString() {
        return "" +
                "wikiHomeEnd = " + wikiHomeEnd + ", " +
                "nameStart = " + nameStart + ", " +
                "nameEnd = " + nameEnd + ", " +
                "filePath = " + "'" + filePath + "', " +
                "";
    }
}
