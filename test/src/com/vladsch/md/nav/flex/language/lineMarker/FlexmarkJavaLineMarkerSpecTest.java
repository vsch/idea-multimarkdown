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

package com.vladsch.md.nav.flex.language.lineMarker;

import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.enh.testUtil.cases.MdEnhSpecTest;
import com.vladsch.md.nav.flex.language.FlexmarkLineMarkerProvider;
import com.vladsch.md.nav.flex.language.MdFlexmarkLineMarkerExtension;
import com.vladsch.md.nav.flex.settings.FlexmarkHtmlSettings;
import com.vladsch.md.nav.parser.MdLexParser;
import com.vladsch.md.nav.testUtil.MdJavaLineMarkerSpecTest;
import com.vladsch.md.nav.testUtil.MdSpecTest;
import com.vladsch.plugin.test.util.SpecTestCaseJavaProjectDescriptor;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(value = Parameterized.class)
public class FlexmarkJavaLineMarkerSpecTest extends MdJavaLineMarkerSpecTest {
    private static final String SPEC_RESOURCE = "flexmark_java_line_marker_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(FlexmarkJavaLineMarkerSpecTest.class, SPEC_RESOURCE);
    public static final ResourceLocation SPEC_EXAMPLE_FILE = ResourceLocation.of(FlexmarkJavaLineMarkerSpecTest.class, "sample_spec_test.md");
    public static final ResourceLocation SAMPLE_TEST_CASE = ResourceLocation.of(FlexmarkJavaLineMarkerSpecTest.class, "SampleTest.txt");

    public static final DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, MdEnhSpecTest.ALL_ENHANCED_PARSER_OPTIONS)
            .set(PARSER_SETTINGS, s -> s.setOptionsFlags(s.getOptionsFlags() | MdLexParser.FLEXMARK_FRONT_MATTER))
            .set(HTML_SETTINGS, s -> {
                FlexmarkHtmlSettings flexmarkHtmlSettings = s.getExtension(FlexmarkHtmlSettings.KEY);
                flexmarkHtmlSettings.getFlexmarkSectionLanguages().putAll(FlexmarkHtmlSettings.DEFAULT_SECTION_LANGUAGES);
            })
            .set(DEBUG_LOG_SETTINGS, it -> it.trace("com.vladsch.md.nav.parser.cache"))
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("disable-spec-file", new MutableDataSet().set(LINE_MARKER_SETTINGS, it -> it.disable(FlexmarkLineMarkerProvider.FLEXMARK_SPEC_FILE)));
        optionsMap.put("disable-spec-example-option", new MutableDataSet().set(LINE_MARKER_SETTINGS, it -> it.disable(FlexmarkLineMarkerProvider.FLEXMARK_SPEC_EXAMPLE_OPTION)));
        optionsMap.put("disable-spec-example", new MutableDataSet().set(LINE_MARKER_SETTINGS, it -> it.disable(MdFlexmarkLineMarkerExtension.SPEC_EXAMPLE)));
        optionsMap.put("add-spec-file", new MutableDataSet()
                .set(PARSER_SETTINGS, s -> s.setOptionsFlags(s.getOptionsFlags() | MdLexParser.PRODUCTION_SPEC_PARSER))
                .set(ADDITIONAL_PROJECT_FILES, t -> {
                    t.add(SPEC_EXAMPLE_FILE, "com/vladsch/md/nav/flex/language/lineMarker/" + SPEC_EXAMPLE_FILE.getResourcePath());
                })
        );
        optionsMap.put("add-test-case", new MutableDataSet()
                .set(ADDITIONAL_PROJECT_FILES, t -> {
                    t.add(SAMPLE_TEST_CASE, "com/vladsch/md/nav/flex/language/lineMarker/SampleTest.java");
                })
        );
    }

    private SpecTestCaseJavaProjectDescriptor myProjectDescriptor;

    public FlexmarkJavaLineMarkerSpecTest() {
        super(optionsMap, OPTIONS);
    }

    @Override
    protected void tuneFixture(JavaModuleFixtureBuilder moduleBuilder) throws Exception {
        super.tuneFixture(moduleBuilder);

        moduleBuilder.addJdk("/Applications/IntelliJ-IDEA-2019.3-CE-EAP.app/Contents/jbr/Contents/Home");
        moduleBuilder.setLanguageLevel(LanguageLevel.JDK_1_8);
        moduleBuilder.addLibrary("test_lib",
                "/Users/vlad/src/projects/idea-multimarkdown3/WebViewDebugSample/lib/annotations-18.0.0.jar"
                , "/Users/vlad/src/projects/idea-multimarkdown3/lib/flexmark-parent.jar"
                , "/Users/vlad/src/projects/idea-multimarkdown3/lib/flexmark-util.jar"
                , "/Users/vlad/src/projects/idea-multimarkdown3/lib/flexmark-test-util.jar"
                , "/Users/vlad/src/projects/plugin-test-util/lib/junit-4.12.jar"
                , "/Users/vlad/src/projects/plugin-test-util/lib/hamcrest-core-1.3.jar"
        );
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }
}
