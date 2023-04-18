// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.api;

import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKeyBase;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.md.nav.parser.SyntheticFlexmarkNodes;
import com.vladsch.md.nav.parser.ast.MdASTCompositeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface MdParser extends DataHolder {
    @NotNull
    @Override
    default Map<? extends DataKeyBase<?>, Object> getAll() {
        return getDocument().getAll();
    }

    @NotNull
    @Override
    default Collection<? extends DataKeyBase<?>> getKeys() {
        return getDocument().getKeys();
    }

    @Override
    default boolean contains(@NotNull DataKeyBase<?> key) {
        return getDocument().contains(key);
    }

    @NotNull
    @Override
    default MutableDataHolder toMutable() {
        return getDocument().toMutable();
    }

    @NotNull
    @Override
    default DataHolder toImmutable() {
        return getDocument().toImmutable();
    }

    @NotNull
    Document getDocument();

    MdParser addHandlers(VisitHandler<?>... handlers);

    MdParser addHandlers(VisitHandler<?>[]... handlers);

    MdParser addHandlers(Collection<VisitHandler<?>> handlers);

    @Nullable
    <N extends Node> Visitor<Node> getVisitor(Class<N> nodeClass);

    // add synthetic nodes but also visit child nodes and knock out the synthetic nodes with the children
    // if childToVisit is not null then it will be used as the parent of the corresponding list to be punched out by the child list, otherwise the parent of all synthetic nodes will be used
    void addCompositeTokensWithChildren(@NotNull SyntheticFlexmarkNodes nodes);

    void visitChildren(Node node);

    void addCompositeTokens(@NotNull SyntheticFlexmarkNodes nodes);

    void addToken(Node node, IElementType tokenType);

    // add token from characters of input char sequence
    void addToken(int startIndex, int endIndex, IElementType tokenType, IElementType originalTokenType);

    void includeTrailingEOL(Node node);

    @NotNull
    MdASTCompositeNode pushCompositeNode(@NotNull SyntheticFlexmarkNodes nodes);

    void popCompositeNode(@NotNull MdASTCompositeNode node);

    void includeToTrailingEOL(Node node);

    void includeTrailing(Node node, CharPredicate charSet);

    void includeToTrailing(Node node, CharPredicate charSet);

    void includeLeadingIndent(Node node, int maxColumns);
}
