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

package com.vladsch.md.nav.parser;

import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.flex.testUtil.cases.FlexOptionsForStyleSettings;
import com.vladsch.md.nav.parser.util.FlexmarkLexParser;
import com.vladsch.md.nav.parser.util.FlexmarkLexRenderer;
import com.vladsch.md.nav.testUtil.MdLightPlatformCodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.renderers.LightFixtureSpecRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;

public class MdParserTest extends MdLightPlatformCodeInsightFixtureSpecTestCase {
    private static final String SPEC_RESOURCE = "lex_parser_ast_spec.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(MdParserTest.class, SPEC_RESOURCE);

    final private static DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, BASIC_LEGACY_OPTIONS)
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
    public MdParserTest() {
        super(optionsMap, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }

    @Override
    public LightFixtureSpecRenderer<?> createExampleSpecRenderer(@NotNull SpecExample example, @Nullable DataHolder options) {
        // set no wrap by default and enter as wrap
        DataHolder OPTIONS = ComboSpecTestCase.aggregate(getDefaultOptions(), options);
        FlexmarkLexParser parser = new FlexmarkLexParser(OPTIONS);
        FlexmarkLexRenderer renderer = new FlexmarkLexRenderer(OPTIONS);

        return new LightFixtureSpecRenderer<MdParserTest>(this, example, OPTIONS) {
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
