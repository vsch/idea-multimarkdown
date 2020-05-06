// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.ext.gfm.tasklist.TaskListItemCase;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum TaskListItemCaseType implements ComboBoxAdaptable<TaskListItemCaseType> {
    AS_IS(0, CodeStyleBundle.message("task-list-item.case.as-is"), TaskListItemCase.AS_IS),
    LOWERCASE(1, CodeStyleBundle.message("task-list-item.case.lowercase"), TaskListItemCase.LOWERCASE),
    UPPERCASE(2, CodeStyleBundle.message("task-list-item.case.uppercase"), TaskListItemCase.UPPERCASE);

    public final @NotNull String displayName;
    public final int intValue;
    public final TaskListItemCase flexMarkEnum;

    TaskListItemCaseType(int intValue, @NotNull String displayName, @NotNull TaskListItemCase flexMarkEnum) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.flexMarkEnum = flexMarkEnum;
    }

    public static Static<TaskListItemCaseType> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(AS_IS));

    @NotNull
    @Override
    public ComboBoxAdapter<TaskListItemCaseType> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public TaskListItemCaseType[] getValues() { return values(); }
}
