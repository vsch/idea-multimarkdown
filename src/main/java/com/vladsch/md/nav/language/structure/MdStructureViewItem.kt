// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.psi.element.MdStructureViewPresentableElement
import com.vladsch.md.nav.psi.util.MdPsiImplUtil

abstract class MdStructureViewItem<T : PsiElement>(protected val element: T) : StructureViewTreeElement, SortableTreeElement {
    companion object {
        val EMPTY_ARRAY = arrayOf<TreeElement>()
    }

    override fun getValue(): Any {
        return element
    }

    override fun navigate(requestFocus: Boolean) {
        if (element is NavigationItem) {
            element.navigate(requestFocus)
        }
    }

    override fun canNavigate(): Boolean {
        return element is NavigationItem && element.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return element is NavigationItem && (element.canNavigate() || element.canNavigateToSource())
    }

    override fun getAlphaSortKey(): String {
        if (element is MdStructureViewPresentableElement) {
            return element.structureViewPresentation.presentableText ?: ""
        } else {
            return ""
        }
    }

    override fun getPresentation(): ItemPresentation {
        if (element is MdStructureViewPresentableElement)
            return element.structureViewPresentation
        else
            return MdPsiImplUtil.getPresentation(element.javaClass.toString(), element.toString(), null)
    }
}
