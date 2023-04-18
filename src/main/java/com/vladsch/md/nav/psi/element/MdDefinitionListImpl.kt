// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.vladsch.md.nav.psi.util.MdPsiBundle
import icons.MdIcons
import javax.swing.Icon

open class MdDefinitionListImpl(node: ASTNode) : MdCompositeImpl(node), MdDefinitionList {
    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.DEFINITION_LIST
    }

    override fun getStructureViewPresentation(): ItemPresentation {
        return MdElementItemPresentation(this)
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun getPresentableText(): String? {
        return MdPsiBundle.message("definition-list")
    }

    /*
                override fun getBreadcrumbInfo(): String {
                    return PsiBundle.message("ordered-list")
                }

                override fun getBreadcrumbTooltip(): String? {
                    return null;
                }
            */
}
