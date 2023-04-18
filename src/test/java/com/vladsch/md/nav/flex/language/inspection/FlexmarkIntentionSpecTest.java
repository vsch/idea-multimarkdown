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

package com.vladsch.md.nav.flex.language.inspection;

import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.flex.settings.FlexmarkDebugSettings;
import com.vladsch.md.nav.flex.testUtil.cases.FlexOptionsForStyleSettings;
import com.vladsch.md.nav.testUtil.MdLightPlatformIntentionSpecTestCase;
import com.vladsch.md.nav.testUtil.cases.MdEnhSpecTest;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(value = Parameterized.class)
public class FlexmarkIntentionSpecTest extends MdLightPlatformIntentionSpecTestCase implements FlexOptionsForStyleSettings {
    private static final String SPEC_RESOURCE = "flexmark_intention_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(FlexmarkIntentionSpecTest.class, SPEC_RESOURCE);

    final private static DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, MdEnhSpecTest.ALL_ENHANCED_PARSER_OPTIONS);

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(FlexOptionsForStyleSettings.getOptionsMap());
        optionsMap.put("flexmark-features", new MutableDataSet().set(APPLICATION_SETTINGS, s -> s.getDebugSettings().getExtension(FlexmarkDebugSettings.KEY).setEnableFlexmarkFeatures(true)));
    }
    public FlexmarkIntentionSpecTest() {
        super(optionsMap, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }
}
