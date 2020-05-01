// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum FilePathType implements ComboBoxAdaptable<FilePathType> {
    PATH_FROM_PROJECT(0, CodeStyleBundle.message("image-uniquify.path-from-project")),
    FILENAME_ONLY(1, CodeStyleBundle.message("image-uniquify.filename-only")),
    PATH_FROM_PARENT(2, CodeStyleBundle.message("image-uniquify.path-from-parent"));

    public final int intValue;
    public final @NotNull String displayName;

    FilePathType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<FilePathType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(PATH_FROM_PARENT));

    @NotNull
    @Override
    public ComboBoxAdapter<FilePathType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public FilePathType[] getValues() { return values(); }
}
