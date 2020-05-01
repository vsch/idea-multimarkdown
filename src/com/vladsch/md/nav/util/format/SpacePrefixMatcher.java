// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format;

import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import org.jetbrains.annotations.NotNull;

public class SpacePrefixMatcher implements LinePrefixMatcher {
    private final int myLeadingSpaces;

    private SpacePrefixMatcher(final int leadingSpaces) {
        myLeadingSpaces = leadingSpaces;
    }

    /**
     * Get the size of the prefix matched on the given line
     *
     * @param lineChars      line characters
     * @param indentColumn   column number of first character on the line
     * @param editContext parser settings to use for determining the prefix removal pattern
     *
     * @return column of first non-prefix line content
     */

    @Override
    public int contentColumn(
            @NotNull CharSequence lineChars,
            int indentColumn,
            @NotNull PsiEditContext editContext
    ) {
        // default 4 leading spaces removed
        int column = indentColumn;
        int leadingSpaces = myLeadingSpaces;
        int i = 0;

        while (leadingSpaces > 0 && i < lineChars.length()) {
            switch (lineChars.charAt(i++)) {
                case '\t':
                    int spaces = min(columnsToNextTabStop(column), leadingSpaces);
                    leadingSpaces -= spaces;
                    column += spaces;
                    break;

                case ' ':
                    leadingSpaces--;
                    column++;
                    break;

                default:
                    return column;
            }
        }
        return column;
    }

    private static int min(int a, int b) {
        return Math.min(a, b);
    }

    public final static LinePrefixMatcher FIXED_1_SPACES_MATCHER = new SpacePrefixMatcher(1);
    public final static LinePrefixMatcher FIXED_2_SPACES_MATCHER = new SpacePrefixMatcher(2);
    public final static LinePrefixMatcher FIXED_3_SPACES_MATCHER = new SpacePrefixMatcher(3);
    public final static LinePrefixMatcher FIXED_4_SPACES_MATCHER = new SpacePrefixMatcher(4);
    public final static LinePrefixMatcher FIXED_5_SPACES_MATCHER = new SpacePrefixMatcher(5);
    public final static LinePrefixMatcher FIXED_6_SPACES_MATCHER = new SpacePrefixMatcher(6);
    public final static LinePrefixMatcher FIXED_7_SPACES_MATCHER = new SpacePrefixMatcher(7);
    public final static LinePrefixMatcher FIXED_8_SPACES_MATCHER = new SpacePrefixMatcher(8);
    public final static LinePrefixMatcher FIXED_9_SPACES_MATCHER = new SpacePrefixMatcher(9);
    public final static LinePrefixMatcher FIXED_10_SPACES_MATCHER = new SpacePrefixMatcher(10);
    public final static LinePrefixMatcher FIXED_11_SPACES_MATCHER = new SpacePrefixMatcher(11);
    public final static LinePrefixMatcher FIXED_12_SPACES_MATCHER = new SpacePrefixMatcher(12);

    private final static LinePrefixMatcher[] FIXED_SPACES_MATCHERS = new LinePrefixMatcher[] {
            LinePrefixMatcher.NULL,
            FIXED_1_SPACES_MATCHER,
            FIXED_2_SPACES_MATCHER,
            FIXED_3_SPACES_MATCHER,
            FIXED_4_SPACES_MATCHER,
            FIXED_5_SPACES_MATCHER,
            FIXED_6_SPACES_MATCHER,
            FIXED_7_SPACES_MATCHER,
            FIXED_8_SPACES_MATCHER,
            FIXED_9_SPACES_MATCHER,
            FIXED_10_SPACES_MATCHER,
            FIXED_11_SPACES_MATCHER,
            FIXED_12_SPACES_MATCHER,
    };

    public final static LinePrefixMatcher NON_INDENT_SPACES_MATCHER = FIXED_3_SPACES_MATCHER;

    public static int columnsToNextTabStop(int column) {
        return 4 - column % 4;
    }

    public static LinePrefixMatcher maxSpaces(int leadingSpaces) {
        return leadingSpaces < FIXED_SPACES_MATCHERS.length ? FIXED_SPACES_MATCHERS[leadingSpaces] : new SpacePrefixMatcher(leadingSpaces);
    }
}
