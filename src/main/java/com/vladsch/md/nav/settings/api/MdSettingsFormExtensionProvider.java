// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.settings.RenderingProfileSynchronizer;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

public interface MdSettingsFormExtensionProvider<S, P> {
    ExtensionPointName<MdSettingsFormExtensionProvider<?, ?>> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.settingsConfigurableExtension");
    MdExtensions<MdSettingsFormExtensionProvider<?, ?>> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdSettingsFormExtensionProvider<?, ?>[0]);

    @NotNull
    MdSettingsComponent<P> createComponent(S settings, RenderingProfileSynchronizer profileSynchronizer, final Object parent);

    boolean isAvailable(final @NotNull Object parent);

    @NotNull
    String getExtensionName();
}
