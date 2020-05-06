// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.vladsch.md.nav.settings.RenderingProfileManager;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

public interface MdRenderingProfileManagerFactory {
    ExtensionPointName<MdRenderingProfileManagerFactory> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.renderingProfileManagerFactory");
    MdExtensions<MdRenderingProfileManagerFactory> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdRenderingProfileManagerFactory[0]);

    @NotNull
    RenderingProfileManager getInstance(@NotNull Project project);
}
