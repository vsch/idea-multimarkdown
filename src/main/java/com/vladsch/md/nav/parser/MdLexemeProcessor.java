// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.lexer.LexerPosition;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.util.sequence.Range;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.diagnostic.Logger.getInstance;
import static com.vladsch.md.nav.psi.util.MdTypes.ASIDE_BLOCK_WHITESPACE;
import static com.vladsch.md.nav.psi.util.MdTypes.BLANK_LINE;
import static com.vladsch.md.nav.psi.util.MdTypes.BLOCK_QUOTE_WHITESPACE;
import static com.vladsch.md.nav.psi.util.MdTypes.EOL;
import static com.vladsch.md.nav.psi.util.MdTypes.VERBATIM_CONTENT;
import static com.vladsch.md.nav.psi.util.MdTypes.WHITESPACE;

public class MdLexemeProcessor {
    private static final Logger LOG = getInstance("com.vladsch.md.nav.parser");

    final protected CharSequence buffer;
    final protected LexerToken[] lexerTokens;

    protected LexerToken lexerToken = null;
    protected int startOffset = 0;
    protected int endOffset = 0;
    protected int lexemeIndex = 0;
    protected int currentOffset = 0;
    protected LexerToken prevToken = null;

    public MdLexemeProcessor(final CharSequence buffer, final LexerToken[] lexerTokens, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.lexerTokens = lexerTokens;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.currentOffset = this.startOffset;
        this.lexemeIndex = initialState;
        initialize();
    }

    protected void logStackTrace() {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement traceElement : traceElements) {
            if (LOG.isDebugEnabled())
                LOG.debug(traceElement.getMethodName() + " at " + traceElement.getFileName() + ":" + traceElement.getLineNumber());
        }
    }

    public CharSequence getBuffer() {
        return buffer;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public int getLexemeIndex() {
        return lexemeIndex;
    }

    public int getCurrentOffset() {
        return currentOffset;
    }

    public LexerToken getPrevToken() {
        return prevToken;
    }

    private void initialize() {
        lexerToken = null;
        LOG.debug(String.format("start lexer buffer end %d, start %d, end %d, state %d", buffer.length(), startOffset, endOffset, lexemeIndex));

        // prime the lexeme stream, if the first is white space we need to start with that
        if (lexerTokens != null && lexerTokens.length > lexemeIndex) {
            // lexemeIndex needs to be advanced if currentOffset > 0
            if (currentOffset > 0) {
                while (lexemeIndex < lexerTokens.length) {
                    LexerToken token = lexerTokens[lexemeIndex];
                    Range range = token.getRange();
                    if (range.getEnd() + startOffset > currentOffset) {
                        break;
                    }

                    lexemeIndex++;
                }

                if (lexemeIndex >= lexerTokens.length) {
                    lexemeIndex = 0;
                }
            }

            LOG.debug(String.format("start lexer buffer end %d, start %d, end %d, index %d", buffer.length(), startOffset, endOffset, lexemeIndex));

            lexerToken = lexerTokens[lexemeIndex];
            if (currentOffset < lexerToken.getRange().getStart() + startOffset) {
                lexerToken = getNextWhitespace(lexerToken.getRange().getStart() + startOffset);
            } else {
                lexemeIndex++;
            }
        }

        if (lexerToken == null) {
            // create a dummy whitespace token for the whole file
            lexerToken = getNextWhitespace(endOffset);
        }

        currentOffset = lexerToken.getRange().getEnd() + startOffset;

        //assert currentOffset <= endOffset;
        if (currentOffset > endOffset) {
            currentOffset = endOffset;
        }

        LOG.debug("start advanced to " + currentOffset + " (" + (lexerToken == null ? "null" : lexerToken.toString()) + ") \"" + (lexerToken == null ? "" : (buffer.subSequence(lexerToken.getRange().getStart() + startOffset, lexerToken.getRange().getEnd() + startOffset))) + "\"");
    }

    private LexerToken getNextWhitespace(int offset) {
        int pos = currentOffset;
        IElementType tokenType = WHITESPACE;

        IElementType prevTokenType = prevToken == null ? null : prevToken.getElementType();

        // here we also split into block quote and aside whitespace
        while (pos < offset) {
            char c = buffer.charAt(pos);
            if (c == '\n') {
                if (tokenType != WHITESPACE) break;
                pos++;
                tokenType = (prevTokenType == null
                        || prevTokenType == BLANK_LINE
                        || prevTokenType == VERBATIM_CONTENT
                        || prevTokenType == EOL
                        || prevTokenType == BLOCK_QUOTE_WHITESPACE
                        || prevTokenType == ASIDE_BLOCK_WHITESPACE
                ) ? BLANK_LINE : EOL;
                break;
            } else if (c == '>') {
                if (tokenType == ASIDE_BLOCK_WHITESPACE) break;
                tokenType = BLOCK_QUOTE_WHITESPACE;
            } else if (c == '|') {
                if (tokenType == BLOCK_QUOTE_WHITESPACE) break;
                tokenType = ASIDE_BLOCK_WHITESPACE;
            }
            pos++;
        }

        return new LexerToken(currentOffset - startOffset, pos - startOffset, tokenType);
    }

    public LexerToken getLexerToken() {
        return lexerToken;
    }

    public int getLineColumn(int posOffset) {
        int colStart = Math.min(posOffset + startOffset, endOffset);
        int colOffset = colStart;
        while (colOffset-- > startOffset) {
            if (buffer.charAt(colOffset) == '\n') {
                return colStart - colOffset - 1;
            }
        }
        return colStart;
    }

    public void advance() {
        prevToken = lexerToken;
        if (currentOffset < endOffset) {
            do {
                if (lexerTokens != null && lexemeIndex >= 0 && lexemeIndex < lexerTokens.length) {
                    if (lexerToken == null || currentOffset < lexerToken.getRange().getStart() + startOffset) {
                        lexerToken = getNextWhitespace(lexerTokens[lexemeIndex].getRange().getStart() + startOffset);
                    } else {
                        lexerToken = lexerTokens[lexemeIndex];
                        if (currentOffset < lexerToken.getRange().getStart() + startOffset) {
                            lexerToken = getNextWhitespace(lexerToken.getRange().getStart() + startOffset);
                        } else {
                            lexemeIndex++;
                        }
                    }
                } else {
                    if (currentOffset < endOffset) {
                        lexerToken = getNextWhitespace(endOffset);
                    } else {
                        lexerToken = null;
                    }
                }
            } while (lexerToken != null && lexerToken.getRange().getEnd() + startOffset < currentOffset);

            currentOffset = lexerToken == null ? endOffset : lexerToken.getRange().getEnd() + startOffset;
        } else {
            lexerToken = null;
        }

        if (currentOffset > endOffset) {
            lexerToken = null;
            currentOffset = endOffset;
        }

        LOG.debug("advanced to " + currentOffset + " (" + (lexerToken == null ? "null" : lexerToken.toString()) + ") \"" + (lexerToken == null ? "" : (buffer.subSequence(lexerToken.getRange().getStart() + startOffset, lexerToken.getRange().getEnd() + startOffset))) + "\"");
    }

    public void restore(@NotNull LexerPosition lexerPosition) {
        currentOffset = lexerPosition.getOffset();
        lexemeIndex = lexerPosition.getState();
        lexerToken = null;
        prevToken = null;
        advance();
    }

    public CharSequence getBufferSequence() {
        return buffer;
    }

    public int getBufferEnd() {
        return buffer.length();
    }
}
