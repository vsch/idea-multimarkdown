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

package com.vladsch.md.nav.testUtil;

import com.vladsch.flexmark.test.util.SettableExtractedInstance;
import com.vladsch.flexmark.test.util.SettableInstance;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.DataKeyAggregator;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.md.nav.language.MdCodeStyleSettings;
import com.vladsch.md.nav.parser.Extensions;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdHtmlSettings;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdPreviewSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.plugin.test.util.SpecTestSetup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public class MdSpecTestSetup extends SpecTestSetup {
    static {
        MdResourceUrlResolvers.registerResourceUrlResolvers();
    }

    // @formatter:off
    final static public DataKey<Consumer<MdApplicationSettings>> APPLICATION_SETTINGS = new DataKey<>("APPLICATION_SETTINGS", (it) -> {});
    final static public DataKey<Consumer<MdCodeStyleSettings>> STYLE_SETTINGS = new DataKey<>("STYLE_SETTINGS", (it) -> {});
    final static public DataKey<Consumer<MdRenderingProfile>> RENDERING_PROFILE = new DataKey<>("PROFILE_CONSUMER", (it) -> {});
    final static public DataKey<Consumer<MdParserSettings>> PARSER_SETTINGS = new DataKey<>("PARSER_SETTINGS", (it) -> {});
    final static public DataKey<Consumer<MdPreviewSettings>> PREVIEW_SETTINGS = new DataKey<>("PREVIEW_SETTINGS", (it) -> {});
    final static public DataKey<Consumer<MdHtmlSettings>> HTML_SETTINGS = new DataKey<>("HTML_SETTINGS", (it) -> {});
    // @formatter:on

    final private static SettableExtractedInstance<MdRenderingProfile, MdParserSettings> PARSER_SETTINGS_OPTION = new SettableExtractedInstance<>(PARSER_SETTINGS, MdRenderingProfile::getParserSettings);
    final private static SettableExtractedInstance<MdRenderingProfile, MdPreviewSettings> PREVIEW_SETTINGS_OPTION = new SettableExtractedInstance<>(PREVIEW_SETTINGS, MdRenderingProfile::getPreviewSettings);
    final private static SettableExtractedInstance<MdRenderingProfile, MdHtmlSettings> HTML_SETTINGS_OPTION = new SettableExtractedInstance<>(HTML_SETTINGS, MdRenderingProfile::getHtmlSettings);

    final public static SettableInstance<MdApplicationSettings> APPLICATION_SETTINGS_OPTION = new SettableInstance<>(APPLICATION_SETTINGS);
    final public static SettableInstance<MdCodeStyleSettings> STYLE_SETTINGS_OPTION = new SettableInstance<>(STYLE_SETTINGS);
    final public static SettableInstance<MdRenderingProfile> RENDERING_PROFILE_OPTION = new SettableInstance<>(RENDERING_PROFILE, Arrays.asList(PARSER_SETTINGS_OPTION, PREVIEW_SETTINGS_OPTION, HTML_SETTINGS_OPTION));

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
            combined = APPLICATION_SETTINGS_OPTION.aggregateActions(combined, other, overrides);
            combined = STYLE_SETTINGS_OPTION.aggregateActions(combined, other, overrides);
            combined = RENDERING_PROFILE_OPTION.aggregateActions(combined, other, overrides);
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

    final public static Consumer<MdRenderingProfile> BASIC_OPTIONS = profile -> {
        //noinspection PointlessBitwiseExpression
        profile.getParserSettings().setPegdownFlags(0
//                        | Extensions.ABBREVIATIONS
//                        | Extensions.ASIDE
                        | Extensions.ATXHEADERSPACE
                        | Extensions.AUTOLINKS
//                        | Extensions.DEFINITIONS
                        | Extensions.FENCED_CODE_BLOCKS
//                        | Extensions.FOOTNOTES
//                        | Extensions.FORCELISTITEMPARA
//                        | Extensions.HARDWRAPS
//                        | Extensions.INSERTED
//                        | Extensions.MULTI_LINE_IMAGE_URLS
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
//                        | Extensions.TOC
//                        | Extensions.TRACE_PARSER
                        | Extensions.WIKILINKS
        );

        //noinspection PointlessBitwiseExpression
        profile.getParserSettings().setOptionsFlags(0L
//                        | MdLexParser.ADMONITION_EXT
//                        | MdLexParser.ATTRIBUTES_EXT
//                        | MdLexParser.COMMONMARK_LISTS
                        | MdLexParser.EMOJI_SHORTCUTS
//                        | MdLexParser.ENUMERATED_REFERENCES_EXT
//                        | MdLexParser.FLEXMARK_FRONT_MATTER
//                        | MdLexParser.GFM_LOOSE_BLANK_LINE_AFTER_ITEM_PARA
//                        | MdLexParser.GFM_TABLE_RENDERING
//                        | MdLexParser.GITBOOK_URL_ENCODING
//                        | MdLexParser.GITHUB_LISTS
                        | MdLexParser.COMMONMARK_LISTS
                        | MdLexParser.GITHUB_WIKI_LINKS
//                        | MdLexParser.GITLAB_EXT
//                        | MdLexParser.GITLAB_MATH_EXT
//                        | MdLexParser.GITLAB_MERMAID_EXT
//                        | MdLexParser.HEADER_ID_NO_DUPED_DASHES
//                        | MdLexParser.HEADER_ID_NON_ASCII_TO_LOWERCASE
//                        | MdLexParser.JEKYLL_FRONT_MATTER
//                        | MdLexParser.MACROS_EXT
//                        | MdLexParser.NO_TEXT_ATTRIBUTES
//                        | MdLexParser.PARSE_HTML_ANCHOR_ID
//                        | MdLexParser.PLANTUML_FENCED_CODE
//                        | MdLexParser.PUML_FENCED_CODE
//                        | MdLexParser.SIM_TOC_BLANK_LINE_SPACER
//                        | MdLexParser.SPACE_IN_LINK_URLS
        );
        
        profile.getHtmlSettings().setAddAnchorLinks(true);
    };

    final public static Consumer<MdRenderingProfile> BASIC_LEGACY_OPTIONS = profile -> {
        //noinspection PointlessBitwiseExpression
        int extensionFlags = 0
                | Extensions.ABBREVIATIONS
//                        | Extensions.ASIDE
                | Extensions.ATXHEADERSPACE
                | Extensions.AUTOLINKS
                | Extensions.DEFINITIONS
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
//                | MdLexParser.COMMONMARK_LISTS
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
        profile.getHtmlSettings().setAddAnchorLinks(true);
    };
}
