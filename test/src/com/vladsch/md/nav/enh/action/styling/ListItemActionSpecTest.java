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

package com.vladsch.md.nav.enh.action.styling;

import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.md.nav.enh.testUtil.MdEnhLightPlatformActionSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import org.junit.runners.Parameterized;

import java.util.List;

public class ListItemActionSpecTest extends MdEnhLightPlatformActionSpecTestCase {
    private static final String SPEC_RESOURCE = "list_item_action_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(ListItemActionSpecTest.class, SPEC_RESOURCE);

    public ListItemActionSpecTest() {
        super(null);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return CodeInsightFixtureSpecTestCase.getTests(RESOURCE_LOCATION);
    }
}
