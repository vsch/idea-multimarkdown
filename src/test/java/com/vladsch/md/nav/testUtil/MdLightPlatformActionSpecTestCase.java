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
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.testUtil.cases.MdLightFixtureActionSpecTest;
import com.vladsch.md.nav.testUtil.renderers.MdActionSpecRenderer;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.renderers.ActionSpecRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class MdLightPlatformActionSpecTestCase extends MdLightPlatformCodeInsightFixtureSpecTestCase implements MdLightFixtureActionSpecTest {
    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(MdLightFixtureActionSpecTest.getOptionsMap());
    }

    private @NotNull BasedSequence myFormatResult = BasedSequence.NULL;

    public MdLightPlatformActionSpecTestCase(@Nullable Map<String, ? extends DataHolder> optionMap, @Nullable DataHolder... defaultOptions) {
        super(CodeInsightFixtureSpecTestCase.optionsMaps(optionsMap, optionMap), defaultOptions);
    }

    @Override
    public ActionSpecRenderer<?> createExampleSpecRenderer(@NotNull SpecExample example, @Nullable DataHolder options) {
        return new MdActionSpecRenderer<>(this, example, options);
    }

    @Override
    @NotNull
    public BasedSequence getFormatResult() {
        return myFormatResult;
    }

    @Override
    public void setFormatResult(@NotNull BasedSequence formatResult) {
        this.myFormatResult = formatResult;
    }
}
