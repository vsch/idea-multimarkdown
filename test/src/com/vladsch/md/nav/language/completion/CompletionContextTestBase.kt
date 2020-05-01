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
import com.vladsch.md.nav.language.completion.util.TextContext
import com.vladsch.md.nav.enh.testUtil.MdEnhSpecTestSetup
import com.vladsch.plugin.util.TestUtils
import com.vladsch.plugin.util.toBased
import com.vladsch.plugin.util.indexOrNull

open class CompletionContextTestBase(val context: MdCompletionContext) {
    fun String.params(): TextContext? {
        val pos = indexOf('|')
        return context.getContext(substring(0, pos) + TestUtils.DUMMY_IDENTIFIER + substring(pos + 1), 0, pos, false)
    }

    fun String.wantParams(isDefault: Boolean, isAutoPopup: Boolean): String? {
        val pos = indexOf(TestUtils.CARET_CHAR).indexOrNull() ?: indexOf('|').indexOrNull() ?: throw IllegalStateException("Caret position missing, use ${TestUtils.CARET_CHAR} or | to mark caret")
        val elementText = substring(0, pos) + TestUtils.DUMMY_IDENTIFIER + substring(pos + 1)
        val params = context.getContext(elementText, 0, pos, isDefault) ?: return null
        val wantParams = context.wantParams(params, isDefault, isAutoPopup)
        if (!wantParams) return null

        // everything after pos needs to skip | dummy identifier
        return substring(0, pos - params.prefix.length) + TestUtils.START_CHAR + params.prefix + TestUtils.CARET_CHAR + substring(pos + 1, params.replacementOffset + 1) + TestUtils.END_CHAR + substring(params.replacementOffset + 1)
    }

    fun Context(
        prefix: CharSequence,              // prefix for completion
        replacementOffset: Int,            // end position for id to set for completion context
        beforeStartChar: Char,             // character before start marker or '\0' if marker at start of text
        beforeStartChars: CharSequence,    // characters before start marker
        afterEndChar: Char,                // character after endPos or '\0' if at end of text
        afterCaretChar: Char,              // character after caret position or '\0' if at end of text
        afterCaretChars: CharSequence,     // characters after caret position
        hasEndMarker: Boolean              // true if afterChar == ':'
    ): TextContext {
        return TextContext(
            prefix.toBased(),
            replacementOffset,
            beforeStartChar,
            beforeStartChars.toBased(),
            afterEndChar,
            afterCaretChar,
            afterCaretChars.toBased(),
            hasEndMarker
        )
    }

    companion object {
        init {
            MdEnhSpecTestSetup.RUNNING_TESTS()
        }
    }
}

