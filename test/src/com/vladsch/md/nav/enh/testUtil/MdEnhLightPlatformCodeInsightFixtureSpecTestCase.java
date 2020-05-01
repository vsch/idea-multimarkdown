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

package com.vladsch.md.nav.enh.testUtil;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.md.nav.enh.testUtil.cases.MdEnhCodeInsightFixtureSpecTestCase;
import com.vladsch.md.nav.testUtil.MdLightPlatformCodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class MdEnhLightPlatformCodeInsightFixtureSpecTestCase extends MdLightPlatformCodeInsightFixtureSpecTestCase implements MdEnhCodeInsightFixtureSpecTestCase {

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(MdEnhCodeInsightFixtureSpecTestCase.getOptionsMap());
    }
    public MdEnhLightPlatformCodeInsightFixtureSpecTestCase(@Nullable Map<String, ? extends DataHolder> optionMap, @Nullable DataHolder... defaultOptions) {
        super(CodeInsightFixtureSpecTestCase.optionsMaps(optionsMap, optionMap), defaultOptions);
    }
}
