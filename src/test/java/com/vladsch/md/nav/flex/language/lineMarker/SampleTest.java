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

package com.vladsch.md.nav.flex.language.lineMarker;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.FlexmarkSpecExampleRenderer;
import com.vladsch.flexmark.test.util.SpecExampleRenderer;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SampleTest extends ComboSpecTestCase {
    private static final String SPEC_RESOURCE = "sample_spec_test.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(SampleTest.class, SPEC_RESOURCE);
    public static final DataKey<Boolean> SELF = new DataKey<>("SELF", false);
    public static final DataKey<Integer> SELF_MARGIN = new DataKey<>("SELF_MARGIN", -1);

    // standard options
    private static final DataHolder OPTIONS = new MutableDataSet()
            .toImmutable();

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("self", new MutableDataSet().set(SELF, true));
        optionsMap.put("margin", new MutableDataSet().set(CUSTOM_OPTION, SampleTest::marginOption));
    }
    public SampleTest(@NotNull SpecExample example) {
        super(example, optionsMap, OPTIONS);
    }

    public static DataHolder marginOption(String option, String params) {
        int rightMargin = -1;
        if (params != null) {
            if (!params.matches("\\d*")) {
                throw new IllegalStateException("'margin' option requires a numeric or empty (for default margin) argument");
            }

            rightMargin = Integer.parseInt(params);
        }

        int finalRightMargin = rightMargin;
        return new MutableDataSet().set(SELF_MARGIN, finalRightMargin);
    }

    @Override
    @NotNull
    final public SpecExampleRenderer getSpecExampleRenderer(@NotNull SpecExample example, @Nullable DataHolder exampleOptions) {
        DataHolder OPTIONS = aggregate(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, OPTIONS, Parser.builder(OPTIONS).build(), HtmlRenderer.builder(OPTIONS).build(), true);
    }
}
