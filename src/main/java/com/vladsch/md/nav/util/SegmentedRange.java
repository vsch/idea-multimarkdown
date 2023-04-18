// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.util.sequence.Range;
import com.vladsch.md.nav.parser.LexParserState;
import com.vladsch.md.nav.psi.util.MdTypes;

import java.util.ArrayList;

public class SegmentedRange {
    final public static SegmentedRange EMPTY = new SegmentedRange(0, 0, MdTypes.NONE, MdTypes.NONE);

    final protected ArrayList<Range> segments;
    final protected IElementType tokenType;           // this is the element type of the range
    final protected IElementType originalTokenType;   // for synthetic nodes this is the original element type for determining whether it gets excluded by child

    public IElementType getOriginalTokenType() {
        return originalTokenType;
    }

    public ArrayList<Range> getSegments() {
        return segments;
    }

    public IElementType getTokenType() {
        return tokenType;
    }

    //public void setTokenType(IElementType tokenType) { this.tokenType = tokenType; }

    public boolean isEmpty() { return segments.isEmpty(); }

    public SegmentedRange(IElementType tokenType, IElementType originalTokenType) {
        this.tokenType = tokenType;
        this.originalTokenType = originalTokenType;
        segments = new ArrayList<>(1);
    }

    public SegmentedRange(int start, int end, IElementType tokenType, IElementType originalTokenType) {
        this(Range.of(start, end), tokenType, originalTokenType);
    }

    public SegmentedRange(Range range, IElementType tokenType, IElementType originalTokenType) {
        this(tokenType, originalTokenType);
        segments.add(range);
    }

    SegmentedRange(ArrayList<Range> ranges, IElementType tokenType, IElementType originalTokenType) {
        this.tokenType = tokenType;
        this.originalTokenType = originalTokenType;
        segments = new ArrayList<>(ranges);
    }

    SegmentedRange(SegmentedRange that) {
        this.tokenType = that.tokenType;
        this.originalTokenType = that.originalTokenType;
        segments = new ArrayList<>(that.segments);
    }

    SegmentedRange(SegmentedRange that, IElementType tokenType) {
        this.tokenType = tokenType;
        this.originalTokenType = that.originalTokenType;
        segments = new ArrayList<>(that.segments);
    }

    public SegmentedRange withTokenType(IElementType tokenType) {
        return this.isEmpty() || tokenType == this.tokenType ? this : new SegmentedRange(this, tokenType);
    }

    public boolean doesContain(Range range) {
        for (Range range1 : segments) {
            if (range1.doesContain(range)) return true;
        }
        return false;
    }

    public void addIntersections(Range range, SegmentedRange segmentedRange) {
        for (Range range1 : segmentedRange.getSegments()) {
            if (range.doesOverlap(range1)) {
                Range newRange = range.intersect(range1);
                segments.add(newRange);
            }
        }
    }

    public SegmentedRange intersections(Range range, IElementType tokenType, IElementType originalTokenType) {
        SegmentedRange newSegmented = new SegmentedRange(tokenType, originalTokenType);
        for (Range range1 : getSegments()) {
            if (range.doesOverlap(range1)) {
                Range newRange = range.intersect(range1);
                newSegmented.segments.add(newRange);
            }
        }
        return newSegmented;
    }

    public void addSegment(Range range) {
        segments.add(range);
    }

    public void addIntersections(SegmentedRange segmentedRange1, SegmentedRange segmentedRange2) {
        for (Range range1 : segmentedRange1.getSegments()) {
            for (Range range2 : segmentedRange2.getSegments()) {
                if (range1.doesOverlap(range2)) {
                    Range newRange = range1.intersect(range2);
                    segments.add(newRange);
                }
            }
        }
    }

    public SegmentedRange exclude(SegmentedRange segmentedRange) {
        for (Range range : segmentedRange.getSegments()) {
            if (exclude(range).isEmpty()) break;
        }
        return this;
    }

    public SegmentedRange exclude(Range range) {
        int i, iMax = segments.size();

        for (i = 0; i < iMax; i++) {
            Range range1 = segments.get(i);
            if (range1.doesOverlap(range)) {
                if (range1.doesContain(range)) {
                    if (range1.doesProperlyContain(range)) {
                        // split range1 into 2 and add the new one
                        Range newRange1 = range1.withEnd(range.getStart());
                        Range newRange2 = range1.withStart(range.getEnd());
                        segments.set(i, newRange1);
                        i++;
                        segments.add(i, newRange2);
                        iMax++;
                    } else {
                        if (range1.isEqual(range)) {
                            // remove, they are the same
                            segments.remove(i);
                            i--;
                            iMax--;
                        } else {
                            // truncate range1 and replace
                            Range newRange1 = range1.exclude(range);
                            segments.set(i, newRange1);
                        }
                    }
                } else if (range.doesContain(range1)) {
                    // delete it
                    segments.remove(i);
                    i--;
                    iMax--;
                } else {
                    // they overlap but neither contains the other
                    // truncate range1 and replace
                    Range newRange1 = range1.exclude(range);
                    segments.set(i, newRange1);
                }
            }
        }
        return this;
    }

    //protected boolean isExcludedByChild(IElementType child) { return !isSynthetic && isExcludedByChild(tokenType, child); }

    public boolean isExcludedByChild(LexParserState lexParserState, SegmentedRange child) {
        return lexParserState.isExcludedByChild(originalTokenType, child.originalTokenType);
        //|| (isSynthetic && child.isSynthetic && isExcludedByChild(tokenType, child.tokenType));
    }

    public boolean isExcludedByParent(LexParserState lexParserState, SegmentedRange parent) {
        return lexParserState.isExcludedByParent(parent.originalTokenType, originalTokenType);
        //|| (isSynthetic && child.isSynthetic && isExcludedByChild(tokenType, child.tokenType));
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("" + tokenType + " ");
        if (!isEmpty()) for (Range range : segments) out.append(range.toString());
        else out.append("<empty>");
        return out.toString();
    }
}
