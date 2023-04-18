// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.parserExtensions;

import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.md.nav.parser.SyntheticFlexmarkNodes;
import com.vladsch.md.nav.parser.api.MdParser;
import com.vladsch.md.nav.parser.ast.MdASTCompositeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class MdParserHandlerBase<T> {
    private final @NotNull MdParser myParser;
    protected final @NotNull T myOptions;

    protected MdParserHandlerBase(final @NotNull MdParser parser, @NotNull DataKey<T> key) {
        myParser = parser;
        myOptions = key.get(parser.getDocument());
    }

    protected void addHandlers(final @NotNull VisitHandler<?>... handlers) {myParser.addHandlers(handlers);}

    protected void addHandlers(final @NotNull VisitHandler<?>[]... handlers) {myParser.addHandlers(handlers);}

    protected void addHandlers(final @NotNull Collection<VisitHandler<?>> handlers) {myParser.addHandlers(handlers);}

    @Nullable
    protected <N extends Node> Visitor<Node> getVisitor(final @NotNull Class<N> nodeClass) {return myParser.getVisitor(nodeClass);}

    protected void addCompositeTokensWithChildren(@NotNull final SyntheticFlexmarkNodes nodes) {myParser.addCompositeTokensWithChildren(nodes);}

    protected void addCompositeTokens(@NotNull final SyntheticFlexmarkNodes nodes) {myParser.addCompositeTokens(nodes);}

    protected void addToken(final @NotNull Node node, final IElementType tokenType) {myParser.addToken(node, tokenType);}

    protected void addToken(final int startIndex, final int endIndex, final @NotNull IElementType tokenType, final @NotNull IElementType originalTokenType) {myParser.addToken(startIndex, endIndex, tokenType, originalTokenType);}

    protected void includeTrailingEOL(final @NotNull Node node) {myParser.includeTrailingEOL(node);}

    protected void includeToTrailingEOL(final @NotNull Node node) {myParser.includeToTrailingEOL(node);}

    protected void includeTrailing(final @NotNull Node node, final @NotNull CharPredicate charSet) {myParser.includeTrailing(node, charSet);}

    protected void includeLeadingIndent(final @NotNull Node node, final int maxColumns) {myParser.includeLeadingIndent(node, maxColumns);}

    public void visitChildren(final @NotNull Node node) {myParser.visitChildren(node);}

    @NotNull
    protected MdASTCompositeNode pushCompositeNode(@NotNull SyntheticFlexmarkNodes nodes) {return myParser.pushCompositeNode(nodes);}

    protected void popCompositeNode(@NotNull MdASTCompositeNode node) {myParser.popCompositeNode(node);}
}
