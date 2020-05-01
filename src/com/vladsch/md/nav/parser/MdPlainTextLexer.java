// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.lexer.RestartableLexer;
import com.intellij.lexer.TokenIterator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.vladsch.md.nav.psi.util.MdTypes.COMMENT_CLOSE;
import static com.vladsch.md.nav.psi.util.MdTypes.COMMENT_OPEN;
import static com.vladsch.md.nav.psi.util.MdTypes.COMMENT_TEXT;
import static com.vladsch.md.nav.psi.util.MdTypes.TEXT;

public class MdPlainTextLexer extends Lexer implements RestartableLexer {
    public static final String HTML_COMMENT_OPEN = "<!--";
    public static final int HTML_COMMENT_OPEN_LENGTH = HTML_COMMENT_OPEN.length();
    public static final String HTML_COMMENT_CLOSE = "-->";
    public static final int HTML_COMMENT_CLOSE_LENGTH = HTML_COMMENT_CLOSE.length();

    protected int startOffset = 0;
    protected int endOffset = 0;
    protected int lexemeStart = 0;
    protected int lexemeEnd = 0;
    protected int currentOffset = 0;
    protected CharSequence buffer = null;
    protected IElementType lexemeType = null;

    public MdPlainTextLexer() {
        super();
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.lexemeStart = this.lexemeEnd = this.currentOffset = this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.lexemeType = null;
        //System.out.format("start plain text lexer buffer end %d, start %d, end %d, state %d\n", buffer.length(), startOffset, endOffset, initialState);
        advance();
    }

    @Override
    public int getState() {
        if (lexemeType == COMMENT_OPEN) return 0;
        else if (lexemeType == COMMENT_TEXT) return 1;
        else if (lexemeType == COMMENT_CLOSE) return 2;
        else return 0;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return lexemeStart >= lexemeEnd ? null : lexemeType;
    }

    @Override
    public int getTokenStart() {
        return lexemeStart;
    }

    @Override
    public int getTokenEnd() {
        return lexemeEnd;
    }

    @Override
    public int getStartState() {
        return 0;
    }

    @Override
    public boolean isRestartableState(int state) {
        return state == 0;
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState, TokenIterator tokenIterator) {
        start(buffer, startOffset, endOffset, initialState);
    }

    private boolean isCommentStart(int offset) {
        if (offset < 0 || offset + 3 >= endOffset) return false;
        return buffer.charAt(offset + 0) == '<' &&
                buffer.charAt(offset + 1) == '!' &&
                buffer.charAt(offset + 2) == '-' &&
                buffer.charAt(offset + 3) == '-';
    }

    private boolean isCommentEnd(int offset) {
        if (offset < 0 || offset + 2 >= endOffset) return false;
        return buffer.charAt(offset + 0) == '-' &&
                buffer.charAt(offset + 1) == '-' &&
                buffer.charAt(offset + 2) == '>';
    }

    @Override
    public void advance() {
        if (lexemeType == COMMENT_OPEN) {
            // we are in comment text, take to comment end or end of file
            if (isCommentEnd(currentOffset)) {
                // no text
                lexemeStart = lexemeEnd;
                currentOffset += HTML_COMMENT_CLOSE_LENGTH;
                lexemeEnd = currentOffset;
                lexemeType = COMMENT_CLOSE;
            } else {
                while (true) {
                    if (currentOffset < endOffset) {
                        if (isCommentEnd(currentOffset)) {
                            lexemeStart = lexemeEnd;
                            lexemeEnd = currentOffset;
                            lexemeType = COMMENT_TEXT;
                            break;
                        }
                    } else {
                        lexemeStart = lexemeEnd;
                        currentOffset = lexemeEnd = endOffset;
                        lexemeType = COMMENT_TEXT;
                        break;
                    }
                    currentOffset++;
                }
            }
        } else if (lexemeType == COMMENT_TEXT) {
            // we must be in comment end or end of file
            if (isCommentEnd(currentOffset)) {
                lexemeStart = lexemeEnd;
                currentOffset += HTML_COMMENT_CLOSE_LENGTH;
                lexemeEnd = currentOffset;
                lexemeType = COMMENT_CLOSE;
            } else {
                lexemeStart = lexemeEnd;
                currentOffset = lexemeEnd = endOffset;
                lexemeType = COMMENT_CLOSE;
            }
        } else if (isCommentStart(currentOffset)) {
            lexemeStart = lexemeEnd;
            currentOffset += HTML_COMMENT_OPEN_LENGTH;
            lexemeEnd = currentOffset;
            lexemeType = COMMENT_OPEN;
        } else {
            // take to comment start or end of file
            while (true) {
                if (currentOffset < endOffset) {
                    if (isCommentStart(currentOffset)) {
                        lexemeStart = lexemeEnd;
                        lexemeEnd = currentOffset;
                        lexemeType = TEXT;
                        break;
                    }
                } else {
                    lexemeStart = lexemeEnd;
                    currentOffset = lexemeEnd = endOffset;
                    lexemeType = TEXT;
                    break;
                }
                currentOffset++;
            }
        }

        //System.out.print("plain text advanced to " + currentOffset + " (" + (getTokenType() == null ? "null" : getTokenType().toString()) + ")\n");
    }

    static class MarkdownLexerPosition implements LexerPosition {

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

        @Override
        public int getOffset() {
            return offset;
        }

        @Override
        public int getState() {
            return state;
        }
    }

    @NotNull
    @Override
    public LexerPosition getCurrentPosition() {
        return new MarkdownLexerPosition(currentOffset, 0, lexemeStart, lexemeEnd);
    }

    @Override
    public void restore(@NotNull LexerPosition lexerPosition) {
        currentOffset = lexerPosition.getOffset();
        lexemeStart = ((MarkdownLexerPosition) lexerPosition).getStart();
        lexemeEnd = ((MarkdownLexerPosition) lexerPosition).getEnd();
        if (isCommentStart(lexemeStart) && lexemeEnd == lexemeStart + HTML_COMMENT_OPEN_LENGTH) lexemeType = COMMENT_OPEN;
        else if (isCommentStart(lexemeStart - HTML_COMMENT_OPEN_LENGTH) && isCommentEnd(lexemeEnd)) lexemeType = COMMENT_TEXT;
        else if (isCommentEnd(lexemeStart) && lexemeEnd == lexemeStart + HTML_COMMENT_CLOSE_LENGTH) lexemeType = COMMENT_CLOSE;
        else lexemeType = TEXT;
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return buffer.length();
    }
}
