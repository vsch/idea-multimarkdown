/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
 */
package com.vladsch.idea.multimarkdown.parser;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;
import com.vladsch.idea.multimarkdown.settings.MultiMarkdownGlobalSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.*;

/**
 * Lexer/Parser Combination that uses pegdown behind the scenes to do the heavy lifting
 * here we just fake everything.
 */
public class MultiMarkdownLexParser { //implements Lexer, PsiParser {
    private static final Logger LOGGER = Logger.getInstance(MultiMarkdownLexParser.class);

    private int currentStringLength;
    private char[] currentChars;

    private static HashSet<IElementType> excludedTokenTypes = new HashSet<IElementType>();
    private static Map<IElementType, HashSet<IElementType>> overrideExclusions = new HashMap<IElementType, HashSet<IElementType>>();
    private static Map<IElementType, HashMap<IElementType, IElementType>> combinationSplits = new HashMap<IElementType, HashMap<IElementType, IElementType>>();

    protected ArrayList<SegmentedRange> parentRanges = new ArrayList<SegmentedRange>();
    protected int minStackLevel = 0;
    protected int tableRows = 0;
    protected int rowColumns = 0;

    protected static boolean recursingBold = false;
    protected static boolean recursingItalic = false;
    protected static boolean recursingStrike = false;

    protected Map<String, MarkdownASTVisitor.ParserNodeInfo> abbreviations = new HashMap<String, MarkdownASTVisitor.ParserNodeInfo>();
    protected String abbreviationsRegEx = "";
    protected Pattern abbreviationsPattern = null;

    protected boolean githubWikiLinks;

    private boolean parseCalled = false;

    // when an exclusion is added then the parent range will not be punched out by the child
    // default child range punches out a hole in the parent range.
    static protected void addExclusion(IElementType parent, IElementType child) {
        HashSet<IElementType> childExclusions;
        if (!overrideExclusions.containsKey(child)) {
            childExclusions = new HashSet<IElementType>();
            overrideExclusions.put(child, childExclusions);
        } else {
            childExclusions = overrideExclusions.get(child);
        }

        childExclusions.add(parent);
    }

    static protected void addInlineExclusions(IElementType parent) {
        addInlineExclusions(parent, true);
    }

    static protected void addInlineExclusions(IElementType parent, boolean addEmph) {
        addExclusion(parent, CODE);
        addExclusion(parent, SPECIAL_TEXT);
        addExclusion(parent, TEXT);

        if (addEmph) {
            addExclusion(parent, BOLD);
            addExclusion(parent, BOLD_MARKER);
            addExclusion(parent, BOLDITALIC);
            addExclusion(parent, ITALIC);
            addExclusion(parent, ITALIC_MARKER);
            addExclusion(parent, STRIKETHROUGH);
            addExclusion(parent, STRIKETHROUGH_BOLD);
            addExclusion(parent, STRIKETHROUGH_BOLDITALIC);
            addExclusion(parent, STRIKETHROUGH_ITALIC);
            addExclusion(parent, STRIKETHROUGH_MARKER);
        }
    }

    static protected void addExcludeFromInlines(IElementType parent) {
        addExclusion(BOLD, parent);
        addExclusion(BOLD_MARKER, parent);
        addExclusion(BOLDITALIC, parent);
        addExclusion(ITALIC, parent);
        addExclusion(ITALIC_MARKER, parent);
        addExclusion(STRIKETHROUGH, parent);
        addExclusion(STRIKETHROUGH_BOLD, parent);
        addExclusion(STRIKETHROUGH_BOLDITALIC, parent);
        addExclusion(STRIKETHROUGH_ITALIC, parent);
        addExclusion(STRIKETHROUGH_MARKER, parent);
    }

    static protected boolean isExcluded(IElementType parent, IElementType child) {
        HashSet<IElementType> childExclusions;

        if (child == null || parent == null) return true;
        if (!overrideExclusions.containsKey(child)) return false;

        childExclusions = overrideExclusions.get(child);
        return childExclusions.contains(parent);
    }

    static protected void addCombinationSplit(IElementType resultingType, IElementType elementType1, IElementType elementType2) {
        if (!combinationSplits.containsKey(elementType1)) combinationSplits.put(elementType1, new HashMap<IElementType, IElementType>(2));
        if (!combinationSplits.get(elementType1).containsKey(elementType2))
            combinationSplits.get(elementType1).put(elementType2, resultingType);
        if (!combinationSplits.containsKey(elementType2)) combinationSplits.put(elementType2, new HashMap<IElementType, IElementType>(2));
        if (!combinationSplits.get(elementType2).containsKey(elementType1))
            combinationSplits.get(elementType2).put(elementType1, resultingType);
    }

