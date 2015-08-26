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

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiMarkdownLexer extends Lexer {

    private static final Logger LOGGER = Logger.getInstance(MultiMarkdownLexer.class);
    protected MultiMarkdownLexParser lexParser = null;
    protected int startOffset = 0;
    protected int endOffset = 0;
    protected int lexemeIndex = 0;
    protected int currentOffset = 0;
    protected CharSequence buffer = null;
    protected MultiMarkdownLexParser.LexerToken lexerToken = null;
    protected MultiMarkdownLexParser.LexerToken[] lexerTokens = null;

    public MultiMarkdownLexer() {
        super();

        lexParser = new MultiMarkdownLexParser();
    }

    @Override public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        lexParser.parseMarkdown(buffer.toString());
        this.currentOffset = this.startOffset = startOffset;
        this.endOffset = endOffset;
        lexemeIndex = initialState;
        lexerTokens = lexParser.getLexerTokens();
        lexerToken = null;
        //System.out.format("start lexer buffer end %d, start %d, end %d, state %d\n", buffer.length(), startOffset, endOffset, initialState);

        advance();
    }

    @Override public int getState() {
        return lexemeIndex;
    }

    @Nullable @Override public IElementType getTokenType() {
        return lexerToken != null ? lexerToken.getElementType() : null;
    }

    @Override public int getTokenStart() {
        return lexerToken != null ? lexerToken.getRange().getStart() : endOffset;
    }

    @Override public int getTokenEnd() {
        return lexerToken != null ? lexerToken.getRange().getEnd() : endOffset;
    }

    @Override public void advance() {
        do {
            if (lexerTokens != null && lexemeIndex >= 0 && lexemeIndex < lexerTokens.length) {
                if (lexerToken == null || currentOffset < lexerToken.getRange().getStart()) {
                    lexerToken = lexParser.getSkippedSpaceToken(currentOffset, lexerTokens[lexemeIndex].getRange().getStart());
                } else {
                    lexerToken = lexerTokens[lexemeIndex];
                    if (currentOffset < lexerToken.getRange().getStart()) {
                        lexerToken = lexParser.getSkippedSpaceToken(currentOffset, lexerToken.getRange().getStart());
                    } else {
                        lexemeIndex++;
                    }
                }
            } else {
                if (currentOffset < endOffset) {
                    lexerToken = lexParser.getSkippedSpaceToken(currentOffset, endOffset);
                } else {
                    lexerToken = null;
                }
            }
        } while (lexerToken != null && lexerToken.getRange().getEnd() < currentOffset);

        currentOffset = lexerToken == null ? endOffset : lexerToken.getRange().getEnd();

        //System.out.print("advanced to " + currentOffset + " (" + (lexerToken == null ? "null" : lexerToken.toString()) + ")\n");
    }

    class MarkdownLexerPosition implements LexerPosition {

        protected int offset;
        protected int state;

        MarkdownLexerPosition(int offset, int state) {
            this.offset = offset;
            this.state = state;
        }

        @Override public int getOffset() {
            return offset;
        }

        @Override public int getState() {
            return state;
        }
    }

    @NotNull @Override public LexerPosition getCurrentPosition() {
        return new MarkdownLexerPosition(currentOffset, lexemeIndex);
    }

    @Override public void restore(@NotNull LexerPosition lexerPosition) {
        currentOffset = lexerPosition.getOffset();
        lexemeIndex = lexerPosition.getState();
        lexerToken = null;
        advance();
    }

    @NotNull @Override public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override public int getBufferEnd() {
        return buffer.length();
    }
}
