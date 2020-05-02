// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.testUtil;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.md.nav.testUtil.cases.MdEnhCodeInsightFixtureSpecTestCase;
import com.vladsch.plugin.test.util.cases.CodeInsightFixtureSpecTestCase;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class MdEnhLightPlatformCodeInsightFixtureSpecTestCase extends MdLightPlatformCodeInsightFixtureSpecTestCase implements MdEnhCodeInsightFixtureSpecTestCase {

    final private static Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.putAll(MdEnhCodeInsightFixtureSpecTestCase.getOptionsMap());
    }
    public MdEnhLightPlatformCodeInsightFixtureSpecTestCase(@Nullable Map<String, ? extends DataHolder> optionMap, @Nullable DataHolder... defaultOptions) {
        super(CodeInsightFixtureSpecTestCase.optionsMaps(optionsMap, optionMap), defaultOptions);
    }
}
