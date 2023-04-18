// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.util;

import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.flex.psi.*;
import com.vladsch.md.nav.parser.api.MdTypeFactoryRegistry;
import com.vladsch.md.nav.psi.element.*;

public interface MdTypes {
    //Key<String> HEADER_REF_ID_DATA_KEY = new Key<String>("MultiMarkdownHeaderReferenceId");
    IElementType FILE = new MdTokenType("FILE");// for testing

    // no specific element, blank lines and white spaces
    IElementType NONE = new MdTokenType("NONE");
    IElementType WHITESPACE = new MdTokenType("WHITESPACE");
    IElementType BLOCK_QUOTE_WHITESPACE = new MdTokenType("BLOCK_QUOTE_WHITESPACE");
    IElementType ASIDE_BLOCK_WHITESPACE = new MdTokenType("ASIDE_BLOCK_WHITESPACE");
    IElementType EOL = new MdTokenType("EOL");
    IElementType BLANK_LINE = new MdTokenType("BLANK_LINE");

    IElementType ABBREVIATION = new MdTokenType("ABBREVIATION");
    IElementType ABBREVIATION_OPEN = new MdTokenType("ABBREVIATION_OPEN");
    IElementType ABBREVIATION_CLOSE = new MdTokenType("ABBREVIATION_CLOSE");
    IElementType ABBREVIATION_SHORT_TEXT = new MdTokenType("ABBREVIATION_SHORT_TEXT");
    IElementType ABBREVIATION_EXPANDED_TEXT = new MdTokenType("ABBREVIATION_EXPANDED_TEXT");
    IElementType ABBREVIATED = new MdTokenType("ABBREVIATED");
    IElementType ABBREVIATED_TEXT = new MdTokenType("ABBREVIATED_TEXT");
    IElementType ANCHOR_LINK = new MdTokenType("ANCHOR_LINK");
    IElementType BLOCK_QUOTE = new MdTokenType("BLOCK_QUOTE");
    IElementType BLOCK_QUOTE_MARKER = new MdTokenType("BLOCK_QUOTE_MARKER");
    IElementType ASIDE_BLOCK = new MdTokenType("ASIDE_BLOCK");
    IElementType ASIDE_BLOCK_MARKER = new MdTokenType("ASIDE_BLOCK_MARKER");
    IElementType BOLD = new MdTokenType("BOLD");
    IElementType BOLD_TEXT = new MdTokenType("BOLD_TEXT");
    IElementType BOLD_MARKER = new MdTokenType("BOLD_MARKER");
    IElementType BULLET_LIST = new MdTokenType("BULLET_LIST");
    IElementType BULLET_LIST_ITEM = new MdTokenType("BULLET_LIST_ITEM");
    IElementType BULLET_LIST_ITEM_MARKER = new MdTokenType("BULLET_LIST_ITEM_MARKER");
    IElementType CODE = new MdTokenType("CODE");
    IElementType CODE_TEXT = new MdTokenType("CODE_TEXT");
    IElementType CODE_MARKER = new MdTokenType("CODE_MARKER");
    IElementType DEFINITION_LIST = new MdTokenType("DEFINITION_LIST");
    IElementType DEFINITION_TERM_ELEMENT = new MdTokenType("DEFINITION_TERM_ELEMENT");
    IElementType DEFINITION_TERM = new MdTokenType("DEFINITION_TERM");
    IElementType DEFINITION_MARKER = new MdTokenType("DEFINITION_MARKER");
    IElementType DEFINITION_TERM_MARKER = new MdTokenType("DEFINITION_TERM_MARKER");
    IElementType DEFINITION = new MdTokenType("DEFINITION");
    IElementType HRULE = new MdTokenType("HRULE");
    IElementType HRULE_TEXT = new MdTokenType("HRULE_TEXT");
    IElementType INLINE_HTML = new MdTokenType("INLINE_HTML");
    IElementType HTML_ENTITY = new MdTokenType("HTML_ENTITY");
    IElementType ITALIC = new MdTokenType("ITALIC");
    IElementType ITALIC_TEXT = new MdTokenType("ITALIC_TEXT");
    IElementType ITALIC_MARKER = new MdTokenType("ITALIC_MARKER");
    IElementType MAIL_LINK = new MdTokenType("MAIL_LINK");
    IElementType ORDERED_LIST = new MdTokenType("ORDERED_LIST");
    IElementType ORDERED_LIST_ITEM = new MdTokenType("ORDERED_LIST_ITEM");
    IElementType ORDERED_LIST_ITEM_MARKER = new MdTokenType("ORDERED_LIST_ITEM_MARKER");
    IElementType QUOTE = new MdTokenType("QUOTE");
    IElementType QUOTED_TEXT = new MdTokenType("QUOTED_TEXT");
    IElementType QUOTE_MARKER = new MdTokenType("QUOTE_MARKER");
    IElementType SMARTS = new MdTokenType("SMARTS");
    IElementType SPECIAL = new MdTokenType("SPECIAL");
    IElementType SPECIAL_TEXT = new MdTokenType("SPECIAL_TEXT");
    IElementType SPECIAL_TEXT_MARKER = new MdTokenType("SPECIAL_TEXT_MARKER");
    IElementType LINE_BREAK_SPACES = new MdTokenType("LINE_BREAK_SPACES");
    IElementType STRIKETHROUGH = new MdTokenType("STRIKETHROUGH");
    IElementType STRIKETHROUGH_TEXT = new MdTokenType("STRIKETHROUGH_TEXT");
    IElementType STRIKETHROUGH_MARKER = new MdTokenType("STRIKETHROUGH_MARKER");
    IElementType TABLE = new MdTokenType("TABLE");
    IElementType TABLE_BODY = new MdTokenType("TABLE_BODY");
    IElementType TABLE_HEADER = new MdTokenType("TABLE_HEADER");
    IElementType TABLE_ROW = new MdTokenType("TABLE_ROW");
    IElementType TABLE_CELL = new MdTokenType("TABLE_CELL");
    IElementType TABLE_CAPTION = new MdTokenType("TABLE_CAPTION");
    IElementType TABLE_CAPTION_TEXT = new MdTokenType("TABLE_CAPTION_TEXT");

    IElementType UNDERLINE = new MdTokenType("UNDERLINE");
    IElementType UNDERLINE_TEXT = new MdTokenType("UNDERLINE_TEXT");
    IElementType UNDERLINE_MARKER = new MdTokenType("UNDERLINE_MARKER");
    IElementType SUPERSCRIPT = new MdTokenType("SUPERSCRIPT");
    IElementType SUPERSCRIPT_TEXT = new MdTokenType("SUPERSCRIPT_TEXT");
    IElementType SUPERSCRIPT_MARKER = new MdTokenType("SUPERSCRIPT_MARKER");
    IElementType SUBSCRIPT = new MdTokenType("SUBSCRIPT");
    IElementType SUBSCRIPT_TEXT = new MdTokenType("SUBSCRIPT_TEXT");
    IElementType SUBSCRIPT_MARKER = new MdTokenType("SUBSCRIPT_MARKER");

