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

package com.vladsch.md.nav.testUtil.cases;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.plugin.test.util.cases.LightFixtureLineMarkerSpecTest;

import java.util.HashMap;
import java.util.Map;

public interface MdLightFixtureLineMarkerSpecTest extends MdCodeInsightFixtureSpecTestCase, LightFixtureLineMarkerSpecTest {
    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdCodeInsightFixtureSpecTestCase.getOptionsMap());
                optionsMap.putAll(LightFixtureLineMarkerSpecTest.getOptionsMap());
            }
            return optionsMap;
        }
    }
}
