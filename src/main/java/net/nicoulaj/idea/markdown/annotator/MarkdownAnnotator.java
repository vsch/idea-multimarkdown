/*
 * Copyright (c) 2011 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import net.nicoulaj.idea.markdown.highlighter.MarkdownSyntaxHighlighter;
import net.nicoulaj.idea.markdown.lang.MarkdownTokenTypes;
import net.nicoulaj.idea.markdown.settings.MarkdownGlobalSettings;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.*;

/**
 * {@link ExternalAnnotator} responsible for syntax highlighting Markdown files.
 * <p/>
 * This is a hack to avoid implementing {@link com.intellij.lexer.Lexer},
 * and directly use the AST from <a href="http://pegdown.org">pegdown</a>'s {@link PegDownProcessor} instead.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.4
 */
public class MarkdownAnnotator implements ExternalAnnotator {

    /**
     * The {@link PegDownProcessor} used for building the document AST.
     */
    protected final PegDownProcessor processor = new PegDownProcessor(MarkdownGlobalSettings.getInstance().getExtensionsValue());

    /**
     * Annotates the specified file.
     * <p/>
     * Builds the AST and visit it with a {@link MarkdownASTVisitor}.
     *
     * @param file   the file to annotate.
     * @param holder the container which receives annotations created by the plugin.
     */
    public void annotate(PsiFile file, AnnotationHolder holder) {
        processor.parseMarkdown(file.textToCharArray()).accept(new MarkdownASTVisitor(holder));
    }

    /**
     * {@link org.pegdown.ast.Visitor} used by {@link MarkdownAnnotator} to highlight a Markdown document.
     *
     * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
     * @since 0.4
     */
    public class MarkdownASTVisitor implements Visitor {

        /**
         * The {@link com.intellij.openapi.fileTypes.SyntaxHighlighter} used by
         * {@link #highlight(org.pegdown.ast.Node, com.intellij.psi.tree.IElementType)}.
         */
        protected final SyntaxHighlighter SYNTAX_HIGHLIGHTER = new MarkdownSyntaxHighlighter();

        /**
         * The {@link AnnotationHolder} to use for creating {@link com.intellij.lang.annotation.Annotation}s.
         */
        protected AnnotationHolder annotationHolder;

        /**
         * Build a new instance of {@link MarkdownASTVisitor}.
         *
         * @param annotationHolder the {@link AnnotationHolder} to use for creating {@link com.intellij.lang.annotation.Annotation}s.
         */
        public MarkdownASTVisitor(AnnotationHolder annotationHolder) {
            this.annotationHolder = annotationHolder;
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
                    highlight(node, MarkdownTokenTypes.HRULE);
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
            highlight(node, MarkdownTokenTypes.ERROR_ELEMENT);
        }

        /**
         * Visit the {@link TextNode}.
         *
         * @param node the {@link TextNode} to visit
         */
        public void visit(TextNode node) {
            highlight(node, MarkdownTokenTypes.TEXT);
        }

        /**
         * Visit the {@link SpecialTextNode}.
         *
         * @param node the {@link SpecialTextNode} to visit
         */
        public void visit(SpecialTextNode node) {
            highlight(node, MarkdownTokenTypes.SPECIAL_TEXT);
        }

        /**
         * Visit the {@link EmphNode}.
         *
         * @param node the {@link EmphNode} to visit
         */
        public void visit(EmphNode node) {
            highlight(node, MarkdownTokenTypes.ITALIC);
            visitChildren(node);
        }

        /**
         * Visit the {@link ExpImageNode}.
         *
         * @param node the {@link ExpImageNode} to visit
         */
        public void visit(ExpImageNode node) {
            highlight(node, MarkdownTokenTypes.IMAGE);
            visitChildren(node);
        }

        /**
         * Visit the {@link StrongNode}.
         *
         * @param node the {@link StrongNode} to visit
         */
        public void visit(StrongNode node) {
            highlight(node, MarkdownTokenTypes.BOLD);
            visitChildren(node);
        }

