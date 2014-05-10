/*
 * Copyright (c) 2011-2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
package net.nicoulaj.idea.markdown.lang;

import com.intellij.psi.tree.TokenSet;

/**
 * Token type sets for the Markdown language.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public interface MarkdownTokenTypeSets extends MarkdownTokenTypes {

    /** Plain text token type set. */
    TokenSet TEXT_SET = TokenSet.create(TEXT);

    /** Bold text token type set. */
    TokenSet BOLD_SET = TokenSet.create(BOLD);

    /** Italic token type set. */
    TokenSet ITALIC_SET = TokenSet.create(ITALIC);

    /** Header of level 1 token type set. */
    TokenSet HEADER_LEVEL_1_SET = TokenSet.create(HEADER_LEVEL_1);

    /** Header of level 2 token type set. */
    TokenSet HEADER_LEVEL_2_SET = TokenSet.create(HEADER_LEVEL_2);

    /** Header of level 3 token type set. */
    TokenSet HEADER_LEVEL_3_SET = TokenSet.create(HEADER_LEVEL_3);

    /** Header of level 4 token type set. */
    TokenSet HEADER_LEVEL_4_SET = TokenSet.create(HEADER_LEVEL_4);

    /** Header of level 5 token type set. */
    TokenSet HEADER_LEVEL_5_SET = TokenSet.create(HEADER_LEVEL_5);

    /** Header of level 6 token type set. */
    TokenSet HEADER_LEVEL_6_SET = TokenSet.create(HEADER_LEVEL_6);

    /** Code token type set. */
    TokenSet CODE_SET = TokenSet.create(CODE);

    /** Quote token type set. */
    TokenSet QUOTE_SET = TokenSet.create(QUOTE);

    /** Table token type set. */
    TokenSet TABLE_SET = TokenSet.create(TABLE);

    /** HRule token type set. */
    TokenSet HRULE_SET = TokenSet.create(HRULE);

    /** Special text token type set. */
    TokenSet SPECIAL_TEXT_SET = TokenSet.create(SPECIAL_TEXT);

    /** Strikethrough token type set. */
    TokenSet STRIKETHROUGH_SET = TokenSet.create(STRIKETHROUGH);

    /** Link token type set. */
    TokenSet EXPLICIT_LINK_SET = TokenSet.create(EXPLICIT_LINK);

    /** Image token type set. */
    TokenSet IMAGE_SET = TokenSet.create(IMAGE);

    /** Reference image token type set. */
    TokenSet REFERENCE_IMAGE_SET = TokenSet.create(REFERENCE_IMAGE);

    /** Reference link token type set. */
    TokenSet REFERENCE_LINK_SET = TokenSet.create(REFERENCE_LINK);

    /** Wiki link token type set. */
    TokenSet WIKI_LINK_SET = TokenSet.create(WIKI_LINK);

    /** Auto link token type set. */
    TokenSet AUTO_LINK_SET = TokenSet.create(AUTO_LINK);

    /** Mail link token type set. */
    TokenSet MAIL_LINK_SET = TokenSet.create(MAIL_LINK);

    /** Verbatim token type set. */
    TokenSet VERBATIM_SET = TokenSet.create(VERBATIM);

    /** Block quote token type set. */
    TokenSet BLOCK_QUOTE_SET = TokenSet.create(BLOCK_QUOTE);

    /** Bullet list token type set. */
    TokenSet BULLET_LIST_SET = TokenSet.create(BULLET_LIST);

    /** Ordered list token type set. */
    TokenSet ORDERED_LIST_SET = TokenSet.create(ORDERED_LIST);

    /** List item token type set. */
    TokenSet LIST_ITEM_SET = TokenSet.create(LIST_ITEM);

    /** Definition list token type set. */
    TokenSet DEFINITION_LIST_SET = TokenSet.create(DEFINITION_LIST);

    /** Definition token type set. */
    TokenSet DEFINITION_SET = TokenSet.create(DEFINITION);

    /** Definition term token type set. */
    TokenSet DEFINITION_TERM_SET = TokenSet.create(DEFINITION_TERM);

    /** Table body token type set. */
    TokenSet TABLE_BODY_SET = TokenSet.create(TABLE_BODY);

    /** Table cell token type set. */
    TokenSet TABLE_CELL_SET = TokenSet.create(TABLE_CELL);

    /** Table column token type set. */
    TokenSet TABLE_COLUMN_SET = TokenSet.create(TABLE_COLUMN);

    /** Table header token type set. */
    TokenSet TABLE_HEADER_SET = TokenSet.create(TABLE_HEADER);

    /** Table row token type set. */
    TokenSet TABLE_ROW_SET = TokenSet.create(TABLE_ROW);

    /** Table caption token type set. */
    TokenSet TABLE_CAPTION_SET = TokenSet.create(TABLE_CAPTION);

    /** HTML block token type set. */
    TokenSet HTML_BLOCK_SET = TokenSet.create(HTML_BLOCK);

    /** Inline HTML token type set. */
    TokenSet INLINE_HTML_SET = TokenSet.create(INLINE_HTML);

    /** Reference token type set. */
    TokenSet REFERENCE_SET = TokenSet.create(REFERENCE);

    /** Abbreviation token type set. */
    TokenSet ABBREVIATION_SET = TokenSet.create(ABBREVIATION);
}
