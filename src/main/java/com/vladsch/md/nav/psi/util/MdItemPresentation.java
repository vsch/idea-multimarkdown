// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.util;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class MdItemPresentation implements ItemPresentation {
    final @Nullable String presentableText;
    final @Nullable String locationString;
    final @Nullable Icon icon;

    public MdItemPresentation(@Nullable String presentableText, @Nullable String locationString, @Nullable Icon icon) {
        this.presentableText = presentableText;
        this.locationString = locationString;
        this.icon = icon;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return presentableText;
    }

    @Nullable
    @Override
    public String getLocationString() {
        return locationString;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return icon;
    }
}
