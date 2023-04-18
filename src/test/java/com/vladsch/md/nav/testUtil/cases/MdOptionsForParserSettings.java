/*
 * Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.md.nav.testUtil.cases;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.parser.Extensions;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.testUtil.MdSpecTest;

import java.util.HashMap;
import java.util.Map;

public interface MdOptionsForParserSettings extends MdSpecTest {
    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdSpecTest.getOptionsMap());

                // NOTE: for historical reasons anchorlinks and anchorlinks-wrap options are in parser when these are HTML settings
                // parser options: pegdown
                optionsMap.put("parser-abbreviations", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.ABBREVIATIONS)));
                optionsMap.put("parser-anchorlinks", new MutableDataSet().set(HTML_SETTINGS, settings -> settings.setAddAnchorLinks(true)));
                optionsMap.put("parser-chorlinks-wrap", new MutableDataSet().set(HTML_SETTINGS, settings -> settings.setAnchorLinksWrapText(true)));
                optionsMap.put("parser-aside", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.ASIDE)));
                optionsMap.put("parser-autolinks", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.AUTOLINKS)));
                optionsMap.put("parser-definitions", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.DEFINITIONS)));
                optionsMap.put("parser-fenced-code-blocks", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.FENCED_CODE_BLOCKS)));
                optionsMap.put("parser-subscript", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.SUBSCRIPT)));
                optionsMap.put("parser-hardwraps", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.HARDWRAPS)));
                optionsMap.put("parser-atxheaderspace", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.ATXHEADERSPACE)));
                optionsMap.put("parser-quotes", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.QUOTES)));
                optionsMap.put("parser-relaxedhrules", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.RELAXEDHRULES)));
                optionsMap.put("parser-smarts", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.SMARTS)));
                optionsMap.put("parser-strikethrough", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.STRIKETHROUGH)));
                optionsMap.put("parser-suppress-html-blocks", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.SUPPRESS_HTML_BLOCKS)));
                optionsMap.put("parser-suppress-inline-html", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.SUPPRESS_INLINE_HTML)));
                optionsMap.put("parser-tables", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.TABLES)));
                optionsMap.put("parser-tasklistitems", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.TASKLISTITEMS)));
                optionsMap.put("parser-wikilinks", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.WIKILINKS)));
                optionsMap.put("parser-footnotes", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.FOOTNOTES)));
                optionsMap.put("parser-superscript", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.SUPERSCRIPT)));
                optionsMap.put("parser-inserted", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.INSERTED)));
                optionsMap.put("parser-toc", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.TOC)));
                optionsMap.put("parser-html-deep-parser", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.HTML_DEEP_PARSER)));
                optionsMap.put("parser-definition-break-double-blank-line", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.DEFINITION_BREAK_DOUBLE_BLANK_LINE)));
                optionsMap.put("parser-multi-line-image-urls", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.MULTI_LINE_IMAGE_URLS)));
                optionsMap.put("parser-intellij-dummy-identifier", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() | Extensions.INTELLIJ_DUMMY_IDENTIFIER)));

                // parser off options
                optionsMap.put("parser-no-abbreviations", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.ABBREVIATIONS)));
                optionsMap.put("parser-no-anchorlinks", new MutableDataSet().set(HTML_SETTINGS, settings -> settings.setAddAnchorLinks(false)));
                optionsMap.put("parser-no-aside", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.ASIDE)));
                optionsMap.put("parser-no-autolinks", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.AUTOLINKS)));
                optionsMap.put("parser-no-definitions", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.DEFINITIONS)));
                optionsMap.put("parser-no-fenced-code-blocks", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.FENCED_CODE_BLOCKS)));
                optionsMap.put("parser-no-subscript", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.SUBSCRIPT)));
                optionsMap.put("parser-no-hardwraps", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.HARDWRAPS)));
                optionsMap.put("parser-no-atxheaderspace", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.ATXHEADERSPACE)));
                optionsMap.put("parser-no-quotes", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.QUOTES)));
                optionsMap.put("parser-no-relaxedhrules", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.RELAXEDHRULES)));
                optionsMap.put("parser-no-smarts", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.SMARTS)));
                optionsMap.put("parser-no-strikethrough", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.STRIKETHROUGH)));
                optionsMap.put("parser-no-suppress-html-blocks", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.SUPPRESS_HTML_BLOCKS)));
                optionsMap.put("parser-no-suppress-inline-html", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.SUPPRESS_INLINE_HTML)));
                optionsMap.put("parser-no-tables", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.TABLES)));
                optionsMap.put("parser-no-tasklistitems", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.TASKLISTITEMS)));
                optionsMap.put("parser-no-wikilinks", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.WIKILINKS)));
                optionsMap.put("parser-no-footnotes", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.FOOTNOTES)));
                optionsMap.put("parser-no-superscript", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.SUPERSCRIPT)));
                optionsMap.put("parser-no-inserted", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.INSERTED)));
                optionsMap.put("parser-no-toc", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.TOC)));
                optionsMap.put("parser-no-html-deep-parser", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.HTML_DEEP_PARSER)));
                optionsMap.put("parser-no-definition-break-double-blank-line", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.DEFINITION_BREAK_DOUBLE_BLANK_LINE)));
                optionsMap.put("parser-no-anchorlinks-wrap", new MutableDataSet().set(HTML_SETTINGS, settings -> settings.setAnchorLinksWrapText(false)));
                optionsMap.put("parser-no-multi-line-image-urls", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.MULTI_LINE_IMAGE_URLS)));
                optionsMap.put("parser-no-intellij-dummy-identifier", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setPegdownFlags(settings.getPegdownFlags() & ~Extensions.INTELLIJ_DUMMY_IDENTIFIER)));

                // parser options: extensions
                optionsMap.put("parser-jekyll-front-matter", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.JEKYLL_FRONT_MATTER)));
                optionsMap.put("parser-github-wiki-links", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.GITHUB_WIKI_LINKS)));
                optionsMap.put("parser-emoji-shortcuts", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.EMOJI_SHORTCUTS)));
                optionsMap.put("parser-gitbook-url-encoding", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.GITBOOK_URL_ENCODING)));
                optionsMap.put("parser-sim-toc-blank-line-spacer", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.SIM_TOC_BLANK_LINE_SPACER)));
                optionsMap.put("parser-gfm-loose-blank-line-after-item-para", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA)));
                optionsMap.put("parser-flexmark-front-matter", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.FLEXMARK_FRONT_MATTER)));
                optionsMap.put("parser-gfm-table-rendering", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.GFM_TABLE_RENDERING)));
                optionsMap.put("parser-github-lists", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.GITHUB_LISTS)));
                optionsMap.put("parser-commonmark-lists", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.COMMONMARK_LISTS)));
                optionsMap.put("parser-attributes-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.ATTRIBUTES_EXT)));
                optionsMap.put("parser-enumerated-references-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.ENUMERATED_REFERENCES_EXT)));
                optionsMap.put("parser-header-id-no-duped-dashes", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.HEADER_ID_NO_DUPED_DASHES)));
                optionsMap.put("parser-parse-html-anchor-id", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.PARSE_HTML_ANCHOR_ID)));
                optionsMap.put("parser-no-text-attributes", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.NO_TEXT_ATTRIBUTES)));
                optionsMap.put("parser-admonition-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.ADMONITION_EXT)));
                optionsMap.put("parser-gitlab-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.GITLAB_EXT)));
                optionsMap.put("parser-gitlab-math-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.GITLAB_MATH_EXT)));
                optionsMap.put("parser-gitlab-mermaid-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.GITLAB_MERMAID_EXT)));
                optionsMap.put("parser-macros-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.MACROS_EXT)));
                optionsMap.put("parser-header-id-non-ascii-to-lowercase", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.HEADER_ID_NON_ASCII_TO_LOWERCASE)));
                optionsMap.put("parser-puml-fenced-code", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.PUML_FENCED_CODE)));
                optionsMap.put("parser-plantuml-fenced-code", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.PLANTUML_FENCED_CODE)));
                optionsMap.put("parser-space-in-link-urls", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() | MdLexParser.SPACE_IN_LINK_URLS)));

                // parser options: extensions
                optionsMap.put("parser-no-jekyll-front-matter", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.JEKYLL_FRONT_MATTER)));
                optionsMap.put("parser-no-github-wiki-links", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.GITHUB_WIKI_LINKS)));
                optionsMap.put("parser-no-emoji-shortcuts", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.EMOJI_SHORTCUTS)));
                optionsMap.put("parser-no-gitbook-url-encoding", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.GITBOOK_URL_ENCODING)));
                optionsMap.put("parser-no-sim-toc-blank-line-spacer", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.SIM_TOC_BLANK_LINE_SPACER)));
                optionsMap.put("parser-no-gfm-loose-blank-line-after-item-para", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA)));
                optionsMap.put("parser-no-flexmark-front-matter", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.FLEXMARK_FRONT_MATTER)));
                optionsMap.put("parser-no-gfm-table-rendering", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.GFM_TABLE_RENDERING)));
                optionsMap.put("parser-no-github-lists", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.GITHUB_LISTS)));
                optionsMap.put("parser-no-commonmark-lists", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.COMMONMARK_LISTS)));
                optionsMap.put("parser-no-attributes-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.ATTRIBUTES_EXT)));
                optionsMap.put("parser-no-enumerated-references-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.ENUMERATED_REFERENCES_EXT)));
                optionsMap.put("parser-no-header-id-no-duped-dashes", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.HEADER_ID_NO_DUPED_DASHES)));
                optionsMap.put("parser-no-parse-html-anchor-id", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.PARSE_HTML_ANCHOR_ID)));
                optionsMap.put("parser-no-no-text-attributes", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.NO_TEXT_ATTRIBUTES)));
                optionsMap.put("parser-no-admonition-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.ADMONITION_EXT)));
                optionsMap.put("parser-no-gitlab-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.GITLAB_EXT)));
                optionsMap.put("parser-no-gitlab-math-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.GITLAB_MATH_EXT)));
                optionsMap.put("parser-no-gitlab-mermaid-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.GITLAB_MERMAID_EXT)));
                optionsMap.put("parser-no-macros-ext", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.MACROS_EXT)));
                optionsMap.put("parser-no-header-id-non-ascii-to-lowercase", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.HEADER_ID_NON_ASCII_TO_LOWERCASE)));
                optionsMap.put("parser-no-puml-fenced-code", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.PUML_FENCED_CODE)));
                optionsMap.put("parser-no-plantuml-fenced-code", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.PLANTUML_FENCED_CODE)));
                optionsMap.put("parser-no-space-in-link-urls", new MutableDataSet().set(PARSER_SETTINGS, settings -> settings.setOptionsFlags(settings.getOptionsFlags() & ~MdLexParser.SPACE_IN_LINK_URLS)));
            }
            return optionsMap;
        }
    }
}
