// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.diff.FlyweightCapableTreeStructure;
import com.vladsch.flexmark.util.misc.DelimitedBuilder;
import com.vladsch.md.nav.parser.ast.MdASTCompositeNode;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class MdParserAdapter implements PsiParser, LightPsiParser {
    private static final Logger LOG = getInstance("com.vladsch.md.nav.parser");

    @Override
    public void parseLight(IElementType root, PsiBuilder builder) {
        MdLexer lexer = (MdLexer) ((PsiBuilderImpl) builder).getLexer();
        //lexer.setForPsi(true);
        // we want comments
        builder.enforceCommentTokens(TokenSet.EMPTY);

        LexerData lexerData = lexer.getLexerData();
        if (lexerData != null) {
            final MdASTCompositeNode parsedTree = lexerData.rootNode;

            //if (lexerData.lexerTokens.length > 0) assert builder.getCurrentOffset() == lexerData.lexerTokens[0].getRange().getStart();
            assert builder.getCurrentOffset() == 0;
            new PsiBuilderFillingVisitor(builder).visitNode(parsedTree);
        } else {
            PsiBuilder.Marker rootMarker = builder.mark();
            while (!builder.eof()) builder.advanceLexer();
            rootMarker.done(root);
        }

        if (!builder.eof()) {
            // we drain and use the list as part of the exception message
            DelimitedBuilder out = new DelimitedBuilder("\n");
            while (!builder.eof()) {
                //IElementType type = builder.getTokenType();
                //int startOffset = lexer.getTokenStart();
                //int endOffset = lexer.getTokenEnd();
                //out.append(type == null ? "null" : type.toString()).append("[").append(startOffset).append(", ").append(endOffset).append("]").mark();
                builder.advanceLexer();
            }

            //throw new IllegalStateException("Builder had elements left:\n" + out.toString());
            LOG.error("Builder had elements left:\n" + out.toString());
        }
    }

    @NotNull
    public FlyweightCapableTreeStructure<LighterASTNode> parseLightStub(IElementType root, PsiBuilder builder) {
        parseLight(root, builder);
        FlyweightCapableTreeStructure<LighterASTNode> rootNode = builder.getLightTree();
        return rootNode;
    }

    @NotNull
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        MdLexer lexer = (MdLexer) ((PsiBuilderImpl) builder).getLexer();
        LexerData lexerData = lexer.getLexerData();
        parseLight(root, builder);
        ASTNode rootNode = builder.getTreeBuilt();
        return rootNode;
    }
}
