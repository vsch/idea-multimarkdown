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

import com.vladsch.idea.multimarkdown.TestUtils.*
import com.vladsch.idea.multimarkdown.printData
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(value = Parameterized::class)
class TestLinkResolver_MarkdownTest_wiki__normal_file constructor(val rowId:Int, val fullPath: String
                                                                  , val linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?) -> LinkRef
                                                                  , val linkText: String
                                                                  , val linkAddress: String
                                                                  , val linkAnchor: String?
                                                                  , val linkTitle: String?
                                                                  , resolvesLocalRel: String?
                                                                  , resolvesExternalRel: String?
                                                                  , val linkAddressText: String?
                                                                  , multiResolvePartial: Array<String>
                                                                  , val inspectionResults: ArrayList<InspectionResult>?
) {
    val resolvesLocal: String?
    val resolvesExternal: String?
    val filePathInfo = FileRef(fullPath)
    val resolver = GitHubLinkResolver(MarkdownTestData, filePathInfo)
    val linkRef = LinkRef.parseLinkRef(filePathInfo, linkAddress + linkAnchor.startWith('#'), linkRefType)
    val linkRefNoExt = LinkRef.parseLinkRef(filePathInfo, linkRef.filePathNoExt + linkAnchor.startWith('#'), linkRefType)
    val fileList = ArrayList<FileRef>(MarkdownTestData.filePaths.size)
    val multiResolve: Array<String>
    val localLinkRef = resolvesLocalRel
    val externalLinkRef = resolvesExternalRel
    val skipTest = linkRef is UrlLinkRef// || (linkRef !is ImageLinkRef && linkRef.hasExt && !linkRef.isMarkdownExt)

    fun resolveRelativePath(filePath:String?):PathInfo? {
        return if (filePath == null) null else PathInfo.append(filePathInfo.path, filePath.splitToSequence("/"))
    }

    init {
        resolvesLocal = resolveRelativePath(resolvesLocalRel)?.filePath
        resolvesExternal = resolveRelativePath(resolvesExternalRel)?.filePath

        var multiResolveAbs = ArrayList<String>()

        if (multiResolvePartial.size == 0 && resolvesLocal != null) multiResolveAbs.add(resolvesLocal)

        for (path in multiResolvePartial) {
            multiResolveAbs.add(resolveRelativePath(path)?.filePath.orEmpty())
        }

        multiResolve = multiResolveAbs.toArray(Array(0, { "" }))

        for (path in MarkdownTestData.filePaths) {
            fileList.add(FileRef(path))
        }
    }


    @Test fun test_ResolveLocal() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, LinkResolver.ONLY_LOCAL, fileList)
        assertEqualsMessage("Local does not match", resolvesLocal, localRef?.filePath)
    }

    @Test fun test_ResolveExternal() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, LinkResolver.ONLY_REMOTE, fileList)
        assertEqualsMessage("External does not match", resolvesExternal, localRef?.filePath)
    }

    @Test fun test_LocalLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, LinkResolver.ONLY_LOCAL, fileList) as? FileRef
        val localRefAddress = if (localRef != null) resolver.linkAddress(linkRef, localRef, (linkRef.hasExt || (linkRef.hasAnchor && linkAnchor?.contains('.') ?: false)), null) else null
        assertEqualsMessage("Local link address does not match", this.linkAddressText, localRefAddress)
    }

    @Test fun test_MultiResolve() {
        if (skipTest) return
        val localRefs = resolver.multiResolve(linkRef, LinkResolver.ONLY_LOCAL or LinkResolver.LOOSE_MATCH, fileList)
        val actuals = Array<String>(localRefs.size, { "" })
        for (i in localRefs.indices) {
            actuals[i] = localRefs[i].filePath
        }
        compareOrderedLists("MultiResolve does not match", multiResolve, actuals)
    }

    @Test fun test_InspectionResults() {
        if (skipTest || this.inspectionResults == null) return
        val targetRef = resolver.resolve(linkRef, LinkResolver.LOOSE_MATCH, fileList) as? FileRef
        if (targetRef != null) {
            val inspectionResults = resolver.inspect(linkRef, targetRef)
            if (this.inspectionResults.size < inspectionResults.size) {
                for (inspection in inspectionResults) {
                    println(inspection.toArrayOfTestString(rowId, filePathInfo.path))
                }
            }

            compareUnorderedLists("InspectionResults do not match", this.inspectionResults, inspectionResults)
        }
    }

    companion object {
            fun resolveRelativePath(filePathInfo: PathInfo, filePath:String?):PathInfo? {
                return if (filePath == null) null else PathInfo.append(filePathInfo.path, filePath.splitToSequence("/"))
            }

        @Parameterized.Parameters(name = "{index}: filePath = {0}, linkRef = {3}, linkAnchor = {4}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            val data = MarkdownTest__MarkdownTest_wiki__normal_file_md.data()
            val inspectionData = MarkdownTest__MarkdownTest_wiki__normal_file_md.mismatchInfo()
            val amendedData = ArrayList<Array<Any?>>()

            var i = 0
            for (row in data) {
                val amendedRow = Array<Any?>(row.size+2,{null})
                val inspections = ArrayList<InspectionResult>()
                val filePathInfo = PathInfo(row[0] as String)
                for (inspectionRow in inspectionData) {
                    if (inspectionRow[0] == i) {
                        inspections.add(InspectionResult(inspectionRow[1] as String, inspectionRow[2] as Severity, inspectionRow[3] as String?, resolveRelativePath(filePathInfo, inspectionRow[4] as String?)?.filePath))
                    }
                }

                System.arraycopy(row, 0, amendedRow, 1, row.size)
                amendedRow[0] = i
                amendedRow[row.size+1] = inspections
                amendedData.add(amendedRow)
                i++
            }

            return amendedData
        }
    }
}

