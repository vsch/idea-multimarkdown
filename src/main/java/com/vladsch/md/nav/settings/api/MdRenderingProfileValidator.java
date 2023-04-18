// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;

public interface MdRenderingProfileValidator {
    ExtensionPointName<MdRenderingProfileValidator> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.renderingProfileValidator");
    MdExtensions<MdRenderingProfileValidator> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdRenderingProfileValidator[0]);

    void validateSettings(@NotNull MdRenderingProfile renderingProfile);
}