        /**
         * Visit the {@link ExpLinkNode}.
         *
         * @param node the {@link ExpLinkNode} to visit
         */
        public void visit(ExpLinkNode node) {
            highlight(node, MarkdownTokenTypes.EXPLICIT_LINK);
            visitChildren(node);
        }

        /**
         * Visit the {@link RefLinkNode}.
         *
         * @param node the {@link RefLinkNode} to visit
         */
        public void visit(final RefLinkNode node) {
            highlight(node, MarkdownTokenTypes.REFERENCE_LINK);
            visitChildren(node);
        }

        /**
         * Visit the {@link AutoLinkNode}.
         *
         * @param node the {@link AutoLinkNode} to visit
         */
        public void visit(AutoLinkNode node) {
            highlight(node, MarkdownTokenTypes.AUTO_LINK);
        }

        /**
         * Visit the {@link MailLinkNode}.
         *
         * @param node the {@link MailLinkNode} to visit
         */
        public void visit(MailLinkNode node) {
            highlight(node, MarkdownTokenTypes.MAIL_LINK);
        }

        /**
         * Visit the {@link HeaderNode}.
         *
         * @param node the {@link HeaderNode} to visit
         */
        public void visit(HeaderNode node) {
            switch (node.getLevel()) {
                case 1:
                    highlight(node, MarkdownTokenTypes.HEADER_LEVEL_1);
                    break;
                case 2:
                    highlight(node, MarkdownTokenTypes.HEADER_LEVEL_2);
                    break;
                case 3:
                    highlight(node, MarkdownTokenTypes.HEADER_LEVEL_3);
                    break;
                case 4:
                    highlight(node, MarkdownTokenTypes.HEADER_LEVEL_4);
                    break;
                case 5:
                    highlight(node, MarkdownTokenTypes.HEADER_LEVEL_5);
                    break;
                case 6:
                    highlight(node, MarkdownTokenTypes.HEADER_LEVEL_6);
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
            highlight(node, MarkdownTokenTypes.CODE);
        }

        /**
         * Visit the {@link VerbatimNode}.
         *
         * @param node the {@link VerbatimNode} to visit
         */
        public void visit(VerbatimNode node) {
            highlight(node, MarkdownTokenTypes.VERBATIM);
        }

        /**
         * Visit the {@link WikiLinkNode}.
         *
         * @param node the {@link WikiLinkNode} to visit
         */
        public void visit(WikiLinkNode node) {
            highlight(node, MarkdownTokenTypes.REFERENCE_LINK); // TODO Add a dedicated token type
        }

        /**
         * Visit the {@link QuotedNode}.
         *
         * @param node the {@link QuotedNode} to visit
         */
        public void visit(QuotedNode node) {
            highlight(node, MarkdownTokenTypes.QUOTE);
        }

        /**
         * Visit the {@link BlockQuoteNode}.
         *
         * @param node the {@link BlockQuoteNode} to visit
         */
        public void visit(BlockQuoteNode node) {
            highlight(node, MarkdownTokenTypes.BLOCK_QUOTE);
            visitChildren(node);
        }

        /**
         * Visit the {@link BulletListNode}.
         *
         * @param node the {@link BulletListNode} to visit
         */
        public void visit(BulletListNode node) {
            highlight(node, MarkdownTokenTypes.BULLET_LIST);
            visitChildren(node);
        }

        /**
         * Visit the {@link OrderedListNode}.
         *
         * @param node the {@link OrderedListNode} to visit
         */
        public void visit(OrderedListNode node) {
            highlight(node, MarkdownTokenTypes.ORDERED_LIST);
            visitChildren(node);
        }

        /**
         * Visit the {@link ListItemNode}.
         *
         * @param node the {@link ListItemNode} to visit
         */
        public void visit(ListItemNode node) {
            highlight(node, MarkdownTokenTypes.LIST_ITEM);
            visitChildren(node);
        }

        /**
         * Visit the {@link DefinitionListNode}.
         *
         * @param node the {@link DefinitionListNode} to visit
         */
        public void visit(DefinitionListNode node) {
            highlight(node, MarkdownTokenTypes.DEFINITION_LIST);
            visitChildren(node);
        }

        /**
         * Visit the {@link DefinitionNode}.
         *
         * @param node the {@link DefinitionNode} to visit
         */
        public void visit(DefinitionNode node) {
            highlight(node, MarkdownTokenTypes.DEFINITION);
            visitChildren(node);
        }

        /**
         * Visit the {@link DefinitionTermNode}.
         *
         * @param node the {@link DefinitionTermNode} to visit
         */
        public void visit(DefinitionTermNode node) {
            highlight(node, MarkdownTokenTypes.DEFINITION_TERM);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableNode}.
         *
         * @param node the {@link TableNode} to visit
         */
        public void visit(TableNode node) {
            highlight(node, MarkdownTokenTypes.TABLE);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableBodyNode}.
         *
         * @param node the {@link TableBodyNode} to visit
         */
        public void visit(TableBodyNode node) {
            highlight(node, MarkdownTokenTypes.TABLE_BODY);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableCellNode}.
         *
         * @param node the {@link TableCellNode} to visit
         */
        public void visit(TableCellNode node) {
            highlight(node, MarkdownTokenTypes.TABLE_CELL);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableColumnNode}.
         *
         * @param node the {@link TableColumnNode} to visit
         */
        public void visit(TableColumnNode node) {
            highlight(node, MarkdownTokenTypes.TABLE_COLUMN);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableHeaderNode}.
         *
         * @param node the {@link TableHeaderNode} to visit
         */
        public void visit(TableHeaderNode node) {
            highlight(node, MarkdownTokenTypes.TABLE_HEADER);
            visitChildren(node);
        }

        /**
         * Visit the {@link TableRowNode}.
         *
         * @param node the {@link TableRowNode} to visit
         */
        public void visit(TableRowNode node) {
            highlight(node, MarkdownTokenTypes.TABLE_ROW);
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
            highlight(node, MarkdownTokenTypes.HTML_BLOCK);
        }

        /**
         * Visit the {@link InlineHtmlNode}.
         * <p/>
         * TODO: Real HTML support not implemented.
         *
         * @param node the {@link InlineHtmlNode} to visit
         */
        public void visit(InlineHtmlNode node) {
            highlight(node, MarkdownTokenTypes.INLINE_HTML);
        }

        /**
         * Visit the {@link ReferenceNode}.
         *
         * @param node the {@link ReferenceNode} to visit
         */
        public void visit(ReferenceNode node) {
            highlight(node, MarkdownTokenTypes.REFERENCE);
            visitChildren(node);
        }

        /**
         * Visit the {@link RefImageNode}.
         *
         * @param node the {@link RefImageNode} to visit
         */
        public void visit(RefImageNode node) {
            highlight(node, MarkdownTokenTypes.IMAGE); // TODO Add a dedicated token type
            visitChildren(node);
        }

        /**
         * Visit the {@link AbbreviationNode}.
         *
         * @param node the {@link AbbreviationNode} to visit
         */
        public void visit(AbbreviationNode node) {
            highlight(node, MarkdownTokenTypes.ABBREVIATION);
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
         * Set the highlighting type for the {@link Node} after an {@link com.intellij.psi.tree.IElementType}.
         *
         * @param node      the {@link Node} to setup highlighting for
         * @param tokenType the {IElementType} to use for highlighting
         */
        protected void highlight(Node node, IElementType tokenType) {
            if (node.getStartIndex() < node.getEndIndex())
                annotationHolder.createInfoAnnotation(new TextRange(node.getStartIndex(), node.getEndIndex()), null)
                                .setTextAttributes(SYNTAX_HIGHLIGHTER.getTokenHighlights(tokenType)[0]);
        }
    }
}
