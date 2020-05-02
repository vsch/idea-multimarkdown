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

package com.vladsch.md.nav.flex.language.completion

import com.vladsch.md.nav.flex.language.FlexmarkOptionsKeywordCompletion
import com.vladsch.md.nav.language.completion.CompletionContextTestBase
import com.vladsch.md.nav.language.completion.CompletionTestParamRowGenerator
import com.vladsch.md.nav.language.completion.util.MdCompletionContext
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class FlexmarkOptionsKeywordCompletionContextTest : CompletionContextTestBase() {

    override val context: MdCompletionContext get() = FlexmarkOptionsKeywordCompletion.completionContext

    companion object {
        @Parameterized.Parameters(name = "{1}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            return CompletionTestParamRowGenerator()
                .row("auto", "⟦⦙⟧", "⦙", isDefault = false, isAuto = true)
                .row("auto", "⟦o⦙⟧", "o⦙", isDefault = false, isAuto = true)
                .row("auto", "⟦op⦙⟧", "op⦙", isDefault = false, isAuto = true)
                .row("auto", "⟦opt⦙⟧", "opt⦙", isDefault = false, isAuto = true)
                .row("auto", "⟦opti⦙⟧", "opti⦙", isDefault = false, isAuto = true)
                .row("auto", "⟦optio⦙⟧", "optio⦙", isDefault = false, isAuto = true)
                .row("auto", "⟦option⦙⟧", "option⦙", isDefault = false, isAuto = true)
                .row("auto", null, "options⦙", isDefault = false, isAuto = true)

                .row("auto", "abc ⟦⦙⟧", "abc ⦙", isDefault = false, isAuto = true)
                .row("auto", "abc ⟦o⦙⟧", "abc o⦙", isDefault = false, isAuto = true)
                .row("auto", "abc ⟦op⦙⟧", "abc op⦙", isDefault = false, isAuto = true)
                .row("auto", "abc ⟦opt⦙⟧", "abc opt⦙", isDefault = false, isAuto = true)
                .row("auto", "abc ⟦opti⦙⟧", "abc opti⦙", isDefault = false, isAuto = true)
                .row("auto", "abc ⟦optio⦙⟧", "abc optio⦙", isDefault = false, isAuto = true)
                .row("auto", "abc ⟦option⦙⟧", "abc option⦙", isDefault = false, isAuto = true)
                .row("auto", null, "abc options⦙", isDefault = false, isAuto = true)

                .row("auto", null, "abc⦙", isDefault = false, isAuto = true)
                .row("auto", null, "abco⦙", isDefault = false, isAuto = true)
                .row("auto", null, "abcop⦙", isDefault = false, isAuto = true)
                .row("auto", null, "abcopt⦙", isDefault = false, isAuto = true)
                .row("auto", null, "abcopti⦙", isDefault = false, isAuto = true)
                .row("auto", null, "abcoptio⦙", isDefault = false, isAuto = true)
                .row("auto", null, "abcoption⦙", isDefault = false, isAuto = true)
                .row("auto", null, "abcoptions⦙", isDefault = false, isAuto = true)

                .row("basic", "abc ⟦⦙⟧", "abc ⦙", isDefault = false, isAuto = false)

                .row("default", "⟦opt⦙⟧", "opt⦙", isDefault = true, isAuto = true)

                .rows
        }
    }

    @Test
    fun test_case() {
        assertEquals(location, expected, input.wantParams(isDefault, isAuto))
    }
}

