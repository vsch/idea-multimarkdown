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
import com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class MultiMarkdownHighlighterColors {

    private static TextAttributesKey createKey(String key) { return createTextAttributesKey("MULTIMARKDOWN." + key);}

    private static TextAttributesKey createKey(String key, TextAttributesKey defaultKey) { return createTextAttributesKey("MULTIMARKDOWN." + key, defaultKey);}

    //  default language attributes
    //TEMPLATE_LANGUAGE_COLOR IDENTIFIER NUMBER KEYWORD STRING BLOCK_COMMENT LINE_COMMENT DOC_COMMENT OPERATION_SIGN BRACES DOT SEMICOLON
    //COMMA PARENTHESES BRACKETS LABEL CONSTANT LOCAL_VARIABLE GLOBAL_VARIABLE FUNCTION_DECLARATION FUNCTION_CALL PARAMETER CLASS_NAME
    //INTERFACE_NAME CLASS_REFERENCE INSTANCE_METHOD INSTANCE_FIELD STATIC_METHOD STATIC_FIELD DOC_COMMENT_MARKUP DOC_COMMENT_TAG
    //DOC_COMMENT_TAG_VALUE VALID_STRING_ESCAPE INVALID_STRING_ESCAPE PREDEFINED_SYMBOL METADATA MARKUP_TAG MARKUP_ATTRIBUTE MARKUP_ENTITY

    public static final TextAttributesKey ABBREVIATION_ATTR_KEY = createKey("ABBREVIATION");
    public static final TextAttributesKey ABBREVIATED_TEXT_ATTR_KEY = createKey("ABBREVIATED_TEXT");
    public static final TextAttributesKey ANCHOR_LINK_ATTR_KEY = createKey("ANCHOR_LINK");
    public static final TextAttributesKey AUTO_LINK_ATTR_KEY = createKey("AUTO_LINK");
    public static final TextAttributesKey BLOCK_QUOTE_ATTR_KEY = createKey("BLOCK_QUOTE");
    public static final TextAttributesKey BOLD_ATTR_KEY = createKey("BOLD");
    public static final TextAttributesKey BOLD_MARKER_ATTR_KEY = createKey("BOLD_MARKER");
    public static final TextAttributesKey BOLDITALIC_ATTR_KEY = createKey("BOLDITALIC");
    public static final TextAttributesKey BULLET_LIST_ATTR_KEY = createKey("BULLET_LIST");
    public static final TextAttributesKey COMMENT_ATTR_KEY = createKey("COMMENT", BLOCK_COMMENT);
    public static final TextAttributesKey CODE_ATTR_KEY = createKey("CODE");
    public static final TextAttributesKey DEFINITION_ATTR_KEY = createKey("DEFINITION");
    public static final TextAttributesKey DEFINITION_LIST_ATTR_KEY = createKey("DEFINITION_LIST");
    public static final TextAttributesKey DEFINITION_TERM_ATTR_KEY = createKey("DEFINITION_TERM");
    public static final TextAttributesKey EXPLICIT_LINK_ATTR_KEY = createKey("EXPLICIT_LINK");
    public static final TextAttributesKey HEADER_LEVEL_1_ATTR_KEY = createKey("HEADER_LEVEL_1");
    public static final TextAttributesKey SETEXT_HEADER_LEVEL_1_ATTR_KEY = createKey("SETEXT_HEADER_LEVEL_1");
    public static final TextAttributesKey HEADER_LEVEL_2_ATTR_KEY = createKey("HEADER_LEVEL_2");
    public static final TextAttributesKey SETEXT_HEADER_LEVEL_2_ATTR_KEY = createKey("SETEXT_HEADER_LEVEL_2");
    public static final TextAttributesKey HEADER_LEVEL_3_ATTR_KEY = createKey("HEADER_LEVEL_3");
    public static final TextAttributesKey HEADER_LEVEL_4_ATTR_KEY = createKey("HEADER_LEVEL_4");
    public static final TextAttributesKey HEADER_LEVEL_5_ATTR_KEY = createKey("HEADER_LEVEL_5");
    public static final TextAttributesKey HEADER_LEVEL_6_ATTR_KEY = createKey("HEADER_LEVEL_6");
    public static final TextAttributesKey HRULE_ATTR_KEY = createKey("HRULE");
    public static final TextAttributesKey HTML_BLOCK_ATTR_KEY = createKey("HTML_BLOCK");
    public static final TextAttributesKey IMAGE_ATTR_KEY = createKey("IMAGE");
    public static final TextAttributesKey INLINE_HTML_ATTR_KEY = createKey("INLINE_HTML");
    public static final TextAttributesKey ITALIC_ATTR_KEY = createKey("ITALIC");
    public static final TextAttributesKey ITALIC_MARKER_ATTR_KEY = createKey("ITALIC_MARKER");
    public static final TextAttributesKey LIST_ITEM_ATTR_KEY = createKey("LIST_ITEM");
    public static final TextAttributesKey MAIL_LINK_ATTR_KEY = createKey("MAIL_LINK");
    public static final TextAttributesKey ORDERED_LIST_ATTR_KEY = createKey("ORDERED_LIST");
    public static final TextAttributesKey QUOTE_ATTR_KEY = createKey("QUOTE");
    public static final TextAttributesKey REFERENCE_ATTR_KEY = createKey("REFERENCE");
    public static final TextAttributesKey REFERENCE_IMAGE_ATTR_KEY = createKey("REFERENCE_IMAGE");
    public static final TextAttributesKey REFERENCE_LINK_ATTR_KEY = createKey("REFERENCE_LINK");
    public static final TextAttributesKey SMARTS_ATTR_KEY = createKey("SMARTS");
    public static final TextAttributesKey SPECIAL_TEXT_ATTR_KEY = createKey("SPECIAL_TEXT");
    public static final TextAttributesKey STRIKETHROUGH_ATTR_KEY = createKey("STRIKETHROUGH");
    public static final TextAttributesKey STRIKETHROUGH_BOLD_ATTR_KEY = createKey("STRIKETHROUGH_BOLD");
    public static final TextAttributesKey STRIKETHROUGH_ITALIC_ATTR_KEY = createKey("STRIKETHROUGH_ITALIC");
    public static final TextAttributesKey STRIKETHROUGH_BOLDITALIC_ATTR_KEY = createKey("STRIKETHROUGH_BOLDITALIC");
    public static final TextAttributesKey STRIKETHROUGH_MARKER_ATTR_KEY = createKey("STRIKETHROUGH_MARKER");
    public static final TextAttributesKey TABLE_ATTR_KEY = createKey("TABLE");
    public static final TextAttributesKey TABLE_BODY_ATTR_KEY = createKey("TABLE_BODY");
    public static final TextAttributesKey TABLE_CAPTION_ATTR_KEY = createKey("TABLE_CAPTION");
    public static final TextAttributesKey TABLE_CELL_REVEN_CEVEN_ATTR_KEY = createKey("TABLE_CELL_REVEN_CEVEN");
    public static final TextAttributesKey TABLE_CELL_REVEN_CODD_ATTR_KEY = createKey("TABLE_CELL_REVEN_CODD");
    public static final TextAttributesKey TABLE_CELL_RODD_CEVEN_ATTR_KEY = createKey("TABLE_CELL_RODD_CEVEN");
    public static final TextAttributesKey TABLE_CELL_RODD_CODD_ATTR_KEY = createKey("TABLE_CELL_RODD_CODD");
    public static final TextAttributesKey TABLE_COLUMN_ATTR_KEY = createKey("TABLE_COLUMN");
    public static final TextAttributesKey TABLE_HEADER_ATTR_KEY = createKey("TABLE_HEADER");
    public static final TextAttributesKey TABLE_ROW_EVEN_ATTR_KEY = createKey("TABLE_ROW_EVEN");
    public static final TextAttributesKey TABLE_ROW_ODD_ATTR_KEY = createKey("TABLE_ROW_ODD");
    public static final TextAttributesKey TASK_ITEM_ATTR_KEY = createKey("TASK_ITEM");
    public static final TextAttributesKey TASK_DONE_ITEM_ATTR_KEY = createKey("TASK_DONE_ITEM");
    public static final TextAttributesKey TASK_ITEM_MARKER_ATTR_KEY = createKey("TASK_ITEM_MARKER");
    public static final TextAttributesKey TASK_DONE_ITEM_MARKER_ATTR_KEY = createKey("TASK_DONE_ITEM_MARKER");
    public static final TextAttributesKey TEXT_ATTR_KEY = createKey("TEXT");
    public static final TextAttributesKey VERBATIM_ATTR_KEY = createKey("VERBATIM");
    public static final TextAttributesKey WIKI_LINK_ATTR_KEY = createKey("WIKI_LINK");
    public static final TextAttributesKey WIKI_LINK_REF_ATTR_KEY = createKey("WIKI_LINK_REF", WIKI_LINK_ATTR_KEY);
    public static final TextAttributesKey WIKI_LINK_TEXT_ATTR_KEY = createKey("WIKI_LINK_TEXT", WIKI_LINK_ATTR_KEY);
    public static final TextAttributesKey WIKI_LINK_SEPARATOR_ATTR_KEY = createKey("WIKI_LINK_SEPARATOR", WIKI_LINK_ATTR_KEY);
}
