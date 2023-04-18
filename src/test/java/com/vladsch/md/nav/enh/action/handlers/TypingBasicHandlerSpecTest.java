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

package com.vladsch.md.nav.enh.action.handlers;

import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.testUtil.MdLightPlatformActionSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.renderers.ActionSpecRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypingBasicHandlerSpecTest extends MdLightPlatformActionSpecTestCase {
    private static final String SPEC_RESOURCE = "typing_basic_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(TypingBasicHandlerSpecTest.class, SPEC_RESOURCE);

    public static final DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, BASIC_OPTIONS)
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("smart-edit-asterisks", new MutableDataSet().set(APPLICATION_SETTINGS, applicationSettings -> applicationSettings.getDocumentSettings().setSmartEditAsterisks(true)));
        optionsMap.put("smart-edit-underscore", new MutableDataSet().set(APPLICATION_SETTINGS, applicationSettings -> applicationSettings.getDocumentSettings().setSmartEditUnderscore(true)));
        optionsMap.put("smart-edit-tildes", new MutableDataSet().set(APPLICATION_SETTINGS, applicationSettings -> applicationSettings.getDocumentSettings().setSmartEditTildes(true)));
        optionsMap.put("smart-edit-back-tics", new MutableDataSet().set(APPLICATION_SETTINGS, applicationSettings -> applicationSettings.getDocumentSettings().setSmartEditBackTicks(true)));
        optionsMap.put("wrap-on-space", new MutableDataSet().set(APPLICATION_SETTINGS, applicationSettings -> applicationSettings.getDocumentSettings().setWrapOnlyOnTypingSpace(true)));
    }
    public TypingBasicHandlerSpecTest() {
        super(optionsMap, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }

    @Override
    public ActionSpecRenderer<?> createExampleSpecRenderer(@NotNull SpecExample example, @Nullable DataHolder options) {
        // set no wrap by default and enter as wrap
        return super.createExampleSpecRenderer(example, options);
    }
}
