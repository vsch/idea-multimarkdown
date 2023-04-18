// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.parser;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.util.sequence.Range;
import com.vladsch.md.nav.util.DataPrinterAware;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.vladsch.md.nav.psi.util.MdPsiImplUtil.isWhitespace;

public class LexerToken implements Comparable<LexerToken>, DataPrinterAware {
    @Override
    public int compareTo(@NotNull LexerToken o) {
        return compare(o);
    }

    private Range range;
    private final IElementType elementType;
    private final int nesting;

    public int getNesting() {
        return nesting;
    }

    public TextRange getTextRange() {
        return new TextRange(range.getStart(), range.getEnd());
    }

    @NotNull
    @Override
    public String testData() {
        return "LexerToken(" + range.getStart() + ", " + range.getEnd() + ", " + (elementType instanceof DataPrinterAware ? ((DataPrinterAware) elementType).testData() : elementType.toString()) + ", -1)";
    }

    @NotNull
    @Override
    public String className(@Nullable Class<Object> inParent) {
        return DefaultImpls.className(this, null);
    }

    public LexerToken(final Range range, final IElementType elementType) {
        this.range = range;
        this.elementType = elementType;
        this.nesting = Integer.MAX_VALUE;
    }

    public LexerToken(int start, int end, final IElementType elementType) {
        this.range = Range.of(start, end);
        this.elementType = elementType;
        this.nesting = Integer.MAX_VALUE;
    }

    public LexerToken(int start, int end, final IElementType elementType, int nesting) {
        this.range = Range.of(start, end);
        this.elementType = elementType;
        this.nesting = nesting;
    }

    public LexerToken(final Range range, final IElementType elementType, int nesting) {
        this.range = range;
        this.elementType = elementType;
        this.nesting = nesting;
    }

    public Range getRange() { return range; }

    public void setRange(com.vladsch.flexmark.util.sequence.Range value) { range = value; }

    public IElementType getElementType() { return elementType; }

    public String toString() {
        return "MultiMarkdownLexParser$LexerToken" + (range == null ? "null range" : range.toString()) + " " + (elementType == null ? "null type" : elementType.toString());
    }

    public boolean isWhiteSpace() { return isWhitespace(elementType); }

    public boolean isSkippedSpace() { return isWhiteSpace(); }

    public int compare(LexerToken that) {
        int rangeCompare = this.range.compare(that.range);
        // HACK: integer < 0 signals testing after all has been resolved, at that point nesting is no longer valid, only range and type are needed
        return rangeCompare != 0 ? rangeCompare : (this.nesting < 0 || that.nesting < 0 ? (this.elementType == that.elementType ? 0 : 1) : Integer.compare(this.nesting, that.nesting));
    }

    public boolean doesExtend(LexerToken that) {
        return this.elementType == that.elementType && this.range.isAdjacent(that.range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LexerToken)) return false;

        LexerToken token = (LexerToken) o;

        if (nesting != token.nesting) return false;
        if (!range.equals(token.range)) return false;
        return elementType.equals(token.elementType);
    }

    @Override
    public int hashCode() {
        int result = range.hashCode();
        result = 31 * result + elementType.hashCode();
        result = 31 * result + nesting;
        return result;
    }
}
