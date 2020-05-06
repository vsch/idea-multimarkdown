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
import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.FlexmarkSpecExampleRenderer;
import com.vladsch.flexmark.test.util.SpecExampleRenderer;
import com.vladsch.flexmark.test.util.TestUtils;
import com.vladsch.flexmark.test.util.spec.IParseBase;
import com.vladsch.flexmark.test.util.spec.IRenderBase;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.md.nav.parser.util.MdFlexmarkSpecTestBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MdPlainTextLexerTest extends ComboSpecTestCase {
    private static final String SPEC_RESOURCE = "plain_text_lexer_ast_spec.md";
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(MdPlainTextLexerTest.class, SPEC_RESOURCE);

    public static final DataKey<Boolean> IGNORE = TestUtils.IGNORE;
    public static final DataKey<Boolean> FAIL = TestUtils.FAIL;
    public static final DataKey<Boolean> NO_FILE_EOL = TestUtils.NO_FILE_EOL;
    public static final DataKey<Integer> TIMED_ITERATIONS = TestUtils.TIMED_ITERATIONS;
    public static final DataKey<Boolean> EMBED_TIMED = TestUtils.EMBED_TIMED;
    public static final DataKey<Boolean> TIMED = TestUtils.TIMED;

    private static final DataHolder OPTIONS = new MutableDataSet().set(NO_FILE_EOL, true).toImmutable();
    private static final Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("flexmark-fail", new MutableDataSet().set(FAIL, true));
        optionsMap.put("flexmark-ignore", new com.vladsch.flexmark.util.data.MutableDataSet().set(IGNORE, true));
        optionsMap.put("pegdown-fail", new MutableDataSet().set(FAIL, false));
        optionsMap.put("no-smarts", new MutableDataSet().set(MdFlexmarkSpecTestBase.PARSER_SETTINGS, s -> s.setPegdownFlags(s.getPegdownFlags() & ~Extensions.SMARTS)));
        optionsMap.put("no-footnotes", new MutableDataSet().set(MdFlexmarkSpecTestBase.PARSER_SETTINGS, s -> s.setPegdownFlags(s.getPegdownFlags() & ~Extensions.FOOTNOTES)));
        optionsMap.put("no-abbr", new MutableDataSet().set(MdFlexmarkSpecTestBase.PARSER_SETTINGS, s -> s.setPegdownFlags(s.getPegdownFlags() & ~Extensions.ABBREVIATIONS)));
    }
    public MdPlainTextLexerTest(SpecExample example) {
        super(example, optionsMap, OPTIONS);
    }

    private interface LexerVisitor {
        Function<Object, VisitHandler<?>[]> VISIT_HANDLERS = visitor -> new VisitHandler<?>[] {
                new VisitHandler<>(LexerNode.class, ((LexerVisitor) visitor)::visit),
        };

        void visit(LexerNode node);
    }

    private static class LexerNode extends Node {
        final private String lexemeType;

        public LexerNode(String lexemeType) {
            this.lexemeType = lexemeType;
        }

        public LexerNode(BasedSequence chars, String lexemeType) {
            super(chars);
            this.lexemeType = lexemeType;
        }

        @NotNull
        @Override
        public BasedSequence[] getSegments() {
            return EMPTY_SEGMENTS;
        }

        @NotNull
        @Override
        public String getNodeName() {
            return lexemeType;
        }
    }

    private static class Parser extends IParseBase {
        final private MdPlainTextLexer myLexer;

        public Parser() {
            this(null);
        }

        public Parser(DataHolder options) {
            super(options);
            myLexer = new MdPlainTextLexer();
        }

        @NotNull
        @Override
        public Node parse(@NotNull BasedSequence input) {
            // here we make the lexer parse the input sequence from start to finish and accumulate everything in custom nodes
            LexerNode root = new LexerNode("Root");
            myLexer.start(input, 0, input.length());
            while (true) {
                IElementType lexeme = myLexer.getTokenType();
                if (lexeme == null) break;
                LexerNode node = new LexerNode(input.subSequence(myLexer.getTokenStart(), myLexer.getTokenEnd()), lexeme.toString());
                root.appendChild(node);
                myLexer.advance();
            }

            root.setCharsFromContent();
            return root;
        }
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return getTestData(RESOURCE_LOCATION);
    }

    @Override
    public @NotNull
    SpecExampleRenderer getSpecExampleRenderer(@NotNull SpecExample example, @Nullable DataHolder exampleOptions) {
        DataHolder combinedOptions = aggregate(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, combinedOptions, new Parser(combinedOptions), IRenderBase.NULL_RENDERER, true);
    }
}
