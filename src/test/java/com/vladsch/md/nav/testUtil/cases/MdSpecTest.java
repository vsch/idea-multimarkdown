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

import com.vladsch.flexmark.test.util.SettableInstance;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.language.MdCodeStyleSettings;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdHtmlSettings;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdPreviewSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.testUtil.MdSpecTestSetup;
import com.vladsch.plugin.test.util.cases.SpecTest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface MdSpecTest extends SpecTest {
    DataKey<Boolean> WANT_CACHE_LOGS = new DataKey<>("WANT_CACHE_LOGS", false);
    DataKey<Boolean> WANT_CACHE_TRACE = new DataKey<>("WANT_CACHE_TRACE", false);
    DataKey<Boolean> WANT_CACHE_TIMESTAMP = new DataKey<>("WANT_CACHE_TIMESTAMP", false);

    DataKey<Consumer<MdApplicationSettings>> APPLICATION_SETTINGS = MdSpecTestSetup.APPLICATION_SETTINGS;
    DataKey<Consumer<MdRenderingProfile>> RENDERING_PROFILE = MdSpecTestSetup.RENDERING_PROFILE;
    DataKey<Consumer<MdParserSettings>> PARSER_SETTINGS = MdSpecTestSetup.PARSER_SETTINGS;
    DataKey<Consumer<MdPreviewSettings>> PREVIEW_SETTINGS = MdSpecTestSetup.PREVIEW_SETTINGS;
    DataKey<Consumer<MdHtmlSettings>> HTML_SETTINGS = MdSpecTestSetup.HTML_SETTINGS;
    DataKey<Consumer<MdCodeStyleSettings>> STYLE_SETTINGS = MdSpecTestSetup.STYLE_SETTINGS;

    Consumer<MdRenderingProfile> BASIC_OPTIONS = MdSpecTestSetup.BASIC_OPTIONS;
    SettableInstance<MdApplicationSettings> APPLICATION_SETTINGS_OPTION = MdSpecTestSetup.APPLICATION_SETTINGS_OPTION;
    SettableInstance<MdCodeStyleSettings> STYLE_SETTINGS_OPTION = MdSpecTestSetup.STYLE_SETTINGS_OPTION;
    SettableInstance<MdRenderingProfile> RENDERING_PROFILE_OPTION = MdSpecTestSetup.RENDERING_PROFILE_OPTION;

    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(SpecTest.getOptionsMap());

                optionsMap.put("with-cache-logs", new MutableDataSet().set(WANT_CACHE_LOGS, true));
                optionsMap.put("with-cache-trace", new MutableDataSet().set(WANT_CACHE_LOGS, true).set(WANT_CACHE_TRACE, true));
                optionsMap.put("with-cache-timestamp", new MutableDataSet().set(WANT_CACHE_LOGS, true).set(WANT_CACHE_TIMESTAMP, true));
                optionsMap.put("no-cache-logs", new MutableDataSet().set(WANT_CACHE_LOGS, false));
                optionsMap.put("no-cache-trace", new MutableDataSet().set(WANT_CACHE_LOGS, false).set(WANT_CACHE_TRACE, false));
                optionsMap.put("no-cache-timestamp", new MutableDataSet().set(WANT_CACHE_LOGS, true).set(WANT_CACHE_TIMESTAMP, false));
            }
            return optionsMap;
        }
    }
}
