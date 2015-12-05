/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.util

open class BitField(val bits: Int, val prevField: BitField? = null) : DataPrinterAware {
    val prevBits: Int = prevField?.totalBits ?: 0
    val totalBits: Int = prevBits + bits

    init {
        assert(bits > 0, {"BitField bits $bits must be > 0"})
        assert(totalBits <= 31, {"BitField total bits $totalBits must be <= 31"})
    }

    fun unboxed(flags: Int): Int = (flags and (((1 shl bits) - 1))) shl prevBits
    fun unboxedFlags(flags: Int): Int = flags and ((((1 shl bits) - 1)) shl prevBits)
    fun flags(flags: Int): Int = (flags ushr prevBits) and ((1 shl bits) - 1)

    override fun testData():String = super.testData() + "($bits${if (prevField != null) "," + prevField.testData() else ""})"
}

abstract class EnumBitField<T>(bits: Int, prevField: BitField? = null) : BitField(bits, prevField) {
    abstract fun boxed(flags: Int): T
}
