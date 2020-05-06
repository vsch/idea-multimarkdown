// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.vladsch.md.nav.language.MdCodeStyleSettings;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.md.nav.psi.util.MdNodeVisitorHandler;
import org.jetbrains.annotations.NotNull;

public interface MdStripTrailingSpacesDocumentFilter extends MdNodeVisitorHandler {
    @NotNull
    MdCodeStyleSettings getCodeStyleSettings();

    @NotNull
    MdFile getPsiFile();

    int getLineNumber(int offset);

    void checkBlockForTrailingSpaces(@NotNull ASTNode node);

    void keepLineTrailingSpaces(@NotNull ASTNode node, int keepTrailingSpaces);

    default void disableOffsetRange(TextRange range, boolean inclusiveEndLine) {
        keepOffsetTrailingSpaces(range, 3, inclusiveEndLine);
    }

    default void disableLine(int startLine) {
        keepLineTrailingSpaces(startLine, startLine + 1, 3);
    }

    default void disableLineRange(int startLine, int endLine) {
        keepLineTrailingSpaces(startLine, endLine, 3);
    }

    default void keepOffsetTrailingSpaces(TextRange range, int keepTrailingSpaces, boolean inclusiveEndLine) {
        keepLineTrailingSpaces(getLineNumber(range.getStartOffset()), getLineNumber(range.getEndOffset()) + (inclusiveEndLine ? 1 : 0), keepTrailingSpaces);
    }

    default void keepLineTrailingSpaces(int startLine, int keepTrailingSpaces) {
        keepLineTrailingSpaces(startLine, startLine + 1, keepTrailingSpaces);
    }

    void keepLineTrailingSpaces(int startLine, int endLine, int keepTrailingSpaces);
}
