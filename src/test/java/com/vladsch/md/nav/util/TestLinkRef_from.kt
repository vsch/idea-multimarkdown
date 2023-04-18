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
package com.vladsch.md.nav.util

import org.junit.Test
import kotlin.test.assertEquals

class TestLinkRef_from {

    val exclusionMap: Map<String, String>? = null

    @Test
    fun test_explicitFromWiki_1() {
        val containingFile = FileRef("/Users/vlad/src/MarkdownTest/MardownTest.wiki/Home.md")
        val wikiLinkRef = WikiLinkRef(containingFile, "Home", null, null, false)
        val linkRef = LinkRef.from(wikiLinkRef, exclusionMap)

        assertEquals("#", linkRef.filePathWithAnchor)
    }

    @Test
    fun test_explicitFromWiki_2() {
        val containingFile = FileRef("/Users/vlad/src/MarkdownTest/MardownTest.wiki/Home.md")
        val wikiLinkRef = WikiLinkRef(containingFile, "Home", "#anchor", null, false)
        val linkRef = LinkRef.from(wikiLinkRef, null)

        assertEquals("#anchor", linkRef.filePathWithAnchor)
    }

    @Test
    fun test_explicitFromWiki_3() {
        val containingFile = FileRef("/Users/vlad/src/MarkdownTest/MardownTest.wiki/single-line-test.md")
        val wikiLinkRef = WikiLinkRef(containingFile, "Home", null, null, false)
        val linkRef = LinkRef.from(wikiLinkRef, null)

        assertEquals("Home", linkRef.filePathWithAnchor)
    }

    @Test
    fun test_explicitFromWiki_4() {
        val containingFile = FileRef("/Users/vlad/src/MarkdownTest/MardownTest.wiki/single-line-test.md")
        val wikiLinkRef = WikiLinkRef(containingFile, "Home", "#anchor", null, false)
        val linkRef = LinkRef.from(wikiLinkRef, null)

        assertEquals("Home#anchor", linkRef.filePathWithAnchor)
    }
}

