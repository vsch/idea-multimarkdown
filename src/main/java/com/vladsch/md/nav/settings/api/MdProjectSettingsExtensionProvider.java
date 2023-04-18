// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.settings.MdProjectSettings;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

public interface MdProjectSettingsExtensionProvider {
    ExtensionPointName<MdProjectSettingsExtensionProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.projectSettingsExtension");
    MdExtensions<MdProjectSettingsExtensionProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdProjectSettingsExtensionProvider[0]);

    void initializeProjectSettingsService(@NotNull MdProjectSettings projectSettings);
}
