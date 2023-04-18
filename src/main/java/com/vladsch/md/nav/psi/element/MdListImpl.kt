// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.vladsch.md.nav.actions.handlers.util.PsiEditContext

abstract class MdListImpl(node: ASTNode) : MdCompositeImpl(node), MdList/*, PsiListLikeElement*/ {
    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getLocationString(): String? {
        return null
    }

    final override fun getTaskItemDescendantPriority(editContext: PsiEditContext, wantEmptyItems: Boolean, wantCompleteItems: Boolean, emptiesCombined: Boolean): Int = super.getTaskItemDescendantPriority(editContext, wantEmptyItems, wantCompleteItems, false)
    final override fun getTaskItemDescendantPriority(editContext: PsiEditContext): Int = super.getTaskItemDescendantPriority(editContext)
    final override fun hasTaskItemDescendants(wantEmptyItems: Boolean, wantCompleteItems: Boolean, emptiesCombined: Boolean): Boolean = super.hasTaskItemDescendants(wantEmptyItems, wantCompleteItems, false)
    final override fun getHasTaskItemDescendants(): Boolean = super.getHasTaskItemDescendants()
    final override fun getHasIncompleteTaskItemDescendants(): Boolean = super.getHasIncompleteTaskItemDescendants()
    final override fun getFirstItem(): MdListItem? = super.getFirstItem()
    final override fun getLastItem(): MdListItem? = super.getLastItem()
}
