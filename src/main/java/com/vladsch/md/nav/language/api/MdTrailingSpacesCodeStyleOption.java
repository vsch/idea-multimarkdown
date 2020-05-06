// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.vladsch.md.nav.settings.TrailingSpacesType;
import org.jetbrains.annotations.NotNull;

public class MdTrailingSpacesCodeStyleOption {
    private final @NotNull String optionId;
    private final @NotNull String optionName;
    private final @NotNull String optionDescription;
    private final @NotNull TrailingSpacesType[] excludedOptions;

    public MdTrailingSpacesCodeStyleOption(@NotNull String optionId, @NotNull String optionName, @NotNull String optionDescription, @NotNull TrailingSpacesType... excludedOptions) {
        this.optionId = optionId;
        this.optionName = optionName;
        this.optionDescription = optionDescription;
        this.excludedOptions = excludedOptions;
    }

    @NotNull
    public String getOptionId() {
        return optionId;
    }

    @NotNull
    public String getOptionDescription() {
        return optionDescription;
    }

    @NotNull
    public String getOptionName() {
        return optionName;
    }

    public TrailingSpacesType[] getExcludedOptions() {
        return excludedOptions;
    }
}
