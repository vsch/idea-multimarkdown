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
class TestLinkResolver_Basic_Readme constructor(val rowId: Int, val fullPath: String
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
                                                , val inspectionExtResults: ArrayList<InspectionResult>?
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
        val localRef = resolver.resolve(linkRef, Want(Local.REF, Remote.REF), fileList)
        assertEqualsMessage("Local does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", resolvesLocal, localRef?.filePath)
    }

    @Test fun test_ResolveExternal() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, Want(Local.NONE, Remote.REF, Links.URL), fileList)
        assertEqualsMessage("External does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", resolvesExternal, localRef?.filePath)
    }

    @Test fun test_LocalLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, Want(Local.REF, Remote.REF), fileList)
        val localRefAddress = if (localRef != null) resolver.linkAddress(linkRef, localRef, null, null) else null
        assertEqualsMessage("Local link address does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", this.linkAddressText, localRefAddress)
    }

    @Test fun test_RemoteLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, Want(Local.REF, Remote.REF), fileList) as? FileRef
        val remoteRef = resolver.resolve(linkRef, Want(Local.NONE, Remote.URL, Links.URL), fileList)
        val remoteRefAddress = if (localRef == null && remoteRef != null) resolver.linkAddress(linkRef, remoteRef, linkRef !is WikiLinkRef && (linkRef.hasExt || (linkRef.hasAnchor && linkAnchor?.contains('.') ?: false)), null) else null
        assertEqualsMessage("Remote based link address does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", this.remoteAddressText, remoteRefAddress)
    }

    @Test fun test_AnyLinkAddress() {
        if (skipTest) return
        val anyRef = resolver.resolve(linkRef, Want(), fileList)
        val linkAddress = if (anyRef != null) resolver.linkAddress(linkRef, anyRef, null, null) else null
        assertEqualsMessage("Want() link address does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", this.linkAddressText ?: this.remoteAddressText, linkAddress)
    }

    @Test fun test_OnlyURILinkAddress() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, Want(), fileList)
        val uriRef = resolver.resolve(linkRef, Want(Local.URI, Remote.URI, Links.URL), fileList)
        val href = if (targetRef != null && targetRef is FileRef && this.uriText != null) {
            "file://" + targetRef.filePath.replace(" ", "%20").replace("#", "%23")
        } else if (targetRef is LinkRef) targetRef.filePathWithAnchor
        else null
        assertEqualsMessage("LinkResolver.ONLY_URI link address does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", href, (uriRef as? LinkRef)?.filePathWithAnchor)
    }

    @Test fun test_RemoteURILinkAddress() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, Want(Local.REF,  Remote.REF, Links.URL), fileList)
        val uriRef = resolver.resolve(linkRef, Want(Local.NONE, Remote.URL, Links.URL), fileList)
        val href = if (targetRef != null && targetRef is FileRef && this.uriText != null) {
            if (resolver.projectResolver.isUnderVcs(targetRef)) {
                val prefix = if (targetRef.isUnderWikiDir) "" else "${if (linkRef is ImageLinkRef) "raw" else "blob"}/master/"
                val parts = this.uriText.split("#", limit = 2)
                "https://github.com/vsch/MarkdownTest/" + prefix + parts[0].replace("#", "%23").replace(" ", "%20") + (if (parts.size > 1) parts[1].prefixWith('#') else "")
            } else {
                null
            }
        } else if (targetRef is LinkRef) targetRef.filePathWithAnchor
        else null
        assertEqualsMessage("LinkResolver.ONLY_URI link address does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", href, (uriRef as? LinkRef)?.filePathWithAnchor)
    }

    @Test fun test_MultiResolve() {
        if (skipTest) return
        val localFileRef = if (localLinkRef != null) FileRef(localLinkRef) else null
        val looseMatch = localFileRef == null || localFileRef.path.isEmpty() && linkRef.anchor == null
        val localRefs = resolver.multiResolve(linkRef, Want(Local.REF, Remote.REF, if (looseMatch) Match.LOOSE else null), fileList)
        val actuals = Array<String>(localRefs.size, { "" })
        for (i in localRefs.indices) {
            actuals[i] = localRefs[i].filePath
        }
        compareOrderedLists("MultiResolve ${if (looseMatch) "looseMatch" else "exact" } does not match ${if (looseMatch) resolver.getMatcher(linkRef, false).linkLooseMatch else resolver.getMatcher(linkRef, false).linkAllMatch}", multiResolve, actuals)
    }

    @Test fun test_MultiResolveExactNoMatch() {
        if (skipTest || localLinkRef != null) return
        val localRefs = resolver.multiResolve(linkRef, Want(Local.REF, Remote.REF), fileList)
        val actuals = Array<String>(localRefs.size, { "" })
        for (i in localRefs.indices) {
            actuals[i] = localRefs[i].filePath
        }
        compareOrderedLists("MultiResolve exact does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", arrayOf<String>(), actuals)
    }

    @Test fun test_Resolve() {
        if (skipTest) return
        //        val localRefs = resolver.multiResolve(if (linkRef is WikiLinkRef) linkRef else linkRefNoExt, LinkResolver.ONLY_LOCAL or LinkResolver.LOOSE_MATCH, fileList)
        val localRefs = resolver.multiResolve(linkRef, Want(), fileList)
        val targetRef = if (localRefs.size > 0) localRefs[0] else null
        assertEqualsMessage("Resolve does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", this.resolvesLocal ?: this.resolvesExternal ?: null, if (targetRef is LinkRef) targetRef.filePathWithAnchor else targetRef?.filePath)
    }

    @Test fun test_InspectionResults() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, Want(), fileList) as? FileRef
        if (targetRef != null) {
            val inspections = resolver.inspect(linkRef, targetRef, rowId)
            if (inspectionResults == null || inspectionResults.size < inspections.size) {
                for (inspection in inspections) {
                    //println(inspection.toArrayOfTestString(rowId, filePathInfo.path))
                }
            }

            compareUnorderedLists("InspectionResults do not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", inspectionResults, inspections)
        }
    }

    @Test fun test_ResolveHttp() {
        if (skipTest) return
        if (resolvesLocal != null) {
            val targetRef = FileRef(resolvesLocal)
            val linkRefUri = resolver.processMatchOptions(linkRef, targetRef, Want(Local.URL, Remote.URL, Links.URL))
            if (linkRefUri != null && linkRefUri is LinkRef && linkRefUri.isExternal) {
                val originalCaseLinkRefUri = if (linkRefUri.filePath.endsWith("/wiki")) linkRefUri else linkRefUri.replaceFilePath(linkRefUri.path + linkRef.fileName.ifEmpty { linkRefUri.fileName })
                val localRef = resolver.resolve(originalCaseLinkRefUri, Want(Local.REF, Remote.REF), fileList)
                assertEqualsMessage("ResolveHttp $originalCaseLinkRefUri does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", resolvesLocal, localRef?.filePath)
            } else {
                assertEqualsMessage("ResolveHttp no remote link ${resolver.getMatcher(linkRef, false).linkAllMatch} for $resolvesLocal", resolvesLocal, linkRefUri?.filePath)
            }
        }
    }

    @Test fun test_ResolveHttpRaw() {
        if (skipTest) return
        if (resolvesLocal != null) {
            val targetRef = FileRef(resolvesLocal)
            if (linkRef.isRelative && !targetRef.isUnderWikiDir) {
                targetRef.isRawFile = true
                val rawLinkRef = linkRef.replaceFilePath("raw/" + linkRef.filePath, true)

                val linkRefUri = resolver.processMatchOptions(rawLinkRef, targetRef, Want(Local.URL, Remote.URL, Links.URL))
                if (linkRefUri != null && linkRefUri is LinkRef && linkRefUri.isExternal) {
                    val originalCaseLinkRefUri = if (linkRefUri.filePath.endsWith("/wiki")) linkRefUri else linkRefUri.replaceFilePath(linkRefUri.path + linkRef.fileName.ifEmpty { linkRefUri.fileName })
                    val localRef = resolver.resolve(originalCaseLinkRefUri, Want(Local.REF, Remote.REF), fileList)
                    assertEqualsMessage("ResolveHttpRaw $originalCaseLinkRefUri does not match ${resolver.getMatcher(rawLinkRef, false).linkAllMatch}", resolvesLocal, localRef?.filePath)
                } else {
                    assertEqualsMessage("ResolveHttpRaw no remote link ${resolver.getMatcher(rawLinkRef, false).linkAllMatch} for $resolvesLocal", resolvesLocal, linkRefUri?.filePath)
                }
            }
        }
    }

    //    // Only true for wiki pages using relative image links
    //    @Test fun test_NonResolveHttp() {
    //        if (skipTest) return
    //        // test to make sure linkrefs with blob/../ do not resolve
    //        if (resolvesLocal != null && linkRef is ImageLinkRef) {
    //            val targetRef = FileRef(resolvesLocal)
    //            if (!targetRef.isUnderWikiDir) {
    //                val linkRefUri = resolver.processMatchOptions(linkRef, targetRef, Want(Local.NONE, Remote.URL, Links.URL)_URI)
    //                if (linkRefUri != null && linkRefUri is LinkRef && linkRefUri.isExternal) {
    //                    val originalCaseLinkRefUri = linkRefUri.replaceFilePath((linkRefUri.path + linkRef.fileName.ifEmpty { linkRefUri.fileName }).replace("/raw/","/blob/"))
    //                    val localRef = resolver.resolve(originalCaseLinkRefUri, Want(Local.REF, Remote.REF), fileList)
    //                    assertEqualsMessage("ResolveHttp $originalCaseLinkRefUri does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", null, localRef?.filePath)
    //                } else {
    //                    assertEqualsMessage("ResolveHttp no remote link ${resolver.getMatcher(linkRef, false).linkAllMatch} for $resolvesLocal", resolvesLocal, linkRefUri?.filePath)
    //                }
    //            }
    //        }
    //    }

    @Test fun test_MultiResolveHttp() {
        if (skipTest) return
        if (resolvesLocal != null) {
            val targetRef = FileRef(resolvesLocal)
            val linkRefUri = resolver.processMatchOptions(linkRef, targetRef, Want(Local.URL, Remote.URL, Links.URL))

            assert(linkRefUri == null || linkRefUri.isExternal)

            if (linkRefUri != null && linkRefUri is LinkRef && linkRefUri.isExternal) {
                val originalCaseLinkRefUri = if (linkRefUri.filePath.endsWith("/wiki")) linkRefUri else linkRefUri.replaceFilePath(linkRefUri.path + linkRef.fileName.ifEmpty { linkRefUri.fileName })
                val localRefs = resolver.multiResolve(originalCaseLinkRefUri, Want())
                val resolvedTargetRef = if (localRefs.size > 0) localRefs[0] else null
                assertEqualsMessage("MultiResolveHttp $originalCaseLinkRefUri does not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", this.resolvesLocal, if (resolvedTargetRef is LinkRef) resolvedTargetRef.filePathWithAnchor else resolvedTargetRef?.filePath)
            }
        }
    }

    @Test fun test_InspectionResultsUri() {
        if (skipTest) return
        if (resolvesLocal != null) {
            val targetRef = FileRef(resolvesLocal)
            val linkRefUri = resolver.processMatchOptions(linkRef, targetRef, Want(Local.URL, Remote.URL, Links.URL))

            assert(linkRefUri == null || linkRefUri.isExternal)

            if (linkRefUri != null && linkRefUri is LinkRef && linkRefUri.isExternal) {
                val originalCaseLinkRefUri = if (linkRefUri.filePath.endsWith("/wiki")) linkRefUri else linkRefUri.replaceFilePath(linkRefUri.path + linkRef.fileName.ifEmpty { linkRefUri.fileName })
                val inspections = resolver.inspect(originalCaseLinkRefUri, targetRef, rowId)
                if (inspectionExtResults == null || inspectionExtResults.size < inspections.size) {
                    for (inspection in inspections) {
                        //println(inspection.toArrayOfTestString(rowId, filePathInfo.path))
                    }
                }

                compareUnorderedLists("InspectionResultsUri $originalCaseLinkRefUri do not match ${resolver.getMatcher(linkRef, false).linkAllMatch}", inspectionExtResults, inspections)
            }
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
                val amendedRow = Array<Any?>(row.size + 3, { null })
                val inspections = ArrayList<InspectionResult>()
                val inspectionsExt = ArrayList<InspectionResult>()
                val filePathInfo = PathInfo(row[0] as String)
                for (inspectionRow in inspectionData) {
                    if (inspectionRow[0] == i) {
                        val inspectionResult = InspectionResult(inspectionRow[1] as String, inspectionRow[2] as Severity, inspectionRow[3] as String?, resolveRelativePath(filePathInfo, inspectionRow[5] as String?)?.filePath)
                        inspectionResult.referenceId = i
                        inspections.add(inspectionResult)

                        val inspectionExtResult = InspectionResult(inspectionRow[1] as String, inspectionRow[2] as Severity, inspectionRow[4] as String?, resolveRelativePath(filePathInfo, inspectionRow[5] as String?)?.filePath)
                        inspectionExtResult.referenceId = i
                        inspectionsExt.add(inspectionExtResult)
                    }
                }

                System.arraycopy(row, 0, amendedRow, 1, row.size)
                amendedRow[0] = i
                amendedRow[row.size + 1] = inspections
                amendedRow[row.size + 2] = inspectionsExt
                amendedData.add(amendedRow)
                i++
            }

            return amendedData
        }
    }
}

