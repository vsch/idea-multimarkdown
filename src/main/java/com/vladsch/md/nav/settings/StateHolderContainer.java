// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StateHolderContainer {
    @NotNull
    StateHolder getStateHolder();

    /**
     * Used to load under alternate state holder name
     * only used for loading. Not saving.
     */
    @Nullable
    default StateHolder getAlternateStateHolder() {
        return null;
    }
}
