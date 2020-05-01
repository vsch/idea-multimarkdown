// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.util.MdExtensions;

public interface MdApplicationSettingsExtensionProvider {
    ExtensionPointName<MdApplicationSettingsExtensionProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.applicationSettingsExtension");
    MdExtensions<MdApplicationSettingsExtensionProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdApplicationSettingsExtensionProvider[0]);

    /**
     * load service instance by instantiating it if settings persistence is not done by application settings but settings extend application settings
     * <p>
     * NOTE: will be called from first MdApplicationSettings.instance instantiation so should not use settings stored by MdApplicationSettings since some of them may not have loaded yet
     */
    void initializeApplicationSettingsService();
}
