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
class TestLinkResolver_Basic_wiki_Home constructor(val rowId:Int, val fullPath: String
                                                   , val linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?, targetRef: FileRef?) -> LinkRef
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
    val linkRef = LinkRef.parseLinkRef(filePathInfo, linkAddress + linkAnchor.prefixWith('#'), null, linkRefType)
    val linkRefNoExt = LinkRef.parseLinkRef(filePathInfo, linkRef.filePathNoExt + linkAnchor.prefixWith('#'), null, linkRefType)
    val fileList = ArrayList<FileRef>(MarkdownTestData.filePaths.size)
    val multiResolve: Array<String>
    val localLinkRef = resolvesLocalRel
    val externalLinkRef = resolvesExternalRel
    val skipTest = linkRef.isExternal && !linkRef.filePath.startsWith(MarkdownTestData.mainGitHubRepo.baseUrl, ignoreCase = true)

    fun resolveRelativePath(filePath: String?): PathInfo? {
        return if (filePath == null) null else if (filePath.startsWith("http://", "https://")) PathInfo(filePath) else PathInfo.appendParts(filePathInfo.path, filePath)
    }

    init {
        resolvesLocal = resolveRelativePath(resolvesLocalRel)?.filePath
        resolvesExternal = resolveRelativePath(resolvesExternalRel)?.filePath

        var multiResolveAbs = ArrayList<String>()

        if (multiResolvePartial.size == 0 && resolvesLocal != null) multiResolveAbs.add(resolvesLocal)

        for (path in multiResolvePartial) {
            val resolvedPath = resolveRelativePath(path)?.filePath.orEmpty()
            multiResolveAbs.add(resolvedPath)
        }

        multiResolve = multiResolveAbs.toArray(Array(0, { "" }))

        for (path in MarkdownTestData.filePaths) {
            fileList.add(FileRef(path))
        }
    }


    @Test fun test_ResolveLocal() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, LinkResolver.PREFER_LOCAL, fileList)
        assertEqualsMessage("Local does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", resolvesLocal, localRef?.filePath)
    }

    @Test fun test_ResolveExternal() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, LinkResolver.ONLY_REMOTE, fileList)
        assertEqualsMessage("External does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", resolvesExternal, localRef?.filePath)
    }

    @Test fun test_LocalLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, LinkResolver.PREFER_LOCAL, fileList) as? FileRef
        val localRefAddress = if (localRef != null) resolver.linkAddress(linkRef, localRef, ((linkRef.hasExt && linkRef.ext == localRef.ext) || (linkRef.hasAnchor && linkAnchor?.contains('.') ?: false)), null) else null
        assertEqualsMessage("Local link address does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", this.linkAddressText, localRefAddress)
    }

    @Test fun test_MultiResolve() {
        if (skipTest || linkRef.filePath.isEmpty() && linkRef.anchor == null) return
        val localFileRef = if (localLinkRef != null) FileRef(localLinkRef) else null
        val looseMatch = localFileRef == null || localFileRef.path.isEmpty()
        val localRefs = resolver.multiResolve(linkRef, LinkResolver.PREFER_LOCAL or if (looseMatch) LinkResolver.LOOSE_MATCH else 0, fileList)
        val actuals = Array<String>(localRefs.size, { "" })
        for (i in localRefs.indices) {
            actuals[i] = localRefs[i].filePath
        }
        compareOrderedLists("MultiResolve ${if (looseMatch) "looseMatch" else "exact" } does not match ${if (looseMatch) resolver.getMatcher(linkRef, false).linkLooseMatch else resolver.getMatcher(linkRef, false).linkAllMatch}", multiResolve, actuals)
    }

    @Test fun test_InspectionResults() {
        if (skipTest || this.inspectionResults == null) return
        val looseTargetRef = resolver.resolve(linkRef, LinkResolver.LOOSE_MATCH, fileList) as? FileRef
        val targetRef = resolver.resolve(linkRef, LinkResolver.ANY, fileList) as? FileRef
        if (targetRef != null || looseTargetRef != null) {
            val inspectionResults = resolver.inspect(linkRef, targetRef ?: looseTargetRef as FileRef, rowId)
            if (this.inspectionResults.size < inspectionResults.size) {
                for (inspection in inspectionResults) {
                    //println(inspection.toArrayOfTestString(rowId, filePathInfo.path))
                }
            }

            compareUnorderedLists("InspectionResults do not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", this.inspectionResults, inspectionResults)
        }
    }

    companion object {
            fun resolveRelativePath(filePathInfo: PathInfo, filePath:String?):PathInfo? {
                return if (filePath == null) null else PathInfo.appendParts(filePathInfo.path, filePath.splitToSequence("/"))
            }

        @Parameterized.Parameters(name = "{index}: filePath = {1}, linkRef = {4}, linkAnchor = {5}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            val data = MarkdownTest__MarkdownTest_wiki__Home_md.data()
            val inspectionData = MarkdownTest__MarkdownTest_wiki__Home_md.mismatchInfo()
            val amendedData = ArrayList<Array<Any?>>()

            var i = 0
            for (row in data) {
                val amendedRow = Array<Any?>(row.size+2,{null})
                val inspections = ArrayList<InspectionResult>()
                val filePathInfo = PathInfo(row[0] as String)
                for (inspectionRow in inspectionData) {
                    if (inspectionRow[0] == i) {
                        val inspectionResult = InspectionResult(inspectionRow[1] as String, inspectionRow[2] as Severity, inspectionRow[3] as String?, resolveRelativePath(filePathInfo, inspectionRow[4] as String?)?.filePath)
                        inspectionResult.referenceId = i
                        inspections.add(inspectionResult)
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