    // GitLab Nodes
    IElementType GITLAB_DEL = new MdElementType("GITLAB_DEL");
    IElementType GITLAB_DEL_TEXT = STRIKETHROUGH_TEXT;
    IElementType GITLAB_DEL_MARKER = STRIKETHROUGH_MARKER;
    IElementType GITLAB_INS = new MdElementType("GITLAB_INS");
    IElementType GITLAB_INS_TEXT = UNDERLINE_TEXT;
    IElementType GITLAB_INS_MARKER = UNDERLINE_MARKER;
    IElementType GITLAB_MATH = new MdElementType("GITLAB_MATH");
    IElementType GITLAB_MATH_TEXT = new MdTokenType("GITLAB_MATH_TEXT");
    IElementType GITLAB_MATH_MARKER = new MdTokenType("GITLAB_MATH_MARKER");
    IElementType GITLAB_BLOCK_QUOTE = new MdElementType("GITLAB_BLOCK_QUOTE");
    IElementType GITLAB_BLOCK_QUOTE_MARKER = BLOCK_QUOTE_MARKER;
    IElementType GITLAB_BLOCK_QUOTE_INFO = new MdTokenType("GITLAB_BLOCK_QUOTE_INFO");

    IElementType TABLE_CAPTION_MARKER = new MdTokenType("TABLE_CAPTION_MARKER");
    IElementType TABLE_CELL_REVEN_CEVEN = new MdTokenType("TABLE_CELL_REVEN_CEVEN");
    IElementType TABLE_CELL_REVEN_CODD = new MdTokenType("TABLE_CELL_REVEN_CODD");
    IElementType TABLE_CELL_RODD_CEVEN = new MdTokenType("TABLE_CELL_RODD_CEVEN");
    IElementType TABLE_CELL_RODD_CODD = new MdTokenType("TABLE_CELL_RODD_CODD");
    IElementType TABLE_HDR_CELL_REVEN_CEVEN = new MdTokenType("TABLE_HDR_CELL_REVEN_CEVEN");
    IElementType TABLE_HDR_CELL_REVEN_CODD = new MdTokenType("TABLE_HDR_CELL_REVEN_CODD");
    IElementType TABLE_HDR_CELL_RODD_CEVEN = new MdTokenType("TABLE_HDR_CELL_RODD_CEVEN");
    IElementType TABLE_HDR_CELL_RODD_CODD = new MdTokenType("TABLE_HDR_CELL_RODD_CODD");
    IElementType TABLE_SEP_COLUMN_ODD = new MdTokenType("TABLE_SEP_COLUMN_ODD");
    IElementType TABLE_SEP_COLUMN_EVEN = new MdTokenType("TABLE_SEP_COLUMN_EVEN");
    IElementType TABLE_SEPARATOR = new MdTokenType("TABLE_SEPARATOR");
    IElementType TABLE_MARKER = new MdTokenType("TABLE_MARKER");
    IElementType TABLE_SEP_ROW_ODD = new MdTokenType("TABLE_SEP_ROW_ODD");
    IElementType TABLE_ROW_EVEN = new MdTokenType("TABLE_ROW_EVEN");
    IElementType TABLE_ROW_ODD = new MdTokenType("TABLE_ROW_ODD");
    IElementType TABLE_HDR_ROW_EVEN = new MdTokenType("TABLE_HDR_ROW_EVEN");
    IElementType TABLE_HDR_ROW_ODD = new MdTokenType("TABLE_HDR_ROW_ODD");
    IElementType TASK_ITEM = new MdTokenType("TASK_ITEM");
    IElementType TASK_DONE_ITEM = new MdTokenType("TASK_DONE_ITEM");
    IElementType TASK_ITEM_MARKER = new MdTokenType("TASK_ITEM_MARKER");
    IElementType TASK_DONE_ITEM_MARKER = new MdTokenType("TASK_DONE_ITEM_MARKER");
    IElementType TEXT = new MdTokenType("TEXT");
    IElementType PARAGRAPH_BLOCK = new MdTokenType("PARAGRAPH_BLOCK");
    IElementType TEXT_BLOCK = new MdTokenType("TEXT_BLOCK");

    IElementType VERBATIM_OPEN = new MdTokenType("VERBATIM_OPEN");
    IElementType VERBATIM_CLOSE = new MdTokenType("VERBATIM_CLOSE");
    IElementType VERBATIM_CONTENT = new MdTokenType("VERBATIM_CONTENT");

    IElementType WIKI_LINK_OPEN = new MdTokenType("WIKI_LINK_OPEN");
    IElementType WIKI_LINK_SEPARATOR = new MdTokenType("WIKI_LINK_SEPARATOR");
    IElementType WIKI_LINK_CLOSE = new MdTokenType("WIKI_LINK_CLOSE");
    IElementType WIKI_LINK_REF_ANCHOR_MARKER = new MdTokenType("WIKI_LINK_REF_ANCHOR_MARKER");

    IElementType IMAGE_LINK_REF_OPEN = new MdTokenType("IMAGE_LINK_REF_OPEN");
    IElementType IMAGE_LINK_REF_CLOSE = new MdTokenType("IMAGE_LINK_REF_CLOSE");
    IElementType IMAGE_LINK_REF_TITLE_MARKER = new MdTokenType("IMAGE_TITLE_MARKER");
    IElementType IMAGE_LINK_REF_TEXT_OPEN = new MdTokenType("IMAGE_LINK_REF_TEXT_OPEN");
    IElementType IMAGE_LINK_REF_TEXT_CLOSE = new MdTokenType("IMAGE_LINK_REF_TEXT_CLOSE");

    IElementType LINK_REF_OPEN = new MdTokenType("LINK_REF_OPEN");
    IElementType LINK_REF_CLOSE = new MdTokenType("LINK_REF_CLOSE");
    IElementType LINK_REF_TEXT_OPEN = new MdTokenType("LINK_REF_TEXT_OPEN");
    IElementType LINK_REF_TEXT_CLOSE = new MdTokenType("LINK_REF_TEXT_CLOSE");
    IElementType LINK_REF_TITLE_MARKER = new MdTokenType("LINK_REF_TITLE_MARKER");
    IElementType LINK_REF_ANCHOR_MARKER = new MdTokenType("LINK_REF_ANCHOR_MARKER");

    IElementType REFERENCE_TEXT_OPEN = new MdTokenType("REFERENCE_TEXT_OPEN");
    IElementType REFERENCE_TEXT_CLOSE = new MdTokenType("REFERENCE_TEXT_CLOSE");
    IElementType REFERENCE_TITLE_MARKER = new MdTokenType("REFERENCE_TITLE_MARKER");
    IElementType REFERENCE_ANCHOR_MARKER = new MdTokenType("REFERENCE_ANCHOR_MARKER");

    IElementType REFERENCE_IMAGE_TEXT_OPEN = new MdTokenType("REFERENCE_IMAGE_TEXT_OPEN");
    IElementType REFERENCE_IMAGE_TEXT_CLOSE = new MdTokenType("REFERENCE_IMAGE_TEXT_CLOSE");
    IElementType REFERENCE_IMAGE_REFERENCE_OPEN = new MdTokenType("REFERENCE_IMAGE_REFERENCE_OPEN");
    IElementType REFERENCE_IMAGE_REFERENCE_CLOSE = new MdTokenType("REFERENCE_IMAGE_REFERENCE_CLOSE");
    IElementType REFERENCE_IMAGE_REFERENCE_OPEN2 = new MdTokenType("REFERENCE_IMAGE_REFERENCE_OPEN2");
    IElementType REFERENCE_IMAGE_REFERENCE_CLOSE2 = new MdTokenType("REFERENCE_IMAGE_REFERENCE_CLOSE2");

