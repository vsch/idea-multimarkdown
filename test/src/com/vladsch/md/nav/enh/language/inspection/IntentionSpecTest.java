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

package com.vladsch.md.nav.enh.language.inspection;

import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.enh.testUtil.cases.MdEnhSpecTest;
import com.vladsch.md.nav.inspections.emoji.InvalidEmojiShortcutInspection;
import com.vladsch.md.nav.inspections.table.GitHubTableInspection;
import com.vladsch.md.nav.testUtil.MdLightPlatformIntentionSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(value = Parameterized.class)
public class IntentionSpecTest extends MdLightPlatformIntentionSpecTestCase {
    private static final String SPEC_RESOURCE = "intention_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(IntentionSpecTest.class, SPEC_RESOURCE);

    final private static DataHolder OPTIONS = new MutableDataSet()
            .set(RENDERING_PROFILE, MdEnhSpecTest.ALL_ENHANCED_PARSER_OPTIONS);

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("invalid-emoji-shortcut", new MutableDataSet().set(INSPECTION_CLASSES, new Class[] { InvalidEmojiShortcutInspection.class }));
        optionsMap.put("git-hub-table", new MutableDataSet().set(INSPECTION_CLASSES, new Class[] { GitHubTableInspection.class }));
    }
    public IntentionSpecTest() {
        super(optionsMap, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }
}
