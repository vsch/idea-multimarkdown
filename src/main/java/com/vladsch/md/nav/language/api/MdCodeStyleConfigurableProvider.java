// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.api;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.vladsch.md.nav.util.MdExtensions;
import org.jetbrains.annotations.Nullable;

public interface MdCodeStyleConfigurableProvider {
    ExtensionPointName<MdCodeStyleConfigurableProvider> EP_NAME = ExtensionPointName.create("com.vladsch.idea.multimarkdown.codeStyleConfigurableProvider");
    MdExtensions<MdCodeStyleConfigurableProvider> EXTENSIONS = new MdExtensions<>(EP_NAME, new MdCodeStyleConfigurableProvider[0]);

    @Nullable
    CodeStyleConfigurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings);
}
