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

import com.vladsch.plugin.test.util.ParamRowGenerator
import com.vladsch.plugin.test.util.ParamRowGenerator.ColumnProvider
import com.vladsch.plugin.test.util.ParamRowGenerator.Decorator

class CompletionTestParamRowGenerator(private val lineProvider: LineProvider? = null) : ParamRowGenerator() {
    fun row(type: String, expected: String?, input: String, isDefault: Boolean, isAuto: Boolean): CompletionTestParamRowGenerator {
        val escaped = input.replace("\n", "\\n").replace("\"", "\\\"")
        super.row(1, arrayOf("$type - \"$escaped\"", expected, input, isDefault, isAuto),
            Decorator { _, prefix, suffix -> "$prefix$type - \"$escaped\"\n$suffix" },
            lineProvider,
            ColumnProvider { 26 + type.length + 1 + (expected?.length ?: 3) })
        return this
    }
}