    IElementType REFERENCE_LINK_TEXT_OPEN = new MdTokenType("REFERENCE_LINK_TEXT_OPEN");
    IElementType REFERENCE_LINK_TEXT_CLOSE = new MdTokenType("REFERENCE_LINK_TEXT_CLOSE");
    IElementType REFERENCE_LINK_REFERENCE_OPEN = new MdTokenType("REFERENCE_LINK_REFERENCE_OPEN");
    IElementType REFERENCE_LINK_REFERENCE_CLOSE = new MdTokenType("REFERENCE_LINK_REFERENCE_CLOSE");
    IElementType REFERENCE_LINK_REFERENCE_OPEN2 = new MdTokenType("REFERENCE_LINK_REFERENCE_OPEN2");
    IElementType REFERENCE_LINK_REFERENCE_CLOSE2 = new MdTokenType("REFERENCE_LINK_REFERENCE_CLOSE2");
    IElementType DUMMY_REFERENCE = new MdTokenType("DUMMY_REFERENCE");

    IElementType HEADER_ATX_MARKER = new MdTokenType("HEADER_ATX_MARKER");
    IElementType HEADER_SETEXT_MARKER = new MdTokenType("HEADER_SETEXT_MARKER");

    IElementType FOOTNOTE_OPEN = new MdTokenType("FOOTNOTE_OPEN");
    IElementType FOOTNOTE_CLOSE = new MdTokenType("FOOTNOTE_CLOSE");
    IElementType FOOTNOTE_TEXT = new MdElementType("FOOTNOTE_TEXT");
    IElementType FOOTNOTE_REF_OPEN = new MdTokenType("FOOTNOTE_REF_OPEN");
    IElementType FOOTNOTE_REF_CLOSE = new MdTokenType("FOOTNOTE_REF_CLOSE");
    IElementType FOOTNOTE = new MdElementType("FOOTNOTE");
    IElementType FOOTNOTE_REF = new MdElementType("FOOTNOTE_REF");
    IElementType FOOTNOTE_ID = new MdElementType("FOOTNOTE_ID");
    IElementType FOOTNOTE_REF_ID = new MdElementType("FOOTNOTE_REF_ID");
    IElementType FOOTNOTE_REF_ID_LEAF = new MdElementType("FOOTNOTE_REF_ID_LEAF");
    //IElementType FOOTNOTE_END = new MultiMarkdownElementType("FOOTNOTE_END");

    IElementType MACRO_OPEN = new MdTokenType("MACRO_OPEN");
    IElementType MACRO_CLOSE = new MdTokenType("MACRO_CLOSE");
    IElementType MACRO_TEXT = new MdElementType("MACRO_TEXT");
    IElementType MACRO_REF_OPEN = new MdTokenType("MACRO_REF_OPEN");
    IElementType MACRO_REF_CLOSE = new MdTokenType("MACRO_REF_CLOSE");
    IElementType MACRO = new MdElementType("MACRO");
    IElementType MACRO_REF = new MdElementType("MACRO_REF");
    IElementType MACRO_ID = new MdElementType("MACRO_ID");
    IElementType MACRO_REF_ID = new MdElementType("MACRO_REF_ID");
    IElementType MACRO_REF_ID_LEAF = new MdElementType("MACRO_REF_ID_LEAF");

    // attribute and enumerated references
    IElementType ATTRIBUTES = new MdElementType("ATTRIBUTES");
    IElementType ATTRIBUTES_OPEN = new MdTokenType("ATTRIBUTES_OPEN");
    IElementType ATTRIBUTE = new MdElementType("ATTRIBUTE");
    IElementType ATTRIBUTE_NAME = new MdTokenType("ATTRIBUTE_NAME");
    IElementType ATTRIBUTE_NAME_ID = new MdTokenType("ATTRIBUTE_NAME_ID"); // implicit id name: #
    IElementType ATTRIBUTE_NAME_CLASS = new MdTokenType("ATTRIBUTE_NAME_CLASS"); // implicit class name: .
    IElementType ATTRIBUTE_VALUE_SEP = new MdTokenType("ATTRIBUTE_VALUE_SEP");
    IElementType ATTRIBUTE_VALUE_OPEN = new MdTokenType("ATTRIBUTE_VALUE_OPEN");
    IElementType ATTRIBUTE_VALUE = new MdTokenType("ATTRIBUTE_VALUE");
    IElementType ATTRIBUTE_ID_VALUE = new MdElementType("ATTRIBUTE_ID_VALUE");
    IElementType ATTRIBUTE_VALUE_CLOSE = new MdTokenType("ATTRIBUTE_VALUE_CLOSE");
    IElementType ATTRIBUTES_CLOSE = new MdTokenType("ATTRIBUTES_CLOSE");

    IElementType ENUM_REF_FORMAT = new MdElementType("ENUM_REF_FORMAT");
    IElementType ENUM_REF_FORMAT_OPEN = new MdTokenType("ENUM_REF_FORMAT_OPEN");
    IElementType ENUM_REF_FORMAT_TYPE = new MdElementType("ENUM_REF_FORMAT_TYPE");
    IElementType ENUM_REF_FORMAT_CLOSE = new MdTokenType("ENUM_REF_FORMAT_CLOSE");
    IElementType ENUM_REF_FORMAT_TEXT = new MdElementType("ENUM_REF_FORMAT_TEXT");
    IElementType ENUM_REF_LINK = new MdElementType("ENUM_REF_LINK");
    IElementType ENUM_REF_LINK_OPEN = new MdTokenType("ENUM_REF_LINK_OPEN");
    IElementType ENUM_REF_LINK_CLOSE = new MdTokenType("ENUM_REF_LINK_CLOSE");
    IElementType ENUM_REF_TEXT = new MdElementType("ENUM_REF_TEXT");
    IElementType ENUM_REF_TEXT_OPEN = new MdTokenType("ENUM_REF_TEXT_OPEN");
    IElementType ENUM_REF_TEXT_CLOSE = new MdTokenType("ENUM_REF_TEXT_CLOSE");
    IElementType ENUM_REF_ID = new MdElementType("ENUM_REF_ID");

    IElementType ADMONITION_BLOCK = new MdElementType("ADMONITION_BLOCK");
    IElementType ADMONITION_MARKER = new MdTokenType("ADMONITION_MARKER");
    IElementType ADMONITION_INFO = new MdTokenType("ADMONITION_INFO");
    IElementType ADMONITION_TITLE = new MdTokenType("ADMONITION_TITLE");

    IElementType TOC_OPEN = new MdTokenType("TOC_OPEN");
    IElementType TOC_CLOSE = new MdTokenType("TOC_CLOSE");
    IElementType TOC_OPTION = new MdTokenType("TOC_OPTION");

    // parseable and instantiatable Element types
    IElementType TOC = new MdElementType("TOC");
    IElementType TOC_KEYWORD = new MdElementType("TOC_KEYWORD");

    IElementType HEADER_TEXT = new MdElementType("HEADER_TEXT");
    IElementType SETEXT_HEADER = new MdElementType("SETEXT_HEADER");
    IElementType ATX_HEADER = new MdElementType("ATX_HEADER");

    IElementType HTML_BLOCK = new MdElementType("HTML_BLOCK");
    IElementType HTML_BLOCK_TEXT = new MdElementType("HTML_BLOCK_TEXT");

