/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

package com.vladsch.md.nav.parser.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.FlexmarkSpecExampleRenderer;
import com.vladsch.flexmark.test.util.SpecExampleRenderer;
import com.vladsch.flexmark.test.util.TestUtils;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.language.MdCodeStyleSettings;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdHtmlSettings;
import com.vladsch.md.nav.settings.MdParserSettings;
import com.vladsch.md.nav.settings.MdPreviewSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.testUtil.MdEnhSpecTestSetup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public abstract class MdFlexmarkSpecTestBase extends ComboSpecTestCase {
    public static final DataKey<Boolean> IGNORE = TestUtils.IGNORE;
    public static final DataKey<Boolean> FAIL = TestUtils.FAIL;
    public static final DataKey<Boolean> NO_FILE_EOL = TestUtils.NO_FILE_EOL;
    public static final DataKey<Integer> TIMED_ITERATIONS = TestUtils.TIMED_ITERATIONS;
    public static final DataKey<Boolean> EMBED_TIMED = TestUtils.EMBED_TIMED;
    public static final DataKey<Boolean> TIMED = TestUtils.TIMED;
    final static public DataKey<Consumer<MdApplicationSettings>> APPLICATION_SETTINGS = MdEnhSpecTestSetup.APPLICATION_SETTINGS;
    final static public DataKey<Consumer<MdRenderingProfile>> RENDERING_PROFILE = MdEnhSpecTestSetup.RENDERING_PROFILE;
    final static public DataKey<Consumer<MdParserSettings>> PARSER_SETTINGS = MdEnhSpecTestSetup.PARSER_SETTINGS;
    final static public DataKey<Consumer<MdPreviewSettings>> PREVIEW_SETTINGS = MdEnhSpecTestSetup.PREVIEW_SETTINGS;
    final static public DataKey<Consumer<MdHtmlSettings>> HTML_SETTINGS = MdEnhSpecTestSetup.HTML_SETTINGS;
    final static public DataKey<Consumer<MdCodeStyleSettings>> STYLE_SETTINGS = MdEnhSpecTestSetup.STYLE_SETTINGS;
    final public static Consumer<MdRenderingProfile> BASIC_OPTIONS = MdEnhSpecTestSetup.BASIC_OPTIONS;
    final public static Consumer<MdRenderingProfile> ENHANCED_OPTIONS = MdEnhSpecTestSetup.ENHANCED_OPTIONS;
    final public static Consumer<MdRenderingProfile> LEGACY_OPTIONS = MdEnhSpecTestSetup.LEGACY_OPTIONS;

    // standard options
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(HtmlRenderer.RENDER_HEADER_ID, true)
            .set(HtmlRenderer.INDENT_SIZE, 2)
            .toImmutable();

    public MdFlexmarkSpecTestBase(@NotNull SpecExample example, @Nullable Map<String, ? extends DataHolder> optionMap, @Nullable DataHolder... defaultOptions) {
        super(example, optionMap, dataHolders(OPTIONS, defaultOptions));
    }

    @Override
    @NotNull
    final public SpecExampleRenderer getSpecExampleRenderer(@NotNull SpecExample example, @Nullable DataHolder exampleOptions) {
        DataHolder OPTIONS = aggregate(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, OPTIONS, new FlexmarkLexParser(OPTIONS), new FlexmarkLexRenderer(OPTIONS), true);
    }
}
