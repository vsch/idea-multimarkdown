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

import java.util.*
import java.util.function.Consumer

// fast filtering of elements based on elemKey
// main use to get files whose names match
// this is an ordered list with hash map access by key
abstract class IndexedList<K, T> {
    private object EMPTY_LIST {
        val indexList = ArrayList<Int>();
    }

    protected val list: ArrayList<T>
    protected val map: MutableMap<K, ArrayList<Int>>

    constructor() {
        this.list = ArrayList<T>()
        this.map = HashMap<K, ArrayList<Int>>()
    }

    constructor(list: Collection<T>) {
        this.list = ArrayList<T>(list.size)
        this.map = HashMap<K, ArrayList<Int>>()
        addAll(list)
    }

    constructor(vararg list: T) {
        this.list = ArrayList<T>(list.size)
        this.map = HashMap<K, ArrayList<Int>>()
        addAll(*list)
    }

    abstract fun elemKey(item: T): K

    fun addAll(list: Collection<T>) {
        var index = this.list.size
        for (item in list) {
            add(index++, item)
            this.list.add(item)
        }
    }

    fun addAll(vararg list: T) {
        var index = this.list.size
        for (item in list) {
            add(index++, item)
            this.list.add(item)
        }
    }

    private fun addAll(key: K, list: List<T>, indices: List<Int>) {
        val indexList = map.getOrPut(key, { ArrayList<Int>(2) })

        val index = this.list.size
        for (i in 0..indices.lastIndex) {
            indexList.add(i + index)
            this.list.add(list[indices[i]])
        }
    }

    private fun add(index: Int, item: T) {
        map.getOrPut(elemKey(item), { ArrayList<Int>(2) }).add(index)
    }

    fun addTo(indexedList: IndexedList<K, T>, vararg keys: K) {
        for (key in keys) {
            if (map.containsKey(key)) {
                indexedList.addAll(key, this.list, map.getOrElse(key, { EMPTY_LIST.indexList }))
            }
        }
    }

    fun addTo(indexedList: IndexedList<K, in T>, keys: Collection<K>) {
        for (key in keys) {
            if (map.containsKey(key)) {
                indexedList.addAll(key, this.list, map.getOrElse(key, { EMPTY_LIST.indexList }))
            }
        }
    }

    fun sorted(c: Comparator<in T>): List<T> {
        val newList = ArrayList<T>(list.size)
        newList.addAll(list)
        newList.sort(c)
        return newList
    }

    // delegate access to list
    val isEmpty: Boolean
        get() = list.isEmpty()

    val size: Int
        get() = list.size

    fun toArray(): Array<Any> = list.toArray()
    fun toArray(a: Array<out T>): Array<out T> = list.toArray(a)
    operator fun get(index: Int): T = list[index]
    fun forEach(action: Consumer<in T>) = list.forEach(action)

    // delegate access to map
    fun containsKey(key: K): Boolean = map.containsKey(key)

    fun keySet(): Set<K> = map.keys
}
