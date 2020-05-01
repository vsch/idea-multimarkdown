// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.psi.tree.IElementType;
import com.vladsch.flexmark.test.util.ExampleOption;
import com.vladsch.flexmark.test.util.TestUtils;
import com.vladsch.md.nav.psi.util.MdTypes;
import icons.FlexmarkIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.HashMap;

public class FlexmarkPsi {
    final private static HashMap<String, FlexmarkOptionInfo> FLEXMARK_BUILT_IN_OPTIONS = buildBuiltInOptions();
    final public static int FLEXMARK_BUILT_IN_OPTION_COUNT = maxIndex();

    final public static FlexmarkOptionInfo FLEXMARK_NORMAL_OPTION_INFO = new FlexmarkOptionInfo(ExampleOption.of(""), FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE, MdTypes.FLEXMARK_EXAMPLE_OPTION_NAME, FLEXMARK_BUILT_IN_OPTION_COUNT);
    final public static FlexmarkOptionInfo FLEXMARK_DISABLED_OPTION_INFO = new FlexmarkOptionInfo(ExampleOption.of("-"), FlexmarkIcons.Element.HIDDEN_FLEXMARK_SPEC, MdTypes.FLEXMARK_EXAMPLE_OPTION_DISABLED_NAME, FLEXMARK_BUILT_IN_OPTION_COUNT);
    final public static FlexmarkOptionInfo FLEXMARK_INVALID_OPTION_INFO = new FlexmarkOptionInfo(ExampleOption.of(""), FlexmarkIcons.Element.HIDDEN_FLEXMARK_SPEC_ERRORS, MdTypes.FLEXMARK_EXAMPLE_OPTION_NAME, FLEXMARK_BUILT_IN_OPTION_COUNT);

    // CAUTION: min index is used to determine icon for the option list in spec example so this should be 0
    final public static FlexmarkOptionInfo FLEXMARK_ERROR_OPTION_INFO = new FlexmarkOptionInfo(ExampleOption.of(""), FlexmarkIcons.Element.SPEC_EXAMPLE_ERRORS, MdTypes.FLEXMARK_EXAMPLE_OPTION_NAME, 0);

    final public static FlexmarkOptionInfo FLEXMARK_OPTION_IGNORE = FLEXMARK_BUILT_IN_OPTIONS.get(TestUtils.IGNORE_OPTION_NAME);
    final public static FlexmarkOptionInfo FLEXMARK_OPTION_FAIL = FLEXMARK_BUILT_IN_OPTIONS.get(TestUtils.FAIL_OPTION_NAME);
    final public static FlexmarkOptionInfo FLEXMARK_OPTION_EMBED_TIMED = FLEXMARK_BUILT_IN_OPTIONS.get(TestUtils.EMBED_TIMED_OPTION_NAME);
    final public static FlexmarkOptionInfo FLEXMARK_OPTION_TIMED = FLEXMARK_BUILT_IN_OPTIONS.get(TestUtils.TIMED_OPTION_NAME);
    // NOTE: this one is not an option to use in spec example but set in data holder
//    final public static FlexmarkOptionInfo FLEXMARK_OPTION_TIMED_ITERATIONS = FLEXMARK_BUILT_IN_OPTIONS.get(TestUtils.TIMED_ITERATIONS_OPTION_NAME);
    final public static FlexmarkOptionInfo FLEXMARK_OPTION_NO_FILE_EOL = FLEXMARK_BUILT_IN_OPTIONS.get(TestUtils.NO_FILE_EOL_OPTION_NAME);
    final public static FlexmarkOptionInfo FLEXMARK_OPTION_FILE_EOL = FLEXMARK_BUILT_IN_OPTIONS.get(TestUtils.FILE_EOL_OPTION_NAME);

