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
package com.vladsch.idea.multimarkdown.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.*;

public class MultiMarkdownParser implements PsiParser {
    public void parseLightImpl(IElementType root, PsiBuilder builder) {
        if (root == WIKI_LINK) {
            parseWikiLink(builder);
        } else {
            parseRoot(root, builder);
        }
    }

    protected boolean parseRoot(IElementType root, PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();
        while (!builder.eof()) {
            if (builder.getTokenType() == COMMENT) {
                PsiBuilder.Marker tokenMarker = builder.mark();
                builder.advanceLexer();
                tokenMarker.done(COMMENT);
            } else if (parseWikiLink(builder)) {
            } else {
                builder.advanceLexer();
            }
        }

        rootMarker.done(root);
        return true;
    }

    protected boolean parseWikiLink(PsiBuilder builder) {
        if (builder.getTokenType() != WIKI_LINK_OPEN) return false;

        PsiBuilder.Marker wikiLinkMarker = builder.mark();
        builder.advanceLexer();

        // creole syntax ref then title, github syntax either ref or title comes first
        if (builder.getTokenType() == WIKI_LINK_REF || builder.getTokenType() == WIKI_LINK_REF_ANCHOR_MARKER) {
            PsiBuilder.Marker marker = builder.mark();
            if (builder.getTokenType() == WIKI_LINK_REF) builder.advanceLexer();
            marker.done(WIKI_LINK_REF);

            if (builder.getTokenType() == WIKI_LINK_REF_ANCHOR_MARKER) {
                builder.advanceLexer();
                PsiBuilder.Marker anchor = builder.mark();
                if (builder.getTokenType() == WIKI_LINK_REF_ANCHOR) builder.advanceLexer();
                anchor.done(WIKI_LINK_REF_ANCHOR);
            }
        }

        if (builder.getTokenType() == WIKI_LINK_TEXT) {
            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();
            marker.done(WIKI_LINK_TEXT);
        }

        if (builder.getTokenType() == WIKI_LINK_SEPARATOR) {
            builder.advanceLexer();

            if (builder.getTokenType() == WIKI_LINK_REF || builder.getTokenType() == WIKI_LINK_REF_ANCHOR_MARKER) {
                PsiBuilder.Marker marker = builder.mark();
                if (builder.getTokenType() == WIKI_LINK_REF) builder.advanceLexer();
                marker.done(WIKI_LINK_REF);
                if (builder.getTokenType() == WIKI_LINK_REF_ANCHOR_MARKER) {
                    builder.advanceLexer();
                    PsiBuilder.Marker anchor = builder.mark();
                    if (builder.getTokenType() == WIKI_LINK_REF_ANCHOR) builder.advanceLexer();
                    anchor.done(WIKI_LINK_REF_ANCHOR);
                }
            }

            if (builder.getTokenType() == WIKI_LINK_TEXT) {
                PsiBuilder.Marker marker = builder.mark();
                builder.advanceLexer();
                marker.done(WIKI_LINK_TEXT);
            }
        }

        if (builder.getTokenType() == WIKI_LINK_CLOSE) {
            builder.advanceLexer();
        }

        wikiLinkMarker.done(WIKI_LINK);
        return true;
    }

    /**
     * Parse the contents of the specified PSI builder and returns an AST tree with the
     * specified type of root element.
     *
     * @param root    the type of the root element in the AST tree.
     * @param builder the builder which is used to retrieve the original file tokens and build the AST tree.
     * @return the root of the resulting AST tree.
     */
    @NotNull
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        parseLightImpl(root, builder);
        return builder.getTreeBuilt();
    }
}
