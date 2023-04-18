// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.psi.element

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElementVisitor

abstract class MdPsiElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), MdPsiElement {

    override fun accept(visitor: PsiElementVisitor) {
        if (visitor is MdPsiVisitor)
            visitor.visitPsiElement(this)
        else
            super.accept(visitor)
    }

    override fun getName(): String? {
        return text
    }

    //    override fun getPresentation(): ItemPresentation {
    //        return MdPsiImplUtil.getPresentation(this)
    //    }

    companion object {
        private val LOG = Logger.getInstance(MdPsiElementImpl::class.java)
    }
}
