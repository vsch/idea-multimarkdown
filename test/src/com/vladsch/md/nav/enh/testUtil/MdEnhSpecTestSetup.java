/*
 * Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

package com.vladsch.md.nav.enh.testUtil;

import com.vladsch.flexmark.test.util.SettableInstance;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.DataKeyAggregator;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.actions.api.MdCaretContextInfoHandler;
import com.vladsch.md.nav.actions.api.MdElementContextInfoProvider;
import com.vladsch.md.nav.actions.api.MdFormatElementHandler;
import com.vladsch.md.nav.editor.api.MdEditorCustomizationProvider;
import com.vladsch.md.nav.flex.parser.MdSpecExampleParserExtension;
import com.vladsch.md.nav.flex.parser.MdSpecExampleStripTrailingSpacesExtension;
import com.vladsch.md.nav.flex.settings.FlexmarkHtmlSettingsExtensionProvider;
import com.vladsch.md.nav.flex.settings.FlexmarkProjectSettingsExtensionProvider;
import com.vladsch.md.nav.language.api.MdCodeStyleConfigurableProvider;
import com.vladsch.md.nav.language.api.MdFoldingBuilderProvider;
import com.vladsch.md.nav.language.api.MdStripTrailingSpacesExtension;
import com.vladsch.md.nav.parser.Extensions;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.parser.api.MdParserExtension;
import com.vladsch.md.nav.psi.api.MdBlockPrefixProvider;
import com.vladsch.md.nav.psi.api.MdElementTextProvider;
import com.vladsch.md.nav.psi.api.MdTypeFactory;
import com.vladsch.md.nav.settings.MathConversionType;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.api.MdApplicationRestartRequiredProvider;
import com.vladsch.md.nav.settings.api.MdApplicationSettingsExtensionProvider;
import com.vladsch.md.nav.settings.api.MdExtensionInfoProvider;
import com.vladsch.md.nav.settings.api.MdProjectSettingsExtensionHandler;
import com.vladsch.md.nav.settings.api.MdProjectSettingsExtensionProvider;
import com.vladsch.md.nav.settings.api.MdRenderingProfileManagerFactory;
import com.vladsch.md.nav.settings.api.MdSettingsExtensionProvider;
import com.vladsch.md.nav.settings.api.MdSettingsFormExtensionProvider;
import com.vladsch.md.nav.spellchecking.api.MdSpellcheckingIdentifierTokenizer;
import com.vladsch.md.nav.testUtil.MdResourceUrlResolvers;
import com.vladsch.md.nav.testUtil.MdSpecTestSetup;
import com.vladsch.md.nav.vcs.api.MdOnProjectSettingsChangedActivityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public class MdEnhSpecTestSetup extends MdSpecTestSetup {
    public static void RUNNING_TESTS() {
        MdSpecTestSetup.RUNNING_TESTS();
        MdPlugin.getTestExtensions()
                .addExtensions(MdParserExtension.EP_NAME, () -> new MdParserExtension[] {
                        // extensions
                        new MdSpecExampleParserExtension(),
                })

                .addExtensions(MdSettingsExtensionProvider.EP_NAME, () -> new MdSettingsExtensionProvider[] {
                        new FlexmarkHtmlSettingsExtensionProvider(),
                })

                .addExtensions(MdBlockPrefixProvider.EP_NAME, () -> new MdBlockPrefixProvider[] {
                })

                .addExtensions(MdCaretContextInfoHandler.EP_NAME, () -> new MdCaretContextInfoHandler[] {
                })
                .addExtensions(MdEditorCustomizationProvider.EP_NAME, () -> new MdEditorCustomizationProvider[] {

                })
                .addExtensions(MdProjectSettingsExtensionProvider.EP_NAME, () -> new MdProjectSettingsExtensionProvider[] {
                        new FlexmarkProjectSettingsExtensionProvider(),
                })
                .addExtensions(MdProjectSettingsExtensionHandler.EP_NAME, () -> new MdProjectSettingsExtensionHandler[] {

                })
                .addExtensions(MdApplicationSettingsExtensionProvider.EP_NAME, () -> new MdApplicationSettingsExtensionProvider[] {

                })
                .addExtensions(MdApplicationRestartRequiredProvider.EP_NAME, () -> new MdApplicationRestartRequiredProvider[] {

                })
                .addExtensions(MdSettingsFormExtensionProvider.EP_NAME, () -> new MdSettingsFormExtensionProvider[] {

                })
                .addExtensions(MdExtensionInfoProvider.EP_NAME, () -> new MdExtensionInfoProvider[] {

                })
                .addExtensions(MdRenderingProfileManagerFactory.EP_NAME, () -> new MdRenderingProfileManagerFactory[] {
                })
                .addExtensions(MdCodeStyleConfigurableProvider.EP_NAME, () -> new MdCodeStyleConfigurableProvider[] {

                })
                .addExtensions(MdFoldingBuilderProvider.EP_NAME, () -> new MdFoldingBuilderProvider[] {

                })
                .addExtensions(MdSpellcheckingIdentifierTokenizer.EP_NAME, () -> new MdSpellcheckingIdentifierTokenizer[] {

                })
                .addExtensions(MdTypeFactory.EP_NAME, () -> new MdTypeFactory[] {
                })
                .addExtensions(MdElementTextProvider.EP_NAME, () -> new MdElementTextProvider[] {
                })
                .addExtensions(MdStripTrailingSpacesExtension.EP_NAME, () -> new MdStripTrailingSpacesExtension[] {
                        new MdSpecExampleStripTrailingSpacesExtension(),
                })
                .addExtensions(MdOnProjectSettingsChangedActivityProvider.EP_NAME, () -> new MdOnProjectSettingsChangedActivityProvider[] {

                })
                .addExtensions(MdElementContextInfoProvider.EP_NAME, () -> new MdElementContextInfoProvider[] {
                })
                .addExtensions(MdFormatElementHandler.EP_NAME, () -> new MdFormatElementHandler[] {
                })
        ;
    }

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
