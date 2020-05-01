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

package com.vladsch.md.nav.enh.language.lineMarker;

import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.enh.testUtil.cases.MdEnhSpecTest;
import com.vladsch.md.nav.language.MdLineMarkerProvider;
import com.vladsch.md.nav.testUtil.MdLightPlatformLineMarkerSpecTest;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.SpecTest;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(value = Parameterized.class)
public class LineMarkerSpecTest extends MdLightPlatformLineMarkerSpecTest {
    private static final String SPEC_RESOURCE = "line_marker_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(LineMarkerSpecTest.class, SPEC_RESOURCE);
    public static final ResourceLocation MARKDOWN_WITH_HEADINGS_LOCATION = ResourceLocation.of(LineMarkerSpecTest.class, "MarkdownWithHeadings.md");
    public static final ResourceLocation MARKDOWN_WITH_ANCHOR_LOCATION = ResourceLocation.of(LineMarkerSpecTest.class, "MarkdownWithAnchor.md");
    public static final ResourceLocation MARKDOWN_WITH_ID_ATTRIBUTE_LOCATION = ResourceLocation.of(LineMarkerSpecTest.class, "MarkdownWithIdAttribute.md");
    public static final ResourceLocation JEKYLL_INCLUDE_LOCATION = ResourceLocation.of(LineMarkerSpecTest.class, "jekyll_include.md");
    public static final ResourceLocation JAVA_FILE_LOCATION = ResourceLocation.of(LineMarkerSpecTest.class, "JavaFile.txt");
    public static final ResourceLocation SAMPLE_IMAGE = ResourceLocation.of(LineMarkerSpecTest.class, "sample.png");

    public static final DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, MdEnhSpecTest.ALL_ENHANCED_PARSER_OPTIONS)
            .set(SpecTest.ADDITIONAL_PROJECT_FILES, t -> {
                t.add(MARKDOWN_WITH_HEADINGS_LOCATION);
                t.add(MARKDOWN_WITH_ANCHOR_LOCATION);
                t.add(MARKDOWN_WITH_ID_ATTRIBUTE_LOCATION);
                t.add(JEKYLL_INCLUDE_LOCATION, "/_includes/jekyll_include.md");
                t.add(JAVA_FILE_LOCATION, "JavaFile.txt");
                t.add(SAMPLE_IMAGE);
            })
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("disable-link-ref-markdown", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_REF_MARKDOWN)));
        optionsMap.put("disable-link-ref-file", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_REF_FILE)));
        optionsMap.put("disable-link-ref-web", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_REF_WEB)));
        optionsMap.put("disable-link-ref-ftp", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_REF_FTP)));
        optionsMap.put("disable-link-ref-mail", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_REF_MAIL)));
        optionsMap.put("disable-link-ref-github", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_REF_GITHUB)));
        optionsMap.put("disable-link-ref-image", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_REF_IMAGE)));
        optionsMap.put("disable-link-anchor-heading", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_ANCHOR_HEADING)));
        optionsMap.put("disable-link-anchor-a-tag", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_ANCHOR_A_TAG)));
        optionsMap.put("disable-link-anchor-id-attribute", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_ANCHOR_ID_ATTRIBUTE)));
        optionsMap.put("disable-link-anchor-line-selection", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.LINK_ANCHOR_LINE_SELECTION)));
        optionsMap.put("disable-reference-ref-link", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.REFERENCE_REF_LINK)));
        optionsMap.put("disable-reference-ref-image", new MutableDataSet().set(SpecTest.LINE_MARKER_SETTINGS, it -> it.disable(MdLineMarkerProvider.REFERENCE_REF_IMAGE)));
    }
    public LineMarkerSpecTest() {
        super(optionsMap, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }
}
