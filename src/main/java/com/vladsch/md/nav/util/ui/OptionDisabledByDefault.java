// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.ui;

import com.intellij.codeInsight.daemon.GutterIconDescriptor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public class OptionDisabledByDefault extends GutterIconDescriptor.Option {
    public OptionDisabledByDefault(@NotNull String id, @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String name, Icon icon) {
        super(id, name, icon);
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
