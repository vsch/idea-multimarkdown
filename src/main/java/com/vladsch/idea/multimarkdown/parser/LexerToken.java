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

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.vladsch.idea.multimarkdown.util.DataPrinterAware;
import com.vladsch.idea.multimarkdown.util.StringUtilKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.vladsch.idea.multimarkdown.psi.MultiMarkdownTypes.NONE;

public class LexerToken implements Comparable<LexerToken>, DataPrinterAware {
    @Override
    public int compareTo(@NotNull LexerToken o) {
        return compare(o);
    }

    private final MultiMarkdownLexParser.Range range;
    private final IElementType elementType;
    private int nesting;

    @NotNull
    @Override
    public String testData() {
        return "LexerToken(" + range.start + ", " + range.end + ", " + (elementType instanceof DataPrinterAware ? ((DataPrinterAware) elementType).testData() : elementType.toString()) + ", -1)";
    }

    @NotNull
    @Override
    public String className(@Nullable Class<Object> inParent) {
        return DefaultImpls.className(this,null);
    }

    public LexerToken(final MultiMarkdownLexParser.Range range, final IElementType elementType) {
        this.range = range;
        this.elementType = elementType;
        this.nesting = Integer.MAX_VALUE;
    }

    public LexerToken(int start, int end, final IElementType elementType) {
        this.range = new MultiMarkdownLexParser.Range(start, end);
        this.elementType = elementType;
        this.nesting = Integer.MAX_VALUE;
    }

    public LexerToken(int start, int end, final IElementType elementType, int nesting) {
        this.range = new MultiMarkdownLexParser.Range(start, end);
        this.elementType = elementType;
        this.nesting = nesting;
    }

    public LexerToken(final MultiMarkdownLexParser.Range range, final IElementType elementType, int nesting) {
        this.range = range;
        this.elementType = elementType;
        this.nesting = nesting;
    }

    public MultiMarkdownLexParser.Range getRange() { return range; }

    public IElementType getElementType() { return elementType; }

    public String toString() {
        return "MultiMarkdownLexParser$LexerToken" + range.toString() + " " + elementType.toString();
    }

    public boolean isWhiteSpace() { return elementType == TokenType.WHITE_SPACE; }

    public boolean isSkippedSpace() { return elementType == NONE; }

    public int compare(LexerToken that) {
        int rangeCompare = this.range.compare(that.range);
        // HACK: integer < 0 signals testing after all has been resolved, at that point nesting is no longer valid, only range and type are needed
        return rangeCompare != 0 ? rangeCompare : (this.nesting < 0 || that.nesting < 0 ? (this.elementType == that.elementType ? 0 : 1) : (this.nesting < that.nesting ? -1 : (this.nesting > that.nesting ? 1 : 0)));
    }

    public boolean doesExtend(LexerToken that) {
        return this.elementType == that.elementType && this.range.isAdjacent(that.range);
    }
}
