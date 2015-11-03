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
package com.vladsch.idea.multimarkdown.psi;

import com.intellij.psi.tree.TokenSet;

public interface MultiMarkdownTokenTypeSets extends MultiMarkdownTypes {

    TokenSet ABBREVIATION_SET = TokenSet.create(ABBREVIATION);
    TokenSet ABBREVIATED_TEXT_SET = TokenSet.create(ABBREVIATED_TEXT);
    TokenSet ANCHOR_LINK_SET = TokenSet.create(ANCHOR_LINK);
    TokenSet AUTO_LINK_SET = TokenSet.create(AUTO_LINK);
    TokenSet BLOCK_QUOTE_SET = TokenSet.create(BLOCK_QUOTE);
    TokenSet BOLD_MARKER_SET = TokenSet.create(BOLD_MARKER);
    TokenSet BOLD_SET = TokenSet.create(BOLD);
    TokenSet BOLDITALIC_SET = TokenSet.create(BOLDITALIC);
    TokenSet BULLET_LIST_SET = TokenSet.create(BULLET_LIST);
    TokenSet COMMENT_SET = TokenSet.create(COMMENT);
    TokenSet CODE_SET = TokenSet.create(CODE);
    TokenSet DEFINITION_LIST_SET = TokenSet.create(DEFINITION_LIST);
    TokenSet DEFINITION_SET = TokenSet.create(DEFINITION);
    TokenSet DEFINITION_TERM_SET = TokenSet.create(DEFINITION_TERM);
    TokenSet EXPLICIT_LINK_SET = TokenSet.create(EXPLICIT_LINK, LINK_REF_TEXT_OPEN, LINK_REF_TEXT_CLOSE, LINK_REF_OPEN, LINK_REF_CLOSE, LINK_REF_TITLE_MARKER);
    TokenSet FOOTNOTE_SET = TokenSet.create(FOOTNOTE);
    TokenSet FOOTNOTE_REF_SET = TokenSet.create(FOOTNOTE_REF);
    TokenSet HEADER_LEVEL_1_SET = TokenSet.create(HEADER_LEVEL_1);
    TokenSet SETEXT_HEADER_LEVEL_1_SET = TokenSet.create(SETEXT_HEADER_LEVEL_1);
    TokenSet HEADER_LEVEL_2_SET = TokenSet.create(HEADER_LEVEL_2);
    TokenSet SETEXT_HEADER_LEVEL_2_SET = TokenSet.create(SETEXT_HEADER_LEVEL_2);
    TokenSet HEADER_LEVEL_3_SET = TokenSet.create(HEADER_LEVEL_3);
    TokenSet HEADER_LEVEL_4_SET = TokenSet.create(HEADER_LEVEL_4);
    TokenSet HEADER_LEVEL_5_SET = TokenSet.create(HEADER_LEVEL_5);
    TokenSet HEADER_LEVEL_6_SET = TokenSet.create(HEADER_LEVEL_6);
    TokenSet HRULE_SET = TokenSet.create(HRULE);
    TokenSet HTML_BLOCK_SET = TokenSet.create(HTML_BLOCK);
    TokenSet IMAGE_SET = TokenSet.create(IMAGE, IMAGE_LINK_REF_CLOSE, IMAGE_LINK_REF_OPEN, IMAGE_ALT_TEXT_OPEN, IMAGE_ALT_TEXT_CLOSE, IMAGE_LINK_REF_TITLE_MARKER);
    TokenSet INLINE_HTML_SET = TokenSet.create(INLINE_HTML);
    TokenSet ITALIC_MARKER_SET = TokenSet.create(ITALIC_MARKER);
    TokenSet ITALIC_SET = TokenSet.create(ITALIC);
    TokenSet IMAGE_LINK_REF_SET = TokenSet.create(IMAGE_LINK_REF);
    TokenSet IMAGE_LINK_REF_TITLE_SET = TokenSet.create(IMAGE_LINK_REF_TITLE);
    TokenSet IMAGE_ALT_TEXT_SET = TokenSet.create(IMAGE_ALT_TEXT);
    TokenSet LINK_REF_SET = TokenSet.create(LINK_REF);
    TokenSet LINK_REF_TEXT_SET = TokenSet.create(LINK_REF_TEXT);
    TokenSet LINK_REF_TITLE_SET = TokenSet.create(LINK_REF_TITLE);
    TokenSet LINK_REF_ANCHOR_SET = TokenSet.create(LINK_REF_ANCHOR);
    TokenSet LINK_REF_ANCHOR_MARKER_SET = TokenSet.create(LINK_REF_ANCHOR_MARKER);
    TokenSet LIST_ITEM_SET = TokenSet.create(LIST_ITEM);
    TokenSet MAIL_LINK_SET = TokenSet.create(MAIL_LINK);
    TokenSet ORDERED_LIST_SET = TokenSet.create(ORDERED_LIST);
    TokenSet QUOTE_SET = TokenSet.create(QUOTE);
    TokenSet REFERENCE_IMAGE_SET = TokenSet.create(REFERENCE_IMAGE);
    TokenSet REFERENCE_LINK_SET = TokenSet.create(REFERENCE_LINK);
    TokenSet REFERENCE_SET = TokenSet.create(REFERENCE);
    TokenSet SMARTS_SET = TokenSet.create(SMARTS);
    TokenSet SPECIAL_TEXT_SET = TokenSet.create(SPECIAL_TEXT);
    TokenSet STRIKETHROUGH_MARKER_SET = TokenSet.create(STRIKETHROUGH_MARKER);
    TokenSet STRIKETHROUGH_SET = TokenSet.create(STRIKETHROUGH);
    TokenSet STRIKETHROUGH_BOLD_SET = TokenSet.create(STRIKETHROUGH_BOLD);
    TokenSet STRIKETHROUGH_ITALIC_SET = TokenSet.create(STRIKETHROUGH_ITALIC);
    TokenSet STRIKETHROUGH_BOLDITALIC_SET = TokenSet.create(STRIKETHROUGH_BOLDITALIC);
    TokenSet TABLE_BODY_SET = TokenSet.create(TABLE_BODY);
    TokenSet TABLE_CAPTION_SET = TokenSet.create(TABLE_CAPTION);
    TokenSet TABLE_CELL_REVEN_CEVEN_SET = TokenSet.create(TABLE_CELL_REVEN_CEVEN);
    TokenSet TABLE_CELL_REVEN_CODD_SET = TokenSet.create(TABLE_CELL_REVEN_CODD);
    TokenSet TABLE_CELL_RODD_CEVEN_SET = TokenSet.create(TABLE_CELL_RODD_CEVEN);
    TokenSet TABLE_CELL_RODD_CODD_SET = TokenSet.create(TABLE_CELL_RODD_CODD);
    TokenSet TABLE_COLUMN_SET = TokenSet.create(TABLE_COLUMN);
    TokenSet TABLE_HEADER_SET = TokenSet.create(TABLE_HEADER);
    TokenSet TABLE_ROW_EVEN_SET = TokenSet.create(TABLE_ROW_EVEN);
    TokenSet TABLE_ROW_ODD_SET = TokenSet.create(TABLE_ROW_ODD);
    TokenSet TABLE_SET = TokenSet.create(TABLE);
    TokenSet TASK_ITEM_SET = TokenSet.create(TASK_ITEM);
    TokenSet TASK_DONE_ITEM_SET = TokenSet.create(TASK_DONE_ITEM);
    TokenSet TASK_ITEM_MARKER_SET = TokenSet.create(TASK_ITEM_MARKER);
    TokenSet TASK_DONE_MARKER_ITEM_SET = TokenSet.create(TASK_DONE_ITEM_MARKER);
    TokenSet TEXT_SET = TokenSet.create(TEXT);
    TokenSet VERBATIM_SET = TokenSet.create(VERBATIM);
    TokenSet WIKI_LINK_SET = TokenSet.create(WIKI_LINK_OPEN, WIKI_LINK_CLOSE);
    TokenSet WIKI_LINK_SEPARATOR_SET = TokenSet.create(WIKI_LINK_SEPARATOR);
    TokenSet WIKI_LINK_REF_SET = TokenSet.create(WIKI_LINK_REF);
    TokenSet WIKI_LINK_REF_ANCHOR_MARKER_SET = TokenSet.create(WIKI_LINK_REF_ANCHOR_MARKER);
    TokenSet WIKI_LINK_REF_ANCHOR_SET = TokenSet.create(WIKI_LINK_REF_ANCHOR);
    TokenSet WIKI_LINK_TEXT_SET = TokenSet.create(WIKI_LINK_TEXT);
}
