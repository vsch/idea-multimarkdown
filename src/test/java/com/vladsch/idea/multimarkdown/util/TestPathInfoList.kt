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

import com.vladsch.idea.multimarkdown.TestUtils
import com.vladsch.idea.multimarkdown.printData
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*
import kotlin.test.assertEquals

class TestPathInfoList {

    fun compareOrdered(expected: Array<String>, actual: Array<PathInfo>) {

    }

    @Test
    fun testTest_add() {
        val list = PathInfoList()
        val array = ArrayList<PathInfo>()
        var flag = false

        for (path in PathInfoListData.filePaths) {
            val pathInfo = PathInfo(path.toString())
            list.addAll(pathInfo)
            array.add(pathInfo)
        }

        TestUtils.compareOrderedLists(null, array, list)
        TestUtils.compareUnorderedLists(null, ArrayList<String>(), list.keySet())
    }
}

