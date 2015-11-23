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
package com.vladsch.idea.multimarkdown

import java.lang.reflect.Constructor

fun dataColText(col: Any?, padStart: Int = 0, padEnd: Int = 0): String {
    val text: String
    if (col == null) text = "null";
    else {
        when (col) {
            is Array<*> -> {
                var colText = "";
                for (elem in col) {
                    if (colText.isNotEmpty()) colText += ", ";
                    colText += dataColText(elem)
                }
                text = "arrayOf<String>($colText)";
            }
            is Boolean -> text = col.toString()
            is Int -> text = col.toString()
            is kotlin.reflect.KCallable<*> -> text = "::" + col.toString().substringAfterLast('.')
            else -> text = "\"$col\"";
        }
    }

    return text.padStart(padStart, ' ').padEnd(padEnd, ' ')
}

fun printData(data: Collection<Array<Any?>>, header: Array<String>): Unit {
    var colWidths = Array<Int>(data.last().size, { 0 });

    for (i in header.indices) {
        val col = header[i]
        val colText = dataColText(col)
        if (colWidths[i] < colText.length) colWidths[i] = colText.length
    }

    for (row in data) {
        for (i in row.indices) {
            val col = row[i]
            val colText = dataColText(col)
            if (colWidths[i] < colText.length) colWidths[i] = colText.length
        }
    }

    System.out.println("            return arrayListOf<Array<Any?>>(")

    var rowText = "";
    for (i in header.indices) {
        val col = header[i]
        if (rowText.isNotEmpty()) rowText += ", ";
        rowText += dataColText(col, padEnd = colWidths[i])
    }

    val rowPad = data.size.toString().length
    System.out.println("                /* ${"".padStart(rowPad)}   arrayOf<Any?>($rowText) */")

    var rowIndex = 0
    for (row in data) {
        rowText = "";
        for (i in row.indices) {
            val col = row[i]
            if (rowText.isNotEmpty()) rowText += ", ";
            rowText += dataColText(col, padEnd = colWidths[i])
        }
        if (rowIndex > 0) System.out.print(",\n")
        System.out.print("                /* ${rowIndex.toString().padStart(rowPad)} */arrayOf<Any?>($rowText) /* ${rowIndex.toString().padStart(rowPad)} */")
        rowIndex++
    }

    System.out.println("\n            )")
}


