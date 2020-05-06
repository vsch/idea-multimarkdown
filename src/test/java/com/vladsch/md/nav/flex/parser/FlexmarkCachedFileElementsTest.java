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

import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.flex.psi.FlexmarkExample;
import com.vladsch.md.nav.flex.testUtil.cases.FlexOptionsForStyleSettings;
import com.vladsch.md.nav.testUtil.MdEnhLightPlatformActionSpecTestCase;
import com.vladsch.md.nav.testUtil.cases.MdCachedFileElementsTestCase;
import com.vladsch.md.nav.testUtil.cases.MdEnhSpecTest;
import com.vladsch.md.nav.testUtil.cases.MdLightFixtureActionSpecTest;
import com.vladsch.md.nav.testUtil.renderers.FileElementStashSpecRenderer;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.renderers.ActionSpecRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(value = Parameterized.class)
public class FlexmarkCachedFileElementsTest extends MdEnhLightPlatformActionSpecTestCase implements MdCachedFileElementsTestCase, FlexOptionsForStyleSettings {
    private static final String SPEC_RESOURCE = "flexmark_file_element_stash_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(FlexmarkCachedFileElementsTest.class, SPEC_RESOURCE);

    public static final DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, MdEnhSpecTest.ENHANCED_OPTIONS)
            .set(MdLightFixtureActionSpecTest.ACTION_NAME, MdLightFixtureActionSpecTest.SKIP_ACTION)
//            .set(WANT_CACHE_TIMESTAMP, true).set(WANT_CACHE_LOGS, true)
//            .set(WANT_CACHE_LOGS, true)
//            .set(WANT_CACHE_TRACE, true)
//            .set(WANT_AST, true)
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(MdCachedFileElementsTestCase.getOptionsMap());
        optionsMap.putAll(FlexOptionsForStyleSettings.getOptionsMap());

        optionsMap.put("find-flexmark-spec", new MutableDataSet().set(FIND_CLASSES, new Class<?>[] { FlexmarkExample.class }));
    }
    public FlexmarkCachedFileElementsTest() {
        super(optionsMap, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }

    @Override
    public ActionSpecRenderer<?> createExampleSpecRenderer(@NotNull SpecExample example, @Nullable DataHolder options) {
        return new FileElementStashSpecRenderer<>(this, example, options);
    }
}