    static {

        addCombinationSplit(BOLDITALIC, BOLD, ITALIC);
        addCombinationSplit(STRIKETHROUGH_BOLDITALIC, BOLDITALIC, STRIKETHROUGH);
        addCombinationSplit(STRIKETHROUGH_BOLDITALIC, BOLD, STRIKETHROUGH_ITALIC);
        addCombinationSplit(STRIKETHROUGH_BOLDITALIC, ITALIC, STRIKETHROUGH_BOLD);
        addCombinationSplit(STRIKETHROUGH_BOLDITALIC, STRIKETHROUGH_ITALIC, STRIKETHROUGH_BOLD);

        addCombinationSplit(STRIKETHROUGH_BOLD, BOLD, STRIKETHROUGH);
        addCombinationSplit(STRIKETHROUGH_ITALIC, ITALIC, STRIKETHROUGH);

        // these are not used for highlighting, only to punch out the range of their parents
        excludedTokenTypes.add(TABLE_BODY);
        excludedTokenTypes.add(TABLE_HEADER);

        addExclusion(ANCHOR_LINK, INLINE_HTML);

        // these can affect text and should combine attributes
        addInlineExclusions(TABLE_HEADER);
        addInlineExclusions(TABLE_CELL_RODD_CODD);
        addInlineExclusions(TABLE_CELL_RODD_CEVEN);
        addInlineExclusions(TABLE_CELL_REVEN_CODD);
        addInlineExclusions(TABLE_CELL_REVEN_CEVEN);
        addInlineExclusions(TABLE_CAPTION);

        // task items
        //addInlineExclusions(TASK_ITEM);
        //addInlineExclusions(TASK_DONE_ITEM);
        addInlineExclusions(TASK_ITEM_MARKER);
        addInlineExclusions(TASK_DONE_ITEM_MARKER);
        //addInlineExclusions(FOOTNOTE);

        // let all the inlines not punch through each other
        addInlineExclusions(STRIKETHROUGH_BOLDITALIC, false);
        addExclusion(STRIKETHROUGH_BOLDITALIC, STRIKETHROUGH_BOLD);
        addExclusion(STRIKETHROUGH_BOLDITALIC, STRIKETHROUGH_ITALIC);
        addExclusion(STRIKETHROUGH_BOLDITALIC, STRIKETHROUGH);
        addExclusion(STRIKETHROUGH_BOLDITALIC, BOLDITALIC);
        addExclusion(STRIKETHROUGH_BOLDITALIC, BOLD);
        addExclusion(STRIKETHROUGH_BOLDITALIC, ITALIC);

        addInlineExclusions(STRIKETHROUGH_ITALIC, false);
        addExclusion(STRIKETHROUGH_ITALIC, STRIKETHROUGH);
        addExclusion(STRIKETHROUGH_ITALIC, ITALIC);

        addInlineExclusions(STRIKETHROUGH_BOLD, false);
        addExclusion(STRIKETHROUGH_BOLD, STRIKETHROUGH);
        addExclusion(STRIKETHROUGH_BOLD, BOLD);

        addInlineExclusions(STRIKETHROUGH, false);

        addInlineExclusions(BOLDITALIC, false);
        addExclusion(BOLDITALIC, BOLD);
        addExclusion(BOLDITALIC, ITALIC);

        addInlineExclusions(BOLD, false);
        addInlineExclusions(ITALIC, false);

        // these should override text
        addInlineExclusions(AUTO_LINK);
        addInlineExclusions(ANCHOR_LINK);
        addInlineExclusions(REFERENCE);
        addInlineExclusions(REFERENCE_IMAGE);
        addInlineExclusions(REFERENCE_LINK);
        addInlineExclusions(EXPLICIT_LINK);
        addInlineExclusions(IMAGE);
        addInlineExclusions(ABBREVIATION);
        addInlineExclusions(QUOTE);

        addInlineExclusions(HEADER_LEVEL_1);
        addInlineExclusions(HEADER_LEVEL_2);
        addInlineExclusions(HEADER_LEVEL_3);
        addInlineExclusions(HEADER_LEVEL_4);
        addInlineExclusions(HEADER_LEVEL_5);
        addInlineExclusions(HEADER_LEVEL_6);

        addInlineExclusions(DEFINITION);
        addInlineExclusions(DEFINITION_TERM);

        // to allow strike, bold and italics to show
        // list item is useless, should not punch out block quote, but it should punch out bullet_list
        // that way only the bullets will be left to punch out  the block quote
        addExclusion(BLOCK_QUOTE, LIST_ITEM);
    }

    public
    @Nullable
    LexerToken[] parseMarkdown(final RootNode rootNode, char[] currentChars, int pegdownExtensions) {
        assert !parseCalled;
        if (rootNode == null) return null;

        this.currentChars = currentChars;
        this.currentStringLength = currentChars.length;

        // process tokens right away and return them
        this.githubWikiLinks = (pegdownExtensions & MultiMarkdownLexParserManager.GITHUB_WIKI_LINKS) != 0;
        MarkdownASTVisitor visitor = new MarkdownASTVisitor();
        rootNode.accept(visitor);
        ArrayList<LexerToken> lexerTokens = visitor.getTokens();

        LexerToken[] tokens = new LexerToken[lexerTokens.size()];
        tokens = lexerTokens.toArray(tokens);

        if (tokens.length > 0) {
            Arrays.sort(tokens);

            // now need to step through and merge consecutive tokens
            int iMax = tokens.length;
            LexerToken thisToken = tokens[0];
            lexerTokens = new ArrayList<LexerToken>(iMax);

            for (int i = 1; i < iMax; i++) {
                LexerToken thatToken = tokens[i];

                if (!thatToken.doesExtend(thisToken)) {
                    lexerTokens.add(thisToken);
                    thisToken = thatToken;
                } else {
                    thisToken.getRange().expandToInclude(thatToken.getRange());
                }
            }
            lexerTokens.add(thisToken);

            // now we generate lexemes from the combined optimized tokens
            tokens = new LexerToken[lexerTokens.size()];
            tokens = lexerTokens.toArray(tokens);

            // we create a list of non-intersecting, sorted, ranges
            tokens = splitLexerTokens(tokens);
        }

        parseCalled = true;
        this.currentChars = null;
        return tokens;
    }

    public static LexerToken getWhiteSpaceToken(int start, int end) {
        return new LexerToken(new Range(start, end), TokenType.WHITE_SPACE);
    }

    public static LexerToken getSkippedSpaceToken(int start, int end) {
        //if (end > currentStringLength) {
        //    int tmp = 0;
        //}
        return new LexerToken(new Range(start, end), NONE);
    }

    public LexerToken[] splitLexerTokens(LexerToken[] tokens) {
        int end = tokens.length;

        if (end > 0) {
            ArrayList<LexerToken> lexemes = new ArrayList<LexerToken>(tokens.length);

            // do all of them
            splitLexemes(lexemes, tokens, 0, Integer.MAX_VALUE);

            //end = lexemes.size() - 1;
            //for (int i = 0; i < end; i++) {
            //    LexerToken t1 = lexemes.get(i);
            //    LexerToken t2 = lexemes.get(i + 1);
            //    if (t1.compare(t2) > 0 || t1.getRange().doesOverlap(t2.getRange())) {
            //        int tmp = 0;
            //    }
            //}

            //if (lexemes.get(end).getRange().getEnd() > currentStringLength) {
            //    int tmp = 0;
            //    lexemes.get(end).getRange().setEnd(currentStringLength);
            //    if (lexemes.get(end).getRange().isEmpty())
            //    {
            //        // pegdown issue, creates nodes beyond the end of file
            //        lexemes.remove(end);
            //    }
            //}

            LexerToken[] lexerTokens = new LexerToken[lexemes.size()];
            return lexemes.toArray(lexerTokens);
        }
        return null;
    }

