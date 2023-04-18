// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.format;

import org.jetbrains.annotations.NotNull;

public class TaskItemCounts {
    public int completeItems;
    public int incompleteItems;
    public int emptyItems;

    public TaskItemCounts() {
        this(0, 0, 0);
    }

    public TaskItemCounts(@NotNull TaskItemCounts other) {
        this(other.completeItems, other.incompleteItems, other.emptyItems);
    }

    public TaskItemCounts(final int completeItems, final int incompleteItems, final int emptyItems) {
        this.completeItems = completeItems;
        this.incompleteItems = incompleteItems;
        this.emptyItems = emptyItems;
    }

    void copy(@NotNull TaskItemCounts other) {
        completeItems = other.completeItems;
        incompleteItems = other.incompleteItems;
        emptyItems = other.emptyItems;
    }

    void add(@NotNull TaskItemCounts other) {
        completeItems += other.completeItems;
        incompleteItems += other.incompleteItems;
        emptyItems += other.emptyItems;
    }

    void remove(@NotNull TaskItemCounts other) {
        completeItems -= other.completeItems;
        incompleteItems -= other.incompleteItems;
        emptyItems -= other.emptyItems;
    }
}
