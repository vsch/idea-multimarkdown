// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.vladsch.md.nav.parser.ast.MdASTCompositeNode;
import org.jetbrains.annotations.NotNull;

public class LexerData {
    @NotNull public final LexerToken[] lexerTokens;
    //@NotNull public final Map<Integer, String> headerOffsetAnchorIds;
    @NotNull public final MdASTCompositeNode rootNode;

    public LexerData(@NotNull LexerToken[] lexerTokens, /*@NotNull Map<Integer, String> headerOffsetAnchorIds,*/ @NotNull MdASTCompositeNode rootNode) {
        this.lexerTokens = lexerTokens;
        //this.headerOffsetAnchorIds = headerOffsetAnchorIds;
        this.rootNode = rootNode;
    }
}
