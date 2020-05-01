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

package com.vladsch.md.nav.enh.testUtil.renderers;

import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.md.nav.testUtil.renderers.MdActionSpecRenderer;
import com.vladsch.plugin.test.util.cases.LightFixtureActionSpecTest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdEnhActionSpecRenderer<T extends LightFixtureActionSpecTest> extends MdActionSpecRenderer<T> {
    public MdEnhActionSpecRenderer(@NotNull T specTestBase, @NotNull SpecExample example, @Nullable DataHolder options) {
        super(specTestBase, example, options);
    }

    @Override
    protected void executeRendererAction(@NotNull String action) {
        switch (action) {
            default:
                super.executeRendererAction(action);
                break;
        }
    }
}
