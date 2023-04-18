// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util

open class BitField(val bits: Int, val prevField: BitField? = null) : DataPrinterAware {
    val prevBits: Int = prevField?.totalBits ?: 0
    val totalBits: Int = prevBits + bits

    init {
        assert(bits > 0, { "BitField bits $bits must be > 0" })
        assert(totalBits <= 31, { "BitField total bits $totalBits must be <= 31" })
    }

    fun unboxed(flags: Int): Int = (flags and (((1 shl bits) - 1))) shl prevBits
    fun unboxedFlags(flags: Int): Int = flags and ((((1 shl bits) - 1)) shl prevBits)
    fun flags(flags: Int): Int = (flags ushr prevBits) and ((1 shl bits) - 1)

    override fun testData(): String = super.testData() + "($bits${if (prevField != null) "," + prevField.testData() else ""})"
}

abstract class EnumBitField<T>(bits: Int, prevField: BitField? = null) : BitField(bits, prevField) {
    abstract fun boxed(flags: Int): T
}
