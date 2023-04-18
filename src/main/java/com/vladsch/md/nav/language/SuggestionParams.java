// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import org.jetbrains.annotations.Nullable;

public class SuggestionParams {
    final public @Nullable String typeText;
    final public @Nullable Double priority;

    public SuggestionParams(@Nullable final String typeText, @Nullable final Double priority) {
        this.typeText = typeText;
        this.priority = priority;
    }
}