    MdJekyllFrontMatterBlockStubElementType JEKYLL_FRONT_MATTER_BLOCK_ELEM = new MdJekyllFrontMatterBlockStubElementType("JEKYLL_FRONT_MATTER_BLOCK_ELEM");
    IElementType JEKYLL_FRONT_MATTER_BLOCK = new MdElementType("JEKYLL_FRONT_MATTER_BLOCK");
    IElementType JEKYLL_FRONT_MATTER_OPEN = new MdElementType("JEKYLL_FRONT_MATTER_OPEN");
    IElementType JEKYLL_FRONT_MATTER_CLOSE = new MdElementType("JEKYLL_FRONT_MATTER_CLOSE");

    MdJekyllIncludeStubType JEKYLL_INCLUDE_TAG_ELEM = new MdJekyllIncludeStubType("JEKYLL_INCLUDE_TAG_ELEM");
    IElementType JEKYLL_TAG_BLOCK_ELEM = new MdElementType("JEKYLL_TAG_BLOCK_ELEM");
    IElementType JEKYLL_TAG_NAME = new MdElementType("JEKYLL_TAG_NAME");
    IElementType JEKYLL_INCLUDE_TAG_LINK_REF = new MdElementType("JEKYLL_INCLUDE_TAG_LINK_REF");
    IElementType JEKYLL_TAG_PARAMETERS = new MdElementType("JEKYLL_TAG_PARAMETERS");
    IElementType JEKYLL_TAG_OPEN = new MdElementType("JEKYLL_TAG_OPEN");
    IElementType JEKYLL_TAG_CLOSE = new MdElementType("JEKYLL_TAG_CLOSE");
    IElementType JEKYLL_LINKREF_OPEN = new MdElementType("JEKYLL_LINKREF_OPEN");
    IElementType JEKYLL_LINKREF_CLOSE = new MdElementType("JEKYLL_LINKREF_CLOSE");

    FlexmarkFrontMatterBlockStubElementType FLEXMARK_FRONT_MATTER_BLOCK_ELEM = new FlexmarkFrontMatterBlockStubElementType("FLEXMARK_FRONT_MATTER_BLOCK_ELEM");
    IElementType FLEXMARK_FRONT_MATTER_BLOCK = new MdElementType("FLEXMARK_FRONT_MATTER_BLOCK");
    IElementType FLEXMARK_FRONT_MATTER_OPEN = new MdElementType("FLEXMARK_FRONT_MATTER_OPEN");
    IElementType FLEXMARK_FRONT_MATTER_CLOSE = new MdElementType("FLEXMARK_FRONT_MATTER_CLOSE");

    // these are stub types
    FlexmarkExampleStubElementType FLEXMARK_EXAMPLE = new FlexmarkExampleStubElementType("FLEXMARK_EXAMPLE"); //new MultiMarkdownElementType("FLEXMARK_EXAMPLE");
    FlexmarkExampleOptionsStubElementType FLEXMARK_EXAMPLE_OPTIONS = new FlexmarkExampleOptionsStubElementType("FLEXMARK_EXAMPLE_OPTIONS"); //new MultiMarkdownElementType("FLEXMARK_EXAMPLE_OPTIONS");
    FlexmarkExampleOptionStubElementType FLEXMARK_EXAMPLE_OPTION = new FlexmarkExampleOptionStubElementType("FLEXMARK_EXAMPLE_OPTION");

    IElementType FLEXMARK_EXAMPLE_OPTION_TYPE = new MdElementType("FLEXMARK_EXAMPLE_OPTION_TYPE");
    IElementType FLEXMARK_EXAMPLE_OPEN = new MdElementType("FLEXMARK_EXAMPLE_OPEN");
    IElementType FLEXMARK_EXAMPLE_CLOSE = new MdElementType("FLEXMARK_EXAMPLE_CLOSE");
    IElementType FLEXMARK_EXAMPLE_SECTION = new MdElementType("FLEXMARK_EXAMPLE_SECTION");
    IElementType FLEXMARK_EXAMPLE_SECTION_OPEN = new MdElementType("FLEXMARK_EXAMPLE_SECTION_OPEN");
    IElementType FLEXMARK_EXAMPLE_NUMBER_SEPARATOR = new MdElementType("FLEXMARK_EXAMPLE_NUMBER_SEPARATOR");
    IElementType FLEXMARK_EXAMPLE_SECTION_CLOSE = new MdElementType("FLEXMARK_EXAMPLE_SECTION_CLOSE");
    IElementType FLEXMARK_EXAMPLE_NUMBER = new MdElementType("FLEXMARK_EXAMPLE_NUMBER");
    IElementType FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD = new MdElementType("FLEXMARK_EXAMPLE_EXAMPLE_KEYWORD");
    IElementType FLEXMARK_EXAMPLE_OPTIONS_KEYWORD = new MdElementType("FLEXMARK_EXAMPLE_OPTIONS_KEYWORD");
    IElementType FLEXMARK_EXAMPLE_OPTIONS_OPEN = new MdElementType("FLEXMARK_EXAMPLE_OPTIONS_OPEN");
    IElementType FLEXMARK_EXAMPLE_OPTIONS_CLOSE = new MdElementType("FLEXMARK_EXAMPLE_OPTIONS_CLOSE");
    IElementType FLEXMARK_EXAMPLE_OPTION_SEPARATOR = new MdElementType("FLEXMARK_EXAMPLE_OPTION_SEPARATOR");
    IElementType FLEXMARK_EXAMPLE_SEPARATOR = new MdElementType("FLEXMARK_EXAMPLE_SEPARATOR");
    IElementType FLEXMARK_EXAMPLE_SOURCE = new MdElementType("FLEXMARK_EXAMPLE_SOURCE");
    IElementType FLEXMARK_EXAMPLE_HTML = new MdElementType("FLEXMARK_EXAMPLE_HTML");
    IElementType FLEXMARK_EXAMPLE_AST = new MdElementType("FLEXMARK_EXAMPLE_AST");
    IElementType FLEXMARK_EXAMPLE_OPTION_NAME = new MdElementType("FLEXMARK_EXAMPLE_OPTION_NAME");
    IElementType FLEXMARK_EXAMPLE_OPTION_PARAM_OPEN = new MdElementType("FLEXMARK_EXAMPLE_OPTION_PARAM_OPEN");
    IElementType FLEXMARK_EXAMPLE_OPTION_PARAM = new MdElementType("FLEXMARK_EXAMPLE_OPTION_PARAM");
    IElementType FLEXMARK_EXAMPLE_OPTION_PARAM_CLOSE = new MdElementType("FLEXMARK_EXAMPLE_OPTION_PARAM_CLOSE");
    IElementType FLEXMARK_EXAMPLE_OPTION_IGNORE_NAME = new MdElementType("FLEXMARK_EXAMPLE_OPTION_IGNORE_NAME");
    IElementType FLEXMARK_EXAMPLE_OPTION_FAIL_NAME = new MdElementType("FLEXMARK_EXAMPLE_OPTION_FAIL_NAME");
    IElementType FLEXMARK_EXAMPLE_OPTION_EMBED_TIMED_NAME = new MdElementType("FLEXMARK_EXAMPLE_OPTION_EMBED_TIMED_NAME");
    IElementType FLEXMARK_EXAMPLE_OPTION_TIMED_NAME = new MdElementType("FLEXMARK_EXAMPLE_OPTION_TIMED_NAME");
    IElementType FLEXMARK_EXAMPLE_OPTION_NO_FILE_EOL_NAME = new MdElementType("FLEXMARK_EXAMPLE_OPTION_NO_FILE_EOL_NAME");
    IElementType FLEXMARK_EXAMPLE_OPTION_FILE_EOL_NAME = new MdElementType("FLEXMARK_EXAMPLE_OPTION_FILE_EOL_NAME");
    IElementType FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME = new MdElementType("FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME");

