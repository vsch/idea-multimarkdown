// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi.index;

import com.intellij.psi.stubs.StubIndexKey;
import com.vladsch.md.nav.flex.psi.FlexmarkExampleOption;
import com.vladsch.md.nav.psi.index.MdStubIndexExtension;
import org.jetbrains.annotations.NotNull;

public class FlexmarkExampleOptionIndex extends MdStubIndexExtension<FlexmarkExampleOption> {
    public static final StubIndexKey<String, FlexmarkExampleOption> KEY = StubIndexKey.createIndexKey("markdown.flexmark.example.option.index");
    private static final FlexmarkExampleOptionIndex ourInstance = new FlexmarkExampleOptionIndex();

    public static FlexmarkExampleOptionIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    public StubIndexKey<String, FlexmarkExampleOption> getKey() {
        return KEY;
    }
}
