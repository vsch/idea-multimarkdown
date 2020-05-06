// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.vladsch.plugin.util.maxLimit
import icons.MdIcons
import javax.swing.Icon

open class MdDefinitionTermImpl(node: ASTNode) : MdCompositeImpl(node), MdDefinitionTerm, MdStructureViewPresentableElement, MdStructureViewPresentableItem {
    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.DEFINITION_TERM
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getLocationString(): String? {
        // FIX: can put text part of the first definition item
        return null
    }

    override fun getPresentableText(): String? {
        return text?.substring(0, text.length.maxLimit(50)) ?: ""
    }
}
