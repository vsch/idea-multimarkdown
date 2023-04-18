// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util

import java.util.*

/**
 * Used to define a bit set based Enum whose members are bit masks of an int representing
 * a set of flags corresponding to member constants. The integer flags can be mapped to
 * `Set<T>` of that enum's member constants and vice-versa.
 *
 * The convoluted companion is needed otherwise no way that I could figure out how
 * to initialize the enum constant to integer map needed for all the mappings.
 *
 * Member functions for Long and Collections only work in derived classes and since inside these
 * we need an instance of BitSetEnum.this, it will not help to move them out. This was the
 * best and minimal code in use that I could find, and that also worked.
 *
 * If you don't need containsSome or containsAll then you can make the companion a one line without the
 * code in {}.
 *
 *  **Usage**:
 *
 * ```kotlin
 *     enum class MyEnum(val flags:Long) {
 *          MEMBER_1(1),
 *          MEMBER_2(2),
 *          MEMBER_3(4),
 *          MEMBER_4(8);
 *
 *          fun isIn(flags:Long):Boolean = this in flags
 *          companion object : BitSetEnum<MyEnum>(MyEnum::class.java, {it.flags}) {
 *              fun containsSome(flags:Long, vararg enumConstants:MyEnum):Boolean = flags.someIn(*enumConstants)
 *              fun containsAll(flags:Long, vararg enumConstants:MyEnum):Boolean = flags.allIn(*enumConstants)
 *          }
 *     }
 * ```
 *
 *  convert between `Long` and `Array<MyEnum>` or `Collection<MyEnum>`
 *
 * ```kotlin
 * val enumSet = MyEnum.asSet(7)
 * val manualEnumSet = setOf(MyEnum.MEMBER_1, MyEnum.MEMBER_2, MyEnum.MEMBER_3)
 * val enumArray = arrayOf(MyEnum.MEMBER_1, MyEnum.MEMBER_2, MyEnum.MEMBER_3)
 * MyEnum.asFlags(enumSet) == 7          // true
 * MyEnum.asFlags(manualEnumSet) == 7    // true
 * MyEnum.asFlags(enumArray) == 7        // true
 * ```
 *
 * membership using `Enum.isIn` member function
 *
 * ```kotlin
 * MEMBER_1.isIn(5) // true
 * MEMBER_1.isIn(6) // false
 * ```
 *
 * or `containsSome`
 *
 * ```kotlin
 * MyEnum.containsSome(5, MEMBER_1, MEMBER_2)  // true
 * MyEnum.containsSome(12, MEMBER_1, MEMBER_2) // false
 * ```
 *
 * or `containsAll`
 *
 * ```kotlin
 * MyEnum.containsAll(3, MEMBER_1, MEMBER_2)  // true
 * MyEnum.containsAll(11, MEMBER_1, MEMBER_2) // true
 * MyEnum.containsAll(5, MEMBER_1, MEMBER_2)  // false
 * ```
 */

open class LongBitSetEnum<T : Enum<T>>(enumClass: Class<T>, map: (T) -> Long) {

    val enumFlagMap = HashMap<T, Long>()

    init {
        for (enumConstant in enumClass.enumConstants) {
            enumFlagMap.put(enumConstant as T, map(enumConstant))
        }
    }

    fun add(enumConstant: T, flags: Long) {
        enumFlagMap.put(enumConstant, flags)
    }

    fun flags(enumConstant: T): Long {
        return enumFlagMap[enumConstant] ?: 0L
    }

    operator fun Long.contains(enumConstant: T): Boolean {
        val memberFlags = enumFlagMap[enumConstant] ?: 0L
        return memberFlags != 0L && memberFlags and this == memberFlags
    }

    fun Long.someIn(vararg enumConstants: T): Boolean {
        for (enumConstant in enumConstants) {
            if (this.contains(enumConstant)) return true
        }
        return false
    }

    fun Long.allIn(vararg enumConstants: T): Boolean {
        for (enumConstant in enumConstants) {
            if (!this.contains(enumConstant)) return false
        }
        return true
    }

    fun T.isIn(flags: Long): Boolean = flags.contains(this)

    fun asFlags(collection: Collection<T>): Long = collection.fold(0L) { total, it -> total or (enumFlagMap.get(it) ?: 0L) }
    fun asFlags(collection: Array<T>): Long = collection.fold(0L) { total, it -> total or (enumFlagMap.get(it) ?: 0L) }
    fun asFlags(collection: Map<T, Boolean>): Long {
        var flags = 0L
//        collection.forEach() { if (it.value) flags = flags or (enumFlagMap.get(it.key) ?: 0) }
        for (it in collection) {
            if (it.value) flags = flags or (enumFlagMap.get(it.key) ?: 0L)
        }
        return flags
    }

    fun asSet(map: Map<T, Boolean>): Set<T> {
        return asSet(asFlags(map))
    }

    fun asSet(flags: Long): Set<T> {
        val collection = HashSet<T>()
        for (enumConstant in enumFlagMap.keys) {
            if (enumConstant.isIn(flags)) collection.add(enumConstant)
        }
        return collection
    }

    fun asMap(flags: Long): Map<T, Boolean> {
        val collection = HashMap<T, Boolean>()
        for (enumConstant in enumFlagMap.keys) {
            collection.put(enumConstant, enumConstant.isIn(flags))
        }
        return collection
    }

    fun asMap(collection: Array<T>): Map<T, Boolean> {
        return asMap(asFlags(collection))
    }

    fun asMap(collection: Collection<T>): Map<T, Boolean> {
        return asMap(asFlags(collection))
    }

    fun asList(flags: Long): List<T> {
        return asArrayList(flags)
    }

    fun asList(map: Map<T, Boolean>): List<T> {
        return asList(asFlags(map))
    }

    fun asArrayList(map: Map<T, Boolean>): ArrayList<T> {
        return asArrayList(asFlags(map))
    }

    inline fun <reified T1 : T> asArray(map: Map<T, Boolean>): Array<T1> {
        return asArray(asFlags(map))
    }

    fun asArrayList(flags: Long): ArrayList<T> {
        val collection = ArrayList<T>()
        for (enumConstant in enumFlagMap.keys) {
            if (enumConstant.isIn(flags)) collection.add(enumConstant)
        }
        return collection
    }

    fun enumConstant(name: String): T? {
        for (enumConstant in enumFlagMap.keys) {
            if (enumConstant.name == name) return enumConstant
        }
        return null
    }

    inline fun <reified T1 : T> asArray(flags: Long): Array<T1> {
        val collection = ArrayList<T1>()
        for (enumConstant in enumFlagMap.keys) {
            if (enumConstant.isIn(flags)) collection.add(enumConstant as T1)
        }
        return collection.toTypedArray()
    }
}