    protected int splitLexemes(ArrayList<LexerToken> lexemes, LexerToken[] tokens, int start, int rangeEnd) {
        LexerToken token = tokens[start];
        Range range = token.getRange();
        Range range1;

        if (range.end <= rangeEnd) {
            int end = tokens.length;

            start++;
            for (; start < end && (range1 = tokens[start].getRange()).end <= rangeEnd; start++) {
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
                    //if (!range.doesNotOverlap(range1)) {
                    //    int tmp = 0;
                    //    //assert false;
                    //}
                    //if (range.compare(range1) <= 0) {
                    //    int tmp = 0;
                    //    //assert false;
                    //}
                    lexemes.add(token);
                    token = tokens[start];
                    range = token.getRange();
                } else {
                    if (range.doesOverlap(range1) && !range.doesContain(range1)) {
                        // split the range and continue
                        // if it contains it, then skip it
                        Range newRange = new Range(range);
                        newRange.end = range1.start;
                        if (newRange.isEmpty()) {
                            token = tokens[start];
                            range = token.getRange();
                        } else {
                            LexerToken newToken = new LexerToken(newRange, token.getElementType());
                            lexemes.add(newToken);

                            range = new Range(range);
                            range.start = range1.end;
                            if (range.isEmpty()) {
                                token = tokens[start];
                                range = token.getRange();
                            } else {
                                start = splitLexemes(lexemes, tokens, start, range.start);
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

    protected static class LexerToken implements Comparable<LexerToken> {

        @Override
        public int compareTo(@NotNull LexerToken o) {
            return compare(o);
        }

        private final Range range;
        private final IElementType elementType;
        private int nesting;

        public LexerToken(final Range range, final IElementType elementType) {
            this.range = range;
            this.elementType = elementType;
            this.nesting = Integer.MAX_VALUE;
        }

        public LexerToken(int start, int end, final IElementType elementType) {
            this.range = new Range(start, end);
            this.elementType = elementType;
            this.nesting = Integer.MAX_VALUE;
        }

        public LexerToken(final Range range, final IElementType elementType, int nesting) {
            this.range = range;
            this.elementType = elementType;
            this.nesting = nesting;
        }

        public Range getRange() { return range; }

        public IElementType getElementType() { return elementType; }

        public String toString() {
            return "MultiMarkdownLexParser$LexerToken" + range.toString() + " " + elementType.toString();
        }

        public boolean isWhiteSpace() { return elementType == TokenType.WHITE_SPACE; }

        public boolean isSkippedSpace() { return elementType == NONE; }

        public int compare(LexerToken that) {
            int rangeCompare = this.range.compare(that.range);
            return rangeCompare != 0 ? rangeCompare : (this.nesting < that.nesting ? -1 : (this.nesting > that.nesting ? 1 : 0));
        }

        public boolean doesExtend(LexerToken that) {
            return this.elementType == that.elementType && this.range.isAdjacent(that.range);
        }
    }

    protected void pushRange(Range range, IElementType type) {
        SegmentedRange segmentedRange = new SegmentedRange(range);
        segmentedRange.setTokenType(type);
        parentRanges.add(segmentedRange);
    }

    protected void pushRange(SegmentedRange segmentedRange) {
        parentRanges.add(segmentedRange);
    }

    protected void pushRange(int start, int end, IElementType type) {
        SegmentedRange segmentedRange = new SegmentedRange(start, end);
        segmentedRange.setTokenType(type);
        parentRanges.add(segmentedRange);
    }

    protected SegmentedRange popRange() {
        assert parentRanges.size() > 0;
        //if (parentRanges.size() <= minStackLevel) {
        //    int tmp = 0;
        //}
        return parentRanges.remove(parentRanges.size() - 1);
    }

    protected SegmentedRange getRange() {
        assert parentRanges.size() > 0;
        return parentRanges.get(parentRanges.size() - 1);
    }

    protected class MarkdownASTVisitor implements Visitor {
        protected final ArrayList<LexerToken> tokens = new ArrayList<LexerToken>(100);

        public ArrayList<LexerToken> getTokens() { return tokens; }

        class ParserNodeInfo {
            public int startIndex = 0;
            public int endIndex = 0;
            public StringBuilder text = new StringBuilder();
            public StringBuilder expansion = null;
        }

        protected void collectChildrensText(Node node, ParserNodeInfo nodeInfo) {
            if (node.getClass() == TextNode.class || node.getClass() == SpecialTextNode.class) {
                nodeInfo.text.append(((TextNode) node).getText());
                if (nodeInfo.startIndex == 0) {
                    nodeInfo.startIndex = node.getStartIndex();
                }
                nodeInfo.endIndex = node.getEndIndex();
            } else if (node instanceof SuperNode) {
                for (Node child : node.getChildren()) {
                    collectChildrensText(child, nodeInfo);
                }
            }
        }

        @Override
        public void visit(TocNode node) {

        }

        public void visit(RootNode node) {
            for (AbbreviationNode abbrNode : node.getAbbreviations()) {
                ParserNodeInfo abbrNodeInfo = new ParserNodeInfo();
                ParserNodeInfo expansionNodeInfo = new ParserNodeInfo();

                // need to collect the expanded abbreviation text from abbreviationNodes during child visit
                collectChildrensText(abbrNode, abbrNodeInfo);
                collectChildrensText(abbrNode.getExpansion(), expansionNodeInfo);

                String abbr = abbrNodeInfo.text.toString();
                abbrNodeInfo.expansion = expansionNodeInfo.text;
                if (!abbreviations.containsKey(abbr)) {
                    // we overwrite the old values? or we keep them all for error resolution
                    abbreviations.put(abbr, abbrNodeInfo);
                    if (abbreviationsRegEx.length() > 0) abbreviationsRegEx += "|";
                    abbreviationsRegEx += "\\b\\Q" + abbr + "\\E\\b";
                }
            }

            for (AbbreviationNode abbreviationNode : node.getAbbreviations()) {
                abbreviationNode.accept(this);
            }

            for (ReferenceNode referenceNode : node.getReferences()) {
                referenceNode.accept(this);
            }

            visitChildren(node);
        }

        public void visit(FootnoteNode node) {
            //ArrayList<Node> children = new ArrayList<Node>(1);
            //children.add(node.getFootnote());
            addTokenWithChildren(node, FOOTNOTE, node.getFootnote().getChildren());
        }

        public void visit(FootnoteRefNode node) {
            addToken(node, FOOTNOTE_REF);
        }

        public void visit(TextNode node) {
            if (node instanceof CommentNode) {
                addToken(node, COMMENT);
            } else if (node instanceof WikiPageRefNode) {
                addToken(node, WIKI_LINK_REF);
            } else if (node instanceof WikiPageTitleNode) {
                addToken(node, WIKI_LINK_TEXT);
            } else {
                if (abbreviations.isEmpty()) {
                    addToken(node, TEXT);
                } else {
                    addTextTokenWithAbbreviations(node, TEXT, ABBREVIATED_TEXT);
                }
            }
        }

        protected void addTextTokenWithAbbreviations(TextNode node, IElementType tokenType, IElementType abbreviationType) {
            int endIndex = node.getEndIndex();
            int startIndex = node.getStartIndex();

            // compensate for missing EOL at end of input causes pegdown to return a range past end of input
            // in this case IDEA ignores the range. :(
            if (endIndex > currentStringLength) endIndex = currentStringLength;

            Range range = new Range(startIndex, endIndex);
            if (!range.isEmpty() && (parentRanges.size() <= 0 || excludeAncestors(range, tokenType))) {
                // wasn't stripped out, set it
                // see if it contains abbreviations, we color them differently from text
                if (abbreviationsPattern == null) {
                    abbreviationsPattern = Pattern.compile(abbreviationsRegEx);
                }

                String nodeText = node.getText();// currentString.substring(startIndex, endIndex);
                Matcher m = abbreviationsPattern.matcher(nodeText);
                int lastPos = startIndex;

                while (m.find()) {
                    //String found = m.group();
                    int foundStart = startIndex + m.start(0);
                    int foundEnd = startIndex + m.end(0);

                    if (lastPos < foundStart) {
                        range = new Range(lastPos, foundStart);
                        tokens.add(new LexerToken(range, tokenType));
                    }

                    if (foundStart < foundEnd) {
                        range = new Range(foundStart, foundEnd);
                        tokens.add(new LexerToken(range, abbreviationType));
                    }

                    lastPos = foundEnd;
                }

                if (lastPos < endIndex) {
                    range = new Range(lastPos, endIndex);
                    tokens.add(new LexerToken(range, tokenType));
                }

                //System.out.print("adding " + tokenType + " for [" + range.getStart() + ", " + range.getEnd() + ")\n");
            }
        }

        public void visit(SimpleNode node) {
            switch (node.getType()) {
                case HRule:
                    addToken(node, HRULE);
                    break;

                case Apostrophe:
                case Ellipsis:
                case Emdash:
                case Endash:
                    addToken(node, SMARTS);
                    break;

                case Linebreak:
                case Nbsp:
                    break;
            }
        }

        public void visit(SuperNode node) {
            visitChildren(node);
        }

        public void visit(ParaNode node) {
            visitChildren(node);
        }

        public void visit(Node node) {
            addToken(node, TokenType.ERROR_ELEMENT);
        }

        public void visit(SpecialTextNode node) {
            if ((node.getEndIndex() - node.getStartIndex() > 1)) addToken(node, SPECIAL_TEXT);
            else visit((TextNode) node); // so that it is handled in TextNode manner
        }

        protected void splitOutMarker(StrongEmphSuperNode node, IElementType markerType) {
            String marker = node.getChars();// != null ? node.getChars() : "~~";
            int markerLength = marker.length();

            ArrayList<Node> children = new ArrayList<Node>(1);
            children.add(node);

            SuperNode parentNode = new SuperNode(children);
            parentNode.setStartIndex(node.getStartIndex());
            parentNode.setEndIndex(node.getEndIndex());

            // now need to truncate children to this range
            limitChildrensRange(parentNode, node.getStartIndex() + markerLength, node.getEndIndex() - (node.isClosed() ? markerLength : 0));

            addTokenWithChildren(parentNode, markerType);
        }

        /**
         * split out the lead-in and terminating sequence into faked parent node
         * and add new strikethrough_marker token type for the lead and trail chars with child text node
         * taking on the strikethrough attribute. Then we can color the lead-in and terminating chars separately.
         */
        public void visit(StrikeNode node) {
            if (!recursingStrike) {
                recursingStrike = true;
                splitOutMarker(node, STRIKETHROUGH_MARKER);
                recursingStrike = false;
            } else {
                addTokenWithChildren(node, STRIKETHROUGH);
            }
        }

        /**
         * split out the lead-in and terminating sequence into faked parent node
         * and add new bold_marker and italic_marker token types for the lead and trail chars with child text node
         * taking on the BOLD and ITALIC tokens. So we can color the lead-in and terminating chars separately.
         */
        public void visit(StrongEmphSuperNode node) {
            if (node.isClosed()) {
                IElementType parentTokenType = node.isStrong() ? BOLD_MARKER : ITALIC_MARKER;
                IElementType tokenType = node.isStrong() ? BOLD : ITALIC;

                if (tokenType == BOLD && !recursingBold || tokenType == ITALIC && !recursingItalic) {
                    if (tokenType == BOLD) recursingBold = true;
                    else recursingItalic = true;
                    splitOutMarker(node, parentTokenType);
                    if (tokenType == BOLD) recursingBold = false;
                    else recursingItalic = false;
                } else {
                    addTokenWithChildren(node, tokenType);
                }
            } else {
                // not closed, ignore
                visitChildren(node);
            }
        }

        protected Node getLastChild(SuperNode node) {
            Node lastChild = null;

            for (; ; ) {
                List<Node> children = node.getChildren();
                int size = children.size();

                if (size <= 0) break;
                lastChild = children.get(size - 1);

                if (!(lastChild instanceof SuperNode)) break;
                node = (SuperNode) lastChild;
            }
            return lastChild;
        }

        protected void limitChildrensRange(SuperNode parentNode, int startIndex, int endIndex) {
            for (Node node : parentNode.getChildren()) {
                if (node.getStartIndex() < startIndex) ((AbstractNode) node).setStartIndex(startIndex);
                if (node.getEndIndex() > endIndex) ((AbstractNode) node).setEndIndex(endIndex);
                if (node instanceof SuperNode) limitChildrensRange((SuperNode) node, startIndex, endIndex);
            }
        }

        void addTokens(SyntheticNodes nodes) {
            for (SyntheticNodes.SyntheticNode node : nodes.getNodes()) {
                addToken(node.getStartIndex(), node.getEndIndex(), node.getType());
            }
        }

        void addTokensWithChildren(SyntheticNodes nodes) {
            SyntheticNodes.SyntheticNode currentNode = nodes.currentNode();

            for (SyntheticNodes.SyntheticNode node : nodes.getNodes()) {
                if (node == currentNode) {
                    addTokenWithChildren(node.getStartIndex(), node.getEndIndex(), node.getType(), nodes.getNode().getChildren());
                } else {
                    addToken(node.getStartIndex(), node.getEndIndex(), node.getType());
                }
            }
        }

        public void visit(ExpImageNode node) {
            if (MultiMarkdownPlugin.isLicensed()) {
                SyntheticNodes nodes = new SyntheticNodes(currentChars, node, IMAGE, IMAGE_LINK_REF_TEXT, 2, IMAGE_LINK_REF_TEXT_OPEN, 1, IMAGE_LINK_REF_CLOSE);

                if (!node.title.isEmpty() && nodes.chopLastTail(node.title, IMAGE_LINK_REF_TITLE, IMAGE_LINK_REF_TITLE_MARKER)) {
                    nodes.chopTail(-1, IMAGE_LINK_REF_TITLE_MARKER);
                }

                if (!node.url.isEmpty()) {
                    nodes.chopLastTail(node.url, IMAGE_LINK_REF);
                }

                // now chop off lead and tail alt markers
                if (nodes.chopTail(']', IMAGE_LINK_REF_TEXT_CLOSE)) {
                    nodes.next();
                    nodes.chopTail('(', IMAGE_LINK_REF_OPEN);
                    nodes.prev();
                }

                // need to process children with the current node
                nodes.dropWhiteSpace(IMAGE_LINK_REF_TEXT_CLOSE, IMAGE_LINK_REF_OPEN, IMAGE_LINK_REF);
                addTokensWithChildren(nodes);
            } else {
                addTokenWithChildren(node, IMAGE);
            }
        }

        public void visit(ExpLinkNode node) {
            if (MultiMarkdownPlugin.isLicensed()) {
                SyntheticNodes nodes = new SyntheticNodes(currentChars, node, EXPLICIT_LINK, LINK_REF_TEXT, 1, LINK_REF_TEXT_OPEN, LINK_REF_CLOSE);

                // now chop off lead child nodes
                int childNode = nodes.current();
                if (nodes.chopChildren(SyntheticNodes.ChildLocationType.LEAD, LINK_REF_TEXT_CLOSE)) nodes.next();

                if (nodes.chopTail('(', LINK_REF_OPEN)) nodes.next();

                if (!node.title.isEmpty() && nodes.chopLastTail(node.title, LINK_REF_TITLE, LINK_REF_TITLE_MARKER)) {
                    nodes.chopTail(-1, LINK_REF_TITLE_MARKER);
                }

                if (!node.url.isEmpty()) {
                    nodes.chopLastTail(node.url, LINK_REF);
                    nodes.next();
                    nodes.chopLastTail('#', LINK_REF_ANCHOR_MARKER, LINK_REF_ANCHOR);
                }

                //nodes.validateAllContiguous();
                // need to process children with the current node
                nodes.dropWhiteSpace(LINK_REF_TEXT_CLOSE, LINK_REF_OPEN, LINK_REF, LINK_REF_ANCHOR_MARKER, LINK_REF_ANCHOR);

                // need to process children with the current node
                nodes.setCurrent(childNode);
                addTokensWithChildren(nodes);
            } else {
                addTokenWithChildren(node, EXPLICIT_LINK);
            }
        }

        public void visit(final RefLinkNode node) {
            if (MultiMarkdownPlugin.isLicensed()) {
                SyntheticNodes nodes = new SyntheticNodes(currentChars, node, REFERENCE_LINK);
                addTokenWithChildren(node, REFERENCE_LINK);
            } else {
                addTokenWithChildren(node, REFERENCE_LINK);
            }
        }

        public void visit(ReferenceNode node) {
            addTokenWithChildren(node, REFERENCE);
        }

        public void visit(RefImageNode node) {
            addTokenWithChildren(node, REFERENCE_IMAGE);
        }

        public void visit(AutoLinkNode node) {
            if (MultiMarkdownPlugin.isLicensed()) {
                SyntheticNodes nodes = new SyntheticNodes(currentChars, node, AUTO_LINK, LINK_REF);
                nodes.chopStart('<', AUTO_LINK);
                nodes.chopEnd('>', AUTO_LINK);
                nodes.chopLastTail('#', LINK_REF_ANCHOR_MARKER, LINK_REF_ANCHOR);
                addTokens(nodes);
            } else {
                addToken(node, AUTO_LINK);
            }
        }

        public void visit(MailLinkNode node) {
            addToken(node, MAIL_LINK);
        }

        public void visit(HeaderNode node) {
            //visitChildren(node);

            switch (node.getLevel()) {
                case 1:
                    addTokenWithChildren(node, node.isSetext() ? SETEXT_HEADER_LEVEL_1 : HEADER_LEVEL_1);
                    break;
                case 2:
                    addTokenWithChildren(node, node.isSetext() ? SETEXT_HEADER_LEVEL_2 : HEADER_LEVEL_2);
                    break;
                case 3:
                    addTokenWithChildren(node, HEADER_LEVEL_3);
                    break;
                case 4:
                    addTokenWithChildren(node, HEADER_LEVEL_4);
                    break;
                case 5:
                    addTokenWithChildren(node, HEADER_LEVEL_5);
                    break;
                case 6:
                    addTokenWithChildren(node, HEADER_LEVEL_6);
                    break;
            }
        }

        public void visit(CodeNode node) {
            addToken(node, CODE);
        }

        public void visit(VerbatimNode node) {
            addToken(node, VERBATIM);
        }

        public void visit(WikiLinkNode node) {
            SyntheticNodes nodes = new SyntheticNodes(currentChars, node, WIKI_LINK, WIKI_LINK_REF, 2, WIKI_LINK_OPEN, WIKI_LINK_CLOSE);

            if (githubWikiLinks) {
                nodes.chopLead("|", WIKI_LINK_TEXT, WIKI_LINK_SEPARATOR);
                nodes.chopTail("#", WIKI_LINK_REF_ANCHOR_MARKER, WIKI_LINK_REF_ANCHOR);
            } else {
                nodes.chopTail("|", WIKI_LINK_SEPARATOR, WIKI_LINK_TEXT);
                nodes.chopTail("#", WIKI_LINK_REF_ANCHOR_MARKER, WIKI_LINK_REF_ANCHOR);
            }

            addTokens(nodes);
        }

        public void visit(QuotedNode node) {
            addTokenWithChildren(node, QUOTE);
        }

        public void visit(BlockQuoteNode node) {
            // here some children will punch out the block quote's > because they span more than one line and know nothing
            // of indentations. So we have to punch out holes of every child's new line that starts with > and optional space
            addTokenWithChildren(node, BLOCK_QUOTE);
        }

        public void visit(BulletListNode node) {
            addTokenWithChildren(node, BULLET_LIST);
        }

        public void visit(OrderedListNode node) {
            addTokenWithChildren(node, ORDERED_LIST);
        }

        public void visit(ListItemNode node) {
            if (node instanceof TaskListNode) {
                SuperNode newNode = new SuperNode();
                TaskListNode taskListNode = (TaskListNode) node;

                // marker is only the marker characters
                newNode.setStartIndex(taskListNode.getStartIndex() - taskListNode.getTaskListMarker().length());
                newNode.setEndIndex(newNode.getStartIndex() + 3);

                // new node is all the text following
                addTokenWithChildren(newNode, (taskListNode.isDone() ? TASK_DONE_ITEM_MARKER : TASK_ITEM_MARKER));
                //addTokenWithChildren(node, (taskListNode.isDone() ? TASK_DONE_ITEM : TASK_ITEM));
                addTokenWithChildren(node, LIST_ITEM);
            } else {
                addTokenWithChildren(node, LIST_ITEM);
            }
        }

        public void visit(DefinitionListNode node) {
            addTokenWithChildren(node, DEFINITION_LIST);
        }

        public void visit(DefinitionNode node) {
            addTokenWithChildren(node, DEFINITION);
        }

        public void visit(DefinitionTermNode node) {
            addTokenWithChildren(node, DEFINITION_TERM);
        }

        public void visit(TableNode node) {
            tableRows = 0;
            addTokenWithChildren(node, TABLE);
        }

        public void visit(TableBodyNode node) {
            addTokenWithChildren(node, TABLE_BODY);
        }

        public void visit(TableCellNode node) {
            rowColumns++;
            addTokenWithChildren(node, (tableRows & 1) != 0 ? ((rowColumns & 1) != 0 ? TABLE_CELL_RODD_CODD : TABLE_CELL_RODD_CEVEN)
                    : ((rowColumns & 1) != 0 ? TABLE_CELL_REVEN_CODD : TABLE_CELL_REVEN_CEVEN));
        }

        // Not called, TableColumnNode is only used as part of TableNode.getColumns()
        public void visit(TableColumnNode node) {
            addTokenWithChildren(node, TABLE_COLUMN);
        }

        public void visit(TableHeaderNode node) {
            addTokenWithChildren(node, TABLE_HEADER);
        }

        public void visit(TableRowNode node) {
            tableRows++;
            rowColumns = 0;
            addTokenWithChildren(node, (tableRows & 1) != 0 ? TABLE_ROW_ODD : TABLE_ROW_EVEN);
        }

        public void visit(TableCaptionNode node) {
            addTokenWithChildren(node, TABLE_CAPTION);
        }

        abstract class NodeFactory {
            abstract public TextNode newNode(String text);
        }

        class CommentNode extends TextNode {
            public CommentNode(String text) {
                super(text);
            }
        }

        class WikiPageRefNode extends TextNode {
            public WikiPageRefNode(String text) {
                super(text);
            }
        }

        class WikiPageTitleNode extends TextNode {
            public WikiPageTitleNode(String text) {
                super(text);
            }
        }

        protected boolean extractHtmlComments(TextNode node, NodeFactory factory) {
            String html = node.getText();
            Pattern p = Pattern.compile("(\"|\'|<!--|-->)", Pattern.CASE_INSENSITIVE);
            ArrayList<TextNode> nodes = null;
            Matcher m = p.matcher(html);
            TextNode textNode;
            CommentNode commentNode;
            int lastPos = 0;
            boolean inDQuotes = false;
            boolean inSQuotes = false;
            int startComment = -1;
            int endPos;

            while (m.find()) {
                String found = m.group();

                if (inSQuotes && !found.equals("'")) continue;
                if (inDQuotes && !found.equals("\"")) continue;
                if (startComment >= 0 && !found.equals("-->")) continue;

                if (found.equals("\"")) {
                    inDQuotes = !inDQuotes;
                } else if (found.equals("'")) {
                    inSQuotes = !inSQuotes;
                } else if (found.equals("<!--")) {
                    startComment = m.start(0);
                } else if (startComment >= 0 && found.equals("-->")) {
                    // have a comment
                    if (nodes == null) nodes = new ArrayList<TextNode>(10);

                    endPos = startComment;
                    if (lastPos < endPos) {
                        textNode = factory.newNode(html.substring(lastPos, endPos));
                        textNode.setStartIndex(node.getStartIndex() + lastPos);
                        textNode.setEndIndex(node.getStartIndex() + endPos);
                        nodes.add(textNode);
                    }

                    lastPos = startComment;
                    endPos = m.end(0);
                    commentNode = new CommentNode(html.substring(lastPos, endPos));
                    commentNode.setStartIndex(node.getStartIndex() + lastPos);
                    commentNode.setEndIndex(node.getStartIndex() + endPos);
                    nodes.add(commentNode);

                    lastPos = endPos;
                    startComment = -1;
                }
            }

            if (nodes != null) {
                endPos = html.length();
                if (lastPos < endPos) {
                    textNode = factory.newNode(html.substring(lastPos, endPos));
                    textNode.setStartIndex(node.getStartIndex() + lastPos);
                    textNode.setEndIndex(node.getStartIndex() + endPos);
                    nodes.add(textNode);
                }

                for (Node node1 : nodes) {
                    node1.accept(this);
                }
                return true;
            }
            return false;
        }

        public void visit(HtmlBlockNode node) {
            if (node.getChildren().size() > 1 || !extractHtmlComments(node, new NodeFactory() {
                @Override
                public TextNode newNode(String text) {
                    return new HtmlBlockNode(text);
                }
            })) addToken(node, HTML_BLOCK);
        }

        public void visit(InlineHtmlNode node) {
            if (node.getChildren().size() > 1 || !extractHtmlComments(node, new NodeFactory() {
                @Override
                public TextNode newNode(String text) {
                    return new InlineHtmlNode(text);
                }
            })) addToken(node, INLINE_HTML);
        }

        public void visit(AbbreviationNode node) {
            addTokenWithChildren(node, ABBREVIATION);
        }

        public void visit(AnchorLinkNode node) {
            addToken(node, ANCHOR_LINK);
        }

        protected void visitChildren(SuperNode node) {
            visitChildren(node.getChildren());
        }

        protected void visitChildren(List<Node> children) {
            // here we combine multiple segments of TextNode and SpecialText into a single TextNode
            int startIndex = 0, endIndex = 0;
            String combinedText = null;
            Node lastTextNode = null;

            for (Node child : children) {
                boolean processed = false;
                if (child.getClass() == TextNode.class || (child.getClass() == SpecialTextNode.class && child.getEndIndex() - child.getStartIndex() <= 1)) {
                    if (combinedText != null) {
                        // combine range and text, if possible
                        if (endIndex == child.getStartIndex()) {
                            // combine
                            endIndex = child.getEndIndex();
                            combinedText += ((TextNode) child).getText();
                            lastTextNode = null;
                            processed = true;
                        } else {
                            // insert collected up to now
                            if (lastTextNode != null) {
                                lastTextNode.accept(this);
                                lastTextNode = null;
                            } else {
                                TextNode newNode = new TextNode(combinedText);
                                newNode.setStartIndex(startIndex);
                                newNode.setEndIndex(endIndex);
                                newNode.accept(this);
                            }

                            combinedText = null;
                        }
                    }

                    if (combinedText == null) {
                        startIndex = child.getStartIndex();
                        endIndex = child.getEndIndex();
                        combinedText = ((TextNode) child).getText();
                        lastTextNode = child;
                        processed = true;
                    }
                }

                if (!processed) {
                    if (combinedText != null) {
                        // process accumulated to date
                        if (lastTextNode != null) {
                            lastTextNode.accept(this);
                        } else {
                            TextNode newNode = new TextNode(combinedText);
                            newNode.setStartIndex(startIndex);
                            newNode.setEndIndex(endIndex);
                            newNode.accept(this);
                        }
                        combinedText = null;
                        lastTextNode = null;
                    }

                    child.accept(this);
                }
            }

            if (combinedText != null) {
                // process the last combined
                if (lastTextNode != null) {
                    lastTextNode.accept(this);
                } else {
                    TextNode newNode = new TextNode(combinedText);
                    newNode.setStartIndex(startIndex);
                    newNode.setEndIndex(endIndex);
                    newNode.accept(this);
                }
            }
        }

        protected boolean excludeAncestors(Range range, IElementType type) {
            for (SegmentedRange parentRange : parentRanges) {
                if (parentRange != null && parentRange.isExcludedBy(type)) {
                    //System.out.println("Excluding parent " + parentRange + " by " + type + " " + range);
                    parentRange.exclude(range);
                    //System.out.println("    Excluded parent " + parentRange + " by " + type + " " + range + "\n");
                }
            }
            return true;
        }

        protected boolean excludeAncestors(SegmentedRange segmentedRange) {
            for (SegmentedRange parentRange : parentRanges) {
                if (parentRange != null && parentRange.isExcludedBy(segmentedRange.getTokenType())) {
                    //System.out.println("Excluding parent " + parentRange + " by " + segmentedRange);
                    parentRange.exclude(segmentedRange);
                    //System.out.println("    Excluded parent " + parentRange + " by " + segmentedRange + "\n");
                }
            }
            return true;
        }

        protected void addSplitCombinations() {
            if (parentRanges.size() > 0) {
                SegmentedRange segmentedRange = parentRanges.get(parentRanges.size() - 1);
                IElementType tokenType = segmentedRange.getTokenType();

                //System.out.println("split combos " + segmentedRange.toString());

                if (combinationSplits.containsKey(tokenType)) {
                    SegmentedRange combinationRange = null;

                    // here we have to combine bold and italic into bolditalic, etc of our parent ranges
                    for (SegmentedRange parentRange : parentRanges) {
                        if (tokenType != parentRange.getTokenType()
                                && !parentRange.isEmpty()
                                && combinationSplits.containsKey(tokenType)
                                && combinationSplits.get(tokenType).containsKey(parentRange.getTokenType())) {

                            // we will create an intersection and make it punch through parents and this range
                            tokenType = combinationSplits.get(tokenType).get(parentRange.getTokenType());
                            SegmentedRange splitRange = new SegmentedRange();
                            splitRange.setTokenType(tokenType);
                            splitRange.addIntersections(segmentedRange, parentRange);

                            //System.out.println("Add intersections of " + segmentedRange + " by " + parentRange + " = " + splitRange);
                            segmentedRange = combinationRange = splitRange;

                            if (segmentedRange.isEmpty()) break;

                            // continue to make combinations with other parents
                        }
                    }

                    if (combinationRange != null && !combinationRange.isEmpty()) pushRange(combinationRange);
                }
            }
        }

        // to overcome the problem that a parent has a wider range than the child selectively
        // punch out the child's range from the parent's so that we can eliminate parent's highlighting
        // on the child text
        protected void addTokenWithChildren(Node node, IElementType tokenType) {
            addTokenWithChildren(node, tokenType, node.getChildren());
        }

        protected void addTokenWithChildren(Node node, IElementType tokenType, List<Node> children) {
            addTokenWithChildren(node.getStartIndex(), node.getEndIndex(), tokenType, children);
        }

        protected void addTokenWithChildren(int startIndex, int endIndex, IElementType tokenType, List<Node> children) {
            //if (node.getEndIndex() > currentStringLength) {
            //    ((SuperNode) node).setEndIndex(currentStringLength);
            //    if (node.getStartIndex() >= node.getEndIndex()) {
            //        return;
            //    }
            //}

            int entryStackLevel = parentRanges.size();
            Range range = new Range(startIndex, endIndex);

            //System.out.println("addTokenWithChildren " + tokenType + range);

            // compensate for missing EOL at end of input causes pegdown to return a range past end of input
            // in this case IDEA ignores the range. :(
            if (range.getEnd() > currentStringLength) range.setEnd(currentStringLength);

            if (!range.isEmpty()) {
                pushRange(range, tokenType);
                //System.out.println("    addTokenWithChildren:pushed(" + tokenType + range + ")[" + parentRanges.size() + "]");
                int prevMinStackLevel = minStackLevel;
                int stackLevel = parentRanges.size();
                minStackLevel = stackLevel;

                // add split combinations to the parent stack, then children
                addSplitCombinations();

                //if (parentRanges.size() < stackLevel) {
                //    int tmp = 0;
                //}

                visitChildren(children);

                minStackLevel = prevMinStackLevel;

                // leave self on stack so it gets punched out by combinations, pop combination, we will now add them
                //if (parentRanges.size() - stackLevel < 0) {
                //    int tmp = 0;
                //}
                ArrayList<SegmentedRange> fakeRanges = new ArrayList<SegmentedRange>(parentRanges.size() - stackLevel);
                while (stackLevel < parentRanges.size()) fakeRanges.add(popRange());

                for (SegmentedRange segmentedRange : fakeRanges) {
                    addSegmentedToken(segmentedRange, true);
                }
                //System.out.println("    addTokenWithChildren:poping[" + parentRanges.size() + "]");
                SegmentedRange segmentedRange = popRange();
                //System.out.println("    addTokenWithChildren:poped(" + segmentedRange + ")[" + parentRanges.size() + "]");
                addSegmentedToken(segmentedRange, true);
            }
            //if (entryStackLevel != parentRanges.size()) {
            //    int tmp = 0;
            //    //assert false;
            //}
        }

        protected void addSegmentedToken(SegmentedRange segmentedRange, boolean excludeAncestors) {
            IElementType tokenType = segmentedRange.getTokenType();
            boolean renderRange = !excludedTokenTypes.contains(tokenType);

            if (parentRanges.size() <= 0) excludeAncestors = false;

            for (Range range : segmentedRange.getSegments()) {
                // now exclude from ancestors what is left by the children
                if (!excludeAncestors || excludeAncestors(range, tokenType)) {
                    // wasn't stripped out, set it
                    if (renderRange) {
                        tokens.add(new LexerToken(range, tokenType, parentRanges.size()));
                        //System.out.print("adding " + tokenType + " for [" + range.getStart() + ", " + range.getEnd() + ")\n");
                    }
                }
            }
        }

        protected void addToken(Node node, IElementType tokenType) {
            addToken(node.getStartIndex(), node.getEndIndex(), tokenType);
        }

        protected void addToken(Range range, IElementType tokenType) {
            addToken(range.getStart(), range.getEnd(), tokenType);
        }

        protected void addToken(int startIndex, int endIndex, IElementType tokenType) {
            //if (tokenType == QUOTE) {
            //    int tmp = 0;
            //}
            // compensate for missing EOL at end of input causes pegdown to return a range past end of input
            // in this case IDEA ignores the range. :(
            if (endIndex > currentStringLength) endIndex = currentStringLength;

            Range range = new Range(startIndex, endIndex);
            if (!range.isEmpty() && (parentRanges.size() <= 0 || excludeAncestors(range, tokenType))) {
                // wasn't stripped out, set it
                tokens.add(new LexerToken(range, tokenType));
                //System.out.print("adding " + tokenType + " for [" + range.getStart() + ", " + range.getEnd() + ")\n");
            }
        }
    }

    /**
     * Helper Classes
     */
    static class Range {

        protected int start;
        protected int end;

        public int getStart() { return start; }

        public void setStart(int start) { this.start = start; }

        public int getEnd() { return end; }

        public void setEnd(int end) { this.end = end; }

        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public Range(Range that) {
            this.start = that.start;
            this.end = that.end;
        }

        public boolean doesNotOverlap(Range that) { return that.end <= start || that.start >= end; }

        public boolean doesOverlap(Range that) { return !(that.end <= start || that.start >= end); }

        public boolean isEqual(Range that) { return end == that.end && start == that.start; }

        public boolean doesContain(Range that) { return end >= that.end && start <= that.start; }

        public boolean doesProperlyContain(Range that) { return end > that.end && start < that.start; }

        public boolean isEmpty() { return start >= end; }

        public boolean intersect(Range that) {
            if (start < that.start) start = that.start;
            if (end > that.end) end = that.end;

            if (start >= end) start = end = 0;
            return !isEmpty();
        }

        public boolean exclude(Range that) {
            // lets make sure we don't need to split into 2 ranges
            //assert (doesOverlap(that) && !doesProperlyContain(that));

            if (start >= that.start && start < that.end) start = that.end;
            if (end <= that.end && end > that.start) end = that.start;

            if (start >= end) start = end = 0;
            return !isEmpty();
        }

        public int compare(Range that) {
            if (this.start < that.start) {
                return -1;
            } else if (this.start > that.start) {
                return 1;
            } else if (this.end > that.end) {
                return -1;
            } else if (this.end < that.end) {
                return 1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "[" + start + ", " + end + ")";
        }

        public boolean isAdjacent(Range that) {
            return this.start == that.end || this.end == that.start;
        }

        public void expandToInclude(Range that) {
            if (this.start > that.start) this.start = that.start;
            if (this.end < that.end) this.end = that.end;
        }
    }

    static class SegmentedRange {

        protected ArrayList<Range> segments;
        protected IElementType tokenType;

        public ArrayList<Range> getSegments() { return segments; }

        public IElementType getTokenType() { return tokenType; }

        public void setTokenType(IElementType tokenType) { this.tokenType = tokenType; }

        public boolean isEmpty() { return segments.isEmpty(); }

        SegmentedRange() { segments = new ArrayList<Range>(); }

        SegmentedRange(int start, int end) {
            segments = new ArrayList<Range>(1);
            segments.add(0, new Range(start, end));
        }

        SegmentedRange(Range range) {
            segments = new ArrayList<Range>(1);
            segments.add(0, range);
        }

        SegmentedRange(ArrayList<Range> ranges) { segments = new ArrayList<Range>(ranges); }

        SegmentedRange(SegmentedRange that) { segments = new ArrayList<Range>(that.segments); }

        public boolean doesContain(Range range) {
            for (Range range1 : segments) {
                if (range1.doesContain(range)) return true;
            }
            return false;
        }

        public void addIntersections(Range range, SegmentedRange segmentedRange) {
            for (Range range1 : segmentedRange.getSegments()) {
                if (range.doesOverlap(range1)) {
                    Range newRange = new Range(range);
                    newRange.intersect(range1);
                    segments.add(newRange);
                }
            }
        }

        public void addIntersections(SegmentedRange segmentedRange1, SegmentedRange segmentedRange2) {
            for (Range range1 : segmentedRange1.getSegments()) {
                for (Range range2 : segmentedRange2.getSegments()) {
                    if (range1.doesOverlap(range2)) {
                        Range newRange = new Range(range1);
                        newRange.intersect(range2);
                        segments.add(newRange);
                    }
                }
            }
        }

        public SegmentedRange exclude(SegmentedRange segmentedRange) {
            for (Range range : segmentedRange.getSegments()) {
                exclude(range);
            }
            return this;
        }

        public SegmentedRange exclude(Range range) {
            int i, iMax = segments.size();

            for (i = 0; i < iMax; i++) {
                Range range1 = segments.get(i);
                if (range1.doesOverlap(range)) {
                    if (range1.doesContain(range)) {
                        if (range1.doesProperlyContain(range)) {
                            // split range1 into 2 and add the new one
                            Range newRange1 = new Range(range1);
                            Range newRange2 = new Range(range1);
                            newRange1.setEnd(range.getStart());
                            newRange2.setStart(range.getEnd());
                            segments.set(i, newRange1);
                            i++;
                            segments.add(i, newRange2);
                            iMax++;
                        } else {
                            if (range1.isEqual(range)) {
                                // remove, they are the same
                                segments.remove(i);
                                i--;
                                iMax--;
                            } else {
                                // truncate range1 and replace
                                Range newRange1 = new Range(range1);
                                newRange1.exclude(range);
                                segments.set(i, newRange1);
                            }
                        }
                    } else if (range.doesContain(range1)) {
                        // delete it
                        segments.remove(i);
                        i--;
                        iMax--;
                    } else {
                        // they overlap but neither contains the other
                        // truncate range1 and replace
                        Range newRange1 = new Range(range1);
                        newRange1.exclude(range);
                        segments.set(i, newRange1);
                    }
                }
            }
            return this;
        }

        protected boolean isExcludedBy(IElementType child) { return !isExcluded(tokenType, child); }

        @Override
        public String toString() {
            String out = "" + tokenType + " ";
            if (!isEmpty()) for (Range range : segments) out += range.toString();
            else out += "<empty>";
            return out;
        }
    }

    /*
    static class SplitSet {

        private IElementType[] elementSet;
        private IElementType resultingType;

        public SplitSet(IElementType[] elementSet, IElementType resultingType) {
            this.elementSet = elementSet;
            this.resultingType = resultingType;
        }

        public boolean hasExactSet(ArrayList<IElementType> elementSet) {
            for (IElementType elementType : this.elementSet) {
                boolean hadElement = false;
                for (IElementType elementType1 : elementSet) {
                    if (elementType == elementType1) {
                        hadElement = true;
                        break;
                    }
                }
                if (!hadElement) return false;
            }
            for (IElementType elementType : elementSet) {
                boolean hadElement = false;
                for (IElementType elementType1 : this.elementSet) {
                    if (elementType == elementType1) {
                        hadElement = true;
                        break;
                    }
                }
                if (!hadElement) return false;
            }
            return true;
        }

        public boolean hasExactSet(IElementType... elementSet) {
            for (IElementType elementType : this.elementSet) {
                boolean hadElement = false;
                for (IElementType elementType1 : elementSet) {
                    if (elementType == elementType1) {
                        hadElement = true;
                        break;
                    }
                }
                if (!hadElement) return false;
            }
            for (IElementType elementType : elementSet) {
                boolean hadElement = false;
                for (IElementType elementType1 : this.elementSet) {
                    if (elementType == elementType1) {
                        hadElement = true;
                        break;
                    }
                }
                if (!hadElement) return false;
            }
            return true;
        }

        public boolean containsElement(IElementType elementType) {
            boolean hadElement = false;
            for (IElementType elementType1 : elementSet) {
                if (elementType == elementType1) {
                    hadElement = true;
                    break;
                }
            }
            return hadElement;
        }
    }
    */
}
