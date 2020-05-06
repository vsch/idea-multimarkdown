// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.testUtil.cases;

import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.testUtil.MdEnhSpecTestSetup;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface MdEnhSpecTest extends MdSpecTest {
    Consumer<MdRenderingProfile> ENHANCED_OPTIONS = MdEnhSpecTestSetup.ENHANCED_OPTIONS;
    Consumer<MdRenderingProfile> ALL_ENHANCED_PARSER_OPTIONS = MdEnhSpecTestSetup.ALL_ENHANCED_PARSER_OPTIONS;
    Consumer<MdRenderingProfile> LEGACY_OPTIONS = MdEnhSpecTestSetup.LEGACY_OPTIONS;

    Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, DataHolder> getOptionsMap() {
        synchronized (optionsMap) {
            if (optionsMap.isEmpty()) {
                optionsMap.putAll(MdSpecTest.getOptionsMap());
            }
            return optionsMap;
        }
    }
}
