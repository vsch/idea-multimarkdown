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

package com.vladsch.md.nav.testUtil;

import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.testUtil.cases.MdLightFixtureActionSpecTest;
import com.vladsch.md.nav.testUtil.cases.MdLightFixtureIntentionSpecTest;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.SpecTest;
import com.vladsch.plugin.test.util.renderers.IntentionSpecRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class MdLightPlatformIntentionSpecTestCase extends MdLightPlatformActionSpecTestCase implements MdLightFixtureIntentionSpecTest {
    final private static DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, BASIC_OPTIONS)
            .set(ACTION_NAME, MdLightFixtureActionSpecTest.SKIP_ACTION)
            .set(SpecTest.NO_FILE_EOL, true)
            .set(SpecTest.WANT_AST, false)
            .set(INTENTION_ACTION, "")
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(MdLightFixtureIntentionSpecTest.getOptionsMap());
    }
    public MdLightPlatformIntentionSpecTestCase(@Nullable Map<String, ? extends DataHolder> optionMap, @Nullable DataHolder... defaultOptions) {
        super(CodeInsightFixtureSpecTestCase.optionsMaps(optionsMap, optionMap), CodeInsightFixtureSpecTestCase.dataHolders(OPTIONS, defaultOptions));
    }

    @Override
    public IntentionSpecRenderer<?> createExampleSpecRenderer(@NotNull SpecExample example, @Nullable DataHolder options) {
        return new IntentionSpecRenderer<>(this, example, options);
    }
}
