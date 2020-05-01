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
package com.vladsch.md.nav.util

import com.intellij.openapi.util.TextRange
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.flexmark.util.sequence.LineAppendableImpl
import org.junit.Assert.assertEquals
import org.junit.Test

class MdIndentConverterTest {

    private fun originalLines(lines: String): List<BasedSequence> {
        return LineAppendableImpl(0).append(lines).lines.toList()
    }

    private fun lines(lines: String): List<BasedSequence> {
            return LineAppendableImpl(0).append(lines).linesInfo.map { it.textNoEOL }.toList()
    }

    @Test
    @Throws(Exception::class)
    fun test_Basic() {
        val original = """0123456789
0123456789
0123456789
0123456789
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """    0123456789
    0123456789
    0123456789
    0123456789
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), null, null)
        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(original, "", "", false, null, null))
        for (i in 0 .. decoded.length) {
            assertEquals("$i should be $i", i, converter.getOffsetInHost(i, range))
        }

        assertEquals(44, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Basic0() {
        val original = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """    0123456789
    0123456789
    0123456789
    0123456789
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(original), null)
        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(original, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(original, "", "", false, null, null))
        assertEquals(0, converter.getOffsetInHost(0, range))
        assertEquals(9, converter.getOffsetInHost(9, range))
        assertEquals(10, converter.getOffsetInHost(10, range))
        assertEquals(24, converter.getOffsetInHost(24, range))
        assertEquals(42, converter.getOffsetInHost(42, range))
        assertEquals(43, converter.getOffsetInHost(43, range))
        assertEquals(44, converter.getOffsetInHost(44, range))
        assertEquals(44, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Basic1() {
        val original = """    0123456789
    0123456789
    0123456789
    0123456789
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """    0123456789
    0123456789
    0123456789
    0123456789
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(decoded), null)

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "", false, null, null))

        assertEquals(4, converter.getOffsetInHost(0, range))
        assertEquals(13, converter.getOffsetInHost(9, range))
        assertEquals(14, converter.getOffsetInHost(10, range))

        assertEquals(19, converter.getOffsetInHost(11, range))
        assertEquals(20, converter.getOffsetInHost(12, range))
        assertEquals(29, converter.getOffsetInHost(21, range))

        assertEquals(34, converter.getOffsetInHost(22, range))
        assertEquals(43, converter.getOffsetInHost(31, range))
        assertEquals(44, converter.getOffsetInHost(32, range))

        assertEquals(49, converter.getOffsetInHost(33, range))
        assertEquals(58, converter.getOffsetInHost(42, range))
        assertEquals(59, converter.getOffsetInHost(43, range))

        assertEquals(60, converter.getOffsetInHost(44, range))
        assertEquals(60, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Basic2() {
        val original = """    0123456789
>     0123456789
>     0123456789
>     0123456789
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """    0123456789
>     0123456789
>     0123456789
>     0123456789
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(decoded), null)

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "> ", false, null, null))

        assertEquals(4, converter.getOffsetInHost(0, range))
        assertEquals(13, converter.getOffsetInHost(9, range))
        assertEquals(14, converter.getOffsetInHost(10, range))

        assertEquals(21, converter.getOffsetInHost(11, range))
        assertEquals(22, converter.getOffsetInHost(12, range))
        assertEquals(31, converter.getOffsetInHost(21, range))

        assertEquals(38, converter.getOffsetInHost(22, range))
        assertEquals(47, converter.getOffsetInHost(31, range))
        assertEquals(48, converter.getOffsetInHost(32, range))

        assertEquals(55, converter.getOffsetInHost(33, range))
        assertEquals(64, converter.getOffsetInHost(42, range))
        assertEquals(65, converter.getOffsetInHost(43, range))

        assertEquals(66, converter.getOffsetInHost(44, range))
        assertEquals(66, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Fenced1() {
        val original = """0123456789
> 0123456789
> 0123456789
> 0123456789
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """0123456789
> 0123456789
> 0123456789
> 0123456789
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(decoded), null)

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "> ", true, null, null))

        assertEquals(0, converter.getOffsetInHost(0, range))
        assertEquals(9, converter.getOffsetInHost(9, range))
        assertEquals(10, converter.getOffsetInHost(10, range))

        assertEquals(13, converter.getOffsetInHost(11, range))
        assertEquals(14, converter.getOffsetInHost(12, range))
        assertEquals(23, converter.getOffsetInHost(21, range))

        assertEquals(26, converter.getOffsetInHost(22, range))
        assertEquals(35, converter.getOffsetInHost(31, range))
        assertEquals(36, converter.getOffsetInHost(32, range))

        assertEquals(39, converter.getOffsetInHost(33, range))
        assertEquals(48, converter.getOffsetInHost(42, range))
        assertEquals(49, converter.getOffsetInHost(43, range))

        assertEquals(50, converter.getOffsetInHost(44, range))
        assertEquals(50, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_BasicSemi() {
        val original = """0123456789;
0123456789;
0123456789;
0123456789;
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """    0123456789;
    0123456789;
    0123456789;
    0123456789;
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(original), lines(decoded))

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "", false, ";", null))
        assertEquals(0, converter.getOffsetInHost(0, range))
        assertEquals(9, converter.getOffsetInHost(9, range))
        assertEquals(10, converter.getOffsetInHost(10, range))

        assertEquals(12, converter.getOffsetInHost(11, range))
        assertEquals(13, converter.getOffsetInHost(12, range))
        assertEquals(22, converter.getOffsetInHost(21, range))

        assertEquals(24, converter.getOffsetInHost(22, range))
        assertEquals(33, converter.getOffsetInHost(31, range))
        assertEquals(34, converter.getOffsetInHost(32, range))

        assertEquals(36, converter.getOffsetInHost(33, range))
        assertEquals(45, converter.getOffsetInHost(42, range))
        assertEquals(46, converter.getOffsetInHost(43, range))

        assertEquals(48, converter.getOffsetInHost(44, range))
        assertEquals(48, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Basic1Semi() {
        val original = """    0123456789;
    0123456789;
    0123456789;
    0123456789;
"""

        val unprefixed = """0123456789;
0123456789;
0123456789;
0123456789;
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """    0123456789;
    0123456789;
    0123456789;
    0123456789;
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(unprefixed), lines(decoded))

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "", false, ";", null))

        assertEquals(4, converter.getOffsetInHost(0, range))
        assertEquals(13, converter.getOffsetInHost(9, range))
        assertEquals(14, converter.getOffsetInHost(10, range))

        assertEquals(20, converter.getOffsetInHost(11, range))
        assertEquals(21, converter.getOffsetInHost(12, range))
        assertEquals(30, converter.getOffsetInHost(21, range))

        assertEquals(36, converter.getOffsetInHost(22, range))
        assertEquals(45, converter.getOffsetInHost(31, range))
        assertEquals(46, converter.getOffsetInHost(32, range))

        assertEquals(52, converter.getOffsetInHost(33, range))
        assertEquals(61, converter.getOffsetInHost(42, range))
        assertEquals(62, converter.getOffsetInHost(43, range))

        assertEquals(64, converter.getOffsetInHost(44, range))
        assertEquals(64, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Basic2Semi() {
        val original = """    0123456789;
>     0123456789;
>     0123456789;
>     0123456789;
"""

        val unprefixed = """0123456789;
0123456789;
0123456789;
0123456789;
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """    0123456789;
>     0123456789;
>     0123456789;
>     0123456789;
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(unprefixed), lines(decoded))

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "> ", false, ";", null))

        assertEquals(4, converter.getOffsetInHost(0, range))
        assertEquals(13, converter.getOffsetInHost(9, range))
        assertEquals(14, converter.getOffsetInHost(10, range))

        assertEquals(22, converter.getOffsetInHost(11, range))
        assertEquals(23, converter.getOffsetInHost(12, range))
        assertEquals(32, converter.getOffsetInHost(21, range))

        assertEquals(40, converter.getOffsetInHost(22, range))
        assertEquals(49, converter.getOffsetInHost(31, range))
        assertEquals(50, converter.getOffsetInHost(32, range))

        assertEquals(58, converter.getOffsetInHost(33, range))
        assertEquals(67, converter.getOffsetInHost(42, range))
        assertEquals(68, converter.getOffsetInHost(43, range))

        assertEquals(70, converter.getOffsetInHost(44, range))
        assertEquals(70, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Fenced1Semi() {
        val original = """0123456789;
> 0123456789;
> 0123456789;
> 0123456789;
"""

        val unprefixed = """0123456789;
0123456789;
0123456789;
0123456789;
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """0123456789;
> 0123456789;
> 0123456789;
> 0123456789;
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(unprefixed), lines(decoded))

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "> ", true, ";", null))

        assertEquals(0, converter.getOffsetInHost(0, range))
        assertEquals(9, converter.getOffsetInHost(9, range))
        assertEquals(10, converter.getOffsetInHost(10, range))

        assertEquals(14, converter.getOffsetInHost(11, range))
        assertEquals(15, converter.getOffsetInHost(12, range))
        assertEquals(24, converter.getOffsetInHost(21, range))

        assertEquals(28, converter.getOffsetInHost(22, range))
        assertEquals(37, converter.getOffsetInHost(31, range))
        assertEquals(38, converter.getOffsetInHost(32, range))

        assertEquals(42, converter.getOffsetInHost(33, range))
        assertEquals(51, converter.getOffsetInHost(42, range))
        assertEquals(52, converter.getOffsetInHost(43, range))

        assertEquals(54, converter.getOffsetInHost(44, range))
        assertEquals(54, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    fun test_Basic1Semi2() {
        val original = """    0123456789;;
    0123456789;;
    0123456789;;
    0123456789;;
"""

        val unprefixed = """0123456789;;
0123456789;;
0123456789;;
0123456789;;
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """    0123456789;
    0123456789;
    0123456789;
    0123456789;
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(unprefixed), lines(decoded))

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "", false, ";", null))

        assertEquals(4, converter.getOffsetInHost(0, range))
        assertEquals(13, converter.getOffsetInHost(9, range))
        assertEquals(14, converter.getOffsetInHost(10, range))

        assertEquals(21, converter.getOffsetInHost(11, range))
        assertEquals(22, converter.getOffsetInHost(12, range))
        assertEquals(31, converter.getOffsetInHost(21, range))

        assertEquals(38, converter.getOffsetInHost(22, range))
        assertEquals(47, converter.getOffsetInHost(31, range))
        assertEquals(48, converter.getOffsetInHost(32, range))

        assertEquals(55, converter.getOffsetInHost(33, range))
        assertEquals(64, converter.getOffsetInHost(42, range))
        assertEquals(65, converter.getOffsetInHost(43, range))

        assertEquals(68, converter.getOffsetInHost(44, range))
        assertEquals(68, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Basic2Semi2() {
        val original = """    0123456789;
>     0123456789;
>     0123456789;
>     0123456789;
"""

        val unprefixed = """0123456789;
0123456789;
0123456789;
0123456789;
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """    0123456789;
>     0123456789;
>     0123456789;
>     0123456789;
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(unprefixed), lines(decoded))

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "> ", false, ";", null))

        assertEquals(4, converter.getOffsetInHost(0, range))
        assertEquals(13, converter.getOffsetInHost(9, range))
        assertEquals(14, converter.getOffsetInHost(10, range))

        assertEquals(22, converter.getOffsetInHost(11, range))
        assertEquals(23, converter.getOffsetInHost(12, range))
        assertEquals(32, converter.getOffsetInHost(21, range))

        assertEquals(40, converter.getOffsetInHost(22, range))
        assertEquals(49, converter.getOffsetInHost(31, range))
        assertEquals(50, converter.getOffsetInHost(32, range))

        assertEquals(58, converter.getOffsetInHost(33, range))
        assertEquals(67, converter.getOffsetInHost(42, range))
        assertEquals(68, converter.getOffsetInHost(43, range))

        assertEquals(70, converter.getOffsetInHost(44, range))
        assertEquals(70, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Fenced1Semi2() {
        val original = """0123456789;
> 0123456789;
> 0123456789;
> 0123456789;
"""

        val unprefixed = """0123456789;
0123456789;
0123456789;
0123456789;
"""

        val decoded = """0123456789
0123456789
0123456789
0123456789
"""

        val encoded = """0123456789;
> 0123456789;
> 0123456789;
> 0123456789;
"""

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(unprefixed), lines(decoded))

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "> ", true, ";", null))

        assertEquals(0, converter.getOffsetInHost(0, range))
        assertEquals(9, converter.getOffsetInHost(9, range))
        assertEquals(10, converter.getOffsetInHost(10, range))

        assertEquals(14, converter.getOffsetInHost(11, range))
        assertEquals(15, converter.getOffsetInHost(12, range))
        assertEquals(24, converter.getOffsetInHost(21, range))

        assertEquals(28, converter.getOffsetInHost(22, range))
        assertEquals(37, converter.getOffsetInHost(31, range))
        assertEquals(38, converter.getOffsetInHost(32, range))

        assertEquals(42, converter.getOffsetInHost(33, range))
        assertEquals(51, converter.getOffsetInHost(42, range))
        assertEquals(52, converter.getOffsetInHost(43, range))

        assertEquals(54, converter.getOffsetInHost(44, range))
        assertEquals(54, converter.getOffsetInHost(45, range))
        assertEquals(-1, converter.getOffsetInHost(46, range))
    }

    @Test
    @Throws(Exception::class)
    fun test_Fenced2Semi2() {
        val original = """0123456789;
> 0123456789;
>,
> 0123456789;
> 0123456789;
""".replace(',', ' ')

        val unprefixed = """0123456789;
0123456789;

0123456789;
0123456789;
"""

        val decoded = """0123456789
0123456789

0123456789
0123456789
"""

        val encoded = """0123456789;
> 0123456789;
>,
> 0123456789;
> 0123456789;
""".replace(',', ' ')

        val converter = MdIndentConverter(original, 0, originalLines(original), lines(unprefixed), lines(decoded))

        val outChars = StringBuilder()
        val range = TextRange.allOf(original)
        val wasDecoded = converter.decode(range, outChars)

        assertEquals(true, wasDecoded)
        assertEquals(decoded, outChars.toString())
        assertEquals(encoded, MdIndentConverter.encode(decoded, "", "> ", true, ";", null))

        // line 1
        assertEquals(0, converter.getOffsetInHost(0, range))
        assertEquals(9, converter.getOffsetInHost(9, range))
        assertEquals(10, converter.getOffsetInHost(10, range))

        // line 2
        assertEquals(14, converter.getOffsetInHost(11, range))
        assertEquals(15, converter.getOffsetInHost(12, range))
        assertEquals(24, converter.getOffsetInHost(21, range))

        // line 3
        assertEquals(28, converter.getOffsetInHost(22, range))
        assertEquals(31, converter.getOffsetInHost(23, range))

        // line 4
        assertEquals(32, converter.getOffsetInHost(24, range))
        assertEquals(33, converter.getOffsetInHost(25, range))
        assertEquals(41, converter.getOffsetInHost(33, range))
        assertEquals(45, converter.getOffsetInHost(34, range))

        // line 5
        assertEquals(46, converter.getOffsetInHost(35, range))
        assertEquals(55, converter.getOffsetInHost(44, range))

        assertEquals(57, converter.getOffsetInHost(45, range))
        assertEquals(57, converter.getOffsetInHost(46, range))
        assertEquals(-1, converter.getOffsetInHost(47, range))
    }
}
