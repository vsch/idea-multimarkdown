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

import com.intellij.openapi.editor.colors.TextAttributesKey;
import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class MarkdownHighlighterColors {
    // we also create defaults so that the user can reset his scheme
    public static final TextAttributesKey DEFAULT_ABBREVIATION_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.ABBREVIATION");
    public static final TextAttributesKey DEFAULT_ANCHOR_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.ANCHOR_LINK");
    public static final TextAttributesKey DEFAULT_AUTO_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.AUTO_LINK");
    public static final TextAttributesKey DEFAULT_BLOCK_QUOTE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.BLOCK_QUOTE");
    public static final TextAttributesKey DEFAULT_BOLD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.BOLD");
    public static final TextAttributesKey DEFAULT_BOLD_MARKER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.BOLD_MARKER");
    public static final TextAttributesKey DEFAULT_BOLDITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.BOLDITALIC");
    public static final TextAttributesKey DEFAULT_BULLET_LIST_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.BULLET_LIST");
    public static final TextAttributesKey DEFAULT_COMMENT_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.COMMENT", BLOCK_COMMENT);
    public static final TextAttributesKey DEFAULT_CODE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.CODE");
    public static final TextAttributesKey DEFAULT_DEFINITION_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.DEFINITION");
    public static final TextAttributesKey DEFAULT_DEFINITION_LIST_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.DEFINITION_LIST");
    public static final TextAttributesKey DEFAULT_DEFINITION_TERM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.DEFINITION_TERM");
    public static final TextAttributesKey DEFAULT_EXPLICIT_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.EXPLICIT_LINK");
    public static final TextAttributesKey DEFAULT_HEADER_LEVEL_1_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.HEADER_LEVEL_1");
    public static final TextAttributesKey DEFAULT_HEADER_LEVEL_2_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.HEADER_LEVEL_2");
    public static final TextAttributesKey DEFAULT_HEADER_LEVEL_3_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.HEADER_LEVEL_3");
    public static final TextAttributesKey DEFAULT_HEADER_LEVEL_4_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.HEADER_LEVEL_4");
    public static final TextAttributesKey DEFAULT_HEADER_LEVEL_5_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.HEADER_LEVEL_5");
    public static final TextAttributesKey DEFAULT_HEADER_LEVEL_6_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.HEADER_LEVEL_6");
    public static final TextAttributesKey DEFAULT_HRULE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.HRULE");
    public static final TextAttributesKey DEFAULT_HTML_BLOCK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.HTML_BLOCK");
    public static final TextAttributesKey DEFAULT_IMAGE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.IMAGE");
    public static final TextAttributesKey DEFAULT_INLINE_HTML_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.INLINE_HTML");
    public static final TextAttributesKey DEFAULT_ITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.ITALIC");
    public static final TextAttributesKey DEFAULT_ITALIC_MARKER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.ITALIC_MARKER");
    public static final TextAttributesKey DEFAULT_LIST_ITEM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.LIST_ITEM");
    public static final TextAttributesKey DEFAULT_MAIL_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.MAIL_LINK");
    public static final TextAttributesKey DEFAULT_ORDERED_LIST_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.ORDERED_LIST");
    public static final TextAttributesKey DEFAULT_QUOTE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.QUOTE");
    public static final TextAttributesKey DEFAULT_REFERENCE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.REFERENCE");
    public static final TextAttributesKey DEFAULT_REFERENCE_IMAGE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.REFERENCE_IMAGE");
    public static final TextAttributesKey DEFAULT_REFERENCE_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.REFERENCE_LINK");
    public static final TextAttributesKey DEFAULT_SMARTS_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.SMARTS");
    public static final TextAttributesKey DEFAULT_SPECIAL_TEXT_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.SPECIAL_TEXT");
    public static final TextAttributesKey DEFAULT_STRIKETHROUGH_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.STRIKETHROUGH");
    public static final TextAttributesKey DEFAULT_STRIKETHROUGH_BOLD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.STRIKETHROUGH_BOLD");
    public static final TextAttributesKey DEFAULT_STRIKETHROUGH_ITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.STRIKETHROUGH_ITALIC");
    public static final TextAttributesKey DEFAULT_STRIKETHROUGH_BOLDITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.STRIKETHROUGH_BOLDITALIC");
    public static final TextAttributesKey DEFAULT_STRIKETHROUGH_MARKER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.STRIKETHROUGH_MARKER");
    public static final TextAttributesKey DEFAULT_TABLE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE");
    public static final TextAttributesKey DEFAULT_TABLE_BODY_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_BODY");
    public static final TextAttributesKey DEFAULT_TABLE_CAPTION_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_CAPTION");
    public static final TextAttributesKey DEFAULT_TABLE_CELL_REVEN_CEVEN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_CELL_REVEN_CEVEN");
    public static final TextAttributesKey DEFAULT_TABLE_CELL_REVEN_CODD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_CELL_REVEN_CODD");
    public static final TextAttributesKey DEFAULT_TABLE_CELL_RODD_CEVEN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_CELL_RODD_CEVEN");
    public static final TextAttributesKey DEFAULT_TABLE_CELL_RODD_CODD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_CELL_RODD_CODD");
    public static final TextAttributesKey DEFAULT_TABLE_COLUMN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_COLUMN");
    public static final TextAttributesKey DEFAULT_TABLE_HEADER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_HEADER");
    public static final TextAttributesKey DEFAULT_TABLE_ROW_EVEN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_ROW_EVEN");
    public static final TextAttributesKey DEFAULT_TABLE_ROW_ODD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TABLE_ROW_ODD");
    public static final TextAttributesKey DEFAULT_TASK_ITEM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TASK_ITEM");
    public static final TextAttributesKey DEFAULT_TASK_DONE_ITEM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TASK_DONE_ITEM");
    public static final TextAttributesKey DEFAULT_TEXT_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.TEXT");
    public static final TextAttributesKey DEFAULT_VERBATIM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.VERBATIM");
    public static final TextAttributesKey DEFAULT_WIKI_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN_DEFAULT.WIKI_LINK");

    public static final TextAttributesKey ABBREVIATION_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ABBREVIATION", DEFAULT_ABBREVIATION_ATTR_KEY);
    public static final TextAttributesKey ANCHOR_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ANCHOR_LINK", DEFAULT_ANCHOR_LINK_ATTR_KEY);
    public static final TextAttributesKey AUTO_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.AUTO_LINK", DEFAULT_AUTO_LINK_ATTR_KEY);
    public static final TextAttributesKey BLOCK_QUOTE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BLOCK_QUOTE", DEFAULT_BLOCK_QUOTE_ATTR_KEY);
    public static final TextAttributesKey BOLD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BOLD", DEFAULT_BOLD_ATTR_KEY);
    public static final TextAttributesKey BOLD_MARKER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BOLD_MARKER", DEFAULT_BOLD_MARKER_ATTR_KEY);
    public static final TextAttributesKey BOLDITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BOLDITALIC", DEFAULT_BOLDITALIC_ATTR_KEY);
    public static final TextAttributesKey BULLET_LIST_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BULLET_LIST", DEFAULT_BULLET_LIST_ATTR_KEY);
    public static final TextAttributesKey COMMENT_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.COMMENT", DEFAULT_COMMENT_ATTR_KEY);
    public static final TextAttributesKey CODE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.CODE", DEFAULT_CODE_ATTR_KEY);
    public static final TextAttributesKey DEFINITION_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.DEFINITION", DEFAULT_DEFINITION_ATTR_KEY);
    public static final TextAttributesKey DEFINITION_LIST_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.DEFINITION_LIST", DEFAULT_DEFINITION_LIST_ATTR_KEY);
    public static final TextAttributesKey DEFINITION_TERM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.DEFINITION_TERM", DEFAULT_DEFINITION_TERM_ATTR_KEY);
    public static final TextAttributesKey EXPLICIT_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.EXPLICIT_LINK", DEFAULT_EXPLICIT_LINK_ATTR_KEY);
    public static final TextAttributesKey HEADER_LEVEL_1_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_1", DEFAULT_HEADER_LEVEL_1_ATTR_KEY);
    public static final TextAttributesKey SETEXT_HEADER_LEVEL_1_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.SETEXT_HEADER_LEVEL_1", HEADER_LEVEL_1_ATTR_KEY);
    public static final TextAttributesKey HEADER_LEVEL_2_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_2", DEFAULT_HEADER_LEVEL_2_ATTR_KEY);
    public static final TextAttributesKey SETEXT_HEADER_LEVEL_2_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.SETEXT_HEADER_LEVEL_2", HEADER_LEVEL_2_ATTR_KEY);
    public static final TextAttributesKey HEADER_LEVEL_3_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_3", DEFAULT_HEADER_LEVEL_3_ATTR_KEY);
    public static final TextAttributesKey HEADER_LEVEL_4_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_4", DEFAULT_HEADER_LEVEL_4_ATTR_KEY);
    public static final TextAttributesKey HEADER_LEVEL_5_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_5", DEFAULT_HEADER_LEVEL_5_ATTR_KEY);
    public static final TextAttributesKey HEADER_LEVEL_6_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_6", DEFAULT_HEADER_LEVEL_6_ATTR_KEY);
    public static final TextAttributesKey HRULE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HRULE", DEFAULT_HRULE_ATTR_KEY);
    public static final TextAttributesKey HTML_BLOCK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HTML_BLOCK", DEFAULT_HTML_BLOCK_ATTR_KEY);
    public static final TextAttributesKey IMAGE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.IMAGE", DEFAULT_IMAGE_ATTR_KEY);
    public static final TextAttributesKey INLINE_HTML_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.INLINE_HTML", DEFAULT_INLINE_HTML_ATTR_KEY);
    public static final TextAttributesKey ITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ITALIC", DEFAULT_ITALIC_ATTR_KEY);
    public static final TextAttributesKey ITALIC_MARKER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ITALIC_MARKER", DEFAULT_ITALIC_MARKER_ATTR_KEY);
    public static final TextAttributesKey LIST_ITEM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.LIST_ITEM", DEFAULT_LIST_ITEM_ATTR_KEY);
    public static final TextAttributesKey MAIL_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.MAIL_LINK", DEFAULT_MAIL_LINK_ATTR_KEY);
    public static final TextAttributesKey ORDERED_LIST_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ORDERED_LIST", DEFAULT_ORDERED_LIST_ATTR_KEY);
    public static final TextAttributesKey QUOTE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.QUOTE", DEFAULT_QUOTE_ATTR_KEY);
    public static final TextAttributesKey REFERENCE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.REFERENCE", DEFAULT_REFERENCE_ATTR_KEY);
    public static final TextAttributesKey REFERENCE_IMAGE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.REFERENCE_IMAGE", DEFAULT_REFERENCE_IMAGE_ATTR_KEY);
    public static final TextAttributesKey REFERENCE_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.REFERENCE_LINK", DEFAULT_REFERENCE_LINK_ATTR_KEY);
    public static final TextAttributesKey SMARTS_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.SMARTS", DEFAULT_SMARTS_ATTR_KEY);
    public static final TextAttributesKey SPECIAL_TEXT_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.SPECIAL_TEXT", DEFAULT_SPECIAL_TEXT_ATTR_KEY);
    public static final TextAttributesKey STRIKETHROUGH_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.STRIKETHROUGH", DEFAULT_STRIKETHROUGH_ATTR_KEY);
    public static final TextAttributesKey STRIKETHROUGH_BOLD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.STRIKETHROUGH_BOLD", DEFAULT_STRIKETHROUGH_BOLD_ATTR_KEY);
    public static final TextAttributesKey STRIKETHROUGH_ITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.STRIKETHROUGH_ITALIC", DEFAULT_STRIKETHROUGH_ITALIC_ATTR_KEY);
    public static final TextAttributesKey STRIKETHROUGH_BOLDITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.STRIKETHROUGH_BOLDITALIC", DEFAULT_STRIKETHROUGH_BOLDITALIC_ATTR_KEY);
    public static final TextAttributesKey STRIKETHROUGH_MARKER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.STRIKETHROUGH_MARKER", DEFAULT_STRIKETHROUGH_MARKER_ATTR_KEY);
    public static final TextAttributesKey TABLE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE", DEFAULT_TABLE_ATTR_KEY);
    public static final TextAttributesKey TABLE_BODY_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_BODY", DEFAULT_TABLE_BODY_ATTR_KEY);
    public static final TextAttributesKey TABLE_CAPTION_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CAPTION", DEFAULT_TABLE_CAPTION_ATTR_KEY);
    public static final TextAttributesKey TABLE_CELL_REVEN_CEVEN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CELL_REVEN_CEVEN", DEFAULT_TABLE_CELL_REVEN_CEVEN_ATTR_KEY);
    public static final TextAttributesKey TABLE_CELL_REVEN_CODD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CELL_REVEN_CODD", DEFAULT_TABLE_CELL_REVEN_CODD_ATTR_KEY);
    public static final TextAttributesKey TABLE_CELL_RODD_CEVEN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CELL_RODD_CEVEN", DEFAULT_TABLE_CELL_RODD_CEVEN_ATTR_KEY);
    public static final TextAttributesKey TABLE_CELL_RODD_CODD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CELL_RODD_CODD", DEFAULT_TABLE_CELL_RODD_CODD_ATTR_KEY);
    public static final TextAttributesKey TABLE_COLUMN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_COLUMN", DEFAULT_TABLE_COLUMN_ATTR_KEY);
    public static final TextAttributesKey TABLE_HEADER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_HEADER", DEFAULT_TABLE_HEADER_ATTR_KEY);
    public static final TextAttributesKey TABLE_ROW_EVEN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_ROW_EVEN", DEFAULT_TABLE_ROW_EVEN_ATTR_KEY);
    public static final TextAttributesKey TABLE_ROW_ODD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_ROW_ODD", DEFAULT_TABLE_ROW_ODD_ATTR_KEY);
    public static final TextAttributesKey TASK_ITEM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TEXT", DEFAULT_TASK_ITEM_ATTR_KEY);
    public static final TextAttributesKey TASK_DONE_ITEM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TEXT", DEFAULT_TASK_DONE_ITEM_ATTR_KEY);
    public static final TextAttributesKey TEXT_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TEXT", DEFAULT_TEXT_ATTR_KEY);
    public static final TextAttributesKey VERBATIM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.VERBATIM", DEFAULT_VERBATIM_ATTR_KEY);
    public static final TextAttributesKey WIKI_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.WIKI_LINK", DEFAULT_WIKI_LINK_ATTR_KEY);

}
