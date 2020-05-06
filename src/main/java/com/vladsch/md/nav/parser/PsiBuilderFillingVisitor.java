// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.parser.ast.MdASTChildVisitor;
import com.vladsch.md.nav.parser.ast.MdASTLeafNode;
import com.vladsch.md.nav.parser.ast.MdASTNode;
import com.vladsch.md.nav.psi.util.MdTypes;
import org.jetbrains.annotations.NotNull;

public class PsiBuilderFillingVisitor extends MdASTChildVisitor {
    @NotNull
    private final PsiBuilder builder;

    public PsiBuilderFillingVisitor(@NotNull PsiBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void visitNode(@NotNull MdASTNode node) {
        if (node instanceof MdASTLeafNode) {
            return;
        }

        //ensureBuilderInPosition(node.getStartOffset(), node.getElementType() != MultiMarkdownParserDefinition.MULTIMARKDOWN_FILE);
//        if (node.getStartOffset() == node.getEndOffset()) {
//            int tmp = 0;
//        }
        ensureBuilderInPosition(node.getStartOffset(), node.getStartOffset() < node.getEndOffset());
        IElementType elementType = builder.getTokenType();

        final PsiBuilder.Marker marker = builder.mark();

        super.visitNode(node);
        IElementType nextType = builder.getTokenType();

        //ensureBuilderInPosition(node.getEndOffset(), false);
        // we cannot check exact end because we don't store leaf ast nodes
        //if (node.getElementType() == MultiMarkdownTypes.VERBATIM_CONTENT) ensureBuilderInPosition(node.getEndOffset() + 1, false);
        //else ensureBuilderInPosition(node.getEndOffset(), false);
        ensureBuilderInPosition(node.getEndOffset(), false);

        marker.done(node.getElementType());
    }

    private void ensureBuilderInPosition(int position, boolean exactPos) {
        while (builder.getCurrentOffset() < position && !builder.eof()) {
            if (builder.getTokenType() == MdTypes.BLANK_LINE) {
                // we make this a composite
                final PsiBuilder.Marker marker = builder.mark();
                builder.advanceLexer();
                marker.done(MdTypes.BLANK_LINE);
            } else {
                builder.advanceLexer();
            }
        }

//        if (exactPos && builder.getCurrentOffset() != position) {
//            int tmp = 0;
//            //throw new AssertionError("parsed tree and lexer are out of sync");
//        }
    }
}
