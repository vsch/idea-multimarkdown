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
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(value = Parameterized::class)
class TestLinkResolver_MarkdownTest__Readme constructor(val rowId: Int, val fullPath: String
                                                        , val linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?, targetRef: FileRef?) -> LinkRef
                                                        , val linkText: String
                                                        , val linkAddress: String
                                                        , val linkAnchor: String?
                                                        , val linkTitle: String?
                                                        , resolvesLocalRel: String?
                                                        , resolvesExternalRel: String?
                                                        , val linkAddressText: String?
                                                        , val remoteAddressText: String?
                                                        , val uriText: String?
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
    val skipTest = linkRef.isExternal

    fun resolveRelativePath(filePath: String?): PathInfo? {
        return if (filePath == null) null else if (filePath.startsWith("http://", "https://")) PathInfo(filePath) else PathInfo.appendParts(filePathInfo.path, filePath.splitToSequence("/"))
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
        val localRef = resolver.resolve(linkRef, LinkResolver.PREFER_LOCAL, fileList)
        assertEqualsMessage("Local does not match", resolvesLocal, localRef?.filePath)
    }

    @Test fun test_ResolveExternal() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, LinkResolver.PREFER_REMOTE or LinkResolver.ACCEPT_URI, fileList)
        assertEqualsMessage("External does not match", resolvesExternal, localRef?.filePath)
    }

    @Test fun test_LocalLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, LinkResolver.PREFER_LOCAL, fileList)
        val localRefAddress = if (localRef != null) resolver.linkAddress(linkRef, localRef, (linkRef.hasExt || (linkRef.hasAnchor && linkAnchor?.contains('.') ?: false)), null) else null
        assertEqualsMessage("Local link address does not match", this.linkAddressText, localRefAddress)
    }

    @Test fun test_RemoteLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, LinkResolver.PREFER_LOCAL, fileList) as? FileRef
        val remoteRef = resolver.resolve(linkRef, LinkResolver.PREFER_REMOTE or LinkResolver.ACCEPT_URI, fileList)
        val remoteRefAddress = if (localRef == null && remoteRef != null) resolver.linkAddress(linkRef, remoteRef, linkRef !is WikiLinkRef && (linkRef.hasExt || (linkRef.hasAnchor && linkAnchor?.contains('.') ?: false)), null) else null
        assertEqualsMessage("Remote based link address does not match", this.remoteAddressText, remoteRefAddress)
    }

    @Test fun test_AnyLinkAddress() {
        if (skipTest) return
        val anyRef = resolver.resolve(linkRef, LinkResolver.ANY, fileList)
        val linkAddress = if (anyRef != null) resolver.linkAddress(linkRef, anyRef, null, null) else null
        assertEqualsMessage("LinkResolver.ANY link address does not match", this.linkAddressText ?: this.remoteAddressText, linkAddress)
    }

    @Test fun test_OnlyURILinkAddress() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, LinkResolver.ANY, fileList)
        val uriRef = resolver.resolve(linkRef, LinkResolver.ONLY_URI, fileList)
        val href = if (targetRef != null && targetRef is FileRef && this.uriText != null) {
            "file://" + targetRef.filePath
        } else if (targetRef is LinkRef) targetRef.filePathWithAnchor
        else null
        assertEqualsMessage("LinkResolver.ONLY_URI link address does not match", href, (uriRef as? LinkRef)?.filePathWithAnchor)
    }

    @Test fun test_RemoteURILinkAddress() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, LinkResolver.ANY, fileList)
        val uriRef = resolver.resolve(linkRef, LinkResolver.PREFER_REMOTE or LinkResolver.ONLY_URI, fileList)
        val href = if (targetRef != null && targetRef is FileRef && this.uriText != null) {
            if (resolver.projectResolver.isUnderVcs(targetRef)) {
                val prefix = if (targetRef.isUnderWikiDir) "" else "blob/master/"
                val parts = this.uriText.split("#", limit = 2)
                "https://github.com/vsch/MarkdownTest/" + prefix + parts[0].replace("#", "%23").replace(" ", "%20") + (if (parts.size > 1) parts[1].prefixWith('#') else "")
            } else {
                "file://" + targetRef.filePath
            }
        } else if (targetRef is LinkRef) targetRef.filePathWithAnchor
        else null
        assertEqualsMessage("LinkResolver.ONLY_URI link address does not match", href, (uriRef as? LinkRef)?.filePathWithAnchor)
    }

    @Test fun test_MultiResolve() {
        if (skipTest) return
        //        val localRefs = resolver.multiResolve(if (linkRef is WikiLinkRef) linkRef else linkRefNoExt, LinkResolver.ONLY_LOCAL or LinkResolver.LOOSE_MATCH, fileList)
        val localRefs = resolver.multiResolve(linkRef, LinkResolver.PREFER_LOCAL or LinkResolver.LOOSE_MATCH, fileList)
        val actuals = Array(localRefs.size, { "" })
        for (i in localRefs.indices) {
            actuals[i] = localRefs[i].filePath
        }
        compareOrderedLists("MultiResolve does not match", multiResolve, actuals)
    }

    @Test fun test_Resolve() {
        if (skipTest) return
        //        val localRefs = resolver.multiResolve(if (linkRef is WikiLinkRef) linkRef else linkRefNoExt, LinkResolver.ONLY_LOCAL or LinkResolver.LOOSE_MATCH, fileList)
        val localRefs = resolver.multiResolve(linkRef, LinkResolver.ANY, fileList)
        val targetRef = if (localRefs.size > 0) localRefs[0] else null
        assertEqualsMessage("Resolve does not match", this.resolvesLocal ?: this.resolvesExternal ?: null, if (targetRef is LinkRef) targetRef.filePathWithAnchor else targetRef?.filePath)
    }

    @Test fun test_InspectionResults() {
        if (skipTest || this.inspectionResults == null) return
        val targetRef = resolver.resolve(linkRef, LinkResolver.LOOSE_MATCH, fileList) as? FileRef
        if (targetRef != null) {
            val inspectionResults = resolver.inspect(linkRef, targetRef, null)
            if (this.inspectionResults.size < inspectionResults.size) {
                for (inspection in inspectionResults) {
                    println(inspection.toArrayOfTestString(rowId, filePathInfo.path))
                }
            }

            compareUnorderedLists("InspectionResults do not match", this.inspectionResults, inspectionResults)
        }
    }

    companion object {
        fun resolveRelativePath(filePathInfo: PathInfo, filePath: String?): PathInfo? {
            return if (filePath == null) null else PathInfo.appendParts(filePathInfo.path, filePath.splitToSequence("/"))
        }

        @Parameterized.Parameters(name = "{index}: filePath = {1}, linkRef = {4}, linkAnchor = {5}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            val data = MarkdownTest__Readme_md.data()
            val inspectionData = MarkdownTest__Readme_md.mismatchInfo()
            val amendedData = ArrayList<Array<Any?>>()

            var i = 0
            for (row in data) {
                val amendedRow = Array<Any?>(row.size + 2, { null })
                val inspections = ArrayList<InspectionResult>()
                val filePathInfo = PathInfo(row[0] as String)
                for (inspectionRow in inspectionData) {
                    if (inspectionRow[0] == i) {
                        inspections.add(InspectionResult(inspectionRow[1] as String, inspectionRow[2] as Severity, inspectionRow[3] as String?, resolveRelativePath(filePathInfo, inspectionRow[4] as String?)?.filePath))
                    }
                }

                System.arraycopy(row, 0, amendedRow, 1, row.size)
                amendedRow[0] = i
                amendedRow[row.size + 1] = inspections
                amendedData.add(amendedRow)
                i++
            }

            return amendedData
        }
    }
}

