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
package com.vladsch.md.nav.util;

import com.vladsch.md.nav.editor.util.HtmlProviderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CountingBagTest.class,

        TestPathInfo.class,
        TestFileInfo.class,
        TestLinkRef_from.class,
        TestLinkMatcher_MultiSub.class,
        TestGitHubLinkResolver_normalizedLinkRef.class,
        WantBitFieldTest.class,
        WantTest.class,
        TestLinkRef.class,

        TestLinkResolver_Basic_wiki_Home.class,
        TestLinkResolver_Basic_wiki_normal_file.class,
        TestLinkResolver_Basic_Readme.class,
        TestLinkResolver_Completion.class,

        HtmlProviderTest.class,
        MdIndentConverterTest.class,
})

public class UtilTestSuite {

}
