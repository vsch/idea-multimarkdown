// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.psi.util;

import com.vladsch.md.nav.flex.psi.FlexmarkExampleOption;
import com.vladsch.md.nav.util.Result;
import com.vladsch.md.nav.util.ResultFunction;
import org.jetbrains.annotations.NotNull;

public interface FlexmarkExampleOptionProcessor<T> extends ResultFunction<FlexmarkExampleOption, T> {
    @Override
    @NotNull
    Result<T> apply(@NotNull FlexmarkExampleOption option);
}
