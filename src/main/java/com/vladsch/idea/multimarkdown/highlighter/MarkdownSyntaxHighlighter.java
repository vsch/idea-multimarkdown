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
package com.vladsch.idea.multimarkdown.highlighter;

import com.intellij.lexer.EmptyLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.parser.MarkdownLexer;
import com.vladsch.idea.multimarkdown.psi.MarkdownTokenTypeSets;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class MarkdownSyntaxHighlighter extends SyntaxHighlighterBase {
    protected static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

    static {
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TEXT_SET, MarkdownHighlighterColors.TEXT_ATTR_KEY);

        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.ABBREVIATION_SET, MarkdownHighlighterColors.ABBREVIATION_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.ANCHOR_LINK_SET, MarkdownHighlighterColors.ANCHOR_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.AUTO_LINK_SET, MarkdownHighlighterColors.AUTO_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.BLOCK_QUOTE_SET, MarkdownHighlighterColors.BLOCK_QUOTE_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.BOLD_MARKER_SET, MarkdownHighlighterColors.BOLD_MARKER_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.BOLD_SET, MarkdownHighlighterColors.BOLD_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.BOLDITALIC_SET, MarkdownHighlighterColors.BOLDITALIC_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.BULLET_LIST_SET, MarkdownHighlighterColors.BULLET_LIST_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.COMMENT_SET, MarkdownHighlighterColors.COMMENT_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.CODE_SET, MarkdownHighlighterColors.CODE_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.DEFINITION_LIST_SET, MarkdownHighlighterColors.DEFINITION_LIST_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.DEFINITION_SET, MarkdownHighlighterColors.DEFINITION_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.DEFINITION_TERM_SET, MarkdownHighlighterColors.DEFINITION_TERM_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.EXPLICIT_LINK_SET, MarkdownHighlighterColors.EXPLICIT_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.HEADER_LEVEL_1_SET, MarkdownHighlighterColors.HEADER_LEVEL_1_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.SETEXT_HEADER_LEVEL_1_SET, MarkdownHighlighterColors.SETEXT_HEADER_LEVEL_1_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.HEADER_LEVEL_2_SET, MarkdownHighlighterColors.HEADER_LEVEL_2_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.SETEXT_HEADER_LEVEL_2_SET, MarkdownHighlighterColors.SETEXT_HEADER_LEVEL_2_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.HEADER_LEVEL_3_SET, MarkdownHighlighterColors.HEADER_LEVEL_3_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.HEADER_LEVEL_4_SET, MarkdownHighlighterColors.HEADER_LEVEL_4_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.HEADER_LEVEL_5_SET, MarkdownHighlighterColors.HEADER_LEVEL_5_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.HEADER_LEVEL_6_SET, MarkdownHighlighterColors.HEADER_LEVEL_6_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.HRULE_SET, MarkdownHighlighterColors.HRULE_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.HTML_BLOCK_SET, MarkdownHighlighterColors.HTML_BLOCK_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.IMAGE_SET, MarkdownHighlighterColors.IMAGE_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.INLINE_HTML_SET, MarkdownHighlighterColors.INLINE_HTML_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.ITALIC_MARKER_SET, MarkdownHighlighterColors.ITALIC_MARKER_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.ITALIC_SET, MarkdownHighlighterColors.ITALIC_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.LIST_ITEM_SET, MarkdownHighlighterColors.LIST_ITEM_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.MAIL_LINK_SET, MarkdownHighlighterColors.MAIL_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.ORDERED_LIST_SET, MarkdownHighlighterColors.ORDERED_LIST_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.QUOTE_SET, MarkdownHighlighterColors.QUOTE_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.REFERENCE_IMAGE_SET, MarkdownHighlighterColors.REFERENCE_IMAGE_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.REFERENCE_LINK_SET, MarkdownHighlighterColors.REFERENCE_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.REFERENCE_SET, MarkdownHighlighterColors.REFERENCE_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.SMARTS_SET, MarkdownHighlighterColors.SMARTS_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.SPECIAL_TEXT_SET, MarkdownHighlighterColors.SPECIAL_TEXT_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.STRIKETHROUGH_MARKER_SET, MarkdownHighlighterColors.STRIKETHROUGH_MARKER_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.STRIKETHROUGH_SET, MarkdownHighlighterColors.STRIKETHROUGH_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.STRIKETHROUGH_BOLD_SET, MarkdownHighlighterColors.STRIKETHROUGH_BOLD_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.STRIKETHROUGH_ITALIC_SET, MarkdownHighlighterColors.STRIKETHROUGH_ITALIC_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.STRIKETHROUGH_BOLDITALIC_SET, MarkdownHighlighterColors.STRIKETHROUGH_BOLDITALIC_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_BODY_SET, MarkdownHighlighterColors.TABLE_BODY_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_CAPTION_SET, MarkdownHighlighterColors.TABLE_CAPTION_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_CELL_REVEN_CEVEN_SET, MarkdownHighlighterColors.TABLE_CELL_REVEN_CEVEN_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_CELL_REVEN_CODD_SET, MarkdownHighlighterColors.TABLE_CELL_REVEN_CODD_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_CELL_RODD_CEVEN_SET, MarkdownHighlighterColors.TABLE_CELL_RODD_CEVEN_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_CELL_RODD_CODD_SET, MarkdownHighlighterColors.TABLE_CELL_RODD_CODD_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_COLUMN_SET, MarkdownHighlighterColors.TABLE_COLUMN_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_HEADER_SET, MarkdownHighlighterColors.TABLE_HEADER_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_ROW_EVEN_SET, MarkdownHighlighterColors.TABLE_ROW_EVEN_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_ROW_ODD_SET, MarkdownHighlighterColors.TABLE_ROW_ODD_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.TABLE_SET, MarkdownHighlighterColors.TABLE_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.VERBATIM_SET, MarkdownHighlighterColors.VERBATIM_ATTR_KEY);
        fillMap(ATTRIBUTES, MarkdownTokenTypeSets.WIKI_LINK_SET, MarkdownHighlighterColors.WIKI_LINK_ATTR_KEY);
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new MarkdownLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(ATTRIBUTES.get(tokenType));
    }
}
