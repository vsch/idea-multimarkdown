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
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.vladsch.idea.multimarkdown.MarkdownLanguage;
import com.vladsch.idea.multimarkdown.psi.MarkdownFile;
import com.vladsch.idea.multimarkdown.psi.MarkdownTokenTypeSets;
import com.vladsch.idea.multimarkdown.psi.MarkdownTypes;
import org.jetbrains.annotations.NotNull;

public class MarkdownParserDefinition implements ParserDefinition {

    public static final TokenSet WHITE_SPACES = TokenSet.create(MarkdownTypes.NONE);
    public static final TokenSet TODO_COMMENT_SET = TokenSet.create(
            MarkdownTypes.DEFINITION, MarkdownTypes.DEFINITION_TERM,
            MarkdownTypes.TABLE_CELL_REVEN_CEVEN, MarkdownTypes.TABLE_CELL_REVEN_CODD, MarkdownTypes.TABLE_CELL_RODD_CEVEN, MarkdownTypes.TABLE_CELL_RODD_CODD,
            MarkdownTypes.CODE, MarkdownTypes.VERBATIM,
            MarkdownTypes.BLOCK_QUOTE,
            MarkdownTypes.COMMENT,
            MarkdownTypes.TEXT
    );

    public static final IFileElementType FILE = new IFileElementType(Language.<MarkdownLanguage>findInstance(MarkdownLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new MarkdownLexer();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return MarkdownTokenTypeSets.COMMENT_SET;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new MarkdownParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new MarkdownFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        // TODO: this is dependent on the type of ASTNodes, some can and others cannot have spaces or breaks
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return MarkdownTypes.Factory.createElement(node);
    }
}
