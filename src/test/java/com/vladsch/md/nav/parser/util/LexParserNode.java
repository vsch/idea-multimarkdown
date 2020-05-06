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

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;

public class LexParserNode extends Node {
    final private String lexemeType;

    public LexParserNode(String lexemeType) {
        this.lexemeType = lexemeType;
    }

    public LexParserNode(BasedSequence chars, String lexemeType) {
        super(chars);
        this.lexemeType = lexemeType;
    }

    @NotNull
    @Override
    public BasedSequence[] getSegments() {
        return EMPTY_SEGMENTS;
    }

    @Override
    public void getAstExtra(@NotNull StringBuilder out) {
        astExtraChars(out);
    }

    @NotNull
    @Override
    public String getNodeName() {
        return lexemeType;
    }
}
