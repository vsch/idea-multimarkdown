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
package com.vladsch.idea.multimarkdown.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.pegdown.LinkRenderer;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.*;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

import java.util.List;
import java.util.Map;

public class MultiMarkdownToHtmlSerializer extends ToHtmlSerializer {
    final public static int NO_WIKI_LINKS = 1;

    protected final Project project;
    protected final Document document;
    protected int flags = 0;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public boolean isSet(int flags) {
        return (this.flags & flags) != 0;
    }

    public void setFlag(int flags) {
        this.flags |= flags;
    }

    public void clearFlag(int flags) {
        this.flags &= ~flags;
    }

    public MultiMarkdownToHtmlSerializer(Project project, Document document, LinkRenderer linkRenderer) {
        super(linkRenderer);
        this.project = project;
        this.document = document;
    }

    public MultiMarkdownToHtmlSerializer(LinkRenderer linkRenderer) {
        super(linkRenderer);
        this.project = null;
        this.document = null;
    }

    public MultiMarkdownToHtmlSerializer(LinkRenderer linkRenderer, List<ToHtmlSerializerPlugin> plugins) {
        super(linkRenderer, plugins);
        this.project = null;
        this.document = null;
    }

    public MultiMarkdownToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers) {
        super(linkRenderer, verbatimSerializers);
        project = null;
        document = null;
    }

    public MultiMarkdownToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers, List<ToHtmlSerializerPlugin> plugins) {
        super(linkRenderer, verbatimSerializers, plugins);
        project = null;
        document = null;
    }

    public void visit(HeaderNode node) {
        printBreakBeforeTag(node, "h" + node.getLevel());
    }

    @Override
    public void visit(AnchorLinkNode node) {
        printAnchorLink(linkRenderer.render(node));
    }

    public void visit(ListItemNode node) {
        if (node instanceof TaskListNode) {
            // vsch: #185 handle GitHub style task list items, these are a bit messy because the <input> checkbox needs to be
            // included inside the optional <p></p> first grand-child of the list item, first child is always RootNode
            // because the list item text is recursively parsed.
            Node firstChild = node.getChildren().size() == 0 ? null : (node.getChildren().get(0).getChildren().size() == 0 ? null : node.getChildren().get(0).getChildren().get(0));
            if (firstChild != null) {
                boolean firstIsPara = firstChild instanceof ParaNode;
                int indent = node.getChildren().size() > 1 ? 2 : 0;
                boolean startWasNewLine = printer.endsWithNewLine();

                printer.println().print("<li class=\"task-list-item\">").indent(indent);
                if (firstIsPara) {
                    printer.println().print("<p>");
                    printer.print("<span class=\"taskitem\">" + (((TaskListNode) node).isDone() ? "X" : "O") + "</span>");
                    visitChildren((SuperNode) firstChild);

                    // render the other children, the p tag is taken care of here
                    visitChildrenSkipFirst(node);
                    printer.print("</p>");
                } else {
                    printer.print("<span class=\"taskitem\">" + (((TaskListNode) node).isDone() ? "X" : "O") + "</span>");
                    visitChildren(node);
                }
                printer.indent(-indent).printchkln(indent != 0).print("</li>")
                        .printchkln(startWasNewLine);
            }
        } else {
            printConditionallyIndentedTag(node, "li");
        }
    }

    @Override
    protected void visitChildren(SuperNode node) {
        visitChildrenSkipFirst(node, 0);
    }

    @Override
    protected void visitChildrenSkipFirst(SuperNode node) {
        visitChildrenSkipFirst(node, 1);
    }

    public void visit(WikiLinkNode node) {
        if (isSet(NO_WIKI_LINKS)) {
            printer.printEncoded("[[" + node.getText() + "]]");
        } else {
            printLink(linkRenderer.render(node));
        }
    }

    protected void visitChildrenSkipFirst(SuperNode node, int skipFirst) {
        // here we combine multiple segments of TextNode and SpecialText into a single TextNode
        int startIndex = 0, endIndex = 0;
        String combinedText = null;
        Node lastTextNode = null;

        for (Node child : node.getChildren()) {
            if (skipFirst > 0) {
                skipFirst--;
                continue;
            }

            boolean processed = false;
            // TODO: we don't really need to do this here, it is needed for the parser but not for HTML Serialization
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

    protected void printAnchorLink(LinkRenderer.Rendering rendering) {
        printer.print('<').print('a');
        printAttribute("href", rendering.href);
        for (LinkRenderer.Attribute attr : rendering.attributes) {
            printAttribute(attr.name, attr.value);
        }
        printer.print('>').print("<span class=\"octicon octicon-link\"></span>").print(rendering.text).print("</a>");
    }
}
