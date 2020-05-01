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

import com.vladsch.md.nav.MdPlugin
import com.vladsch.md.nav.testUtil.TestCaseUtils.assertEqualsMessage
import com.vladsch.md.nav.testUtil.TestCaseUtils.compareUnorderedLists
import com.vladsch.md.nav.vcs.GitHubLinkResolver
import com.vladsch.plugin.util.prefixWith
import com.vladsch.plugin.util.startsWith
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(value = Parameterized::class)
class TestLinkResolver_Basic_wiki_normal_file constructor(
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
    val skipTest = linkRef.isExternal && !linkRef.filePath.startsWith(MarkdownTestData.mainGitHubRepo.baseUrl ?: "/", ignoreCase = true)

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
        val localRef = resolver.resolve(linkRef, Want(Local.NONE, Remote.REF, Links.NONE), fileList)
        assertEqualsMessage(location + "\nExternal does not match ${resolver.getMatcher(linkRef).linkAllMatch}", resolvesExternal, localRef?.filePath)
    }

    @Test
    fun test_LocalLinkAddress() {
        if (skipTest) return
        val localRef = resolver.resolve(linkRef, Want(Local.REF, Remote.REF), fileList)
        val localRefAddress = if (localRef != null) resolver.linkAddress(linkRef, localRef, ((linkRef.hasExt && linkRef.ext == localRef.ext) || (linkRef.hasAnchor && linkAnchor?.contains('.') ?: false)), null, reduceToAnchor = true) else null
        assertEqualsMessage(location + "\nLocal link address does not match ${resolver.getMatcher(linkRef).linkAllMatch}", this.linkAddressText, localRefAddress)
    }

    @Test
    fun test_InspectionResults() {
        if (skipTest || this.inspectionResults == null) return
        val looseTargetRef = resolver.resolve(linkRef, Want(Match.LOOSE), fileList) as? FileRef
        val targetRef = resolver.resolve(linkRef, Want(), fileList) as? FileRef
        if (targetRef != null || looseTargetRef != null) {
            val inspectionResults = resolver.inspect(linkRef, targetRef ?: looseTargetRef as FileRef, location)
            compareUnorderedLists(location + "\nInspectionResults do not match ${resolver.getMatcher(linkRef).linkAllMatch}", this.inspectionResults, inspectionResults)
        }
    }

    companion object {
        init {
            MdPlugin.RUNNING_TESTS = true
        }

        fun resolveRelativePath(filePathInfo: PathInfo, filePath: String?): PathInfo? {
            return if (filePath == null) null else PathInfo.appendParts(filePathInfo.path, filePath.splitToSequence("/"))
        }

        @Parameterized.Parameters(name = "{index}: filePath = {1}, linkRef = {4}, linkAnchor = {5}")
        @JvmStatic
        fun data(): Collection<Array<Any?>> {
            return MarkdownTest__MarkdownTest_wiki__normal_file_md.data()
        }
    }
}

