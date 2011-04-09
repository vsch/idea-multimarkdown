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
     * Default style for plain text.
     */
    public static TextAttributesKey PLAIN_TEXT_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.PLAIN_TEXT", new TextAttributes(null, null, null, null, Font.PLAIN)
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
     * Default style for links.
     */
    public static TextAttributesKey LINK_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.LINK", new TextAttributes(new Color(0, 0, 255), null, null, null, Font.PLAIN)
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
            "MARKDOWN.HEADER_LEVEL_1", new TextAttributes(null, new Color(210, 210, 210), null, null, Font.BOLD)
    );

    /**
     * Default style for headers of level 2.
     */
    public static TextAttributesKey HEADER_LEVEL_2_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_2", new TextAttributes(null, new Color(220, 220, 220), null, null, Font.BOLD)
    );

    /**
     * Default style for headers of level 3.
     */
    public static TextAttributesKey HEADER_LEVEL_3_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_3", new TextAttributes(null, new Color(230, 230, 230), null, null, Font.BOLD)
    );

    /**
     * Default style for headers of level 4.
     */
    public static TextAttributesKey HEADER_LEVEL_4_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_4", new TextAttributes(null, new Color(230, 230, 230), null, null, Font.PLAIN)
    );

    /**
     * Default style for headers of level 5.
     */
    public static TextAttributesKey HEADER_LEVEL_5_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_5", new TextAttributes(null, new Color(230, 230, 230), null, null, Font.PLAIN)
    );

    /**
     * Default style for headers of level 6.
     */
    public static TextAttributesKey HEADER_LEVEL_6_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HEADER_LEVEL_6", new TextAttributes(null, new Color(230, 230, 230), null, null, Font.PLAIN)
    );

    /**
     * Default style for code.
     */
    public static TextAttributesKey CODE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.CODE", new TextAttributes(null, new Color(220, 240, 255), null, null, Font.PLAIN)
    );

    /**
     * Default style for quotes.
     */
    public static TextAttributesKey QUOTE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.QUOTE", new TextAttributes(null, new Color(220, 240, 255), null, null, Font.PLAIN)
    );

    /**
     * Default style for tables.
     */
    public static TextAttributesKey TABLE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.TABLE", new TextAttributes(null, new Color(220, 240, 255), null, null, Font.PLAIN)
    );

    /**
     * Default style for HRules.
     */
    public static TextAttributesKey HRULE_ATTR_KEY = TextAttributesKey.createTextAttributesKey(
            "MARKDOWN.HRULE", new TextAttributes(null, new Color(230, 230, 230), null, null, Font.PLAIN)
    );
}
