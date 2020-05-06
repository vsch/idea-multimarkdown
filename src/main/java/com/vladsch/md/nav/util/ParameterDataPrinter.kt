// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util

import kotlin.reflect.KCallable

class UnquotedString(val text: String) {
    override fun toString(): String {
        return text
    }
}

fun dataColText(colStringer: ((Array<Any?>) -> String)? = null, row: Array<Any?>?, col: Any?, padStart: Int = 0, padEnd: Int = 0): String {
    val text: String
    if (col == null) text = "null";
    else {
        if (colStringer != null && row != null) text = colStringer(row)
        else {
            when (col) {
                is DataPrinterAware -> text = col.testData()
                is Array<*> -> {
                    var colText = "";
                    for (elem in col) {
                        if (colText.isNotEmpty()) colText += ", ";
                        colText += dataColText(null, null, elem)
                    }
                    text = "arrayOf<String>($colText)";
                }
                is UnquotedString -> text = col.toString()
                is Boolean -> text = col.toString()
                is Int -> when (col) {
                    Integer.MAX_VALUE -> text = "Integer.MAX_VALUE"
                    Integer.MIN_VALUE -> text = "Integer.MIN_VALUE"
                    else -> text = col.toString()
                }
                is KCallable<*> -> text = "::" + col.toString().substringAfterLast('.')
                else -> text = "\"${col.toString().replace("\\", "\\\\").replace("\"", "\\\"")}\"";
            }
        }
    }

    return text.padStart(padStart, ' ').padEnd(padEnd, ' ')
}

fun printData(data: Collection<Array<Any?>>, header: Array<String>, colStringersMap: Map<String, (Array<Any?>) -> String>? = null): Unit {
    if (data.size == 0) return

    var colWidths = Array<Int>(data.last().size, { 0 });
    val colStringers = Array<((Array<Any?>) -> String)?>(data.last().size, { null })

    for (i in header.indices) {
        val col = header[i]
        colStringers[i] = colStringersMap?.get(header[i])
        val colText = dataColText(null, null, col)
        if (colWidths[i] < colText.length) colWidths[i] = colText.length
    }

    for (row in data) {
        for (i in row.indices) {
            val col = row[i]
            val colText = dataColText(colStringers[i], row, col)
            if (colWidths[i] < colText.length) colWidths[i] = colText.length
        }
    }

    System.out.println("            return arrayListOf<Array<Any?>>(")
    // remove last column width, we don't pad the last column
    colWidths[colWidths.lastIndex] = 0

    var rowText = "";
    for (i in header.indices) {
        val col = header[i]
        if (rowText.isNotEmpty()) rowText += ", ";
        rowText += dataColText(null, null, col, padEnd = colWidths[i])
    }

    val rowPad = data.size.toString().length
    System.out.println("                /* ${"".padStart(rowPad)}   arrayOf<Any?>($rowText) */")

    var rowIndex = 0
    for (row in data) {
        rowText = "";
        for (i in row.indices) {
            val col = row[i]
            if (rowText.isNotEmpty()) rowText += ", ";
            rowText += dataColText(colStringers[i], row, col, padEnd = colWidths[i])
        }
        if (rowIndex > 0) System.out.print(",\n")
        System.out.print("                /* ${rowIndex.toString().padStart(rowPad)} */arrayOf<Any?>($rowText) /* ${rowIndex.toString().padStart(rowPad)} */")
        rowIndex++
    }

    System.out.println("\n            )")
}
