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
package net.nicoulaj.idea.markdown.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

/**
 * The default styles for each of token defined for Markdown.
 * <p/>
 * Anyone who has better taste than me, feel free to contribute :)
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 0.1
 */
public class MarkdownHighlighterColors {

    /**
     * Default style for text.
     */
    public static TextAttributesKey TEXT_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.TEXT", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for bold text.
     */
    public static TextAttributesKey BOLD_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.BOLD", new TextAttributes(null, null, null, null, Font.BOLD)
    );

    /**
     * Default style for italic text.
     */
    public static TextAttributesKey ITALIC_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.ITALIC", new TextAttributes(null, null, null, null, Font.ITALIC)
    );

    /**
     * Default style for images.
     */
    public static TextAttributesKey IMAGE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.IMAGE", new TextAttributes(new Color(30, 100, 0), null, null, null, Font.PLAIN)
    );

    /**
     * Default style for headers of level 1.
     */
    public static TextAttributesKey HEADER_LEVEL_1_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_1", new TextAttributes(Color.BLACK, new Color(180, 180, 180), null, null, Font.BOLD)
    );

    /**
     * Default style for headers of level 2.
     */
    public static TextAttributesKey HEADER_LEVEL_2_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_2", new TextAttributes(Color.BLACK, new Color(200, 200, 200), null, null, Font.BOLD)
    );

    /**
     * Default style for headers of level 3.
     */
    public static TextAttributesKey HEADER_LEVEL_3_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_3", new TextAttributes(Color.BLACK, new Color(220, 220, 220), null, null, Font.BOLD)
    );

    /**
     * Default style for headers of level 4.
     */
    public static TextAttributesKey HEADER_LEVEL_4_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_4", new TextAttributes(Color.BLACK, new Color(220, 220, 220), null, null, Font.PLAIN)
    );

    /**
     * Default style for headers of level 5.
     */
    public static TextAttributesKey HEADER_LEVEL_5_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_5", new TextAttributes(Color.BLACK, new Color(220, 220, 220), null, null, Font.PLAIN)
    );

    /**
     * Default style for headers of level 6.
     */
    public static TextAttributesKey HEADER_LEVEL_6_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_6", new TextAttributes(Color.BLACK, new Color(220, 220, 220), null, null, Font.PLAIN)
    );

    /**
     * Default style for quotes.
     */
    public static TextAttributesKey QUOTE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.QUOTE", new TextAttributes(Color.BLACK, new Color(230, 230, 230), null, null, Font.PLAIN)
    );

    /**
     * Default style for HRules.
     */
    public static TextAttributesKey HRULE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HRULE", new TextAttributes(Color.BLACK, new Color(230, 230, 230), null, null, Font.BOLD)
    );

    /**
     * Default style for special text.
     */
    public static final TextAttributesKey SPECIAL_TEXT_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.SPECIAL_TEXT", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for explicit links.
     */
    public static TextAttributesKey EXPLICIT_LINK_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.EXPLICIT_LINK", new TextAttributes(Color.BLUE, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for reference links.
     */
    public static final TextAttributesKey REFERENCE_LINK_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.REFERENCE_LINK", new TextAttributes(Color.BLUE, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for auto links.
     */
    public static final TextAttributesKey AUTO_LINK_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.AUTO_LINK", new TextAttributes(Color.BLUE, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for mail links.
     */
    public static final TextAttributesKey MAIL_LINK_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.MAIL_LINK", new TextAttributes(Color.BLUE, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for code.
     */
    public static TextAttributesKey CODE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.CODE", new TextAttributes(Color.BLACK, new Color(230, 230, 230), null, null, Font.PLAIN)
    );

    /**
     * Default style for verbatim.
     */
    public static final TextAttributesKey VERBATIM_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.VERBATIM", new TextAttributes(Color.BLACK, new Color(230, 230, 230), null, null, Font.PLAIN)
    );

    /**
     * Default style for blockquotes.
     */
    public static final TextAttributesKey BLOCK_QUOTE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.BLOCK_QUOTE", new TextAttributes(Color.BLACK, new Color(230, 230, 230), null, null, Font.PLAIN)
    );

    /**
     * Default style for bullet lists.
     */
    public static final TextAttributesKey BULLET_LIST_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.BULLET_LIST", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for ordered lists.
     */
    public static final TextAttributesKey ORDERED_LIST_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.ORDERED_LIST", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for list items.
     */
    public static final TextAttributesKey LIST_ITEM_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.LIST_ITEM", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for definition lists.
     */
    public static final TextAttributesKey DEFINITION_LIST_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.DEFINITION_LIST", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for definitions.
     */
    public static final TextAttributesKey DEFINITION_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.DEFINITION", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for definition terms.
     */
    public static final TextAttributesKey DEFINITION_TERM_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.DEFINITION_TERM", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for tables.
     */
    public static TextAttributesKey TABLE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.TABLE", new TextAttributes(Color.BLACK, new Color(230, 230, 230), null, null, Font.PLAIN)
    );

    /**
     * Default style for tables body.
     */
    public static final TextAttributesKey TABLE_BODY_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.TABLE_BODY", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for table cells.
     */
    public static final TextAttributesKey TABLE_CELL_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.TABLE_CELL", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for table columns.
     */
    public static final TextAttributesKey TABLE_COLUMN_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.TABLE_COLUMN", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for table headers.
     */
    public static final TextAttributesKey TABLE_HEADER_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.TABLE_HEADER", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for table rows.
     */
    public static final TextAttributesKey TABLE_ROW_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.TABLE_ROW", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for HTML blocks.
     */
    public static final TextAttributesKey HTML_BLOCK_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HTML_BLOCK", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for .
     */
    public static final TextAttributesKey INLINE_HTML_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.INLINE_HTML", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for references.
     */
    public static final TextAttributesKey REFERENCE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.REFERENCE", new TextAttributes(null, null, null, null, Font.PLAIN)
    );

    /**
     * Default style for abbreviations.
     */
    public static final TextAttributesKey ABBREVIATION_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.ABBREVIATION", new TextAttributes(null, null, null, null, Font.PLAIN)
    );
}
