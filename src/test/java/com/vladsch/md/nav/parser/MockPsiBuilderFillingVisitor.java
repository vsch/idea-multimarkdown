/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

package com.vladsch.md.nav.parser;

import com.intellij.psi.tree.IElementType;
import com.vladsch.md.nav.parser.ast.MdASTChildVisitor;
import com.vladsch.md.nav.parser.ast.MdASTLeafNode;
import com.vladsch.md.nav.parser.ast.MdASTNode;
import com.vladsch.md.nav.parser.util.MockPsiBuilder;
import com.vladsch.md.nav.psi.util.MdTypes;
import org.jetbrains.annotations.NotNull;

public class MockPsiBuilderFillingVisitor extends MdASTChildVisitor {
    @NotNull
    private final MockPsiBuilder builder;

    public MockPsiBuilderFillingVisitor(@NotNull MockPsiBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void visitNode(@NotNull MdASTNode node) {
        if (node instanceof MdASTLeafNode) {
            return;
        }

        //ensureBuilderInPosition(node.getStartOffset(), node.getElementType() != MultiMarkdownParserDefinition.MULTIMARKDOWN_FILE);
//        if (node.getStartOffset() == node.getEndOffset()) {
//            int tmp = 0;
//        }
        ensureBuilderInPosition(node.getStartOffset(), node.getStartOffset() < node.getEndOffset(), false);
        IElementType elementType = builder.getTokenType();

        final MockPsiBuilder.Marker marker = builder.mark();

        super.visitNode(node);
        IElementType nextType = builder.getTokenType();

        //ensureBuilderInPosition(node.getEndOffset(), false);
        // we cannot check exact end because we don't store leaf ast nodes
        //if (node.getElementType() == MultiMarkdownTypes.VERBATIM_CONTENT) ensureBuilderInPosition(node.getEndOffset() + 1, false, false);
        //else ensureBuilderInPosition(node.getEndOffset(), false, false);
        ensureBuilderInPosition(node.getEndOffset(), false, false);

        marker.done(node.getElementType());
    }

    private void ensureBuilderInPosition(int position, boolean exactPos, boolean suppressBlankLine) {
        while (builder.getCurrentOffset() < position && !builder.eof()) {
            if (!suppressBlankLine && builder.getTokenType() == MdTypes.BLANK_LINE) {
                // we make this a composite
                final MockPsiBuilder.Marker marker = builder.mark();
                builder.advanceLexer();
                marker.done(MdTypes.BLANK_LINE);
            } else {
                builder.advanceLexer();
            }
        }

//        if (exactPos && builder.getCurrentOffset() != position) {
//            int tmp = 0;
//            //throw new AssertionError("parsed tree and lexer are out of sync");
//        }
    }
}
