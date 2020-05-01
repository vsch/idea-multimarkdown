// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class MdElementItemPresentation implements ItemPresentation {
    final @NotNull MdStructureViewPresentableItem element;
    final @Nullable Icon icon;

    public MdElementItemPresentation(@NotNull MdStructureViewPresentableItem element) {
        this(element, null);
    }

    public MdElementItemPresentation(@NotNull MdStructureViewPresentableItem element, @Nullable Icon icon) {
        this.element = element;
        this.icon = icon;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        if (!element.isValid()) return null;
        return element.getPresentableText();
    }

    @Nullable
    @Override
    public String getLocationString() {
        if (!element.isValid()) return null;
        return element.getLocationString();
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        if (icon != null) return icon;
        return element.getIcon(0);
    }
}
