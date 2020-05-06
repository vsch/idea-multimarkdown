// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.structure

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.vladsch.md.nav.psi.element.MdHeaderElement
import com.vladsch.md.nav.psi.element.MdHeaderElementImpl
import java.util.*

open class MdStructureViewHeaderElement(element: MdHeaderElement) : MdStructureViewItem<MdHeaderElement>(element) {
    override fun getChildren(): Array<TreeElement> {
        val treeElements = ArrayList<TreeElement>()
        element.headingSectionLooping()
            .doLoop { it, _ ->
                if (it is MdHeaderElement) {
                    treeElements.add(MdStructureViewHeaderElement(it))
                }
            }

        if (treeElements.isNotEmpty()) return treeElements.toTypedArray()
        return EMPTY_ARRAY
    }

    override fun getPresentation(): ItemPresentation = (element as MdHeaderElementImpl).structureViewPresentation
}
