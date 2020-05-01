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

package com.vladsch.md.nav.parser.util;

import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.test.util.spec.IParseBase;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.misc.DelimitedBuilder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.parser.LexerData;
import com.vladsch.md.nav.parser.MdLexer;
import com.vladsch.md.nav.parser.MockPsiBuilderFillingVisitor;
import com.vladsch.md.nav.parser.ast.MdASTCompositeNode;
import com.vladsch.md.nav.psi.util.MdTypes;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.enh.testUtil.MdEnhSpecTestSetup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class FlexmarkLexParser extends IParseBase {
    final private MdLexer myLexer;

    public FlexmarkLexParser(@Nullable DataHolder options) {
        super(options);

        MdRenderingProfile renderingProfile = MdEnhSpecTestSetup.RENDERING_PROFILE_OPTION.setInstanceData(new MdRenderingProfile(), options);
        int extensionFlags = renderingProfile.getParserSettings().getPegdownFlags();
        long optionsFlags = renderingProfile.getParserSettings().getOptionsFlags();
        myLexer = new MdLexer(renderingProfile, extensionFlags, optionsFlags);
    }

    @NotNull
    @Override
    public Node parse(@NotNull BasedSequence input) {
        // here we make the lexer parse the input sequence from start to finish and accumulate everything in custom nodes
        ArrayList<Lexeme> lexemes = new ArrayList<>();

        myLexer.start(input, 0, input.length());

        while (myLexer.getTokenType() != null) {
            Lexeme lexeme = new Lexeme(myLexer.getTokenType(), myLexer.getTokenStart(), myLexer.getTokenEnd());
            lexemes.add(lexeme);
            myLexer.advance();
        }

        MockPsiBuilder builder = new MockPsiBuilder(input, lexemes);

        LexerData lexerData = myLexer.getLexerData();
        if (lexerData != null) {
            final MdASTCompositeNode parsedTree = lexerData.rootNode;

            assert builder.getCurrentOffset() == 0;
            new MockPsiBuilderFillingVisitor(builder).visitNode(parsedTree);
        } else {
            MockPsiBuilder.Marker rootMarker = builder.mark();
            while (!builder.eof()) builder.advanceLexer();
            rootMarker.done(MdTypes.FILE);
        }

        if (!builder.eof()) {
            // we drain and use the list as part of the exception message
            DelimitedBuilder out = new DelimitedBuilder("\n");
            while (!builder.eof()) {
                IElementType type = builder.getTokenType();
                int offset = builder.getOffset();
                Lexeme lexeme = lexemes.get(offset);
                out.append(type.toString()).append("[").append(lexeme.getStart()).append(", ").append(lexeme.getEnd()).append("]").mark();
                builder.advanceLexer();
            }

            throw new IllegalStateException("Builder had elements left:\n" + out.toString());
        }

        LexParserNode root = builder.buildTree(input);
        return root;
    }
}
