// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.TokenSet;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.sequence.Range;
import com.vladsch.md.nav.parser.ast.MdASTCompositeNode;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.settings.MdProjectSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.plugin.util.psi.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Lexer/Parser Combination that uses pegdown behind the scenes to do the heavy lifting here we just fake everything.
 */
public class MdLexParser {
    public static final long PUML_FENCED_CODE = 0x100000000L; // plant UML
    public static final long PLANTUML_FENCED_CODE = 0x80000000L; // plant UML
    public static final long GITHUB_WIKI_LINKS = 0x40000000L;
    public static final long JEKYLL_FRONT_MATTER = 0x20000000L;
    public static final long EMOJI_SHORTCUTS = 0x10000000L;
    public static final long HEADER_ID_REF_TEXT_TRIM_TRAILING_SPACES = 0x08000000L; // trim trailing spaces in ref text in headings used for heading id 
    public static final long GITBOOK_URL_ENCODING = 0x04000000L; //exclude + from link url encode/decode
    public static final long FLEXMARK_FRONT_MATTER = 0x00200000L; //implements Lexer, PsiParser {
    public static final long SPACE_IN_LINK_URLS = 0x00100000L; // allow spaces in urls and convert them to %20 in html
    public static final long PRODUCTION_SPEC_PARSER = 0x000800000L; // override test mode for spec parsing and use production spec example settings
    public static final long NOT_USED_3 = 0x000400000L; // THIS IS NOT USED
    public static final long GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA = 0x00020000L; //gfm loose item rules
    public static final long SIM_TOC_BLANK_LINE_SPACER = 0x00010000L; //use sim toc blank line spacer
    public static final long GFM_TABLE_RENDERING = 0x00008000L; //use gfm rules for table rendering
    public static final long GITHUB_LISTS = 0x00004000L; //use fixed rules for list indentation
    public static final long COMMONMARK_LISTS = 0x00002000L; //use commonmark rules for list indentation
    public static final long JIRA_CONVERSION = 0x00001000L; //JIRA conversion
    public static final long YOU_TRACK_CONVERSION = 0x00000800L; //JIRA conversion
    public static final long ATTRIBUTES_EXT = 0x00000400L; // { } attributes extension
    public static final long ENUMERATED_REFERENCES_EXT = 0x00000200L; // enumerated references [@type:id] & [#type:id]
    public static final long HEADER_ID_NO_DUPED_DASHES = 0x00000100L; // generate heading ids without duplicated dashes
    public static final long PARSE_HTML_ANCHOR_ID = 0x00000080L; // parse HTML <a> tags inlines for ID attribute
    public static final long NO_TEXT_ATTRIBUTES = 0x00000040L; // no text attributes
    public static final long ADMONITION_EXT = 0x00000020L; // admonition extension
    public static final long GITLAB_EXT = 0x00000010L; // gitlab extension
    public static final long GITLAB_MATH_EXT = 0x00000008L; // gitlab math extension
    public static final long GITLAB_MERMAID_EXT = 0x00000004L; // gitlab mermaid extension
    public static final long MACROS_EXT = 0x00000002L; // macros
    public static final long HEADER_ID_NON_ASCII_TO_LOWERCASE = 0x00000001L; // heading id

    public static final String EXAMPLE_END = "````````````````````````````````";
    public static final String EXAMPLE_START_PREFIX = EXAMPLE_END + " example";
    public static final String EXAMPLE_TYPE_BREAK = ".";
    public static final String EXAMPLE_TEST_END = "````````````````";

    public static final LexerToken[] EMPTY_TOKENS = new LexerToken[0];
    public static final LexerData EMPTY_DATA = new LexerData(EMPTY_TOKENS, new MdASTCompositeNode(MdTypes.WHITESPACE, 0, 0));

    public static MdLexer createLexer(Project project) {
        final MdRenderingProfile renderingProfile = project == null ? MdRenderingProfile.getDEFAULT() : MdProjectSettings.getInstance(project).getRenderingProfile();
        return new MdLexer(renderingProfile);
    }

    @NotNull
    public static LexerData parseFlexmarkMarkdown(@Nullable final Document document, final int extensionFlags, long parserOptions) {
        if (document == null) return EMPTY_DATA;

        LexParserFlexmarkASTVisitor visitor = new LexParserFlexmarkASTVisitor(document);
        visitor.build();
        ArrayList<LexerToken> lexerTokens = visitor.getTokens();

        LexerToken[] tokens = getData(lexerTokens);
        return tokens == null ? EMPTY_DATA : new LexerData(tokens, visitor.getRootASTNode());
    }

