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

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*
import kotlin.test.assertEquals

@RunWith(value = Parameterized::class)
class WantBitFieldTest constructor(val rowId: Int, val bits: Int, val flags: Int, val boxed: Int, val unboxed: Int, val prevField: BitField?) {

    val bitField: BitField

    init {
        bitField = BitField(bits, prevField)
    }

    @Test
    fun testBasic_Flags() {
        val actual = bitField.flags(flags)
        assertEquals(boxed, actual)
    }

    @Test
    fun testBasic_Unboxed() {
        val actual = bitField.unboxedFlags(flags)
        assertEquals(unboxed, actual)
    }

    companion object {
        @Parameterized.Parameters(name = "{index}: bits = {1}, flags = {2}, prevFields = {5}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            val data = completionData
            val amendedData = ArrayList<Array<Any?>>()
            val cleanData = false

            var i = 0
            for (row in data) {
                val amendedRow = Array<Any?>(row.size + 1, { null })
                System.arraycopy(row, 0, amendedRow, 1, row.size)
                amendedRow[0] = i
                amendedData.add(amendedRow)
                i++
            }

            if (cleanData) {
                val header = arrayOf("bits", "flags", "boxed", "unboxed", "prevFields")
                printData(data, header)
            }

            return amendedData
        }

        val completionData = arrayListOf<Array<Any?>>(
            /* @formatter:off */
                /*      arrayOf<Any?>("bits", "flags", "boxed", "unboxed", "prevFields") */
                /*  0 */arrayOf<Any?>(1     , 0      , 0      , 0        , null) /*  0 */,
                /*  1 */arrayOf<Any?>(1     , 1      , 1      , 1        , null) /*  1 */,
                /*  2 */arrayOf<Any?>(1     , 255    , 1      , 1        , null) /*  2 */,
                /*  3 */arrayOf<Any?>(1     , 0      , 0      , 0        , BitField(1)) /*  3 */,
                /*  4 */arrayOf<Any?>(1     , 2      , 1      , 2        , BitField(1)) /*  4 */,
                /*  5 */arrayOf<Any?>(1     , 255    , 1      , 2        , BitField(1)) /*  5 */,
                /*  6 */arrayOf<Any?>(1     , 0      , 0      , 0        , BitField(2)) /*  6 */,
                /*  7 */arrayOf<Any?>(1     , 4      , 1      , 4        , BitField(2)) /*  7 */,
                /*  8 */arrayOf<Any?>(1     , 255    , 1      , 4        , BitField(2)) /*  8 */,
                /*  9 */arrayOf<Any?>(1     , 0      , 0      , 0        , BitField(1,BitField(1))) /*  9 */,
                /* 10 */arrayOf<Any?>(1     , 4      , 1      , 4        , BitField(1,BitField(1))) /* 10 */,
                /* 11 */arrayOf<Any?>(1     , 255    , 1      , 4        , BitField(1,BitField(1))) /* 11 */,
                /* 12 */arrayOf<Any?>(1     , 0      , 0      , 0        , BitField(3)) /* 12 */,
                /* 13 */arrayOf<Any?>(1     , 8      , 1      , 8        , BitField(3)) /* 13 */,
                /* 14 */arrayOf<Any?>(1     , 255    , 1      , 8        , BitField(3)) /* 14 */,
                /* 15 */arrayOf<Any?>(1     , 0      , 0      , 0        , BitField(1,BitField(2))) /* 15 */,
                /* 16 */arrayOf<Any?>(1     , 8      , 1      , 8        , BitField(1,BitField(2))) /* 16 */,
                /* 17 */arrayOf<Any?>(1     , 255    , 1      , 8        , BitField(1,BitField(2))) /* 17 */,
                /* 18 */arrayOf<Any?>(1     , 0      , 0      , 0        , BitField(2,BitField(1))) /* 18 */,
                /* 19 */arrayOf<Any?>(1     , 8      , 1      , 8        , BitField(2,BitField(1))) /* 19 */,
                /* 20 */arrayOf<Any?>(1     , 255    , 1      , 8        , BitField(2,BitField(1))) /* 20 */,
                /* 21 */arrayOf<Any?>(1     , 0      , 0      , 0        , BitField(1,BitField(1,BitField(1)))) /* 21 */,
                /* 22 */arrayOf<Any?>(1     , 8      , 1      , 8        , BitField(1,BitField(1,BitField(1)))) /* 22 */,
                /* 23 */arrayOf<Any?>(1     , 255    , 1      , 8        , BitField(1,BitField(1,BitField(1)))) /* 23 */,
                /* 24 */arrayOf<Any?>(2     , 0      , 0      , 0        , null) /* 24 */,
                /* 25 */arrayOf<Any?>(2     , 1      , 1      , 1        , null) /* 25 */,
                /* 26 */arrayOf<Any?>(2     , 2      , 2      , 2        , null) /* 26 */,
                /* 27 */arrayOf<Any?>(2     , 3      , 3      , 3        , null) /* 27 */,
                /* 28 */arrayOf<Any?>(2     , 255    , 3      , 3        , null) /* 28 */,
                /* 29 */arrayOf<Any?>(2     , 0      , 0      , 0        , BitField(1)) /* 29 */,
                /* 30 */arrayOf<Any?>(2     , 2      , 1      , 2        , BitField(1)) /* 30 */,
                /* 31 */arrayOf<Any?>(2     , 4      , 2      , 4        , BitField(1)) /* 31 */,
                /* 32 */arrayOf<Any?>(2     , 6      , 3      , 6        , BitField(1)) /* 32 */,
                /* 33 */arrayOf<Any?>(2     , 255    , 3      , 6        , BitField(1)) /* 33 */,
                /* 34 */arrayOf<Any?>(2     , 0      , 0      , 0        , BitField(2)) /* 34 */,
                /* 35 */arrayOf<Any?>(2     , 4      , 1      , 4        , BitField(2)) /* 35 */,
                /* 36 */arrayOf<Any?>(2     , 8      , 2      , 8        , BitField(2)) /* 36 */,
                /* 37 */arrayOf<Any?>(2     , 12     , 3      , 12       , BitField(2)) /* 37 */,
                /* 38 */arrayOf<Any?>(2     , 255    , 3      , 12       , BitField(2)) /* 38 */,
                /* 39 */arrayOf<Any?>(2     , 0      , 0      , 0        , BitField(1,BitField(1))) /* 39 */,
                /* 40 */arrayOf<Any?>(2     , 4      , 1      , 4        , BitField(1,BitField(1))) /* 40 */,
                /* 41 */arrayOf<Any?>(2     , 8      , 2      , 8        , BitField(1,BitField(1))) /* 41 */,
                /* 42 */arrayOf<Any?>(2     , 12     , 3      , 12       , BitField(1,BitField(1))) /* 42 */,
                /* 43 */arrayOf<Any?>(2     , 255    , 3      , 12       , BitField(1,BitField(1))) /* 43 */,
                /* 44 */arrayOf<Any?>(2     , 0      , 0      , 0        , BitField(3)) /* 44 */,
                /* 45 */arrayOf<Any?>(2     , 8      , 1      , 8        , BitField(3)) /* 45 */,
                /* 46 */arrayOf<Any?>(2     , 16     , 2      , 16       , BitField(3)) /* 46 */,
                /* 47 */arrayOf<Any?>(2     , 24     , 3      , 24       , BitField(3)) /* 47 */,
                /* 48 */arrayOf<Any?>(2     , 255    , 3      , 24       , BitField(3)) /* 48 */,
                /* 49 */arrayOf<Any?>(2     , 0      , 0      , 0        , BitField(1,BitField(2))) /* 49 */,
                /* 50 */arrayOf<Any?>(2     , 8      , 1      , 8        , BitField(1,BitField(2))) /* 50 */,
                /* 51 */arrayOf<Any?>(2     , 16     , 2      , 16       , BitField(1,BitField(2))) /* 51 */,
                /* 52 */arrayOf<Any?>(2     , 24     , 3      , 24       , BitField(1,BitField(2))) /* 52 */,
                /* 53 */arrayOf<Any?>(2     , 255    , 3      , 24       , BitField(1,BitField(2))) /* 53 */,
                /* 54 */arrayOf<Any?>(2     , 0      , 0      , 0        , BitField(2,BitField(1))) /* 54 */,
                /* 55 */arrayOf<Any?>(2     , 8      , 1      , 8        , BitField(2,BitField(1))) /* 55 */,
                /* 56 */arrayOf<Any?>(2     , 16     , 2      , 16       , BitField(2,BitField(1))) /* 56 */,
                /* 57 */arrayOf<Any?>(2     , 24     , 3      , 24       , BitField(2,BitField(1))) /* 57 */,
                /* 58 */arrayOf<Any?>(2     , 255    , 3      , 24       , BitField(2,BitField(1))) /* 58 */,
                /* 59 */arrayOf<Any?>(2     , 0      , 0      , 0        , BitField(1,BitField(1,BitField(1)))) /* 59 */,
                /* 60 */arrayOf<Any?>(2     , 8      , 1      , 8        , BitField(1,BitField(1,BitField(1)))) /* 60 */,
                /* 61 */arrayOf<Any?>(2     , 16     , 2      , 16       , BitField(1,BitField(1,BitField(1)))) /* 61 */,
                /* 62 */arrayOf<Any?>(2     , 24     , 3      , 24       , BitField(1,BitField(1,BitField(1)))) /* 62 */,
                /* 63 */arrayOf<Any?>(2     , 255    , 3      , 24       , BitField(1,BitField(1,BitField(1)))) /* 63 */,
                /* 64 */arrayOf<Any?>(2     , 0x7FFFFFFF    , 3      , 0x60000000       , BitField(29)) /* 64 */
                /* @formatter:on */
        )
    }
}
