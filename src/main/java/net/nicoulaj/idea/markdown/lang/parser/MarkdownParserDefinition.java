/*
 * Copyright (c) 2011 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import net.nicoulaj.idea.markdown.file.MarkdownFileElementType;
import net.nicoulaj.idea.markdown.lang.lexer.MarkdownLexer;
import net.nicoulaj.idea.markdown.lang.psi.MarkdownPsiCreator;
import net.nicoulaj.idea.markdown.lang.psi.impl.MarkdownFileImpl;
import org.jetbrains.annotations.NotNull;

/**
 * TODO Add Javadoc comment.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownParserDefinition implements ParserDefinition {

    /**
     * TODO Add Javadoc comment.
     */
    protected static final MarkdownFileElementType FILE_ELEMENT_TYPE = new MarkdownFileElementType();

    /**
     * TODO Add Javadoc comment.
     *
     * @param project TODO Add Javadoc comment.
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public Lexer createLexer(Project project) {
        return new MarkdownLexer();
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @param project TODO Add Javadoc comment.
     * @return TODO Add Javadoc comment.
     */
    public PsiParser createParser(Project project) {
        return new MarkdownParser();
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    public IFileElementType getFileNodeType() {
        return FILE_ELEMENT_TYPE;
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public TokenSet getWhitespaceTokens() {
        return TokenSet.EMPTY;
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @param node TODO Add Javadoc comment.
     * @return TODO Add Javadoc comment.
     */
    @NotNull
    public PsiElement createElement(ASTNode node) {
        return MarkdownPsiCreator.createElement(node);
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @param viewProvider TODO Add Javadoc comment.
     * @return TODO Add Javadoc comment.
     */
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new MarkdownFileImpl(viewProvider);
    }

    /**
     * TODO Add Javadoc comment.
     *
     * @param left  TODO Add Javadoc comment.
     * @param right TODO Add Javadoc comment.
     * @return TODO Add Javadoc comment.
     */
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