    final public static FlexmarkOptionInfo[] FLEXMARK_OPTIONS_INFO;
    static {
        FlexmarkOptionInfo[] info = new FlexmarkOptionInfo[FLEXMARK_BUILT_IN_OPTION_COUNT];
        info[FLEXMARK_OPTION_IGNORE.index] = FLEXMARK_OPTION_IGNORE;
        info[FLEXMARK_OPTION_FAIL.index] = FLEXMARK_OPTION_FAIL;
        info[FLEXMARK_OPTION_EMBED_TIMED.index] = FLEXMARK_OPTION_EMBED_TIMED;
        info[FLEXMARK_OPTION_TIMED.index] = FLEXMARK_OPTION_TIMED;
//        info[FLEXMARK_OPTION_TIMED_ITERATIONS.index] = FLEXMARK_OPTION_TIMED_ITERATIONS;
        info[FLEXMARK_OPTION_NO_FILE_EOL.index] = FLEXMARK_OPTION_NO_FILE_EOL;
        info[FLEXMARK_OPTION_FILE_EOL.index] = FLEXMARK_OPTION_FILE_EOL;
        FLEXMARK_OPTIONS_INFO = info;
    }

//    FLEXMARK_OPTION_IGNORE,                  IGNORE_OPTION_NAME
//    FLEXMARK_OPTION_FAIL,                    FAIL_OPTION_NAME
//    FLEXMARK_OPTION_EMBED_TIMED,             EMBED_TIMED_OPTION_NAME
//    FLEXMARK_OPTION_TIMED,                   TIMED_OPTION_NAME
    // NOTE: this one is not an option to use in spec example but set in data holder
//    FLEXMARK_OPTION_TIMED_ITERATIONS,        TIMED_ITERATIONS_OPTION_NAME
//    FLEXMARK_OPTION_NO_FILE_EOL,             NO_FILE_EOL_OPTION_NAME
//    FLEXMARK_OPTION_FILE_EOL,                FILE_EOL_OPTION_NAME

    private static int maxIndex() {
        int maxIndex = 0;
        for (FlexmarkOptionInfo info : FLEXMARK_BUILT_IN_OPTIONS.values()) {
            if (maxIndex < info.index) maxIndex = info.index;
        }
        return maxIndex + 1;
    }

    // CAUTION: min index is used to determine icon for the option list in spec example so the index in each should reflect priority to determine option list icon
    private static HashMap<String, FlexmarkOptionInfo> buildBuiltInOptions() {
        Object[][] options = {
                new Object[] { TestUtils.IGNORE_OPTION_NAME, FlexmarkIcons.Element.SPEC_EXAMPLE_IGNORED, MdTypes.FLEXMARK_EXAMPLE_OPTION_IGNORE_NAME, 1 },
                new Object[] { TestUtils.FAIL_OPTION_NAME, FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_FAIL, MdTypes.FLEXMARK_EXAMPLE_OPTION_FAIL_NAME, 2 },
                new Object[] { TestUtils.EMBED_TIMED_OPTION_NAME, FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_TIMED, MdTypes.FLEXMARK_EXAMPLE_OPTION_EMBED_TIMED_NAME, 3 },
                new Object[] { TestUtils.TIMED_OPTION_NAME, FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_TIMED, MdTypes.FLEXMARK_EXAMPLE_OPTION_TIMED_NAME, 4 },
                // NOTE: this one is not an option to use in spec example but set in data holder
//                new Object[] { TestUtils.TIMED_ITERATIONS_OPTION_NAME, MdIcons.FLEXMARK_SPEC_EXAMPLE_TIMED, MdTypes.FLEXMARK_EXAMPLE_OPTION_TIMED_NAME, 5 },
                new Object[] { TestUtils.NO_FILE_EOL_OPTION_NAME, FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_NO_FILE_EOL, MdTypes.FLEXMARK_EXAMPLE_OPTION_NO_FILE_EOL_NAME, 6 },
                new Object[] { TestUtils.FILE_EOL_OPTION_NAME, FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE_FILE_EOL, MdTypes.FLEXMARK_EXAMPLE_OPTION_FILE_EOL_NAME, 7 },
        };

        return TestUtils.buildOptionsMap(true, options, (option, data) -> new FlexmarkOptionInfo(option, (Icon) data[1], (IElementType) data[2], (int) data[3]));
    }

    @Nullable
    public static FlexmarkOptionInfo getBuiltInFlexmarkOption(@Nullable CharSequence option) {
        return option == null ? null : FLEXMARK_BUILT_IN_OPTIONS.get(option.toString());
    }

    public static boolean isBuiltInFlexmarkOption(@Nullable CharSequence option) {
        return getBuiltInFlexmarkOption(option) != null;
    }

    @NotNull
    public static FlexmarkOptionInfo getFlexmarkOptionInfo(@Nullable String text) {
        if (text == null || text.trim().isEmpty()) return FLEXMARK_INVALID_OPTION_INFO;
        FlexmarkOptionInfo info = getBuiltInFlexmarkOption(text);
        if (info != null) return info;
        ExampleOption exampleOption = ExampleOption.of(text);
        return new FlexmarkOptionInfo(exampleOption, FlexmarkIcons.Element.FLEXMARK_SPEC_EXAMPLE, MdTypes.FLEXMARK_EXAMPLE_OPTION_NAME, FLEXMARK_BUILT_IN_OPTION_COUNT);
    }
}
