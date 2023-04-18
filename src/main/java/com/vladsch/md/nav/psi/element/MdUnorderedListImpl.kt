// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.vladsch.md.nav.psi.util.MdPsiBundle
import icons.MdIcons
import javax.swing.Icon

class MdUnorderedListImpl(node: ASTNode) : MdListImpl(node), MdUnorderedList {
    override fun getIcon(flags: Int): Icon? {
        return MdIcons.Element.BULLET_LIST
    }

    override fun getPresentableText(): String? {
        return MdPsiBundle.message("bullet-list")
    }
}
