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

class MismatchReason protected constructor(val id: String, val severity: MismatchReason.Severity, fixedLink: String? = null, fixedFilePath: String? = null) {
    enum class Severity private constructor(val value: Int) {
        INFO(0), WEAK_WARNING(1), WARNING(2), ERROR(3)
    }

    val fixedLink: String?
    val fixedFilePath: String?

    val fixedLinkRef: FileRef? by lazy {
        when {
            this.fixedLink is String -> FileRef(this.fixedLink)
            else -> null
        }
    }

    val fixedFileRef: FileRef? by lazy {
        when {
            this.fixedFilePath is String -> FileRef(this.fixedFilePath)
            else -> null
        }
    }

    init {
        this.fixedLink = fixedLink.orEmpty()
        this.fixedFilePath = fixedFilePath.orEmpty()
    }
}

class MismatchReasons() : Map<String, MismatchReason> {
    protected val reasons = HashMap<String, MismatchReason>(10)

    val containsErrors: Boolean
        get() = containsSeverity(MismatchReason.Severity.ERROR)

    val containsWarnings: Boolean
        get() = containsSeverity(MismatchReason.Severity.WARNING)

    val containsWeakWarnings: Boolean
        get() = containsSeverity(MismatchReason.Severity.WEAK_WARNING)

    val containsInfo: Boolean
        get() = containsSeverity(MismatchReason.Severity.INFO)

    fun containsSeverity(severity: MismatchReason.Severity): Boolean {
        for (reason in reasons.values) {
            if (reason.severity === severity) return true
        }
        return false
    }

    fun add(reason: MismatchReason) {
        reasons.put(reason.id, reason)
    }

    override val size: Int
        get() = reasons.size

    override fun isEmpty(): Boolean = reasons.isEmpty()
    override fun containsKey(key: String): Boolean = reasons.containsKey(key)
    override fun containsValue(value: MismatchReason): Boolean = reasons.containsValue(value)
    override fun get(key: String): MismatchReason? = get(key)

    override val keys: Set<String>
        get() = reasons.keys
    override val values: Collection<MismatchReason>
        get() = reasons.values
    override val entries: Set<Map.Entry<String, MismatchReason>>
        get() = reasons.entries
}
