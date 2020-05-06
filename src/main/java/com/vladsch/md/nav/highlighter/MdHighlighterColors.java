// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.highlighter;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import java.util.HashMap;

import static com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class MdHighlighterColors {
    public static MdHighlighterColors getInstance() {
        return ServiceManager.getService(MdHighlighterColors.class);
    }

    final private HashMap<String, TextAttributesKey> markdownKeyMap = new HashMap<>();

    public TextAttributesKey createKey(String key) {
        return markdownKeyMap.computeIfAbsent(key, k -> createTextAttributesKey("MARKDOWN_NAVIGATOR." + k));
    }

    public TextAttributesKey createKey(String key, TextAttributesKey defaultKey) {
        return markdownKeyMap.computeIfAbsent(key, k -> createTextAttributesKey("MARKDOWN_NAVIGATOR." + k, defaultKey));
    }

    //  default language attributes
    //TEMPLATE_LANGUAGE_COLOR IDENTIFIER NUMBER KEYWORD STRING BLOCK_COMMENT LINE_COMMENT DOC_COMMENT OPERATION_SIGN BRACES DOT SEMICOLON
    //COMMA PARENTHESES BRACKETS LABEL CONSTANT LOCAL_VARIABLE GLOBAL_VARIABLE FUNCTION_DECLARATION FUNCTION_CALL PARAMETER CLASS_NAME
    //INTERFACE_NAME CLASS_REFERENCE INSTANCE_METHOD INSTANCE_FIELD STATIC_METHOD STATIC_FIELD DOC_COMMENT_MARKUP DOC_COMMENT_TAG
    //DOC_COMMENT_TAG_VALUE VALID_STRING_ESCAPE INVALID_STRING_ESCAPE PREDEFINED_SYMBOL METADATA MARKUP_TAG MARKUP_ATTRIBUTE MARKUP_ENTITY
    public final TextAttributesKey TEXT_ATTR_KEY = createKey("TEXT");

    //public static final TextAttributesKey TABLE_CELL_REVEN_CODD_CODE_KEY = createKey("TABLE_CELL_REVEN_CODD_CODE_KEY");

    public final TextAttributesKey ABBREVIATION_ATTR_KEY = createKey("ABBREVIATION");
    public final TextAttributesKey ABBREVIATED_TEXT_ATTR_KEY = createKey("ABBREVIATED_TEXT");
    public final TextAttributesKey ABBREVIATION_SHORT_TEXT_ATTR_KEY = createKey("ABBREVIATION_SHORT_TEXT", TEXT_ATTR_KEY);
    public final TextAttributesKey ABBREVIATION_EXPANDED_TEXT_ATTR_KEY = createKey("ABBREVIATION_EXPANDED_TEXT", ABBREVIATION_ATTR_KEY);
    public final TextAttributesKey AUTO_LINK_ATTR_KEY = createKey("AUTO_LINK", EditorColors.REFERENCE_HYPERLINK_COLOR);
    public final TextAttributesKey ASIDE_BLOCK_ATTR_KEY = createKey("ASIDE_BLOCK", BLOCK_COMMENT);
    public final TextAttributesKey BLOCK_QUOTE_ATTR_KEY = createKey("BLOCK_QUOTE", BLOCK_COMMENT);
    public final TextAttributesKey BOLD_ATTR_KEY = createKey("BOLD", TEXT_ATTR_KEY);
    public final TextAttributesKey BOLD_MARKER_ATTR_KEY = createKey("BOLD_MARKER", OPERATION_SIGN);
    public final TextAttributesKey UNDERLINE_ATTR_KEY = createKey("UNDERLINE", TEXT_ATTR_KEY);
    public final TextAttributesKey UNDERLINE_MARKER_ATTR_KEY = createKey("UNDERLINE_MARKER", OPERATION_SIGN);
    public final TextAttributesKey SUPERSCRIPT_ATTR_KEY = createKey("SUPERSCRIPT", TEXT_ATTR_KEY);
    public final TextAttributesKey SUPERSCRIPT_MARKER_ATTR_KEY = createKey("SUPERSCRIPT_MARKER", OPERATION_SIGN);
    public final TextAttributesKey SUBSCRIPT_ATTR_KEY = createKey("SUBSCRIPT", TEXT_ATTR_KEY);
    public final TextAttributesKey SUBSCRIPT_MARKER_ATTR_KEY = createKey("SUBSCRIPT_MARKER", OPERATION_SIGN);
    public final TextAttributesKey BULLET_LIST_ATTR_KEY = createKey("BULLET_LIST");
    public final TextAttributesKey COMMENT_ATTR_KEY = createKey("COMMENT", LINE_COMMENT);
    public final TextAttributesKey BLOCK_COMMENT_ATTR_KEY = createKey("BLOCK_COMMENT", BLOCK_COMMENT);
    public final TextAttributesKey CODE_ATTR_KEY = createKey("CODE", TEMPLATE_LANGUAGE_COLOR);
    public final TextAttributesKey CODE_MARKER_ATTR_KEY = createKey("CODE_MARKER", CODE_ATTR_KEY);
    public final TextAttributesKey GITLAB_MATH_ATTR_KEY = createKey("GITLAB_MATH", CODE_ATTR_KEY);
    public final TextAttributesKey GITLAB_MATH_MARKER_ATTR_KEY = createKey("GITLAB_MATH_MARKER", CODE_MARKER_ATTR_KEY);
    public final TextAttributesKey DEFINITION_MARKER_ATTR_KEY = createKey("DEFINITION_MARKER");
    public final TextAttributesKey DEFINITION_TERM_ATTR_KEY = createKey("DEFINITION_TERM");
    public final TextAttributesKey FOOTNOTE_ATTR_KEY = createKey("FOOTNOTE", BRACKETS);
    public final TextAttributesKey FOOTNOTE_REF_ATTR_KEY = createKey("FOOTNOTE_REF", BRACKETS);
    public final TextAttributesKey FOOTNOTE_ID_ATTR_KEY = createKey("FOOTNOTE_ID", IDENTIFIER);
    public final TextAttributesKey ATX_HEADER_ATTR_KEY = createKey("ATX_HEADER");
    public final TextAttributesKey SETEXT_HEADER_ATTR_KEY = createKey("SETEXT_HEADER", ATX_HEADER_ATTR_KEY);
    public final TextAttributesKey HEADER_TEXT_ATTR_KEY = createKey("HEADER_TEXT", TEXT_ATTR_KEY);
    public final TextAttributesKey HEADER_ATX_MARKER_ATTR_KEY = createKey("HEADER_ATX_MARKER", ATX_HEADER_ATTR_KEY);
    public final TextAttributesKey HEADER_SETEXT_MARKER_ATTR_KEY = createKey("HEADER_SETEXT_MARKER", SETEXT_HEADER_ATTR_KEY);
    public final TextAttributesKey HRULE_ATTR_KEY = createKey("HRULE", ATX_HEADER_ATTR_KEY);
    public final TextAttributesKey HTML_BLOCK_ATTR_KEY = createKey("HTML_BLOCK", TEMPLATE_LANGUAGE_COLOR);
    public final TextAttributesKey INLINE_HTML_ATTR_KEY = createKey("INLINE_HTML", HTML_BLOCK_ATTR_KEY);
    public final TextAttributesKey HTML_ENTITY_ATTR_KEY = createKey("HTML_ENTITY", MARKUP_ENTITY);
    public final TextAttributesKey ITALIC_ATTR_KEY = createKey("ITALIC");
    public final TextAttributesKey ITALIC_MARKER_ATTR_KEY = createKey("ITALIC_MARKER", OPERATION_SIGN);

    public final TextAttributesKey EXPLICIT_LINK_ATTR_KEY = createKey("EXPLICIT_LINK", BRACKETS);
    public final TextAttributesKey LINK_REF_ATTR_KEY = createKey("LINK_REF", AUTO_LINK_ATTR_KEY);
    public final TextAttributesKey LINK_REF_TEXT_ATTR_KEY = createKey("LINK_REF_TEXT", TEXT_ATTR_KEY);
    public final TextAttributesKey LINK_REF_TITLE_ATTR_KEY = createKey("LINK_REF_TITLE", TEXT_ATTR_KEY);
    public final TextAttributesKey LINK_REF_ANCHOR_ATTR_KEY = createKey("LINK_REF_ANCHOR", LINK_REF_ATTR_KEY);
    public final TextAttributesKey LINK_REF_ANCHOR_MARKER_ATTR_KEY = createKey("LINK_REF_ANCHOR_MARKER", EXPLICIT_LINK_ATTR_KEY);

    public final TextAttributesKey MAIL_LINK_ATTR_KEY = createKey("MAIL_LINK", AUTO_LINK_ATTR_KEY);
    public final TextAttributesKey ORDERED_LIST_ATTR_KEY = createKey("ORDERED_LIST", BULLET_LIST_ATTR_KEY);
    public final TextAttributesKey QUOTE_ATTR_KEY = createKey("QUOTE", STRING);
    public final TextAttributesKey QUOTED_TEXT_ATTR_KEY = createKey("QUOTED_TEXT", QUOTE_ATTR_KEY);

    public final TextAttributesKey VERBATIM_ATTR_KEY = createKey("VERBATIM", TEMPLATE_LANGUAGE_COLOR);
    public final TextAttributesKey VERBATIM_MARKER_ATTR_KEY = createKey("VERBATIM_MARKER", VERBATIM_ATTR_KEY);
    public final TextAttributesKey VERBATIM_CONTENT_ATTR_KEY = createKey("VERBATIM_CONTENT", VERBATIM_ATTR_KEY);
    public final TextAttributesKey VERBATIM_LANG_ATTR_KEY = createKey("VERBATIM_LANG", VERBATIM_ATTR_KEY);

    public final TextAttributesKey REFERENCE_ATTR_KEY = createKey("REFERENCE", FOOTNOTE_ATTR_KEY);
    public final TextAttributesKey REFERENCE_LINK_REF_ATTR_KEY = createKey("REFERENCE_LINK_REF", AUTO_LINK_ATTR_KEY);
    public final TextAttributesKey REFERENCE_TEXT_ATTR_KEY = createKey("REFERENCE_TEXT", IDENTIFIER);
    public final TextAttributesKey REFERENCE_TITLE_ATTR_KEY = createKey("REFERENCE_TITLE", TEXT_ATTR_KEY);
    public final TextAttributesKey REFERENCE_ANCHOR_ATTR_KEY = createKey("REFERENCE_ANCHOR", REFERENCE_LINK_REF_ATTR_KEY);
    public final TextAttributesKey REFERENCE_ANCHOR_MARKER_ATTR_KEY = createKey("REFERENCE_ANCHOR_MARKER", REFERENCE_ATTR_KEY);

    public final TextAttributesKey IMAGE_ATTR_KEY = createKey("IMAGE", BRACKETS);
    public final TextAttributesKey IMAGE_LINK_REF_ATTR_KEY = createKey("IMAGE_LINK_REF", AUTO_LINK_ATTR_KEY);
    public final TextAttributesKey IMAGE_URL_CONTENT_ATTR_KEY = createKey("IMAGE_URL_CONTENT", VERBATIM_CONTENT_ATTR_KEY);
    public final TextAttributesKey IMAGE_ALT_TEXT_ATTR_KEY = createKey("IMAGE_ALT_TEXT", TEXT_ATTR_KEY);
    public final TextAttributesKey IMAGE_LINK_REF_TITLE_ATTR_KEY = createKey("IMAGE_LINK_REF_TITLE", TEXT_ATTR_KEY);

    public final TextAttributesKey REFERENCE_IMAGE_ATTR_KEY = createKey("REFERENCE_IMAGE", IMAGE_ATTR_KEY);
    public final TextAttributesKey REFERENCE_IMAGE_REFERENCE_ATTR_KEY = createKey("REFERENCE_IMAGE_REFERENCE", REFERENCE_TEXT_ATTR_KEY);
    public final TextAttributesKey REFERENCE_IMAGE_TEXT_ATTR_KEY = createKey("REFERENCE_IMAGE_TEXT", IMAGE_ALT_TEXT_ATTR_KEY);

    public final TextAttributesKey REFERENCE_LINK_ATTR_KEY = createKey("REFERENCE_LINK", EXPLICIT_LINK_ATTR_KEY);
    public final TextAttributesKey REFERENCE_LINK_REFERENCE_ATTR_KEY = createKey("REFERENCE_LINK_REFERENCE", REFERENCE_TEXT_ATTR_KEY);
    public final TextAttributesKey REFERENCE_LINK_TEXT_ATTR_KEY = createKey("REFERENCE_LINK_TEXT", LINK_REF_TEXT_ATTR_KEY);

    public final TextAttributesKey ATTRIBUTES_MARKER_ATTR_KEY = createKey("ATTRIBUTES_MARKER", FOOTNOTE_REF_ATTR_KEY);
    public final TextAttributesKey ATTRIBUTE_NAME_ATTR_KEY = createKey("ATTRIBUTE_NAME", MARKUP_ATTRIBUTE);
    public final TextAttributesKey ATTRIBUTE_VALUE_SEP_ATTR_KEY = createKey("ATTRIBUTE_VALUE_SEP", OPERATION_SIGN);
    public final TextAttributesKey ATTRIBUTE_VALUE_MARKER_ATTR_KEY = createKey("ATTRIBUTE_VALUE_MARKER", QUOTE_ATTR_KEY);
    public final TextAttributesKey ENUM_REF_FORMAT_ATTR_KEY = createKey("ENUM_REF_FORMAT", FOOTNOTE_ATTR_KEY);
    public final TextAttributesKey ENUM_REF_LINK_ATTR_KEY = createKey("ENUM_REF_LINK", FOOTNOTE_REF_ATTR_KEY);
    public final TextAttributesKey ENUM_REF_TEXT_ATTR_KEY = createKey("ENUM_REF_TEXT", FOOTNOTE_REF_ATTR_KEY);
    public final TextAttributesKey ENUM_REF_ID_ATTR_KEY = createKey("ENUM_REF_ID", FOOTNOTE_ID_ATTR_KEY);

    public final TextAttributesKey ANCHOR_ATTR_KEY = createKey("ANCHOR", INLINE_HTML_ATTR_KEY);
    public final TextAttributesKey ANCHOR_ID_ATTR_KEY = createKey("ANCHOR_ID", IDENTIFIER);

    public final TextAttributesKey EMOJI_MARKER_ATTR_KEY = createKey("EMOJI_MARKER", HTML_ENTITY_ATTR_KEY);
    public final TextAttributesKey EMOJI_ID_ATTR_KEY = createKey("EMOJI_ID", IDENTIFIER);

    public final TextAttributesKey SMARTS_ATTR_KEY = createKey("SMARTS", TEXT_ATTR_KEY);
    public final TextAttributesKey SPECIAL_TEXT_ATTR_KEY = createKey("SPECIAL_TEXT", VALID_STRING_ESCAPE);
    public final TextAttributesKey SPECIAL_TEXT_MARKER_ATTR_KEY = createKey("SPECIAL_TEXT_MARKER", SPECIAL_TEXT_ATTR_KEY);
    public final TextAttributesKey LINE_BREAK_SPACES_ATTR_KEY = createKey("LINE_BREAK_SPACES", INVALID_STRING_ESCAPE);
    public final TextAttributesKey STRIKETHROUGH_ATTR_KEY = createKey("STRIKETHROUGH");
    public final TextAttributesKey STRIKETHROUGH_MARKER_ATTR_KEY = createKey("STRIKETHROUGH_MARKER", OPERATION_SIGN);
    public final TextAttributesKey TABLE_ATTR_KEY = createKey("TABLE");
    public final TextAttributesKey TOC_ATTR_KEY = createKey("TOC", KEYWORD);
    public final TextAttributesKey GEN_CONTENT_ATTR_KEY = createKey("GEN_CONTENT", BLOCK_COMMENT);
    public final TextAttributesKey TOC_MARKER_ATTR_KEY = createKey("TOC_MARKER", REFERENCE_ATTR_KEY);
    public final TextAttributesKey TOC_OPTION_ATTR_KEY = createKey("TOC_OPTION", IDENTIFIER);
    public final TextAttributesKey SIM_TOC_TITLE_ATTR_KEY = createKey("SIM_TOC_TITLE", IDENTIFIER);
    public final TextAttributesKey TABLE_CAPTION_ATTR_KEY = createKey("TABLE_CAPTION");
    public final TextAttributesKey TABLE_CAPTION_MARKER_ATTR_KEY = createKey("TABLE_CAPTION_MARKER", TABLE_ATTR_KEY);
    public final TextAttributesKey TABLE_CELL_REVEN_CEVEN_ATTR_KEY = createKey("TABLE_CELL_REVEN_CEVEN");
    public final TextAttributesKey TABLE_CELL_REVEN_CODD_ATTR_KEY = createKey("TABLE_CELL_REVEN_CODD");
    public final TextAttributesKey TABLE_CELL_RODD_CEVEN_ATTR_KEY = createKey("TABLE_CELL_RODD_CEVEN");
    public final TextAttributesKey TABLE_CELL_RODD_CODD_ATTR_KEY = createKey("TABLE_CELL_RODD_CODD");
    public final TextAttributesKey TABLE_ROW_EVEN_ATTR_KEY = createKey("TABLE_ROW_EVEN");
    public final TextAttributesKey TABLE_ROW_ODD_ATTR_KEY = createKey("TABLE_ROW_ODD");
    public final TextAttributesKey TABLE_HDR_CELL_REVEN_CEVEN_ATTR_KEY = createKey("TABLE_HDR_CELL_REVEN_CEVEN", TABLE_CELL_REVEN_CEVEN_ATTR_KEY);
    public final TextAttributesKey TABLE_HDR_CELL_REVEN_CODD_ATTR_KEY = createKey("TABLE_HDR_CELL_REVEN_CODD", TABLE_CELL_REVEN_CODD_ATTR_KEY);
    public final TextAttributesKey TABLE_HDR_CELL_RODD_CEVEN_ATTR_KEY = createKey("TABLE_HDR_CELL_RODD_CEVEN", TABLE_CELL_RODD_CEVEN_ATTR_KEY);
    public final TextAttributesKey TABLE_HDR_CELL_RODD_CODD_ATTR_KEY = createKey("TABLE_HDR_CELL_RODD_CODD", TABLE_CELL_RODD_CODD_ATTR_KEY);
    public final TextAttributesKey TABLE_HDR_ROW_EVEN_ATTR_KEY = createKey("TABLE_HDR_ROW_EVEN", TABLE_ROW_EVEN_ATTR_KEY);
    public final TextAttributesKey TABLE_HDR_ROW_ODD_ATTR_KEY = createKey("TABLE_HDR_ROW_ODD", TABLE_ROW_ODD_ATTR_KEY);
    public final TextAttributesKey TABLE_SEP_COLUMN_ODD_ATTR_KEY = createKey("TABLE_SEP_COLUMN_ODD", TABLE_CELL_REVEN_CODD_ATTR_KEY);
    public final TextAttributesKey TABLE_SEP_COLUMN_EVEN_ATTR_KEY = createKey("TABLE_SEP_COLUMN_EVEN", TABLE_CELL_REVEN_CEVEN_ATTR_KEY);
    public final TextAttributesKey TABLE_SEPARATOR_ATTR_KEY = createKey("TABLE_SEPARATOR");
    public final TextAttributesKey TASK_ITEM_MARKER_ATTR_KEY = createKey("TASK_ITEM_MARKER");
    public final TextAttributesKey TASK_DONE_ITEM_MARKER_ATTR_KEY = createKey("TASK_DONE_ITEM_MARKER");
    public final TextAttributesKey TASK_DONE_ITEM_TEXT_ATTR_KEY = createKey("TASK_DONE_ITEM_TEXT", BLOCK_COMMENT);
    public final TextAttributesKey WIKI_LINK_ATTR_KEY = createKey("WIKI_LINK", BRACKETS);
    public final TextAttributesKey WIKI_LINK_REF_ATTR_KEY = createKey("WIKI_LINK_REF", WIKI_LINK_ATTR_KEY);
    public final TextAttributesKey WIKI_LINK_REF_ANCHOR_ATTR_KEY = createKey("WIKI_LINK_REF_ANCHOR", WIKI_LINK_REF_ATTR_KEY);
    public final TextAttributesKey WIKI_LINK_REF_ANCHOR_MARKER_ATTR_KEY = createKey("WIKI_LINK_REF_ANCHOR_MARKER", WIKI_LINK_ATTR_KEY);
    public final TextAttributesKey WIKI_LINK_TEXT_ATTR_KEY = createKey("WIKI_LINK_TEXT", WIKI_LINK_REF_ATTR_KEY);
    public final TextAttributesKey WIKI_LINK_SEPARATOR_ATTR_KEY = createKey("WIKI_LINK_SEPARATOR", WIKI_LINK_ATTR_KEY);

    public final TextAttributesKey JEKYLL_FRONT_MATTER_BLOCK_ATTR_KEY = createKey("JEKYLL_FRONT_MATTER_BLOCK", TEMPLATE_LANGUAGE_COLOR);
    public final TextAttributesKey JEKYLL_FRONT_MATTER_MARKER_ATTR_KEY = createKey("JEKYLL_FRONT_MATTER_MARKER", VERBATIM_MARKER_ATTR_KEY);
    public final TextAttributesKey JEKYLL_TAG_MARKER_ATTR_KEY = createKey("JEKYLL_TAG_MARKER", VERBATIM_MARKER_ATTR_KEY);
    public final TextAttributesKey JEKYLL_TAG_NAME_ATTR_KEY = createKey("JEKYLL_TAG_NAME", KEYWORD);
    public final TextAttributesKey JEKYLL_TAG_PARAMETERS_ATTR_KEY = createKey("JEKYLL_TAG_PARAMETERS", LINK_REF_ATTR_KEY);

    public final TextAttributesKey FLEXMARK_MARKER_ATTR_KEY = createKey("FLEXMARK_MARKER", VERBATIM_MARKER_ATTR_KEY);
    public final TextAttributesKey FLEXMARK_EXAMPLE_SECTION_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_SECTION", TEXT_ATTR_KEY);
    public final TextAttributesKey FLEXMARK_EXAMPLE_SECTION_MARKER_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_SECTION_MARKERS", OPERATION_SIGN);
    public final TextAttributesKey FLEXMARK_EXAMPLE_NUMBER_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_NUMBER", NUMBER);
    public final TextAttributesKey FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD", KEYWORD);
    public final TextAttributesKey FLEXMARK_EXAMPLE_OPTIONS_KEYWORD_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_OPTIONS_KEYWORD", KEYWORD);
    public final TextAttributesKey FLEXMARK_EXAMPLE_OPTIONS_MARKER_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_OPTIONS_MARKER", FLEXMARK_EXAMPLE_OPTIONS_KEYWORD_ATTR_KEY);
    public final TextAttributesKey FLEXMARK_EXAMPLE_OPTION_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_OPTION", IDENTIFIER);
    public final TextAttributesKey FLEXMARK_EXAMPLE_OPTION_PARAM_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_OPTION_PARAM", STRING);
    public final TextAttributesKey FLEXMARK_EXAMPLE_OPTION_PARAM_MARKER_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_OPTION_PARAM_MARKER", BRACKETS);
    public final TextAttributesKey FLEXMARK_EXAMPLE_OPTION_BUILT_IN_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_OPTION_BUILT_IN", VERBATIM_MARKER_ATTR_KEY);
    public final TextAttributesKey FLEXMARK_EXAMPLE_OPTION_IGNORE_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_OPTION_IGNORE", VERBATIM_MARKER_ATTR_KEY);
    public final TextAttributesKey FLEXMARK_EXAMPLE_OPTION_FAIL_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_OPTION_FAIL", VERBATIM_MARKER_ATTR_KEY);
    public final TextAttributesKey FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME", LINE_COMMENT);
    public final TextAttributesKey FLEXMARK_EXAMPLE_SEPARATOR_ATTR_KEY = createKey("FLEXMARK_EXAMPLE_SEPARATOR", VERBATIM_MARKER_ATTR_KEY);

    public final TextAttributesKey DEBUG_FORMAT_TEXT_BLOCK_KEY = createKey("DEBUG_FORMAT_TEXT_BLOCK");
    public final TextAttributesKey DEBUG_FORMAT_PREFIX_KEY = createKey("DEBUG_FORMAT_PREFIX");

    public final TextAttributesKey DEBUG_FLEXMARK_AST_KEY = createKey("DEBUG_FLEXMARK_AST_KEY", DEBUG_FORMAT_PREFIX_KEY);
    public final TextAttributesKey DEBUG_FLEXMARK_SOURCE_KEY = createKey("DEBUG_FLEXMARK_SOURCE_KEY", DEBUG_FORMAT_TEXT_BLOCK_KEY);

    public final TextAttributesKey ADMONITION_MARKER_ATTR_KEY = createKey("ADMONITION_MARKER", VERBATIM_MARKER_ATTR_KEY);
    public final TextAttributesKey ADMONITION_INFO_ATTR_KEY = createKey("ADMONITION_INFO", VERBATIM_LANG_ATTR_KEY);
    public final TextAttributesKey ADMONITION_TITLE_ATTR_KEY = createKey("ADMONITION_TITLE", LINK_REF_TITLE_ATTR_KEY);

    public final TextAttributesKey MACRO_ATTR_KEY = createKey("MACRO", VERBATIM_MARKER_ATTR_KEY);
    public final TextAttributesKey MACRO_REF_ATTR_KEY = createKey("MACRO_REF", VERBATIM_MARKER_ATTR_KEY);
    public final TextAttributesKey MACRO_ID_ATTR_KEY = createKey("MACRO_ID", VERBATIM_LANG_ATTR_KEY);

//    public boolean wasMigrated = false;
//    final public Object migrationLock = new Object();
//
//    public void migrateAttributes() {
//        boolean isMigrated = false;
//
//        synchronized (migrationLock) {
//            isMigrated = wasMigrated;
//            wasMigrated = true;
//        }
//
//        if (!isMigrated) {
//            EditorColorsScheme[] schemes = EditorColorsManager.getInstance().getAllSchemes();
//            TextAttributes emptyAttributes = new TextAttributes();
//
//            for (EditorColorsScheme scheme : schemes) {
//                if (EditorColorsManager.getInstance().isDefaultScheme(scheme)) continue;
//
//                final List<TextAttributesKey> keys = new ArrayList<>(markdownNewToOldKeyMap.keySet());
//                for (TextAttributesKey newKey : keys) {
//                    TextAttributesKey oldKey = markdownNewToOldKeyMap.get(newKey);
//                    TextAttributes fallBack = oldKey.getFallbackAttributeKey() == null ? null : scheme.getAttributes(oldKey.getFallbackAttributeKey());
//
//                    TextAttributes oldAttributes = scheme.getAttributes(oldKey);
//                    TextAttributes newAttributes = scheme.getAttributes(newKey);
//
//                    if (oldAttributes != null && !oldAttributes.isEmpty() && (fallBack == null || !oldAttributes.equals(fallBack))) {
//                        // copy old to new and erase old
//                        scheme.setAttributes(newKey, oldAttributes);
//                        scheme.setAttributes(oldKey, emptyAttributes);
//                    }
//                }
//            }
//        }
//    }
}
