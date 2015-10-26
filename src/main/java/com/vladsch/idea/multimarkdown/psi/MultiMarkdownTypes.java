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
package com.vladsch.idea.multimarkdown.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.psi.impl.*;

public interface MultiMarkdownTypes {

    IElementType CRLF = new MultiMarkdownTokenType("CRLF");
    IElementType KEY = new MultiMarkdownTokenType("KEY");
    IElementType SEPARATOR = new MultiMarkdownTokenType("SEPARATOR");
    IElementType VALUE = new MultiMarkdownTokenType("VALUE");

    // no specific element, blank lines and white spaces
    IElementType NONE = new MultiMarkdownTokenType("NONE");

    IElementType ABBREVIATION = new MultiMarkdownElementType("ABBREVIATION");
    IElementType ABBREVIATED_TEXT = new MultiMarkdownElementType("ABBREVIATED_TEXT");
    IElementType ANCHOR_LINK = new MultiMarkdownTokenType("ANCHOR_LINK");
    IElementType AUTO_LINK = new MultiMarkdownTokenType("AUTO_LINK");
    IElementType BLOCK_QUOTE = new MultiMarkdownElementType("BLOCK_QUOTE");
    IElementType BOLD = new MultiMarkdownTokenType("BOLD");
    IElementType BOLD_MARKER = new MultiMarkdownTokenType("BOLD_MARKER");
    IElementType BOLDITALIC = new MultiMarkdownTokenType("BOLDITALIC");
    IElementType BULLET_LIST = new MultiMarkdownElementType("BULLET_LIST");
    IElementType CODE = new MultiMarkdownTokenType("CODE");
    IElementType DEFINITION = new MultiMarkdownElementType("DEFINITION");
    IElementType DEFINITION_LIST = new MultiMarkdownElementType("DEFINITION_LIST");
    IElementType DEFINITION_TERM = new MultiMarkdownElementType("DEFINITION_TERM");
    IElementType EXPLICIT_LINK = new MultiMarkdownElementType("EXPLICIT_LINK");
    IElementType HEADER_LEVEL_1 = new MultiMarkdownTokenType("HEADER_LEVEL_1");
    IElementType SETEXT_HEADER_LEVEL_1 = new MultiMarkdownTokenType("HEADER_LEVEL_1");
    IElementType HEADER_LEVEL_2 = new MultiMarkdownTokenType("SETEXT_HEADER_LEVEL_2");
    IElementType SETEXT_HEADER_LEVEL_2 = new MultiMarkdownTokenType("SETEXT_HEADER_LEVEL_2");
    IElementType HEADER_LEVEL_3 = new MultiMarkdownTokenType("HEADER_LEVEL_3");
    IElementType HEADER_LEVEL_4 = new MultiMarkdownTokenType("HEADER_LEVEL_4");
    IElementType HEADER_LEVEL_5 = new MultiMarkdownTokenType("HEADER_LEVEL_5");
    IElementType HEADER_LEVEL_6 = new MultiMarkdownTokenType("HEADER_LEVEL_6");
    IElementType HRULE = new MultiMarkdownTokenType("HRULE");
    IElementType HTML_BLOCK = new MultiMarkdownTokenType("HTML_BLOCK");
    IElementType IMAGE = new MultiMarkdownElementType("IMAGE");
    IElementType INLINE_HTML = new MultiMarkdownTokenType("INLINE_HTML");
    IElementType ITALIC = new MultiMarkdownTokenType("ITALIC");
    IElementType ITALIC_MARKER = new MultiMarkdownTokenType("ITALIC_MARKER");
    IElementType LIST_ITEM = new MultiMarkdownElementType("LIST_ITEM");
    IElementType MAIL_LINK = new MultiMarkdownTokenType("MAIL_LINK");
    IElementType ORDERED_LIST = new MultiMarkdownElementType("ORDERED_LIST");
    IElementType QUOTE = new MultiMarkdownTokenType("QUOTE");
    IElementType REFERENCE = new MultiMarkdownElementType("REFERENCE");
    IElementType REFERENCE_IMAGE = new MultiMarkdownElementType("REFERENCE_IMAGE");
    IElementType REFERENCE_LINK = new MultiMarkdownElementType("REFERENCE_LINK");
    IElementType SMARTS = new MultiMarkdownTokenType("SMARTS");
    IElementType SPECIAL_TEXT = new MultiMarkdownTokenType("SPECIAL_TEXT");
    IElementType STRIKETHROUGH = new MultiMarkdownTokenType("STRIKETHROUGH");
    IElementType STRIKETHROUGH_BOLD = new MultiMarkdownTokenType("STRIKETHROUGH_BOLD");
    IElementType STRIKETHROUGH_ITALIC = new MultiMarkdownTokenType("STRIKETHROUGH_ITALIC");
    IElementType STRIKETHROUGH_BOLDITALIC = new MultiMarkdownTokenType("STRIKETHROUGH_BOLDITALIC");
    IElementType STRIKETHROUGH_MARKER = new MultiMarkdownTokenType("STRIKETHROUGH_MARKER");
    IElementType TABLE = new MultiMarkdownElementType("TABLE");
    IElementType TABLE_BODY = new MultiMarkdownElementType("TABLE_BODY");
    IElementType TABLE_CAPTION = new MultiMarkdownTokenType("TABLE_CAPTION");
    IElementType TABLE_CELL_REVEN_CEVEN = new MultiMarkdownTokenType("TABLE_CELL_REVEN_CEVEN");
    IElementType TABLE_CELL_REVEN_CODD = new MultiMarkdownTokenType("TABLE_CELL_REVEN_CODD");
    IElementType TABLE_CELL_RODD_CEVEN = new MultiMarkdownTokenType("TABLE_CELL_RODD_CEVEN");
    IElementType TABLE_CELL_RODD_CODD = new MultiMarkdownTokenType("TABLE_CELL_RODD_CODD");
    IElementType TABLE_COLUMN = new MultiMarkdownElementType("TABLE_COLUMN");
    IElementType TABLE_HEADER = new MultiMarkdownElementType("TABLE_HEADER");
    IElementType TABLE_ROW_EVEN = new MultiMarkdownElementType("TABLE_ROW_EVEN");
    IElementType TABLE_ROW_ODD = new MultiMarkdownElementType("TABLE_ROW_ODD");
    IElementType TASK_ITEM = new MultiMarkdownTokenType("TASK_ITEM");
    IElementType TASK_DONE_ITEM = new MultiMarkdownTokenType("TASK_DONE_ITEM");
    IElementType TASK_ITEM_MARKER = new MultiMarkdownTokenType("TASK_ITEM_MARKER");
    IElementType TASK_DONE_ITEM_MARKER = new MultiMarkdownTokenType("TASK_DONE_ITEM_MARKER");
    IElementType TEXT = new MultiMarkdownTokenType("TEXT");
    IElementType VERBATIM = new MultiMarkdownElementType("VERBATIM");

