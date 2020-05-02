// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.testUtil;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKeyAggregator;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.md.nav.parser.Extensions;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public class MdEnhSpecTestSetup extends MdSpecTestSetup {
    private final static SettingsKeyAggregator INSTANCE = new SettingsKeyAggregator();
    static {
        DataSet.registerDataKeyAggregator(INSTANCE);
    }

    private static class SettingsKeyAggregator implements DataKeyAggregator {
        SettingsKeyAggregator() {}

        @NotNull
        @Override
        public DataHolder aggregate(@NotNull DataHolder combined) {
            return combined;
        }

        @NotNull
        @Override
        public DataHolder aggregateActions(@NotNull DataHolder combined, @NotNull DataHolder other, @NotNull DataHolder overrides) {
            return combined;
        }

        @NotNull
        @Override
        public DataHolder clean(DataHolder combined) {
            return combined;
        }

        @Nullable
        @Override
        public Set<Class<?>> invokeAfterSet() {
            return null;
        }
    }

    final public static Consumer<MdRenderingProfile> ENHANCED_OPTIONS = profile -> {
        //noinspection PointlessBitwiseExpression
        profile.getParserSettings().setPegdownFlags(0
                        | Extensions.ABBREVIATIONS
//                        | Extensions.ASIDE
                        | Extensions.ATXHEADERSPACE
                        | Extensions.AUTOLINKS
                        | Extensions.DEFINITIONS
                        | Extensions.EXTANCHORLINKS
//                        | Extensions.EXTANCHORLINKS_WRAP
                        | Extensions.FENCED_CODE_BLOCKS
//                        | Extensions.FOOTNOTES
//                        | Extensions.FORCELISTITEMPARA
//                        | Extensions.HARDWRAPS
//                        | Extensions.INSERTED
                        | Extensions.MULTI_LINE_IMAGE_URLS
                        | Extensions.QUOTES
                        | Extensions.RELAXEDHRULES
                        | Extensions.SMARTS
                        | Extensions.STRIKETHROUGH
                        | Extensions.SUBSCRIPT
                        | Extensions.SUPERSCRIPT
//                        | Extensions.SUPPRESS_HTML_BLOCKS
//                        | Extensions.SUPPRESS_INLINE_HTML
                        | Extensions.TABLES
                        | Extensions.TASKLISTITEMS
                        | Extensions.TOC
//                        | Extensions.TRACE_PARSER
                        | Extensions.WIKILINKS
        );

        //noinspection PointlessBitwiseExpression
        profile.getParserSettings().setOptionsFlags(0L
                        | MdLexParser.ADMONITION_EXT
                        | MdLexParser.ATTRIBUTES_EXT
                        | MdLexParser.COMMONMARK_LISTS
                        | MdLexParser.EMOJI_SHORTCUTS
                        | MdLexParser.ENUMERATED_REFERENCES_EXT
//                        | MdLexParser.FLEXMARK_FRONT_MATTER
//                        | MdLexParser.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA
//                        | MdLexParser.GFM_TABLE_RENDERING
//                        | MdLexParser.GITBOOK_URL_ENCODING
//                        | MdLexParser.GITHUB_LISTS
                        | MdLexParser.GITHUB_WIKI_LINKS
//                        | MdLexParser.GITLAB_EXT
                        | MdLexParser.GITLAB_MATH_EXT
                        | MdLexParser.GITLAB_MERMAID_EXT
//                        | MdLexParser.HEADER_ID_NO_DUPED_DASHES
//                        | MdLexParser.HEADER_ID_NON_ASCII_TO_LOWERCASE
                        | MdLexParser.JEKYLL_FRONT_MATTER
                        | MdLexParser.MACROS_EXT
//                        | MdLexParser.NO_TEXT_ATTRIBUTES
//                        | MdLexParser.PARSE_HTML_ANCHOR_ID
//                        | MdLexParser.PLANTUML_FENCED_CODE
//                        | MdLexParser.PUML_FENCED_CODE
                        | MdLexParser.SIM_TOC_BLANK_LINE_SPACER
//                        | MdLexParser.SPACE_IN_LINK_URLS
        );
    };

    final public static Consumer<MdRenderingProfile> ALL_ENHANCED_PARSER_OPTIONS = profile -> {
        //noinspection PointlessBitwiseExpression
        profile.getParserSettings().setPegdownFlags(0
                        | Extensions.ABBREVIATIONS
                        | Extensions.ASIDE
                        | Extensions.ATXHEADERSPACE
                        | Extensions.AUTOLINKS
                        | Extensions.DEFINITIONS
                        | Extensions.EXTANCHORLINKS
//                        | Extensions.EXTANCHORLINKS_WRAP
                        | Extensions.FENCED_CODE_BLOCKS
                        | Extensions.FOOTNOTES
//                        | Extensions.FORCELISTITEMPARA
//                        | Extensions.HARDWRAPS
                        | Extensions.INSERTED
                        | Extensions.MULTI_LINE_IMAGE_URLS
                        | Extensions.QUOTES
                        | Extensions.RELAXEDHRULES
                        | Extensions.SMARTS
                        | Extensions.STRIKETHROUGH
                        | Extensions.SUBSCRIPT
                        | Extensions.SUPERSCRIPT
//                        | Extensions.SUPPRESS_HTML_BLOCKS
//                        | Extensions.SUPPRESS_INLINE_HTML
                        | Extensions.TABLES
                        | Extensions.TASKLISTITEMS
                        | Extensions.TOC
//                        | Extensions.TRACE_PARSER
                        | Extensions.WIKILINKS
        );

        //noinspection PointlessBitwiseExpression
        profile.getParserSettings().setOptionsFlags(0L
                        | MdLexParser.ADMONITION_EXT
                        | MdLexParser.ATTRIBUTES_EXT
                        | MdLexParser.COMMONMARK_LISTS
                        | MdLexParser.EMOJI_SHORTCUTS
                        | MdLexParser.ENUMERATED_REFERENCES_EXT
                        | MdLexParser.FLEXMARK_FRONT_MATTER
//                        | MdLexParser.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA
//                        | MdLexParser.GFM_TABLE_RENDERING
//                        | MdLexParser.GITBOOK_URL_ENCODING
//                        | MdLexParser.GITHUB_LISTS
                        | MdLexParser.GITHUB_WIKI_LINKS
                        | MdLexParser.GITLAB_EXT
                        | MdLexParser.GITLAB_MATH_EXT
                        | MdLexParser.GITLAB_MERMAID_EXT
//                        | MdLexParser.HEADER_ID_NO_DUPED_DASHES
//                        | MdLexParser.HEADER_ID_NON_ASCII_TO_LOWERCASE
                        | MdLexParser.JEKYLL_FRONT_MATTER
                        | MdLexParser.MACROS_EXT
//                        | MdLexParser.NO_TEXT_ATTRIBUTES
                        | MdLexParser.PARSE_HTML_ANCHOR_ID
                        | MdLexParser.PLANTUML_FENCED_CODE
                        | MdLexParser.PUML_FENCED_CODE
                        | MdLexParser.SIM_TOC_BLANK_LINE_SPACER
//                        | MdLexParser.SPACE_IN_LINK_URLS
        );
    };

    final public static Consumer<MdRenderingProfile> LEGACY_OPTIONS = profile -> {
        //noinspection PointlessBitwiseExpression
        int extensionFlags = 0
                | Extensions.ABBREVIATIONS
//                        | Extensions.ASIDE
                | Extensions.ATXHEADERSPACE
                | Extensions.AUTOLINKS
                | Extensions.DEFINITIONS
                | Extensions.EXTANCHORLINKS
//                        | Extensions.EXTANCHORLINKS_WRAP
                | Extensions.FENCED_CODE_BLOCKS
                | Extensions.FOOTNOTES
//                        | Extensions.FORCELISTITEMPARA
//                        | Extensions.HARDWRAPS
//                        | Extensions.INSERTED
                | Extensions.MULTI_LINE_IMAGE_URLS
                | Extensions.QUOTES
                | Extensions.RELAXEDHRULES
                | Extensions.SMARTS
                | Extensions.STRIKETHROUGH
//                        | Extensions.SUBSCRIPT
//                        | Extensions.SUPERSCRIPT
//                        | Extensions.SUPPRESS_HTML_BLOCKS
//                        | Extensions.SUPPRESS_INLINE_HTML
                | Extensions.TABLES
                | Extensions.TASKLISTITEMS
                | Extensions.TOC
//                        | Extensions.TRACE_PARSER
                | Extensions.WIKILINKS;

        profile.getParserSettings().setPegdownFlags(extensionFlags);
        assert profile.getParserSettings().getPegdownFlags() == extensionFlags;

        //noinspection PointlessBitwiseExpression
        long optionsFlags = 0L
//                        | MdLexParser.ADMONITION_EXT
//                        | MdLexParser.ATTRIBUTES_EXT
//                        | MdLexParser.COMMONMARK_LISTS
                | MdLexParser.EMOJI_SHORTCUTS
//                        | MdLexParser.ENUMERATED_REFERENCES_EXT
//                | MdLexParser.FLEXMARK_FRONT_MATTER
//                        | MdLexParser.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA
//                        | MdLexParser.GFM_TABLE_RENDERING
                | MdLexParser.GITBOOK_URL_ENCODING
//                        | MdLexParser.GITHUB_LISTS
                | MdLexParser.GITHUB_WIKI_LINKS
//                        | MdLexParser.GITLAB_EXT
//                        | MdLexParser.GITLAB_MATH_EXT
//                        | MdLexParser.GITLAB_MERMAID_EXT
//                        | MdLexParser.HEADER_ID_NO_DUPED_DASHES
//                        | MdLexParser.HEADER_ID_NON_ASCII_TO_LOWERCASE
                | MdLexParser.JEKYLL_FRONT_MATTER
//                        | MdLexParser.MACROS_EXT
//                        | MdLexParser.NO_TEXT_ATTRIBUTES
//                        | MdLexParser.PARSE_HTML_ANCHOR_ID
//                        | MdLexParser.PLANTUML_FENCED_CODE
//                        | MdLexParser.PUML_FENCED_CODE
//                        | MdLexParser.SIM_TOC_BLANK_LINE_SPACER
//                        | MdLexParser.SPACE_IN_LINK_URLS
                ;

        profile.getParserSettings().setOptionsFlags(optionsFlags);
        assert profile.getParserSettings().getOptionsFlags() == optionsFlags;
    };
}
