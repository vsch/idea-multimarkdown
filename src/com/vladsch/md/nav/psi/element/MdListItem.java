// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.intellij.lang.ASTNode;
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MdListItem extends MdTaskItemContainer, MdIndentingComposite, MdStructureViewPresentableElement, MdStructureViewPresentableItem, MdBreadcrumbElement, MdBlockElementWithChildren {
    int LOW_PRIORITY = 0;
    int NORMAL_PRIORITY = 1;
    int HIGH_PRIORITY = 2;

    @Nullable
    ASTNode getListItemMarker();

    /**
     * -1 - not a task item
     * 0 - low priority '-' list item marker
     * 1 - normal priority '+' list item marker or ordered list item
     * 2 - high priority '*' list item marker
     * @return priority of task item (based on list item marker type)
     * @param editContext
     */
    int getTaskItemPriority(@NotNull PsiEditContext editContext);

    int directListParentLevel();

    boolean isEmptyItem();

    @Nullable
    ASTNode getTaskItemMarker();

    @NotNull
    MdTaskItemType getTaskItemType();

    default boolean isTaskItem() { return getTaskItemType() != MdTaskItemType.NONE;}

    default boolean isCompleteTaskItem() { return getTaskItemType() == MdTaskItemType.COMPLETE;}

    default boolean isIncompleteTaskItem() { return getTaskItemType() == MdTaskItemType.INCOMPLETE;}

    boolean isWantedTaskItem(final boolean wantEmptyItems, final boolean wantCompleteItems, final boolean emptiesCombined);

    boolean isTaskItemPrefix(@NotNull CharSequence prefix);

    @Nullable
    MdListItem getNextItem();

    @Nullable
    MdListItem getPrevItem();
}
