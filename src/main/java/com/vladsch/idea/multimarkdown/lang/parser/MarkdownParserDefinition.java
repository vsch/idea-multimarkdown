/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
* Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
package com.vladsch.idea.multimarkdown.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.EmptyLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.vladsch.idea.multimarkdown.lang.psi.impl.MarkdownPsiElementImpl;
import com.vladsch.idea.multimarkdown.file.MarkdownFileElementType;
import com.vladsch.idea.multimarkdown.lang.psi.impl.MarkdownFileImpl;
import org.jetbrains.annotations.NotNull;

/**
 * The parser implementation for Markdown.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownParserDefinition implements ParserDefinition {

    /**
     * The {@link MarkdownFileElementType} instance.
     *
     * @see #getFileNodeType()
     */
    protected static final MarkdownFileElementType FILE_ELEMENT_TYPE = new MarkdownFileElementType();

    /**
     * Get the lexer for lexing files in the specified project.
     *
     * @param project the project to which the lexer is connected.
     * @return an {@link EmptyLexer} instance.
     */
    @NotNull
    public Lexer createLexer(Project project) {
        return new EmptyLexer();
    }

    /**
     * Get the parser for parsing files in the specified project.
     *
     * @param project the project to which the parser is connected.
     * @return a {@link MarkdownParser} instance.
     */
    public PsiParser createParser(Project project) {
        return new MarkdownParser();
    }

    /**
     * Get the element type of the node describing a Markdown file.
     *
     * @return {@link #FILE_ELEMENT_TYPE}
     */
    public IFileElementType getFileNodeType() {
        return FILE_ELEMENT_TYPE;
    }

    /**
     * Get the set of token types which are treated as whitespace by the PSI builder.
     *
     * @return {@link TokenSet#EMPTY}
     */
    @NotNull
    public TokenSet getWhitespaceTokens() {
        return TokenSet.EMPTY;
    }

    /**
     * Get the set of token types which are treated as comments by the PSI builder.
     *
     * @return {@link TokenSet#EMPTY}
     */
    @NotNull
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    /**
     * Get the set of element types which are treated as string literals.
     *
     * @return {@link TokenSet#EMPTY}
     */
    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    /**
     * Create a PSI element for the specified AST node.
     *
     * @param node the AST node.
     * @return the PSI element matching the element type of the AST node.
     */
    @NotNull
    public PsiElement createElement(ASTNode node) {
        return new MarkdownPsiElementImpl(node);
    }

    /**
     * Create a PSI element for the specified virtual file.
     *
     * @param viewProvider virtual file.
     * @return the PSI file element.
     */
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new MarkdownFileImpl(viewProvider);
    }

    /**
     * Check if the specified two token types need to be separated by a space according to the language grammar.
     *
     * @param left  the first token to check.
     * @param right the second token to check.
     * @return {@link SpaceRequirements#MAY}
     */
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
