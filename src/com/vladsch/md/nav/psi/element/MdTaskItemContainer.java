// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import com.vladsch.md.nav.util.looping.MdPsiIterator;
import org.jetbrains.annotations.NotNull;

public interface MdTaskItemContainer extends MdPsiElement {
    default boolean hasTaskItemDescendants(final boolean wantEmptyItems, final boolean wantCompleteItems, final boolean emptiesCombined) {
        return childLooping()
                .recurseCanContainTasksOrHeaders()
                .filter(MdListItem.class, it -> it.isWantedTaskItem(wantEmptyItems, wantCompleteItems, emptiesCombined))
                .doLoop(false, (it, loop) -> {
                    loop.Return(true);
                });
    }

    /**
     * Used to accumulate max task priority per iteration
     */
    DataKey<Integer> MAX_TASK_PRIORITY = new DataKey<>("MAX_TASK_PRIORITY", -1);

    /**
     * Get Max priority of task item descendants
     *
     * @param editContext
     * @param wantEmptyItems
     * @param wantCompleteItems
     * @param emptiesCombined
     *
     * @return
     */
    default int getTaskItemDescendantPriority(@NotNull PsiEditContext editContext, final boolean wantEmptyItems, final boolean wantCompleteItems, final boolean emptiesCombined) {
        return childLooping()
                .recurseCanContainTasksOrHeaders()
                .filter(MdListItem.class, it -> it.isWantedTaskItem(wantEmptyItems, wantCompleteItems, emptiesCombined))
                .doLoop(-1, (it, loop) -> {
                    int priority = Math.max(it.getTaskItemPriority(editContext), MAX_TASK_PRIORITY.get(loop.getData()));
                    MAX_TASK_PRIORITY.set(loop.getData(), priority);
                    loop.setResult(priority);
                });
    }

    default boolean getHasIncompleteTaskItemDescendants() {
        return hasTaskItemDescendants(true, false, false);
    }

    default boolean getHasTaskItemDescendants() {
        return hasTaskItemDescendants(true, true, false);
    }

    default int getIncompleteTaskItemDescendantPriority(@NotNull PsiEditContext editContext) {
        return getTaskItemDescendantPriority(editContext, true, false, false);
    }

    default int getTaskItemDescendantPriority(@NotNull PsiEditContext editContext) {
        return getTaskItemDescendantPriority(editContext, true, true, false);
    }

    default MdPsiIterator<MdListItem> taskItemLooping() {
        return taskItemLooping(true, true, false);
    }

    default MdPsiIterator<MdListItem> taskItemLooping(final boolean wantEmptyItems, boolean wantCompleteItems, final boolean emptiesCombined) {
        return childLooping()
                .filter(MdListItem.class, it -> it.isWantedTaskItem(wantEmptyItems, wantCompleteItems, emptiesCombined))
                .recursive();
    }
}
