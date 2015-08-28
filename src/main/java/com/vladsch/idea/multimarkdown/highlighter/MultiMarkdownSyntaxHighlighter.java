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

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.parser.MultiMarkdownLexer;
import org.jetbrains.annotations.NotNull;
import org.pegdown.Extensions;
import java.util.HashMap;
import java.util.Map;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTokenTypeSets.*;
import static com.vladsch.idea.multimarkdown.highlighter.MultiMarkdownHighlighterColors.*;

public class MultiMarkdownSyntaxHighlighter extends SyntaxHighlighterBase {
    protected static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

    protected final boolean forSampleDoc;

    static {
        fillMap(ATTRIBUTES, TEXT_SET, TEXT_ATTR_KEY);
        fillMap(ATTRIBUTES, ABBREVIATION_SET, ABBREVIATION_ATTR_KEY);
        fillMap(ATTRIBUTES, ANCHOR_LINK_SET, ANCHOR_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, AUTO_LINK_SET, AUTO_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, BLOCK_QUOTE_SET, BLOCK_QUOTE_ATTR_KEY);
        fillMap(ATTRIBUTES, BOLD_MARKER_SET, BOLD_MARKER_ATTR_KEY);
        fillMap(ATTRIBUTES, BOLD_SET, BOLD_ATTR_KEY);
        fillMap(ATTRIBUTES, BOLDITALIC_SET, BOLDITALIC_ATTR_KEY);
        fillMap(ATTRIBUTES, BULLET_LIST_SET, BULLET_LIST_ATTR_KEY);
        fillMap(ATTRIBUTES, COMMENT_SET, COMMENT_ATTR_KEY);
        fillMap(ATTRIBUTES, CODE_SET, CODE_ATTR_KEY);
        fillMap(ATTRIBUTES, DEFINITION_LIST_SET, DEFINITION_LIST_ATTR_KEY);
        fillMap(ATTRIBUTES, DEFINITION_SET, DEFINITION_ATTR_KEY);
        fillMap(ATTRIBUTES, DEFINITION_TERM_SET, DEFINITION_TERM_ATTR_KEY);
        fillMap(ATTRIBUTES, EXPLICIT_LINK_SET, EXPLICIT_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, HEADER_LEVEL_1_SET, HEADER_LEVEL_1_ATTR_KEY);
        fillMap(ATTRIBUTES, SETEXT_HEADER_LEVEL_1_SET, SETEXT_HEADER_LEVEL_1_ATTR_KEY);
        fillMap(ATTRIBUTES, HEADER_LEVEL_2_SET, HEADER_LEVEL_2_ATTR_KEY);
        fillMap(ATTRIBUTES, SETEXT_HEADER_LEVEL_2_SET, SETEXT_HEADER_LEVEL_2_ATTR_KEY);
        fillMap(ATTRIBUTES, HEADER_LEVEL_3_SET, HEADER_LEVEL_3_ATTR_KEY);
        fillMap(ATTRIBUTES, HEADER_LEVEL_4_SET, HEADER_LEVEL_4_ATTR_KEY);
        fillMap(ATTRIBUTES, HEADER_LEVEL_5_SET, HEADER_LEVEL_5_ATTR_KEY);
        fillMap(ATTRIBUTES, HEADER_LEVEL_6_SET, HEADER_LEVEL_6_ATTR_KEY);
        fillMap(ATTRIBUTES, HRULE_SET, HRULE_ATTR_KEY);
        fillMap(ATTRIBUTES, HTML_BLOCK_SET, HTML_BLOCK_ATTR_KEY);
        fillMap(ATTRIBUTES, IMAGE_SET, IMAGE_ATTR_KEY);
        fillMap(ATTRIBUTES, INLINE_HTML_SET, INLINE_HTML_ATTR_KEY);
        fillMap(ATTRIBUTES, ITALIC_MARKER_SET, ITALIC_MARKER_ATTR_KEY);
        fillMap(ATTRIBUTES, ITALIC_SET, ITALIC_ATTR_KEY);
        fillMap(ATTRIBUTES, LIST_ITEM_SET, LIST_ITEM_ATTR_KEY);
        fillMap(ATTRIBUTES, MAIL_LINK_SET, MAIL_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, ORDERED_LIST_SET, ORDERED_LIST_ATTR_KEY);
        fillMap(ATTRIBUTES, QUOTE_SET, QUOTE_ATTR_KEY);
        fillMap(ATTRIBUTES, REFERENCE_IMAGE_SET, REFERENCE_IMAGE_ATTR_KEY);
        fillMap(ATTRIBUTES, REFERENCE_LINK_SET, REFERENCE_LINK_ATTR_KEY);
        fillMap(ATTRIBUTES, REFERENCE_SET, REFERENCE_ATTR_KEY);
        fillMap(ATTRIBUTES, SMARTS_SET, SMARTS_ATTR_KEY);
        fillMap(ATTRIBUTES, SPECIAL_TEXT_SET, SPECIAL_TEXT_ATTR_KEY);
        fillMap(ATTRIBUTES, STRIKETHROUGH_MARKER_SET, STRIKETHROUGH_MARKER_ATTR_KEY);
        fillMap(ATTRIBUTES, STRIKETHROUGH_SET, STRIKETHROUGH_ATTR_KEY);
        fillMap(ATTRIBUTES, STRIKETHROUGH_BOLD_SET, STRIKETHROUGH_BOLD_ATTR_KEY);
        fillMap(ATTRIBUTES, STRIKETHROUGH_ITALIC_SET, STRIKETHROUGH_ITALIC_ATTR_KEY);
        fillMap(ATTRIBUTES, STRIKETHROUGH_BOLDITALIC_SET, STRIKETHROUGH_BOLDITALIC_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_BODY_SET, TABLE_BODY_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_CAPTION_SET, TABLE_CAPTION_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_CELL_REVEN_CEVEN_SET, TABLE_CELL_REVEN_CEVEN_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_CELL_REVEN_CODD_SET, TABLE_CELL_REVEN_CODD_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_CELL_RODD_CEVEN_SET, TABLE_CELL_RODD_CEVEN_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_CELL_RODD_CODD_SET, TABLE_CELL_RODD_CODD_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_COLUMN_SET, TABLE_COLUMN_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_HEADER_SET, TABLE_HEADER_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_ROW_EVEN_SET, TABLE_ROW_EVEN_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_ROW_ODD_SET, TABLE_ROW_ODD_ATTR_KEY);
        fillMap(ATTRIBUTES, TABLE_SET, TABLE_ATTR_KEY);
        fillMap(ATTRIBUTES, TASK_ITEM_SET, TASK_ITEM_ATTR_KEY);
        fillMap(ATTRIBUTES, TASK_DONE_ITEM_SET, TASK_DONE_ITEM_ATTR_KEY);
        fillMap(ATTRIBUTES, TASK_ITEM_MARKER_SET, TASK_ITEM_MARKER_ATTR_KEY);
        fillMap(ATTRIBUTES, TASK_DONE_MARKER_ITEM_SET, TASK_DONE_ITEM_MARKER_ATTR_KEY);
        fillMap(ATTRIBUTES, VERBATIM_SET, VERBATIM_ATTR_KEY);
        fillMap(ATTRIBUTES, WIKI_LINK_SET, WIKI_LINK_ATTR_KEY);
    }

    public MultiMarkdownSyntaxHighlighter(boolean forSampleDoc) {
        super();
        this.forSampleDoc = forSampleDoc;
    }

    public MultiMarkdownSyntaxHighlighter() {
        super();
        this.forSampleDoc = false;
    }


    @NotNull
    public Lexer getHighlightingLexer() {
        return forSampleDoc ? new MultiMarkdownLexer(Extensions.ALL_WITH_OPTIONALS) : new MultiMarkdownLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(ATTRIBUTES.get(tokenType));
    }
}
