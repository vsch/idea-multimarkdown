// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.util

import com.vladsch.plugin.util.suffixWith

enum class Severity constructor(val value: Int) : DataPrinterAware {
    INFO(0), WEAK_WARNING(1), WARNING(2), ERROR(3);

    override fun testData(): String = super.testData() + "." + toString()
}

class InspectionResult(
    val location: String?,
    val id: String,
    val severity: Severity,
    val fixedLink: String? = null,
    val fixedFilePath: String? = null
) {
    var referenceId: Any? = location

    var handled: Boolean = false

    fun compareTo(other: InspectionResult): Int {
        return if (severity == other.severity && fixedLink == other.fixedLink && fixedFilePath == other.fixedFilePath) id.compareTo(other.id) else -1
    }

    private fun dataPrint(value: Any?): String {
        return if (value == null) "null" else when (value) {
            is Unit -> ""
            is Int, is Long, is Boolean, is Float, is Double, is Byte -> "$value"
            is Char -> "'$value'"
            else -> "\"$value\""
        }
    }

    override fun toString(): String {
        return "InspectionResults(${dataPrint(referenceId)}, \"$id\", Severity.$severity, ${if (fixedLink == null) "null" else "\"$fixedLink\""}, ${if (fixedFilePath == null) "null" else "\"$fixedFilePath\""})"
    }

    fun isA(id: String) = this.id == id

    //    /*  0 */arrayOf<Any?>(18, GitHubLinkInspector.ID_WIKI_LINK_HAS_DASHES , Severity.WEAK_WARNING, "Normal File", null) /*  0 */
    fun toArrayOfTestString(rowId: Int = 0, removePrefix: String = ""): String {
        val rowPad = " ".repeat(3 - rowId.toString().length) + rowId
        return "arrayOf($rowPad, \"$id\", Severity.$severity, ${if (fixedLink == null) "null" else "\"$fixedLink\""}, ${if (fixedFilePath == null) "null" else "\"${if (!removePrefix.isEmpty()) PathInfo.relativePath(removePrefix.suffixWith('/'), fixedFilePath, blobRawEqual = false) else fixedFilePath}\""})"
    }

    companion object {
        @JvmStatic
        fun handled(results: List<InspectionResult>, vararg ids: String) {
            for (result in results) {
                if (!result.handled && ids.contains(result.id)) {
                    result.handled = true
                }
            }
        }
    }
}
