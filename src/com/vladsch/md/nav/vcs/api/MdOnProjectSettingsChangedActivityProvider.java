// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.vcs.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

public interface MdOnProjectSettingsChangedActivityProvider {
    ExtensionPointName<MdOnProjectSettingsChangedActivityProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.projectSettingsChangedActivity");
    MdExtensions<MdOnProjectSettingsChangedActivityProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdOnProjectSettingsChangedActivityProvider[0]);

    MdOnProjectSettingsChangedActivity getProjectSettingsChangedActivity(@NotNull Project project);
}
