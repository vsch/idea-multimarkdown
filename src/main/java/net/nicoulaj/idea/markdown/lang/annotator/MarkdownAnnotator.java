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
package net.nicoulaj.idea.markdown.lang.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import net.nicoulaj.idea.markdown.highlighter.MarkdownSyntaxHighlighter;
import net.nicoulaj.idea.markdown.lang.MarkdownTokenTypes;
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
    protected final PegDownProcessor processor = new PegDownProcessor();

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
         * <p/>
         * Visits children nodes.
         * <p/>
         * TODO Abbreviation and Reference nodes are not visited.
         *
         * @param node the {@link org.pegdown.ast.RootNode} to visit
         */
        public void visit(RootNode node) {
            visitChildren(node);
        }

        /**
         * Visit the {@link org.pegdown.ast.SimpleNode}.
         * <p/>
         * Does nothing.
         *
         * @param node the {@link org.pegdown.ast.SimpleNode} to visit
         */
        public void visit(SimpleNode node) {
        }

        /**
         * Visit the {@link org.pegdown.ast.SuperNode}.
         * <p/>
         * Visits children nodes.
         *
         * @param node the {@link org.pegdown.ast.SuperNode} to visit
         */
        public void visit(SuperNode node) {
            visitChildren(node);
        }

        /**
         * Visit the {@link org.pegdown.ast.ParaNode}.
         * <p/>
         * Visits children nodes.
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
         * <p/>
         * Highlights node as text.
         *
         * @param node the {@link TextNode} to visit
         */
        public void visit(TextNode node) {
            highlight(node, MarkdownTokenTypes.TEXT);
        }

        /**
         * Visit the {@link SpecialTextNode}.
         * <p/>
         * Highlights node as text.
         *
         * @param node the {@link SpecialTextNode} to visit
         */
        public void visit(SpecialTextNode node) {
            highlight(node, MarkdownTokenTypes.TEXT);
        }

        /**
         * Visit the {@link EmphNode}.
         * <p/>
         * Highlights the node as link.
         *
         * @param node the {@link EmphNode} to visit
         */
        public void visit(EmphNode node) {
            highlight(node, MarkdownTokenTypes.ITALIC);
        }

        /**
         * Visit the {@link StrongNode}.
         * <p/>
         * Highlights the node as link.
         *
         * @param node the {@link StrongNode} to visit
         */
        public void visit(StrongNode node) {
            highlight(node, MarkdownTokenTypes.BOLD);
        }

        /**
         * Visit the {@link ExpLinkNode}.
         * <p/>
         * Highlights the node as link or image..
         *
         * @param node the {@link ExpLinkNode} to visit
         */
        public void visit(ExpLinkNode node) {
            if (node.getImage()) highlight(node, MarkdownTokenTypes.IMAGE);
            else highlight(node, MarkdownTokenTypes.LINK);
        }

        /**
         * Visit the {@link RefLinkNode}.
         * <p/>
         * Highlights the node as link.
         *
         * @param node the {@link RefLinkNode} to visit
         */
        public void visit(final RefLinkNode node) {
            highlight(node, MarkdownTokenTypes.LINK);
        }

        /**
         * Visit the {@link AutoLinkNode}.
         * <p/>
         * Highlights the node as link.
         *
         * @param node the {@link AutoLinkNode} to visit
         */
        public void visit(AutoLinkNode node) {
            highlight(node, MarkdownTokenTypes.LINK);
        }

        /**
         * Visit the {@link AutoLinkNode}.
         * <p/>
         * Highlights the node as link.
         *
         * @param node the {@link AutoLinkNode} to visit
         */
        public void visit(MailLinkNode node) {
            highlight(node, MarkdownTokenTypes.LINK);
        }

        /**
         * Visit the {@link HeaderNode}.
         * <p/>
         * Highlights the node as header with given level.
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
         * <p/>
         * Highlights the node as code.
         *
         * @param node the {@link CodeNode} to visit
         */
        public void visit(CodeNode node) {
            highlight(node, MarkdownTokenTypes.CODE);
        }

        /**
         * Visit the {@link VerbatimNode}.
         * <p/>
         * Highlights the node as code.
         *
         * @param node the {@link VerbatimNode} to visit
         */
        public void visit(VerbatimNode node) {
            highlight(node, MarkdownTokenTypes.CODE);
        }

        /**
         * Visit the {@link QuotedNode}.
         * <p/>
         * Highlights the node as quote.
         *
         * @param node the {@link QuotedNode} to visit
         */
        public void visit(QuotedNode node) {
            highlight(node, MarkdownTokenTypes.QUOTE);
        }

        /**
         * Visit the {@link QuotedNode}.
         * <p/>
         * Highlights the node as quote.
         *
         * @param node the {@link QuotedNode} to visit
         */
        public void visit(BlockQuoteNode node) {
            highlight(node, MarkdownTokenTypes.QUOTE);
        }

        /**
         * Visit the {@link BulletListNode}.
         * <p/>
         * Visits children nodes.
         *
         * @param node the {@link BulletListNode} to visit
         */
        public void visit(BulletListNode node) {
            visitChildren(node);
        }

        /**
         * Visit the {@link OrderedListNode}.
         * <p/>
         * Visits children nodes.
         *
         * @param node the {@link OrderedListNode} to visit
         */
        public void visit(OrderedListNode node) {
            visitChildren(node);
        }

        /**
         * Visit the {@link TightListItemNode}.
         * <p/>
         * FIXME: Does nothing as there is no way to get the absolute start and end index of a token.
         *
         * @param node the {@link TightListItemNode} to visit
         */
        public void visit(TightListItemNode node) {
        }

        /**
         * Visit the {@link LooseListItemNode}.
         * <p/>
         * FIXME: Does nothing as there is no way to get the absolute start and end index of a token.
         *
         * @param node the {@link LooseListItemNode} to visit
         */
        public void visit(LooseListItemNode node) {
        }

        /**
         * Visit the {@link TableNode}.
         * <p/>
         * Highlights the node as a table.
         * TODO: Should we highlight into tables ?
         *
         * @param node the {@link TableNode} to visit
         */
        public void visit(TableNode node) {
            highlight(node, MarkdownTokenTypes.TABLE);
        }

        /**
         * Visit the {@link TableNode}.
         * <p/>
         * Highlights the node as a table.
         * TODO: Should we highlight into tables ?
         *
         * @param node the {@link TableNode} to visit
         */
        public void visit(TableBodyNode node) {
        }

        /**
         * Visit the {@link TableCellNode}.
         * <p/>
         * Does not do anything, tables highlighting is taken care of by {@link #visit(org.pegdown.ast.TableNode)}.
         * TODO: Should we highlight into tables ?
         *
         * @param node the {@link TableCellNode} to visit
         */
        public void visit(TableCellNode node) {
        }

        /**
         * Visit the {@link TableColumnNode}.
         * <p/>
         * Does not do anything, tables highlighting is taken care of by {@link #visit(org.pegdown.ast.TableNode)}.
         * TODO: Should we highlight into tables ?
         *
         * @param node the {@link TableColumnNode} to visit
         */
        public void visit(TableColumnNode node) {
        }

        /**
         * Visit the {@link TableHeaderNode}.
         * <p/>
         * Does not do anything, tables highlighting is taken care of by {@link #visit(org.pegdown.ast.TableNode)}.
         * TODO: Should we highlight into tables ?
         *
         * @param node the {@link TableHeaderNode} to visit
         */
        public void visit(TableHeaderNode node) {
        }

        /**
         * Visit the {@link TableRowNode}.
         * <p/>
         * Does not do anything, tables highlighting is taken care of by {@link #visit(org.pegdown.ast.TableNode)}.
         * TODO: Should we highlight into tables ?
         *
         * @param node the {@link TableRowNode} to visit
         */
        public void visit(TableRowNode node) {
        }

        /**
         * Visit the {@link HtmlBlockNode}.
         * <p/>
         * Highlights the node as code.
         * TODO: Real HTML support not implemented.
         *
         * @param node the {@link HtmlBlockNode} to visit
         */
        public void visit(HtmlBlockNode node) {
            highlight(node, MarkdownTokenTypes.CODE);
        }

        /**
         * Visit the {@link InlineHtmlNode}.
         * <p/>
         * Highlights the node as code.
         * TODO: Real HTML support not implemented.
         *
         * @param node the {@link InlineHtmlNode} to visit
         */
        public void visit(InlineHtmlNode node) {
            highlight(node, MarkdownTokenTypes.CODE);
        }

        /**
         * Visit the {@link InlineHtmlNode}.
         * <p/>
         * Highlights the node as reference.
         * TODO: Not implemented.
         *
         * @param node the {@link InlineHtmlNode} to visit
         */
        public void visit(ReferenceNode node) {
        }

        /**
         * Visit the {@link InlineHtmlNode}.
         * <p/>
         * Highlights the node as reference.
         * TODO: Not implemented.
         *
         * @param node the {@link InlineHtmlNode} to visit
         */
        public void visit(AbbreviationNode node) {
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
            annotationHolder.createInfoAnnotation(new TextRange(node.getStartIndex(), node.getEndIndex()), null).setTextAttributes(SYNTAX_HIGHLIGHTER.getTokenHighlights(tokenType)[0]);
        }
    }
}
