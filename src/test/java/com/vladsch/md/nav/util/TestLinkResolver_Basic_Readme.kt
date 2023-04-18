/*
 * Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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
@file:Suppress("UNUSED_VARIABLE")

package com.vladsch.md.nav.util

import com.vladsch.md.nav.testUtil.TestCaseUtils.assertEqualsMessage
import com.vladsch.md.nav.testUtil.TestCaseUtils.compareOrderedLists
import com.vladsch.md.nav.testUtil.TestCaseUtils.compareUnorderedLists
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.ifEmpty
import com.vladsch.plugin.util.prefixWith
import com.vladsch.plugin.util.startsWith
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(value = Parameterized::class)
class TestLinkResolver_Basic_Readme constructor(
    val location: String
    , val fullPath: String
    , val linkRefType: (containingFile: FileRef, linkRef: String, anchor: String?, targetRef: FileRef?, isNormalized: Boolean) -> LinkRef
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

    private val resolvesLocal: String?
    private val resolvesExternal: String?
    private val filePathInfo = FileRef(fullPath)
    private val resolver = GitHubLinkResolver(MarkdownTestData, filePathInfo)
    private val linkRef = LinkRef.parseLinkRef(filePathInfo, linkAddress + linkAnchor.prefixWith('#'), null, linkRefType)
    private val linkRefNoExt = LinkRef.parseLinkRef(filePathInfo, linkRef.filePathNoExt + linkAnchor.prefixWith('#'), null, linkRefType)
    private val fileList = ArrayList<FileRef>(MarkdownTestData.filePaths.size)
    private val multiResolve: Array<String>
    private val localLinkRef = resolvesLocalRel
    private val externalLinkRef = resolvesExternalRel
    private val skipTest = linkRef.isExternal && !linkRef.filePath.startsWith(MarkdownTestData.mainGitHubRepo.baseUrl ?: "/", ignoreCase = true)

    private fun resolveRelativePath(filePath: String?): PathInfo? {
        return if (filePath == null) null else if (filePath.startsWith("http://", "https://")) PathInfo(filePath) else PathInfo.appendParts(filePathInfo.path, filePath)
    }

    init {
        resolvesLocal = resolveRelativePath(resolvesLocalRel)?.filePath
        resolvesExternal = resolveRelativePath(resolvesExternalRel)?.filePath

        val multiResolveAbs = ArrayList<String>()

        if (multiResolvePartial.isEmpty() && resolvesLocal != null) multiResolveAbs.add(resolvesLocal)

        for (path in multiResolvePartial) {
            val resolvedPath = resolveRelativePath(path)?.filePath.orEmpty()
            multiResolveAbs.add(resolvedPath)
        }

        multiResolve = multiResolveAbs.toArray(Array(0) { "" })

        for (path in MarkdownTestData.filePaths) {
            fileList.add(FileRef(path))
        }
    }

    @Test
    fun test_ResolveLocal() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, Want(Local.REF, Remote.REF), fileList)
        assertEqualsMessage(location + "\nLocal does not match ${resolver.getMatcher(linkRef).linkAllMatch}", resolvesLocal, localRef?.filePath)
    }

    @Test
    fun test_ResolveExternal() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, Want(Local.NONE, Remote.REF, Links.URL), fileList)
        assertEqualsMessage(location + "\nExternal does not match ${resolver.getMatcher(linkRef).linkAllMatch}", resolvesExternal, localRef?.filePath)
    }

    @Test
    fun test_LocalLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, Want(Local.REF, Remote.REF), fileList)
        val localRefAddress = if (localRef != null) resolver.linkAddress(linkRef, localRef, null, null, reduceToAnchor = true) else null
        assertEqualsMessage(location + "\nLocal link address does not match ${resolver.getMatcher(linkRef).linkAllMatch}", this.linkAddressText, localRefAddress)
    }

    @Test
    fun test_RelativeLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, Want(Local.REL, Remote.REL), fileList)
        assertEqualsMessage(location + "\nExpected Relative linkRef, got $localRef", true, true)
        assertEqualsMessage(location + "\nRelative link address does not match ${resolver.getMatcher(linkRef).linkAllMatch}", this.linkAddressText, (localRef as? LinkRef)?.filePathWithAnchor)
    }

    @Test
    fun test_RemoteLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, Want(Local.REF, Remote.REF), fileList) as? FileRef
        val remoteRef = resolver.resolve(linkRef, Want(Local.NONE, Remote.URL, Links.URL), fileList)
        val remoteRefAddress = if (localRef == null && remoteRef != null) resolver.linkAddress(linkRef, remoteRef, linkRef !is WikiLinkRef && (linkRef.hasExt || (linkRef.hasAnchor && linkAnchor?.contains('.') ?: false)), null, reduceToAnchor = true) else null
        assertEqualsMessage(location + "\nRemote based link address does not match ${resolver.getMatcher(linkRef).linkAllMatch}", this.remoteAddressText, remoteRefAddress)
    }

    @Test
    fun test_AnyLinkAddress() {
        if (skipTest) return
        val anyRef = resolver.resolve(linkRef, Want(), fileList)
        val linkAddress = if (anyRef != null) if (anyRef is FileRef) resolver.linkAddress(linkRef, anyRef, null, null, reduceToAnchor = true) else anyRef.filePath else null
        assertEqualsMessage(location + "\nWant() link address does not match ${resolver.getMatcher(linkRef).linkAllMatch}", this.linkAddressText
            ?: this.remoteAddressText, linkAddress)
    }

    @Test
    fun test_OnlyURILinkAddress() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, Want(), fileList)
        val uriRef = resolver.resolve(linkRef, Want(Local.URI, Remote.URI, Links.REL), fileList)
        val href = if (targetRef != null && targetRef is FileRef && this.uriText != null) {
            "file://" + targetRef.filePath.replace(" ", "%20").replace("#", "%23") + linkRef.anchorText
        } else if (targetRef is LinkRef) targetRef.filePathWithAnchor
        else null
        assertEqualsMessage(location + "\nLinkResolver.ONLY_URI link address does not match ${resolver.getMatcher(linkRef).linkAllMatch}", href, (uriRef as? LinkRef)?.filePathWithAnchor)
    }

    @Test
    fun test_ResolveURILinkAddress() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, Want(), fileList)
        val href = (if (targetRef != null && targetRef is FileRef && this.uriText != null) {
            "file://" + targetRef.filePath.replace(" ", "%20").replace("#", "%23")
        } else if (targetRef is LinkRef) targetRef.filePathWithAnchor
        else null) ?: return

        val uriLinkRef = linkRef.replaceFilePath(href, true)
        val uriRef = resolver.resolve(uriLinkRef, Want(Local.URI, Remote.URI, Links.REL), fileList)
        assertEqualsMessage(location + "\nLinkResolver.ONLY_URI link address does not match ${resolver.getMatcher(linkRef).linkAllMatch}", href + linkRef.anchorText, (uriRef as? LinkRef)?.filePathWithAnchor)
    }

    @Test
    fun test_RemoteURILinkAddress() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, Want(Local.REF, Remote.REF, Links.URL), fileList)
        val uriRef = resolver.resolve(linkRef, Want(Local.NONE, Remote.URL, Links.URL), fileList)
        val href = if (targetRef != null && targetRef is FileRef && this.uriText != null) {
            if (resolver.projectResolver.isUnderVcs(targetRef)) {
                val contFilePrefix = linkRef.containingFile.path.substring("/Users/vlad/src/MarkdownTest/".length)
                val prefix = if (targetRef.isUnderWikiDir) "" else "${if (linkRef is ImageLinkRef) "raw" else "blob"}/master/" + contFilePrefix
                val parts = this.uriText.split("#", limit = 2)
                "https://github.com/vsch/MarkdownTest/" + prefix + parts[0].replace("#", "%23").replace(" ", "%20") + (if (parts.size > 1) parts[1].prefixWith('#') else "")
            } else {
                null
            }
        } else if (targetRef is LinkRef) targetRef.filePathWithAnchor
        else null
        assertEqualsMessage(location + "\nLinkResolver.ONLY_URI link address does not match ${resolver.getMatcher(linkRef).linkAllMatch}", href, (uriRef as? LinkRef)?.filePathWithAnchor)
    }

    @Test
    fun test_ResolveRemoteURILinkAddress() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, Want(Local.REF, Remote.REF, Links.URL), fileList)
        val uriRef = resolver.resolve(linkRef, Want(Local.NONE, Remote.URL, Links.URL), fileList)
        val href = if (targetRef != null && targetRef is FileRef && this.uriText != null) {
            if (resolver.projectResolver.isUnderVcs(targetRef)) {
                val contFilePrefix = linkRef.containingFile.path.substring("/Users/vlad/src/MarkdownTest/".length)
                val prefix = if (targetRef.isUnderWikiDir) "" else "${if (linkRef is ImageLinkRef) "raw" else "blob"}/master/" + contFilePrefix
                val parts = this.uriText.split("#", limit = 2)
                "https://github.com/vsch/MarkdownTest/" + prefix + parts[0].replace("#", "%23").replace(" ", "%20") + (if (parts.size > 1) parts[1].prefixWith('#') else "")
            } else {
                null
            }
        } else if (targetRef is LinkRef) targetRef.filePathWithAnchor
        else null

        if (uriRef is LinkRef) {
            val urlRef = resolver.resolve(uriRef, Want(Local.NONE, Remote.URL, Links.URL), fileList)
            assertEqualsMessage(location + "\nLinkResolver.URL link address does not match ${resolver.getMatcher(linkRef).linkAllMatch}", uriRef.filePathWithAnchor, (urlRef as? LinkRef)?.filePathWithAnchor)
        }
    }

    @Test
    fun test_MultiResolveExactNoMatch() {
        if (skipTest || localLinkRef != null) return
        val localRefs = resolver.multiResolve(linkRef, Want(Local.REF, Remote.REF), fileList)
        val actuals = Array<String>(localRefs.size) { "" }
        for (i in localRefs.indices) {
            actuals[i] = localRefs[i].filePath
        }
        compareOrderedLists(location + "\nMultiResolve exact does not match ${resolver.getMatcher(linkRef).linkAllMatch}", arrayOf<String>(), actuals)
    }

    @Test
    fun test_Resolve() {
        if (skipTest) return
        val localRefs = resolver.multiResolve(linkRef, Want(Links.URL), fileList)
        val targetRef = if (localRefs.isNotEmpty()) localRefs[0] else null
        assertEqualsMessage(location + "\nResolve does not match ${resolver.getMatcher(linkRef).linkAllMatch}", this.resolvesLocal
            ?: this.resolvesExternal, if (targetRef is LinkRef) targetRef.filePathWithAnchor else targetRef?.filePath)
    }

    @Test
    fun test_InspectionResults() {
        if (skipTest) return
        val targetRef = resolver.resolve(linkRef, Want(), fileList) as? FileRef
        if (targetRef != null) {
            val inspections = resolver.inspect(linkRef, targetRef, location)
            compareUnorderedLists(location + "\nInspectionResults do not match ${resolver.getMatcher(linkRef).linkAllMatch}", inspectionResults, inspections)
        }
    }

    @Test
    fun test_ResolveHttp() {
        if (skipTest) return
        if (resolvesLocal != null) {
            val targetRef = FileRef(resolvesLocal)
            val linkRefUri = resolver.processMatchOptions(linkRef, targetRef, Want(Local.URL, Remote.URL, Links.URL))
            if (linkRefUri != null && linkRefUri is LinkRef && linkRefUri.isExternal) {
                val originalCaseLinkRefUri = if (linkRefUri.filePath.endsWith("/wiki")) linkRefUri else linkRefUri.replaceFilePath(linkRefUri.path + linkRef.fileName.ifEmpty { linkRefUri.fileName })
                val localRef = resolver.resolve(originalCaseLinkRefUri, Want(Local.REF, Remote.REF), fileList)
                assertEqualsMessage(location + "\nResolveHttp $originalCaseLinkRefUri does not match ${resolver.getMatcher(linkRef).linkAllMatch}", resolvesLocal, localRef?.filePath)
            } else {
                assertEqualsMessage(location + "\nResolveHttp no remote link ${resolver.getMatcher(linkRef).linkAllMatch} for $resolvesLocal", resolvesLocal, linkRefUri?.filePath)
            }
        }
    }

    @Test
    fun test_ResolveAbs() {
        if (skipTest) return
        if (resolvesLocal != null) {
            val targetRef = FileRef(resolvesLocal)
            val linkRefAbs = resolver.resolve(linkRef, Want(Local.ABS, Remote.ABS, Links.NONE))
            val linkRefUrl = resolver.resolve(linkRef, Want(Local.URL, Remote.URL, Links.NONE))
            val vcsRoot = resolver.projectResolver.getVcsRoot(targetRef)
            if (vcsRoot == null || linkRefUrl == null) return
            var linkRefExp: String? = PathInfo.relativePath(vcsRoot.baseUrl
                ?: "/", linkRefUrl.filePath, withPrefix = false, blobRawEqual = false).removePrefix("blob/").removePrefix("raw/").removePrefix("master/").prefixWith('/').ifEmpty("/")
            if (linkRefExp.startsWith("/wiki/") || linkRefExp == "/wiki") linkRefExp = "/../..$linkRefExp"
            val targetVcsRoot = resolver.projectResolver.getVcsRoot(targetRef)
            val fileVcsRoot = resolver.projectResolver.getVcsRoot(linkRef.containingFile)
            if (targetVcsRoot?.basePath != fileVcsRoot?.basePath) linkRefExp = null
            assertEqualsMessage(location + "\nResolveAbs no remote link ${resolver.getMatcher(linkRef).linkAllMatch} for $resolvesLocal", linkRefExp, linkRefAbs?.filePath)
        }
    }

    @Test
    fun test_ResolveHttpRaw() {
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
                    assertEqualsMessage(location + "\nResolveHttpRaw $originalCaseLinkRefUri does not match ${resolver.getMatcher(rawLinkRef).linkAllMatch}", resolvesLocal, localRef?.filePath)
                } else {
                    assertEqualsMessage(location + "\nResolveHttpRaw no remote link ${resolver.getMatcher(rawLinkRef).linkAllMatch} for $resolvesLocal", resolvesLocal, linkRefUri?.filePath)
                }
            }
        }
    }

    // QUERY: is this an outdated test or should be enabled?
    //
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
    //                    assertEqualsMessage(location + "\nResolveHttp $originalCaseLinkRefUri does not match ${resolver.getMatcher(linkRef).linkAllMatch}", null, localRef?.filePath)
    //                } else {
    //                    assertEqualsMessage(location + "\nResolveHttp no remote link ${resolver.getMatcher(linkRef).linkAllMatch} for $resolvesLocal", resolvesLocal, linkRefUri?.filePath)
    //                }
    //            }
    //        }
    //    }

    @Test
    fun test_MultiResolveHttp() {
        if (skipTest) return
        if (resolvesLocal != null) {
            val targetRef = FileRef(resolvesLocal)
            val linkRefUri = resolver.processMatchOptions(linkRef, targetRef, Want(Local.URL, Remote.URL, Links.URL))

            assert(linkRefUri == null || linkRefUri.isExternal)

            if (linkRefUri != null && linkRefUri is LinkRef && linkRefUri.isExternal) {
                val originalCaseLinkRefUri = if (linkRefUri.filePath.endsWith("/wiki")) linkRefUri else linkRefUri.replaceFilePath(linkRefUri.path + linkRef.fileName.ifEmpty { linkRefUri.fileName })
                val localRefs = resolver.multiResolve(originalCaseLinkRefUri, Want())
                val resolvedTargetRef = if (localRefs.isNotEmpty()) localRefs[0] else null
                assertEqualsMessage(location + "\nMultiResolveHttp $originalCaseLinkRefUri does not match ${resolver.getMatcher(linkRef).linkAllMatch}", this.resolvesLocal, if (resolvedTargetRef is LinkRef) resolvedTargetRef.filePathWithAnchor else resolvedTargetRef?.filePath)
            }
        }
    }

    @Test
    fun test_InspectionResultsUri() {
        if (skipTest) return
        if (resolvesLocal != null) {
            val targetRef = FileRef(resolvesLocal)
            val linkRefUri = resolver.processMatchOptions(linkRef, targetRef, Want(Local.URL, Remote.URL, Links.URL))

            assert(linkRefUri == null || linkRefUri.isExternal)

            if (linkRefUri != null && linkRefUri is LinkRef && linkRefUri.isExternal) {
                val originalCaseLinkRefUri = if (linkRefUri.filePath.endsWith("/wiki")) linkRefUri else linkRefUri.replaceFilePath(linkRefUri.path + linkRef.fileName.ifEmpty { linkRefUri.fileName })
                val inspections = resolver.inspect(originalCaseLinkRefUri, targetRef, location)
                compareUnorderedLists(location + "\nInspectionResultsUri $originalCaseLinkRefUri do not match ${resolver.getMatcher(linkRef).linkAllMatch}", inspectionExtResults, inspections)
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
            return MarkdownTest__Readme_md.data()
        }
    }
}

