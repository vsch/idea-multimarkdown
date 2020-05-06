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

package com.vladsch.md.nav.flex.parser;

import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.flex.testUtil.cases.FlexOptionsForStyleSettings;
import com.vladsch.md.nav.parser.util.FlexmarkLexParser;
import com.vladsch.md.nav.parser.util.FlexmarkLexRenderer;
import com.vladsch.md.nav.testUtil.MdEnhLightPlatformCodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.renderers.LightFixtureSpecRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;

public class FlexmarkParserTest extends MdEnhLightPlatformCodeInsightFixtureSpecTestCase implements FlexOptionsForStyleSettings {
    private static final String SPEC_RESOURCE = "flexmark_lex_parser_ast_spec.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(FlexmarkParserTest.class, SPEC_RESOURCE);

    final private static DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, BASIC_LEGACY_OPTIONS)
            .toImmutable();

    final private static HashMap<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(FlexOptionsForStyleSettings.getOptionsMap());
    }
    public FlexmarkParserTest() {
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

        return new LightFixtureSpecRenderer<FlexmarkParserTest>(this, example, OPTIONS) {
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
