// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util

import com.vladsch.plugin.util.min

class CountingBag<T>(size: Int = 0) : Bag<T, Int> {
    var counts = HashMap<T, Int>(size)
        private set

    constructor(other: Iterable<T>) : this() {
        addAll(other)
    }

    constructor(other: CountingBag<T>) : this(other.counts.size) {
        counts.putAll(other.counts)
    }

    override fun add(element: T): Boolean {
        counts[element] = counts.computeIfAbsent(element) { 0 } + 1
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach { add(it) }
        return true
    }

    override fun clear() {
        counts.clear()
    }

    override fun iterator(): MutableIterator<T> {
        return counts.keys.iterator()
    }

    override fun remove(element: T): Boolean {
        val count = counts[element] ?: return false
        if (count == 1) counts.remove(element)
        else counts[element] = count - 1
        return true
    }

    override fun getCountOf(element: T): Int {
        return counts.getOrDefault(element, 0)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var removed = true
        elements.forEach { removed = remove(it) && removed }
        return removed
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val other = CountingBag<T>(elements)
        val retained = HashMap<T, Int>()
        var hadAll = true

        other.counts.forEach {
            if (counts.containsKey(it.key)) {
                retained[it.key] = counts[it.key]!!.min(it.value)
            } else {
                hadAll = false
            }
        }

        counts = retained
        return hadAll
    }

    override val size: Int
        get() = counts.map { it.value }.sum()

    override fun getCountOf(elements: Collection<T>): Int {
        var count = 0
        elements.forEach { count += getCountOf(it) }
        return count
    }

    override fun getCountOfAll(): Int {
        return counts.map { it.value }.sum()
    }

    override fun contains(element: T): Boolean {
        return counts.containsKey(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        val other = CountingBag<T>(this)
        elements.forEach {
            if (!other.remove(it)) return false
        }
        return true
    }

    override fun isEmpty(): Boolean {
        return counts.isEmpty()
    }
}
