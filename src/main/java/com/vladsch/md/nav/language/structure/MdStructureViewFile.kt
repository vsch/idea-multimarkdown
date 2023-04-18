// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language.structure

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.vladsch.md.nav.psi.element.MdFile
import com.vladsch.md.nav.psi.element.MdHeaderElement
import java.util.*

open class MdStructureViewFile(element: MdFile) : MdStructureViewItem<MdFile>(element) {

    override fun getValue(): Any {
        return element
    }

    override fun getChildren(): Array<TreeElement> {
        val treeElements = ArrayList<TreeElement>(6)
        var headerLevel = 7

        // add all headings at file level
        element.childLooping()
            .filter(MdHeaderElement::class.java)
            .doLoop { it, _ ->
                if (it.headerLevel <= headerLevel) {
                    headerLevel = it.headerLevel
                    treeElements.add(MdStructureViewHeaderElement(it))
                }
            }

        return treeElements.toTypedArray()
    }

    override fun getPresentation(): ItemPresentation {
        return element.structureViewPresentation
    }
}
