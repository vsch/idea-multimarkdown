// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.settings.api.MdSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnwrappedSettings<T extends MdSettings> implements Item<T> {
    private final @NotNull T settings;
    private final boolean readOnly;

    public UnwrappedSettings(boolean readOnly, @NotNull final T settings) {
        this.settings = settings;
        this.readOnly = readOnly;
    }

    public UnwrappedSettings(@NotNull final T settings) {
        this(false, settings);
    }

    @NotNull
    @Override
    public T createItem(@Nullable final Element element) {
        if (!readOnly || element != null) {
            settings.resetToDefaults();
            settings.loadState(element);
        }
        return settings;
    }

    @Nullable
    @Override
    public Element saveState(@Nullable final Element element) {
        return readOnly ? null : settings.saveState(element);
    }

    @Override
    public void loadState(@Nullable final Element element) {
        if (!readOnly || element != null) {
            settings.resetToDefaults();
            settings.loadState(element);
        }
    }
}
