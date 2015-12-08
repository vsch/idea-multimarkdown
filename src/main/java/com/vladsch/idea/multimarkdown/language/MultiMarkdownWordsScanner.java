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
package com.vladsch.idea.multimarkdown.language;

import com.intellij.lang.cacheBuilder.VersionedWordsScanner;
import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Processor;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * The default implementation of a words scanner based on a custom language lexer.
 *
 * @author max
 */

public class MultiMarkdownWordsScanner extends VersionedWordsScanner {
    private static final Logger logger = Logger.getLogger(MultiMarkdownWordsScanner.class);

    private final Lexer myLexer;
    private final TokenSet myIdentifierTokenSet;
    private final TokenSet myCommentTokenSet;
    private final TokenSet myLiteralTokenSet;
    private final TokenSet mySkipCodeContextTokenSet;
    private boolean myMayHaveFileRefsInLiterals;
    private boolean myKeepCodeTokensWhole;
    private boolean myUseSpaceBreaks;
    private WordOccurrence.Kind myDefaultKind;
    final private int myVersion;

    /**
     * Creates a new instance of the words scanner.
     *
     * @param lexer              the lexer used for breaking the text into tokens.
     * @param identifierTokenSet the set of token types which represent identifiers.
     * @param commentTokenSet    the set of token types which represent comments.
     * @param literalTokenSet    the set of token types which represent literals.
     */
    public MultiMarkdownWordsScanner(final Lexer lexer, final TokenSet identifierTokenSet, final TokenSet commentTokenSet,
            final TokenSet literalTokenSet, int version) {
        this(lexer, identifierTokenSet, commentTokenSet, literalTokenSet, TokenSet.EMPTY, version);
    }

    /**
     * Creates a new instance of the words scanner.
     *
     * @param lexer                   the lexer used for breaking the text into tokens.
     * @param identifierTokenSet      the set of token types which represent identifiers.
     * @param commentTokenSet         the set of token types which represent comments.
     * @param literalTokenSet         the set of token types which represent literals.
     * @param skipCodeContextTokenSet the set of token types which should not be considered as code context.
     */
    public MultiMarkdownWordsScanner(final Lexer lexer, final TokenSet identifierTokenSet, final TokenSet commentTokenSet,
            final TokenSet literalTokenSet, @NotNull TokenSet skipCodeContextTokenSet, int version) {
        myLexer = lexer;
        myIdentifierTokenSet = identifierTokenSet;
        myCommentTokenSet = commentTokenSet;
        myLiteralTokenSet = literalTokenSet;
        mySkipCodeContextTokenSet = skipCodeContextTokenSet;
        myDefaultKind = WordOccurrence.Kind.CODE;
        myVersion = version;
    }

    @Override public int getVersion() {
        return myVersion;
    }

    public void processWords(CharSequence fileText, Processor<WordOccurrence> processor) {
        myLexer.start(fileText);
        WordOccurrence occurrence = new WordOccurrence(fileText, 0, 0, null); // shared occurrence

        IElementType type;
        while ((type = myLexer.getTokenType()) != null) {
            if (myIdentifierTokenSet.contains(type)) {
                if (!stripWords(processor, fileText, myLexer.getTokenStart(), myLexer.getTokenEnd(), WordOccurrence.Kind.CODE, occurrence, false, myKeepCodeTokensWhole, myUseSpaceBreaks)) return;
            } else if (myCommentTokenSet.contains(type)) {
                if (!stripWords(processor, fileText, myLexer.getTokenStart(), myLexer.getTokenEnd(), WordOccurrence.Kind.COMMENTS, occurrence, false, false, myUseSpaceBreaks)) return;
            } else if (myLiteralTokenSet.contains(type)) {
                if (!stripWords(processor, fileText, myLexer.getTokenStart(), myLexer.getTokenEnd(), WordOccurrence.Kind.LITERALS, occurrence, myMayHaveFileRefsInLiterals, false, myUseSpaceBreaks)) return;
            } else if (myDefaultKind != null && !mySkipCodeContextTokenSet.contains(type)) {
                if (!stripWords(processor, fileText, myLexer.getTokenStart(), myLexer.getTokenEnd(), myDefaultKind, occurrence, false, myDefaultKind == WordOccurrence.Kind.CODE && myKeepCodeTokensWhole, myUseSpaceBreaks)) return;
            }
            myLexer.advance();
        }
    }

