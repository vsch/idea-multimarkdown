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

import com.vladsch.idea.multimarkdown.MultiMarkdownFileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class FilePathInfo {
    public static final String WIKI_PAGE_EXTENSION = ".md";
    public static final String WIKI_HOME_EXT = ".wiki";
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
        this.wikiHomeEnd = (wikiHomeEnd = filePath.indexOf(WIKI_HOME_EXT + "/", 0)) >= nameStart || wikiHomeEnd < 0 ? 0 :
                wikiHomeEnd + WIKI_HOME_EXT.length();

        // if file name ends in . then it has no extension and the . is part of its name.
        this.nameEnd = (extStart = filePath.lastIndexOf('.', filePath.length())) <= nameStart ? filePath.length() : extStart;
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

    protected static boolean compare(boolean caseSensitive, boolean spaceDashEquivalent, int i, int iMax, @NotNull String param, int paramOffs, @NotNull String tail, int tailOffs) {
        for (; i < iMax; i++) {
            char tC = tail.charAt(i + tailOffs);
            char pC = param.charAt(i + paramOffs);
            if (tC == pC) continue;
            if (caseSensitive || Character.toLowerCase(pC) != Character.toLowerCase(tC)) return false;
            if (spaceDashEquivalent || !((pC == ' ' || pC == '-') && (tC == ' ' || tC == '-'))) return false;
        }
        return true;
    }

    public static boolean endsWith(boolean caseSensitive, boolean spaceDashEquivalent, @NotNull String param, @NotNull String tail) {
        if (!spaceDashEquivalent && caseSensitive) {
            return param.endsWith(tail);
        }

        int tailLen = tail.length();
        int paramLen = param.length();
        int paramOffs = paramLen - tailLen;

        return paramLen >= tailLen && compare(caseSensitive, spaceDashEquivalent, 0, tailLen, tail, 0, param, paramOffs);
    }

    public static boolean equivalent(boolean caseSensitive, boolean spaceDashEquivalent, @NotNull String param, @NotNull String other) {
        if (!spaceDashEquivalent) {
            return caseSensitive ? param.equals(other) : param.equalsIgnoreCase(other);
        }

        int tailLen = other.length();
        int paramLen = param.length();

        return paramLen == tailLen && compare(caseSensitive, spaceDashEquivalent, 0, tailLen, other, 0, param, 0);
    }

    @NotNull
    public String getExt() {
        return nameEnd + 1 >= filePath.length() ? "" : filePath.substring(nameEnd + 1);
    }

    @NotNull
    public String getExtWithDot() {
        return nameEnd == filePath.length() ? "" : filePath.substring(nameEnd);
    }

    public boolean hasWikiPageExt() {
        return filePath.endsWith(WIKI_PAGE_EXTENSION);
    }

    @NotNull
    public String getFilePath() {
        return filePath;
    }

    @NotNull
    public String getFilePathAsWikiRef() {
        return asWikiRef(filePath);
    }

    public boolean containsSpaces() {
        return filePath.indexOf(' ') >= 0;
    }

    public boolean isWikiHome() {
        return filePath.endsWith(WIKI_HOME_EXT);
    }

    @NotNull
    public String getFilePathNoExt() {
        return filePath.substring(0, nameEnd);
    }

    @NotNull
    public String getFilePathNoExtAsWikiRef() {
        return asWikiRef(getFilePathNoExt());
    }

    @NotNull
    public String getPath() {
        return nameStart == 0 ? "" : filePath.substring(0, nameStart);
    }

    @NotNull
    public String getPathAsWikiRef() {
        return asWikiRef(getPath());
    }

    public boolean isUnderWikiHome() {
        return wikiHomeEnd > 0;
    }

    @NotNull
    public String getWikiHome() {
        return wikiHomeEnd <= 0 ? "" : filePath.substring(0, wikiHomeEnd);
    }

    public boolean pathContainsSpaces() {
        return getPath().indexOf(' ') >= 0;
    }

    @NotNull
    public String getFileName() {
        return filePath.substring(nameStart, filePath.length());
    }

    public boolean fileNameContainsSpaces() {
        return getFileName().indexOf(' ') >= 0;
    }

    @NotNull
    public String getFileNameAsWikiRef() {
        return asWikiRef(getFileName());
    }

    @NotNull
    public String getFileNameNoExt() {
        return filePath.substring(nameStart, nameEnd);
    }

    @NotNull
    public String getFileNameNoExtAsWikiRef() {
        return asWikiRef(getFileNameNoExt());
    }

    public static boolean isExtInList(boolean caseSensitive, @NotNull String ext, String... extList) {
        for (String listExt : extList) {
            if (equivalent(caseSensitive, false, listExt, ext)) return true;
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
}
