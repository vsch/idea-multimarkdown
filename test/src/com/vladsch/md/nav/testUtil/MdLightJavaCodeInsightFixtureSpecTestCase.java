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
import com.vladsch.md.nav.testUtil.cases.MdCodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.LightJavaCodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class MdLightJavaCodeInsightFixtureSpecTestCase extends LightJavaCodeInsightFixtureSpecTestCase implements MdCodeInsightFixtureSpecTestCase {
    // standard options
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, BASIC_OPTIONS)
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(MdCodeInsightFixtureSpecTestCase.getOptionsMap());
    }

    private @Nullable StringBuilder myCacheLogs;

    public MdLightJavaCodeInsightFixtureSpecTestCase(@Nullable Map<String, ? extends DataHolder> optionMap, @Nullable DataHolder... defaultOptions) {
        super(CodeInsightFixtureSpecTestCase.optionsMaps(optionsMap, optionMap), CodeInsightFixtureSpecTestCase.dataHolders(OPTIONS, defaultOptions));
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

    /**
     * Return absolute path to the test data. Not intended to be overridden in tests written as part of the IntelliJ IDEA codebase;
     * must be overridden in plugins which use the test framework.
     *
     * @see #getBasePath()
     */
    protected String getTestDataPath() {
        return "/Users/vlad/src/projects/idea-multimarkdown3/test/testData/";
//        String path = isCommunity() ? PlatformTestUtil.getCommunityPath() : IdeaTestExecutionPolicy.getHomePathWithPolicy();
//        return StringUtil.trimEnd(FileUtil.toSystemIndependentName(path), "/") + '/' +
//                StringUtil.trimStart(FileUtil.toSystemIndependentName(getBasePath()), "/");
    }
}
