// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.lexer.RestartableLexer;
import com.intellij.lexer.TokenIterator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.parser.ast.MdASTNode;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

import static com.intellij.openapi.diagnostic.Logger.getInstance;
import static com.vladsch.md.nav.psi.util.MdTypes.BULLET_LIST;
import static com.vladsch.md.nav.psi.util.MdTypes.DEFINITION_LIST;
import static com.vladsch.md.nav.psi.util.MdTypes.ORDERED_LIST;

public class MdLexer extends Lexer implements RestartableLexer {
    private static final Logger LOG = getInstance("com.vladsch.md.nav.parser");

    protected MdLexemeProcessor myLexemeProcessor = null;

    protected Integer pegdownExtensions = null;
    protected Long parserOptions = null;
    protected LexerData lexerData = null;
    protected final @NotNull MdRenderingProfile renderingProfile;
    protected HashSet<Integer> myFileLevelLexemeStarts = null;

    public LexerData getLexerData() {
        return lexerData;
    }

    public MdLexer(final @NotNull MdRenderingProfile renderingProfile) {
        this(renderingProfile, null, null);
    }

    public MdLexer(final @NotNull MdRenderingProfile renderingProfile, @Nullable Integer pegdownExtensions, @Nullable Long parserOptions) {
        super();

        this.pegdownExtensions = pegdownExtensions;
        this.parserOptions = parserOptions;
        this.renderingProfile = renderingProfile;
    }

    protected void logStackTrace() {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement traceElement : traceElements) {
            if (LOG.isDebugEnabled())
                LOG.debug(traceElement.getMethodName() + " at " + traceElement.getFileName() + ":" + traceElement.getLineNumber());
        }
    }

    @Override
    public int getStartState() {
        return 0;
    }

    @Override
    public boolean isRestartableState(final int state) {
        return state == 0;
    }

    @Override
    public void start(@NotNull final CharSequence buffer, final int startOffset, final int endOffset, final int initialState, final TokenIterator tokenIterator) {
        start(buffer, startOffset, endOffset, initialState);
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        LexerToken[] lexerTokens = null;

        if (buffer.length() > 0) {
            lexerData = MdLexParserManager.parseMarkdown(renderingProfile, buffer.subSequence(startOffset, endOffset), pegdownExtensions, parserOptions);
            lexerTokens = lexerData.lexerTokens;
        }

        myLexemeProcessor = new MdLexemeProcessor(buffer, lexerTokens, startOffset, endOffset, initialState);
        myFileLevelLexemeStarts = null;
    }

    // this is only valid when parsing the whole file, for reparse highly depends on restart being respected which it is not
    // the IDE assumes that all needed information is stored in state
    boolean isFileLevelOffset(int lexemeStart) {
        if (lexerData != null) {
            if (myFileLevelLexemeStarts == null) {
                // compute file level lexeme start offsets
                myFileLevelLexemeStarts = new HashSet<>();

                // add all root level starts
                for (MdASTNode astNode : lexerData.rootNode.getChildren()) {
                    myFileLevelLexemeStarts.add(astNode.getStartOffset());

                    // cannot work all the time, first item of a list can be significant for indentation purposes.
                    // Only real top level items can be re-parsed without preceding text but this makes files with long
                    // lists too slow.
                    // as an alternative can check if item matches first item's indentation level then it can also be started from without
                    // affecting indentation of other items
                    IElementType type = astNode.getElementType();
                    if (type == BULLET_LIST || type == ORDERED_LIST || type == DEFINITION_LIST) {
                        // these are holders of items so pass through first children
                        int firstItemIndent = -1;
                        for (MdASTNode itemNode : astNode.getChildren()) {
                            int startOffset = itemNode.getStartOffset();
                            int itemStart = myLexemeProcessor.getLineColumn(startOffset);

                            if (firstItemIndent == -1 || firstItemIndent == itemStart || type == DEFINITION_LIST) {
                                // this is either the first item or has the same indentation, we can start parsing from it
                                firstItemIndent = itemStart;
                                myFileLevelLexemeStarts.add(startOffset);
                            }
                        }
                    }
                }
            }
            return myFileLevelLexemeStarts.contains(lexemeStart);
        }
        return true;
    }

    @Override
    public int getState() {
        LexerToken lexerToken = myLexemeProcessor.getLexerToken();

        // here we need to return 0 in low bit when the file can be re-parsed from this point, ie. file level elements
        return (lexerToken != null && isFileLevelOffset(lexerToken.getRange().getStart()) ? 0 : 1);
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        LexerToken lexerToken = myLexemeProcessor.getLexerToken();

        return lexerToken != null ? lexerToken.getElementType() : null;
    }

    @Override
    public int getTokenStart() {
        LexerToken lexerToken = myLexemeProcessor.getLexerToken();

        return lexerToken != null ? lexerToken.getRange().getStart() + myLexemeProcessor.getStartOffset() : myLexemeProcessor.getEndOffset();
    }

    @Override
    public int getTokenEnd() {
        LexerToken lexerToken = myLexemeProcessor.getLexerToken();

        return lexerToken != null ? lexerToken.getRange().getEnd() + myLexemeProcessor.getStartOffset() : myLexemeProcessor.getEndOffset();
    }

    @Override
    public void advance() {
        myLexemeProcessor.advance();
    }

    static class MarkdownLexerPosition implements LexerPosition {
        protected int offset;
        protected int state;

        MarkdownLexerPosition(int offset, int state) {
            this.offset = offset;
            this.state = state;
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
        return new MarkdownLexerPosition(myLexemeProcessor.getCurrentOffset(), myLexemeProcessor.getLexemeIndex());
    }

    @Override
    public void restore(@NotNull LexerPosition lexerPosition) {
        myLexemeProcessor.restore(lexerPosition);
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return myLexemeProcessor.getBuffer();
    }

    @Override
    public int getBufferEnd() {
        return myLexemeProcessor.getBufferEnd();
    }
}
