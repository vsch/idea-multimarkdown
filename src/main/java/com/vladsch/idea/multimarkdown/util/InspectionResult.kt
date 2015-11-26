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

enum class Severity private constructor(val value: Int) {
    INFO(0), WEAK_WARNING(1), WARNING(2), ERROR(3)
}

class InspectionResult(val id: String, val severity: Severity, val fixedLink: String? = null, val fixedFilePath: String? = null) {

    constructor(mismatch: InspectionResult, fixedLink: String? = null, fixedFilePath: String? = null) : this(mismatch.id, mismatch.severity, fixedLink, fixedFilePath)

    fun compareTo(other: InspectionResult) :Int{
        return if (severity == other.severity && fixedLink == other.fixedLink && fixedFilePath == other.fixedFilePath) id.compareTo(other.id) else -1
    }

    override fun toString():String {
        return "InspectionResults(GitHubLinkInspector.ID_$id, Severity.$severity, ${if (fixedLink == null) "null" else "\"$fixedLink\"" }, ${if (fixedFilePath == null) "null" else "\"$fixedFilePath\""}),"
    }

    fun isA(id: String) = this.id == id

//    /*  0 */arrayOf<Any?>(18, GitHubLinkInspector.ID_WIKI_LINK_HAS_DASHES , Severity.WEAK_WARNING, "Normal File", null) /*  0 */
    fun toArrayOfTestString(rowId:Int = 0, removePrefix:String = ""):String {
        val rowPad = " ".repeat(3-rowId.toString().length)+rowId
        return "arrayOf($rowPad, GitHubLinkInspector.ID_$id, Severity.$severity, ${if (fixedLink == null) "null" else "\"$fixedLink\"" }, ${if (fixedFilePath == null) "null" else "\"${if (!removePrefix.isEmpty()) PathInfo.relativePath(removePrefix.endWith('/'), fixedFilePath) else fixedFilePath}\""}),"
    }
}
