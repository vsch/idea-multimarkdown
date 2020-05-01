// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex.settings;

import com.intellij.openapi.project.Project;
import com.vladsch.md.nav.settings.api.MdProjectSettingsExtensionProvider;
import org.jetbrains.annotations.NotNull;

public class FlexmarkProjectSettingsExtensionProvider implements MdProjectSettingsExtensionProvider {
    @Override
    public void initializeProjectSettingsService(@NotNull final Project project) {
        FlexmarkProjectSettingsManager.getInstance(project);
    }
}
