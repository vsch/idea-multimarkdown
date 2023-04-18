// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings

import com.vladsch.md.nav.parser.MdLexParser
import com.vladsch.md.nav.util.LongBitSetEnum

enum class ParserOptions(val flags: Long) {
    JEKYLL_FRONT_MATTER(MdLexParser.JEKYLL_FRONT_MATTER),
    GITHUB_WIKI_LINKS(MdLexParser.GITHUB_WIKI_LINKS), // github wiki link format
    EMOJI_SHORTCUTS(MdLexParser.EMOJI_SHORTCUTS), // translate emoji
    GITBOOK_URL_ENCODING(MdLexParser.GITBOOK_URL_ENCODING), // exclude + from url encode/decode
    SIM_TOC_BLANK_LINE_SPACER(MdLexParser.SIM_TOC_BLANK_LINE_SPACER), // add blank line after toc element
    GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA(MdLexParser.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA), // use github loose item rules
    FLEXMARK_FRONT_MATTER(MdLexParser.FLEXMARK_FRONT_MATTER),
    GFM_TABLE_RENDERING(MdLexParser.GFM_TABLE_RENDERING), // gfm rules for table rendering
    GITHUB_LISTS(MdLexParser.GITHUB_LISTS), // fixed rules for list indentations
    COMMONMARK_LISTS(MdLexParser.COMMONMARK_LISTS), // commonmark rules for list indentations
    ATTRIBUTES_EXT(MdLexParser.ATTRIBUTES_EXT), // attributes extension
    ENUMERATED_REFERENCES_EXT(MdLexParser.ENUMERATED_REFERENCES_EXT), // enumerated references ext
    HEADER_ID_NO_DUPED_DASHES(MdLexParser.HEADER_ID_NO_DUPED_DASHES),
    PARSE_HTML_ANCHOR_ID(MdLexParser.PARSE_HTML_ANCHOR_ID),
    NO_TEXT_ATTRIBUTES(MdLexParser.NO_TEXT_ATTRIBUTES),
    ADMONITION_EXT(MdLexParser.ADMONITION_EXT),
    GITLAB_EXT(MdLexParser.GITLAB_EXT),
    GITLAB_MATH_EXT(MdLexParser.GITLAB_MATH_EXT),
    GITLAB_MERMAID_EXT(MdLexParser.GITLAB_MERMAID_EXT),
    MACROS_EXT(MdLexParser.MACROS_EXT),
    HEADER_ID_NON_ASCII_TO_LOWERCASE(MdLexParser.HEADER_ID_NON_ASCII_TO_LOWERCASE),
    SPACE_IN_LINK_URLS(MdLexParser.SPACE_IN_LINK_URLS),
    PRODUCTION_SPEC_PARSER(MdLexParser.PRODUCTION_SPEC_PARSER), // internal test, even when running tests use production spec parser instead of test spec parser
    HEADER_ID_REF_TEXT_TRIM_TRAILING_SPACES(MdLexParser.HEADER_ID_REF_TEXT_TRIM_TRAILING_SPACES), // trim trailing spaces in ref text in headings used for heading id
    DUMMY(0L), // dummy place holder

    @Deprecated("No longer used, migration purposes only")
    PUML_FENCED_CODE(MdLexParser.PUML_FENCED_CODE),

    @Deprecated("No longer used, migration purposes only")
    PLANTUML_FENCED_CODE(MdLexParser.PLANTUML_FENCED_CODE);
    
    companion object : LongBitSetEnum<ParserOptions>(ParserOptions::class.java, { it.flags })
}
