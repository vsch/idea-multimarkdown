/*
 * Copyright (c) 2011-2013 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import net.nicoulaj.idea.markdown.highlighter.MarkdownSyntaxHighlighter;
import net.nicoulaj.idea.markdown.settings.MarkdownGlobalSettings;
import net.nicoulaj.idea.markdown.settings.MarkdownGlobalSettingsListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.*;

import java.util.HashSet;
import java.util.Set;

import static net.nicoulaj.idea.markdown.lang.MarkdownTokenTypes.*;

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

    /** The {@link PegDownProcessor} used for building the document AST. */
    private PegDownProcessor processor = new PegDownProcessor(MarkdownGlobalSettings.getInstance().getExtensionsValue(),
                                                              MarkdownGlobalSettings.getInstance().getParsingTimeout());

    /** Build a new instance of {@link MarkdownAnnotator}. */
    public MarkdownAnnotator() {
        // Listen to global settings changes.
        MarkdownGlobalSettings.getInstance().addListener(new MarkdownGlobalSettingsListener() {
            public void handleSettingsChanged(@NotNull final MarkdownGlobalSettings newSettings) {
                processor = new PegDownProcessor(newSettings.getExtensionsValue(),
                                                 newSettings.getParsingTimeout());
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
     * Collect {@link net.nicoulaj.idea.markdown.annotator.MarkdownAnnotator.HighlightableToken}s from the given file.
     *
     * @param source the source text to process.
     * @return a {@link Set} of {@link net.nicoulaj.idea.markdown.annotator.MarkdownAnnotator.HighlightableToken}s that should be used to do the file syntax highlighting.
     */
    @Override
    public Set<HighlightableToken> doAnnotate(final String source) {
        final MarkdownASTVisitor visitor = new MarkdownASTVisitor();
        try {
            processor.parseMarkdown(source.toCharArray()).accept(visitor);
        } catch (Exception e) {
            LOGGER.error("Failed processing Markdown document", e);
        }
        return visitor.getTokens();
    }

    /**
     * Convert collected {@link net.nicoulaj.idea.markdown.annotator.MarkdownAnnotator.HighlightableToken}s in syntax highlighting annotations.
     *
     * @param file             the source file.
     * @param annotationResult the {@link Set} of {@link net.nicoulaj.idea.markdown.annotator.MarkdownAnnotator.HighlightableToken}s collected on the file.
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
         * Build a new instance of {@link net.nicoulaj.idea.markdown.annotator.MarkdownAnnotator.HighlightableToken}.
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
                addToken(node, HRULE);
                break;
            case Apostrophe:
            case Ellipsis:
            case Emdash:
            case Endash:
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
            addToken(node, ERROR_ELEMENT);
        }

        /**
         * Visit the {@link TextNode}.
         *
         * @param node the {@link TextNode} to visit
         */
        public void visit(TextNode node) {
            addToken(node, TEXT);
        }

        /**
         * Visit the {@link SpecialTextNode}.
         *
         * @param node the {@link SpecialTextNode} to visit
         */
        public void visit(SpecialTextNode node) {
            addToken(node, SPECIAL_TEXT);
        }

        /**
         * Visit the {@link StrongEmphSuperNode}.
         *
         * @param node the {@link StrongEmphSuperNode} to visit
         */
        public void visit(StrongEmphSuperNode node) {
            addToken(node, node.isStrong() ? BOLD : ITALIC);
            visitChildren(node);
        }

        /**
         * Visit the {@link ExpImageNode}.
         *
         * @param node the {@link ExpImageNode} to visit
         */
        public void visit(ExpImageNode node) {
            addToken(node, IMAGE);
            visitChildren(node);
        }

        /**
         * Visit the {@link ExpLinkNode}.
         *
         * @param node the {@link ExpLinkNode} to visit
         */
        public void visit(ExpLinkNode node) {
            addToken(node, EXPLICIT_LINK);
            visitChildren(node);
        }

        /**
         * Visit the {@link RefLinkNode}.
         *
         * @param node the {@link RefLinkNode} to visit
         */
        public void visit(final RefLinkNode node) {
            addToken(node, REFERENCE_LINK);
            visitChildren(node);
        }

        /**
         * Visit the {@link AutoLinkNode}.
         *
         * @param node the {@link AutoLinkNode} to visit
         */
        public void visit(AutoLinkNode node) {
            addToken(node, AUTO_LINK);
        }

        /**
         * Visit the {@link MailLinkNode}.
         *
         * @param node the {@link MailLinkNode} to visit
         */
        public void visit(MailLinkNode node) {
            addToken(node, MAIL_LINK);
        }

        /**
         * Visit the {@link HeaderNode}.
         *
         * @param node the {@link HeaderNode} to visit
         */
        public void visit(HeaderNode node) {
            switch (node.getLevel()) {
            case 1:
                addToken(node, HEADER_LEVEL_1);
                break;
            case 2:
                addToken(node, HEADER_LEVEL_2);
                break;
            case 3:
                addToken(node, HEADER_LEVEL_3);
                break;
            case 4:
                addToken(node, HEADER_LEVEL_4);
                break;
            case 5:
                addToken(node, HEADER_LEVEL_5);
                break;
            case 6:
                addToken(node, HEADER_LEVEL_6);
                break;
            }
            visitChildren(node);
        }

        /**
         * Visit the {@link CodeNode}.
         *
         * @param node the {@link CodeNode} to visit
         */
        public void visit(CodeNode node) {
            addToken(node, CODE);
        }

        /**
         * Visit the {@link VerbatimNode}.
         *
         * @param node the {@link VerbatimNode} to visit
         */
        public void visit(VerbatimNode node) {
            addToken(node, VERBATIM);
        }

        /**
         * Visit the {@link WikiLinkNode}.
         *
         * @param node the {@link WikiLinkNode} to visit
         */
        public void visit(WikiLinkNode node) {
            addToken(node, WIKI_LINK);
        }

        /**
         * Visit the {@link QuotedNode}.
         *
         * @param node the {@link QuotedNode} to visit
         */
        public void visit(QuotedNode node) {
            addToken(node, QUOTE);
        }

        /**
         * Visit the {@link BlockQuoteNode}.
         *
         * @param node the {@link BlockQuoteNode} to visit
         */
        public void visit(BlockQuoteNode node) {
            addToken(node, BLOCK_QUOTE);
            visitChildren(node);
        }

        /**
         * Visit the {@link BulletListNode}.
         *
         * @param node the {@link BulletListNode} to visit
         */
        public void visit(BulletListNode node) {
            addToken(node, BULLET_LIST);
            visitChildren(node);
        }

        /**
         * Visit the {@link OrderedListNode}.
         *
         * @param node the {@link OrderedListNode} to visit
         */
        public void visit(OrderedListNode node) {
            addToken(node, ORDERED_LIST);
            visitChildren(node);
        }

        /**
         * Visit the {@link ListItemNode}.
         *
         * @param node the {@link ListItemNode} to visit
         */
        public void visit(ListItemNode node) {
            addToken(node, LIST_ITEM);
            visitChildren(node);
        }

        /**
         * Visit the {@link DefinitionListNode}.
         *
         * @param node the {@link DefinitionListNode} to visit
         */
        public void visit(DefinitionListNode node) {
            addToken(node, DEFINITION_LIST);
            visitChildren(node);
        }

        /**
         * Visit the {@link DefinitionNode}.
         *
         * @param node the {@link DefinitionNode} to visit
         */
        public void visit(DefinitionNode node) {
            addToken(node, DEFINITION);
            visitChildren(node);
        }

        /**
         * Visit the {@link DefinitionTermNode}.
         *
         * @param node the {@link DefinitionTermNode} to visit
         */
        public void visit(DefinitionTermNode node) {
            addToken(node, DEFINITION_TERM);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableNode}.
         *
         * @param node the {@link TableNode} to visit
         */
        public void visit(TableNode node) {
            addToken(node, TABLE);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableBodyNode}.
         *
         * @param node the {@link TableBodyNode} to visit
         */
        public void visit(TableBodyNode node) {
            addToken(node, TABLE_BODY);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableCellNode}.
         *
         * @param node the {@link TableCellNode} to visit
         */
        public void visit(TableCellNode node) {
            addToken(node, TABLE_CELL);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableColumnNode}.
         *
         * @param node the {@link TableColumnNode} to visit
         */
        public void visit(TableColumnNode node) {
            addToken(node, TABLE_COLUMN);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableHeaderNode}.
         *
         * @param node the {@link TableHeaderNode} to visit
         */
        public void visit(TableHeaderNode node) {
            addToken(node, TABLE_HEADER);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableRowNode}.
         *
         * @param node the {@link TableRowNode} to visit
         */
        public void visit(TableRowNode node) {
            addToken(node, TABLE_ROW);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableCaptionNode}.
         *
         * @param node the {@link TableCaptionNode} to visit
         */
        public void visit(TableCaptionNode node) {
            addToken(node, TABLE_CAPTION);
            visitChildren(node);
        }

        /**
         * Visit the {@link HtmlBlockNode}.
         * <p/>
         * TODO: Real HTML support not implemented.
         *
         * @param node the {@link HtmlBlockNode} to visit
         */
        public void visit(HtmlBlockNode node) {
            addToken(node, HTML_BLOCK);
        }

        /**
         * Visit the {@link InlineHtmlNode}.
         * <p/>
         * TODO: Real HTML support not implemented.
         *
         * @param node the {@link InlineHtmlNode} to visit
         */
        public void visit(InlineHtmlNode node) {
            addToken(node, INLINE_HTML);
        }

        /**
         * Visit the {@link ReferenceNode}.
         *
         * @param node the {@link ReferenceNode} to visit
         */
        public void visit(ReferenceNode node) {
            addToken(node, REFERENCE);
            visitChildren(node);
        }

        /**
         * Visit the {@link RefImageNode}.
         *
         * @param node the {@link RefImageNode} to visit
         */
        public void visit(RefImageNode node) {
            addToken(node, REFERENCE_IMAGE);
            visitChildren(node);
        }

        /**
         * Visit the {@link AbbreviationNode}.
         *
         * @param node the {@link AbbreviationNode} to visit
         */
        public void visit(AbbreviationNode node) {
            addToken(node, ABBREVIATION);
            visitChildren(node);
        }

        /**
         * Visit a {@link SuperNode}'s children.
         *
         * @param node the {@link Node} to visit
         */
        protected void visitChildren(SuperNode node) {
            for (Node child : node.getChildren()) child.accept(this);
        }

        /**
         * Add the given {@link Node} to the set of highlightable tokens.
         *
         * @param node      the {@link Node} to setup highlighting for
         * @param tokenType the {IElementType} to use for highlighting
         */
        protected void addToken(Node node, IElementType tokenType) {
            if (node.getStartIndex() < node.getEndIndex())
                tokens.add(new HighlightableToken(new TextRange(node.getStartIndex(), node.getEndIndex()), tokenType));
        }
    }
}