    IElementType VERBATIM = new MdElementType("VERBATIM");
    IElementType VERBATIM_LANG = new MdElementType("VERBATIM_LANG");

    MdWikiLinkStubElementType WIKI_LINK = new MdWikiLinkStubElementType("WIKI_LINK");
    //MultiMarkdownWikiLinkRefStubElementType WIKI_LINK_REF = new MultiMarkdownWikiLinkRefStubElementType("WIKI_LINK_REF");
    IElementType WIKI_LINK_REF = new MdElementType("WIKI_LINK_REF");
    IElementType WIKI_LINK_REF_ANCHOR = new MdElementType("WIKI_LINK_REF_ANCHOR");
    IElementType WIKI_LINK_TEXT = new MdElementType("WIKI_LINK_TEXT");

    //IElementType IMAGE = new MultiMarkdownElementType("IMAGE");
    MdImageLinkStubElementType IMAGE = new MdImageLinkStubElementType("IMAGE");
    IElementType IMAGE_LINK_REF = new MdElementType("IMAGE_LINK_REF");
    IElementType IMAGE_LINK_REF_TITLE = new MdElementType("IMAGE_TITLE");
    IElementType IMAGE_LINK_REF_TEXT = new MdElementType("IMAGE_LINK_TEXT");
    IElementType IMAGE_URL_CONTENT = new MdTokenType("IMAGE_URL_CONTENT");

    //IElementType REFERENCE = new MultiMarkdownTokenType("REFERENCE");
    MdReferenceStubElementType REFERENCE = new MdReferenceStubElementType("REFERENCE");
    IElementType REFERENCE_LINK_REF = new MdElementType("REFERENCE_LINK_REF");
    IElementType REFERENCE_TITLE = new MdElementType("REFERENCE_TITLE");
    IElementType REFERENCE_TEXT = new MdElementType("REFERENCE_TEXT");
    IElementType REFERENCE_TEXT_LEAF = new MdElementType("REFERENCE_TEXT_LEAF");
    IElementType REFERENCE_ANCHOR = new MdElementType("REFERENCE_ANCHOR");
    IElementType REFERENCE_END = new MdElementType("REFERENCE_END");

    IElementType SIM_TOC_OPEN = new MdTokenType("SIM_TOC_OPEN");
    IElementType SIM_TOC = new MdTokenType("SIM_TOC");
    IElementType SIM_TOC_KEYWORD = new MdElementType("SIM_TOC_KEYWORD");
    IElementType SIM_TOC_OPTIONS = new MdElementType("SIM_TOC_OPTIONS");
    IElementType SIM_TOC_OPTION = new MdElementType("SIM_TOC_OPTION");
    IElementType SIM_TOC_CONTENT = new MdTokenType("SIM_TOC_CONTENT");
    IElementType SIM_TOC_CLOSE = new MdTokenType("SIM_TOC_CLOSE");
    IElementType SIM_TOC_HEADER_MARKERS = new MdTokenType("SIM_TOC_HEADER_MARKERS");
    IElementType SIM_TOC_TITLE_MARKER = new MdTokenType("SIM_TOC_TITLE_MARKER");
    IElementType SIM_TOC_TITLE = new MdElementType("SIM_TOC_TITLE");

    IElementType GEN_CONTENT = new MdTokenType("GEN_CONTENT");

    //IElementType EXPLICIT_LINK = new MultiMarkdownElementType("EXPLICIT_LINK");
    MdExplicitLinkStubElementType EXPLICIT_LINK = new MdExplicitLinkStubElementType("EXPLICIT_LINK");
    IElementType LINK_REF = new MdElementType("LINK_REF");
    IElementType LINK_REF_TEXT = new MdElementType("LINK_REF_TEXT");
    IElementType LINK_REF_TITLE = new MdElementType("LINK_REF_TITLE");
    IElementType LINK_REF_ANCHOR = new MdElementType("LINK_REF_ANCHOR");

    //IElementType AUTO_LINK = new MultiMarkdownElementType("AUTO_LINK");
    MdAutoLinkStubElementType AUTO_LINK = new MdAutoLinkStubElementType("AUTO_LINK");
    IElementType AUTO_LINK_ELEM = new MdElementType("AUTO_LINK_ELEM");
    IElementType AUTO_LINK_REF = new MdElementType("AUTO_LINK_REF");
    IElementType AUTO_LINK_ANCHOR = new MdElementType("AUTO_LINK_ANCHOR");

    IElementType AUTO_LINK_OPEN = new MdTokenType("AUTO_LINK_OPEN");
    IElementType AUTO_LINK_CLOSE = new MdTokenType("AUTO_LINK_CLOSE");
    IElementType AUTO_LINK_ANCHOR_MARKER = new MdTokenType("AUTO_LINK_ANCHOR_MARKER");

    IElementType REFERENCE_IMAGE = new MdTokenType("REFERENCE_IMAGE");
    IElementType REFERENCE_IMAGE_REFERENCE = new MdTokenType("REFERENCE_IMAGE_REFERENCE");
    IElementType REFERENCE_IMAGE_REFERENCE_LEAF = new MdTokenType("REFERENCE_IMAGE_REFERENCE_LEAF");
    IElementType REFERENCE_IMAGE_TEXT = new MdTokenType("REFERENCE_IMAGE_TEXT");

    IElementType REFERENCE_LINK = new MdTokenType("REFERENCE_LINK");
    IElementType REFERENCE_LINK_REFERENCE = new MdTokenType("REFERENCE_LINK_REFERENCE");
    IElementType REFERENCE_LINK_REFERENCE_LEAF = new MdTokenType("REFERENCE_LINK_REFERENCE_LEAF");
    IElementType REFERENCE_LINK_TEXT = new MdTokenType("REFERENCE_LINK_TEXT");

    IElementType COMMENT = new MdElementType("COMMENT");
    IElementType COMMENT_OPEN = new MdElementType("COMMENT_OPEN");
    IElementType COMMENT_CLOSE = new MdElementType("COMMENT_CLOSE");
    IElementType COMMENT_TEXT = new MdElementType("COMMENT_TEXT");
    IElementType BLOCK_COMMENT = new MdElementType("BLOCK_COMMENT");
    IElementType BLOCK_COMMENT_OPEN = new MdElementType("BLOCK_COMMENT_OPEN");
    IElementType BLOCK_COMMENT_CLOSE = new MdElementType("BLOCK_COMMENT_CLOSE");
    IElementType BLOCK_COMMENT_TEXT = new MdElementType("BLOCK_COMMENT_TEXT");

    IElementType ANCHOR = new MdElementType("ANCHOR");
    IElementType ANCHOR_MARK = new MdElementType("ANCHOR_MARK");
    IElementType ANCHOR_ID = new MdElementType("ANCHOR_ID");

