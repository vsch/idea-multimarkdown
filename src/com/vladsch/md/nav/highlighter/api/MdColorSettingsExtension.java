// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.highlighter.api;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdColorSettingsExtension {
    ExtensionPointName<MdColorSettingsExtension> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.colorSettingsExtension");
    MdExtensions<MdColorSettingsExtension> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdColorSettingsExtension[0]);

    interface MdColorSettings {
        void addTextAttributesKey(@NotNull String name, @NotNull TextAttributesKey attributesKey);
    }

    void addTextAttributes(@NotNull MdColorSettings colorSettings);

    @Nullable
    String getDemoText();
}
