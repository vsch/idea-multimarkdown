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

package com.vladsch.md.nav.enh.testUtil.cases;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.md.nav.enh.testUtil.MdEnhSpecTestSetup;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.testUtil.cases.MdSpecTest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface MdEnhSpecTest extends MdSpecTest {
    Consumer<MdRenderingProfile> ENHANCED_OPTIONS = MdEnhSpecTestSetup.ENHANCED_OPTIONS;
    Consumer<MdRenderingProfile> ALL_ENHANCED_PARSER_OPTIONS = MdEnhSpecTestSetup.ALL_ENHANCED_PARSER_OPTIONS;
    Consumer<MdRenderingProfile> LEGACY_OPTIONS = MdEnhSpecTestSetup.LEGACY_OPTIONS;

    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdSpecTest.getOptionsMap());
            }
            return optionsMap;
        }
    }
}
