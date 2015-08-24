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

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.psi.MarkdownTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MarkdownPlainTextLexer extends Lexer {

    private static final Logger LOGGER = Logger.getInstance(MarkdownPlainTextLexer.class);
    protected int startOffset = 0;
    protected int endOffset = 0;
    protected int lexemeStart = 0;
    protected int lexemeEnd = 0;
    protected int currentOffset = 0;
    protected CharSequence buffer = null;

    public MarkdownPlainTextLexer() {
        super();
    }

    @Override public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.lexemeStart = this.lexemeEnd = this.currentOffset = this.startOffset = startOffset;
        this.endOffset = endOffset;
        //System.out.format("start plain text lexer buffer end %d, start %d, end %d, state %d\n", buffer.length(), startOffset, endOffset, initialState);
        advance();
    }

    @Override public int getState() {
        return 0;
    }

    @Nullable @Override public IElementType getTokenType() {
        return lexemeStart < lexemeEnd ? MarkdownTypes.COMMENT : null;
    }

    @Override public int getTokenStart() {
        return lexemeStart;
    }

    @Override public int getTokenEnd() {
        return lexemeEnd;
    }

    @Override public void advance() {
        while (true) {
            if (currentOffset < endOffset) {
                if (buffer.charAt(currentOffset) == '\n') {
                    lexemeStart = lexemeEnd;
                    currentOffset++;
                    lexemeEnd = currentOffset;
                    break;
                }
            } else {
                lexemeStart = lexemeEnd;
                currentOffset = lexemeEnd = endOffset;
                break;
            }
            currentOffset++;
        }
        //System.out.print("plain text advanced to " + currentOffset + " (" + (getTokenType() == null ? "null" : getTokenType().toString()) + ")\n");
    }

    class MarkdownLexerPosition implements LexerPosition {

        protected int offset;
        protected int state;
        protected int start;
        protected int end;

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        MarkdownLexerPosition(int offset, int state, int start, int end) {
            this.offset = offset;
            this.state = state;
            this.start = start;
            this.end = end;
        }

        @Override public int getOffset() {
            return offset;
        }

        @Override public int getState() {
            return state;
        }
    }

    @NotNull @Override public LexerPosition getCurrentPosition() {
        return new MarkdownLexerPosition(currentOffset, 0, lexemeStart, lexemeEnd);
    }

    @Override public void restore(@NotNull LexerPosition lexerPosition) {
        currentOffset = lexerPosition.getOffset();
        lexemeStart = ((MarkdownLexerPosition)lexerPosition).getStart();
        lexemeEnd = ((MarkdownLexerPosition)lexerPosition).getEnd();
    }

    @NotNull @Override public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override public int getBufferEnd() {
        return buffer.length();
    }
}
