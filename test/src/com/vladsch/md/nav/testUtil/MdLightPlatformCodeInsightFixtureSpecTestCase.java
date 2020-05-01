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

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.MdPlugin;
import com.vladsch.md.nav.testUtil.cases.MdCodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.LightPlatformCodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class MdLightPlatformCodeInsightFixtureSpecTestCase extends LightPlatformCodeInsightFixtureSpecTestCase implements MdCodeInsightFixtureSpecTestCase {
    // standard options
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, BASIC_OPTIONS)
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(MdCodeInsightFixtureSpecTestCase.getOptionsMap());
    }

    private @Nullable StringBuilder myCacheLogs;

    public MdLightPlatformCodeInsightFixtureSpecTestCase(@Nullable Map<String, ? extends DataHolder> optionMap, @Nullable DataHolder... defaultOptions) {
        super(CodeInsightFixtureSpecTestCase.optionsMaps(optionsMap, optionMap), CodeInsightFixtureSpecTestCase.dataHolders(OPTIONS, defaultOptions));
    }

    @Override
    public void setUp() throws Exception {
        MdPlugin.RUNNING_TESTS = false;
        super.setUp();
    }

    @Override
    @Nullable
    public StringBuilder getCacheLogs() {
        return myCacheLogs;
    }

    @Override
    public void setCacheLogs(@Nullable StringBuilder cacheLogs) {
        myCacheLogs = cacheLogs;
    }

    /**
     * Returns relative path to the test data.
     */
    @Override
    protected String getBasePath() {
        return "";
    }
}
