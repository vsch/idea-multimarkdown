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

package com.vladsch.md.nav.testUtil.cases;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.plugin.test.util.cases.LightFixtureActionSpecTest;
import com.vladsch.plugin.test.util.cases.LightFixtureIntentionSpecTest;
import com.vladsch.plugin.test.util.renderers.ActionSpecRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public interface MdLightFixtureIntentionSpecTest extends MdLightFixtureActionSpecTest, LightFixtureIntentionSpecTest {
    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdLightFixtureActionSpecTest.getOptionsMap());
                optionsMap.putAll(LightFixtureIntentionSpecTest.getOptionsMap());
            }
            return optionsMap;
        }
    }

    @Override
    default <T extends LightFixtureActionSpecTest> void beforeDoTestAction(@NotNull ActionSpecRenderer<T> specRenderer, @NotNull DataHolder specRendererOptions) {
        // allow passing data for tests in those cases where the UI needs to supply input
        if (specRendererOptions.contains(LightFixtureIntentionSpecTest.FILE_PARAM)) {
            getFile().putUserData(MdFile.TEST_PARAM, LightFixtureIntentionSpecTest.FILE_PARAM.get(specRendererOptions));
        }
    }

    @Override
    default <T extends LightFixtureActionSpecTest> void afterDoTestAction(@NotNull ActionSpecRenderer<T> specRenderer, @NotNull DataHolder specRendererOptions) {
        if (specRendererOptions.contains(LightFixtureIntentionSpecTest.FILE_PARAM)) {
            getFile().putUserData(MdFile.TEST_PARAM, null);
        }
    }
}