    IElementType EMOJI = new MdElementType("EMOJI");
    IElementType EMOJI_MARKER = new MdElementType("EMOJI_MARKER");
    IElementType EMOJI_ID = new MdElementType("EMOJI_ID");

    IElementType MARKDOWN_OUTER_BLOCK = new MdElementType("MARKDOWN_OUTER_BLOCK");

//    TemplateDataElementType MARKDOWN_TEMPLATE_DATA =
//            new MdTemplateDataElementType("MARKDOWN_TEMPLATE_DATA", MdLanguage.INSTANCE, HTML_BLOCK_TEXT, MARKDOWN_OUTER_BLOCK);

    static void addTypeFactories(MdTypeFactoryRegistry resolver) {
        resolver.addTypeFactory(ABBREVIATED, MdAbbreviatedText.class, MdAbbreviatedTextImpl.class, MdAbbreviatedTextImpl::new);
        resolver.addTypeFactory(ABBREVIATED_TEXT, MdAbbreviationRefId.class, MdAbbreviationRefIdImpl.class, MdAbbreviationRefIdImpl::new);
        resolver.addTypeFactory(ABBREVIATION, MdAbbreviation.class, MdAbbreviationImpl.class, MdAbbreviationImpl::new);
        resolver.addTypeFactory(ABBREVIATION_EXPANDED_TEXT, MdAbbreviationText.class, MdAbbreviationTextImpl.class, MdAbbreviationTextImpl::new);
        resolver.addTypeFactory(ABBREVIATION_SHORT_TEXT, MdAbbreviationId.class, MdAbbreviationIdImpl.class, MdAbbreviationIdImpl::new);
        resolver.addTypeFactory(ADMONITION_BLOCK, MdAdmonition.class, MdAdmonitionImpl.class, MdAdmonitionImpl::new);
        resolver.addTypeFactory(ANCHOR, MdRefAnchor.class, MdRefAnchorImpl.class, MdRefAnchorImpl::new);
        resolver.addTypeFactory(ANCHOR_ID, MdRefAnchorId.class, MdRefAnchorIdImpl.class, MdRefAnchorIdImpl::new);
        resolver.addTypeFactory(ANCHOR_LINK, MdAnchorLink.class, MdAnchorLinkImpl.class, MdAnchorLinkImpl::new);
        resolver.addTypeFactory(ASIDE_BLOCK, MdAsideBlock.class, MdAsideBlockImpl.class, MdAsideBlockImpl::new);
        resolver.addTypeFactory(ATTRIBUTE, MdAttribute.class, MdAttributeImpl.class, MdAttributeImpl::new);
        resolver.addTypeFactory(ATTRIBUTE_ID_VALUE, MdAttributeIdValue.class, MdAttributeIdValueImpl.class, MdAttributeIdValueImpl::new);
        resolver.addTypeFactory(ATTRIBUTE_NAME, MdAttributeName.class, MdAttributeNameImpl.class, MdAttributeNameImpl::new);
        resolver.addTypeFactory(ATTRIBUTE_NAME_CLASS, MdAttributeNameImplicitClass.class, MdAttributeNameImplicitClassImpl.class, MdAttributeNameImplicitClassImpl::new);
        resolver.addTypeFactory(ATTRIBUTE_NAME_ID, MdAttributeNameImplicitId.class, MdAttributeNameImplicitIdImpl.class, MdAttributeNameImplicitIdImpl::new);
        resolver.addTypeFactory(ATTRIBUTE_VALUE, MdAttributeValue.class, MdAttributeValueImpl.class, MdAttributeValueImpl::new);
        resolver.addTypeFactory(ATTRIBUTES, MdAttributes.class, MdAttributesImpl.class, MdAttributesImpl::new);
        resolver.addTypeFactory(ATX_HEADER, MdAtxHeader.class, MdAtxHeaderImpl.class, MdAtxHeaderImpl::new);
        resolver.addTypeFactory(AUTO_LINK, MdAutoLink.class, MdAutoLinkImpl.class, MdAutoLinkImpl::new);
        resolver.addTypeFactory(AUTO_LINK_ANCHOR, MdAutoLinkAnchor.class, MdAutoLinkAnchorImpl.class, MdAutoLinkAnchorImpl::new);
        resolver.addTypeFactory(AUTO_LINK_REF, MdAutoLinkRef.class, MdAutoLinkRefImpl.class, MdAutoLinkRefImpl::new);
        resolver.addTypeFactory(BLANK_LINE, MdBlankLine.class, MdBlankLineImpl.class, MdBlankLineImpl::new);
        resolver.addTypeFactory(BLOCK_COMMENT, MdBlockComment.class, MdBlockCommentImpl.class, MdBlockCommentImpl::new);
        resolver.addTypeFactory(BLOCK_QUOTE, MdBlockQuote.class, MdBlockQuoteImpl.class, MdBlockQuoteImpl::new);
        resolver.addTypeFactory(BOLD, MdInlineBold.class, MdInlineBoldImpl.class, MdInlineBoldImpl::new);
        resolver.addTypeFactory(BULLET_LIST, MdUnorderedList.class, MdUnorderedListImpl.class, MdUnorderedListImpl::new);
        resolver.addTypeFactory(BULLET_LIST_ITEM, MdUnorderedListItem.class, MdUnorderedListItemImpl.class, MdUnorderedListItemImpl::new);
        resolver.addTypeFactory(CODE, MdInlineCode.class, MdInlineCodeImpl.class, MdInlineCodeImpl::new);
        resolver.addTypeFactory(COMMENT, MdInlineComment.class, MdInlineCommentImpl.class, MdInlineCommentImpl::new);
        resolver.addTypeFactory(DEFINITION, MdDefinition.class, MdDefinitionImpl.class, MdDefinitionImpl::new);
        resolver.addTypeFactory(DEFINITION_LIST, MdDefinitionList.class, MdDefinitionListImpl.class, MdDefinitionListImpl::new);
        resolver.addTypeFactory(DEFINITION_TERM_ELEMENT, MdDefinitionTerm.class, MdDefinitionTermImpl.class, MdDefinitionTermImpl::new);
        resolver.addTypeFactory(EMOJI, MdEmoji.class, MdEmojiImpl.class, MdEmojiImpl::new);
        resolver.addTypeFactory(EMOJI_ID, MdEmojiId.class, MdEmojiIdImpl.class, MdEmojiIdImpl::new);
        resolver.addTypeFactory(ENUM_REF_FORMAT, MdEnumeratedReferenceFormat.class, MdEnumeratedReferenceFormatImpl.class, MdEnumeratedReferenceFormatImpl::new);
        resolver.addTypeFactory(ENUM_REF_FORMAT_TEXT, MdEnumeratedReferenceFormatText.class, MdEnumeratedReferenceFormatTextImpl.class, MdEnumeratedReferenceFormatTextImpl::new);
        resolver.addTypeFactory(ENUM_REF_FORMAT_TYPE, MdEnumeratedReferenceFormatType.class, MdEnumeratedReferenceFormatTypeImpl.class, MdEnumeratedReferenceFormatTypeImpl::new);
        resolver.addTypeFactory(ENUM_REF_ID, MdEnumeratedReferenceId.class, MdEnumeratedReferenceIdImpl.class, MdEnumeratedReferenceIdImpl::new);
        resolver.addTypeFactory(ENUM_REF_LINK, MdEnumeratedReferenceLink.class, MdEnumeratedReferenceLinkImpl.class, MdEnumeratedReferenceLinkImpl::new);
        resolver.addTypeFactory(ENUM_REF_TEXT, MdEnumeratedReferenceText.class, MdEnumeratedReferenceTextImpl.class, MdEnumeratedReferenceTextImpl::new);
        resolver.addTypeFactory(EXPLICIT_LINK, MdExplicitLink.class, MdExplicitLinkImpl.class, MdExplicitLinkImpl::new);
        resolver.addTypeFactory(FLEXMARK_EXAMPLE, FlexmarkExample.class, FlexmarkExampleImpl.class, FlexmarkExampleImpl::new);
        resolver.addTypeFactory(FLEXMARK_EXAMPLE_AST, FlexmarkExampleAst.class, FlexmarkExampleAstImpl.class, FlexmarkExampleAstImpl::new);
        resolver.addTypeFactory(FLEXMARK_EXAMPLE_HTML, FlexmarkExampleHtml.class, FlexmarkExampleHtmlImpl.class, FlexmarkExampleHtmlImpl::new);
        resolver.addTypeFactory(FLEXMARK_EXAMPLE_OPTION, FlexmarkExampleOption.class, FlexmarkExampleOptionImpl.class, FlexmarkExampleOptionImpl::new);
        resolver.addTypeFactory(FLEXMARK_EXAMPLE_OPTION_NAME, FlexmarkExampleOptionName.class, FlexmarkExampleOptionNameImpl.class, FlexmarkExampleOptionNameImpl::new);
        resolver.addTypeFactory(FLEXMARK_EXAMPLE_OPTIONS, FlexmarkExampleOptions.class, FlexmarkExampleOptionsImpl.class, FlexmarkExampleOptionsImpl::new);
        resolver.addTypeFactory(FLEXMARK_EXAMPLE_SOURCE, FlexmarkExampleSource.class, FlexmarkExampleSourceImpl.class, FlexmarkExampleSourceImpl::new);
        resolver.addTypeFactory(FLEXMARK_FRONT_MATTER_BLOCK_ELEM, FlexmarkFrontMatterBlock.class, FlexmarkFrontMatterBlockImpl.class, FlexmarkFrontMatterBlockImpl::new);
        resolver.addTypeFactory(FOOTNOTE, MdFootnote.class, MdFootnoteImpl.class, MdFootnoteImpl::new);
        resolver.addTypeFactory(FOOTNOTE_ID, MdFootnoteId.class, MdFootnoteIdImpl.class, MdFootnoteIdImpl::new);
        resolver.addTypeFactory(FOOTNOTE_REF, MdFootnoteRef.class, MdFootnoteRefImpl.class, MdFootnoteRefImpl::new);
        resolver.addTypeFactory(FOOTNOTE_REF_ID, MdFootnoteRefId.class, MdFootnoteRefIdImpl.class, MdFootnoteRefIdImpl::new);
        resolver.addTypeFactory(FOOTNOTE_TEXT, MdFootnoteText.class, MdFootnoteTextImpl.class, MdFootnoteTextImpl::new);
        resolver.addTypeFactory(GITLAB_BLOCK_QUOTE, MdGitLabBlockQuote.class, MdGitLabBlockQuoteImpl.class, MdGitLabBlockQuoteImpl::new);
        resolver.addTypeFactory(GITLAB_DEL, MdInlineGitLabDel.class, MdInlineGitLabDelImpl.class, MdInlineGitLabDelImpl::new);
        resolver.addTypeFactory(GITLAB_INS, MdInlineGitLabIns.class, MdInlineGitLabInsImpl.class, MdInlineGitLabInsImpl::new);
        resolver.addTypeFactory(GITLAB_MATH, MdInlineGitLabMath.class, MdInlineGitLabMathImpl.class, MdInlineGitLabMathImpl::new);
        resolver.addTypeFactory(HEADER_TEXT, MdHeaderText.class, MdHeaderTextImpl.class, MdHeaderTextImpl::new);
        resolver.addTypeFactory(HRULE, MdHRule.class, MdHRuleImpl.class, MdHRuleImpl::new);
        resolver.addTypeFactory(HTML_BLOCK, MdHtmlBlock.class, MdHtmlBlockImpl.class, MdHtmlBlockImpl::new);
        resolver.addTypeFactory(HTML_ENTITY, MdInlineHtmlEntity.class, MdInlineHtmlEntityImpl.class, MdInlineHtmlEntityImpl::new);
        resolver.addTypeFactory(IMAGE, MdImageLink.class, MdImageLinkImpl.class, MdImageLinkImpl::new);
        resolver.addTypeFactory(IMAGE_LINK_REF, MdImageLinkRef.class, MdImageLinkRefImpl.class, MdImageLinkRefImpl::new);
        resolver.addTypeFactory(IMAGE_LINK_REF_TEXT, MdImageLinkText.class, MdImageLinkTextImpl.class, MdImageLinkTextImpl::new);
        resolver.addTypeFactory(IMAGE_LINK_REF_TITLE, MdImageLinkTitle.class, MdImageLinkTitleImpl.class, MdImageLinkTitleImpl::new);
        resolver.addTypeFactory(IMAGE_URL_CONTENT, MdImageMultiLineUrlContent.class, MdImageMultiLineUrlContentImpl.class, MdImageMultiLineUrlContentImpl::new);
        resolver.addTypeFactory(INLINE_HTML, MdInlineHtml.class, MdInlineHtmlImpl.class, MdInlineHtmlImpl::new);
        resolver.addTypeFactory(ITALIC, MdInlineItalic.class, MdInlineItalicImpl.class, MdInlineItalicImpl::new);
        resolver.addTypeFactory(JEKYLL_FRONT_MATTER_BLOCK_ELEM, MdJekyllFrontMatterBlock.class, MdJekyllFrontMatterBlockImpl.class, MdJekyllFrontMatterBlockImpl::new);
        resolver.addTypeFactory(JEKYLL_INCLUDE_TAG_ELEM, MdJekyllInclude.class, MdJekyllIncludeImpl.class, MdJekyllIncludeImpl::new);
        resolver.addTypeFactory(JEKYLL_INCLUDE_TAG_LINK_REF, MdJekyllIncludeLinkRef.class, MdJekyllIncludeLinkRefImpl.class, MdJekyllIncludeLinkRefImpl::new);
        resolver.addTypeFactory(LINE_BREAK_SPACES, MdInlineHardBreak.class, MdInlineHardBreakImpl.class, MdInlineHardBreakImpl::new);
        resolver.addTypeFactory(LINK_REF, MdLinkRef.class, MdLinkRefImpl.class, MdLinkRefImpl::new);
        resolver.addTypeFactory(LINK_REF_ANCHOR, MdLinkAnchor.class, MdLinkAnchorImpl.class, MdLinkAnchorImpl::new);
        resolver.addTypeFactory(LINK_REF_TEXT, MdLinkText.class, MdLinkTextImpl.class, MdLinkTextImpl::new);
        resolver.addTypeFactory(LINK_REF_TITLE, MdLinkTitle.class, MdLinkTitleImpl.class, MdLinkTitleImpl::new);
        resolver.addTypeFactory(MACRO, MdMacro.class, MdMacroImpl.class, MdMacroImpl::new);
        resolver.addTypeFactory(MACRO_ID, MdMacroId.class, MdMacroIdImpl.class, MdMacroIdImpl::new);
        resolver.addTypeFactory(MACRO_REF, MdMacroRef.class, MdMacroRefImpl.class, MdMacroRefImpl::new);
        resolver.addTypeFactory(MACRO_REF_ID, MdMacroRefId.class, MdMacroRefIdImpl.class, MdMacroRefIdImpl::new);
        resolver.addTypeFactory(MACRO_TEXT, MdMacroText.class, MdMacroTextImpl.class, MdMacroTextImpl::new);
        resolver.addTypeFactory(MAIL_LINK, MdInlineMailLink.class, MdInlineMailLinkImpl.class, MdInlineMailLinkImpl::new);
        resolver.addTypeFactory(ORDERED_LIST, MdOrderedList.class, MdOrderedListImpl.class, MdOrderedListImpl::new);
        resolver.addTypeFactory(ORDERED_LIST_ITEM, MdOrderedListItem.class, MdOrderedListItemImpl.class, MdOrderedListItemImpl::new);
        resolver.addTypeFactory(PARAGRAPH_BLOCK, MdParagraph.class, MdParagraphImpl.class, MdParagraphImpl::new);
        resolver.addTypeFactory(QUOTE, MdInlineQuoted.class, MdInlineQuotedImpl.class, MdInlineQuotedImpl::new);
        resolver.addTypeFactory(REFERENCE, MdReference.class, MdReferenceImpl.class, MdReferenceImpl::new);
        resolver.addTypeFactory(REFERENCE_ANCHOR, MdReferenceAnchor.class, MdReferenceAnchorImpl.class, MdReferenceAnchorImpl::new);
        resolver.addTypeFactory(REFERENCE_IMAGE, MdReferenceImage.class, MdReferenceImageImpl.class, MdReferenceImageImpl::new);
        resolver.addTypeFactory(REFERENCE_IMAGE_REFERENCE, MdReferenceImageReference.class, MdReferenceImageReferenceImpl.class, MdReferenceImageReferenceImpl::new);
        resolver.addTypeFactory(REFERENCE_IMAGE_TEXT, MdReferenceImageText.class, MdReferenceImageTextImpl.class, MdReferenceImageTextImpl::new);
        resolver.addTypeFactory(REFERENCE_LINK, MdReferenceLink.class, MdReferenceLinkImpl.class, MdReferenceLinkImpl::new);
        resolver.addTypeFactory(REFERENCE_LINK_REF, MdReferenceLinkRef.class, MdReferenceLinkRefImpl.class, MdReferenceLinkRefImpl::new);
        resolver.addTypeFactory(REFERENCE_LINK_REFERENCE, MdReferenceLinkReference.class, MdReferenceLinkReferenceImpl.class, MdReferenceLinkReferenceImpl::new);
        resolver.addTypeFactory(REFERENCE_LINK_TEXT, MdReferenceLinkText.class, MdReferenceLinkTextImpl.class, MdReferenceLinkTextImpl::new);
        resolver.addTypeFactory(REFERENCE_TEXT, MdReferenceIdentifier.class, MdReferenceIdentifierImpl.class, MdReferenceIdentifierImpl::new);
        resolver.addTypeFactory(REFERENCE_TITLE, MdReferenceTitle.class, MdReferenceTitleImpl.class, MdReferenceTitleImpl::new);
        resolver.addTypeFactory(SETEXT_HEADER, MdSetextHeader.class, MdSetextHeaderImpl.class, MdSetextHeaderImpl::new);
        resolver.addTypeFactory(SIM_TOC, MdSimToc.class, MdSimTocImpl.class, MdSimTocImpl::new);
        resolver.addTypeFactory(SIM_TOC_CONTENT, MdSimTocContent.class, MdSimTocContentImpl.class, MdSimTocContentImpl::new);
        resolver.addTypeFactory(SMARTS, MdInlineTypographic.class, MdInlineTypographicImpl.class, MdInlineTypographicImpl::new);
        resolver.addTypeFactory(SPECIAL, MdInlineSpecialText.class, MdInlineSpecialTextImpl.class, MdInlineSpecialTextImpl::new);
        resolver.addTypeFactory(STRIKETHROUGH, MdInlineStrikethrough.class, MdInlineStrikethroughImpl.class, MdInlineStrikethroughImpl::new);
        resolver.addTypeFactory(SUBSCRIPT, MdInlineSubscript.class, MdInlineSubscriptImpl.class, MdInlineSubscriptImpl::new);
        resolver.addTypeFactory(SUPERSCRIPT, MdInlineSuperscript.class, MdInlineSuperscriptImpl.class, MdInlineSuperscriptImpl::new);
        resolver.addTypeFactory(TABLE, MdTable.class, MdTableImpl.class, MdTableImpl::new);
        resolver.addTypeFactory(TABLE_BODY, MdTableBody.class, MdTableBodyImpl.class, MdTableBodyImpl::new);
        resolver.addTypeFactory(TABLE_CAPTION, MdTableCaption.class, MdTableCaptionImpl.class, MdTableCaptionImpl::new);
        resolver.addTypeFactory(TABLE_CELL, MdTableCell.class, MdTableCellImpl.class, MdTableCellImpl::new);
        resolver.addTypeFactory(TABLE_HEADER, MdTableHeader.class, MdTableHeaderImpl.class, MdTableHeaderImpl::new);
        resolver.addTypeFactory(TABLE_ROW, MdTableRow.class, MdTableRowImpl.class, MdTableRowImpl::new);
        resolver.addTypeFactory(TABLE_SEPARATOR, MdTableSeparator.class, MdTableSeparatorImpl.class, MdTableSeparatorImpl::new);
        resolver.addTypeFactory(TEXT_BLOCK, MdTextBlock.class, MdTextBlockImpl.class, MdTextBlockImpl::new);
        resolver.addTypeFactory(TOC, MdToc.class, MdTocImpl.class, MdTocImpl::new);
        resolver.addTypeFactory(UNDERLINE, MdInlineUnderline.class, MdInlineUnderlineImpl.class, MdInlineUnderlineImpl::new);
        resolver.addTypeFactory(VERBATIM, MdVerbatim.class, MdVerbatimImpl.class, MdVerbatimImpl::new);
        resolver.addTypeFactory(VERBATIM_CONTENT, MdVerbatimContent.class, MdVerbatimContentImpl.class, MdVerbatimContentImpl::new);
        resolver.addTypeFactory(VERBATIM_LANG, MdVerbatimLanguage.class, MdVerbatimLanguageImpl.class, MdVerbatimLanguageImpl::new);
        resolver.addTypeFactory(WIKI_LINK, MdWikiLink.class, MdWikiLinkImpl.class, MdWikiLinkImpl::new);
        resolver.addTypeFactory(WIKI_LINK_REF, MdWikiLinkRef.class, MdWikiLinkRefImpl.class, MdWikiLinkRefImpl::new);
        resolver.addTypeFactory(WIKI_LINK_REF_ANCHOR, MdWikiLinkAnchor.class, MdWikiLinkAnchorImpl.class, MdWikiLinkAnchorImpl::new);
        resolver.addTypeFactory(WIKI_LINK_TEXT, MdWikiLinkText.class, MdWikiLinkTextImpl.class, MdWikiLinkTextImpl::new);
    }
}
