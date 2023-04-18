// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.ext.gfm.tasklist.TaskListItemPlacement;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TaskListItemPlacementType implements ComboBoxAdaptable<TaskListItemPlacementType> {
    AS_IS(0, CodeStyleBundle.message("task-list-item.placement.as-is"), TaskListItemPlacement.AS_IS),
    INCOMPLETE_FIRST(1, CodeStyleBundle.message("task-list-item.placement.incomplete-first"), TaskListItemPlacement.INCOMPLETE_FIRST),
    INCOMPLETE_NESTED_FIRST(2, CodeStyleBundle.message("task-list-item.placement.incomplete-nested-first"), TaskListItemPlacement.INCOMPLETE_NESTED_FIRST),
    COMPLETE_TO_NON_TASK(3, CodeStyleBundle.message("task-list-item.placement.complete-to-non-task"), TaskListItemPlacement.COMPLETE_TO_NON_TASK),
    COMPLETE_NESTED_TO_NON_TASK(4, CodeStyleBundle.message("task-list-item.placement.complete-nested-to-non-task"), TaskListItemPlacement.COMPLETE_NESTED_TO_NON_TASK);

    public final @NotNull String displayName;
    public final int intValue;
    public final TaskListItemPlacement flexMarkEnum;

    TaskListItemPlacementType(int intValue, @NotNull String displayName, @NotNull TaskListItemPlacement flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<TaskListItemPlacementType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    public boolean isIncompleteDescendants() {
        return this == INCOMPLETE_NESTED_FIRST || this == COMPLETE_NESTED_TO_NON_TASK;
    }

    public boolean isCompleteToItem() {
        return this == TaskListItemPlacementType.COMPLETE_TO_NON_TASK || this == TaskListItemPlacementType.COMPLETE_NESTED_TO_NON_TASK;
    }

    public boolean isAsIs() {
        return this == TaskListItemPlacementType.AS_IS;
    }

    public boolean isSorted() {
        return this != TaskListItemPlacementType.AS_IS;
    }

    @NotNull
    @Override
    public ComboBoxAdapter<TaskListItemPlacementType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TaskListItemPlacementType[] getValues() { return values(); }
}
