// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.testUtil.renderers;

import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.plugin.test.util.cases.LightFixtureActionSpecTest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MdEnhActionSpecRenderer<T extends LightFixtureActionSpecTest> extends MdActionSpecRenderer<T> {
    public MdEnhActionSpecRenderer(@NotNull T specTestBase, @NotNull SpecExample example, @Nullable DataHolder options) {
        super(specTestBase, example, options);
    }

    @Override
    protected void executeRendererAction(@NotNull String action) {
        switch (action) {
            default:
                super.executeRendererAction(action);
                break;
        }
    }
}
