// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TaskItemContinuationType implements ComboBoxAdaptable<TaskItemContinuationType> {
    ALIGN_TO_FIRST(-1, CodeStyleBundle.message("task-item.continuation-alignment.to-first-line")),
    ALIGN_TO_ITEM(0, CodeStyleBundle.message("task-item.continuation-alignment.to-item")),
    ;

    public final @NotNull String displayName;
    public final int intValue;

    TaskItemContinuationType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<TaskItemContinuationType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(ALIGN_TO_FIRST));

    public boolean isListIndent() {
        return this == ALIGN_TO_ITEM;
    }

    public boolean isAlignToFirst() {
        return this == ALIGN_TO_FIRST;
    }

    @NotNull
    @Override
    public ComboBoxAdapter<TaskItemContinuationType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TaskItemContinuationType[] getValues() { return values(); }
}
