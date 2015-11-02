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
package com.vladsch.idea.multimarkdown.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.pegdown.ast.Node;
import org.pegdown.ast.TextNode;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.*;

@RunWith(value = Parameterized.class)
public class TestSyntheticNodes {

    private static TextNode testNode(String text, int start) {
        TextNode node = new TextNode(text);
        node.setEndIndex(start);
        node.setEndIndex(start + text.length());
        return node;
    }

    @Test
    public void test_chopLead() {
        TextNode node = testNode("[[WikiRef]]", 100);
        SyntheticNodes nodes = new SyntheticNodes(node, WIKI_LINK_REF);
        nodes.chopMarkers(2, WIKI_LINK_OPEN, WIKI_LINK_CLOSE);
    }
}
