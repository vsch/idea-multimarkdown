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
    public static final TextAttributesKey SPECIAL_TEXT_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.SPECIAL_TEXT");

    /** Default style for reference links. */
    public static final TextAttributesKey REFERENCE_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.REFERENCE_LINK");

    /** Default style for Wiki links. */
    public static final TextAttributesKey WIKI_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.WIKI_LINK");

    /** Default style for auto links. */
    public static final TextAttributesKey AUTO_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.AUTO_LINK");

    /** Default style for mail links. */
    public static final TextAttributesKey MAIL_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.MAIL_LINK");

    /** Default style for verbatim. */
    public static final TextAttributesKey VERBATIM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.VERBATIM");

    /** Default style for blockquotes. */
    public static final TextAttributesKey BLOCK_QUOTE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BLOCK_QUOTE");

    /** Default style for bullet lists. */
    public static final TextAttributesKey BULLET_LIST_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BULLET_LIST");

    /** Default style for ordered lists. */
    public static final TextAttributesKey ORDERED_LIST_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ORDERED_LIST");

    /** Default style for list items. */
    public static final TextAttributesKey LIST_ITEM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.LIST_ITEM");

    /** Default style for definition lists. */
    public static final TextAttributesKey DEFINITION_LIST_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.DEFINITION_LIST");

    /** Default style for definitions. */
    public static final TextAttributesKey DEFINITION_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.DEFINITION");

    /** Default style for definition terms. */
    public static final TextAttributesKey DEFINITION_TERM_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.DEFINITION_TERM");

    /** Default style for tables body. */
    public static final TextAttributesKey TABLE_BODY_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_BODY");

    /** Default style for table cells. */
    public static final TextAttributesKey TABLE_CELL_RODD_CODD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CELL_RODD_CODD");
    public static final TextAttributesKey TABLE_CELL_RODD_CEVEN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CELL_RODD_CEVEN");
    public static final TextAttributesKey TABLE_CELL_REVEN_CODD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CELL_REVEN_CODD");
    public static final TextAttributesKey TABLE_CELL_REVEN_CEVEN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CELL_REVEN_CEVEN");

    /** Default style for table columns. */
    public static final TextAttributesKey TABLE_COLUMN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_COLUMN");

    /** Default style for table headers. */
    public static final TextAttributesKey TABLE_HEADER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_HEADER");

    /** Default style for table rows. */
    public static final TextAttributesKey TABLE_ROW_ODD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_ROW_ODD");
    public static final TextAttributesKey TABLE_ROW_EVEN_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_ROW_EVEN");

    /** Default style for table captions. */
    public static final TextAttributesKey TABLE_CAPTION_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE_CAPTION");

    /** Default style for HTML blocks. */
    public static final TextAttributesKey HTML_BLOCK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HTML_BLOCK");

    /** Default style for inline HTML. */
    public static final TextAttributesKey INLINE_HTML_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.INLINE_HTML");

    /** Default style for references. */
    public static final TextAttributesKey REFERENCE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.REFERENCE");

    /** Default style for abbreviations. */
    public static final TextAttributesKey ABBREVIATION_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ABBREVIATION");

    /** Default style for text. */
    public static final TextAttributesKey TEXT_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TEXT");

    /** Default style for bold text. */
    public static final TextAttributesKey BOLD_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BOLD");
    public static final TextAttributesKey BOLD_MARKER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BOLD_MARKER");

    /** Default style for bold text. */
    public static final TextAttributesKey ANCHOR_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ANCHOR_LINK");

    /** Default style for bold text. */
    public static final TextAttributesKey BOLDITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.BOLDITALIC");

    /** Default style for italic text. */
    public static final TextAttributesKey ITALIC_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ITALIC");
    public static final TextAttributesKey ITALIC_MARKER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.ITALIC_MARKER");

    /** Default style for strikethrough text. */
    public static final TextAttributesKey STRIKETHROUGH_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.STRIKETHROUGH");
    public static final TextAttributesKey STRIKETHROUGH_MARKER_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.STRIKETHROUGH_MARKER");

    /** Default style for images. */
    public static final TextAttributesKey IMAGE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.IMAGE");

    /** Default style for reference images. */
    public static final TextAttributesKey REFERENCE_IMAGE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.REFERENCE_IMAGE");

    /** Default style for headers of level 1. */
    public static final TextAttributesKey HEADER_LEVEL_1_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_1");

    /** Default style for headers of level 2. */
    public static final TextAttributesKey HEADER_LEVEL_2_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_2");

    /** Default style for headers of level 3. */
    public static final TextAttributesKey HEADER_LEVEL_3_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_3");

    /** Default style for headers of level 4. */
    public static final TextAttributesKey HEADER_LEVEL_4_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_4");

    /** Default style for headers of level 5. */
    public static final TextAttributesKey HEADER_LEVEL_5_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_5");

    /** Default style for headers of level 6. */
    public static final TextAttributesKey HEADER_LEVEL_6_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HEADER_LEVEL_6");

    /** Default style for quotes. */
    public static final TextAttributesKey QUOTE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.QUOTE");

    /** Default style for quotes. */
    public static final TextAttributesKey SMARTS_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.SMARTS");

    /** Default style for HRules. */
    public static final TextAttributesKey HRULE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.HRULE");

    /** Default style for explicit links. */
    public static final TextAttributesKey EXPLICIT_LINK_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.EXPLICIT_LINK");

    /** Default style for code. */
    public static final TextAttributesKey CODE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.CODE");

    /** Default style for tables. */
    public static final TextAttributesKey TABLE_ATTR_KEY = createTextAttributesKey("MULTIMARKDOWN.TABLE");
}
