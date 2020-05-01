// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.md.nav.psi.element.MdListItem;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TaskItemPriorityType implements ComboBoxAdaptable<TaskItemPriorityType> {
    LOW(MdListItem.LOW_PRIORITY, CodeStyleBundle.message("task-item-priority.low")),
    NORMAL(MdListItem.NORMAL_PRIORITY, CodeStyleBundle.message("task-item-priority.normal")),
    HIGH(MdListItem.HIGH_PRIORITY, CodeStyleBundle.message("task-item-priority.high")),
    ;

    public final @NotNull String displayName;
    public final int intValue;

    TaskItemPriorityType(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<TaskItemPriorityType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(NORMAL));

    @NotNull
    @Override
    public ComboBoxAdapter<TaskItemPriorityType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TaskItemPriorityType[] getValues() { return values(); }
}
