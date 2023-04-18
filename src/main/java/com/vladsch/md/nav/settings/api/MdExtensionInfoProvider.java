// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.Nullable;

/**
 * Provider to allow synchronization to plugin's update chanel selection
 * <p>
 * return non-null values for the given chanel's plugin repository URL
 */
public interface MdExtensionInfoProvider {
    ExtensionPointName<MdExtensionInfoProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.extensionInfoProvider");
    MdExtensions<MdExtensionInfoProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdExtensionInfoProvider[0]);

    @Nullable
    String legacyRelease();

    @Nullable
    String legacyEapRelease();

    @Nullable
    String eapRelease();
}
