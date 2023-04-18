// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings

import com.vladsch.md.nav.parser.Extensions
import com.vladsch.md.nav.util.BitSetEnum

enum class PegdownExtensions(val flags: Int) {
    ABBREVIATIONS(Extensions.ABBREVIATIONS),
    ANCHORLINKS(Extensions.ANCHORLINKS),
    ASIDE(Extensions.ASIDE),
    AUTOLINKS(Extensions.AUTOLINKS),
    DEFINITIONS(Extensions.DEFINITIONS),
    FENCED_CODE_BLOCKS(Extensions.FENCED_CODE_BLOCKS),
    SUBSCRIPT(Extensions.SUBSCRIPT),
    HARDWRAPS(Extensions.HARDWRAPS),
    ATXHEADERSPACE(Extensions.ATXHEADERSPACE),
    QUOTES(Extensions.QUOTES),
    RELAXEDHRULES(Extensions.RELAXEDHRULES),
    SMARTS(Extensions.SMARTS),
    STRIKETHROUGH(Extensions.STRIKETHROUGH),
    SUPPRESS_HTML_BLOCKS(Extensions.SUPPRESS_HTML_BLOCKS),
    SUPPRESS_INLINE_HTML(Extensions.SUPPRESS_INLINE_HTML),
    TABLES(Extensions.TABLES),
    TASKLISTITEMS(Extensions.TASKLISTITEMS),
    WIKILINKS(Extensions.WIKILINKS),
    FOOTNOTES(Extensions.FOOTNOTES),
    SUPERSCRIPT(Extensions.SUPERSCRIPT),
    INSERTED(Extensions.INSERTED),
    TOC(Extensions.TOC),

    // extra flags
    HTML_DEEP_PARSER(Extensions.HTML_DEEP_PARSER),
    DEFINITION_BREAK_DOUBLE_BLANK_LINE(Extensions.DEFINITION_BREAK_DOUBLE_BLANK_LINE),
    EXTANCHORLINKS(Extensions.EXTANCHORLINKS),
    MULTI_LINE_IMAGE_URLS(Extensions.MULTI_LINE_IMAGE_URLS),
    INTELLIJ_DUMMY_IDENTIFIER(Extensions.INTELLIJ_DUMMY_IDENTIFIER),
    ;

    companion object : BitSetEnum<PegdownExtensions>(PegdownExtensions::class.java, { it.flags })
}