    protected static boolean stripWords(final Processor<WordOccurrence> processor,
            final CharSequence tokenText,
            int from,
            int to,
            final WordOccurrence.Kind kind,
            @NotNull WordOccurrence occurrence,
            boolean mayHaveFileRefs,
            boolean keepTokensWhole,
            boolean useSpaceBreaks
    ) {
        // This code seems strange but it is more effective as Character.isJavaIdentifier_xxx_ is quite costly operation due to unicode
        int index = from;
        StringBuilder tokens = new StringBuilder(to - from + 100);
        StringBuilder foreign = new StringBuilder(to - from + 100);
        try {
            if (keepTokensWhole) {
                tokens.append('\'');
                tokens.append(tokenText.subSequence(from, to));
                tokens.append('\'');
                tokens.append(' ');
                occurrence.init(tokenText, from, to, kind);
                if (!processor.process(occurrence)) return false;

                if (mayHaveFileRefs) {
                    foreign.append('\'');
                    foreign.append(tokenText.subSequence(from, to));
                    foreign.append('\'');
                    foreign.append(' ');
                    occurrence.init(tokenText, from, to, WordOccurrence.Kind.FOREIGN_LANGUAGE);
                    if (!processor.process(occurrence)) return false;
                }
            } else {
ScanWordsLoop:
                while (true) {
                    while (true) {
                        if (index == to) break ScanWordsLoop;
                        char c = tokenText.charAt(index);
                        if ((useSpaceBreaks && c != ' ' && c != '\n' && c != '\t') || isAsciiIdentifierPart(c) || Character.isJavaIdentifierStart(c)) {
                            break;
                        }
                        index++;
                    }
                    int wordStart = index;
                    while (true) {
                        index++;
                        if (index == to) break;
                        char c = tokenText.charAt(index);
                        if (c == ' ' || c == '\n' || c == '\t') break;
                        if (useSpaceBreaks || isAsciiIdentifierPart(c)) continue;
                        if (!Character.isJavaIdentifierPart(c)) break;
                    }
                    int wordEnd = index;

                    tokens.append('\'');
                    tokens.append(tokenText.subSequence(wordStart, wordEnd));
                    tokens.append('\'');
                    tokens.append(' ');
                    occurrence.init(tokenText, wordStart, wordEnd, kind);
                    if (!processor.process(occurrence)) return false;

                    if (mayHaveFileRefs) {
                        foreign.append('\'');
                        foreign.append(tokenText.subSequence(wordStart, wordEnd));
                        foreign.append('\'');
                        foreign.append(' ');
                        occurrence.init(tokenText, wordStart, wordEnd, WordOccurrence.Kind.FOREIGN_LANGUAGE);
                        if (!processor.process(occurrence)) return false;
                    }
                }
            }
            return true;
        } finally {
            if (tokens.length() > 0) {
                logger.info(kind.toString() + ": " + tokens.subSequence(0, Math.min(tokens.length(), 100)).toString());
            } else {
                logger.info(" no " + kind.toString() + ": " + "tokens in " + tokenText.subSequence(from, Math.min(to, from + 100)));
            }

            if (foreign.length() > 0) {
                logger.info(kind.toString() + ": " + tokens.subSequence(0, Math.min(foreign.length(), 100)).toString());
            }
        }
    }

    private static boolean isAsciiIdentifierPart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '$';
    }

    public void setMayHaveFileRefsInLiterals(final boolean mayHaveFileRefsInLiterals) {
        myMayHaveFileRefsInLiterals = mayHaveFileRefsInLiterals;
    }

    public void setKeepCodeTokensWhole(boolean keepCodeTokensWhole) {
        this.myKeepCodeTokensWhole = keepCodeTokensWhole;
    }

    public void setUseSpaceBreaks(boolean useSpaceBreaks) {
        this.myUseSpaceBreaks = useSpaceBreaks;
    }

    public void setDefaultKind(WordOccurrence.Kind defaultKind) {
        this.myDefaultKind = defaultKind;
    }
}

