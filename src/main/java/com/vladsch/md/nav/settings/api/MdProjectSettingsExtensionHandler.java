// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.vladsch.md.nav.util.MdExtensions;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public interface MdProjectSettingsExtensionHandler {
    ExtensionPointName<MdProjectSettingsExtensionHandler> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.projectSettingsHandler");
    MdExtensions<MdProjectSettingsExtensionHandler> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdProjectSettingsExtensionHandler[0]);

    /**
     * Used to add plugin settings to management operations, which are not part of application settings
     */
    void resetToDefault(@NotNull Project project);

    void exportSettings(@NotNull Project project, @NotNull Element root);

    void importSettings(@NotNull Project project, @NotNull Element root);

    void copyFromProject(@NotNull Project project, @NotNull Project sourceProject);
}
