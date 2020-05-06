// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi;

import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FlexmarkExampleStub extends StubElement<FlexmarkExample> {
    @Nullable
    FlexmarkExampleOptions getOptionsList();

    @NotNull
    String getSection();

    @NotNull
    String getNumber();

    boolean isWithIgnored();

    boolean isWithErrors();
}
