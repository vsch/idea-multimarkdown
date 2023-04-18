// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.testUtil.cases;

import com.vladsch.flexmark.util.data.DataHolder;

import java.util.HashMap;
import java.util.Map;

public interface MdEnhOptionsForHistorySettings extends MdEnhSpecTest {
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
