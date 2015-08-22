/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
* Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
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
package com.vladsch.idea.multimarkdown.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.lang.MarkdownTokenTypes;
import com.vladsch.idea.multimarkdown.settings.MarkdownGlobalSettingsListener;
import com.vladsch.idea.multimarkdown.highlighter.MarkdownSyntaxHighlighter;
import com.vladsch.idea.multimarkdown.settings.MarkdownGlobalSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.*;

import java.util.*;

/**
 * {@link ExternalAnnotator} responsible for syntax highlighting Markdown files.
 * <p/>
 * This is a hack to avoid implementing {@link com.intellij.lexer.Lexer},
 * and directly use the AST from <a href="http://pegdown.org">pegdown</a>'s {@link PegDownProcessor} instead.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.4
 */
public class MarkdownAnnotator extends ExternalAnnotator<String, Set<MarkdownAnnotator.HighlightableToken>> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getInstance(MarkdownAnnotator.class);

    /**
     * The {@link com.intellij.openapi.fileTypes.SyntaxHighlighter} used by
     * {@link #apply(com.intellij.psi.PsiFile, java.util.Set, com.intellij.lang.annotation.AnnotationHolder)}.
     */
    private static final SyntaxHighlighter SYNTAX_HIGHLIGHTER = new MarkdownSyntaxHighlighter();

    private MarkdownGlobalSettingsListener globalSettingsListener = null;

    /** The {@link PegDownProcessor} used for building the document AST. */
    private ThreadLocal<PegDownProcessor> processor = initProcessor();

    private int currentStringLength;

    private static Map<IElementType, HashSet<IElementType>> overrideExclusions = new HashMap<IElementType, HashSet<IElementType>>();

    private static HashSet<IElementType> excludedTokenTypes = new HashSet<IElementType>();

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
        if (parent != MarkdownTokenTypes.STRIKETHROUGH && parent != MarkdownTokenTypes.STRIKETHROUGH_MARKER) {
            addExclusion(parent, MarkdownTokenTypes.STRIKETHROUGH);
            addExclusion(parent, MarkdownTokenTypes.STRIKETHROUGH_MARKER);
        }

        addExclusion(parent, MarkdownTokenTypes.TEXT);
        addExclusion(parent, MarkdownTokenTypes.SPECIAL_TEXT);
        addExclusion(parent, MarkdownTokenTypes.CODE);

        if (parent != MarkdownTokenTypes.ITALIC && parent != MarkdownTokenTypes.BOLD) {
            addExclusion(parent, MarkdownTokenTypes.BOLDITALIC);
            addExclusion(parent, MarkdownTokenTypes.BOLD);
            addExclusion(parent, MarkdownTokenTypes.BOLD_MARKER);
            addExclusion(parent, MarkdownTokenTypes.ITALIC);
            addExclusion(parent, MarkdownTokenTypes.ITALIC_MARKER);
        }
    }

    static protected boolean isExcluded(IElementType parent, IElementType child) {
        HashSet<IElementType> childExclusions;

        if (!overrideExclusions.containsKey(child)) return false;

        childExclusions = overrideExclusions.get(child);
        if (!childExclusions.contains(parent)) return false;
        return true;
    }

    static {
        // these are not used for highlighting, only to punch out the range of their parents
        excludedTokenTypes.add(MarkdownTokenTypes.TABLE_BODY);
        excludedTokenTypes.add(MarkdownTokenTypes.TABLE_COLUMN);
        excludedTokenTypes.add(MarkdownTokenTypes.TABLE_HEADER);
//        excludedTokenTypes.add(MarkdownTokenTypes.TABLE_CELL);

        // table_cell does not punch out table_row so that the | dividers stay in the row, text will punch out row.
        addExclusion(MarkdownTokenTypes.TABLE_ROW_ODD, MarkdownTokenTypes.TABLE_CELL_RODD_CODD);
        addExclusion(MarkdownTokenTypes.TABLE_ROW_ODD, MarkdownTokenTypes.TABLE_CELL_RODD_CEVEN);
        addExclusion(MarkdownTokenTypes.TABLE_ROW_ODD, MarkdownTokenTypes.TABLE_COLUMN);

        addExclusion(MarkdownTokenTypes.TABLE_ROW_EVEN, MarkdownTokenTypes.TABLE_CELL_REVEN_CODD);
        addExclusion(MarkdownTokenTypes.TABLE_ROW_EVEN, MarkdownTokenTypes.TABLE_CELL_REVEN_CEVEN);
        addExclusion(MarkdownTokenTypes.TABLE_ROW_EVEN, MarkdownTokenTypes.TABLE_COLUMN);

//        // leave table header alone
//        addExclusion(MarkdownTokenTypes.TABLE_HEADER, MarkdownTokenTypes.TABLE_ROW_ODD);
//        addExclusion(MarkdownTokenTypes.TABLE_HEADER, MarkdownTokenTypes.TABLE_ROW_EVEN);
//        addExclusion(MarkdownTokenTypes.TABLE_HEADER, MarkdownTokenTypes.TABLE_CELL_RODD_CODD);
//        addExclusion(MarkdownTokenTypes.TABLE_HEADER, MarkdownTokenTypes.TABLE_CELL_RODD_CEVEN);
//        addExclusion(MarkdownTokenTypes.TABLE_HEADER, MarkdownTokenTypes.TABLE_CELL_REVEN_CODD);
//        addExclusion(MarkdownTokenTypes.TABLE_HEADER, MarkdownTokenTypes.TABLE_CELL_REVEN_CEVEN);
//        addExclusion(MarkdownTokenTypes.TABLE_HEADER, MarkdownTokenTypes.TABLE_COLUMN);

        // thee can affect text and should combine attributes
        addInlineExclusions(MarkdownTokenTypes.TABLE_HEADER);
        addInlineExclusions(MarkdownTokenTypes.TABLE_CELL_RODD_CODD);
        addInlineExclusions(MarkdownTokenTypes.TABLE_CELL_RODD_CEVEN);
        addInlineExclusions(MarkdownTokenTypes.TABLE_CELL_REVEN_CODD);
        addInlineExclusions(MarkdownTokenTypes.TABLE_CELL_REVEN_CEVEN);
        addInlineExclusions(MarkdownTokenTypes.TABLE_CAPTION);

        // list item is useless, should not punch out block quote, but it should punch out bullet_list
        // that way only the bullets will be left to punch out  the block quote
        addExclusion(MarkdownTokenTypes.BLOCK_QUOTE, MarkdownTokenTypes.LIST_ITEM);
        addInlineExclusions(MarkdownTokenTypes.BLOCK_QUOTE);

        // let all the inlines not punch through each other
        addInlineExclusions(MarkdownTokenTypes.STRIKETHROUGH);
//        addInlineExclusions(MarkdownTokenTypes.STRIKETHROUGH_MARKER);
        addInlineExclusions(MarkdownTokenTypes.BOLDITALIC);

        addInlineExclusions(MarkdownTokenTypes.BOLD);
//        addInlineExclusions(MarkdownTokenTypes.BOLD_MARKER);
        addInlineExclusions(MarkdownTokenTypes.ITALIC);
//        addInlineExclusions(MarkdownTokenTypes.ITALIC_MARKER);

        // bold or italic does not punch out bolditalic

        // these should override text
        addInlineExclusions(MarkdownTokenTypes.AUTO_LINK);
        addInlineExclusions(MarkdownTokenTypes.REFERENCE);
        addInlineExclusions(MarkdownTokenTypes.REFERENCE_IMAGE);
        addInlineExclusions(MarkdownTokenTypes.REFERENCE_LINK);
        addInlineExclusions(MarkdownTokenTypes.EXPLICIT_LINK);
        addInlineExclusions(MarkdownTokenTypes.IMAGE);
        addInlineExclusions(MarkdownTokenTypes.ABBREVIATION);
        addInlineExclusions(MarkdownTokenTypes.QUOTE);

        addInlineExclusions(MarkdownTokenTypes.HEADER_LEVEL_1);
        addInlineExclusions(MarkdownTokenTypes.HEADER_LEVEL_2);
        addInlineExclusions(MarkdownTokenTypes.HEADER_LEVEL_3);
        addInlineExclusions(MarkdownTokenTypes.HEADER_LEVEL_4);
        addInlineExclusions(MarkdownTokenTypes.HEADER_LEVEL_5);
        addInlineExclusions(MarkdownTokenTypes.HEADER_LEVEL_6);

        addInlineExclusions(MarkdownTokenTypes.DEFINITION);
        addInlineExclusions(MarkdownTokenTypes.DEFINITION_TERM);
    }

    // Quick fix to overlapping ranges preventing syntax highlighting in parent nodes with highlighting
    // this punches out the children from the parent range so that only unclaimed regions will take the parent's
    // highlight
    // KLUDGE: needs attention
    class Range {
        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        protected int start;

        protected int end;

        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public Range(Range that) {
            this.start = that.start;
            this.end = that.end;
        }

        public boolean doesNotOverlap(Range that) {
            return that.end <= start || that.start >= end;
        }

        public boolean doesOverlap(Range that) {
            return !(that.end <= start || that.start >= end);
        }

        public boolean isEqual(Range that) {
            return end == that.end && start == that.start;
        }

        public boolean doesContain(Range that) {
            return end >= that.end && start <= that.start;
        }

        public boolean doesProperlyContain(Range that) {
            return end > that.end && start < that.start;
        }

        public boolean isEmpty() {
            return start >= end;
        }

        public boolean intersect(Range that) {
            if (start < that.start) start = that.start;
            if (end > that.end) end = that.end;

            if (start >= end) start = end = 0;
            return !isEmpty();
        }

        public boolean exclude(Range that) {
            // lets make sure we don't need to split into 2 ranges
            assert (doesOverlap(that) && !doesProperlyContain(that));

            if (start >= that.start && start < that.end) start = that.end;
            if (end <= that.end && end > that.start) end = that.start;

            if (start >= end) start = end = 0;
            return !isEmpty();
        }
    }

    class SegmentedRange {
        public ArrayList<Range> getSegments() {
            return segments;
        }

        protected ArrayList<Range> segments;

        public IElementType getTokenType() {
            return tokenType;
        }

        public void setTokenType(IElementType tokenType) {
            this.tokenType = tokenType;
        }

        protected IElementType tokenType;

        public boolean isEmpty() {
            return segments.isEmpty();
        }

        SegmentedRange() {
            segments = new ArrayList<Range>();
        }

        SegmentedRange(int start, int end) {
            segments = new ArrayList<Range>(1);
            segments.add(0, new Range(start, end));
        }

        SegmentedRange(Range range) {
            segments = new ArrayList<Range>(1);
            segments.add(0, range);
        }

        SegmentedRange(ArrayList<Range> ranges) {
            segments = new ArrayList<Range>(ranges);
        }

        SegmentedRange(SegmentedRange that) {
            segments = new ArrayList<Range>(that.segments);
        }

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

        protected boolean isExcludedBy(IElementType child) {
            return !isExcluded(tokenType, child);
        }
    }

    protected ArrayList<SegmentedRange> parentRanges = null;

    protected int tableRows = 0;

    protected int rowColumns = 0;

    /** Init/reinit thread local {@link PegDownProcessor}. */
    private static ThreadLocal<PegDownProcessor> initProcessor() {
        return new ThreadLocal<PegDownProcessor>() {
            @Override protected PegDownProcessor initialValue() {
                return new PegDownProcessor(MarkdownGlobalSettings.getInstance().getExtensionsValue(),
                        MarkdownGlobalSettings.getInstance().getParsingTimeout());
            }
        };
    }

    protected void pushRange(Range range, IElementType type) {
        SegmentedRange segmentedRange = new SegmentedRange(range);
        segmentedRange.setTokenType(type);
        parentRanges.add(parentRanges.size(), segmentedRange);
    }

    protected void pushRange(SegmentedRange segmentedRange) {
        parentRanges.add(parentRanges.size(), segmentedRange);
    }

    protected void pushRange(int start, int end, IElementType type) {
        SegmentedRange segmentedRange = new SegmentedRange(start, end);
        segmentedRange.setTokenType(type);
        parentRanges.add(parentRanges.size(), segmentedRange);
    }

    protected SegmentedRange popRange() {
        assert (parentRanges.size() > 0);
        return parentRanges.remove(parentRanges.size() - 1);
    }

    protected SegmentedRange getRange() {
        assert (parentRanges.size() > 0);
        return parentRanges.get(parentRanges.size() - 1);
    }

    protected void clearStack() {
        if (parentRanges == null) {
            parentRanges = new ArrayList<SegmentedRange>(100);
        } else {
            parentRanges.clear();
        }
    }

    /** Build a new instance of {@link MarkdownAnnotator}. */
    public MarkdownAnnotator() {
        clearStack();

        // Listen to global settings changes.
        MarkdownGlobalSettings.getInstance().addListener(globalSettingsListener = new MarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MarkdownGlobalSettings newSettings) {
                processor.remove();
                processor = initProcessor();
            }
        });
    }

    /**
     * Get the text source of the given file.
     *
     * @param file the {@link PsiFile} to process.
     * @return the file text.
     */
    @Nullable @Override
    public String collectInformation(@NotNull PsiFile file) {
        return file.getText();
    }

    /**
     * Collect {@link MarkdownAnnotator.HighlightableToken}s from the given file.
     *
     * @param source the source text to process.
     * @return a {@link Set} of {@link MarkdownAnnotator.HighlightableToken}s that should be used to do the file syntax highlighting.
     */
    @Override
    public Set<HighlightableToken> doAnnotate(final String source) {
        final MarkdownASTVisitor visitor = new MarkdownASTVisitor();
        try {
            clearStack();
            currentStringLength = source.length();
            processor.get().parseMarkdown(source.toCharArray()).accept(visitor);
        } catch (Exception e) {
            LOGGER.error("Failed processing Markdown document", e);
        }
        return visitor.getTokens();
    }

    /**
     * Convert collected {@link MarkdownAnnotator.HighlightableToken}s in syntax highlighting annotations.
     *
     * @param file             the source file.
     * @param annotationResult the {@link Set} of {@link MarkdownAnnotator.HighlightableToken}s collected on the file.
     * @param holder           the annotation holder.
     */
    @Override
    public void apply(final @NotNull PsiFile file,
            final Set<HighlightableToken> annotationResult,
            final @NotNull AnnotationHolder holder) {

        for (final HighlightableToken token : annotationResult) {
            final TextAttributesKey[] attrs = SYNTAX_HIGHLIGHTER.getTokenHighlights(token.getElementType());

            if (attrs.length > 0) holder.createInfoAnnotation(token.getRange(), null).setTextAttributes(attrs[0]);
        }
    }

    /**
     * Describes a range of text that should be highlighted with a specific element type.
     *
     * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
     * @since 0.8
     */
    protected class HighlightableToken {

        /** The text range. */
        protected final TextRange range;

        /** The associated element type. */
        protected final IElementType elementType;

        /**
         * Build a new instance of {@link MarkdownAnnotator.HighlightableToken}.
         *
         * @param range       the text range.
         * @param elementType the associated element type.
         */
        public HighlightableToken(final TextRange range, final IElementType elementType) {
            this.range = range;
            this.elementType = elementType;
        }

        /**
         * Get the token text range.
         *
         * @return {@link #range}
         */
        public TextRange getRange() {
            return range;
        }

        /**
         * Get the token element type.
         *
         * @return {@link #elementType}
         */
        public IElementType getElementType() {
            return elementType;
        }
    }

    /**
     * {@link org.pegdown.ast.Visitor} used by {@link MarkdownAnnotator} to highlight a Markdown document.
     *
     * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
     * @since 0.4
     */

    protected static boolean recursingBold = false;

    protected static boolean recursingItalic = false;
    protected static boolean recursingStrike = false;

    protected class MarkdownASTVisitor implements Visitor {

        /** The collected token set. */
        protected final Set<HighlightableToken> tokens = new HashSet<HighlightableToken>(20);

        /**
         * Get the collected tokens set.
         *
         * @return {@link #tokens}
         */
        public Set<HighlightableToken> getTokens() {
            return tokens;
        }

        /**
         * Visit the {@link org.pegdown.ast.RootNode}.
         *
         * @param node the {@link org.pegdown.ast.RootNode} to visit
         */
        public void visit(RootNode node) {
            for (AbbreviationNode abbreviationNode : node.getAbbreviations()) abbreviationNode.accept(this);
            for (ReferenceNode referenceNode : node.getReferences()) referenceNode.accept(this);
            visitChildren(node);
        }

        /**
         * Visit the {@link org.pegdown.ast.SimpleNode}.
         *
         * @param node the {@link org.pegdown.ast.SimpleNode} to visit
         */
        public void visit(SimpleNode node) {
            switch (node.getType()) {
            case HRule:
                addToken(node, MarkdownTokenTypes.HRULE);
                break;

            case Apostrophe:
            case Ellipsis:
            case Emdash:
            case Endash:
                addToken(node, MarkdownTokenTypes.SMARTS);
                break;

            case Linebreak:
            case Nbsp:
                break;
            }
        }

        /**
         * Visit the {@link org.pegdown.ast.SuperNode}.
         *
         * @param node the {@link org.pegdown.ast.SuperNode} to visit
         */
        public void visit(SuperNode node) {
            visitChildren(node);
        }

        /**
         * Visit the {@link org.pegdown.ast.ParaNode}.
         *
         * @param node the {@link org.pegdown.ast.ParaNode} to visit
         */
        public void visit(ParaNode node) {
            visitChildren(node);
        }

        /**
         * Visit the {@link org.pegdown.ast.Node}.
         * <p/>
         * This method should never get called, highlights node as error.
         *
         * @param node the {@link org.pegdown.ast.Node} to visit
         */
        public void visit(Node node) {
            addToken(node, TokenType.ERROR_ELEMENT);
        }

        /**
         * Visit the {@link TextNode}.
         *
         * @param node the {@link TextNode} to visit
         */
        public void visit(TextNode node) {
            addToken(node, MarkdownTokenTypes.TEXT);
        }

        /**
         * Visit the {@link SpecialTextNode}.
         *
         * @param node the {@link SpecialTextNode} to visit
         */
        public void visit(SpecialTextNode node) {
            addToken(node, (node.getEndIndex() - node.getStartIndex() > 1) ? MarkdownTokenTypes.SPECIAL_TEXT : MarkdownTokenTypes.TEXT);
        }

        /**
         * Visit the {@link StrikeNode}.
         *
         * @param node the {@link StrikeNode} to visit
         */
        @Override
        public void visit(StrikeNode node) {
            if (!recursingStrike) {
                String marker = node.getChars();// != null ? node.getChars() : "~~";
                int markerLength = marker.length();

                ArrayList<Node> children = new ArrayList<Node>(1);
                children.add(node);

                SuperNode parentNode = new SuperNode(children);
                parentNode.setStartIndex(node.getStartIndex());
                parentNode.setEndIndex(node.getEndIndex());

                // now need to truncate children to this range
                limitChildrensRange(parentNode, node.getStartIndex() + markerLength, node.getEndIndex() - (node.isClosed() ? markerLength : 0));

                recursingStrike = true;
                addTokenWithChildren(parentNode, MarkdownTokenTypes.STRIKETHROUGH_MARKER);
                recursingStrike = false;
            } else {
                addTokenWithChildren(node, MarkdownTokenTypes.STRIKETHROUGH);
            }
        }

        /**
         * Visit the {@link StrongEmphSuperNode}.
         *
         * @param node the {@link StrongEmphSuperNode} to visit
         *             <p/>
         *             <p/>
         *             split out the lead-in and terminating sequence into faked parent node
         *             and add new bold_marker and italic_marker token types for the lead and trail chars with child text node
         *             taking on the BOLD and ITALIC tokens. So we can color the lead-in and terminating chars separately.
         */
        public void visit(StrongEmphSuperNode node) {
            if (node.isClosed()) {
                IElementType parentTokenType = node.isStrong() ? MarkdownTokenTypes.BOLD_MARKER : MarkdownTokenTypes.ITALIC_MARKER;
                IElementType tokenType = node.isStrong() ? MarkdownTokenTypes.BOLD : MarkdownTokenTypes.ITALIC;

                if (tokenType == MarkdownTokenTypes.BOLD && !recursingBold || tokenType == MarkdownTokenTypes.ITALIC && !recursingItalic) {
                    String marker = node.getChars();
                    int markerLength = marker.length();

                    ArrayList<Node> children = new ArrayList<Node>(1);
                    children.add(node);

                    SuperNode parentNode = new SuperNode(children);
                    parentNode.setStartIndex(node.getStartIndex());
                    parentNode.setEndIndex(node.getEndIndex());

                    // now need to truncate children to this range
                    limitChildrensRange(parentNode, node.getStartIndex() + markerLength, node.getEndIndex() - markerLength);

                    if (tokenType == MarkdownTokenTypes.BOLD) recursingBold = true;
                    else recursingItalic = true;
                    addTokenWithChildren(parentNode, parentTokenType);
                    if (tokenType == MarkdownTokenTypes.BOLD) recursingBold = false;
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

        /**
         * Visit the {@link ExpImageNode}.
         *
         * @param node the {@link ExpImageNode} to visit
         */
        public void visit(ExpImageNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.IMAGE);
        }

        /**
         * Visit the {@link ExpLinkNode}.
         *
         * @param node the {@link ExpLinkNode} to visit
         */
        public void visit(ExpLinkNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.EXPLICIT_LINK);
        }

        /**
         * Visit the {@link RefLinkNode}.
         *
         * @param node the {@link RefLinkNode} to visit
         */
        public void visit(final RefLinkNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.REFERENCE_LINK);
        }

        /**
         * Visit the {@link AutoLinkNode}.
         *
         * @param node the {@link AutoLinkNode} to visit
         */
        public void visit(AutoLinkNode node) {
            addToken(node, MarkdownTokenTypes.AUTO_LINK);
        }

        /**
         * Visit the {@link MailLinkNode}.
         *
         * @param node the {@link MailLinkNode} to visit
         */
        public void visit(MailLinkNode node) {
            addToken(node, MarkdownTokenTypes.MAIL_LINK);
        }

        /**
         * Visit the {@link HeaderNode}.
         *
         * @param node the {@link HeaderNode} to visit
         */
        public void visit(HeaderNode node) {
            visitChildren(node);

            switch (node.getLevel()) {
            case 1:
                addToken(node, MarkdownTokenTypes.HEADER_LEVEL_1);
                break;
            case 2:
                addToken(node, MarkdownTokenTypes.HEADER_LEVEL_2);
                break;
            case 3:
                addToken(node, MarkdownTokenTypes.HEADER_LEVEL_3);
                break;
            case 4:
                addToken(node, MarkdownTokenTypes.HEADER_LEVEL_4);
                break;
            case 5:
                addToken(node, MarkdownTokenTypes.HEADER_LEVEL_5);
                break;
            case 6:
                addToken(node, MarkdownTokenTypes.HEADER_LEVEL_6);
                break;
            }
        }

        /**
         * Visit the {@link CodeNode}.
         *
         * @param node the {@link CodeNode} to visit
         */
        public void visit(CodeNode node) {
            addToken(node, MarkdownTokenTypes.CODE);
        }

        /**
         * Visit the {@link VerbatimNode}.
         *
         * @param node the {@link VerbatimNode} to visit
         */
        public void visit(VerbatimNode node) {
            addToken(node, MarkdownTokenTypes.VERBATIM);
        }

        /**
         * Visit the {@link WikiLinkNode}.
         *
         * @param node the {@link WikiLinkNode} to visit
         */
        public void visit(WikiLinkNode node) {
            addToken(node, MarkdownTokenTypes.WIKI_LINK);
        }

        /**
         * Visit the {@link QuotedNode}.
         *
         * @param node the {@link QuotedNode} to visit
         */
        public void visit(QuotedNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.QUOTE);
        }

        /**
         * Visit the {@link BlockQuoteNode}.
         *
         * @param node the {@link BlockQuoteNode} to visit
         */
        public void visit(BlockQuoteNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.BLOCK_QUOTE);
        }

        /**
         * Visit the {@link BulletListNode}.
         *
         * @param node the {@link BulletListNode} to visit
         */
        public void visit(BulletListNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.BULLET_LIST);
        }

        /**
         * Visit the {@link OrderedListNode}.
         *
         * @param node the {@link OrderedListNode} to visit
         */
        public void visit(OrderedListNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.ORDERED_LIST);
        }

        /**
         * Visit the {@link ListItemNode}.
         *
         * @param node the {@link ListItemNode} to visit
         */
        public void visit(ListItemNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.LIST_ITEM);
        }

        /**
         * Visit the {@link DefinitionListNode}.
         *
         * @param node the {@link DefinitionListNode} to visit
         */
        public void visit(DefinitionListNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.DEFINITION_LIST);
        }

        /**
         * Visit the {@link DefinitionNode}.
         *
         * @param node the {@link DefinitionNode} to visit
         */
        public void visit(DefinitionNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.DEFINITION);
        }

        /**
         * Visit the {@link DefinitionTermNode}.
         *
         * @param node the {@link DefinitionTermNode} to visit
         */
        public void visit(DefinitionTermNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.DEFINITION_TERM);
        }

        /**
         * Visit the {@link TableNode}.
         *
         * @param node the {@link TableNode} to visit
         */
        public void visit(TableNode node) {
            tableRows = 0;
            addTokenWithChildren(node, MarkdownTokenTypes.TABLE);
        }

        /**
         * Visit the {@link TableBodyNode}.
         *
         * @param node the {@link TableBodyNode} to visit
         */
        public void visit(TableBodyNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.TABLE_BODY);
        }

        /**
         * Visit the {@link TableCellNode}.
         *
         * @param node the {@link TableCellNode} to visit
         */
        public void visit(TableCellNode node) {
            rowColumns++;
            addTokenWithChildren(node, (tableRows & 1) != 0 ? ((rowColumns & 1) != 0 ? MarkdownTokenTypes.TABLE_CELL_RODD_CODD : MarkdownTokenTypes.TABLE_CELL_RODD_CEVEN)
                    : ((rowColumns & 1) != 0 ? MarkdownTokenTypes.TABLE_CELL_REVEN_CODD : MarkdownTokenTypes.TABLE_CELL_REVEN_CEVEN));
        }

        /**
         * Visit the {@link TableColumnNode}.
         *
         * @param node the {@link TableColumnNode} to visit
         */
        public void visit(TableColumnNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.TABLE_COLUMN);
        }

        /**
         * Visit the {@link TableHeaderNode}.
         *
         * @param node the {@link TableHeaderNode} to visit
         */
        public void visit(TableHeaderNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.TABLE_HEADER);
        }

        /**
         * Visit the {@link TableRowNode}.
         *
         * @param node the {@link TableRowNode} to visit
         */
        public void visit(TableRowNode node) {
            tableRows++;
            rowColumns = 0;
            addTokenWithChildren(node, (tableRows & 1) != 0 ? MarkdownTokenTypes.TABLE_ROW_ODD : MarkdownTokenTypes.TABLE_ROW_EVEN);
        }

        /**
         * Visit the {@link TableCaptionNode}.
         *
         * @param node the {@link TableCaptionNode} to visit
         */
        public void visit(TableCaptionNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.TABLE_CAPTION);
        }

        /**
         * Visit the {@link HtmlBlockNode}.
         * <p/>
         * TODO: Real HTML support not implemented.
         *
         * @param node the {@link HtmlBlockNode} to visit
         */
        public void visit(HtmlBlockNode node) {
            addToken(node, MarkdownTokenTypes.HTML_BLOCK);
        }

        /**
         * Visit the {@link InlineHtmlNode}.
         * <p/>
         * TODO: Real HTML support not implemented.
         *
         * @param node the {@link InlineHtmlNode} to visit
         */
        public void visit(InlineHtmlNode node) {
            addToken(node, MarkdownTokenTypes.INLINE_HTML);
        }

        /**
         * Visit the {@link ReferenceNode}.
         *
         * @param node the {@link ReferenceNode} to visit
         */
        public void visit(ReferenceNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.REFERENCE);
        }

        /**
         * Visit the {@link RefImageNode}.
         *
         * @param node the {@link RefImageNode} to visit
         */
        public void visit(RefImageNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.REFERENCE_IMAGE);
        }

        /**
         * Visit the {@link AbbreviationNode}.
         *
         * @param node the {@link AbbreviationNode} to visit
         */
        public void visit(AbbreviationNode node) {
            addTokenWithChildren(node, MarkdownTokenTypes.ABBREVIATION);
        }

        public void visit(AnchorLinkNode node) {
            addToken(node, MarkdownTokenTypes.ANCHOR_LINK);
        }

        /**
         * Visit a {@link SuperNode}'s children.
         *
         * @param node the {@link Node} to visit
         */
        protected void visitChildren(SuperNode node) {
            for (Node child : node.getChildren()) child.accept(this);
        }

        protected boolean excludeAncestors(Range range, IElementType type) {
            int iMax = parentRanges.size();
            boolean applyType = true;

            for (SegmentedRange parentRange : parentRanges) {
                if (parentRange.isExcludedBy(type)) {
                    parentRange.exclude(range);
                } else {
                    // exclude the parent from this range, we will assume that it is fully contained
//                    applyType = false;
                }
            }
            return applyType;
        }

        // to overcome the problem that a parent has a wider range than the child, we add children first
        // so that their attributes take priority
        protected void addTokenWithChildren(Node node, IElementType tokenType) {
            int endIndex = node.getEndIndex();
            int startIndex = node.getStartIndex();

            // compensate for missing EOL at end of input causes pegdown to return a range past end of input
            // in this case IDEA ignores the range. :(
            if (endIndex > currentStringLength) endIndex = currentStringLength;

            if (startIndex < endIndex) {
                SegmentedRange boldItalic = null;

                if (tokenType == MarkdownTokenTypes.BOLD || tokenType == MarkdownTokenTypes.ITALIC) {
                    Range range = new Range(startIndex, endIndex);

                    // here we have to combine bold and italic into bolditalic of our parent ranges
                    for (SegmentedRange parentRange : parentRanges) {
                        if (parentRange.getTokenType() == MarkdownTokenTypes.BOLD && tokenType == MarkdownTokenTypes.ITALIC
                                || parentRange.getTokenType() == MarkdownTokenTypes.ITALIC && tokenType == MarkdownTokenTypes.BOLD) {
                            if (boldItalic == null) {
                                boldItalic = new SegmentedRange();
                                boldItalic.setTokenType(MarkdownTokenTypes.BOLDITALIC);
                            }

                            // we will create an intersection and make it punch through parents and this range
                            boldItalic.addIntersections(range, parentRange);
                        }
                    }

                    // we now have a range that we need
                    if (boldItalic != null && boldItalic.isEmpty()) boldItalic = null;
                }

                pushRange(startIndex, endIndex, tokenType);

                if (boldItalic != null) {
                    pushRange(boldItalic);
                    visitChildren((SuperNode) node);
                    popRange();
                    addSegmentedToken(boldItalic, true);
                } else {
                    visitChildren((SuperNode) node);
                }

                SegmentedRange segmentedRange = popRange();
                addSegmentedToken(segmentedRange, true);
            } else {
                // empty nothing to do
                return;
            }
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
                        tokens.add(new HighlightableToken(new TextRange(range.getStart(), range.getEnd()), tokenType));
                        //System.out.print("adding " + tokenType + " for [" + range.getStart() + ", " + range.getEnd() + ")\n");
                    }
                }
            }
        }

        /**
         * Add the given {@link Node} to the set of highlightable tokens.
         *
         * @param node      the {@link Node} to setup highlighting for
         * @param tokenType the {IElementType} to use for highlighting
         */
        protected void addToken(Node node, IElementType tokenType) {
            int endIndex = node.getEndIndex();
            int startIndex = node.getStartIndex();

            if (tokenType == MarkdownTokenTypes.QUOTE) {
                int tmp = 0;
            }
            // compensate for missing EOL at end of input causes pegdown to return a range past end of input
            // in this case IDEA ignores the range. :(
            if (endIndex > currentStringLength) endIndex = currentStringLength;

            Range range = new Range(startIndex, endIndex);
            if (!range.isEmpty() && (parentRanges.size() <= 0 || excludeAncestors(range, tokenType))) {
                // wasn't stripped out, set it
                tokens.add(new HighlightableToken(new TextRange(range.getStart(), range.getEnd()), tokenType));
                //System.out.print("adding " + tokenType + " for [" + range.getStart() + ", " + range.getEnd() + ")\n");
            }
        }
    }
}
