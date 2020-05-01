/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

package com.vladsch.md.nav.enh.parser;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.enh.testUtil.MdEnhLightPlatformCodeInsightFixtureSpecTestCase;
import com.vladsch.md.nav.enh.testUtil.cases.MdEnhSpecTest;
import com.vladsch.md.nav.flex.testUtil.cases.FlexOptionsForStyleSettings;
import com.vladsch.md.nav.parser.Extensions;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.parser.api.HtmlPurpose;
import com.vladsch.md.nav.parser.api.ParserPurpose;
import com.vladsch.md.nav.parser.flexmark.MdNavigatorExtension;
import com.vladsch.md.nav.parser.util.FlexmarkLexParser;
import com.vladsch.md.nav.parser.util.FlexmarkLexRenderer;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.MdRenderingProfileManager;
import com.vladsch.md.nav.vcs.GitHubLinkResolver;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.renderers.LightFixtureSpecRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;

public class MdEnhFlexmarkParserTest extends MdEnhLightPlatformCodeInsightFixtureSpecTestCase implements FlexOptionsForStyleSettings {
    private static final String SPEC_RESOURCE = "enh_lex_parser_ast_spec.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(MdEnhFlexmarkParserTest.class, SPEC_RESOURCE);

    final private static DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, MdEnhSpecTest.LEGACY_OPTIONS)
            .set(HtmlRenderer.INDENT_SIZE, 2)
            .toImmutable();

    final private static HashMap<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(FlexOptionsForStyleSettings.getOptionsMap());

        optionsMap.put("no-smarts", new MutableDataSet().set(PARSER_SETTINGS, s -> s.setPegdownFlags(s.getPegdownFlags() & ~Extensions.SMARTS)));
        optionsMap.put("no-footnotes", new MutableDataSet().set(PARSER_SETTINGS, s -> s.setPegdownFlags(s.getPegdownFlags() & ~Extensions.FOOTNOTES)));
        optionsMap.put("no-abbr", new MutableDataSet().set(PARSER_SETTINGS, s -> s.setPegdownFlags(s.getPegdownFlags() & ~Extensions.ABBREVIATIONS)));

        optionsMap.put("parse-ref-anchors", new MutableDataSet().set(PARSER_SETTINGS, s -> s.setOptionsFlags(s.getOptionsFlags() | MdLexParser.PARSE_HTML_ANCHOR_ID)));
        optionsMap.put("macros-ext", new MutableDataSet().set(PARSER_SETTINGS, s -> s.setOptionsFlags(s.getOptionsFlags() | MdLexParser.MACROS_EXT)));
        optionsMap.put("gitlab-ext", new MutableDataSet().set(PARSER_SETTINGS, s -> s.setOptionsFlags(s.getOptionsFlags() | MdLexParser.GITLAB_EXT)));
        optionsMap.put("gitlab-math-ext", new MutableDataSet().set(PARSER_SETTINGS, s -> s.setOptionsFlags(s.getOptionsFlags() | MdLexParser.GITLAB_MATH_EXT)));
        optionsMap.put("gitlab-mermaid-ext", new MutableDataSet().set(PARSER_SETTINGS, s -> s.setOptionsFlags(s.getOptionsFlags() | MdLexParser.GITLAB_MERMAID_EXT)));
    }
    public MdEnhFlexmarkParserTest() {
        super(optionsMap, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }

    GitHubLinkResolver myLinkResolver;
    MdRenderingProfile myRenderingProfile;

    @Override
    public LightFixtureSpecRenderer<?> createExampleSpecRenderer(@NotNull SpecExample example, @Nullable DataHolder options) {
        // set no wrap by default and enter as wrap
        DataHolder OPTIONS = ComboSpecTestCase.aggregate(getDefaultOptions(), options).toMutable()
                .set(MdNavigatorExtension.LINK_RESOLVER, () -> {
                    if (myLinkResolver == null) {
                        myLinkResolver = new GitHubLinkResolver(getFile());
                    }
                    return myLinkResolver;
                })
                .set(MdNavigatorExtension.RENDERING_PROFILE, () -> {
                    if (myRenderingProfile == null) {
                        myRenderingProfile = MdRenderingProfileManager.getProfile(getProject());
                    }
                    return myRenderingProfile;
                })
//                .set(FlexmarkLexRenderer.PARSER_PURPOSE, ParserPurpose.PARSER)
//                .set(FlexmarkLexRenderer.HTML_PURPOSE, HtmlPurpose.RENDER)
                .toImmutable();

        FlexmarkLexParser parser = new FlexmarkLexParser(OPTIONS);
        FlexmarkLexRenderer renderer = new FlexmarkLexRenderer(OPTIONS);

        return new LightFixtureSpecRenderer<MdEnhFlexmarkParserTest>(this, example, OPTIONS) {
            @NotNull
            @Override
            protected String renderHtml() {
                String input = getEditor().getDocument().getImmutableCharSequence().toString();
                Node document = parser.parse(input);
                String html = renderer.render(document);
                return html;
            }

            @Override
            protected void renderAst(StringBuilder out) {
                super.renderAst(out);
            }
        };
    }
}