    IElementType WIKI_LINK_OPEN = new MultiMarkdownTokenType("WIKI_LINK_OPEN");
    IElementType WIKI_LINK_SEPARATOR = new MultiMarkdownTokenType("WIKI_LINK_SEPARATOR");
    IElementType WIKI_LINK_CLOSE = new MultiMarkdownTokenType("WIKI_LINK_CLOSE");
    IElementType WIKI_LINK_REF = new MultiMarkdownTokenType("WIKI_LINK_REF");
    IElementType WIKI_LINK_REF_ANCHOR_MARKER = new MultiMarkdownTokenType("WIKI_LINK_REF_ANCHOR_MARKER");
    IElementType WIKI_LINK_REF_ANCHOR = new MultiMarkdownTokenType("WIKI_LINK_REF_ANCHOR");
    IElementType WIKI_LINK_TITLE = new MultiMarkdownTokenType("WIKI_LINK_TITLE");

    IElementType COMMENT = new MultiMarkdownElementType("COMMENT");
    IElementType WIKI_LINK = new MultiMarkdownElementType("WIKI_LINK");

    class Factory {

        public static PsiElement createElement(ASTNode node) {
            IElementType type = node.getElementType();
            if (type == COMMENT) {
                return new MultiMarkdownCommentImpl(node);
            } else if (type == WIKI_LINK) {
                return new MultiMarkdownWikiLinkImpl(node);
            } else if (type == WIKI_LINK_REF) {
                return new MultiMarkdownWikiPageRefImpl(node);
            } else if (type == WIKI_LINK_REF_ANCHOR) {
                return new MultiMarkdownWikiPageRefAnchorImpl(node);
            } else if (type == WIKI_LINK_TITLE) {
                return new MultiMarkdownWikiPageTitleImpl(node);
            }
            throw new AssertionError("Unknown element type: " + type);
        }
    }
}
