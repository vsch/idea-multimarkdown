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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CountingBagTest {
    @Test
    fun test_basic() {
        val bag = CountingBag<String>()

        bag.add("a")
        assertTrue(bag.contains("a"))
        assertEquals(1, bag.getCountOf("a"))
        assertEquals(1, bag.size)

        bag.add("a")
        assertTrue(bag.contains("a"))
        assertEquals(2, bag.getCountOf("a"))
        assertEquals(2, bag.size)

        bag.remove("a")
        assertTrue(bag.contains("a"))
        assertEquals(1, bag.getCountOf("a"))
        assertEquals(1, bag.size)


        bag.remove("a")
        assertFalse(bag.contains("a"))
        assertEquals(0, bag.getCountOf("a"))
        assertEquals(0, bag.size)
    }

    @Test
    fun test_basic2() {
        val bag = CountingBag<String>()

        bag.add("a")
        assertTrue(bag.contains("a"))
        assertFalse(bag.contains("b"))
        assertEquals(1, bag.getCountOf("a"))
        assertEquals(1, bag.size)

        bag.add("a")
        assertTrue(bag.contains("a"))
        assertFalse(bag.contains("b"))
        assertEquals(2, bag.getCountOf("a"))
        assertEquals(0, bag.getCountOf("b"))
        assertEquals(2, bag.size)

        bag.add("b")
        assertTrue(bag.contains("a"))
        assertTrue(bag.contains("b"))
        assertEquals(2, bag.getCountOf("a"))
        assertEquals(1, bag.getCountOf("b"))
        assertEquals(3, bag.size)

        bag.add("b")
        assertTrue(bag.contains("a"))
        assertTrue(bag.contains("b"))
        assertEquals(2, bag.getCountOf("a"))
        assertEquals(2, bag.getCountOf("b"))
        assertEquals(4, bag.size)

        bag.remove("a")
        assertTrue(bag.contains("a"))
        assertTrue(bag.contains("b"))
        assertEquals(1, bag.getCountOf("a"))
        assertEquals(2, bag.getCountOf("b"))
        assertEquals(3, bag.size)


        bag.remove("a")
        assertFalse(bag.contains("a"))
        assertTrue(bag.contains("b"))
        assertEquals(0, bag.getCountOf("a"))
        assertEquals(2, bag.getCountOf("b"))
        assertEquals(2, bag.size)

        bag.remove("b")
        assertFalse(bag.contains("a"))
        assertTrue(bag.contains("b"))
        assertEquals(0, bag.getCountOf("a"))
        assertEquals(1, bag.getCountOf("b"))
        assertEquals(1, bag.size)


        bag.remove("b")
        assertFalse(bag.contains("a"))
        assertFalse(bag.contains("b"))
        assertEquals(0, bag.getCountOf("a"))
        assertEquals(0, bag.getCountOf("b"))
        assertEquals(0, bag.size)
    }
}
