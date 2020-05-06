// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.vladsch.flexmark.util.data.DataKey;
import org.jetbrains.annotations.NotNull;

public interface MdSettingsExtensionsHolder {
    @NotNull
    <T extends MdSettingsExtension<T>> T getExtension(@NotNull DataKey<T> key);

    <T extends MdSettingsExtension<T>> void setExtension(@NotNull T value);
}