    @Nullable
    public static LexerToken[] getData(ArrayList<LexerToken> lexerTokens) {
        LexerToken[] tokens = new LexerToken[lexerTokens.size()];
        tokens = lexerTokens.toArray(tokens);
        TokenSet NON_MERGE_TOKEN_SET = LexParserState.getInstance().getState().NON_MERGE_TOKEN_SET;

        if (tokens.length > 0) {
            Arrays.sort(tokens);

            // now need to step through and merge consecutive tokens
            int iMax = tokens.length;
            LexerToken thisToken = tokens[0];
            lexerTokens = new ArrayList<LexerToken>(iMax);

            for (int i = 1; i < iMax; i++) {
                LexerToken thatToken = tokens[i];

                // same region, keep deepest child since it did not get excluded by the parent so it should survive
                // QUERY: is this still valid?
                // FIX: in reality here we only get exact duplicates, need to trace where they are generated
                if (thisToken.getRange().equals(thatToken.getRange())) {
                    if (thisToken.getNesting() < thatToken.getNesting()) thisToken = thatToken;
                    continue;
                }

                if (!thatToken.doesExtend(thisToken) || PsiUtils.isTypeOf(thisToken.getElementType(), NON_MERGE_TOKEN_SET)) {
                    lexerTokens.add(thisToken);
                    thisToken = thatToken;
                } else {
                    thisToken.setRange(thisToken.getRange().expandToInclude(thatToken.getRange()));
                }
            }
            lexerTokens.add(thisToken);

            // now we generate lexemes from the combined optimized tokens
            tokens = new LexerToken[lexerTokens.size()];
            tokens = lexerTokens.toArray(tokens);

            // we create a list of non-intersecting, sorted, ranges
            tokens = splitLexerTokens(tokens);
        }

        return tokens;
    }

    protected static
    @Nullable
    LexerToken[] splitLexerTokens(@NotNull LexerToken[] tokens) {
        int end = tokens.length;

        if (end > 0) {
            ArrayList<LexerToken> lexemes = new ArrayList<LexerToken>(tokens.length);

            // do all of them
            splitLexemes(lexemes, tokens, 0, Integer.MAX_VALUE);
            LexerToken[] lexerTokens = new LexerToken[lexemes.size()];
            return lexemes.toArray(lexerTokens);
        }
        return null;
    }

    protected static int splitLexemes(@NotNull ArrayList<LexerToken> lexemes, @NotNull LexerToken[] tokens, int start, int rangeEnd) {
        LexerToken token = tokens[start];
        Range range = token.getRange();
        com.vladsch.flexmark.util.sequence.Range range1;

        if (range.getEnd() <= rangeEnd) {
            int end = tokens.length;

            start++;
            for (; start < end && (range1 = tokens[start].getRange()).getEnd() <= rangeEnd; start++) {
                if (range.compare(range1) <= 0 && (range.doesNotOverlap(range1) || range.equals(range1))) {
                    // it comes before, add it, if it equals, then skip it
                    if (!range.equals(range1)) {
                        if (range.equals(token.getRange())) {
                            lexemes.add(token);
                        } else {
                            LexerToken newToken = new LexerToken(range, token.getElementType());
                            lexemes.add(newToken);
                        }
                        token = tokens[start];
                        range = token.getRange();
                    }
                } else if (!range.doesContain(range1)) {
                    lexemes.add(token);
                    token = tokens[start];
                    range = token.getRange();
                } else {
                    if (range.doesOverlap(range1) && !range.doesContain(range1)) {
                        // split the range and continue if it contains it, then skip it
                        com.vladsch.flexmark.util.sequence.Range newRange = range.withEnd(range1.getStart());
                        if (newRange.isEmpty()) {
                            token = tokens[start];
                            range = token.getRange();
                        } else {
                            LexerToken newToken = new LexerToken(newRange, token.getElementType());
                            lexemes.add(newToken);

                            range = range.withStart(range1.getEnd());
                            if (range.isEmpty()) {
                                token = tokens[start];
                                range = token.getRange();
                            } else {
                                start = splitLexemes(lexemes, tokens, start, range.getStart());
                            }
                        }
                    }
                }
            }

            if (!range.isEmpty()) {
                if (range.equals(token.getRange())) {
                    lexemes.add(token);
                } else {
                    LexerToken newToken = new LexerToken(range, token.getElementType());
                    lexemes.add(newToken);
                }
            }
        }
        return start;
    }
}
