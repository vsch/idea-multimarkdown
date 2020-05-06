/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.md.nav.settings

import com.vladsch.md.nav.testUtil.TestCaseUtils
import org.jdom.Element
import org.junit.Test
import kotlin.test.assertEquals

class SerializersTest {

    class Container : TagItemHolder("Container") {
        var boolValue1 = false
        var boolValue2 = false
        var intValue = 0
        var floatValue = 0f
        var doubleValue = 0.0
        var stringValue = ""
        var stringContent = ""
        var stringArray = arrayListOf<String>()
        var intArray = arrayListOf<Int>()
        var intSet = hashSetOf<Int>()
        var stringIntMap = hashMapOf<String, Int>()

        init {
            addItems(
                BooleanAttribute("boolValue1", { boolValue1 }, { boolValue1 = it }),
                BooleanAttribute("boolValue2", { boolValue2 }, { boolValue2 = it }),
                IntAttribute("intValue", { intValue }, { intValue = it }),
                FloatAttribute("floatValue", { floatValue }, { floatValue = it }),
                DoubleAttribute("doubleValue", { doubleValue }, { doubleValue = it }),
                StringAttribute("stringValue", { stringValue }, { stringValue = it }),
                StringContent("stringContent", { stringContent }, { stringContent = it }),
                ArrayListItem("stringArray", { stringArray }, { stringArray = it }, { it }, { it }),
                ArrayListItem("intArray", { intArray }, { intArray = it }, Int::toString, String::toInt),
                HashSetItem<Int>("intSet", { intSet }, { intSet = it }, Int::toString, String::toInt),
                HashMapItem<String, Int>("stringIntMap", { stringIntMap }, { stringIntMap = it }, { key, value -> Pair(key, value.toString()) }, { key, value ->
                    Pair(key, value?.toInt() ?: 0)
                })
            )
        }
    }

    @Test
    fun test_BasicSave() {
        val element = Element("root")
        val container = Container()
        val container2 = Container()

        container.boolValue1 = false
        container.boolValue2 = true
        container.intValue = 5
        container.floatValue = 10f
        container.doubleValue = 100.0
        container.stringValue = "Test"
        container.stringContent = "Test2"
        container.stringArray = arrayListOf("Test1", "Test2", "Test3")
        container.intArray = arrayListOf(1, 2, 3, 4, 5)
        container.intSet = hashSetOf(1, 2, 3, 4, 5, 1, 2, 3, 4, 5)
        container.stringIntMap = hashMapOf(Pair("1", 1), Pair("2", 2), Pair("3", 3), Pair("4", 4))

        container.saveState(element)
        container2.loadState(element)

        assertEquals(container.boolValue1, container2.boolValue1)
        assertEquals(container.boolValue2, container2.boolValue2)
        assertEquals(container.intValue, container2.intValue)
        assertEquals(container.floatValue, container2.floatValue)
        assertEquals(container.doubleValue, container2.doubleValue)
        assertEquals(container.stringValue, container2.stringValue)
        assertEquals(container.stringContent, container2.stringContent)
        TestCaseUtils.compareOrderedLists("", container.stringArray, container2.stringArray)
        TestCaseUtils.compareOrderedLists("", container.intArray.toTypedArray(), container2.intArray.toTypedArray())
        TestCaseUtils.compareUnorderedLists("", container.intSet.toTypedArray(), container2.intSet.toTypedArray())
        TestCaseUtils.compareUnorderedLists("", container.stringIntMap.map({ "\"${it.key}\", ${it.value}" }).toTypedArray(), container2.stringIntMap.map({ "\"${it.key}\", ${it.value}" }).toTypedArray())
    }
}
