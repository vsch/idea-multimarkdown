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

package com.vladsch.md.nav.language.completion

import com.vladsch.md.nav.language.completion.util.MdCompletionContext
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class EmojiShortcutCompletionContextTest : CompletionContextTestBase() {

    override val context: MdCompletionContext get() = EmojiShortcutCompletion.completionContext

    companion object {
        @Parameterized.Parameters(name = "{1}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            return CompletionTestParamRowGenerator()
                .row("auto", null, "abc⦙", false, true)
                .row("auto", null, "abc ⦙", false, true)
                .row("auto", null, "abc: ⦙", false, true)
                .row("auto", null, "a:bc ⦙", false, true)
                .row("auto", null, ":abc ⦙", false, true)
                .row("auto", null, "abc ⦙:", false, true)
                .row("auto", null, "abc ⦙ :", false, true)
                .row("auto", null, "abc:⦙", false, true)
                .row("auto", null, "abc :⦙", false, true)
                .row("auto", "abc :⟦w⦙⟧", "abc :w⦙", false, true)
                .row("auto", "abc :⟦w⦙ar⟧", "abc :w⦙ar", false, true)
                .row("auto", "abc :⟦war⦙0123⟧ ", "abc :war⦙0123 ", false, true)
                .row("auto", "abc :⟦w⦙arning⟧ text", "abc :w⦙arning text", false, true)
                .row("auto", "⟦w⦙⟧", "w⦙", true, true)
                .row("auto", "⟦wa⦙⟧", "wa⦙", true, true)
                .row("auto", "⟦wa⦙rning⟧", "wa⦙rning", true, true)

                .row("basic", "abc :⟦⦙⟧", "abc :⦙", false, false)
                .row("basic", ":⟦w⦙⟧", ":w⦙", false, false)
                .row("basic", ":⟦wa⦙⟧", ":wa⦙", false, false)
                .row("basic", ":⟦wa⦙rning⟧", ":wa⦙rning", false, false)
                .row("basic", null, "⦙", false, false)
                .row("basic", null, "w⦙", false, false)
                .row("basic", null, "w⦙", false, false)
                .row("basic", null, "wa⦙", false, false)
                .row("basic", null, "wa⦙rning", false, false)

                .row("default", "⟦abc⦙⟧", "abc⦙", true, true)
                .row("default", "⟦abc ⦙⟧", "abc ⦙", true, true)
                .row("default", "⟦w⦙⟧", "w⦙", true, true)
                .row("default", "⟦wa⦙⟧", "wa⦙", true, true)
                .row("default", "⟦wa⦙rning⟧", "wa⦙rning", true, true)

                .rows
        }
    }

    @Test
    fun test_case() {
        assertEquals(location, expected, input.wantParams(isDefault, isAuto))
    }
}

