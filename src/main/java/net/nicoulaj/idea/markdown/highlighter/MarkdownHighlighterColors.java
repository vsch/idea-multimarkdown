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
package net.nicoulaj.idea.markdown.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * The default styles for each of token defined for Markdown.
 * <p/>
 * Anyone who has better taste than me, feel free to contribute :)
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownHighlighterColors {

    /** Default style for special text. */
    public static final TextAttributesKey SPECIAL_TEXT_ATTR_KEY = createTextAttributesKey("MARKDOWN.SPECIAL_TEXT", STRING);

    /** Default style for reference links. */
    public static final TextAttributesKey REFERENCE_LINK_ATTR_KEY = createTextAttributesKey("MARKDOWN.REFERENCE_LINK", STRING);

    /** Default style for Wiki links. */
    public static final TextAttributesKey WIKI_LINK_ATTR_KEY = createTextAttributesKey("MARKDOWN.WIKI_LINK", STRING);

    /** Default style for auto links. */
    public static final TextAttributesKey AUTO_LINK_ATTR_KEY = createTextAttributesKey("MARKDOWN.AUTO_LINK", STRING);

    /** Default style for mail links. */
    public static final TextAttributesKey MAIL_LINK_ATTR_KEY = createTextAttributesKey("MARKDOWN.MAIL_LINK", STRING);

    /** Default style for verbatim. */
    public static final TextAttributesKey VERBATIM_ATTR_KEY = createTextAttributesKey("MARKDOWN.VERBATIM", BLOCK_COMMENT);

    /** Default style for blockquotes. */
    public static final TextAttributesKey BLOCK_QUOTE_ATTR_KEY = createTextAttributesKey("MARKDOWN.BLOCK_QUOTE", BLOCK_COMMENT);

    /** Default style for bullet lists. */
    public static final TextAttributesKey BULLET_LIST_ATTR_KEY = createTextAttributesKey("MARKDOWN.BULLET_LIST", IDENTIFIER);

    /** Default style for ordered lists. */
    public static final TextAttributesKey ORDERED_LIST_ATTR_KEY = createTextAttributesKey("MARKDOWN.ORDERED_LIST", IDENTIFIER);

    /** Default style for list items. */
    public static final TextAttributesKey LIST_ITEM_ATTR_KEY = createTextAttributesKey("MARKDOWN.LIST_ITEM", IDENTIFIER);

    /** Default style for definition lists. */
    public static final TextAttributesKey DEFINITION_LIST_ATTR_KEY = createTextAttributesKey("MARKDOWN.DEFINITION_LIST", IDENTIFIER);

    /** Default style for definitions. */
    public static final TextAttributesKey DEFINITION_ATTR_KEY = createTextAttributesKey("MARKDOWN.DEFINITION", IDENTIFIER);

    /** Default style for definition terms. */
    public static final TextAttributesKey DEFINITION_TERM_ATTR_KEY = createTextAttributesKey("MARKDOWN.DEFINITION_TERM", IDENTIFIER);

    /** Default style for tables body. */
    public static final TextAttributesKey TABLE_BODY_ATTR_KEY = createTextAttributesKey("MARKDOWN.TABLE_BODY", IDENTIFIER);

    /** Default style for table cells. */
    public static final TextAttributesKey TABLE_CELL_ATTR_KEY = createTextAttributesKey("MARKDOWN.TABLE_CELL", IDENTIFIER);

    /** Default style for table columns. */
    public static final TextAttributesKey TABLE_COLUMN_ATTR_KEY = createTextAttributesKey("MARKDOWN.TABLE_COLUMN", IDENTIFIER);

    /** Default style for table headers. */
    public static final TextAttributesKey TABLE_HEADER_ATTR_KEY = createTextAttributesKey("MARKDOWN.TABLE_HEADER", IDENTIFIER);

    /** Default style for table rows. */
    public static final TextAttributesKey TABLE_ROW_ATTR_KEY = createTextAttributesKey("MARKDOWN.TABLE_ROW", IDENTIFIER);

    /** Default style for HTML blocks. */
    public static final TextAttributesKey HTML_BLOCK_ATTR_KEY = createTextAttributesKey("MARKDOWN.HTML_BLOCK", IDENTIFIER);

    /** Default style for inline HTML. */
    public static final TextAttributesKey INLINE_HTML_ATTR_KEY = createTextAttributesKey("MARKDOWN.INLINE_HTML", IDENTIFIER);

    /** Default style for references. */
    public static final TextAttributesKey REFERENCE_ATTR_KEY = createTextAttributesKey("MARKDOWN.REFERENCE", IDENTIFIER);

    /** Default style for abbreviations. */
    public static final TextAttributesKey ABBREVIATION_ATTR_KEY = createTextAttributesKey("MARKDOWN.ABBREVIATION", IDENTIFIER);

    /** Default style for text. */
    public static final TextAttributesKey TEXT_ATTR_KEY = createTextAttributesKey("MARKDOWN.TEXT", IDENTIFIER);

    /** Default style for bold text. */
    public static final TextAttributesKey BOLD_ATTR_KEY = createTextAttributesKey("MARKDOWN.BOLD", STRING);

    /** Default style for italic text. */
    public static final TextAttributesKey ITALIC_ATTR_KEY = createTextAttributesKey("MARKDOWN.ITALIC", STRING);

    /** Default style for images. */
    public static final TextAttributesKey IMAGE_ATTR_KEY = createTextAttributesKey("MARKDOWN.IMAGE", STRING);

    /** Default style for reference images. */
    public static final TextAttributesKey REFERENCE_IMAGE_ATTR_KEY = createTextAttributesKey("MARKDOWN.REFERENCE_IMAGE", STRING);

    /** Default style for headers of level 1. */
    public static final TextAttributesKey HEADER_LEVEL_1_ATTR_KEY = createTextAttributesKey("MARKDOWN.HEADER_LEVEL_1", KEYWORD);

    /** Default style for headers of level 2. */
    public static final TextAttributesKey HEADER_LEVEL_2_ATTR_KEY = createTextAttributesKey("MARKDOWN.HEADER_LEVEL_2", KEYWORD);

    /** Default style for headers of level 3. */
    public static final TextAttributesKey HEADER_LEVEL_3_ATTR_KEY = createTextAttributesKey("MARKDOWN.HEADER_LEVEL_3", KEYWORD);

    /** Default style for headers of level 4. */
    public static final TextAttributesKey HEADER_LEVEL_4_ATTR_KEY = createTextAttributesKey("MARKDOWN.HEADER_LEVEL_4", KEYWORD);

    /** Default style for headers of level 5. */
    public static final TextAttributesKey HEADER_LEVEL_5_ATTR_KEY = createTextAttributesKey("MARKDOWN.HEADER_LEVEL_5", KEYWORD);

    /** Default style for headers of level 6. */
    public static final TextAttributesKey HEADER_LEVEL_6_ATTR_KEY = createTextAttributesKey("MARKDOWN.HEADER_LEVEL_6", KEYWORD);

    /** Default style for quotes. */
    public static final TextAttributesKey QUOTE_ATTR_KEY = createTextAttributesKey("MARKDOWN.QUOTE", STRING);

    /** Default style for HRules. */
    public static final TextAttributesKey HRULE_ATTR_KEY = createTextAttributesKey("MARKDOWN.HRULE", BLOCK_COMMENT);

    /** Default style for explicit links. */
    public static final TextAttributesKey EXPLICIT_LINK_ATTR_KEY = createTextAttributesKey("MARKDOWN.EXPLICIT_LINK", STRING);

    /** Default style for code. */
    public static final TextAttributesKey CODE_ATTR_KEY = createTextAttributesKey("MARKDOWN.CODE", BLOCK_COMMENT);

    /** Default style for tables. */
    public static final TextAttributesKey TABLE_ATTR_KEY = createTextAttributesKey("MARKDOWN.TABLE", IDENTIFIER);
}
