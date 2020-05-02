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

package com.vladsch.md.nav.enh.action.styling;

import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.testUtil.MdEnhLightPlatformActionSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.SpecTest;
import com.vladsch.plugin.test.util.renderers.ActionSpecRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormatDocumentActionSpecTest extends MdEnhLightPlatformActionSpecTestCase {
    private static final String SPEC_RESOURCE = "format_document_action_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(FormatDocumentActionSpecTest.class, SPEC_RESOURCE);

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("code-style-format-control", new MutableDataSet().set(SpecTest.CODE_STYLE_SETTINGS, settings -> {
            settings.FORMATTER_ON_TAG = "@formatter:on";
            settings.FORMATTER_OFF_TAG = "@formatter:off";
            settings.FORMATTER_TAGS_ENABLED = true;
            settings.FORMATTER_TAGS_ACCEPT_REGEXP = false;
        }));

        optionsMap.put("code-style-no-format-control", new MutableDataSet().set(SpecTest.CODE_STYLE_SETTINGS, settings -> {
            settings.FORMATTER_TAGS_ENABLED = false;
        }));
    }

    public FormatDocumentActionSpecTest() {
        super(optionsMap);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }

    @Override
    public ActionSpecRenderer<?> createExampleSpecRenderer(@NotNull SpecExample example, @Nullable DataHolder options) {
        // set no wrap by default and enter as wrap
        return super.createExampleSpecRenderer(example, appendDefaultExampleOptions(example, options, "code-style-format-control, format-element, margin[72]"));
    }
}
