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

package com.vladsch.md.nav.testUtil.cases;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger;
import com.vladsch.md.nav.psi.element.MdFile;
import com.vladsch.plugin.test.util.cases.LightFixtureActionSpecTest;
import com.vladsch.plugin.test.util.cases.SpecTest;
import com.vladsch.plugin.test.util.renderers.ActionSpecRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.vladsch.md.nav.enh.testUtil.MdEnhTestActions.ListBulletItems;
import static com.vladsch.md.nav.enh.testUtil.MdEnhTestActions.ListOrderedItems;
import static com.vladsch.md.nav.enh.testUtil.MdEnhTestActions.ListTaskItems;
import static com.vladsch.md.nav.enh.testUtil.MdEnhTestActions.ListToggleTaskItemDone;
import static com.vladsch.md.nav.enh.testUtil.MdEnhTestActions.ReformatDocument;
import static com.vladsch.md.nav.enh.testUtil.MdEnhTestActions.ReformatElement;

public interface MdLightFixtureActionSpecTest extends MdCodeInsightFixtureSpecTestCase, LightFixtureActionSpecTest {

    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdCodeInsightFixtureSpecTestCase.getOptionsMap());
                optionsMap.putAll(LightFixtureActionSpecTest.getOptionsMap());

                optionsMap.put("number-action", new MutableDataSet().set(ACTION_NAME, ListOrderedItems));
                optionsMap.put("task-action", new MutableDataSet().set(ACTION_NAME, ListTaskItems));
                optionsMap.put("toggle-task-done", new MutableDataSet().set(ACTION_NAME, ListToggleTaskItemDone));
                optionsMap.put("bullet-action", new MutableDataSet().set(ACTION_NAME, ListBulletItems));
                optionsMap.put("format-element", new MutableDataSet().set(ACTION_NAME, ReformatElement));
            }
            return optionsMap;
        }
    }

    @NotNull
    BasedSequence getFormatResult();

    void setFormatResult(@NotNull BasedSequence formatResult);

    @Override
    default <T extends LightFixtureActionSpecTest> void beforeDoTestAction(@NotNull ActionSpecRenderer<T> specRenderer, @NotNull DataHolder specRendererOptions) {
        IndentingLogger.LOG_COMPUTE.debug(MdCodeInsightFixtureSpecTestCase.BANNER_AFTER_ACTION);
        if (SpecTest.WANT_RANGES.get(specRendererOptions)) {
            setFormatResult(BasedSequence.NULL);
            specRenderer.getResultFile().putUserData(MdFile.FORMAT_RESULT, BasedSequence.NULL);
        } else {
            specRenderer.getResultFile().putUserData(MdFile.FORMAT_RESULT, null);
        }
    }

    @Override
    default <T extends LightFixtureActionSpecTest> void afterDoTestAction(@NotNull ActionSpecRenderer<T> specRenderer, @NotNull DataHolder specRendererOptions) {
        BasedSequence result = specRenderer.getResultFile().getUserData(MdFile.FORMAT_RESULT);
        specRenderer.getResultFile().putUserData(MdFile.FORMAT_RESULT, null);
        setFormatResult(result == null ? BasedSequence.NULL : result);
    }

    @Override
    default <T extends LightFixtureActionSpecTest> void renderTesActionHtml(@NotNull StringBuilder html, @NotNull ActionSpecRenderer<T> specRenderer, DataHolder specRendererOptions) {
        if (SpecTest.WANT_RANGES.get(specRendererOptions) && getFormatResult().isNotNull()) {
            specRenderer.renderRanges(html, getFormatResult());
        }
    }
}
