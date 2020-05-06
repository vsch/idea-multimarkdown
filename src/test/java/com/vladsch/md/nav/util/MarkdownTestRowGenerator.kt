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

import com.vladsch.plugin.test.util.ParamRowGenerator
import com.vladsch.plugin.test.util.ParamRowGenerator.ColumnProvider
import com.vladsch.plugin.test.util.ParamRowGenerator.Decorator

class MarkdownTestRowGenerator(private val lineProvider: LineProvider? = null) : ParamRowGenerator() {
    fun row(
        fullPath: String
        , linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?, targetRef: FileRef?, isNormalized: Boolean) -> LinkRef
        , linkText: String
        , linkAddress: String
        , linkAnchor: String?
        , linkTitle: String?
        , resolvesLocalRel: String?
        , resolvesExternalRel: String?
        , linkAddressText: String?
        , remoteAddressText: String?
        , uriText: String?
        , multiResolvePartial: Array<String>
    ): MarkdownTestRowGenerator {
        super.row(1, arrayOf(
            /*  1 */ fullPath,
            /*  2 */ linkRefType,
            /*  3 */ linkText,
            /*  4 */ linkAddress,
            /*  5 */ linkAnchor,
            /*  6 */ linkTitle,
            /*  7 */ resolvesLocalRel,
            /*  8 */ resolvesExternalRel,
            /*  9 */ linkAddressText,
            /* 10 */ remoteAddressText,
            /* 11 */ uriText,
            /* 12 */ multiResolvePartial,
            /* 13 */ arrayListOf<InspectionResult>(),
            /* 14 */ arrayListOf<InspectionResult>()
        ),

            Decorator { _, prefix, suffix -> "$prefix\"$fullPath\"\n$suffix" },
            lineProvider,
            ColumnProvider { 39 })
        return this
    }

    class InspectionRowGenerator(private val lineProvider: LineProvider? = null) : ParamRowGenerator() {
        fun row(id: String): InspectionRowGenerator {
            super.row(2, arrayOf(),
                Decorator { index, prefix, suffix -> "$suffix\n$prefix\"$id\"" },
                lineProvider,
                ColumnProvider { 72 })
            return this
        }
    }

    fun withInspection(
        id: String,
        severity: Severity,
        fixedLink: String? = null,
        fixedExternalLink: String? = null,
        fixedFilePath: String? = null
    ): MarkdownTestRowGenerator {
        val row = InspectionRowGenerator().row(id).rows[0]

        // fixed link inspections
        @Suppress("UNCHECKED_CAST")
        (rows[rows.size - 1][13] as ArrayList<InspectionResult>).add(InspectionResult(row[0] as String, id, severity, fixedLink, fixedFilePath))

        // HACK: has extension filtered out if no fixed external link inspections
        if (fixedExternalLink != null || id != "ID_LINK_TARGETS_WIKI_HAS_EXT") {
            @Suppress("UNCHECKED_CAST")
            (rows[rows.size - 1][14] as ArrayList<InspectionResult>).add(InspectionResult(row[0] as String, id, severity, fixedExternalLink, fixedFilePath))
        }

        return this
    }
}
