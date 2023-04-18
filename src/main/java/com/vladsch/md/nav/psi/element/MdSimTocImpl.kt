// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.vladsch.md.nav.psi.util.MdPsiImplUtil
import com.vladsch.md.nav.psi.util.MdTypes

open class MdSimTocImpl(node: ASTNode) : MdCompositeImpl(node), MdSimToc {
    override fun getTocContentElement(): PsiElement? {
        return MdPsiImplUtil.findChildByType(this, MdTypes.SIM_TOC_CONTENT)
    }

    override fun tocContent(): String {
        return tocContentElement?.text ?: ""
    }
}
